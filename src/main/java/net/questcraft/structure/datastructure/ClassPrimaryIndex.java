package net.questcraft.structure.datastructure;

/**
 * Used purely for instantiating a {@code TreeNodePrimaryIndex}
 *
 * @see net.questcraft.structure.datastructure.TreeNodePrimaryIndex
 */
public class ClassPrimaryIndex extends TreeNodePrimaryIndex {
    public ClassPrimaryIndex(Class<?> cls, String table, String column) {
        super(cls, table, column);
    }
}
