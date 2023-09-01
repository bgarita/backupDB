package Test;

import Utilities.Archivos;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Bosco Garita, 01/09/2023
 */
public class TestZipFiles {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Archivos archivos = new Archivos();
        File origen = new File("\\Backups\\Osais");
        File destino = new File("\\Backups\\testing");
        archivos.zipFile(origen, destino);
    }
    
}
