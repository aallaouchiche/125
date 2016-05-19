package app.playground2;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Address;
import util.DBConstants;
import util.Name;

public class InsertExample
{
    private static final Logger log =
        LoggerFactory.getLogger( InsertExample.class );
                
    private static final int[]  UNUSED_CAL_FIELDS   =
    {
        Calendar.HOUR_OF_DAY,
        Calendar.MINUTE,
        Calendar.SECOND,
        Calendar.MILLISECOND
    };
    
    private Calendar    calendar    = getCalendar( 1, 1, 2016 );
        
    private final Name[]  names   =
    {
        new Name( "Alfred", "Hitchcock" ),
        new Name( "Ayn", "Rand" )
    };
    
    private final Address[]   addresses   =
    {
        new Address(
            "1313 Mockingbird Ln",
            "#2B",
            "London",
            "AL",
            "11111"
        ),
        new Address(
            "4200 Yellowbrick Rd",
            null,
            "Fantasy",
            "CA",
            "22222"
        )
    };
    
    private final CreditCard[][]    creditCards =
    {
        {
            new CreditCard(
                "Albert Einstein",
                addresses[0],
                "1111111111111111",
                "111",
                nextDate()
            ),
            new CreditCard(
                "Mata Hari",
                addresses[0],
                "2222222222222222",
                "222",
                nextDate()
            ),
            new CreditCard(
                "Penny Lane",
                addresses[0],
                "3333333333333333",
                "333",
                nextDate()
            )
        },
        {
            new CreditCard(
                "Mary Finklestein",
                addresses[1],
                "4444444444444444",
                "444",
                nextDate()
            ),
            new CreditCard(
                "General Tsao",
                addresses[1],
                "5555555555555555",
                "555",
                nextDate()
            )
        }
    };
    
    private final String  connName      = null;
    private final String  connPassword  = null;
    private final String  connURL       = DBConstants.DB_URL;
    
    public static void main(String[] args)
        throws SQLException
    {
        CleanPlayground.main( args );
        new InsertExample().execute();
    }

    public void execute()
        throws SQLException
    {
        CustomerTable   table   = 
            CustomerTable.getInstance( connName, connPassword, connURL );
        
        log.info( "adding customers" );
        for ( int inx = 0 ; inx < names.length ; ++inx )
        {
            Customer    customer    = 
                new Customer( names[inx], addresses[inx] );
            for ( CreditCard card : creditCards[inx] )
                customer.addCreditCard( card );
            table.insert( customer );
        }
        
        log.info( "reading customers" );
        List<Customer>  customers   = table.selectAll();
        for ( Customer customer : customers )
            log.info( customer.toString() );
    }
    
    private Calendar getCalendar( int day, int month, int year )
    {
        Calendar    calendar    = Calendar.getInstance();
        calendar.set( Calendar.DAY_OF_MONTH, day );
        calendar.set( Calendar.MONTH, month );
        calendar.set( Calendar.YEAR, year );
        for ( int field : UNUSED_CAL_FIELDS )
            calendar.set( field, calendar.getMinimum( field ) );
        
        return calendar;
    }
    
    private Date nextDate()
    {
        calendar.add( Calendar.MONTH, 1 );
        Date    date    = calendar.getTime();
        return date;
    }
}
