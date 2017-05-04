package mkz.mkz_semestralka.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import mkz.mkz_semestralka.controller.Controller;
import mkz.mkz_semestralka.core.Constrains;
import mkz.mkz_semestralka.core.game.Game;

/**
 * Actuall view component.
 *
 * Created on 27.04.2017.
 *
 * @author Zdenek Vales
 */

public class BoardView extends View{

    /**
     * Burlywood color.
     */
    public static final int DEF_BG_COLOR = Color.parseColor("#DEB887");
    /**
     * Saddlebrown color.
     */
    public static final int DEF_BORDER_COLOR = Color.parseColor("#8B4513");

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

        selected = null;
    }

    private void drawBoard(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        // background
        canvas.drawRect(0,0,w,h,bgPaint);

        // grid
        for(int i = 0; i < 10 ; i++) {
            float x = w*i/10;
            canvas.drawLine(x,0f,x,h,gridPaint);
        }
        for(int i = 0; i < 3; i++) {
            float y = h*i/3;
            canvas.drawLine(0f,y,w,y,gridPaint);
        }

        // border
        canvas.drawRect(0,0,w,h,borderPaint);
        canvas.drawLine(0,h/3f,w*9f/10,h/3f,smallBorderPaint);
        canvas.drawLine(w/10f,h*2f/3f,w,h*2f/3f,smallBorderPaint);
    }

    private void placeStones(Canvas canvas,int[] firstPlayerStones, int[] secondPlayerStones) {
        // first player
        for (int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            firstPlayersStones[i] = new Stone(1, firstPlayerStones[i], Stone.DEF_FIRST_PLAYER_COLOR, this);
            if(firstPlayerStones[i] != Game.OUT_OF_BOARD) {
                firstPlayersStones[i].draw(canvas);
            }
        }

        // second player
        for (int i = 0; i < Constrains.MAX_NUMBER_OF_STONES; i++) {
            secondPlayersStones[i] = new Stone(2, secondPlayerStones[i], Stone.DEF_SECOND_PLAYER_COLOR, this);
            if(secondPlayerStones[i] != Game.OUT_OF_BOARD) {
                secondPlayersStones[i].draw(canvas);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(boardDirty) {
            drawBoard(canvas);
        }

        if(canDrawStones) {
            placeStones(canvas, firstPlayerStonePositions, secondPlayerStonePositions);
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

        if(fieldNumber < 1 || fieldNumber > 30) {
            return new int[] {x,y};
        } else if(fieldNumber < 11) {
            x = (fieldNumber - 1)*getFieldWidth();
            y = 0;
        } else if(fieldNumber < 21) {
            x = 10 * getFieldWidth() - (fieldNumber - 10) * getFieldWidth() ;
            y = getFieldHeight();
        } else {
            x = (fieldNumber - 21)*getFieldWidth();
            y = 2*getFieldHeight();
        }

        return new int[] {x,y};
    }

    public void setFirstPlayerStonePositions(int[] firstPlayerStonePositions) {
        this.firstPlayerStonePositions = firstPlayerStonePositions;
    }

    public void setSecondPlayerStonePositions(int[] secondPlayerStonePositions) {
        this.secondPlayerStonePositions = secondPlayerStonePositions;
    }

    public void setCanDrawStones(boolean canDrawStones) {
        this.canDrawStones = canDrawStones;
    }
}
