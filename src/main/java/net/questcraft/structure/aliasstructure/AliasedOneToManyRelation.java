package net.questcraft.structure.aliasstructure;

import net.questcraft.stmt.metadata.features.TableClauseFeature;
import net.questcraft.stmt.metadata.features.TableColumnFeature;
import net.questcraft.structure.OneToManyRelation;
import org.jetbrains.annotations.NotNull;

public class AliasedOneToManyRelation extends OneToManyRelation<AliasedNode> {
    private final AliasedNode relation;

    private final AliasedNode.Alias<TableClauseFeature> table;
    private final AliasedNode.Alias<TableColumnFeature> column;

    public AliasedOneToManyRelation(AliasedNode.Alias<TableColumnFeature> column, AliasedNode.Alias<TableClauseFeature> table, AliasedNode relation, String relationalChildColumn, AliasedNode relation1) {
        super(column.getValue().getPlaceHolder().getColumn(), table.getValue().getPlaceHolder().getTable(), relation, relationalChildColumn);
        this.table = table;
        this.column = column;
        this.relation = relation1;
    }

    @NotNull
    public AliasedNode.Alias<TableClauseFeature> getAliasTable() {
        return table;
    }

    @NotNull
    public AliasedNode.Alias<TableColumnFeature> getAliasColumn() {
        return column;
    }

    @Override
    public AliasedNode getRelation() {
        return this.relation;
    }

//    static AliasedOneToManyRelation buildFromClassRelation(OneToManyRelation<ClassNode> relation, AliasedNode treeNode) {
//        return new AliasedOneToManyRelation(relation.getColumn(), relation.getTable(), relation.getRelationalChildColumn(), treeNode);
//    }
}
