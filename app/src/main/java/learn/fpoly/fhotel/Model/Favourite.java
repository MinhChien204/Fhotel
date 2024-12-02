package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

public class Favourite {
    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;


    @SerializedName("roomId")
    private String roomId;

    public Favourite() {
    }

    public Favourite(String id) {
        this.id = id;
    }

    public Favourite(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public Favourite(String id, String userId, String roomId) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
    }

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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
