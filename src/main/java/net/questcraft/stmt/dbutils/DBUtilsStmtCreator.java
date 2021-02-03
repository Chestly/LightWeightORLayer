package net.questcraft.stmt.dbutils;

import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.transaction.SQLTransaction;
import net.questcraft.structure.DataTreeNode;
import net.questcraft.structure.datastructure.ClassNode;

public interface DBUtilsStmtCreator {
    SQLTransaction createTable(DataTreeNode<ClassNode> reference, boolean deepn) throws FatalORLayerException;

    SQLTransaction createDatabase(String name) throws FatalORLayerException;
}
