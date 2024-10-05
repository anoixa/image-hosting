package moe.imtop1.imagehosting.system.domain.vo;

import lombok.Data;

@Data
public class ValidateCodeVo {
    private String codeKey ;
    private String codeValue ;


    public ValidateCodeVo() {
    }

    public ValidateCodeVo(String codeKey, String codeValue) {
        this.codeKey = codeKey;
        this.codeValue = codeValue;
    }

    public String getCodeKey() {
        return codeKey;
    }

    public void setCodeKey(String codeKey) {
        this.codeKey = codeKey;
    }

    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }
}