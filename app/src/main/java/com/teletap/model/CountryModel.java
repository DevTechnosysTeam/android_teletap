package com.teletap.model;

import androidx.annotation.NonNull;

public class CountryModel {

    private String CountryName, country_code ;

    public CountryModel(String countryName, String country_code) {
        CountryName = countryName;
        this.country_code = country_code;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }


    @NonNull
    @Override
    public String toString() {
        return CountryName;
    }
}
