package net.questcraft.transaction;

import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.exceptions.MildORLayerException;
import net.questcraft.jdbccontacter.Contactor;
import net.questcraft.jdbccontacter.SQLContactor;
import net.questcraft.stmt.metadata.SQLStmtMetaData;
import net.questcraft.stmt.metadata.StatementMetaData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.questcraft.transaction.SQLTransaction.SQLTransactionResults.TransactionResultType.*;
import static net.questcraft.stmt.metadata.StatementMetaData.StatementMetaType.QUERY;

public class InnoDBTransaction implements SQLTransaction {
    private final Contactor contactor;
    private final List<StatementMetaData> data;

    public InnoDBTransaction(ORSetup.DBInformation information) {
        this.data = new ArrayList<>();
        this.contactor = new SQLContactor(information);
    }

    @Override
    public InnoDBTransactionResults execute() {
        try {
            Connection connection = this.contactor.getConnection();

            try {
                connection.setAutoCommit(false);
                InnoDBTransactionResults.InnoDBTransactionBuilder build = new InnoDBTransactionResults.InnoDBTransactionBuilder().connection(connection).resultType(SUCCESS);

                for (StatementMetaData metaData : this.data) {
                    if (metaData.getType().equals(QUERY)) build.resultSet(this.contactor.executeQuery(this.contactor.createPreparedStmt(metaData, connection)));
                    else this.contactor.executeUpdate(this.contactor.createPreparedStmt(metaData, connection));
                }
                return build.build();
            } catch (FatalORLayerException | SQLException e) {
                return new InnoDBTransactionResults.InnoDBTransactionBuilder().connection(connection).exceptions(e).resultType(FAIL).build();
            }
        } catch (FatalORLayerException e) {
            return new InnoDBTransactionResults.InnoDBTransactionBuilder().resultType(CONNECTION_ERROR).exceptions(e).build();
        }
    }

    @Override
    public InnoDBTransaction addStmt(StatementMetaData sqlStmt) {
        if (!checkExistence(sqlStmt)) this.data.add(sqlStmt);
        return this;
    }

    @Override
    public InnoDBTransaction addStmts(SQLStmtMetaData... sqlStmt) {
        for (SQLStmtMetaData sqlStmtMetaData : sqlStmt) {
            if (!checkExistence(sqlStmtMetaData)) this.data.add(sqlStmtMetaData);
        }
        return this;
    }

    private boolean checkExistence(StatementMetaData data) {
        for (StatementMetaData datum : this.data) {
            if (datum.equals(data)) return true;
        }
        return false;
    }

    @Override
    public InnoDBTransaction addAll(SQLTransaction transaction) {
        for (StatementMetaData datum : transaction.getData()) {
            if (!checkExistence(datum)) this.data.add(datum);
        }
        return this;
    }

    @Override
    public List<StatementMetaData> getData() {
        return new ArrayList<>(this.data);
    }

    public static class InnoDBTransactionResults implements SQLTransactionResults {
        private final TransactionResultType resultType;
        private final Exception exception;

        private final Connection connection;
        private final ResultSet resultSet;

        public InnoDBTransactionResults(InnoDBTransactionBuilder builder) {
            this.resultType = builder.getResultType();
            this.exception = builder.getExceptions();
            this.connection = builder.getConnection();
            this.resultSet = builder.getResultSet();
        }

        @Override
        public boolean commit() {
            try (Connection connection = this.connection) {
                if (this.resultType().equals(SUCCESS)) connection.commit();
                return this.resultType().equals(SUCCESS);
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        public void rollBack() throws FatalORLayerException {
            try (Connection connection = this.connection) {
                connection.rollback();
            } catch (SQLException e) {
                throw new FatalORLayerException(e);
            }
        }

        @Override
        public ResultSet resultSet() {
            return this.resultSet;
        }

        @Override
        public TransactionResultType resultType() {
            return this.resultType;
        }

        @Override
        public boolean failed() {
            return resultType.equals(FAIL) || resultType.equals(CONNECTION_ERROR);
        }


        @Override
        public Exception exception() throws MildORLayerException {
            if (this.resultType().equals(SUCCESS))
                throw new MildORLayerException("The Execution was a Success, There is no exception!");
            return this.exception;
        }

        @Override
        public void close() throws Exception {
            if (this.resultType().equals(SUCCESS)) this.commit();
            else if (this.resultType().equals(FAIL)) this.rollBack();
            else if (this.resultType().equals(CONNECTION_ERROR)) throw new FatalORLayerException("Issue when instantiating SQL connection");
        }

        private static class InnoDBTransactionBuilder extends SQLTransactionResultBuilder<InnoDBTransactionBuilder> {
            @Override
            public InnoDBTransactionBuilder self() {
                return this;
            }

            @Override
            public InnoDBTransactionResults build() {
                return new InnoDBTransactionResults(this);
            }
        }
    }
}
