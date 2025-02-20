package iou.enums;

import java.util.Arrays;

public enum ExpenseField implements Field {

    DATE("Date", DATE_COLUMN_INDEX),
    DESCRIPTION("Description", 1),
    ANN_PAID(User.ANN.getName() + " Paid", 2),
    BOB_PAID(User.BOB.getName() + " Paid", 3);

    private final String name;

    private final int index;

    ExpenseField(String name, int position) {
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

    public static ExpenseField getByIndex(int index) {
        return Arrays.stream(values())
            .filter(field -> field.getIndex() == index)
            .findFirst()
            .orElse(null);
    }
}
