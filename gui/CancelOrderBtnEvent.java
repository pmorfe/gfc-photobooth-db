package gui;

import java.util.EventObject;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 8:42 PM
 */
public class CancelOrderBtnEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CancelOrderBtnEvent(Object source) {
        super(source);
    }
}
