package net.questcraft.structure.sqlstructure;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.structure.datastructure.ClassNode;

public interface SQLStructureGenerator {
    /**
     * Creates a SQLTreeNode by Querying the DataBase at the root table specified and
     * uses the ClassTreeNode for all OneToMany and Child relationships. If these are incorrect
     * then the SQLTreeNode will be off.
     *
     * @param root The root table to start at and generate from
     * @return The SQLTreeNode representing the table structure
     * @throws FatalORLayerException If a error is encountered
     *
     * @see SQLTreeNode
     */
    SQLTreeNode create(ClassNode root) throws FatalORLayerException;
}
