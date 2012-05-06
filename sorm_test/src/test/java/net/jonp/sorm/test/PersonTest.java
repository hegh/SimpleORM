package net.jonp.sorm.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import net.jonp.sorm.CacheMode;
import net.jonp.sorm.Dialect;
import net.jonp.sorm.SormContext;
import net.jonp.sorm.SormIterable;
import net.jonp.sorm.SormIterator;
import net.jonp.sorm.SormSession;

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

        assertEquals(person, test);

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

        assertArrayEquals(people, test);
    }

    @Test
    public void testIterator()
        throws SQLException
    {
        // We will be testing both cached and non-cached reads by removing some
        // elements from the cache
        final SormSession session = context.getSession(CacheMode.Immediate);
        final Person[] people = buildSimpleObjects(100);

        Person.Orm.create(session, people);

        // Evict every other element from the cache
        for (int i = 0; i < people.length; i += 2) {
            session.cacheDel(Person.class, people[i]);
        }

        final Integer[] keys = getKeys(people);
        final SormIterable<Person> iterable = Person.Orm.matches(session, Arrays.asList(keys));
        final SormIterator<Person> iterator = iterable.iterator();
        try {
            int i = 0;
            while (iterator.hasNext()) {
                final Person person = iterator.next();
                assertEquals(people[i++], person);
            }
        }
        finally {
            iterator.close();
        }
    }

    @Test
    public void testSingleUpdate()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.None);
        final Person.Orm orm = new Person.Orm(session);
        final Person person = populate(orm, 1)[0];

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
        final Person.Orm orm = new Person.Orm(session);
        final Person[] people = populate(orm, 100);

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

        assertArrayEquals(people, test);
    }

    @Test
    public void testSingleDelete()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.None);
        final Person.Orm orm = new Person.Orm(session);
        final Person person = populate(orm, 1)[0];

        Person.Orm.delete(session, person);

        final Person test = Person.Orm.read(session, person.getId());
        assertNull(test);
    }

    @Test
    public void testMultipleDelete()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.Immediate);
        final Person.Orm orm = new Person.Orm(session);
        final Person[] people = populate(orm, 100);
        Person.Orm.delete(session, people);

        for (final Person person : people) {
            final Person test = Person.Orm.read(session, person.getId());
            assertNull(test);
        }
    }

    @Test
    public void testMapUnmap()
        throws SQLException
    {
        final SormSession session = context.getSession(CacheMode.Immediate);
        final Person[] friends = buildSimpleObjects(4);
        Person.Orm.create(session, friends);

        for (int i = 0; i < friends.length; i++) {
            friends[i].setFriends(new HashSet<Person>());
        }

        for (int i = 0; i < friends.length; i++) {
            for (int j = i + 1; j < friends.length; j++) {
                friends[i].getFriends().add(friends[j]);
                friends[j].getFriends().add(friends[i]);
                Person.Orm.mapFriends(session, friends[i], friends[j]);
            }
        }

        final Integer[] keys = getKeys(friends);
        final Collection<Person> collection = Person.Orm.read(session, keys);
        assertEquals(friends.length, collection.size());

        final Person[] people = collection.toArray(new Person[collection.size()]);
        for (final Person person : people) {
            person.setFriends(new HashSet<Person>(Person.Orm.readMappedFriends(session, person)));
        }

        assertArrayEquals(friends, people);

        for (int i = 0; i < friends.length; i++) {
            assertCollectionsMatch(friends[i].getFriends(), people[i].getFriends());
        }
    }

    @Test
    public void testInstantiatedOrm()
        throws SQLException
    {
        final SormSession session = context.getSession();
        final Person.Orm orm = new Person.Orm(session);
        final Person[] people = populate(orm, 100);

        // Evict every other element from the cache
        for (int i = 0; i < people.length; i += 2) {
            session.cacheDel(Person.class, people[i]);
        }

        final Integer[] keys = getKeys(people);
        final Collection<Person> testCollection = PersonLib.read(orm, keys);
        final Person test[] = testCollection.toArray(new Person[testCollection.size()]);

        assertArrayEquals(people, test);

        for (int i = 0; i < test.length; i++) {
            assertEquals(people[i].getSpouseObject(), test[i].getSpouseObject());
            assertEquals(people[i].getMotherObject(), test[i].getMotherObject());
            assertEquals(people[i].getFatherObject(), test[i].getFatherObject());
            assertCollectionsMatch(people[i].getFriends(), test[i].getFriends());
            assertCollectionsMatch(people[i].getChildren(), test[i].getChildren());
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

            // Height range: 145 - 200 cm in increments of 5 cm
            // Weight range: 50 - 125 kg in increments of 2.5 kg
            final PhysicalDescription description = new PhysicalDescription();
            description.setHeight(145 + (i * 5) % 55);
            description.setWeight(50 + (i * 2.5 % 75));
            description.setHairColor(Color.values()[i % Color.values().length]);
            description.setEyeColor(Color.values()[(i * 2) % Color.values().length]);

            if (i % 4 == 0) {
                // One in four has dyed hair
                description.setHairColorAlt(Color.values()[(i + 1) % Color.values().length]);
            }

            objs[i].setDescription(description);

            dobcal.add(Calendar.DAY_OF_YEAR, i);
        }

        return objs;
    }

    /**
     * Build a number of objects with inter-object relationships of all types,
     * and insert them into the database (one at a time).
     * 
     * @param orm The Orm object to use for all updates.
     * @param count The number of objects to build and insert.
     * @return The objects that were built.
     * @throws SQLException If there was an error inserting the objects.
     */
    private Person[] populate(final Person.Orm orm, final int count)
        throws SQLException
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


            // Height range: 145 - 200 cm in increments of 5 cm
            // Weight range: 50 - 125 kg in increments of 2.5 kg
            final PhysicalDescription description = new PhysicalDescription();
            description.setHeight(145 + (i * 5) % 55);
            description.setWeight(50 + (i * 2.5 % 75));
            description.setHairColor(Color.values()[i % Color.values().length]);
            description.setEyeColor(Color.values()[(i * 2) % Color.values().length]);

            if (i % 4 == 0) {
                // One in four has dyed hair
                description.setHairColorAlt(Color.values()[(i + 1) % Color.values().length]);
            }

            objs[i].setDescription(description);

            PersonLib.create(orm, objs[i]);

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
                PersonLib.makeFriends(orm, objs[i - 5], objs[i - 1]);
                PersonLib.makeFriends(orm, objs[i - 4], objs[i - 1]);
                PersonLib.makeFriends(orm, objs[i - 5], objs[i - 0]);
                PersonLib.makeFriends(orm, objs[i - 4], objs[i - 0]);
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

    private static <T> void assertCollectionsMatch(final Collection<T> lhs, final Collection<T> rhs)
    {
        if (null != lhs && null != rhs) {
            assertTrue(lhs.containsAll(rhs));
            assertTrue(rhs.containsAll(lhs));
        }
        else if (null == lhs && null == rhs) {
            return;
        }
        else {
            fail("One null collection");
        }
    }
}
