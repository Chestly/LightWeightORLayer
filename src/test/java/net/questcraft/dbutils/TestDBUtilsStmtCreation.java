package net.questcraft.dbutils;

import net.questcraft.ORLayerUtils;
import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.dbutils.DBUtilsStmtCreator;
import net.questcraft.stmt.dbutils.InnoDBUtilsStmtCreator;
import net.questcraft.structure.ClassTreeNodeGenerator;
import net.questcraft.structure.DataTreeNode;
import net.questcraft.structure.TreeNodeGenerator;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.structuretests.StructuredTestTable2;
import net.questcraft.utils.TestingManagerUtils;
import org.junit.Test;

public class TestDBUtilsStmtCreation {
    final ORSetup.DBInformation information = TestingManagerUtils.testingDatabaseEnvironment();

    public TestDBUtilsStmtCreation() throws FatalORLayerException {
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTableStmtCreation() throws FatalORLayerException {
        DBUtilsStmtCreator creator = new InnoDBUtilsStmtCreator(this.information);
        TreeNodeGenerator<Class<StructuredTestTable2>> generator = new ClassTreeNodeGenerator<>();
        DataTreeNode<ClassNode> treeNode = (DataTreeNode<ClassNode>) generator.generate(StructuredTestTable2.class);

        creator.createTable(treeNode, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTableStructureStmtCreation() throws FatalORLayerException {
        DBUtilsStmtCreator creator = new InnoDBUtilsStmtCreator(this.information);
        TreeNodeGenerator<Class<StructuredTestTable2>> generator = new ClassTreeNodeGenerator<>();
        DataTreeNode<ClassNode> treeNode = (DataTreeNode<ClassNode>) generator.generate(StructuredTestTable2.class);

        creator.createTable(treeNode, true);
    }

    @Test
    public void apiTest() {
    }
}
