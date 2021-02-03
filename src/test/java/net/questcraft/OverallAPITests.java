package net.questcraft;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.exceptions.ORLayerException;
import net.questcraft.structuretests.*;
import net.questcraft.utils.TestingManagerUtils;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OverallAPITests {
    final ORSetup.DBInformation information = TestingManagerUtils.testingDatabaseEnvironment();;
    final ORLayerUtils util = ORSetup.configureORLayerUtils(information);


    public OverallAPITests() throws FatalORLayerException {
    }


    @Test
    public void testCreate() throws FatalORLayerException {
        StructuredTestTable2 test2 = new StructuredTestTable2("Username", "Sure bud",
                new StructuredTestTable1(10L, 20),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "20", "randomValueThing"));
                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(23L, 20, "intNow"));
                }}));

        util.create(test2);
    }

    @Test
    public void testUpdate() throws FatalORLayerException {
        StructuredTestTable2 test = new StructuredTestTable2("UserName", "Sure bud",
                new StructuredTestTable1(10L, 22),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "20", "randomValueThing"));

                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(24L, 20, "testAgain"));

                }}));

        util.update(test);
    }

    @Test
    public void testQuery() throws FatalORLayerException {
        StructuredTestTable2 username = util.query(StructuredTestTable2.class, "Username");
        System.out.println("YAY");
    }

    @Test
    public void testDelete() throws FatalORLayerException {
        util.delete(StructuredTestTable2.class, "Username");
    }

    @Test
    public void testQueryDirectSQL() throws ORLayerException, SQLException {
        ResultSet resultSet = util.querySQL("");
        while (resultSet.next()) {
            System.out.println("HI");
        }
    }

    @Test
    public void testModifyDirectSQL() throws ORLayerException {
        boolean success = util.modifySQL("INSERT INTO testTable2 (username, password) VALUES (?,?)", "meany", "otehrMeany");
        System.out.println(success);
    }

    @Test
    public void testJdbcURL() throws FatalORLayerException {
        System.out.println(JDBCURL.fromString("jdbc:mariadb://192.168.0.75:3306/SundtMemesDB").format());
    }

    @Test
    public void testResourceTableCreation() throws FatalORLayerException {
        final ORLayerDBUtils orLayerDBUtils = ORSetup.configureDBUtils(this.information);

        orLayerDBUtils.createTable("testTable1.sql");
    }

    @Test
    public void testTableCreationViaClass() throws FatalORLayerException {
        final ORLayerDBUtils orLayerDBUtils = ORSetup.configureDBUtils(this.information);

        orLayerDBUtils.createTable(StructuredTestTable1.class, false);
    }

    @Test
    public void testDBCreation() throws FatalORLayerException {
        final ORLayerDBUtils orLayerDBUtils = ORSetup.configureDBUtils(this.information);
        final ORSetup.DBInformation newDB = orLayerDBUtils.createDB("NewDB");

        final ORLayerUtils orLayerUtils = ORSetup.configureORLayerUtils(newDB);
        final ORLayerDBUtils utils = ORSetup.configureDBUtils(newDB);

        utils.createTable(StructuredTestTable1.class, false);
        orLayerUtils.create(new StructuredTestTable1(1L, 5));
        System.out.println(orLayerUtils.query(StructuredTestTable1.class, 1));
    }

}
