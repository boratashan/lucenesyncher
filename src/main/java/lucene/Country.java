package lucene;

public class Country {
    private String id;
    private String CountryName;
    private String CountryCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }


    public Country(String id, String countryName, String countryCode) {
        this.id = id;
        CountryName = countryName;
        CountryCode = countryCode;
    }
}
