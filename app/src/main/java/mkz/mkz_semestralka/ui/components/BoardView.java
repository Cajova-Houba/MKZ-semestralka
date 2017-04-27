package mkz.mkz_semestralka.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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

}
