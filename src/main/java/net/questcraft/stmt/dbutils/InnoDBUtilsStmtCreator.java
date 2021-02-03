package net.questcraft.stmt.dbutils;

import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.SQLStmtMetaData;
import net.questcraft.stmt.metadata.StatementMetaData;
import net.questcraft.stmt.metadata.components.ColumnMetaDataStmtComponent.ColumnMetaDataStmtBuilder;
import net.questcraft.stmt.metadata.components.CreateDataBaseStmtComponent;
import net.questcraft.stmt.metadata.components.CreateTableStmtComponent.CreateTableStmtComponentBuilder;
import net.questcraft.stmt.metadata.features.*;
import net.questcraft.stmt.metadata.features.ColumnDataTypeFeature.DataType;
import net.questcraft.structure.DataTreeNode;
import net.questcraft.structure.ParentChildRelation;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.transaction.InnoDBTransaction;
import net.questcraft.transaction.SQLTransaction;

import static net.questcraft.stmt.metadata.StatementMetaData.StatementMetaType.MODIFY;

public class InnoDBUtilsStmtCreator implements DBUtilsStmtCreator {
    private final ORSetup.DBInformation information;

    public InnoDBUtilsStmtCreator(ORSetup.DBInformation information) {
        this.information = information;
    }

    @Override
    public SQLTransaction createTable(DataTreeNode<ClassNode> reference, boolean deep) throws FatalORLayerException {
        if (deep) return this.createTableStructure(reference);
        else return this.createTable(reference.getData());
    }

    private SQLTransaction createTableStructure(DataTreeNode<ClassNode> reference) throws FatalORLayerException {
        SQLTransaction transaction = new InnoDBTransaction(this.information).addAll(this.createTable(reference.getData()));

        for (ParentChildRelation<DataTreeNode<ClassNode>> relation : reference) {
            transaction.addAll(this.createTableStructure(relation.getRelation()));
        }

        reference.forOneToMany(relation -> transaction.addAll(this.createTableStructure(relation.getRelation())));

        return transaction;
    }

    private SQLTransaction createTable(ClassNode reference) throws FatalORLayerException {
        SQLTransaction transaction = new InnoDBTransaction(this.information);
        StatementMetaData metaData = new SQLStmtMetaData(MODIFY);
        CreateTableStmtComponentBuilder builder = new CreateTableStmtComponentBuilder();
        CreateTableColumnValuesFeature columnValuesFeature = new CreateTableColumnValuesFeature();

        builder.feature(new TableClauseFeature(reference.table())).feature(columnValuesFeature);

        for (String column : reference.getValues().keySet()) {
            ColumnMetaDataStmtBuilder columnMetaDataStmtBuilder = new ColumnMetaDataStmtBuilder();
            columnMetaDataStmtBuilder.feature(new TableColumnFeature.PureColumnFeature(column));
            columnMetaDataStmtBuilder.feature(ColumnDataTypeFeature.mapFromClass(reference.getValues().get(column)).conditionallyProvide(20, DataType.VARCHAR));
            columnMetaDataStmtBuilder.feature(this.modifiers(reference, column));

            columnValuesFeature.addColumn(columnMetaDataStmtBuilder.build());
        }

        return transaction.addStmt(metaData.add(builder.build()));
    }

    private TableColumnDataFeature modifiers(ClassNode reference, String column) throws FatalORLayerException {
        TableColumnDataFeature feature = new TableColumnDataFeature();


        if (reference.getPrimaryIndex().getColumn().equals(column)) {
            feature.addModifier(TableColumnDataFeature.ColumnModifier.PRIMARY);

            DataType type = DataType.mapFromClass(reference.getPrimaryIndex().getValueClass());

            switch (type) {
                case TINY_INT:
                case SMALL_INT:
                case INTEGER:
                case BIG_INT:
                case REAL:
                case FLOAT:
                case DOUBLE:
                    feature.addModifier(TableColumnDataFeature.ColumnModifier.INCREMENT);
            }
        }

        return feature;
    }


    @Override
    public SQLTransaction createDatabase(String name) throws FatalORLayerException {
        SQLTransaction transaction = new InnoDBTransaction(this.information);
        StatementMetaData metaData = new SQLStmtMetaData(MODIFY)
                .add(new CreateDataBaseStmtComponent.CreateDBStmtBuilder().feature(new DBNameFeature(name)).build());

        return transaction.addStmt(metaData);
    }
}
