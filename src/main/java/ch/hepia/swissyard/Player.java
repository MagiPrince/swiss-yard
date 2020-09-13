package ch.hepia.swissyard;
import ch.hepia.swissyard.communication.*;

import java.io.Serializable;
import java.util.Optional;

import ch.hepia.swissyard.view.Drawable;

public abstract class Player implements Serializable, Drawable {
    private static final long serialVersionUID = 3147744678832131227L;
    private String name;
    private Place place;
    private transient Client c;

    /**
     * Constructor Player
     * 
     * @param name the player name
     * @param stop the starting stop
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Player(String name, Stop stop, String ipBroker){
        this.name = name;
        this.place = stop;

        this.c = new Client(ipBroker);
    }

    /**
     * Returns the name of the player
     * 
     * @return the name of the player
     */
    public String getName(){return this.name;}
    
    /**
     * Returns the place of the player
     * 
     * @return the place of the player
     */
    public Place getPlace(){return this.place;}

    /**
     * Returns the Coordinate of the player
     * 
     * @return the Coordinate of the player
     */
    public Coordinate getCoordinate() throws NonExistantPlaceException {
        try {
            return getPlace().getCoordinate();
        } catch (NonExistantPlaceException e) {
            throw e;
        }
    }

    /**
     * Takes a transport and modifies players place
     * 
     * @throws NonExistantPlaceException if the transport doesn't exist
     */
    public void takeTransport() throws NonExistantPlaceException{
        try{
            if (this.place.getClass().equals(Stop.class) && this.place.isTransportAtTheStop()){
                this.place = this.place.next();
                c.send(new MessageToCom(getClass().getName(), this.place.getClass().getName(), this, this.place));
            }
        }
        catch (NonExistantPlaceException e){ throw e;}
    }

    /**
     * Leaves a transport at a stop and modifies players place
     * 
     * @throws NonExistantPlaceException if the stop doesn't exist
     */    
    public void getOutOfTransport() throws NonExistantPlaceException{
        try {
            if (this.place.getClass().equals(Transport.class) && this.place.isTransportAtTheStop()){
                this.place = this.place.next();
                c.send(new MessageToCom(getClass().getName(), this.place.getClass().getName(), this, this.place));
            }
        }
        catch (NonExistantPlaceException e){ throw e;}
    }

    /**
     * Returns if this catched the thief
     * 
     * @param player the player that could be a thief
     * @return if this catched the thief
     */
    public abstract boolean catchThief(Player player);


    @Override 
    public String toString(){
        return this.getName() + "(" + this.getClass().getName() + ")";
    }

}
