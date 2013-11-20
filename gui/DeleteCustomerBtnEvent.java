package gui;

import model.CustomerOrder;

import java.util.EventObject;

/**
 * Author: Pippo Morfe
 * Date: 15/11/2013
 * Time: 3:11 PM
 */
public class DeleteCustomerBtnEvent extends EventObject {
    private CustomerOrder customerOrder;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DeleteCustomerBtnEvent(Object source) {
        super(source);
    }

    public DeleteCustomerBtnEvent(Object source, CustomerOrder customerOrder) {
        super(source);
        this.customerOrder = customerOrder;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }
}
