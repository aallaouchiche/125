package app.playground1;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.Address;
import util.Name;

class Student
{
    private static final String CREATE_TABLE_SQL    =
        "CREATE TABLE student ( "
        + "student_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, "
        + "first_name VARCHAR(30), "
        + "last_name VARCHAR(30) NOT NULL, "
        + "birth_date DATE NOT NULL, "
        + "matriculation_date DATE NOT NULL, "
        + "address1 VARCHAR(30) NOT NULL, "
        + "address2 VARCHAR(30), "
        + "city VARCHAR(30) NOT NULL, "
        + "state VARCHAR(30) NOT NULL, "
        + "zip_code CHARACTER(5) NOT NULL, "
        + "PRIMARY KEY ( student_id )"
        + ")";
    
    private static final String DROP_TABLE_SQL  =
        "DROP TABLE student";
    
    private static final String INSERT_SQL  =
        "INSERT INTO student ("
        +    "first_name, "
        +    "last_name, "
        +    "birth_date, "
        +    "matriculation_date, "
        +    "address1, "
        +    "address2, "
        +    "city, "
        +    "state, "
        +    "zip_code"
        +    ")"
        +    "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    
    private static final String SELECT_ALL_SQL  =
         "SELECT * FROM student "
        + "ORDER BY last_name, first_name";

    public int      ident;
    public Name     name;
    public Address  address;
    public Date     birthDate;
    public Date     matriculationDate;
    
    private final Connection    conn;
    private Statement           selectStatement     = null;
    private Statement           selectOneStatement  = null;
    private PreparedStatement   selectAllStatement  = null;
    private PreparedStatement   insertStatement     = null;
    private ResultSet           resultSet           = null;
    
    public Student( Connection conn )
    {
        this( conn, new Name(), new Address(), new Date( 0 ), new Date( 0 ) );
    }
    
    public Student(
        Connection conn,
        Name name,
        Address address,
        Date birthDate,
        Date matriculationDate
    )
    {
        this.conn = conn;
        this.name = name;
        this.address = address;
        this.birthDate = birthDate;
        this.matriculationDate = matriculationDate;
    }
    
    public boolean exists()
        throws SQLException
    {
        // IMPORTANT: Derby stores table names in UPPER CASE
        DatabaseMetaData    metaData    = conn.getMetaData();
        ResultSet           resultSet   =
            metaData.getTables( null, null, "STUDENT", null );
        
        boolean rval = resultSet.next();
        resultSet.close();
        
        return rval;
    }
    
    public void createTable()
        throws SQLException
    {
        Statement   statement   = conn.createStatement();
        statement.executeUpdate( CREATE_TABLE_SQL );
        statement.close();
    }
    
    public void dropTable()
        throws SQLException
    {
        Statement   statement   = conn.createStatement();
        statement.executeUpdate( DROP_TABLE_SQL );
        statement.close();
    }
    
    public void insert()
        throws SQLException
    {
        int flags   = Statement.RETURN_GENERATED_KEYS;
        if ( insertStatement == null )
            insertStatement = conn.prepareStatement( INSERT_SQL, flags );
        
        int inx = 1;
        insertStatement.setString( inx++, name.first );
        insertStatement.setString( inx++, name.last );
        insertStatement.setDate( inx++, birthDate );
        insertStatement.setDate( inx++, matriculationDate );
        insertStatement.setString( inx++, address.address1 );
        insertStatement.setString( inx++, address.address2 );
        insertStatement.setString( inx++, address.city );
        insertStatement.setString( inx++, address.state );
        insertStatement.setString( inx++, address.zip_code );
        insertStatement.executeUpdate();
        
        ResultSet   keys    = insertStatement.getGeneratedKeys();
        keys.next();
        ident = keys.getInt( 1 );
        keys.close();
    }
    
    public void select( String sql )
        throws SQLException
    {
        if ( selectStatement == null )
            selectStatement = conn.createStatement();
        resultSet = selectStatement.executeQuery( sql );
    }
    
    public void selectAll()
        throws SQLException
    {
        int flags   = ResultSet.TYPE_SCROLL_SENSITIVE;
        if ( selectAllStatement == null )
            selectAllStatement = 
                conn.prepareStatement( SELECT_ALL_SQL, flags );
        resultSet = selectAllStatement.executeQuery();
    }
    
    public void selectOne( String firstName, String lastName )
        throws SQLException
    {
        if ( selectOneStatement == null )
            selectOneStatement = conn.createStatement();
        // Build a string that looks something like:
        //    SELECT * FROM student WHERE first_name = 'john'
        //        AND last_name = 'doe'
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( "SELECT * FROM student WHERE ");
        bldr.append( "first_name = '" ).append(firstName ).append( "' AND " );
        bldr.append( "last_name = '" ).append( lastName ).append( "'" );
        
        selectOneStatement.executeQuery( bldr.toString() );
        resultSet = selectOneStatement.getResultSet();
    }
    
    public boolean next()
        throws SQLException
    {
        boolean rcode   = false;
        if ( resultSet != null && (rcode = resultSet.next()) )
        {
            ident = resultSet.getInt( "student_id" );
            name.first = resultSet.getString( "first_name" );
            name.last = resultSet.getString( "last_name" );
            birthDate = resultSet.getDate( "birth_date" );
            matriculationDate = resultSet.getDate( "matriculation_date" );
            address.address1 = resultSet.getString( "address1" );
            address.address2 = resultSet.getString( "address2" );
            address.city = resultSet.getString( "city" );
            address.state = resultSet.getString( "state" );
            address.zip_code = resultSet.getString( "zip_code" );
        }
        
        return rcode;
    }
    
    public boolean first()
        throws SQLException
    {
        boolean rcode   = false;
        if ( resultSet != null )
        {
            resultSet.beforeFirst();
            rcode = next();
        }
        
        return rcode;
    }
    
    public void close()
        throws SQLException
    {
        close( insertStatement );
        close( selectAllStatement );
        close( selectOneStatement );
        close( selectStatement );
    }
    
    private void close( Statement statement )
        throws SQLException
    {
        if ( statement != null )
            statement.close();
    }
    
    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        
        bldr.append( ident ).append( ", " );
        bldr.append( name.last ).append( ", " );
        bldr.append( name.first ).append( ", " );
        bldr.append( birthDate.toString() ).append( ", " );
        bldr.append( matriculationDate.toString() ).append( ", " );
        bldr.append( address.address1 ).append( ", " );
        bldr.append( address.address2 ).append( ", " );
        bldr.append( address.city ).append( ", " );
        bldr.append( address.state ).append( ", " );
        bldr.append( address.zip_code );
        
        return bldr.toString();
    }
}