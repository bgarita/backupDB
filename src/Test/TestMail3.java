package Test;

import Mail.SendMail;


/**
 *
 * @author bgarita
 */
public class TestMail3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // ***************** ENVIO SIN ADJUNTOS *****************
        
        /*
        Parámetros:
        Título, Texto, Destinatario, Error, Mensaje de error
        */
        SendMail mail = 
                new SendMail("Prueba", "<h1>Hola</h1><h2>Sin adjuntos<h2>", "bgarita@hotmail.com", false, "");
        String[] archivos = {};
        mail.sendMail("ajasja@gmail.es", archivos);
    } // end main
    
}
