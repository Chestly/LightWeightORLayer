package net.questcraft.structure;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.structure.aliasstructure.AliasedNode;
import net.questcraft.structure.datastructure.DataNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class DataTreeNode<T extends DataNode<?>> implements TreeNode, Iterable<ParentChildRelation<DataTreeNode<T>>> {
    private final DataNode<T> dataTreeNode;


    private final AliasedNode aliasedTreeNode;


    private final Set<ParentChildRelation<DataTreeNode<T>>> children;


    private final Set<OneToManyRelation<DataTreeNode<T>>> oneToMany;


    public DataTreeNode(@NotNull Builder<T> builder) {
        this.dataTreeNode = builder.dataTreeNode;
        this.aliasedTreeNode = builder.aliasedTreeNode;
        this.children = builder.children;
        this.oneToMany = builder.oneToMany;
    }

    @NotNull
    @Override
    public Iterator<ParentChildRelation<DataTreeNode<T>>> iterator() {
        return new DataTreeNodeIterator();
    }

    @Override
    public void forEach(Consumer<? super ParentChildRelation<DataTreeNode<T>>> action) {
        for (ParentChildRelation<DataTreeNode<T>> relation : this) {
            action.accept(relation);
        }
    }

    public void forOneToMany(ExceptionalConsumer<? super OneToManyRelation<DataTreeNode<T>>> action) throws FatalORLayerException {
        try {
            for (OneToManyRelation<DataTreeNode<T>> relation : this.oneToMany) {
                action.accept(relation);
            }
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    public Stream<OneToManyRelation<DataTreeNode<T>>> streamOneToMany() {
        return this.oneToMany.stream();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public T getData() {
        return (T) dataTreeNode;
    }

    @NotNull
    public AliasedNode getAlias() {
        return aliasedTreeNode;
    }

    public boolean hasOneToMany() {
        return !this.oneToMany.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTreeNode<?> that = (DataTreeNode<?>) o;
        return Objects.equals(dataTreeNode, that.dataTreeNode) &&
                Objects.equals(aliasedTreeNode, that.aliasedTreeNode) &&
                Objects.equals(children, that.children) &&
                Objects.equals(oneToMany, that.oneToMany);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataTreeNode, aliasedTreeNode, children, oneToMany);
    }

    private class DataTreeNodeIterator implements Iterator<ParentChildRelation<DataTreeNode<T>>> {
        private final int MAX;
        private int current = 0;

        public DataTreeNodeIterator() {
            this.MAX = children().length;
        }

        @Override
        public boolean hasNext() {
            return this.current != MAX;
        }

        @Override
        public ParentChildRelation<DataTreeNode<T>> next() {
            return children()[this.current++];
        }

        @SuppressWarnings("unchecked")
        private ParentChildRelation<DataTreeNode<T>>[] children() {
            return (ParentChildRelation<DataTreeNode<T>>[]) children.toArray(new ParentChildRelation[0]);
        }
    }


    public abstract static class Builder<T extends DataNode<?>> {
        private final DataNode<T> dataTreeNode;
        private final AliasedNode aliasedTreeNode;

        private final Set<ParentChildRelation<DataTreeNode<T>>> children;
        private final Set<OneToManyRelation<DataTreeNode<T>>> oneToMany;

        public Builder(@NotNull DataNode<T> dataTreeNode, @NotNull AliasedNode aliasedTreeNode) {
            this.dataTreeNode = dataTreeNode;
            this.aliasedTreeNode = aliasedTreeNode;
            this.children = new HashSet<>();
            this.oneToMany = new HashSet<>();
        }

        public Builder<T> addChild(@NotNull ParentChildRelation<DataTreeNode<T>> treeNode) {
            this.children.add(treeNode);
            return this;
        }

        public Builder<T> addOneToMany(@NotNull OneToManyRelation<DataTreeNode<T>> relation) {
            this.oneToMany.add(relation);
            return this;
        }

        public abstract DataTreeNode<T> build();
    }
}
