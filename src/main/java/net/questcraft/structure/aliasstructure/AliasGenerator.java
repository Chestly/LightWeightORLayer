package net.questcraft.structure.aliasstructure;

import net.questcraft.structure.datastructure.ClassNode;

public interface AliasGenerator {
    AliasedNode generate(ClassNode node);


//    AliasClauseValueFeature generateJoin(JavaTreeNode<T> child);
//
//    AliasClauseValueFeature generateToColumn(JavaTreeNode<T> child, String column);
//
//    static AliasClauseValueFeature generateToColumn(AliasClauseValueFeature feature, String column) {
//        return feature.add(column);
//    }
//
//    AliasClauseValueFeature getAlias(TableClauseFeature location);
}
