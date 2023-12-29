package dk.lyngby.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.lyngby.TokenFactory;
import dk.lyngby.dto.UserDto;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;

import java.util.Map;

public class ApplicationUtils {

    public static UserDto verifyToken(String token, String user, String role) throws TokenException {
        Gson gson = new GsonBuilder().create();
        String extractedUser = TokenFactory.verifyToken(token, "841D8A6C80CBA4FCAD32D5367C18C53B", getClaimBuilder(user, role));
        return gson.fromJson(extractedUser, UserDto.class);
    }

    public static ClaimBuilder getClaimBuilder(String username, String role) {
        return ClaimBuilder.builder()
                .issuer("cphbusiness")
                .audience("cphbusiness")
                .claimSet(Map.of("username", username, "role", role))
                .expirationTime(Long.parseLong("3600000"))
                .issueTime(3600000L)
                .build();
    }
}
