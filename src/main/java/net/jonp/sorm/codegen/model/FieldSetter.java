package net.jonp.sorm.codegen.model;

import net.jonp.sorm.codegen.StringUtil;

/**
 * Represents a field setter.
 */
public class FieldSetter
{
    private final Field field;
    private String accessor = "public";
    private String name; // Default returned by getter
    private String content; // Default returned by getter

    public FieldSetter(final Field _field)
    {
        field = _field;
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
        if (null == name && null != getField().getName()) {
            return String.format("set%s", StringUtil.capFirst(getField().getName()));
        }

        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getContent()
    {
        if (null == content && null != getName()) {
            return String.format("%%{}.%s(%%{%s})", getName(), getField().getName());
        }

        return content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }

    public Field getField()
    {
        return field;
    }
}
