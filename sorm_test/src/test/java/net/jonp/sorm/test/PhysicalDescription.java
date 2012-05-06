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
}
