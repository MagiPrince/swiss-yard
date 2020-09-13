package ch.hepia.swissyard;
import ch.hepia.swissyard.communication.*;

import ch.hepia.swissyard.view.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;

import java.util.*;

public class Stop implements Serializable, Place, Drawable {
    private static final long serialVersionUID = 1086693315229829770L;
    private String name;
    private String id;
    private Coordinate coord;
    private Map<Transport, Integer> nextDepartures = new LinkedHashMap<Transport,Integer>();
    private transient Client c;

    private static final int ARRIVALTIMESTAMP = 30;
    private static final int DEPARTURETIMESTAMP = 0;
    private final String LIMIT = "5";
    private boolean isEmpty = true;

    private static Client singleton;
    private static Client getClient() {
        if(singleton == null)
            singleton = new Client("localhost");
        return singleton;
    }

    /**
     * Constructor Stop
     * 
     * @param name the stop name
     * @param id the stop id
     * @param coord the stop coordinate
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Stop(String name, String id, Coordinate coord, String ipBroker){
        this.name = name;
        this.coord = coord;
        this.id = id;
        c = getClient();
    }

    /**
     * Constructor Stop
     * 
     * @param name the stop name
     * @param id the stop id
     * @param coord the stop coordinate
     */
    public Stop(String name, String id, Coordinate coord){
        this(name, id, coord, "localhost");
    }

    /**
     * Constructor Stop (Used for tests)
     * 
     * @param name the stop name
     * @param id the stop id
     * @param coord the stop coordinate
     * @param map the next departures
     */
    public Stop(String name, String id, Coordinate coord, LinkedHashMap<Transport,Integer> map){
        this(name, id, coord, "localhost");
        this.nextDepartures = map;
    } 

    /**
     * Constructor Stop
     */
    public Stop(){
        this("undefined", "undefined", new Coordinate());
    }

    public void update(){
        nextDepartures = ApiParser.getNextDeparture(this.id, LIMIT);
        try {
            isEmpty = isTransportAtTheStop();
        } catch (Exception e) {
            c.send(toString() + " has no departure on the schedule : " + e.getMessage());
        }
    }

    /**
     * Returns the name of the stop
     * 
     * @return the name of the stop
     */
    public String getName(){return this.name;}

    /**
     * Returns the id of the id
     * 
     * @return the id of the id
     */
    public String getId(){return this.id;}

    
    public Coordinate getCoordinate(){
        return new Coordinate(this.coord.getType(), this.coord.getLongitude(), this.coord.getLatitude());
    }


    public boolean isTransportAtTheStop() throws NonExistantPlaceException {
        Place next = next();
        Integer timestamp = this.nextDepartures.get(next);
        return timestamp > DEPARTURETIMESTAMP && timestamp <= ARRIVALTIMESTAMP;
    }

    public void arrived() throws NonExistantPlaceException{
        if(isEmpty && isTransportAtTheStop()){
            isEmpty = false;
            c.send(new MessageToCom(getClass().getName(), Transport.class.getName(), this, next(), MessageType.ARRIVED));
        }
    }

    public void left() throws NonExistantPlaceException {
        if (!(isEmpty || isTransportAtTheStop())){
            Place next = next();
            this.nextDepartures.remove(next);
            c.send(new MessageToCom(getClass().getName(), Transport.class.getName(), this, next(), MessageType.LEFT));
        
            update();
        }
    }

    public void timePassed(Integer time){
        this.nextDepartures.forEach((k,v) -> this.nextDepartures.replace(k, v-time));
    }

    public Place next() throws NonExistantPlaceException{
        if (this.nextDepartures.size()<1){ throw new NonExistantPlaceException();}
        return this.nextDepartures.entrySet().iterator().next().getKey();
    }

    @Override
    public String toString() {
        return this.name + "(Stop)";
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()){
            return false;
        }
        Stop s = (Stop) o;
        return s.getName().equals(this.name) && s.getId().equals(this.id) && s.getCoordinate().equals(this.coord);
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public void draw(GraphicsContext gc, double scale) {
        if (visible()){
            gc.setStroke(Color.GREEN);
            double maxWidth = gc.getCanvas().getWidth();
            double maxHeight = gc.getCanvas().getHeight();
            double x = this.coord.getLatitude() * 1000 % maxWidth;
            double y = this.coord.getLongitude() * 1000 % maxHeight;
            gc.strokeOval(x, y, 5, 5);

            // Constraining the text position to the canvas frame
//            double textPositionX = (position.get(0) + 20 < gc.getCanvas().getWidth()) ? position.get(0) + 3 : position.get(0) - (this.name.length() * 3);
//            gc.strokeText(this.name, textPositionX, position.get(1));
        }
    }
}