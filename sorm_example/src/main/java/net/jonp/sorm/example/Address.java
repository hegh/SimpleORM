package net.jonp.sorm.example;

/**
 * Represents an address.
 */
public class Address
{
    private final String number;
    private final String street;
    private final String apartment;
    private final String city;
    private final String state;
    private final String zipcode;

    public Address(final String number, final String street, final String apartment, final String city, final String state,
                   final String zipcode)
    {
        this.number = number;
        this.street = street;
        this.apartment = apartment;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public String getNumber()
    {
        return number;
    }

    public String getStreet()
    {
        return street;
    }

    public String getApartment()
    {
        return apartment;
    }

    public String getCity()
    {
        return city;
    }

    public String getState()
    {
        return state;
    }

    public String getZipcode()
    {
        return zipcode;
    }
}
