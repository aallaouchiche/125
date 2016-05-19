package app.playground3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class RSInsertExample
{
    private static final Logger log =
        LoggerFactory.getLogger( RSInsertExample.class );
                                        
    private static final String SELECT_ALL_SQL  = "SELECT * FROM account";
    
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;
    
    private final String[]  firstNames  =
        { "jane", "manny", "sally", "moe", "curly" };
    
    private final String[]  lastNames   =
        { "brady", "pep1", "brady", "pep2", "stooge" };

    public static void main(String[] args)
    {
        try
        {
            new RSInsertExample().execute();
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
        
        resultSet.moveToInsertRow();
        for ( int inx = 0 ; inx < firstNames.length ; ++inx )
        {
            String  first   = firstNames[inx];
            String  last    = lastNames[inx];
            String  info    = "Adding " + first + " " + last;
            log.info( info );
            
            resultSet.updateString( "first_name", first );
            resultSet.updateString( "last_name", last );
            resultSet.updateInt( "credits", 1 );
            resultSet.updateBoolean( "blocked", false );
            resultSet.insertRow();
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
