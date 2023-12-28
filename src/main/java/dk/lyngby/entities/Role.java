package dk.lyngby.entities;

import io.javalin.security.RouteRole;
import lombok.Getter;
import lombok.ToString;

@Getter
public class Role {

    private RoleName roleName;

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    public enum RoleName implements RouteRole {
        ANYONE,
        USER,
        ADMIN
    }
}
