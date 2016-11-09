package es.jpery.dentalClinic.dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by j.pery on 9/11/16.
 */
public class UserDAO {

    private Connection connection;

    public void connect() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:file:" + "./DB", "sa","");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return;
        }
    }
}
