package net.jonp.sorm;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Wraps a database connection and useful metadata used by Sorm.
 */
public class SormSession
{
    private final SormContext _context;
    private final Connection _connection;

    // Used for Immediate CacheMode; map of object class onto a map of
    // identifier onto object
    private final Map<Class<? extends SormObject>, Map<Object, WeakReference<Object>>> _weakCache =
        new HashMap<Class<? extends SormObject>, Map<Object, WeakReference<Object>>>();

    // TODO: Two strong caches with definite ordering will be needed for Delayed
    // CacheMode (one for in-use objects, and one for deleted objects)

    private final CacheMode _cacheMode;

    private boolean _closed = false;

    protected SormSession(final SormContext context, final Connection connection, final CacheMode cacheMode)
    {
        _context = context;
        _connection = connection;
        _cacheMode = cacheMode;

        // TODO: CacheMode.Delayed is not yet supported
        if (CacheMode.Delayed == _cacheMode) {
            throw new UnsupportedOperationException("Delayed CacheMode is not yet implemented.");
        }
    }

    /** Get the {@link SormContext} that spawned this {@link SormSession}. */
    public SormContext getContext()
    {
        return _context;
    }

    /** Get the {@link Connection} wrapped by this {@link SormSession}. */
    public Connection getConnection()
    {
        return _connection;
    }

    /**
     * Get the name of the dialect of this {@link SormSession}. To get the
     * actual {@link Dialect}, use {@link #getContext()} and
     * {@link SormContext#getDialect()}.
     */
    public String getDialect()
    {
        return getContext().getDialect().getName();
    }

    /** Get the {@link CacheMode} of this {@link SormSession}. */
    public CacheMode getCacheMode()
    {
        return _cacheMode;
    }

    /** Test whether this {@link SormSession} is closed. */
    public boolean isClosed()
    {
        return _closed;
    }

    /**
     * Close this {@link SormSession}. If it is already closed, does nothing. If
     * it is a per-thread session in the {@link SormContext}, it is disposed.
     */
    public void close()
        throws SQLException
    {
        if (!isClosed()) {
            getContext().killSession(this);
            getConnection().close();
            _weakCache.clear();
            _closed = true;
        }
    }

    /**
     * Add an object to the cache.
     * 
     * @param type The type of object (so there is no worry over colliding
     *            keys).
     * @param key The key for the object.
     * @param value The object.
     * @throws IllegalStateException If the {@link SormSession} is closed.
     */
    public void cacheAdd(final Class<? extends SormObject> type, final Object key, final Object value)
    {
        if (isClosed()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is closed.");
        }

        if (CacheMode.None == getCacheMode()) {
            // No caching in this mode
            return;
        }

        if (null == value) {
            // Nothing to do, don't bother evicting since it probably wasn't
            // here in the first place
            return;
        }

        Map<Object, WeakReference<Object>> typemap;
        synchronized (_weakCache) {
            typemap = _weakCache.get(type);

            if (null == typemap) {
                typemap = new WeakHashMap<Object, WeakReference<Object>>();
                _weakCache.put(type, typemap);
            }
        }

        synchronized (typemap) {
            typemap.put(key, new WeakReference<Object>(value));
        }
    }

    /**
     * Get a cached object.
     * 
     * @param type The type of object.
     * @param key The key of the object.
     * @return The cached object, or <code>null</code> if the object was not in
     *         the cache.
     * @throws IllegalStateException If the {@link SormSession} is closed.
     */
    public <T> T cacheGet(final Class<T> type, final Object key)
    {
        if (isClosed()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is closed.");
        }

        if (CacheMode.None == getCacheMode()) {
            // No caching in this mode
            return null;
        }

        final Map<Object, WeakReference<Object>> typemap;
        synchronized (_weakCache) {
            typemap = _weakCache.get(type);
        }

        if (null == typemap) {
            return null;
        }

        final WeakReference<Object> ref = typemap.get(key);
        if (null == ref) {
            return null;
        }

        return cast(type, ref.get());
    }

    /**
     * Remove an item from the cache.
     * 
     * @param type The type of item to remove.
     * @param key The key of the item to remove.
     * @throws IllegalStateException If the {@link SormSession} is closed.
     */
    public void cacheDel(final Class<? extends SormObject> type, final Object key)
    {
        if (isClosed()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is closed.");
        }

        if (CacheMode.None == getCacheMode()) {
            // No caching in this mode
            return;
        }

        final Map<Object, WeakReference<Object>> typemap;
        synchronized (_weakCache) {
            typemap = _weakCache.get(type);
        }

        if (null != typemap) {
            typemap.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(final Class<T> type, final Object o)
    {
        return (T)o;
    }
}
