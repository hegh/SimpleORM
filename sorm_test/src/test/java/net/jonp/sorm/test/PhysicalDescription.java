package net.jonp.sorm.test;

/**
 * Represents the physical description of a person.
 */
public class PhysicalDescription
{
    private int _height;
    private double _weight;
    private Color _hairColor;
    private Color _eyeColor;
    private Color _hairColorAlt;

    public PhysicalDescription()
    {
        // Nothing to do
    }

    public PhysicalDescription(final int height, final double weight, final Color hairColor, final Color eyeColor,
                               final Color hairColorAlt)
    {
        _height = height;
        _weight = weight;
        _hairColor = hairColor;
        _eyeColor = eyeColor;
        _hairColorAlt = hairColorAlt;
    }

    /** Get height in cm. */
    public int getHeight()
    {
        return _height;
    }

    /** Set height in cm. */
    public void setHeight(final int height)
    {
        _height = height;
    }

    /** Get weight in kg. */
    public double getWeight()
    {
        return _weight;
    }

    /** Set weight in kg. */
    public void setWeight(final double weight)
    {
        _weight = weight;
    }

    /** Get base hair color. */
    public Color getHairColor()
    {
        return _hairColor;
    }

    /** Set base hair color. */
    public void setHairColor(final Color hairColor)
    {
        _hairColor = hairColor;
    }

    /** Get eye color. */
    public Color getEyeColor()
    {
        return _eyeColor;
    }

    /** Set eye color. */
    public void setEyeColor(final Color eyeColor)
    {
        _eyeColor = eyeColor;
    }

    /** Get dyed hair color. */
    public Color getHairColorAlt()
    {
        return _hairColorAlt;
    }

    /** Set dyed hair color. */
    public void setHairColorAlt(final Color hairColorAlt)
    {
        _hairColorAlt = hairColorAlt;
    }

    @Override
    public String toString()
    {
        return String.format("H: %d, W: %.02f, HC: %s, EC: %s, HD: %s", getHeight(), getWeight(), getHairColor(), getEyeColor(),
                             getHairColorAlt());
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object rhs)
    {
        if (null == rhs) {
            return false;
        }
        else if (rhs instanceof PhysicalDescription) {
            final PhysicalDescription pd = (PhysicalDescription)rhs;
            return (equal(getHeight(), pd.getHeight()) && equal(getWeight(), pd.getWeight(), .001) &&
                    equal(getHairColor(), pd.getHairColor()) && equal(getEyeColor(), pd.getEyeColor()) && equal(getHairColorAlt(),
                                                                                                                pd.getHairColorAlt()));
        }
        else {
            return false;
        }
    }

    private static boolean equal(final Object a, final Object b)
    {
        if (a == b) {
            return true;
        }
        else if (null == a || null == b) {
            return false;
        }
        else {
            return a.equals(b);
        }
    }

    private static boolean equal(final double a, final double b, final double delta)
    {
        return (Math.abs(a - b) <= delta);
    }
}
