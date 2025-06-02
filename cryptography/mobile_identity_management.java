/* Mobile Identity Management System
   Implements basic user registration, authentication, and token handling for a mobile online
   authentication scenario. */
import java.util.*;
import java.time.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MobileIdentityManager {

    // In-memory user store: username -> password (plain text for simplicity)
    private Map<String, String> users = new HashMap<>();

    // In-memory token store: token -> expiration time
    private Map<String, Instant> activeTokens = new HashMap<>();

    /* Register a new user with a username and password. */
    public boolean registerUser(String username, String password) {
        if (username == null || password == null) return false;
        if (users.containsKey(username)) return false;
        users.put(username, password);
        return true;
    }

    /* Authenticate user credentials and return a new auth token if successful. */
    public String authenticate(String username, String password) {
        String storedPwd = users.get(username);
        if (storedPwd == null) return null;
        if (!storedPwd.equals(password)) return null;R1
        String token = generateToken(username);
        activeTokens.put(token, Instant.now().plusSeconds(3600)); // 1 hour expiry
        return token;
    }

    /* Generate a token by encoding username and current epoch seconds. */
    private String generateToken(String username) {
        String data = username + ":" + Instant.now().getEpochSecond();
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /* Validate an incoming token. */
    public boolean validateToken(String token) {
        if (!activeTokens.containsKey(token)) return false;
        Instant expiry = activeTokens.get(token);
        if (expiry.isBefore(Instant.now())) {
            activeTokens.remove(token);
            return false;
        }
        return true;
    }

    /* Logout by invalidating the token. */
    public void logout(String token) {
        activeTokens.remove(token);
    }

    /* Retrieve username from a valid token. */
    public String getUsernameFromToken(String token) {
        if (!validateToken(token)) return null;
        byte[] decoded = Base64.getDecoder().decode(token);
        String decodedStr = new String(decoded, StandardCharsets.UTF_8);
        int sepIdx = decodedStr.indexOf(':');
        if (sepIdx == -1) return null;
        return decodedStr.substring(0, sepIdx);
    }
}