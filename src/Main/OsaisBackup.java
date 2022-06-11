package Main;

import MySQL.DoBackup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        String sourceFile, targetFile, userFile, user, passw, destino;
        Properties DBList, targetFolder, backupUser;
        InputStream input, input2, input3;
        int port;
        String host;

        sourceFile = "backupDB.properties";
        targetFile = "destinoF.properties";
        userFile   = "backupUser.properties";

        DBList = new Properties();
        targetFolder = new Properties();
        backupUser = new Properties();

        try {
            input = new FileInputStream(sourceFile);    // Lectura
            input2 = new FileInputStream(targetFile);   // Lectura
            input3 = new FileInputStream(userFile);     // Lectura
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OsaisBackup.class.getName()).log(Level.SEVERE, null, ex);
            File f = new File(sourceFile);
            System.out.println(f.getAbsolutePath());
            f = new File(targetFile);
            System.out.println(f.getAbsolutePath());
            return;
        } // end try-catch 

        try {
            DBList.load(input);
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
            
            Enumeration<?> en = DBList.propertyNames();
            List<String> databases = new ArrayList<>();
            
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String value = DBList.getProperty(key);
                databases.add(value);
            } // end while

            DoBackup backup = 
                    new DoBackup(databases, destino, user, passw, port, host);
            backup.run();
            
            input.close();
            input2.close();
        } catch (Exception ex) {
            Logger.getLogger(OsaisBackup.class.getName()).log(Level.SEVERE, null, ex);
        } // end try-catch

    } // end main
    
} // end class

