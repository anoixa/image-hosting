package moe.imtop1.imagehosting.common.enums;

public enum StrategiesEnum {
    LOCAL("1", "本地储存"),
    MINIO("2", "MinIO对象储存");

    private String code ;
    private String value ;

    StrategiesEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
