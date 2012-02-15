package net.jonp.sorm.codegen.model;

import net.jonp.sorm.codegen.StringUtil;

/**
 * Represents a getter for a field.
 */
public class FieldGetter
{
    private final Field field;
    private String accessor = "public";
    private String name; // Default returned by getter
    private boolean override = false;

    private String content; // Default returned by getter

    public FieldGetter(final Field _field)
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
            return String.format("get%s", StringUtil.capFirst(getField().getName()));
        }

        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public boolean isOverride()
    {
        return override;
    }

    public void setOverride(final boolean override)
    {
        this.override = override;
    }

    public String getContent()
    {
        if ((null == content || content.isEmpty()) && null != getName()) {
            return String.format("%%{}.%s()", getName());
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
