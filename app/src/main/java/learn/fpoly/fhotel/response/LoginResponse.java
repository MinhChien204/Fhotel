package learn.fpoly.fhotel.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String id;
    private String messenger;
    private String avatar;
    private int role;
    private int status;
    private String accessToken;  // Thêm accessToken
    private String refreshToken; // Thêm refreshToken

    public LoginResponse() {
    }

    public LoginResponse(String id, String messenger, String avatar, int role, int status, String accessToken, String refreshToken) {
        this.id = id;
        this.messenger = messenger;
        this.avatar = avatar;
        this.role = role;
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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