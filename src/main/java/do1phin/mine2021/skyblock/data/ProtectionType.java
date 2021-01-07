package do1phin.mine2021.skyblock.data;

import java.util.Arrays;

public enum ProtectionType {
    ALLOW_ONLY_OWNER(0), ALLOW_INVITED(1), ALLOW_ALL(2);

    public final int value;

    ProtectionType(int value) {
        this.value = value;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static ProtectionType valueOf(int value) {
        return Arrays.stream(values())
                .filter(legNo -> legNo.value == value)
                .findFirst().get();
    }
}
