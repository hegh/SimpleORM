package net.jonp.sorm.codegen.model;

/**
 * A setter used by parameters for queries.
 */
public class ParamSetter
{
    private final QueryParam param;
    private String content = "%{}";

    public ParamSetter(final QueryParam _param)
    {
        param = _param;
    }

    public QueryParam getParam()
    {
        return param;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }
}
