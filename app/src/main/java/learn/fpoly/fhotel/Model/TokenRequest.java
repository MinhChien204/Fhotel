package learn.fpoly.fhotel.Model;

public class TokenRequest {
    private String idToken;

    public TokenRequest(String idToken) {
        this.idToken = idToken;
    }

    public TokenRequest() {
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
