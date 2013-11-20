package model;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 8:54 PM
 */
public class CustomerOrder {
    private int orderId;
    private String fname;
    private String lname;
    private String contactNo;
    private String street;
    private String suburb;
    private String postCode;
    private String city;
    private String state;
    private String cost;
    private String amountPaid;
    private String balance;
    private String comment;
    private String sideNotes;
    private String isDelivered;

    public CustomerOrder(int orderId, String fname, String lname, String contactNo,
                         String street, String suburb, String postCode, String city,
                         String state, String cost, String amountPaid, String balance, String comment,String sideNotes,String isDelivered) {

        this.orderId = orderId;
        this.fname = fname;
        this.lname = lname;
        this.contactNo = contactNo;
        this.street = street;
        this.suburb = suburb;
        this.postCode = postCode;
        this.city = city;
        this.state = state;
        this.cost = cost;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.comment = comment;
        this.sideNotes = sideNotes;
        this.isDelivered = isDelivered;
    }

    public CustomerOrder(String fname, String lname, String contactNo,
                         String street, String suburb, String postCode, String city,
                         String state, String cost, String amountPaid, String balance, String comment,String sideNotes,String isDelivered) {

        this.fname = fname;
        this.lname = lname;
        this.contactNo = contactNo;
        this.street = street;
        this.suburb = suburb;
        this.postCode = postCode;
        this.city = city;
        this.state = state;
        this.cost = cost;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.comment = comment;
        this.sideNotes = sideNotes;
        this.isDelivered = isDelivered;
    }

    public String toString(){
        return lname+", "+fname;
    }
    public int getOrderId() {
        return orderId;
    }

    public String getSideNotes() {
        return sideNotes;
    }

    public void setSideNotes(String sideNotes) {
        this.sideNotes = sideNotes;
    }

    public String getDelivered() {
        return isDelivered;
    }

    public void setDelivered(String delivered) {
        isDelivered = delivered;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
