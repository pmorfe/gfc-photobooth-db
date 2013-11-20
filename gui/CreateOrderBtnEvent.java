package gui;

import model.CustomerOrder;

import java.util.EventObject;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 9:10 PM
 */
public class CreateOrderBtnEvent extends EventObject {
    private CustomerOrder customerOrder;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateOrderBtnEvent(Object source) {
        super(source);
    }

    public CreateOrderBtnEvent(Object source, CustomerOrder customerOrder) {
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
