package sootforcer;//package sootforcer;
//
//import battlecode.common.Clock;
//import battlecode.common.GameActionException;
//import battlecode.common.MapLocation;
//import battlecode.common.RobotInfo;
//import kotlin.jvm.internal.Intrinsics;
//
///**
// * Created by Hermen on 28/5/2017.
// */
//public class MapOld {
//
//    public static void addMapAnalysis() throws GameActionException {
//        DistributedComputing.getBroadcastChannel(DistributedComputing.addTask(DistributedComputing.TASK_MAP_ANALYSIS,false));
//        DistributedComputing.afterEveryTurn();
//    }
//    public static void doMapAnalysis() throws GameActionException{
//
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
//
//
//
//
//
//        while(Clock.getBytecodesLeft() > 300){
//            float x = ((Clock.getBytecodesLeft() * 2.2f) % (C.MAP_ANALYSIS_DISTANCE_2x)) - C.MAP_ANALYSIS_DISTANCE;
//            float y = ((Clock.getBytecodesLeft() + R.semiRandom) % (C.MAP_ANALYSIS_DISTANCE_2x)) - C.MAP_ANALYSIS_DISTANCE;
//
//            MapLocation temp = R.myLocation.translate(x,y);
//            MapLocation m = new MapLocation(Math.round(temp.x), Math.round(temp.y));
//
//            if(m.isWithinDistance(R.myLocation,C.MAP_ANALYSIS_DISTANCE)){
////                Test.dot(m);
//
////                Test.log("channel: " + broadcastchannel);
//                if(R.rc.onTheMap(m)){
//
//                    if(R.rc.isCircleOccupied(m,1)){
//                        if(R.rc.isLocationOccupied(m)){
//                            RobotInfo robot = R.rc.senseRobotAtLocation(m);
//                            if(robot != null){
//                                if(robot.getTeam().equals(R.ally)){
//                                    R.pushToMapChannel(m,R.TILE_ALLY);
//                                }else{
//                                    R.pushToMapChannel(m,R.TILE_ENEMY);
//                                }
//                            }else{
//                                R.pushToMapChannel(m,R.TILE_TREE);
//                            }
//                        }
//                        else{
//                            R.pushToMapChannel(m,R.TILE_PARTIAL);
//                        }
////                        Test.dot(m,0,0,0);
//                    }
//                    else{
//                        R.pushToMapChannel(m,R.TILE_PASSABLE);
////                        Test.dot(m,230,255,230);
//                    }
//                }
//                else{
//                    R.pushToMapChannel(m,R.TILE_OFFMAP);
////                    Test.dot(m,0,0,0);
//                }
//
//            }
//
//        }
//
//        debug_showMapAnalysis();
//
//    }
//
//    public static void debug_showMapAnalysis() throws GameActionException{
//        if(R.semiRandom % 100 == 0) {
//
//            if (Test.LOG_MODE == Test.Modes.MAPANALYSIS) {
//                for (float x = Math.round(R.map_left); x <= R.map_right; x += 1) {
//                    for (float y = Math.round(R.map_bot); y <= R.map_top; y += 1) {
//                        MapLocation m = new MapLocation(x, y);
//                        int result = R.getMapSpot(m);
//
//
//                        switch (result) {
//                            case R.TILE_UNKNOWN:
////                                //R.//rc.setIndicatorDot(m, 100, 100, 100);
//                                break;
//                            case R.TILE_PASSABLE:
//                                //R.//rc.setIndicatorDot(m, 255, 255, 255);
//                                break;
//                            case R.TILE_TREE:
//                                //R.//rc.setIndicatorDot(m, 0, 0, 0);
//                                break;
//                            case R.TILE_PARTIAL:
//                                //R.//rc.setIndicatorDot(m, 70, 30, 10);
//                                break;
//                            case R.TILE_OFFMAP:
//                                //Not sure why it keeps placing these on inmap territory
//                                //R.//rc.setIndicatorDot(m, 50, 50, 100);
//                                break;
//                            case R.TILE_ALLY:
//                                //R.//rc.setIndicatorDot(m, 0, 255, 0);
//                                break;
//                            case R.TILE_ENEMY:
//                                //R.//rc.setIndicatorDot(m, 255, 0, 0);
//                                break;
//                            default:
//                                //Hmm, what is this?
//                                //R.//rc.setIndicatorDot(m, 255, 0, 255);
//                                break;
//                        }
//
//
//                    }
//                }
//            }
//        }
//
//    }
//
//
//
//    private static boolean hasPathFindingResult = false;
//
//
//    private static MapLocation[] path = new MapLocation[300];
//    private static float[] scores = new float[300];
//    private static int nodeCounter = -1;
//    private static float score = 0;
//
//
//    private static MapLocation start;
//    private static MapLocation end;
//
//    private static String pathTracker;
//    private static String visitedTracker;
//
//    private static int pushedResultsCounter = 0;
//    private static int backtrackcount = 0;
//
//    private static int shortCircuitIndex;
//
//    private static float bestOptionPseudo;
//    private static float bestOptionScore;
//    private static MapLocation bestNext;
//
//
//
//    public static void pathFinding() throws GameActionException{
//
//        if(hasPathFindingResult) return;
//
//        start = new MapLocation((float)Math.round(R.rc.readBroadcastFloat(R.PATH_FINDING_START_X)) - 0.5f, (float)Math.round(R.rc.readBroadcastFloat(R.PATH_FINDING_START_Y))  - 0.5f);
//        MapLocation newEnd = new MapLocation((float)Math.round(R.rc.readBroadcastFloat(R.PATH_FINDING_END_X))  - 0.5f, (float)Math.round(R.rc.readBroadcastFloat(R.PATH_FINDING_END_Y))  - 0.5f);
//
//        float bestScore = getBestScoreSoFar();
//
//        //Gotta reset because the start position has changed / this is the first time were doing pathfinding
//        if(nodeCounter < 0 || !start.isWithinDistance(path[0],1) || !newEnd.isWithinDistance(end,1)){
//            path[0] = start;
//            Test.log("start has changed?");
//            reset();
//        }
//        end = newEnd;
//
//
//        int counter = 0;
//
//
//        while(Clock.getBytecodesLeft() > 1900){
//            Test.beginClockTestAvg(5);
//            MapLocation currentNode = path[nodeCounter];
//
//            if(nodeCounter > 0) {
//
//                if(currentNode.x < R.map_left || currentNode.x > R.map_right || currentNode.y > R.map_top || currentNode.y < R.map_bot){
//                    backTrackTo(nodeCounter - 10);
//                    currentNode = path[nodeCounter];
//                }
//                else {
//                    int index = pathTracker.indexOf(currentNode.toString());
//                    if (index >= 0) {
//                        visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());
//
//                        backTrackTo((index / 30) + 1);
//                        currentNode = path[nodeCounter];
//
//                        visitedTracker = visitedTracker.substring(0, Math.max(0, visitedTracker.length() - 30)); //destroy some recent history so that we can revisit parts we shortcircuited in
//
////                    Test.log("!!!backtracked from: " + currentNode.toString() + "  to:  " + path[nodeCounter]);
//                    } else {
//                        scores[nodeCounter] = score;
//                        pathTracker = Intrinsics.stringPlus(pathTracker, String.format("%-29s;", currentNode.toString()));
//                        visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());
//
//                        if (currentNode.isWithinDistance(end, 1)) {
//                            //Yaaay, we did it boys, fasted path yet!
//                            hasPathFindingResult = true;
//                            pushedResultsCounter = 0;
//                            break;
//                        }
//                    }
//                }
//            }
//
//            counter++;
//
////            Test.log("node: " + nodeCounter);
//
//            bestOptionPseudo = -10000;
//            bestOptionScore = -10000;
//            shortCircuitIndex = -1;
//
//            considerOption(currentNode.translate(-1,0));
//            considerOption(currentNode.translate(1,0));
//            considerOption(currentNode.translate(0,1));
//            considerOption(currentNode.translate(0,-1));
//
//
//            if(bestOptionScore <= -800){
////              Test.log("bestoptions too low");
//                visitedTracker = Intrinsics.stringPlus(visitedTracker, currentNode.toString());
//
//                Test.dot(currentNode, 100,200,255);
//                backTrackTo(nodeCounter - 1);
//
//            }else{
//                considerOption(currentNode.translate(-1,-1));
//                considerOption(currentNode.translate(-1,1));
//                considerOption(currentNode.translate(1,1));
//                considerOption(currentNode.translate(1,-1));
//
//
//
//                path[++nodeCounter] = bestNext;
//                score += bestOptionScore;
//
//                if(score < bestScore || nodeCounter >= 299){
//                    Test.log("score too low");
//                    debug_drawpath(true,true);
//                    //Okay time to give up, try another path
//                    reset();
//                }
//            }
//
//
//            Test.endClockTestAvg(5, "woah expensive");
//        }
//
//        Test.log("walked " + counter + "  steps this turn");
//
//        debug_drawpath(true, true);
//
////        //R.//rc.setIndicatorLine(start,end, 255,100,200);
//
//    }
//
//
//    public static void  considerOption(MapLocation m) throws GameActionException{
//        if(path[Math.max(nodeCounter -1,0)].equals(m)) return;
//
//
//
////        if(m.x > R.map_right || m.x < R.map_left || m.y > R.map_top || m.y < R.map_bot) return;
//        float score = getScoreOf(m);
//        float pseudo = score + ((float)((m.hashCode() + Clock.getBytecodesLeft()) % 256)) / 404f;
//        if(pseudo > bestOptionPseudo){
//            bestOptionPseudo = pseudo;
//            bestOptionScore = score;
//            bestNext = m;
//        }
//    }
//
//    public static void backTrackTo(int step){
//        if(step <= 2 || backtrackcount > 5000){
//            Test.log("!!backtracked too much, resetting!!");
//            reset();
//        }else{
////            Test.log("before:");
//
////            Test.log(pathTracker);
//
//            pathTracker = pathTracker.substring(0,step * 30);
////            Test.log("after:");
//
////            Test.log(pathTracker);
//            nodeCounter = step;
//            score = scores[step];
//            backtrackcount++;
//
//            //R.//rc.setIndicatorDot(bestNext, 100,0,0);
//        }
//    }
//
//    public static void reset(){
//
//        Test.log("resetting");
//        score = 0;
//        nodeCounter = 0;
//        pathTracker = "";
//        backtrackcount = 0;
//        hasPathFindingResult = false;
//        visitedTracker = "";
//    }
//
//
//    public static void debug_drawpath(boolean always, boolean red){
//        if(R.myId == 10658) {
////            if (R.myId % 5 == 0 || always) {
//                int color = R.myId % 255;
//                for (int i = 0; i < nodeCounter - 1; i++) {
//                    if (red) {
//                        //R.//rc.setIndicatorLine(path[i], path[i + 1], color, 125, 0);
//                    } else {
//                        //R.//rc.setIndicatorLine(path[i], path[i + 1], 0, 125, color);
//
//                    }
//                }
////            }
//        }
//
//    }
//
//
//    public static void debug_drawfinishedpath() throws GameActionException{
//        MapLocation[] nodes = BroadcastListMapLocationsHighPrecision.getAll(R.LIST_PATHFINDING_NODES_START);
//
//        for (int i = 0; i < nodes.length - 1; i++) {
//            //R.//rc.setIndicatorLine(nodes[i], nodes[i + 1], 255, 255, 255);
//        }
//
//
//    }
//
//    public static float getScoreOf(MapLocation m) throws GameActionException{
//        int tile1 = R.getMapSpot(m.translate(-0.5f,0.5f));
//        int tile2 = R.getMapSpot(m.translate(-0.5f,-0.5f));
//        int tile3 = R.getMapSpot(m.translate(0.5f,0.5f));
//        int tile4 = R.getMapSpot(m.translate(0.5f,-0.5f));
//
//        float score = ((- m.distanceTo(end)) / 2f)  -(m.distanceTo(start) / 6f)  ;
//
//
//        int pathIndex = pathTracker.indexOf(m.toString());
//
//        if(pathIndex >= 0){
//            if((pathIndex / 30) + 6 < nodeCounter ){
//               //A shortcircuit opportunity, this is worth exploring
//                return 1000;
//            }
//            else{
//                //We were just there, dont bother
//                return -1000;
//            }
//        }
//        else if(visitedTracker.contains(m.toString())){
////        if(m.isWithinDistance(path[nodeCounter - 1],0.5f) || (nodeCounter > 2 && m.isWithinDistance(path[nodeCounter - 3],0.5f) )  ){
//            //prevent backtracing
//
//            //R.//rc.setIndicatorDot(m,100,100,100);
//            return -1000;
//        }
//
//        int treeNess = 0;
//
//        switch (tile1){
//            case R.TILE_OFFMAP:
//                score -= 20;
//                treeNess += 4;
//                break;
//            case R.TILE_TREE:
//                score -= 24f;
//                treeNess += 4;
//                break;
//            case R.TILE_PARTIAL:
//                score -= 7f;
//                treeNess += 2;
//                break;
//            case R.TILE_UNKNOWN:
//                score -= 5f;
//                treeNess += 1;
//                break;
//            default:
//                score += 3f;
//                break;
//        }
//
//        switch (tile2){
//            case R.TILE_OFFMAP:
//                score -= 20;
//                treeNess += 4;
//                break;
//            case R.TILE_TREE:
//                score -= 24f;
//                treeNess += 4;
//                break;
//            case R.TILE_PARTIAL:
//                score -= 7f;
//                treeNess += 2;
//                break;
//            case R.TILE_UNKNOWN:
//                score -= 5f;
//                treeNess += 1;
//                break;
//            default:
//                score += 3f;
//                break;
//        }
//        switch (tile3){
//            case R.TILE_OFFMAP:
//                score -= 20;
//                treeNess += 4;
//                break;
//            case R.TILE_TREE:
//                score -= 24f;
//                treeNess += 4;
//                break;
//            case R.TILE_PARTIAL:
//                score -= 7f;
//                treeNess += 2;
//                break;
//            case R.TILE_UNKNOWN:
//                score -= 5f;
//                treeNess += 1;
//                break;
//            default:
//                score += 3f;
//                break;
//        }
//        switch (tile4){
//            case R.TILE_OFFMAP:
//                score -= 20;
//                treeNess += 4;
//                break;
//            case R.TILE_TREE:
//                score -= 24f;
//                treeNess += 4;
//                break;
//            case R.TILE_PARTIAL:
//                score -= 7f;
//                treeNess += 2;
//                break;
//            case R.TILE_UNKNOWN:
//                score -= 5f;
//                treeNess += 1;
//                break;
//            default:
//                score += 3f;
//                break;
//        }
//
//        if(treeNess > 7) return -1000;
//
//        return score;
//    }
//
//
//    public static float getBestScoreSoFar() throws GameActionException{
//
//        int lastTurn = R.rc.readBroadcast(R.PATH_FINDING_LAST_SET);
//        if(lastTurn < 300){
//             return R.rc.readBroadcastFloat(R.PATH_FINDING_BEST_SCORE) - (12f * (R.turn - lastTurn));
//        }else {
//             return R.rc.readBroadcastFloat(R.PATH_FINDING_BEST_SCORE) - (7f * (R.turn - lastTurn));
//        }
//    }
//
//    public static void broadcastPathfindingResults() throws GameActionException{
//        if(hasPathFindingResult && Clock.getBytecodesLeft() > 1000){
//            Test.log("Found new best path with score: " + score);
//            debug_drawpath(true, false);
//
//
//
//            if(score >= getBestScoreSoFar()) {
//                R.rc.broadcastFloat(R.PATH_FINDING_BEST_SCORE, score);
//                R.rc.broadcast(R.PATH_FINDING_LAST_SET, R.turn);
//
//                while (Clock.getBytecodesLeft() > 400 && pushedResultsCounter < nodeCounter) {
//                    int xchannel = R.LIST_PATHFINDING_NODES_START + 1 +  (2*pushedResultsCounter);
//                    R.rc.broadcastFloat(xchannel, path[pushedResultsCounter].x);
//                    R.rc.broadcastFloat(xchannel + 1, path[pushedResultsCounter].y);
//                    pushedResultsCounter++;
//                }
//
//                if (pushedResultsCounter >= nodeCounter) {
//
//                    Test.log("finished pushing");
//
//                    R.rc.broadcast(R.LIST_PATHFINDING_NODES_START, nodeCounter * 2);
//
//                   reset();
//                }
//
//            }
//            else{
//                Test.log("failed to push");
//                //I guess someone else found a better path in between
//                reset();
//
//            }
//
//
//
//
//
//        }
//    }
//
//}
//
//
//
////
////            int backto1 = considerOption(currentNode.translate(-1,0));
////            if(backto1 < 0){
////                int backto2 = considerOption(currentNode.translate(1,0));
////                if(backto2 < 0){
////                    int backto3 = considerOption(currentNode.translate(0,1));
////                    if(backto3 < 0){
////                        int backto4 = considerOption(currentNode.translate(0,-1));
////                        if(backto4 < 0){
////
////
////                        }
////                        else{
////                            Test.log("hit4: " + backto4);
////                            backTrackTo(backto4 + 1);
////                        }
////                    }
////                    else{
////                        Test.log("hit3: " + backto3);
////
////                        backTrackTo(backto3 + 1);
////                    }
////                }
////                else{
////                    Test.log("hit2: " + backto2);
////
////                    backTrackTo(backto2 + 1);
////                }
////            }
////            else{
////                Test.log("hit1: " + backto1);
////
////                backTrackTo(backto1 + 1);
////            }
//
//
////            MapLocation m1 = currentNode.translate(-1,0);
////            MapLocation m2 = currentNode.translate(1,0);
////            MapLocation m3 = currentNode.translate(0,-1);
////            MapLocation m4 = currentNode.translate(0,1);
////
////
////
////            float score1 = getScoreOf(m1);
////            float score2 = getScoreOf(m2);
////            float score3 = getScoreOf(m3);
////            float score4 = getScoreOf(m4);
////
////            float pseudoScore1 = score1 +  ((float)(Clock.getBytecodesLeft() % 256)) / 1704f;
////            float pseudoScore2 = score2 +  ((float)(Clock.getBytecodesLeft() % 155)) / 1032f;
////            float pseudoScore3 = score3 +  ((float)(Clock.getBytecodesLeft() % 201)) / 1338f;
////            float pseudoScore4 = score4 +  ((float)(Clock.getBytecodesLeft() % 180)) / 1200f;
////
////            if(pseudoScore1 > pseudoScore2){
////                if(pseudoScore1 > pseudoScore3){
////                    if(pseudoScore1 > pseudoScore4){
////                        if(score1 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////
////                            path[nodeCounter] = m1;
////                            score += score1;
////                        }
////                    }else{
////                        if(score4 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////                            path[nodeCounter] = m4;
////                            score += score4;
////                        }
////                    }
////                }
////                else{
////                    if(pseudoScore3 > pseudoScore4){
////                        if(score3 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////                            path[nodeCounter] = m3;
////                            score += score3;
////                        }
////                    }else{
////                        if(score4 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////                            path[nodeCounter] = m4;
////                            score += score4;
////                        }
////                    }
////                }
////            }
////            else{
////                if(pseudoScore2 > pseudoScore3){
////                    if(pseudoScore2 > pseudoScore4){
////                        if(score2 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////                            path[nodeCounter] = m2;
////                            score += score2;
////                        }
////                    }else{
////                        if(score4 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////                            path[nodeCounter] = m4;
////                            score += score4;
////                        }
////                    }
////                }
////                else{
////                    if(pseudoScore3 > pseudoScore4){
////                        if(score3 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////                            path[nodeCounter] = m3;
////                            score += score3;
////                        }
////                    }else{
////                        if(score4 <= -20){
////                            nodeCounter -= 5;
////                            if(++backtrackcount > 5){
////                                reset();
////                            }
////
////                        }else {
////                            path[nodeCounter] = m4;
////                            score += score4;
////                        }
////                    }
////                }
////            }
