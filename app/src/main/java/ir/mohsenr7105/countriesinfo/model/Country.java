package ir.mohsenr7105.countriesinfo.model;

import java.util.Locale;

public class Country {
    private static final String LOG = Country.class.getSimpleName();

    private String name, farsiName, nativeName, alpha2Code, alpha3Code, capitalNativeName,
        callingCodes, region, timeZones, currencies, languages;
    private Long population, area;

    public Country(){

    }

    public Country(String name, String farsiName, String alpha2Code){
        this.name = name;
        this.farsiName = farsiName;
        this.alpha2Code = alpha2Code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFarsiName(String farsiName) {
        this.farsiName = farsiName;
    }

    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public void setCapitalNativeName(String capitalNativeName) {
        this.capitalNativeName = capitalNativeName;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public void setCallingCodes(String callingCodes) {
        this.callingCodes = callingCodes;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setTimeZones(String timeZones) {
        this.timeZones = timeZones;
    }

    public String getName() {
        return name;
    }

    public String getFarsiName() {
        return farsiName;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getCapitalNativeName() {
        return capitalNativeName;
    }

    public Long getPopulation() {
        return population;
    }

    public Long getArea() {
        return area;
    }

    public String getCallingCodes() {
        return callingCodes;
    }

    public String getCurrencies() {
        return currencies;
    }

    public String getLanguages() {
        return languages;
    }

    public String getRegion() {
        return region;
    }

    public String getTimeZones() {
        return timeZones;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"%1$s %2$s %3$s %4$s %5$s %6$s %7$d %8$d",
                getName(), getNativeName(), getFarsiName(), getAlpha2Code(), getAlpha3Code(),
                getCapitalNativeName(), getPopulation(), getArea());
    }
}
