package de.codecentric.mjl.db;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// see http://rmannibucau.wordpress.com/2012/12/11/ensure-some-applicationscoped-beans-are-eagerly-initialized-with-javaee/
// on why we need to use an ejb here
@Singleton
@Startup
public class DBSetup {

    @Inject
    private DataSource dataSource;

    @PostConstruct
    public void afterCreate() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareCall("CREATE TABLE contacts (fullname VARCHAR(255), email VARCHAR(255))").execute();
            connection.prepareCall("CREATE TABLE messages (msg VARCHAR(255))").execute();
        }
    }
}
