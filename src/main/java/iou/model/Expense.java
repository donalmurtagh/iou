package iou.model;

import iou.enums.TransactionType;

public class Expense extends Transaction {

    public Expense() {
        super(TransactionType.EXPENSE);
    }
}
