package iou.enums;

public enum ExpenseField implements IField {

    DATE("Date", DATE_COLUMN_INDEX), DESCRIPTION("Description", 1), MAUDE_PAID("Maude paid", 2), DONAL_PAID(
            "Donal paid", 3);

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
