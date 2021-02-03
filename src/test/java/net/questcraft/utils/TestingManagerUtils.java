package net.questcraft.utils;

import com.google.common.base.Joiner;
import net.questcraft.ORSetup;
import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.StatementMetaData;
import net.questcraft.transaction.SQLTransaction;

import java.util.List;
import java.util.stream.Collectors;

public class TestingManagerUtils {
    public static ORSetup.DBInformation testingDatabaseEnvironment() throws FatalORLayerException {
        return new ORSetup.DBInformation(
                ConfigReader.readString("url"),
                ConfigReader.readString("password"),
                ConfigReader.readString("username"));
    }

    public static String parseTransaction(SQLTransaction transaction) {
        List<String> parsed = transaction.getData().stream().map(StatementMetaData::parse).collect(Collectors.toList());
        return Joiner.on("\n").join(parsed);
    }

}
