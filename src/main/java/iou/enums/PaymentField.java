package iou.enums;

public enum PaymentField implements Field {

    DATE("Date", DATE_COLUMN_INDEX),
    DESCRIPTION("Description", 1),
    PAID_BY("Paid By", 2),
    AMOUNT("Amount", 3);

    private String name;

    private int index;

    private PaymentField(String name, int position) {
        this.name = name;
        this.index = position;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static PaymentField getByIndex(int index) {

        for (PaymentField field : PaymentField.values()) {
            if (field.getIndex() == index) {
                return field;
            }
        }
        return null;
    }
}
