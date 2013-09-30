package iou.controller;

public class Factory {

    private static final Controller CONTROLLER_INSTANCE = new ControllerImpl();

    public static Controller getController() {
        return CONTROLLER_INSTANCE;
    }
}
