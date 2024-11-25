package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Room {
    @SerializedName("_id")
    private String id;
    private String name;
    private Double price;
    private Double rating;
    private String description;
    private List<String> image; // Thay đổi kiểu dữ liệu thành List<String>
    private int capacity;
    private String room_code;
    private String status;

    public Room(String id, String name, Double price, Double rating, String description, List<String> image, int capacity, String room_code, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.description = description;
        this.image = image;
        this.capacity = capacity;
        this.room_code = room_code;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoom_code() {
        return room_code;
    }

    public void setRoom_code(String room_code) {
        this.room_code = room_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
