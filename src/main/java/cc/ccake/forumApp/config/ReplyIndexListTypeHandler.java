package cc.ccake.forumApp.config;

import cc.ccake.forumApp.model.ReplyIndex;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReplyIndexListTypeHandler implements TypeHandler<List<ReplyIndex>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setParameter(PreparedStatement ps, int i, List<ReplyIndex> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, mapper.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("Error converting List<ReplyIndex> to String", e);
        }
    }

    @Override
    public List<ReplyIndex> getResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String json = rs.getString(columnName);
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new SQLException("Error converting String to List<ReplyIndex>", e);
        }
    }

    @Override
    public List<ReplyIndex> getResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            String json = rs.getString(columnIndex);
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new SQLException("Error converting String to List<ReplyIndex>", e);
        }
    }

    @Override
    public List<ReplyIndex> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            String json = cs.getString(columnIndex);
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new SQLException("Error converting String to List<ReplyIndex>", e);
        }
    }
}
