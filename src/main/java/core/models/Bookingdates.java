package core.models;

public class Bookingdates {
    private String checkin;
    private String checkout;

    // Пустой конструктор для Jackson
    public Bookingdates() {}

    public Bookingdates (String checkin, String checkout){
        this.checkin = checkin;
        this.checkout = checkout;
    }

    // Геттеры и сеттеры
    public String getCheckin() { return checkin; }
    public void setCheckin(String checkin) { this.checkin = checkin; }

    public String getCheckout() { return checkout; }
    public void setCheckout(String checkout) { this.checkout = checkout; }
}
