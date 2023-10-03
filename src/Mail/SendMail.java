package Mail;

import Utilities.Bitacora;
import java.io.IOException;
import java.util.GregorianCalendar;

/**
 *
 * @author bosco, 06/10/2018
 */
public class SendMail {

    private String titulo;
    private String texto;
    private String destinatario;
    private boolean error;
    private String errorMsg;

    public SendMail() {
        this.titulo = "";
        this.texto = "";
        this.destinatario = "";
        this.error = false;
        this.errorMsg = "";
    } // end constructor

    public SendMail(String titulo, String texto, String destinatario, boolean error, String error_msg) {
        this.titulo = titulo;
        this.texto = texto;
        this.destinatario = destinatario;
        this.error = error;
        this.errorMsg = error_msg;
    } // end full contructor

    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * Envía un correo ya sea en formato de texto o HTML con una lista de adjuntos.
     * La lista puede venir vacía, con uno o más archivos.  Si la lista viene vacía
     * simplemente se envía el texto/HTML sin adjuntos.
     *
     * @author Bosco Garita, Octubre 2018
     * @param senderMask String se usa para enmascarar el remitente para que
     * quien reciba el correo responda a esta dirección y no al que realmente
     * está enviando.
     * @param archivos String[] arreglo de rutas de archivos a enviar
     * @return boolean true=Exito, false=Fallido
     */
    public boolean sendMail(String senderMask, String[] archivos) {
        Bitacora b = new Bitacora();
        MailSender sender = new MailSender();

        if (Correo.malformado(destinatario)) {
            this.errorMsg = "Correo mal formado " + destinatario + ". No fue enviado.";
            b.writeToLog("\n" + this.errorMsg
                    + GregorianCalendar.getInstance().getTime());
            this.error = true;

            return false;
        } // end if

        try {
            sender.initGMail();
            // Si hay máscara cambio el remitente
            if (!senderMask.trim().isEmpty()) {
                sender.setRemitente(senderMask);
            } // end if
            sender.sendAttachmentMail_GM(
                    destinatario, titulo, texto, archivos);
            if (sender.isError()) {
                // Aquí no se escribe en bitácora porque la clase MailSender ya lo hizo
                this.error = true;
                this.errorMsg = sender.getErrorMessage();
                return false;
            } // end if
            System.out.println("Message sent to " + destinatario);
        } catch (IOException ex) {
            this.error = true;
            this.errorMsg = "ERROR: " + ex.getMessage() + " " + destinatario + ". "
                    + "Correo no enviado.";
            b.writeToLog(this.errorMsg);
            return false;
        } // end try-catch // end try-catch

        // Si llega hasta acá significa que todo salió bien
        return true;
    } // end sendMail
    
    /**
     * Envía un correo ya sea en formato de texto o HTML con un archivo adjunto.
     * El parámetro archivo puede venir vacío, lo cual hará que se envíe el 
     * texto/HTML sin adjuntos.
     *
     * @author Bosco Garita, Octubre 2018
     * @param senderMask String se usa para enmascarar el remitente para que
     * quien reciba el correo responda a esta dirección y no al que realmente
     * está enviando. Pero esto solo funciona con correo propio, no con Gmail
     * u otros similares.
     * @param archivo String nombre ruta completa del adjunto
     * @return boolean true=Exito, false=Fallido
     */
    public boolean sendMail(String senderMask, String archivo) {
        String[] archivos = {archivo};
        return sendMail(senderMask, archivos);
    } // end sendMail
} // end class
