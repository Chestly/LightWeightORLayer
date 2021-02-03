package net.questcraft.stmt;

import net.questcraft.transaction.SQLTransaction;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.structure.DataTreeNode;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.structure.datastructure.ObjectNode;

public interface StmtCreator {
    /**
     *
     * @param node
     * @return
     */
    SQLTransaction getCreate(DataTreeNode<ObjectNode> node) throws FatalORLayerException;
    SQLTransaction getUpdate(DataTreeNode<ObjectNode> node, Object primKey) throws FatalORLayerException;
    SQLTransaction getQuery(Object primVal, DataTreeNode<ClassNode> node) throws FatalORLayerException;
    SQLTransaction getDelete(Object primVal, DataTreeNode<ClassNode> node) throws FatalORLayerException;
}
