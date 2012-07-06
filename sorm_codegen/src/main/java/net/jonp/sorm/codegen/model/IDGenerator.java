package net.jonp.sorm.codegen.model;

/**
 * The options for generating IDs for SORM objects.
 */
public enum IDGenerator
{
    /** IDs are retrieved <i>after</i> creating objects in the database. */
    Post,

    /** IDs are retrieved <i>before</i> creating objects in the database. */
    Pre,
    //
    ;
}
