package app.playground3;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class CleanAccount
{
    private static final Logger log =
        LoggerFactory.getLogger( CleanAccount.class );
                                        
    private static final String CREATE_TABLE_SQL    =
        "CREATE TABLE account ( "
        + "ident INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, "
        + "first_name VARCHAR( 30 ), "
        + "last_name VARCHAR( 30 ) NOT NULL, "
        + "credits INTEGER, "
        + "blocked BOOLEAN, "
        + "PRIMARY KEY ( ident )"
        + ")";

    private static final String DROP_TABLE_SQL  = "DROP TABLE account";
    
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;
    
    public static void main( String[] args )
    {
        try
        {
            new CleanAccount().clean();
        }
        catch ( SQLException exc )
        {
            exc.printStackTrace();
        }
    }
    
    public void clean()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            clean( conn );
        }
    }
    
    private void clean( Connection conn )
        throws SQLException
    {
        dropTable( conn );
        createTable( conn );
    }
    
    private boolean exists( Connection conn )
        throws SQLException
    {
        DatabaseMetaData    metaData    = conn.getMetaData();
        ResultSet           resultSet   =
        metaData.getTables( null, null, "ACCOUNT", null );
        
        boolean rval = resultSet.next();
        resultSet.close();
        
        return rval;
    }

    private void dropTable( Connection conn )
        throws SQLException
    {
        if ( exists( conn ) )
        {
            log.info( "dropping account table" );;
            Statement   statement   = conn.createStatement();
            statement.executeUpdate( DROP_TABLE_SQL );
            statement.close();
        }
   }

    private void createTable( Connection conn )        
        throws SQLException
    {
        if ( !exists( conn ) )
        {
            log.info( "creating account table" );;
            Statement   statement   = conn.createStatement();
            statement.executeUpdate( CREATE_TABLE_SQL );
            statement.close();
        }
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection conn = 
            DriverManager.getConnection( connURL, connName, connPassword );
        return conn;
    }
}
