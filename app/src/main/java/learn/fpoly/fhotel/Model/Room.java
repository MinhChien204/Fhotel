package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

public class Room {
    @SerializedName("_id")
    private String id;
    private String name;
    private Double price;
    private Double rating;
    private String description;
    private String image;
    private int capacity;
    private String room_code;
    private String status;


    // Constructor

    public Room(String id, String name, Double price, Double rating, String description, String image, int capacity, String room_code, String status) {
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

    // Getters và Setters

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

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomCode() {
        return room_code;
    }

    public void setRoomCode(String room_code) {
        this.room_code = room_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
