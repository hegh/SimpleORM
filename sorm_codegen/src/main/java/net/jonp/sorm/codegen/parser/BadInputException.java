package net.jonp.sorm.codegen.parser;

/**
 * Thrown when a SimpleORM configuration file has errors.
 */
public class BadInputException
    extends Exception
{
    /**
     * Construct a new BadInputException.
     * 
     * @param message The message.
     */
    public BadInputException(final String message)
    {
        super(message);
    }

    /**
     * Construct a new BadInputException.
     * 
     * @param message The message.
     * @param cause The cause of the exception.
     */
    public BadInputException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
