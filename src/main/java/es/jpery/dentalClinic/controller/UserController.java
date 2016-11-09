package es.jpery.dentalClinic.controller;

import es.jpery.dentalClinic.dao.UserDAO;
import es.jpery.dentalClinic.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Edited by JPery on 03/11/16
 */

@RestController
@RequestMapping("/users")
public class UserController {

    @CrossOrigin
    @RequestMapping(value = "/{userid}")
    public User getUserById(@PathVariable int userid)
    {
        UserDAO userDAO = new UserDAO();
        userDAO.connect();
        return new User(userid,"foo");
    }

    @CrossOrigin
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> checkUsernameAndPassword(@RequestBody User user) {
        if (user.getName().equals("foo") && user.getPassword().equals("foo")) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}
