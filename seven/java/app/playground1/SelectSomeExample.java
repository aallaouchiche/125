package app.playground1;

import static util.DBConstants.DB_URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class SelectSomeExample
{
    private static final Logger log =
        LoggerFactory.getLogger( SelectSomeExample.class );
    
    private Connection  conn;
                
    public static void main(String[] args)
    {
        new SelectSomeExample().execute();
    }

    public SelectSomeExample()
    {
        try
        {
            DBConstants.init();
            conn = DriverManager.getConnection( DB_URL );
        }
        catch ( ClassNotFoundException exc )
        {
            log.error( "driver not found", exc );
        }
        catch ( SQLException exc )
        {
            log.error( "db connection failed", exc );
        }
    }
    
    public void execute()
    {
        String  sql =
             "SELECT * FROM student WHERE birth_date >= '1995-01-01'"
            + "ORDER BY last_name";
        try
        {
            Student student = new Student( conn );
            student.select( sql );
            log.info( "listing students" );
            while ( student.next() )
                log.info( student.toString() );
            log.info( "done listing students" );
        }
        catch ( SQLException exc )
        {
            log.error( "select error", exc );
        }
        finally
        {
            DBConstants.closeConnection( conn );
        }
    }

}
