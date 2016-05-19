package app.playground2;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class CleanPlayground
{
    private static final Logger log =
        LoggerFactory.getLogger( CleanPlayground.class );
                            
    private static final String connName        = null;
    private static final String connPassword    = null;
    private static final String connURL         = DBConstants.DB_URL;

    public static void main( String[] args )
    {
        try
        {
            CustomerTable   custTable   = 
                CustomerTable.getInstance( connName, connPassword, connURL );
            CreditCardTable ccTable =
                CreditCardTable.getInstance( connName, connPassword, connURL );
            
            log.info( "dropping credit_card table" );
            ccTable.dropTable();
            log.info( "dropping customer table" );
            custTable.dropTable();
            
            log.info( "creating customer table" );
            custTable.createTable();
            log.info( "creating credit_card table" );
            ccTable.createTable();
        }
        catch ( SQLException exc )
        {
            log.error( "", exc );
        }
    }
}
 