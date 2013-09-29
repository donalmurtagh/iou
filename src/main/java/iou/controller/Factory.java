package iou.controller;

public class Factory {

    private static final IController CONTROLLER_INSTANCE = new ControllerImpl();

    public static IController getController() {
        return CONTROLLER_INSTANCE;
    }
}
