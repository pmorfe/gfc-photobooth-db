package gui;

import java.util.EventObject;

/**
 * Author: Pippo Morfe
 * Date: 15/11/2013
 * Time: 4:07 PM
 */
public class GoSearchBtnEvent extends EventObject {
    private int orderId;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public GoSearchBtnEvent(Object source) {
        super(source);
    }

    public GoSearchBtnEvent(Object source, int orderId) {
        super(source);
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
