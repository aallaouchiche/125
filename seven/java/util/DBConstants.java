package util;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConstants
{
    public static final String  DB_URL                  = 
        "jdbc:derby:EmployeeDb;create=true";
    public static final String  DRIVER_CLASS_NAME       =
        "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String  DERBY_DIR_NAME          =
        "target/derbyDb";
    public static final String  DERBY_HOME_PROP_NAME    =
        "derby.system.home";
    public static final String  DERBY_HOME              =
        "target/derbyDb";
    
    public static void init()
        throws ClassNotFoundException
    {
        File    derbyDir    = new File( DERBY_DIR_NAME );
        derbyDir.mkdirs();
        System.setProperty( DERBY_HOME_PROP_NAME, DERBY_HOME );
        Class.forName( DRIVER_CLASS_NAME );
    }
    
    public static boolean closeConnection( Connection conn )
    {
        boolean rval    = true;
        
        try
        {
            if ( conn != null )
                conn.close();
        }
        catch ( SQLException exc )
        {
            exc.printStackTrace();
            rval = false;
        }
        
        return rval;
    }
}
