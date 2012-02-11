package net.jonp.sorm.codegen.model;

import net.jonp.sorm.codegen.SQLType;

/**
 * Represents a parameter to a query.
 */
public class QueryParam
{
    private String type;
    private String name;
    private SQLType sql_type; // Default returned by getter
    private final ParamSetter set = new ParamSetter(this);

    public QueryParam()
    {
        // Nothing to do
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

    public SQLType getSql_type()
    {
        if (null == sql_type && null != getType()) {
            try {
                return SQLType.valueOf(getType());
            }
            catch (final IllegalArgumentException iae) {
                return null;
            }
        }

        return sql_type;
    }

    public void setSql_type(final SQLType sqlType)
    {
        sql_type = sqlType;
    }

    public ParamSetter getSet()
    {
        return set;
    }
}
