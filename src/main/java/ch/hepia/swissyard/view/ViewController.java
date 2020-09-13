package ch.hepia.swissyard.view;

import ch.hepia.swissyard.Controller;
import ch.hepia.swissyard.Player;
import ch.hepia.swissyard.Stop;
import ch.hepia.swissyard.Transport;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewController implements Initializable {
    private Player player;

    @FXML Canvas mapCanvas;
    @FXML TextArea logWindow;
    @FXML Button changePlace;
    @FXML ComboBox<Player> playerSelection;


    /**
     * Returns the graphics context of the canvas
     * @return the graphics context of the canvas
     */
    public GraphicsContext getContext() {
        return this.mapCanvas.getGraphicsContext2D();
    }

    /**
     * Appends text to the log window
     * @param message the message to print
     */
    public void printMessage(String message) {
        this.logWindow.appendText(message + System.lineSeparator());
    }

    /**
     * Initialize all the controls
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.logWindow.appendText("Bienvenue dans Swiss-Yard" + System.lineSeparator());
        GraphicsContext gc = this.getContext();
        gc.strokeRect(1,1, this.mapCanvas.getWidth()-2, this.mapCanvas.getHeight()-2);
        gc.setFill(Color.WHITE);
        gc.fillRect(2,2, this.mapCanvas.getWidth()-4, this.mapCanvas.getHeight()-4);
    }

    /**
     * Shows all the players to the combobox
     * @param pl all the players to be shown
     */
    public void populatePlayers (List<Player> pl){
        this.playerSelection.setItems(FXCollections.observableArrayList(pl));
    }

    /**
     * Handles action on the players combobox
     * @param e the action of selection
     */
    public void onPlayerSelect(ActionEvent e) {
        this.player = this.playerSelection.getValue();
        this.changePlace.setDisable(this.player.getPlace() instanceof Stop);
    }

    /**
     * Handles action the the change place button
     * @param e the action of click
     */
    public void onChangePlaceClicked(ActionEvent e) {
        View.controller.clicked(this.player);
    }

    /**
     * Sets the Change Place Button
     * @param p the player who's at a stop
     */
    public void openDoors(Player p) {
        if (p.equals(player))
            this.changePlace.setDisable(false);
    }

    /**
     * Sets the Change Place Button to disabled if the selected player is not at a stop
     * @param p the player who's in a transport
     */
    public void closeDoors(Player p) {
        if (p.equals(player))
            this.changePlace.setDisable(true);
    }
}
