package iou.gui;

import iou.util.GuiUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class AppLauncher {

    private static final String LOG_FILE_PATH = "iou.gui.resources.log4j";

    private static final Logger LOGGER = Logger.getLogger(AppLauncher.class);

    /**
     * @param args
     * @throws java.io.FileNotFoundException
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws FileNotFoundException, MalformedURLException {

        // A very long-winded way of loading a log4j properties file from the classpath
        ResourceBundle log4jRB = PropertyResourceBundle.getBundle(LOG_FILE_PATH);
        Enumeration<String> keys = log4jRB.getKeys();
        Properties log4jProps = new Properties();

        while (keys.hasMoreElements()) {
            String propKey = keys.nextElement();
            String propValue = log4jRB.getString(propKey);
            log4jProps.put(propKey, propValue);
        }

        PropertyConfigurator.configure(log4jProps);
        LOGGER.debug("Launching login window");

        // Load the application's icon
        File imageFile = new File(".", "bin/iou/gui/resources/dollar.gif");
        LOGGER.debug("Loading application icon from: " + imageFile.getAbsolutePath());
        Image appImage = Toolkit.getDefaultToolkit().getImage(imageFile.toURI().toURL());
        GuiUtils.setApplicationImage(appImage);

        // Create an instance of the login window and pass along any args
        LoginFrame.main(args);
    }
}
