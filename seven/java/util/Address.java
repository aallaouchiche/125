package util;

public class Address
{
    public String   address1;
    public String   address2;
    public String   city;
    public String   state;
    public String   zip_code;
    
    public Address( )
    {
        this( "", "", "", "", "" );
    }
    
    public Address(
        String  address1,
        String  address2,
        String  city,
        String  state,
        String  zip_code
    )
    {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
    }
    
    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( address1 ).append( "," );
        bldr.append( address2 ).append( "," );
        bldr.append( city ).append( "," );
        bldr.append( state ).append( "," );
        bldr.append( zip_code );
        
        return bldr.toString();
    }
}