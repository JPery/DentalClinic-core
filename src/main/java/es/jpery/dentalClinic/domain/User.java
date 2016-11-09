package es.jpery.dentalClinic.domain;

/**
 * Edited by JPery on 03/11/16
 */
public class User {

    Integer id;
    String name;
    String password;

    public User(){}

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.password = "";
    }

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPassword() { return password; }
}
