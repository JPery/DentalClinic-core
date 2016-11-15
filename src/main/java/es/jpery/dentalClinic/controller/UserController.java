package es.jpery.dentalClinic.controller;

import es.jpery.dentalClinic.dao.UserDAO;
import es.jpery.dentalClinic.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserDAO userDAO;

    @CrossOrigin
    @RequestMapping(value = "/{userid}")
    public User getUserById(@PathVariable int userid) {
        return userDAO.getUserbyID(userid);
    }

    @CrossOrigin
    @RequestMapping(value = "/")
    public List<User> getUsers() {
        return userDAO.getUsers();
    }

    @CrossOrigin
    @PostMapping(value = "/login")
    public ResponseEntity<User> checkUsernameAndPassword(@RequestBody User user) {
        if (userDAO.checkUser(user)) {
            return new ResponseEntity(user, HttpStatus.OK);
        } else {
            return new ResponseEntity(user, HttpStatus.UNAUTHORIZED);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (userDAO.addUser(user)) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PutMapping(value = "/{userid}")
    public ResponseEntity<User> updateUser(@PathVariable int userid, @RequestBody User user) {
        if (userDAO.updateUser(userid, user)) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}