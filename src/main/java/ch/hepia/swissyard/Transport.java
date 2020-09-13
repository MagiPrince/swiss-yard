package ch.hepia.swissyard;
import ch.hepia.swissyard.communication.*;
import ch.hepia.swissyard.view.Drawable;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;
import java.io.Serializable;

public class Transport implements Serializable, Place {
    private static final long serialVersionUID = 7968568739148278729L;
    private static final int ARRIVALTIMESTAMP = 30;
    private static final int DEPARTURETIMESTAMP = 0;
    private String id;
    private String category;
    private String number;
    private Map<Stop, Integer> passLine;
    private transient Client c;

    private boolean moving = true;

    /**
     * Constructor Transport
     * 
     * @param id the transport id
     * @param category the transport category
     * @param number the transport number
     * @param passLine the transport passLine
     */
    public Transport(String id, String category, String number, LinkedHashMap<Stop, Integer> passLine){
        this.id = id;
        this.category = category;
        this.number = number;
        this.passLine = passLine;
        c = new Client("localhost");
    }

    /**
     * Constructor Transport
     * 
     * @param id the transport id
     * @param passLine the transport passLine
     */
     public Transport(String id, LinkedHashMap<Stop, Integer> passLine){
        this.id = id;
        this.passLine = passLine;
        c = new Client("localhost");
    }


    public Coordinate getCoordinate() throws NonExistantPlaceException{
        try{
            if (isTransportAtTheStop() || getStop(2).isEmpty()){
                return next().getCoordinate();
            }
            return Coordinate.middle(next().getCoordinate(), getStop(2).get().getCoordinate()).get();
        } catch (NonExistantPlaceException e) {
            throw e;
        }
    }

    public Optional<Integer> getNextDepartureTime(){
        Optional<Stop> s = Optional.of(this.passLine.keySet().iterator().next());
        return Optional.of(this.passLine.get(s.get()));
    }

    public void update(){
        ApiParser.updatePassLine(this);
        try {
            moving = isTransportAtTheStop();
        } catch (Exception e) {
            c.send(toString() + " has no Stop on the passLine : " + e.getMessage());
        }
    }

    public void left() throws NonExistantPlaceException{
        if (!(moving || isTransportAtTheStop())){
            moving = true;
            c.send(new MessageToCom(getClass().getName(), Stop.class.getName(), this, next(), MessageType.LEFT));    
        }
    }

    public void arrived() throws NonExistantPlaceException {
        if (moving && isTransportAtTheStop()){
            moving = false;
            Place next = next();
            this.passLine.remove(next);
            if (next.equals(getTerminus().get())){
                c.send(new MessageToCom(getClass().getName(), Stop.class.getName(), this, next(), MessageType.TERMINUS));
            }
            c.send(new MessageToCom(getClass().getName(), Stop.class.getName(), this, next(), MessageType.ARRIVED));
        }
    }

    /**
     * Returns an Optional describing the terminus stop of the line
     * 
     * @return an Optional describing the terminus stop of the line
     */
    private Optional<Stop> getTerminus(){
        return getStop(this.passLine.size());
    }

    public void timePassed(Integer time){
        this.passLine.forEach((k,v) -> this.passLine.replace(k, v-time));
    }
    
    public boolean isTransportAtTheStop() throws NonExistantPlaceException {
        Place next = next();
        Integer timestamp = this.passLine.get(next);
        if (timestamp <= 0){
            if (getStop(2).isPresent()){
                next = getStop(2).get();
                timestamp = this.passLine.get(next);
                return timestamp > DEPARTURETIMESTAMP && timestamp <= ARRIVALTIMESTAMP;
            }
            else {
                throw new NonExistantPlaceException();
            }
        }
        return timestamp > DEPARTURETIMESTAMP && timestamp <= ARRIVALTIMESTAMP;
    }

    /**
     * Returns an Optional describing the ith stop of the passLine
     * 
     * @return an Optional describing the ith stop of the passLine
     */
    private Optional<Stop> getStop(int i){
        int nb = 0;
        Stop s = new Stop();
        Iterator<Map.Entry<Stop,Integer>> it = this.passLine.entrySet().iterator();
        while (it.hasNext() && nb < i){
            s = it.next().getKey();
            nb++;
        }
        if (nb != i || nb == 0){return Optional.empty();}
        return Optional.ofNullable(s);
    }

    
    public Place next() throws NonExistantPlaceException{
        Optional<Stop> s = getStop(1);
        if (s.isEmpty()){
            return s.get();
        }
        throw new NonExistantPlaceException();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()){
            return false;
        }
        Transport t = (Transport) o;
        return t.id.equals(this.id) && t.category.equals(this.category) && t.number.equals(this.number);
    }

    public String toString(){
        return this.category + " " + this.number;
    }
}