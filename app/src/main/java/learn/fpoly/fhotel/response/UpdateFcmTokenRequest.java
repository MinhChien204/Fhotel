package learn.fpoly.fhotel.response;

public class UpdateFcmTokenRequest {
    private String userId;
    private String fcmToken;

    public UpdateFcmTokenRequest(String userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
