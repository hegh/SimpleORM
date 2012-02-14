package net.jonp.sorm.test;


import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.jonp.sorm.Dialect;
import net.jonp.sorm.SormContext;
import net.jonp.sorm.SormSession;

import org.apache.log4j.Logger;


/**
 * Provides a database initialization library to the JUnit test cases.
 */
public class DBInit
{
    private static final Logger LOG = Logger.getLogger(DBInit.class);

    private static final Map<Dialect, String> FILEMAP;
    private static final Map<Dialect, String> SCRIPTMAP;

    static {
        final Map<Dialect, String> filemap = new HashMap<Dialect, String>();
        filemap.put(Dialect.H2, "h2_test");
        filemap.put(Dialect.SQLite, "sqlite_test.db");
        FILEMAP = Collections.unmodifiableMap(filemap);

        final Map<Dialect, String> scriptmap = new HashMap<Dialect, String>();
        scriptmap.put(Dialect.H2, "dbinit.h2.sql");
        scriptmap.put(Dialect.SQLite, "dbinit.sqlite.sql");
        SCRIPTMAP = Collections.unmodifiableMap(scriptmap);
    }

    public static void delete(final Dialect dialect)
    {
        if (Dialect.H2 == dialect) {
            final File dbfile = new File(FILEMAP.get(dialect) + ".h2.db");
            final File tracefile = new File(FILEMAP.get(dialect) + ".trace.db");

            if (dbfile.isFile()) {
                if (!dbfile.delete()) {
                    fail("Failed to delete " + dbfile);
                }
            }

            if (tracefile.isFile()) {
                if (!tracefile.delete()) {
                    fail("Failed to delete " + tracefile);
                }
            }
        }
        else if (Dialect.SQLite == dialect) {
            final File dbfile = new File(FILEMAP.get(dialect));
            if (dbfile.isFile()) {
                if (!dbfile.delete()) {
                    fail("Failed to delete " + dbfile);
                }
            }
        }
        else {
            throw new IllegalArgumentException("Cannot delete a database of dialect " + dialect);
        }
    }

    /**
     * Create a new database (deleting an existing database if one exists), and
     * initialize it. Calls {@link org.junit.Assert#fail(String)} if there is an
     * error.
     * 
     * @param dialect The dialect of the database to initialize.
     * @return A {@link SormContext} attached to the database.
     */
    public static SormContext dbinit(final Dialect dialect)
    {
        delete(dialect);

        final File dbfile = new File(FILEMAP.get(dialect));
        if (dbfile.exists()) {
            fail("Required object " + dbfile + " exists but is not a file; refusing to continue.");
        }

        final SormContext ctx;
        try {
            ctx = new SormContext(dialect, FILEMAP.get(dialect), null, null);
        }
        catch (final ClassNotFoundException cnfe) {
            LOG.error("Failed to load database driver class", cnfe);
            fail("Failed to load database driver class: " + cnfe.getMessage());
            return null;
        }

        try {
            initializeDatabase(ctx);
        }
        catch (final IOException ioe) {
            LOG.error("Failed to initialize database", ioe);
            fail("Failed to initialize database: " + ioe.getMessage());
        }
        catch (final SQLException sqle) {
            LOG.error("Failed to initialize database", sqle);
            fail("Failed to initialize database: " + sqle.getMessage());
        }

        return ctx;
    }

    private static void initializeDatabase(final SormContext ctx)
        throws SQLException, IOException
    {
        final BufferedReader in =
            new BufferedReader(new InputStreamReader(DBInit.class.getClassLoader().getResourceAsStream(
                                                                                                       SCRIPTMAP.get(ctx
                                                                                                           .getDialect()))));
        try {
            final SormSession session = ctx.getTransientSession();
            try {
                final Connection conn = session.getConnection();
                final Statement stmt = conn.createStatement();

                try {
                    String cmd;
                    while ((cmd = readCommand(in)) != null) {
                        LOG.debug("Executing initialization command:\n" + cmd);
                        stmt.execute(cmd);
                    }
                }
                finally {
                    stmt.close();
                }
            }
            finally {
                session.close();
            }
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ioe) {
                LOG.warn("Failed to close stream", ioe);
            }
        }
    }

    private static String readCommand(final BufferedReader in)
        throws IOException
    {
        final StringBuilder sb = new StringBuilder();

        boolean last = false;
        String line;
        while (!last && (line = in.readLine()) != null) {
            if (line.endsWith(";")) {
                line = line.substring(0, line.length() - 1);
                last = true;
            }

            if (!line.trim().isEmpty()) {
                sb.append(" ").append(line);
            }
        }

        if (sb.length() > 0) {
            return sb.toString();
        }
        else {
            return null;
        }
    }
}
