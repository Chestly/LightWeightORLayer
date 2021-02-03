package net.questcraft.structuretests;

import net.questcraft.annotations.SQLColumnName;
import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLPrimaryIndex;

import java.util.List;

@SQLNode("testTable2")
public class StructuredTestTable2 {
    @SQLPrimaryIndex private String username;
    private String password;
    @SQLColumnName("postID") private StructuredTestTable1 cardPost;
    //Generic in Iterable MUST be of type @SQLNode
    private StructuredTestTable3 profilePic;


    public StructuredTestTable2(String username, String password, StructuredTestTable1 cardPost, StructuredTestTable3 profilePic) {
        this.username = username;
        this.password = password;
        this.cardPost = cardPost;
        this.profilePic = profilePic;
    }

    public StructuredTestTable2() {
    }
}
