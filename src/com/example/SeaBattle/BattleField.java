package com.example.SeaBattle;

import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import static com.example.SeaBattle.Ship.generateShipPosition;

/**
 * Created by Ivan on 11.11.2014.
 */

/**
 * Main model class, contained all info about ship's positions,
 * generate it at random.
 *
 */
public class BattleField {
    /** size of fields will be NxN */
    static final int N = 10;
    /** number of coordinate data - to store i and j pos*/
    static final int K = 2;

    public BattleField() {
        setAllDirect();
        curDir = Toward.NONE;
    }

    static final int EMPTY = 0;
    static final int SHIP = 1;
    static final int ATTACKED = 2;
    static final int ATTACKED_SHIP = 3;
    /**
     * Field is matrix, where
     * 0 - empty cell
     * 1 - ship part
     * 2 - attacked cell
     * 3 - attacked ship part
     */
    private int[][] player = new int[N][N];
    int[][] enemy = new int[N][N];
    //to store matrix during generating ship's position
    int[][] savedField = new int[N][N];
    //array to store coordinate of enemy's hit
    Stack<Pair> curEnemyHit = new Stack<>();
    //current direction for hitting
    Toward curDir;
    /**Store all directions, used when have
     * hit ship only one time, then we need to count
     * directions while don't find appropriate.
     */
    Stack<Toward> allDirect = new Stack<>();
    //Score, actually not used, but might.
    int playerShipCount = 0, enemyShipCount = 0;

    static final int fourDeck = 4;
    static final int threeDeck = 3;
    static final int twoDeck = 2;
    static final int oneDeck = 1;

    private boolean successfulHitPlayer = false;
    //Shot ship but have not killed yet, now is trying to kill.
    private boolean shipShooting = false;

    private void setAllDirect(){
        allDirect.push(Toward.LEFT);
        allDirect.push(Toward.DOWN);
        allDirect.push(Toward.RIGHT);
        allDirect.push(Toward.UP);
    }

    private void setSavedField(int[][] field){
        savedField = new int[N][N];
        for(int i = 0; i < N; ++i)
            for(int j = 0; j < N; ++j)
                 savedField[i][j] = field[i][j];
    }

    /**
     * Add ship's part on the cell of
     * field's field.
     * @param s is Ship must added on the field
     * @param playerField if true it refers to player field
     *               false - to enemy.
     * @return true if it possible to add
     * and it had been added,
     * false in other case.
     */
    public boolean addShip(Ship s, boolean playerField ) {
        int[][] field;
        if(playerField)
             field = player;
        else field = enemy;

        int n = s.getDeckNumber();
        int[][] pos = s.getPosition();
        //save our field  for restoring in unsuccessful adding ship
        setSavedField(field);
        //true - need to add ship on the board
        boolean toAdd = true;

        for (int i = 0; i < n; ++i) {
            //check each cell on which the ship placed
            if(field[pos[i][0]][pos[i][1]] == 1){
                //if it has busy by any other ship
                toAdd = false;
                break;
            }
        }
        if(toAdd) {
            if (!checkNeighbourCells(s, field))
                toAdd = false;
        }
        if(toAdd) {
            printField(field);
            for (int i = 0; i < n; ++i){
                field[pos[i][0]][pos[i][1]] = 1;}

            return true;
        }
        //restore cause unsuccessful try has changed field
        else field = savedField;
        return false;
    }

    private void printField(int[][] f) {
        for(int[] i: f) {
            System.out.print('\n');
            for (int j : i)
                System.out.print(j + " ");
        }
    }

    /**
     * Check on busy neighbour cells.
     * Need to avoid conflicts on ship's placement.
     * READ TODO.txt
     * @return true if  there are no conflicts
     * @param s is ship must be checked on possible touching to other ships.
     */
    private boolean checkNeighbourCells(Ship s, int[][]field) {
        String tag = "pos ";
        Log.e(tag, s.getPosition().toString());
        //get cells
        int[][] p = s.getPosition();
        int n = p.length;
        //get horizontal/vertical direction
        int dir = s.getDirection();
        try {
            for (int i = 0; i < n; ++i) {
                //at first check only cells that are not bordered
                //!!!
                if (p[i][0] < N - 1 && p[i][0] > 0 && p[i][1] < N - 1 && p[i][1] > 0) {
                    //check touching with another ships on diagonal
                    if (field[p[i][0] + 1][p[i][1] + 1] == 1 ||
                            field[p[i][0] - 1][p[i][1] - 1] == 1 ||
                            field[p[i][0] - 1][p[i][1] + 1] == 1 ||
                            field[p[i][0] + 1][p[i][1] - 1] == 1
                            )
                        return false;
                    //check toching with another ships
                    switch (dir) {
                        //vertical
                        case 0:
                            //check both side of ship
                            //if(field[p[i][0]][p[i][1] + 1] == 1 ||
                            //      field[p[i][0]][p[i][1] - 1] == 1)
                            //    return false;
                            //check stern and poop
                            if (i == 0) {
                                if (getBorderTouch(s) == 0) {
                                    if (field[p[i][0] - 1][p[i][1]] == 1 ||
                                            field[p[n - 1][0] + 1][p[n - 1][1]] == 1)
                                        return false;
                                }

                                //TODO:else{}
                            }
                            break;
                        //horizontal
                        case 1:
                            //if(field[p[i][0] + 1][p[i][1]] == 1 ||
                            //      field[p[i][0] - 1][p[i][1]] == 1)
                            //    return false;
                            if (i == 0) {
                                if (getBorderTouch(s) == 0) {
                                    if (field[p[i][0]][p[i][1] - 1] == 1 ||
                                            field[p[n - 1][0]][p[n - 1][1] + 1] == 1)
                                        return false;
                                }
                                //TODO:else{}
                            }
                    }

                }

                //TODO:!!
                else if (n == 1) {
                    //first row
                    if (p[0][0] == 0) {
                        if (p[0][1] > 0 && p[0][1] < N - 1) {
                            if (field[p[0][0] + 1][p[0][1]] == 1 ||
                                    field[p[0][0] + 1][p[0][1] + 1] == 1 ||
                                    field[p[0][0] + 1][p[0][1] - 1] == 1 ||
                                    field[p[0][0]][p[0][1] + 1] == 1 ||
                                    field[p[0][0]][p[0][1] - 1] == 1)
                                return false;
                        }
                        //TODO:else
                    }

                    //last
                    else if (p[0][0] == N - 1) {
                        if (p[0][1] > 0 && p[0][1] < N - 1) {
                            if (field[p[0][0] - 1][p[0][1]] == 1 ||
                                    field[p[0][0] - 1][p[0][1] + 1] == 1 ||
                                    field[p[0][0] - 1][p[0][1] - 1] == 1 ||
                                    field[p[0][0]][p[0][1] + 1] == 1 ||
                                    field[p[0][0]][p[0][1] - 1] == 1)
                                return false;
                        }
                        //TODO:else
                    }
                } else return false;
            }
        }
        catch (Exception e){e.printStackTrace(); return false;}
        return true;
    }

    private boolean checkNeighbourCellsOnBorder(Ship s, int[][] field) {
        int[][] p = s.getPosition();
        int n = p.length;
        //get horizontal/vertical direction
        int dir = s.getDirection();
        /////////////////check stern and poop////////////////////
        switch (dir) {
            //vertical
            case 0:
                //top
                if (p[0][0] > 0)
                    if (field[p[0][0]][p[0][1] - 1] == 1)
                        return false;
                //bottom
                if (p[n - 1][0] < N - 1)
                    if (field[p[n - 1][0]][p[n - 1][1] + 1] == 1)
                        return false;
                break;
            case 1:
                //left side
                if (p[0][0] > 0)
                    if (field[p[0][0]][p[0][1] - 1] == 1)
                        return false;
                //right side
                if (p[n - 1][0] < N - 1)
                    if (field[p[n - 1][0]][p[n - 1][1] + 1] == 1)
                        return false;

        }
        //////////////////////////////////////////////////////////
        //check corner

        return true;
    }

    private boolean checkCorners(Ship s, int[][] field) {
        return true;
    }

    /**
     *
     * @param s Ship might be placed at the border of field
     * @return
     */
    int getBorderTouch(Ship s) {
        //get cells
        int[][] p = s.getPosition();
        int n = p.length;
        //get horizontal/vertical direction
        int dir = s.getDirection();
        int border = 0;
        if (p[0][0] == 0 || p[0][0] == N - 1)
            border = 1;
        if (p[n - 1][0] == 0 || p[n - 1][0] == N - 1)
            border = 2;
        if (p[0][1] == 0 || p[0][1] == N - 1)
            border = 3;
        if (p[n - 1][0] == 0 || p[n - 1][0] == N - 1)
            border = 4;

        return border;
    }
    /**
     * Random place enemy ships.
     * @param player true - generate player field,
     *               else - pc.
     */
    public void setField(boolean player){
        final int nShip = 10;
        Ship[] ships = generateShips(nShip);
        for(int i = 0; i <= nShip/2; ++i){
            /**
            if(i > 5) {
                while(!addShip(ships[i], player)){
                    ships[i].setPosition(generateShipPosition(oneDeck));}
            }
            else*/ if (i > 2){
                while(!addShip(ships[i], player)){
                    ships[i].setPosition(generateShipPosition(twoDeck));}
            }
            else if(i > 0){
                while(!addShip(ships[i], player)){
                    ships[i].setPosition(generateShipPosition(threeDeck));}
            }
            else {
                while(!addShip(ships[i], player)){
                    ships[i].setPosition(generateShipPosition(fourDeck));}
            }
        }
    }

    /**
     * Generate ships.
     * @param N ship's number
     * @return array of ships with length N
     */
    private Ship[] generateShips(int N){
        Ship[] s = new Ship[N];
        s[0] = new Ship(generateShipPosition(fourDeck));
        s[1] = new Ship(generateShipPosition(threeDeck));
        s[2] = new Ship(generateShipPosition(threeDeck));
        s[3] = new Ship(generateShipPosition(twoDeck));
        s[4] = new Ship(generateShipPosition(twoDeck));
        s[5] = new Ship(generateShipPosition(twoDeck));
        s[6] = new Ship(generateShipPosition(oneDeck));
        s[7] = new Ship(generateShipPosition(oneDeck));
        s[8] = new Ship(generateShipPosition(oneDeck));
        s[9] = new Ship(generateShipPosition(oneDeck));
        return s;
    }

    /**
     * Change field, its game move.
     * @param ip i coordinate of cell(row)
     * @param jp j coordinate of cell(column)
     * @return true if player hit some ship, else false
     */
    boolean hitEnemy(int ip, int jp) {
        //have already attacked
        if(enemy[ip][jp] >= ATTACKED )
            return true;
        if (enemy[ip][jp] == SHIP) {    //here is a part of ship
            enemy[ip][jp] = ATTACKED_SHIP;//means attacked cell
            if(isKilledShip(ip,jp, true)) {
                changeAttackedCell(enemy);
                ++playerShipCount;
            }
            return true;
        }
        enemy[ip][jp] = ATTACKED;
        return false;
    }


    /**
     * Directions for hitting player's ship.
     */
    enum Toward{DOWN, UP, LEFT, RIGHT, NONE}

    /**
     * Hit player's cell.
     * THIS METHOD WORK INCORRECT
     * (a little bit - there is trouble with enemy's hit logic :)
     * @return true if successful,
     * false if not.
     */
    boolean hitPlayer(){
        Pair p = getCoordinateToHit();
        if (player[p.i][p.j] == SHIP) {    //here is a part of ship
            player[p.i][p.j] = ATTACKED_SHIP;//means attacked cell
            successfulHitPlayer = true;
            shipShooting = true;
            //if pc just has killed some ship
            //if(isKilledShip(p.i, p.j, false)) {
            if(isKilledShip(curEnemyHit.get(0).i,curEnemyHit.get(0).j, false)){
                successfulHitPlayer = false;
                shipShooting = false;
                changeAttackedCell(player);
                curEnemyHit.clear();
                curDir = Toward.NONE;
                setAllDirect();
                ++enemyShipCount;
            }
            return true;
        }
        else successfulHitPlayer = false;
        /**
         * Something in logic was WRONG!!!
         * Need to be fixed in next version.
         * Now just reset all variables.
         */
        if(player[p.i][p.j] >= ATTACKED){
            successfulHitPlayer = false;
            shipShooting = false;
            curEnemyHit.clear();
            curDir = Toward.NONE;
            setAllDirect();
        }
        //did not hit any ship
        else player[p.i][p.j] = ATTACKED;

        //have hit ship in this session, but previous hit also was unsuccessful
        if(shipShooting){
            //it means we got one of ship's ending
            if(curEnemyHit.size() > 2 && !successfulHitPlayer) {
                Pair startShip = new Pair();
                startShip = curEnemyHit.get(0);
                curEnemyHit.clear();
                curEnemyHit.push(startShip);
                switch (curDir) {
                    case UP:
                        curDir = Toward.DOWN;
                        break;
                    case DOWN:
                        curDir = Toward.UP;
                        break;
                    case RIGHT:
                        curDir = Toward.LEFT;
                        break;
                    case LEFT:
                        curDir = Toward.RIGHT;
                        break;
                }
            }
            else {
                curEnemyHit.pop();
                //change next direction
                /**
                 * must be deleted if ==
                 */
                Toward t = allDirect.pop();
                //if (curDir == t)
                //    curDir = allDirect.pop();
                 curDir = t;
            }
        }
        else {
            setAllDirect();
            curEnemyHit.clear();
        }
        //have hit ship in this session, and
        //previous hit was successful
        //but now unsuccessful
        if(successfulHitPlayer){
            successfulHitPlayer = false;

        }
        return false;
    }

    /**
     * Mark all cell( abreast killed by pc ship ) as ATTACKED.
     */
    private void changeAttackedCell(int[][] field) {
        Stack<Pair> ship = new Stack<>();
        for(int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                if (field[i][j] == ATTACKED_SHIP) {
                    //save ship positions, cause it will be mark as ATTACKED
                    ship.push(new Pair(i, j));
                }
            }
        }

        for(int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                if (field[i][j] == ATTACKED_SHIP) {
                    /** not touch the border ship!!! */
                    if (field[i + 1][j] != ATTACKED_SHIP)
                        field[i + 1][j] = ATTACKED;
                    if (field[i - 1][j] != ATTACKED_SHIP)
                        field[i - 1][j] = ATTACKED;
                    if (field[i][j + 1] != ATTACKED_SHIP)
                        field[i][j + 1] = ATTACKED;
                    if (field[i][j - 1] != ATTACKED_SHIP)
                        field[i][j - 1] = ATTACKED;

                    if (field[i + 1][j + 1] != ATTACKED_SHIP)
                        field[i + 1][j + 1] = ATTACKED;
                    if (field[i - 1][j - 1] != ATTACKED_SHIP)
                        field[i - 1][j - 1] = ATTACKED;
                    if (field[i - 1][j + 1] != ATTACKED_SHIP)
                        field[i - 1][j + 1] = ATTACKED;
                    if (field[i + 1][j - 1] != ATTACKED_SHIP)
                        field[i + 1][j - 1] = ATTACKED;
                }
            }
        }
        //restore ship
        while(!ship.empty()){
            Pair t = ship.pop();
            field[t.i][t.j] = ATTACKED_SHIP;
        }
    }

    /**
     * Get position of cell, if pc have git ship,
     * but have not killed.
     * @return position.
     */
    private Pair getContinuingHit(){
        Pair p = new Pair();
        Pair last = curEnemyHit.lastElement();
        p.i = last.i;
        p.j = last.j;

        switch (curDir) {
            case UP:
                if(p.i - 1 >= 0) {
                    p.i--;
                }
                else {
                    p.i = curEnemyHit.get(0).i + 1;
                    p.j = curEnemyHit.get(0).j;
                    curDir = Toward.DOWN;
                }
                break;
            case DOWN:
                if(p.i + 1 <= N -1) {
                    p.i++;
                   // p.j = j;
                }
                else {
                    p.i = curEnemyHit.get(0).i - 1;
                    p.j = curEnemyHit.get(0).j;
                    curDir = Toward.UP;
                }
                break;
            case RIGHT:
                if(p.j + 1 <= N -1) {
                    //p.i = i;
                    p.j++;
                }
                else {
                    p.i = curEnemyHit.get(0).i;
                    p.j = curEnemyHit.get(0).j - 1;
                    curDir = Toward.LEFT;
                }
                break;
            case LEFT:
                if(p.j -1 >= 0) {
                    //p.i = i;
                    p.j--;
                }
                else {
                    p.i = curEnemyHit.get(0).i;
                    p.j = curEnemyHit.get(0).j + 1;
                    curDir = Toward.RIGHT;
                }
        }
        return p;
    }

    /**
     * Get coordinate of player's cell must be hitted
     * @return Pair contained i and j pos of cell.
     */
    private Pair getCoordinateToHit() {
        Pair p = new Pair();
        Random r = new Random();
        //pc keep his move and has attacked ship on the previous move
        if(!curEnemyHit.empty()) {
            p = new Pair(getContinuingHit());
            curEnemyHit.push(p);
        }
        //last hit was unsuccessful or has killed some ship
        // or it's first time hitting
        while(curEnemyHit.empty()) {
            p.i = r.nextInt(BattleField.N);
            p.j = r.nextInt(BattleField.N);
            if (player[p.i][p.j] != ATTACKED &&
                    player[p.i][p.j] != ATTACKED_SHIP) {
                curEnemyHit.push(p);
                int i = p.i, j = p.j;
                //get random direction for further hits
                int x = r.nextInt(100);
                if (x > 75) {
                    if (i > 0 && player[i - 1][j] != ATTACKED)
                        curDir = Toward.UP;
                    else curDir = Toward.DOWN;
                } else if (x > 50) {
                    if (i < N - 1 && player[i + 1][j] != ATTACKED)
                        curDir = Toward.DOWN;
                    else curDir = Toward.UP;
                } else if (x > 25) {
                    if (j > 0 && player[i][j - 1] != ATTACKED)
                        curDir = Toward.LEFT;
                    else curDir = Toward.RIGHT;
                } else {
                    if (j < N - 1 && player[i][j + 1] != ATTACKED)
                        curDir = Toward.RIGHT;
                    else curDir = Toward.LEFT;
                }
            }

        }
        return p;
    }

    /**
     * Check cell if it part of ship and did some ship here was killed.
     * //It must be called after attacking only!(after BattleField::hit(...))
     * @param ip i coordinate of cell(row)
     * @param jp j coordinate of cell(column)
     * @param playerAttacked if true - attacker was player,
     *               else enemy(pc)
     * @return true if ship's part located at (ip,jp) belong to some ship
     * and this ship is killed(every its part was attacked);
     * false if it's not a part of ship or this ship had not been killed.
     */
     boolean isKilledShip(int ip, int jp, boolean playerAttacked){
        int[][] field;
        if(!playerAttacked)
            field = player;
        else field = enemy;
        if(field[ip][jp] == ATTACKED_SHIP) {
            //check neighbour on NOT  hitting
            //any part of ship is not attacked
            if(field[ip][jp+1] == 1 ||
                    field[ip][jp-1] == 1 ||
                    field[ip+1][jp] == 1 ||
                    field[ip-1][jp] == 1)
                return false;
            Toward shipDirection = getAttackedNeighbourCell(ip, jp, field);
            int si = ip, sj = jp;
            if(shipDirection ==  Toward.UP ||
                    shipDirection == Toward.DOWN) {
                while (field[ip--][jp] != EMPTY) {// < SHIP ?????
                    if (field[ip][jp] == SHIP)
                        return false;
                    if (ip == 0)
                        break;}
                ip = si;
                jp = sj;
                while (field[ip++][jp] != EMPTY) {
                    if (field[ip][jp] == SHIP)
                        return false;
                    if (ip == N - 1)
                        break;}
                return true;
            }
            else if(shipDirection ==  Toward.LEFT ||
                    shipDirection == Toward.RIGHT) {
                while(field[ip][jp--] != EMPTY  ){
                    if(field[ip][jp] == SHIP)
                        return false;
                    if(jp == 0)
                        break;}
                ip = si;
                jp = sj;
                while(field[ip][jp++] != EMPTY ){
                    if(field[ip][jp] == SHIP)
                        return false;
                    if(jp == N - 1)
                        break;}
                return true;
            }
        }
        return false;
    }

     Toward getAttackedNeighbourCell(int ip, int jp, int[][] field) {
        int up = 0, down = 0, lt = 0, rt = 0;
        if(ip - 1 >= 0 && field[ip - 1][jp] == ATTACKED_SHIP)
                ++up;
         if(ip + 1 < N && field[ip + 1][jp] == ATTACKED_SHIP)
                ++down;
         if(jp - 1 >= 0 && field[ip][jp - 1] == ATTACKED_SHIP)
                ++lt;
         if(jp  + 1 < N && field[ip][jp + 1] == ATTACKED_SHIP)
                ++rt;
         if(up > 0)
             return Toward.UP;
         if(down > 0)
             return Toward.DOWN;
         if(lt > 0)
             return Toward.LEFT;
         if(rt > 0)
             return Toward.RIGHT;
         return Toward.NONE;
    }

    int getPlayerScore(){ return playerShipCount; }
    int getEnemyScore(){
        return enemyShipCount;
    }

    public int[][] getPlayer() {
        return player;
    }

    public int[][] getEnemy() {
        return enemy;
    }

    /**
     * Store coordinates of cell.
     * i - row, j - column;
     */
    private static class Pair{
        int i, j;
        Pair(){
            i = -1;
            j = -1;
        }
        Pair(Pair p){
            this.i = p.i;
            this.j = p.j;
        }

        Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }
}
