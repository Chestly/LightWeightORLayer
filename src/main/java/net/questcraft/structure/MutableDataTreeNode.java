package net.questcraft.structure;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.structure.aliasstructure.AliasedNode;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.structure.datastructure.DataNode;
import net.questcraft.structure.datastructure.ObjectNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MutableDataTreeNode<T extends DataNode<?>> extends DataTreeNode<T> {
    public MutableDataTreeNode(@NotNull Builder<T> builder) {
        super(builder);
    }

    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public DataTreeNode<ClassNode> toClassNode() throws FatalORLayerException {
        if (this.getData() instanceof ClassNode) return (MutableDataTreeNode<ClassNode>) this;

        ClassTreeNodeGenerator<Object> generator = new ClassTreeNodeGenerator<>();
        return generator.generate((Class<Object>) this.getData().getCls(), this.getAlias().seed());
    }

    @Contract(pure = true)
    public DataTreeNode<ObjectNode> toObjectNode(Object root) throws FatalORLayerException {
        if (!root.getClass().equals(this.getData().getCls())) throw new FatalORLayerException("Given object must be of the same class as the root of this TreeNode");

        ObjectTreeNodeGenerator<Object> generator = new ObjectTreeNodeGenerator<>();
        return generator.generate(root, this.getAlias().seed());
    }

    public static class Builder<T extends DataNode<?>> extends DataTreeNode.Builder<T>{
        public Builder(@NotNull DataNode<T> dataTreeNode, @NotNull AliasedNode aliasedTreeNode) {
            super(dataTreeNode, aliasedTreeNode);
        }

        @Override
        public DataTreeNode<T> build() {
            return new MutableDataTreeNode<>(this);
        }
    }
}
