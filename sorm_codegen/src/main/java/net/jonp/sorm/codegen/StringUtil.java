package net.jonp.sorm.codegen;

import java.util.LinkedList;
import java.util.List;

/**
 * Static library of useful string functions.
 */
public class StringUtil
{
    private StringUtil()
    {
        // Nothing to do
    }

    /**
     * Update the given string so its initial character is capitalized.
     * 
     * @param s The string to update.
     * @return The updated string, unless it was <code>null</code> or empty, in
     *         which case the original string is returned.
     */
    public static String capFirst(final String s)
    {
        if (null == s || s.isEmpty()) {
            return s;
        }
        else {
            return (s.substring(0, 1).toUpperCase() + s.substring(1));
        }
    }

    /**
     * Expand a string by duplicating and concatenating it multiple times.
     * 
     * @param s The string.
     * @param count The number of times it should appear in the output.
     * @return The multiplied string.
     */
    public static String multiplyString(final String s, int count)
    {
        final StringBuilder buf = new StringBuilder();

        while (count > 0) {
            buf.append(s);
            count--;
        }

        return buf.toString();
    }

    /**
     * Tokenize a string. Ignores separator strings if they occur within quotes
     * (double, single, and backtick).
     * 
     * @param s The string to tokenize.
     * @param sep The separator string.
     * @param keepEmptyTokens True to keep empty tokens, false to skip them.
     *            Even if true, will not include an empty token at the end if
     *            the string being tokenized ends with a separator.
     * @return An array of tokens from the string, minus the separator tokens.
     */
    public static String[] tokenize(final String s, final String sep, final boolean keepEmptyTokens)
    {
        final char[] chars = s.toCharArray();
        final char[] sepchars = sep.toCharArray();

        int sepmatch = 0;
        char quoteChar = 0;

        final List<String> tokens = new LinkedList<String>();
        final StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            if (quoteChar != 0) {
                // Inside of a quote
                if (chars[i] == quoteChar) {
                    quoteChar = 0;
                }

                currentToken.append(chars[i]);
            }
            else {
                if (chars[i] == '"' || chars[i] == '`' || chars[i] == '\'') {
                    quoteChar = chars[i];
                    currentToken.append(chars[i]);
                }
                else {
                    if (sepchars[sepmatch] == chars[i]) {
                        sepmatch++;
                        if (sepmatch == sepchars.length) {
                            // Found a separator
                            if (currentToken.length() > 0 || keepEmptyTokens) {
                                tokens.add(currentToken.toString());
                            }

                            currentToken.setLength(0);
                            sepmatch = 0;
                        }
                    }
                    else {
                        for (int j = 0; j < sepmatch; j++) {
                            currentToken.append(sepchars[j]);
                        }

                        sepmatch = 0;
                        currentToken.append(chars[i]);
                    }
                }
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(new String[tokens.size()]);
    }
}
