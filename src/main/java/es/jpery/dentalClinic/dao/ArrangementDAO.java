package es.jpery.dentalClinic.dao;

import es.jpery.dentalClinic.domain.Arrangement;

import java.util.List;

/**
 * Created by JPery on 9/11/16.
 */
public interface ArrangementDAO {
    List<Arrangement> getArrangements();
    List<Arrangement> getArrangementsbyUserID(int userID);
    Arrangement getArrangementbyID(int id);
    boolean addArrangement(Arrangement arrangement);
    boolean updateArrangement(int arrangementid, Arrangement arrangement);
    List<Arrangement> getAvailableArrangementsByArrangement(Arrangement arrangement);
}
