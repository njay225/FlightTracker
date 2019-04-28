package com.min.nisal.FlightTracker;

class Flight {

    private String origin;
    private String destination;
    private String time;
    private String flightNo;
    private String reference;
    private String user;
    private Long date;


    Flight(String origin, String destination, String time, Long date, String flightNo, String reference, String user){
        this.destination = destination;
        this.flightNo = flightNo;
        this.origin = origin;
        this.reference = reference;
        this.time = time;
        this.user = user;
        this.date = date;
    }


    public String getTime() {
        return time;
    }

    public String getDestination() {
        return destination;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public String getOrigin() {
        return origin;
    }

    public String getReference() {
        return reference;
    }

    public String getUser() {
        return user;
    }

    public Long getDate() {
        return date;
    }
}
