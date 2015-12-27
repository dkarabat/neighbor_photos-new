package info.fandroid.navdrawer.util;

import android.app.Application;

import java.util.Date;

/**
 * Created by Dmitriy on 12/27/2015.
 */
public class LocationParams extends Application {

    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private Integer radius = 5000;
    private Long fromDate;
    private Long toDate;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Long getFromDate() {
        return fromDate = System.currentTimeMillis() / 1000L;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate = System.currentTimeMillis() / 1000L;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }
}
