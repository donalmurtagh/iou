package iou.enums;

public enum PaymentField implements Field {

    DATE("Date", DATE_COLUMN_INDEX),
    DESCRIPTION("Description", 1),
    PAID_BY("Paid By", 2),
    AMOUNT("Amount", 3);

    private final String name;

    private final int index;

    PaymentField(String name, int position) {
        this.name = name;
        this.index = position;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
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
