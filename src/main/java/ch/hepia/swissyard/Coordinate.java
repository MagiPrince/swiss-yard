package ch.hepia.swissyard;

import java.io.Serializable;
import java.util.Optional;
import java.util.Vector;

public class Coordinate implements Serializable{
    private static final long serialVersionUID = -2737477967980563644L;
    private static final double EARTH_RADIUS_CONVERTION = 6377.1008d;
    private String type;
    private double lng;
    private double lat;

    public Coordinate(){
        this.type = "null";
        this.lng = 0.0;
        this.lat = 0.0;
    }

    // TODO: OPTIMIZE: type with an enum instead of a String
    public Coordinate(String type, double longitude, double latitude){
        this.type = type;
        this.lng = longitude;
        this.lat = latitude;
    }

    public String getType(){return this.type;}
    public double getLongitude(){ return this.lng;}
    public double getLatitude(){ return this.lat;}

    /**
     * Converts a angle in degree into radians
     * @param angle the angle in degrees
     * @return the angle in radians
     */
    private static double getRadians(double angle) {
        return angle * Math.PI / 180.0;
    }

    /**
     * Returns the min value for X if the earth was flattened
     * @return the min value for X if the earth was flattened
     */
    public static double getMinCartesianX() {
        return Coordinate.getCartesianCoords(-90,-180).get(0);
    }

    /**
     * Returns the max value for X if the earth was flattened
     * @return the max value for X if the earth was flattened
     */
    public static double getMaxCartesianX() {
        return Coordinate.getCartesianCoords(+90, +180).get(0);
    }

    /**
     * Returns the min value for Y if the earth was flattened
     * @return the min value for Y if the earth was flattened
     */
    public static double getMinCartesianY() {
        return Coordinate.getCartesianCoords(-90,-180).get(0);
    }

    /**
     * Returns the max value for Y if the earth was flattened
     * @return the max value for Y if the earth was flattened
     */
    public static double getMaxCartesianY() {
        return Coordinate.getCartesianCoords(+0, +180).get(0);
    }

    /**
     * Maps a know position into a more conveniable range
     * @param rangeXStart the lower bound of the range of X
     * @param rangeXStop the upper bound of the range of X
     * @param rangeYStart the lower bound of the range of Y
     * @param rangeYStop the upper bound of the range of Y
     * @return a position contained in [rangeXStart, rangeXStop] X [rangeYStart, rangeYStop]
     */
    public Vector<Double> positionMappedToRange(double rangeXStart, double rangeXStop, double rangeYStart, double rangeYStop) {
        Vector<Double> pos = this.getCartesianCoords();
        // Mapping the first component in the range [rangeXStart, rangeXStop]
        pos.set(0, Coordinate.mapRange(getMinCartesianX(),getMaxCartesianX(),rangeXStart,rangeXStop,pos.get(0)));
        // Mapping the second component in the range [rangeYStart, rangeYStop]
        pos.set(1, Coordinate.mapRange(getMinCartesianY(),getMaxCartesianY(),rangeYStart,rangeYStop,pos.get(1)));
        return pos;
    }

    /**
     * Maps a range into another one
     * @param start1 the lower bound of the starting range
     * @param stop1 the upper bound of the starting range
     * @param start2 the lower bound of the final range
     * @param stop2 the upper bound of the final range
     * @param valToMap the value to map from the starting range to the final range
     * @return the value mapped from the starting
     */
    public static double mapRange(double start1, double stop1, double start2, double stop2, double valToMap){
        return start2 + ((valToMap - start1)*(stop2 - start2))/(stop1 - start1);
    }

    /**
     * Returns the cartesian coordinates of the current GPS location
     * @return a position vector with two components (x,y)
     */
    public Vector<Double> getCartesianCoords() {
        return Coordinate.getCartesianCoords(this.lat, this.lng);
    }

    /**
     * Converts a GPS location into a position vector for catersian positionning systems
     * @param lat the latitude of the GPS location
     * @param lng the longitude of the GPS location
     * @return a vector position with two components (x,y)
     */
    public static Vector<Double> getCartesianCoords(double lat, double lng) {
        Vector<Double> pos = new Vector<>();
        double radLat = getRadians(lat);
        double radLng = getRadians(lng);

        // Does conversion for spherical coordinate system into cartesian coordinate system
        // X = R * cos(lng) * cos(lat)
        pos.addElement((EARTH_RADIUS_CONVERTION * Math.cos(radLat) * Math.cos(radLng)));
        // Z = r*sin(lat)
        pos.addElement((EARTH_RADIUS_CONVERTION * Math.sin(radLng)));
        // For 3d
        // Y = R * cos(lng) * sin(lat)

        return pos;
    }

    private static double average(double d1, double d2){
        return d1*d2/2;
    }

    public static Optional<Coordinate> middle(Coordinate c1, Coordinate c2){
        if (c1.getType().equals(c2.getType())){
            return Optional.empty();
        }
        return Optional.of( new Coordinate( c1.getType(), average(c1.getLongitude(),c2.getLongitude()), average(c1.getLatitude(), c2.getLatitude()) ) );
    }

    @Override
    public String toString(){
        return "Coordinate : [Type : " + this.type + ", Longitude : " + this.lng + ", Latitude : " + this.lat + "]";
    }

    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()){
            return false;
        }
        Coordinate coord = (Coordinate) o;
        return coord.getType().equals(this.type) && coord.getLongitude() == this.lng && coord.getLatitude() == this.lat;
    }
}