package dk.statsbiblioteket.metadatarepository.xmltapes.junit;

import dk.statsbiblioteket.metadatarepository.xmltapes.sqlindex.SQLIndex;

public class PostgresTestSettings {
    public static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    public static final String JDBC_URL = "jdbc:postgresql:tapes";
    public static final String POSTGRES_USER = "ktc";
    public static final String POSTGRES_PASS = "foobar";
    
    
    public static SQLIndex getPostgreIndex() {
        return new SQLIndex(POSTGRES_DRIVER, JDBC_URL, POSTGRES_USER, POSTGRES_PASS);
    }
}