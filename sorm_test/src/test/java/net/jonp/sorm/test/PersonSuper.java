package net.jonp.sorm.test;

import java.util.Date;

/**
 * Superclass for Person, providing for basic comparisons.
 */
public abstract class PersonSuper
{
    public abstract String getName();

    public abstract Date getDob();

    public abstract String getGender();

    @Override
    public boolean equals(final Object o)
    {
        if (null == o) {
            return false;
        }
        else if (o instanceof PersonSuper) {
            final PersonSuper p = (PersonSuper)o;
            return (equal(getName(), p.getName()) && equal(getDob(), p.getDob()) && equal(getGender(), p.getGender()));
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        if (null == getName()) {
            return 0;
        }
        else {
            return getName().hashCode();
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
}
