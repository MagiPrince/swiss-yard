package ch.hepia.swissyard;

import java.util.*;
import java.util.stream.Collectors;

import ch.hepia.swissyard.communication.*;
import ch.hepia.swissyard.view.View;
import javafx.stage.Stage;

public class Controller {
    private Random rand = new Random();

    private List<Stop> stopList;
    private List<Player> players = new ArrayList<>();
    private Map<Player, View> playerViews= new HashMap<>();
    private boolean playing;
    private Class<? extends Player> winner = Thief.class;
    private Broker broker;

    private final long MAXDURATION = 3600L;
    private final int MAXPLAYERS = 8;
    private final int MINPLAYERS = 1;

    private View view;

    /**
     * Constructor Controller
     * 
     * @param v the view
     */
    public Controller(View v) {
        this.view = v;
        this.broker = new Broker("localhost");
        this.stopList = getStopList();
        // TODO: uncomment the line below this when the timestamp bug has a small chance to be raised like in day time to check if the game can run
//         this.stopList.forEach(stop -> stop.update());
    }

    /**
     * Returns the whole list of stops from Api
     * 
     * @return the stopList
     */
    public List<Stop> getStopList() {
        return ApiParser.getStations();
    }

    /**
     * Starts the game changing status playing
     */
    public void startGame() {
        this.playing = true;
    }

    /**
     * Create players
     * 
     * @param nb the number of players to create
     * @throws NonExistantPlaceException if there is not enough known stops
     * @throws IllegalArgumentException if nb is under minimum or above maximum players allowed 
     */
    public void createPlayers(int nb) throws NonExistantPlaceException,IllegalArgumentException {
        if (nb < MINPLAYERS || nb > MAXPLAYERS){
            throw new IllegalArgumentException();
        }
        if (stopList.size() <= nb){
            throw new NonExistantPlaceException();
        }
        this.players.add(new Thief(randomUnusedStop()));
        for (int i = 1; i < nb; i++) {
            this.players.add(new Policeman("Policeman" + i, randomUnusedStop()));
        }
    }

    /**
     * Returns a random unused stop
     * 
     * @return the stop
     */
    private Stop randomUnusedStop() {
        Stop stop;
        do {
            stop = stopList.get(rand.nextInt(stopList.size()));
        } while (playersAtPlace(stop).size() > 0);
        return stop;
    }

    /**
     * Returns the number of players
     * 
     * @return the number of players
     */
    public int nbPlayers() {
        return this.players.size();
    }

    /**
     * Makes a stop visible for a player
     * 
     * @param stop the stop on which the player clicked
     * @param player the player who wants details about the stop
     */
    public void clicked(Stop stop, Player player) {
        stop.update();
        showDetailsPlace(stop);
    }

    /**
     * Makes a transport visible for a player
     * 
     * @param transport the transport on which the player clicked
     * @param player the player who wants details about the transport
     */
    public void clicked(Transport transport, Player player) {
        showDetailsPlace(transport);
    }


    /**
     * Makes a player move from his Place
     * 
     * @param player the player who wants to move
     */
    public void clicked(Player player){
        try{    
            Place next = player.getPlace().next();
            if (next.getClass().equals(Stop.class)){
                leave(player, next);
            }
            else{
                take(player, next);
            }
        } catch (NonExistantPlaceException e){System.err.println(e.getMessage());}
    }


    /**
     * Makes a player get out of his transport at a stop
     * 
     * @param player the player who wants to go down
     * @param stop the stop where the player stops
     */
    private void leave(Player player, Place stop){
        stop.update();
        try {
            player.getOutOfTransport();
            log("player " + player.getName() + " has unboarded ");
        } catch (NonExistantPlaceException e) {
            log("Error while trying to get out of transport" + e.getMessage());
        }
    }

    /**
     * Makes a player get take the transport at his stop
     * 
     * @param player the player who wants to get into a transport
     * @param transport the transport to take
     */
    private void take(Player player, Place transport) {
        transport.update();
        try {
            player.takeTransport();
            log("player " + player.getName() + " has boarded");
        } catch (NonExistantPlaceException e){
            log("Error while trying to take transport" + e.getMessage());
        }
    }

    /**
     * Returns the List of players at a specific place
     * 
     * @param place the place where players could be
     * @return all the players at that place
     */
    private List<Player> playersAtPlace(Place place){
        return this.players.stream().filter(p-> p.getPlace().equals(place))
                                    .collect(Collectors.toList());
    }

    /**
     * Remove opportunity for players at a place to move
     * 
     * @param place the closed place
     */
    private void closeDoors(Place place){
        playersAtPlace(place).forEach(p -> {
            view.closeDoors(p);
        });
    }

    /**
     * Give opportunity for players at a place to move
     * 
     * @param place the opened place
     */
    private void openDoors(Place place){
        playersAtPlace(place).forEach(p -> {
            view.openDoors(p);
        });
    }

    /**
     * Remove opportunity for players to move from a place
     * 
     * @param place the transport at a terminus
     */
    private void playersMustMove(Place place) {
        playersAtPlace(place).forEach(p -> {
            try {
                p.getOutOfTransport();
            } catch (NonExistantPlaceException e) {
                log(e.toString());
            }
        });
    }

    /**
     * Shows stops on the map
     */
    public void showStops() {
        view.showStops(stopList);
    }

    /**
     * Shows players on the map
     */
    public void showPlayers() {
        view.showPlayer(players);
    }

    /**
     * Shows details of a place
     * 
     * @param place the place to have details
     */
    private void showDetailsPlace(Place place){
        //log(place.getSchedule);
    }

    /**
     * Makes a place operate actions if it changed of status
     * 
     * @param place the place to check
     */
    private void checkPlaceStatus(Place place) {
        try {
            place.arrived();
            place.left();
        } catch (NonExistantPlaceException e){
            log("Error while trying to check status : " + e.getMessage());
        }
    }

    /**
     * Makes time pass for all players places,
     * checks if there has been major modification on the place
     * during that time
     * 
     * @param time the time that passed since last checkup
     */
    public void timePassed(Integer time) {
        players.forEach(
            p -> {
                Place place = p.getPlace();
                if (place.getClass().equals(Transport.class)){
                    place.timePassed(time);
                }
                checkPlaceStatus(place);
            }
        );
    }

    /**
     * Returns if the thief is still in liberty
     * 
     * @return if the thief is still in liberty
     */
    public boolean thiefInLiberty() {
        return this.playing;
    }

    /**
     * Returns if the police ran out of time
     * 
     * @return if the police ran out of time
     */
    public boolean ranOutOfTime(long gameTime) {
        return gameTime >= MAXDURATION;
    }

    /**
     * Makes policemen wins, changes game status 
     */
    public void thiefCatched() {
        this.playing = false;
        this.winner = Policeman.class;
    }

    /**
     * Shows the ending screen with the winner
     */
    public void endTheGame() {
         view.showWinner(winner);
    }

    /**
     * Shows a message to the player
     * 
     * @param msg the message to show in logs
     */
    private void log(String msg) {
        view.printMessage(msg);
    }

    /**
     * Handles a text message
     * 
     * @param msg the message to handle
     */
    public void handleMessage(String msg) {
        log(msg);
    }

    /**
     * Handles a message communicated by a place
     * 
     * @param msg the message
     * @throws UnhandledMessageException if the message is unreadable
     */
    private void handlePlaceMessage(MessageToCom msg) throws UnhandledMessageException{
        Optional<MessageType> type = msg.safeGetType();
        Optional<Place> place = msg.safeGetPlace();
        if (type.isEmpty() || place.isEmpty()){ throw new UnhandledMessageException();}
        if (type.get().equals(MessageType.TERMINUS)){
            playersMustMove(place.get());
        }
        else if(type.get().equals(MessageType.LEFT)){
            closeDoors(place.get());
        }
        else {
            openDoors(place.get());
        }
    }

    /**
     * Checks if the place changement means that 
     * a policeman catches the thief, if yes changes game status
     * 
     * @param player the player who changed of place
     */
    private void changedPlace(Player player){
        players.forEach(p-> {
            if(p.catchThief(player)){
                thiefCatched();
                log(player + " catched " + p + " !");
            } else if (player.catchThief(p)){
                thiefCatched();
                log(p + " catched " + player + " !");
            }
        });
    }

    /**
     * Handles a message communicated by a player
     * 
     * @param msg the message
     * @throws UnhandledMessageException if the message is unreadable
     */
    private void handlePlayerMessage(MessageToCom msg) throws UnhandledMessageException{
        Optional<Place> place = msg.safeGetPlace();
        Optional<Player> player = msg.safeGetPlayer();
        if (place.isEmpty() || player.isEmpty()){ throw new UnhandledMessageException();}
        
        showPlayers();
        showDetailsPlace(place.get());
        changedPlace(player.get());
    }

    /**
     * Handles an event message
     * 
     * @param msg the message
     * @throws UnhandledMessageException if the message is unreadable
     */
    public void handleMessage(MessageToCom msg) throws UnhandledMessageException{
        log(msg.toString());
        try {
            if (msg.getAuteurClass().equals(Transport.class.getName()) || 
                msg.getAuteurClass().equals(Stop.class.getName())){
                handlePlaceMessage(msg);
            }
            else {
                handlePlayerMessage(msg);
            }
        } catch (UnhandledMessageException e){ throw e;}
    }
}