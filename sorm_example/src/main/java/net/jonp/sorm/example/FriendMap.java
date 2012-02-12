package net.jonp.sorm.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import net.jonp.sorm.Dialect;
import net.jonp.sorm.SormContext;
import net.jonp.sorm.SormSession;

import org.apache.log4j.Logger;

/**
 * Shows how to use Sorm with a basic social network explorer.
 */
public class FriendMap
{
    private static final Logger LOG = Logger.getLogger(FriendMap.class);

    private static final String DBFILE = "friendmap.db";

    public static void main(final String[] args)
    {
        final boolean needsInit = !new File(DBFILE).isFile();

        final Dialect dialect = new Dialect("sqlite", "org.sqlite.JDBC", "jdbc:sqlite:");
        final SormContext ctx;
        try {
            ctx = new SormContext(dialect, DBFILE, null, null);
        }
        catch (final ClassNotFoundException cnfe) {
            LOG.error("Failed to load database driver class", cnfe);
            System.err.println("Failed to load database driver class: " + cnfe.getMessage());
            System.exit(1);
            return;
        }

        if (needsInit) {
            try {
                initializeDatabase(ctx);
            }
            catch (final IOException ioe) {
                LOG.error("Failed to initialize database", ioe);
                System.err.println("Failed to initialize database: " + ioe.getMessage());
                System.exit(1);
                return;
            }
            catch (final SQLException sqle) {
                LOG.error("Failed to initialize database", sqle);
                System.err.println("Failed to initialize database: " + sqle.getMessage());
                System.exit(1);
                return;
            }
        }

        // TODO: Start a GUI that provides basic access and manipulation of
        // Person objects
    }

    private static void initializeDatabase(final SormContext ctx)
        throws SQLException, IOException
    {
        final BufferedReader in =
            new BufferedReader(new InputStreamReader(FriendMap.class.getClassLoader().getResourceAsStream("dbinit.sqlite.sql")));
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
                LOG.debug("Failed to close stream", ioe);
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
