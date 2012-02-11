package net.jonp.sorm.codegen.app;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;

import net.jonp.sorm.codegen.CodeGenerator;
import net.jonp.sorm.codegen.model.Sorm;
import net.jonp.sorm.codegen.parser.BadInputException;
import net.jonp.sorm.codegen.parser.XMLParser;

import org.apache.log4j.Logger;

/**
 * A command-line entry-point for {@link XMLParser}.
 */
public class CodeGenProgram
{
    private static final Logger log = Logger.getLogger(CodeGenProgram.class);

    private static final String PROGNAME = "CodeGen";

    private static Reader in;
    private static PrintWriter out;

    public static void main(final String[] args)
    {
        final XMLParser parser = new XMLParser();

        try {
            if (!parseArgs(args)) {
                System.exit(0);
                return;
            }
        }
        catch (final IllegalParameterException ipe) {
            System.err.println("Bad parameter: " + ipe.getParameter());
            System.err.println(ipe.getMessage());
            log.error("Bad parameter", ipe);
            usage(System.err, PROGNAME);
            System.exit(1);
            return;
        }

        final Sorm sorm;
        try {
            sorm = parser.parseSorm(in);
        }
        catch (final BadInputException bie) {
            System.err.println("Bad input file: " + bie.getMessage());
            log.error("Bad input", bie);
            System.exit(1);
            return;
        }
        catch (final IOException ioe) {
            System.err.println("I/O error: " + ioe.getMessage());
            log.error("I/O error", ioe);
            System.exit(1);
            return;
        }

        final CodeGenerator codegen = new CodeGenerator();
        codegen.setSorm(sorm);
        codegen.setOut(out);

        try {
            codegen.run();
        }
        finally {
            out.close();
        }
    }

    private static void usage(final PrintStream out, final String exe)
    {
        out.printf("Usage: %s [OPTION]...%n", exe);
        out.printf("OPTION may be:%n");
        out.printf("  -i <file>  Read the XML configuration from <file> instead of stdin%n");
        out.printf("  -o <file>  Write the generated Java source to <file> instead of stdout%n");
        out.printf("  -h%n");
        out.printf("  -?%n");
        out.printf("  --help     Display usage information and exit%n");
    }

    private static boolean parseArgs(final String... args)
        throws IllegalParameterException
    {
        int i = 0;

        try {
            for (i = 0; i < args.length; i++) {
                if ("-i".equals(args[i])) {
                    try {
                        in = new FileReader(args[++i]);
                    }
                    catch (final IOException ioe) {
                        throw new IllegalParameterException("Unable to open input file " + args[i] + ": " + ioe.getMessage(),
                                                            args[i - 1], ioe);
                    }
                }
                else if ("-o".equals(args[i])) {
                    try {
                        out = new PrintWriter(new FileWriter(args[++i]));
                    }
                    catch (final IOException ioe) {
                        throw new IllegalParameterException("Unable to open output file " + args[i] + ": " + ioe.getMessage(),
                                                            args[i - 1]);
                    }
                }
                else if ("-h".equals(args[i]) || "--help".equals(args[i]) || "-?".equals(args[i])) {
                    usage(System.out, PROGNAME);
                    return false;
                }
                else {
                    throw new IllegalParameterException(args[i]);
                }
            }
        }
        catch (final ArrayIndexOutOfBoundsException aioobe) {
            throw new IllegalParameterException("Parameter " + args[i - 1] + " requires an argument", args[i - 1], aioobe);
        }

        return true;
    }
}
