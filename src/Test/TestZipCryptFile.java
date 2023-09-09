package Test;

import Utilities.Archivos;
import java.io.File;
import java.io.IOException;


/**
 *
 * @author Bosco Garita, 01/09/2023
 */
public class TestZipCryptFile {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Archivos archivos = new Archivos();
        File origen = new File("\\Backups\\Osais");
        File destino = new File("\\Backups\\testing.zip");
        archivos.zipCryptFile(origen, destino); // Esta versión usa contraseña para la extracción
    }

}
