package moe.imtop1.imagehosting.common.enums;

/**
 * 布尔类型枚举
 * @author anoixa
 */
public enum BooleanEnum {
    TRUE(1, "1"),
    FALSE(0, "0");

    private final int code;
    private final String value;

    BooleanEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static BooleanEnum fromCode(int code) {
        for (BooleanEnum b : BooleanEnum.values()) {
            if (b.getCode() == code) {
                return b;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static BooleanEnum fromValue(String value) {
        for (BooleanEnum b : BooleanEnum.values()) {
            if (b.getValue().equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    public static BooleanEnum fromBoolean(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public boolean asBoolean() {
        return this == TRUE;
    }
}
