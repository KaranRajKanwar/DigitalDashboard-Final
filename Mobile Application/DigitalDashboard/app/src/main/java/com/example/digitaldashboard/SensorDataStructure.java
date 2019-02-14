package com.example.digitaldashboard;

public class SensorDataStructure {
    private String address;
    private String altitude;
    private String latitude;
    private String longitude;
    private String speed;

    public SensorDataStructure(){

    }
    public SensorDataStructure(String address, String altitude, String latitude, String longitude, String speed){
        this.address = address;
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSpeed(){
        return speed;
    }

    public void setSpeed(String speed){
        this.speed = speed;
    }

}