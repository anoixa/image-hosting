package moe.imtop1.imagehosting.framework.handler;

import moe.imtop1.imagehosting.common.utils.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * JSON to Map
 * @author anoixa
 */
@MappedTypes(Map.class)
@MappedJdbcTypes(value = JdbcType.OTHER, includeNullJdbcType = true)
@Component
@SuppressWarnings(value = { "unchecked" })
public class JsonMapTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String jsonString = JsonUtil.toJSONString(parameter);
            ps.setObject(i, jsonString);
        } catch (Exception e) {
            throw new SQLException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String json = rs.getString(columnName);
            return json == null ? null : JsonUtil.parseObject(json, Map.class);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to map", e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            String json = rs.getString(columnIndex);
            return json == null ? null : JsonUtil.parseObject(json, Map.class);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to map", e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            String json = cs.getString(columnIndex);
            return json == null ? null : JsonUtil.parseObject(json, Map.class);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to map", e);
        }
    }
}