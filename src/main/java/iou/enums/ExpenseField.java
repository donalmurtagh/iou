package iou.enums;

public enum ExpenseField implements Field {

    DATE("Date", DATE_COLUMN_INDEX),
    DESCRIPTION("Description", 1),
    ANN_PAID(User.ANN.getName() + " Paid", 2),
    BOB_PAID(User.BOB.getName() + " Paid", 3);

    private String name;

    private int index;

    private ExpenseField(String name, int position) {
        this.name = name;
        this.index = position;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static ExpenseField getByIndex(int index) {

        for (ExpenseField field : ExpenseField.values()) {
            if (field.getIndex() == index) {
                return field;
            }
        }
        return null;
    }
}
