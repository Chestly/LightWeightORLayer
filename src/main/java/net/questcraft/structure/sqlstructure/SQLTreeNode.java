package net.questcraft.structure.sqlstructure;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SQLTreeNode {
    private final String table;

    private final SQLColumn[] sqlColumns;
    private final Map<SQLColumn, SQLTreeNode> children;

    public SQLTreeNode(SQLTreeNodeBuilder builder) {
        this.table = builder.table;
        this.sqlColumns = builder.sqlColumns;
        this.children =  builder.children;
    }

    @Nullable
    public SQLTreeNode getChildByColumn(String columnName) {
        for (SQLColumn sqlColumn : this.children.keySet()) {
            if (sqlColumn.getName().equals(columnName)) return this.children.get(sqlColumn);
        }
        return null;
    }

    public static class SQLTreeNodeBuilder {
        private final String table;
        private SQLColumn[] sqlColumns;
        private Map<SQLColumn, SQLTreeNode> children;

        public SQLTreeNodeBuilder(String table) {
            this.table = table;
        }

        public SQLTreeNodeBuilder self() {
            return this;
        }

        public SQLTreeNode build() {
            return new SQLTreeNode(this);
        }

        public SQLTreeNodeBuilder addSQLColumn(SQLColumn column) {
            if (this.sqlColumns.length < this.sqlColumns.length + 1) {
                SQLColumn[] expectedBuf = new SQLColumn[this.sqlColumns.length + 1];
                System.arraycopy(this.sqlColumns, 0, expectedBuf, 0, this.sqlColumns.length);
                this.sqlColumns = expectedBuf;
            }
            this.sqlColumns[this.sqlColumns.length - 1] = column;

            return self();
        }

        public SQLTreeNodeBuilder addChild(SQLColumn column, SQLTreeNode child) {
            this.children.put(column, child);
            return self();
        }
    }
}
