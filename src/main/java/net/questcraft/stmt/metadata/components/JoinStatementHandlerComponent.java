package net.questcraft.stmt.metadata.components;

import com.google.common.base.Joiner;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.features.TableClauseFeature;
import net.questcraft.structure.aliasstructure.AliasedNode;

import java.util.*;

public class JoinStatementHandlerComponent extends StatementComponent {
    private final List<JoinStatement> joins;

    public static final int IDENTIFIER = 13;

    public JoinStatementHandlerComponent(JoinStmtHandlerBuilder builder) {
        super(builder);
        this.joins = builder.joins;
    }

    @Override
    public String parse() {
        List<String> toParse = new ArrayList<>();
        for (JoinStatement join : joins) {
            toParse.add(join.parse());
        }

        return Joiner.on(" ").join(toParse);
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class JoinStmtHandlerBuilder extends StatementComponent.StmtComponentBuilder<JoinStmtHandlerBuilder> {
        private final List<JoinStatement> joins;
        //String - Table dependant on : JoinStatement -  The dependency
        private final Set<TableDependency> dependencies;

        public JoinStmtHandlerBuilder() {
            this.joins = new ArrayList<>();
            this.dependencies = new HashSet<>();
        }

        @Override
        public JoinStmtHandlerBuilder self() {
            return this;
        }

        public JoinStmtHandlerBuilder addJoin(JoinStatement joinStatement) {
            if (this.joinsContains(joinStatement)) return this;

            this.joins.add(joinStatement);

            List<TableDependency> toRemove = new ArrayList<>();

            AliasedNode.Alias<TableClauseFeature> table = joinStatement.getTable();

            for (TableDependency dependency : this.dependencies) {
                if (dependency.getTable().equals(table)) {
                    this.recursivelyAddDependency(dependency, toRemove);
                }
            }
            for (TableDependency dependency : toRemove) {
                this.dependencies.remove(dependency);
            }

            return this;
        }

        public boolean dependenciesContains(JoinStatement statement) {
            if (!this.joins.contains(statement))
                for (TableDependency dependency : this.dependencies) {
                    if (dependency.getJoin().parse().equals(statement.parse())) return true;
                }
            return false;
        }

        public boolean joinsContains(JoinStatement statement) {
            for (JoinStatement join : this.joins) {
                if (join.parse().equals(statement.parse())) return true;
            }
            return false;
        }

        public JoinStmtHandlerBuilder addJoin(JoinStatement joinStatement, AliasedNode.Alias<TableClauseFeature> table) {
            if (table == null) this.addJoin(joinStatement);
            else if (!this.dependenciesContains(joinStatement)) {
                ArrayList<TableDependency> toRemove = new ArrayList<>();
                this.recursivelyAddDependency(new TableDependency(table, joinStatement), toRemove);
                for (TableDependency dependency : toRemove) {
                    this.dependencies.remove(dependency);
                }
            }

            return this;
        }

        //JoinStatement - The Dependency : table The table its dependant on
        private void recursivelyAddDependency(TableDependency tableDependency, List<TableDependency> toRemove) {
            boolean found = false;

            for (int i = 0; i < this.joins.size(); i++) {
                JoinStatement stmt = joins.get(i);
                if (stmt.getTable().equals(tableDependency.getTable()) && !this.joinsContains(tableDependency.getJoin())) {
                    found = true;
                    this.joins.add(i + 1, tableDependency.getJoin());
                    toRemove.add(tableDependency);
                }
            }

            for (TableDependency key : this.dependencies) {
                if (key.getTable().equals(tableDependency.getJoin().getTable())) {
                    this.recursivelyAddDependency(key, toRemove);
                }
            }

            if (!found) this.dependencies.add(tableDependency);
        }

        @Override
        public JoinStatementHandlerComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new JoinStatementHandlerComponent(this);
        }

        private static class TableDependency {
            private final AliasedNode.Alias<TableClauseFeature> table;
            private final JoinStatement join;

            public TableDependency(AliasedNode.Alias<TableClauseFeature> table, JoinStatement join) {
                this.table = table;
                this.join = join;
            }

            public AliasedNode.Alias<TableClauseFeature> getTable() {
                return table;
            }

            public JoinStatement getJoin() {
                return join;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                TableDependency that = (TableDependency) o;
                return Objects.equals(table, that.table) &&
                        Objects.equals(join, that.join);
            }

            @Override
            public int hashCode() {
                return Objects.hash(table, join);
            }
        }
    }

    /**
     * Marker interface for all Join Statements
     */
    public interface JoinStatement {
        String parse();

        AliasedNode.Alias<TableClauseFeature> getTable();
    }
}
