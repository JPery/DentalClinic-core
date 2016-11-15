package es.jpery.dentalClinic.dao;

import es.jpery.dentalClinic.domain.User;
import java.util.List;

/**
 * Created by JPery on 9/11/16.
 */
public interface UserDAO {
    List<User> getUsers();
    User getUserbyID(int id);
    boolean checkUser(User userToCheck);
    boolean addUser(User user);
    boolean updateUser(int userid, User user);
}
