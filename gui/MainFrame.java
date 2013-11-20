package gui;

import controller.Controller;
import model.CustomerOrder;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 2:39 PM
 */
public class MainFrame extends JFrame {
    private HomePanel homePanel;
    private Controller controller;
    private AddCustomerPanel addCustomerPanel;
    private AuthPanel authPanel;


    public MainFrame() {
        homePanel = new HomePanel();
        controller = new Controller();
        addCustomerPanel = new AddCustomerPanel();
        authPanel = new AuthPanel();

        JPanel footerPanel = new JPanel();
        // set up the layout of the MainFrame
        setLayout(new BorderLayout());
        add(authPanel, BorderLayout.CENTER);
        JRootPane rootPane = MainFrame.this.getRootPane();
        rootPane.setDefaultButton(authPanel.getSignInBtn());

        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        footerPanel.add(new JLabel(" © GFC PhotoBooth 2013 All rights Reserved. Created By: Pippo Morfe"));
        add(footerPanel, BorderLayout.SOUTH);
        setTitle("GFC PhotoBooth Database");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 800));
        setResizable(false);
        setLocationRelativeTo(null);

        homePanel.setAddCustomerBtnEventListener(new AddCustomerBtnEventListener() {
            public void addCustomerBtnEventOccured(AddCustomerBtnEvent event) {
                controller.swapPanel(MainFrame.this, homePanel, addCustomerPanel);
            }
        });

        homePanel.setSaveUpdateBtnEventListener(new SaveUpdateBtnEventListener() {
            public void saveUpdateBtnEventOccured(SaveUpdateBtnEvent event) {

                controller.updateCustomerOrder(event.getCustomerOrder(), homePanel);
            }
        });
        homePanel.setDeleteCustomerBtnEventListener(new DeleteCustomerBtnEventListener() {
            public void deleteCustomerBtnEventOccured(DeleteCustomerBtnEvent event) {
                controller.deleteCustomerOrder(event.getCustomerOrder(), homePanel);
            }
        });

        homePanel.setGoSearchBtnEventListner(new GoSearchBtnEventListener() {
            public void goSearchBtnEventOccured(GoSearchBtnEvent event) {
                List<CustomerOrder> customerOrderList = controller.searchCustomer(event.getOrderId());
                homePanel.setData(customerOrderList);
                homePanel.refresh();
            }
        });
        homePanel.setSearchListener(new SearchFieldKeyEventListener() {
            public void searchFieldKeyEventOccured(SearchFieldKeyEvent event) {
                controller.searchCustomerOrder(event.getString(), homePanel);
            }
        });
        addCustomerPanel.setCancelOrderBtnEventListener(new CancelOrderBtnEventListener() {
            public void cancelOrderBtnEventOccured(CancelOrderBtnEvent event) {
                controller.swapPanel(MainFrame.this, addCustomerPanel, homePanel);
            }
        });

        addCustomerPanel.setCreateOrderBtnEventListener(new CreateOrderBtnEventListener() {
            public void createOrderBtnEventOccured(CreateOrderBtnEvent event) {

                controller.addCustomerOrder(event.getCustomerOrder(), homePanel);
                controller.swapPanel(MainFrame.this, addCustomerPanel, homePanel);
            }
        });

        authPanel.setAuthenticationEventListener(new AuthEventListener() {
            public void authEventOccured(AuthEvent event) {
                controller.connectToDatabase(MainFrame.this,
                        event.getUsername(), event.getPassword());

                boolean isConnected = false;

                if (controller.getDb().getConn() != null) {
                    isConnected = true;
                }

                if (isConnected) {
                    List<CustomerOrder> customerOrderList = controller
                            .searchCustomer(0);
                    homePanel.setData(customerOrderList);
                    controller.populateTheTree(homePanel.getCustomerTree(), homePanel.getDm());

                    //  List<CustomerOrder> memberList = controller
                    //         .searchMember("");
                    //  homePanel.setData(memberList);

                    //   setJMenuBar(createMenus());
                    controller.swapPanel(MainFrame.this, authPanel,
                            homePanel);

                } else {
                    JOptionPane
                            .showMessageDialog(
                                    MainFrame.this,
                                    "Failed to login, Please check your username and password again.",
                                    "Wrong Credentials",
                                    JOptionPane.OK_OPTION);
                }

            }
        });

    }
}
