package app.playground3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.DBConstants;

public class DumpAccount
{    
    private static final String SELECT_ALL_SQL  = "SELECT * FROM account";
    
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;

    public static void main(String[] args)
    {
        try
        {
            new DumpAccount().dump();
        }
        catch ( SQLException exc )
        {
            exc.printStackTrace();
        }
    }
    
    public void dump()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            dump( conn );
        }
    }

    public void dump( Connection conn )
        throws SQLException
    {
        Statement   statement   = conn.createStatement();
        statement.executeQuery( SELECT_ALL_SQL );
        ResultSet   resultSet   = statement.getResultSet();
        
        System.out.println( "BEGIN DUMP OF ACCOUNT TABLE" );
        while ( resultSet.next() )
        {
            String          first   = resultSet.getString( "first_name" );
            String          last    = resultSet.getString( "last_name" );
            int             credits = resultSet.getInt( "credits" );
            boolean         blocked = resultSet.getBoolean( "blocked" );

            StringBuilder   bldr    = new StringBuilder();
            bldr.append( last ).append( ", " ).append( first ).append( ": " );
            bldr.append( credits ).append( "/" ).append( blocked );
            System.out.println( bldr );
        }
        System.out.println( "END DUMP OF ACCOUNT TABLE" );
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection conn = 
            DriverManager.getConnection( connURL, connName, connPassword );
        return conn;
    }
}
