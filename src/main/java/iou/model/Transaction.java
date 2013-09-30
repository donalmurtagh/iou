package iou.model;

import iou.enums.TransactionType;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

public abstract class Transaction {

    private TransactionType type;
    private String description;
    private Date date = new Date();
    private long id;
    private float annPaid;
    private float bobPaid;

    public Transaction(TransactionType type) {
        this.type = type;
    }

    public void setAnnPaid(float amount) {
        this.annPaid = amount;
    }

    public void setBobPaid(float amount) {
        this.bobPaid = amount;
    }

    public float getBobPaid() {
        return bobPaid;
    }

    public float getAnnPaid() {
        return annPaid;
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

        return String.format("id=%s, description=%s, date=%s, ann paid=%s, bob paid=%s",
                id, description, date, annPaid, bobPaid);
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
