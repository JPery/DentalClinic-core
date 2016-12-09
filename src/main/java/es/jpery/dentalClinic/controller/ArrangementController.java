package es.jpery.dentalClinic.controller;

import es.jpery.dentalClinic.dao.ArrangementDAO;
import es.jpery.dentalClinic.domain.Arrangement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by JPery on 9/11/16
 */
@RestController
@RequestMapping("/arrangements")
public class ArrangementController {

    @Autowired
    private ArrangementDAO arrangementDAO;

    @CrossOrigin
    @RequestMapping(value = "/{arrangementid}")
    public Arrangement getUserById(@PathVariable int arrangementid) {
        return arrangementDAO.getArrangementbyID(arrangementid);
    }

    @CrossOrigin
    @RequestMapping(value = "/")
    public List<Arrangement> getUsers() {
        return arrangementDAO.getArrangements();
    }

    @CrossOrigin
    @RequestMapping(value = "/user={ownerid}")
    public ResponseEntity<List<Arrangement>> getArrangementsbyUserID(@PathVariable int ownerid){
        return new ResponseEntity(arrangementDAO.getArrangementsbyUserID(ownerid),HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping(value = "/")
    public ResponseEntity<Arrangement> addArrangement(@RequestBody Arrangement arrangement) {
        if (arrangementDAO.addArrangement(arrangement)) {
            return new ResponseEntity(arrangement, HttpStatus.CREATED);
        } else {
            return new ResponseEntity(arrangement, HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PutMapping(value = "/{arrangementid}")
    public ResponseEntity<Arrangement> updateArrangement(@PathVariable int arrangementid, @RequestBody Arrangement arrangement) {
        if (arrangementDAO.updateArrangement(arrangementid, arrangement)) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/date={date}")
    public ResponseEntity<List<Arrangement>> getAvailableArrangementsByArrangement(@PathVariable String date){
        try {
            return new ResponseEntity(arrangementDAO.getAvailableArrangementsByArrangement(new Arrangement(-1, -1, Arrangement.FORMAT.parse(date), -1,"")), HttpStatus.OK);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    @CrossOrigin
    @DeleteMapping(value = "/{arrangementid}")
    public ResponseEntity<?> deleteArrangement(@PathVariable int arrangementid) {
        if (arrangementDAO.deleteArrangement(arrangementid)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}