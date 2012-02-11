package net.jonp.sorm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Provides {@link SormSession}s.
 */
public class SormContext
{
    private static final Logger LOG = Logger.getLogger(SormContext.class);

    private final Dialect _dialect;
    private final String _server;
    private final String _user;
    private final String _passwd;

    private boolean _closed = false;

    private final Map<Thread, SormSession> _sessions = new HashMap<Thread, SormSession>();
    private final ThreadLocal<SormSession> _session = new ThreadLocal<SormSession>();

    /**
     * Construct a new {@link SormContext}.
     * 
     * @param dialect The dialect to use.
     * @param server The name of the server to connect to. If the server
     *            requires a non-standard port number, or a more complex URL,
     *            make sure to include it all in this (for example,
     *            <code>my.server.net:1234/path/on/server</code>).
     * @param user The user name, or <code>null</code> to not pass a user
     *            name/password.
     * @param passwd The password, or <code>null</code> to not pass a user
     *            name/password.
     * @throws ClassNotFoundException If unable to load the driver for the
     *             specified dialect.
     */
    public SormContext(final Dialect dialect, final String server, final String user, final String passwd)
        throws ClassNotFoundException
    {
        _dialect = dialect;
        _server = server;
        _user = user;
        _passwd = passwd;

        Class.forName(_dialect.getDriver());
    }

    /**
     * Get the dialect of this context.
     * 
     * @return The dialect.
     */
    public Dialect getDialect()
    {
        return _dialect;
    }

    /**
     * Wrapper around {@link #getSession(CacheMode)} with
     * {@link CacheMode#Immediate}.
     */
    public SormSession getSession()
        throws SQLException
    {
        return getSession(CacheMode.Immediate);
    }

    /**
     * Get the session for this thread, constructing one if necessary. If your
     * thread will not live long, use {@link #getTransientSession()} and make
     * sure to close the session when you are finished.
     * 
     * @param cacheMode The cache mode for the session. If this thread already
     *            has a session, but the cache mode does not match, that session
     *            will be closed and a new session opened.
     * @return The session for this thread.
     * @throws SQLException If there was a problem creating a new connection.
     */
    public SormSession getSession(final CacheMode cacheMode)
        throws SQLException
    {
        if (_closed) {
            throw new IllegalStateException(getClass().getSimpleName() + " is closed");
        }

        SormSession session = _session.get();
        if (null != session && session.getCacheMode() != cacheMode) {
            session.close();
            session = null;
        }

        if (null == session) {
            LOG.info("Building a new session for a new thread");
            session = makeSession(cacheMode);

            _session.set(session);
            synchronized (_sessions) {
                _sessions.put(Thread.currentThread(), session);
            }
        }

        return session;
    }

    /**
     * Call this when your thread is dying or you want the next call from this
     * thread to {@link #getSession(CacheMode)} to construct a new session.
     */
    public void dispose()
    {
        final SormSession session = _session.get();

        if (null != session) {
            LOG.info("Disposing of session due to thread request");
            _session.remove();

            synchronized (_sessions) {
                _sessions.remove(Thread.currentThread());
            }

            try {
                session.close();
            }
            catch (final SQLException sqle) {
                LOG.debug("Error closing session", sqle);
            }
        }
    }

    /**
     * Wrapper around {@link #getTransientSession(CacheMode)} with
     * {@link CacheMode#Immediate}.
     */
    public SormSession getTransientSession()
        throws SQLException
    {
        return getTransientSession(CacheMode.Immediate);
    }

    /**
     * Construct a new session intended for a brief usage.
     * 
     * @param cacheMode The cache mode to use.
     * @return A new session. Make sure to close this when you are finished with
     *         it.
     * @throws SQLException If there was a problem creating a new connection.
     */
    public SormSession getTransientSession(final CacheMode cacheMode)
        throws SQLException
    {
        if (_closed) {
            throw new IllegalStateException(getClass().getSimpleName() + " is closed");
        }

        final SormSession session = makeSession(cacheMode);
        return session;
    }

    /**
     * Closes all pooled database connections. Threaded connections are not
     * touched, as we cannot access them.
     */
    public void close()
    {
        synchronized (this) {
            if (!_closed) {
                _closed = true;
                synchronized (_sessions) {
                    LOG
                        .info("Closing " + _sessions.size() + " per-thread sessions due to " + getClass().getSimpleName() +
                              " close");
                    final Iterator<SormSession> itSessions = _sessions.values().iterator();
                    while (itSessions.hasNext()) {
                        final SormSession session = itSessions.next();
                        itSessions.remove();

                        try {
                            session.close();
                        }
                        catch (final SQLException sqle) {
                            // Ignore it
                        }
                    }
                }
            }
        }
    }

    /**
     * Construct a new connection to the database and return a
     * {@link SormSession} wrapped around it.
     * 
     * @param cacheMode The cache mode.
     * @return A new {@link SormSession}.
     * @throws SQLException If there was a problem connecting to the database.
     */
    private SormSession makeSession(final CacheMode cacheMode)
        throws SQLException
    {
        final Connection connection = DriverManager.getConnection(getDialect().getProtocol() + _server, _user, _passwd);
        final SormSession session = new SormSession(connection, getDialect().getName(), cacheMode);
        return session;
    }
}
