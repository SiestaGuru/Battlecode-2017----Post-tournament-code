package sootforcer.DataStructures;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import sootforcer.R;

/**
 * Created by Hermen on 22/4/2017.
 */
public class BroadcastMapLocsHighRolling {

    //Not as cheap as some of the other lists, but pretty useful in that it can track everything that has been added to it
    //since last turn by other units.

    //Make sure to call onTurnStart and onTurnEnd every turn.
    //OnTurnStart is necessary for both pushing and getall
    //OnTurnEnd is only necessary for getall
    //So yeah, you can get weird results if you go over a turns bytecode limit sometimes

    public int firstElementPointer;
    public int lastElementPointer;
    public int tempLastElementPointer;
    public int startChannel;
    public int endChannel;
    public int finalElement;


    //firstelementpointer is exclusive, lastelement pointer is inclusive.. yeah i dont remember why either
    public BroadcastMapLocsHighRolling(int startChannel, int endChannel){
        this.startChannel = startChannel;
        this.endChannel = endChannel;

        try {
            lastElementPointer = R.rc.readBroadcast(startChannel);
        }catch (Exception e){}
        tempLastElementPointer = lastElementPointer;
        firstElementPointer = lastElementPointer;

        finalElement = (endChannel - (startChannel + 1)) - 2;

    }

    public void push(MapLocation val) throws Exception
    {
        tempLastElementPointer += 2;
        if( tempLastElementPointer  >= finalElement) {
            tempLastElementPointer = 0;
        }

        R.rc.broadcastFloat(startChannel + tempLastElementPointer+1, val.x);
        R.rc.broadcastFloat(startChannel + tempLastElementPointer+2, val.y);
        R.rc.broadcast(startChannel, tempLastElementPointer);
//        Test.log("pushing last: " + tempLastElementPointer);
    }


    public void onTurnStart() throws Exception{
        lastElementPointer = R.rc.readBroadcast(startChannel);
        tempLastElementPointer = lastElementPointer;

//        Test.log("last pointer: " + lastElementPointer);

    }


    public int size(){
        if(lastElementPointer < firstElementPointer){
            return (lastElementPointer  +   (finalElement - firstElementPointer)) / 2;
        }else{
            return  (lastElementPointer - firstElementPointer)/2;
        }

    }

    public void onTurnEnd(){
        firstElementPointer = tempLastElementPointer;
    }


    public MapLocation[] getAll() throws Exception{

        MapLocation[] returnval;

//        Test.log("getall - first: " +  firstElementPointer +  " lastPointer: " + lastElementPointer + " final: " + finalElement);

        if(lastElementPointer < firstElementPointer){
            returnval = new MapLocation[(( finalElement - firstElementPointer) + lastElementPointer)/2];
            int counter = 0;

            for(int i = firstElementPointer + 2; i < finalElement; i+=2){
                returnval[counter++] = new MapLocation(R.rc.readBroadcastFloat(startChannel + i + 1), R.rc.readBroadcastFloat(startChannel + i + 2));
            }
            for(int i = 0; i <= lastElementPointer; i+=2){
                returnval[counter++] = new MapLocation(R.rc.readBroadcastFloat(startChannel + i + 1), R.rc.readBroadcastFloat(startChannel + i + 2));
            }

//            Test.log("this is wrong!");

        }else{
            int counter = 0;
            returnval = new MapLocation[(lastElementPointer- firstElementPointer)/2];
            for(int i = firstElementPointer + 2; i <= lastElementPointer; i+=2){
                returnval[counter++] = new MapLocation(R.rc.readBroadcastFloat(startChannel + i + 1), R.rc.readBroadcastFloat(startChannel + i + 2));
            }
        }

        return returnval;
    }

    public MapLocation[] getAll(int max) throws GameActionException{

        MapLocation[] returnval;

//        Test.log("getall - first: " +  firstElementPointer +  " lastPointer: " + lastElementPointer + " final: " + finalElement);

        if(lastElementPointer < firstElementPointer){
            returnval = new MapLocation[Math.min(((finalElement - firstElementPointer) + lastElementPointer)/2,max)];
            int counter = 0;

            for(int i = firstElementPointer + 2; i < finalElement; i+=2){
                returnval[counter++] = new MapLocation(R.rc.readBroadcastFloat(startChannel + i + 1), R.rc.readBroadcastFloat(startChannel + i + 2));
                if(counter >= max) return returnval;
            }
            for(int i = 0; i <= lastElementPointer; i+=2){
                returnval[counter++] = new MapLocation(R.rc.readBroadcastFloat(startChannel + i + 1), R.rc.readBroadcastFloat(startChannel + i + 2));
                if(counter >= max) return returnval;
            }

//            Test.log("this is wrong!");

        }else{
            int counter = 0;
            returnval = new MapLocation[Math.min((lastElementPointer- firstElementPointer)/2,max)];
            for(int i = firstElementPointer + 2; i <= lastElementPointer; i+=2){
                returnval[counter++] = new MapLocation(R.rc.readBroadcastFloat(startChannel + i + 1), R.rc.readBroadcastFloat(startChannel + i + 2));
                if(counter >= max) return returnval;
            }
        }

        return returnval;
    }
}
