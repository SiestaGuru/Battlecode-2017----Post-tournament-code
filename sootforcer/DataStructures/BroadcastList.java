package sootforcer.DataStructures;

import sootforcer.R;

/**
 * Created by Hermen on 22/4/2017.
 */
public class BroadcastList {

    public static void push(int startChannel, int endChannel, int val) throws Exception
    {
        int size =  R.rc.readBroadcast(startChannel);
        size++;
        if(startChannel + size  <= endChannel) {
            R.rc.broadcast(startChannel + size, val);
            R.rc.broadcast(startChannel, size);
        }
    }

    //Better not call this if above size, reintroduces old variables
    public static void set(int startChannel, int endChannel, int index, int val) throws Exception
    {

        if(index <  endChannel - startChannel) {
            int size = R.rc.readBroadcast(startChannel);
            if(index >= size){
                R.rc.broadcast(startChannel, index + 1);
            }
            R.rc.broadcast(startChannel + 1  + index, val);
        }
    }
    public static int get(int startChannel, int index) throws Exception
    {
        return R.rc.readBroadcast(startChannel + 1 + index);
    }

    public static int[] getAll(int startChannel) throws Exception{
        int size =  R.rc.readBroadcast(startChannel);
        int[] returnval = new int[size];
        for(int i = 0 ; i < size; i++){
            returnval[i] = R.rc.readBroadcast(startChannel + 1 + i);
        }
        return returnval;
    }

    public static void clear(int startChannel) throws Exception{
        R.rc.broadcast(startChannel, 0);
    }
}
