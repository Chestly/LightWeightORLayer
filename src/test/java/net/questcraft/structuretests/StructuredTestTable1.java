package net.questcraft.structuretests;

import net.questcraft.annotations.SQLColumnName;
import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLPrimaryIndex;

@SQLNode("testTable1")
public class StructuredTestTable1 {

    @SQLColumnName("id") @SQLPrimaryIndex() private Long testID;
    @SQLColumnName("title") private Integer thingTitle;
//    private StructuredTestTable4 nextOne;

    public StructuredTestTable1(Long testID, Integer thingTitle) {
        this.testID = testID;
        this.thingTitle = thingTitle;
    }

    public StructuredTestTable1() {
    }

    @Override
    public String toString() {
        return "StructuredTestTable1{" +
                "testID=" + testID +
                ", thingTitle=" + thingTitle +
                '}';
    }
}
