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
    }
}
