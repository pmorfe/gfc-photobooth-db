package gui;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import model.CustomerOrder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.util.List;


/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 2:53 PM
 */
public class HomePanel extends JPanel {
    private JTable searchTbl;
    private SearchTableModel searchMdl;
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
    private JButton edit;
    private JButton cancel;
    private JButton save;
    private JButton saveToPdfBtn;
    private JTree customerTree;
    private JButton addCustomer;
    private JButton deleteCustomer;
    private DefaultMutableTreeNode selectedNode;
    private DefaultTreeModel dm;
    private JTextField searchFld;
    private SaveToPdf saveToPdf;
    private JFileChooser fileChooser;
    private AddCustomerBtnEventListener addCustomerBtnEventListener;
    private SaveUpdateBtnEventListener saveUpdateBtnEventListener;
    private CustomerOrder selectedCustomerOrder;
    private DeleteCustomerBtnEventListener deleteCustomerBtnEventListener;
    private JButton goSearchBtn;
    private GoSearchBtnEventListener goSearchBtnEventListener;
    private CustomerOrder searchCustomerOrder;
    private SearchFieldKeyEventListener searchListener;
    private JTextField orderNoFld;
    private JComboBox<String> isDeliveredCB;
    private JTextArea sideNotes;
    private int rowSelected;


    /**
     * <p>
     * Constructor Method for the {@link gui.HomePanel}.
     * </p>
     */
    public HomePanel() {

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
        comments = new JTextArea(8, 30);
        edit = new JButton("Edit");
        cancel = new JButton("Cancel");
        save = new JButton("Save");
        saveToPdfBtn = new JButton("Create Invoice");
        dm = new DefaultTreeModel(new DefaultMutableTreeNode("Customers"));
        customerTree = new JTree(dm);
        addCustomer = new JButton("Add");
        deleteCustomer = new JButton("Delete");
        searchMdl = new SearchTableModel();
        searchTbl = new JTable(searchMdl);
        goSearchBtn = new JButton("Go");
        saveToPdf = new SaveToPdf();
        orderNoFld = new JTextField(3);
        isDeliveredCB = new JComboBox<String>(new String[]{"No", "Yes"});
        sideNotes = new JTextArea(8, 30);
        searchFld = new JTextField(5);
        fileChooser = new JFileChooser();
        rowSelected = 0;


        DefaultMutableTreeNode delivered = addTreeCategory("Delivered Already");
        DefaultMutableTreeNode notDelivered = addTreeCategory("Not Yet Delivered");

        addTreeCategory(delivered, "FULLY PAID (D)");
        addTreeCategory(delivered, "PARTIALLY PAID (D)");
        addTreeCategory(delivered, "UNPAID (D)");

        addTreeCategory(notDelivered, "Fully Paid");
        addTreeCategory(notDelivered, "Partially Paid");
        addTreeCategory(notDelivered, "Unpaid");

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

        searchFld.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent arg0) {

            }

            @Override
            public void keyReleased(KeyEvent arg0) {

            }

            @Override
            public void keyTyped(KeyEvent arg0) {
                // System.out.println(searchFld.getText() + "\n");
                String name = searchFld.getText().trim();
                if (!name.equals("")) {
                    SearchFieldKeyEvent event = new SearchFieldKeyEvent(arg0,
                            name + "%");
                    if (searchListener != null) {
                        searchListener.searchFieldKeyEventOccured(event);
                    }
                } else {
                    name = "0";
                    SearchFieldKeyEvent event = new SearchFieldKeyEvent(arg0,
                            name + "%");
                    if (searchListener != null) {
                        searchListener.searchFieldKeyEventOccured(event);
                    }
                }
            }

        });

        customerTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                selectedNode = (DefaultMutableTreeNode) customerTree
                        .getLastSelectedPathComponent();
                searchCustomerOrder = null;
                refresh();
                if (selectedNode != null || customerTree.isRowSelected(0)) {
                    Object obj = selectedNode.getUserObject();

                    selectedCustomerOrder = null;
                    // setFieldsValuesEmpty();
                    if (obj instanceof CustomerOrder) {
                        selectedCustomerOrder = (CustomerOrder) selectedNode.getUserObject();

                        setFieldsValues(selectedCustomerOrder);

                    } else {
                        setFieldsToEmpty();
                    }
                }
            }

        });

        customerTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        homePanelLayout();
        setFieldsEditability(false);

        goSearchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id = 0;
                try {
                    id = Integer.parseInt(searchFld.getText());
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(HomePanel.this,
                            "Please enter numbers only for Order Id",
                            "Invalid Operation", JOptionPane.OK_OPTION);
                }

                GoSearchBtnEvent event = new GoSearchBtnEvent(e, id);

                if (goSearchBtnEventListener != null) {
                    goSearchBtnEventListener.goSearchBtnEventOccured(event);
                }
            }
        });
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (selectedCustomerOrder != null || searchCustomerOrder != null) {
                    edit.setVisible(false);
                    save.setVisible(true);
                    cancel.setVisible(true);
                    customerTree.setEnabled(false);
                    searchFld.setEnabled(false);
                    searchTbl.setEnabled(false);
                    addCustomer.setEnabled(false);
                    deleteCustomer.setEnabled(false);
                    setFieldsEditability(true);
                } else {
                    JOptionPane.showMessageDialog(HomePanel.this,
                            "Please select a Customer's Order to edit first",
                            "Invalid Operation", JOptionPane.OK_OPTION);
                }

            }
        });

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (validateInput()) {
                    int id = 0;
                    if (selectedCustomerOrder != null) {
                        id = selectedCustomerOrder.getOrderId();
                    } else if (searchCustomerOrder != null) {
                        id = searchCustomerOrder.getOrderId();
                    }
                    CustomerOrder customerOrder = new CustomerOrder(id, fnameFld.getText().trim(), lnameFld.getText().trim(), contactNoFld.getText().trim(),
                            streetFld.getText().trim(), suburbFld.getText().trim(), postCodeFld.getText().trim(), (String) cityCB.getSelectedItem(), (String) stateCB.getSelectedItem(),
                            costFld.getText().trim(), amountFld.getText().trim(), balanceFld.getText().trim(), comments.getText().trim(), sideNotes.getText().trim(), (String) isDeliveredCB.getSelectedItem());
                    SaveUpdateBtnEvent event = new SaveUpdateBtnEvent(e, customerOrder);

                    if (saveUpdateBtnEventListener != null) {
                        saveUpdateBtnEventListener.saveUpdateBtnEventOccured(event);
                    }
                    edit.setVisible(true);
                    save.setVisible(false);
                    cancel.setVisible(false);
                    customerTree.setEnabled(true);
                    searchFld.setEnabled(true);
                    searchTbl.setEnabled(true);
                    addCustomer.setEnabled(true);
                    deleteCustomer.setEnabled(true);
                    setFieldsEditability(false);
                }
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                edit.setVisible(true);
                save.setVisible(false);
                cancel.setVisible(false);
                customerTree.setEnabled(true);
                searchFld.setEnabled(true);
                searchTbl.setEnabled(true);
                addCustomer.setEnabled(true);
                deleteCustomer.setEnabled(true);
                setFieldsEditability(false);
            }
        });

        addCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                AddCustomerBtnEvent ev = new AddCustomerBtnEvent(e);

                if (addCustomerBtnEventListener != null) {
                    addCustomerBtnEventListener.addCustomerBtnEventOccured(ev);
                }

            }
        });

        deleteCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (selectedCustomerOrder != null || searchCustomerOrder != null) {
                    CustomerOrder customerOrder = null;

                    if (selectedCustomerOrder != null) {
                        customerOrder = selectedCustomerOrder;
                        selectedCustomerOrder = null;
                    } else if (searchCustomerOrder != null) {
                        customerOrder = searchCustomerOrder;
                        searchCustomerOrder = null;
                    }

                    int option = JOptionPane.showConfirmDialog(
                            HomePanel.this,
                            "Do you really want to DELETE "
                                    + customerOrder.getFname() + "'s Order ?",
                            "Delete " + customerOrder.getFname() + "'s Order?",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (JOptionPane.OK_OPTION == option) {
                        DeleteCustomerBtnEvent ev = new DeleteCustomerBtnEvent(e, customerOrder);

                        if (deleteCustomerBtnEventListener != null) {
                            deleteCustomerBtnEventListener.deleteCustomerBtnEventOccured(ev);
                        }
                        setFieldsToEmpty();
                    }
                } else {
                    JOptionPane.showMessageDialog(HomePanel.this,
                            "Please select a Customer's Order to delete first",
                            "Invalid Operation", JOptionPane.OK_OPTION);
                }
            }
        });

        searchTbl.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    selectedCustomerOrder = null;
                    customerTree.clearSelection();
                    if (searchTbl.getSelectedRow() >= 0) {
                        setRowSelected(searchTbl.getSelectedRow());
                        List<CustomerOrder> memberList = searchMdl.getCustomerOrderList();
                        searchCustomerOrder = memberList.get(searchTbl.getSelectedRow());
                        setFieldsValues(searchCustomerOrder);

                    }
                }
            }
        });

        saveToPdfBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                CustomerOrder saveToPdfMember = null;
                if (searchCustomerOrder != null) {
                    saveToPdfMember = searchCustomerOrder;
                } else if (selectedCustomerOrder != null) {
                    saveToPdfMember = selectedCustomerOrder;
                }

                if (saveToPdfMember != null) {

                    if (fileChooser.showSaveDialog(HomePanel.this) == JFileChooser.APPROVE_OPTION) {

                        try {
                            Document document = new Document();
                            PdfWriter
                                    .getInstance(document,
                                            new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath() + ".pdf"));

                            document.open();
                            saveToPdf.addMetaData(document, saveToPdfMember);
                            saveToPdf.addTitlePage(document, saveToPdfMember);
                            saveToPdf.addContent(document, saveToPdfMember);

                            document.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // JOptionPane.showMessageDialog(HomePanel.this,
                            //      "Filename already Exists. Please choose another name",
                            //      "Failed to export to pdf",
                            //      JOptionPane.OK_OPTION);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(HomePanel.this,
                            "Please select a Customer order first",
                            "Failed to export to pdf", JOptionPane.OK_OPTION);
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

    public void setSearchListener(SearchFieldKeyEventListener listener) {
        this.searchListener = listener;
    }

    public void setDeleteCustomerBtnEventListener(DeleteCustomerBtnEventListener listener) {
        this.deleteCustomerBtnEventListener = listener;
    }

    public DefaultMutableTreeNode addTreeCategory(String childName) {
        DefaultMutableTreeNode newChild;

        newChild = new DefaultMutableTreeNode(childName);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) dm
                .getRoot();
        dm.insertNodeInto(newChild, root, root.getChildCount());
        customerTree.expandPath(new TreePath(dm.getPathToRoot(newChild
                .getParent())));
        return newChild;
    }

    public void addTreeCategory(DefaultMutableTreeNode parent, String childName) {
        DefaultMutableTreeNode newChild;

        newChild = new DefaultMutableTreeNode(childName);

        dm.insertNodeInto(newChild, parent, parent.getChildCount());
        customerTree.expandPath(new TreePath(dm.getPathToRoot(newChild
                .getParent())));
    }

    public void setSaveUpdateBtnEventListener(SaveUpdateBtnEventListener listener) {
        this.saveUpdateBtnEventListener = listener;
    }


    public void setFieldsValues(CustomerOrder customerOrder) {
        fnameFld.setText(customerOrder.getFname());
        lnameFld.setText(customerOrder.getLname());
        streetFld.setText(customerOrder.getStreet());
        suburbFld.setText(customerOrder.getSuburb());
        contactNoFld.setText(customerOrder.getContactNo());
        postCodeFld.setText(customerOrder.getPostCode());
        costFld.setText(customerOrder.getCost());
        amountFld.setText(customerOrder.getAmountPaid());
        comments.setText(customerOrder.getComment());
        orderNoFld.setText(Integer.toString(customerOrder.getOrderId()));
        sideNotes.setText(customerOrder.getSideNotes());

        for (int i = 0; cityCB.getItemCount() > i; i++) {

            String city = cityCB.getItemAt(i);
            if (city.equals(customerOrder.getCity())) {
                cityCB.setSelectedItem(city);
            }
        }

        for (int i = 0; stateCB.getItemCount() > i; i++) {

            String state = stateCB.getItemAt(i);
            if (state.equals(customerOrder.getState())) {
                stateCB.setSelectedItem(state);
            }
        }
        for (int i = 0; isDeliveredCB.getItemCount() > i; i++) {

            String isDelivered = isDeliveredCB.getItemAt(i);
            if (isDelivered.equals(customerOrder.getDelivered())) {
                isDeliveredCB.setSelectedItem(isDelivered);
            }
        }
    }

    public void setGoSearchBtnEventListner(GoSearchBtnEventListener listner) {
        this.goSearchBtnEventListener = listner;
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
        orderNoFld.setText("");
        isDeliveredCB.setSelectedIndex(0);
        sideNotes.setText("");

    }

    public void setFieldsEditability(boolean isEditable) {
        fnameFld.setEditable(isEditable);
        lnameFld.setEditable(isEditable);
        streetFld.setEditable(isEditable);
        suburbFld.setEditable(isEditable);
        contactNoFld.setEditable(isEditable);
        postCodeFld.setEditable(isEditable);
        costFld.setEditable(isEditable);
        amountFld.setEditable(isEditable);
        comments.setEditable(isEditable);
        cityCB.setEnabled(isEditable);
        stateCB.setEnabled(isEditable);
        sideNotes.setEditable(isEditable);
        isDeliveredCB.setEnabled(isEditable);
    }

    public void homePanelLayout() {

        JPanel customerTreePanel = new JPanel();
        JPanel customerDetailsPanel = new JPanel();
        JPanel orderPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        JPanel groupButtonsPanel = new JPanel();
        JPanel memberButtonsPanel = new JPanel();
        JPanel combineButtonsPanel = new JPanel();
        JPanel searchPanel = new JPanel();
        JPanel saveToPdfPanel = new JPanel();
        JPanel customersFullName = new JPanel();
        JPanel suburbPostcodePanel = new JPanel();
        JPanel cityStatePanel = new JPanel();
        JPanel paymentPanel = new JPanel();
        JPanel searchCombinePanel = new JPanel();
        JPanel orderStatusPanel = new JPanel();
        JPanel sideNotesPanel = new JPanel();


        int space = 0;
        int spaceDetails = 120;
        // setting up the borders
        Border spaceBorder = BorderFactory.createEmptyBorder(space, space,
                space, space);
        Border titleBorder = BorderFactory.createTitledBorder("GFC PhotoBooth Customers");
        customerTreePanel.setBorder(BorderFactory.createCompoundBorder(spaceBorder,
                titleBorder));

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

        Border titleBorderCustomer = BorderFactory
                .createTitledBorder("Customer");
        memberButtonsPanel.setBorder(titleBorderCustomer);

        Border titleBorderSearch = BorderFactory.createTitledBorder("Search");
        searchPanel.setBorder(titleBorderSearch);

        customerDetailsPanel.setLayout(new GridBagLayout());
        orderPanel.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        saveToPdfPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        saveToPdfPanel.add(saveToPdfBtn);

        /*************** Table ************/

        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchCombinePanel.setLayout(new BoxLayout(searchCombinePanel, BoxLayout.X_AXIS));
        groupButtonsPanel.setLayout(new FlowLayout());
        customerTreePanel.setLayout(new BorderLayout());
        combineButtonsPanel.setLayout(new BoxLayout(combineButtonsPanel,
                BoxLayout.Y_AXIS));

        searchCombinePanel.add(searchFld);
        searchCombinePanel.add(goSearchBtn);
        searchPanel.add(searchCombinePanel);
        searchPanel.add(new JScrollPane(searchTbl));

        memberButtonsPanel.add(addCustomer);
        memberButtonsPanel.add(deleteCustomer);
        combineButtonsPanel.add(memberButtonsPanel);
        customerTreePanel.setPreferredSize(new Dimension(25, 33));
        customerTreePanel.add(combineButtonsPanel, BorderLayout.SOUTH);
        customerTreePanel.add(searchPanel, BorderLayout.CENTER);
        customerTreePanel.add(new JScrollPane(customerTree), BorderLayout.NORTH);

        /////////// Customer Details //////////
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
        balanceFld.setText("0");
        balanceFld.setEditable(false);
        orderPanel.add(balanceFld, gc);
        ///////////Comments////////////
        gc.gridx = 0;
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.EAST;
        orderPanel.add(new JLabel("Description: "), gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        orderPanel.add(new JScrollPane(comments), gc);
        /////////Buttons//////
        save.setVisible(false);
        cancel.setVisible(false);
        groupButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        groupButtonsPanel.add(edit);
        groupButtonsPanel.add(save);
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
        infoPanel.add(saveToPdfPanel);
        infoPanel.add(orderStatusPanel);
        infoPanel.add(customerDetailsPanel);
        infoPanel.add(orderPanel);
        infoPanel.add(sideNotesPanel);
        infoPanel.add(groupButtonsPanel);
        setLayout(new BorderLayout());


        customerTreePanel.setPreferredSize(new Dimension(200, 100));
        add(new JScrollPane(customerTreePanel), BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
    }

    public boolean validateInput() {
        if (fnameFld.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(HomePanel.this,
                    "Please enter the Customer's first name. First name must not be empty.",
                    "Invalid First Name", JOptionPane.OK_OPTION);
            return false;
        } else if (fnameFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            HomePanel.this,
                            "First name exceeds the number of characters allowed. 20 characters only",
                            "Invalid First Name", JOptionPane.OK_OPTION);
            return false;
        } else if (lnameFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            HomePanel.this,
                            "Last name exceeds the number of characters allowed. 20 characters only",
                            "Invalid Last Name", JOptionPane.OK_OPTION);
            return false;
        } else if (lnameFld.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(HomePanel.this,
                    "Please enter the customer's last name. Last name must not be empty.",
                    "Invalid Last Name", JOptionPane.OK_OPTION);
            return false;
        } else if (contactNoFld.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(HomePanel.this,
                    "Please enter the customer's contact no. Contact number must not be empty.",
                    "Invalid Contact Number", JOptionPane.OK_OPTION);
            return false;
        } else if (contactNoFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            HomePanel.this,
                            "Contact number exceeds the number of characters allowed. 20 characters only",
                            "Invalid Contact Number", JOptionPane.OK_OPTION);
            return false;
        } else if (streetFld.getText().trim().length() > 50) {
            JOptionPane
                    .showMessageDialog(
                            HomePanel.this,
                            "Street address exceeds the number of characters allowed. 50 characters only",
                            "Invalid Street Address", JOptionPane.OK_OPTION);
            return false;
        } else if (suburbFld.getText().trim().length() > 50) {
            JOptionPane
                    .showMessageDialog(
                            HomePanel.this,
                            "Suburb exceeds the number of characters allowed. 50 characters only",
                            "Invalid Suburb", JOptionPane.OK_OPTION);
            return false;
        } else if (costFld.getText().trim().length() > 15) {
            JOptionPane
                    .showMessageDialog(
                            HomePanel.this,
                            "Cost exceeds the number of characters allowed. 15 characters only",
                            "Invalid Cost", JOptionPane.OK_OPTION);
            return false;
        } else if (amountFld.getText().trim().length() > 20) {
            JOptionPane
                    .showMessageDialog(
                            HomePanel.this,
                            "Amount Paid exceeds the number of characters allowed. 20 characters only",
                            "Invalid Amount Paid", JOptionPane.OK_OPTION);
            return false;
        }

        return true;
    }

    public void setAddCustomerBtnEventListener(AddCustomerBtnEventListener listener) {
        this.addCustomerBtnEventListener = listener;
    }

    public void refresh() {
        searchMdl.fireTableDataChanged();
    }

    public DefaultTreeModel getDm() {
        return dm;
    }

    public void setDm(DefaultTreeModel dm) {
        this.dm = dm;
    }

    public JTree getCustomerTree() {
        return customerTree;
    }

    public void setCustomerTree(JTree customerTree) {
        this.customerTree = customerTree;
    }

    public CustomerOrder getSelectedCustomerOrder() {
        return selectedCustomerOrder;
    }

    public void setSelectedCustomerOrder(CustomerOrder selectedCustomerOrder) {
        this.selectedCustomerOrder = selectedCustomerOrder;
    }

    public void setData(List<CustomerOrder> customerOrderList) {
        searchMdl.setData(customerOrderList);
    }

    public JTextField getSearchFld() {
        return searchFld;
    }

    public JTable getSearchTbl() {

        return searchTbl;
    }

    public void setSearchTbl(JTable searchTbl) {
        this.searchTbl = searchTbl;
    }

    public CustomerOrder getSearchCustomerOrder() {
        return searchCustomerOrder;
    }

    public void setSearchCustomerOrder(CustomerOrder searchCustomerOrder) {
        this.searchCustomerOrder = searchCustomerOrder;
    }

    public SearchTableModel getSearchMdl() {

        return searchMdl;
    }

    public void setSearchMdl(SearchTableModel searchMdl) {
        this.searchMdl = searchMdl;
    }

    public int getRowSelected() {
        return rowSelected;
    }

    public void setRowSelected(int rowSelected) {
        this.rowSelected = rowSelected;
    }
}
