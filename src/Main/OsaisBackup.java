package Main;

import MySQL.DoBackup;
import Utilities.Archivos;
import Utilities.Bitacora;
import Utilities.Props;
import java.io.File;
import java.io.IOException;
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
        String databaseProps = "backupDB.properties";
        String targetProps = "destinoF.properties";
        String userProps = "backupUser.properties";
        
        String user;
        String passw;
        String destino;
        
        Properties dbList;
        Properties targetFolder;
        Properties backupUser;

        int port;
        String host;

        try {
            dbList = Props.getProps(new File(databaseProps));
            targetFolder = Props.getProps(new File(targetProps));
            backupUser = Props.getProps(new File(userProps));

            if (targetFolder.isEmpty()) {
                throw new Exception("Target folder is empty. See " + targetProps);
            } // end if

            destino = targetFolder.getProperty("backup_folder");

            user = backupUser.getProperty("usr");
            int days;
            try {
                days = Integer.parseInt(targetFolder.getProperty("keep_file_days"));
            } catch (NumberFormatException ex) {
                days = 30;
            }
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

            // Eliminar los backups que superen los días de permanencia
            removeOldFiles(destino, days);

            DoBackup backup
                    = new DoBackup(databases, destino, user, passw, port, host);
            backup.run();

        } catch (Exception ex) {
            Logger.getLogger(OsaisBackup.class.getName()).log(Level.SEVERE, null, ex);
            File f = new File(databaseProps);
            System.out.println(f.getAbsolutePath());
            f = new File(targetProps);
            System.out.println(f.getAbsolutePath());
        } // end try-catch

    } // end main

    private static void removeOldFiles(String destino, int days) {
        System.out.println("Checking file life cycle... Keeping " + days + "days.");
        Archivos archivos = new Archivos();
        File folder = new File(destino);
        String[] children = folder.list();

        for (String f : children) {
            try {
                File backup = new File(folder + "/" + f);
                if (archivos.getAge(backup) > days) {
                    backup.delete();
                }
            } catch (IOException ex) {
                new Bitacora().writeToLog("\n" + OsaisBackup.class.getName() + "--> " + ex.getMessage());
                Logger.getLogger(OsaisBackup.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // end for
    } // end removOldFiles

} // end class

