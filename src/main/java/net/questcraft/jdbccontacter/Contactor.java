package net.questcraft.jdbccontacter;

import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.StatementMetaData;

import java.sql.*;

public interface Contactor {
    Connection getConnection() throws FatalORLayerException;

    default PreparedStatement createPreparedStmt(StatementMetaData metaData, Connection connection) throws FatalORLayerException {
        try {
            PreparedStatement statement = connection.prepareStatement(metaData.parse());
            for (int i = 0; i < metaData.values().size(); i++) {
                statement.setObject(i + 1, metaData.values().get(i));
            }
            return statement;
        } catch (SQLException e) {
            throw new FatalORLayerException(e);
        }
    }

    int executeUpdate(PreparedStatement statement) throws FatalORLayerException;

    ResultSet executeQuery(PreparedStatement statement) throws FatalORLayerException;

    static Connection getConnection(ORSetup.DBInformation info) throws SQLException {
        String url = info.getUrl().format();
        String user = info.getUsername();
        String password = info.getPassword();
        return DriverManager.getConnection(url, user, password);
    }
}
