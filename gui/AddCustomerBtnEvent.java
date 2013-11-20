package gui;

import java.util.EventObject;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 8:21 PM
 */
public class AddCustomerBtnEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public AddCustomerBtnEvent(Object source) {
        super(source);
    }
}
