package app.playground2;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.DBConstants;

public class SimpleQueryExample
{
    private static final String SELECT_SQL  =
        "SELECT first_name, last_name, birth_date "
        + "FROM student ORDER BY last_name, first_name";
    
    public static void main(String[] args)
    {
        try
        {
            new SimpleQueryExample().execute();
        }
        catch ( Exception exc )
        {
            exc.printStackTrace();
        }
    }
    
    public SimpleQueryExample()
        throws ClassNotFoundException
    {
        DBConstants.init();
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection  conn    = 
            DriverManager.getConnection( DBConstants.DB_URL );
        return conn;
    }

    public void execute()
        throws SQLException
    {
        try ( Connection conn = getConnection();)
        {
            execute( conn );
        }
    }
    
    public void execute( Connection conn )
        throws SQLException
    {
        Statement   statement   = conn.createStatement();
        ResultSet   resultSet   = statement.executeQuery( SELECT_SQL );
        String      fmt         = "%s, %s: born on %s%n";
        
        while ( resultSet.next() )
        {
            String  first   = resultSet.getString( "first_name" );
            String  last    = resultSet.getString( "last_name" );
            Date    born    = resultSet.getDate( "birth_date" );
            System.out.printf( fmt, last, first, born.toString() );
        }
        
        statement.close();
    }
}
