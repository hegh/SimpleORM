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
    private final Connection _connection;
    private final String _dialect;

    // Used for Immediate CacheMode; map of object class onto a map of
    // identifier onto object
    private final Map<Class<? extends SormObject>, Map<Object, WeakReference<Object>>> _weakCache =
        new HashMap<Class<? extends SormObject>, Map<Object, WeakReference<Object>>>();

    // TODO: Two strong caches with definite ordering will be needed for Delayed
    // CacheMode (one for in-use objects, and one for deleted objects)

    private final CacheMode _cacheMode;

    private boolean _closed = false;

    protected SormSession(final Connection connection, final String dialect, final CacheMode cacheMode)
    {
        _connection = connection;
        _dialect = dialect;
        _cacheMode = cacheMode;

        // TODO: CacheMode.Delayed is not yet supported
        if (CacheMode.Delayed == _cacheMode) {
            throw new UnsupportedOperationException("Delayed CacheMode is not yet implemented.");
        }
    }

    public Connection getConnection()
    {
        return _connection;
    }

    public String getDialect()
    {
        return _dialect;
    }

    public CacheMode getCacheMode()
    {
        return _cacheMode;
    }

    public boolean isClosed()
    {
        return _closed;
    }

    public void close()
        throws SQLException
    {
        if (!isClosed()) {
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

        final WeakReference<Object> ref = typemap.get(type);
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
