package iou.model;

import iou.enums.TransactionType;

public class Payment extends Transaction {

    public Payment() {
        super(TransactionType.PAYMENT);
    }

    /**
     * The amount Donal paid Maude.
     */
    @Override
    public void setDonalPaid(float amount) {

        // If Donal paid Maude, then Maude cannot also have paid Donal
        if (amount != 0) {
            super.setDonalPaid(amount);
            super.setMaudePaid(0);
        }
    }

    /**
     * The amount Maude paid Donal
     */
    @Override
    public void setMaudePaid(float amount) {

        // If Maude paid Donal, then Donal cannot also have paid Maude
        if (amount != 0) {
            super.setMaudePaid(amount);
            super.setDonalPaid(0);
        }
    }
}
