package iou.gui;

import iou.util.GuiUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;

public class AppLauncher {

    private static final Logger LOGGER = Logger.getLogger(AppLauncher.class);

    public static void main(String[] args) {

        InputStream log4jConfig = AppLauncher.class.getResourceAsStream("/log4j.properties");
        PropertyConfigurator.configure(log4jConfig);
        LOGGER.debug("Launching login window");

        // Load the application's icon
        URL imageFile = AppLauncher.class.getResource("/dollar.gif");
        LOGGER.debug("Loading application icon from: " + imageFile);
        Image appImage = Toolkit.getDefaultToolkit().getImage(imageFile);
        GuiUtils.setApplicationImage(appImage);

        // Create an instance of the login window and pass along any args
        LoginFrame.main(args);
    }
}
