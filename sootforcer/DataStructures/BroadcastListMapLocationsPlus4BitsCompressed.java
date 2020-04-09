package sootforcer.DataStructures;

import battlecode.common.MapLocation;
import sootforcer.Compression;
import sootforcer.R;

/**
 * Created by Hermen on 25/5/2017.
 */
public class BroadcastListMapLocationsPlus4BitsCompressed {

    //Can probably be made more precise if instead of straight x and y values, we use an anchor point
    //not at 0,0.     now we have to do be able to have vals from 0 to 1024 to nsure it works on all maps
    //but with a different anchor, we might only need from 0 to 128, giving 4 more bits of precision
    public static void push(int startChannel, int endChannel, MapLocation m, int info) throws Exception
    {
        int size =  R.rc.readBroadcast(startChannel)+1;
        if(startChannel + size  <= endChannel) {
            R.rc.broadcast(startChannel + size, ((int)(m.x * 8f)) << 18 | ((int)(m.y*8f)) << 4 | info);
            R.rc.broadcast(startChannel, size);
        }
    }
    public static void push(int startChannel, int endChannel, MapLocation m, boolean bool1, boolean bool2, boolean bool3, boolean bool4) throws Exception
    {
        int size =  R.rc.readBroadcast(startChannel)+1;
        if(startChannel + size  <= endChannel) {
            R.rc.broadcast(startChannel + size,  ((int)(m.x * 8f)) << 18 | ((int)(m.y*8f)) << 4 | (bool1?8:0) | (bool2?4:0) | (bool3?2:0) | (bool4?1:0));
            R.rc.broadcast(startChannel, size);
        }
    }



    //Better not call this if above size, reintroduces old variables
    public static void set(int startChannel, int endChannel, int index,  MapLocation m, int info) throws Exception
    {
        if(index <  endChannel - startChannel) {
            int size = R.rc.readBroadcast(startChannel);
            if(index >= size){
                R.rc.broadcast(startChannel, index + 1);
            }
            R.rc.broadcast(startChannel + 1  + index, ((int)(m.x * 8f)) << 18 | ((int)(m.y*8f)) << 4 | info );
        }
    }

    //Better not call this if above size, reintroduces old variables
    public static void set(int startChannel, int endChannel, int index,  MapLocation m, boolean bool1, boolean bool2, boolean bool3, boolean bool4) throws Exception
    {
        if(index <  endChannel - startChannel) {
            int size = R.rc.readBroadcast(startChannel);
            if(index >= size){
                R.rc.broadcast(startChannel, index + 1);
            }
            R.rc.broadcast(startChannel + 1  + index, ((int)(m.x * 8f)) << 18 | ((int)(m.y*8f)) << 4 | (bool1?8:0) | (bool2?4:0) | (bool3?2:0) | (bool4?1:0));
        }
    }


    public static MapLocation getExample(int startChannel, int index) throws Exception
    {
        int broadcast =  R.rc.readBroadcast(startChannel + 1 + index);


        int info = broadcast % 16;
        boolean bool1 = (broadcast & 8) != 0;
        boolean bool2 = (broadcast & 4) != 0;
        boolean bool3 = (broadcast & 2) != 0;
        boolean bool4 = (broadcast & 1) != 0;

        return new MapLocation (   ((float)(broadcast >>> 18))/8f    , ((float)((broadcast >>> 4) % Compression.index14)/8f));
    }

    public static MapLocation[] getAllExample(int startChannel) throws Exception{
        int size =  R.rc.readBroadcast(startChannel);
        MapLocation[] returnval = new MapLocation[size];
        for(int i = 0 ; i < size; i++){
            int broadcast =  R.rc.readBroadcast(startChannel + 1 + i);

            int info = broadcast % 16;
            boolean bool1 = (broadcast & 8) != 0;
            boolean bool2 = (broadcast & 4) != 0;
            boolean bool3 = (broadcast & 2) != 0;
            boolean bool4 = (broadcast & 1) != 0;


            returnval[i] = new MapLocation (  ((float)(broadcast >>> 18))/8f    , ((float)((broadcast >>> 4) % Compression.index14)/8f));
        }
        return returnval;
    }

    public static void clear(int startChannel) throws Exception{
        R.rc.broadcast(startChannel, 0);
    }
}
