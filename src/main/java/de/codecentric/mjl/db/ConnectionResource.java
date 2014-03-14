package de.codecentric.mjl.db;

import javax.annotation.Resource;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionResource {

    // Beispiel, wie mit @Produces externe Resourcen in den CDI-Kontext integriert werden können
    // Durch dieses @Produces/@Resource-Pärchen ist es möglich die DataSource überall mit einer einfachen @Inject
    // Annotation einzubinden
    @Produces
    @Resource(lookup = "java:jboss/datasources/javaeePlaygroundDS")
    public DataSource dataSource;

    // Für Connections muss etwas mehr gemacht werden. Deshalb ist es auch möglich, Methoden mit @Produces zu annotieren
    @Produces
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Um die Connections nach Verwendung wieder sauber aufzuräumen gibt es die @Disposes Annotation
    public void closeConnection(@Disposes Connection connection) throws SQLException {
        connection.close();
    }

}
