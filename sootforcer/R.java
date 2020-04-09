package sootforcer;

import battlecode.common.*;
import sootforcer.*;
import sootforcer.C;
import sootforcer.DataStructures.BroadcastList;
import sootforcer.DataStructures.BroadcastMapLocsHighRolling;
import sootforcer.M;

/**
 * Created by Hermen on 22/4/2017.
 */
public class R {

    public static RobotController rc;
    public static R robot;
    public static int turn;
    public static int turnsLeft;
    public static int turnLimit;
    public static int semiRandom;


    public static int treeCount;
    public static int robotCount;
    public static float bulletCount;

    public static Team enemy;
    public static Team ally;

    public static MapLocation myLocation;
    public static float myX;
    public static float myY;
    public static float negMyX;
    public static float negMyY;
    public static RobotType myType;
    public static boolean player1;
    public static int myId;
    public static boolean amIGardener;
    public static boolean amISoldier;
    public static boolean amIArchon;
    public static boolean amITank;
    public static boolean amIScout;
    public static boolean amILumber;
    public static int myNr;

    public static int spaciousness;


    public static int maxHp;
    public static float radius;
    public static float sightradius;
    public static float bulletsightradius;
    public static float maxMove;
    public static float maxMoveNeg;
    public static float bulletSpeed;



    public static float map_left = 99999;
    public static float map_right = -99999;
    public static float map_top = -99999;
    public static float map_bot = 99999;
    public static MapLocation mapCenter;
    public static int lastMapRead = 0;

    public static int startArchonCount;
    public static int enemyArchon1Id;
    public static int enemyArchon2Id;
    public static int enemyArchon3Id;


    public static MapLocation randomGoal;
    public static MapLocation mainTarget;


    public static RobotInfo[] robotsEnemy;
    public static RobotInfo[] robotsAlly;

    public static TreeInfo[] trees;
    public static BulletInfo[] bullets;


    public static int lastRandomChange;

    public static float floatSafety;

    public static boolean canBroadcast;


    public static MapLocation hexOrigin;
    public static float hexOriginX;
    public static float hexOriginY;

    public static MapLocation squareOrigin;
    public static float squareOriginX;
    public static float squareOriginY;

    public static MapLocation prevLocation = null;
    public static MapLocation beforePrevLocation = null;
    public static MapLocation beforeBeforePrevLocation = null;

    public static boolean deathFlag;

    public  static float stuckRating;

    private static int stalematecount = 0;

    public void run(){
        try {
            baseInitial();
            initial();
        }catch (Exception e){
            Test.log(e);
            Clock.yield();
        }
        while(true){
            try {
                basePreStep();
                step();
                basePostStep(); //Necessary
                //Not necessary, but nice to do. Also helps burn remaining bytecode
                if(Clock.getBytecodesLeft() > 500){
                    lowkeyCalcs();
                    if(Clock.getBytecodesLeft() > 600) {
                        baseLowkeyCalcs();
                    }
                }
                Clock.yield();
            }catch (Exception e){
                Test.log(e);
                Clock.yield();
            }
        }
    }




    public static void basePreStep() throws Exception{

        turn = rc.getRoundNum();
        semiRandom++;
        turnsLeft = turnLimit - turn;

        trees = null;
        robotsEnemy = null;
        robotsAlly = null;

        sootforcer.M.prestep();
        myLocation = rc.getLocation();
        myX = myLocation.x;
        myY = myLocation.y;
        negMyX = -myX;
        negMyY = -myY;

        treeCount = rc.getTreeCount();
        robotCount = rc.getRobotCount();
        bulletCount = rc.getTeamBullets();

       // Test.log("PENTA: " + GameConstants.P);

        MonsterMove.angledRectangleCount = 0;
        MonsterMove.circleCount = 0;
        MonsterMove.vectorCircleCount = 0;

        float mainx = rc.readBroadcastFloat(MAIN_TARGET_X);
        if(mainx >0) {
            if((turn - rc.readBroadcast(COMMANDER_TURN_REPORTED)) < 10) {
                mainTarget = new MapLocation(mainx, rc.readBroadcastFloat(MAIN_TARGET_Y));
            }else{
                mainTarget = null;
            }
        }else{
            mainTarget = null;
        }

        if(!amIGardener && !amITank){
            if(amIScout || turn % 3 ==0){
                canBroadcast = true;
            }else{
                canBroadcast = false;
            }
        }else{
            canBroadcast = false;
        }


        if(!deathFlag && rc.getHealth() < 5.1f){
            deathFlag = true;
            if(amISoldier){
                rc.broadcast(SOLDIER_DEATH_FLAGS, rc.readBroadcast(SOLDIER_DEATH_FLAGS) + 1);
            } else if(amIGardener){
                rc.broadcast(GARDENER_DEATH_FLAGS, rc.readBroadcast(GARDENER_DEATH_FLAGS) + 1);
            } else if(amILumber){
                rc.broadcast(LUMBER_DEATH_FLAGS, rc.readBroadcast(LUMBER_DEATH_FLAGS) + 1);
            } else if(amIScout){
                rc.broadcast(SCOUT_DEATH_FLAGS, rc.readBroadcast(SCOUT_DEATH_FLAGS) + 1);
            } else if(amITank){
                rc.broadcast(TANK_DEATH_FLAGS, rc.readBroadcast(TANK_DEATH_FLAGS) + 1);
            }
        }

        tankList.onTurnStart();
        soldierList.onTurnStart();
        gardenerList.onTurnStart();
        scoutList.onTurnStart();
        lumberTargetsList.onTurnStart();
        if(amIGardener || amISoldier) {
            emergencyDefenceList.onTurnStart();
        }
        DistributedComputing.beforeEveryTurn();

    }


    public static void basePostStep() throws Exception
    {
        if(canBroadcast && Clock.getBytecodesLeft() > 100 && Commander.isCommanderArchon){
            BroadcastList.push(LIST_MY_TROOP_REPORT_HASHES_START, LIST_MY_TROOP_REPORT_HASHES_END, myLocation.hashCode());
        }

        tankList.onTurnEnd();
        soldierList.onTurnEnd();
        gardenerList.onTurnEnd();
        scoutList.onTurnEnd();
        lumberTargetsList.onTurnEnd();
        emergencyDefenceList.onTurnEnd();


    }


    public void baseInitial() throws Exception{
        robot = this;
        ally = rc.getTeam();
        enemy= ally.opponent();

        treeCount = rc.getTreeCount();
        robotCount = rc.getRobotCount();
        bulletCount = rc.getTeamBullets();

        turnLimit = rc.getRoundLimit();
        turn = rc.getRoundNum();
        myLocation = rc.getLocation();
        prevLocation = myLocation;
        beforePrevLocation = myLocation;
        beforeBeforePrevLocation = myLocation;

        myX = myLocation.x;
        myY = myLocation.y;
        negMyX = -myX;
        negMyY = -myY;

        player1 = ally.equals(Team.A);

        floatSafety = 0.0001f;

        turn = rc.getRoundNum() - 1;
        turnsLeft = 1 + turnLimit  - rc.getRoundNum();
        myType = rc.getType();
        myLocation = rc.getLocation();
        myId = rc.getID();
        semiRandom = turn + myId;
        amIGardener = myType.equals(RobotType.GARDENER);
        amISoldier = myType.equals(RobotType.SOLDIER);
        amITank = myType.equals(RobotType.TANK);
        amIScout = myType.equals(RobotType.SCOUT);
        amIArchon = myType.equals(RobotType.ARCHON);
        amILumber = myType.equals(RobotType.LUMBERJACK);


        if(amILumber){
            myNr = rc.readBroadcast(LUMBER_TOTAL) + 1;
            rc.broadcast(LUMBER_TOTAL,myNr);
        }
        if(amIScout){
            myNr = rc.readBroadcast(SCOUT_TOTAL) + 1;
            rc.broadcast(SCOUT_TOTAL,myNr);
        }
        if(amITank){
            myNr = rc.readBroadcast(TANK_TOTAL) + 1;
            rc.broadcast(TANK_TOTAL,myNr);
        }
        if(amISoldier){
            myNr = rc.readBroadcast(SOLDIER_TOTAL) + 1;
            rc.broadcast(SOLDIER_TOTAL,myNr);
        }
        if(amIGardener){
            myNr = rc.readBroadcast(GARDENER_TOTAL) + 1;
            rc.broadcast(GARDENER_TOTAL,myNr);
        }

        mapType = rc.readBroadcast(MAP_TYPE);


        maxHp = myType.maxHealth;
        radius = myType.bodyRadius;
        sightradius = myType.sensorRadius - floatSafety;
        bulletsightradius = myType.bulletSightRadius - floatSafety;
        maxMove = myType.strideRadius - floatSafety;
        maxMoveNeg = -maxMove;
        bulletSpeed = myType.bulletSpeed;

        startArchonCount = rc.readBroadcast(START_ARCHON_COUNT);

        readEnemyArchonIds();
        Commander.initialize();
        sootforcer.C.init();

        Test.init();
        sootforcer.M.init();
        BulletDodging.init();


        if(amIArchon){
            if(!Archon.amIStuckGardenerSpaceCheck()){
                Archon.stuckFromStart = false;
            }
            else{
                Archon.stuckFromStart = true;
            }
            Archon.initialIdentifyArchonLocationsAndMapSize();
        }else{

            readMapSize();
            if(map_left == 0 || map_top == 0 || map_right == 0 || map_bot == 0){
                map_left = 99999;
                map_right = -99999;
                map_top = -99999;
                map_bot = 99999;
                tryUpdateMap();
            }
        }






    }

    public static void dealWithMacroCircle() throws Exception{

        float desire = rc.readBroadcastFloat(MACRO_SPHERE_DESIRE);

        if(desire < -0.5f){
            MapLocation macroCircleCenter = new MapLocation(rc.readBroadcastFloat(MACRO_SPHERE_X),rc.readBroadcastFloat(MACRO_SPHERE_Y));
            float outer = rc.readBroadcastFloat(MACRO_SPHERE_SIZE) + 2;
            if(!amIGardener || Gardener.strategyType == MOBILE_FACTORY || Gardener.strategyType == FLEE_THEN_SETTLE_APPROPRIATE || Gardener.strategyType == FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO) {
                sootforcer.M.addVectorCircleZone(macroCircleCenter, outer, -50, -20);
            }
            Test.lineTo(macroCircleCenter);

            float insideDist = amIGardener || rc.readBroadcastBoolean(MACRO_SPHERE_FIRE_INDISCRIMINATELY) ?  rc.readBroadcastFloat(MACRO_SPHERE_SIZE) - 1.5f: 15;
            if(macroCircleCenter.isWithinDistance(myLocation,insideDist)){
                rc.broadcastInt(NOOOOOO_PLEASE_STOP_I_HAVE_FAMILY, rc.readBroadcastInt(NOOOOOO_PLEASE_STOP_I_HAVE_FAMILY) + 1);
                canBroadcast = true;  //eh, might as well allow broadcasting now, for gardeners etc
                Test.debug_halo(255,0,255,3);
            }
//            else{
//                Test.log("too far: " + myLocation.distanceTo(macroCircleCenter)   +  "  / " +   rc.readBroadcastFloat(MACRO_SPHERE_SIZE));
//            }
        }

    }

    public static void baseLowkeyCalcs() throws Exception{


        if(Adjustables.ALLOW_VP) {
            if(turnsLeft < 4){
                rc.donate(rc.getVictoryPointCost() *  (int)(R.bulletCount / rc.getVictoryPointCost())  + floatSafety);
            }
            if (rc.getTeamVictoryPoints() + (R.bulletCount / rc.getVictoryPointCost()) >= GameConstants.VICTORY_POINTS_TO_WIN) {
                rc.donate(R.bulletCount);
            }
        }



        if(Clock.getBytecodesLeft() > 500) {
            int switchOperations = semiRandom % 4;
            switch (switchOperations) {
                case 0:
                    readMapSize();
                    updateMapSize();
                    break;
                case 1:
                    readEnemyArchonIds();
                    break;
                case 2:
                    if ((Commander.isCommanderArchon && !rc.readBroadcastBoolean(R.COMMANDER_ISARCHON)) || turn - rc.readBroadcast(COMMANDER_TURN_REPORTED) > 10)
                        Commander.isCommanderArchon = false;
                    break;
                case 3:
                    if (!Commander.amICommander) {
                        mapType = rc.readBroadcast(MAP_TYPE);
                    }
                    if(Adjustables.ALLOW_VP) {
                        if (turn > 200 && R.bulletCount > 2000 && R.treeCount > 15) {
                            rc.donate((rc.getVictoryPointCost() * 5) + floatSafety);
                        }
                        if (R.treeCount == 0 && R.bulletCount > rc.getVictoryPointCost() + 20 && getMyArchon1Loc() == null && getMyArchon2Loc() == null && getMyArchon3Loc() == null && (rc.readBroadcast(GARDENER_TOTAL) - rc.readBroadcast(GARDENER_DEATH_FLAGS)) == 0) {
                            if (++stalematecount > 80) {
                                rc.donate(rc.getVictoryPointCost() + floatSafety);
                            }
                        }
                    }
                    break;

            }
        }
        if(canBroadcast && Clock.getBytecodesLeft() > 500){
            tryUpdateMap();
        }

       if( Clock.getBytecodesLeft() > 500) {
           if (R.canBroadcast) {
//            Map.broadcastPathfindingResults();
               DistributedComputing.doWork();
           }
//           else if (Clock.getBytecodesLeft() > 1000) {
//            Map.broadcastPathfindingResults();
//            Map.pathFinding();
//           }
       }


    }










    public static void addDesireToEdges(float distance, float baseDesire, float distDesire){
        float realDist = distance + radius;
        float relevance = realDist + maxMove;
        if(myX - map_left < relevance){
            if(baseDesire != 0) sootforcer.M.addRectangle(map_left,map_left + realDist,map_top,map_bot,baseDesire);
            if(distDesire != 0){
                sootforcer.M.addVectorCircleZone(new MapLocation(map_left,myY),realDist,0,distDesire);
            }
        } else if(map_right - myX < relevance){
            if(baseDesire != 0) sootforcer.M.addRectangle(map_right,map_right - realDist,map_top,map_bot,baseDesire);
            if(distDesire != 0){
                sootforcer.M.addVectorCircleZone(new MapLocation(map_right,myY),realDist,0,distDesire);
            }
        }
        if(myY - map_bot < relevance){
            if(baseDesire != 0) sootforcer.M.addRectangle(map_left,map_right,map_bot,map_bot+realDist,baseDesire);
            if(distDesire != 0){
                sootforcer.M.addVectorCircleZone(new MapLocation(myX,map_bot),realDist,0,distDesire);
            }
        } else if(map_top - myY < relevance){
            if(baseDesire != 0) sootforcer.M.addRectangle(map_left,map_right,map_top,map_top-realDist,baseDesire);
            if(distDesire != 0){
                sootforcer.M.addVectorCircleZone(new MapLocation(myX,map_top),realDist,0,distDesire);
            }
        }
    }

    public static MapLocation getRandomGoal() throws Exception{
        if(randomGoal == null ||  turn - lastRandomChange  > 60){

            float x;
            float y;
            int rand = semiRandom + Clock.getBytecodesLeft();
            if(rand % 2 == 0){

                //The best spots are occasionally the corners
                switch (rand % 8){
                    case 0:
                        x = map_left;
                        y = map_top;
                        break;
                    case 2:
                        x = map_left;
                        y = map_bot;
                        break;
                    case 4:
                        x = map_right;
                        y = map_top;
                        break;
                    case 6:
                        x = map_right;
                        y = map_bot;
                        break;
                    default:
                        x= mapCenter.x;
                        y=mapCenter.y;
                        break;
                }
            }
            else {
                //But other random spots should be checked too
                if (lastMapRead == 0 || turn - lastMapRead > 10) {
                    readMapSize();
                }
                float difx = map_right - map_left;
                float dify = map_top - map_bot;

                x = map_left + (((Clock.getBytecodeNum() / 3 + rand) * 2) % difx);
                y = map_bot + (((Clock.getBytecodeNum() / 2 + turnsLeft) * 3) % dify);

            }

            randomGoal = new MapLocation(x, y);



            lastRandomChange = turn;
        }

//        //rc.setIndicatorLine(myLocation, randomGoal , 0,0,255);


        if(randomGoal.isWithinDistance(myLocation,1)){
            randomGoal = null;
            randomGoal = getRandomGoal();
        }


            return randomGoal;
    }



    //Heavyish method
    public static int estimateSpaciousness() throws Exception{
        int freeSpots = 0;

        float dist = sootforcer.C.ARCHON_BUILD_DISTANCE;
        for(int i = sootforcer.M.totalDirections -1; i >= 0; i--){
            MapLocation spot = myLocation.add(sootforcer.M.someDirections[i], dist);
            if(rc.onTheMap(spot, sootforcer.C.GARDENER_RADIUS) && !rc.isCircleOccupied(spot, sootforcer.C.GARDENER_RADIUS)) {
                freeSpots++;
            }
        }
        dist += 1.5;
        for(int i = sootforcer.M.totalDirections -1; i >= 0; i--){
            MapLocation spot = myLocation.add(sootforcer.M.someDirections[i], dist);
            if(rc.onTheMap(spot, sootforcer.C.GARDENER_RADIUS) && !rc.isCircleOccupied(spot, sootforcer.C.GARDENER_RADIUS)) {
                freeSpots++;
            }
        }
        dist += 1.5;
        for(int i = sootforcer.M.totalDirections -1; i >= 0; i--){
            MapLocation spot = myLocation.add(sootforcer.M.someDirections[i], dist);
            if(rc.onTheMap(spot, sootforcer.C.GARDENER_RADIUS) && !rc.isCircleOccupied(spot, sootforcer.C.GARDENER_RADIUS)) {
                freeSpots++;
            }
        }
        dist += 1.5;
        for(int i = sootforcer.M.totalDirections -1; i >= 0; i--){
            MapLocation spot = myLocation.add(M.someDirections[i], dist);
            if(rc.onTheMap(spot, sootforcer.C.GARDENER_RADIUS) && !rc.isCircleOccupied(spot, sootforcer.C.GARDENER_RADIUS)) {
                freeSpots++;
            }
        }

        return freeSpots;
    }




    public static void readEnemyArchonIds() throws Exception {
        if( enemyArchon1Id == 0) enemyArchon1Id = rc.readBroadcast(THEIR_ARCHON_1_ID);
        if( startArchonCount > 1 && enemyArchon2Id == 0) enemyArchon2Id = rc.readBroadcast(THEIR_ARCHON_2_ID);
        if( startArchonCount > 2 &&  enemyArchon3Id == 0) enemyArchon3Id = rc.readBroadcast(THEIR_ARCHON_3_ID);
    }

    public static void spottedEnemyArchon(RobotInfo archon) throws Exception{
        int archonnr;

        if(startArchonCount == 1){
            archonnr = 1;
        }
        else {
            int id = archon.ID;
            if (id == enemyArchon1Id) {
                archonnr = 1;
            } else if (id == enemyArchon2Id) {
                archonnr = 2;
            } else if (id == enemyArchon3Id) {
                archonnr = 3;
            } else {
                readEnemyArchonIds();
                if (id == enemyArchon1Id) {
                    archonnr = 1;
                } else if (id == enemyArchon2Id) {
                    archonnr = 2;
                } else if (id == enemyArchon3Id) {
                    archonnr = 3;
                } else {
                    //hmm, guess we haven't seen the archon yet. let's try to figure out which it is.
                    //4+ archons could also be whats happening, but that's super rare and a problem that will likely be solved soon
                    if (startArchonCount == 2) {
                        if (enemyArchon1Id > 0) {
                            archonnr = 2;
                        } else if (enemyArchon2Id > 0) {
                            archonnr = 1;
                        } else {
                            MapLocation m1 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_1_X), rc.readBroadcastFloat(THEIR_ARCHON_1_Y));
                            MapLocation m2 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_2_X), rc.readBroadcastFloat(THEIR_ARCHON_2_Y));
                            if (archon.location.distanceTo(m1) < archon.location.distanceTo(m2)) {
                                archonnr = 1;
                            } else {
                                archonnr = 2;
                            }
                        }
                    } else {  //archoncount must be 3

                        if (enemyArchon1Id > 0) {
                            if (enemyArchon2Id > 0) {
                                archonnr = 3;
                            } else if (enemyArchon3Id > 0) {
                                archonnr = 2;
                            } else {
                                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_2_X), rc.readBroadcastFloat(THEIR_ARCHON_2_Y));
                                MapLocation m2 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_3_X), rc.readBroadcastFloat(THEIR_ARCHON_3_Y));
                                if (archon.location.distanceTo(m1) < archon.location.distanceTo(m2)) {
                                    archonnr = 2;
                                } else {
                                    archonnr = 3;
                                }
                            }
                        } else if (enemyArchon2Id > 0) {

                            if (enemyArchon1Id > 0) {
                                archonnr = 3;
                            } else if (enemyArchon3Id > 0) {
                                archonnr = 1;
                            } else {
                                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_1_X), rc.readBroadcastFloat(THEIR_ARCHON_1_Y));
                                MapLocation m2 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_3_X), rc.readBroadcastFloat(THEIR_ARCHON_3_Y));
                                if (archon.location.distanceTo(m1) < archon.location.distanceTo(m2)) {
                                    archonnr = 1;
                                } else {
                                    archonnr = 3;
                                }
                            }

                        } else if (enemyArchon3Id > 0) {
                            if (enemyArchon1Id > 0) {
                                archonnr = 2;
                            } else if (enemyArchon2Id > 0) {
                                archonnr = 1;
                            } else {
                                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_1_X), rc.readBroadcastFloat(THEIR_ARCHON_1_Y));
                                MapLocation m2 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_2_X), rc.readBroadcastFloat(THEIR_ARCHON_2_Y));
                                if (archon.location.distanceTo(m1) < archon.location.distanceTo(m2)) {
                                    archonnr = 1;
                                } else {
                                    archonnr = 2;
                                }
                            }
                        } else {
                            MapLocation m1 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_1_X), rc.readBroadcastFloat(THEIR_ARCHON_1_Y));
                            MapLocation m2 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_2_X), rc.readBroadcastFloat(THEIR_ARCHON_2_Y));
                            MapLocation m3 = new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_3_X), rc.readBroadcastFloat(THEIR_ARCHON_3_Y));

                            if (archon.location.distanceTo(m1) < archon.location.distanceTo(m2)) {
                                if (archon.location.distanceTo(m1) < archon.location.distanceTo(m3)) {

                                    archonnr = 1;
                                }
                                else{
                                    archonnr = 3;
                                }
                            } else {
                                if (archon.location.distanceTo(m2) < archon.location.distanceTo(m3)) {
                                    archonnr = 2;
                                }
                                else{
                                    archonnr = 3;
                                }
                            }
                        }
                    }
                    if (archonnr == 1) {
                        rc.broadcast(THEIR_ARCHON_1_ID, id);
                        enemyArchon1Id = id;
                    } else if (archonnr == 2) {
                        rc.broadcast(THEIR_ARCHON_2_ID, id);
                        enemyArchon2Id = id;
                    } else{
                        rc.broadcast(THEIR_ARCHON_3_ID, id);
                        enemyArchon3Id = id;
                    }
                }
            }
        }


        int lastUpdated;
        if(archonnr == 1){
            lastUpdated = rc.readBroadcast(THEIR_ARCHON_1_LAST_TAGGED);
        } else if(archonnr == 2){
            lastUpdated = rc.readBroadcast(THEIR_ARCHON_2_LAST_TAGGED);
        } else{
            lastUpdated = rc.readBroadcast(THEIR_ARCHON_3_LAST_TAGGED);

        }

        if(lastUpdated < turn){
            if(archonnr == 1){
                rc.broadcast(THEIR_ARCHON_1_LAST_TAGGED, turn);
                rc.broadcast(THEIR_ARCHON_1_LAST_SPOTTED, turn);
                rc.broadcastFloat(THEIR_ARCHON_1_X, archon.location.x);
                rc.broadcastFloat(THEIR_ARCHON_1_Y, archon.location.y);

            } else if(archonnr == 2){
                rc.broadcast(THEIR_ARCHON_2_LAST_TAGGED, turn);
                rc.broadcast(THEIR_ARCHON_2_LAST_SPOTTED, turn);
                rc.broadcastFloat(THEIR_ARCHON_2_X, archon.location.x);
                rc.broadcastFloat(THEIR_ARCHON_2_Y, archon.location.y);
            } else{
                rc.broadcast(THEIR_ARCHON_3_LAST_TAGGED, turn);
                rc.broadcast(THEIR_ARCHON_3_LAST_SPOTTED, turn);
                rc.broadcastFloat(THEIR_ARCHON_3_X, archon.location.x);
                rc.broadcastFloat(THEIR_ARCHON_3_Y, archon.location.y);
            }
        }



    }


    public static void spottedEnemyGardener(MapLocation loc) throws Exception{
//        BroadcastListMapLocationsHighPrecision.push(LIST_ENEMY_GARDENER_LOCS_START,LIST_ENEMY_GARDENER_LOCS_END,loc);
        gardenerList.push(loc);
    }

    public static void spottedEnemySoldier(MapLocation loc) throws Exception{
//        BroadcastListMapLocationsHighPrecision.push(LIST_ENEMY_SOLDIER_LOCS_START,LIST_ENEMY_SOLDIER_LOCS_END,loc);
        soldierList.push(loc);
    }
    public static void spottedEnemyScout(MapLocation loc) throws Exception{
//        BroadcastListMapLocationsHighPrecision.push(LIST_ENEMY_SCOUTS_LOCS_START,LIST_ENEMY_SCOUTS_LOCS_END,loc);
        scoutList.push(loc);
    }
    public static void spottedEnemyTank(MapLocation loc) throws Exception{
//        BroadcastListMapLocationsHighPrecision.push(LIST_ENEMY_TANKS_LOCS_START,LIST_ENEMY_TANKS_LOCS_END,loc);
        tankList.push(loc);
    }

     public static void readMapSize() throws Exception{
        map_left = rc.readBroadcastFloat(MAP_LEFT);
        map_right = rc.readBroadcastFloat(MAP_RIGHT);
        map_top = rc.readBroadcastFloat(MAP_TOP);
        map_bot = rc.readBroadcastFloat(MAP_BOT);
        lastMapRead = turn;

        mapCenter = new MapLocation((map_right + map_left) / 2, (map_top + map_bot) / 2);


        if(Test.LOG_MODE == Test.Modes.MAPANALYSIS) {
            MapLocation m1 = new MapLocation(map_left, map_top);
            MapLocation m2 = new MapLocation(map_left, map_bot);
            MapLocation m3 = new MapLocation(map_right, map_top);
            MapLocation m4 = new MapLocation(map_right, map_bot);

            //rc.setIndicatorLine(m1, m2, 255,0,0);
            //rc.setIndicatorLine(m1, m3, 255,0,0);
            //rc.setIndicatorLine(m4, m2, 255,0,0);
            //rc.setIndicatorLine(m4, m3, 255,0,0);
        }
    }
    public static void tryUpdateMap() throws Exception{
        if(lastMapRead != turn){
            readMapSize();
        }

        float dist1 = sightradius;
        float dist2 = dist1 - 0.1f;

        MapLocation m;
        m = myLocation.add(Direction.WEST, dist1);
        if(rc.onTheMap(m)){
            mapLeftUpdateAttempt(m.x);
        }
        else{
            m = myLocation.add(Direction.WEST, dist2);
            if(rc.onTheMap(m)){
                mapLeftUpdateAttempt(m.x);
            }
        }

        m = myLocation.add(Direction.EAST, dist1);
        if(rc.onTheMap(m)){
            mapRightUpdateAttempt(m.x);
        }
        else{
            m = myLocation.add(Direction.EAST, dist2);
            if(rc.onTheMap(m)){
                mapRightUpdateAttempt(m.x);
            }
        }
        m = myLocation.add(Direction.NORTH, dist1);
        if(rc.onTheMap(m)){
            mapTopUpdateAttempt(m.y);
        }
        else{
            m = myLocation.add(Direction.NORTH, dist2);
            if(rc.onTheMap(m)){
                mapTopUpdateAttempt(m.y);
            }
        }
        m = myLocation.add(Direction.SOUTH, dist1);
        if(rc.onTheMap(m)){
            mapBotUpdateAttempt(m.y);
        }
        else{
            m = myLocation.add(Direction.SOUTH, dist2);
            if(rc.onTheMap(m)){
                mapBotUpdateAttempt(m.y);
            }
        }

        updateMapSize();


    }
    public static void mapLeftUpdateAttempt(float x) throws Exception{
        if(x < map_left){rc.broadcastFloat(MAP_LEFT,x); map_left = x;}
    }
    public static void mapRightUpdateAttempt(float x) throws Exception{
        if(x > map_right){rc.broadcastFloat(MAP_RIGHT,x); map_right = x;}
    }
    public static void mapTopUpdateAttempt(float y) throws Exception{
        if(y > map_top){rc.broadcastFloat(MAP_TOP,y); map_top = y;}
    }
    public static void mapBotUpdateAttempt(float y) throws Exception{
        if(y < map_bot){rc.broadcastFloat(MAP_BOT,y); map_bot = y;}
    }

    public static void updateMapSize(){
        float total = (map_right - map_left) * (map_top-map_bot);

        if(total < 1000){
            mapSize = TINY;
        } else if(total < 1800){
            mapSize = SMALL;
        } else if(total < 2500){
            mapSize = MEDIUM;
        } else if(total < 4000){
            mapSize = LARGE;
        } else{
            mapSize = HUGE;
        }

//        Test.log("r: " + map_right + " L: " + map_left +  " t: " + map_top + " b: " + map_bot);


    }



    public static MapLocation getMyArchon1Loc() throws Exception{
        if(turn - rc.readBroadcast(MY_ARCHON_1_LAST_SPOTTED) < 5){
            return new MapLocation(rc.readBroadcastFloat(MY_ARCHON_1_X),rc.readBroadcastFloat(MY_ARCHON_1_Y));
        }
        return null;
    }
    public static MapLocation getMyArchon2Loc() throws Exception{
        if(turn - rc.readBroadcast(MY_ARCHON_2_LAST_SPOTTED) < 5){
            return new MapLocation(rc.readBroadcastFloat(MY_ARCHON_2_X),rc.readBroadcastFloat(MY_ARCHON_2_Y));
        }
        return null;
    }
    public static MapLocation getMyArchon3Loc() throws Exception{
        if(turn - rc.readBroadcast(MY_ARCHON_3_LAST_SPOTTED) < 5){
            return new MapLocation(rc.readBroadcastFloat(MY_ARCHON_3_X),rc.readBroadcastFloat(MY_ARCHON_3_Y));
        }
        return null;
    }

    public static MapLocation getTheirArchon1Loc() throws Exception{
        if(turn - rc.readBroadcast(THEIR_ARCHON_1_LAST_SPOTTED) < 5){
            return new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_1_X),rc.readBroadcastFloat(THEIR_ARCHON_1_Y));
        }
        return null;
    }
    public static MapLocation getTheirArchon2Loc() throws Exception{
        if(turn - rc.readBroadcast(THEIR_ARCHON_2_LAST_SPOTTED) < 5){
            return new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_2_X),rc.readBroadcastFloat(THEIR_ARCHON_2_Y));
        }
        return null;
    }
    public static MapLocation getTheirArchon3Loc() throws Exception{
        if(turn - rc.readBroadcast(THEIR_ARCHON_3_LAST_SPOTTED) < 5){
            return new MapLocation(rc.readBroadcastFloat(THEIR_ARCHON_3_X),rc.readBroadcastFloat(THEIR_ARCHON_3_Y));
        }
        return null;
    }


    public static void stuckCalculations(){
        if(myLocation.isWithinDistance(beforePrevLocation,maxMove/ 5f) && myLocation.isWithinDistance(beforePrevLocation,maxMove/ 4f)){
            stuckRating++;

            if(stuckRating > 40){
                Test.lineTo(myLocation.add(Direction.NORTH),255,255,0);
                Test.log("Stuck: " + stuckRating);


                if(stuckRating > 80 && !amIArchon && !amITank && R.treeCount > 10){

                    RobotInfo[] veryClose = rc.senseNearbyRobots(radius + C.GARDENER_RADIUS + 1.9f,ally);

                    for(int i = 0 ; i < veryClose.length; i++){
                        if(veryClose[i].type.equals(RobotType.GARDENER)){
                            Test.lineTo(myLocation.add(Direction.NORTH, 2000),0,0,0);
                            Test.lineTo(myLocation.add(Direction.EAST, 2000),0,0,0);
                            Test.lineTo(myLocation.add(Direction.SOUTH, 2000),0,0,0);
                            Test.lineTo(myLocation.add(Direction.WEST, 2000),0,0,0);
                            rc.disintegrate();
                            return;
                        }
                    }
                }
            }
        }
        else {
            stuckRating *= 0.75f;
        }
    }












    //Overload this
    public boolean shouldBecomeCommander() throws Exception {return false;}
    public void initial() throws Exception{}
    public void step() throws Exception{}
    public void lowkeyCalcs() throws Exception{ }





    //Strategies
    public static final int FREE_FOR_ALL = 0;
    public static final int DODGE_SNIPE_CLUSTERS = 1;

    //Scout exclusive
    public static final int HUNT = 2;
    public static final int SCOUT = 3;
    public static final int HIDE = 4;
    public static final int DOGFIGHT = 5;
    public static final int MACRO_EMERGENCY = 6;
    public static final int ARCHON_GUARD = 7;
    public static final int TANK_BUDDY = 8;



    //Gardener tasks
    public static final int UNKNOWN_TASK = 0;
    public static final int SETTLE_HEX = 1;
    public static final int SETTLE_SQUARES = 2;
    public static final int FLEE_THEN_SETTLE_APPROPRIATE = 3;
    public static final int SETTLE_APPROPRIATE = 4;
    public static final int FLEE_THEN_SETTLE_APPROPRIATE_STAGE_TWO = 5;
    public static final int MOBILE_FACTORY = 6;
    public static final int FLEE_TEMPORARY = 7;



    //Maptypes
    public static final int UNKNOWN_MAP_TYPE = 0;

    public static final int EXTREMELY_TIGHT = 1;
    public static final int CORNER_NO_TREES = 2;
    public static final int ECON_MAP = 3;
    public static final int PROBABLE_RUSH = 4;
    public static final int CERTAIN_RUSH = 5;
    public static final int GENERIC = 6;

    public static int mapType = UNKNOWN_MAP_TYPE;


    public static final int UNKNOWN_MAP_SIZE= -1;
    public static final int TINY = 0;
    public static final int SMALL = 1;
    public static final int MEDIUM = 2;
    public static final int LARGE = 3;
    public static final int HUGE = 4;

    public static int mapSize = UNKNOWN_MAP_SIZE;


    public static final int UNKNOWN_SHAPE = -1;
    public static final int HEX_SHAPE = 1;
    public static final int SQUARE_SHAPE = 0;
    public static int treeShape = SQUARE_SHAPE;


    //Colors
    public static final int[] RED = new int[]{255,0,0};
    public static final int[] GREEN = new int[]{0,255,0};
    public static final int[] BLUE = new int[]{0,0,255};
    public static final int[] WHITE = new int[]{255,255,255};
    public static final int[] BLACK = new int[]{0,0,0};

    public static final int[] CYAN = new int[]{0,255,255};
    public static final int[] MAGENTA = new int[]{255,0,255};
    public static final int[] YELLOW = new int[]{255,255,0};
    public static final int[] GRAY = new int[]{122,122,122};
    public static final int[] ORANGE = new int[]{243, 156, 18};
    public static final int[] PURPLE = new int[]{99, 57, 116};
    public static final int[] AQUA = new int[]{17, 120, 100};
    public static final int[] BROWN = new int[]{147, 81, 22};
    public static final int[] DARKBLUE = new int[]{33, 47, 70};
    public static final int[] LIGHTGRAY = new int[]{192,192,192};
    public static final int[] DARKGRAY = new int[]{192,192,192};
    public static final int[] DARKGREEN = new int[]{0,64,0};
    public static final int[] OLIVE = new int[]{100,100,0};
    public static final int[] DARKORANGE = new int[]{255,140,0};
    public static final int[] GOLD = new int[]{255,215,0};
    public static final int[] PINK = new int[]{255,105,180};
    public static final int[] ORANGERED = new int[]{255,69,0};
    public static final int[] TEAL = new int[]{0,128,128};
    public static final int[] DARKRED = new int[]{64,0,0};

    public static final int[] RED1 = new int[]{123, 36, 28};
    public static final int[] BLUE1 = new int[]{26, 82, 118};
    public static final int[] GREEN1 = new int[]{25, 111, 61};
    public static final int[] YELLOW1 = new int[]{244, 208, 63};
    public static final int[] PURPLE1 = new int[]{128, 0, 128};
    public static final int[] BLUE2 = new int[]{102,205,170};

    public static final int[] GARDENERCOLOR = new int[]{50, 230, 0};
    public static final int[] LUMBERJACKCOLOR = new int[]{165, 28, 238};
    public static final int[] SOLDIERCOLOR = new int[]{255, 20, 0};
    public static final int[] TANKCOLOR = new int[]{120, 5, 5};
    public static final int[] SCOUTCOLOR = new int[]{30, 236, 180};
    public static final int[] TREECOLOR = new int[]{250, 91, 14};



    //Broadcast Channels
    public static final int COMMANDER_ID = 0; //int
    public static final int COMMANDER_TURN_REPORTED = 1; //int
    public static final int COMMANDER_ISARCHON = 2; //bool


    public static final int START_ARCHON_COUNT = 3;  //int

    public static final int MY_ARCHON_1_ID = 4;  //int
    public static final int MY_ARCHON_1_STUCK_FROM_START = 5; //bool
    public static final int MY_ARCHON_1_LAST_SPOTTED = 6;
    public static final int MY_ARCHON_1_X = 7; //float
    public static final int MY_ARCHON_1_Y = 8; //float

    public static final int MY_ARCHON_2_ID = 9;  //int
    public static final int MY_ARCHON_2_STUCK_FROM_START = 10; //bool
    public static final int MY_ARCHON_2_LAST_SPOTTED = 11;
    public static final int MY_ARCHON_2_X = 12; //float
    public static final int MY_ARCHON_2_Y = 13; //float

    public static final int MY_ARCHON_3_ID = 14;  //int
    public static final int MY_ARCHON_3_STUCK_FROM_START = 15; //bool
    public static final int MY_ARCHON_3_LAST_SPOTTED = 16;
    public static final int MY_ARCHON_3_X = 17; //float
    public static final int MY_ARCHON_3_Y = 18; //float


    public static final int THEIR_ARCHON_1_ID = 19;  //int
    public static final int THEIR_ARCHON_1_STUCK_FROM_START = 20; //bool
    public static final int THEIR_ARCHON_1_LAST_SPOTTED = 21; //int
    public static final int THEIR_ARCHON_1_LAST_TAGGED = 22; //float
    public static final int THEIR_ARCHON_1_X = 23; //float
    public static final int THEIR_ARCHON_1_Y = 24; //float

    public static final int THEIR_ARCHON_2_ID = 25;  //int
    public static final int THEIR_ARCHON_2_STUCK_FROM_START = 26; //bool
    public static final int THEIR_ARCHON_2_LAST_SPOTTED = 27; //int
    public static final int THEIR_ARCHON_2_LAST_TAGGED = 28; //float
    public static final int THEIR_ARCHON_2_X = 29; //float
    public static final int THEIR_ARCHON_2_Y = 30; //float

    public static final int THEIR_ARCHON_3_ID = 31;  //int
    public static final int THEIR_ARCHON_3_STUCK_FROM_START = 32; //bool
    public static final int THEIR_ARCHON_3_LAST_SPOTTED = 33; //int
    public static final int THEIR_ARCHON_3_LAST_TAGGED = 34; //float
    public static final int THEIR_ARCHON_3_X = 35; //float
    public static final int THEIR_ARCHON_3_Y = 36; //float


    public static final int MAP_LEFT = 37; //float
    public static final int MAP_RIGHT = 38; //float
    public static final int MAP_TOP = 39; //float
    public static final int MAP_BOT = 40; //float

    public static final int BEST_ARCHON_INITIAL_GARDENER = 41; //int
    public static final int SHORTEST_RUSH_INITIAL = 42; //float
    public static final int LONGEST_RUSH_INITIAL = 43; //float
    public static final int FREE_ARCHONS_START = 44; //int
    public static final int SHORTEST_RUSH_X = 45; //float
    public static final int SHORTEST_RUSH_Y = 46; //float
    public static final int BUILT_INITIAL_GARDENER = 47; //bool

    public static final int ARCHON_1_SPACIOUSNESS = 48; //int
    public static final int ARCHON_2_SPACIOUSNESS = 49; //int
    public static final int ARCHON_3_SPACIOUSNESS = 50; //int


    public static final int MAIN_TARGET_X = 51; //int
    public static final int MAIN_TARGET_Y = 52; //int


    public static final int SOLDIER_TOTAL = 53; //int
    public static final int LUMBER_TOTAL = 113; //int
    public static final int GARDENER_TOTAL = 54; //int
    public static final int SCOUT_TOTAL = 55; //int
    public static final int TANK_TOTAL = 56; //int


    public static final int TURN_MASS_COMBAT_REPORTS = 57; //int
    public static final int MASS_COMBAT_REPORTS = 58; //int
    public static final int MASS_COMBAT_LASTTURN = 59; //int



    public static final int CLUSTER_1_SIZE = 60;
    public static final int CLUSTER_1_X = 61;
    public static final int CLUSTER_1_Y = 62;

    public static final int CLUSTER_2_SIZE = 63;
    public static final int CLUSTER_2_X = 64;
    public static final int CLUSTER_2_Y = 65;

    public static final int CLUSTER_3_SIZE = 66;
    public static final int CLUSTER_3_X = 67;
    public static final int CLUSTER_3_Y = 68;

    public static final int CLUSTER_4_SIZE = 69;
    public static final int CLUSTER_4_X = 70;
    public static final int CLUSTER_4_Y = 71;

    public static final int CLUSTER_5_SIZE = 72;
    public static final int CLUSTER_5_X = 73;
    public static final int CLUSTER_5_Y = 74;

    public static final int CLUSTER_6_SIZE = 75;
    public static final int CLUSTER_6_X = 76;
    public static final int CLUSTER_6_Y = 77;

    public static final int CLUSTER_7_SIZE = 78;
    public static final int CLUSTER_7_X = 79;
    public static final int CLUSTER_7_Y = 80;


    public static final int HEX_ORIGIN_X = 81;
    public static final int HEX_ORIGIN_Y = 82;



    public static final int ARCHON_1_GARDENER_SCORE = 83;
    public static final int ARCHON_2_GARDENER_SCORE = 84;
    public static final int ARCHON_3_GARDENER_SCORE = 85;
    public static final int LAST_UPDATED_GARDENER_SCORE = 86;

    public static final int LATEST_GARDENER_TASK = 87;
    public static final int LATEST_GARDENER_SPOT_X = 88;
    public static final int LATEST_GARDENER_SPOT_Y = 89;

    public static final int SQUARE_TREE_ORIGIN_X = 90;
    public static final int SQUARE_TREE_ORIGIN_Y = 91;

    public static final int TREE_SHAPE = 92; //0 = hexes, 1 = squares
    public static final int SHAPE_TYPE_LASTSWITCHED = 93;

    public static final int ARCHON_1_SUGGESTING_SHAPE = 94; //0 = hexes, 1=squares
    public static final int ARCHON_2_SUGGESTING_SHAPE = 95;
    public static final int ARCHON_3_SUGGESTING_SHAPE = 96;

    public static final int MAP_TYPE = 97;

    public static final int LAST_BUILD_SOMETHING = 98;
    public static final int LAST_BUILD_MILITARY = 99;


    public static final int SOLDIER_DESIRE = 100; //float
    public static final int LUMBER_DESIRE = 101; //float
    public static final int GARDENER_DESIRE = 102; //float
    public static final int SCOUT_DESIRE = 103; //float
    public static final int TANK_DESIRE = 104; //float
    public static final int TREE_DESIRE = 105; //float



    public static final int SOLDIER_DEATH_FLAGS = 106; //int
    public static final int LUMBER_DEATH_FLAGS = 107; //int
    public static final int GARDENER_DEATH_FLAGS = 108; //int
    public static final int SCOUT_DEATH_FLAGS = 109; //int
    public static final int TANK_DEATH_FLAGS = 110; //int

    public static final int MY_ARCHONS_ALIVE = 111; //int

    public static final int MOBILE_GARDENER_LAST_REPORTED = 112; //int
    //skip 113


    public static final int BUILD_AT_LEAST_COUNT = 114; //int


    public static final int MAIN_ARCHON_UNDER_ARREST = 115; //bool

    public static final int PATH_FINDING_START_X = 116;
    public static final int PATH_FINDING_START_Y = 117;
    public static final int PATH_FINDING_END_X = 118;
    public static final int PATH_FINDING_END_Y = 119;
    public static final int PATH_FINDING_BEST_SCORE = 120;
    public static final int PATH_FINDING_LAST_SET = 121;

    public static final int MAP_TREE_RATIO = 122; //Float from 0 to 1

    public static final int GUARD_THEIR_1_LAST_REPORTED= 123; //int
    public static final int GUARD_THEIR_2_LAST_REPORTED= 124; //int
    public static final int GUARD_THEIR_3_LAST_REPORTED= 125; //int

    public static final int ESTIMATED_SHOOTERS_ALIVE = 126; //int



    //Intended to create two giant vector circles for soldiers so they'll do coordinated macro movement
    //There's an internal 'negative' sphere they avoid, and a larger positive sphere with large positive desire inwards
    //While on the little edge, soldiers are supposed to shoot inwards
    public static final int MACRO_SPHERE_DESIRE = 123; //Float
    public static final int MACRO_SPHERE_X = 124; //Float
    public static final int MACRO_SPHERE_Y = 125; //Float
    public static final int MACRO_SPHERE_SIZE = 126; //Float
    public static final int MACRO_SPHERE_INNER_SIZE = 127; //Float
    public static final int MACRO_SPHERE_FIRE_INDISCRIMINATELY = 128; //Boolean

    public static final int NOOOOOO_PLEASE_STOP_I_HAVE_FAMILY = 129; //Int (amount of robots reporting in inside of the macro sphere)

    public static final int FOCUS_VP = 130; //boolean




    public static final int LIST_ENEMY_SOLDIER_LOCS_START = 1700;
    public static final int LIST_ENEMY_SOLDIER_LOCS_END = 1841;
    public static final BroadcastMapLocsHighRolling soldierList = new BroadcastMapLocsHighRolling(LIST_ENEMY_SOLDIER_LOCS_START,LIST_ENEMY_SOLDIER_LOCS_END);

    public static final int LIST_ENEMY_SCOUTS_LOCS_START = 1850;
    public static final int LIST_ENEMY_SCOUTS_LOCS_END = 1901;
    public static final BroadcastMapLocsHighRolling scoutList = new BroadcastMapLocsHighRolling(LIST_ENEMY_SCOUTS_LOCS_START,LIST_ENEMY_SCOUTS_LOCS_END);

    public static final int LIST_ENEMY_TANKS_LOCS_START = 1910;
    public static final int LIST_ENEMY_TANKS_LOCS_END = 1981;
    public static final BroadcastMapLocsHighRolling tankList = new BroadcastMapLocsHighRolling(LIST_ENEMY_TANKS_LOCS_START,LIST_ENEMY_TANKS_LOCS_END);

    public static final int LIST_ENEMY_GARDENER_LOCS_START = 2000;
    public static final int LIST_ENEMY_GARDENER_LOCS_END = 2061;
    public static final BroadcastMapLocsHighRolling gardenerList = new BroadcastMapLocsHighRolling(LIST_ENEMY_GARDENER_LOCS_START,LIST_ENEMY_GARDENER_LOCS_END);


    public static final int LIST_SNIPE_THESE_START = 2110;
    public static final int LIST_SNIPE_THESE_END = 2200;

    public static final int LIST_LUMBER_TARGETS_START = 2310;
    public static final int LIST_LUMBER_TARGETS_END = 2401;
    public static final BroadcastMapLocsHighRolling lumberTargetsList = new BroadcastMapLocsHighRolling(LIST_LUMBER_TARGETS_START,LIST_LUMBER_TARGETS_END);

    public static final int LIST_EMERGENCY_DEFENCE_START = 2410;
    public static final int LIST_EMERGENCY_DEFENCE_END = 2481;
    public static final BroadcastMapLocsHighRolling emergencyDefenceList = new BroadcastMapLocsHighRolling(LIST_EMERGENCY_DEFENCE_START,LIST_EMERGENCY_DEFENCE_END);



    public static final int LIST_MY_TROOP_REPORT_HASHES_START = 3000;
    public static final int LIST_MY_TROOP_REPORT_HASHES_END = 3100;


    public static final int LIST_PATHFINDING_NODES_START = 3110;
    public static final int LIST_PATHFINDING_NODES_END = 3710;


    public static final int DISTRIBUTED_COMPUTING = 4000; //Gaat tot 4513.    32 variables,  32 variabeles per taak (Waarvan 1 de taak status bijhoud)





    public static final int MAP_START = 5000;
    public static final int MAP_END = 7809;




}
