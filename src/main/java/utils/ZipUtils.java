package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void createZip(Map<String, byte[]> srcFic, String zipFic) throws IOException {
        
        try(
            FileOutputStream fos = new FileOutputStream(zipFic);
            ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            for (var pair : srcFic.entrySet()) {
                try {
                    zos.putNextEntry(new ZipEntry(pair.getKey()));
                    zos.write(pair.getValue());
                    zos.closeEntry();
                } catch (Exception e) {
                    System.err.println("ERREUR lors du zippage de "+ pair.getKey() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERREUR lors de la création du fichier zip : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
