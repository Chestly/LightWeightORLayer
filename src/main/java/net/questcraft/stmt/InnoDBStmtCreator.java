package net.questcraft.stmt;

import com.google.common.annotations.Beta;
import net.questcraft.transaction.InnoDBTransaction;
import net.questcraft.ORSetup;
import net.questcraft.transaction.SQLTransaction;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.SQLStmtMetaData;
import net.questcraft.stmt.metadata.StatementMetaData;
import net.questcraft.stmt.metadata.components.*;
import net.questcraft.stmt.metadata.features.*;
import net.questcraft.structure.*;
import net.questcraft.structure.aliasstructure.AliasedNode;
import net.questcraft.structure.datastructure.*;

import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.questcraft.transaction.SQLTransaction.SQLTransactionResults.TransactionResultType.*;
import static net.questcraft.stmt.metadata.StatementMetaData.StatementMetaType.MODIFY;
import static net.questcraft.stmt.metadata.StatementMetaData.StatementMetaType.QUERY;

public class InnoDBStmtCreator implements StmtCreator {
    public static final String PREDICTED_AUTO_INCREMENT_QUERY = "SELECT AUTO_INCREMENT AS 'next' FROM information_schema.tables WHERE table_name = ?";
    protected final ORSetup.DBInformation information;

    public InnoDBStmtCreator(ORSetup.DBInformation information) {
        this.information = information;
    }

    @Override
    public SQLTransaction getCreate(DataTreeNode<ObjectNode> node) throws FatalORLayerException {
        return this.recursivelyCreateInsert(node, new InnoDBTransaction(this.information));
    }

    private SQLTransaction recursivelyCreateInsert(DataTreeNode<ObjectNode> node, SQLTransaction transaction) throws FatalORLayerException {
        for (ParentChildRelation<DataTreeNode<ObjectNode>> child : node) {
            this.recursivelyCreateInsert(child.getRelation(), transaction);
        }
        node.forOneToMany((relation -> this.recursivelyCreateInsert(relation.getRelation(), transaction)));

        StatementMetaData metaData = new SQLStmtMetaData(MODIFY);

        final InsertColumnsClauseFeature colsFeature = new InsertColumnsClauseFeature();
        final InsertValuesFeature valueFeature = new InsertValuesFeature();

        node.getData().getValues().forEach((column, object) -> {
            colsFeature.addColumn(new TableColumnFeature(node.getData().table(), column));
            valueFeature.addValue(object);
        });
        for (ParentChildRelation<DataTreeNode<ObjectNode>> relation : node) {
            colsFeature.addColumn(new TableColumnFeature(relation.getTable(), relation.getColumn()));

            final ObjectNode data = relation.getRelation().getData();

            if (data.getPrimaryIndex().getValue() == null) {
                Logger.getLogger("InnoDBStmtCreator").log(Level.WARNING, "This is a beta feature and might cause errors. To fix this issue please provide a primary key for the class: " + data.getCls().toString());
                final int value = this.retrievePredictedAutoIncrementedNext(new TableClauseFeature(data.table()));
                Logger.getLogger("InnoDBStmtCreator").log(Level.INFO, "Predicted next primary key(assuming AUTO_INCREMENT) is: " + value);


                valueFeature.addValue(value);
            } else
                valueFeature.addValue(data.getPrimaryIndex().getValue());
        }

        metaData.add(new InsertStmtComponent.InsertStmtBuilder()
                .feature(new TableClauseFeature(node.getData().table()))
                .feature(colsFeature)
                .feature(valueFeature)
                .build());
        transaction.addStmt(metaData);

        return transaction;
    }

    @Beta
    private int retrievePredictedAutoIncrementedNext(TableClauseFeature table) throws FatalORLayerException {
        SQLStmtMetaData metaData = new SQLStmtMetaData(QUERY);

        metaData.add(new PureSQLComponent.PureSQLStmtComponentBuilder().feature(new PureSQLFeature(PREDICTED_AUTO_INCREMENT_QUERY, table.getTable())).build());

        try (InnoDBTransaction.InnoDBTransactionResults results = (new InnoDBTransaction(this.information).addStmt(metaData).execute())) {
            if (results.failed()) throw results.exception();

            if (results.resultSet().next()) return results.resultSet().getInt("next");

            throw new FatalORLayerException("Failed the predicted next primary key for the table: " + table.getTable());
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    /**
     * Creates an Update Transaction with each child table as a separate update(not using Inner Joins).
     * <p>
     * Primary Keys are Immutable
     *
     * @param node    The ObjectTreeNode to create from
     * @param primKey The primary key to update too(Immutable)
     * @return The Transaction to run
     * @throws FatalORLayerException If fails to create the transaction
     * @see SQLTransaction
     * @see ObjectNode
     */
    @Override
    public SQLTransaction getUpdate(DataTreeNode<ObjectNode> node, Object primKey) throws FatalORLayerException {
        if (primKey == null) throw new FatalORLayerException("Cannot update with a null Primary Key");

        SQLTransaction innoDBTransaction = new InnoDBTransaction(this.information);
        this.recursivelyGetUpdate(node, innoDBTransaction, primKey);

        return innoDBTransaction;
    }

    private void recursivelyGetUpdate(DataTreeNode<ObjectNode> node, SQLTransaction transaction, Object primKey) throws FatalORLayerException {
        for (ParentChildRelation<DataTreeNode<ObjectNode>> child : node) {
            final DataTreeNode<ObjectNode> relation = child.getRelation();
            final Object value = relation.getData().getPrimaryIndex().getValue();

            if (value == null) {
                try {
                    Logger.getLogger("InnoDBStmtCreator").log(Level.WARNING, "Child primary key is null. Assuming that it is a auto_increment.");
                    final int next = retrievePredictedAutoIncrementedNext(new TableClauseFeature(child.getTable()));

                    this.recursivelyGetUpdate(relation, transaction, next);
                } catch (FatalORLayerException e) {
                    throw new FatalORLayerException("Caught an exception while either generating a new auto increment key or while retrieving the statement for that value. The  error is: " + e.getMessage());
                }
            } else this.recursivelyGetUpdate(relation, transaction, value);
        }

        this.handleOneToMany(node, transaction, primKey);

        StatementMetaData metaData = new SQLStmtMetaData(MODIFY);

        metaData.add(new UpdateStmtComponent.UpdateStmtBuilder()
                .feature(new TableClauseFeature(node.getData().table()))
                .build())
                .add(new SetStmtComponent.SetStmtBuilder()
                        .feature(this.recursivelyGetUpdateSetClause(node, new UpdateSetClauseFeature()))
                        .build())
                .add(new QueryWhereClauseStmtComponent.QueryWhereClauseStmtBuilder()
                        .feature(new QueryWhereClauseFeature().addQuery(node.getData().getPrimaryIndex().toTableColumnFeature(), primKey))
                        .build());


        transaction.addStmt(metaData);
    }

    private UpdateSetClauseFeature recursivelyGetUpdateSetClause(DataTreeNode<ObjectNode> node, UpdateSetClauseFeature feature) throws FatalORLayerException {
        for (String column : node.getData().getValues().keySet()) {
            if (!node.getData().getPrimaryIndex().getColumn().equals(column)) {
                Object value = node.getData().getValues().get(column);
                feature.value(new TableColumnFeature(node.getData().table(), column), value);
            }
        }
        return feature;
    }

    @SuppressWarnings("unchecked")
    private void handleOneToMany(DataTreeNode<ObjectNode> parent, SQLTransaction transaction, Object primaryKey) throws FatalORLayerException {
        assert parent instanceof MutableDataTreeNode : "Parent node must be mutable!";

        if (parent.hasOneToMany()) {
//            StatementMetaData parentQuery = new SQLStmtMetaData(QUERY);
//            SQLTransaction parentQueryTransaction = new InnoDBTransaction(this.information);
//
//            parentQuery.add(new SelectStmtComponent.SelectSmtBuilder().feature(new SelectParamsClauseFeature(
//                    this.recursivelyRetrieveValues(parent, new HashSet<>()))).build())
//                    .add(new FromTableStmtComponent.FromTableStmtBuilder().feature(new TableClauseFeature(parent.getData().getTable(), parent.getAlias().tableAlias())).build());
//
//            parentQuery.add(this.addJoins(new JoinStatementHandlerComponent.JoinStmtHandlerBuilder(), parent).build());
//
//            parentQuery.add(new WhereClauseStmtComponent.WhereClauseStmtBuilder().
//                    feature(new QueryWhereClauseFeature().addQuery(
//                            new TableColumnFeature(parent.getAlias().tableAlias().getValue(), parent.getData().getPrimaryIndex().getColumn()),
//                            parent.getData().getPrimaryIndex().getValue()))
//                    .build());
            try (SQLTransaction.SQLTransactionResults results = this.getQuery(parent.getData().getPrimaryIndex().getValue(), ((MutableDataTreeNode) parent).toClassNode()).execute()) {
                final ResultSet resultSet = results.resultSet();
                boolean beforeFirst = resultSet.isBeforeFirst();

                ObjectTreeNodeGenerator<Object> generator = new ObjectTreeNodeGenerator<>();
                final Object sqlObject = generator.fromSQL((Class<Object>) parent.getData().getCls(), parent, resultSet);
                if (sqlObject != null) {
                    DataTreeNode<ObjectNode> node = generator.generate(sqlObject);

                    parent.forOneToMany(relation -> {
                        if (results.resultType().equals(SUCCESS) && beforeFirst) {
                            transaction.addAll(this.getDifferences(parent, node).toTransaction());
                        } else if (!beforeFirst) {
                            transaction.addAll(this.getCreate(relation.getRelation()));
                        }
                    });

                } else throw new FatalORLayerException("Failed to find parent Data in the DataBase");
            } catch (Exception e) {
                throw new FatalORLayerException(e);
            }
        } else {

//            StatementMetaData parentQuery = new SQLStmtMetaData(QUERY);
//            SQLTransaction parentQueryTransaction = new InnoDBTransaction(this.information);
//
//            parentQuery.add(new SelectStmtComponent.SelectSmtBuilder().feature(new SelectParamsClauseFeature(
//                    this.recursivelyRetrieveValues(parent, new HashSet<>()))).build())
//                    .add(new FromTableStmtComponent.FromTableStmtBuilder().feature(new TableClauseFeature(parent.getData().getTable(), parent.getAlias().tableAlias())).build());
//
//            parentQuery.add(this.addJoins(new JoinStatementHandlerComponent.JoinStmtHandlerBuilder(), parent).build());
//
//            parentQuery.add(new WhereClauseStmtComponent.WhereClauseStmtBuilder().
//                    feature(new QueryWhereClauseFeature().addQuery(
//                            new TableColumnFeature(parent.getAlias().tableAlias().getValue(), parent.getData().getPrimaryIndex().getColumn()),
//                            parent.getData().getPrimaryIndex().getValue()))
//                    .build());

            try (SQLTransaction.SQLTransactionResults results = this.getQuery(parent.getData().getPrimaryIndex().getValue(), ((MutableDataTreeNode) parent).toClassNode()).execute()) {
                if (results.resultType().equals(FAIL)) throw results.exception();
                if (results.resultSet().isBeforeFirst()) {
                    ObjectTreeNodeGenerator<Object> generator = new ObjectTreeNodeGenerator<>();
                    final Object sqlObj = generator.fromSQL((Class<Object>) parent.getData().getCls(), ((MutableDataTreeNode<?>) parent).toClassNode(), results.resultSet());

                    if (sqlObj != null) {
                        DataTreeNode<ObjectNode> node = generator.generate(sqlObj);

                        node.forOneToMany(relation -> transaction.addAll(this.getDelete(relation.getRelation().getData().getPrimaryIndex().getValue(), ((MutableDataTreeNode<?>) relation.getRelation()).toClassNode())));
                    }
                }
            } catch (Exception e) {
                throw new FatalORLayerException(e);
            }
        }
    }

    private DBDifferences getDifferences(DataTreeNode<ObjectNode> primary, DataTreeNode<ObjectNode> secondary) throws FatalORLayerException {
        DBDifferences.DBDifferenceBuilder builder = new DBDifferences.DBDifferenceBuilder();

        primary.forOneToMany(primaryRelation -> {
            DataTreeNode<ObjectNode> primaryNode = primaryRelation.getRelation();
            final ObjectNode primaryNodeData = primaryNode.getData();

            secondary.forOneToMany(secondaryRelation -> {
                DataTreeNode<ObjectNode> secondaryNode = secondaryRelation.getRelation();
                final ObjectNode secondaryNodeData = secondaryNode.getData();

                if (primaryNodeData.getPrimaryIndex().getValue() != null && (primaryNodeData.getPrimaryIndex().getValue().equals(secondaryNodeData.getPrimaryIndex().getValue())) &&
                        primaryNodeData.getCls().equals(secondaryNodeData.getCls()) &&
                        !primaryNode.equals(secondaryNode)) {

                    builder.addUpdate(primaryNode);
                } else if (primaryNodeData.getPrimaryIndex().getValue() != null && primary.streamOneToMany().noneMatch(relation -> {
                    final ObjectNode relationData = relation.getRelation().getData();
                    if (relationData.getPrimaryIndex().getValue() != null)
                        return relationData.getPrimaryIndex().getValue().equals(secondaryNodeData.getPrimaryIndex().getValue()) &&
                                relationData.getCls().equals(secondaryNodeData.getCls());
                    return false;
                })) {
                    assert secondaryNode instanceof MutableDataTreeNode<?> : "Node must be of type MutableTreeNode to create Data to SQL differences";
                    builder.addDelete(new DBDifferences.ClassKey<>(
                                    secondaryNodeData.getCls(),
                                    secondaryNodeData.getPrimaryIndex().getValue()),
                            ((MutableDataTreeNode<ObjectNode>) secondaryNode).toClassNode());
                }
            });

//            if (primaryNodeData.getPrimaryIndex().getValue() != null && secondary.streamOneToMany().noneMatch(relation ->
//                    relation.getRelation().getData().getPrimaryIndex().getValue().equals(primaryNodeData.getPrimaryIndex().getValue()) &&
//                    relation.getRelation().getData().getCls().equals(primaryNodeData.getCls()))) {
//                assert secondaryNode instanceof MutableDataTreeNode<?> : "Node must be of type MutableTreeNode to create Data to SQL differences";
//                builder.addDelete(new DBDifferences.ClassKey<>(
//                                secondaryNodeData.getCls(),
//                                secondaryNodeData.getPrimaryIndex().getValue()),
//                        ((MutableDataTreeNode<ObjectNode>) secondaryNode).toClassNode());
//            }


            if (primaryNodeData.getPrimaryIndex().getValue() == null || secondary.streamOneToMany().noneMatch(relation -> relation.getRelation().getData().getPrimaryIndex().getValue().equals(primaryNodeData.getPrimaryIndex().getValue()) &&
                    relation.getRelation().getData().getCls().equals(primaryNodeData.getCls()))) {
                builder.addCreate(primaryNode);
            }
        });

        return builder.build(this.information);
    }

    private static class DBDifferences extends InnoDBStmtCreator {
        private final Set<DataTreeNode<ObjectNode>> toUpdate;
        private final Set<DataTreeNode<ObjectNode>> toCreate;
        private final Map<ClassKey<?>, DataTreeNode<ClassNode>> toDelete;

        public DBDifferences(ORSetup.DBInformation information, DBDifferenceBuilder builder) {
            super(information);
            this.toUpdate = builder.toUpdate;
            this.toCreate = builder.toCreate;
            this.toDelete = builder.toDelete;
        }

        public SQLTransaction toTransaction() throws Exception {
            SQLTransaction transaction = new InnoDBTransaction(this.information);

            for (DataTreeNode<ObjectNode> objectTreeNode : this.toUpdate) {
                transaction.addAll(this.getUpdate(objectTreeNode, objectTreeNode.getData().getPrimaryIndex().getValue()));
            }
            for (DataTreeNode<ObjectNode> objectTreeNode : this.toCreate) {
                transaction.addAll(this.getCreate(objectTreeNode));
            }
            for (ClassKey<?> key : this.toDelete.keySet()) {
                transaction.addAll(this.getDelete(key.getKey(), this.toDelete.get(key)));
            }

            return transaction;
        }

        private static class DBDifferenceBuilder {
            private final Set<DataTreeNode<ObjectNode>> toUpdate;
            private final Set<DataTreeNode<ObjectNode>> toCreate;
            private final Map<ClassKey<?>, DataTreeNode<ClassNode>> toDelete;

            public DBDifferenceBuilder() {
                this.toUpdate = new HashSet<>();
                this.toCreate = new HashSet<>();
                this.toDelete = new HashMap<>();
            }

            public DBDifferenceBuilder addUpdate(DataTreeNode<ObjectNode> value) {
                this.toUpdate.add(value);
                return this;
            }

            public DBDifferenceBuilder addCreate(DataTreeNode<ObjectNode> value) {
                this.toCreate.add(value);
                return this;
            }

            public DBDifferenceBuilder addDelete(ClassKey<?> key, DataTreeNode<ClassNode> value) {
                this.toDelete.put(key, value);
                return this;
            }

            public DBDifferences build(ORSetup.DBInformation information) {
                return new DBDifferences(information, this);
            }
        }

        private static class ClassKey<T> {
            private final Class<T> cls;
            private final Object key;

            public ClassKey(Class<T> cls, Object key) {
                this.cls = cls;
                this.key = key;
            }

            public Class<T> getCls() {
                return cls;
            }

            public Object getKey() {
                return key;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ClassKey<?> classKey = (ClassKey<?>) o;
                return Objects.equals(cls, classKey.cls) &&
                        Objects.equals(key, classKey.key);
            }

            @Override
            public int hashCode() {
                return Objects.hash(cls, key);
            }
        }
    }


    @Override
    public SQLTransaction getQuery(Object primVal, DataTreeNode<ClassNode> node) throws FatalORLayerException {
        StatementMetaData metaData = new SQLStmtMetaData(QUERY);

        metaData.add(new SelectStmtComponent.SelectSmtBuilder()
                .feature(new SelectParamsClauseFeature(
                        this.recursivelyRetrieveValues(node, new HashSet<>()))).build())
                .add(new FromTableStmtComponent.FromTableStmtBuilder().feature(new TableClauseFeature(node.getData().table(), node.getAlias().tableAlias())).build());

        metaData.add(this.addJoins(new JoinStatementHandlerComponent.JoinStmtHandlerBuilder(), node).build());

        metaData.add(new QueryWhereClauseStmtComponent.QueryWhereClauseStmtBuilder().
                feature(new QueryWhereClauseFeature().addQuery(new TableColumnFeature(node.getAlias().tableAlias().getValue(), node.getData().getPrimaryIndex().getColumn()), primVal))
                .build());

        InnoDBTransaction innoDBTransaction = new InnoDBTransaction(this.information);
        innoDBTransaction.addStmt(metaData);

        return innoDBTransaction;
    }

    private Set<TableColumnFeature> recursivelyRetrieveValues(DataTreeNode<?> node, Set<TableColumnFeature> columns) throws FatalORLayerException {
        for (ParentChildRelation<? extends DataTreeNode<?>> child : node) {
            this.recursivelyRetrieveValues(child.getRelation(), columns);
        }

        node.forOneToMany(relation -> {
            this.recursivelyRetrieveValues(relation.getRelation(), columns);
        });

        Set<String> dataColumns;

        if (node.getData() instanceof ObjectNode) dataColumns = ((ObjectNode) node.getData()).getValues().keySet();
        else if (node.getData() instanceof ClassNode) dataColumns = ((ClassNode) node.getData()).getValues().keySet();
        else throw new FatalORLayerException("DataTreeNode type must be of either ObjectNode or ClassNode");

        for (String column : dataColumns) {
            if (!node.getAlias().hasColumnAlias(column))
                throw new FatalORLayerException("Failed to find column alias for column: " + column + " of class: " + node.getData().getCls().toString() + " This is most likely a internal error.");

            columns.add(new TableColumnFeature(
                    node.getAlias().tableAlias().getValue(),
                    column,
                    node.getAlias().columnAlias(column).wrap()));
        }


        return columns;
    }

    private <T extends DataNode<?>> JoinStatementHandlerComponent.JoinStmtHandlerBuilder addJoins(JoinStatementHandlerComponent.JoinStmtHandlerBuilder stmtHandler, DataTreeNode<T> node) throws FatalORLayerException {
        return recursivelyAddJoins(stmtHandler, node, true);
    }

    private <T extends DataNode<?>> JoinStatementHandlerComponent.JoinStmtHandlerBuilder recursivelyAddJoins(JoinStatementHandlerComponent.JoinStmtHandlerBuilder stmtHandler, DataTreeNode<T> node, boolean first) throws FatalORLayerException {

        for (ParentChildRelation<DataTreeNode<T>> relation : node) {
            final DataTreeNode<T> child = relation.getRelation();

            this.recursivelyAddJoins(stmtHandler, child, false);

            stmtHandler.addJoin(new LeftJoinStmtComponent.LeftJoinBuilder(new AliasedNode.Alias<>(
                    child.getAlias().tableAlias(),
                    new TableClauseFeature(child.getData().getPrimaryIndex().getTable())))

                    .feature(new TableClauseFeature(child.getData().table()))
                    .feature(child.getAlias().tableAlias())
                    .feature(new TableColumnFeature(node.getAlias().tableAlias().getValue(), relation.getColumn()))
                    .feature(new TableColumnFeature(child.getAlias().tableAlias().getValue(), child.getData().getPrimaryIndex().getColumn()))
                    .build(), first ? null : new AliasedNode.Alias<>(node.getAlias().tableAlias(), new TableClauseFeature(node.getData().table())));
        }
        node.forOneToMany(relation -> {
            DataTreeNode<T> descendant = relation.getRelation();

            this.recursivelyAddJoins(stmtHandler, descendant, false);

             final T descendantData = descendant.getData();
            final String table = descendantData.table();

            stmtHandler.addJoin(new LeftJoinStmtComponent.LeftJoinBuilder(new AliasedNode.Alias<>(
                    descendant.getAlias().tableAlias(),
                    new TableClauseFeature(descendant.getData().getPrimaryIndex().getTable())))

                    .feature(new TableClauseFeature(table))
                    .feature(descendant.getAlias().tableAlias())
                    .feature(new TableColumnFeature(descendant.getAlias().tableAlias().getValue(), relation.getRelationalChildColumn()))
                    .feature(new TableColumnFeature(node.getAlias().tableAlias().getValue(), descendantData.getPrimaryIndex().getColumn()))
                    .build(), first ? null : new AliasedNode.Alias<>(node.getAlias().tableAlias().getValue(), new TableClauseFeature(node.getData().table())));
        });
        return stmtHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SQLTransaction getDelete(Object primVal, DataTreeNode<ClassNode> node) throws FatalORLayerException {
        final SQLTransaction query = this.getQuery(primVal, node);
        try (SQLTransaction.SQLTransactionResults results = query.execute()) {
            ObjectTreeNodeGenerator<Object> generator = new ObjectTreeNodeGenerator<>();

            if (results.resultType().equals(FAIL)) throw results.exception();
            final Object sqlStructure = generator.fromSQL((Class<Object>) node.getData().getCls(), ((MutableDataTreeNode<?>) node).toClassNode(), results.resultSet());
            if (sqlStructure == null) return new InnoDBTransaction(this.information);
            final DataTreeNode<ObjectNode> sqlTree = generator.generate(sqlStructure);

            return this.recursivelyCreateDelete(sqlTree, new InnoDBTransaction(this.information));
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    private SQLTransaction recursivelyCreateDelete(DataTreeNode<ObjectNode> sqlTree, final SQLTransaction transaction) throws FatalORLayerException {
        for (ParentChildRelation<DataTreeNode<ObjectNode>> relation : sqlTree) {
            this.recursivelyCreateDelete(relation.getRelation(), transaction);
        }
        sqlTree.forOneToMany(relation -> this.recursivelyCreateDelete(relation.getRelation(), transaction));

        StatementMetaData metaData = new SQLStmtMetaData(MODIFY);

        metaData.add(new DeleteRowStmtComponent.DeleteRowStmtBuilder()
                .feature(new TableClauseFeature(sqlTree.getData().table()))
                .build());
        metaData.add(new QueryWhereClauseStmtComponent.QueryWhereClauseStmtBuilder()
                .feature(new QueryWhereClauseFeature().addQuery(sqlTree.getData().getPrimaryIndex().toTableColumnFeature(), sqlTree.getData().getPrimaryIndex().getValue()))
                .build());

        transaction.addStmt(metaData);

        return transaction;
    }

}
