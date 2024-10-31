package learn.fpoly.fhotel.Model;

public class Hotel {
    private int idHotel;
    private int imgHotel;
    private String nameHotel;
    private String locationHotel;
    private double priceHotel;
    private double rateHotel;

    public Hotel() {
    }

    public Hotel(int idHotel, int imgHotel, String nameHotel, String locationHotel, double priceHotel, double rateHotel) {
        this.idHotel = idHotel;
        this.imgHotel = imgHotel;
        this.nameHotel = nameHotel;
        this.locationHotel = locationHotel;
        this.priceHotel = priceHotel;
        this.rateHotel = rateHotel;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public int getImgHotel() {
        return imgHotel;
    }

    public void setImgHotel(int imgHotel) {
        this.imgHotel = imgHotel;
    }

    public String getNameHotel() {
        return nameHotel;
    }

    public void setNameHotel(String nameHotel) {
        this.nameHotel = nameHotel;
    }

    public String getLocationHotel() {
        return locationHotel;
    }

    public void setLocationHotel(String locationHotel) {
        this.locationHotel = locationHotel;
    }

    public double getPriceHotel() {
        return priceHotel;
    }

    public void setPriceHotel(double priceHotel) {
        this.priceHotel = priceHotel;
    }

    public double getRateHotel() {
        return rateHotel;
    }

    public void setRateHotel(double rateHotel) {
        this.rateHotel = rateHotel;
    }
}
