package net.jonp.sorm.codegen.model;

import net.jonp.sorm.LinkMode;
import net.jonp.sorm.SQLType;

/**
 * Represents a linked value.
 */
public class FieldLink
{
    private final Field field;
    private LinkMode mode = LinkMode.None;
    private String type;
    private String key_type;
    private SQLType sql_type; // Default returned by getter
    private FieldLinkCollection collection = null;

    public FieldLink(final Field _field)
    {
        field = _field;
    }

    public LinkMode getMode()
    {
        return mode;
    }

    public void setMode(final LinkMode mode)
    {
        this.mode = mode;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public Field getField()
    {
        return field;
    }

    public String getKey_type()
    {
        return key_type;
    }

    public void setKey_type(final String keyType)
    {
        key_type = keyType;
    }

    public SQLType getSql_type()
    {
        if (null == sql_type && null != getKey_type()) {
            try {
                return SQLType.valueOf(getKey_type());
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

    public FieldLinkCollection getCollection()
    {
        return collection;
    }

    public void setCollection(final FieldLinkCollection collection)
    {
        this.collection = collection;
    }
}
