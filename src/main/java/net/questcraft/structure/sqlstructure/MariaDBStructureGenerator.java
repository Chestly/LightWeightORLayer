package net.questcraft.structure.sqlstructure;

import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.stmt.metadata.SQLStmtMetaData;
import net.questcraft.stmt.metadata.StatementMetaData;
import net.questcraft.stmt.metadata.components.DescribeTableStmtComponent;
import net.questcraft.stmt.metadata.features.TableClauseFeature;

import static net.questcraft.stmt.metadata.StatementMetaData.StatementMetaType.QUERY;

public class MariaDBStructureGenerator implements SQLStructureGenerator {
    private final ORSetup.DBInformation information;

    public MariaDBStructureGenerator(ORSetup.DBInformation information) {
        this.information = information;
    }

    @Override
    public SQLTreeNode create(ClassNode root) throws FatalORLayerException {
        return this.recursivelyCreate(root, new SQLTreeNode.SQLTreeNodeBuilder(root.table()));
    }

    private SQLTreeNode recursivelyCreate(final ClassNode javaTreeNode, final SQLTreeNode.SQLTreeNodeBuilder node) throws FatalORLayerException {
        StatementMetaData data = new SQLStmtMetaData(QUERY);
        data.add(new DescribeTableStmtComponent.DescribeTableStmtBuilder().feature(new TableClauseFeature(javaTreeNode.table())).build());

        //buildTODO This isnt necessary, but
//        try (SQLTransaction.SQLTransactionResults transaction = new MariaDBTransaction(this.information).addStmt(data).execute()) {
//            if (transaction.resultType() == SUCCESS)
//                while (transaction.resultSet().next()) {
//                    SQLColumn column = new SQLColumn.SQLColumnBuilder(
//                            transaction.resultSet().getString("Key").equalsIgnoreCase("pri"),
//                            transaction.resultSet().getString("field"),
//                            transaction.resultSet().ge
//
//                            )
//                    for (ClassTreeNode treeNode : javaTreeNode) {
//
//                    }
//                }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        for (ClassTreeNode treeNode : javaTreeNode) {
//            node.addChild(new SQLColumn.SQLColumnBuilder(javaTreeNode.getTable(), javaTreeNode.childRef(treeNode)), this.recursivelyCreate(javaTreeNode, new SQLTreeNode.SQLTreeNodeBuilder(treeNode.getTable())));
//
//        }
        return node.build();
    }
}
