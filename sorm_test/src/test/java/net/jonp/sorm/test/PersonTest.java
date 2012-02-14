package net.jonp.sorm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import net.jonp.sorm.CacheMode;
import net.jonp.sorm.SormContext;
import net.jonp.sorm.SormSession;
import net.jonp.sorm.example.Person;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests ORM accesses on the Person test object.
 */
public class PersonTest
{
    private static SormContext context;

    @BeforeClass
    public static void setUpClass()
        throws Exception
    {
        context = DBInit.dbinit();
    }

    @AfterClass
    public static void tearDownClass()
    {
        context.close();
        DBInit.delete();
    }

    @Test
    public void testSingleInsert()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.None);
        testSingleInsertImpl(session);
    }

    @Test
    public void testCachedSingleInsert()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.Immediate);
        final Person person = testSingleInsertImpl(session);

        // XXX: By holding onto a strong reference to the Person in the
        // cache, this should prevent the garbage collector from clearing it
        // out
        // However, it is still possible, since the key in the weak hash map
        // is a boxed Integer, and may be cleared anyway
        assertNotNull(session.cacheGet(Person.class, person.getId()));
    }

    /**
     * Insert a person, then test that reading the person with that ID matches.
     * 
     * @param session The session to use.
     * @return The original person that was inserted.
     * @throws SQLException If there was a problem.
     */
    private Person testSingleInsertImpl(final SormSession session)
        throws SQLException
    {
        final Person person = buildSimpleObjects(1)[0];
        Person.Orm.create(session, person);

        final Person test = Person.Orm.read(session, person.getId());

        assertPersonEquals(person, test);

        return person;
    }

    @Test
    public void testMultipleInsert()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.None);
        final Person[] people = buildSimpleObjects(100);

        Person.Orm.create(session, people);

        final Integer keys[] = new Integer[people.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = people[i].getId();
        }

        final Collection<Person> testCollection = Person.Orm.read(session, keys);
        final Person test[] = testCollection.toArray(new Person[testCollection.size()]);

        for (int i = 0; i < people.length; i++) {
            assertPersonEquals(people[i], test[i]);
        }
    }

    @Test
    public void testSingleUpdate()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.None);
        final Person person = populate(1)[0];

        final java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());

        person.setName("newname");
        person.setGender("trans");
        person.setDob(today);
        Person.Orm.update(session, person);

        final Person test = Person.Orm.read(session, person.getId());
        assertEquals("newname", test.getName());
        assertEquals("trans", test.getGender());
        assertEquals(today, test.getDob());
    }

    @Test
    public void testMultipleUpdate()
    {
        fail("Not implemented");
    }

    @Test
    public void testSingleDelete()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.None);
        final Person person = populate(1)[0];

        Person.Orm.delete(session, person);

        final Person test = Person.Orm.read(session, person.getId());
        assertNull(test);
    }

    @Test
    public void testMultipleDelete()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.Immediate);
        final Person[] people = populate(100);
        Person.Orm.delete(session, people);

        for (final Person person : people) {
            final Person test = Person.Orm.read(session, person.getId());
            assertNull(test);
        }
    }

    /**
     * Build objects with no inter-object relationships. Does not insert them
     * into the database.
     * 
     * @param count The number of objects to build.
     * @return The objects that were built.
     */
    private Person[] buildSimpleObjects(final int count)
    {
        final Calendar dobcal = Calendar.getInstance();
        dobcal.add(Calendar.YEAR, -count);
        dobcal.add(Calendar.MONTH, -count);

        final Person[] objs = new Person[count];
        for (int i = 0; i < count; i++) {
            objs[i] = new Person();
            objs[i].setName("Person " + i);
            objs[i].setDob(dobcal.getTime());

            // Alternate male/female
            if (i % 2 == 0) {
                objs[i].setGender("male");
            }
            else {
                objs[i].setGender("female");
            }

            dobcal.add(Calendar.DAY_OF_YEAR, i);
        }

        return objs;
    }

    /**
     * Build a number of objects with inter-object relationships of all types,
     * and insert them into the database (one at a time). A {@link SormSession}
     * for this thread will be left open.
     * 
     * @param count The number of objects to build and insert.
     * @return The objects that were built.
     * @throws SQLException If there was an error inserting the objects.
     */
    private Person[] populate(final int count)
        throws SQLException
    {
        final SormSession session = context.getSession();

        final Calendar dobcal = Calendar.getInstance();
        dobcal.add(Calendar.YEAR, -count);
        dobcal.add(Calendar.MONTH, -count);

        final Person[] objs = new Person[count];
        for (int i = 0; i < count; i++) {
            objs[i] = new Person();
            objs[i].setName("Person " + i);
            objs[i].setDob(dobcal.getTime());

            // Alternate male/female
            if (i % 2 == 0) {
                objs[i].setGender("male");
            }
            else {
                objs[i].setGender("female");
            }

            Person.Orm.create(session, objs[i]);

            // Alternate marrying the current with the previous with making
            // the current pair the previous pair's children
            if (i % 4 == 1) {
                objs[i - 1].setSpouse(objs[i - 0].getId());
                objs[i - 0].setSpouse(objs[i - 1].getId());
            }
            else if (i % 4 == 3) {
                objs[i - 1].setFather(objs[i - 3].getId());
                objs[i - 1].setMother(objs[i - 2].getId());
                objs[i - 0].setFather(objs[i - 3].getId());
                objs[i - 0].setMother(objs[i - 2].getId());
            }

            objs[i].setFriends(new HashSet<Person>());

            // Make the children of each set of parents friends
            if (i % 8 == 7) {
                objs[i - 5].getFriends().add(objs[i - 1]);
                objs[i - 1].getFriends().add(objs[i - 5]);
                Person.Orm.mapFriends(session, objs[i - 5], objs[i - 1]);

                objs[i - 4].getFriends().add(objs[i - 1]);
                objs[i - 1].getFriends().add(objs[i - 4]);
                Person.Orm.mapFriends(session, objs[i - 4], objs[i - 1]);

                objs[i - 5].getFriends().add(objs[i - 0]);
                objs[i - 0].getFriends().add(objs[i - 5]);
                Person.Orm.mapFriends(session, objs[i - 5], objs[i - 0]);

                objs[i - 4].getFriends().add(objs[i - 0]);
                objs[i - 0].getFriends().add(objs[i - 4]);
                Person.Orm.mapFriends(session, objs[i - 4], objs[i - 0]);
            }

            dobcal.add(Calendar.DAY_OF_YEAR, i);
        }

        return objs;
    }

    private void assertPersonEquals(final Person expected, final Person actual)
    {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getDob(), actual.getDob());
    }
}
