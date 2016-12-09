package es.jpery.dentalClinic.dao;

import es.jpery.dentalClinic.domain.Arrangement;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by JPery on 9/11/16.
 */
@Repository
public class ArrangementDAOImpl implements ArrangementDAO {
    private static final String openTime = "09:30:00";
    private static final String closeTime = "20:30:00";
    private static Connection connection;

    public ArrangementDAOImpl() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:file:./DB", "SA", "");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
        }
        createDB();
    }

    private void createDB() {
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS ARRANGEMENTS (ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0, INCREMENT BY 1) NOT NULL,"
                    + "TITLE INTEGER NOT NULL, "
                    + "DATE TIMESTAMP NOT NULL, "
                    + "OWNER INTEGER NOT NULL,"
                    + "COMMENT VARCHAR (40),"
                    + "PRIMARY KEY (ID)) ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Arrangement> getArrangements() {
        List<Arrangement> arrangements = new ArrayList<Arrangement>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRANGEMENTS");
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Arrangement arrangement = new Arrangement(result.getInt(1), result.getInt(2), result.getTimestamp(3), result.getInt(4), result.getString(5));
                arrangements.add(arrangement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrangements;
    }

    @Override
    public List<Arrangement> getArrangementsbyUserID(int userID) {
        List<Arrangement> arrangements = new ArrayList<Arrangement>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRANGEMENTS WHERE OWNER = ?");
            preparedStatement.setInt(1, userID);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Arrangement arrangement = new Arrangement(result.getInt(1), result.getInt(2), result.getTimestamp(3), result.getInt(4), result.getString(5));
                arrangements.add(arrangement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrangements;
    }

    @Override
    public Arrangement getArrangementbyID(int id) {
        Arrangement arrangement = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRANGEMENTS WHERE ID = ?;");
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
                arrangement = new Arrangement(result.getInt(1), result.getInt(2), result.getTimestamp(3), result.getInt(4), result.getString(5));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrangement;
    }

    @Override
    public boolean addArrangement(Arrangement arrangement) {
        try {
            if (arrangement.getKindOfIntervention() > 0) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ARRANGEMENTS (title, date, owner,comment) VALUES ?, ?, ?, ?",Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, arrangement.getKindOfIntervention());
                preparedStatement.setTimestamp(2, new Timestamp(arrangement.getDate().getTime()));
                preparedStatement.setInt(3, arrangement.getOwner());
                preparedStatement.setString(4,arrangement.getComment());
                preparedStatement.execute();
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if(generatedKeys.next()) {
                    arrangement.setId(generatedKeys.getInt(1));
                }

                return true;
            } else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateArrangement(int arrangementid, Arrangement arrangement) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ARRANGEMENTS SET title = ?, date = ?, owner = ?, comment = ? WHERE id = ?");
            preparedStatement.setInt(1, arrangement.getKindOfIntervention());
            preparedStatement.setDate(2, new Date(arrangement.getDate().getTime()));
            preparedStatement.setInt(3, arrangement.getOwner());
            preparedStatement.setString(4, arrangement.getComment());
            preparedStatement.setInt(5, arrangementid);
            return preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Arrangement> getAvailableArrangementsByArrangement(Arrangement arrangement) {
        List<Arrangement> availableArrangements = new ArrayList<>();
        int newDay = 0;
        try {
            while (availableArrangements.size() < 5) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRANGEMENTS WHERE date >= ? AND date <= ?");
                java.util.Date in = arrangement.getDate();
                LocalDateTime now = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
                int year = now.getYear();
                int month = now.getMonthValue();
                int day = now.getDayOfMonth();
                java.util.Date init = getOpenTimeInDate(arrangement);
                java.util.Date end = getCloseTimeInDate(arrangement);
                preparedStatement.setTimestamp(1, new Timestamp(init.getTime()));
                preparedStatement.setTimestamp(2, new Timestamp(end.getTime()));
                ResultSet resultSet = preparedStatement.executeQuery();
                List<Arrangement> busyArrangements = new ArrayList<>();
                while (resultSet.next()) {
                    Arrangement DBarrangement = new Arrangement(resultSet.getInt(1), resultSet.getInt(2), resultSet.getTimestamp(3), resultSet.getInt(4), resultSet.getString(5));
                    busyArrangements.add(DBarrangement);
                }
                for (int i = 8; i < 20; i++) {
                    String hour = (i < 10) ? "0" + i : "" + i;
                    String date = year + "-" + month + "-" + day + " " + " " + hour + ":30:00";
                    Arrangement newArrangement = new Arrangement(-1, -1, Arrangement.FORMAT.parse(date), -1, "");
                    if (!isBusy(busyArrangements, newArrangement)) {
                        availableArrangements.add(newArrangement);
                    }
                }
                return availableArrangements;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private java.util.Date getOpenTimeInDate(Arrangement arrangement) {
        java.util.Date in = arrangement.getDate();
        LocalDateTime now = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        try {
            return Arrangement.FORMAT.parse(year + "-" + month + "-" + day + " " + openTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private java.util.Date getCloseTimeInDate(Arrangement arrangement) {
        java.util.Date in = arrangement.getDate();
        LocalDateTime now = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        try {
            return Arrangement.FORMAT.parse(year + "-" + month + "-" + day + " " + closeTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean isBusy(List<Arrangement> busyArrangements, Arrangement arrangementToCheck) {
        boolean busy = false;
        if (getOpenTimeInDate(arrangementToCheck).compareTo(arrangementToCheck.getDate()) > 0 && getCloseTimeInDate(arrangementToCheck).compareTo(arrangementToCheck.getDate()) < 0) {
            busy = true;
        } else {
            for (int i = 0; i < busyArrangements.size() && !busy; i++) {
                if (busyArrangements.get(i).getDate().compareTo(arrangementToCheck.getDate()) == 0) {
                    busy = true;
                }
            }
        }
        return busy;
    }

    @Override
    public boolean deleteArrangement(int arrangementid){
        int rows=0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ARRANGEMENTS WHERE ID = ?");
            preparedStatement.setInt(1, arrangementid);
            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows>0;
    }
}
