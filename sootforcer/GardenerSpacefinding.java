package sootforcer;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import sootforcer.BigString;
import sootforcer.Map;
import sootforcer.R;
import sootforcer.Test;
import kotlin.jvm.internal.Intrinsics;

/**
 * Created by Hermen on 29/5/2017.
 */
public class GardenerSpacefinding extends sootforcer.R {

    private static MapLocation[] path = new MapLocation[40];
    private static float[] scores = new float[40];
    private static int nodeCounter = -1;
    private static float score = 0;
    private static float highestScoreReached = 0;

    private static int attempts = 0;


    private static MapLocation start;

    private static String pathTracker;
    private static String visitedTracker;

    private static int pushedResultsCounter = 0;
    private static int backtrackcount = 0;

    private static float bestOptionPseudo;
    private static float bestOptionScore;
    private static MapLocation bestNext;

    private static byte[] mapcache;




    private static boolean hasPathFindingResult =  false;


    private static MapLocation[] startSpots = null;
    private static float[] spotScores = null;
    private static int spots;
    private static int hasCompleted;
    private static int currentlyProcessing;

    private static float baseDistanceToMapCenter = 0;
    private static float sightMinus16;

    public static void init(MapLocation[] startingSpots){
        startSpots = startingSpots;
        spots = startingSpots.length;
        spotScores = new float[spots];
        hasCompleted = 0;
        currentlyProcessing = 0;
        sightMinus16 = sightradius - 1.6f;
        reset();
    }

    public static boolean hasInit(MapLocation[] checkSpots){
        if(spots >0 && checkSpots.length == spots && checkSpots[0].equals(startSpots[0])){
            return true;
        }
        return false;
}

    public static boolean hasCompletedAnalysis(){
        if(hasCompleted >= spots) return true;
        return false;
    }

    public static MapLocation getBestSpot(RobotType robotBuild, float scoreThreshhold){

        if(spots <= 0) return null;
        if(hasCompleted < spots) return null;

        MapLocation bestSpot = null;
        float bestScore = scoreThreshhold;

        for(int i = 0 ; i < spots; i++){
            if (spotScores[i] > bestScore) {
                if(robotBuild == null || sootforcer.R.rc.canBuildRobot(robotBuild, sootforcer.R.myLocation.directionTo(startSpots[i]))) {
                    bestScore = scores[i];
                    bestSpot = startSpots[i];
//                    Test.log("good result");

                }
//                else{
//                    Test.log("build issue");
//
//                }
            }
//            else{
//                Test.log("score issue: " + spotScores[i] + " , " + bestScore);
//            }
        }

        return bestSpot;
    }

    public static MapLocation getBestSpot(RobotType robotBuild, float scoreThreshhold, MapLocation[] ignoreSpots){

        if(spots <= 0) return null;
        if(hasCompleted < spots) return null;

        MapLocation bestSpot = null;
        float bestScore = scoreThreshhold;

        outerloop:
        for(int i = 0 ; i < spots; i++){
            if (spotScores[i] > bestScore) {
                if(robotBuild == null || sootforcer.R.rc.canBuildRobot(robotBuild, sootforcer.R.myLocation.directionTo(startSpots[i]))) {

                    for(int j = 0; j < ignoreSpots.length; j++){
                        if(ignoreSpots[j].isWithinDistance(startSpots[i],0.1f)) continue outerloop;
                    }

                    bestScore = scores[i];
                    bestSpot = startSpots[i];
                }
            }

        }

        return bestSpot;
    }


    public static float getBestScore(RobotType robotBuild, float scoreThreshhold){
        if(spots <= 0) return -10000;
        if(hasCompleted < spots) return -10000;

        MapLocation bestSpot = null;
        float bestScore = scoreThreshhold;

        for(int i = 0 ; i < spots; i++){
            if (spotScores[i] > bestScore) {
                if(robotBuild == null || sootforcer.R.rc.canBuildRobot(robotBuild, sootforcer.R.myLocation.directionTo(startSpots[i]))) {
                    bestScore = scores[i];
                    bestSpot = startSpots[i];
                }
            }
        }
        if(bestScore == scoreThreshhold) return -10000;
        else return bestScore;
    }


    public static void doWork() throws GameActionException{
        if(hasPathFindingResult){

            sootforcer.Test.log("Found score for spot: " + currentlyProcessing + "  :  " + highestScoreReached);
            spotScores[currentlyProcessing] = highestScoreReached;

            hasCompleted++;
            currentlyProcessing++;
            if(currentlyProcessing >= spots){
                currentlyProcessing = 0;
            }
            reset();
        }
        if(spots >0) {
            pathFinding();
        }
    }



    public static void pathFinding() throws GameActionException {

        if(hasPathFindingResult) return;



        //First time were doing pathfinding, gotta set it up
        if(nodeCounter < 1 || path[1] == null){

            reset();
        }

        if(start == null){
            sootforcer.Test.log(startSpots.length + "  length,  processing" + currentlyProcessing );
        }


//        int counter = 0;

        while(Clock.getBytecodesLeft() > 1000){
            MapLocation currentNode = path[nodeCounter];

            if(nodeCounter > 0) {
                if(currentNode.x < sootforcer.R.map_left || currentNode.x > sootforcer.R.map_right || currentNode.y > sootforcer.R.map_top || currentNode.y < sootforcer.R.map_bot){
                    backTrackTo(nodeCounter-1);
                    currentNode = path[nodeCounter];
                }
                else {
                    int index = pathTracker.indexOf(currentNode.toString());
                    if (index >= 0) {

                        visitedTracker = visitedTracker.substring(0, Math.max(0, visitedTracker.length() - 30)); //destroy some recent history so that we can revisit parts we shortcircuited in
                        visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());

                        backTrackTo((index / 30) + 1);
                        currentNode = path[nodeCounter];

//                      Test.log("!!!backtracked from: " + currentNode.toString() + "  to:  " + path[nodeCounter]);
                    } else {
                        scores[nodeCounter] = score;
                        pathTracker = Intrinsics.stringPlus(pathTracker, String.format("%-29s;", currentNode.toString()));
                        visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());
                    }
                }
            }

//            counter++;

//          Test.log("node: " + nodeCounter  +  "score: " + score   + " / " + highestScoreReached);

            bestOptionPseudo = -10000;
            bestOptionScore = -10000;

            considerOption(currentNode.translate(-1,0));
            considerOption(currentNode.translate(1,0));
            considerOption(currentNode.translate(0,1));
            considerOption(currentNode.translate(0,-1));

            if(bestOptionScore <= -800){
                visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());
//                Test.dot(currentNode, 100,200,255);
                backTrackTo(nodeCounter - 1);

//                Test.log("too low");


            }else{
                path[++nodeCounter] = bestNext;

                if(bestOptionScore < 500) {
//                    Test.log("!!SHOULD WORK!!");

                    score += bestOptionScore;
                    if(nodeCounter > 7){
                        if(score > highestScoreReached) {
                            highestScoreReached = score;
                        }
                    }else {
                        if(score / 2 > highestScoreReached) {
                            highestScoreReached = score / 2;
                        }
                    }
                }
//                else{
//                    Test.log("too high");
//                }
            }


            if(nodeCounter > 20){
                backTrackTo(nodeCounter - 10);
            }

            if(++attempts >= 90){
                //Okay time to give up, try another path
                hasPathFindingResult = true;
                debug_drawpath(true,false);
                return;
            }
        }

//        Test.log("walked " + counter + "  steps this turn");

//        debug_drawpath(true, true);

//        //R.//rc.setIndicatorLine(start,end, 255,100,200);

    }


    public static void debug_drawpath(boolean always, boolean red){
//        if(R.myId == 10658) {
//        if (R.myId % 5 == 0 || always) {
        if(sootforcer.Test.LOG_MODE == sootforcer.Test.Modes.MAPANALYSIS) {
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
//        }

    }

    public static void reset(){

        Test.log("resetting");
        score = 0;
        nodeCounter = 1;
        backtrackcount = 0;
        visitedTracker = "";
        hasPathFindingResult = false;
        attempts = 0;
        highestScoreReached = -10000;
        mapcache = BigString.amount11236.getBytes();

        start = startSpots[currentlyProcessing];
        path[0] = start;
        path[1] = start;
        baseDistanceToMapCenter = start.distanceTo(sootforcer.R.mapCenter);
        pathTracker = start.toString() + start.toString();

    }

    public static void backTrackTo(int step){
        attempts++;
        if(step > 2){
            pathTracker = pathTracker.substring(0,  30);
            nodeCounter = step;
            score = scores[step];
            backtrackcount++;
//            //R.//rc.setIndicatorDot(bestNext, 100,0,0);
        }
    }


    private static final float offmapscore = -25f;
    private static final float treescore = -30f;
    private static final float partialscore = -12f;
    private static final float unknownscore = 0f;
    private static final float freeScore = 0.2f;
    private static final float unitScore = -12f;


    private static final float minusHalf = -0.5f;

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
//            //R.//rc.setIndicatorDot(m,100,100,100);
            score = -1000;
        }
        else{


            if(m.isWithinDistance(myLocation, sightMinus16)){
                if(!rc.onTheMap(m, 1.4f)){
                    score = -1000;
                }else {
                    if (rc.isCircleOccupied(m, 1.6f)) {
                        if (rc.isCircleOccupied(m, 0.8f)) {
                            score = -1000; //clearly impassable
//                            Test.dot(m, 100, 0, 0);
                        } else {
                            score = -100f; //partially passable?
//                            Test.dot(m, 0, 100, 0);

                        }
                    } else {
                        score = 0f; //free spot
//                        Test.dot(m, 0, 0, 100);

                    }
                }
            }else {

//            if(m.x < R.map_left || m.x > R.map_right || m.y < R.map_bot || m.y > R.map_top){
//                score = -1000;
//
//            }else {
                score = 12;
                int treeNess = 0;

                float x = m.x;
                float y = m.y;
                int spot1 = Math.addExact(Math.floorMod(Math.round(Float.sum(x, minusHalf)), 106), Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y, minusHalf)), 106), 106));
                int spot2 = Math.addExact(Math.floorMod(Math.round(Float.sum(x, minusHalf)), 106), Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y, 0.5f)), 106), 106));
                int spot3 = Math.addExact(Math.floorMod(Math.round(Float.sum(x, 0.5f)), 106), Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y, minusHalf)), 106), 106));
                int spot4 = Math.addExact(Math.floorMod(Math.round(Float.sum(x, 0.5f)), 106), Math.multiplyExact(Math.floorMod(Math.round(Float.sum(y, 0.5f)), 106), 106));

                //                switch (cache[xStart][yStart]){

                switch (mapcache[spot1]) {
                    case sootforcer.Map.TILE_OFFMAP:
                        score = Float.sum(score, offmapscore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_TREE:
                        score = Float.sum(score, treescore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_PARTIAL:
                        score = Float.sum(score, partialscore);
                        treeNess = Math.addExact(treeNess, 2);
                        break;
                    case sootforcer.Map.TILE_UNKNOWN:
                        score = Float.sum(score, unknownscore);
                        treeNess = Math.addExact(treeNess, 1);
                        break;
                    case sootforcer.Map.TILE_PASSABLE:
                        score = Float.sum(score, freeScore);
                        break;
                    case sootforcer.Map.TILE_ALLY:
                    case sootforcer.Map.TILE_ENEMY:
                        score = Float.sum(score, unitScore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    default:
                        int val = sootforcer.Map.getMapSpot(spot1);
                        mapcache[spot1] = (byte) val;

                        switch (val) {
                            case sootforcer.Map.TILE_OFFMAP:
                                score = Float.sum(score, offmapscore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_TREE:
                                score = Float.sum(score, treescore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_PARTIAL:
                                score = Float.sum(score, partialscore);
                                treeNess = Math.addExact(treeNess, 2);
                                break;
                            case sootforcer.Map.TILE_UNKNOWN:
                                score = Float.sum(score, unknownscore);
                                treeNess = Math.addExact(treeNess, 1);
                                break;
                            case sootforcer.Map.TILE_PASSABLE:
                                score = Float.sum(score, freeScore);
                                break;
                            case sootforcer.Map.TILE_ALLY:
                            case sootforcer.Map.TILE_ENEMY:
                                score = Float.sum(score, unitScore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                        }
                        break;
                }
                switch (mapcache[spot2]) {
                    case sootforcer.Map.TILE_OFFMAP:
                        score = Float.sum(score, offmapscore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_TREE:
                        score = Float.sum(score, treescore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_PARTIAL:
                        score = Float.sum(score, partialscore);
                        treeNess = Math.addExact(treeNess, 2);
                        break;
                    case sootforcer.Map.TILE_UNKNOWN:
                        score = Float.sum(score, unknownscore);
                        treeNess = Math.addExact(treeNess, 1);
                        break;
                    case sootforcer.Map.TILE_PASSABLE:
                        score = Float.sum(score, freeScore);
                        break;
                    case sootforcer.Map.TILE_ALLY:
                    case sootforcer.Map.TILE_ENEMY:
                        score = Float.sum(score, unitScore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    default:
                        int val = sootforcer.Map.getMapSpot(spot2);
                        mapcache[spot2] = (byte) val;

                        switch (val) {
                            case sootforcer.Map.TILE_OFFMAP:
                                score = Float.sum(score, offmapscore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_TREE:
                                score = Float.sum(score, treescore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_PARTIAL:
                                score = Float.sum(score, partialscore);
                                treeNess = Math.addExact(treeNess, 2);
                                break;
                            case sootforcer.Map.TILE_UNKNOWN:
                                score = Float.sum(score, unknownscore);
                                treeNess = Math.addExact(treeNess, 1);
                                break;
                            case sootforcer.Map.TILE_PASSABLE:
                                score = Float.sum(score, freeScore);
                                break;
                            case sootforcer.Map.TILE_ALLY:
                            case sootforcer.Map.TILE_ENEMY:
                                score = Float.sum(score, unitScore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                        }
                        break;
                }
                switch (mapcache[spot3]) {
                    case sootforcer.Map.TILE_OFFMAP:
                        score = Float.sum(score, offmapscore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_TREE:
                        score = Float.sum(score, treescore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_PARTIAL:
                        score = Float.sum(score, partialscore);
                        treeNess = Math.addExact(treeNess, 2);
                        break;
                    case sootforcer.Map.TILE_UNKNOWN:
                        score = Float.sum(score, unknownscore);
                        treeNess = Math.addExact(treeNess, 1);
                        break;
                    case sootforcer.Map.TILE_PASSABLE:
                        score = Float.sum(score, freeScore);
                        break;
                    case sootforcer.Map.TILE_ALLY:
                    case sootforcer.Map.TILE_ENEMY:
                        score = Float.sum(score, unitScore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    default:
                        int val = sootforcer.Map.getMapSpot(spot3);
                        mapcache[spot3] = (byte) val;

                        switch (val) {
                            case sootforcer.Map.TILE_OFFMAP:
                                score = Float.sum(score, offmapscore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_TREE:
                                score = Float.sum(score, treescore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_PARTIAL:
                                score = Float.sum(score, partialscore);
                                treeNess = Math.addExact(treeNess, 2);
                                break;
                            case sootforcer.Map.TILE_UNKNOWN:
                                score = Float.sum(score, unknownscore);
                                treeNess = Math.addExact(treeNess, 1);
                                break;
                            case sootforcer.Map.TILE_PASSABLE:
                                score = Float.sum(score, freeScore);
                                break;
                            case sootforcer.Map.TILE_ALLY:
                            case sootforcer.Map.TILE_ENEMY:
                                score = Float.sum(score, unitScore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                        }
                        break;
                }
                switch (mapcache[spot4]) {
                    case sootforcer.Map.TILE_OFFMAP:
                        score = Float.sum(score, offmapscore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_TREE:
                        score = Float.sum(score, treescore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    case sootforcer.Map.TILE_PARTIAL:
                        score = Float.sum(score, partialscore);
                        treeNess = Math.addExact(treeNess, 2);
                        break;
                    case sootforcer.Map.TILE_UNKNOWN:
                        score = Float.sum(score, unknownscore);
                        treeNess = Math.addExact(treeNess, 1);
                        break;
                    case sootforcer.Map.TILE_PASSABLE:
                        score = Float.sum(score, freeScore);
                        break;
                    case sootforcer.Map.TILE_ALLY:
                    case sootforcer.Map.TILE_ENEMY:
                        score = Float.sum(score, unitScore);
                        treeNess = Math.addExact(treeNess, 4);
                        break;
                    default:
                        int val = sootforcer.Map.getMapSpot(spot4);
                        mapcache[spot4] = (byte) val;

                        switch (val) {
                            case sootforcer.Map.TILE_OFFMAP:
                                score = Float.sum(score, offmapscore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_TREE:
                                score = Float.sum(score, treescore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                            case sootforcer.Map.TILE_PARTIAL:
                                score = Float.sum(score, partialscore);
                                treeNess = Math.addExact(treeNess, 2);
                                break;
                            case sootforcer.Map.TILE_UNKNOWN:
                                score = Float.sum(score, unknownscore);
                                treeNess = Math.addExact(treeNess, 1);
                                break;
                            case sootforcer.Map.TILE_PASSABLE:
                                score = Float.sum(score, freeScore);
                                break;
                            case sootforcer.Map.TILE_ALLY:
                            case Map.TILE_ENEMY:
                                score = Float.sum(score, unitScore);
                                treeNess = Math.addExact(treeNess, 4);
                                break;
                        }
                        break;
                }
                if (treeNess > 7) {
                    score = -2000;
                } else if (sootforcer.R.mapCenter != null) {
                    score = Float.sum(score, m.distanceTo(start) - (m.distanceTo(R.mapCenter) - baseDistanceToMapCenter));
                } else {
                    score = Float.sum(score, m.distanceTo(start));
                }
            }
//            }
        }
        float pseudo =  Float.sum(score,((float)Math.floorMod((Math.addExact( m.hashCode(),Clock.getBytecodesLeft())) , 256)) / 404 );
        if(pseudo > bestOptionPseudo){
            bestOptionPseudo = pseudo;
            bestOptionScore = score;
            bestNext = m;
        }
    }



}
