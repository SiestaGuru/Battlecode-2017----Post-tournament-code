package sootforcer;

import sootforcer.R;

/**
 * Created by Hermen on 7/6/2017.
 */
public class Adjustables extends sootforcer.R {

    public static int PREDICTIVE_SHOOTING_MODE;

    public static float DIMINISH_2ND;
    public static float DIMINISH_3RD;

    public static float TREE_EXTRA_DESIRE;
    public static float SOLDIER_EXTRA_DESIRE;
    public static float GARDENER_EXTRA_DESIRE;
    public static float LUMBER_EXTRA_DESIRE;
    public static float TANK_EXTRA_DESIRE;
    public static float SCOUT_EXTRA_DESIRE;


    public static float DANGER_FIRST;
    public static float DANGER_2ND;
    public static float DANGER_3RD;

    public static float DODGE_SPOTS_EXTRA;
    public static boolean ALLOW_VP;
    public static int HANDICAP;

    public static void init() {


        PREDICTIVE_SHOOTING_MODE = 12;


        TREE_EXTRA_DESIRE = 30;
        SOLDIER_EXTRA_DESIRE = 5;
        GARDENER_EXTRA_DESIRE = 30;
        LUMBER_EXTRA_DESIRE = 0;
        TANK_EXTRA_DESIRE = 0;
        SCOUT_EXTRA_DESIRE = 0;


        HANDICAP = 0;




        //Seems to be rather clearly the best choice, 31 wins (out of 50) versus the previous standard 5's  24 wins
        //Value 1 also has 30 wins,  while 3 has 24, 10  24
        DODGE_SPOTS_EXTRA = 0;

        //seems to be approx the best, based on a lot of testing, but its rather random
        DIMINISH_2ND = sootforcer.R.maxMove * 0.2f;
        DIMINISH_3RD = R.maxMove * 0.4f;
            //Old:
//      DIMINISH_2ND = R.maxMove * 0.25f;
//      DIMINISH_3RD = R.maxMove * 0.35f;


        //tested the danger first, not the others. seems to possibly peak around 350ish, but rather random again
        DANGER_FIRST = -350;
        DANGER_2ND = -180;
        DANGER_3RD = -100;
        ALLOW_VP = true;

    }
}
