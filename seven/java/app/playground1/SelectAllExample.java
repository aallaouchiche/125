package app.playground1;

import static util.DBConstants.DB_URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class SelectAllExample
{
    private static final Logger log =
        LoggerFactory.getLogger( SelectAllExample.class );
                
    private Connection  conn;
    
    public static void main(String[] args)
    {
        new SelectAllExample().execute();
    }
    
    public SelectAllExample()
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
            student.selectAll();
            log.info( "begin student list" );
            while ( student.next() )
                log.info( student.toString() );
            log.info( "end student list" );
            student.close();
        }
        catch ( SQLException exc )
        {
            log.error( "statement creation error", exc );
        }
        finally
        {
            DBConstants.closeConnection( conn );
        }
    }
}
