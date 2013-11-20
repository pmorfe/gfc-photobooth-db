package controller;

import gui.HomePanel;
import gui.ProgressDialog;
import model.CustomerOrder;
import model.Database;
import model.User;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 8:29 PM
 */
public class Controller {
    private Database db;
    private User user;
    private ProgressDialog progressDialog;

    public boolean connectToDatabase(JFrame parent,String username,String pass) {


        Connection conn = null;
        String dbName = "GFCPhotoBooth";
        user = new User(username,pass);

        try {
            db = new Database(conn, dbName);
            dbConnect(parent,db,user);
        } catch (SQLException e) {
            //e.printStackTrace();
            user = null;
            return false;
        }


        return true;
    }

    /**
     * <p>
     * This method will try to connect to the database
     * </p>
     *
     * @param db
     *            The database trying to connect.
     * */
    public void dbConnect(JFrame parent, final Database db, final User user) throws SQLException {
        progressDialog = new ProgressDialog(parent);
        progressDialog.setMaximum(100);


        class DatabaseConnection extends SwingWorker<Integer,Integer>{

            public DatabaseConnection(){

            }

            @Override
            protected Integer doInBackground() throws Exception {


                publish(20);
                setProgress(20);
                if (!db.dbConnectForExisting(user.getUsername(),user.getPassword())) {

                    db.dbConnect();
                    publish(40);
                    setProgress(40);
                    db.turnOnBuiltInUsers( user.getUsername(),user.getPassword());
                    publish(60);
                    setProgress(60);
                    db.dbConnectForExisting(user.getUsername(),user.getPassword());
                    publish(80);
                    setProgress(80);
                    db.createTablePD();
                    db.dbCommit();
                }

                db.getConn().setAutoCommit(false);

                publish(100);
                setProgress(100);
                return null;
            }

            @Override
            protected void done() {
                super.done();
                progressDialog.setVisible(false);
            }

            @Override
            protected void process(java.util.List<Integer> value) {
                int progress = value.get(value.size()-1);

                progressDialog.setValue( progress);
            }
        }
        DatabaseConnection dcWorker = new DatabaseConnection();
        dcWorker.execute();

        progressDialog.setVisible(true);

    }

    public void disconnectToDatabase(User user) {
        // //////////////Disconnect///////////////////////////
        try {
            db.dbDisconnect();

        } catch (SQLException e) {
            System.out.println("Failed to disconnect");
        }
        // /////////////Shutdown//////////////////////////
        try {
            db.dbShutDown(user.getUsername(),user.getPassword());

        } catch (SQLException e) {
            System.out.println("Failed to shutdown");
        }
    }

    public Database getDb() {
        return db;
    }

    public User getUser() {
        return user;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    /**
     * <p>
     * This method will swap JPanels and refresh the JFrame where they were
     * added
     * </p>
     *
     * @param frame
     *            It is a JFrame where the two JPanels were added.
     * @param oldPanel
     *            The JPanel that will be replaced.
     * @param newPanel
     *            The new JPanel that will replace the old JPanel.
     * */
    public void swapPanel(JFrame frame, JPanel oldPanel, JPanel newPanel) {
        frame.remove(oldPanel);
        frame.add(newPanel, BorderLayout.CENTER);
        frame.validate();
        frame.repaint();
    }

    public void addCustomerOrder(CustomerOrder customerOrder,HomePanel homePanel) {
        CustomerOrder co;

        db.insertCustomerOrder(customerOrder.getFname(), customerOrder.getLname(), customerOrder.getContactNo(),
                customerOrder.getStreet(), customerOrder.getSuburb(), customerOrder.getPostCode(), customerOrder.getCity(),
                customerOrder.getState(), customerOrder.getCost(), customerOrder.getAmountPaid(),
                customerOrder.getBalance(), customerOrder.getComment(),customerOrder.getSideNotes(),customerOrder.getDelivered());
        try {
            db.dbCommit();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) homePanel.getDm().getRoot();
        root.removeAllChildren();
        homePanel.getDm().reload();

        DefaultMutableTreeNode delivered = homePanel.addTreeCategory("Delivered Already");
        DefaultMutableTreeNode notDelivered = homePanel.addTreeCategory("Not Yet Delivered");

        homePanel.addTreeCategory(delivered,"FULLY PAID (D)");
        homePanel.addTreeCategory(delivered,"PARTIALLY PAID (D)");
        homePanel.addTreeCategory(delivered,"UNPAID (D)");

        homePanel.addTreeCategory(notDelivered,"Fully Paid");
        homePanel.addTreeCategory(notDelivered,"Partially Paid");
        homePanel.addTreeCategory(notDelivered,"Unpaid");

        populateTheTree(homePanel.getCustomerTree(), homePanel.getDm());

        co=db.getLastOrder();
        homePanel.setSelectedCustomerOrder(co);
        homePanel.setFieldsValues(co);

        homePanel.getCustomerTree().setSelectionPath(findCustomerOrderOnTree(root,co));

    }
    public void deleteCustomerOrder(CustomerOrder customerOrder,HomePanel homePanel) {
        CustomerOrder co;

        db.removeCustomerOrderById(customerOrder.getOrderId());
        try {
            db.dbCommit();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) homePanel.getDm().getRoot();
        root.removeAllChildren();
        homePanel.getDm().reload();

        DefaultMutableTreeNode delivered = homePanel.addTreeCategory("Delivered Already");
        DefaultMutableTreeNode notDelivered = homePanel.addTreeCategory("Not Yet Delivered");

        homePanel.addTreeCategory(delivered,"FULLY PAID (D)");
        homePanel.addTreeCategory(delivered,"PARTIALLY PAID (D)");
        homePanel.addTreeCategory(delivered,"UNPAID (D)");

        homePanel.addTreeCategory(notDelivered,"Fully Paid");
        homePanel.addTreeCategory(notDelivered,"Partially Paid");
        homePanel.addTreeCategory(notDelivered,"Unpaid");


        populateTheTree(homePanel.getCustomerTree(), homePanel.getDm());
        if(!homePanel.getSearchFld().getText().isEmpty()){
            int id = 0;
            try{
                id =Integer.parseInt(homePanel.getSearchFld().getText());
            } catch (NumberFormatException nfe){

            }
            List<CustomerOrder> customerOrderList= searchCustomer(id);
            homePanel.setData(customerOrderList);
            homePanel.refresh();
        }


    }
    public List<CustomerOrder> searchCustomer(int id) {
        return db.getCustomerOrderById(id);
    }
    public void updateCustomerOrder(CustomerOrder customerOrder,HomePanel homePanel) {


        db.updateCustomerOrder(customerOrder.getOrderId(),customerOrder.getFname(), customerOrder.getLname(), customerOrder.getContactNo(),
                customerOrder.getStreet(), customerOrder.getSuburb(), customerOrder.getPostCode(), customerOrder.getCity(),
                customerOrder.getState(), customerOrder.getCost(), customerOrder.getAmountPaid(),
                customerOrder.getBalance(), customerOrder.getComment(),customerOrder.getSideNotes(),customerOrder.getDelivered());
        try {
            db.dbCommit();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) homePanel.getDm().getRoot();
        root.removeAllChildren();
        homePanel.getDm().reload();

        DefaultMutableTreeNode delivered = homePanel.addTreeCategory("Delivered Already");
        DefaultMutableTreeNode notDelivered = homePanel.addTreeCategory("Not Yet Delivered");

        homePanel.addTreeCategory(delivered,"FULLY PAID (D)");
        homePanel.addTreeCategory(delivered,"PARTIALLY PAID (D)");
        homePanel.addTreeCategory(delivered,"UNPAID (D)");

        homePanel.addTreeCategory(notDelivered,"Fully Paid");
        homePanel.addTreeCategory(notDelivered,"Partially Paid");
        homePanel.addTreeCategory(notDelivered,"Unpaid");

        populateTableAndTree(homePanel,customerOrder,root);
    }

    public void populateTableAndTree(HomePanel homePanel,CustomerOrder customerOrder,DefaultMutableTreeNode root){
        populateTheTree(homePanel.getCustomerTree(), homePanel.getDm());
        if(!homePanel.getSearchFld().getText().isEmpty()){
            if(hasNumber(homePanel.getSearchFld().getText())){
                int id = 0;
                try{
                    id =Integer.parseInt(homePanel.getSearchFld().getText());
                    List<CustomerOrder> customerOrderList= searchCustomer(id);
                    homePanel.setData(customerOrderList);
                    homePanel.refresh();
                } catch (NumberFormatException nfe){
                    //searchCustomerOrder(homePanel.getSearchFld().getText(),homePanel);

                }
            }else{
                List<CustomerOrder> customerOrderList= db.getCustomerOrderByLName(homePanel.getSearchFld().getText()+"%");

                homePanel.setData(customerOrderList);
                homePanel.refresh();
            }




        }
        homePanel.setFieldsValues(customerOrder);

        if(homePanel.getSelectedCustomerOrder()!=null){
            homePanel.setSelectedCustomerOrder(customerOrder);
            homePanel.getCustomerTree().setSelectionPath(findCustomerOrderOnTree(root,customerOrder));
        }else if(homePanel.getSearchCustomerOrder()!=null){
            int row = homePanel.getRowSelected();

            homePanel.setSearchCustomerOrder(customerOrder);
            homePanel.getSearchTbl().setRowSelectionInterval(row,row);

        }
    }
     public boolean hasNumber(String string){
         if(string.matches(".*\\d.*")){
             return true;
         } else{
            return false;
         }
     }
    public void searchCustomerOrder(String string,HomePanel homePanel){
        List<CustomerOrder> customerOrderList= db.getCustomerOrderByLName(string);
        homePanel.setData(customerOrderList);
        homePanel.refresh();
    }
    public void populateTheTree(JTree customerTree, DefaultTreeModel dm) {
        List<CustomerOrder> customerOrderList;



        customerOrderList = db.getCustomerOrder();

        for (int i = 0; customerOrderList.size() > i; i++) {
            CustomerOrder customerOrder = customerOrderList.get(i);
            DefaultMutableTreeNode newChild;
            DefaultMutableTreeNode parent;

            if(customerOrder.getDelivered().equals("No")) {

            if(Integer.parseInt(customerOrder.getBalance())==0){

                addNewChildInTheTree(customerTree, dm,customerOrder,"Fully Paid");

            }else if(Integer.parseInt(customerOrder.getBalance())<Integer.parseInt(customerOrder.getCost())){
                addNewChildInTheTree(customerTree, dm,customerOrder,"Partially Paid");

            }else if(Integer.parseInt(customerOrder.getBalance())==Integer.parseInt(customerOrder.getCost())){
                addNewChildInTheTree(customerTree, dm,customerOrder,"Unpaid");

            }
            }else if(customerOrder.getDelivered().equals("Yes")){
                if(Integer.parseInt(customerOrder.getBalance())==0){

                    addNewChildInTheTree(customerTree, dm,customerOrder,"FULLY PAID (D)");

                }else if(Integer.parseInt(customerOrder.getBalance())<Integer.parseInt(customerOrder.getCost())){
                    addNewChildInTheTree(customerTree, dm,customerOrder,"PARTIALLY PAID (D)");

                }else if(Integer.parseInt(customerOrder.getBalance())==Integer.parseInt(customerOrder.getCost())){
                    addNewChildInTheTree(customerTree, dm,customerOrder,"UNPAID (D)");

                }
            }


        }


    }

    public void addNewChildInTheTree(JTree customerTree,DefaultTreeModel dm,CustomerOrder customerOrder,String name){
        TreePath path;
        DefaultMutableTreeNode newChild;
        DefaultMutableTreeNode parent;

        newChild = new DefaultMutableTreeNode(customerOrder);
        path = find((DefaultMutableTreeNode) dm.getRoot(),
                name);

        parent = (DefaultMutableTreeNode) path
                .getLastPathComponent();
        dm.insertNodeInto(newChild, parent,
                parent.getChildCount());
        customerTree.expandPath(new TreePath(dm
                .getPathToRoot(newChild.getParent())));
    }

    private TreePath findCustomerOrderOnTree(DefaultMutableTreeNode root, CustomerOrder cusOrder) {
        DefaultMutableTreeNode parent = null;
        CustomerOrder customerOrder=null;

        Enumeration<DefaultMutableTreeNode> e;
        e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            parent = e.nextElement();
            if( parent.getUserObject() instanceof CustomerOrder){
            customerOrder=(CustomerOrder)parent.getUserObject();
            if (customerOrder.getOrderId()==cusOrder.getOrderId()) {

                return new TreePath(parent.getPath());
            }
            }
        }
        return null;
    }
    private TreePath find(DefaultMutableTreeNode root, String group) {
        DefaultMutableTreeNode parent = null;

        Enumeration<DefaultMutableTreeNode> e;
        e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            parent = e.nextElement();

            if (parent.toString().equalsIgnoreCase(group)) {

                return new TreePath(parent.getPath());
            }
        }
        return null;
    }

}
