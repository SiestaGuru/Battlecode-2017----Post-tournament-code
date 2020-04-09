package sootforcer.DataStructures;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import sootforcer.R;

/**
 * Created by Hermen on 22/4/2017.
 */
public class BroadcastListMapLocationsHighPrecision {

    public static void push(int startChannel, int endChannel, MapLocation val) throws GameActionException
    {
        int size =  R.rc.readBroadcast(startChannel);
        if(startChannel + size + 1  <= endChannel) {

            R.rc.broadcastFloat(startChannel + size+1, val.x);
            R.rc.broadcastFloat(startChannel + size+2, val.y);
            R.rc.broadcast(startChannel, size + 2);
        }
    }

    //Better not call this if above size, reintroduces old variables
    public static void set(int startChannel, int endChannel, int index, MapLocation val) throws GameActionException
    {
        index*=2;
        if(index <  endChannel - startChannel) {
            int size = R.rc.readBroadcast(startChannel);
            if(index > size){
                R.rc.broadcast(startChannel, index + 2);
            }
            R.rc.broadcastFloat(startChannel + index, val.x);
            R.rc.broadcastFloat(startChannel + 1  + index, val.y);
        }
    }
    public static MapLocation get(int startChannel, int index) throws GameActionException
    {
        index *= 2;
        return  new MapLocation(R.rc.readBroadcastFloat(startChannel + index), R.rc.readBroadcastFloat(startChannel + index + 1));
    }

    public static MapLocation[] getAll(int startChannel) throws GameActionException{
        int size =  R.rc.readBroadcast(startChannel);
        MapLocation[] returnval = new MapLocation[size/2];
        for(int i = 0 ; i < size; i+=2){
            returnval[i/2] = new MapLocation(R.rc.readBroadcastFloat(startChannel + i + 1), R.rc.readBroadcastFloat(startChannel + i + 2));
        }
        return returnval;
    }

    public static void clear(int startChannel) throws GameActionException{
        R.rc.broadcast(startChannel, 0);
    }


}
