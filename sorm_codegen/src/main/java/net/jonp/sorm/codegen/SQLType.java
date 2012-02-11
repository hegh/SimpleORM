package net.jonp.sorm.codegen;

import java.sql.PreparedStatement;
import java.sql.Types;

public enum SQLType
{
    /** Deals with SQL Array objects. */
    Array("java.sql.Array", "getArray", "setArray", "ARRAY"),

    /** Deals with InputStreams. */
    AsciiStream("java.io.InputStream", "getAsciiStream", "setAsciiStream", null),

    /** Deals with BigDecimal objects. */
    BigDecimal("java.math.BigDecimal", "getBigDecimal", "setBigDecimal", "NUMERIC"),

    /** Deals with InputStreams. */
    BinaryStream("java.io.InputStream", "getBinaryStream", "setBinaryStream", null),

    /** Deals with SQL Blob objects. */
    Blob("java.sql.Blob", "getBlob", "setBlob", "BLOB"),

    /** A boolean. */
    Boolean("Boolean", "getBoolean", "setBoolean", "BOOLEAN"),

    /** A byte. */
    Byte("Byte", "getByte", "setByte", "SMALLINT"),

    /** An array of bytes. */
    ByteA("byte[]", "getBytes", "setBytes", "VARBINARY"),

    /** Deals with Readers. */
    CharStream("java.io.Reader", "getCharacterStream", "setCharacterStream", null),

    /** Deals with SQL Clob objects. */
    Clob("java.sql.Clob", "getClob", "setClob", "CLOB"),

    /** Deals with SQL Date objects. */
    Date("java.sql.Date", "getDate", "setDate", "DATE"),

    /** A double. */
    Double("Double", "getDouble", "setDouble", "DOUBLE"),

    /** A float. */
    Float("Float", "getFloat", "setFloat", "FLOAT"),

    /** An integer. */
    Integer("Integer", "getInt", "setInt", "INTEGER"),

    /** A long. */
    Long("Long", "getLong", "setLong", "BIGINT"),

    /** Deals with Readers. */
    NCharStream("java.io.Reader", "getNCharacterStream", "setNCharacterStream", null),

    /** Deals with SQL NClob objects. */
    NClob("NClob", "getNClob", "setNClob", "NCLOB"),

    /** No type (no column corresponds to this field). */
    None(null, null, null, null),

    /** A string. */
    NString("String", "getNString", "setNString", "NVARCHAR"),

    /** An object. */
    Object("Object", "getObject", "setObject", null),

    /** A SQL Ref object. */
    Ref("java.sql.Ref", "getRef", "setRef", "REF"),

    /** A SQL RowID object. */
    RowId("java.sql.RowId", "getRowId", "setRowId", "ROWID"),

    /** A short. */
    Short("Short", "getShort", "setShort", "SMALLINT"),

    /** A SQL SQLXML object. */
    SQLXML("java.sql.SQLXML", "getSQLXML", "setSQLXML", "SQLXML"),

    /** A string. */
    String("String", "getString", "setString", "VARCHAR"),

    /** A SQL Time object. */
    Time("java.sql.Time", "getTime", "setTime", "TIME"),

    /** A SQL Timestamp object. */
    Timestamp("java.sql.Timestamp", "getTimestamp", "setTimestamp", "TIMESTAMP"),

    /** A URL. */
    URL("java.net.URL", "getURL", "setURL", "DATALINK"),

    /** An internal value used to call the pk function. */
    _PK_GETTER(null, null, null, null),

    // Autoformatter hint
    ;

    /** The class name to use for this type. */
    public final String typeName;

    /** The ResultSet getter function to use for this type. */
    public final String getter;

    /** The PreparedStatement setter function to use for this type. */
    public final String setter;

    /**
     * The type field from {@link Types}, or <code>null</code> if not supported
     * for this type.
     */
    public final String sqltype;

    /**
     * Construct a SQLType.
     * 
     * @param _typeName The Java type name.
     * @param _getter The name of the {@link PreparedStatement} getter to use.
     * @param _setter The name of the {@link PreparedStatement} setter to use.
     * @param _sqltype The name of the {@link Types} field to use.
     */
    private SQLType(final String _typeName, final String _getter, final String _setter, final String _sqltype)
    {
        typeName = _typeName;
        getter = _getter;
        setter = _setter;
        sqltype = _sqltype;
    }
}
