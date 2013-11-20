package gui;

import model.CustomerOrder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 8:13 PM
 */
public class AddCustomerPanel extends JPanel {

    private JTextField fnameFld;
    private JTextField lnameFld;
    private JTextField streetFld;
    private JComboBox<String> cityCB;
    private JComboBox<String> stateCB;
    private JTextField suburbFld;
    private JTextField postCodeFld;
    private JTextField contactNoFld;
    private JTextField costFld;
    private JTextField amountFld;
    private JTextField balanceFld;
    private JTextArea comments;
    private JButton cancel;
    private JButton create;
    private CancelOrderBtnEventListener cancelOrderBtnEventListener;
    private CreateOrderBtnEventListener createOrderBtnEventListener;
    private JTextField orderNoFld;
    private JComboBox<String> isDeliveredCB;
    private JTextArea sideNotes;

    public AddCustomerPanel() {
        fnameFld = new JTextField(10);
        lnameFld = new JTextField(10);
        streetFld = new JTextField(15);
        suburbFld = new JTextField(10);
        contactNoFld = new JTextField(10);
        postCodeFld = new JTextField(10);
        costFld = new JTextField(5);
        balanceFld = new JTextField(5);
        String city[] = {"Melbourne", "Sydney", "Brisbane", "Perth", "Adelaide", "Darwin", "Canberra", "Hobart"};
        cityCB = new JComboBox<String>(city);
        String state[] = {"Victoria", "New South Wales", "Queensland", "Western Australia", "South Australia", "Northern Territory", "Australian Capital Territory", "Tasmania"};
        stateCB = new JComboBox<String>(state);
        amountFld = new JTextField(5);
        comments = new JTextArea(5, 30);
        cancel = new JButton("Cancel");
        create = new JButton("Create");
        orderNoFld = new JTextField(3);
        isDeliveredCB = new JComboBox<String>(new String[]{"No", "Yes"});
        sideNotes = new JTextArea(5, 30);

        AddCustomerLayout();

        cityCB.setRenderer(new DefaultListCellRenderer() {
            public void paint(Graphics g) {
                setForeground(Color.BLACK);
                super.paint(g);
            }
        });
        stateCB.setRenderer(new DefaultListCellRenderer() {
            public void paint(Graphics g) {
                setForeground(Color.BLACK);
                super.paint(g);
            }
        });

        isDeliveredCB.setRenderer(new DefaultListCellRenderer() {
            public void paint(Graphics g) {
                setForeground(Color.BLACK);
                super.paint(g);
            }
        });

        create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (validateInput()) {
                    CustomerOrder customerOrder = new CustomerOrder(fnameFld.getText().trim(), lnameFld.getText().trim(), contactNoFld.getText().trim(),
                            streetFld.getText().trim(), suburbFld.getText().trim(), postCodeFld.getText().trim(), (String) cityCB.getSelectedItem(), (String) stateCB.getSelectedItem(),
                            costFld.getText().trim(), amountFld.getText().trim(), balanceFld.getText().trim(), comments.getText().trim(), sideNotes.getText().trim(), (String) isDeliveredCB.getSelectedItem());

                    CreateOrderBtnEvent event = new CreateOrderBtnEvent(e, customerOrder);
                    if (createOrderBtnEventListener != null) {
                        createOrderBtnEventListener.createOrderBtnEventOccured(event);
                    }
                    setFieldsToEmpty();
                }
            }
        });

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int option = JOptionPane
                        .showConfirmDialog(
                                AddCustomerPanel.this,
                                "Do you really want to cancel? The customer's order details will not be save.",
                                "Cancel Customer's Order", JOptionPane.OK_CANCEL_OPTION);
                if (JOptionPane.OK_OPTION == option) {
                    CancelOrderBtnEvent event = new CancelOrderBtnEvent(e);
                    setFieldsToEmpty();
                    if (cancelOrderBtnEventListener != null) {
                        cancelOrderBtnEventListener.cancelOrderBtnEventOccured(event);
                    }
                }
            }
        });

        //Auto Calculation in Cost, Paid Amount and Balance
        DocumentFilter df = new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int i, String string, AttributeSet as) throws BadLocationException {

                if (isDigit(string)) {
                    super.insertString(fb, i, string, as);
                    calcAndSetTotal();
                }
            }

            @Override
            public void remove(FilterBypass fb, int i, int i1) throws BadLocationException {
                super.remove(fb, i, i1);
                calcAndSetTotal();
            }

            @Override
            public void replace(FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException {
                if (isDigit(string)) {
                    super.replace(fb, i, i1, string, as);
                    calcAndSetTotal();

                }
            }

            private boolean isDigit(String string) {
                for (int n = 0; n < string.length(); n++) {
                    char c = string.charAt(n);//get a single character of the string
                    //System.out.println(c);
                    if (!Character.isDigit(c)) {//if its an alphabetic character or white space
                        return false;
                    }
                }
                return true;
            }

            void calcAndSetTotal() {
                int balance = 0;

                if (!costFld.getText().isEmpty()) {
                    balance = Integer.parseInt(costFld.getText());
                }
                if (!amountFld.getText().isEmpty()) {
                    balance -= Integer.parseInt(amountFld.getText());//we must add this
                }


                balanceFld.setText(String.valueOf(balance));
            }
        };

        ((AbstractDocument) (costFld.getDocument())).setDocumentFilter(df);
        ((AbstractDocument) (amountFld.getDocument())).setDocumentFilter(df);
    }

    public void setCancelOrderBtnEventListener(CancelOrderBtnEventListener listener) {
        this.cancelOrderBtnEventListener = listener;
    }

    public void setCreateOrderBtnEventListener(CreateOrderBtnEventListener listener) {
        this.createOrderBtnEventListener = listener;
    }

    public void setFieldsToEmpty() {

        fnameFld.setText("");
        lnameFld.setText("");
        streetFld.setText("");
        suburbFld.setText("");
        contactNoFld.setText("");
        postCodeFld.setText("");
        costFld.setText("");
        amountFld.setText("");
        comments.setText("");
        cityCB.setSelectedIndex(0);
        stateCB.setSelectedIndex(0);
        sideNotes.setText("");
        orderNoFld.setText("");
        isDeliveredCB.setSelectedIndex(0);

    }

    public boolean validateInput() {
        if (fnameFld.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(AddCustomerPanel.this,
                    "Please enter the Customer's first name. First name must not be empty.",
                    "Invalid First Name", JOptionPane.OK_OPTION);
            return false;
        } else if (fnameFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            AddCustomerPanel.this,
                            "First name exceeds the number of characters allowed. 20 characters only",
                            "Invalid First Name", JOptionPane.OK_OPTION);
            return false;
        } else if (lnameFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            AddCustomerPanel.this,
                            "Last name exceeds the number of characters allowed. 20 characters only",
                            "Invalid Last Name", JOptionPane.OK_OPTION);
            return false;
        } else if (lnameFld.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(AddCustomerPanel.this,
                    "Please enter the customer's last name. Last name must not be empty.",
                    "Invalid Last Name", JOptionPane.OK_OPTION);
            return false;
        } else if (contactNoFld.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(AddCustomerPanel.this,
                    "Please enter the customer's contact no. Contact number must not be empty.",
                    "Invalid Contact Number", JOptionPane.OK_OPTION);
            return false;
        } else if (contactNoFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            AddCustomerPanel.this,
                            "Contact number exceeds the number of characters allowed. 20 characters only",
                            "Invalid Contact Number", JOptionPane.OK_OPTION);
            return false;
        } else if (streetFld.getText().trim().length() > 50) {
            JOptionPane
                    .showMessageDialog(
                            AddCustomerPanel.this,
                            "Street address exceeds the number of characters allowed. 50 characters only",
                            "Invalid Street Address", JOptionPane.OK_OPTION);
            return false;
        } else if (suburbFld.getText().trim().length() > 50) {
            JOptionPane
                    .showMessageDialog(
                            AddCustomerPanel.this,
                            "Suburb exceeds the number of characters allowed. 50 characters only",
                            "Invalid Suburb", JOptionPane.OK_OPTION);
            return false;
        } else if (costFld.getText().trim().length() > 15) {
            JOptionPane
                    .showMessageDialog(
                            AddCustomerPanel.this,
                            "Cost exceeds the number of characters allowed. 15 characters only",
                            "Invalid Cost", JOptionPane.OK_OPTION);
            return false;
        } else if (amountFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            AddCustomerPanel.this,
                            "Amount Paid exceeds the number of characters allowed. 20 characters only",
                            "Invalid Amount Paid", JOptionPane.OK_OPTION);
            return false;
        }

        return true;
    }

    public void AddCustomerLayout() {
        JPanel customerDetailsPanel = new JPanel();
        JPanel orderPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        JPanel groupButtonsPanel = new JPanel();
        JPanel customersFullName = new JPanel();
        JPanel suburbPostcodePanel = new JPanel();
        JPanel cityStatePanel = new JPanel();
        JPanel paymentPanel = new JPanel();
        JPanel orderStatusPanel = new JPanel();
        JPanel sideNotesPanel = new JPanel();

        int space = 50;
        int spaceDetails = 100;
        // setting up the borders

        Border spaceBorderStatus = BorderFactory.createEmptyBorder(5,
                spaceDetails, 10, spaceDetails);
        Border titleBorderStatus = BorderFactory
                .createTitledBorder("Status");
        orderStatusPanel.setBorder(BorderFactory.createCompoundBorder(
                spaceBorderStatus, titleBorderStatus));

        Border spaceBorderDetails = BorderFactory.createEmptyBorder(10,
                spaceDetails, 10, spaceDetails);
        Border titleBorderDetails = BorderFactory
                .createTitledBorder("Customer Details");
        customerDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
                spaceBorderDetails, titleBorderDetails));

        Border spaceBorderOrder = BorderFactory.createEmptyBorder(5,
                spaceDetails, 10, spaceDetails);
        Border titleBorderOrder = BorderFactory
                .createTitledBorder("Orders");
        orderPanel.setBorder(BorderFactory.createCompoundBorder(
                spaceBorderOrder, titleBorderOrder));

        Border spaceBorderNotes = BorderFactory.createEmptyBorder(5,
                spaceDetails, 10, spaceDetails);
        Border titleBorderNotes = BorderFactory
                .createTitledBorder("Notes");
        sideNotesPanel.setBorder(BorderFactory.createCompoundBorder(
                spaceBorderNotes, titleBorderNotes));

        customerDetailsPanel.setLayout(new GridBagLayout());
        orderPanel.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        // ///////First Name///////////////
        customersFullName
                .setLayout(new BoxLayout(customersFullName, BoxLayout.X_AXIS));
        gc.gridx = 0;
        gc.gridy = 0;

        gc.weightx = .5;
        gc.weighty = .5;

        gc.fill = GridBagConstraints.NONE;

        gc.anchor = GridBagConstraints.EAST;
        customerDetailsPanel.add(new JLabel("First Name: "), gc);
        customersFullName.add(fnameFld);
        // //////////Last Name//////////////
        customersFullName.add(new JLabel("  Last Name: "));
        customersFullName.add(lnameFld);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        customerDetailsPanel.add(customersFullName, gc);
        // //////////Contact #//////////////
        gc.gridx = 0;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.EAST;
        customerDetailsPanel.add(new JLabel("Contact #: "), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        customerDetailsPanel.add(contactNoFld, gc);
        // //////////Street//////////////
        gc.gridx = 0;
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.EAST;
        customerDetailsPanel.add(new JLabel("Street: "), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        customerDetailsPanel.add(streetFld, gc);
        // //////////Suburb//////////////
        suburbPostcodePanel.setLayout(new BoxLayout(suburbPostcodePanel, BoxLayout.X_AXIS));
        gc.gridx = 0;
        gc.gridy = 3;
        gc.anchor = GridBagConstraints.EAST;
        customerDetailsPanel.add(new JLabel("Suburb: "), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        suburbPostcodePanel.add(suburbFld);
        // //////////Post Code//////////////

        suburbPostcodePanel.add(new JLabel("Post Code: "));

        suburbPostcodePanel.add(postCodeFld);
        customerDetailsPanel.add(suburbPostcodePanel, gc);
        //////////City///////////////
        cityStatePanel.setLayout(new BoxLayout(cityStatePanel, BoxLayout.X_AXIS));
        gc.gridx = 0;
        gc.gridy = 4;
        gc.anchor = GridBagConstraints.EAST;
        customerDetailsPanel.add(new JLabel("City: "), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        cityStatePanel.add(cityCB);
        // //////////Post Code//////////////

        cityStatePanel.add(new JLabel("State: "));

        cityStatePanel.add(stateCB);
        customerDetailsPanel.add(cityStatePanel, gc);
        // //////////Cost//////////////
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.EAST;
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.X_AXIS));
        orderPanel.add(new JLabel("Cost: "), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        costFld.setText("0");
        paymentPanel.add(costFld);
        // //////////Amount Paid//////////////

        paymentPanel.add(new JLabel("Paid Amount: "));
        amountFld.setText("0");
        paymentPanel.add(amountFld);
        orderPanel.add(paymentPanel, gc);
        ////////////// Balance ///////////
        gc.gridx = 0;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.EAST;
        orderPanel.add(new JLabel("Balance: "), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        balanceFld.setEditable(false);
        balanceFld.setText("0");
        orderPanel.add(balanceFld, gc);
        ///////////Comments////////////
        gc.gridx = 0;
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.EAST;
        orderPanel.add(new JLabel("Comments"), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        orderPanel.add(new JScrollPane(comments), gc);
        /////////Buttons//////
        Border spaceBorder = BorderFactory.createEmptyBorder(20, space, space,
                5);
        Border titleBorder = BorderFactory.createTitledBorder("Add New Customer Order");
        infoPanel.setBorder(BorderFactory.createCompoundBorder(spaceBorder,
                titleBorder));
        groupButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        groupButtonsPanel.add(create);
        groupButtonsPanel.add(cancel);
        /////////Status//////////
        orderStatusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        orderStatusPanel.add(new JLabel("Order ID: "));
        orderNoFld.setEditable(false);
        orderStatusPanel.add(orderNoFld);
        orderStatusPanel.add(new JLabel("Delivered? "));
        orderStatusPanel.add(isDeliveredCB);
        //////////Notes////////
        sideNotesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sideNotesPanel.add(new JLabel("Notes: "));
        sideNotesPanel.add(new JScrollPane(sideNotes));
        // /////////////Combine all panels ///////////
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        infoPanel.add(orderStatusPanel);
        infoPanel.add(customerDetailsPanel);
        infoPanel.add(orderPanel);
        infoPanel.add(sideNotesPanel);
        infoPanel.add(groupButtonsPanel);
        setLayout(new BorderLayout());

        add(infoPanel, BorderLayout.CENTER);
    }


}
