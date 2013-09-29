package iou.enums;

public enum TranDialogMode {
    ADD_PAYMENT("Add Payment"), UPDATE_PAYMENT("Update Payment"), ADD_EXPENSE("Add Expense"), UPDATE_EXPENSE(
            "Update Expense");

    private String title;

    TranDialogMode(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
