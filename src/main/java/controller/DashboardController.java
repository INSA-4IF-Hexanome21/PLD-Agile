package controller;

/**
 *
 * @author aza
 */

import java.io.*;

public class DashboardController {

    public static byte[] getDashboardHTML() throws IOException {
        InputStream is = DashboardController.class
                .getClassLoader()
                .getResourceAsStream("view/Index.html");

        if (is == null) throw new FileNotFoundException("index.html not found");

        return is.readAllBytes();
    }
}
