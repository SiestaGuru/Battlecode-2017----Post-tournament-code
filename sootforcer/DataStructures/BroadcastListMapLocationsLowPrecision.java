package sootforcer.DataStructures;

import battlecode.common.MapLocation;
import sootforcer.Compression;
import sootforcer.R;

/**
 * Created by Hermen on 22/4/2017.
 */
public class BroadcastListMapLocationsLowPrecision {


    //This broadcast array uses slightly less bytecode on pushes, and half the channels per item
    //It's not a huge difference, so the high precision one is probably recommended
    //However, by altering this technique a little. We could probably add 2,4,6 or 8 extra bits of information given even lower
    //Precision. That's pretty neat for 1 broadcast channel. (although an equivalent using 3 channels with two floats and an int would
    //serve a similar purpose with more information).

    //Note that it's better to use these straight into the code if bytecode is an issue.
    //The method calls add overhead and make accessing the data a little harder

    //Can probably be made more precise if instead of straight x and y values, we use an anchor point
    //not at 0,0.     now we have to do be able to have vals from 0 to 1024 to nsure it works on all maps
    //but with a different anchor, we might only need from 0 to 128, giving 4 more bits of precision
    public static void push(int startChannel, int endChannel, MapLocation m) throws Exception
    {
        int size =  R.rc.readBroadcast(startChannel);
        size++;
        if(startChannel + size  <= endChannel) {
            R.rc.broadcast(startChannel + size, ((int)(m.x * 32f)) << 16 | ((int)(m.y*32f)));
            R.rc.broadcast(startChannel, size);
        }
    }

    //Better not call this if above size, reintroduces old variables
    public static void set(int startChannel, int endChannel, int index,  MapLocation m) throws Exception
    {

        if(index <  endChannel - startChannel) {
            int size = R.rc.readBroadcast(startChannel);
            if(index >= size){
                R.rc.broadcast(startChannel, index + 1);
            }
            R.rc.broadcast(startChannel + 1  + index, ((int)(m.x * 32f)) << 16 | ((int)(m.y*32f)));
        }
    }
    public static MapLocation get(int startChannel, int index) throws Exception
    {
        int broadcast =  R.rc.readBroadcast(startChannel + 1 + index);
        return  new MapLocation( ((float)(broadcast >>> 16))/32f  , ((float)(broadcast % Compression.index16))/32f   );
    }

    public static MapLocation[] getAll(int startChannel) throws Exception{
        int size =  R.rc.readBroadcast(startChannel);
        MapLocation[] returnval = new MapLocation[size];
        for(int i = 0 ; i < size; i++){
            int broadcast =  R.rc.readBroadcast(startChannel + 1 + i);
            returnval[i] = new MapLocation( ((float)(broadcast >>> 16))/32f  , ((float)(broadcast % Compression.index16))/32f   );
        }
        return returnval;
    }

    public static void clear(int startChannel) throws Exception{
        R.rc.broadcast(startChannel, 0);
    }
}
