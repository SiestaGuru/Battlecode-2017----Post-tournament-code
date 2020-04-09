package sootforcer;

import battlecode.common.*;
import sootforcer.Builder;
import sootforcer.C;
import sootforcer.Commander;
import sootforcer.M;
import sootforcer.Map;
import sootforcer.MonsterMove;
import sootforcer.R;
import sootforcer.Test;


/**
 * Created by Hermen on 22/4/2017.
 */
public class Archon extends Builder {


    public static boolean stuckFromStart;

    public static int gardenerBuildFailure;
    public static boolean alreadyHaveInitialGardener;



    public static boolean strugglingToFindPlannedSpot = false;


    public static boolean justDeterminedWhoShouldBuildGardener = false;




    public static MapLocation[] registeredMapSpotsInSight = new MapLocation[20];


    public static final float AVOID_GARDENERS_AND_TREES_MICRO = GameConstants.BULLET_TREE_RADIUS + RobotType.ARCHON.bodyRadius + 0.1f;
    public static final float AVOID_GARDENERS_AND_TREES_MICRO_FROM = AVOID_GARDENERS_AND_TREES_MICRO + RobotType.ARCHON.strideRadius;



    public static final float AVOID_GARDENERS_AND_TREES_MACRO =  AVOID_GARDENERS_AND_TREES_MICRO + 4f;
    public static final float AVOID_GARDENERS_AND_TREES_MACRO_FROM = AVOID_GARDENERS_AND_TREES_MACRO + RobotType.ARCHON.strideRadius;


    private static int friendlyTreesNear;


    private static int stat = 0;

    private static int underArrestBySoldiers = 0;
    private static int underArrestByScouts = 0;
    private static int underArrestByLumbers = 0;

    private  static MapLocation[] underArrestUnits = new MapLocation[20];
    private static int underArrestCount = 0;
    private static int enemyScoutCount = 0;

    private static MapLocation plannedOutSpot =null;


    private static int avoidGardenerOnWrongSideCounter = 0;
    private static int lastUpdatedSpaciosness = -100;


    public void step() throws Exception{
        if(turn < Adjustables.HANDICAP) return;


        if(stuckFromStart && !amIStuckGardenerSpaceCheck()){
            stuckFromStart = false;
        }
        friendlyTreesNear = 0;

        robotsEnemy = rc.senseNearbyRobots(sightradius,enemy);
        robotsAlly = rc.senseNearbyRobots(sightradius,ally);


        dealWithRobots();
        dealWithTrees();

        if(!alreadyHaveInitialGardener) {
            shouldWeDoInitialGardener();
        } else{
            normalGardenerCalculations();
        }



        Commander.doCommanderIfNecessary();
        planOutSpots();


        if(sootforcer.Test.LOG_MODE == sootforcer.Test.Modes.BYTECODEEXPERIMENTS && Clock.getBytecodesLeft() > 1500) {
            bytecodeTests();
        }


        sootforcer.Test.log("Trees ratio: " +  rc.readBroadcastFloat(MAP_TREE_RATIO), sootforcer.Test.Modes.MAPANALYSIS);


        if(Clock.getBytecodesLeft() > 6000) {


            stuckCalculations();


            if(mapSize >= MEDIUM) {
                avoidFriendlyArchons();
            }


            if(mapType == CERTAIN_RUSH && turn < 50){
                //In rush maps, running at the enemy at the start a decent idea
                //Allows our gardener to hide more easily
                if(mainTarget != null) {
                    sootforcer.M.addVector(mainTarget, 2);
                }else{
                    sootforcer.M.addVector(mapCenter,2);
                }
            }else {
                sootforcer.M.addVectorCircleZone(mapCenter, (map_right - map_left) / 4f, 0, -3);
                if(mainTarget != null) {
                    sootforcer.M.addVector(mainTarget, -3);
                    sootforcer.M.addVectorCircleZone(mainTarget, (map_right - map_left) / 4f, 0, -3);

                }else{
                    sootforcer.M.addVector(mapCenter,-3);
                }
            }

            if(strugglingToFindPlannedSpot || friendlyTreesNear > 12) {
                sootforcer.M.addVectorCircleZone(prevLocation, 2, 0, -30); //Attempt at defeating local minima, just start moving
                //rc.setIndicatorLine(myLocation,prevLocation, 255,0,0);
                sootforcer.M.addVector(getRandomGoal(),6);
            }
            else{
                sootforcer.M.addVector(getRandomGoal(),1);
            }


            avoidRegisteredSpots();
            dealWithEdges();
            dealWithMacroCircle();

            if(Clock.getBytecodesLeft() > 3000){
                MapLocation[] enemySoldiers = soldierList.getAll(20);
                for(int i = enemySoldiers.length - 1; i >= 0; i--){
                    sootforcer.M.addVectorCircleZone(enemySoldiers[i], 15, -2, -2);
                }
                MapLocation[] enemyTanks = tankList.getAll(20);
                for(int i = enemyTanks.length - 1; i >= 0; i--){
                    sootforcer.M.addVectorCircleZone(enemyTanks[i], 15, 0, -5);
                }
            }


            sootforcer.M.calculateBestMove(800, true);
            sootforcer.M.doBestMove();
        }

        if(myNr == 1){
            rc.broadcast(MY_ARCHON_1_LAST_SPOTTED, turn);
            rc.broadcastFloat(MY_ARCHON_1_X, myX);
            rc.broadcastFloat(MY_ARCHON_1_Y, myY);

            sootforcer.Map.debug_drawfinishedpath();
        } else if(myNr == 2){
            rc.broadcast(MY_ARCHON_2_LAST_SPOTTED, turn);
            rc.broadcastFloat(MY_ARCHON_2_X, myX);
            rc.broadcastFloat(MY_ARCHON_2_Y, myY);
        } else if(myNr == 3){
            rc.broadcast(MY_ARCHON_3_LAST_SPOTTED, turn);
            rc.broadcastFloat(MY_ARCHON_3_X, myX);
            rc.broadcastFloat(MY_ARCHON_3_Y, myY);
        } else{
            //TODO: If were a new archon, check whether we can upgrade to a numbered one

        }
    }




    public void planOutSpots() throws Exception{

        if(turn > 30 && turn % 10 ==0 && Clock.getBytecodesLeft() > 18000) {
            determineTreeShape(false);

            float bestScore;
            MapLocation bestSpot;

            if (treeShape == HEX_SHAPE) {
                bestScore = bestGardenerHexSpotScore;
                bestSpot = bestGardenerHexSpot;
            } else if (treeShape == SQUARE_SHAPE) {
                bestScore = bestGardenerSquareSpotScore;
                bestSpot = bestGardenerSquareSpot;
            } else {
                bestScore = -100000;
                bestSpot = null;
            }

            if(bestScore > 4){
                plannedOutSpot = bestSpot;
            }else{
                plannedOutSpot = null;
                strugglingToFindPlannedSpot = true;
            }

            sootforcer.Test.log("spot planned out");

        }

        if(plannedOutSpot != null){
            strugglingToFindPlannedSpot = false;

            if(mapCenter != null){
                MapLocation sitOnSpot = plannedOutSpot.add(plannedOutSpot.directionTo(mapCenter), GameConstants.GENERAL_SPAWN_OFFSET + radius + sootforcer.C.GARDENER_RADIUS);


                sootforcer.M.addSpecialMapLocation(sitOnSpot, 35);
                sootforcer.M.addVector(plannedOutSpot, 5);
                sootforcer.M.addVector(sitOnSpot, 1);
                sootforcer.M.addSpecialMapLocation(myLocation.add(myLocation.directionTo(sitOnSpot), maxMove), 35);

                sootforcer.Test.lineTo(plannedOutSpot,80,255,190);
            }
        }

    }



    //If we spread, chances ar emuch better we can pump gardeners out
    public void avoidFriendlyArchons() throws GameActionException{

        if(startArchonCount > 1){



            float avoidDistance;
            if(mapSize == TINY){
                avoidDistance = 12;
            } else if(mapSize == SMALL){
                avoidDistance = 16;
            } else if(mapSize == MEDIUM){
                avoidDistance = 22;
            } else if(mapSize == LARGE){
                avoidDistance = 28;
            } else{
                avoidDistance = 35;
            }


            if(myNr == 1){
                if(turn - rc.readBroadcast(MY_ARCHON_2_LAST_SPOTTED) < 5){
                    MapLocation m = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_2_X),rc.readBroadcastFloat(MY_ARCHON_2_Y));
                    if(m.isWithinDistance(myLocation,avoidDistance + 1)) {
                        MonsterMove.addVectorCircle(m, avoidDistance, -1, -2);
                    }
                }
                if(turn - rc.readBroadcast(MY_ARCHON_3_LAST_SPOTTED) < 5){
                    MapLocation m = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_3_X),rc.readBroadcastFloat(MY_ARCHON_3_Y));
                    if(m.isWithinDistance(myLocation,avoidDistance + 1)) {
                        MonsterMove.addVectorCircle(m, avoidDistance, -1, -2);
                    }
                }
            }
            else if(myNr == 2){
                    if(turn - rc.readBroadcast(MY_ARCHON_1_LAST_SPOTTED) < 5){
                        MapLocation m = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_1_X),rc.readBroadcastFloat(MY_ARCHON_1_Y));
                        if(m.isWithinDistance(myLocation,avoidDistance + 1)) {
                            MonsterMove.addVectorCircle(m, avoidDistance, -1, -2);
                        }
                    }
                    if(turn - rc.readBroadcast(MY_ARCHON_3_LAST_SPOTTED) < 5){
                        MapLocation m = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_3_X),rc.readBroadcastFloat(MY_ARCHON_3_Y));
                        if(m.isWithinDistance(myLocation,avoidDistance + 1)) {
                            MonsterMove.addVectorCircle(m, avoidDistance, -1f, -2);
                        }
                    }

            } else{
                if(turn - rc.readBroadcast(MY_ARCHON_1_LAST_SPOTTED) < 5){
                    MapLocation m = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_1_X),rc.readBroadcastFloat(MY_ARCHON_1_Y));
                    if(m.isWithinDistance(myLocation,avoidDistance + 1)) {
                        MonsterMove.addVectorCircle(m, avoidDistance, -1f, -2);
                    }
                }
                if(turn - rc.readBroadcast(MY_ARCHON_2_LAST_SPOTTED) < 5){
                    MapLocation m = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_2_X),rc.readBroadcastFloat(MY_ARCHON_2_Y));
                    if(m.isWithinDistance(myLocation, avoidDistance + 1)) {
                        MonsterMove.addVectorCircle(m, avoidDistance, -1f, -2);
                    }
                }
            }

        }
    }


    public boolean shouldBecomeCommander() throws Exception{
        if(turn == 1){
            if(stuckFromStart){
               return false;
            }
            else{
                if(rc.readBroadcast(COMMANDER_ID) > 0){
                    return false;
                }
                else{
                    return true;
                }
            }
        }
        else{
            if(stuckFromStart){
                if(turn - rc.readBroadcast(COMMANDER_TURN_REPORTED) > 3){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                if(turn - rc.readBroadcast(COMMANDER_TURN_REPORTED) > 2){
                    return true;
                }
                else{
                    return false;
                }
            }

        }
    }


    public void bytecodeTests() throws Exception{

        float test1 = 0;
        float locX = 0;
        float Z1_p21x = 0;
        float locY = 0;
        float Z1_p21y = 0;
        float Z1_p21MagSquared = 0;
        float test2 = 0;
        float Z1_p41x = 0;
        float Z1_p41y = 0;
        float Z1_dotsection2 = 0;
        float Z1_dotsection4 = 0;
        float Z1_p41MagSquared = 0;
        float Z1_desire = 0;
        float Z1_boundcenter1 = 0;
        float Z1_boundcenter2 = 0;
        float Z1_tolerance1 = 0;
        float Z1_tolerance2 = 0;

        MapLocation center = new MapLocation(0,0);
        MapLocation center2 = new MapLocation(0,0);
        MapLocation center3 = myLocation;
        float distallowed = 4;


        sootforcer.Test.beginClockTest(1);
        test1 = Float.sum(locX * Z1_p21x, locY * Z1_p21y);
        if (test1 >= Z1_dotsection2 && test1 <= Z1_p21MagSquared) {
            test2 = Float.sum(locX * Z1_p41x, locY * Z1_p41y);
            if (test2 >= Z1_dotsection4 && test2 <= Z1_p41MagSquared) {
                sootforcer.M.lastZoneValuation = Float.sum(sootforcer.M.lastZoneValuation, Z1_desire);
            }
        }

        sootforcer.Test.endClockTest(1, "old");
        if (Float.intBitsToFloat((Float.floatToIntBits(Float.sum(Float.sum(locX * Z1_p21x , locY * Z1_p21y) , Z1_boundcenter1)) & 0b01111111111111111111111111111111)) <= Z1_tolerance1 && Float.intBitsToFloat((Float.floatToIntBits( Float.sum(Float.sum(locX * Z1_p41x, locY * Z1_p41y) , Z1_boundcenter2)) & 0b01111111111111111111111111111111)) <= Z1_tolerance2) {
            sootforcer.M.lastZoneValuation = Float.sum(sootforcer.M.lastZoneValuation, Z1_desire);
        }


        sootforcer.Test.endClockTest(1, "new");




//        int local = 0;
//        int m = 0;
//
//        boolean a = true;
//
//        boolean b = false;
//
//        int k = 32947843;
//
//        float test1 = 2.3f;
//        float test2;
//        int angledRectangleCount = 0;
//
//        Direction dir = Direction.EAST;
//        float avoidwidth = 1;
//        MapLocation start = myLocation;
//        MapLocation end = myLocation.add(Direction.EAST,20);
//        float desire = 10;




//        if(myNr == 1) {
//            DistributedComputing.addCountingTask(R.turn * 100, 0);
//        }







//        Test.log("before");
//
//        tankList.onTurnStart();
//        tankList.push(myLocation);
//        tankList.push(end);
//
//        MapLocation[] results = tankList.getAll();
//
//        for(int i = 0; i < results.length; i++){
//            Test.log(results[i].toString());
//        }
//
//        tankList.onTurnEnd();
//        Test.log("after");



//        String s= "";
//        String c = " hoi";
//
//        StringBuilder build = new StringBuilder();
//        StringBuffer buffer = new StringBuffer();
//        Test.beginClockTest(1);
//
//        Test.endClockTest(1, "ifcheck asd");
//        Test.log("rand: " + Math.random());
//        Test.beginClockTest(1);
//        if(s.equals(s))m++;
//        Test.endClockTest(1, "ifcheck fd");

//        for(float i = -40; i <= 40; i++){
//            if(StringBasedMapLocationAnalysis.containsNearbyMapLocation2(myLocation.add(Direction.EAST,i/10f))){
//                Test.log("E Y: " + i);
//            }else{
//                Test.log("E N: " + i);
//            }
//
//            if(StringBasedMapLocationAnalysis.containsNearbyMapLocation2(myLocation.add(Direction.SOUTH,i/10f))){
//                Test.log("S Y: " + i);
//            }else{
//                Test.log("S N: " + i);
//            }
//        }


//        for(int i = 0; i < 100; i++) {
//            int rand = Math.floorMod(Math.addExact( Clock.getBytecodeNum(),Math.addExact( Clock.getBytecodesLeft(),    Math.multiplyExact(Clock.getBytecodeNum(), Math.floorDiv(Math.addExact(Math.multiplyExact(Clock.getBytecodesLeft(), Clock.getBytecodeNum()), Clock.getBytecodeNum()),Clock.getBytecodesLeft())))) , 256);
//            Test.log("rand: " + rand);
//        }


//        Stack<MapLocation> nodes = new Stack<>();





//        Test.beginClockTest(1);
//        Test.endClockTest(1, "contains 1");
//
//
//        Test.beginClockTest(1);
//        Test.endClockTest(1, "contains 2");
//
//
////
//        Test.beginClockTest(1);
////        if(  0=> i <= j) l++;
//
////        float yu = Float.sum(i * i, j * j);
//
//        Test.endClockTest(1, "contains 3");
//
//        Test.beginClockTest(1);
////        float g = Math.p
//        Test.endClockTest(1, "contains 4");

//        Test.beginClockTest(1);
//        boolean f = StringBasedMapLocationAnalysis.contains5(m1);
//        Test.endClockTest(1, "contains 5");



//        Test.beginClockTest(1);



//        Test.endClockTest(1, "std");


//        Test.beginClockTest(1);



//        Test.endClockTest(1, "foreach");


//        BulletInfo[] bullets = rc.senseNearbyBullets(7);
//
//
//        zoneLoc = 0;
//        Test.beginClockTest(1);
//        dodgeBullets(bullets);
//
//        Test.endClockTest(1, "oldtest");
//
//        System.out.println("old made: " + zoneLoc);
//
//
//        zoneLoc = 0;
//        Test.beginClockTest(1);
//        dodgeBulletsNew(bullets);
//
//        Test.endClockTest(1, "newtest");
//
//        System.out.println("new made: " + zoneLoc);



    }

    final public  void test1(){
        System.out.println("hoi");
    }
    private static void test2(){
        System.out.println("hoi");

    }

    public void dealWithEdges(){

        if(mapType == CORNER_NO_TREES){
            addDesireToEdges(4,-20,-6);
        } else if(mapType == EXTREMELY_TIGHT){
            if(alreadyHaveInitialGardener || turn < 20 || turn > 60) {
                addDesireToEdges(4, -20, -6);
            }else{
                addDesireToEdges(2, 0, 3);
            }
        } else {
            if(mapSize <= SMALL) {
                addDesireToEdges(sootforcer.C.GARDENER_RADIUS, -10, -3);
            }
            else if(mapSize <= MEDIUM){
                addDesireToEdges(5, -10, -3);
            }else{
                addDesireToEdges(10, 0, -2);
                addDesireToEdges(5, 0, -2);
            }
        }

    }

    public void avoidRegisteredSpots() throws Exception{
        if(Clock.getBytecodesLeft() < 5000) return;

        for(int i = 0 ; i < 20; i++){
            MapLocation spot = registeredMapSpotsInSight[i];
            if(spot == null) continue;
            if(!spot.isWithinDistance(myLocation,sightradius - 0.2f) || rc.isCircleOccupiedExceptByThisRobot(spot,0.1f)){
                registeredMapSpotsInSight[i] = null;
                continue;
            }

            if(spot.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MICRO_FROM)){
                MonsterMove.addVectorCircle(spot,AVOID_GARDENERS_AND_TREES_MICRO,-15,-3);
            }
        }
    }


    public static void registerBestSpot() throws Exception{

        if(treeShape == HEX_SHAPE) {
            if (bestGardenerHexSpot != null) {
                //rc.setIndicatorLine(R.myLocation, bestGardenerHexSpot, 255, 0, 0);
                MapLocation s1 = bestGardenerHexSpot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                MapLocation s2 = bestGardenerHexSpot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                MapLocation s3 = bestGardenerHexSpot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                MapLocation s4 = bestGardenerHexSpot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                MapLocation s5 = bestGardenerHexSpot.translate(sootforcer.C.HEX_LEFT_X_DIF, 0);
                MapLocation s6 = bestGardenerHexSpot.translate(sootforcer.C.HEX_RIGHT_X_DIF, 0);
//            MapLocation s1 = bestGardenerHexSpot.translate(C.HEX_LEFT_X_DIF, C.HEX_RIGHT_OTHER_ROW);
//            MapLocation s2 = bestGardenerHexSpot.translate(C.HEX_LEFT_X_DIF, C.HEX_LEFT_OTHER_ROW);
//            MapLocation s3 = bestGardenerHexSpot.translate(C.HEX_RIGHT_X_DIF, C.HEX_RIGHT_OTHER_ROW);
//            MapLocation s4 = bestGardenerHexSpot.translate(C.HEX_RIGHT_X_DIF, C.HEX_LEFT_OTHER_ROW);
//            MapLocation s5 = bestGardenerHexSpot.translate(0, C.HEX_TOP_Y_DIF);
//            MapLocation s6 = bestGardenerHexSpot.translate(0, C.HEX_BOT_Y_DIF);


                spotsClaimedBuilder.append(String.format(";%8d%8d", (int) bestGardenerHexSpot.x, (int) bestGardenerHexSpot.y));

                spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s1.x, (int) s1.y));
                spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s2.x, (int) s2.y));
                spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s3.x, (int) s3.y));
                spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s4.x, (int) s4.y));
                spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s5.x, (int) s5.y));
                spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s6.x, (int) s6.y));


                int i = 0;

                if(bestGardenerHexSpot.isWithinDistance(myLocation,sightradius) && rc.onTheMap(bestGardenerHexSpot)) {
                    for (; i < 20; i++) {
                        if (registeredMapSpotsInSight[i] == null) {
                            registeredMapSpotsInSight[i] = bestGardenerHexSpot;
                            break;
                        }
                    }
                }
                if(s1.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s1)) {

                    for (; i < 20; i++) {
                        if (registeredMapSpotsInSight[i] == null) {
                            registeredMapSpotsInSight[i] = s1;
                            break;
                        }
                    }
                }
                if(s2.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s2)) {
                    for (; i < 20; i++) {
                        if (registeredMapSpotsInSight[i] == null) {
                            registeredMapSpotsInSight[i] = s2;
                            break;
                        }
                    }
                }
                if(s3.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s3)) {
                    for (; i < 20; i++) {
                        if (registeredMapSpotsInSight[i] == null) {
                            registeredMapSpotsInSight[i] = s3;
                            break;
                        }
                    }
                }
                if(s4.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s4)) {
                    for (; i < 20; i++) {
                        if (registeredMapSpotsInSight[i] == null) {
                            registeredMapSpotsInSight[i] = s4;
                            break;
                        }
                    }
                }
                if(s5.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s5)) {

                    for (; i < 20; i++) {
                        if (registeredMapSpotsInSight[i] == null) {
                            registeredMapSpotsInSight[i] = s5;
                            break;
                        }
                    }
                }
                if(s6.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s6)) {
                    for (; i < 20; i++) {
                        if (registeredMapSpotsInSight[i] == null) {
                            registeredMapSpotsInSight[i] = s6;
                            break;
                        }
                    }
                }


            }
        } else if(treeShape == SQUARE_SHAPE){
            //rc.setIndicatorLine(R.myLocation, bestGardenerSquareSpot, 255, 0, 0);
            MapLocation s1 = bestGardenerSquareSpot.translate(sootforcer.C.SQUARE_SPACING, sootforcer.C.SQUARE_SPACING);
            MapLocation s2 = bestGardenerSquareSpot.translate(0, sootforcer.C.SQUARE_SPACING);
            MapLocation s3 = bestGardenerSquareSpot.translate(-sootforcer.C.SQUARE_SPACING, sootforcer.C.SQUARE_SPACING);
            MapLocation s4 = bestGardenerSquareSpot.translate(sootforcer.C.SQUARE_SPACING, -sootforcer.C.SQUARE_SPACING);
            MapLocation s5 = bestGardenerSquareSpot.translate(0, -sootforcer.C.SQUARE_SPACING);
            MapLocation s6 = bestGardenerSquareSpot.translate(-sootforcer.C.SQUARE_SPACING, -sootforcer.C.SQUARE_SPACING);
            MapLocation s7 = bestGardenerSquareSpot.translate(sootforcer.C.SQUARE_SPACING, 0);
            MapLocation s8 = bestGardenerSquareSpot.translate(-sootforcer.C.SQUARE_SPACING, 0);

            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) bestGardenerSquareSpot.x, (int) bestGardenerSquareSpot.y));

            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s1.x, (int) s1.y));
            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s2.x, (int) s2.y));
            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s3.x, (int) s3.y));
            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s4.x, (int) s4.y));
            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s5.x, (int) s5.y));
            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s6.x, (int) s6.y));
            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s7.x, (int) s7.y));
            spotsClaimedBuilder.append(String.format(";%8d%8d", (int) s8.x, (int) s8.y));


            int i = 0;

            if(bestGardenerSquareSpot.isWithinDistance(myLocation,sightradius) && rc.onTheMap(bestGardenerSquareSpot)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = bestGardenerSquareSpot;
                        break;
                    }
                }
            }
            if(s1.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s1)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s1;
                        break;
                    }
                }
            }
            if(s2.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s2)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s2;
                        break;
                    }
                }
            }
            if(s3.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s3)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s3;
                        break;
                    }
                }
            }
            if(s4.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s4)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s4;
                        break;
                    }
                }
            }
            if(s5.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s5)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s5;
                        break;
                    }
                }
            }
            if(s6.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s6)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s6;
                        break;
                    }
                }
            }
            if(s7.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s7)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s7;
                        break;
                    }
                }
            }
            if(s8.isWithinDistance(myLocation,sightradius) && rc.onTheMap(s8)) {

                for (; i < 20; i++) {
                    if (registeredMapSpotsInSight[i] == null) {
                        registeredMapSpotsInSight[i] = s8;
                        break;
                    }
                }
            }

        }
    }


    public void dealWithTrees() throws Exception{
        if(trees == null) {
            trees = rc.senseNearbyTrees();
        }
        int count = Math.min(trees.length,20);

        boolean alreadyGoingForShakeTree = false;

        int pushedTrees = 0;

        for(int i = 0; i < count; i++){
            TreeInfo tree = trees[i];
            MapLocation loc = tree.location;
            float dist = myLocation.distanceTo(loc);

            if(tree.containedBullets > 0){
                if(rc.canShake(tree.getID())){
                    rc.shake(tree.getID());
                }
                else if(!alreadyGoingForShakeTree && turn < 100){
                    sootforcer.M.addVector(loc, 7);
                    alreadyGoingForShakeTree = true;
                    sootforcer.Test.lineTo(loc);
                }
            }



            if(tree.team.equals(ally)) {
                if (mapType != EXTREMELY_TIGHT) {
                    if (loc.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MACRO_FROM)) {

                        sootforcer.M.addVectorCircleZone(loc, AVOID_GARDENERS_AND_TREES_MACRO, -5, -2);

                        if (loc.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MICRO_FROM)) {

                            sootforcer.M.addVectorCircleZone(loc, AVOID_GARDENERS_AND_TREES_MICRO, -30, -5);
                        }
                    }
                }

                if ((mapType == CERTAIN_RUSH && R.turn < 200) || strugglingToFindPlannedSpot) {
                    sootforcer.M.addVector(loc, -3);
                }

                friendlyTreesNear++;
            }
            else{

                if(!stuckFromStart) {
                    if (dist < 6 && (pushedTrees < 3 || stuckRating > 20 || (plannedOutSpot != null && plannedOutSpot.isWithinDistance(myLocation, 4)))) {
                        lumberTargetsList.push(loc);
                        pushedTrees++;
                        sootforcer.Test.lineTo(loc, 120, 40, 170);

                    } else if (tree.containedRobot != null && (tree.containedRobot == RobotType.LUMBERJACK || tree.containedRobot == RobotType.TANK || tree.containedRobot == RobotType.SOLDIER)) {
                        lumberTargetsList.push(loc);
                        pushedTrees++;
                        sootforcer.Test.lineTo(loc, 120, 40, 170);
                    }
                }

                if(strugglingToFindPlannedSpot){
                    sootforcer.M.addVector(loc, -2);
                }


                if(tree.containedBullets > 0) {
                    alreadyGoingForShakeTree = true;
                    sootforcer.M.addSpecialMapLocationUnchecked(myLocation.add(myLocation.directionTo(loc),maxMove),2);
                    float treeChasingDesire = (tree.containedBullets / 2) - myLocation.distanceTo(loc);
                    if(R.treeCount < 5){
                        treeChasingDesire *= 1.5f;
                    } else if(R.treeCount < 10){
                        treeChasingDesire *= 1.25f;
                    }
                    sootforcer.M.addVector(loc, Math.min(7,Math.max(2,treeChasingDesire)));
                }
            }


        }
    }

    public void dealWithRobots() throws Exception{

        robotsEnemy = rc.senseNearbyRobots(sightradius, enemy);
        robotsAlly = rc.senseNearbyRobots(sightradius, ally);
        underArrestByLumbers = 0;
        underArrestBySoldiers = 0;
        underArrestByScouts = 0;
        underArrestCount = 0;
        enemyScoutCount = 0;

        for (int i = 0; i < robotsEnemy.length; i++) {
            RobotInfo r = robotsEnemy[i];
            MapLocation loc = r.location;
            RobotType type = r.type;
            float dist = myLocation.distanceTo(loc);

            switch (type) {
                case GARDENER:
                    spottedEnemyGardener(loc);
                    break;
                case SCOUT:
                    spottedEnemyScout(loc);
                    enemyScoutCount++;
                    if(dist < 7){
                        underArrestByScouts++;
                        if(underArrestCount < 19){
                            underArrestUnits[underArrestCount++] = loc;
                        }
                    }
                    break;
                case SOLDIER:
                    spottedEnemySoldier(loc);
                    if(dist < 7){
                        underArrestBySoldiers++;
                        if(underArrestCount < 19){
                            underArrestUnits[underArrestCount++] = loc;
                        }
                    }

                    sootforcer.M.addVectorCircleZone(loc,11,-4,-2);
                    break;
                case ARCHON:
                    spottedEnemyArchon(r);
                    break;
                case LUMBERJACK:
                    if(dist < 7){
                        underArrestByLumbers++;
                        if(underArrestCount < 19){
                            underArrestUnits[underArrestCount++] = loc;
                        }
                    }

                    sootforcer.M.addVectorCircleZone(loc,6,-4,-4);

                    break;
                case TANK:
                    spottedEnemyTank(loc);

                    sootforcer.M.addVectorCircleZone(loc,10,-4,-3);

                    break;
            }
        }




        for (int i = 0; i < robotsAlly.length; i++) {
            RobotInfo r = robotsAlly[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;

            switch (type) {
                case GARDENER:

                    if(underArrestCount > 0) {
                        //Try shielding the gardener somewhat


                        for(int j = 0 ; j < underArrestCount; j++) {
                            MapLocation eLoc = underArrestUnits[j];

                            if (eLoc.isWithinDistance(loc, 10) && eLoc.distanceTo(loc) >  dist + 0.5f) {
                                MapLocation partWay = eLoc.add(eLoc.directionTo(loc), eLoc.distanceTo(loc) * 0.7f);
                                sootforcer.M.addVector(partWay,4);

                                sootforcer.Test.lineTo(partWay);
                                //rc.setIndicatorLine(eLoc,loc,150,255,255);
                            }

                        }



                    }else{
                        if (mapType == EXTREMELY_TIGHT) {
                            if (loc.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MICRO_FROM + 2)) {
                                sootforcer.M.addVectorCircleZone(loc, AVOID_GARDENERS_AND_TREES_MICRO + 2, -60, -40);
                            }
                        } else {

                            if (friendlyTreesNear < 13) {
//                            if (loc.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MACRO_FROM)) {
//                                Test.lineTo(loc);
//
//                                M.addVectorCircleZone(loc, AVOID_GARDENERS_AND_TREES_MACRO, 0, -2);
//                                if (loc.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MICRO_FROM)) {
//                                    M.addVectorCircleZone(loc, AVOID_GARDENERS_AND_TREES_MICRO, -30, -5);
//                                }
//                            }

                                if (loc.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MACRO_FROM)) {
                                    sootforcer.Test.lineTo(loc);

                                    sootforcer.M.addVectorCircleZone(loc, AVOID_GARDENERS_AND_TREES_MACRO, 0, -2);
                                    if (loc.isWithinDistance(myLocation, AVOID_GARDENERS_AND_TREES_MICRO_FROM)) {
                                        sootforcer.M.addVectorCircleZone(loc, AVOID_GARDENERS_AND_TREES_MICRO, -30, -5);
                                    }
                                }


//
                                sootforcer.M.addVector(loc, -1);
                            } else {
                                //Too many trees near.. at risk of getting stuck
//                            M.addVector(loc,-10);
                                sootforcer.M.addVector(myLocation.add(loc.directionTo(myLocation).rotateLeftDegrees(40), 2), 15);
                                sootforcer.M.addVector(loc, -1.5f);
                            }
                        }
                    }
                    break;

            }
        }
    }

    public void initial() throws Exception{





        float hexX = rc.readBroadcastFloat(HEX_ORIGIN_X);

        if(hexX > 0){
            hexOrigin = new MapLocation(hexX, rc.readBroadcastFloat(HEX_ORIGIN_Y));
        }else{
            trees = rc.senseNearbyTrees();
            if(trees.length > 0){
                //So we might be able to build around this tree
                hexOrigin = trees[0].location;
            }else{
                hexOrigin = myLocation;
            }
            rc.broadcastFloat(HEX_ORIGIN_X,hexOrigin.x);
            rc.broadcastFloat(HEX_ORIGIN_Y,hexOrigin.y);
        }
        hexOriginX = hexOrigin.x;
        hexOriginY = hexOrigin.y;



        float sqrX = rc.readBroadcastFloat(SQUARE_TREE_ORIGIN_X);

        if(sqrX > 0){
            squareOrigin = new MapLocation(sqrX, rc.readBroadcastFloat(SQUARE_TREE_ORIGIN_Y));
        }else{
            if(trees == null) {
                trees = rc.senseNearbyTrees();
            }
            if(trees.length > 0){
                //So we might be able to build around this tree
                squareOrigin = trees[0].location;
            }else{
                squareOrigin = myLocation;
            }
            rc.broadcastFloat(SQUARE_TREE_ORIGIN_X,squareOrigin.x);
            rc.broadcastFloat(SQUARE_TREE_ORIGIN_Y,squareOrigin.y);
        }
        squareOriginX = squareOrigin.x;
        squareOriginY = squareOrigin.y;


    }



    public static boolean amIStuckGardenerSpaceCheck() throws GameActionException{
        for(int i = 24; i >= 0; i--){
            if(!rc.isCircleOccupied(myLocation.add(M.someDirections[i], sootforcer.C.ARCHON_BUILD_DISTANCE), C.GARDENER_RADIUS)){
                return false;
            }
        }
        return true;
    }


    public static void initialIdentifyArchonLocationsAndMapSize() throws Exception{

        if(turn != 0){
            myNr = 4; //a new archon
            return;
        }





        MapLocation[] ourLocations = rc.getInitialArchonLocations(ally);
        MapLocation[] theirLocations = rc.getInitialArchonLocations(enemy);

        if(rc.readBroadcast(MY_ARCHON_1_ID) == 0){
            myNr = 1;
            rc.broadcast(MY_ARCHON_1_ID, myId);
            rc.broadcast(MY_ARCHON_1_LAST_SPOTTED, turn);
            rc.broadcastBoolean(MY_ARCHON_1_STUCK_FROM_START, stuckFromStart);
            rc.broadcastFloat(MY_ARCHON_1_X, myLocation.x);
            rc.broadcastFloat(MY_ARCHON_1_Y, myLocation.y);

            startArchonCount = ourLocations.length;
            rc.broadcast(START_ARCHON_COUNT,startArchonCount);

            Map.addMapAnalysis();

        } else if(rc.readBroadcast(MY_ARCHON_2_ID) == 0){
            myNr = 2;
            rc.broadcast(MY_ARCHON_2_ID, myId);
            rc.broadcast(MY_ARCHON_2_LAST_SPOTTED, turn);
            rc.broadcastBoolean(MY_ARCHON_2_STUCK_FROM_START, stuckFromStart);
            rc.broadcastFloat(MY_ARCHON_2_X, myLocation.x);
            rc.broadcastFloat(MY_ARCHON_2_Y, myLocation.y);

            startArchonCount = ourLocations.length;

        } else if(rc.readBroadcast(MY_ARCHON_3_ID) == 0) {
            myNr = 3;
            rc.broadcast(MY_ARCHON_3_ID, myId);
            rc.broadcast(MY_ARCHON_3_LAST_SPOTTED, turn);
            rc.broadcastBoolean(MY_ARCHON_3_STUCK_FROM_START, stuckFromStart);
            rc.broadcastFloat(MY_ARCHON_3_X, myLocation.x);
            rc.broadcastFloat(MY_ARCHON_3_Y, myLocation.y);

            startArchonCount = ourLocations.length;

        } else{
            myNr = 4;
            return;
        }
        updateSpaciousness();


        float furthestLeft = 99999;
        float furthestRight = -99999;
        float furthestTop = -99999;
        float furthestBot = 99999;

        for (int i = 0; i < ourLocations.length; i++){
            MapLocation loc = ourLocations[i];
            if(loc.x < furthestLeft){
                furthestLeft = loc.x;
            }
            if(loc.x > furthestRight){
                furthestRight = loc.x;
            }
            if(loc.y < furthestBot){
                furthestBot = loc.y;
            }
            if(loc.y > furthestTop){
                furthestTop = loc.y;
            }
        }
        for (int i = 0; i < theirLocations.length; i++){
            MapLocation loc = theirLocations[i];
            if(loc.x < furthestLeft){
                furthestLeft = loc.x;
            }
            if(loc.x > furthestRight){
                furthestRight = loc.x;
            }
            if(loc.y < furthestBot){
                furthestBot = loc.y;
            }
            if(loc.y > furthestTop){
                furthestTop = loc.y;
            }
        }

        //Find the equivalent enemy, figure out whether it's stuck
        float middleX = (furthestLeft + furthestRight) / 2f;
        float middleY = (furthestBot + furthestTop) / 2f;
        float oppositeX  =  middleX + (middleX - myLocation.x);
        float oppositeY  =  middleY + (middleY - myLocation.y);
        MapLocation oppositeLoc = new MapLocation(oppositeX,oppositeY);
        for (int i = 0; i < theirLocations.length; i++) {
            if(theirLocations[i].distanceTo(oppositeLoc) < 1){
                oppositeLoc = theirLocations[i];
            }
        }
        if(myNr == 1){
            rc.broadcastBoolean(THEIR_ARCHON_1_STUCK_FROM_START, stuckFromStart);
            rc.broadcastFloat(THEIR_ARCHON_1_X, oppositeLoc.x);
            rc.broadcastFloat(THEIR_ARCHON_1_Y, oppositeLoc.y);
        } else if(myNr == 2){
            rc.broadcastBoolean(THEIR_ARCHON_2_STUCK_FROM_START, stuckFromStart);
            rc.broadcastFloat(THEIR_ARCHON_2_X, oppositeLoc.x);
            rc.broadcastFloat(THEIR_ARCHON_2_Y, oppositeLoc.y);
        } else if(myNr == 3) {
            rc.broadcastBoolean(THEIR_ARCHON_3_STUCK_FROM_START, stuckFromStart);
            rc.broadcastFloat(THEIR_ARCHON_3_X, oppositeLoc.x);
            rc.broadcastFloat(THEIR_ARCHON_3_Y, oppositeLoc.y);
        }



        //Now, let's figure out the shortest distance between an archon of mine that isn't stuck, and one of theirs that is
        MapLocation[] mineNotStuck = new MapLocation[startArchonCount];
        MapLocation[] theirsNotStuck = new MapLocation[startArchonCount];
        if (!stuckFromStart) {
            mineNotStuck[myNr - 1] = myLocation;
            theirsNotStuck[myNr - 1] = oppositeLoc;
        }
        if (myNr == 2) {
            if (!rc.readBroadcastBoolean(MY_ARCHON_1_STUCK_FROM_START)) {
                mineNotStuck[0] = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_1_X), rc.readBroadcastFloat(MY_ARCHON_1_Y));
                theirsNotStuck[0] = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_1_X), rc.readBroadcastFloat(THEIR_ARCHON_1_Y));
            }
        } else if (myNr == 3) {
            if (!rc.readBroadcastBoolean(MY_ARCHON_1_STUCK_FROM_START)) {
                mineNotStuck[0] = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_1_X), rc.readBroadcastFloat(MY_ARCHON_1_Y));
                theirsNotStuck[0] = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_1_X), rc.readBroadcastFloat(THEIR_ARCHON_1_Y));
            }
            if (!rc.readBroadcastBoolean(MY_ARCHON_2_STUCK_FROM_START)) {
                mineNotStuck[1] = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_2_X), rc.readBroadcastFloat(MY_ARCHON_2_Y));
                theirsNotStuck[1] = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_2_X), rc.readBroadcastFloat(THEIR_ARCHON_2_Y));
            }
        }

        float shortestRush = 999999;
        float longestRush = -999999;
        boolean foundAnyNonStuck = false;
        int freeFriendlies = 0;
        int ourArchonShortestRush = -1;
        int ourArchonLongestRush = -1;
        MapLocation theirArchonClosestRush = null;
        MapLocation myArchonClosestRush = null;

        for (int i = 0; i < mineNotStuck.length; i++) {
            if (mineNotStuck[i] == null) continue;
            freeFriendlies++;
            for (int j = 0; j < theirsNotStuck.length; j++) {
                if (theirsNotStuck[j] == null) continue;
                foundAnyNonStuck = true;
                float dist = mineNotStuck[i].distanceTo(theirsNotStuck[i]);
                if (dist < shortestRush) {
                    shortestRush = dist;
                    ourArchonShortestRush = i + 1;
                    theirArchonClosestRush = theirsNotStuck[i];
                    myArchonClosestRush = mineNotStuck[i];
                }
                if (dist > longestRush) {
                    longestRush = dist;
                    ourArchonLongestRush = i + 1;
                }
            }
        }
        if(!foundAnyNonStuck){
            shortestRush = -1;
            longestRush = -1;
        }


        if(myNr != 1) {
            readMapSize();
        }


        //Find the map boundaries as well as we can atm
        float left = myX - sightradius;
        float right = myX  + sightradius;
        float bot = myY - sightradius;
        float top = myY + sightradius;

        while(left < furthestLeft && !rc.onTheMap(new MapLocation(left,myY))){
            left += 0.5f;
        }
        while(right > furthestRight && !rc.onTheMap(new MapLocation(right,myY))){
            right -= 0.5f;
        }
        while(top > furthestTop && !rc.onTheMap(new MapLocation(myX,top))){
            top -= 0.5f;
        }
        while(bot < furthestBot && !rc.onTheMap(new MapLocation(myX,bot))){
            bot += 0.5f;
        }

//        Test.log("should be left : " + Math.min(furthestLeft,left));
        mapLeftUpdateAttempt(Math.min(furthestLeft,left));
        mapRightUpdateAttempt(Math.max(furthestRight,right));
        mapTopUpdateAttempt(Math.max(furthestTop,top));
        mapBotUpdateAttempt(Math.min(furthestBot,bot));
        updateMapSize();

        mapCenter = new MapLocation((map_right + map_left) / 2, (map_top + map_bot) / 2);



        //Let's have the last of our archons determine who should build our first gardener
        if(myNr == startArchonCount){
            if(foundAnyNonStuck) {


                int bestArchon = myNr;

                if(myNr == 1){
                    bestArchon = 1;
                }else {
                    int score1 = 0;
                    int score2 = 0;
                    int score3 = 0;

                    if (mineNotStuck[0] == null) {
                        score1 -= 10000;
                    }
                    if (mineNotStuck[1] == null) {
                        score2 -= 10000;
                    }
                    if (startArchonCount < 3 || mineNotStuck[2] == null) {
                        score3 -= 10000;
                    }

                    if (startArchonCount == 3) {
                        //With 3 free, give a bonus to the furthest archon
                        if(freeFriendlies == 3) {
                            if (ourArchonLongestRush == 1) score1 += 20;
                            else if (ourArchonLongestRush == 2) score2 += 20;
                            else if (ourArchonLongestRush == 3) score3 += 20;
                        }
                        //Else, give a bonus to the closest
                        else{
                            if (ourArchonShortestRush == 1) score1 += 20;
                            else if (ourArchonShortestRush == 2) score2 += 20;
                            else if (ourArchonShortestRush == 3) score3 += 20;
                        }

                        score1 += rc.readBroadcast(ARCHON_1_SPACIOUSNESS);
                        score2 += rc.readBroadcast(ARCHON_2_SPACIOUSNESS);
                        score3 += spaciousness;
                    } else if (startArchonCount == 2) {
                        //With 2, give a bonus to the closest archon
                        if (ourArchonShortestRush == 1) score1 += 10;
                        else if (ourArchonShortestRush == 2) score2 += 10;
                        score1 += rc.readBroadcast(ARCHON_1_SPACIOUSNESS);
                        score2 += spaciousness;
                    }

//                    Test.log("Scores    1: " + score1 + "   2: " + score2  +  "    3:" + score3);
                    if(score3 > score2 && score3 > score1){
                        bestArchon = 3;
                    } else if(score2 > score3 && score2 > score1){
                        bestArchon = 2;
                    }
                    else{
                        bestArchon = 1;
                    }
                }




                if (bestArchon == myNr) {
                    rc.broadcast(BEST_ARCHON_INITIAL_GARDENER, myNr);
                    initialGardenerAndBuildRequest(shortestRush,longestRush,theirArchonClosestRush, freeFriendlies);
                } else {
                    rc.broadcast(BEST_ARCHON_INITIAL_GARDENER, bestArchon);
                }


                rc.broadcastFloat(SHORTEST_RUSH_X, theirArchonClosestRush.x);
                rc.broadcastFloat(SHORTEST_RUSH_Y, theirArchonClosestRush.y);


                rc.broadcastFloat(SHORTEST_RUSH_INITIAL, shortestRush);
                rc.broadcastFloat(LONGEST_RUSH_INITIAL, longestRush);

                rc.broadcastFloat(PATH_FINDING_START_X, myArchonClosestRush.x);
                rc.broadcastFloat(PATH_FINDING_START_Y, myArchonClosestRush.y);
                rc.broadcastFloat(PATH_FINDING_END_X, theirArchonClosestRush.x);
                rc.broadcastFloat(PATH_FINDING_END_Y, theirArchonClosestRush.y);
                rc.broadcastFloat(PATH_FINDING_BEST_SCORE,-1000000);
            }else{
                rc.broadcast(BEST_ARCHON_INITIAL_GARDENER, -1);
                rc.broadcastFloat(SHORTEST_RUSH_INITIAL, -1);
                rc.broadcastFloat(LONGEST_RUSH_INITIAL, -1);
                rc.broadcastFloat(SHORTEST_RUSH_X, middleX);
                rc.broadcastFloat(SHORTEST_RUSH_Y, middleY);
            }
            rc.broadcast(FREE_ARCHONS_START, freeFriendlies);

        }






//        if(stuckFromStart) {
//            //rc.setIndicatorLine(myLocation,oppositeLoc, 255, 0, 0);
//        }
//        else{
//            //rc.setIndicatorLine(myLocation,oppositeLoc, 0, 255, 0);
//        }






        ////rc.setIndicatorLine(myLocation,new MapLocation(middleX,middleY),255,0,0);
       // //rc.setIndicatorLine(new MapLocation(furthestLeft,furthestTop), new MapLocation(furthestRight,furthestBot), 255,0,0);

    }




    public void lowkeyCalcs() throws Exception{
        if(Clock.getBytecodesLeft() > 500) {
            tryUpdateMap();
        }
        if(Clock.getBytecodesLeft() > 5000 && turn - lastUpdatedSpaciosness > 4){
            if( updateSpaciousness() >= 0){
                lastUpdatedSpaciosness = turn;
            }
        }
    }




    //Should return the same value on the same turn for all archons
    public static boolean shouldAGardenerBeBuild() throws Exception{
        if(R.bulletCount > RobotType.GARDENER.bulletCost){

            float gardenerDesire = rc.readBroadcast(GARDENER_DESIRE) * 1.1f;

            gardenerDesire += R.bulletCount / 30f;

            if(gardenerDesire > rc.readBroadcast(SOLDIER_DESIRE) && gardenerDesire > rc.readBroadcast(TREE_DESIRE) * 1.1f && gardenerDesire > rc.readBroadcast(LUMBER_DESIRE) && gardenerDesire > rc.readBroadcast(SCOUT_DESIRE)   && gardenerDesire > rc.readBroadcast(TANK_DESIRE) * 0.8f  ){
                return true;
            }



        }
        return false;
    }


    public static void normalGardenerCalculations() throws Exception{
        boolean shouldIBuildAGardener = false;
        treeShape = UNKNOWN_SHAPE;

         if(justDeterminedWhoShouldBuildGardener){

           float score1 = rc.readBroadcastFloat(ARCHON_1_GARDENER_SCORE);
            float score2 = rc.readBroadcastFloat(ARCHON_2_GARDENER_SCORE);
            float score3 = rc.readBroadcastFloat(ARCHON_3_GARDENER_SCORE);

            if(myNr == 1){
                if(score1 >= score2 && score1 >= score3){
                    shouldIBuildAGardener = true;
                    treeShape = rc.readBroadcast(ARCHON_1_SUGGESTING_SHAPE);
                    rc.broadcast(TREE_SHAPE,treeShape);
                }
            }else if(myNr == 2){
                if(score2 > score1 && score2 >= score3){
                    shouldIBuildAGardener = true;
                    treeShape = rc.readBroadcast(ARCHON_2_SUGGESTING_SHAPE);
                    rc.broadcast(TREE_SHAPE,treeShape);
                }
            }else if(myNr == 3){
                if(score3 > score1 && score3 > score2){
                    shouldIBuildAGardener = true;
                    treeShape = rc.readBroadcast(ARCHON_3_SUGGESTING_SHAPE);
                    rc.broadcast(TREE_SHAPE,treeShape);
                }
            }

            justDeterminedWhoShouldBuildGardener = false;
        }
        else{
            if(shouldAGardenerBeBuild()){

                //Check if were the only archon left, if so, just build the gardener
                //TODO: add check for whether archons are still stuck
                if(startArchonCount == 1){
                    shouldIBuildAGardener = true;
                } else if(startArchonCount == 2){
                    if(myNr == 1){
                        if(turn - rc.readBroadcast(MY_ARCHON_2_LAST_SPOTTED) > 4){
                            shouldIBuildAGardener = true;
                        }
                    } else if(myNr == 2){
                        if(turn - rc.readBroadcast(MY_ARCHON_1_LAST_SPOTTED) > 4){
                            shouldIBuildAGardener = true;
                        }
                    }
                } else if(startArchonCount == 3){
                    if(myNr == 1){
                        if(turn - rc.readBroadcast(MY_ARCHON_2_LAST_SPOTTED) > 4  && turn - rc.readBroadcast(MY_ARCHON_3_LAST_SPOTTED) > 4 ){
                            shouldIBuildAGardener = true;
                        }
                    } else if(myNr == 2){
                        if(turn - rc.readBroadcast(MY_ARCHON_1_LAST_SPOTTED) > 4  && turn - rc.readBroadcast(MY_ARCHON_3_LAST_SPOTTED) > 4 ){
                            shouldIBuildAGardener = true;
                        }
                    } else if(myNr == 3){
                        if(turn - rc.readBroadcast(MY_ARCHON_1_LAST_SPOTTED) > 4  && turn - rc.readBroadcast(MY_ARCHON_2_LAST_SPOTTED) > 4 ){
                            shouldIBuildAGardener = true;
                        }
                    }
                }


                if(!shouldIBuildAGardener) {
                    //check whether we can even build anything
                    if(!amIStuckGardenerSpaceCheck()){
                        //if so, determine how much we want to build the gardener

                        if(rc.readBroadcast(LAST_UPDATED_GARDENER_SCORE) != turn){
                            rc.broadcastFloat(ARCHON_1_GARDENER_SCORE, -10000);
                            rc.broadcastFloat(ARCHON_2_GARDENER_SCORE, -10000);
                            rc.broadcastFloat(ARCHON_3_GARDENER_SCORE, -10000);
                        }

                        determineTreeShape(false);

                        float bestScore;
                        if(treeShape == HEX_SHAPE){
                            bestScore = bestGardenerHexSpotScore * 3;
                        } else if (treeShape == SQUARE_SHAPE){
                            bestScore = bestGardenerSquareSpotScore * 3;
                        } else{
                            bestScore = -100000;
                        }

                        bestScore -= robotsEnemy.length * 3;
                        bestScore -= gardenerBuildFailure * 2f;

                        if(spaciousness < 0 ){
                            bestScore -= 5;
                        }
                        else if(spaciousness < 50){
                            bestScore -= 2;
                        } else if(spaciousness < 100){
                            bestScore -= 1;
                        }

                        if(R.treeCount > 30){
                            if (mainTarget != null) {
                                bestScore -= myLocation.distanceTo(mainTarget) * 1.5f;
                            } else {
                                bestScore -= myLocation.distanceTo(mapCenter) * 1.5f;
                            }
                        }else {
                            if (mainTarget != null) {
                                bestScore -= myLocation.distanceTo(mainTarget) / 2f;
                            } else {
                                bestScore -= myLocation.distanceTo(mapCenter) / 2f;
                            }
                        }




                        MapLocation[] eSoldiers = soldierList.getAll(20);
                        for(int i = eSoldiers.length - 1; i>=0 ; i--){
                            if(eSoldiers[i].isWithinDistance(myLocation, 30)){
                                bestScore -= 1;
                            }
                        }


                        if(myNr == 1){
                            rc.broadcastFloat(ARCHON_1_GARDENER_SCORE, bestScore);
                            rc.broadcast(ARCHON_1_SUGGESTING_SHAPE,treeShape);
                        } else if(myNr == 2){
                            rc.broadcastFloat(ARCHON_2_GARDENER_SCORE, bestScore);
                            rc.broadcast(ARCHON_2_SUGGESTING_SHAPE,treeShape);
                        } else if(myNr == 3){
                            rc.broadcastFloat(ARCHON_3_GARDENER_SCORE, bestScore);
                            rc.broadcast(ARCHON_3_SUGGESTING_SHAPE,treeShape);
                        }
                        rc.broadcast(LAST_UPDATED_GARDENER_SCORE,turn);
                        justDeterminedWhoShouldBuildGardener = true;
                        return;
                    }

                }
            }
        }

        if(shouldIBuildAGardener && R.bulletCount > RobotType.GARDENER.bulletCost){


             if(underArrestCount == 0) {

                 rc.broadcastBoolean(MAIN_ARCHON_UNDER_ARREST, false);

                 MapLocation bestSpot = null;


                 boolean mobileFactory = false;

                 if (R.treeCount > 15 && turn - rc.readBroadcast(MOBILE_GARDENER_LAST_REPORTED) > 5) {
                     mobileFactory = true;
                     if (mainTarget != null) {
                         bestSpot = mainTarget;
                     } else {
                         bestSpot = mapCenter;
                     }
                 } else {


                     if(robotsEnemy.length - enemyScoutCount == 0 &&  (R.treeCount > 5 || turn < 250)) {
                         if (treeShape == UNKNOWN_SHAPE) {
                             determineTreeShape(true);
                         }
                         if (treeShape == HEX_SHAPE) {
                             analyzeNearbyHexSpots(3);
                             bestSpot = bestGardenerHexSpot;
                         } else if (treeShape == SQUARE_SHAPE) {
                             analyzeSquareSpots(3);
                             bestSpot = bestGardenerSquareSpot;
                         }
                     }
                 }


                 if(bestSpot != null && !mobileFactory && bestSpot.distanceTo(mapCenter) < myLocation.distanceTo(mapCenter)   && (R.treeCount < 70 || !myLocation.isWithinDistance(mapCenter, 5)) ){

                     if(++avoidGardenerOnWrongSideCounter < 15){
                         sootforcer.Test.log("wait a moment with gardener, dont want to put it on the wrong side");
                        plannedOutSpot = bestSpot;
                        return;
                     }
                     else{
                         bestSpot = myLocation.add(mapCenter.directionTo(myLocation));
                     }
                 }
                 else{
                     avoidGardenerOnWrongSideCounter = 0;
                 }

                 if (bestSpot != null) {
                     if (buildGardener(myLocation.directionTo(bestSpot))) {

                         if (!mobileFactory) {
                             registerBestSpot();
                             if (treeShape == HEX_SHAPE) {
                                 rc.broadcast(LATEST_GARDENER_TASK, SETTLE_HEX);
                             } else {
                                 rc.broadcast(LATEST_GARDENER_TASK, SETTLE_SQUARES);
                             }
                             rc.broadcastFloat(LATEST_GARDENER_SPOT_X, bestSpot.x);
                             rc.broadcastFloat(LATEST_GARDENER_SPOT_Y, bestSpot.y);
                         } else {
                             rc.broadcast(LATEST_GARDENER_TASK, MOBILE_FACTORY);
                             rc.broadcast(MOBILE_GARDENER_LAST_REPORTED, turn);
                         }

                     }
                 } else {
                     //So, if we get here, it means we can't just happily build econ gardeners with designated spots.
                     //They'll probably have to flee, and have to be placed towards safety if possible

                     Direction bestDir;

                     if(robotsEnemy.length > 0){
                         bestDir = myLocation.directionTo(robotsEnemy[0].location).opposite();
                     }else if(mainTarget != null){
                         bestDir = myLocation.directionTo(mainTarget).opposite();
                     } else{
                         bestDir = myLocation.directionTo(mapCenter).opposite();
                     }

                     if (buildGardener(bestDir)) {
                         plannedOutSpot = null;
                         strugglingToFindPlannedSpot = false;
                         rc.broadcast(LATEST_GARDENER_TASK, FLEE_THEN_SETTLE_APPROPRIATE);
                         rc.broadcastFloat(LATEST_GARDENER_SPOT_X, 0);
                         rc.broadcastFloat(LATEST_GARDENER_SPOT_Y, 0);
                     }

                     sootforcer.Test.log("Placing gardener towards safety");
                 }

             }
             else{
                 underArrestGardenerCalculations();
             }
        }


    }




    public static void underArrestGardenerCalculations() throws  Exception{

        rc.broadcastBoolean(MAIN_ARCHON_UNDER_ARREST, true);


        Direction buildDir = null;

        if(underArrestByScouts > 0 && (underArrestByLumbers > 0 || underArrestBySoldiers > 0)){
            //Okay, just give up, our gardener will instantly die. Better use the bullets on bullets

            sootforcer.Test.log("Under arrest -  various");

            return;
        }else if(underArrestByScouts > 0){

            if(underArrestByScouts == 1){
                //Under a scout arrest, we may be relatively fine. Just wait till we have 130 bullets, then pop a gardener on the opposite side

                sootforcer.Test.log("Under arrest -  one scout");

                if(R.bulletCount < 130){
                    return;
                }


                buildDir = myLocation.directionTo(underArrestUnits[0]).opposite();


            }
            else{
                sootforcer.Test.log("Under arrest -  two+ scouts");

                //2 scouts cant be beat by pumping gardeners out, just wait
                return;
            }
        } else{

            if(underArrestCount == 1){

                //If there's only a single unit, we may be able to get away just plopping it on the opposite side
                if(R.bulletCount < 130) return;


                buildDir = myLocation.directionTo(underArrestUnits[0]).opposite();


            }

            sootforcer.Test.log("Under arrest -  land forces");




        }


        if(buildDir != null){
            buildGardener(buildDir);
            rc.broadcast(LATEST_GARDENER_TASK, FLEE_THEN_SETTLE_APPROPRIATE);
            rc.broadcastFloat(LATEST_GARDENER_SPOT_X, 0);
            rc.broadcastFloat(LATEST_GARDENER_SPOT_Y, 0);
        }


    }




    public static void determineTreeShape(boolean direct) throws Exception{

        if(rc.readBroadcast(TREE_SHAPE) == HEX_SHAPE){
            analyzeNearbyHexSpots(3);
            if((bestGardenerHexSpot  == null|| turn < 3 ) && Clock.getBytecodesLeft() > 15000){
                int lastSwitch = rc.readBroadcast(SHAPE_TYPE_LASTSWITCHED);

                if(turn - lastSwitch  > 50 || (turn < 3 && lastSwitch == 0)){

                    //consider switching
                    analyzeSquareSpots(2);
                    if(bestGardenerSquareSpotScore * 0.8f > bestGardenerHexSpotScore){
                        treeShape = SQUARE_SHAPE;

                        if(direct){
                            rc.broadcast(TREE_SHAPE,SQUARE_SHAPE);
                        }
                    }
                    else{
                        treeShape = HEX_SHAPE;
                    }
                }else{
                    treeShape = HEX_SHAPE;
                }
            }else{
                treeShape = HEX_SHAPE;
            }
        }
        else{
            analyzeSquareSpots(3);
            if(bestGardenerSquareSpot == null && Clock.getBytecodesLeft() > 15000){
                int lastSwitch = rc.readBroadcast(SHAPE_TYPE_LASTSWITCHED);
                if(turn - lastSwitch  > 50){
                    //consider switching
                    analyzeNearbyHexSpots(2);
                    if(bestGardenerHexSpotScore * 0.9f > bestGardenerSquareSpotScore){
                        treeShape = HEX_SHAPE;

                        if(direct){
                            rc.broadcast(TREE_SHAPE,HEX_SHAPE);
                        }
                    }
                    else{
                        treeShape = SQUARE_SHAPE;
                    }
                }
                else{
                    treeShape = SQUARE_SHAPE;
                }
            }
            else{
                treeShape = SQUARE_SHAPE;
            }
        }


    }

    public static void shouldWeDoInitialGardener() throws Exception{
        if(rc.readBroadcastBoolean(BUILT_INITIAL_GARDENER)){
            alreadyHaveInitialGardener = true;
        }else{
            if(turn <= 2){
                if(turn == 1) return;
                if(rc.readBroadcast(BEST_ARCHON_INITIAL_GARDENER) != myNr){
                    return;
                }
            }
            MapLocation shortestRush = new MapLocation(rc.readBroadcastFloat(SHORTEST_RUSH_X),rc.readBroadcastFloat(SHORTEST_RUSH_Y));
            initialGardenerAndBuildRequest(rc.readBroadcastFloat(SHORTEST_RUSH_INITIAL),rc.readBroadcastFloat(LONGEST_RUSH_INITIAL),shortestRush,rc.readBroadcast(FREE_ARCHONS_START));
        }

    }


    //The opening is super important, so let's make it count
    //Basic idea:
    //If we want to start aggressive, place the gardener towards them, then build a soldier
    //If we want to play greedy/defensive, place the gardener towards the most space/where it can build 6 trees. Then build a tree or a scout
    //If there's a ton of trees, place the gardener towards the most space, then build a lumberjack or scout.
     public static void initialGardenerAndBuildRequest(float shortestRush, float longestRush, MapLocation shortestDistanceLoc, int freeArchonCount) throws Exception{

        Direction bestDir = null;

        int task;

        Commander.analyzeMapType();

        determineTreeShape(true);

        MapLocation spot;
        if(treeShape == HEX_SHAPE){
            spot = bestGardenerHexSpot;
            task = SETTLE_HEX;
        } else if(treeShape == SQUARE_SHAPE){
            spot = bestGardenerSquareSpot;
            task = SETTLE_SQUARES;
        } else{
            task = SETTLE_APPROPRIATE;
            spot = null;
        }


        if(mapType == CORNER_NO_TREES) {
            //Point bestdir to the corner were in, so that we dont get stuck behind the gardener
            if (myX - map_left < 4) {
                if (myY - map_bot < 4) {
                    bestDir = Direction.SOUTH.rotateRightDegrees(45);
                } else if (map_top - myY < 4) {
                    bestDir = Direction.WEST.rotateRightDegrees(45);
                }
            } else if (map_right - myX < 4) {
                if (myY - map_bot < 4) {
                    bestDir = Direction.EAST.rotateRightDegrees(45);
                } else if (map_top - myY < 4) {
                    bestDir = Direction.NORTH.rotateRightDegrees(45);
                }
            }
        } else if(mapType == ECON_MAP || mapType == GENERIC){
            if(spot != null){
                bestDir = myLocation.directionTo(spot);
            }
        } else if(mapType == EXTREMELY_TIGHT){
            if(spot != null){
                bestDir = myLocation.directionTo(spot);
            }
        } else if(mapType == PROBABLE_RUSH || mapType == CERTAIN_RUSH){
            bestDir = myLocation.directionTo(shortestDistanceLoc).rotateLeftDegrees(30);
            task = FLEE_THEN_SETTLE_APPROPRIATE;
            spot = null;
        }


        if(bestDir == null){
            if(spot != null){
                bestDir = myLocation.directionTo(spot);
            }else{
                bestDir = myLocation.directionTo(shortestDistanceLoc).opposite();
            }
        }




        ////rc.setIndicatorLine(myLocation,myLocation.add(bestDir,C.ARCHON_BUILD_DISTANCE),0,255,255);


        if(buildGardener(bestDir)) {
            alreadyHaveInitialGardener = true;
            rc.broadcastBoolean(BUILT_INITIAL_GARDENER, true);

            rc.broadcast(LATEST_GARDENER_TASK,task);

            if(spot != null){
                sootforcer.Test.log("yes spot  ");
                registerBestSpot();
                rc.broadcastFloat(LATEST_GARDENER_SPOT_X,spot.x);
                rc.broadcastFloat(LATEST_GARDENER_SPOT_Y,spot.y);
            }
            else{
                sootforcer.Test.log("no spot  shape " + treeShape);
            }
        }
    }



    public static int updateSpaciousness() throws Exception{
        int space = estimateSpaciousness();
        if(myNr == 1){
            rc.broadcast(ARCHON_1_SPACIOUSNESS, space);
        } else if(myNr == 2){
            rc.broadcast(ARCHON_2_SPACIOUSNESS, space);
        }else if(myNr == 3){
            rc.broadcast(ARCHON_3_SPACIOUSNESS, space);
        }
        spaciousness = space;
        sootforcer.Test.log("Spaciousness = " + space, Test.Modes.ECON);
        return space;
    }







    public static boolean buildGardener(Direction dir) throws GameActionException {
        if (rc.canHireGardener(dir)) {
            rc.hireGardener(dir);
            gardenerBuildFailure = 0;
            return true;
        }
        // Now try a bunch of similar angles
        int currentCheck = 1;

        while (currentCheck <= 20) {
            // Try the offset of the left side
            if (rc.canHireGardener(dir.rotateLeftDegrees(9 * currentCheck))) {
                rc.hireGardener(dir.rotateLeftDegrees(9 * currentCheck));
                //System.out.println("build gardener");
                gardenerBuildFailure = 0;
                return true;
            }
            // Try the offset on the right side
            if (rc.canHireGardener(dir.rotateRightDegrees(9 * currentCheck))) {
                rc.hireGardener(dir.rotateRightDegrees(9 * currentCheck));
                //System.out.println("build gardener");
                gardenerBuildFailure = 0;
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }



        if (rc.canHireGardener(Direction.NORTH)) {
            rc.hireGardener(Direction.NORTH);
            gardenerBuildFailure = 0;
            return true;
        }
        if (rc.canHireGardener(Direction.EAST)) {
            rc.hireGardener(Direction.EAST);
            gardenerBuildFailure = 0;
            return true;
        }
        if (rc.canHireGardener(Direction.SOUTH)) {
            rc.hireGardener(Direction.SOUTH);
            gardenerBuildFailure = 0;
            return true;
        }
        if (rc.canHireGardener(Direction.WEST)) {
            rc.hireGardener(Direction.WEST);
            gardenerBuildFailure = 0;
            return true;
        }




        if(gardenerBuildFailure > 30) gardenerBuildFailure = 0;

        //Ok, so let's try some different angles if we're unable to find anything. No more lil maze!
        currentCheck = 1;
        float max = 21 + (gardenerBuildFailure * 3);
        float degrees = 360 / max;
        dir = Direction.NORTH;


        while (currentCheck <= max) {
            // Try the offset of the left side
            if (rc.canHireGardener(dir.rotateLeftDegrees(degrees * ((float) currentCheck)))) {
                rc.hireGardener(dir.rotateLeftDegrees(degrees * ((float) currentCheck)));
                gardenerBuildFailure = 0;
                return true;
            }
            // Try the offset on the right side
            if (rc.canHireGardener(dir.rotateRightDegrees(degrees * ((float) currentCheck)))) {
                rc.hireGardener(dir.rotateRightDegrees(degrees * ((float) currentCheck)));
                gardenerBuildFailure = 0;
                return true;
            }

            // No move performed, try slightly further
            currentCheck++;
        }

        gardenerBuildFailure++;
        return false;
    }
}
