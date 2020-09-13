package ch.hepia.swissyard.communication;

import java.io.Serializable;
import java.util.Optional;

import ch.hepia.swissyard.Place;
import ch.hepia.swissyard.Player;
import ch.hepia.swissyard.Transport;
import ch.hepia.swissyard.Stop;
import ch.hepia.swissyard.Thief;



public class MessageToCom implements Serializable{
    private static final long serialVersionUID = 6831498629975041136L;
    private static final String UNKNOWN = "Message unknown";

    private String auteurClass;
    private String messageClass;
    private Player player;
    private Place place;
    private Stop stop;
    private Transport transport;
    private MessageType type;

    /**
     * Constructor MessageToCom
     * @param auteurClass the message author className
     * @param messageClass the subject className
     * @param player the player who sent the message
     * @param place the subject place
     */
    public MessageToCom(String auteurClass, String messageClass, Player player, Place place){
        this.auteurClass = auteurClass;
        this.messageClass = messageClass;
        this.player = player;
        this.place = place;
    }

    /**
     * Constructor MessageToCom
     * @param auteurClass the message author className
     * @param messageClass the subject className
     * @param transport the transport who sent the message
     * @param stop the subject stop
     * @param type the type of the message
     */
    public MessageToCom(String auteurClass, String messageClass, Transport transport, Place stop, MessageType type){
        this.auteurClass = auteurClass;
        this.messageClass = messageClass;
        this.place = transport;
        this.stop = (Stop) stop;
        this.type = type;
    }

    /**
     * Constructor MessageToCom
     * @param auteurClass the message author className
     * @param messageClass the subject className
     * @param stop the stop who sent the message
     * @param transport the subject transport
     * @param type the type of the message
     */
    public MessageToCom(String auteurClass, String messageClass, Stop stop, Place transport, MessageType type){
        this.auteurClass = auteurClass;
        this.messageClass = messageClass;
        this.place = stop;
        this.transport = (Transport) transport;
        this.type = type;
    }

    /**
     * Returns the author className
     * @return the author className
     */
    public String getAuteurClass(){
        return this.auteurClass;
    }

    /**
     * Returns the subject className
     * @return the subject className
     */
    public String getMessageClass(){
        return this.messageClass;
    }

    /**
     * Returns an Optional describing a player if defined or an empty Optional
     * @return an Optional describing a player
     */
    public Optional<Player> safeGetPlayer(){
        return Optional.ofNullable(this.player);
    }

    /**
     * Returns an Optional describing a place if defined or an empty Optional
     * @return an Optional describing a place
     */
    public Optional<Place> safeGetPlace(){
        return Optional.ofNullable(this.place);
    }

    /**
     * Returns an Optional describing a message type if defined or an empty Optional
     * @return an Optional describing a message type
     */
    public Optional<MessageType> safeGetType(){
        return Optional.ofNullable(this.type);
    }

    /**
     * Returns an Optional describing a transport if defined or an empty Optional
     * @return an Optional describing a transport
     */
    private Optional<Place> safeGetTransport(){
        return Optional.ofNullable(this.transport);
    }

    /**
     * Returns an Optional describing a stop if defined or an empty Optional
     * @return an Optional describing a stop
     */
    private Optional<Place> safeGetStop(){
        return Optional.ofNullable(this.stop);
    }

    /**
     * Returns a String representation of the event sent by a Place 
     * @param transport the Optional transport concerned by the message
     * @param stop the Optional stop concerned by the message
     * @return a String representation of the event
     */
    private String placeMessageToString(Optional<Place> transport, Optional<Place> stop){

        if (transport.isEmpty() || stop.isEmpty()){
            return UNKNOWN;
        }

        if (this.type.equals(MessageType.LEFT)){
            return transport.get() + " left " + stop.get();
        }
        else if (this.type.equals(MessageType.ARRIVED)) {
            return transport.get() + " arrived  at " + stop.get();
        }
        else if (this.type.equals(MessageType.TERMINUS)){
            return transport.get() + " is at his terminus " + stop.get();
        }

        return UNKNOWN;
    }

    /**
     * Returns a String representation of the event 
     * @return a String representation of the event
     */
    private String messageToString(){        
        if (auteurClass.equals(Transport.class.getName()))
            return placeMessageToString(safeGetPlace(), safeGetStop());

        if (auteurClass.equals(Stop.class.getName()))
            return placeMessageToString(safeGetTransport(), safeGetPlace());
        
        if (safeGetPlayer().isEmpty() || safeGetPlace().isEmpty()){
            return UNKNOWN;        
        }

        if (messageClass.equals(Transport.class.getName()))
            return this.player + " took the " + this.place;
        if (messageClass.equals(Stop.class.getName()))
            return this.player + " got out of the transport at " + this.place;

        return UNKNOWN;
    }
    
    @Override
    public String toString(){
        return "Event : " + messageToString();
    }

}
