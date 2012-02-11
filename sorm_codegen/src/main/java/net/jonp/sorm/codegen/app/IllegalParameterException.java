package net.jonp.sorm.codegen.app;


/**
 * Thrown when an illegal parameter is passed to {@link CodeGenProgram}.
 */
public class IllegalParameterException
    extends Exception
{
    private final String param;

    /**
     * Construct a new IllegalParameterException.
     * 
     * @param _param The illegal parameter.
     */
    public IllegalParameterException(final String _param)
    {
        super("Bad parameter: " + _param);

        param = _param;
    }

    /**
     * Construct a new IllegalParameterException.
     * 
     * @param _param The illegal parameter.
     * @param _cause The reason the problem was discovered.
     */
    public IllegalParameterException(final String _param, final Throwable _cause)
    {
        super("Bad parameter: " + _param, _cause);

        param = _param;
    }

    /**
     * Construct a new IllegalParameterException.
     * 
     * @param _msg The message.
     * @param _param The illegal parameter.
     */
    public IllegalParameterException(final String _msg, final String _param)
    {
        super(_msg);

        param = _param;
    }

    /**
     * Construct a new IllegalParameterException.
     * 
     * @param _msg The message.
     * @param _param The illegal parameter.
     * @param _cause The reason the problem was discovered.
     */
    public IllegalParameterException(final String _msg, final String _param, final Throwable _cause)
    {
        super(_msg, _cause);

        param = _param;
    }

    /**
     * Get the illegal parameter.
     * 
     * @return The illegal parameter.
     */
    public String getParameter()
    {
        return param;
    }
}
