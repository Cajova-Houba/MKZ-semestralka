package mkz.mkz_semestralka.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import mkz.mkz_semestralka.controller.Controller;
import mkz.mkz_semestralka.core.Constrains;
import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.game.Game;
import mkz.mkz_semestralka.core.game.PlayerNum;
import mkz.mkz_semestralka.ui.GameActivity;

/**
 * Actuall view component.
 *
 * Created on 27.04.2017.
 *
 * @author Zdenek Vales
 */

public class BoardView extends View {

    private final static Logger logger = Logger.getLogger(BoardView.class);

    /**
     * Burlywood color.
     */
    public static final int DEF_BG_COLOR = Color.parseColor("#DEB887");
    /**
     * Saddlebrown color.
     */
    public static final int DEF_BORDER_COLOR = Color.parseColor("#8B4513");

    public static int gamePosToFieldNumber(int[] position) {
        return (position[1] -1)*10 + position[0];
    }

    private Paint bgPaint;
    private Paint borderPaint;
    private Paint smallBorderPaint;
    private Paint gridPaint;


    private Stone[] firstPlayersStones;
    private Stone[] secondPlayersStones;

    private int[] firstPlayerStonePositions;
    private int[] secondPlayerStonePositions;

    /**
     * Currently selected stone.
     */
    private Stone selected;

    private boolean boardDirty;

    private boolean canDrawStones;

    private Controller controller;

    private GameActivity parent;

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        // background
        bgPaint = new Paint();
        bgPaint.setColor(DEF_BG_COLOR);
        bgPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(DEF_BORDER_COLOR);
        borderPaint.setStrokeWidth(16);

        smallBorderPaint = new Paint();
        smallBorderPaint.setStyle(Paint.Style.STROKE);
        smallBorderPaint.setColor(DEF_BORDER_COLOR);
        smallBorderPaint.setStrokeWidth(8);

        gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(2);

        firstPlayersStones = new Stone[Constrains.MAX_NUMBER_OF_STONES];
        secondPlayersStones = new Stone[Constrains.MAX_NUMBER_OF_STONES];

        boardDirty = true;
        canDrawStones = false;

        controller = Controller.getInstance();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    int x = (int)event.getX();
                    int y = (int)event.getY();
                    onBoardClick(x,y);
                }
                return true;
            }
        });

        selected = null;
    }

    private void drawBoard(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        // background
        canvas.drawRect(0, 0, w, h, bgPaint);

        // grid
        for (int i = 0; i < 10; i++) {
            float x = w * i / 10;
            canvas.drawLine(x, 0f, x, h, gridPaint);
        }
        for (int i = 0; i < 3; i++) {
            float y = h * i / 3;
            canvas.drawLine(0f, y, w, y, gridPaint);
        }

        // border
        canvas.drawRect(0, 0, w, h, borderPaint);
        canvas.drawLine(0, h / 3f, w * 9f / 10, h / 3f, smallBorderPaint);
        canvas.drawLine(w / 10f, h * 2f / 3f, w, h * 2f / 3f, smallBorderPaint);
    }

    private void placeStones(Canvas canvas, int[] firstPlayerStones, int[] secondPlayerStones) {
        // first player
        for (int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            firstPlayersStones[i] = new Stone(1, firstPlayerStones[i], Stone.DEF_FIRST_PLAYER_COLOR, this);
            if (firstPlayerStones[i] != Game.OUT_OF_BOARD) {
                firstPlayersStones[i].draw(canvas);
            }
        }

        // second player
        for (int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            secondPlayersStones[i] = new Stone(2, secondPlayerStones[i], Stone.DEF_SECOND_PLAYER_COLOR, this);
            if (secondPlayerStones[i] != Game.OUT_OF_BOARD) {
                secondPlayersStones[i].draw(canvas);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);

        if (canDrawStones) {
            placeStones(canvas, firstPlayerStonePositions, secondPlayerStonePositions);
        }

        if(selected != null) {
            selected.drawSelected(canvas);
        }
    }

    public int getFieldWidth() {
        return getWidth() / 10;
    }

    public int getFieldHeight() {
        return getHeight() / 3;
    }

    /**
     * Converts field number to XY coordinates.
     * The coordinates of upper left corner are returned.
     *
     * @param fieldNumber
     * @return
     */
    public int[] fieldNumberToCoordinates(int fieldNumber) {
        int x = 0;
        int y = 0;

        if (fieldNumber < 1 || fieldNumber > 30) {
            return new int[]{x, y};
        } else if (fieldNumber < 11) {
            x = (fieldNumber - 1) * getFieldWidth();
            y = 0;
        } else if (fieldNumber < 21) {
            x = 10 * getFieldWidth() - (fieldNumber - 10) * getFieldWidth();
            y = getFieldHeight();
        } else {
            x = (fieldNumber - 21) * getFieldWidth();
            y = 2 * getFieldHeight();
        }

        return new int[]{x, y};
    }

    /**
     * Updates stones on the board.
     * Called from controller.
     *
     * @param firstPlayerStones
     * @param secondPlayerStones
     */
    public void updateStones(int[] firstPlayerStones, int[] secondPlayerStones) {
        // there is still reference in first/secondPlayerStones field
        this.selected = null;

        setFirstPlayerStonePositions(firstPlayerStones);
        setSecondPlayerStonePositions(secondPlayerStones);
        boardDirty = true;
        canDrawStones = true;
    }

    public void setFirstPlayerStonePositions(int[] firstPlayerStonePositions) {
        this.firstPlayerStonePositions = firstPlayerStonePositions;
    }

    public void setSecondPlayerStonePositions(int[] secondPlayerStonePositions) {
        this.secondPlayerStonePositions = secondPlayerStonePositions;
    }

    public void setParent(GameActivity parent) {
        this.parent = parent;
    }

    /**
     * Converts pixel coordinates [0-CANVAS_WIDTH, 0-CANVAS_HEIGHT] to senet coordinates [1-10,1-3].
     * Note that the fields in the middle row are numbered from right.
     *
     * @param x
     * @param y
     * @return
     */
    private int[] getGamePosition(double x, double y) {
        int gy = (int)(Math.floor(3*y / getHeight())+1);
        int gx = (int)(Math.floor(10*x / getWidth())+1);
        if(gy == 2) {
            gx = 11 - gx;
        }

        return new int[] {gx, gy};
    }

    /**
     * Returns either current player's stone on field or null.
     * @param fieldNumber
     * @return
     */
    public Stone selectStoneOnField(int fieldNumber) {
        PlayerNum player = Game.getInstance().getCurrentPlayerNum();
        Stone[] stones;
        if (player == PlayerNum.PLAYER_1) {
            stones = firstPlayersStones;
        } else {
            stones = secondPlayersStones;
        }
        for (Stone stone : stones) {
            if (stone.getField() == fieldNumber) {
                return  stone;
            }
        }

        return  null;
    }

    public void select(Stone selected) {
        this.selected = selected;
        this.invalidate();
    }

    /**
     * Deselect the currently selected stone.
     */
    public void deselect() {
        if(this.selected == null) {
            return;
        }

        if(this.selected.getField() == Game.OUT_OF_BOARD) {
            return;
        }

        this.selected = null;
        this.invalidate();
    }

    public void onBoardClick(int x, int y) {
        if (!Game.getInstance().isRunning()) {
            logger.w("Game's not running yet.");
            return;
        }

        if (!Game.getInstance().isMyTurn()) {
            logger.w("Not my turn(" + Game.getInstance().getMyPlayerNum() + ". Current turn: " + Game.getInstance().getCurrentPlayerNum());
            parent.displayToast("Teď nejsi na řadě!");
            return;
        }

        int[] gPos = getGamePosition(x, y);
        int fieldNumber = gamePosToFieldNumber(gPos);
        Stone selectedTmp = selectStoneOnField(fieldNumber);

        logger.i(String.format("Clicked on %d,%d - field %d.\n", gPos[0], gPos[1], fieldNumber));

        if (this.selected == null && selectedTmp != null) {
            logger.i(String.format("Selected: %s.", selectedTmp));
            select(selectedTmp);
            controller.select(selectedTmp.getField());
        } else if (this.selected != null && selectedTmp != null) {

            // if user click's on same stone, deselect it
            if (this.selected.getField() == selectedTmp.getField()) {
                logger.i(String.format("Deselecting %s.", this.selected));
                deselect();
            } else {
                logger.i(String.format("Selected %s instead of %s.", selectedTmp, this.selected));
                deselect();
                select(selectedTmp);
                controller.select(selectedTmp.getField());
            }
        } else if (this.selected != null && selectedTmp == null) {

            logger.i(String.format("Moving %s to %d.", this.selected, fieldNumber));

            controller.move(this.selected.getField(), fieldNumber);
        }
    }

    public Stone getSelected() {
        return selected;
    }
}