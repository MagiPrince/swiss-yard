package ch.hepia.swissyard;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Vector;

public class Policeman extends Player {
    private static final long serialVersionUID = 7007773628753497779L;

    /**
     * Constructor Policeman
     * 
     * @param name the player name
     * @param stop the starting stop
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Policeman(String name, Stop stop, String ipBroker){
        super(name, stop, ipBroker);
    }
    
    /**
     * Constructor Policeman
     * 
     * @param stop the starting stop
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Policeman(Stop stop, String ipBroker) {
        super("Policeman", stop, ipBroker);
    }
    
    /**
     * Constructor Policeman
     * 
     * @param name the player name
     * @param stop the starting stop
     */
    public Policeman(String name, Stop stop){
        super(name, stop, "localhost");
    }
    
    /**
     * Constructor Policeman
     * 
     * @param stop the starting stop
     */
    public Policeman(Stop stop) {
        super("Policeman", stop, "localhost");
    }


    public boolean catchThief(Player player){
        return player.getClass().equals(Thief.class) && player.getPlace().equals(this.getPlace());
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public void draw(GraphicsContext gc, double scale) {
        if(visible()) {
            double x = 0;
            double y = 0;
            try {
                x = this.getCoordinate().getLatitude() * 1000 % 880;
                y = this.getCoordinate().getLongitude() * 1000 % 600;
            } catch (NonExistantPlaceException e) {
                e.printStackTrace();
            }
            gc.setFill(Color.BLUE);
            gc.fillOval(x * scale, y * scale, 5, 5);
        }
    }
}
