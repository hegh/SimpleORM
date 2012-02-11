package net.jonp.sorm.codegen.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a generic query.
 */
public class NamedQuery
{
    private String accessor = "public";
    private String type;
    private String name;
    private final List<QueryParam> params = new LinkedList<QueryParam>();
    private final Query query = new Query();

    public NamedQuery()
    {
        // Nothing to do
    }

    public String getAccessor()
    {
        return accessor;
    }

    public void setAccessor(final String accessor)
    {
        this.accessor = accessor;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public List<QueryParam> getParams()
    {
        return params;
    }

    public Query getQuery()
    {
        return query;
    }
}
