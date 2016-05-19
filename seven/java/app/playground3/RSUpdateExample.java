package app.playground3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class RSUpdateExample
{
    private static final Logger log =
        LoggerFactory.getLogger( RSUpdateExample.class );
                                        
    private static final String SELECT_ALL_SQL  = "SELECT * FROM account";
    
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;
    
    public static void main( String[] args )
    {
        try
        {
            new RSUpdateExample().execute();
            DumpAccount.main( args );
        }
        catch ( SQLException exc )
        {
            log.error( "Account dump failure", exc );
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
    
    private void execute( Connection conn )
        throws SQLException
    {
        int         type        = ResultSet.TYPE_SCROLL_SENSITIVE;
        int         curr        = ResultSet.CONCUR_UPDATABLE;
        Statement   statement   = conn.createStatement( type, curr );
        statement.executeQuery( SELECT_ALL_SQL );
        ResultSet   resultSet   = statement.getResultSet();
        
        while ( resultSet.next() )
        {
            String  first   = resultSet.getString( "first_name" );
            String  last    = resultSet.getString( "last_name" );
            String  info    = "Updating " + first + " " + last;
            log.info( info );
            
            int     credits = resultSet.getInt( "credits" );
            resultSet.updateInt( "credits", ++credits );
            resultSet.updateRow();
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
