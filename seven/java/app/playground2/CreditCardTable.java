package app.playground2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.Address;

public class CreditCardTable
{
    private static CreditCardTable  instance;
    
    private final String connName;
    private final String connPassword;
    private final String connURL;
    
    private static final String CREATE_TABLE_SQL    =
        "CREATE TABLE credit_card ( "
        + "ident INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, "
        + "cust_id INTEGER, "
        + "name VARCHAR(45), "
        + "number VARCHAR(25) NOT NULL, "
        + "cvv VARCHAR(5) NOT NULL, "
        + "expiration_date DATE NOT NULL, "
        + "address1 VARCHAR(30) NOT NULL, "
        + "address2 VARCHAR(30), "
        + "city VARCHAR(30) NOT NULL, "
        + "state VARCHAR(30) NOT NULL, "
        + "zip_code CHARACTER(5) NOT NULL, "
        + "PRIMARY KEY ( ident ), "
        + "FOREIGN KEY ( cust_id ) "
        +     " REFERENCES customer ( ident )"
        + ")";
    
    private static final String INSERT_SQL  =
        "INSERT INTO credit_card ("
        +   "cust_id, "
        +   "name, "
        +   "number, "
        +   "cvv, "
        +   "expiration_date, "
        +   "address1, "
        +   "address2, "
        +   "city, "
        +   "state, "
        +   "zip_code"
        + ")"
        + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    private static final String DROP_TABLE_SQL    =
        "DROP TABLE credit_card";

    private static final String DELETE_ROW_SQL    =
        "DELETE FROM TABLE credit_card "
        + "WHERE ident = ?";
    
    private static final String SELECT_ALL_SQL  =
        "SELECT * FROM credit_card "
        + "WHERE cust_id = ?";

    private 
    CreditCardTable( String connName, String connPassword, String connURL )
    {
        this.connName = connName;
        this.connPassword = connPassword;
        this.connURL = connURL;
    }
    
    public static CreditCardTable 
    getInstance( String connName, String connPassword, String connURL )
    {
        if ( instance == null )
            instance = new CreditCardTable( connName, connPassword, connURL );
        return instance;
    }
    
    public boolean exists()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            boolean rval    = exists( conn );
            return rval;
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
    
    public void dropTable()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            dropTable( conn );
        }
    }
    
    public void insert( Connection conn, Customer customer )
        throws SQLException
    {
        int                 customerID  = customer.getIdent();
        List<CreditCard>    cards       = customer.getCreditCards();
        PreparedStatement   statement   = conn.prepareStatement( INSERT_SQL );
        for ( CreditCard card : cards )
        {
            String  name    = card.getName();
            Address addr    = card.getAddress();
            String  number  = card.getNumber();
            String  cvv     = card.getCvv();
            Date    expiry  = 
                new Date( card.getExpiry().getTime() );

            int     inx     = 1;
            statement.setInt( inx++, customerID );
            statement.setString( inx++, name );
            statement.setString( inx++, number );
            statement.setString( inx++, cvv );
            statement.setDate( inx++, expiry );
            statement.setString( inx++, addr.address1 );
            statement.setString( inx++, addr.address2 );
            statement.setString( inx++, addr.city );
            statement.setString( inx++, addr.state );
            statement.setString( inx++, addr.zip_code );
            
            statement.executeUpdate();
        }
    }
    
    public List<CreditCard> selectAll( Customer customer )
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            List<CreditCard>    list    = selectAll( conn, customer );
            return list;
        }
    }
    
    public void deleteRow( CreditCard card )
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            deleteRow( conn, card );
        }
    }
    
    private void deleteRow( Connection conn, CreditCard card )
        throws SQLException
    {
        Integer ident   = card.getIdent();
        if ( ident == null )
            throw new IllegalArgumentException();
        PreparedStatement   statement   = 
            conn.prepareStatement( DELETE_ROW_SQL );
        statement.setInt( 1,  ident );
        statement.executeUpdate();
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
    
    private boolean exists( Connection conn )
        throws SQLException
    {
        DatabaseMetaData    metaData    = conn.getMetaData();
        ResultSet           resultSet   =
            metaData.getTables( null, null, "CREDIT_CARD", null );
        
        boolean rval = resultSet.next();
        resultSet.close();
        
        return rval;
    }
    
    public List<CreditCard> selectAll( Connection conn, Customer customer )
        throws SQLException
    {
        List<CreditCard>    list        = new ArrayList<>();
        PreparedStatement   statement   = 
            conn.prepareStatement( SELECT_ALL_SQL );
        statement.setInt( 1, customer.getIdent() );
        ResultSet           results     = statement.executeQuery();
        
        while ( results.next() )
        {
            String      name        = results.getString( "name" );
            String      number      = results.getString( "number" );
            String      cvv         = results.getString( "cvv" );
            Date        expiry      = results.getDate( "expiration_date" );
            String      addr1       = results.getString( "address1" );
            String      addr2       = results.getString( "address2" );
            String      city        = results.getString( "city" );
            String      state       = results.getString( "state" );
            String      zip         = results.getString( "zip_code" );
            Address     addr        = 
                new Address( addr1, addr2, city, state, zip );
            CreditCard  card        = 
                new CreditCard( name, addr, number, cvv, expiry );
            list.add( card );
        }
        
        return list;
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection conn = 
            DriverManager.getConnection( connURL, connName, connPassword );
        return conn;
    }
    
}
