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
    private static final long HOUR = 3600000;

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
                    + "TITLE VARCHAR(40) NOT NULL, "
                    + "DATE TIMESTAMP NOT NULL, "
                    + "OWNER INTEGER NOT NULL,"
                    + "PRIMARY KEY (ID)) ");
            s.executeUpdate("INSERT INTO ARRANGEMENTS (title, date, owner) SELECT 'Filling', '2017-01-05 18:30:00', 0 FROM INFORMATION_SCHEMA.TABLES WHERE not exists (select  * from ARRANGEMENTS where id=0)");
            s.executeUpdate("INSERT INTO ARRANGEMENTS (title, date, owner) SELECT 'Endodontics', '2017-01-05 19:30:00', 1 FROM INFORMATION_SCHEMA.TABLES WHERE not exists (select  * from ARRANGEMENTS where id=1)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Arrangement> getArrangements() {
        List<Arrangement>  arrangements = new ArrayList<Arrangement>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRANGEMENTS");
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Arrangement arrangement = new Arrangement(result.getInt(1), result.getString(2), result.getTimestamp(3),result.getInt(4));
                arrangements.add(arrangement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrangements;
    }

    @Override
    public List<Arrangement> getArrangementsbyUserID(int userID) {
        List<Arrangement>  arrangements = new ArrayList<Arrangement>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRANGEMENTS WHERE OWNER = ?");
            preparedStatement.setInt(1,userID);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Arrangement arrangement = new Arrangement(result.getInt(1), result.getString(2), result.getTimestamp(3),result.getInt(4));
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
                arrangement = new Arrangement(result.getInt(1), result.getString(2), result.getTimestamp(3),result.getInt(4));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrangement;
    }

    @Override
    public boolean addArrangement(Arrangement arrangement) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ARRANGEMENTS (title, date, owner) VALUES ?, ?, ?");
            preparedStatement.setString(1, arrangement.getTitle());
            preparedStatement.setTimestamp(2, new Timestamp(arrangement.getDate().getTime()-HOUR));
            preparedStatement.setInt(3, arrangement.getOwner());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateArrangement(int arrangementid, Arrangement arrangement) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ARRANGEMENTS SET id=?, title = ?, date = ?, owner = ? WHERE id = ?");
            preparedStatement.setInt(1, arrangement.getId());
            preparedStatement.setString(2, arrangement.getTitle());
            preparedStatement.setDate(3, new Date(arrangement.getDate().getTime()));
            preparedStatement.setInt(4, arrangement.getOwner());
            preparedStatement.setInt(5, arrangementid);
            return preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Arrangement> getAvailableArrangementsByArrangement(Arrangement arrangement){
        List<Arrangement> availableArrangements = new ArrayList<>();
        int newDay = 0;
        try {
            while(availableArrangements.size()<5) {
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
                System.out.println(resultSet.getFetchSize());
                List<Arrangement> busyArrangements = new ArrayList<>();
                while (resultSet.next()) {
                    Arrangement DBarrangement = new Arrangement(resultSet.getInt(1), resultSet.getString(2), resultSet.getTimestamp(3), resultSet.getInt(4));
                    busyArrangements.add(DBarrangement);
                }

                for (int i = 8; i < 21; i++) {
                    String hour = (i < 10) ? "0" + i : "" + i;
                    String date = year + "-" + month + "-" + day + " " + " " + hour + ":30:00";
                    Arrangement newArrangement = new Arrangement(-1, "", Arrangement.FORMAT.parse(date), -1);
                    if (!isBusy(busyArrangements, newArrangement)) {
                        availableArrangements.add(newArrangement);
                    }
                }
                return availableArrangements;
            }
        }
        catch(Exception e){e.printStackTrace();}
        return null;
    }



    private java.util.Date getOpenTimeInDate(Arrangement arrangement){
        java.util.Date in = arrangement.getDate();
        LocalDateTime now = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        try {
            return Arrangement.FORMAT.parse(year + "-" + month + "-" + day + " " + openTime);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    private java.util.Date getCloseTimeInDate(Arrangement arrangement){
        java.util.Date in = arrangement.getDate();
        LocalDateTime now = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        try {
            return Arrangement.FORMAT.parse(year + "-" + month + "-" + day + " " + closeTime);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }


    private boolean isBusy(List<Arrangement> busyArrangements, Arrangement arrangementToCheck){
        boolean busy=false;
        if(getOpenTimeInDate(arrangementToCheck).compareTo(arrangementToCheck.getDate()) > 0 && getCloseTimeInDate(arrangementToCheck).compareTo(arrangementToCheck.getDate()) < 0){
            busy = true;
        }
        else {
            for (int i = 0; i < busyArrangements.size() && !busy; i++) {
                if (busyArrangements.get(i).getDate().compareTo(arrangementToCheck.getDate()) == 0) {
                    busy = true;
                }
            }
        }
        return busy;
    }
}