package util;

public class Name
{
    public String   first;
    public String   last;
    public String   middle;
    
    public Name()
    {
        this( "", "" );
    }
    
    public Name( String first, String last )
    {
        this( first, last, "" );
    }
    
    public Name( String first, String last, String middle )
    {
        this.first = first;
        this.last = last;
        this.middle = middle;
    }
    
    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( last ).append( "," );
        bldr.append( first ).append( "," );
        bldr.append( middle );
        
        return bldr.toString();
    }
}