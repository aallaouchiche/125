package app.playground1;

import static util.DBConstants.DB_URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class DropTableExample
{
    private static final Logger log =
        LoggerFactory.getLogger( DropTableExample.class );
                
    public static void main(String[] args)
    {
        try
        {
            DBConstants.init();
            Connection  conn = DriverManager.getConnection( DB_URL );
            Student student = new Student( conn );
            if ( student.exists() )
            {
                log.info( "dropping table" );
                student.dropTable();
            }
            else
                log.info( "table does not exist" );
            student.close();
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
}
