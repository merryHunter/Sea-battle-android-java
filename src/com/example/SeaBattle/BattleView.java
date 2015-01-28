package com.example.SeaBattle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Ivan on 18.11.2014.
 */

/**
 * View board and ships(model).
 */
public class BattleView extends SurfaceView {
    //background
    private Bitmap bitmap;
    private Paint paint;
    /** Use true - player is making move, false - pc
     * used to determine who making move and red appropriate
     * (red/green) circle notification. */
    private boolean turn = true;
    /**
     * 1 if player won
     * 2 if enemy won,
     * -1 if nobody have won yet.
     */
    private int winner = -1;
    //dynamic cell size for viewing at miscellaneous screens.
    private static  int cellSize ;
    //start of  player's board
    private int xStart = 0, yStart = 0;
    //
    private int endEnemyFieldY;
    private  int startEnemyFieldX;
    //updated model
    private int[][] player;
    private int[][] enemy;

    public BattleView(Context context) {
        super(context);
        paint = new Paint();
        setWillNotDraw(false);
        bitmap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.battle1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.save();
        drawBoard(canvas);
        canvas.restore();
        canvas.save();
        drawShips(canvas);
        canvas.restore();
        checkWinner(canvas);
    }

    private boolean checkWinner(Canvas canvas) {
        if(winner != -1) {
            paint.setColor(Color.MAGENTA);
            paint.setTextSize(72);
            //canvas.drawRect(150, 150, getWidth() * 0.7f, getHeight() * 0.7f, paint);
            if(winner == 1)
                canvas.drawText("You won!",getWidth()/2,
                        getHeight()/2,paint );
            else canvas.drawText("Game over!",getWidth()/2,
                    getHeight()/2, paint );
            return true;
        }
        return false;
    }

    private void drawShips(Canvas canvas) {
        Paint rp = new Paint();
        for(int i = 0; i < BattleField.N; ++i)
            for(int j = 0; j < BattleField.N;++j) {
                if (player[i][j] == BattleField.SHIP) {
                    rp.setARGB(150, 0, 150, 100);
                    canvas.drawRect( j * cellSize,i * cellSize,
                            (j + 1) * cellSize, (i + 1) * cellSize, rp);
                }
                else if(player[i][j] == BattleField.ATTACKED){
                    rp.setARGB(150, 0, 0, 100);
                    canvas.drawRect( j * cellSize,i * cellSize,
                            (j + 1) * cellSize,(i + 1) * cellSize,  rp);
                }
                else if(player[i][j] == BattleField.ATTACKED_SHIP) {
                    rp.setARGB(150, 250, 0, 0);
                    canvas.drawRect( j * cellSize, i * cellSize,
                            (j + 1) * cellSize,(i + 1) * cellSize, rp);
                }

            }
        canvas.translate(startEnemyFieldX,0);

        for(int i = 0; i < BattleField.N; ++i)
            for(int j = 0; j < BattleField.N;++j) {
                if (enemy[i][j] == BattleField.ATTACKED) {
                    rp.setARGB(150, 0, 0, 100);
                    //rp.setARGB(150, 196, 0, 171);
                    canvas.drawRect( j * cellSize, i * cellSize,
                            (j + 1) * cellSize, (i + 1) * cellSize, rp);
                }
                else if(enemy[i][j] == BattleField.ATTACKED_SHIP) {
                    rp.setARGB(150, 250, 0, 0);
                    canvas.drawRect(j * cellSize,i * cellSize,
                            (j + 1) * cellSize, (i + 1) * cellSize,  rp);
                }
            }

    }

    private void drawBoard(Canvas canvas) {
        cellSize = getWidth()/(2*BattleField.N ) - 8;
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        //draw player
        int n = BattleField.N * cellSize;
        //vertical lines
        for(int i = 0; i <= n; i += cellSize)
            canvas.drawLine(i, yStart, i, n, paint );
        //horizontal lines
        for(int i = 0; i <= n; i += cellSize)
            canvas.drawLine(xStart, i, n, i, paint );
        //draw indicator
        if(turn)
            paint.setColor(Color.GREEN);
        else
            paint.setColor(Color.RED);

        canvas.drawCircle(n+75, getHeight()/2,10,paint);
        //draw enemy
        startEnemyFieldX = n + getWidth()/10;
        endEnemyFieldY = cellSize * BattleField.N;
        canvas.translate(startEnemyFieldX,0);
        paint.setColor(Color.BLACK);
        //vertical lines
        for(int i = 0; i <= n; i += cellSize)
            canvas.drawLine(i, yStart, i, n, paint );
        //horizontal lines
        for(int i = 0; i <= n; i += cellSize)
            canvas.drawLine(xStart, i, n, i, paint );
    }


    public void setBoard(int[][] player, int[][] enemy){
        this.player = new int[BattleField.N][BattleField.N];
        this.enemy = new int[BattleField.N][BattleField.N];
        for(int i = 0; i <BattleField.N; ++i)
            for(int j = 0; j <BattleField.N; ++j){
                this.player[i][j] = player[i][j];
                this.enemy[i][j] = enemy[i][j];
        }
    }

    public void setCell(boolean player){
        int[][] field;
        if(player)
            field = this.player;
        else field = this.enemy;

    }

    public void setTurn(boolean t) {
        this.turn = t;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Defines the extra padding for the shape name text
        int textPadding = 10;
        int shapeWidth = 100;
        int contentWidth = shapeWidth;
        // Resolve the width based on our minimum and the measure spec
        int minw = contentWidth + getPaddingLeft() + getPaddingRight();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 0);
        // Ask for a height that would let the view get as big as it can
        int shapeHeight = 100;
        int minh = shapeHeight + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);
        // Calling this method determines the measured width and height
        // Retrieve with getMeasuredWidth or getMeasuredHeight methods later
        setMeasuredDimension(w, h);
    }

    public int getEndEnemyFieldY() {
        return endEnemyFieldY;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getStartEnemyFieldX() {
        return startEnemyFieldX;
    }
}
