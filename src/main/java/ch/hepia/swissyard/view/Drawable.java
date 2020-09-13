package ch.hepia.swissyard.view;

import javafx.scene.canvas.GraphicsContext;


public interface Drawable {
    /**
     * Allows an object to be drawn on a canvas in a JavaFX Frame
     * @param gc the canvas's graphic context
     * @param scale the scale to display the graphics
     */
    void draw(GraphicsContext gc, double scale);

    /**
     * If the object has to be drawn or not
     * @return the object is visible ?
     */
    boolean visible();
}
