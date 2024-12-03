package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

public class Room {
    @SerializedName("_id")
    private String id;
    private String name;
    private Double price;
    private Float rating;
    private String description;
    private String image;
    private int capacity;
    private int favouritestatus;
    private String room_code;
    private String status;


    // Constructor

    public Room(String status) {
        this.status = status;
    }

    public Room(int favouritestatus) {
        this.favouritestatus = favouritestatus;
    }

    public Room(String id, String name, Double price, Float rating, String description, String image, int capacity, int favouritestatus, String room_code, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.description = description;
        this.image = image;
        this.capacity = capacity;
        this.favouritestatus = favouritestatus;
        this.room_code = room_code;
        this.status = status;
    }

    // Getters và Setters

    public int getFavouritestatus() {
        return favouritestatus;
    }

    public void setFavouritestatus(int favouritestatus) {
        this.favouritestatus = favouritestatus;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
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
