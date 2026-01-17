package pl.mpc.asmo.model;

public enum SubscribeType {
    FREE(0),
    PRO(1),
    ENTERPRICE(2);

    private int value;
    SubscribeType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
