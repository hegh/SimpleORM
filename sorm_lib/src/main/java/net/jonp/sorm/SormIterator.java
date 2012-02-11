package net.jonp.sorm;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * A wrapper around {@link Iterator} to provide a {@link #close()} method.
 */
public interface SormIterator<E>
    extends Iterator<E>
{
    public void close()
        throws SQLException;
}
