package net.questcraft;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.dbutils.DBUtilsStmtCreator;
import net.questcraft.stmt.dbutils.InnoDBUtilsStmtCreator;
import net.questcraft.stmt.metadata.SQLStmtMetaData;
import net.questcraft.stmt.metadata.StatementMetaData;
import net.questcraft.stmt.metadata.components.PureSQLComponent;
import net.questcraft.stmt.metadata.features.PureSQLFeature;
import net.questcraft.structure.ClassTreeNodeGenerator;
import net.questcraft.structure.DataTreeNode;
import net.questcraft.structure.TreeNodeGenerator;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.transaction.InnoDBTransaction;
import net.questcraft.transaction.SQLTransaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

import static net.questcraft.transaction.SQLTransaction.SQLTransactionResults.TransactionResultType.SUCCESS;

public class ORLayerDBUtils {
    private final ORSetup.DBInformation information;

    private static final String MANIFEST_LOC = "manifest" + File.separator;

    public ORLayerDBUtils(ORSetup.DBInformation information) {
        this.information = information;
    }

    public ORSetup.DBInformation createDB(String name) throws FatalORLayerException {
        DBUtilsStmtCreator stmtCreator = new InnoDBUtilsStmtCreator(this.information.toDBServer());

        SQLTransaction transaction = stmtCreator.createDatabase(name);

        try (SQLTransaction.SQLTransactionResults results = transaction.execute()) {
            JDBCURL url = new JDBCURL(this.information.getUrl(), name);
            return new ORSetup.DBInformation(url, this.information.getPassword(), this.information.getUsername());
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    public boolean createTable(String name) throws FatalORLayerException {
        return this.createTable(name, MANIFEST_LOC);
    }

    public boolean createTable(String name, String location) throws FatalORLayerException {
        final URL resource = getClass().getClassLoader().getResource(location + name);
        if (resource == null) throw new FatalORLayerException("You must provide a manifest file in your resources!");
        File file = new File(resource.getFile());

        try {
            Scanner scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();

            while (scanner.hasNextLine()) builder.append(scanner.nextLine());

            SQLTransaction transaction = new InnoDBTransaction(this.information)
                    .addStmt(new SQLStmtMetaData(StatementMetaData.StatementMetaType.MODIFY).
                            add(new PureSQLComponent.PureSQLStmtComponentBuilder().feature(new PureSQLFeature(builder.toString())).build()));

            try (SQLTransaction.SQLTransactionResults execute = transaction.execute()) {
                if (execute.resultType().equals(SUCCESS)) return true;
                else throw new FatalORLayerException(execute.exception());
            } catch (Exception e) {
                throw new FatalORLayerException(e);
            }
        } catch (FileNotFoundException e) {
            throw new FatalORLayerException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> boolean createTable(Class<T> reference, boolean deep) throws FatalORLayerException {
        DBUtilsStmtCreator stmtCreator = new InnoDBUtilsStmtCreator(this.information);
        TreeNodeGenerator<Class<T>> generator = new ClassTreeNodeGenerator<>();
        DataTreeNode<ClassNode> classTreeNode = (DataTreeNode<ClassNode>) generator.generate(reference);

        SQLTransaction transaction = stmtCreator.createTable(classTreeNode, deep);

        try (SQLTransaction.SQLTransactionResults results = transaction.execute()) {
            return !results.failed();
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

}
