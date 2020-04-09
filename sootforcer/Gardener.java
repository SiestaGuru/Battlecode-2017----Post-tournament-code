package sootforcer;

import battlecode.common.*;
import sootforcer.*;
import sootforcer.Builder;
import sootforcer.C;
import sootforcer.M;
import sootforcer.R;
import sootforcer.Test;

/**
 * Created by Hermen on 22/4/2017.
 */
public class Gardener extends Builder {


    public static int unitBuildFailures = 0;
    public static int plantFailures = 0;
    public static int plantDirectionFailures = 0;


    public static MapLocation dedicatedSpot = null;
    public static int strategyType;

    public static MapLocation chaseSpotForSquareFinish = null;
    public static MapLocation placeTreeAfterChaseSpot = null;
    public static int lastChaseSpotTurn = -10;

    private static final int FLEE_TIME_STD = 120;
    public static int startFleeingTurn = -100;
    private static int gardenerDesiredInRow = 0;

    private static int enemyScouts;
    private static int friendlyScouts;
    private static int friendlySoldiers;

    private static int enemyVeryDangerous;

    private static int startedGoingForSpot = 0;
    private static int nearCuttableTrees = 0;

    private  static MapLocation closestEnemy;

    private int plantedTrees = 0;

    private static float robotsInTreesScore;

    private int noGoodUnitSpotsCounter = 0;

    private static MapLocation[] unitBuildSpots = null;
    private static MapLocation[] treeBuildSpots = null;
    private static MapLocation dontBuildOnTreeSpot = null;

    private static int ignoreHealingTree = -1;
    private static int turnsAgoLastBuildsomething;
    private static int treeKillingDecisionMade = 0;


    private static int lastBuildScout;
    private static int lastBuildLumber;

    private static boolean treeWasHighestLast;

    public void step() throws Exception{


        Commander.doCommanderIfNecessary();

        if(mainTarget != null) {
            sootforcer.M.addVectorCircleZone(mainTarget, 16, 20,-3);
        }
        if(strategyType == MOBILE_FACTORY){
            canBroadcast = true;
            if(turn % 3 == 0) rc.broadcast(MOBILE_GARDENER_LAST_REPORTED, turn);
        }

        turnsAgoLastBuildsomething = turn - rc.readBroadcast(LAST_BUILD_SOMETHING);
        canBroadcast();
        tacticalTreeKilling();

        dealWithRobots();
        dealWithTrees();
        buildStuff();
        dealWithMacroCircle();





        boolean moveToSpot = false;
        if(chaseSpotForSquareFinish != null){
            //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,255,0,0);
             if(turn - lastChaseSpotTurn < 5){
                sootforcer.M.addVector(chaseSpotForSquareFinish, 100);
                sootforcer.M.addSpecialMapLocation(chaseSpotForSquareFinish, 1000);
                moveToSpot = true;
            }else{
                chaseSpotForSquareFinish = null;
            }
        }

        if(enemyVeryDangerous > 0 && (plantedTrees < 3 || (plantedTrees < 4 && strategyType == SETTLE_SQUARES))   && strategyType != FLEE_THEN_SETTLE_APPROPRIATE && strategyType != MOBILE_FACTORY && strategyType != FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO){
            strategyType = FLEE_TEMPORARY;
            startFleeingTurn = turn - (FLEE_TIME_STD - 5);
            sootforcer.Test.log("Fleeing because of enemies");

        }

        if(!moveToSpot){
            if ((strategyType == SETTLE_HEX || strategyType == SETTLE_SQUARES)){
                sootforcer.Test.log("Settling", sootforcer.Test.Modes.GARDENER_VARIED);
//                //rc.setIndicatorDot(myLocation, 0,0,255);

                if(dedicatedSpot != null){

                    if(unitBuildSpots == null || !unitBuildSpots[0].isWithinDistance(dedicatedSpot, 3)){
                        if(strategyType == SETTLE_HEX){
                            unitBuildSpots = new MapLocation[6];

                            unitBuildSpots[0] = dedicatedSpot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                            unitBuildSpots[1] = dedicatedSpot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                            unitBuildSpots[2] = dedicatedSpot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                            unitBuildSpots[3] = dedicatedSpot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                            unitBuildSpots[4] = dedicatedSpot.translate(sootforcer.C.HEX_LEFT_X_DIF, 0);
                            unitBuildSpots[5] = dedicatedSpot.translate(sootforcer.C.HEX_RIGHT_X_DIF, 0);

                            treeBuildSpots = unitBuildSpots;

                            GardenerSpacefinding.init(unitBuildSpots);
                        }else if(strategyType == SETTLE_SQUARES){

                            MapLocation top = dedicatedSpot.translate(0, sootforcer.C.SQUARE_SPACING);
                            MapLocation bot = dedicatedSpot.translate(0,-sootforcer.C.SQUARE_SPACING);
                            MapLocation left = dedicatedSpot.translate(-sootforcer.C.SQUARE_SPACING,0);
                            MapLocation right = dedicatedSpot.translate(sootforcer.C.SQUARE_SPACING,0);
                            unitBuildSpots = new MapLocation[4];
                            treeBuildSpots = new MapLocation[8];

                            unitBuildSpots[0] = top;
                            unitBuildSpots[1] = bot;
                            unitBuildSpots[2] = left;
                            unitBuildSpots[3] = right;

                            treeBuildSpots[0] = top;
                            treeBuildSpots[1] = bot;
                            treeBuildSpots[2] = left;
                            treeBuildSpots[3] = right;

                            treeBuildSpots[4] = top.translate(-sootforcer.C.SQUARE_SPACING,0);
                            treeBuildSpots[5] = top.translate(sootforcer.C.SQUARE_SPACING,0);
                            treeBuildSpots[6] = bot.translate(-sootforcer.C.SQUARE_SPACING,0);
                            treeBuildSpots[7] = bot.translate(sootforcer.C.SQUARE_SPACING,0);
                            GardenerSpacefinding.init(unitBuildSpots);
                        }
                    }

                    sootforcer.M.addVector(dedicatedSpot, 20);
                    sootforcer.M.addSpecialMapLocation(dedicatedSpot, 600);
                    //rc.setIndicatorLine(myLocation, dedicatedSpot, 100, 100, 100);


                    if(dedicatedSpot.isWithinDistance(myLocation, 0.1f)){
                        startedGoingForSpot = 100000;
                    }

                    if(turn - startedGoingForSpot > 120){
                        //Looks like we can't reach our spot
                        if(R.treeCount > 8 && turn - rc.readBroadcast(MOBILE_GARDENER_LAST_REPORTED) > 5){
                            strategyType = MOBILE_FACTORY;
                            dedicatedSpot = null;
                            unitBuildSpots = null;
                        }
                        else{
                            dedicatedSpot = null;
                            unitBuildSpots = null;
                            strategyType = FLEE_THEN_SETTLE_APPROPRIATE;
                            startFleeingTurn = turn;
                            sootforcer.Test.log("Fleeing because cant reach our spot", sootforcer.Test.Modes.GARDENER_VARIED);

                        }

                    }
                }
                else{
                    sootforcer.Test.log("Fleeing because no spot", sootforcer.Test.Modes.GARDENER_VARIED);
                    strategyType = FLEE_THEN_SETTLE_APPROPRIATE;
                    startFleeingTurn = turn;
                }

                if(turn % 25 ==0 && Clock.getBytecodesLeft() > 5000){
                    plantedTrees = rc.senseNearbyTrees(2,ally).length;

                    sootforcer.Test.log("planted trees: " + plantedTrees);

                }


            }
            else if(strategyType == FLEE_THEN_SETTLE_APPROPRIATE || strategyType == FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO || strategyType == FLEE_TEMPORARY) {
                BulletDodging.dodgeBulletsTrig();
                sootforcer.Test.log("Fleeing", sootforcer.Test.Modes.GARDENER_VARIED);


                //rc.setIndicatorDot(myLocation, 255, 0, 200);

                if (myLocation.isWithinDistance(beforePrevLocation, 0.1f)) {
                    startFleeingTurn -= 5; //Let's speed the process up if were standing still
                }

//                if(turn > 80 && rc.readBroadcast(GARDENER_TOTAL) == 1){
//                    strategyType = SETTLE_APPROPRIATE;
//                }

                if (strategyType == FLEE_THEN_SETTLE_APPROPRIATE) {
                    if (mainTarget != null) {
                        sootforcer.M.addVector(mainTarget, -4);
                    } else {
                        sootforcer.M.addVector(mapCenter, -4);
                    }
                    addDesireToEdges(1, -15, -6);

                } else if (strategyType == FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO) {
                    if (mainTarget != null) {
                        sootforcer.M.addVector(mainTarget, -2);
                    } else {
                        sootforcer.M.addVector(mapCenter, -4);
                    }
                    addDesireToEdges(2.5f, -20, -8);
                } else{
                    addDesireToEdges(2, -15, -4);
                }

                if(startFleeingTurn == FLEE_TEMPORARY && dedicatedSpot != null){
                    sootforcer.M.addVector(dedicatedSpot , 5);
                }


                MapLocation m1 = getMyArchon1Loc();
                MapLocation m2 = getMyArchon2Loc();
                MapLocation m3 = getMyArchon3Loc();
                MapLocation t1 = getTheirArchon1Loc();
                MapLocation t2 = getTheirArchon2Loc();
                MapLocation t3 = getTheirArchon3Loc();

                if (m1 != null && t1 != null) {
                    sootforcer.M.addLineDesireSoft(m1, m1.directionTo(t1), 12, -10, -3);
                }
                if (m1 != null && t2 != null) {
                    sootforcer.M.addLineDesireSoft(m1, m1.directionTo(t2), 12, -10, -3);
                }
                if (m1 != null && t3 != null) {
                    sootforcer.M.addLineDesireSoft(m1, m1.directionTo(t3), 12, -10, -3);
                }
                if (m2 != null && t1 != null) {
                    sootforcer.M.addLineDesireSoft(m2, m2.directionTo(t1), 12, -10, -3);
                }
                if (m2 != null && t2 != null) {
                    sootforcer.M.addLineDesireSoft(m2, m2.directionTo(t2), 12, -10, -3);
                }
                if (m2 != null && t3 != null) {
                    sootforcer.M.addLineDesireSoft(m2, m2.directionTo(t3), 12, -10, -3);
                }
                if (m3 != null && t1 != null) {
                    sootforcer.M.addLineDesireSoft(m3, m3.directionTo(t1), 12, -10, -3);
                }
                if (m3 != null && t2 != null) {
                    sootforcer.M.addLineDesireSoft(m3, m3.directionTo(t2), 12, -10, -3);
                }
                if (m3 != null && t3 != null) {
                    sootforcer.M.addLineDesireSoft(m3, m3.directionTo(t3), 12, -10, -3);
                }

                if(turn < 200) {
                    if (m1 != null) {
                        sootforcer.M.addVectorCircleZone(m1, 15, -30, -3);
                    }
                    if (m2 != null) {
                        sootforcer.M.addVectorCircleZone(m2, 15, -30, -3);
                    }
                    if (m3 != null) {
                        sootforcer.M.addVectorCircleZone(m3, 15, -30, -3);
                    }
                }
                if (t1 != null) {
                    sootforcer.M.addVectorCircleZone(t1, 20, -30, -5);
                }
                if (t2 != null) {
                    sootforcer.M.addVectorCircleZone(t2, 20, -30, -5);
                }
                if (t3 != null) {
                    sootforcer.M.addVectorCircleZone(t3, 20, -30, -5);
                }


                if (Clock.getBytecodesLeft() > 2000) {

                    MapLocation[] enemyTanks = tankList.getAll(20);
                    for (int i = enemyTanks.length - 1; i >= 0; i--) {
                        sootforcer.M.addVectorCircleZone(enemyTanks[i], 12, -50, -6);
                    }
                    MapLocation[] enemyScouts = scoutList.getAll(5);
                    for (int i = enemyScouts.length - 1; i >= 0; i--) {
                        sootforcer.M.addVectorCircleZone(enemyScouts[i], 17, -50, -3);
                    }
                }

                if (Clock.getBytecodesLeft() > 2000) {
                    MapLocation[] pings = rc.senseBroadcastingRobotLocations();
                    for (int i = pings.length - 1; i >= 0 && MonsterMove.vectorCircleCount < 15; i--) {
                        sootforcer.M.addVectorCircleZone(pings[i], 12, 0, -1);
                    }
                }

                if (turn - startFleeingTurn > FLEE_TIME_STD || (treeWasHighestLast && R.bulletCount > 90 && enemyVeryDangerous == 0)) {

                    if(strategyType == FLEE_TEMPORARY && dedicatedSpot != null){
                        if(dedicatedSpot.isWithinDistance(myLocation, 0.1f) || turn - startFleeingTurn > FLEE_TIME_STD + 20){
                            strategyType = SETTLE_APPROPRIATE;
                        }
                    }else {
                        strategyType = SETTLE_APPROPRIATE;
                    }
                }

            }
            else if(strategyType == SETTLE_APPROPRIATE){
                sootforcer.Test.log("Settling (prep)", sootforcer.Test.Modes.GARDENER_VARIED);

//                //rc.setIndicatorDot(myLocation, 0,255,0);


                int treeShape = rc.readBroadcast(TREE_SHAPE);

                if(dedicatedSpot != null && dedicatedSpot.isWithinDistance(myLocation, 8)){
                    if(treeShape == HEX_SHAPE) {
                        strategyType = SETTLE_HEX;
                    }else{
                        strategyType = SETTLE_SQUARES;
                    }
                    startedGoingForSpot = turn;

                }else {
                    if (treeShape == HEX_SHAPE) {
                        analyzeNearbyHexSpots(2);
                        if (bestGardenerHexSpot != null) {
                            strategyType = SETTLE_HEX;
                            dedicatedSpot = bestGardenerHexSpot;
                            startedGoingForSpot = turn;
                        } else {
                            strategyType = FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO;
                            startFleeingTurn = turn - (FLEE_TIME_STD - 10); // so we dont run aorund for too long again
                        }
                    } else {
                        analyzeSquareSpots(2);
                        if (bestGardenerSquareSpot != null) {
                            strategyType = SETTLE_SQUARES;
                            dedicatedSpot = bestGardenerSquareSpot;
                            startedGoingForSpot = turn;
                        } else {
                            strategyType = FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO;
                            startFleeingTurn = turn - (FLEE_TIME_STD - 10); //-60 so we dont run aorund for too long again
                        }
                    }
                }
            }
            else if (strategyType == MOBILE_FACTORY){
                sootforcer.Test.log("Mobile factory", sootforcer.Test.Modes.GARDENER_VARIED);

                BulletDodging.dodgeBulletsTrig();

                if(mainTarget != null){
                    sootforcer.M.addVector(mainTarget,5);
                    sootforcer.M.addVector(mapCenter, 2);

                    if(mapSize >= MEDIUM) {
                        sootforcer.M.addVectorCircleZone(mainTarget, 25, -20, -4);
                    }else{
                        sootforcer.M.addVectorCircleZone(mainTarget, 15, -20, -4);
                    }

                    sootforcer.Test.lineTo(mainTarget, sootforcer.Test.Modes.GARDENER_VARIED);
                }
                else{
                    sootforcer.M.addVector(mapCenter, 5);
                }

                if(unitBuildFailures > 10){
                    //Guess we're stuck somehow, let's give another gardener opporunity to be our mobile factory
                    strategyType = FLEE_THEN_SETTLE_APPROPRIATE;
                    dedicatedSpot = null;
                    sootforcer.Test.log("Fleeing because unit build failures" , sootforcer.Test.Modes.GARDENER_VARIED);

                }

               // Now in deal with robots
                if(mapSize >= MEDIUM) {
                    MapLocation[] enemySoldiers = soldierList.getAll(20);
                    for (int i = enemySoldiers.length - 1; i >= 0; i--) {
                        sootforcer.M.addVectorCircleZone(enemySoldiers[i], 20, -50, -6);
                    }
                    MapLocation[] enemyTanks = tankList.getAll(20);
                    for (int i = enemyTanks.length - 1; i >= 0; i--) {
                        sootforcer.M.addVectorCircleZone(enemyTanks[i], 15, -50, -6);
                    }
                    MapLocation[] enemyScouts = scoutList.getAll(5);
                    for (int i = enemyScouts.length - 1; i >= 0; i--) {
                        sootforcer.M.addVectorCircleZone(enemyScouts[i], 17, -50, -3);
                    }
                }

                MapLocation north = myLocation.add(Direction.NORTH,2.5f);
                MapLocation south = myLocation.add(Direction.SOUTH,2.5f);
                MapLocation east = myLocation.add(Direction.EAST,2.5f);
                MapLocation west = myLocation.add(Direction.WEST,2.5f);

                //rc.setIndicatorLine(north,west, 20,130,50);
                //rc.setIndicatorLine(north,east, 20,130,50);
                //rc.setIndicatorLine(south,west, 20,130,50);
                //rc.setIndicatorLine(south,east, 20,130,50);
            }
            else {
                sootforcer.Test.log("Gardener: what am I supposed to do?");

                strategyType = FLEE_THEN_SETTLE_APPROPRIATE;
                //rc.setIndicatorDot(myLocation, 100,100,100);

                sootforcer.M.addVector(getRandomGoal(), 2);
            }
        }

        sootforcer.M.calculateBestMove(2000,dedicatedSpot!= null && dedicatedSpot.isWithinDistance(myLocation,0.1f));

        sootforcer.M.doBestMove();

        if(chaseSpotForSquareFinish != null) {
            if (chaseSpotForSquareFinish.isWithinDistance(myLocation, 0.0001f)) {
                Direction dir = myLocation.directionTo(placeTreeAfterChaseSpot);
                if (rc.canPlantTree(dir)) {
                    rc.plantTree(dir);
                    afterSuccesfulBuild(true);
                    chaseSpotForSquareFinish = null;
                } else {
                    chaseSpotForSquareFinish = null;
                    sootforcer.Test.log("cant build");


                }
            }
        }



        if(!canBroadcast || !GardenerSpacefinding.hasCompletedAnalysis()) { //save processing for communal goals
            GardenerSpacefinding.doWork();
        }
    }


    public static void canBroadcast() throws Exception{
        if(!canBroadcast) {
            MapLocation m1 = getMyArchon1Loc();
            if (m1 != null && m1.isWithinDistance(myLocation, 10)) {
                canBroadcast = true;
            } else {
                MapLocation m2 = getMyArchon1Loc();
                if (m2 != null && m2.isWithinDistance(myLocation, 10)) {
                    canBroadcast = true;
                } else {
                    MapLocation m3 = getMyArchon1Loc();
                    if (m3 != null && m3.isWithinDistance(myLocation, 10)) {
                        canBroadcast = true;
                    }
                }
            }
        }

    }

    public void dealWithRobots() throws Exception {

        robotsEnemy = rc.senseNearbyRobots(sightradius, enemy);
        robotsAlly = rc.senseNearbyRobots(sightradius, ally);

        friendlyScouts = 0;
        friendlySoldiers = 0;

        enemyScouts = 0;
        closestEnemy = null;
        enemyVeryDangerous = 0;


        //Note: friends first
        for (int i = 0; i < robotsAlly.length; i++) {
            RobotInfo r = robotsAlly[i];
            RobotType type = r.type;
            switch (type) {
                case SCOUT:
                    friendlyScouts++;
                    break;
                case SOLDIER:
                    friendlySoldiers++;
                    break;
            }

        }


        for (int i = 0; i < robotsEnemy.length; i++) {
            RobotInfo r = robotsEnemy[i];
            MapLocation loc = r.location;
            RobotType type = r.type;
            float dist = myLocation.distanceTo(loc);

            if(closestEnemy == null) closestEnemy = loc;

            switch (type) {
                case SOLDIER:
                    enemyVeryDangerous++;
                    sootforcer.M.addVector(loc,-10);
                    spottedEnemySoldier(loc);

                    if(friendlySoldiers == 0){
                        emergencyDefenceList.push(loc);
                    }
                    break;
                case GARDENER:
                    spottedEnemyGardener(loc);
                    break;
                case LUMBERJACK:
                    enemyVeryDangerous++;
                    sootforcer.M.addVector(loc,-10);
                    if(friendlySoldiers == 0){
                        emergencyDefenceList.push(loc);
                    }
                    break;
                case ARCHON:
                    spottedEnemyArchon(r);
                    break;
                case SCOUT:
                    spottedEnemyScout(loc);
                    enemyScouts++;
                    if(dist < 3 || rc.getHealth() < 15){
                        enemyScouts++; //emergency
                    }
                    break;
                case TANK:
                    enemyVeryDangerous++;
                    sootforcer.M.addVector(loc,-10);
                    spottedEnemyTank(loc);
                    if(friendlySoldiers == 0){
                        emergencyDefenceList.push(loc);
                    }
                    break;
            }
        }



        if (robotsEnemy.length > 0) {
            canBroadcast = true; //No point in trying to remain silent if they can see us
        }


        MapLocation[] enemySoldiers = soldierList.getAll(20);
        for (int i = enemySoldiers.length - 1; i >= 0; i--) {
            if(enemySoldiers[i].isWithinDistance(myLocation,15 - plantedTrees)) {
                enemyVeryDangerous++;

                sootforcer.M.addVectorCircleZone(enemySoldiers[i], 12, -50, -6);

                if(friendlySoldiers == 0){
                    emergencyDefenceList.push(enemySoldiers[i]);
                }
            }

        }


    }



    public void dealWithTrees() throws Exception
    {
        robotsInTreesScore = 0;
        trees = rc.senseNearbyTrees(6);
        int count = trees.length;
        nearCuttableTrees = 0;

        boolean alreadyGoingForShakeTree = false;

        float bestWaterHealth = 10000;
        int bestTreeToWater = -1;



        for(int i = 0; i < count; i++){
            TreeInfo tree = trees[i];
            MapLocation loc = tree.location;
            float dist = myLocation.distanceTo(loc);

            if(rc.canWater(tree.ID)) {
                if (tree.health < bestWaterHealth) {
                    if (tree.getID() != ignoreHealingTree) {
                        bestWaterHealth = tree.health;
                        bestTreeToWater = tree.ID;
                    }
                }
            }

            if(dist < 2 + tree.radius && !tree.getTeam().equals(ally)) nearCuttableTrees++;


                if (tree.containedBullets > 0) {
                    if (rc.canShake(tree.getID())) {
                        rc.shake(tree.getID());
                    } else if (strategyType != SETTLE_HEX && strategyType != SETTLE_SQUARES && !alreadyGoingForShakeTree) {
                        if (dist < tree.radius + radius + maxMove * 2) {
                            if (!rc.isLocationOccupied(myLocation.add(myLocation.directionTo(loc), maxMove))) {
                                M.addVector(loc, 7);
                                alreadyGoingForShakeTree = true;
                            }
                        }
                    }
                }


            if(strategyType == FLEE_THEN_SETTLE_APPROPRIATE){
                MonsterMove.addCircle(loc,tree.radius + radius + 2f, -20);
            }

            if(canBroadcast && tree.getTeam() != ally){
                if(dist - tree.radius < 3f){
                    lumberTargetsList.push(loc);
                    sootforcer.Test.lineTo(loc, 120,40,170);
                }else if(tree.containedRobot != null && (tree.containedRobot == RobotType.LUMBERJACK || tree.containedRobot == RobotType.TANK || tree.containedRobot == RobotType.SOLDIER ) ){
                    lumberTargetsList.push(loc);
                    sootforcer.Test.lineTo(loc, 120,40,170);
                }
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
        }

        if(bestTreeToWater >=0){
            rc.water(bestTreeToWater);
        }


    }



    public float buildAutonomyRating(){
        if(strategyType == MOBILE_FACTORY)return 1;

        float autonomy = turn / 2000; //full autonomy granted after turn 1500
        autonomy += R.treeCount / 70f; //full autonomy on 50 trees

        autonomy += enemyScouts * 0.25f;

        if(rc.getHealth() < 15) autonomy *= 1.2f;
        return Math.min(1,autonomy);
    }

    public void buildStuff() throws Exception{





        float gardener = rc.readBroadcastFloat(GARDENER_DESIRE) * 0.8f;
        float soldier = rc.readBroadcastFloat(SOLDIER_DESIRE);
        float tank = rc.readBroadcastFloat(TANK_DESIRE);
        float scout = rc.readBroadcastFloat(SCOUT_DESIRE);
        float lumber = rc.readBroadcastFloat(LUMBER_DESIRE);
        float tree = rc.readBroadcastFloat(TREE_DESIRE);


        float buildAutonomy = buildAutonomyRating();


        if(gardener == 0 && soldier == 0 && tank == 0 && scout == 0 && lumber == 0 && tree == 0){
            soldier += 100;
        }


        if(buildAutonomy > 0.1f){
            sootforcer.Test.log("build autonomy: " + buildAutonomy, sootforcer.Test.Modes.GARDENER_VARIED);


            if(robotsEnemy.length == 0) {
                if (rc.readBroadcast(LUMBER_TOTAL) - rc.readBroadcast(LUMBER_DEATH_FLAGS) < 5) {
                    lumber += 12 * nearCuttableTrees * buildAutonomy;


                    if(dedicatedSpot != null &&( strategyType == SETTLE_HEX || strategyType == SETTLE_SQUARES) &&  !dedicatedSpot.isWithinDistance(myLocation, 2) && (turn - startedGoingForSpot > 15 || myLocation.isWithinDistance(beforePrevLocation,0.1f))) {
                        lumber += 5 * nearCuttableTrees * buildAutonomy;

                    }
                }
            }



            lumber +=  6 * robotsInTreesScore * buildAutonomy;

            if(R.bulletCount > 2000){
                soldier += 30 * buildAutonomy;
            }
        }

        if(enemyScouts > friendlyScouts) {
            scout += 100 * buildAutonomy;
        }

        if(strategyType == FLEE_THEN_SETTLE_APPROPRIATE || strategyType == FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO){
            tree = 0;
        }



        if(turn - lastBuildScout < 15){
            scout -= 40;
        }
        if(turn - lastBuildLumber < 15){
            lumber -= 40;
        }

        if(enemyVeryDangerous > friendlySoldiers){
            soldier += 30;
        }

        int mobileReported = rc.readBroadcast(MOBILE_GARDENER_LAST_REPORTED);
        if(mobileReported > 0 && turn - mobileReported < 5){
            if(strategyType == MOBILE_FACTORY){
                tree *= 0.8f;
                gardener *= 0.9f;
                scout = 0;

                tree -= R.bulletCount / 50;
                gardener -= R.bulletCount / 50;

                if(robotsEnemy.length > 0){
                    soldier *= 0.7f;
                    lumber *= 0.2f;
                    tank *= 0.7f;
                }
            }else{
                soldier *= 0.6f;
                lumber *= 0.6f;
            }
        }

        if(strategyType != MOBILE_FACTORY){
            tank *= 0.8f;
        }

        if(enemyVeryDangerous > 0){
            scout = Math.min(0,scout);
            lumber = Math.min(0,lumber);
        }

        if(turn < 60){
            MapLocation e1 = getTheirArchon1Loc();
            MapLocation e2 = getTheirArchon2Loc();
            MapLocation e3 = getTheirArchon3Loc();

            if(e1 != null && myLocation.isWithinDistance(e1,16) && !rc.readBroadcastBoolean(THEIR_ARCHON_1_STUCK_FROM_START)){
                tree -= 250;
                scout -= 20;
                gardener -= 100;
                lumber -= 20;
                sootforcer.Test.log("oh shit, rush map?");
            }
            else if(e2 != null && myLocation.isWithinDistance(e2,16) && !rc.readBroadcastBoolean(THEIR_ARCHON_2_STUCK_FROM_START)){
                tree -= 250;
                scout -= 20;
                gardener -= 100;
                lumber -= 20;
                sootforcer.Test.log("oh shit, rush map?");
            }
            else if(e3 != null && myLocation.isWithinDistance(e3,16) && !rc.readBroadcastBoolean(THEIR_ARCHON_3_STUCK_FROM_START)){
                tree -= 250;
                scout -= 20;
                gardener -= 100;
                lumber -= 20;
                sootforcer.Test.log("oh shit, rush map?");

            }
        }

        if(turn < 200){
            tank = 0;
        }


        if(Commander.isCommanderArchon){
            if(gardenerDesiredInRow > 8){
                gardener *= 0.5f;
            }
        }else{
            gardener = 0; //all of our archons are dead.
        }


        if(noGoodUnitSpotsCounter > 180 && ignoreHealingTree < 0){
            soldier = 0;
            lumber = 0;
            scout = 0;
            tank = 0;
            gardener = 0;
            tree += 10;
        }
        if(ignoreHealingTree > 0){
            tree -= 50;
            gardener -= 30;
        }





//        if(rc.getHealth() < 2 && (enemyVeryDangerous >0 || enemyScouts > 0) && (rc.readBroadcast(GARDENER_TOTAL) - rc.readBroadcast(GARDENER_DEATH_FLAGS)) <= 2){
//            //Just dump some trees down in our dying breath,
//            tree += 30;
//        }


        sootforcer.Test.log("Soldier: " + soldier + " Tree: " + tree + " Lumber: " + lumber + " scout: " + scout + " tank: " + tank + "  gardener " + gardener, sootforcer.Test.Modes.ECON);

        float bestScore = gardener;
        RobotType bestType = null;

        if(soldier > bestScore){
            bestScore = soldier;
            bestType = RobotType.SOLDIER;
        }
        if(lumber > bestScore){
            bestScore = lumber;
            bestType = RobotType.LUMBERJACK;
        }
        if(tank > bestScore){
            bestScore = tank;
            bestType = RobotType.TANK;
        }
        if(scout > bestScore){
            bestScore = scout;
            bestType = RobotType.SCOUT;
        }

        if(tree > bestScore){
            treeWasHighestLast = true;
            if(strategyType != MOBILE_FACTORY) {
                buildTrees();
                gardenerDesiredInRow = 0;
            }
            return;
        }
        treeWasHighestLast = false;

        if(bestType != null){
            gardenerDesiredInRow = 0;
            if(rc.hasRobotBuildRequirements(bestType) && rc.getBuildCooldownTurns() == 0 ){



                Direction dir = null;


                boolean usingPathfindingResult = false;
                boolean limitAngle = false;

                if(bestType != RobotType.SCOUT && dedicatedSpot != null && myLocation.isWithinDistance(dedicatedSpot, 0.01f)) {
                    if (GardenerSpacefinding.hasInit(unitBuildSpots)) {
                        if (GardenerSpacefinding.hasCompletedAnalysis()) {

                            usingPathfindingResult = true;

                            float threshold = 30 + (R.treeCount /4f) - (turnsAgoLastBuildsomething / 4f);


                            MapLocation bestSpot = GardenerSpacefinding.getBestSpot(bestType,threshold);
                            if(bestSpot != null) {
                                dir = myLocation.directionTo(bestSpot);

                                sootforcer.Test.dot(bestSpot);
                                sootforcer.Test.lineTo(bestSpot);
                                noGoodUnitSpotsCounter = 0;

                            }
                            else if(closestEnemy != null){
                                dir = findAvailableRobotBuildDir(bestType, myLocation.directionTo(closestEnemy).opposite());
                            }
                            else if(noGoodUnitSpotsCounter > 40){
                                //alow much worse spots
                                bestSpot = GardenerSpacefinding.getBestSpot(null,threshold + 20);
                                if(bestSpot != null) {

                                    dir = findAvailableRobotBuildDir(bestType,myLocation.directionTo(bestSpot));
                                    sootforcer.Test.dot(bestSpot);
                                    sootforcer.Test.lineTo(bestSpot);
                                    noGoodUnitSpotsCounter = 0;
                                }
                                else{
                                    sootforcer.Test.log("score too low to build desired robot!");
                                    //R.//rc.setIndicatorLine(myLocation, myLocation.add(C.PI/ 2,3), 0,0,0);
                                    ++noGoodUnitSpotsCounter;
                                }
                            }else{
                                sootforcer.Test.log("no free spot");
                                //R.//rc.setIndicatorLine(myLocation, myLocation.add(C.PI/ 2,3), 0,0,100);
                                ++noGoodUnitSpotsCounter;
                            }
                        }
                    } else if (unitBuildSpots != null) {
                        GardenerSpacefinding.init(unitBuildSpots);
                    }
                }

                if(!usingPathfindingResult) {
                    if (closestEnemy != null) {
                        dir = findAvailableRobotBuildDir(bestType, myLocation.directionTo(closestEnemy).opposite());
                    } else {
                        MapLocation towards = mainTarget;
                        if (towards == null) {
                            if (mapCenter == null) {
                                readMapSize();
                            }
                            towards = mapCenter;
                        }
                        if (towards == null) {
                            towards = getRandomGoal();
                        }

                        if(!myLocation.isWithinDistance(towards,0.01f)) {
                            dir = findAvailableRobotBuildDir(bestType, myLocation.directionTo(towards));
                        }
                    }
                }

                if(dir != null){
                    unitBuildFailures = 0;
                    rc.buildRobot(bestType,dir);
                    afterSuccesfulBuild(false);

                    if(bestType == RobotType.SCOUT){
                        lastBuildScout = turn;
                    }
                    if(bestType == RobotType.LUMBERJACK){
                        lastBuildScout = turn;
                    }

                }
                else{
                    unitBuildFailures++;
                }
            }
        }else{
            if(R.bulletCount > 100){
                gardenerDesiredInRow++;
            }

        }





    }


    public void buildTrees() throws Exception{
        if(dedicatedSpot != null) {
            if (myLocation.isWithinDistance(dedicatedSpot, sootforcer.C.SAFETY)){
                if (R.bulletCount > GameConstants.BULLET_TREE_COST) {


                    if(strategyType == SETTLE_HEX) {
                        MapLocation s1 = dedicatedSpot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                        MapLocation s2 = dedicatedSpot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                        MapLocation s3 = dedicatedSpot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                        MapLocation s4 = dedicatedSpot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                        MapLocation s5 = dedicatedSpot.translate(sootforcer.C.HEX_LEFT_X_DIF, 0);
                        MapLocation s6 = dedicatedSpot.translate(sootforcer.C.HEX_RIGHT_X_DIF, 0);

//                    MapLocation s1 = myLocation.translate(C.HEX_LEFT_X_DIF, C.HEX_RIGHT_OTHER_ROW);
//                    MapLocation s2 = myLocation.translate(C.HEX_LEFT_X_DIF, C.HEX_LEFT_OTHER_ROW);
//                    MapLocation s3 = myLocation.translate(C.HEX_RIGHT_X_DIF, C.HEX_RIGHT_OTHER_ROW);
//                    MapLocation s4 = myLocation.translate(C.HEX_RIGHT_X_DIF, C.HEX_LEFT_OTHER_ROW);
//                    MapLocation s5 = myLocation.translate(0, C.HEX_TOP_Y_DIF);
//                    MapLocation s6 = myLocation.translate(0, C.HEX_BOT_Y_DIF);

//                        //rc.setIndicatorDot(s1, 255, 0, 0);
//                        //rc.setIndicatorDot(s2, 255, 0, 0);
//                        //rc.setIndicatorDot(s3, 255, 0, 0);
//                        //rc.setIndicatorDot(s4, 255, 0, 0);
//                        //rc.setIndicatorDot(s5, 255, 0, 0);
//                        //rc.setIndicatorDot(s6, 255, 0, 0);

                        if (rc.canPlantTree(myLocation.directionTo(s1))     && (dontBuildOnTreeSpot == null || !s1.isWithinDistance(dontBuildOnTreeSpot,1))) {
                            rc.plantTree(myLocation.directionTo(s1));
                            afterSuccesfulBuild(true);

                        } else if (rc.canPlantTree(myLocation.directionTo(s2))&& (dontBuildOnTreeSpot == null || !s2.isWithinDistance(dontBuildOnTreeSpot,1))) {
                            rc.plantTree(myLocation.directionTo(s2));
                            afterSuccesfulBuild(true);
                        } else if (rc.canPlantTree(myLocation.directionTo(s3)) && (dontBuildOnTreeSpot == null || !s3.isWithinDistance(dontBuildOnTreeSpot,1))) {
                            rc.plantTree(myLocation.directionTo(s3));
                            afterSuccesfulBuild(true);
                        } else if (rc.canPlantTree(myLocation.directionTo(s4)) && (dontBuildOnTreeSpot == null || !s4.isWithinDistance(dontBuildOnTreeSpot,1))) {
                            rc.plantTree(myLocation.directionTo(s4));
                            afterSuccesfulBuild(true);
                        } else if (rc.canPlantTree(myLocation.directionTo(s5)) && (dontBuildOnTreeSpot == null || !s5.isWithinDistance(dontBuildOnTreeSpot,1))) {
                            rc.plantTree(myLocation.directionTo(s5));
                            afterSuccesfulBuild(true);
                        } else if (rc.canPlantTree(myLocation.directionTo(s6)) && (dontBuildOnTreeSpot == null || !s6.isWithinDistance(dontBuildOnTreeSpot,1))) {
                            rc.plantTree(myLocation.directionTo(s6));
                            afterSuccesfulBuild(true);
                        }else {
                            if (plantedTrees < 8 && ++plantFailures > 60) {
                                Direction dir = findAvailablePlantDir(Direction.NORTH);
                                if (dir != null  && dontBuildOnTreeSpot == null) {
                                    rc.plantTree(dir);
                                    afterSuccesfulBuild(true);
                                    return;
                                }
                            }
                        }
                    } else if(strategyType == SETTLE_SQUARES){


                        MapLocation top = dedicatedSpot.translate(0, sootforcer.C.SQUARE_SPACING);
                        MapLocation bot = dedicatedSpot.translate(0,-sootforcer.C.SQUARE_SPACING);
                        MapLocation left = dedicatedSpot.translate(-sootforcer.C.SQUARE_SPACING,0);
                        MapLocation right = dedicatedSpot.translate(sootforcer.C.SQUARE_SPACING,0);

                        MapLocation topleft = top.translate(-sootforcer.C.SQUARE_SPACING,0);
                        MapLocation topright = top.translate(sootforcer.C.SQUARE_SPACING,0);
                        MapLocation botleft = bot.translate(-sootforcer.C.SQUARE_SPACING,0);
                        MapLocation botright = bot.translate(sootforcer.C.SQUARE_SPACING,0);

                        boolean topleftFree = rc.onTheMap(topleft,GameConstants.BULLET_TREE_RADIUS) && !rc.isCircleOccupiedExceptByThisRobot(topleft,GameConstants.BULLET_TREE_RADIUS) ;
                        boolean toprightFree = rc.onTheMap(topright,GameConstants.BULLET_TREE_RADIUS) &&!rc.isCircleOccupiedExceptByThisRobot(topright,GameConstants.BULLET_TREE_RADIUS) ;
                        boolean botleftFree = rc.onTheMap(botleft,GameConstants.BULLET_TREE_RADIUS) &&!rc.isCircleOccupiedExceptByThisRobot(botleft,GameConstants.BULLET_TREE_RADIUS) ;
                        boolean botrightFree = rc.onTheMap(botright,GameConstants.BULLET_TREE_RADIUS) &&!rc.isCircleOccupiedExceptByThisRobot(botright,GameConstants.BULLET_TREE_RADIUS);
                        boolean leftFree = rc.onTheMap(left,GameConstants.BULLET_TREE_RADIUS) &&!rc.isCircleOccupiedExceptByThisRobot(left,GameConstants.BULLET_TREE_RADIUS);
                        boolean rightFree = rc.onTheMap(right,GameConstants.BULLET_TREE_RADIUS) &&!rc.isCircleOccupiedExceptByThisRobot(right,GameConstants.BULLET_TREE_RADIUS);
                        boolean topFree = rc.onTheMap(top,GameConstants.BULLET_TREE_RADIUS) &&!rc.isCircleOccupiedExceptByThisRobot(top,GameConstants.BULLET_TREE_RADIUS);
                        boolean botFree = rc.onTheMap(bot,GameConstants.BULLET_TREE_RADIUS) &&!rc.isCircleOccupiedExceptByThisRobot(bot,GameConstants.BULLET_TREE_RADIUS);

                        if(topleftFree){
                            MapLocation opposite = topleft.add(topleft.directionTo(dedicatedSpot), sootforcer.C.TREE_SPAWN);
                            if(!rc.isCircleOccupiedExceptByThisRobot(opposite,myType.bodyRadius)){
                                chaseSpotForSquareFinish = opposite;
                                placeTreeAfterChaseSpot = topleft;
                                lastChaseSpotTurn = turn;
                                return;
                            }
                            else if(topFree){
                                chaseSpotForSquareFinish = top;
                                placeTreeAfterChaseSpot = topleft;
                                lastChaseSpotTurn = turn;
                                return;
                            }
                            else if(leftFree){
                                chaseSpotForSquareFinish = left;
                                placeTreeAfterChaseSpot = topleft;
                                lastChaseSpotTurn = turn;
                                return;
                            } else{

                            }
                        }

                        if(botleftFree){
                            MapLocation opposite = botleft.add(botleft.directionTo(dedicatedSpot),  sootforcer.C.TREE_SPAWN);
                            if(!rc.isCircleOccupiedExceptByThisRobot(opposite,myType.bodyRadius)){
                                chaseSpotForSquareFinish = opposite;
                                placeTreeAfterChaseSpot = botleft;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,0,0,255);

                                return;
                            }else if(botFree){
                                chaseSpotForSquareFinish = bot;
                                placeTreeAfterChaseSpot = botleft;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,255,0,0);
                                return;
                            }
                            else if(leftFree){
                                chaseSpotForSquareFinish = left;
                                placeTreeAfterChaseSpot = botleft;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,0,255,0);

                                return;
                            }
                        }

                        if(botrightFree){
                            MapLocation opposite = botright.add(botright.directionTo(dedicatedSpot),  sootforcer.C.TREE_SPAWN);
                            if(!rc.isCircleOccupiedExceptByThisRobot(opposite,myType.bodyRadius)){
                                chaseSpotForSquareFinish = opposite;
                                placeTreeAfterChaseSpot = botright;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,0,0,255);

                                return;
                            }
                            else if(botFree){
                                chaseSpotForSquareFinish = bot;
                                placeTreeAfterChaseSpot = botright;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,255,0,0);

                                return;
                            }
                            else if(rightFree){
                                chaseSpotForSquareFinish = right;
                                placeTreeAfterChaseSpot = botright;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,0,255,0);

                                return;
                            }
                        }

                        if(toprightFree){
                            MapLocation opposite = topright.add(topright.directionTo(dedicatedSpot),  sootforcer.C.TREE_SPAWN);
                            if(!rc.isCircleOccupiedExceptByThisRobot(opposite,myType.bodyRadius)){
                                chaseSpotForSquareFinish = opposite;
                                placeTreeAfterChaseSpot = topright;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,0,0,255);

                                return;
                            }else if(topFree){
                                chaseSpotForSquareFinish = top;
                                placeTreeAfterChaseSpot = topright;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,255,0,0);

                                return;
                            }
                            else if(rightFree){
                                chaseSpotForSquareFinish = right;
                                placeTreeAfterChaseSpot = topright;
                                lastChaseSpotTurn = turn;
                                //rc.setIndicatorLine(myLocation,chaseSpotForSquareFinish,0,255,0);

                                return;
                            }
                        }

                        if(dontBuildOnTreeSpot != null) {
                            sootforcer.Test.lineTo(dontBuildOnTreeSpot, 40,150,70);
                        }else{
                            sootforcer.Test.log("no spot");
                        }

                            if (topFree) {
                                if (rc.canPlantTree(Direction.NORTH) && (dontBuildOnTreeSpot == null || !top.isWithinDistance(dontBuildOnTreeSpot, 1))) {
                                    rc.plantTree(Direction.NORTH);
                                    afterSuccesfulBuild(true);
                                    return;
                                }
                            }
                            //rc.setIndicatorDot(myLocation, 100, 150, 100);

                            if (botFree) {
                                if (rc.canPlantTree(Direction.SOUTH) && (dontBuildOnTreeSpot == null || !bot.isWithinDistance(dontBuildOnTreeSpot, 1))) {
                                    rc.plantTree(Direction.SOUTH);
                                    afterSuccesfulBuild(true);
                                    return;
                                }
                            }
                            //rc.setIndicatorDot(myLocation, 123, 20, 200);

                            if (leftFree) {
                                if (rc.canPlantTree(Direction.WEST) && (dontBuildOnTreeSpot == null || !left.isWithinDistance(dontBuildOnTreeSpot, 1))) {
                                    rc.plantTree(Direction.WEST);
                                    afterSuccesfulBuild(true);
                                    return;
                                }
                            }
                            //rc.setIndicatorDot(myLocation, 170, 40, 180);

                            if (rightFree) {
                                if (rc.canPlantTree(Direction.EAST) && (dontBuildOnTreeSpot == null || !right.isWithinDistance(dontBuildOnTreeSpot, 1))) {
                                    rc.plantTree(Direction.EAST);
                                    afterSuccesfulBuild(true);
                                    return;
                                }
                            }


                        //rc.setIndicatorDot(myLocation,0,0,0);


                        if( plantedTrees < 8 && ++plantFailures > 60){
                            Direction dir = findAvailablePlantDir(Direction.NORTH);
                            if(dir != null  &&   ((dontBuildOnTreeSpot == null || !myLocation.add(dir, sootforcer.C.TREE_SPAWN).isWithinDistance(dontBuildOnTreeSpot, 1)))){
                                rc.plantTree(dir);
                                afterSuccesfulBuild(true);
                                return;
                            }
                        }



                    }
                }
            }
        }
    }


    public void afterSuccesfulBuild(boolean tree) throws Exception{
        //Only doing this for the opening build, where instant precision is very important
        //Dont want the enemy to track us afterwards

        if(rc.readBroadcast(BUILD_AT_LEAST_COUNT) < 2){
            rc.broadcast(BUILD_AT_LEAST_COUNT, rc.readBroadcast(BUILD_AT_LEAST_COUNT) + 1);
        }

        if(tree){
            plantedTrees++;
            plantFailures = 0;
            plantDirectionFailures = 0;
        }
    }


    public void initial() throws Exception{
        hexOrigin = new MapLocation(rc.readBroadcastFloat(HEX_ORIGIN_X), rc.readBroadcastFloat(HEX_ORIGIN_Y));
        hexOriginX = hexOrigin.x;
        hexOriginY = hexOrigin.y;

        squareOrigin = new MapLocation(rc.readBroadcastFloat(SQUARE_TREE_ORIGIN_Y), rc.readBroadcastFloat(SQUARE_TREE_ORIGIN_Y));
        squareOriginX = squareOrigin.x;
        squareOriginY = squareOrigin.y;


        strategyType = rc.readBroadcast(LATEST_GARDENER_TASK);


        if(strategyType != MOBILE_FACTORY) {
            float hexX = rc.readBroadcastFloat(LATEST_GARDENER_SPOT_X);
            if (hexX > 0) {
                dedicatedSpot = new MapLocation(hexX, rc.readBroadcastFloat(LATEST_GARDENER_SPOT_Y));
            }

            if (strategyType == FLEE_THEN_SETTLE_APPROPRIATE) {
                startFleeingTurn = turn;
            }

            if(strategyType == SETTLE_HEX || strategyType == SETTLE_SQUARES){
                startedGoingForSpot = turn;
            }
        }

        rc.broadcast(LATEST_GARDENER_TASK,0);
        rc.broadcastFloat(LATEST_GARDENER_SPOT_X, -1);

    }


    public void tacticalTreeKilling() throws GameActionException{

        if(turnsAgoLastBuildsomething < 30){
            ignoreHealingTree = -1;
        }
        else if(R.bulletCount > 120 && (dontBuildOnTreeSpot == null  || turn-treeKillingDecisionMade > 90)){

            if(treeBuildSpots != null) {
                for (int i = treeBuildSpots.length - 1; i >= 0; i--) {
                    if (treeBuildSpots[i].isWithinDistance(myLocation, C.TREE_SPAWN + floatSafety) && !rc.isCircleOccupiedExceptByThisRobot(treeBuildSpots[i], 1 + floatSafety)) {
                        return;
                    }
                }
            }


            float treekillDesire = GardenerSpacefinding.getBestScore(null,-100000);

            treekillDesire += R.treeCount;
            treekillDesire += turnsAgoLastBuildsomething;

            treekillDesire+= R.bulletCount / 50f;

            treekillDesire += plantedTrees;

            if(R.turn < 200) treekillDesire -= 20;

            if(treekillDesire > 120){

                MapLocation m = GardenerSpacefinding.getBestSpot(null,-100000);
                if(m != null && m.isWithinDistance(myLocation,sightradius)){
                    sootforcer.Test.lineTo(m,255,255,255);
                    TreeInfo tree = rc.senseTreeAtLocation(m);
                    if(tree != null && tree.getTeam().equals(ally)){
                        ignoreHealingTree = tree.ID;
                        dontBuildOnTreeSpot = tree.location;
                        treeKillingDecisionMade = turn;

                    }else{
                        MapLocation[] ignoreSpots = new MapLocation[]{m};

                        MapLocation m2 = GardenerSpacefinding.getBestSpot(null,-100000, ignoreSpots);
                        if(m2 != null && m2.isWithinDistance(myLocation,sightradius)) {
                            sootforcer.Test.lineTo(m2, 255, 200, 200);
                            TreeInfo tree2 = rc.senseTreeAtLocation(m2);
                            if (tree2 != null && tree2.getTeam().equals(ally)) {
                                ignoreHealingTree = tree2.ID;
                                dontBuildOnTreeSpot = tree2.location;
                                treeKillingDecisionMade = turn;

                            }
                            else{
                                ignoreSpots = new MapLocation[]{m,m2};
                                MapLocation m3 = GardenerSpacefinding.getBestSpot(null,-100000, ignoreSpots);
                                if(m3 != null && m3.isWithinDistance(myLocation,sightradius)) {
                                    sootforcer.Test.lineTo(m3, 255, 150, 150);
                                    TreeInfo tree3 = rc.senseTreeAtLocation(m3);
                                    if (tree3 != null && tree3.getTeam().equals(ally)) {
                                        ignoreHealingTree = tree3.ID;
                                        dontBuildOnTreeSpot = tree3.location;
                                        treeKillingDecisionMade = turn;
                                    }

                                }

                            }
                        }

                    }
                }

            }
            sootforcer.Test.log("DESIRE TO KILL TREES, DIEEE TREES: " + treekillDesire, Test.Modes.GARDENER_VARIED);

        }

        if(turn -treeKillingDecisionMade>200){
            ignoreHealingTree = -1;
            dontBuildOnTreeSpot = null;
        }


    }


    public Direction findAvailableRobotBuildDir(RobotType robot, Direction dir) throws GameActionException {

        if (rc.canBuildRobot(robot, dir)) {
            return dir;
        }

        // Now try a bunch of similar angles
        int currentCheck = 1;

        while(currentCheck<=18) {
            // Try the offset of the left side
            if(rc.canBuildRobot(robot,dir.rotateLeftDegrees(10*currentCheck))) {
                return dir.rotateLeftDegrees(10*currentCheck);
            }
            // Try the offset on the right side
            if(rc.canBuildRobot(robot,dir.rotateRightDegrees(10*currentCheck))) {
                return dir.rotateRightDegrees(10*currentCheck);
            }
            // No move performed, try slightly further
            currentCheck++;
        }

//        if(treeBuildSpot != null){
//            originalEnemyDir = treeStartDir.opposite();
//        }




        currentCheck = 1;

        while(currentCheck<=6) {
            // Try the offset of the left side
            if(rc.canBuildRobot(robot,dir.rotateLeftDegrees(60*currentCheck))) {
                return dir.rotateLeftDegrees(60*currentCheck);
            }
            // Try the offset on the right side
            if(rc.canBuildRobot(robot,dir.rotateRightDegrees(60*currentCheck))) {
                return dir.rotateRightDegrees(60*currentCheck);
            }
            // No move performed, try slightly further
            currentCheck++;
        }


        if(unitBuildFailures > 30) {
            unitBuildFailures = 30;
        }
        //Well, try slowly going through all the available options then to be completely sure were not missing anything
        currentCheck = 1;
        float max = 7 + unitBuildFailures;
        float degrees = 360 / max;
        dir = Direction.NORTH;
        while (currentCheck <= max && Clock.getBytecodesLeft() > 4000) {
            // Try the offset of the left side
            if (rc.canBuildRobot(robot, dir.rotateLeftDegrees(degrees * ((float) currentCheck)))) {
                return dir.rotateLeftDegrees(degrees * ((float) currentCheck));
            }
            // Try the offset on the right side
            if (rc.canBuildRobot(robot, dir.rotateRightDegrees(degrees * ((float) currentCheck)))) {
                return dir.rotateRightDegrees(degrees * ((float) currentCheck));
            }
            // No move performed, try slightly further
            currentCheck++;
        }


        return null;
    }

    public Direction findAvailablePlantDir(Direction dir) throws GameActionException {

        if (rc.canPlantTree(dir)) {
            return dir;
        }

        // Now try a bunch of similar angles
        int currentCheck = 1;

        while(currentCheck<=18) {
            // Try the offset of the left side
            if(rc.canPlantTree(dir.rotateLeftDegrees(10*currentCheck))) {
                return dir.rotateLeftDegrees(10*currentCheck);
            }
            // Try the offset on the right side
            if(rc.canPlantTree(dir.rotateRightDegrees(10*currentCheck))) {
                return dir.rotateRightDegrees(10*currentCheck);
            }
            // No move performed, try slightly further
            currentCheck++;
        }

//        if(treeBuildSpot != null){
//            originalEnemyDir = treeStartDir.opposite();
//        }




        currentCheck = 1;

        while(currentCheck<=6) {
            // Try the offset of the left side
            if(rc.canPlantTree(dir.rotateLeftDegrees(60*currentCheck))) {
                return dir.rotateLeftDegrees(60*currentCheck);
            }
            // Try the offset on the right side
            if(rc.canPlantTree(dir.rotateRightDegrees(60*currentCheck))) {
                return dir.rotateRightDegrees(60*currentCheck);
            }
            // No move performed, try slightly further
            currentCheck++;
        }


        if(plantDirectionFailures > 30) {
            plantDirectionFailures = 30;
        }
        //Well, try slowly going through all the available options then to be completely sure were not missing anything
        currentCheck = 1;
        float max = 7 + plantDirectionFailures;
        float degrees = 360 / max;
        dir = Direction.NORTH;
        while (currentCheck <= max && Clock.getBytecodesLeft() > 4000) {
            // Try the offset of the left side
            if (rc.canPlantTree(dir.rotateLeftDegrees(degrees * ((float) currentCheck)))) {
                return dir.rotateLeftDegrees(degrees * ((float) currentCheck));
            }
            // Try the offset on the right side
            if (rc.canPlantTree(dir.rotateRightDegrees(degrees * ((float) currentCheck)))) {
                return dir.rotateRightDegrees(degrees * ((float) currentCheck));
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        plantDirectionFailures++;


        return null;
    }


    public boolean shouldBecomeCommander() throws Exception {
        if(turn - rc.readBroadcast(COMMANDER_TURN_REPORTED) > 8){
            return true;
        }else{
            return false;
        }
    }

}
