package app.playground2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.Address;
import util.Name;

public class CustomerTable
{
    private static CustomerTable  instance;
    
    private final String connName;
    private final String connPassword;
    private final String connURL;
    
    private static final String CREATE_TABLE_SQL    =
        "CREATE TABLE customer ( "
        + "ident INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, "
        + "first_name VARCHAR( 30 ), "
        + "last_name VARCHAR( 30 ) NOT NULL, "
        + "middle_name VARCHAR( 30 ), "
        + "address1 VARCHAR( 30 ) NOT NULL, "
        + "address2 VARCHAR( 30 ), "
        + "city VARCHAR( 30 ), "
        + "state CHARACTER( 2 ), "
        + "zip VARCHAR( 10 ), "
        + "PRIMARY KEY ( ident )"
        + ")";

    private static final String DROP_TABLE_SQL  = "DROP TABLE customer";
    
    private static final String INSERT_SQL  =
        "INSERT INTO customer ("
        +   "first_name, "
        +   "last_name, "
        +   "middle_name, "
        +   "address1, "
        +   "address2, "
        +   "city, "
        +   "state, "
        +   "zip"
        + ")"
        + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
    
    private static final String SELECT_ALL_SQL  = "SELECT * FROM customer";
    
    private static final String SELECT_BY_NAME_SQL  =
        "SELECT * FROM customer "
        + "WHERE first_name = ? AND last_name = ?";
    
    // Updates all fields EXCEPT credit cards
    private static final String UPDATE_SQL  =
        "UPDATE customer SET "
        +   "first_name = ?, "
        +   "last_name = ?, "
        +   "middle_name = ?, "
        +   "address1 = ?, "
        +   "address2 = ?, "
        +   "city = ?, "
        +   "state = ?, "
        +   "zip = ? "
        + "WHERE ident = ?";
    
    private 
    CustomerTable( String connName, String connPassword, String connURL )
    {
        this.connName = connName;
        this.connPassword = connPassword;
        this.connURL = connURL;
    }
    
    public static CustomerTable 
    getInstance( String connName, String connPassword, String connURL )
    {
        if ( instance == null )
            instance = new CustomerTable( connName, connPassword, connURL );
        return instance;
    }
    
    public boolean exists()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            boolean rcode   = exists( conn );
            return rcode;
        }
    }
    
    private boolean exists( Connection conn )
        throws SQLException
    {
        DatabaseMetaData    metaData    = conn.getMetaData();
        ResultSet           resultSet   =
        metaData.getTables( null, null, "CUSTOMER", null );
        
        boolean rval = resultSet.next();
        resultSet.close();
        
        return rval;
    }

    public void dropTable()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            dropTable( conn );
        }
    }
    
    private void dropTable( Connection conn )
        throws SQLException
    {
        if ( exists() )
        {
            Statement   statement   = conn.createStatement();
            statement.executeUpdate( DROP_TABLE_SQL );
            statement.close();
        }
    }
    
    public void createTable()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            createTable( conn );
        }
    }

    private void createTable( Connection conn )        
        throws SQLException
    {
        if ( !exists() )
        {
            Statement   statement   = conn.createStatement();
            statement.executeUpdate( CREATE_TABLE_SQL );
            statement.close();
        }
    }
    
    public void insert( Customer customer )
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            insert( conn, customer );
        }
    }
    
    private void insert( Connection conn, Customer customer )
        throws SQLException
    {
        Address             addr    = customer.getAddress();
        String              addr1   = addr.address1;
        String              addr2   = addr.address2;
        String              city    = addr.city;
        String              state   = addr.state;
        String              zip     = addr.zip_code;
        
        Name                name    = customer.getName();
        String              first   = name.first;
        String              last    = name.last;
        String              middle  = name.middle;
        
        int                 flags       = Statement.RETURN_GENERATED_KEYS;
        PreparedStatement   statement   = 
            conn.prepareStatement( INSERT_SQL, flags );
        statement.setString( 1, first );
        statement.setString( 2, last );
        statement.setString( 3, middle );
        statement.setString( 4, addr1 );
        statement.setString( 5, addr2 );
        statement.setString( 6, city );
        statement.setString( 7, state );
        statement.setString( 8, zip );
        statement.executeUpdate();

        ResultSet   results = statement.getGeneratedKeys();
        if ( !results.next() )
            throw new Error( "update customer malfunction" );
        customer.setIdent( results.getInt( 1 ) );
        statement.close();
        
        CreditCardTable cardTable   = 
            CreditCardTable.getInstance( connName, connPassword, connURL );
        cardTable.insert( conn, customer );
    }
    
    public List<Customer> selectAll()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            List<Customer>  list    = selectAll( conn );
            return list;
        }
    }
    
    private List<Customer>  selectAll( Connection conn )
        throws SQLException
    {
        PreparedStatement   statement   =
            conn.prepareStatement( SELECT_ALL_SQL );
        ResultSet           results     = statement.executeQuery();
        
        List<Customer>      customers   = buildCustomerList( conn, results );
        statement.close();
        
        return customers;
    }
    
    public List<Customer> select( String first, String last )
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            List<Customer>  list    = select( conn, first, last );
            return list;
        }
    }
    
    private List<Customer>
    select( Connection conn, String first, String last )
        throws SQLException
    {
        PreparedStatement   statement   =
            conn.prepareStatement( SELECT_BY_NAME_SQL );
        statement.setString( 1, first );
        statement.setString( 2, last );
        ResultSet           results     =
            statement.executeQuery();
        List<Customer>      customers   = buildCustomerList( conn, results );
        return customers;
    }
    
    private List<Customer>
    buildCustomerList( Connection conn, ResultSet results )
        throws SQLException
    {
        CreditCardTable     ccTable     = 
            CreditCardTable.getInstance( connName, connPassword, connURL );
        List<Customer>      list        = new ArrayList<>();
        
        while ( results.next() )
        {
            int     ident   = results.getInt( "ident" );
            String  first   = results.getString( "first_name" );
            String  last    = results.getString( "last_name" );
            String  middle  = results.getString( "middle_name" );
            Name    name    = new Name( first, last, middle );
            
            String  addr1   = results.getString( "address1" );
            String  addr2   = results.getString( "address2" );
            String  city    = results.getString( "city" );
            String  state   = results.getString( "state" );
            String  zip     = results.getString( "zip" );
            Address addr    = new Address( addr1, addr2, city, state, zip );
            
            Customer            customer    = 
                new Customer( name, addr, ident );            
            List<CreditCard>    creditCards = 
                ccTable.selectAll( conn, customer );
            for ( CreditCard card : creditCards )
                customer.addCreditCard( card );
            
            list.add( customer );
        }
        
        return list;
    }
    
    public void update( Customer customer )
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            update( conn, customer );
        }
    }
    
    private void update( Connection conn, Customer customer )
        throws SQLException
    {
        PreparedStatement   statement   = conn.prepareStatement( UPDATE_SQL );
        Name                name        = customer.getName();
        Address             addr        = customer.getAddress();
        int                 recID       = customer.getIdent();
        
        statement.setString( 1, name.first );
        statement.setString( 2, name.last );
        statement.setString( 3, name.middle );
        statement.setString( 4, addr.address1 );
        statement.setString( 5, addr.address2 );
        statement.setString( 6, addr.city );
        statement.setString( 7, addr.state );
        statement.setString( 8, addr.zip_code );
        statement.setInt( 9, recID );
        statement.executeUpdate();
        
        statement.close();
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection conn = 
            DriverManager.getConnection( connURL, connName, connPassword );
        return conn;
    }
}
