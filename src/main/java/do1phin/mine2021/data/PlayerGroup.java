package do1phin.mine2021.data;

public enum PlayerGroup {
    USER(0), STAFF(10);

    private final int value;

    PlayerGroup(int value) {
        this.value = value;
    }
}
