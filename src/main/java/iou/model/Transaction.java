package iou.model;

import iou.enums.TransactionType;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

public abstract class Transaction {

    private TransactionType type;
    private String description;
    private Date date = new Date();
    private long id;
    private float maudePaid;
    private float donalPaid;

    public Transaction(TransactionType type) {
        this.type = type;
    }

    public void setMaudePaid(float amount) {
        this.maudePaid = amount;
    }

    public void setDonalPaid(float amount) {
        this.donalPaid = amount;
    }

    public float getBobPaid() {
        return donalPaid;
    }

    public float getAnnPaid() {
        return maudePaid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "id=" + id + ", description=" + description + ", date=" + date
                + ", maude paid=" + maudePaid + ", donal paid=" + donalPaid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Transaction)) {
            return false;
        }

        return this.id == ((Transaction) obj).id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();

    }

}
