package Mail;

import Utilities.Bitacora;
import Utilities.Ut;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Esta clase tiene los métodos necesarios para enviar correos de TEXTO, HTML y
 * con archivo adjunto.
 *
 * @author bgarita
 */
public class MailSender {

    private String sServidorCorreo;
    private String remitente;
    private String[] asCorreoDestino;
    private String errorMessage = "";
    private boolean error;

    private Properties gmailProps;

    public MailSender() {
        error = false;
        errorMessage = "";
    }

    public boolean isError() {
        return error;
    }

    public void initGMail() throws FileNotFoundException, IOException {
        gmailProps = Ut.getMailConfig();
        sServidorCorreo = gmailProps.getProperty("mail.smtp.host");
        remitente = gmailProps.getProperty("mail.smtp.user");
    } // end initGMail

    // Cambiar el emisor de correo
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    } // end setsCorreoOrigen

    /**
     * Método público y estático que envía un correo a las direcciones indicadas
     * en el fichero de propiedades, desde la dirección indicada también en el
     * mismo fichero con el asunto y el contenido que se pasan como parámetros.
     *
     * @param sAsunto String asutno del mensaje
     * @param sTexto String cuerpo del mensaje
     * @return boolean true=exitoso, false=fallido
     */
    public boolean enviarTXTEmail(String sAsunto, String sTexto) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", sServidorCorreo);
            Session mailSesion = Session.getDefaultInstance(props, null);

            Message msg = new MimeMessage(mailSesion);

            msg.setFrom(new InternetAddress(remitente));
            msg.setSubject(sAsunto);
            msg.setSentDate(new java.util.Date());
            msg.setText(sTexto);

            InternetAddress address[] = new InternetAddress[asCorreoDestino.length];
            for (int i = 0; i < asCorreoDestino.length; i++) {
                address[i] = new InternetAddress(asCorreoDestino[i]);
            } // end for

            msg.setRecipients(Message.RecipientType.TO, address);

            Transport.send(msg);
        } catch (MessagingException ex) {
            error = true;
            errorMessage = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return false;
        } // end try-catch
        return true;
    } // enviarTXTEmail

    /**
     *
     * Método público y estático que envía un correo a las direcciones indicadas
     * en el parámetro sendTo, desde la dirección indicada en el archivo de
     * propiedades que se carga en la variable CONFIG_FILE con el asunto y el
     * contenido que se pasan como parámetros.
     *
     * @author Bosco Garita 31/10/2011
     * @param sAsunto String
     * @param sTexto String
     * @param sendTo String[][]
     * @return boolean
     *
     */
    public boolean sendTextMail(String sAsunto, String sTexto, String[][] sendTo) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", sServidorCorreo);
            Session mailSesion = Session.getDefaultInstance(props, null);

            Message msg = new MimeMessage(mailSesion);

            msg.setFrom(new InternetAddress(remitente));
            msg.setSubject(sAsunto);
            msg.setSentDate(new java.util.Date());
            msg.setText(sTexto);

            InternetAddress address[] = new InternetAddress[sendTo.length];
            for (int i = 0; i < sendTo.length; i++) {
                address[i] = new InternetAddress(sendTo[i][0]);
            } // end for

            msg.setRecipients(Message.RecipientType.TO, address);

            Transport.send(msg);
        } catch (MessagingException ex) {
            error = true;
            errorMessage = ex.getMessage();
            return false;
        } // end try-catch

        return true;
    } // end sendTextMail sobrecargado

    /**
     * Método público y estático que envía un correo a las direcciones indicadas
     * en el fichero de propiedades, desde la dirección indicada también en el
     * mismo fichero con el asunto y el contenido que se pasan como parámetros.
     *
     * @param addressx String dirección de correo electrónico
     * @param sAsunto String título del correo
     * @param sTexto String mensaje del correo
     * @return boolean true=Exitoso, false=fallido
     * @throws java.lang.Exception
     */
    public boolean sendHTMLMail(String addressx, String sAsunto, String sTexto) throws Exception {
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(sTexto, "text/html");
        MimeMultipart multiParte = new MimeMultipart();
        multiParte.addBodyPart(bodyPart);

        Session session = Session.getDefaultInstance(this.gmailProps,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmailProps.getProperty("mail.smtp.user"), gmailProps.getProperty("mail.smtp.clave"));
            }
        });

        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(remitente));
        msg.setSubject(sAsunto);
        msg.setSentDate(new java.util.Date());

        //msg.setContent(sTexto, "text/html");

        InternetAddress address[] = new InternetAddress[1];
        address[0] = new InternetAddress(addressx);

        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(sAsunto);
        msg.setSentDate(new java.util.Date());
        msg.setContent(multiParte);

        Transport.send(msg);

        return true;
    } // sendHTMLMail

    /**
     * Envía correos de texto/HTML con una lista de adjuntos
     *
     * @param destinatarios
     * @param sAsunto
     * @param sTexto
     * @param archivos
     * @return
     */
    public boolean sendAttachmentMail_GM(String destinatarios, String sAsunto, String sTexto, String[] archivos) {
        MimeMultipart multiParte;
        BodyPart bodyPart = new MimeBodyPart();

        try {
            Session session = Session.getDefaultInstance(this.gmailProps,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    System.out.println("\nAuthenticating with " + gmailProps.getProperty("mail.smtp.user") + "...");
                    return new PasswordAuthentication(gmailProps.getProperty("mail.smtp.user"), gmailProps.getProperty("mail.smtp.clave"));
                }
            });

            Message msg = new MimeMessage(session);

            // Puede ser una máscara (por alguna razón en GMail este from lo está ignorando, usa el user de la session. 22/10/2018)
            msg.setFrom(new InternetAddress(remitente));

            InternetAddress address[] = new InternetAddress[1];
            address[0] = new InternetAddress(destinatarios);

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(sAsunto);
            msg.setSentDate(new java.util.Date());

            //bodyPart.setText(sTexto); // este se usa cuando solo se manda texto y no html
            bodyPart.setContent(sTexto, "text/html");

            multiParte = new MimeMultipart();
            multiParte.addBodyPart(bodyPart);

            // Agregar los adjuntos (si vienen)
            for (String archivo : archivos) {
                File f = new File(archivo);
                if (!f.exists() || f.isDirectory()){
                    continue;
                }
                BodyPart adj = new MimeBodyPart();
                adj.setDataHandler(new DataHandler(new FileDataSource(archivo)));
                adj.setFileName(f.getName()); // Transmitir el nombre original
                multiParte.addBodyPart(adj);
            } // end for

            //msg.addHeader("X-Priority", "1"); // Prioridad alta
            //msg.addHeader("Dispostion-Notification", "bgarita@coopecaja.fi.cr"); // Acuse de recibo
            msg.setContent(multiParte);

            Transport.send(msg);
            System.out.println("Mail sent successfuly!");
        } catch (Exception ex) {
            System.out.println(ex);
            error = true;
            errorMessage = ex.getMessage();
            return false;
        } // end try-catch

        return true;
    } // sendAttachmentMail_GM

    public String getRemitente() {
        return remitente;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Continuar despues, este no se ha probado ni se ha adptado para que sea HTML
    public boolean sendHTMLMail_GM(String destinatarios, String sAsunto, String sTexto, String[] archivos) {
        MimeMultipart multiParte;
        BodyPart bodyPart = new MimeBodyPart();

        try {
            Session session = Session.getDefaultInstance(this.gmailProps,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(gmailProps.getProperty("mail.smtp.user"), gmailProps.getProperty("mail.smtp.clave"));
                }
            });

            Message msg = new MimeMessage(session);

            // Puede ser una máscara (por alguna razón en GMail este from lo está ignorando, usa el user de la session. 22/10/2018)
            msg.setFrom(new InternetAddress(remitente));

            InternetAddress address[] = new InternetAddress[1];
            address[0] = new InternetAddress(destinatarios);

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(sAsunto);
            msg.setSentDate(new java.util.Date());

            bodyPart.setText(sTexto);

            multiParte = new MimeMultipart();
            multiParte.addBodyPart(bodyPart);

            // Agregar los adjuntos (si vienen)
            for (String archivo : archivos) {
                File f = new File(archivo);
                BodyPart adj = new MimeBodyPart();
                adj.setDataHandler(new DataHandler(new FileDataSource(archivo)));
                adj.setFileName(f.getName()); // Transmitir el nombre original
                multiParte.addBodyPart(adj);
            } // end for

            msg.setContent(multiParte);

            Transport.send(msg);
        } catch (Exception ex) {
            System.out.println(ex);
            error = true;
            errorMessage = ex.getMessage();
            return false;
        } // end try-catch

        return true;
    } // sendAttachmentMail_GM
} // end class
