package learn.fpoly.fhotel.Model;

public class TypeRoom {
    private int _id;
    private String imageRoom, name;

    public TypeRoom() {
    }

    public TypeRoom(int _id, String imageRoom, String name) {
        this._id = _id;
        this.imageRoom = imageRoom;
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getImageRoom() {
        return imageRoom;
    }

    public void setImageRoom(String imageRoom) {
        this.imageRoom = imageRoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
