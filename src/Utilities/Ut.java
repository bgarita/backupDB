package Utilities;

import Constants.SystemConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JTable;

/**
 *
 * @author bgarita
 */
public class Ut {
    /**
     * Retorna un nombre único ideal para nombres de archivo (type=1), nombres
     * de transacción, nombres de sesión web (type=2).
     * @param type int 1=Nombre único ideal para archivos, 2=Nombre único universal (128 bits)
     * @return String unique name
     */
    public static String getUniqueName(int type){
        String uniqueName;
        
        // Set up a default for type parameter
        if (type != SystemConstants.DATE_FORMAT && type != SystemConstants.RANDOM_FORMAT){
            type = SystemConstants.DATE_FORMAT;
        } // end if
        
        if (type == SystemConstants.DATE_FORMAT){
            String fecha = Ut.dtoc(new Date());
            uniqueName = fecha.replaceAll("/", "-") + " " + Ut.getCurrentTime().replaceAll(":", " ");
        } else {
            uniqueName = UUID.randomUUID().toString();
        } // end if-else
        
        return uniqueName;
    } // end uniqueName
    
    
    /**
     * Autor: Bosco Garita 12/09/2009 Este método convierte de cal a caracter.
     *
     * @param Dfecha objeto de tipo Date
     * @return String con el formato "dd/mm/aaaa"
     */
    public static String dtoc(Date Dfecha) {
        Calendar cal = GregorianCalendar.getInstance();

        if (Dfecha == null) {
            return "  /  /    ";
        } // end if

        cal.setTime(Dfecha);

        String dia, mes, año;
        dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
        año = String.valueOf(cal.get(Calendar.YEAR));
        dia = dia.length() < 2 ? "0" + dia : dia;
        mes = mes.length() < 2 ? "0" + mes : mes;

        return dia + "/" + mes + "/" + año;
    } // end dtoc
    
    /**
     * @author: Bosco Garita 15/07/2018 Este método convierte una fecha y hora a
     * String y retorna solamente la hora.
     * @return String con el formato "hh:mm:ss AM/PM"
     */
    public static String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("hh:mm:ss a");
        return f.format(d);
    } // end getCurrentTime
    
    
    /**
     * Devuelve varias características que son prácticas a la hora de
     * desarrollar aplicaciones. Algunas de ellas son de uso exclusivo en
     * Windows XP.
     *
     * @param prop Característica (ver las constantes de Utilitarios)
     * @return String característica deseada
     */
    public static String getProperty(int prop) {
        String name = null;
        switch (prop) {
            case SystemConstants.USER_NAME:
                name = System.getProperty("user.name");
                break;
            case SystemConstants.USER_DIR:
                name = System.getProperty("user.dir");
                break;
            case SystemConstants.USER_HOME:
                name = System.getProperty("user.home");
                break;
            case SystemConstants.TMPDIR:
                name = System.getProperty("java.io.tmpdir");
                break;
            case SystemConstants.OS_NAME:
                name = System.getProperty("os.name");
                break;
            case SystemConstants.OS_VERSION:
                name = System.getProperty("os.version");
                break;
            case SystemConstants.FILE_SEPARATOR:
                name = System.getProperty("file.separator");
                break;
            case SystemConstants.PATH_SEPARATOR:
                name = System.getProperty("path.separator");
                break;
            case SystemConstants.LINE_SEPARATOR:
                name = System.getProperty("line.separator");
                break;
            case SystemConstants.WINDIR:
                if (System.getProperty("os.name").equalsIgnoreCase("Windows XP")) {
                    name = System.getenv("windir");
                } // end if
                break;
            case SystemConstants.SYSTEM32:
                if (System.getProperty("os.name").equalsIgnoreCase("Windows XP")) {
                    name = System.getenv("windir") + "\\system32";
                } // end if
                break;
            case SystemConstants.COMPUTERNAME:
                name = System.getenv("COMPUTERNAME");
                break;
            case SystemConstants.PROCESSOR_IDENTIFIER:
                name = System.getenv("PROCESSOR_IDENTIFIER");
                break;
            case SystemConstants.JAVA_VERSION:
                name = System.getenv("java.version");
                break;
        } // end switch
        return name;
    } // end getProperty
    
    /**
     * Autor: Bosco Garita. 30/01/2011. Busca un valor de tipo String en una
     * JTable. Si el valor es encontrado devolverá true y además seleccionará el
     * dato encontrado.
     *
     * @param table JTable en donde se realiza la búsqueda
     * @param valor String Valor a buscar
     * @param column int columna en donde se buscará
     * @return true, false
     */
    public static boolean seek(JTable table, String valor, int column) {
        if (valor == null) {
            return false;
        }
        // end if

        valor = valor.trim();
        boolean existe = false, toggle = false, extend = false;
        for (int row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, column) == null) {
                continue;
            }
            // end if
            if (table.getValueAt(row, column).toString().trim().equals(valor)) {
                existe = true;
                table.changeSelection(row, column, toggle, extend);
                break;

            } // end if
        } // end for
        return existe;
    } // end seek
    
    public static Properties getMailConfig() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        String configFile = "mail.properties"; // Debe estar en la carpeta de instalacion del sistema
        File mailConfig = new File(configFile);

        // Establecer los parámetros predeterminados
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.user", "osais311266@gmail.com");
        props.put("mail.smtp.clave", "rjzyqnamphqnomif");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        System.out.println("\nSending info mail...");
        System.out.println("Using parameter file: " + mailConfig.getAbsolutePath());
        
        if (mailConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(mailConfig)) {
                props.load(fis);
                fis.close();
            }
        } // end if
        return props;
    } // end getMailConfig
    
    
    public static String fileToString(Path path) {
        StringBuilder sb = new StringBuilder();
        try {
            if (path.toFile().exists()) {
                // Primero intento leer el archivo con UTF-8 y si se da un error
                // lo intento con ISO-8859-1
                Object[] content;
                try {
                    Stream<String> stream = Files.lines(path, Charset.forName("UTF-8"));
                    content = stream.toArray();
                } catch (Exception ex) {
                    Stream<String> stream = Files.lines(path, Charset.forName("ISO-8859-1"));
                    content = stream.toArray();
                } // end try-catch interno
                
                for (Object o : content) {
                    sb.append(o).append("\n");
                } // end for

            } else {
                sb.append("Archivo no encontrado.");
            } // end if-else
        } // end fileToString
        catch (Exception ex) {
            Logger.getLogger(Ut.class.getName()).log(Level.SEVERE, null, ex);
            sb.append(ex.getMessage());
        }
        return sb.toString();
    } // end fileToString
} // end class
