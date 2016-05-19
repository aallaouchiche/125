package app.playground2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class InnerJoinExample
{
    private static final Logger log =
        LoggerFactory.getLogger( InnerJoinExample.class );
    
    private final String JOIN_SQL   =
        "SELECT credit_card.name, "
        +    "credit_card.number, "
        +    "credit_card.expiration_date, "
        +    "customer.first_name, "
        +    "customer.last_name "
        +    "FROM credit_card "
        + "INNER JOIN customer "
        +    "ON credit_card.cust_id = customer.ident";
    
    private final int   CC_NAME_INX         = 1;
    private final int   CC_NUMBER_INX       = 2;
    private final int   CC_EXPIRY_INX       = 3;
    private final int   C_FIRST_NAME_INX    = 4;
    private final int   C_LAST_NAME_INX     = 5;
                                        
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;

    public static void main(String[] args)
    {
        try
        {
            new InnerJoinExample().execute();
        }
        catch ( SQLException exc )
        {
            log.error( "SQL Failure", exc );
        }
    }

    public void execute()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            execute( conn );
        }
    }
    
    public void execute( Connection conn )
        throws SQLException
    {
        Statement   statement   = conn.createStatement();
        statement.executeQuery( JOIN_SQL );
        ResultSet   resultSet   = statement.getResultSet();
        
        System.out.println( "dumping result set" );
        while ( resultSet.next() )
        {
            String  cFirst   = resultSet.getString( C_FIRST_NAME_INX );
            String  cLast    = resultSet.getString( C_LAST_NAME_INX );
            String  ccName   = resultSet.getString( CC_NAME_INX );
            String  ccNumber = resultSet.getString( CC_NUMBER_INX );
            Date    ccExpiry = resultSet.getDate( CC_EXPIRY_INX );
            
            StringBuilder   bldr    = new StringBuilder();
            bldr.append( cLast ).append( ", " );
            bldr.append( cFirst ).append( ": " );
            bldr.append( "\"" ).append( ccName ).append( "\" " );
            bldr.append( ccNumber ).append( " " );
            bldr.append( "expires " ).append( ccExpiry );
            log.info( ": " + bldr.toString() );
        }
        
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
