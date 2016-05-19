package com.scg.meta;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DumpSchema
{
    private static final Logger log =
        LoggerFactory.getLogger( DumpSchema.class );
                            
    private static final String DB_URL          =
        "jdbc:derby://localhost:1527/memory:scgDb";
    private static final String DB_USER_NAME    = "student";
    private static final String DB_PASSWORD     = "student";
    
    public static void main( String[] args )
    {
        try
        {
            new DumpSchema().dump();
        }
        catch ( SQLException exc )
        {
            log.error( "DB error", exc );
        }
    }

    public void dump()
        throws SQLException
    {
        try ( Connection conn = getConnection() )
        {
            dump( conn );
        }
    }
    
    private Connection getConnection()
        throws SQLException
    {
        Connection  conn    =
            DriverManager.getConnection( DB_URL, DB_USER_NAME, DB_PASSWORD );
        return conn;
    }
    
    private void dump( Connection conn )
        throws SQLException
    {        
        DatabaseMetaData    metaData    = conn.getMetaData();
        ResultSet           resultSet   =
            metaData.getTables( null, null, "STUDENT", null );
                
        String[]    types   = 
        {
            "TABLE_CAT", 
            "TABLE_SCHEM", 
            "TABLE_NAME", 
            "TABLE_TYPE", 
            "REMARKS" 
        };
        
        while ( resultSet.next() )
        {
            for ( String type : types )
            {
                System.out.println( type + ": " ); 
                resultSet.getString(type ); 
            }
        }
    }
}
