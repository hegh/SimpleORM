package net.jonp.sorm.codegen.model;


/**
 * Describes the collection held by a field link.
 */
public class FieldLinkCollection
{
    private final FieldLink link;
    private final Query read = new Query();
    private Query create;
    private Query delete;

    public FieldLinkCollection(final FieldLink _link)
    {
        link = _link;
    }

    public Query getRead()
    {
        return read;
    }

    public Query getCreate()
    {
        return create;
    }

    public void setCreate(final Query create)
    {
        this.create = create;
    }

    public Query getDelete()
    {
        return delete;
    }

    public void setDelete(final Query delete)
    {
        this.delete = delete;
    }

    public FieldLink getLink()
    {
        return link;
    }
}
