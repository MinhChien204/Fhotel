package learn.fpoly.fhotel.response;

public class LoginResponse {
    private String messenger;
    private int role;
    private int status;
    private String accessToken;  // Thêm accessToken
    private String refreshToken; // Thêm refreshToken

    public LoginResponse() {
    }

    public LoginResponse(String messenger, int role, int status, String accessToken, String refreshToken) {
        this.messenger = messenger;
        this.role = role;
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
