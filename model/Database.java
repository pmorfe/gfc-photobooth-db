package model;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 11:40 PM
 */
public class Database {
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String dbName;
    private Connection conn = null;

    /**
     * <p>
     * Constructor method for the {@link model.Database}.
     * </p>
     *
     * @param conn
     *            The Connection of the database.
     * @param dbName
     *            The database name.
     *
     **/
    public Database(Connection conn, String dbName) throws SQLException {
        this.conn = conn;
        this.dbName = dbName;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * <p>
     * This method will create a database when it does not exit. and establishes
     * a connection to it.
     * </p>
     *
     * @return Returns True if establishes a connection otherwise returns False
     *
     **/
    public boolean dbConnect() {
        try {
            conn = DriverManager.getConnection("jdbc:derby:" + dbName
                    + ";create=true");
            return true;
        } catch (SQLException e) {
            System.out.println("Did not Connect to the database");
            return false;
        }
    }

    /**
     * <p>
     * This method will establishes a connection to the existing database.
     * </p>
     *
     * @return Returns True if establishes a connection otherwise returns False
     **/
    public boolean dbConnectForExisting(String username,String pass) {
        try {
            conn = DriverManager.getConnection("jdbc:derby:" + dbName+";user="+username+";password="+pass);
            System.out.println("Connected Properly Existing");
            return true;
        } catch (SQLException e) {
            System.out.println("did not Connect Properly Existing");
            return false;
        }
    }
    public void turnOnBuiltInUsers(String username,String pass) throws SQLException {

        loadDriver();
        conn = DriverManager.getConnection("jdbc:derby:" + dbName
                + ";create=true");

        String setProperty =
                "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(";
        String getProperty =
                "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY(";
        String requireAuth = "'derby.connection.requireAuthentication'";
        String defaultConnMode =
                "'derby.database.defaultConnectionMode'";
        String fullAccessUsers = "'derby.database.fullAccessUsers'";
        String readOnlyAccessUsers =
                "'derby.database.readOnlyAccessUsers'";
        String provider = "'derby.authentication.provider'";
        String propertiesOnly = "'derby.database.propertiesOnly'";

        System.out.println("Turning on authentication.");
        Statement s = conn.createStatement();

        // Set and confirm requireAuthentication
        s.executeUpdate(setProperty + requireAuth + ", 'true')");
        ResultSet rs = s.executeQuery(getProperty + requireAuth + ")");
        rs.next();
        System.out.println("Value of requireAuthentication is " +
                rs.getString(1));

        // Set authentication scheme to Derby builtin
        s.executeUpdate(setProperty + provider + ", 'BUILTIN')");

        // Create some sample users
        s.executeUpdate(
                setProperty + "'derby.user.sa', 'ajaxj3x9')");
        s.executeUpdate(
                setProperty + "'derby.user."+username+"', '"+pass+"')");

        // Define noAccess as default connection mode
        s.executeUpdate(
                setProperty + defaultConnMode + ", 'noAccess')");

        // Confirm default connection mode
        rs = s.executeQuery(getProperty + defaultConnMode + ")");
        rs.next();
        System.out.println("Value of defaultConnectionMode is " +
                rs.getString(1));

        // Define read-write user
        s.executeUpdate(
                setProperty + fullAccessUsers + ", 'sa,"+username+"')");

        // Confirm full-access users
        rs = s.executeQuery(getProperty + fullAccessUsers + ")");
        rs.next();
        System.out.println(
                "Value of fullAccessUsers is " + rs.getString(1));

        // We would set the following property to TRUE only when we were
        // ready to deploy. Setting it to FALSE means that we can always
        // override using system properties if we accidentally paint
        // ourselves into a corner.
        s.executeUpdate(setProperty + propertiesOnly + ", 'false')");
        s.close();

        conn.close();
        System.out.println("Closed connection");
        try {
            DriverManager.getConnection("jdbc:derby:" + dbName +
                    ";user=sa;password=badpass;shutdown=true");
        } catch (SQLException se) {
            if ( !se.getSQLState().equals("08006") ) {
                throw se;
            }
        }
    }
    /**
     * <p>
     * This method will commit the changes made to the database.
     * </p>
     **/
    public void dbCommit() throws SQLException {
        conn.commit();
    }

    /**
     * <p>
     * This method will disconnect to the database.
     * </p>
     **/
    public void dbDisconnect() throws SQLException {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {

        }
        System.out.println("Closed Properly");
    }

    /**
     * <p>
     * This method will shutdown the database.
     * </p>
     **/
    public void dbShutDown(String user,String pass) throws SQLException {
        try {
            DriverManager.getConnection("jdbc:derby:" + dbName +
                    ";user="+user+";password="+pass+";shutdown=true");
        } catch (SQLException se) {
            if ( !se.getSQLState().equals("08006") ) {
                throw se;
            }
        }

        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException se) {
            if ( !se.getSQLState().equals("XJ015") ) {
                throw se;
            }
        }
        System.out.println("Shutdown Properly");
    }

    /**
     * <p>
     * This method will load the driver needed for the database connection.
     * </p>
     **/
    public void loadDriver() {
        try {
            Class.forName(driver).newInstance();
            System.out.println("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + driver);
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            System.err.println("\nUnable to instantiate the JDBC driver "
                    + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            System.err.println("\nNot allowed to access the JDBC driver "
                    + driver);
            iae.printStackTrace(System.err);
        }
    }

    /**
     * <p>
     * This method will create the tables needed for the program in the
     * database.
     * </p>
     **/
    public void createTablePD() {
        Statement s = null;

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
		/*
		 * Creating a statement object that we can use for running various SQL
		 * statements commands against the database.
		 */
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("failed to create Statements");
        }
        // sql statements in creating group and member tables in the
        // database
        try {

            s.execute("create table customerOrder(orderId INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,fname varchar(50),"
                    + "lname varchar(50),contact varchar(15),street varchar(50),suburb varchar(50),postCode varchar(4), city varchar(20), state varchar(30),cost varchar(15), amountPaid varchar(15),balance varchar(15),comments clob," +
                    "sideNotes clob,isDelivered varchar(5))");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create table");
        }
    }

     public CustomerOrder getLastOrder(){
         CustomerOrder customerOrder=null;
         Statement s = null;
         PreparedStatement psInsert = null;
         ResultSet rs=null;
         try {
             psInsert = conn
                     .prepareStatement("SELECT orderId,fname,lname,contact,street,suburb,postCode,city," +
                             "state,cost,amountPaid,balance,comments,sideNotes,isDelivered from customerOrder order by orderId desc");
         } catch (SQLException e) {
             System.out.println("Failed to initialize psInsert");
         }
         try {
            // psInsert.setInt(1,orderId);
             psInsert.setMaxRows(1);
             rs = psInsert.executeQuery();
         } catch (SQLException e) {
             System.out.println("failed to create ResultSet orderId");
         }

         try {
             while (rs.next()) {
                 customerOrder = new CustomerOrder(rs.getInt(1), rs.getString(2),
                         rs.getString(3), rs.getString(4), rs.getString(5),
                         rs.getString(6), rs.getString(7), rs.getString(8),
                         rs.getString(9), rs.getString(10), rs.getString(11),
                         rs.getString(12), rs.getString(13),rs.getString(14),rs.getString(15));

             }

         } catch (SQLException e) {
             System.out.println("Failed to query");
         }


         return customerOrder;
     }
    public void insertCustomerOrder(String fName, String lName, String contact,String street, String suburb, String postCode,
                             String city, String state, String cost, String amountPaid,
                             String balance, String comments,String sideNotes, String isDelivered) {
        PreparedStatement psInsert = null;
        // sql script to insert the category.
        try {
            psInsert = conn
                    .prepareStatement("insert into customerOrder(fname,lname,contact,street,suburb,postCode,city,"
                            + "state,cost,amountPaid,balance,comments,sideNotes,isDelivered) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)" );
        } catch (SQLException e) {
            System.out.println("Failed to initialize psInsert");
        }
        // setting up the appopriate values to be inserted
        try {
            psInsert.setString(1, fName);
            psInsert.setString(2, lName);
            psInsert.setString(3, contact);
            psInsert.setString(4, street);
            psInsert.setString(5, suburb);
            psInsert.setString(6, postCode);
            psInsert.setString(7, city);
            psInsert.setString(8, state);
            psInsert.setString(9, cost);
            psInsert.setString(10, amountPaid);
            psInsert.setString(11, balance);
            psInsert.setString(12, comments);
            psInsert.setString(13, sideNotes);
            psInsert.setString(14, isDelivered);


        } catch (SQLException e) {
            System.out.println("Failed to insert data");
        }
        // execute the sql script
        try {
            psInsert.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update");
        }
    }

    public void updateCustomerOrder(int orderId, String fName, String lName, String contact,String street, String suburb, String postCode,
                             String city, String state, String cost, String amountPaid,
                             String balance, String comments,String sideNotes, String isDelivered) {
        PreparedStatement psInsert = null;
        // sql script to update the Member.
        try {
            psInsert = conn
                    .prepareStatement("update customerOrder set fname=?,lname=?,contact=?,street=?,suburb=?,postCode=?,city=?,"
                            + "state=?,cost=?,amountPaid=?,balance=?,comments=?, sideNotes=?,isDelivered=? where orderId=?");
        } catch (SQLException e) {
            System.out.println("Failed to initialize psInsert");
        }
        // setting up the appopriate values to be update
        try {
            psInsert.setString(1, fName);
            psInsert.setString(2, lName);
            psInsert.setString(3, contact);
            psInsert.setString(4, street);
            psInsert.setString(5, suburb);
            psInsert.setString(6, postCode);
            psInsert.setString(7, city);
            psInsert.setString(8, state);
            psInsert.setString(9, cost);
            psInsert.setString(10, amountPaid);
            psInsert.setString(11, balance);
            psInsert.setString(12, comments);
            psInsert.setString(13, sideNotes);
            psInsert.setString(14, isDelivered);
            psInsert.setInt(15, orderId);

        } catch (SQLException e) {
            System.out.println("Failed to update data");
        }
        // execute the sql script
        try {
            psInsert.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update");
        }
    }

    public void removeCustomerOrderById(int orderId) {
        PreparedStatement psRemove = null;

        try {
            psRemove = conn
                    .prepareStatement("delete from customerOrder where orderId = ? ");
        } catch (SQLException e) {
            System.out.println("Failed to initialize psInsert");
        }

        try {
            psRemove.setInt(1, orderId);

        } catch (SQLException e) {
            System.out.println("Failed to delete data");
        }

        try {
            psRemove.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update");
        }

    }

    public List<CustomerOrder> getCustomerOrder() {
        ResultSet rs = null;
        PreparedStatement selectId = null;
        List<CustomerOrder> customerOrders = new LinkedList<CustomerOrder>();
        CustomerOrder customerOrder;

        try {
            selectId = conn
                    .prepareStatement("SELECT orderId,fname,lname,contact,street,suburb,postCode,city" +
                            ",state,cost,amountPaid,balance,comments,sideNotes,isDelivered FROM customerOrder");
        } catch (SQLException e) {
            System.out.println("failed to create Statements");
        }

        try {

            rs = selectId.executeQuery();
        } catch (SQLException e) {
            System.out.println("failed to create ResultSet orderId");
        }

        try {
            while (rs.next()) {
                customerOrder = new CustomerOrder(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8),
                        rs.getString(9), rs.getString(10), rs.getString(11),
                        rs.getString(12), rs.getString(13),rs.getString(14), rs.getString(15));
                customerOrders.add(customerOrder);
            }

        } catch (SQLException e) {
            System.out.println("Failed to query");
        }
        return customerOrders;
    }


    public List<CustomerOrder> getCustomerOrderById(int orderId){
        List<CustomerOrder> customerOrders = new LinkedList<CustomerOrder>();
        ResultSet rs = null;
        PreparedStatement selectId = null;
        CustomerOrder customerOrder;

        try {
            selectId = conn
                    .prepareStatement("SELECT orderId,fname,lname,contact,street,suburb,postCode,city"+
                            ",state,cost,amountPaid,balance,comments,sideNotes,isDelivered FROM customerOrder where orderId=?");
        } catch (SQLException e) {
            System.out.println("failed to create Statements");
        }

        try {
            selectId.setInt(1, orderId);
            rs = selectId.executeQuery();
        } catch (SQLException e) {
            System.out.println("failed to create ResultSet userId");
        }

        try {
            while (rs.next()) {
                customerOrder = new CustomerOrder(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8),
                        rs.getString(9), rs.getString(10), rs.getString(11),
                        rs.getString(12), rs.getString(13),rs.getString(14), rs.getString(15));
                customerOrders.add(customerOrder);
            }

        } catch (SQLException e) {
            System.out.println("Failed to query");
        }

        return customerOrders;
    }

    public List<CustomerOrder> getCustomerOrderByLName(String lName){
        List<CustomerOrder> customerOrders = new LinkedList<CustomerOrder>();
        ResultSet rs = null;
        PreparedStatement selectId = null;
        CustomerOrder customerOrder;

        try {
            selectId = conn
                    .prepareStatement("SELECT orderId,fname,lname,contact,street,suburb,postCode,city"+
                            ",state,cost,amountPaid,balance,comments,sideNotes,isDelivered FROM customerOrder where lname like ?");
        } catch (SQLException e) {
            System.out.println("failed to create Statements");
        }

        try {
            selectId.setString(1, lName);
            rs = selectId.executeQuery();
        } catch (SQLException e) {
            System.out.println("failed to create ResultSet userId");
        }

        try {
            while (rs.next()) {
                customerOrder = new CustomerOrder(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8),
                        rs.getString(9), rs.getString(10), rs.getString(11),
                        rs.getString(12), rs.getString(13),rs.getString(14), rs.getString(15));
                customerOrders.add(customerOrder);
            }

        } catch (SQLException e) {
            System.out.println("Failed to query");
        }

        return customerOrders;
    }

    public void dropTableCustomerOrder() {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("failed to create Statements");
        }

        try {
            s.execute("drop table customerOrder");
        } catch (SQLException e) {
            System.out.println("Failed to Drop Table member");
        }
    }
}
