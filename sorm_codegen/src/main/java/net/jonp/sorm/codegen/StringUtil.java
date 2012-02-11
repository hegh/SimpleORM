package net.jonp.sorm.codegen;

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
}
