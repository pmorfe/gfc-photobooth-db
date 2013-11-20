package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 11:32 PM
 */
public class AuthPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton signInBtn;
    private JLabel userNameLbl;
    private JLabel passwordLbl;
    private JTextField userNameFld;
    private JPasswordField passwordFld;
    private AuthEventListener authEventListener;


    /**
     * <p>
     * Constructor Method for the AuthenticationPanel
     * </p>
     */
    public AuthPanel() {
        signInBtn = new JButton("Sign In");
        userNameLbl = new JLabel("Username: ");
        passwordLbl = new JLabel("Password: ");
        userNameFld = new JTextField(10);
        passwordFld = new JPasswordField(10);

        // Create the layout of the authentication panel
        authPanelLayout();


        // Sign In button action listener. It will get the text on the
        // userNameFld and passwordFld and pass it to the AuthenticationEvent
        signInBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String username = userNameFld.getText();
                char[] password = passwordFld.getPassword();

                AuthEvent authEv = new AuthEvent(AuthPanel.this, username, new String(password));
                if (authEventListener != null) {
                    authEventListener.authEventOccured(authEv);
                }
            }
        });
    }

    /**
     * <p>
     * This method sets up the Layout of the {@link gui.AuthPanel}.
     * </p>
     */
    public void authPanelLayout() {
        JPanel logInInfopanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        int space = 300;
        // Set up Borders for the logInInfopanel
        Border spaceBorder = BorderFactory.createEmptyBorder(space, space,
                space, space);
        Border titleBorder = BorderFactory.createTitledBorder("Sign In");

        logInInfopanel.setBorder(BorderFactory.createCompoundBorder(
                spaceBorder, titleBorder));
        logInInfopanel.setLayout(new GridBagLayout());
        /************** UserName Label *******************/
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;

        gc.weightx = 1;
        gc.weighty = 1;

        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gc.insets = new Insets(0, 0, 0, 5);

        logInInfopanel.add(userNameLbl, gc);
        /************** UserName Field *******************/
        gc.gridx = 1;
        gc.gridy = 0;

        gc.weightx = 1;

        gc.anchor = GridBagConstraints.WEST;

        logInInfopanel.add(userNameFld, gc);
        /************** Password Label *******************/
        gc.gridy = 1;
        gc.gridx = 0;

        gc.weightx = 1;
        gc.weighty = 0.3;

        gc.anchor = GridBagConstraints.EAST;
        gc.insets = new Insets(0, 0, 0, 5);

        logInInfopanel.add(passwordLbl, gc);
        /************** Password Field *******************/
        gc.gridx = 1;
        gc.gridy = 1;

        gc.weightx = 1;

        gc.anchor = GridBagConstraints.WEST;

        logInInfopanel.add(passwordFld, gc);
        /**************** Sign In Button *****************/
        gc.gridx = 1;
        gc.gridy = 2;
        // setting up buttonPanel such as its layout and buttons
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        signInBtn.setMnemonic(KeyEvent.VK_I);
        buttonPanel.add(signInBtn);
        logInInfopanel.add(buttonPanel, gc);

        setLayout(new BorderLayout());
        add(logInInfopanel, BorderLayout.CENTER);

    }

    public JButton getSignInBtn() {
        return signInBtn;
    }

    public JTextField getUserNameFld() {
        return userNameFld;
    }

    public JPasswordField getPasswordFld() {
        return passwordFld;
    }

    public void setAuthenticationEventListener(
            AuthEventListener authEventListener) {
        this.authEventListener = authEventListener;
    }
}
