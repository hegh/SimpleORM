package net.jonp.sorm.codegen.model;

import net.jonp.sorm.codegen.SQLType;

/**
 * Represents a Sorm field.
 */
public class Field
{
    private String accessor = "private";
    private String type;
    private String name;
    private boolean primary = false;
    private SQLType sql_type; // Default returned by getter
    private String sql_column; // Default returned by getter
    private boolean nullable = false;
    private final FieldGetter get = new FieldGetter(this);
    private final FieldSetter set = new FieldSetter(this);
    private final FieldLink link = new FieldLink(this);

    public Field()
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

    public boolean isPrimary()
    {
        return primary;
    }

    public void setPrimary(final boolean primary)
    {
        this.primary = primary;
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

    public String getSql_column()
    {
        if (null == sql_column && null != getName()) {
            return getName();
        }

        return sql_column;
    }

    public void setSql_column(final String sqlColumn)
    {
        sql_column = sqlColumn;
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public void setNullable(final boolean nullable)
    {
        this.nullable = nullable;
    }

    public FieldGetter getGet()
    {
        return get;
    }

    public FieldSetter getSet()
    {
        return set;
    }

    public FieldLink getLink()
    {
        return link;
    }
}
