package sootforcer.DataStructures;

import battlecode.common.MapLocation;
import sootforcer.Test;
import kotlin.jvm.internal.Intrinsics;

/**
 * Created by Hermen on 3/6/2017.
 */
public class Dictionary {


    //While relatively cheap like this, there's a significant gain if the functionality here is inlined, using a static data variable
    //Currently there's overhead on calling the object, calling the method, adding parameters, calling the data object inside of the methods
    //It all adds up to like a nedless ~7 bytecode extra per function call


    //This dictionary supports only int values between 0 (inclusive) and 65536 (exclusive)
    //To support negative ints, just add 32768 on add()  and subtract 32768 on get()
    //Or alternatively, since it just wraps around, detect whenever the returned value is above 32768, and then do x - 65536
    //For longer ints, probably need to add extra chars, or paste the entire number in using decimal


    //This dictionary also doesn't support efficient looping. For that, a different structure will have to be used.
    //Perhaps someting that involves both a string for searching, and an array for looping

    public static final int INVALID = -1;
    public String data = "";


    public void add(MapLocation key, int value){
       data = Intrinsics.stringPlus(data, Intrinsics.stringPlus(String.valueOf((char)value),String.format("%-39s",key.toString())));
    }
    public boolean contains(MapLocation m){
        return data.contains(m.toString());
    }

    //Note, returns the value attached to the last maplocation added that matches
    public int get(MapLocation m){
        int index = data.lastIndexOf(m.toString());
        if(index < 0) return INVALID;
        return data.charAt(index-1);
    }
    //Only use this if we know the value is contained in the string
    public int getUnsafe(MapLocation m){
        return data.lastIndexOf(m.toString());
    }



    public int getPosition(MapLocation m){
        return Math.floorDiv( data.lastIndexOf(m.toString()), 40);
    }

    public void remove(MapLocation m){
        int index = data.indexOf(m.toString());
        if(index >= 0){
            data = Intrinsics.stringPlus(data.substring(0,Math.subtractExact(index,1)),data.substring(Math.addExact(index,39)));
        }
    }

    public void reset(){
        data = "";
    }

    public void removeAll(MapLocation m){
        int index = data.indexOf(m.toString());
        while(index >= 0){
            data = Intrinsics.stringPlus(data.substring(0,Math.subtractExact(index,1)),data.substring(Math.addExact(index,39)));
            index = data.indexOf(m.toString());
        }
    }

    public int size(){return Math.floorDiv(data.length(),40);}

    public void debug_print(){
        Test.log(data);
    }



}
