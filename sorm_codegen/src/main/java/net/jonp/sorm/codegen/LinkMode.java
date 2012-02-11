package net.jonp.sorm.codegen;

public enum LinkMode
{
    /** Not shared. This field is simply directly stored in a database column. */
    None,

    /**
     * One instance of this object owns one instance of that object. This field
     * holds the index to another table (or possibly this table), and the other
     * table should have a field that holds an index into this table to point at
     * this object.
     */
    OneToOne,

    /**
     * One instance of this object owns many instances of that object. There is
     * no actual field on this table; another table has a field that holds an
     * index into this table. Multiple objects from that table will reference
     * one object here.
     */
    OneToMany,

    /**
     * Many instances of this object share one instance of that object. This
     * field holds the index to another table (or possibly this table). The
     * other table does not hold an index into this table.
     */
    ManyToOne,

    /**
     * Many instances of this object share many instances of that object.
     * Neither this table, nor the corresponding other table, actually has a
     * field corresponding to the field of the object. Instead, the indices from
     * both tables are paired in a mapping table.
     */
    ManyToMany,

    // Autoformatter hint
    ;
}
