package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.components.JoinStatementHandlerComponent.JoinStatement;
import net.questcraft.stmt.metadata.features.AliasClauseValueFeature;
import net.questcraft.stmt.metadata.features.TableClauseFeature;
import net.questcraft.structure.aliasstructure.AliasedNode;
import org.jetbrains.annotations.Nullable;

public class InnerJoinComponent extends StatementComponent implements JoinStatement {
    /**
     * INNER JOIN ? AS ? ON ?=?
     *
     * Expected:
     * @see net.questcraft.stmt.metadata.features.TableClauseFeature
     * @see AliasClauseValueFeature
     * @see net.questcraft.stmt.metadata.features.TableColumnFeature
     * @see net.questcraft.stmt.metadata.features.TableColumnFeature
     */
    private final String stmt = "INNER JOIN" + FEATURE_HOLDER + FEATURE_HOLDER + "ON" + FEATURE_HOLDER + "="+ FEATURE_HOLDER;
    public static final int IDENTIFIER = 4;

    private final AliasedNode.Alias<TableClauseFeature> joinedTable;

    public InnerJoinComponent(InnerJoinSmtBuilder builder) {
        super(builder);
        this.joinedTable = builder.joinedTable;
    }

    @Override
    public String parse() {
        return replaceHolder(stmt, true);
    }

    @Nullable
    @Override
    public  AliasedNode.Alias<TableClauseFeature> getTable() {
        return this.joinedTable;
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class InnerJoinSmtBuilder extends StmtComponentBuilder<InnerJoinComponent.InnerJoinSmtBuilder> {
        private final AliasedNode.Alias<TableClauseFeature> joinedTable;

        public InnerJoinSmtBuilder(AliasedNode.Alias<TableClauseFeature> joinedTable) {
            this.joinedTable = joinedTable;
        }

        @Override
        public InnerJoinComponent.InnerJoinSmtBuilder self() {
            return this;
        }

        @Override
        public InnerJoinComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new InnerJoinComponent(this);
        }
    }
}
