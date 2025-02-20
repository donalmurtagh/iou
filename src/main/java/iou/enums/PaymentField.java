package iou.enums;

import java.util.Arrays;

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

    public static PaymentField getByIndex(int index) {
        return Arrays.stream(values())
            .filter(field -> field.getIndex() == index)
            .findFirst()
            .orElse(null);
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }
}
