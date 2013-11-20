package gui;

import java.util.EventObject;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 11:34 PM
 */
public class AuthEvent extends EventObject {
    private String username;
    private String password;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public AuthEvent(Object source) {
        super(source);
    }

    public AuthEvent(Object source, String username, String password) {
        super(source);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
