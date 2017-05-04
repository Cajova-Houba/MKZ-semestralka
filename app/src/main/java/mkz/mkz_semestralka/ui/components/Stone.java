package mkz.mkz_semestralka.ui.components;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


/**
 * Player's stone.
 *
 * @author Zdenek Vales
 */
public class Stone {


    public static final int DEF_FIRST_PLAYER_COLOR = Color.WHITE;
    public static final int DEF_SECOND_PLAYER_COLOR = Color.BLACK;
    public static final int DEF_SELECTED_COLOR = Color.RED;

    public static float getXMargin(float fieldWidth, float stoneWidth) {
        return (fieldWidth - stoneWidth) / 2.0f;
    }

    public static float getYMargin(float fieldHeight, float stoneHeight) {
        return (fieldHeight - stoneHeight) / 2.0f;
    }

    /**
     * 1 if this stone belongs to the first player.
     * 2 if this stone belongs to the second player.
     */
    private final int player;

    /**
     * Number of field for this stone.
     */
    private int field;

    /**
     * Color which will be used for this stone.
     */
    private int color;


    /**
     * Paint to be used to draw this stone.
     */
    private Paint standardPaint;

    /**
     * Paint to be used to draw this stone if it's selected.
     */
    private Paint selectedPaint;

    /**
     * Parental component.
     */
    private BoardView parent;

    public Stone(int player, int field, int color, BoardView parent) {
        this.player = player;
        this.field = field;
        this.color = color;
        this.parent = parent;

        init();
    }

    private void init() {
        standardPaint = new Paint();
        standardPaint.setColor(color);

        selectedPaint = new Paint();
        selectedPaint.setColor(DEF_SELECTED_COLOR);
        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setStrokeWidth(2);
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Draws itself on graphical context.
     * @param gc
     */
    public void draw(Canvas gc) {
        RectF ovalRect = getRekt(field);
        gc.drawOval(ovalRect, standardPaint);
    }

    /**
     * Draws itself as selected.
     * @param gc
     */
    public void drawSelected(Canvas gc) {
        RectF ovalRect = getRekt(field);
        gc.drawOval(ovalRect, standardPaint);
        gc.drawOval(ovalRect, selectedPaint);
    }

    /**
     * Converts the field number to the rect which can be sued to draw the stone.
     * Also adds margin.
     *
     * @param fieldNumber
     * @return
     */
    public RectF getRekt(int fieldNumber) {
        int[] coords = parent.fieldNumberToCoordinates(fieldNumber);

        int fw = parent.getFieldWidth();
        int fh = parent.getFieldHeight();

        // todo: maybe same dimensions?
        float w = fw*0.9f;
        float h = fh*0.9f;

        float x = coords[0] + getXMargin(fw,w);
        float y = coords[1] + getYMargin(fh,h);

        return new RectF(x, y, x+w, y+h);
    }

    public int getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        return "Stone{" +
                "player=" + player +
                ", field=" + field +
                ", color=" + color +
                '}';
    }
}
