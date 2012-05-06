package net.jonp.sorm.test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Superclass for Person, providing for basic comparisons.
 */
public abstract class PersonSuper
{
    private boolean _linked = false;

    boolean isLinked()
    {
        return _linked;
    }

    void setLinked(final boolean linked)
    {
        _linked = linked;
    }

    public abstract String getName();

    public abstract Date getDob();

    public abstract String getGender();

    public abstract Collection<Person> getFriends();

    public abstract void setFriends(Collection<Person> friends);

    public abstract PhysicalDescription getDescription();

    void addFriend(final Person friend)
    {
        Collection<Person> friends = getFriends();
        if (null == friends) {
            friends = new HashSet<Person>();
            setFriends(friends);
        }

        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    void removeFriend(final Person friend)
    {
        final Collection<Person> friends = getFriends();
        if (null != friends) {
            friends.remove(friend);
        }
    }

    @Override
    public boolean equals(final Object o)
    {
        if (null == o) {
            return false;
        }
        else if (o instanceof PersonSuper) {
            final PersonSuper p = (PersonSuper)o;
            return (equal(getName(), p.getName()) && equal(getDob(), p.getDob()) && equal(getGender(), p.getGender()) && equal(getDescription(),
                                                                                                                               p.getDescription()));
        }
        else {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s (%s) born %s looks like %s", getName(), getGender(), getDob(), getDescription());
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
