package net.jonp.sorm;

import java.sql.SQLException;
import java.util.Collection;


/**
 * Base class for all Sorm ORM objects.
 * 
 * @param <K> The key type of object stored and retrieved by this ORM object.
 * @param <T> The type of the object stored and retrieved by this ORM object.
 */
public abstract class SormBase<K, T>
{
    private final SormSession _session;

    public SormBase(final SormSession session)
    {
        _session = session;
    }

    public SormSession getSession()
    {
        return _session;
    }

    public void create(final T... objs)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: create");
    }

    public void create(final Collection<T> objs)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: create");
    }

    public void create(final T obj)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: create");
    }

    public Collection<T> read(final K... keys)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: read");
    }

    public Collection<T> read(final Collection<K> keys)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: read");
    }

    public T read(final K key)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: read");
    }

    public SormIterable<T> matches(final Collection<K> keys)
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: matches");
    }

    public void update(final T... objs)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: update");
    }

    public void update(final Collection<T> objs)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: update");
    }

    public void update(final T obj)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: update");
    }

    public void delete(final T... objs)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: delete");
    }

    public void delete(final Collection<T> objs)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: delete");
    }

    public void delete(final T obj)
        throws SQLException
    {
        throw new UnsupportedOperationException("Unimplemented Sorm method: delete");
    }
}
