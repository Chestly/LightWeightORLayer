package net.questcraft.jdbccontacter;

import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLContactor implements Contactor {
    private final ORSetup.DBInformation util;

    public SQLContactor(ORSetup.DBInformation util) {
        this.util = util;
    }

    @Override
    public Connection getConnection() throws FatalORLayerException {
        //Client MUST Wrap this in a try-with-resources
        try {
            return Contactor.getConnection(this.util);
        } catch (SQLException e) {
            throw new FatalORLayerException(e);
        }
    }

    @Override
    public int executeUpdate(PreparedStatement statement) throws FatalORLayerException {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new FatalORLayerException(e);
        }
    }

    @Override
    public ResultSet executeQuery(PreparedStatement statement) throws FatalORLayerException {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new FatalORLayerException(e);
        }
    }
}
