package app.playground2;

import java.util.ArrayList;
import java.util.List;

import util.Address;
import util.Name;

public class Customer
{
    private Integer                 ident;
    private Name                    name;
    private Address                 address;
    private final List<CreditCard>  creditCards;
    
    public Customer()
    {
        this( null, null, null );
    }
    
    public Customer( Name name, Address address )
    {
        this( name, address, null );
    }
    
    public Customer( Name name, Address address, Integer ident )
    {
        this.ident = ident;
        this.name = name;
        this.address = address;
        this.creditCards = new ArrayList<>();
    }

    public Integer getIdent()
    {
        return ident;
    }

    public void setIdent(Integer ident)
    {
        this.ident = ident;
    }

    public Name getName()
    {
        return name;
    }

    public void setName(Name name)
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

    public List<CreditCard> getCreditCards()
    {
        return creditCards;
    }
    
    public void addCreditCard( CreditCard creditCard )
    {
        creditCards.add( creditCard );
    }
    
    @Override
    public String toString()
    {
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( name ).append( "," ).append( address );
        bldr.append( " {");
        for ( CreditCard card : creditCards )
            bldr.append( " " ).append( card.toString() );
        bldr.append( " }" );
        
        return bldr.toString();
    }
}
