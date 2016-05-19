package app.playground1;

import static util.DBConstants.DB_URL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Address;
import util.Clean;
import util.DBConstants;
import util.Name;

public class InsertExample
{
    private static final Logger log =
        LoggerFactory.getLogger( InsertExample.class );
    
    private final Name[] allNames    =
    {
        new Name( "George", "Washington" ),
        new Name( "Thomas", "Jefferson" ),
        new Name( "Abraham", "Lincoln" ),
        new Name( "Teddy", "Roosevelt" ),
        new Name( "Dwight", "Eisenhower" ),
        new Name( "Joey", "Burns" ),
        new Name( "Frank", "Zappa" ),
        new Name( "Stan", "Clarke" ),
        new Name( "Lucinda", "Williams" ),
        new Name( "Paula", "Frazer" )
    };
    
    private final Address[] allAddresses    =
    {
        new Address(
            "118 Washington Ave",
            "Apt. 4",
            "Los Angeles",
            "CA",
            "11111"
        ),
        new Address(
            "1313 Mocking Bird Ln",
            null,
            "Cairo",
            "NY",
            "22222"
        ),
        new Address(
            "1600 Pennsylvania Ave. NW",
            "Suite 1432",
            "Sioux Falls",
            "MN",
            "33333"
        ),
        new Address(
            "221B Baker Street",
            null,
            "Omaha",
            "NE",
            "44444"
        ),
        new Address(
            "15701 Fifth Ave",
            "#1641",
            "Great Falls",
            "MT",
            "55555"
        ),
        new Address(
            "2525 La Fortuna Ave",
            "#5",
            "Portland",
            "OR",
            "66666"
        ),
        new Address(
            "4059 Mt. Lee Dr.",
            null,
            "Anchorage",
            "AL",
            "77777"
        ),
        new Address(
            "2 Macquarie Street",
            "Suite 11",
            "Oklahoma City",
            "OK",
            "88888"
        ),
        new Address(
            "11 Wall Street",
            null,
            "Sioux Falls",
            "IA",
            "99999"
        ),
        new Address(
            "112 1/2 Beacon Street",
            "Third Floor",
            "Fargo",
            "ND",
            "00000"
        )
    };

    private Connection  conn;
    
    public static void main(String[] args)
    {
        Clean.main( args );
        new InsertExample().execute();
    }
    
    public InsertExample()
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
    
    private void execute()
    {
        InternalDate    iBirthDate          = new InternalDate( 1, 2, 1990 );
        InternalDate    iMatriculationDate  = new InternalDate( 2, 3, 2008 );
                    
        try
        {
            Student student = new Student( conn );
            for ( int inx = 0 ; inx < allNames.length ; ++inx )
            {
                Date    birthDate           = iBirthDate.getDate();
                Date    matriculationDate   = iMatriculationDate.getDate();
                student.name = allNames[inx];
                student.address = allAddresses[inx];
                student.birthDate = birthDate;
                student.matriculationDate = matriculationDate;
                
                log.info( "inserting student " + student.toString() );
                student.insert();

                iBirthDate.nextDate();
                iMatriculationDate.nextDate();
            }
            
            student.selectAll();
            while ( student.next() )
                System.out.println( student );
            
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
    
    /*
     * Don't pay too much attention to this. It's just a way to fudge
     * dates that look interesting and unique when printed out.
     */
    private class InternalDate
    {
        private Calendar    calendar    = Calendar.getInstance();
        
        public InternalDate( int month, int day, int year )
        {
            setDate( month, day, year );
        }
        
        public Date getDate()
        {
            long    millis  = calendar.getTimeInMillis();
            Date    date    = new Date( millis );
            return date;
        }
        
        public Date nextDate()
        {
            int     month   = calendar.get( Calendar.MONTH ) + 1;
            int     year    = calendar.get(Calendar.YEAR ) + 1;
            int     day     = calendar.get(Calendar.DAY_OF_MONTH ) + 1;
            setDate( month, day, year );
            Date    date    = getDate();
            return date;
        }
        
        private void setDate( int month, int day, int year )
        {
            calendar.set( Calendar.MONTH, month % 12 );
            calendar.set( Calendar.YEAR, year );
            calendar.set( Calendar.DAY_OF_MONTH, 1 );
            
            int actualDay   =
                day % calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
            calendar.set( Calendar.DAY_OF_MONTH, actualDay );
        }
    }
}
