package engine.entity;

import javax.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String email;

    private String password;

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}
