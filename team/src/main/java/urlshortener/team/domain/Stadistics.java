package urlshortener.team.domain;

public class Stadistics {

    private String city;
    private String country;

    public Stadistics(String country, String city) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return  "{ city:" + city + "," +
                "country:" + country + "}";
    }


}
