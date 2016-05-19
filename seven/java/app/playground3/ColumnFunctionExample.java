package app.playground3;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DBConstants;

public class ColumnFunctionExample
{
    private static final Logger log =
        LoggerFactory.getLogger( ColumnFunctionExample.class );
    
    private static final String GET_AVERAGES_SQL    =
        "SELECT AVG( temp ), AVG( humidity ), AVG( pressure )"
        + "FROM weather "
        + "WHERE date = ?";
    
    private final String connName       = null;
    private final String connPassword   = null;
    private final String connURL        = DBConstants.DB_URL;
                            
    public static void main(String[] args)
    {
        try
        {
            ColumnFunctionExample   example = new ColumnFunctionExample();
            example.getAverages( WeatherTable.POPULATE_DATE );
        }
        catch ( SQLException exc )
        {
            log.debug( "SQL failure", exc );
        }
        catch ( Exception exc )
        {
            log.debug( "General failure", exc );
        }
    }

    public void getAverages( Date date )
        throws SQLException, Exception
    {
        try ( Connection conn = getConnection() )
        {
            getAverages( conn, date );
        }
    }
    
    public void getAverages( Connection conn, Date date )
        throws SQLException, Exception
    {
        WeatherTable    weather = new WeatherTable();
        weather.clean();
        weather.populateTable( conn );
        
        PreparedStatement   statement   = 
            conn.prepareStatement( GET_AVERAGES_SQL );
        statement.setDate( 1, date );
        ResultSet           resultSet   = statement.executeQuery();
         if ( !resultSet.next() )
            throw new Exception( "weather table query failure" );
        
        String          fmt         = "%5.3f";
        double          temp        = resultSet.getDouble( 1 );
        double          humidity    = resultSet.getDouble( 2 );
        double          pressure    = resultSet.getDouble( 3 );
        String          sTemp       = String.format( fmt, temp );
        String          sHumidity   = String.format( fmt, humidity );
        String          sPressure   = String.format( fmt, pressure );
        StringBuilder   bldr        = new StringBuilder();
        bldr.append( "Averages for " ).append( date ).append( '\n' );
        bldr.append( "temp = " ).append( sTemp ).append( ", " );
        bldr.append( "humidity = " ).append( sHumidity ).append( ", " );
        bldr.append( "pressure = " ).append( sPressure );
        log.info( bldr.toString() );
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection conn = 
            DriverManager.getConnection( connURL, connName, connPassword );
        return conn;
    }
}
