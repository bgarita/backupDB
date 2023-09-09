package MySQL;

import Constants.SystemConstants;
import Mail.SendMail;
import Utilities.Archivos;
import Utilities.Bitacora;
import Utilities.Ut;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bgarita
 */
public class DoBackup {

    private List<String> dataBases; // Datases to backup
    private String targetFolder;    // Folder that contains the backup files
    private String user;            // Database user
    private String passw;           // Database user's password
    private int port;               // Database port
    private final String host;      // Server

    public DoBackup(List<String> dataBases, String targetFolder, String user, String passw, int port, String host) {
        this.dataBases = dataBases;
        this.targetFolder = targetFolder;
        this.user = user;
        this.passw = passw;
        this.port = port;
        this.host = host;
    }

    public void run() {
        System.out.println("Backup in progress..");

        new Bitacora().writeToLog(this.getClass().getName() + "--> " + "Backup started");

        String unique = Ut.getUniqueName(SystemConstants.DATE_FORMAT);
        String db;
        Boolean error = false;

        // Crear el archivo con los defaults
        String defaultsFileName = Ut.getUniqueName(SystemConstants.RANDOM_FORMAT) + ".cnf";
        File defaultsF = new File(defaultsFileName);
        Archivos archivo = new Archivos();
        try {
            archivo.stringToFile("[client]", defaultsFileName, false); // Texto, nombre del archivo, agregar
            archivo.stringToFile("password=" + "\"" + passw.trim() + "\"", defaultsFileName, true);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog("\n" + this.getClass().getName() + "--> " + ex.getMessage());
            defaultsF.delete();
            // Incluir código para enviar esto por correo.
            return;
        } // end try-catch

        // Cambiar después a una lista
        for (String dataBase : dataBases) {
            db = dataBase.trim();

            System.out.println("Processing DB " + db + "...");
            new Bitacora().writeToLog("\n" + this.getClass().getName() + "--> " + "Processing DB " + db + " ");

            /*
            Este string fue probado en mysql server 5.5, 5.6, 5.7, 8.0 y en MariaDB 10
            Si se tiene mysql y MariaDB en el mismo servidor habrá que configurar
            el path para que ubique la carpeta según el motor que se use.  Por ejemplo:
            si es MariaDB entonces en el path debe aparecer la ruta de MariaDB/bin
            pero si es mysql la ruta deberá ser el lugar donde se haya instalado el 
            servidor de mysql.
            
            Luego habrá que crear un parámetro para ubicar la herramienta según
            corresponda con el motor de base de datos.
            
            25/03/2023 Al hacer pruebaS se determinó que las funciones y procedimientos almacenados
            solo se respaldan si el usuario tiene los siguientes permisos:
            GRANT SELECT, SHOW VIEW, CREATE TEMPORARY TABLES, EVENT, TRIGGER ON *.* TO 'backup'@'localhost';
             */
            String tool = "mysqldump ";
            String cmd = tool
                    + "--defaults-file=" + defaultsFileName + " --user=" + user + " --host=" + host + " --port=" + port
                    + " --default-character-set=utf8 --single-transaction=TRUE --routines --events --triggers " + db;
            String fileName = targetFolder + "/" + unique + "_" + db + ".osais";
            String zipFileName = targetFolder + "/" + unique + "_" + db; // No debe llevar extensión
            
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                InputStream is = process.getInputStream();
                FileOutputStream fos = new FileOutputStream(fileName);

                int size = 1000;
                int len;
                byte[] buffer = new byte[size];

                len = is.read(buffer);
                /*
                Nota:
                Por alguna razón se está saliendo con este throw en la versión 
                compilada.  Desde el fuente si funciona.
                Pero esta misma rutina con ligeras diferencias dentro de osais
                y en la versión compilada si está funcionando.
                Hay que revisar con más tiempo. Bosco 04/08/2020
                */
                // Si len es negativo es porque no se pudo traer ningún dato
                if (len < 0){
                    throw new Exception("ERROR: Server not available!\nServer: " + host + "\nDatabase: " + db);
                } // end if

                while (len > 0) {
                    fos.write(buffer, 0, len);
                    len = is.read(buffer);
                } // end while

                fos.close();
                is.close();

                //archivo.zipFile(new File(fileName), new File(zipFileName));
                archivo.zipCryptFile(new File(fileName), new File(zipFileName + ".zip"));

                new File(fileName).delete();
                new Bitacora().writeToLog("\n" + this.getClass().getName() + "--> " + "Database " + db + " completed successfully");
            } catch (Exception ex) {
                error = true;
                new Bitacora().writeToLog("\n" + this.getClass().getName() + "--> " + ex.getMessage() + "\n");
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                
                // Enviar alerta
                FileSystem fs = FileSystems.getDefault();
                Path path = fs.getPath("bkmsg.html");
                String text = Ut.fileToString(path);
                text = text.replace("[msg]", "[DB=" + db + "] Backup failed! <br>See attachment for more details.").replace("blue", "red");
                
                SendMail mail = new SendMail();
                mail.setTitulo("Database Backup");
                mail.setDestinatario("bgarita@hotmail.com"); // Luego hay que parametrizar esto
                mail.setTexto(text);
                path = fs.getPath("bkp.log");
                mail.sendMail("bgarita@hotmail.com", path.getFileName().toFile().getAbsolutePath());
            } // end try-catch
        } // end for

        defaultsF.delete(); // Eliminar el archivo utilizado por mysqldump

        if (error) {
            return;
        } // end if

        System.out.println("Backup process completed successfully!");
        new Bitacora().writeToLog("\n" + this.getClass().getName() + "--> " + "Backup process completed successfully!");

        // Enviar el correo de notificación
        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath("bkmsg.html");
        /*
        10/06/2023
        Por alguna razón las tildes no salen bien en el correo, a pesar de que
        tanto el archivo se lee correctamente y en debug se ven las tildes y
        el html está configurado igual que la clase de java charset=iso-8859-1
        Y si uso la función que está comentada tampoco se convierte a tildes.
        IMPORTANTE:
        Comprobé que esto solo ocurre si corro el backup desde el IDE, la versión
        compilada si lo hace bien.
        */
        //String text = Ut.stringToHTML(Ut.fileToString(path));
        String text = Ut.fileToString(path);
        text = text.replace("[msg]", "Backup ended successfuly!");

        SendMail mail = new SendMail();
        mail.setDestinatario("bgarita@hotmail.com"); // Luego hay que parametrizar esto
        mail.setTitulo("Database Backup");
        mail.setTexto(text);
        mail.sendMail("bgarita@hotmail.com", "");
    } // end run

    public void setPassw(String passw) {
        this.passw = passw;
    }

    public void setDataBases(List<String> dataBases) {
        this.dataBases = dataBases;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPort(int port) {
        this.port = port;
    }

} // end DoBackup
