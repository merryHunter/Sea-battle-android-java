package com.example.SeaBattle;

import java.util.Random;

import static com.example.SeaBattle.BattleField.*;

/**
 * Created by Ivan on 11.11.2014.
 */

/**
 * Model class contained info about ship position.
 */
public class Ship {
    /** Number of deck in ship,
     * in fact it is the size of the pos[][] array,
     * means number of cells that ship use on field
     * */
    private final int nDeck;
    /**
     * Array of cell's position.
     * pos[i][j] - i - row, j - column of position;
     * 0 <= i < nDeck; j == 0 || j == 1, cause cell has 2 dimension(x and y)
     * nDeck is length.
     * */
    private int[][] pos;

    /**
     * Create Ship.
     * @param pos array that contain cells position,
     *            so it consist of nDeck number of  subarrays sa, each of
     *            them with length equals 2.
     *            First subarray sa must be the left or the top cell  of ship,
     *            the next cell must be write in order by row, one by one.
     */
     Ship(int[][] pos) {
        this.nDeck = pos.length;
        this.pos = pos;
    }

    public int getDeckNumber() {
        return nDeck;
    }


    public int[][] getPosition() {
        return pos;
    }

    public boolean setPosition(int[][] positions) {
        if(positions.length > nDeck)
            return false;
        pos = positions;
        return true;
    }

    /**
     * Ship has 2 directions.
     * @return 0 vertical or one deck
     * 1 - horizontal
     * -1 - ship had not been created
     */
    public int getDirection(){
        if( pos.length <= 0)
            return -1;
        if(nDeck == 1)
            return 0;
        //check cell's row
        if(pos[0][0]  == pos[1][0])
            return 1;
        else
            return 0;
    }
    /**
     * Generate ship position at random.
     * Output arrays consists of subarrays of coordinates of cell,
     * in fact each subarray is pair (i, j).
     * @param nDeck number of decks
     * @return array contained cell's positions
     */
    public static int[][] generateShipPosition(int nDeck){
        int[][] pos = new int[nDeck][2];
        Random random = new Random();
        //start of ship, the most left/top coordinate
        int si, sj, direction = 0;
        /**Create start of ship with vertical direction.
         * to prevent ship out of bound(field size),
         *  consider it's deck number;
         *  +1 cause one deck might be placed in (9,9)
         */
        si = random.nextInt(N - nDeck + 1);
        sj = random.nextInt(N);
        pos[0][0] = si;
        pos[0][1] = sj;
        for (int i = 1; i < nDeck; ++i) {
            //add part of ship on rows downward
            pos[i][0] = ++si;
            pos[i][1] = sj;
        }
        //probable change direction to horizontal
        if( random.nextInt(100) >=  50)
           swap(pos);

        return pos;
    }

    /**
     * Swap array's elements and so change ship's direction.
     * @param array is array of ship's coordinates,
     *              consists of subarrays of coordinates of cell,
     *              in fact each subarray is pair (i, j).
     *
     */
    public static void swap(int[][] array) {
        int n = array.length;
        int temp;
        for (int i = 0; i < n; ++i) {
            temp = array[i][0];
            array[i][0] = array[i][1];
            array[i][1] = temp;
        }
    }

}