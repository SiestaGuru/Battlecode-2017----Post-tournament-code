package sootforcer;

import battlecode.common.*;
import sootforcer.BigString;
import sootforcer.C;
import sootforcer.DataStructures.BroadcastListMapLocationsHighPrecision;
import sootforcer.DistributedComputing;
import sootforcer.R;
import sootforcer.Test;
import kotlin.jvm.internal.Intrinsics;

/**
 * Created by Hermen on 28/5/2017.
 */
public class Map extends sootforcer.R {

    public static final int TILE_UNKNOWN = 0;
    public static final int TILE_PASSABLE = 1;
    public static final int TILE_OFFMAP = 2;
    public static final int TILE_TREE = 3;
    public static final int TILE_ALLY = 4;
    public static final int TILE_ENEMY = 5;
    public static final int TILE_PARTIAL = 6;



    private static final int TILE_PASSABLE_4 = TILE_PASSABLE << 4;
    private static final int TILE_PASSABLE_8 = TILE_PASSABLE << 8;
    private static final int TILE_PASSABLE_12 = TILE_PASSABLE << 12;

    private static final int TILE_OFFMAP_4 = TILE_OFFMAP << 4;
    private static final int TILE_OFFMAP_8 = TILE_OFFMAP << 8;
    private static final int TILE_OFFMAP_12 = TILE_OFFMAP << 12;

    private static final int TILE_TREE_4 = TILE_TREE << 4;
    private static final int TILE_TREE_8 = TILE_TREE << 8;
    private static final int TILE_TREE_12 = TILE_TREE << 12;

    private static final int TILE_ALLY_4 = TILE_ALLY << 4;
    private static final int TILE_ALLY_8 = TILE_ALLY << 8;
    private static final int TILE_ALLY_12 = TILE_ALLY << 12;

    private static final int TILE_ENEMY_4 = TILE_ENEMY << 4;
    private static final int TILE_ENEMY_8 = TILE_ENEMY << 8;
    private static final int TILE_ENEMY_12 = TILE_ENEMY << 12;

    private static final int TILE_PARTIAL_4 = TILE_PARTIAL << 4;
    private static final int TILE_PARTIAL_8 = TILE_PARTIAL << 8;
    private static final int TILE_PARTIAL_12 = TILE_PARTIAL << 12;




    public static void pushToMapChannel(MapLocation m, int tileType) throws GameActionException{
        int spot = (Math.round(m.x) % 106) + ((Math.round(m.y) % 106) * 106);
        int channel = sootforcer.R.MAP_START +   spot / 4;
        int value = sootforcer.R.rc.readBroadcast(channel);

        switch (spot % 4){
            case 0:
                value = (0b11111111111111111111111111110000 & value) | (tileType);
                break;
            case 1:
                value = (0b11111111111111111111111100001111 & value) | (tileType << 4);
                break;
            case 2:
                value = (0b11111111111111111111000011111111 & value) | (tileType << 8);
                break;
            case 3:
                value = (0b11111111111111110000111111111111 & value) | (tileType << 12);
                break;
        }

        sootforcer.R.rc.broadcast(channel,value);
    }

    public static void pushToMapChannel(int spot, int tileType) throws GameActionException{
        int channel = sootforcer.R.MAP_START +   spot / 4;
        int value = sootforcer.R.rc.readBroadcast(channel);

        switch (spot % 4){
            case 0:
                value = (0b11111111111111111111111111110000 & value) | (tileType);
                break;
            case 1:
                value = (0b11111111111111111111111100001111 & value) | (tileType << 4);
                break;
            case 2:
                value = (0b11111111111111111111000011111111 & value) | (tileType << 8);
                break;
            case 3:
                value = (0b11111111111111110000111111111111 & value) | (tileType << 12);
                break;
        }

        sootforcer.R.rc.broadcast(channel,value);
    }

    public static int getMapSpot(MapLocation m) throws GameActionException{
        int spot = (Math.round(m.x) % 106) + ((Math.round(m.y) % 106) * 106);
        int value = sootforcer.R.rc.readBroadcast(sootforcer.R.MAP_START + spot / 4);

        switch (spot % 4){
            case 0:
                return (0b00000000000000000000000000001111 & value);
            case 1:
                return (0b00000000000000000000000011110000 & value) >>> 4;
            case 2:
                return (0b00000000000000000000111100000000 & value) >>> 8;
            case 3:
                return (0b00000000000000001111000000000000 & value) >>> 12;
        }

        return 0;
    }


    public static int getMapSpot(int x, int y) throws GameActionException{
        int spot = Math.addExact(Math.floorMod(x,106),Math.multiplyExact(Math.floorMod(y,106) , 106));
        switch (Math.floorMod(spot,4)){
            case 0:
                return (0b00000000000000000000000000001111 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4))));
            case 1:
                return (0b00000000000000000000000011110000 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4)))) >>> 4;
            case 2:
                return (0b00000000000000000000111100000000 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4)))) >>> 8;
            case 3:
                return (0b00000000000000001111000000000000 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4)))) >>> 12;
        }
        return 0;
    }

    public static int getMapSpot(int spot) throws GameActionException{
        switch (Math.floorMod(spot,4)){
            case 0:
                return (0b00000000000000000000000000001111 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4))));
            case 1:
                return (0b00000000000000000000000011110000 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4)))) >>> 4;
            case 2:
                return (0b00000000000000000000111100000000 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4)))) >>> 8;
            case 3:
                return (0b00000000000000001111000000000000 & sootforcer.R.rc.readBroadcast(Math.addExact(sootforcer.R.MAP_START,Math.floorDiv(spot,4)))) >>> 12;
        }
        return 0;
    }


    public static void addMapTreeCountEstimation(int doCounts) throws GameActionException{
        int channel = DistributedComputing.getBroadcastChannel(  DistributedComputing.addTask(DistributedComputing.TASK_TREES_ON_MAP,false));
        DistributedComputing.afterEveryTurn();
        sootforcer.R.rc.broadcast(channel + 3, doCounts);
    }



    public static void countTreePercentage(int channel) throws GameActionException {

        int totalCounts = sootforcer.R.rc.readBroadcast(channel + 1);
        float score = sootforcer.R.rc.readBroadcastFloat(channel + 2);

        int minCounts = sootforcer.R.rc.readBroadcast(channel + 3);



        Test.log("working on tree counting task: " +  totalCounts +  " / " + minCounts);


        float width = sootforcer.R.map_right - sootforcer.R.map_left;
        float height = sootforcer.R.map_top - sootforcer.R.map_bot;

        int safety = 0;

        for(; totalCounts < minCounts && Clock.getBytecodesLeft() > 400; totalCounts++){

            float xPos =  sootforcer.R.map_left + (((float)(Clock.getBytecodesLeft() + sootforcer.R.semiRandom)) % width);
            float yPos =  sootforcer.R.map_bot + (((float)Clock.getBytecodesLeft() / 2f) % height);

            MapLocation m = new MapLocation(xPos,yPos);
//            Test.lineTo(m);

            int tile = getMapSpot(m);

            switch(tile){
                case TILE_TREE:
                    score += 1;
                    break;
                case TILE_UNKNOWN:
                    if(sootforcer.R.turn > 50) {
                        totalCounts--; //Wed prefer to jsut not count these
                    }else{
                        score += 0.25f; //But on an unexplored map, we must count at least some or this task will take forever
                    }
                    break;
                case TILE_PARTIAL:
                    score += 0.6f;
                    break;
                case TILE_ALLY:
                case TILE_ENEMY:
                    //These have partial marks next to them, so decrease score a little to compensate
                    score -= 0.1f;
                    break;

            }
        }

        if(totalCounts >= minCounts){
            sootforcer.R.rc.broadcastFloat( sootforcer.R.MAP_TREE_RATIO,   Math.max(0,score / ((float)totalCounts)));
            DistributedComputing.setCompletedAndInactive(channel);
        }else {
            sootforcer.R.rc.broadcast(channel + 1, totalCounts);
            sootforcer.R.rc.broadcastFloat(channel + 2, score);
        }

    }




    public static void addMapAnalysis() throws GameActionException {
        DistributedComputing.addTask(DistributedComputing.TASK_MAP_ANALYSIS,false);
        DistributedComputing.afterEveryTurn();

    }


    private static final float circleDistCheck = 0.5f;
    private static final float partialCheck = 0.24f;

    public static void doMapAnalysis() throws GameActionException{



//        if(!R.amIScout) {
//            MapLocation now = new MapLocation(Math.round(R.myLocation.x), Math.round(R.myLocation.y));
//            MapLocation old1 = new MapLocation(Math.round(R.prevLocation.x), Math.round(R.prevLocation.y));
//            MapLocation old2 = new MapLocation(Math.round(R.beforePrevLocation.x), Math.round(R.beforePrevLocation.y));
//            MapLocation old3 = new MapLocation(Math.round(R.beforeBeforePrevLocation.x), Math.round(R.beforeBeforePrevLocation.y));
//            if (!old1.isWithinDistance(now, 0.5f)) {
//                RobotInfo oldbot = R.rc.senseRobotAtLocation(old1);
//                if (oldbot != null) {
//                    if (oldbot.getTeam().equals(R.ally)) {
//                        R.pushToMapChannel(old1, R.TILE_ALLY);
//                    } else {
//                        R.pushToMapChannel(old1, R.TILE_ENEMY);
//                    }
//                } else {
//                    R.pushToMapChannel(old1, R.TILE_PASSABLE);
//                }
//            }
//
//            if (!old2.isWithinDistance(now, 0.5f)) {
//                RobotInfo oldbot = R.rc.senseRobotAtLocation(old2);
//                if (oldbot != null) {
//                    if (oldbot.getTeam().equals(R.ally)) {
//                        R.pushToMapChannel(old2, R.TILE_ALLY);
//                    } else {
//                        R.pushToMapChannel(old2, R.TILE_ENEMY);
//                    }
//                } else {
//                    R.pushToMapChannel(old2, R.TILE_PASSABLE);
//                }
//            }
//
//            if (!old3.isWithinDistance(now, 0.5f)) {
//                RobotInfo oldbot = R.rc.senseRobotAtLocation(old3);
//                if (oldbot != null) {
//                    if (oldbot.getTeam().equals(R.ally)) {
//                        R.pushToMapChannel(old3, R.TILE_ALLY);
//                    } else {
//                        R.pushToMapChannel(old3, R.TILE_ENEMY);
//                    }
//                } else {
//                    R.pushToMapChannel(old3, R.TILE_PASSABLE);
//                }
//            }
//
//            R.pushToMapChannel(now, R.TILE_ALLY);
//        }




        int startX =  Math.round(sootforcer.R.myLocation.x) - (int)(sootforcer.C.HALF_LARGEST_SQUARE_IN_SIGHT_SIDES) + 1;
        int startY =  Math.round(sootforcer.R.myLocation.y) - (int)(sootforcer.C.HALF_LARGEST_SQUARE_IN_SIGHT_SIDES) + 1;
        int endX = startX + (int)(sootforcer.C.LARGEST_SQUARE_IN_SIGHT_SIDES) - 2;
        int endY = startY + (int)(sootforcer.C.LARGEST_SQUARE_IN_SIGHT_SIDES) - 2;

        int totalYs = endY - startY;

        int yPos =  startY + Math.floorMod(Math.addExact( Clock.getBytecodeNum(),Math.addExact( Clock.getBytecodesLeft(),    Math.multiplyExact(Clock.getBytecodeNum(), Math.floorDiv(Math.addExact(Math.multiplyExact(Clock.getBytecodesLeft(), Clock.getBytecodeNum()), Clock.getBytecodeNum()),Clock.getBytecodesLeft())))) , totalYs);
        int xPos = startX;
        int yCount = 0;
        int yPartOfSpot = Math.multiplyExact(Math.floorMod(yPos,106) , 106);


//        String done = "";

        while(Clock.getBytecodesLeft() > 400){
//            //R.//rc.setIndicatorDot(new MapLocation(xPos,yPos), yCount * 30, 0,0);


            MapLocation m = new MapLocation(xPos,yPos);
//            done = Intrinsics.stringPlus(done,m.toString());

            if(m.isWithinDistance(sootforcer.R.myLocation, sootforcer.C.MAP_ANALYSIS_DISTANCE)) {
                if (sootforcer.R.rc.onTheMap(m)) {
                    if (sootforcer.R.rc.isCircleOccupied(m, circleDistCheck)) {


                        MapLocation spotCheck = sootforcer.R.rc.isLocationOccupied(m) ? m : null;
                        if(spotCheck == null){
                            spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.SOUTH,partialCheck)) ? m.add(Direction.SOUTH,partialCheck) : null;
                            if(spotCheck == null){
                                spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.NORTH,partialCheck)) ? m.add(Direction.NORTH,partialCheck) : null;
                                if(spotCheck == null){
                                    spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.WEST,partialCheck)) ? m.add(Direction.WEST,partialCheck) : null;
                                    if(spotCheck == null){
                                        spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.EAST,partialCheck)) ? m.add(Direction.EAST,partialCheck) : null;
                                    }
                                }
                            }
                        }



                        if (spotCheck != null) {
                            RobotInfo robot = sootforcer.R.rc.senseRobotAtLocation(spotCheck);
                            if (robot != null) {
                                if (robot.getTeam().equals(sootforcer.R.ally)) {
                                    int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                                    int channel = sootforcer.R.MAP_START +   spot / 4;
                                    int value = sootforcer.R.rc.readBroadcast(channel);
                                    switch (spot % 4){
                                        case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_ALLY);break;
                                        case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_ALLY_4); break;
                                        case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_ALLY_8);break;
                                        case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_ALLY_12);break;
                                    }
                                    sootforcer.R.rc.broadcast(channel,value);

//                                    R.pushToMapChannel(spot, R.TILE_ALLY);
                                } else {
                                    int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                                    int channel = sootforcer.R.MAP_START +   spot / 4;
                                    int value = sootforcer.R.rc.readBroadcast(channel);
                                    switch (spot % 4){
                                        case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_ENEMY);break;
                                        case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_ENEMY_4); break;
                                        case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_ENEMY_8);break;
                                        case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_ENEMY_12);break;
                                    }
                                    sootforcer.R.rc.broadcast(channel,value);

//                                    R.pushToMapChannel(spot, R.TILE_ENEMY);
                                }
                            } else {
                                int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                                int channel = sootforcer.R.MAP_START +   spot / 4;
                                int value = sootforcer.R.rc.readBroadcast(channel);
                                switch (spot % 4){
                                    case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_TREE);break;
                                    case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_TREE_4); break;
                                    case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_TREE_8);break;
                                    case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_TREE_12);break;
                                }
                                sootforcer.R.rc.broadcast(channel,value);

//                                R.pushToMapChannel(spot, R.TILE_TREE);
                            }
                        } else {
                            int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                            int channel = sootforcer.R.MAP_START +   spot / 4;
                            int value = sootforcer.R.rc.readBroadcast(channel);
                            switch (spot % 4){
                                case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_PARTIAL);break;
                                case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_PARTIAL_4); break;
                                case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_PARTIAL_8);break;
                                case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_PARTIAL_12);break;
                            }
                            sootforcer.R.rc.broadcast(channel,value);
//                            R.pushToMapChannel(spot, R.TILE_PARTIAL);
                        }
//                         Test.debug_dot(m,0,0,0);
                    } else {
                        int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                        int channel = sootforcer.R.MAP_START +   spot / 4;
                        int value = sootforcer.R.rc.readBroadcast(channel);
                        switch (spot % 4){
                            case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_PASSABLE);break;
                            case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_PASSABLE_4); break;
                            case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_PASSABLE_8);break;
                            case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_PASSABLE_12);break;
                        }
                        sootforcer.R.rc.broadcast(channel,value);
//                        R.pushToMapChannel(spot, R.TILE_PASSABLE);
//                     Test.debug_dot(m,230,255,230);
                    }
                } else {
                    int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                    int channel = sootforcer.R.MAP_START +   spot / 4;
                    int value = sootforcer.R.rc.readBroadcast(channel);
                    switch (spot % 4){
                        case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_OFFMAP);break;
                        case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_OFFMAP_4); break;
                        case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_OFFMAP_8);break;
                        case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_OFFMAP_12);break;
                    }
                    sootforcer.R.rc.broadcast(channel,value);

//                    R.pushToMapChannel(spot, R.TILE_OFFMAP);
//                    Test.debug_dot(m,0,0,0);
                }
            }
            xPos++;
            if(xPos > endX){
                yCount++;
                if(yCount > totalYs) break;
                yPos++;
                if(yPos > endY) yPos = startY;
                xPos = startX;
                yPartOfSpot = Math.multiplyExact(Math.floorMod(yPos,106) , 106);
            }
        }


        startX -= 2;
        startY -= 2;

        totalYs += 4;
        endX += 2;
        yPos = endY + 1;
        endY+=2;



        xPos = startX;
        yCount = 0;
        //If we somehow have resources left, go check some spots that are in the circle but not in the main square
        while(Clock.getBytecodesLeft() > 400){
//            //R.//rc.setIndicatorDot(new MapLocation(xPos,yPos), yCount * 30, 0,0);
//            done = Intrinsics.stringPlus(done,m.toString());
            if(xPos - startX < 2 ^ yPos - startY < 2 ^ endX - xPos < 2 ^ endY - yPos < 2) {
                MapLocation m = new MapLocation(xPos,yPos);

                if(m.isWithinDistance(sootforcer.R.myLocation, C.MAP_ANALYSIS_DISTANCE)) {
                    if (sootforcer.R.rc.onTheMap(m)) {
                        if (sootforcer.R.rc.isCircleOccupied(m, circleDistCheck)) {


                            MapLocation spotCheck = sootforcer.R.rc.isLocationOccupied(m) ? m : null;
                            if(spotCheck == null){
                                spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.SOUTH,partialCheck)) ? m.add(Direction.SOUTH,partialCheck) : null;
                                if(spotCheck == null){
                                    spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.NORTH,partialCheck)) ? m.add(Direction.NORTH,partialCheck) : null;
                                    if(spotCheck == null){
                                        spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.WEST,partialCheck)) ? m.add(Direction.WEST,partialCheck) : null;
                                        if(spotCheck == null){
                                            spotCheck = sootforcer.R.rc.isLocationOccupied(m.add(Direction.EAST,partialCheck)) ? m.add(Direction.EAST,partialCheck) : null;
                                        }
                                    }
                                }
                            }

                            if (spotCheck != null) {
                                RobotInfo robot = sootforcer.R.rc.senseRobotAtLocation(spotCheck);
                                if (robot != null) {
                                    if (robot.getTeam().equals(sootforcer.R.ally)) {
                                        int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                                        int channel = sootforcer.R.MAP_START +   spot / 4;
                                        int value = sootforcer.R.rc.readBroadcast(channel);
                                        switch (spot % 4){
                                            case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_ALLY);break;
                                            case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_ALLY_4); break;
                                            case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_ALLY_8);break;
                                            case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_ALLY_12);break;
                                        }
                                        sootforcer.R.rc.broadcast(channel,value);

//                                    R.pushToMapChannel(spot, R.TILE_ALLY);
                                    } else {
                                        int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                                        int channel = sootforcer.R.MAP_START +   spot / 4;
                                        int value = sootforcer.R.rc.readBroadcast(channel);
                                        switch (spot % 4){
                                            case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_ENEMY);break;
                                            case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_ENEMY_4); break;
                                            case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_ENEMY_8);break;
                                            case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_ENEMY_12);break;
                                        }
                                        sootforcer.R.rc.broadcast(channel,value);

//                                    R.pushToMapChannel(spot, R.TILE_ENEMY);
                                    }
                                } else {
                                    int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                                    int channel = sootforcer.R.MAP_START +   spot / 4;
                                    int value = sootforcer.R.rc.readBroadcast(channel);
                                    switch (spot % 4){
                                        case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_TREE);break;
                                        case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_TREE_4); break;
                                        case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_TREE_8);break;
                                        case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_TREE_12);break;
                                    }
                                    sootforcer.R.rc.broadcast(channel,value);

//                                R.pushToMapChannel(spot, R.TILE_TREE);
                                }
                            } else {
                                int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                                int channel = sootforcer.R.MAP_START +   spot / 4;
                                int value = sootforcer.R.rc.readBroadcast(channel);
                                switch (spot % 4){
                                    case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_PARTIAL);break;
                                    case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_PARTIAL_4); break;
                                    case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_PARTIAL_8);break;
                                    case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_PARTIAL_12);break;
                                }
                                sootforcer.R.rc.broadcast(channel,value);
//                            R.pushToMapChannel(spot, R.TILE_PARTIAL);
                            }
//                            Test.dot(m,0,0,0);
                        } else {
                            int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                            int channel = sootforcer.R.MAP_START +   spot / 4;
                            int value = sootforcer.R.rc.readBroadcast(channel);
                            switch (spot % 4){
                                case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_PASSABLE);break;
                                case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_PASSABLE_4); break;
                                case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_PASSABLE_8);break;
                                case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_PASSABLE_12);break;
                            }
                            sootforcer.R.rc.broadcast(channel,value);
//                        R.pushToMapChannel(spot, R.TILE_PASSABLE);
//                            Test.dot(m,230,255,230);
                        }
                    } else {
                        int spot =  Math.addExact(Math.floorMod(xPos,106),yPartOfSpot);
                        int channel = sootforcer.R.MAP_START +   spot / 4;
                        int value = sootforcer.R.rc.readBroadcast(channel);
                        switch (spot % 4){
                            case 0:value = (0b11111111111111111111111111110000 & value) | (TILE_OFFMAP);break;
                            case 1:value = (0b11111111111111111111111100001111 & value) | (TILE_OFFMAP_4); break;
                            case 2:value = (0b11111111111111111111000011111111 & value) | (TILE_OFFMAP_8);break;
                            case 3:value = (0b11111111111111110000111111111111 & value) | (TILE_OFFMAP_12);break;
                        }
                        sootforcer.R.rc.broadcast(channel,value);

//                    R.pushToMapChannel(spot, R.TILE_OFFMAP);
//                        Test.dot(m,0,0,0);
                    }
                }
                
            }
//            else{
//                MapLocation m = new MapLocation(xPos,yPos);
//
//                Test.dot(m, 100, 50, 200);
//                xPos++;
//
//            }

            xPos++;
            if(xPos > endX){
                yCount++;
                if(yCount > totalYs) break;
                yPos++;
                if(yPos > endY) yPos = startY;
                xPos = startX;
                yPartOfSpot = Math.multiplyExact(Math.floorMod(yPos,106) , 106);
            }
        }

        debug_showMapAnalysis();

    }

    public static void debug_showMapAnalysis() throws GameActionException{
        if(sootforcer.R.semiRandom % 100 == 0) {

            if (Test.LOG_MODE == Test.Modes.MAPANALYSIS) {
                for (float x = Math.round(sootforcer.R.map_left); x <= sootforcer.R.map_right; x += 1) {
                    for (float y = Math.round(sootforcer.R.map_bot); y <= sootforcer.R.map_top; y += 1) {
                        MapLocation m = new MapLocation(x, y);
                        int result = getMapSpot(m);


                        switch (result) {
                            case TILE_UNKNOWN:
//                                //R.//rc.setIndicatorDot(m, 100, 100, 100);
                                break;
                            case TILE_PASSABLE:
                                //R.//rc.setIndicatorDot(m, 255, 255, 255);
                                break;
                            case TILE_TREE:
                                //R.//rc.setIndicatorDot(m, 0, 0, 0);
                                break;
                            case TILE_PARTIAL:
                                //R.//rc.setIndicatorDot(m, 70, 30, 10);
                                break;
                            case TILE_OFFMAP:
                                //Not sure why it keeps placing these on inmap territory
                                //R.//rc.setIndicatorDot(m, 50, 50, 100);
                                break;
                            case TILE_ALLY:
                                //R.//rc.setIndicatorDot(m, 0, 255, 0);
                                break;
                            case TILE_ENEMY:
                                //R.//rc.setIndicatorDot(m, 255, 0, 0);
                                break;
                            default:
                                //Hmm, what is this?
                                //R.//rc.setIndicatorDot(m, 255, 0, 255);
                                break;
                        }


                    }
                }
            }
        }

    }

    
    private static boolean hasPathFindingResult = false;


    private static MapLocation[] path = new MapLocation[300];
    private static float[] scores = new float[300];
    private static int nodeCounter = -1;
    private static float score = 0;


    private static MapLocation start;
    private static MapLocation end;

    private static String pathTracker;
    private static String visitedTracker;

    private static int pushedResultsCounter = 0;
    private static int backtrackcount = 0;

    private static float bestOptionPseudo;
    private static float bestOptionScore;
    private static MapLocation bestNext;

    private static byte[] mapcache;


    public static void pathFinding() throws GameActionException{

        if(hasPathFindingResult) return;

        start = new MapLocation((float)Math.round(sootforcer.R.rc.readBroadcastFloat(sootforcer.R.PATH_FINDING_START_X)) - 0.5f, (float)Math.round(sootforcer.R.rc.readBroadcastFloat(sootforcer.R.PATH_FINDING_START_Y))  - 0.5f);
        MapLocation newEnd = new MapLocation((float)Math.round(sootforcer.R.rc.readBroadcastFloat(sootforcer.R.PATH_FINDING_END_X))  - 0.5f, (float)Math.round(sootforcer.R.rc.readBroadcastFloat(sootforcer.R.PATH_FINDING_END_Y))  - 0.5f);

        float bestScore = getBestScoreSoFar();

        //Gotta reset because the start position has changed / this is the first time were doing pathfinding
        if(nodeCounter < 1 || !start.isWithinDistance(path[0],1) || !newEnd.isWithinDistance(end,1)){
            path[0] = start;
            path[1] = start;
            Test.log("start has changed?");
            reset();
        }
        end = newEnd;

        int counter = 0;
        while(Clock.getBytecodesLeft() > 1900){
//            Test.beginClockTestAvg(5);
            MapLocation currentNode = path[nodeCounter];

            if(nodeCounter > 0) {

                if(currentNode.x < sootforcer.R.map_left || currentNode.x > sootforcer.R.map_right || currentNode.y > sootforcer.R.map_top || currentNode.y < sootforcer.R.map_bot){
                    backTrackTo(nodeCounter - 10);
                    currentNode = path[nodeCounter];
                }
                else {
                    int index = pathTracker.indexOf(currentNode.toString());
                    if (index >= 0) {

                        //Were shortcircuiting here. So: basically remain on the spot we were last time
                        //but eliminate all the nodes in between
                        MapLocation prev = path[nodeCounter-1];

                        Test.dot(prev,70,140,100);
                        Test.dot(path[nodeCounter],70,140,100);
                        //R.//rc.setIndicatorLine(prev,path[nodeCounter],70,140,100);

                        bestOptionScore = -10000;
                        considerOption(prev);
                        float scoreOnSpot = bestOptionScore;
                        backTrackTo((index / 30) + 1);
                        path[++nodeCounter] = prev;
                        scores[nodeCounter] = scoreOnSpot;
                        currentNode = prev;


//                        currentNode = path[nodeCounter];


//                        visitedTracker = visitedTracker.substring(0, Math.max(0, visitedTracker.length() - 30)); //destroy some recent history so that we can revisit parts we shortcircuited in
                    } else {
                        scores[nodeCounter] = score;
                        pathTracker = Intrinsics.stringPlus(pathTracker, String.format("%-29s;", currentNode.toString()));
                        visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());

                        if (currentNode.isWithinDistance(end, 1)) {
                            //Yaaay, we did it boys, fasted path yet!
                            hasPathFindingResult = true;
                            pushedResultsCounter = 0;
                            break;
                        }
                    }
                }
            }

            counter++;

//            Test.log("node: " + nodeCounter);

            bestOptionPseudo = -10000;
            bestOptionScore = -10000;


            considerOption(currentNode.translate(-1,0));
            considerOption(currentNode.translate(1,0));
            considerOption(currentNode.translate(0,1));
            considerOption(currentNode.translate(0,-1));

            if(bestOptionScore <= -800){
//              Test.log("bestoptions too low");
                visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());

                Test.dot(currentNode, 100,200,255);
                backTrackTo(nodeCounter - 1);

            }else{
//
                considerOption(currentNode.translate(-1,-1));
                considerOption(currentNode.translate(-1,1));
                considerOption(currentNode.translate(1,1));
                considerOption(currentNode.translate(1,-1));

                path[++nodeCounter] = bestNext;

                if(score < 500) { // to be sure
                    score += bestOptionScore;
                }

                if(score < bestScore || nodeCounter >= 299){
                    Test.log("score too low");
                    debug_drawpath(true,true);
                    //Okay time to give up, try another path
                    reset();
                }
            }


//            Test.endClockTestAvg(5, "woah expensive");
        }

        Test.log("walked " + counter + "  steps this turn");

        debug_drawpath(true, true);

//        //R.//rc.setIndicatorLine(start,end, 255,100,200);

    }


    private static final float offmapscore = -300f;
    private static final float treescore = -300f;
    private static final float partialscore = -12f;
    private static final float unknownscore = -50f;
    private static final float minusHalf = -0.5f;
//    private static final float offmapscore = -23f;
//    private static final float treescore = -27f;
//    private static final float partialscore = -10f;
//    private static final float unknownscore = -8f;


    public static void  considerOption(MapLocation m) throws GameActionException{
//        if(path[Math.max(nodeCounter -1,0)].equals(m)) return;
        if(path[nodeCounter -1].equals(m)) return;

        float score;
        int pathIndex = pathTracker.indexOf(m.toString());

        if(pathIndex >= 0){
            if((pathIndex / 30) + 6 < nodeCounter ){
                //A shortcircuit opportunity, this is worth exploring
                score = 1000;
            }
            else{
                //We were just there, dont bother
                score = -1000;
            }
        }
        else if(visitedTracker.contains(m.toString())){
            //R.//rc.setIndicatorDot(m,100,100,100);
            score = -1000;
        }
        else{
            score = 12;
            int treeNess = 0;

            float x = m.x;
            float y = m.y;
            int spot1 = Math.addExact(Math.floorMod(Math.round(Float.sum(x,minusHalf)),106),Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y,minusHalf)),106) , 106));
            int spot2 = Math.addExact(Math.floorMod(Math.round(Float.sum(x,minusHalf)),106),Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y,0.5f)),106) , 106));
            int spot3 = Math.addExact(Math.floorMod(Math.round(Float.sum(x,0.5f)),106),Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y,minusHalf)),106) , 106));
            int spot4 = Math.addExact(Math.floorMod(Math.round(Float.sum(x,0.5f)),106),Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y,0.5f)),106) , 106));


//            MapLocation m1 = new MapLocation(Math.round(Float.sum(x,minusHalf)), Math.round(Float.sum(y,minusHalf)));
//            MapLocation m2 = new MapLocation(Math.round(Float.sum(x,minusHalf)), Math.round(Float.sum(y,0.5f)));
//            MapLocation m3 = new MapLocation(Math.round(Float.sum(x,0.5f)), Math.round(Float.sum(y,minusHalf)));
//            MapLocation m4 = new MapLocation(Math.round(Float.sum(x,0.5f)), Math.round(Float.sum(y,0.5f)));
//
//            //rc.setIndicatorLine(m,m1,255,0,0);
//            //rc.setIndicatorLine(m,m2,255,0,0);
//            //rc.setIndicatorLine(m,m3,255,0,0);
//            //rc.setIndicatorLine(m,m4,255,0,0);


            //                switch (cache[xStart][yStart]){

            switch (mapcache[spot1]){
                case TILE_OFFMAP:
                    score = Float.sum(score,offmapscore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_TREE:
                    score = Float.sum(score,treescore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_PARTIAL:
                    score = Float.sum(score,partialscore);
                    treeNess = Math.addExact(treeNess,2);
                    break;
                case TILE_UNKNOWN:
                    score = Float.sum(score,unknownscore);
                    treeNess = Math.addExact(treeNess,1);
                    break;
                case TILE_ALLY:
                case TILE_PASSABLE:
                case TILE_ENEMY:
                    break;
                default:
                    int val = getMapSpot(spot1);
                    mapcache[spot1] = (byte)val;

                    switch (val){
                        case TILE_OFFMAP:
                            score = Float.sum(score,offmapscore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_TREE:
                            score = Float.sum(score,treescore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_PARTIAL:
                            score = Float.sum(score,partialscore);
                            treeNess = Math.addExact(treeNess,2);
                            break;
                        case TILE_UNKNOWN:
                            score = Float.sum(score,unknownscore);
                            treeNess = Math.addExact(treeNess,1);
                            break;
                    }
                    break;
            }
            switch (mapcache[spot2]){
                case TILE_OFFMAP:
                    score = Float.sum(score,offmapscore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_TREE:
                    score = Float.sum(score,treescore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_PARTIAL:
                    score = Float.sum(score,partialscore);
                    treeNess = Math.addExact(treeNess,2);
                    break;
                case TILE_UNKNOWN:
                    score = Float.sum(score,unknownscore);
                    treeNess = Math.addExact(treeNess,1);
                    break;
                case TILE_ALLY:
                case TILE_PASSABLE:
                case TILE_ENEMY:
                    break;
                default:
                    int val = getMapSpot(spot2);
                    mapcache[spot2] = (byte)val;

                    switch (val){
                        case TILE_OFFMAP:
                            score = Float.sum(score,offmapscore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_TREE:
                            score = Float.sum(score,treescore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_PARTIAL:
                            score = Float.sum(score,partialscore);
                            treeNess = Math.addExact(treeNess,2);
                            break;
                        case TILE_UNKNOWN:
                            score = Float.sum(score,unknownscore);
                            treeNess = Math.addExact(treeNess,1);
                            break;
                    }
                    break;
            }
            switch (mapcache[spot3]){
                case TILE_OFFMAP:
                    score = Float.sum(score,offmapscore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_TREE:
                    score = Float.sum(score,treescore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_PARTIAL:
                    score = Float.sum(score,partialscore);
                    treeNess = Math.addExact(treeNess,2);
                    break;
                case TILE_UNKNOWN:
                    score = Float.sum(score,unknownscore);
                    treeNess = Math.addExact(treeNess,1);
                    break;
                case TILE_ALLY:
                case TILE_PASSABLE:
                case TILE_ENEMY:
                    break;
                default:
                    int val = getMapSpot(spot3);
                    mapcache[spot3] = (byte)val;

                    switch (val){
                        case TILE_OFFMAP:
                            score = Float.sum(score,offmapscore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_TREE:
                            score = Float.sum(score,treescore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_PARTIAL:
                            score = Float.sum(score,partialscore);
                            treeNess = Math.addExact(treeNess,2);
                            break;
                        case TILE_UNKNOWN:
                            score = Float.sum(score,unknownscore);
                            treeNess = Math.addExact(treeNess,1);
                            break;
                    }
                    break;
            }
            switch (mapcache[spot4]){
                case TILE_OFFMAP:
                    score = Float.sum(score,offmapscore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_TREE:
                    score = Float.sum(score,treescore);
                    treeNess = Math.addExact(treeNess,4);
                    break;
                case TILE_PARTIAL:
                    score = Float.sum(score,partialscore);
                    treeNess = Math.addExact(treeNess,2);
                    break;
                case TILE_UNKNOWN:
                    score = Float.sum(score,unknownscore);
                    treeNess = Math.addExact(treeNess,1);
                    break;
                case TILE_ALLY:
                case TILE_PASSABLE:
                case TILE_ENEMY:
                    break;
                default:
                    int val = getMapSpot(spot4);
                    mapcache[spot4] = (byte)val;

                    switch (val){
                        case TILE_OFFMAP:
                            score = Float.sum(score,offmapscore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_TREE:
                            score = Float.sum(score,treescore);
                            treeNess = Math.addExact(treeNess,4);
                            break;
                        case TILE_PARTIAL:
                            score = Float.sum(score,partialscore);
                            treeNess = Math.addExact(treeNess,2);
                            break;
                        case TILE_UNKNOWN:
                            score = Float.sum(score,unknownscore);
                            treeNess = Math.addExact(treeNess,1);
                            break;
                    }
                    break;
            }
            if(treeNess > 7) {
                score = -2000;
                Test.debug_dot(m,150,50,200);
            }else{
                score = Float.sum(score, (m.distanceTo(start) / 6f) - ((m.distanceTo(end)) / 2f));
            }
        }

        float pseudo =  Float.sum(score, ((float)Math.floorMod((Math.addExact( m.hashCode(),Clock.getBytecodesLeft())) , 256)) / 404f);
        if(pseudo > bestOptionPseudo){
            bestOptionPseudo = pseudo;
            bestOptionScore = score;
            bestNext = m;
        }
    }

    public static void backTrackTo(int step){
        if(step <= 3 || backtrackcount > 5000){
            Test.log("!!backtracked too much, resetting!!");
            reset();
        }else{
//            Test.log("before:");

//            Test.log(pathTracker);

            pathTracker = pathTracker.substring(0,step * 30);
//            Test.log("after:");

//            Test.log(pathTracker);
            nodeCounter = step;
            score = scores[step];
            backtrackcount++;

            //R.//rc.setIndicatorDot(bestNext, 100,0,0);
        }
    }

    public static void reset(){

        Test.log("resetting");
        score = 0;
        nodeCounter = 1;
        pathTracker = "";
        backtrackcount = 0;
        hasPathFindingResult = false;
        visitedTracker = "";
        mapcache = BigString.amount11236.getBytes();

    }


    public static void debug_drawpath(boolean always, boolean red){
//        if(R.myId == 10658) {
            if (sootforcer.R.myId % 5 == 0 || always) {
                int color = sootforcer.R.myId % 255;
                for (int i = 0; i < nodeCounter - 1; i++) {
                    if (red) {
                        //R.//rc.setIndicatorLine(path[i], path[i + 1], color, 125, 0);
                    } else {
                        //R.//rc.setIndicatorLine(path[i], path[i + 1], 0, 125, color);

                    }
                }
            }
//        }

    }


    public static void debug_drawfinishedpath() throws GameActionException{
        MapLocation[] nodes = BroadcastListMapLocationsHighPrecision.getAll(sootforcer.R.LIST_PATHFINDING_NODES_START);

        for (int i = 0; i < nodes.length - 1; i++) {
            //R.//rc.setIndicatorLine(nodes[i], nodes[i + 1], 100, 10, 170);
        }


    }




    public static float getBestScoreSoFar() throws GameActionException{

        int lastTurn = sootforcer.R.rc.readBroadcast(sootforcer.R.PATH_FINDING_LAST_SET);
        if(lastTurn < 300){
             return sootforcer.R.rc.readBroadcastFloat(sootforcer.R.PATH_FINDING_BEST_SCORE) - (12f * (sootforcer.R.turn - lastTurn));
        }else {
             return sootforcer.R.rc.readBroadcastFloat(sootforcer.R.PATH_FINDING_BEST_SCORE) - (7f * (sootforcer.R.turn - lastTurn));
        }
    }

    public static void broadcastPathfindingResults() throws GameActionException{
        if(hasPathFindingResult && Clock.getBytecodesLeft() > 1000){
            Test.log("Found new best path with score: " + score);
            debug_drawpath(true, false);



            if(score >= getBestScoreSoFar()) {
                sootforcer.R.rc.broadcastFloat(sootforcer.R.PATH_FINDING_BEST_SCORE, score);
                sootforcer.R.rc.broadcast(sootforcer.R.PATH_FINDING_LAST_SET, sootforcer.R.turn);

                while (Clock.getBytecodesLeft() > 400 && (pushedResultsCounter *8) < nodeCounter) {
                    int xchannel = sootforcer.R.LIST_PATHFINDING_NODES_START + 1 +  (2*pushedResultsCounter);
                    sootforcer.R.rc.broadcastFloat(xchannel, path[pushedResultsCounter*8].x);
                    sootforcer.R.rc.broadcastFloat(xchannel + 1, path[pushedResultsCounter*8].y);
                    pushedResultsCounter++;
                }

                if (pushedResultsCounter * 8 >= nodeCounter) {

                    Test.log("finished pushing");

                    sootforcer.R.rc.broadcast(R.LIST_PATHFINDING_NODES_START, pushedResultsCounter * 2);

                   reset();
                }

            }
            else{
                Test.log("failed to push");
                //I guess someone else found a better path in between
                reset();

            }





        }
    }

}

