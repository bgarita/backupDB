package Test;

import Mail.SendMail;
import Utilities.Ut;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;


/**
 *
 * @author bgarita
 */
public class TestMail2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // ***************** ENVIO CON UN SOLO ADJUNTO *****************
        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath("bkmsg.html");
        String text = Ut.fileToString(path);
        text = text.replace("[msg]", "Backup ended successfuly!");
        
        SendMail mail = new SendMail();
        mail.setDestinatario("bgarita@hotmail.com");
        mail.setTitulo("Prueba");
        mail.setTexto(text);
        mail.sendMail("ajasja@gmail.es", "");
        
        text = Ut.fileToString(path);
        text = text.replace("[msg]", "Backup failed! <br>See attachment for more details.").replace("blue", "red");
        mail.setTexto(text);
        path = fs.getPath("bkp.log");
        mail.sendMail("ajasja@gmail.es", path.getFileName().toFile().getAbsolutePath());
    } // end main
    
}
