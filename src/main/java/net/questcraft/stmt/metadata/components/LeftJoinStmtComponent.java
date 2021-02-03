package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.components.JoinStatementHandlerComponent.JoinStatement;
import net.questcraft.stmt.metadata.features.AliasClauseValueFeature;
import net.questcraft.stmt.metadata.features.TableClauseFeature;
import net.questcraft.structure.aliasstructure.AliasedNode;

public class LeftJoinStmtComponent extends StatementComponent implements JoinStatement {
    /**
     * LEFT JOIN ? AS ? ON ?=?
     *
     * Expected:
     * @see net.questcraft.stmt.metadata.features.TableClauseFeature
     * @see AliasClauseValueFeature
     * @see net.questcraft.stmt.metadata.features.TableColumnFeature
     * @see net.questcraft.stmt.metadata.features.TableColumnFeature
     */
    private final String stmt = "LEFT JOIN" + FEATURE_HOLDER + FEATURE_HOLDER + "ON" + FEATURE_HOLDER + "="+ FEATURE_HOLDER;
    public static final int IDENTIFIER = 12;

    private final AliasedNode.Alias<TableClauseFeature> joinedTable;

    public LeftJoinStmtComponent(LeftJoinBuilder builder) {
        super(builder);
        this.joinedTable = builder.joinedTable;
    }

    @Override
    public String parse() {
        return replaceHolder(stmt, true);
    }

    @Override
    public AliasedNode.Alias<TableClauseFeature> getTable() {
       return this.joinedTable;
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class LeftJoinBuilder extends StatementComponent.StmtComponentBuilder<LeftJoinBuilder> {
        private final AliasedNode.Alias<TableClauseFeature> joinedTable;

        public LeftJoinBuilder(AliasedNode.Alias<TableClauseFeature> joinedTable) {
            this.joinedTable = joinedTable;
        }

        @Override
        public LeftJoinBuilder self() {
            return this;
        }

        @Override
        public LeftJoinStmtComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new LeftJoinStmtComponent(this);
        }
    }
}
