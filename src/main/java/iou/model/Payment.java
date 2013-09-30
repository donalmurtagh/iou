package iou.model;

import iou.enums.TransactionType;

public class Payment extends Transaction {

    public Payment() {
        super(TransactionType.PAYMENT);
    }

    /**
     * The amount Bob paid Ann.
     */
    @Override
    public void setBobPaid(float amount) {

        // If Bob paid Ann, then Ann cannot also have paid Bob
        if (amount != 0) {
            super.setBobPaid(amount);
            super.setAnnPaid(0);
        }
    }

    /**
     * The amount Ann paid Bob
     */
    @Override
    public void setAnnPaid(float amount) {

        // If Abb paid Bob, then Bob cannot also have paid Ann
        if (amount != 0) {
            super.setAnnPaid(amount);
            super.setBobPaid(0);
        }
    }
}
