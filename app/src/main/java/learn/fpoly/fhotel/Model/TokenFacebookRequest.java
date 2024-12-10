package learn.fpoly.fhotel.Model;

public class TokenFacebookRequest {
    private String accessToken;

    // Constructor mặc định
    public TokenFacebookRequest() {
    }

    public TokenFacebookRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter và Setter cho accessToken
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }




}
