package com.example.SeaBattle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import static android.os.SystemClock.sleep;

/**
 * Main activity class, can be interpreted as a controller to view and model.
 * Provides updating model and view, handling touches.
 */
public class SeaBattleMain extends Activity implements View.OnTouchListener {
    private BattleView mBattleView;
    private SeaBattleGame mSeaBattleGame;

    volatile boolean playerTurn = true;
    volatile boolean enemyTurn = false;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApp();

    }

    private void initApp() {
        setContentView(R.layout.main);
    }

    /**
     * Handler to click "Start" button at the main layout;
     * @param v Current view.
     */
    public void goToBattle(View v) {
        mBattleView = new BattleView(this);
        mBattleView.setOnTouchListener(this);
        setContentView(mBattleView);
        mSeaBattleGame = new SeaBattleGame();
        //update view model
        mBattleView.setBoard(mSeaBattleGame.getPlayerPlacement(),
                mSeaBattleGame.getEnemyPlacement());
        mBattleView.invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        int cellSize = mBattleView.getCellSize();
        int startBoardX = mBattleView.getStartEnemyFieldX();
        int endBoardX = cellSize * BattleField.N + startBoardX;
        int endBoardY = mBattleView.getEndEnemyFieldY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //click only over enemy board to hit
                if (x >= startBoardX && y <= endBoardY &&
                        x <= endBoardX) {
                    int ip = y / cellSize;
                    int jp = (x - startBoardX) / cellSize;
                    return playerAttackEnemy(ip, jp);
                }

            case MotionEvent.ACTION_UP:
                enemyAttackPlayer();
            case MotionEvent.ACTION_MOVE:
                //user incorrect touch
        }
        return true;
    }

    /**
     * Attack enemy's ships.
     * @param i row position of cell
     * @param j column position of cell
     * @return True, that means end of player's attack
     *          and then enemy will hit, cause ACTION.UP called after
     *          ACTION.DOW return true;
     *          False, if attack was successul and player will attack
     *          again, cause of game rules.
     */
    private boolean playerAttackEnemy(int i, int j){
        mBattleView.setTurn(playerTurn);
        mBattleView.invalidate();
        if (playerTurn) {
            if (!mSeaBattleGame.attackEnemy(i, j)) {
                //unsuccessful
                mBattleView.setTurn(false);
                mBattleView.setBoard(mSeaBattleGame.getPlayerPlacement(),
                        mSeaBattleGame.getEnemyPlacement());
                //in fact draw red circle that means enemy turn now
                mBattleView.invalidate();
                //turn enemy move
                enemyTurn = true;
                playerTurn = false;
                return true;
            }
            //check winner
            int winner = mSeaBattleGame.getWinner();
            if (winner != -1) {
                mBattleView.setWinner(winner);
                mBattleView.setBoard(mSeaBattleGame.getPlayerPlacement(),
                        mSeaBattleGame.getEnemyPlacement());
                mBattleView.invalidate();
                return false;
            }
            mBattleView.setTurn(true);
            mBattleView.setBoard(mSeaBattleGame.getPlayerPlacement(),
                    mSeaBattleGame.getEnemyPlacement());
            mBattleView.invalidate();
            return false;
        }
        return false;
    }

    private boolean enemyAttackPlayer() {
        if (enemyTurn) {
            mBattleView.setTurn(false);
            mBattleView.invalidate();
            //while enemy is attacking only player's ships
            while (mSeaBattleGame.attackPlayer()) {
                mBattleView.setBoard(mSeaBattleGame.getPlayerPlacement(),
                        mSeaBattleGame.getEnemyPlacement());
                //wait and give info to player that "now enemy is thinking"
                try {
                    sleep(500);
                } catch (Exception ie) {}
                int winner = mSeaBattleGame.getWinner();
                if (winner != -1) {
                    mBattleView.setWinner(winner);
                    mBattleView.setBoard(mSeaBattleGame.getPlayerPlacement(),
                            mSeaBattleGame.getEnemyPlacement());
                    mBattleView.invalidate();
                    return false;
                }
            }
            //hitting finished, cause enemy fluffed
            mBattleView.setBoard(mSeaBattleGame.getPlayerPlacement(),
                    mSeaBattleGame.getEnemyPlacement());
            playerTurn = true;
            mBattleView.setTurn(true);
            mBattleView.invalidate();
            enemyTurn = false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Can restart the game;
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                initApp();
                return true;
        }
        return true;
    }
}
