package app.playground2;

import java.util.Date;

import util.Address;

public class CreditCard
{
    private Integer ident   = null;
    private String  name;
    private Address address;
    private String  number;
    private String  cvv;
    private Date    expiry;
    
    public CreditCard()
    {
    }
    
    public CreditCard( 
        String  name,
        Address address,
        String  number,
        String  cvv,
        Date    expiry
    )
    {
        this.name = name;
        this.address = address;
        this.number = number;
        this.cvv = cvv;
        this.expiry = expiry; 
    }

    public Integer getIdent()
    {
        return ident;
    }

    public void setIdent(Integer ident)
    {
        this.ident = ident;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getCvv()
    {
        return cvv;
    }

    public void setCvv(String cvv)
    {
        this.cvv = cvv;
    }

    public Date getExpiry()
    {
        return expiry;
    }

    public void setExpiry(Date expiry)
    {
        this.expiry = expiry;
    }
    
    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( ident ).append( "," );
        bldr.append( name ).append( "," );
        bldr.append( address ).append( "," );
        bldr.append( number ).append( "," );
        bldr.append( cvv ).append( "," );
        bldr.append( expiry );
        
        return bldr.toString();
    }
}
