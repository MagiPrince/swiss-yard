package ch.hepia.swissyard;

import java.io.Serializable;
import java.util.Optional;

public interface Place extends Serializable {
    
    /**
     * Returns the Optional describing the next Place on the schedule
     * 
     * @return the Optional describing the next Place on the schedule
     */
    public Place next() throws NonExistantPlaceException;
    
    /**
     * Updates the schedule using the Api 
     */
    public void update();
    
    /**
     * Makes time pass on the schedule
     * 
     * @param time the time that passed since last modification
     */
    public void timePassed(Integer time);
    
    /**
     * Returns if the transport is at the stop
     * 
     * @throws NonExistantPlaceException if the schedule is empty
     * @return if the transport is at the stop
     */
    public boolean isTransportAtTheStop() throws NonExistantPlaceException;
    
    /**
     * Checks if the transport arrived and do corresponding actions :
     * send message or/and update schedule
     * 
     * @throws NonExistantPlaceException if the schedule is empty
     */
    public void arrived() throws NonExistantPlaceException;
    
    /**
     * Checks if the transport left and do corresponding actions :
     * send message or/and update schedule
     * 
     * @throws NonExistantPlaceException if the schedule is empty
     */
    public void left() throws NonExistantPlaceException;
    
    /**
     * Returns the coordinates of this place
     * 
     * @throws NonExistantPlaceException if the schedule is empty
     * @return the coordinates of this place
     */
    public Coordinate getCoordinate() throws NonExistantPlaceException;
}