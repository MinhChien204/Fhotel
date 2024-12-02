package learn.fpoly.fhotel.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String id;
    private String messenger;
    private int role;
    private int status;
    private String accessToken;
    private String refreshToken;

    // Thêm các trường mới
    private String name;
    private String email;
    private String profileImage;

    public LoginResponse() {
    }

    public LoginResponse(String id, String messenger, int role, int status, String accessToken, String refreshToken, String name, String email, String profileImage) {
        this.id = id;
        this.messenger = messenger;
        this.role = role;
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    // Getter và Setter cho các trường mới
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // Các getter và setter khác
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
