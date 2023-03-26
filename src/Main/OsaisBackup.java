package Main;

import MySQL.DoBackup;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bgarita
 */
public class OsaisBackup {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String sourceFile;
        String targetFile;
        String userFile;
        String user;
        String passw;
        String destino;
        Properties dbList;
        Properties targetFolder;
        Properties backupUser;
        
        
        
        int port;
        String host;

        sourceFile = "backupDB.properties";
        targetFile = "destinoF.properties";
        userFile = "backupUser.properties";

        dbList = new Properties();
        targetFolder = new Properties();
        backupUser = new Properties();

        try {
            InputStream input = new FileInputStream(sourceFile);    // Lectura
            InputStream input2 = new FileInputStream(targetFile);   // Lectura
            InputStream input3 = new FileInputStream(userFile);     // Lectura
        
            dbList.load(input);
            targetFolder.load(input2);
            backupUser.load(input3);

            if (targetFolder.isEmpty()) {
                throw new Exception("Target folder is empty. See " + targetFile);
            } // end if

            destino = targetFolder.getProperty("backup_folder");

            user = backupUser.getProperty("usr");
            passw = backupUser.getProperty("psw");
            port = Integer.parseInt(backupUser.getProperty("port"));
            host = backupUser.getProperty("host");

            Enumeration<?> en = dbList.propertyNames();
            List<String> databases = new ArrayList<>();

            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String value = dbList.getProperty(key);
                databases.add(value);
            } // end while

            DoBackup backup
                    = new DoBackup(databases, destino, user, passw, port, host);
            backup.run();

            input.close();
            input2.close();
        } catch (Exception ex) {
            Logger.getLogger(OsaisBackup.class.getName()).log(Level.SEVERE, null, ex);
            File f = new File(sourceFile);
            System.out.println(f.getAbsolutePath());
            f = new File(targetFile);
            System.out.println(f.getAbsolutePath());
        } // end try-catch

    } // end main

} // end class

