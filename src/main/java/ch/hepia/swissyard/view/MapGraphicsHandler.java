package ch.hepia.swissyard.view;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class MapGraphicsHandler extends AnimationTimer {

    private List<Drawable> objectsDrawn = new ArrayList<>();
    private GraphicsContext gc;
    private Canvas drawingCanvas;
    private double scale = 1.0;

    public MapGraphicsHandler(GraphicsContext gc) {
        this.gc = gc;
        this.drawingCanvas = gc.getCanvas();
    }

    /**
     * Adds a drawable item to the list of item to draw in the canvas
     * @param d a drawable
     */
    public void addToDraw(Drawable d) {
        objectsDrawn.add(d);
    }

    @Override
    public void handle(long now) {
        // Clearing canvas
        gc.clearRect(0, 0, this.drawingCanvas.getWidth(), this.drawingCanvas.getHeight());
        // Drawing canvas borders
        gc.strokeRect(1,1, this.drawingCanvas.getWidth()-2, this.drawingCanvas.getHeight()-2);
        gc.setFill(Color.WHITE);
        gc.fillRect(2,2, this.drawingCanvas.getWidth()-4, this.drawingCanvas.getHeight()-4);
        // Drawing objects in the list
        objectsDrawn.forEach(o -> o.draw(this.gc, this.scale));
    }
}
