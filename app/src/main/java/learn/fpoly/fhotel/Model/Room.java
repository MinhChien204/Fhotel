package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Room {
    @SerializedName("_id")
    private String id;
    private String name;
    private Double price;
    private Float rating;
    private String description;
    private String image;
    private int capacity;
    private String room_code;
    private String status;
    private List<Service> services;

    // Constructor

    public Room(String status) {
        this.status = status;
    }


    public Room(String id, String name, Double price, Float rating, String description, String image, int capacity, String room_code, String status, List<Service> services) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.description = description;
        this.image = image;
        this.capacity = capacity;
        this.room_code = room_code;
        this.status = status;
        this.services = services;
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
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

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}