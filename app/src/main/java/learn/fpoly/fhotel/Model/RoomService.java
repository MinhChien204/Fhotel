package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

public class RoomService {
    @SerializedName("_id")
    private String id;

    @SerializedName("roomId")
    private String roomId;

    @SerializedName("serviceId")
    private String serviceId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private Double price;

    @SerializedName("image")
    private String image;

    public RoomService() {
    }

    public RoomService(String id, String roomId, String serviceId, String name, String description, Double price, String image) {
        this.id = id;
        this.roomId = roomId;
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
