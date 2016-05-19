package app.playground3;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Calendar;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.playground2.SubQueryExample;
import util.DBConstants;

public class WeatherTable
{
    private static final Logger log =
        LoggerFactory.getLogger( SubQueryExample.class );
    
    public static final Date    POPULATE_DATE;
    
    static
    {
        Calendar    calendar    = Calendar.getInstance();
        calendar.set( 2016, 0, 1, 0, 0, 0 );
        POPULATE_DATE = new Date( calendar.getTimeInMillis() );
    }
                
    private static final String CREATE_TABLE_SQL    =
        "CREATE TABLE weather ( "
        + "ident INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, "
        + "date DATE NOT NULL, "
        + "time TIME NOT NULL, "
        + "temp DOUBLE, "
        + "humidity DOUBLE, "
        + "pressure  double, "
        + "PRIMARY KEY ( ident )"
        + ")";
    
    private static final String INSERT_SQL  =
        "INSERT INTO weather ("
        +   "date, "
        +   "time, "
        +   "temp, "
        +   "humidity, "
        +   "pressure "
        + ")"
        + "VALUES ( ?, ?, ?, ?, ? )";
    
    private static final String SELECT_ALL_SQL  = "SELECT * FROM weather";

    private static final String DROP_TABLE_SQL  = "DROP TABLE weather";
    
    private static final int    SECS_PER_DAY    = 60 * 60 * 24;
    private static final int    SAMPLE_INTERVAL = 300;  // 5 minutes
    private static final double TEMP_MIN        = 30;   // fahrenheit
    private static final double TEMP_MAX        = 55;   // fahrenheit
    private static final double HUMIDITY_MIN    = 25;   // per-cent
    private static final double HUMIDITY_MAX    = 75;   // per-cent
    private static final double PRESSURE_MIN    = 28.9; // millibars
    private static final double PRESSURE_MAX    = 30.5; // millibars
    
    private static final int    numSamples      = 
        (int)(SECS_PER_DAY / SAMPLE_INTERVAL);
    private static final int    midSample       = numSamples / 2;
    
    private int     nextSample      = 0;
    private double  nextTemp        = TEMP_MIN;
    private double  nextHumidity    = HUMIDITY_MIN;
    private double  nextPressure    = PRESSURE_MIN;
    
    private final Random        randy           = new Random();
    
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;
    
    public static void main( String[] args )
    {
        try
        {
            WeatherTable    weather = new WeatherTable();
            weather.clean();
            weather.populateTable();
            weather.dumpTable();
        }
        catch ( SQLException exc )
        {
            log.error( "populate weather table failure", exc );
        }
    }

    public boolean exists()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            boolean rcode   = exists( conn );
            return rcode;
        }
    }
    
    public void clean()
        throws SQLException
    {
        dropTable();
        createTable();
    }
    
    public void createTable()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            createTable( conn );
        }
    }

    private void createTable( Connection conn )        
        throws SQLException
    {
        log.info( "Attempting to create weather table" );
        if ( !exists() )
        {
            Statement   statement   = conn.createStatement();
            statement.executeUpdate( CREATE_TABLE_SQL );
            statement.close();
            log.info( "Weather table created" );
        }
        else
            log.info( "Weather table already exists" );
    }

    public void dropTable()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            dropTable( conn );
        }
    }
    
    private void dropTable( Connection conn )
        throws SQLException
    {
        log.info( "Attempting to delete weather table" );
        if ( exists() )
        {
            Statement   statement   = conn.createStatement();
            statement.executeUpdate( DROP_TABLE_SQL );
            statement.close();
            log.info( "Weather table deleted" );
        }
        else
            log.info( "Weather table doesn't exist" );
    }
    
    private boolean exists( Connection conn )
        throws SQLException
    {
        DatabaseMetaData    metaData    = conn.getMetaData();
        ResultSet           resultSet   =
        metaData.getTables( null, null, "WEATHER", null );
        
        boolean rval = resultSet.next();
        resultSet.close();
        
        return rval;
    }
    
    public void populateTable()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            populateTable( conn );
        }
    }
    
    public void dumpTable()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            dumpTable( conn );
        }
    }
    
    public void dumpTable( Connection conn )
        throws SQLException
    {
        Statement   statement   = conn.createStatement();
        ResultSet   resultSet   = statement.executeQuery( SELECT_ALL_SQL );
        log.info( "dump weather table" );
        while ( resultSet.next() )
        {
            Date            date        = resultSet.getDate( "date" );
            Time            time        = resultSet.getTime( "time" );
            double          temp        = resultSet.getDouble( "temp" );
            double          humidity    = resultSet.getDouble( "humidity" );
            double          pressure    = resultSet.getDouble( "pressure" );
            
            String          fmt         = "%5.3f";
            StringBuilder   bldr        = new StringBuilder();
            String          sTemp       = String.format( fmt, temp );
            String          sHumidity   = String.format( fmt, humidity );
            String          sPressure   = String.format( fmt, pressure );
            
            bldr.append( date ).append( ", " );
            bldr.append( time ).append( ", " );
            bldr.append( sTemp ).append( ", " );
            bldr.append( sHumidity ).append( ", " );
            bldr.append( sPressure );
          
            log.info( bldr.toString());
        }
        log.info( "dump weather table complete" );
    }
    
    public void populateTable( Connection conn )
        throws SQLException
    {        
        long        millis          = POPULATE_DATE.getTime();
        long        deltaTime       = SAMPLE_INTERVAL * 1000;
        
        Date        date            = new Date( millis );
        Time        time            = new Time( millis );
        
        log.info( "populating weather table" );
        PreparedStatement   statement   = conn.prepareStatement( INSERT_SQL );
        while ( nextSample++ < numSamples )
        {
            nextTemp();
            nextHumidity();
            nextPressure();
            statement.setDate( 1, date );
            statement.setTime( 2,  time );
            statement.setDouble( 3,  nextTemp );
            statement.setDouble( 4,  nextHumidity );
            statement.setDouble( 5,  nextPressure );
            statement.executeUpdate();
            
            time = new Time( millis + deltaTime * nextSample );
        }
        log.info( "populating weather table complete" );
    }
    
    private void nextTemp()
    {
        // Allow temperature to vary by 0 - 1 degree
        double  delta   = getDelta( 0, 1 );
        nextTemp += delta;

        if ( nextTemp < TEMP_MIN )
            nextTemp = TEMP_MIN;
        if ( nextTemp > TEMP_MAX )
            nextTemp = TEMP_MAX;
    }
    
    private void nextHumidity()
    {
        // Allow humidity to vary by up to 2%
        double  delta   = getDelta( 0, 2 );
        
        // getDelta returns values that generally ascend before midday.
        // Humidity usually decreases before midday, so change the sign.
        delta *= -1;
        nextHumidity += delta;
//        System.out.printf( "%5.3f, %5.3f%n", delta, nextHumidity );
        if ( nextHumidity < HUMIDITY_MIN )
            nextHumidity = HUMIDITY_MIN;
        if ( nextHumidity > HUMIDITY_MAX )
            nextHumidity = HUMIDITY_MAX;
    }
    
    private void nextPressure()
    {
        // Allow temperature to vary by 0 - .25 millibars
        double  delta   = getDelta( 0, .25 );
        nextPressure += delta;

        // Barometric pressure changes randomly during the day,
        // so change the sign of the delta randomly
        if ( randy.nextBoolean() )
            delta *= -1;
        
        if ( nextPressure < PRESSURE_MIN )
            nextPressure = PRESSURE_MIN;
        if ( nextPressure > PRESSURE_MAX )
            nextPressure = PRESSURE_MAX;
    }
    
    /*
     * Produces random number from a given range. Generally
     * (but not always)the numbers will be positive before midday
     * and negative after midday.
     *  1. Calculate a delta between min and max.
     *  2. Change the sign for everything after midday
     *     (e.g. temp usually get higher before miday,
     *     lower after midday)
     *  3. Randomly change the sign for about 20% of the
     *     calculations (sometimes temp goes down before
     *     midday)
     */
    private double getDelta( double min, double max )
    {
        int     iMax    = (int)(max * 100);
        int     iMin    = (int)(min * 100);
        int     iBound  = iMax - iMin;
        int     iDelta  = randy.nextInt( iBound ) + iMin;
        double  delta   = iDelta / 100.0;
        
        if ( nextSample > midSample )
            delta *= -1;
        if ( randy.nextInt( 5 ) == 0 )
            delta *= -1;
        
        return delta;
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection conn = 
            DriverManager.getConnection( connURL, connName, connPassword );
        return conn;
    }
}
