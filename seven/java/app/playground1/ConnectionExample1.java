package app.playground1;

import static util.DBConstants.DB_URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class ConnectionExample1
{
    private static final Logger log =
        LoggerFactory.getLogger( ConnectionExample1.class );
    
    public static void main(String[] args)
    {
        try
        {
            DBConstants.init();
            Connection  conn = DriverManager.getConnection( DB_URL );
            // do something interesting with the connection
            DBConstants.closeConnection( conn );
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
