package sootforcer;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import sootforcer.Map;
import sootforcer.R;
import sootforcer.Test;

/**
 * Created by Hermen on 28/5/2017.
 */
public class DistributedComputing {

    public static final int first = sootforcer.R.DISTRIBUTED_COMPUTING;

    public static final int variablesPerTask = 32;

    private static int controllerVal;
    private static int originalControllerVal;



    private static int STATUS_RUNNING = 0;
    private static int STATUS_COMPLETED = 1;




    private final static int TASK_COUNTING = 2;
    public final static int TASK_MAP_ANALYSIS = 3;
    public final static int TASK_TREES_ON_MAP = 4;




    //Sets a task up. Responsibility of setting initial parameters lies elsewhere
    public static int addTask(int type, boolean highPriority) throws GameActionException{

        int task;

        if(highPriority) {
            task=findFirstInactiveTask();
        }else{
            task=findLastInactiveTask();
        }
        setActive(task);

        //Bytes  21-9 contain the last turn the task was worked on
        //Bytes 8-2 contain the type of the task
        //Bytes 1-0  contain the status of the task
        int statusTracker = ((sootforcer.R.turn &  0b111111111111) << 8) | ((type & 0b111111) << 2)  | STATUS_RUNNING;


        sootforcer.Test.log("Added task: " + task);

        sootforcer.R.rc.broadcast(getBroadcastChannel(task), statusTracker);
        sootforcer.R.rc.broadcast(first,controllerVal);

        return task;
    }

    public static void finishTask(int task) throws GameActionException{
        sootforcer.R.rc.broadcast(getBroadcastChannel(task),0);
        setInactive(task);
    }


    public static void doWork() throws Exception{

        int task = findFirstActiveTask();

        while(Clock.getBytecodesLeft() > 500 && task >= 0){
            int startBroadCast = getBroadcastChannel(task);
            int taskDetails = sootforcer.R.rc.readBroadcast(startBroadCast);


            int status = (taskDetails & 0b11);
            if( status == STATUS_RUNNING){
                int taskType = (taskDetails >>> 2) & 0b111111;

//                Test.log("Working on task: " +  task + " type: " + taskType);

                switch (taskType){
                    case TASK_COUNTING:
                        doCountingTask(startBroadCast);
                        break;
                    case TASK_MAP_ANALYSIS:
                        sootforcer.Map.doMapAnalysis();
                        break;
                    case TASK_TREES_ON_MAP:
                        Map.countTreePercentage(startBroadCast);
                        break;

                }


            }else if(status == STATUS_COMPLETED){
                //Cleaning up old tasks in case their owners wont
                int turn = (taskDetails >>> 8) & 0b111111111111;
                if(sootforcer.R.turn - turn > 5){
                    setInactive(task);
                    sootforcer.Test.log("Had to clean task " + task);
                }
            }
            else{
                //Unknown status
                setInactive(task);
                sootforcer.Test.log("Had to clean task " + task + "  because unknown status");
            }

            task = findFirstActiveTask(task);
        }

        afterEveryTurn();
    }

    public static void beforeEveryTurn() throws GameActionException{
        controllerVal = sootforcer.R.rc.readBroadcast(first);
        originalControllerVal = controllerVal;
    }

    public static void afterEveryTurn() throws GameActionException{
        if(originalControllerVal != controllerVal){
            sootforcer.R.rc.broadcast(first,controllerVal);
        }
    }

    public static int findFirstInactiveTask() throws GameActionException{

        for(int i = 0; i < 32; i++){
            if((controllerVal & (0b1 << i)) == 0) return i;
        }
        return -1;
    }
    public static int findLastInactiveTask() throws GameActionException{

        for(int i = 31; i >= 0; i--){
            if((controllerVal & (0b1 << i)) == 0) return i;
        }
        return -1;
    }


    //Doesnt use the temp variable, because we dont want a robot placing a task to then immediately start calculating with it
    //Since broadcasts are annoying in that they dont happen straight away
    public static int findFirstActiveTask() throws GameActionException{
        for(int i = 0; i < 32; i++){
            if((originalControllerVal & (0b1 << i)) != 0) return i;
        }
        return -1;
    }

    public static int findFirstActiveTask(int startFrom) throws GameActionException{
        for(int i = startFrom + 1; i < 32; i++){
            if((originalControllerVal & (0b1 << i)) != 0) return i;
        }
        return -1;
    }


    public static void setActive(int task){
        controllerVal |= (0b1 << task);
    }


    public static void setInactive(int task){
        int i = task;
        sootforcer.Test.log("before:  " + controllerVal);
        controllerVal -= controllerVal & (0b1 << i);
        sootforcer.Test.log("fter: " + controllerVal);
    }


    public static void setCompleted(int broadcastId) throws GameActionException{
        sootforcer.R.rc.broadcast(broadcastId,(sootforcer.R.rc.readBroadcast(broadcastId) & 0b11111111111111111111111111111100) | STATUS_COMPLETED);

    }
    public static void setCompletedAndInactive(int broadcastId) throws GameActionException{
        setInactive(getTaskFromChannel(broadcastId));
        setCompleted(broadcastId);
    }


    public static int getBroadcastChannel(int task){
        return  first +  1 + (task * variablesPerTask);
    }
    public static int getTaskFromChannel(int channel){
        return channel - (first+1) / variablesPerTask;
    }










    public static void addCountingTask(int upTo, int start) throws GameActionException{

        int broadcastID = getBroadcastChannel(addTask(TASK_COUNTING,false));


        sootforcer.R.rc.broadcast( broadcastID + 1, upTo);
        sootforcer.R.rc.broadcast( broadcastID + 2, start);
    }

    public static void doCountingTask(int broadcastId) throws GameActionException{
        int end = sootforcer.R.rc.readBroadcastInt(broadcastId + 1);
        int current = sootforcer.R.rc.readBroadcastInt(broadcastId + 2);


        for(; current < end && Clock.getBytecodesLeft() > 200; current++){
            if(current % 100 == 0) {
                sootforcer.Test.log("Counting: " + current + "  / " + end);
            }
        }

        if(current >= end){
            setCompleted(broadcastId);
            Test.log("completed counting up to " + end);
        }
        R.rc.broadcast(broadcastId + 2, current);
    }




}
