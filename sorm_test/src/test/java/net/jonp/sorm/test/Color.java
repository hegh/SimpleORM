package net.jonp.sorm.test;

/**
 * Legal colors for hair and eyes.
 */
public enum Color
{
    Black("Bk"),
    Blue("Bu"),
    Brown("Br"),
    Green("Gn"),
    Gray("Gy"),
    Violet("Vi"),
    Red("Rd"),
    White("Wt"),
    Yellow("Yw"),
    //
    ;

    private final String _abbreviation;

    private Color(final String abbreviation)
    {
        _abbreviation = abbreviation;
    }

    public static Color findByAbbreviation(final String abbreviation)
    {
        if (null == abbreviation) {
            return null;
        }

        for (final Color color : values()) {
            if (abbreviation.equals(color.getAbbreviation())) {
                return color;
            }
        }

        throw new IllegalArgumentException("Unrecognized abbreviation: " + abbreviation);
    }

    public String getAbbreviation()
    {
        return _abbreviation;
    }
}
