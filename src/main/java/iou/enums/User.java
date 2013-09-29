package iou.enums;

public enum User {
    MAUDE, DONAL;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
