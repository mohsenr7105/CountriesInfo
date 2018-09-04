package ir.mohsenr7105.countriesinfo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class Country {
    private static final String LOG = Country.class.getSimpleName();

    private String name, farsiName, alpha2Code;

    Country(String name, String farsiName, String alpha2Code){
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

    public String getName() {
        return name;
    }

    public String getFarsiName() {
        return farsiName;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }
}
