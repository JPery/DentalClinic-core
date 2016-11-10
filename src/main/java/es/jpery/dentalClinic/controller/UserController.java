package es.jpery.dentalClinic.controller;

import es.jpery.dentalClinic.dao.UserDAO;
import es.jpery.dentalClinic.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by JPery on 9/11/16
 */

@RestController
@RequestMapping("/users")
public class UserController {
    private static UserDAO userDAO = null;

    @CrossOrigin
    @RequestMapping(value = "/{userid}")
    public User getUserById(@PathVariable int userid) {
        if (userDAO == null)
            userDAO = new UserDAO();
        return userDAO.getUserbyID(userid);
    }

    @CrossOrigin
    @RequestMapping(value = "/")
    public List<User> getUsers() {
        if (userDAO == null)
            userDAO = new UserDAO();
        return userDAO.getUsers();
    }

    @CrossOrigin
    @PostMapping(value = "/login")
    public ResponseEntity<User> checkUsernameAndPassword(@RequestBody User user) {
        if (userDAO == null)
            userDAO = new UserDAO();
        if (userDAO.checkUser(user)) {
            return new ResponseEntity(user, HttpStatus.OK);
        } else {
            return new ResponseEntity(user, HttpStatus.UNAUTHORIZED);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (userDAO == null)
            userDAO = new UserDAO();
        if (userDAO.addUser(user)) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


    @CrossOrigin
    @PutMapping(value = "/{userid}")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (userDAO == null)
            userDAO = new UserDAO();
        if (userDAO.updateUser(user)) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}