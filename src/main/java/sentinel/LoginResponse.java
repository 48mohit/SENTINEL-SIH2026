package sentinel;

public class LoginResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String fullName;
    private String role;
    private String message;

    public LoginResponse(String token, String refreshToken, String username,
            String fullName, String role, String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
    }

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}