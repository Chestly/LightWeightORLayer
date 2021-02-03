package net.questcraft.structuretests;

import net.questcraft.annotations.*;

@SQLNode("testTable5")
@OneToManyRelationshipChild
public class StructuredTestTable5 {
    @SQLPrimaryIndex
    private Long id;
    @SQLChildRelationalColumn
    private Integer value;
    @SQLColumnName("third")
    private String randomValueThing;

    public StructuredTestTable5(Long id, Integer value, String randomValueThing) {
        this.id = id;
        this.value = value;
        this.randomValueThing = randomValueThing;
    }

    public StructuredTestTable5() {
    }
}
