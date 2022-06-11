package Test;

import Mail.SendMail;
import java.io.File;


/**
 *
 * @author bgarita
 */
public class TestMail1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // ***************** ENVIO CON UNA LISTA DE ADJUNTOS *****************
        SendMail mail = new SendMail();
        File f = new File("C:/Java Programs/BackupDB/manifest.mf");
        File f2 = new File("C:/Java Programs/BackupDB/bkp.log");
        String[] archivos = {f.getAbsolutePath(), f2.getAbsolutePath()};
        mail.setDestinatario("bgarita@hotmail.com");
        mail.setTitulo("Prueba");
        String texto = "<h1>Hola</h1><h2>This is jus a test<h2>";
        mail.setTexto(texto);
        mail.sendMail("ajasja@gmail.es", archivos);
    } // end main
    
}
