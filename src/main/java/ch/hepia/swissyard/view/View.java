package ch.hepia.swissyard.view;

import ch.hepia.swissyard.Controller;
import ch.hepia.swissyard.NonExistantPlaceException;
import ch.hepia.swissyard.Player;
import ch.hepia.swissyard.Stop;
import ch.hepia.swissyard.communication.Broker;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class View extends Application {
    private Parent root;
    private ViewController viewController;
    private MapGraphicsHandler graphicsHandler;
    public static Controller controller;

    private void initView(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader();
        primaryStage.setTitle("Swiss Yard");
        try {
            root = loader.load(getClass().getResource("/fxml/view.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 1080, 720, Color.GREY);
        viewController = loader.getController();
        graphicsHandler = new MapGraphicsHandler(viewController.getContext());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    /**
     * Main of the application because of JavaFX constraint
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.initView(primaryStage);
        graphicsHandler.start();
        int nbPlayers = 2;

        controller = new Controller(this);
        controller.showStops();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime baseTime = startTime;
        LocalDateTime currentTime;
        int timeElapsed;
        long gameTime = 0L;

        try{
            controller.createPlayers(nbPlayers);
            controller.showPlayers();
        } catch (NonExistantPlaceException | IllegalArgumentException e) {
            System.err.println("Error : " + e.getMessage());}
            while (controller.thiefInLiberty() && !controller.ranOutOfTime(gameTime)){
                currentTime = LocalDateTime.now();
                timeElapsed = (int) Duration.between(baseTime.toLocalTime(), currentTime.toLocalTime()).getSeconds();
                if (timeElapsed >= 15){
                    baseTime = currentTime;
                    controller.timePassed(timeElapsed);
                }
                gameTime = Duration.between(baseTime.toLocalTime(), currentTime.toLocalTime()).getSeconds();
            }
         controller.endTheGame();
    }

    public void showStops(List<Stop> stops) {
        stops.forEach(graphicsHandler::addToDraw);
    }

    /**
     * Adds the player to the frame and into the select box
     * @param players the list of players
     */
    public void showPlayer(List<Player> players) {
        viewController.populatePlayers(players);
        players.forEach(graphicsHandler::addToDraw);
    }

    /**
     * Prints a message into the logging windows on the JavaFX Frame
     * @param message the message to be displayed
     */
    public void printMessage(String message) {
        this.viewController.printMessage(message);
    }

    /**
     * Closes the doors of the transport in the view
     * @param p the player which has the transport doors closed
     */
    public void closeDoors(Player p) {
        this.viewController.closeDoors(p);
    }

    /**
     * Opens the doors of the transport in the view
     * @param p the player which has the transport doors opened
     */
    public void openDoors(Player p) {
        this.viewController.openDoors(p);
    }

    /**
     * Shows the winner in the log window and disables all interactions
     * @param winner the winner of the game
     */
    public void showWinner(Class<? extends Player> winner) {
        this.viewController.printMessage("The winner is " + winner.getName());
        this.viewController.playerSelection.setDisable(true);
        this.viewController.changePlace.setDisable(true);
    }
}