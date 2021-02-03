package net.questcraft.transaction;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.exceptions.MildORLayerException;
import net.questcraft.stmt.metadata.SQLStmtMetaData;
import net.questcraft.stmt.metadata.StatementMetaData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public interface SQLTransaction {
    SQLTransactionResults execute() throws FatalORLayerException;

    SQLTransaction addStmt(StatementMetaData sqlStmt);

    SQLTransaction addStmts(SQLStmtMetaData... sqlStmt);

    SQLTransaction addAll(SQLTransaction transaction);

    List<StatementMetaData> getData();

    interface SQLTransactionResults extends AutoCloseable {
        enum TransactionResultType {SUCCESS, FAIL, CONNECTION_ERROR}

        boolean commit();

        void rollBack() throws FatalORLayerException;

        ResultSet resultSet();

        TransactionResultType resultType();

        boolean failed();

        Exception exception() throws MildORLayerException;

        @Override
        void close() throws Exception;

        abstract class SQLTransactionResultBuilder<T extends SQLTransactionResultBuilder<?>> {
            private TransactionResultType resultType;
            private Exception exception;

            private ResultSet resultSet;

            private Connection connection;

            public SQLTransactionResultBuilder() {
            }

            public T resultType(TransactionResultType resultType) {
                this.resultType = resultType;
                return self();
            }


            public T exceptions(Exception exception) {
                this.exception = exception;
                return self();
            }

            public TransactionResultType getResultType() {
                return this.resultType;
            }

            public Exception getExceptions() {
                return exception;
            }

            public T connection(Connection connection) {
                this.connection = connection;
                return self();
            }

            public Connection getConnection() {
                return connection;
            }

            public T resultSet(ResultSet resultSets) {
                this.resultSet = resultSets;
                return self();
            }

            public ResultSet getResultSet() {
                return resultSet;
            }

            public abstract T self();

            public abstract SQLTransactionResults build();
        }
    }
}
