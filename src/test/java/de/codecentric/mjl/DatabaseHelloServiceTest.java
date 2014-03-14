package de.codecentric.mjl;

import de.codecentric.mjl.db.DBSetup;
import de.codecentric.mjl.hello.DatabaseHelloService;
import de.codecentric.mjl.hello.HelloService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 * Ein Beispiel, wie mit Arquillian die Repository-Schicht getestet werden kann.
 * Für den Test wir eine InMemory-Datenbank verwendet.
 *
 * Zusätzlich könnte über die Annotationen @UsingDataSet der Inhalt der DB aus einer JSON-Datei geladen werden.
 */
@RunWith(Arquillian.class)
public class DatabaseHelloServiceTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "dbtest.war")
                .addClasses(DatabaseHelloService.class, HelloService.class, TestConnectionProvider.class, DBSetup.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-ds.xml");
    }

    @Inject
    DatabaseHelloService helloService;

    @Test
    public void testSetHelloMessage() throws Exception {
        helloService.setHelloMessage("foobar");
        assertEquals("foobar", helloService.getHelloMessage());
    }

    public static class TestConnectionProvider {
        // Beispiel, wie mit @Produces externe Resourcen in den CDI-Kontext integriert werden können
        // Durch dieses @Produces/@Resource-Pärchen ist es möglich die DataSource überall mit einer einfachen @Inject
        // Annotation einzubinden
        @Produces
        @Resource(lookup = "java:jboss/datasources/testDS")
        public javax.sql.DataSource dataSource;

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
}
