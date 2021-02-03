package net.questcraft;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.InnoDBSQLStmtBuilder;
import net.questcraft.stmt.metadata.StatementBuilder;
import net.questcraft.stmt.metadata.components.*;
import net.questcraft.stmt.metadata.features.AliasClauseValueFeature;
import net.questcraft.stmt.metadata.features.QueryWhereClauseFeature;
import net.questcraft.stmt.metadata.features.TableClauseFeature;

import net.questcraft.stmt.metadata.features.TableColumnFeature;
import net.questcraft.structure.aliasstructure.AliasedNode;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestStmtComponent {
    @Test
    public void testBuilder() throws FatalORLayerException {
        StatementBuilder builder = new InnoDBSQLStmtBuilder();

        Map<String, String> params = new HashMap<>();
        params.put("Column", "value");
        params.put("anotherColumn", "AnotherValue");

        Set<String> selectParams = new HashSet<>();
        selectParams.add("First one");
        selectParams.add("Second One");

        String str = builder.buildStmt(
                new SelectStmtComponent.SelectSmtBuilder().build(),
                new FromTableStmtComponent.FromTableStmtBuilder().features(new TableClauseFeature("Table")).build(),
                new QueryWhereClauseStmtComponent.QueryWhereClauseStmtBuilder().features(new QueryWhereClauseFeature()).build()

        );

        System.out.println(str);
    }


    @Test
    public void testJoins() throws FatalORLayerException {
        JoinStatementHandlerComponent.JoinStmtHandlerBuilder builder = new JoinStatementHandlerComponent.JoinStmtHandlerBuilder();
        String table1 = "table1";
        String table2 = "table2";
        String table3 = "table3";

        builder.addJoin(new LeftJoinStmtComponent.LeftJoinBuilder(new AliasedNode.Alias<>(new AliasClauseValueFeature(""), new TableClauseFeature("")))
                .feature(new TableClauseFeature(table3))
                .feature(new AliasClauseValueFeature())
                .feature(new TableColumnFeature(table3, "asdf"))
                .feature(new TableColumnFeature("table4", "asdf"))
                .build(), new AliasedNode.Alias<>(new AliasClauseValueFeature(""), new TableClauseFeature("")));
        builder.addJoin(new InnerJoinComponent.InnerJoinSmtBuilder(new AliasedNode.Alias<>(new AliasClauseValueFeature(""), new TableClauseFeature("")))
                .feature(new TableClauseFeature(table2))
                .feature(new AliasClauseValueFeature())
                .feature(new TableColumnFeature(table1, "asdf"))
                .feature(new TableColumnFeature(table3, "asdf"))
                .build(), new AliasedNode.Alias<>(new AliasClauseValueFeature(""), new TableClauseFeature("")));
        builder.addJoin(new InnerJoinComponent.InnerJoinSmtBuilder(new AliasedNode.Alias<>(new AliasClauseValueFeature(""), new TableClauseFeature("")))
                .feature(new TableClauseFeature(table1))
                .feature(new AliasClauseValueFeature())
                .feature(new TableColumnFeature(table1, "asdf"))
                .feature(new TableColumnFeature(table2, "asdf"))
                .build());

        System.out.println(builder.build().parse());
    }
}
