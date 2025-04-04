package core.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GetBookingRQ {
    private String firstname;
    private String lastname;
    private String checkin;
    private String checkout;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }
    public GetBookingRQ() {
    } // Поля остаются с значениями по умолчанию

    public GetBookingRQ(String firstname, String lastname, String checkout,
                        String checkin) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.checkout = checkout;
        this.checkin = checkin;
    }
}

