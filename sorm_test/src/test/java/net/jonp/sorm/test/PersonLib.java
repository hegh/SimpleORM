package net.jonp.sorm.test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


/**
 * Library functions for dealing with Person objects.
 */
public class PersonLib
{
    private PersonLib()
    {
        // Prevent instantiation
    }

    /**
     * A convenience wrapper around
     * {@link #create(net.jonp.sorm.test.Person.Orm, Collection)}.
     */
    public static void create(final Person.Orm orm, final Person... people)
        throws SQLException
    {
        create(orm, Arrays.asList(people));
    }

    /**
     * Create the given people and mark them as linked. Linking them prevents
     * faulting in the same information again if these people are pulled out of
     * the cache during a
     * {@link #read(net.jonp.sorm.test.Person.Orm, Collection)} operation.
     * 
     * @param orm The Orm object to use for database accesses.
     * @param people The people to create.
     * @throws SQLException If there was a problem.
     */
    public static void create(final Person.Orm orm, final Collection<Person> people)
        throws SQLException
    {
        orm.create(people);

        for (final Person person : people) {
            person.setLinked(true);
        }
    }

    /**
     * A convenience wrapper around
     * {@link #read(net.jonp.sorm.test.Person.Orm, Collection)}.
     */
    public static Collection<Person> read(final Person.Orm orm, final Integer... keys)
        throws SQLException
    {
        return read(orm, Arrays.asList(keys));
    }

    /**
     * Read and link the given people by identifier.
     * 
     * @param orm The Orm object to use for database queries.
     * @param keys The identifiers of the people to read.
     * @return The people that were read and linked.
     * @throws SQLException If there was a problem.
     */
    public static Collection<Person> read(final Person.Orm orm, final Collection<Integer> keys)
        throws SQLException
    {
        final Collection<Person> people = orm.read(keys);

        for (final Person person : people) {
            link(orm, person);
        }

        return people;
    }

    /**
     * Reads all linked objects from the database for the given person. Does not
     * link the linked objects recursively (that's a good way to read the entire
     * database by accident).
     * 
     * @param orm The Orm object to use for database queries.
     * @param person The person to link.
     * @throws SQLException If there was a problem.
     */
    public static void link(final Person.Orm orm, final Person person)
        throws SQLException
    {
        if (person.isLinked()) {
            return;
        }

        // Read spouse, mother, and father at the same time, if we need to read
        // any
        final List<Integer> ids = new ArrayList<Integer>(3);

        final Integer spouse = person.getSpouse();
        if (null != spouse) {
            ids.add(spouse);
        }

        final Integer mother = person.getMother();
        if (null != mother) {
            ids.add(mother);
        }

        final Integer father = person.getFather();
        if (null != father) {
            ids.add(father);
        }

        if (!ids.isEmpty()) {
            final Collection<Person> people = orm.read(ids);
            for (final Person p : people) {
                if (p.getId().equals(spouse)) {
                    person.setSpouseObject(p);
                }
                else if (p.getId().equals(mother)) {
                    person.setMotherObject(p);
                }
                else if (p.getId().equals(father)) {
                    person.setFatherObject(p);
                }
            }
        }

        person.setChildren(new HashSet<Person>(orm.readMappedChildren(person)));
        person.setFriends(new HashSet<Person>(orm.readMappedFriends(person)));

        person.setLinked(true);
    }

    public static void makeFriends(final Person.Orm orm, final Person a, final Person b)
        throws SQLException
    {
        a.addFriend(b);
        b.addFriend(a);
        orm.mapFriends(a, b);
    }

    public static void breakFriends(final Person.Orm orm, final Person a, final Person b)
        throws SQLException
    {
        a.removeFriend(b);
        b.removeFriend(a);
        orm.unmapFriends(a, b);
    }
}
