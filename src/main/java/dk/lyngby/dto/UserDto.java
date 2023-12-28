package dk.lyngby.dto;

import lombok.Getter;

@Getter
public class UserDto {

    private String username;
    private String role;

    public UserDto() {
    }

    public UserDto(String username, String role) {
        this.username = username;
        this.role = role;
    }


}
