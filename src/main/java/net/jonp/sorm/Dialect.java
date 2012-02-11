package net.jonp.sorm;

/**
 * Describes a database dialect and provides a few default dialects.
 */
public class Dialect
{
    /** The dialect of a MySQL database. */
    public static final Dialect MySQL = new Dialect("mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://");

    /** The dialect of a PostgreSQL database. */
    public static final Dialect PostgreSQL = new Dialect("postgresql", "org.postgresql.Driver", "jdbc:postgresql://");

    /** The name of this dialect. */
    private final String name;

    /** The JDBC driver to use for databases that speak this dialect. */
    private final String driver;

    /**
     * The JDBC protocol prefix to use for URLs to databases that speak this
     * dialect.
     */
    private final String protocol;

    /**
     * Construct a new Dialect.
     * 
     * @param _name The name of the dialect.
     * @param _driver The driver class.
     * @param _protocol The protocol.
     */
    public Dialect(final String _name, final String _driver, final String _protocol)
    {
        name = _name;
        driver = _driver;
        protocol = _protocol;
    }

    /** Get the name of this dialect. */
    public String getName()
    {
        return name;
    }

    /** Get the JDBC driver to use for databases that speak this dialect. */
    public String getDriver()
    {
        return driver;
    }

    /**
     * Get the JDBC protocol prefix to use for URLs to databases that speak this
     * dialect.
     */
    public String getProtocol()
    {
        return protocol;
    }
}