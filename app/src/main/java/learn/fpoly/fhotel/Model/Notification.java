package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("_id")
    private String id; // ID thông báo
    private String userId; // ID của người dùng nhận thông báo
    private String message; // Nội dung thông báo
    private String type; // Loại thông báo (e.g., password_change, booking_confirmed, booking_canceled, voucher_received)
    private String createdAt; // Thời gian thông báo được tạo

    public Notification(String id, String userId, String message, String type, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Notification() {
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

