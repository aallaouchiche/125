package app.playground2;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;
import util.Name;

public class UpdateExample
{
    private static final Logger log =
        LoggerFactory.getLogger( UpdateExample.class );
                            
    private final String  connName      = null;
    private final String  connPassword  = null;
    private final String  connURL       = DBConstants.DB_URL;
    
    public static void main(String[] args)
        throws SQLException
    {
        UpdateExample   example = new UpdateExample();
        example.updateMiddleName( "Alfred", "Hitchcock", "Joseph" );
    }

    public void 
    updateMiddleName( String first, String last, String newMiddle )
        throws SQLException
    {  
        StringBuilder   bldr        = new StringBuilder();
        bldr.append( "\"" ).append( first ).append( " " );
        bldr.append( last ).append( "\": " );
        
        CustomerTable   table       = 
            CustomerTable.getInstance( connName, connPassword, connURL );
        List<Customer>  customers   = table.select( first, last );
        int             count       = customers.size();
        
        if ( count == 0 )
            bldr.append( "not found" );
        else if ( count > 1 )
            bldr.append( "too many records found" );
        else
        {
            Customer    customer    = customers.get( 0 );
            Name        name        = customer.getName();
            name.middle = newMiddle;
            table.update( customer );
            bldr.append( "middle name updated to \"" ).append( newMiddle );
            bldr.append( "\"" );
        }
        
        log.info( bldr.toString() );
        validateNameChange( first, last );
    }
    
    private void validateNameChange( String first, String last )
        throws SQLException
    {
        CustomerTable   table       = 
            CustomerTable.getInstance( connName, connPassword, connURL );
        List<Customer>  customers   = table.select( first, last );
        
        if ( customers.size() != 1 )
            System.out.println( "validation failed" );
        else
        {
            StringBuilder   bldr    = new StringBuilder();
            bldr.append( "validating middle name: ");
            
            Customer    customer    = customers.get( 0 );
            String      middle      = customer.getName().middle;
            bldr.append( middle );
            log.info( bldr.toString() );
        }
    }
}
