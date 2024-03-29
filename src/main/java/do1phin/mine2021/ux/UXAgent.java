package do1phin.mine2021.ux;

import cn.nukkit.Player;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.network.protocol.ProtocolInfo;
import do1phin.mine2021.ServerAgent;
import do1phin.mine2021.data.Config;
import do1phin.mine2021.data.PlayerData;
import do1phin.mine2021.ui.MessageAgent;
import do1phin.mine2021.utils.CalendarHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UXAgent {

    private final ServerAgent serverAgent;
    private final MessageAgent messageAgent;

    private final Config.SystemConfig systemConfig;

    private final List<UUID> pendingRegisterPlayerList = new ArrayList<>();

    public UXAgent(ServerAgent serverAgent, MessageAgent messageAgent, Config config) {
        this.serverAgent = serverAgent;
        this.messageAgent = messageAgent;

        this.systemConfig = config.parseSystemConfig();

        this.resolveRecipes(config.parseAdditionalRecipes(), config.parseBannedRecipes());
        this.resolveAcceptableProtocols(config.parseAcceptableProtocols());
    }

    public void resolvePlayerJoin(PlayerData playerData) {
        if (playerData.getBanDate() != null
                && !this.processBannedPlayer(playerData)) return;

        if (this.systemConfig.enableGuideBook) this.resolveGuideBook(playerData.getPlayer());

        this.messageAgent.sendBroadcast("message.general.on-player-join",
                new String[]{"%player"}, new String[]{playerData.getName()});
    }

    public void resolvePlayerFirstJoin(Player player) {
        if (this.systemConfig.enableDefaultItems) this.giveDefaultItems(player);
        if (this.systemConfig.enableGuideBook) this.giveGuideBook(player, 1);
        this.addPendingRegisterNewPlayer(player.getUniqueId());

        this.messageAgent.sendBroadcast("message.general.on-player-first-join",
                new String[]{"%player"}, new String[]{player.getName()});
    }

    public void resolvePlayerQuit(Player player) {
        this.messageAgent.sendBroadcast("message.general.on-player-quit",
                new String[]{"%player"}, new String[]{player.getName()});
        this.messageAgent.sendBroadcastPopup("popup.general.on-player-quit",
                new String[]{"%player"}, new String[]{player.getName()});
    }

    private void addPendingRegisterNewPlayer(UUID uuid) {
        this.pendingRegisterPlayerList.add(uuid);
    }

    boolean isPendingRegisterNewPlayer(UUID uuid) {
        return this.pendingRegisterPlayerList.contains(uuid);
    }

    void continueRegisterNewPlayer(Player player) {
        this.pendingRegisterPlayerList.remove(player.getUniqueId());

        if (this.systemConfig.enableWelcomeForm)
            this.messageAgent.sendSimpleForm(player, "form.welcome-form.title", "form.welcome-form.content");
    }

    private boolean processBannedPlayer(PlayerData playerData) {
        if (playerData.getBanDate().getTime() < System.currentTimeMillis()) return true;

        final String[] ymdhm = CalendarHelper.getYMDHMFromTimestamp(playerData.getBanDate());
        playerData.getPlayer().kick(this.messageAgent.getMessage("message.management.player-banned",
                new String[]{"%player", "%year", "%month", "%day", "%hour", "%minute", "%reason"},
                new String[]{playerData.getName(), ymdhm[0], ymdhm[1], ymdhm[2], ymdhm[3], ymdhm[4], playerData.getBanReason()}
        ), false);

        return false;
    }

    public void giveDefaultItems(Player player) {
        this.systemConfig.defaultItemCollection.forEach(item ->
                player.getInventory().addItem(Item.get(item.a, item.b, item.c).clone()));

        player.getInventory().addItem(this.serverAgent.getBlockGenAgent().getBasicBlockGenSource());
    }

    public void giveGuideBook(Player player, int count) {
        final String bookName = this.messageAgent.getText("general.guidebook") + " v" + this.systemConfig.guideBookVersion;

        final ItemBookWritten guideBook = (ItemBookWritten) Item.get(387, 0, count);

        guideBook.writeBook(this.systemConfig.guideBookAuthor, bookName, Arrays.stream(this.systemConfig.guideBookPages)
                .map(s -> s.replaceAll("%player", player.getName()))
                .toArray(String[]::new));
        guideBook.setCustomName(bookName);

        player.getInventory().addItem(guideBook);
    }

    private void resolveGuideBook(final Player player) {
        final String bookName = this.messageAgent.getText("general.guidebook") + " v" + this.systemConfig.guideBookVersion;

        final Item[] legacyBooks = player.getInventory().slots.values().stream()
                .filter(item -> item.getName().equals(bookName))
                .toArray(Item[]::new);

        player.getInventory().removeItem(legacyBooks);
        this.giveGuideBook(player, legacyBooks.length);
    }

    public void resolvePermissions(Player player) {
        if (this.systemConfig.disableDefaultCommands && !player.isOp()) {
            player.addAttachment(this.serverAgent, "nukkit.command", false);
            player.recalculatePermissions();
            player.sendCommandData();
        }
    }

    private void resolveAcceptableProtocols(Collection<Integer> acceptableProtocols) {
        try {
            final Field supportedProtocolsField = ProtocolInfo.class.getField("SUPPORTED_PROTOCOLS");
            final Field modifiersField = Field.class.getDeclaredField("modifiers");

            modifiersField.setAccessible(true);
            modifiersField.setInt(supportedProtocolsField, supportedProtocolsField.getModifiers() & ~Modifier.FINAL);

            supportedProtocolsField.set(null, Stream.concat(ProtocolInfo.SUPPORTED_PROTOCOLS.stream(), acceptableProtocols.stream())
                    .collect(Collectors.toList()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            this.serverAgent.loggerWarning(e.getMessage());
        }
    }

    @SuppressWarnings({"unchecked"})
    private void resolveRecipes(Collection<ShapedRecipe> additionalRecipes, Collection<Item> bannedRecipes) {
        final Class<CraftingManager> craftingManagerClass = CraftingManager.class;
        try {
            final Field shapedRecipesField = craftingManagerClass.getDeclaredField("shapedRecipes");
            shapedRecipesField.setAccessible(true);
            final Map<Integer, Map<UUID, ShapedRecipe>> shapedRecipes = (Map<Integer, Map<UUID, ShapedRecipe>>)
                    shapedRecipesField.get(this.serverAgent.getServer().getCraftingManager());

            final Method getMultiItemHashMethod = craftingManagerClass.getDeclaredMethod("getItemHash", Item.class);
            getMultiItemHashMethod.setAccessible(true);

            final Collection<Integer> hashedBannedRecipes = bannedRecipes.stream().map(results ->
            {
                try {
                    return (Integer) getMultiItemHashMethod.invoke(null, results);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    this.serverAgent.loggerWarning(e.getMessage());
                }
                return 0;
            }).collect(Collectors.toList());

            hashedBannedRecipes.forEach(shapedRecipes::remove);

            final Collection<Recipe> recipes = this.serverAgent.getServer().getCraftingManager().getRecipes().stream().filter(recipe -> {
                try {
                    return !hashedBannedRecipes.contains((int) getMultiItemHashMethod.invoke(null, recipe.getResult()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    this.serverAgent.loggerWarning(e.getMessage());
                }
                return false;
            }).collect(Collectors.toList());

            this.serverAgent.getServer().getCraftingManager().recipes.clear();
            this.serverAgent.getServer().getCraftingManager().recipes.addAll(recipes);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            this.serverAgent.loggerWarning(e.getMessage());
        }

        additionalRecipes.forEach(recipe -> this.serverAgent.getServer().getCraftingManager().registerRecipe(recipe));

        this.serverAgent.getServer().getCraftingManager().rebuildPacket();
    }

    boolean isEnableInventorySave() {
        return this.systemConfig.enableInventorySave;
    }

}
