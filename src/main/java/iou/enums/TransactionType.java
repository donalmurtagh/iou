package iou.enums;

public enum TransactionType {
    EXPENSE {
        @Override
        public String toString() {
            return "EXP";
        }
    },
    PAYMENT {
        @Override
        public String toString() {
            return "PMT";
        }
    };

    public static TransactionType getType(String type) {

        for (TransactionType aTranType : TransactionType.values()) {
            if (aTranType.equals(type)) {
                return aTranType;
            }
        }
        return null;
    }
}
