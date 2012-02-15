package net.jonp.sorm.codegen.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Structure containing the entire Sorm code-generation configuration.
 */
public class Sorm
{
    private String pkg;
    private String accessor = "public";
    private String name;
    private String orm_accessor = "public";
    private String superClass = null;

    private final List<Field> fields = new LinkedList<Field>();
    private final Query create = new Query();
    private final Query pk = new Query();
    private final Query read = new Query();
    private final Query update = new Query();
    private final Query delete = new Query();
    private final List<NamedQuery> queries = new LinkedList<NamedQuery>();

    public Sorm()
    {
        // Nothing to do
    }

    public String getPkg()
    {
        return pkg;
    }

    public void setPkg(final String pkg)
    {
        this.pkg = pkg;
    }

    public String getAccessor()
    {
        return accessor;
    }

    public void setAccessor(final String accessor)
    {
        this.accessor = accessor;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getOrm_accessor()
    {
        return orm_accessor;
    }

    public void setOrm_accessor(final String ormAccessor)
    {
        orm_accessor = ormAccessor;
    }

    public String getSuper()
    {
        return superClass;
    }

    public void setSuper(final String superClass)
    {
        this.superClass = superClass;
    }

    public List<Field> getFields()
    {
        return fields;
    }

    public Query getCreate()
    {
        return create;
    }

    public Query getPk()
    {
        return pk;
    }

    public Query getRead()
    {
        return read;
    }

    public Query getUpdate()
    {
        return update;
    }

    public Query getDelete()
    {
        return delete;
    }

    public List<NamedQuery> getQueries()
    {
        return queries;
    }

    /**
     * If there is a primary field, get it.
     * 
     * @return The primary field, or <code>null</code> if there is none.
     * @throws IllegalStateException If there are multiple.
     */
    public Field getPrimaryField()
    {
        final List<Field> primary = new ArrayList<Field>(1);
        for (final Field f : getFields()) {
            if (f.isPrimary()) {
                primary.add(f);
            }
        }

        if (primary.isEmpty()) {
            return null;
        }
        else if (primary.size() == 1) {
            return primary.get(0);
        }
        else {
            throw new IllegalStateException("Multiple primary fields found.");
        }
    }
}
