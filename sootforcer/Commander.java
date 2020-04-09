package sootforcer;

import battlecode.common.*;
import sootforcer.Adjustables;
import sootforcer.Archon;
import sootforcer.C;
import sootforcer.DataStructures.BroadcastList;
import sootforcer.Map;
import sootforcer.R;
import sootforcer.Test;
//import scala.collection.mutable.StringBuilder;


public class Commander extends sootforcer.R {


    public static boolean amICommander = false;
    public static boolean isCommanderArchon = true; //archon commanders can be trusted more in some areas


    private static int lastMainUpd= 0;


    private static MapLocation[] plausibleClusters;

    private static int enemySoldierCount;
    private static MapLocation[] enemySoldiersLocs;

    private static MapLocation[] enemyGardenerLocs;
    private static MapLocation[] enemyGardenerLocsLastTurn;
    private static MapLocation[] enemyGardenerLocsSecondToLastTurn;
    private static int enemyGardenerCount;
    private static int enemyGardenerCountLastTurn;
    private static int enemyGardenerCountSecondToLastTurn;


    private static MapLocation[] enemyTanksLocs;
    private static MapLocation[] enemyTanksLocsLastTurn;
    private static MapLocation[] enemyTanksLocsSecondToLastTurn;
    private static int enemyTanksCount;
    private static int enemyTanksCountLastTurn;
    private static int enemyTanksCountSecondToLastTurn;


    private static StringBuilder knownOldenemyGardenerLocsBuilder;
    private static StringBuilder plausibleGardenerLocsSpotted;
    private static StringBuilder plausibleGardenerLocsSpottedTwice;

    private static StringBuilder knownOldEnemyArchon1Locs;
    private static StringBuilder knownOldEnemyArchon2Locs;
    private static StringBuilder knownOldEnemyArchon3Locs;


    private static MapLocation[] pings;
    private static MapLocation archon1loc;
    private static MapLocation archon2loc;
    private static MapLocation archon3loc;



    private static float treeScore = -1;
    private static float spareArea = -1;
    private static float crampedScore = -1;
    private static float bulletInTreesScore = 0;
    private static float robotsInTreesScore = 0;



    private static int lastTurnBuildSomething;
    private static int lastTurnBuildMilitary;
    private static int lastSoldier;
    private static int lastGardener;
    private static int lastTank;
    private static int lastLumberjacks;
    private static int lastScout;
    private static int lastTrees;

    //The following are all estimations
    //They're partially based on 'death flags' set, which don't always accurarately represent reality
    //Some with death flags set may survive, some may die without ever setting their flag
    //Archons are not based on death flags, instead on reports. It's usually pretty accurate
    private static int soldiersAlive;
    private static int gardenersAlive;
    private static int tanksAlive;
    private static int scoutsAlive;
    private static int lumbersAlive;
    private static int archonsAlive;


    private static MapLocation cluster1;
    private static MapLocation cluster2;
    private static MapLocation cluster3;
    private static MapLocation cluster4;
    private static MapLocation cluster5;
    private static MapLocation cluster6;
    private static MapLocation cluster7;

    private static MapLocation confirmedEnemySoldier = null;
    private static MapLocation confirmedEnemyScout = null;

    private static MapLocation someProbableEnemy = null;
    private static int lastDecidedGardenerDied = 0;

    private static int nearbyEdges = 0;
    private static float totalUnitsPlusTreesMinusArchons;

    private static MapLocation macroCircle = null;
    private static float macroCircleSize;
    private static int turnsWithoutMacroSightings;


    private static RobotType lastBestType = RobotType.GARDENER; //using arhcon for tree
    private static int repeatBestTYpe = 0; //using arhcon for tree


    public static void doCommanderIfNecessary() throws Exception{
        if(amICommander){
            if(rc.readBroadcastInt(sootforcer.R.COMMANDER_ID) != rc.getID()){
                amICommander = false;
                return;

            }
            doCommanderEveryTurn();
            if(amIArchon){
                doCommanderMainTasks();
            }else if(sootforcer.R.turn % 3 == 0){
                doCommanderMainTasks();
            }
            sootforcer.R.canBroadcast = true;
        }
        else if(sootforcer.R.robot.shouldBecomeCommander()){
            becomeCommander();
            doCommanderEveryTurn();
            if(Clock.getBytecodesLeft() > 10000) {
                doCommanderMainTasks();
            }
        }
    }

    public static void initialize() throws Exception{
        isCommanderArchon = rc.readBroadcastBoolean(sootforcer.R.COMMANDER_ISARCHON);
    }


    private static void becomeCommander() throws Exception{
        amICommander = true;
        rc.broadcast(sootforcer.R.COMMANDER_ID, sootforcer.R.myId);
        rc.broadcast(sootforcer.R.COMMANDER_TURN_REPORTED, sootforcer.R.turn);
        knownOldenemyGardenerLocsBuilder = new StringBuilder();
        plausibleGardenerLocsSpotted = new StringBuilder();
        plausibleGardenerLocsSpottedTwice = new StringBuilder();

        knownOldEnemyArchon1Locs = new StringBuilder();
        knownOldEnemyArchon2Locs = new StringBuilder();
        knownOldEnemyArchon3Locs = new StringBuilder();


        if(amIArchon || amIScout){
            if(amIArchon) {
                rc.broadcastBoolean(sootforcer.R.COMMANDER_ISARCHON, true);
            }else{
                rc.broadcastBoolean(sootforcer.R.COMMANDER_ISARCHON, false);
            }

            plausibleClusters = new MapLocation[7];
            enemySoldiersLocs = new MapLocation[40];
        }
        else{
            rc.broadcastBoolean(sootforcer.R.COMMANDER_ISARCHON, false);
            rc.broadcast(sootforcer.R.CLUSTER_1_SIZE, -1);
            rc.broadcast(sootforcer.R.CLUSTER_2_SIZE, -1);
            rc.broadcast(sootforcer.R.CLUSTER_3_SIZE, -1);
            rc.broadcast(sootforcer.R.CLUSTER_4_SIZE, -1);
            rc.broadcast(sootforcer.R.CLUSTER_5_SIZE, -1);
            rc.broadcast(sootforcer.R.CLUSTER_6_SIZE, -1);
            rc.broadcast(sootforcer.R.CLUSTER_7_SIZE, -1);
        }


        rc.broadcastFloat(sootforcer.R.MACRO_SPHERE_DESIRE, 0);

        enemyGardenerLocs = new MapLocation[50];
        enemyGardenerLocsLastTurn = new MapLocation[50];
        enemyGardenerLocsSecondToLastTurn = new MapLocation[50];

        enemyTanksLocs = new MapLocation[50];
        enemyTanksLocsLastTurn = new MapLocation[50];
        enemyTanksLocsSecondToLastTurn = new MapLocation[50];
        
        enemyGardenerCount = 0;
        enemyGardenerCountLastTurn = 0;
        enemyGardenerCountSecondToLastTurn = 0;



    }

    //Tracking data etc. Don't broadcast in here
    private static void doCommanderEveryTurn() throws Exception
    {
        if(sootforcer.R.turn < 3) {
            sootforcer.Test.log("skipping every turn commander so we dont hit the limit turn 1");
        }

        if(amIArchon || sootforcer.R.amIScout) pings = rc.senseBroadcastingRobotLocations();
        //Test.beginClockTestAvg(7);
        trackEnemies(10000);
        analyzeUnitCounts( );

        controlMacroCircle();


//        if(turn % 5 ==0 && rc.getTeamBullets() > rc.getVictoryPointCost()) {
//            rc.donate(rc.getVictoryPointCost());
//            bulletCount = rc.getTeamBullets();
//        }

        //Test.endClockTestAvg(7, " gardener detection  ");
        //Test.log("gardeners detected: " + enemyGardenerCount);



        MapLocation north = sootforcer.R.myLocation.add(Direction.NORTH,2.7f);
        MapLocation south = sootforcer.R.myLocation.add(Direction.SOUTH,2.7f);
        MapLocation east = sootforcer.R.myLocation.add(Direction.EAST,2.7f);
        MapLocation west = sootforcer.R.myLocation.add(Direction.WEST,2.7f);

        //rc.setIndicatorLine(north,west, 150,70,50);
        //rc.setIndicatorLine(north,east, 150,70,50);
        //rc.setIndicatorLine(south,west, 150,70,50);
        //rc.setIndicatorLine(south,east, 150,70,50);

    }

    private static void doCommanderMainTasks() throws Exception{
        rc.broadcast(sootforcer.R.COMMANDER_TURN_REPORTED, sootforcer.R.turn);

//        Test.log("doing main tasks: " + R.turn);
        //We spread the commander tasks out a little to save the load on every turn
        //Non-archons also ignore some of the tasks that aren't as important
        if(amIArchon){
            if( sootforcer.R.turn > 2){
                prioritizeUnitBuilds();
            }
            if(sootforcer.R.turn % 5 == 3) {
                determineMainTarget();
            }
            if(sootforcer.R.turn % 5 == 4) {
                prioritizeUnitBuilds();
            }
            if(sootforcer.R.turn % 5 == 0) {
                analyzeMapType();
            }

        }else{
            if(sootforcer.R.turn % 2 == 0) {
                determineMainTarget();
            }
            if(sootforcer.R.turn % 2 == 1){
                prioritizeUnitBuilds();
            }
        }


        if(sootforcer.R.turn % 18 == 0){
            sootforcer.Map.addMapTreeCountEstimation(Math.min(180, (int)totalUnitsPlusTreesMinusArchons * 10));
        }


        //Test.log(myType.name() +"-" +R.myId+": Doing commander tasks");

    }


    private static void analyzeUnitCounts() throws Exception{
        int soldiers = rc.readBroadcast(sootforcer.R.SOLDIER_TOTAL);
        int scouts = rc.readBroadcast(sootforcer.R.SCOUT_TOTAL);
        int tanks = rc.readBroadcast(sootforcer.R.TANK_TOTAL);
        int lumberjacks = rc.readBroadcast(sootforcer.R.LUMBER_TOTAL);
        int gardeners = rc.readBroadcast(sootforcer.R.GARDENER_TOTAL);
        int trees = R.treeCount;

        if(lastGardener != 0) {
            if(soldiers > lastSoldier ||  tanks > lastTank || lumberjacks > lastLumberjacks){
                lastTurnBuildSomething = sootforcer.R.turn;
                lastTurnBuildMilitary = sootforcer.R.turn;
                rc.broadcast(sootforcer.R.LAST_BUILD_SOMETHING, sootforcer.R.turn);
                rc.broadcast(sootforcer.R.LAST_BUILD_MILITARY, sootforcer.R.turn);
            }else if(scouts > lastScout ||gardeners > lastGardener || trees > lastTrees){
                lastTurnBuildSomething = sootforcer.R.turn;
                rc.broadcast(sootforcer.R.LAST_BUILD_SOMETHING, sootforcer.R.turn);

            }
        }

        lastGardener = gardeners;
        lastTank = tanks;
        lastScout = scouts;
        lastTrees = trees;
        lastLumberjacks = lumberjacks;
        lastSoldier = soldiers;

        soldiersAlive = soldiers - rc.readBroadcast(sootforcer.R.SOLDIER_DEATH_FLAGS);
        lumbersAlive = lumberjacks - rc.readBroadcast(sootforcer.R.LUMBER_DEATH_FLAGS);
        gardenersAlive = gardeners - rc.readBroadcast(sootforcer.R.GARDENER_DEATH_FLAGS);
        scoutsAlive = scouts - rc.readBroadcast(sootforcer.R.SCOUT_DEATH_FLAGS);
        tanksAlive = tanks - rc.readBroadcast(sootforcer.R.TANK_DEATH_FLAGS);

//        Test.log("soldiers alive: " + soldiersAlive);

        if(sootforcer.R.turn > 10) {
            archonsAlive = 0;
            if (sootforcer.R.turn - rc.readBroadcast(sootforcer.R.MY_ARCHON_1_LAST_SPOTTED) < 5) archonsAlive++;
            if (sootforcer.R.turn - rc.readBroadcast(sootforcer.R.MY_ARCHON_2_LAST_SPOTTED) < 5) archonsAlive++;
            if (sootforcer.R.turn - rc.readBroadcast(sootforcer.R.MY_ARCHON_3_LAST_SPOTTED) < 5) archonsAlive++;
        }else{
            archonsAlive = sootforcer.R.startArchonCount;
        }

        rc.broadcast(ESTIMATED_SHOOTERS_ALIVE, soldiersAlive + scoutsAlive + tanksAlive);


        if(gardenersAlive == 0 && sootforcer.R.turn < 50 && rc.readBroadcastBoolean(sootforcer.R.BUILT_INITIAL_GARDENER)){
            gardenersAlive = 1;
        }

         totalUnitsPlusTreesMinusArchons =  gardenersAlive + sootforcer.R.treeCount + soldiersAlive + lumbersAlive + scoutsAlive + tanksAlive;

        rc.broadcast(sootforcer.R.MY_ARCHONS_ALIVE, archonsAlive);
    }

    private static void trackEnemies(int minBytecode) throws Exception{


        StringBuilder enemyHashes = new StringBuilder();
        StringBuilder enemyGardenerHashes = new StringBuilder();

        enemyGardenerLocsSecondToLastTurn = enemyGardenerLocsLastTurn;
        enemyGardenerLocsLastTurn = enemyGardenerLocs;

        enemyTanksLocsSecondToLastTurn = enemyTanksLocsLastTurn;
        enemyTanksLocsLastTurn = enemyTanksLocs;

        enemyTanksCountSecondToLastTurn = enemyTanksCountLastTurn;
        enemyTanksCountLastTurn = enemyTanksCount;


        enemyGardenerCountSecondToLastTurn = enemyGardenerCountLastTurn;
        enemyGardenerCountLastTurn = enemyGardenerCount;
        int snipeTargetsFound = 0;



        MapLocation plausibleMainEnemyBase = macroCircle;
        float distanceFromBaseAllowed = macroCircleSize - 6;
        int inBaseVicinity = 0;
        if(amIArchon || amIScout){
            if(plausibleMainEnemyBase == null && mapSize > TINY &&  R.treeCount > 10 && soldiersAlive > 6 ){

                if(enemyGardenerCountLastTurn > 0) {
                    int rand = Math.floorMod(Math.addExact(Clock.getBytecodeNum(), Math.addExact(Clock.getBytecodesLeft(), Math.multiplyExact(Clock.getBytecodeNum(), Math.floorDiv(Math.addExact(Math.multiplyExact(Clock.getBytecodesLeft(), Clock.getBytecodeNum()), Clock.getBytecodeNum()), Clock.getBytecodesLeft())))), enemyGardenerCountLastTurn);
                    plausibleMainEnemyBase = enemyGardenerLocsLastTurn[rand].add(myLocation.directionTo(enemyGardenerLocsLastTurn[rand]), 7);
                }
                if(plausibleMainEnemyBase == null) plausibleMainEnemyBase = getTheirArchon1Loc();
                if(plausibleMainEnemyBase == null) plausibleMainEnemyBase = getTheirArchon2Loc();
                if(plausibleMainEnemyBase == null) plausibleMainEnemyBase = getTheirArchon3Loc();

                if(plausibleMainEnemyBase != null) {
                    distanceFromBaseAllowed = Math.min(20,   Math.min((map_top - map_bot) / 1.8f,(map_right - map_left) / 1.8f));
                }
                inBaseVicinity++;
            }
        }




        enemyGardenerCount = 0;
        MapLocation[] eGardeners = sootforcer.R.gardenerList.getAll();
        for(int i = eGardeners.length - 1; i >= 0; i--){
            MapLocation m = eGardeners[i];
            if(enemyHashes.indexOf(Character.toString((char)m.hashCode())) < 0){
                enemyHashes.append((char)(m.hashCode()));
                sootforcer.Test.lineTo(m,100,0,0, sootforcer.Test.Modes.ENEMY_TRACKING);
                enemyGardenerLocs[enemyGardenerCount++] = m;
            }
        }


        //See broadcastlistmaplocations
//        enemyGardenerCount = 0;
//        int size =  R.rc.readBroadcast(R.LIST_ENEMY_GARDENER_LOCS_START);
//        for(int i = 0 ; i < size; i+=2){
//            MapLocation m = new MapLocation(R.rc.readBroadcastFloat(R.LIST_ENEMY_GARDENER_LOCS_START + i + 1), R.rc.readBroadcastFloat(R.LIST_ENEMY_GARDENER_LOCS_START + i + 2));
//
//            if(enemyHashes.indexOf(Character.toString((char) m.hashCode())) < 0){
//                //rc.setIndicatorLine(R.myLocation,m,0,0,0);
//                enemyGardenerLocs[i / 2] = m;
//                enemyGardenerCount++;
//                enemyHashes.append((char) m.hashCode());
//                enemyGardenerHashes.append((char) m.hashCode());
//
//            }
//        }


        int turns1 = sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_1_LAST_SPOTTED);
        int turns2 = sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_2_LAST_SPOTTED);
        int turns3 = sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_3_LAST_SPOTTED);

        if(turns1  < 5) {
            archon1loc = new MapLocation(rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_1_X), rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_1_Y));
            knownOldEnemyArchon1Locs.append(archon1loc);
            enemyHashes.append((char)(archon1loc.hashCode()));

            sootforcer.Map.pushToMapChannel(archon1loc, sootforcer.Map.TILE_ENEMY);


            if(plausibleMainEnemyBase != null && archon1loc.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                inBaseVicinity++;
            }
        }
        else{
            archon1loc = null;
        }
        if(sootforcer.R.startArchonCount > 1 &&  turns2  < 5) {
            archon2loc = new MapLocation(rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_2_X), rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_2_Y));
            knownOldEnemyArchon2Locs.append(archon2loc);

            enemyHashes.append((char)(archon2loc.hashCode()));

            sootforcer.Map.pushToMapChannel(archon2loc, sootforcer.Map.TILE_ENEMY);


            if(plausibleMainEnemyBase != null && archon2loc.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                inBaseVicinity++;
            }
        }
        else{
            archon2loc = null;
        }
        if(sootforcer.R.startArchonCount > 2 &&  turns3  < 5) {
            archon3loc = new MapLocation(rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_3_X), rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_3_Y));
            knownOldEnemyArchon3Locs.append(archon3loc);

            enemyHashes.append((char)(archon3loc.hashCode()));

            sootforcer.Map.pushToMapChannel(archon3loc, sootforcer.Map.TILE_ENEMY);

            if(plausibleMainEnemyBase != null && archon3loc.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                inBaseVicinity++;
            }
        }
        else{
            archon3loc = null;
        }


        MapLocation a1;
        MapLocation a2;
        MapLocation a3;
        if(turns1 < 4){
            a1 = archon1loc;
        }else{
            a1 = null;
        }
        if(turns2 < 4){
            a2 = archon2loc;
        }else{
            a2 = null;
        }
        if(turns3 < 4){
            a3 = archon3loc;
        }else{
            a3 = null;
        }



        if((amIArchon || sootforcer.R.amIScout) ){   //&& R.turn %3 != 1

            StringBuilder alliedHashes = new StringBuilder();

//            Test.beginClockTestAvg(7);

            int hashReports =  sootforcer.R.rc.readBroadcast(sootforcer.R.LIST_MY_TROOP_REPORT_HASHES_START);
            int startPlus1 = sootforcer.R.LIST_MY_TROOP_REPORT_HASHES_START+1;
            for(int i = hashReports-1 ;i>=0; i--){
                alliedHashes.append((char) sootforcer.R.rc.readBroadcast(startPlus1 + i));
            }

            int turns1a = sootforcer.R.turn - rc.readBroadcast(sootforcer.R.MY_ARCHON_1_LAST_SPOTTED);
            int turns2a = sootforcer.R.turn - rc.readBroadcast(sootforcer.R.MY_ARCHON_2_LAST_SPOTTED);
            int turns3a = sootforcer.R.turn - rc.readBroadcast(sootforcer.R.MY_ARCHON_3_LAST_SPOTTED);

            if(turns1a  < 5) {
                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(sootforcer.R.MY_ARCHON_1_X), rc.readBroadcastFloat(sootforcer.R.MY_ARCHON_1_Y));
                alliedHashes.append((char) m1.hashCode());
            }
            if(turns2a  < 5) {
                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(sootforcer.R.MY_ARCHON_2_X), rc.readBroadcastFloat(sootforcer.R.MY_ARCHON_2_Y));
                alliedHashes.append((char) m1.hashCode());
            }
            if(turns3a  < 5) {
                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(sootforcer.R.MY_ARCHON_3_X), rc.readBroadcastFloat(sootforcer.R.MY_ARCHON_3_Y));
                alliedHashes.append((char) m1.hashCode());
            }


            int plausibleClusterIndex = 0;
            int[] plausibleClusterCounts = new int[7];






            MapLocation[] enemySoldiers = sootforcer.R.soldierList.getAll();
            enemySoldierCount = 0;

            int everyFew = Math.max(1,(enemySoldiers.length / 14));

            for(int i = enemySoldiers.length - 1; i >= 0; i--){
                MapLocation m = enemySoldiers[i];
                if(enemyHashes.indexOf(Character.toString((char)m.hashCode())) < 0){
                    enemyHashes.append((char)(m.hashCode()));
                    if (plausibleClusterIndex < 5 && i % everyFew == 0) {
                        plausibleClusters[plausibleClusterIndex++] = m;
                    }
                    enemySoldiersLocs[enemySoldierCount++] = m;
                    sootforcer.Test.lineTo(m,100,0,0, sootforcer.Test.Modes.ENEMY_TRACKING);

                    if(plausibleMainEnemyBase != null && m.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                        inBaseVicinity++;
                    }
                }
            }

            if(enemySoldiers.length >= 1) confirmedEnemySoldier = enemySoldiers[0];
            else confirmedEnemySoldier = null;

            

            //handling scouts. lets not put these in clusters though, too easy for them to dodge
            MapLocation[] enemyScouts = sootforcer.R.scoutList.getAll();
            int listsizescouts =  enemyScouts.length;
            for(int i = listsizescouts - 1 ; i>= 0 ; i--){
                enemyHashes.append((char)(enemyScouts[i].hashCode()));

                if(plausibleMainEnemyBase != null && enemyScouts[i].isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                    inBaseVicinity++;
                }
            }
            if(listsizescouts >= 1) confirmedEnemyScout = enemyScouts[0];
            else confirmedEnemyScout = null;


            enemyTanksCount = 0;
            MapLocation[] eTanks = sootforcer.R.tankList.getAll();
            for(int i = eTanks.length - 1; i >= 0; i--){
                MapLocation m = eTanks[i];
                if(enemyHashes.indexOf(Character.toString((char)m.hashCode())) < 0){
                    enemyHashes.append((char)(m.hashCode()));
                    sootforcer.Test.lineTo(m,100,0,200, sootforcer.Test.Modes.ENEMY_TRACKING);
                    enemyTanksLocs[enemyTanksCount++] = m;

                    if(plausibleMainEnemyBase != null && m.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                        inBaseVicinity++;
                    }
                }
            }





            int pingLength = pings.length - 1;

            if(pingLength > 10) {
                for (int p = pingLength; p >= 0; p-=4) {
                    MapLocation ping = pings[p];
                    if(!ping.isWithinDistance(sootforcer.R.myLocation, sootforcer.R.sightradius)  && alliedHashes.indexOf(Character.toString((char) ping.hashCode())) < 0) {
                        plausibleClusters[plausibleClusterIndex++] = ping;
                        if(plausibleClusterIndex > 6){
                            break;
                        }
                    }
                }
            }


            someProbableEnemy = null;

            int minTracker = minBytecode - 1000;
            boolean stillAllowPrecisionTracking = true;
            String enemyGardenersLongtermMemory = knownOldenemyGardenerLocsBuilder.toString();
            String plausibleGardeners = plausibleGardenerLocsSpotted.toString();
            String plausibleGardenersTwice = plausibleGardenerLocsSpottedTwice.toString();

            String archon1History = knownOldEnemyArchon1Locs.toString();
            String archon2History = knownOldEnemyArchon2Locs.toString();
            String archon3History = knownOldEnemyArchon3Locs.toString();


            pingloop:
            for(int p = pingLength; p >= 0; p--){
                MapLocation ping = pings[p];
                String pinghash = Character.toString((char) ping.hashCode());


                //This is already dealt with
                if(ping.isWithinDistance(sootforcer.R.myLocation, sootforcer.R.sightradius) || alliedHashes.indexOf(pinghash) >= 0 || enemyHashes.indexOf(pinghash) >= 0 ){
                    //Test.drawLine(ping,ping.add(Direction.WEST),0,0,100, Test.Modes.ENEMY_TRACKING);
                    continue;
                }
                else{
//                    Test.lineTo(ping,0,100,0, Test.Modes.ENEMY_TRACKING);
                    //Test.drawLine(ping,ping.add(Direction.WEST),0,100,0, Test.Modes.ENEMY_TRACKING);
                }

                if(someProbableEnemy == null) someProbableEnemy = ping;



                for(int i = plausibleClusterIndex-1 ; i >= 0; i-- ){
                    if(ping.isWithinDistance(plausibleClusters[i], sootforcer.C.CLUSTER_IDENTIFIER_RADIUS)) {
                        plausibleClusterCounts[i]++;
                    }
                }


                if(stillAllowPrecisionTracking) {
                    if (archon1History.contains(ping.toString()) ||    (a1 != null && ping.isWithinDistance(a1, sootforcer.C.TRIPLE_ARCHONMOVE))) {
                        archon1loc = ping;
                        rc.broadcastFloat(sootforcer.R.THEIR_ARCHON_1_X, ping.x);
                        rc.broadcastFloat(sootforcer.R.THEIR_ARCHON_1_Y, ping.y);
                        rc.broadcast(sootforcer.R.THEIR_ARCHON_1_LAST_SPOTTED, sootforcer.R.turn);
                        sootforcer.Map.pushToMapChannel(archon1loc, sootforcer.Map.TILE_ENEMY);
                        if(plausibleMainEnemyBase != null && ping.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                            inBaseVicinity++;
                        }
                        continue;
                    }
                    if (archon2History.contains(ping.toString()) ||    (a2 != null && ping.isWithinDistance(a2, sootforcer.C.TRIPLE_ARCHONMOVE))) {
                        archon2loc = ping;
                        rc.broadcastFloat(sootforcer.R.THEIR_ARCHON_2_X, ping.x);
                        rc.broadcastFloat(sootforcer.R.THEIR_ARCHON_2_Y, ping.y);
                        rc.broadcast(sootforcer.R.THEIR_ARCHON_2_LAST_SPOTTED, sootforcer.R.turn);
                        sootforcer.Map.pushToMapChannel(archon2loc, sootforcer.Map.TILE_ENEMY);
                        if(plausibleMainEnemyBase != null && ping.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                            inBaseVicinity++;
                        }
                        continue;
                    }
                    if (archon3History.contains(ping.toString()) ||    (a3 != null && ping.isWithinDistance(a3, sootforcer.C.TRIPLE_ARCHONMOVE))) {
                        archon3loc = ping;
                        rc.broadcastFloat(sootforcer.R.THEIR_ARCHON_3_X, ping.x);
                        rc.broadcastFloat(sootforcer.R.THEIR_ARCHON_3_Y, ping.y);
                        rc.broadcast(sootforcer.R.THEIR_ARCHON_3_LAST_SPOTTED, sootforcer.R.turn);
                        sootforcer.Map.pushToMapChannel(archon3loc, sootforcer.Map.TILE_ENEMY);
                        if(plausibleMainEnemyBase != null && ping.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                            inBaseVicinity++;
                        }
                        continue;
                    }



                    if (sootforcer.R.turn < 10) {
//                        //rc.setIndicatorLine(R.myLocation,ping,100,0,0);

                        //Any non-archons the first few turns must be gardeners
                        enemyGardenerLocs[enemyGardenerCount] = ping;
                        enemyGardenerCount++;
                        sootforcer.R.gardenerList.push(ping);
                        continue;
                    }


                    String pingstring = ping.toString();
                    //If we get a ping from EXACTLY the same position as one we knew a gardener to be at
                    //It's a gardener again. Thanks gardeners for just standing still most of the time
                    if(enemyGardenersLongtermMemory.contains(pingstring)){
//                        //rc.setIndicatorLine(R.myLocation,ping,0,100,0);

                        enemyGardenerLocs[enemyGardenerCount] = ping;
                        enemyGardenerCount++;
                        sootforcer.R.gardenerList.push(ping);

                        continue;
                    }

                    //If we see a ping over and over, it's highly likely it's an enemy gardener
                    //It could also be a stuck unit, but well, it's acceptable we target those too
                    //Since they're likely in the enemy base
                    if(plausibleGardeners.contains(pingstring)){
                        //We require having seen this exact ping at least twice
                        if(plausibleGardenersTwice.contains(pingstring)){
//                            //rc.setIndicatorLine(R.myLocation,ping,0,0,100);

                            enemyGardenerLocs[enemyGardenerCount] = ping;
                            enemyGardenerCount++;
                            sootforcer.R.gardenerList.push(ping);

//                            //rc.setIndicatorLine(ping,ping.add(Direction.EAST,10000),255,255,255);
                        }else{
                            plausibleGardenerLocsSpottedTwice.append(pingstring);
                        }
                    }
                    plausibleGardenerLocsSpotted.append(pingstring);


                    if (enemyGardenerHashes.indexOf(pinghash) < 0) {
                        for (int i = enemyGardenerCountLastTurn - 1; i >= 0; i--) {
                            if (ping.distanceTo(enemyGardenerLocsLastTurn[i]) < sootforcer.C.GARDENERMOVE) {
//                                //rc.setIndicatorLine(R.myLocation,ping,0,0,100);

                                enemyGardenerLocs[enemyGardenerCount] = ping;
                                enemyGardenerCount++;
                                sootforcer.R.gardenerList.push(ping);

//                                //rc.setIndicatorLine(ping,ping.add(Direction.SOUTH,3),0,200,0);

                                continue pingloop;
                            }
                        }

                        for (int i = enemyGardenerCountSecondToLastTurn - 1; i >= 0; i--) {
                            if (ping.isWithinDistance(enemyGardenerLocsSecondToLastTurn[i], sootforcer.C.DOUBLE_GARDENERMOVE)) {
//                                //rc.setIndicatorLine(R.myLocation,ping,100,100,0);

                                enemyGardenerLocs[enemyGardenerCount] = ping;
                                enemyGardenerCount++;
                                sootforcer.R.gardenerList.push(ping);

//                                //rc.setIndicatorLine(ping,ping.add(Direction.SOUTH,3),0,200,0);

                                continue pingloop;
                            }
                        }


                        for (int i = enemyTanksCountLastTurn - 1; i >= 0; i--) {
                            if (ping.distanceTo(enemyTanksLocsLastTurn[i]) < sootforcer.C.TANKMOVE) {
                                //rc.setIndicatorLine(R.myLocation,ping,0,0,100);

                                enemyTanksLocs[enemyTanksCount] = ping;
                                enemyTanksCount++;
                                sootforcer.R.tankList.push(ping);

//                                //rc.setIndicatorLine(ping,ping.add(Direction.SOUTH,3),0,200,0);
                                if(plausibleMainEnemyBase != null && ping.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                                    inBaseVicinity++;
                                }
                                continue pingloop;
                            }
                        }

                        for (int i = enemyTanksCountSecondToLastTurn - 1; i >= 0; i--) {
                            if (ping.isWithinDistance(enemyTanksLocsSecondToLastTurn[i], sootforcer.C.DOUBLE_TANKMOVE)) {
                                //rc.setIndicatorLine(R.myLocation,ping,100,100,0);
                                enemyTanksLocs[enemyTanksCount] = ping;
                                enemyTanksCount++;
                                sootforcer.R.tankList.push(ping);
//                                //rc.setIndicatorLine(ping,ping.add(Direction.SOUTH,3),0,200,0);
                                if(plausibleMainEnemyBase != null && ping.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                                    inBaseVicinity++;
                                }
                                continue pingloop;
                            }
                        }
                        
                    }


                    if(plausibleMainEnemyBase != null && ping.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                        inBaseVicinity++;
                    }



                    if(ping.x > sootforcer.R.map_right) sootforcer.R.mapRightUpdateAttempt(ping.x);
                    if(ping.x < sootforcer.R.map_left) sootforcer.R.mapLeftUpdateAttempt(ping.x);
                    if(ping.y > sootforcer.R.map_top) sootforcer.R.mapTopUpdateAttempt(ping.y);
                    if(ping.y < sootforcer.R.map_bot) sootforcer.R.mapBotUpdateAttempt(ping.y);

                    if (Clock.getBytecodesLeft() < minTracker) stillAllowPrecisionTracking = false;
                }
            }


            for(int i = enemyGardenerCount - 1; i >= 0; i--){
                knownOldenemyGardenerLocsBuilder.append(enemyGardenerLocs[i].toString());
            }


            cluster1 = null;
            cluster2 = null;
            cluster3 = null;
            cluster4 = null;
            cluster5 = null;
            cluster6 = null;
            cluster7 = null;

            for(int i = 6; i >= 0; i--) {

                MapLocation cluster = plausibleClusters[i];

                int total;
                if(cluster == null){
                    total = -1;
                }else {
                    int count = 0;
                    for (int j = enemySoldierCount - 1; j >= 0; j--) {
                        if (cluster.isWithinDistance(enemySoldiersLocs[j], sootforcer.C.CLUSTER_IDENTIFIER_RADIUS)) {
                            count+=2;
                        }
                    }
                    for (int j = enemyGardenerCount - 1; j >= 0; j--) {
                        if (cluster.isWithinDistance(enemyGardenerLocs[j], sootforcer.C.CLUSTER_IDENTIFIER_RADIUS)) {
                            count++;
                        }
                    }
                    total = plausibleClusterCounts[i] + count;

                    if (total < 6) {
                        total = -1;
                    } else {
                        //Test.drawLine(cluster.add(Direction.NORTH, C.CLUSTER_IDENTIFIER_RADIUS), cluster.add(Direction.SOUTH, C.CLUSTER_IDENTIFIER_RADIUS), 200, 210, 255, Test.Modes.ENEMY_TRACKING);
                        //Test.drawLine(cluster.add(Direction.WEST, C.CLUSTER_IDENTIFIER_RADIUS), cluster.add(Direction.EAST, C.CLUSTER_IDENTIFIER_RADIUS), 200, 210, 255, Test.Modes.ENEMY_TRACKING);
                    }
                }





                switch (i + 1){
                    case 1:
                        rc.broadcast(sootforcer.R.CLUSTER_1_SIZE, total);
                        if(total > 0){
                            rc.broadcastFloat(sootforcer.R.CLUSTER_1_X,cluster.x);
                            rc.broadcastFloat(sootforcer.R.CLUSTER_1_Y,cluster.y);
                            cluster1 = cluster;
                        }
                        break;

                    case 2:
                        rc.broadcast(sootforcer.R.CLUSTER_2_SIZE, total);
                        if(total > 0){
                            rc.broadcastFloat(sootforcer.R.CLUSTER_2_X,cluster.x);
                            rc.broadcastFloat(sootforcer.R.CLUSTER_2_Y,cluster.y);
                            cluster2 = cluster;
                        }
                        break;
                    case 3:
                        rc.broadcast(sootforcer.R.CLUSTER_3_SIZE, total);
                        if(total > 0){
                            rc.broadcastFloat(sootforcer.R.CLUSTER_3_X,cluster.x);
                            rc.broadcastFloat(sootforcer.R.CLUSTER_3_Y,cluster.y);
                            cluster3 = cluster;
                        }
                        break;
                    case 4:
                        rc.broadcast(sootforcer.R.CLUSTER_4_SIZE, total);
                        if(total > 0){
                            rc.broadcastFloat(sootforcer.R.CLUSTER_4_X,cluster.x);
                            rc.broadcastFloat(sootforcer.R.CLUSTER_4_Y,cluster.y);
                            cluster4 = cluster;

                        }
                        break;
                    case 5:
                        rc.broadcast(sootforcer.R.CLUSTER_5_SIZE, total);
                        if(total > 0){
                            rc.broadcastFloat(sootforcer.R.CLUSTER_5_X,cluster.x);
                            rc.broadcastFloat(sootforcer.R.CLUSTER_5_Y,cluster.y);
                            cluster5 = cluster;

                        }
                        break;
                    case 6:
                        rc.broadcast(sootforcer.R.CLUSTER_6_SIZE, total);
                        if(total > 0){
                            rc.broadcastFloat(sootforcer.R.CLUSTER_6_X,cluster.x);
                            rc.broadcastFloat(sootforcer.R.CLUSTER_6_Y,cluster.y);
                            cluster6 = cluster;

                        }
                        break;
                    case 7:
                        rc.broadcast(sootforcer.R.CLUSTER_7_SIZE, total);
                        if(total > 0){
                            rc.broadcastFloat(sootforcer.R.CLUSTER_7_X,cluster.x);
                            rc.broadcastFloat(sootforcer.R.CLUSTER_7_Y,cluster.y);
                            cluster7 = cluster;

                        }
                        break;
                        
                        
                }





            }



//            Test.beginClockTestAvg(9);
            if(amIArchon || amIScout) {

                for (int i = 0; i < enemyGardenerCount && Clock.getBytecodesLeft() > 10000 && snipeTargetsFound < 20; i++) {
                   MapLocation m = enemyGardenerLocs[i];

                    //rc.setIndicatorDot(m, 255, 170, 200);


                    if(plausibleMainEnemyBase != null && m.isWithinDistance(plausibleMainEnemyBase, distanceFromBaseAllowed)){
                        inBaseVicinity++;
                    }

                    String s = m.toString();
                    int len = s.length();

                    int indexOf = enemyGardenersLongtermMemory.indexOf(s);
                    int  count = 0;
                    while(indexOf >= 0 && count < 10){
                        count++;
                        indexOf = enemyGardenersLongtermMemory.indexOf(s,indexOf)+len;
                    }

                    if(count >= 10){
//                        Test.lineTo(m);

                        sootforcer.R.rc.broadcastFloat(sootforcer.R.LIST_SNIPE_THESE_START + 1 + snipeTargetsFound+snipeTargetsFound, m.x);
                        sootforcer.R.rc.broadcastFloat(sootforcer.R.LIST_SNIPE_THESE_START + 2 + snipeTargetsFound+snipeTargetsFound, m.y);
                        snipeTargetsFound++;
                    }

                    sootforcer.Map.pushToMapChannel(m, Map.TILE_ENEMY);



//                    Test.log("history length: "  + count);
                }
            }
//            Test.endClockTestAvg(9,"history count");



            sootforcer.Test.log("allied:" + alliedHashes.toString(), sootforcer.Test.Modes.ENEMY_TRACKING);
            sootforcer.Test.log("enemy:" + enemyHashes.toString(), sootforcer.Test.Modes.ENEMY_TRACKING);

//            Test.endClockTestAvg(7,"tracking");

//            //rc.setIndicatorLine(enemyGardenerLocs[i],enemyGardenerLocs[i].add(Direction.NORTH,2),200,0,0);
        }

        BroadcastList.clear(sootforcer.R.LIST_MY_TROOP_REPORT_HASHES_START);
//        BroadcastListMapLocationsHighPrecision.clear(R.LIST_ENEMY_SCOUTS_LOCS_START);
//        BroadcastListMapLocationsHighPrecision.clear(R.LIST_ENEMY_SOLDIER_LOCS_START);
//        BroadcastListMapLocationsHighPrecision.clear(R.LIST_ENEMY_GARDENER_LOCS_START);
//        BroadcastListMapLocationsHighPrecision.clear(R.LIST_ENEMY_TANKS_LOCS_START);

        rc.broadcast(sootforcer.R.LIST_SNIPE_THESE_START,snipeTargetsFound * 2);



        sootforcer.Test.log("Found in base: " + inBaseVicinity, sootforcer.Test.Modes.ENEMY_TRACKING);



        if(macroCircle == null){

            if(inBaseVicinity > 13 || (mapSize == MEDIUM && inBaseVicinity > 12)){
                macroCircle = plausibleMainEnemyBase;

                macroCircleSize = Math.min(distanceFromBaseAllowed + 15,   Math.min((map_top - map_bot) *0.8f,(map_right - map_left) *0.8f));


                sootforcer.Test.log("!!ACTIVATING MACRO CIRCE!!" , sootforcer.Test.Modes.ENEMY_TRACKING );
            }
            turnsWithoutMacroSightings = 0;
        }
        else{
            int friends = rc.readBroadcast(NOOOOOO_PLEASE_STOP_I_HAVE_FAMILY);
            //Guess we killed everything / were hitting friends
            if(inBaseVicinity - (friends * 2) < 3){
                if(++turnsWithoutMacroSightings > 4   || friends > 4) {
                    macroCircle = null;
                    rc.broadcastFloat(MACRO_SPHERE_DESIRE, 0);

                    sootforcer.Test.log("!!DEACTIVATING MACRO CIRCE!!", sootforcer.Test.Modes.ENEMY_TRACKING);
                }
            }else{
                turnsWithoutMacroSightings = 0;
            }

            rc.broadcastInt(NOOOOOO_PLEASE_STOP_I_HAVE_FAMILY,0);


        }




//        for (int i = 0 ; i < enemyGardenerCountSecondToLastTurn; i++){
//            //rc.setIndicatorLine(enemyGardenerLocsSecondToLastTurn[i],enemyGardenerLocsSecondToLastTurn[i].add(Direction.NORTH,2),0,0,200);
//        }
//        for (int i = 0 ; i < enemyGardenerCountLastTurn; i++){
//            //rc.setIndicatorLine(enemyGardenerLocsLastTurn[i],enemyGardenerLocsLastTurn[i].add(Direction.NORTH,2),0,200,0);
//        }

    }


    private static void prioritizeUnitBuilds() throws Exception {



        float soldier = 50;
        float gardener = 50;
        float tree = 50;
        float lumberjack = 50;
        float tank = 50;
        float scout = 50;
        float turn = sootforcer.R.turn;
        float bullets = R.bulletCount;
        float treeCount = R.treeCount;

        totalUnitsPlusTreesMinusArchons =  gardenersAlive + treeCount + soldiersAlive + lumbersAlive + scoutsAlive + tanksAlive;

        if(totalUnitsPlusTreesMinusArchons ==0) totalUnitsPlusTreesMinusArchons = 1;

        float curGardenerRatio = gardenersAlive / totalUnitsPlusTreesMinusArchons;
        float curSoldierRatio = soldiersAlive / totalUnitsPlusTreesMinusArchons;
        float curLumberRatio = lumbersAlive / totalUnitsPlusTreesMinusArchons;
        float curScoutRatio = scoutsAlive / totalUnitsPlusTreesMinusArchons;
        float curTankRatio = tanksAlive / totalUnitsPlusTreesMinusArchons;
        float curTreeRatio = treeCount / totalUnitsPlusTreesMinusArchons;


        float desiredSoldierRatio = 0.32f;
        float desiredScoutRatio = 0.1f;
        float desiredGardenerRatio = 0.08f;
        float desiredLumberRatio = 0.1f;
        float desiredTankRatio = 0;
        float desiredTreeRatio = 0.4f;

        float globalTreeRatio = rc.readBroadcastFloat(sootforcer.R.MAP_TREE_RATIO);


        float buildAtLeast = rc.readBroadcast(sootforcer.R.BUILD_AT_LEAST_COUNT);

        float shortestRush = rc.readBroadcastFloat(SHORTEST_RUSH_INITIAL);




        switch (sootforcer.R.mapType) {
            case sootforcer.R.ECON_MAP:
                sootforcer.Test.log("Map: Econ", sootforcer.Test.Modes.ECON);


                desiredGardenerRatio = 0.11f;
                desiredTreeRatio = 0.54f;
                desiredScoutRatio = 0.04f;
                desiredLumberRatio = 0.03f;
                desiredSoldierRatio = 0.22f;
                desiredTankRatio = 0.05f;


                if (turn < 25 && buildAtLeast < 1) {
                    scout += 200;
                } else if (turn < 25 && buildAtLeast < 2) {
                    soldier += 200;
                }

                break;
            case sootforcer.R.PROBABLE_RUSH:
                sootforcer.Test.log("Map: Probable Rush", sootforcer.Test.Modes.ECON);

                desiredGardenerRatio = 0.16f;
                desiredTreeRatio = 0.34f;
                desiredScoutRatio = 0.05f;
                desiredLumberRatio = 0.02f;
                desiredSoldierRatio = 0.45f;
                desiredTankRatio = 0;

                if (turn < 25 && buildAtLeast < 1) {
                    soldier += 200;
                } else if(turn < 25 && treeCount < 1){
                    tree += 200;
                } else if (turn < 30 && buildAtLeast < 2) {
                    soldier += 200;
                }


                break;
            case sootforcer.R.CERTAIN_RUSH:
                sootforcer.Test.log("Map: Certain Rush", sootforcer.Test.Modes.ECON);

                desiredGardenerRatio = 0.14f;
                desiredTreeRatio = 0.31f;
                desiredScoutRatio = 0.05f;
                desiredLumberRatio = 0.00f;
                desiredSoldierRatio = 0.5f;
                desiredTankRatio = 0;



                if (turn < 15 && buildAtLeast < 2) {
                    soldier += 200;
                }


                break;
            case sootforcer.R.EXTREMELY_TIGHT:
                sootforcer.Test.log("Map: Extremely Tight", sootforcer.Test.Modes.ECON);


                if(turn < 100) {
                    desiredGardenerRatio = 0.045f;
                    desiredTreeRatio = 0.225f;
                    desiredScoutRatio = 0.1f;
                    desiredLumberRatio = 0.4f;
                    desiredSoldierRatio = 0.23f;
                    desiredTankRatio = 0;
                }else if(turn < 200){
                    desiredGardenerRatio = 0.08f;
                    desiredTreeRatio = 0.34f;
                    desiredScoutRatio = 0.05f;
                    desiredLumberRatio = 0.18f;
                    desiredSoldierRatio = 0.35f;
                    desiredTankRatio = 0;
                } else{
                    desiredGardenerRatio = 0.1f;
                    desiredTreeRatio = 0.51f;
                    desiredScoutRatio = 0.03f;
                    desiredLumberRatio = 0.10f;
                    desiredSoldierRatio = 0.26f;
                    desiredTankRatio = 0;
                }



                if (turn < 50 && buildAtLeast < 1) {
                    scout += 200;
                } else if (turn < 50 && buildAtLeast < 2) {
                    lumberjack += 200;
                }


                break;
            case sootforcer.R.CORNER_NO_TREES:
                sootforcer.Test.log("Map: Corner No trees", sootforcer.Test.Modes.ECON);


                desiredGardenerRatio = 0.12f;
                desiredTreeRatio = 0.50f;
                desiredScoutRatio = 0.06f;
                desiredLumberRatio = 0.01f;
                desiredSoldierRatio = 0.31f;
                desiredTankRatio = 0;


                if (turn < 15 && buildAtLeast < 1) {
                    scout += 200;
                } else if (turn < 15 && buildAtLeast < 2) {
                    soldier += 200;
                }
                break;

            case sootforcer.R.GENERIC:
                sootforcer.Test.log("Map: Generic", sootforcer.Test.Modes.ECON);
                if(turn < 200) {
                    desiredGardenerRatio = 0.12f;
                    desiredTreeRatio = 0.5f;
                    desiredScoutRatio = 0.05f;
                    desiredLumberRatio = 0.03f;
                    desiredSoldierRatio = 0.3f;
                    desiredTankRatio = 0;
                }else{
                    desiredGardenerRatio = 0.16f;
                    desiredTreeRatio = 0.55f;
                    desiredScoutRatio = 0.03f;
                    desiredLumberRatio = 0.02f;
                    desiredSoldierRatio = 0.20f;
                    desiredTankRatio = 0.04f;
                }

                break;
            default:
                sootforcer.Test.log("!!!Unknown maptype!!!" + sootforcer.R.mapType);
                break;
        }

        if(archonsAlive == 0){
            desiredSoldierRatio += 0.05f;
            desiredScoutRatio -= 0.01f;
            desiredTreeRatio -= 0.02f;
            desiredLumberRatio -= 0.01f;
        }
        else if(archonsAlive == 2){
            desiredSoldierRatio -= 0.03f;
            desiredTreeRatio += 0.02f;
            desiredGardenerRatio += 0.01f;
        } else if(archonsAlive == 3){
            desiredSoldierRatio -= 0.05f;
            desiredTreeRatio += 0.025f;
            desiredGardenerRatio += 0.015f;
            desiredScoutRatio += 0.01f;
        }


        switch (sootforcer.R.mapSize) {
            case sootforcer.R.TINY:

                sootforcer.Test.log("tiny", sootforcer.Test.Modes.MAPANALYSIS);
                desiredSoldierRatio += 0.07f;
                desiredGardenerRatio -= 0.02f;
                desiredTreeRatio -= 0.03f;
                desiredLumberRatio -= 0.01f;
                desiredScoutRatio -= 0.01f;

                if(lumbersAlive > 5){
                    lumberjack -= 20;
                }
                break;
            case sootforcer.R.SMALL:
                sootforcer.Test.log("small", sootforcer.Test.Modes.MAPANALYSIS);

                desiredSoldierRatio += 0.02f;
                desiredGardenerRatio -= 0.01f;
                desiredTreeRatio -= 0.01f;

                if(lumbersAlive > 6){
                    lumberjack -= 20;
                }
                break;
            case sootforcer.R.MEDIUM:
                sootforcer.Test.log("medium", sootforcer.Test.Modes.MAPANALYSIS);

                if(lumbersAlive > 7){
                    lumberjack -= 20;
                }
                break;
            case sootforcer.R.LARGE:
                sootforcer.Test.log("large", sootforcer.Test.Modes.MAPANALYSIS);

                desiredSoldierRatio -= 0.05f;
                desiredGardenerRatio += 0.006f;
                desiredTreeRatio += 0.034f;
                desiredScoutRatio += 0.01f;

                if(lumbersAlive > 8){
                    lumberjack -= 20;
                }
                break;
            case sootforcer.R.HUGE:
                sootforcer.Test.log("huge", sootforcer.Test.Modes.MAPANALYSIS);

                desiredSoldierRatio -= 0.08f;
                desiredGardenerRatio += 0.009f;
                desiredTreeRatio += 0.041f;
                desiredScoutRatio += 0.02f;
                desiredLumberRatio += 0.01f;

                if(lumbersAlive > 8){
                    lumberjack -= 20;
                }
                break;
        }

        if(globalTreeRatio > 0){

            if(globalTreeRatio < 0.05f){
                //Ideal for tanks and econ, terrible for lumberjacks
                desiredTankRatio += 0.07f;
                desiredLumberRatio -= 0.05f;
                desiredTreeRatio += 0.04f;
                desiredGardenerRatio += 0.01f;
                desiredSoldierRatio -= 0.07f;
                desiredScoutRatio -= 0.01f;

                sootforcer.Test.log("Tree coverage: almost none" , sootforcer.Test.Modes.ECON);
            }
            else if(globalTreeRatio < 0.1f) {
                desiredTankRatio += 0.03f;
                desiredLumberRatio -= 0.03f;
                desiredTreeRatio += 0.01f;

                sootforcer.Test.log("Tree coverage: sparse" , sootforcer.Test.Modes.ECON);

            }
            else if(globalTreeRatio > 0.5f){
                //Insane forest coverage. more lumbers, more trees, less soldiers/tanks
                desiredLumberRatio += 0.04f;
                desiredTreeRatio += 0.03f;
                desiredScoutRatio += 0.01f;
                desiredTankRatio -= 0.1f;
                desiredSoldierRatio -= 0.05f;

                sootforcer.Test.log("Tree coverage: extreme" , sootforcer.Test.Modes.ECON);

            } else if(globalTreeRatio > 0.25f){
                //Pretty heavy treecover still, but much harder to say here what kind of map it is
                //will activate both on hedgemaze and arena
                desiredLumberRatio += 0.02f;
                desiredTankRatio -= 0.05f;
                desiredSoldierRatio -= 0.01f;

                sootforcer.Test.log("Tree coverage: thick" , sootforcer.Test.Modes.ECON);

            } else{
                sootforcer.Test.log("Tree coverage: standard" , sootforcer.Test.Modes.ECON);
            }
        }


        if(buildAtLeast == 1 && turn < 15 && scoutsAlive == 0 && mapSize >= MEDIUM && shortestRush > 25 && bulletInTreesScore > 100){
            scout += 300;
        }





        soldier +=   ( desiredSoldierRatio - curSoldierRatio) * 200;
        tree +=   (desiredTreeRatio - curTreeRatio) * 200;
        tank +=   (desiredTankRatio - curTankRatio) * 200;
        scout +=   ( desiredScoutRatio - curScoutRatio) * 200;
        lumberjack +=   ( desiredLumberRatio - curLumberRatio) * 200;

        if(amIArchon) {
            gardener += (desiredGardenerRatio - curGardenerRatio) * 200;
        }


        lumberjack += lumberTargetsList.size() * 3;

        if(treeCount == 0){
            tree += 50;
        } else if(treeCount == 1){
            tree += 30;
        } else if(treeCount == 2){
            tree += 10;
        } else if(treeCount == 3){
            tree += 5;
        } else if(treeCount == 4){
            tree += 2;
        }

        if(soldiersAlive > treeCount){
            tree += 10;
        }

        if(scoutsAlive == 0){
            scout += bulletInTreesScore ;
        } else if(scoutsAlive <= 1){
            scout +=  bulletInTreesScore / 5f;
        }

        if(curScoutRatio > 0.075){
            scout -= 20;
        }

        if(scoutsAlive > 5){
            scout -= 100;
        }




        float treeFactorMax;
        float treeFactorMin;

        if(rc.readBroadcast(sootforcer.R.TREE_SHAPE) == sootforcer.R.SQUARE_SHAPE){
            treeFactorMax = 6.5f;
            treeFactorMin = 5f;
        }else{
            treeFactorMax = 5f;
            treeFactorMin = 4f;
        }

        if(amIArchon) {

            if(soldiersAlive + tanksAlive + scoutsAlive > 20){
                gardener += 15;
                tree += 15;

            }

            if(sootforcer.R.mapType != sootforcer.R.EXTREMELY_TIGHT) {
                gardener += Math.min(50, R.bulletCount / 30);
                tree += Math.min(10, R.bulletCount / 100);
            }


            if (treeCount / treeFactorMax > gardenersAlive) {
                gardener += 30;
                tree -= 70;
            } else if(treeCount / treeFactorMin < gardenersAlive){
                tree += 15;
                gardener -= 15;

                if(gardenersAlive >= 2 && treeCount < 2){
                    tree += 20;
                }
            }
        }


        if(turn - lastTurnBuildSomething > 40 && tree > gardener){
            tree -= 30;
        }

        if(soldiersAlive == 0){
            tree -= 50;
        }

        if(turn < 100 && soldiersAlive > 0 && R.treeCount < 1){
            tree += 40;
        }


        lumberjack +=  Math.max(0, robotsInTreesScore * 8   -   (lumbersAlive * 40));


        lumberjack += treeScore / 28;
        lumberjack -= lumbersAlive * 8;

        if(treeScore < 50 && globalTreeRatio < 0.1f && lumbersAlive > 0){
            lumberjack -= 50;
        }

        if(amIArchon){
            if(sootforcer.Archon.spaciousness < 75 && turn < 300){
                soldier -= 10;
                lumberjack += 10;
            }
        }

        scout -= scoutsAlive * 5;

        tank -= tanksAlive * 5;
        if(turn < 50 && shortestRush < 20){
            tree -= 50;
        }


        sootforcer.Test.log("gardeners alive: " + gardenersAlive, sootforcer.Test.Modes.ECON);

        if(amIArchon) {




                if (turn - lastTurnBuildSomething > 60 && bullets > 50   && sootforcer.Archon.spaciousness > 50) {

                    if (turn - lastTurnBuildSomething >  80) {
                        if(bullets > 100) {
                            //Once tree check complete, we should be pretty sure that we have no gardener
                            if (gardenersAlive < 3) {
                                if (turn - lastDecidedGardenerDied > 40 && gardenersAlive > 0) {

                                    rc.broadcast(sootforcer.R.GARDENER_DEATH_FLAGS, rc.readBroadcast(sootforcer.R.GARDENER_DEATH_FLAGS) + 1);
                                    gardenersAlive--;
                                    lastDecidedGardenerDied++;
                                }

                                if (turn - lastTurnBuildSomething > 150) {
                                    gardener += 60;
                                } else {
                                    gardener += 40;
                                }
                            }
                        }
                    }else{
                        //First do a tree check, we should be able to build these before gardeners if we do stil have a gardener around, and trees almost never hurt
                        tree += 70;
                    }
                }




            //All gardeners dead
            if (gardenersAlive == 0) {
                gardener += 500;
            }

            if(Archon.spaciousness < 50){
                gardener -= 20;
            }
        }




        if(soldiersAlive < 2 +  (rc.readBroadcast(SOLDIER_DEATH_FLAGS) / 3)){
            soldier += 40;
        }


        switch (lastBestType){
            case ARCHON:
                tree -= repeatBestTYpe * 3;
                break;
            case SOLDIER:
                soldier -= repeatBestTYpe * 3;
                break;
            case LUMBERJACK:
                lumberjack -= repeatBestTYpe * 3;
                break;
            case GARDENER:
                gardener -= repeatBestTYpe * 3;
                break;
            case TANK:
                soldier -= repeatBestTYpe * 3;
                break;
            case SCOUT:
                scout -= repeatBestTYpe * 5;
                break;
        }

        if(Adjustables.ALLOW_VP) {
            boolean wereFocusingOnVPS = rc.readBroadcastBoolean(FOCUS_VP);
            if (!wereFocusingOnVPS) {
                if (R.treeCount > 80 || (rc.getTeamVictoryPoints() > 700 && R.treeCount > 40)) {
                    wereFocusingOnVPS = true;
                }
            }

            if (wereFocusingOnVPS) {
                rc.donate(Math.max(0, rc.getVictoryPointCost() * (int) ((R.bulletCount - 55) / rc.getVictoryPointCost()) + floatSafety));
                gardener -= 1000;
            }
            rc.broadcastBoolean(FOCUS_VP, wereFocusingOnVPS);
        }

        tree += Adjustables.TREE_EXTRA_DESIRE;
        gardener += Adjustables.GARDENER_EXTRA_DESIRE;
        soldier += Adjustables.SOLDIER_EXTRA_DESIRE;
        scout += Adjustables.SCOUT_EXTRA_DESIRE;
        tank += Adjustables.TANK_EXTRA_DESIRE;
        lumberjack += Adjustables.LUMBER_EXTRA_DESIRE;



        RobotType newBest = RobotType.ARCHON;
        float best = tree;

        if(gardener > best){
            best = gardener;
            newBest = RobotType.GARDENER;
        }
        if(soldier > best){
            best = soldier;
            newBest = RobotType.SOLDIER;
        }
        if(lumberjack > best){
            best = lumberjack;
            newBest = RobotType.LUMBERJACK;
        }
        if(scout > best){
            best = scout;
            newBest = RobotType.SCOUT;
        }
        if(tank > best){
            newBest = RobotType.TANK;
        }

        if(newBest == lastBestType){
            repeatBestTYpe++;
        }else{
            repeatBestTYpe = 0;
        }

        lastBestType = newBest;

        rc.broadcastFloat(sootforcer.R.SOLDIER_DESIRE, soldier);
        rc.broadcastFloat(sootforcer.R.GARDENER_DESIRE, gardener);
        rc.broadcastFloat(sootforcer.R.TREE_DESIRE, tree);
        rc.broadcastFloat(sootforcer.R.LUMBER_DESIRE, lumberjack);
        rc.broadcastFloat(sootforcer.R.TANK_DESIRE, tank);
        rc.broadcastFloat(sootforcer.R.SCOUT_DESIRE, scout);

        if (sootforcer.Test.LOG_MODE == sootforcer.Test.Modes.ECON) {
            sootforcer.Test.log("DESIRED RATIOS:");
            sootforcer.Test.log("Soldier: " + desiredSoldierRatio  + " Gardener: " + desiredGardenerRatio);
            sootforcer.Test.log("Tree: " + desiredTreeRatio  + " Lumber: " + desiredLumberRatio);
            sootforcer.Test.log("Scout: " + desiredScoutRatio  + " Tank: " + desiredTankRatio);

            debug_drawecon(soldier,gardener,lumberjack,tree,scout,tank);

        }
    }



    public static void debug_drawecon(float soldier, float gardener, float lumberjack, float tree, float scout, float tank){
        Test.log("Soldier: " + soldier  + " Gardener: " + gardener);
        Test.log("Tree: " + tree  + " Lumber: " + lumberjack);
        Test.log("Scout: " + scout  + " Tank: " + tank);


      //  Test.log("Dumps:");


        //Draw the econ desires
//        float x1;
//        float x2;
//        float x3;
//        float x4;
//        float x5;
//        float x6;
//        if (R.player1) {
//            x1 = R.map_left + 1;
//            x2 = R.map_left + 2;
//            x3 = R.map_left + 3;
//            x4 = R.map_left + 4;
//            x5 = R.map_left + 5;
//            x6 = R.map_left + 6;
//        } else {
//            x1 = R.map_right - 6;
//            x2 = R.map_right - 5;
//            x3 = R.map_right - 4;
//            x4 = R.map_right - 3;
//            x5 = R.map_right - 2;
//            x6 = R.map_right - 1;
//        }
//        MapLocation m1 = new MapLocation(x1, R.map_bot);
//        MapLocation m2 = new MapLocation(x2, R.map_bot);
//        MapLocation m3 = new MapLocation(x3, R.map_bot);
//        MapLocation m4 = new MapLocation(x4, R.map_bot);
//        MapLocation m5 = new MapLocation(x5, R.map_bot);
//        MapLocation m6 = new MapLocation(x6, R.map_bot);
//        float scale = (R.map_top - R.map_bot) / 100;
////            rc.setIndicatorLine(m1, m1.add(Direction.NORTH, gardener * scale), 50, 230, 0);
////            rc.setIndicatorLine(m2, m2.add(Direction.NORTH, lumberjack * scale), 165, 28, 238);
////            rc.setIndicatorLine(m3, m3.add(Direction.NORTH, soldier * scale), 255, 20, 0);
////            rc.setIndicatorLine(m4, m4.add(Direction.NORTH, tank * scale), 120, 5, 5);
////            rc.setIndicatorLine(m5, m5.add(Direction.NORTH, scout * scale), 30, 236, 180);
////            rc.setIndicatorLine(m6, m6.add(Direction.NORTH, tree * scale), 250, 91, 14);
//
//
//        Test.debug_drawLine(m1, m1.add(Direction.NORTH, gardener * scale), R.GARDENERCOLOR);
//        Test.debug_drawLine(m2, m2.add(Direction.NORTH, lumberjack * scale), R.LUMBERJACKCOLOR);
//        Test.debug_drawLine(m3, m3.add(Direction.NORTH, soldier * scale), R.SOLDIERCOLOR);
//        Test.debug_drawLine(m4, m4.add(Direction.NORTH, tank * scale), R.TANKCOLOR);
//        Test.debug_drawLine(m5, m5.add(Direction.NORTH, scout * scale), R.SCOUTCOLOR);
//        Test.debug_drawLine(m6, m6.add(Direction.NORTH, tree * scale), R.TREECOLOR);

        MapLocation mapstart = new MapLocation(R.map_left,R.map_top);
        Test.drawFloat(mapstart,gardener,R.GARDENERCOLOR);
        Test.drawFloat(mapstart.translate(0,-2),lumberjack,R.LUMBERJACKCOLOR);
        Test.drawFloat(mapstart.translate(0,-4),soldier,R.SOLDIERCOLOR);
        Test.drawFloat(mapstart.translate(0,-6),tank,R.TANKCOLOR);
        Test.drawFloat(mapstart.translate(0,-8),scout,R.SCOUTCOLOR);
        Test.drawFloat(mapstart.translate(0,-10),tree,R.TREECOLOR);


        Test.drawString(mapstart.translate(0,-12), mapType + " " + mapSize  ,R.BLACK);
    }


    public static void analyzeMapType() throws Exception{
//        Test.beginClockTest(9);
        float sight = Math.min(8, sootforcer.R.sightradius);
        if(sootforcer.R.trees == null){
            sootforcer.R.trees = rc.senseNearbyTrees(sight);
        }

        float globalTrees = sootforcer.R.rc.readBroadcastFloat(sootforcer.R.MAP_TREE_RATIO);

        if(sootforcer.R.map_right < 0 ){
            sootforcer.R.tryUpdateMap();
        }else {
            if (sootforcer.R.turn - sootforcer.R.lastMapRead > 5) {
                sootforcer.R.readMapSize();
            }
        }

        robotsInTreesScore = 0;
        bulletInTreesScore = 0;

        MapLocation enemyLoc = sootforcer.R.mainTarget;

        if(enemyLoc == null){
            enemyLoc = new MapLocation(rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_1_X),rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_1_Y));
        }
        float myDist = sootforcer.R.myLocation.distanceTo(enemyLoc);

        int neutralsInRange6 = 0;
        int neutralTreesDetected = 0;
        float choppableAreaDetected = 0;
        float friendlyTreeArea = 0;

        int thingsInRange4 = 0;
        int treesInEnemyDirection = 0;
        float friendlyRobotsArea = (sootforcer.R.radius * sootforcer.R.radius * sootforcer.C.PI);

        int count =  Math.min(20, sootforcer.R.trees.length);
        for(int i = 0; i < count; i++) {
            TreeInfo tree = sootforcer.R.trees[i];
            MapLocation loc = tree.location;
            float dist = loc.distanceTo(sootforcer.R.myLocation);

            if (dist < 4) {
                thingsInRange4++;
            }
            if(tree.team.equals(Team.NEUTRAL)) {
                if(dist < 6){
                    neutralsInRange6++;
                }
                neutralTreesDetected++;

                if(tree.containedBullets > 0){
                    bulletInTreesScore += 2 + tree.containedBullets / 20;
                }

                if(tree.containedRobot != null){
                    switch (tree.containedRobot){
                        case SOLDIER:
                            robotsInTreesScore += 3;
                            break;
                        case LUMBERJACK:
                            robotsInTreesScore += 4;
                            break;
                        case SCOUT:
                            robotsInTreesScore += 1;
                            break;
                        case GARDENER:
                            robotsInTreesScore += 1;
                            break;
                        case TANK:
                            robotsInTreesScore += 10;
                            break;
                    }

                }

            } else if(tree.team.equals(sootforcer.R.ally)){
                friendlyRobotsArea++;
            }

            if(myDist > loc.distanceTo(enemyLoc)){
                treesInEnemyDirection++;
            }
            if(tree.team.equals(Team.NEUTRAL)) {
                if(tree.radius < 0.6){
                    choppableAreaDetected += 0.2f;
                }
                choppableAreaDetected += (tree.radius * tree.radius * sootforcer.C.PI);

            }else{
                friendlyTreeArea += sootforcer.C.PI;
            }
        }

        if(sootforcer.R.robotsAlly == null) {
            sootforcer.R.robotsAlly = rc.senseNearbyRobots(sight, sootforcer.R.ally);
        }

        int length = sootforcer.R.robotsAlly.length;
        for(int i = 0; i < length; i++) {
            if(sootforcer.R.robotsAlly[i].location.isWithinDistance(sootforcer.R.myLocation,4)){
                thingsInRange4++;
            }
            float radius = sootforcer.R.robotsAlly[i].getRadius();
            friendlyRobotsArea += (radius * radius * sootforcer.C.PI);
        }



        int cappedNeutrals = Math.min(12,neutralTreesDetected);
        float cappedArea = Math.min(35,choppableAreaDetected);

        treeScore = ((float)neutralsInRange6 * 0.1f) + ((float)cappedNeutrals * 0.8f) + ((float)treesInEnemyDirection * 4f) + (cappedArea * 0.75f) +  ((float)thingsInRange4 / 3f) - (float)R.treeCount;


         nearbyEdges = 0;

        if(sootforcer.R.myX - sootforcer.R.map_left < 5){
            nearbyEdges++;
        }
        if(sootforcer.R.map_right - sootforcer.R.myX < 5){
            nearbyEdges++;
        }
        if(sootforcer.R.myY - sootforcer.R.map_bot < 5){
            nearbyEdges++;
        }
        if(sootforcer.R.map_top - sootforcer.R.myY < 5){
            nearbyEdges++;
        }

        float totalLands = (sight * sight * C.PI)* (1f-((float)nearbyEdges)*0.2f);
        float usableLands = Math.max(0,totalLands - choppableAreaDetected);
        float takenRatio =  usableLands / totalLands;
        spareArea = Math.max(0, (usableLands - (friendlyRobotsArea + friendlyTreeArea)) * 0.9f); //0.9 is wasted room due to circle shapes


        //So some stats to show what scores we get on maps turn 1:
        //Alone    usable:   120    spare: 97    treescore: 0
        //Chess   usable: 100, spare  67  treescore: 137
        //Arena  usable  122, spare 99, treescore 44
        //Cramped  usable 76, spare 54, treescore 44
        //lil forts   usable 14, spare 1, treescore 278
        //boxed   usable 76,  spare 57, treescore 80


        crampedScore = 300 + (treeScore / 2f) - (usableLands /2f) - (spareArea / 3f)  + (takenRatio * 20);

        if(amIArchon) {
            if (sootforcer.R.spaciousness > 0) {
                crampedScore -= sootforcer.R.spaciousness;
            } else {
                crampedScore -= 100;
            }
        }

        //Cramped scores:
        //Alone 103
        //bugtrap 127
        //blitzkrieg 150
        //chevron 206
        //clusters  10
        //deathstar 287
        //dense forest  179
        //digmeout  252
        //modern art   227
        //omgtree 315
        //misaligned 314
        //peaceful encounter 227

        //So basically just a good indication of: is full on econ possible
        //not a good indication of: should we build lumberjacks / gardeners / etc
        //since it gives high cramped scores on maps you should rush on (omgtree, deathstart)
        //while giving lower cramped scores on maps doing a dense build is good (modern art, bugtrap)


        float shortestRush = rc.readBroadcastFloat(sootforcer.R.SHORTEST_RUSH_INITIAL);
        float longestRush = rc.readBroadcastFloat(sootforcer.R.LONGEST_RUSH_INITIAL);


        if(shortestRush <= 0){
            if(sootforcer.R.turn < 10){
                MapLocation[] inital = rc.getInitialArchonLocations(sootforcer.R.enemy);
                shortestRush = 9000;
                longestRush = 0;
                for(int i = 0 ; i < inital.length; i++){
                    float dist = sootforcer.R.myLocation.distanceTo(inital[i]);
                    if(dist < shortestRush) shortestRush = dist;
                    if(dist > longestRush ) longestRush = dist;
                }
            }else{
                shortestRush = 25;
                longestRush = 25;
            }
        }




        float scoreEcon  = (sootforcer.R.mapSize * 100) - crampedScore;
        if(sootforcer.R.startArchonCount == 2){ scoreEcon += 50;}
        else if(sootforcer.R.startArchonCount == 3){ scoreEcon += 100;}
        scoreEcon += R.treeCount * 5;
        if(shortestRush < 15) scoreEcon -= 50;

        float cornerNoTrees;
        if(nearbyEdges >= 2){
            cornerNoTrees = 250 - treeScore;
        }else{
            cornerNoTrees = 0;
        }
        if(sootforcer.R.mapSize == sootforcer.R.TINY) cornerNoTrees -= 100;
        if(sootforcer.R.mapSize == sootforcer.R.SMALL) cornerNoTrees -= 50;


        float extremelyTight = crampedScore * 0.85f;
        if(sootforcer.R.mapSize == sootforcer.R.TINY) extremelyTight -= 50;
        if(sootforcer.R.turn < 100 && shortestRush < 20) extremelyTight -= 70;
        extremelyTight += treesInEnemyDirection * 3;


        float rushScore = 450;

        switch(sootforcer.R.mapSize){
            case sootforcer.R.TINY:
                rushScore += 250;
                break;
            case sootforcer.R.SMALL:
                rushScore += 200;
                break;
            case sootforcer.R.MEDIUM:
                rushScore += 100;
                break;
            case sootforcer.R.HUGE:
                rushScore -= 100;
                break;
        }


        sootforcer.Test.log("shortest rush: " + shortestRush  + "  longest: " + longestRush +  " cramped: " + crampedScore, sootforcer.Test.Modes.MAPANALYSIS );

        rushScore +=  (40 - shortestRush) * 7;
        rushScore -= longestRush * 4;
        rushScore -= treeScore *0.4f;

        if(sootforcer.R.startArchonCount == 2){ rushScore -= 100;}
        else if(sootforcer.R.startArchonCount == 3){ rushScore -= 200;}

        rushScore -= treesInEnemyDirection * 10;
        rushScore -= sootforcer.R.turn * 2;

        if(shortestRush < 8 && sootforcer.R.startArchonCount == 1 && sootforcer.R.turn < 20){
            rushScore += 200;
        }


        float genericScore = 200;


        sootforcer.Test.log("rush: " + rushScore + " econ: " + scoreEcon + " tight: " + extremelyTight + " corner " + cornerNoTrees + " generic " + genericScore, sootforcer.Test.Modes.MAPANALYSIS);


        float bestScore = -10000;
        int bestMap = sootforcer.R.UNKNOWN_MAP_TYPE;



        if(globalTrees > 0){
            if(globalTrees < 0.05){
                scoreEcon += 100;
                rushScore += 100;
                genericScore += 50;
            }
            else if(globalTrees < 0.1){
                scoreEcon += 50;
                rushScore += 50;
                genericScore += 50;
            }
            else if(globalTrees > 0.5){
                scoreEcon -= 50;
                rushScore -= 50;
                genericScore -= 100;
                extremelyTight += 50;
            }
            else if(globalTrees > 0.25f){
                scoreEcon -= 10;
                rushScore -= 30;
                genericScore -= 30;
                extremelyTight += 25;
            }else{
                genericScore += 20;
                rushScore += 20;
                scoreEcon += 20;
            }
        }



        if(scoreEcon > bestScore){
            bestScore = scoreEcon;
            bestMap = sootforcer.R.ECON_MAP;
        }
        if(extremelyTight > bestScore){
            bestScore = extremelyTight;
            bestMap = sootforcer.R.EXTREMELY_TIGHT;
        }
        if(cornerNoTrees > bestScore){
            bestScore = cornerNoTrees;
            bestMap = sootforcer.R.CORNER_NO_TREES;
        }

        if(genericScore > bestScore){
            bestScore = genericScore;
            bestMap = sootforcer.R.GENERIC;
        }

        if(rushScore > bestScore){
            bestScore = rushScore;
            if(rushScore > 500){
                bestMap = sootforcer.R.CERTAIN_RUSH;
            }
            else{
                bestMap = sootforcer.R.PROBABLE_RUSH;
            }
        }



        sootforcer.R.mapType = bestMap;
        rc.broadcast(sootforcer.R.MAP_TYPE, sootforcer.R.mapType);

        switch (sootforcer.R.mapType){
            case sootforcer.R.EXTREMELY_TIGHT:
                sootforcer.Test.log("map type: extremely tight", sootforcer.Test.Modes.MAPANALYSIS);
                break;
            case sootforcer.R.PROBABLE_RUSH:
                sootforcer.Test.log("map type: probable rush", sootforcer.Test.Modes.MAPANALYSIS);
                break;
            case sootforcer.R.CERTAIN_RUSH:
                sootforcer.Test.log("map type: certain rush", sootforcer.Test.Modes.MAPANALYSIS);
                break;
            case sootforcer.R.ECON_MAP:
                sootforcer.Test.log("map type: econ map", sootforcer.Test.Modes.MAPANALYSIS);
                break;
            case sootforcer.R.CORNER_NO_TREES:
                sootforcer.Test.log("map type: corner no trees", sootforcer.Test.Modes.MAPANALYSIS);
                break;
            case sootforcer.R.GENERIC:
                sootforcer.Test.log("map type: generic", sootforcer.Test.Modes.MAPANALYSIS);

                break;
            default:
                sootforcer.Test.log("map type: " + sootforcer.R.mapType, Test.Modes.MAPANALYSIS);
                break;
        }


//        Test.log("sight area" + (sight * sight * C.PI)  + "total lands: " + totalLands +  "choppable: " + choppableAreaDetected  +  "friendly: " + (friendlyRobotsArea + friendlyTreeArea));
//        Test.log(" usable: " + usableLands   +  "   spare:  " +  spareArea  +  " treescore: " + treeScore);
//        Test.endClockTest(9,"analyze map");
    }

    private static void determineMainTarget() throws Exception{


        MapLocation bestGardener = null;
        float bestGardenerDistance = 9999;

        for (int i = 0 ; i < enemyGardenerCount; i++){
            MapLocation loc = enemyGardenerLocs[i];
            float dist = loc.distanceTo(sootforcer.R.myLocation);

            if(dist < bestGardenerDistance){
                bestGardenerDistance = dist;
                bestGardener = loc;
            }
        }

        for (int i = 0 ; i < enemyGardenerCountLastTurn; i++){
            MapLocation loc = enemyGardenerLocsLastTurn[i];
            float dist = loc.distanceTo(sootforcer.R.myLocation) + 2;

            if(dist < bestGardenerDistance){
                bestGardenerDistance = dist;
                bestGardener = loc;
            }
        }

        for (int i = 0 ; i < enemyGardenerCountSecondToLastTurn; i++){
            MapLocation loc = enemyGardenerLocsSecondToLastTurn[i];
            float dist = loc.distanceTo(sootforcer.R.myLocation) + 4;

            if(dist < bestGardenerDistance){
                bestGardenerDistance = dist;
                bestGardener = loc;
            }
        }


        MapLocation bestArchonLoc = null;

        if(bestGardener == null) {

            float bestArchonScore = 999999;

            if (sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_1_LAST_SPOTTED) < 5) {
                MapLocation l = new MapLocation(rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_1_X), rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_1_Y));

                float dist = sootforcer.R.myLocation.distanceTo(l);
                if (sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_1_LAST_TAGGED) < 5) {
                    dist /= 2;
                }


                if(rc.readBroadcastBoolean(sootforcer.R.THEIR_ARCHON_1_STUCK_FROM_START)){
                    dist += 60;
                }

                if (dist < bestArchonScore) {
                    bestArchonLoc = l;
                    bestArchonScore = dist;
                }
            }

            if (sootforcer.R.startArchonCount > 1 && sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_2_LAST_SPOTTED) < 5) {
                MapLocation l = new MapLocation(rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_2_X), rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_2_Y));

                float dist = sootforcer.R.myLocation.distanceTo(l);
                if (sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_2_LAST_TAGGED) < 5) {
                    dist /= 2;
                }

                if(rc.readBroadcastBoolean(sootforcer.R.THEIR_ARCHON_2_STUCK_FROM_START)){
                    dist += 60;
                }

                if (dist < bestArchonScore) {
                    bestArchonLoc = l;
                    bestArchonScore = dist;
                }
            }
            if (sootforcer.R.startArchonCount > 2 && sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_3_LAST_SPOTTED) < 5) {
                MapLocation l = new MapLocation(rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_3_X), rc.readBroadcastFloat(sootforcer.R.THEIR_ARCHON_3_Y));

                float dist = sootforcer.R.myLocation.distanceTo(l);
                if (sootforcer.R.turn - rc.readBroadcast(sootforcer.R.THEIR_ARCHON_3_LAST_TAGGED) < 5) {
                    dist /= 2;
                }

                if(rc.readBroadcastBoolean(sootforcer.R.THEIR_ARCHON_3_STUCK_FROM_START)){
                    dist += 60;
                }

                if (dist < bestArchonScore) {
                    bestArchonLoc = l;
                    bestArchonScore = dist;
                }
            }
        }

        MapLocation mainTarget = null;



        if(bestGardener != null){
            mainTarget = bestGardener;
        }
        else if(bestArchonLoc != null){
            mainTarget = bestArchonLoc;
        }
        else if(cluster1 != null){
            mainTarget = cluster1;
        }
        else if(cluster2 != null){
            mainTarget = cluster2;
        }
        else if(cluster3 != null){
            mainTarget = cluster3;
        }
        else if(cluster4 != null){
            mainTarget = cluster4;
        }
        else if(cluster5 != null){
            mainTarget = cluster5;
        }
        else if(cluster6 != null){
            mainTarget = cluster6;
        }
        else if(cluster7 != null){
            mainTarget = cluster7;
        }
        else{

            if(sootforcer.R.robotsEnemy == null){
                sootforcer.R.robotsEnemy = rc.senseNearbyRobots(sootforcer.R.sightradius, sootforcer.R.enemy);
            }
            if(sootforcer.R.robotsEnemy.length > 0){
                mainTarget = sootforcer.R.robotsEnemy[0].location;
            }else if(confirmedEnemySoldier != null){
                mainTarget = confirmedEnemySoldier;
            } else if(someProbableEnemy != null){
                mainTarget = someProbableEnemy;
            } else if(confirmedEnemyScout != null && sootforcer.R.turnsLeft < 1000){
                mainTarget = confirmedEnemyScout;
            }
        }


//        float max =  R.map_top + R.map_right - R.map_bot - R.map_left;
//        mainTarget = mainTarget.add(mainTarget.directionTo(R.getRandomGoal()),max/6);


        if(mainTarget != null) {
            rc.broadcastFloat(sootforcer.R.MAIN_TARGET_X, mainTarget.x);
            rc.broadcastFloat(sootforcer.R.MAIN_TARGET_Y, mainTarget.y);

            //rc.setIndicatorLine(mainTarget, mainTarget.add(Direction.NORTH.rotateLeftDegrees(45),5), 0,0,0);
            //rc.setIndicatorLine(mainTarget, mainTarget.add(Direction.EAST.rotateLeftDegrees(45),5), 0,0,0);
            //rc.setIndicatorLine(mainTarget, mainTarget.add(Direction.SOUTH.rotateLeftDegrees(45),5), 0,0,0);
            //rc.setIndicatorLine(mainTarget, mainTarget.add(Direction.WEST.rotateLeftDegrees(45),5), 0,0,0);
            lastMainUpd = sootforcer.R.turn;
        }
        else{
            if(sootforcer.R.turn - lastMainUpd > 10 && sootforcer.R.turn > 100) {
                rc.broadcastFloat(R.MAIN_TARGET_X, -1);
            }
        }


    }


    private static void controlMacroCircle() throws Exception{
        if(macroCircle != null){

            macroCircleSize -= RobotType.SOLDIER.strideRadius / 5;


            if(macroCircleSize < 10){
                macroCircle = null;
                rc.broadcastFloat(MACRO_SPHERE_DESIRE, 0);
            }else{
                rc.broadcastFloat(MACRO_SPHERE_X, macroCircle.x);
                rc.broadcastFloat(MACRO_SPHERE_Y, macroCircle.y);
                rc.broadcastFloat(MACRO_SPHERE_DESIRE, 1000);
                rc.broadcastFloat(MACRO_SPHERE_INNER_SIZE,macroCircleSize);
                rc.broadcastFloat(MACRO_SPHERE_SIZE,macroCircleSize + 2);

                if( (R.treeCount > 30 && macroCircleSize < 30) || macroCircleSize < 25 ){
                    rc.broadcastBoolean(MACRO_SPHERE_FIRE_INDISCRIMINATELY, true);
                }
                else{
                    rc.broadcastBoolean(MACRO_SPHERE_FIRE_INDISCRIMINATELY, false);
                }

                debug_drawMacroCircle();

            }


        }


    }

    private static void debug_drawMacroCircle(){

        MapLocation m1 = macroCircle.add(Direction.NORTH,macroCircleSize);
        MapLocation m2 = macroCircle.add(Direction.NORTH.rotateRightDegrees(40),macroCircleSize);
        MapLocation m3 = macroCircle.add(Direction.NORTH.rotateRightDegrees(80),macroCircleSize);
        MapLocation m4 = macroCircle.add(Direction.NORTH.rotateRightDegrees(120),macroCircleSize);
        MapLocation m5 = macroCircle.add(Direction.NORTH.rotateRightDegrees(160),macroCircleSize);
        MapLocation m6 = macroCircle.add(Direction.NORTH.rotateRightDegrees(200),macroCircleSize);
        MapLocation m7 = macroCircle.add(Direction.NORTH.rotateRightDegrees(240),macroCircleSize);
        MapLocation m8 = macroCircle.add(Direction.NORTH.rotateRightDegrees(280),macroCircleSize);
        MapLocation m9 = macroCircle.add(Direction.NORTH.rotateRightDegrees(320),macroCircleSize);

        //rc.setIndicatorLine(m1,m2, 150,30,220);
        //rc.setIndicatorLine(m2,m3, 150,30,220);
        //rc.setIndicatorLine(m4,m3, 150,30,220);
        //rc.setIndicatorLine(m4,m5, 150,30,220);
        //rc.setIndicatorLine(m6,m5, 150,30,220);
        //rc.setIndicatorLine(m6,m7, 150,30,220);
        //rc.setIndicatorLine(m8,m7, 150,30,220);
        //rc.setIndicatorLine(m8,m9, 150,30,220);
        //rc.setIndicatorLine(m1,m9, 150,30,220);

        //rc.setIndicatorLine(m1,macroCircle, 150,30,220);
        //rc.setIndicatorLine(m4,macroCircle, 150,30,220);
        //rc.setIndicatorLine(m7,macroCircle, 150,30,220);


    }

}
