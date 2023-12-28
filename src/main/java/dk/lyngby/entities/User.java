package dk.lyngby.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.mindrot.jbcrypt.BCrypt;

@Getter
@Setter
@NoArgsConstructor
public class User {

    private int id;
    private String username;
    private String password;
    private Role role;

    public User(String username) {
        this.username = username;
    }
}
