package net.jonp.sorm;

/**
 * Sorm supports these caching modes.
 */
public enum CacheMode
{
    /**
     * Do not use the object cache. Every object read will hit the database,
     * every object delete will hit the database immediately.
     */
    None,

    /**
     * Use the cache for objects that have been touched. Speeds up read access
     * by only hitting the database when necessary, and allows for automated
     * object sharing through the {@link SormSession}. Creates, updates, and
     * deletes all hit the database immediately. Deletes evict items from the
     * cache.
     */
    Immediate,

    /**
     * Use the cache to build up a set of changes, which may all be pushed to
     * the database in one burst transaction using {@link SormSession#flush()}.
     * Created objects will not have assigned identifiers, updates will do
     * nothing (the updates will be written during the burst), reads will hit
     * the database as necessary but outside of the transaction (unless you
     * start one manually), and deletes will move objects from the object cache
     * to an otherwise inaccessible delete cache.
     */
    Delayed,
    //
    ;
}
