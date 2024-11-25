package learn.fpoly.fhotel.Model;


import java.util.List; // Import để sử dụng List

public class Service {
    private String _id;
    private String name;
    private String description;
    private Double price;
    private List<String> image; // Thay đổi từ String sang List<String> để chứa nhiều ảnh

    public Service(String _id, String name, String description, Double price, List<String> image) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public Service() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }
}
