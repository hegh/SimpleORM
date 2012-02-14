package net.jonp.sorm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import net.jonp.sorm.CacheMode;
import net.jonp.sorm.Dialect;
import net.jonp.sorm.SormContext;
import net.jonp.sorm.SormSession;
import net.jonp.sorm.example.Person;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests ORM accesses on the Person test object.
 */
@RunWith(Parameterized.class)
public class PersonTest
{
    private static final Collection<SormContext> contexts = new ArrayList<SormContext>();

    @Parameters
    public static Collection<Object[]> databases()
    {
        contexts.add(DBInit.dbinit(Dialect.H2));
        contexts.add(DBInit.dbinit(Dialect.SQLite));

        final Collection<Object[]> params = new ArrayList<Object[]>(contexts.size());
        for (final SormContext context : contexts) {
            params.add(new Object[] {
                context
            });
        }

        return params;
    }

    @AfterClass
    public static void tearDownClass()
    {
        for (final SormContext context : contexts) {
            context.close();
            DBInit.delete(context.getDialect());
        }
    }

    private final SormContext context;

    public PersonTest(final SormContext _context)
    {
        context = _context;
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

        final Integer[] keys = getKeys(people);

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

        final java.util.Date today = getDayCalendar().getTime();

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
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.None);
        final Person[] people = populate(100);

        final java.util.Date today = getDayCalendar().getTime();

        for (final Person person : people) {
            person.setName("Test");
            person.setGender("trans");
            person.setDob(today);
        }

        Person.Orm.update(session, people);

        final Integer[] keys = getKeys(people);
        final Collection<Person> testCollection = Person.Orm.read(session, keys);
        final Person test[] = testCollection.toArray(new Person[testCollection.size()]);

        for (int i = 0; i < people.length; i++) {
            assertPersonEquals(people[i], test[i]);
        }
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
        final Calendar dobcal = getDayCalendar();
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
     * and insert them into the database (one at a time).
     * 
     * @param count The number of objects to build and insert.
     * @return The objects that were built.
     * @throws SQLException If there was an error inserting the objects.
     */
    private Person[] populate(final int count)
        throws SQLException
    {
        final SormSession session = context.getSession();

        final Calendar dobcal = getDayCalendar();
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

    /** Get a {@link Calendar} set to today, with all time parameters set to 0. */
    private Calendar getDayCalendar()
    {
        final Calendar daycal = Calendar.getInstance();
        daycal.set(Calendar.HOUR_OF_DAY, 0);
        daycal.set(Calendar.MINUTE, 0);
        daycal.set(Calendar.SECOND, 0);
        daycal.set(Calendar.MILLISECOND, 0);

        return daycal;
    }

    private Integer[] getKeys(final Person[] people)
    {
        final Integer keys[] = new Integer[people.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = people[i].getId();
        }

        return keys;
    }

    private void assertPersonEquals(final Person expected, final Person actual)
    {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getDob(), actual.getDob());
    }
}
