package sootforcer.DataStructures;

import battlecode.common.MapLocation;

/**
 * Created by Hermen on 3/6/2017.
 */
public class CycleQueue {

    //Example class, to save bytecodes, just inline verything

    //Make sure that the size is long enough to contain all the variables we want to store. otherwise well overflow
    //And everything will die

    private static MapLocation[] queue;
    private static int headIndex;
    private static int tailIndex;
    private static int lastIndex;


    public CycleQueue(int size){
        queue = new MapLocation[size];
        lastIndex = size - 1;
    }

    public void addStart(MapLocation m){
        headIndex--;
        if(headIndex < 0) headIndex = lastIndex;
        queue[headIndex] = m;
    }
    public void addEnd(MapLocation m){
        tailIndex++;
        if(tailIndex > lastIndex) tailIndex = 0;
        queue[tailIndex] = m;
    }

    public MapLocation peekStart(){
        if(tailIndex == headIndex) return null;
        return queue[headIndex];
    }
    public MapLocation peekEnd(){
        if(tailIndex == headIndex) return null;
        return queue[tailIndex];
    }

    public MapLocation popStart(){
        if(tailIndex == headIndex) return null;
        MapLocation returnval = queue[headIndex];
        headIndex++;
        if(headIndex > lastIndex) headIndex = 0;
        return returnval;
    }
    public MapLocation popEnd(){
        if(tailIndex == headIndex) return null;
        MapLocation returnval = queue[tailIndex];
        tailIndex--;
        if(tailIndex < 0) tailIndex = lastIndex;
        return returnval;
    }

    public void removeAll(){
        tailIndex = headIndex;
    }


}
