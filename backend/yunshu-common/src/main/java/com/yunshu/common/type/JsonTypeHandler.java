package com.yunshu.common.type;

import com.alibaba.fastjson2.JSON;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * JSON 字段 TypeHandler — 用于 MySQL JSON 列的存取
 */
public class JsonTypeHandler<T> extends BaseTypeHandler<Object> {

    private final Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setObject(i, JSON.toJSONString(parameter), Types.OTHER);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String sqlString = rs.getString(columnName);
        return jsonStringToObj(sqlString);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String sqlString = rs.getString(columnIndex);
        return jsonStringToObj(sqlString);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String sqlString = cs.getString(columnIndex);
        return jsonStringToObj(sqlString);
    }

    private Object jsonStringToObj(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            return json;
        }
    }
}
