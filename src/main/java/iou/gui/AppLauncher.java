package iou.gui;

import iou.util.GuiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class AppLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLauncher.class);

    public static void main(String[] args) {
        LOGGER.debug("Launching login window");

        // Load the application's icon
        URL imageFile = AppLauncher.class.getResource("/dollar.gif");
        LOGGER.debug("Loading application icon from: {}", imageFile);
        Image appImage = Toolkit.getDefaultToolkit().getImage(imageFile);
        GuiUtils.setApplicationImage(appImage);

        // Create an instance of the login window and pass along any args
        LoginFrame.main(args);
    }
}
