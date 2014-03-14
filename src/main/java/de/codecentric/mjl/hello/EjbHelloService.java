package de.codecentric.mjl.hello;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;

@Named
@HelloSource(HelloSource.Source.EJB)
@Stateless
public class EjbHelloService implements HelloService, Serializable {

    private static final Logger LOG = Logger.getLogger(EjbHelloService.class.getName());

    private Random random = new Random();

    @Resource(lookup = "java:jboss/datasources/javaeePlaygroundDS")
    private DataSource dataSource;

    public String getHelloMessage() {
        try (Connection connection = dataSource.getConnection()) {
            ResultSet resultSet = null;
            try {
                resultSet = connection.prepareStatement("SELECT msg FROM messages LIMIT 1").executeQuery();
            } catch (SQLException e) {
                return "Unable to execute query: " + e.getMessage();
            }
            try {
                while (resultSet.next()) {
                    return resultSet.getString(1);
                }
            } catch (SQLException e) {
                return "Unable to read resultset: " + e.getMessage();
            }
            return "no message in database";
        } catch (SQLException e) {
            return "Unable to open database connection: " + e.getMessage();
        }
    }

    // In EJBs (gekennzeichnet durch @javax.ejb.Singleton, @javax.ejb.Stateless oder @javax.ejb.Stateful) ist jede
    // Methode automatisch in eine Transaktion eingebettet.
    // Mit der Annotation @TransactionAttribute kann man diese Transaktion konfigurieren (oder auch abschalten).
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setHelloMessage(String message) {
        try (Connection connection = dataSource.getConnection()) {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE messages SET msg=?");
                statement.setString(1, message);
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (false && random.nextBoolean()) {
            throw new RuntimeException("ROLLING, ROLLING, ROLLING ... back");
        }
    }
}