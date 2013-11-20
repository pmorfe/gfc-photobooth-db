package gui;

import java.util.EventObject;

/**
 * Author: Pippo Morfe
 * Date: 17/11/2013
 * Time: 12:57 AM
 */
public class SearchFieldKeyEvent extends EventObject {
    String string;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public SearchFieldKeyEvent(Object source) {
        super(source);
    }

    public SearchFieldKeyEvent(Object source, String string) {
        super(source);
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
