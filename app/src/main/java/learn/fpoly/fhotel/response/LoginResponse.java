package learn.fpoly.fhotel.response;

public class LoginResponse {
    private  String messenger;
    private  int role;
    private  int status;

    public LoginResponse() {
    }

    public LoginResponse(String messenger, int role, int status) {
        this.messenger = messenger;
        this.role = role;
        this.status = status;
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
}
