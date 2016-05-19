package app.playground2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class SubQueryExample
{
    private static final Logger log =
        LoggerFactory.getLogger( SubQueryExample.class );
    
    private final String SUBQUERY_SQL   =
        "SELECT credit_card.name, "
        +    "credit_card.number, "
        +    "credit_card.expiration_date "
        + "FROM credit_card "
        + "WHERE cust_id = "
        +     "( SELECT ident FROM  customer "
        +         "WHERE first_name = ? "
        +         "AND last_name = ? )";
    
    private final int   CC_NAME_INX         = 1;
    private final int   CC_NUMBER_INX       = 2;
    private final int   CC_EXPIRY_INX       = 3;
                                        
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;

    public static void main( String[] args )
    {
        try
        {
            SubQueryExample example = new SubQueryExample();
            example.execute( "Ayn", "Rand" );
        }
        catch ( SQLException exc )
        {
            log.error( "sql subquery failure", exc );
        }
    }
    
    public void execute( String firstName, String lastName )
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            execute( conn, firstName, lastName );
        }
    }
    
    public void 
    execute( Connection conn, String firstName, String lastName )
        throws SQLException
    {
        PreparedStatement   statement = 
            conn.prepareStatement( SUBQUERY_SQL );
        statement.setString( 1, firstName );
        statement.setString( 2, lastName );
        ResultSet           results = statement.executeQuery();
        
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( "All credit cards for " ).append( lastName );
        bldr.append( " " ).append( firstName );
        log.info( bldr.toString() );
        
        while ( results.next() )
        {
            String  name    = results.getString( CC_NAME_INX );
            String  number  = results.getString( CC_NUMBER_INX );
            String  expiry  = results.getString( CC_EXPIRY_INX );
            bldr = new StringBuilder( "    " );
            bldr.append( "\"" ).append( name ).append( "\" " );
            bldr.append( number ).append( " " );
            bldr.append( "expires " ).append( expiry );
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
