package moe.imtop1.imagehosting.framework.handler;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.utils.StringUtil;
import moe.imtop1.imagehosting.framework.utils.EncryptUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字段加密处理器
 * @param <T> 泛型类型
 */
@Component
@Slf4j
public class EncryptTypeHandler<T> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
            return;
        }
        String originalValue = parameter.toString();
        String encryptedValue = EncryptUtil.encrypt(originalValue);
        ps.setString(i, encryptedValue);
        log.debug("Set parameter: {} -> Encrypted: {}", originalValue, encryptedValue);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getNullableResult(rs.getString(columnName), columnName);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getNullableResult(rs.getString(columnIndex), columnIndex);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getNullableResult(cs.getString(columnIndex), columnIndex);
    }

    /**
     * 通用方法，用于解密字段值
     * @param columnValue 列值
     * @param columnIdentifier 列的标识（名称或索引）
     * @return 解密后的值
     * @throws SQLException SQL异常
     */
    @SuppressWarnings("unchecked")
    private T getNullableResult(String columnValue, Object columnIdentifier) throws SQLException {
        log.debug("Get column value: {} from identifier: {}", columnValue, columnIdentifier);
        if (StringUtil.isBlank(columnValue)) {
            return (T) columnValue;
        }
        try {
            String decryptedValue = EncryptUtil.decrypt(columnValue);
            log.debug("Decrypted value: {} -> Original: {}", columnValue, decryptedValue);
            return (T) decryptedValue;
        } catch (Exception e) {
            log.error("Decrypt failed for identifier: {} with value: {}", columnIdentifier, columnValue, e);
            throw new SQLException("Decrypt failed", e);
        }
    }
}
