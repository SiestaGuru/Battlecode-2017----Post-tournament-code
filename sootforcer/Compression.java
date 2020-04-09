package sootforcer;

import battlecode.common.MapLocation;

/**
 * Created by Hermen on 14/5/2017.
 */


//This shows some techniques to get several points of information into one int
//Not intended to be sued through method calls, costs extra bytecodes
//These are just to indicate how it can all be used
public class Compression {


    public static int index5 = 16;
    public static int index6 = 32;
    public static int index7 = 64;
    public static int index8 = 128;
    public static int index9 = 256;
    public static int index10 = 512;
    public static int index11 = 1024;
    public static int index12 = 2048;
    public static int index13 = 4096;
    public static int index14 = 8192;
    public static int index15 = 16384;
    public static int index16 = 32768;
    public static int index17 = 65536;
    public static int index18 = 131072;
    public static int index19 = 262144;
    public static int index20 = 524288;




    //Only works with positive ints
    public static int twoInts(int x, int y){
        return x << 16 | y;
    }
    public static int retrieveFirstInt(int compressed){
        return compressed >>> 16;
    }
    public static int retrieveSecondInt(int compressed){
        return compressed % index16;
    }


    //Rquires map x/y to be above 0 and below 10000
    public static int mapLocation(MapLocation m){
        return ((int)(m.x * 32f)) << 16 | ((int)(m.y*32f));
    }
    public static MapLocation retrieveMap(int compressed){
        return  new MapLocation( ((float)(compressed >>> 16))/32f  , ((float)(compressed % index16))/32f   );
    }


    //Rquires map x/y to be above 0 and below 10000
    //Info is an int up to 16
    public static int mapLocationWith4InfoBits(MapLocation m, int info){

        return ((int)(m.x * 8f)) << 18 | ((int)(m.y*8f)) << 4 | info;
    }
    public static int mapLocationWith4InfoBits(MapLocation m, boolean bool1, boolean bool2, boolean bool3, boolean bool4){

        return ((int)(m.x * 8f)) << 18 | ((int)(m.y*8f)) << 4 | (bool1?8:0) | (bool2?4:0) | (bool3?2:0) | (bool4?1:0);
    }
    //Not to be used in method form cause it's a pain to access the info
    public static MapLocation exampleRetried4InfoBits(int compressed){
        int info = compressed % 16;
        boolean bool1 = (compressed & 8) != 0;
        boolean bool2 = (compressed & 4) != 0;
        boolean bool3 = (compressed & 2) != 0;
        boolean bool4 = (compressed & 1) != 0;

//        Test.log("info: " + info);
//        Test.log(  (bool1?"Y":"N") + (bool2?"Y":"N") + (bool3?"Y":"N") + (bool4?"Y":"N")  );

        return new MapLocation (   ((float)(compressed >>> 18))/8f    , ((float)((compressed >>> 4) % index14)/8f));
    }





}
