package de.codecentric.mjl.hello;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Named
@HelloSource(HelloSource.Source.DATABASE)
public class DatabaseHelloService implements HelloService, Serializable {

    @Inject
    private Connection connection;

    public String getHelloMessage() {
        ResultSet resultSet = null;
        try {
            resultSet = connection.prepareStatement("SELECT msg FROM messages").executeQuery();
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
    }

    // Mit @Transactional kann man auch in einer einfachen Managed-Bean Container-Managed-Transactions definieren
    // Für Transaktionen in EJBs, siehe EjbHelloService.
    public void setHelloMessage(String message) {
        try {
            connection.prepareStatement("DELETE FROM messages").execute();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (msg) VALUES (?)");
            statement.setString(1, message);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}