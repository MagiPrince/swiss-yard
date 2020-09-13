package ch.hepia.swissyard;

import ch.hepia.swissyard.Player;
import ch.hepia.swissyard.Stop;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Vector;

public class Thief extends Player {
    private static final long serialVersionUID = -204294464151274226L;

    /**
     * Constructor Thief
     * 
     * @param name the player name
     * @param stop the starting stop
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Thief(String name, Stop stop, String ipBroker){
        super(name, stop, ipBroker);
    }

    /**
     * Constructor Thief
     * 
     * @param name the player name
     * @param stop the starting stop
     */
    public Thief(String name, Stop stop) {
        super(name, stop,"localhost");
    }
    
    /**
     * Constructor Thief
     * 
     * @param stop the starting stop
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Thief(Stop stop, String ipBroker) {
        super("Thief", stop, ipBroker);
    }

    /**
     * Constructor Thief
     * 
     * @param stop the starting stop
     */
    public Thief(Stop stop){
        super("Thief", stop,"localhost");
    }

    
    public boolean catchThief(Player player){
        return false;
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public void draw(GraphicsContext gc, double scale) {
        if(visible()) {
            gc.setFill(Color.RED);
            double x = 0;
            double y = 0;
            try {
                x = this.getCoordinate().getLatitude() * 1000 % 880;
                y = this.getCoordinate().getLongitude() * 1000 % 600;
            } catch (NonExistantPlaceException e) {
                e.printStackTrace();
            }
            gc.fillOval(scale * x, scale * y, scale * 5,  scale * 5);
        }
    }
}
