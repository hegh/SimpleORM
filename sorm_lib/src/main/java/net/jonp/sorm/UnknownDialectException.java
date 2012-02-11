package net.jonp.sorm;

/**
 * Thrown when a dialect is passed to a Sorm function, but no mapping is
 * available for it.
 */
public class UnknownDialectException
    extends RuntimeException
{
    private final String _dialect;

    public UnknownDialectException(final String dialect)
    {
        super("Unknown dialect: " + dialect);

        _dialect = dialect;
    }

    public UnknownDialectException(final String dialect, final Throwable cause)
    {
        super("Unknown dialect: " + dialect, cause);

        _dialect = dialect;
    }

    public String getDialect()
    {
        return _dialect;
    }
}
