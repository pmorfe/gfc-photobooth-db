package gui;

import model.CustomerOrder;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Author: Pippo Morfe
 * Date: 15/11/2013
 * Time: 3:31 PM
 */
public class SearchTableModel extends AbstractTableModel {

    private List<CustomerOrder> customerOrderList;
    private String[] colNames = {"Customer's Name"};

    /**
     * <p>
     * This class returns a string based on the column
     * </p>
     *
     * @param column An integer which specifies the string to be return
     * @return A string that represents the heading of the column
     */
    @Override
    public String getColumnName(int column) {

        return colNames[column];
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        return customerOrderList.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        CustomerOrder customerOrder = customerOrderList.get(row);
        switch (col) {
            case 0:
                return customerOrder.toString();
        }
        return null;

    }

    public void setData(List<CustomerOrder> customerOrderList) {
        this.customerOrderList = customerOrderList;
    }

    public List<CustomerOrder> getCustomerOrderList() {
        return customerOrderList;
    }
}
