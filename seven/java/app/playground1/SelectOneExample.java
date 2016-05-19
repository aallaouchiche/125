package app.playground1;

import static util.DBConstants.DB_URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class SelectOneExample
{
    private static final Logger log =
        LoggerFactory.getLogger( SelectOneExample.class );
    
    private Connection  conn;
                
    public static void main( String[] args )
    {
        new SelectOneExample().execute();
    }

    public SelectOneExample()
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
        try
        {
            Student student = new Student( conn );
            student.selectOne( "George", "Washington" );
            if ( student.next() )
                log.info( student.toString() );
            else
                log.info( "George Washington not found" );
            student.close();
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
