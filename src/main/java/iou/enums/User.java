package iou.enums;

import java.io.IOException;
import java.util.Properties;

public enum User {
    ANN, BOB;

    private final String name;
    private final String username;

    User() {
        Properties messages = new Properties();

        try {
            messages.load(getClass().getResourceAsStream("/config.properties"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        String keyPrefix = this.name().toLowerCase();
        String nameKey = keyPrefix + ".name";
        String usernameKey = keyPrefix + ".username";

        name = messages.getProperty(nameKey);
        username = messages.getProperty(usernameKey);
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
