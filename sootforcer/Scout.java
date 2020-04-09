package sootforcer;

import battlecode.common.*;
import sootforcer.*;
import sootforcer.C;
import sootforcer.M;
import sootforcer.R;
import sootforcer.Test;

/**
 * Created by Hermen on 22/4/2017.
 */
public class Scout extends Shooter {

    private static float treeChasingDesire;

    private static int lastSwitchedStrat = turn;

    private static MapLocation dogfightEnemy = null;
    private static boolean forceFireAfterMove;
    private static boolean forceFireBeforeMove;


    private static MapLocation[] nearFriendlyTanks = new MapLocation[30];
    private static MapLocation[] nearFriendlyScouts  = new MapLocation[40];
    private static int friendlyTanksCount;
    private static int friendlyScoutsCount;

    private static MapLocation lastTankBuddy  = null;

    private static float totaltreebullets  = 0f;

    protected static int sittingOnTree;
    protected static MapLocation[] avoidWhileSniping = new MapLocation[30];
    protected static int avoidWhilesnipingCount = 0;

    protected static MapLocation[] possibleSnipeTargets = new MapLocation[30];
    protected static float[] possibleSnipeScores = new float[30];
    protected static int possibleSnipeTargetsCount = 0;


    public void step() throws Exception{

        Commander.doCommanderIfNecessary();

        shooterEveryTurn();


        BulletDodging.dodgeBulletsTrig();

        dealWithRobots();
        dealWithTrees();
        dealWithMacroCircle();

        determineStrategy();

        dogfightlogic();
        stuckCalculations();
        considerBeingEnemyArchonGuard();


        if(treeSnipingSpotStandHere != null){
            sootforcer.M.addVector(treeSnipingSpotStandHere,7);
            sootforcer.M.addSpecialMapLocation(treeSnipingSpotStandHere, 10);
            sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.add(myLocation.directionTo(treeSnipingSpotStandHere),maxMove);

            //rc.setIndicatorLine(myLocation,treeSnipingSpotStandHere, 0,0,0);
        }





        if(forceFireBeforeMove){
            fire(false, true);
            sootforcer.M.calculateBestMove(2000,false);
            sootforcer.M.doBestMove();
            sootforcer.Test.log("force fire before");
        }
        else if(forceFireAfterMove){
            sootforcer.M.calculateBestMove(2000,false);
            sootforcer.M.doBestMove();
            fire(false, false);
            sootforcer.Test.log("force fire after");

        }
        else{
            sootforcer.M.calculateBestMove(2000, bullets.length < 4 && robotsEnemy.length == 0);
            fireBeforeMove();
            sootforcer.M.doBestMove();

            if(rc.hasAttacked()){
                sootforcer.Test.log("unforced before");

            }else{
                sootforcer.Test.log("unforced after");

            }
            fireAfterMove();

        }

    }

    public void initial() throws Exception{
        strategyMode = HUNT;
    }





    public void determineStrategy() throws Exception{


        if(strategyMode == TANK_BUDDY  && lastTankBuddy == null ){
            strategyMode = HUNT;
            sootforcer.Test.log("our buddy is dead D:");
        }

        if(amIGuardingSomething){
            strategyMode = ARCHON_GUARD;
        }
        else if((R.treeCount ==0 && R.bulletCount > 135 && turn > 50)){
            //Theyre probably killing off all of our units
            strategyMode = MACRO_EMERGENCY;
        }
        else if(Commander.amICommander){
            strategyMode = HIDE;
        }
        else if(mainTarget == null){
            if(rc.getHealth() < 2){
                if(strategyMode != HIDE) {
                    strategyMode = HIDE;
                    lastSwitchedStrat = turn;
                }
            }else if(strategyMode != DOGFIGHT){
                if(strategyMode != SCOUT) {
                    strategyMode = SCOUT;
                    lastSwitchedStrat = turn;
                }
            }
        }else{
            if(rc.getHealth() < 2){
                if(strategyMode != HIDE) {
                    strategyMode = HIDE;
                    lastSwitchedStrat = turn;
                }
            }
            else if(strategyMode != SCOUT && robotsAlly.length > 4 && semiRandom % 20 == 0){
                strategyMode = SCOUT;
                lastSwitchedStrat = turn;

            }
            else if(strategyMode == SCOUT || strategyMode == DOGFIGHT  || strategyMode == ARCHON_GUARD){
                if(turn - lastSwitchedStrat > 100){
                    strategyMode = HUNT;
                    lastSwitchedStrat = turn;
                }
            }

        }


        if(strategyMode == HUNT) {
            sootforcer.Test.log("hunting");

            sootforcer.Test.debug_halo(256,210,50,1);


            if (mainTarget != null && treeChasingDesire < 5) {
                sootforcer.M.addVector(mainTarget, 3);
            }
            sootforcer.M.addVector(getRandomGoal(),1);

            if(stuckRating > 5 && treeSnipingSpotShootHere == null){
                strategyMode = SCOUT;
                sootforcer.Test.log("Switching to scout mode because were standing still");
            }
        }
        else if(strategyMode == SCOUT){
            sootforcer.Test.log("scouting");

            sootforcer.Test.debug_halo(190,170,250,1);

            sootforcer.M.addVector(getRandomGoal(),7);
        }else if(strategyMode == HIDE){
            sootforcer.Test.log("hiding");
            sootforcer.M.addVector(getRandomGoal(),2);
            sootforcer.M.addVector(mapCenter,-2);

            sootforcer.Test.debug_halo(170,170,170,1);


            if(mainTarget != null){
                MonsterMove.addVectorCircle(mainTarget,12,-10,-5);
                sootforcer.M.addVector(mainTarget,-1);
            }

        } else if(strategyMode == DOGFIGHT){
            sootforcer.Test.log("dogfighting");
            sootforcer.Test.debug_halo(255,40,140,1);


        } else if(strategyMode == MACRO_EMERGENCY){

            sootforcer.Test.debug_halo(255,90,240,1);


            sootforcer.M.addVector(getRandomGoal(),2);


            if(mainTarget != null) {
                sootforcer.M.addVector(mainTarget, 2);
                sootforcer.M.addVectorCircleZone(mainTarget, 8,10,1);
            }
            MapLocation archon1 = getMyArchon1Loc();

            if(archon1 != null){
                sootforcer.M.addVector(archon1, 2);
                sootforcer.M.addVectorCircleZone(archon1, 8,10,1);

            }

            sootforcer.Test.log("emergency");
        } else if(strategyMode == ARCHON_GUARD){
            sootforcer.Test.debug_halo(10,5,30,1);

            sootforcer.Test.log("archon guard");

        } else if(strategyMode == TANK_BUDDY){
            sootforcer.Test.debug_halo(255,180,200,1);

            sootforcer.Test.log("tank buddy");
        }
    }



    public boolean shouldBecomeCommander() throws Exception {
        if(turn - rc.readBroadcast(COMMANDER_TURN_REPORTED) > 6){
            return true;
        }else{
            return false;
        }
    }



    public void dogfightlogic() throws Exception{
        forceFireAfterMove = false;
        forceFireBeforeMove = false;

        if(dogfightEnemy  == null){

            if(robotsEnemy.length == 0 ) {

                if (rc.getHealth() > 6) {

                    boolean found = false;

                    MapLocation[] eScouts = scoutList.getAll(10);

                    for(int i = eScouts.length - 1; i >= 0; i--){
                        if(myLocation.isWithinDistance(eScouts[i],20) || (strategyMode == DOGFIGHT && myLocation.isWithinDistance(eScouts[i],30))){
                            dogfightEnemy = eScouts[i];
                            strategyMode = DOGFIGHT;
                            lastSwitchedStrat = turn;

                            //rc.setIndicatorLine(myLocation,myLocation.add(Direction.NORTH),0,0,0);
                            found = true;
                            break;
                        }
                    }


//                    int size =  R.rc.readBroadcast(LIST_ENEMY_SCOUTS_LOCS_START);
//                    for(int i = 0 ; i < size; i+=2){
//                        MapLocation m = new MapLocation(R.rc.readBroadcastFloat(LIST_ENEMY_SCOUTS_LOCS_START + i + 1), R.rc.readBroadcastFloat(LIST_ENEMY_SCOUTS_LOCS_START + i + 2));
//
//                        if(myLocation.isWithinDistance(m,20) || (strategyMode == DOGFIGHT && myLocation.isWithinDistance(m,30))){
//                            dogfightEnemy = m;
//                            strategyMode = DOGFIGHT;
//                            lastSwitchedStrat = turn;
//
//                            //rc.setIndicatorLine(myLocation,myLocation.add(Direction.NORTH),0,0,0);
//                            found = true;
//                            break;
//                        }
//                    }

                    if(strategyMode == DOGFIGHT && !found){
                        strategyMode = HUNT;
                        lastSwitchedStrat = turn;
                    }
                }
                else if(strategyMode == DOGFIGHT){
                    strategyMode = HIDE;
                    lastSwitchedStrat = turn;
                }
            } else if(strategyMode == DOGFIGHT){
                strategyMode = HUNT;
                lastSwitchedStrat = turn;
            }
        }

        if(dogfightEnemy != null){
            //rc.setIndicatorLine(myLocation,dogfightEnemy,255,0,0);
            if(strategyMode != DOGFIGHT) {
                strategyMode = DOGFIGHT;
                lastSwitchedStrat = turn;
            }

            Direction dir = sootforcer.R.myLocation.directionTo(dogfightEnemy);
                if (sootforcer.R.myLocation.isWithinDistance(dogfightEnemy, sootforcer.C.SCOUT_POINT_BLANK)) {
                    //Point blank. Shoot, then retreat
                    sootforcer.M.addVector(dogfightEnemy, -4);
                    sootforcer.M.addSpecialMapLocation(sootforcer.R.myLocation.subtract(dir, maxMove), 0);
                    forceFireBeforeMove = true;
                } else {
                    //Too far, move in range
                    sootforcer.M.addVector(dogfightEnemy, 4);

                    sootforcer.M.addSpecialMapLocation(dogfightEnemy.subtract(dir, sootforcer.C.SCOUT_POINT_BLANK), 10);
                    sootforcer.M.addSpecialMapLocationUnchecked(sootforcer.R.myLocation.add(dir, maxMove), 0);
                    sootforcer.M.addCircleZone(dogfightEnemy, sootforcer.C.SCOUT_POINT_BLANK, 30);

                    if (sootforcer.R.myLocation.isWithinDistance(dogfightEnemy, maxMove + sootforcer.C.SCOUT_POINT_BLANK)) {
                        forceFireAfterMove = true;
                    }
                }


        }
    }


    public void fire(boolean requireLastTarget, boolean addAvoidCircles) throws GameActionException {
        if (rc.canFireSingleShot()) {


            if(treeSnipingSpotStandHere != null ){
                if(myLocation.isWithinDistance(treeSnipingSpotStandHere,0.02f)) {
                    stuckRating = 0;
                    if (myLocation.isWithinDistance(snipeForGardener, 2.3f)) {
                        if (rc.canFirePentadShot()) {
                            rc.fireSingleShot(myLocation.directionTo(treeSnipingSpotShootHere));
                        } else if (rc.canFireTriadShot()) {
                            rc.fireSingleShot(myLocation.directionTo(treeSnipingSpotShootHere));
                        }
                    }
                    rc.fireSingleShot(myLocation.directionTo(treeSnipingSpotShootHere));
                    return;
                }

            }

            float bestScore = 0;

            RobotInfo bestTarget = null;
            int friendlies = robotsAlly.length;
            int enemies = robotsEnemy.length;
            if(enemies > 0) {


                int lastId;
                if (lastBestTarget != null) {
                    lastId = lastBestTarget.ID;
                } else {
                    lastId = -1;
                }

                for (int i = 0; i < enemies; i++) {
                    RobotInfo r = robotsEnemy[i];
                    float curScore;

                    if (lastId == r.ID) {
                        curScore = 3;
                    } else {
                        curScore = 0;
                    }


                    MapLocation loc = r.location;
                    float dist = myLocation.distanceTo(loc);

                    if(dist < sootforcer.C.SCOUT_POINT_BLANK){
                        curScore += 5;
                    }

                    if(dist > 7 && r.type != RobotType.TANK && r.type != RobotType.ARCHON) continue;

                    boolean emergencyMode = strategyMode == MACRO_EMERGENCY;

                    switch (r.type) {
                        case SOLDIER:

                            if(dist > 6 && !emergencyMode) continue;
                            if(dist > 4 && R.treeCount < 5 && R.bulletCount < 400 && !emergencyMode ) continue;

                            curScore = (8f - dist) * 2 + 3;

                            if (r.health < 10) {
                                curScore += 3;
                            }
                            break;
                        case GARDENER:
                            curScore = 4;
                            if (dist < 4 || emergencyMode) {
                                curScore += 7;
                                if(r.getHealth() <= 2 &&  dist < radius * 2 + GameConstants.BULLET_SPAWN_OFFSET){
                                    curScore += 1000; //just finish this thing now, even if we have an enemy scout on our back
                                }
                            }

                            break;
                        case LUMBERJACK:

                            if(dist > 5.5) continue;
                            if(dist > 4 && R.treeCount < 5 && R.bulletCount < 400 && !emergencyMode) continue;

                            curScore = 8f - dist;
                            if (dist < sootforcer.C.LUMBERJACK_CUT_PLUS_SAFETY + radius) {
                                curScore += 4;
                            }
                            curScore /=2;
                            break;
                        case ARCHON:
                            if (turn > 1500 || R.treeCount > 10 || emergencyMode    ) {
                                curScore = (9f - dist) / 3;
                            }


                            break;
                        case SCOUT:

                            if(dist > 4) continue;

                            if(dogfightEnemy != null) {
                                if (loc.isWithinDistance(dogfightEnemy, 0.01f)){
                                    curScore += 10;
                                }
                            }
                            if (R.treeCount > 15) {
                                curScore += 2;
                            }

                            if (dist < 4) {
                                curScore += 3;
                                if(dist < radius * 2 + GameConstants.BULLET_SPAWN_OFFSET){
                                    curScore += 100; //Immediate threat, kill this before anything else. A single turn of not shooting can be life/death
                                }
                            }
                            break;
                        case TANK:
                            if(dist < 13) {
                                curScore = 6;
                            }
                            break;

                    }

                    if (curScore > bestScore) {
                        Direction dir = myLocation.directionTo(loc);

                        if (dist > radius + r.getRadius() + 1f) {
                            float halfway = radius + (((dist - radius) - r.getRadius()) / 2f);
                            //Check whether there's something in the way
                            if (rc.isLocationOccupied(myLocation.add(dir, halfway))) {
                                continue;
                            }
                        }
                        bestScore = curScore;
                        bestTarget = r;
                    }
                }
            }


            if(bestTarget != null){

                if(!rc.hasMoved() && !forceFireBeforeMove && requireLastTarget &&  bestTarget.ID != lastBestTarget.ID ){
                    return;
                }

                Direction dir;
                MapLocation loc = bestTarget.location;
                RobotType type = bestTarget.type;

                if(lastBestTarget != null && Clock.getBytecodesLeft() > 500){
                    dir = getPredictiveDir(loc, type, 1);
                }
                else{
                    dir = myLocation.directionTo(loc);
                }


                float dist = myLocation.distanceTo(loc);


                switch (type) {
                    case SOLDIER:

                        break;
                    case GARDENER:
                        break;
                    case LUMBERJACK:
                        if(dist > 7) return;
                        break;
                    case TANK:
                        break;
                    case ARCHON:
                        break;
                }

                if(dist > type.bodyRadius + radius + GameConstants.BULLET_SPAWN_OFFSET) {

                    MapLocation m1 = loc.subtract(dir, type.bodyRadius + GameConstants.BULLET_SPAWN_OFFSET);
                    MapLocation m2 = loc.subtract(dir, type.bodyRadius  +1 );
                    MapLocation m3 = loc.subtract(dir, type.bodyRadius +2);

                    if(rc.canSenseLocation( m1) && rc.isLocationOccupiedByTree(m1)){
                        return;
                    }
                    if(dist > type.bodyRadius + 1 && rc.canSenseLocation( m2) && rc.isLocationOccupiedByTree(m2)){
                        return;
                    }
                    if(dist > type.bodyRadius + 2 &&rc.canSenseLocation( m3) && rc.isLocationOccupiedByTree(m3)){
                        return;
                    }


                    for (int i = 0; i < friendlies; i++) {

                        if (Clock.getBytecodesLeft() < 100) break;
                        MapLocation friendly = robotsAlly[i].location;

                        if (!friendly.isWithinDistance(myLocation, 5)) continue;
                        float angle = dir.radiansBetween(myLocation.directionTo(friendly));
                        if (Math.abs(angle) > sootforcer.C.DEGREES_90_IN_RADS) {
                            continue;
                        }
                        float distance = myLocation.distanceTo(friendly);
                        float radius = robotsAlly[i].getRadius();
                        if ((float) Math.abs(distance * Math.sin(angle)) <= radius) {
                            return; //friendly fire
                        }
                    }
                }


                if(addAvoidCircles){
                    MonsterMove.addCircle(myLocation.add(dir,radius + GameConstants.BULLET_SPAWN_OFFSET),radius + floatSafety, -500);
                }

                rc.fireSingleShot(dir);
            }
        }

    }


    public void dealWithRobots() throws Exception {

        robotsEnemy = rc.senseNearbyRobots(sightradius, enemy);
        robotsAlly = rc.senseNearbyRobots(sightradius, ally);

        dogfightEnemy = null;
        nearTrees = null;
        int unitcount = Math.min(robotsEnemy.length,10);

        float scaryfactor = 1.5f;
        if(strategyMode == HIDE){
            scaryfactor *= 2;
        }
        else if(strategyMode == HUNT){
            scaryfactor *= 0.6f;
        }
        if(snipeForGardener != null){
            scaryfactor *= 0.8f;
        }
        if(sittingOnTree == 2){
            //partial cover
            scaryfactor *= 0.5f;
        }else {
            scaryfactor *= 0.7f;
        }
        if(bullets.length > 3){
            scaryfactor *= 1.8f;
        }

        //  Test.drawFloat(myLocation.add(2),sittingOnTree);
        //  Test.drawFloat(myLocation.add(4),scaryfactor);

        avoidWhilesnipingCount = 0;
        possibleSnipeTargetsCount = 0;

        for (int i = 0; i < unitcount; i++) {
            RobotInfo r = robotsEnemy[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;
            Direction dir = myLocation.directionTo(loc);
            switch (type) {
                case SOLDIER:
                    spottedEnemySoldier(loc);
                    if(dist <9) {
                        MonsterMove.addVectorDistanceArray(loc,-2 * scaryfactor,9.5f, M.V_EndDropOff);
                        if (dist < 8) {
                            MonsterMove.addVectorDistanceArray(loc,-20*scaryfactor,10f,M.V_ExpoCenter);
                            MonsterMove.addVectorDistanceArray(loc,-6 * scaryfactor,8.5f, M.V_EndDropOff);
                            avoidWhileSniping[avoidWhilesnipingCount++] = loc;
                            if (dist < 6) {
                                if(nearTrees == null){
                                    nearTrees = rc.senseNearbyTrees(2);
                                }
                                MonsterMove.addVectorCircle(loc, 5, -30  * scaryfactor, -15  * scaryfactor);
                                M.addSpecialMapLocationAway(loc);
                            }
                        }
                    }

                    if(strategyMode == MACRO_EMERGENCY){
                        M.addVector(loc,3);
                    }
                    break;
                case GARDENER:
                    spottedEnemyGardener(loc);

                    //Move towards
                    M.addCircleZone(loc, C.POINT_BLANK_SOLDIER_ON_GARDENER, 30);


                    if(dist < C.POINT_BLANK_SOLDIER_ON_GARDENER){
                        MapLocation[] spots = Helper.FindCircleIntersectionPoints(myLocation,maxMove,loc,radius + r.getRadius() + R.floatSafety);
                        if(spots != null){
                            M.addSpecialMapLocation(spots[0],10);
                            M.addSpecialMapLocation(spots[1],10);
                        }
                    }

                    if(robotsEnemy.length <= 3){
                        strategyMode = HUNT;
                        lastSwitchedStrat = turn;
                    }


                    if(dist < 6){
                        possibleSnipeScores[possibleSnipeTargetsCount] = -( dist +  r.health / 4f);
                        possibleSnipeTargets[possibleSnipeTargetsCount++] = loc;
                    }


                    break;
                case LUMBERJACK:
                    MonsterMove.addVectorCircle(loc, C.LUMBERJACK_DANGER,-500,-20  * scaryfactor);
                    MonsterMove.addVectorCircle(loc, C.LUMBERJACK_DANGER_PLUS_SOLDIER_MOVE,-50  * scaryfactor,-3  * scaryfactor);
                    M.noBoostSpecialLocations[M.noBoostLocationsCount++] = myLocation.subtract(dir, maxMove);
                    //M.addVector(loc,3);

                    if(dist < 7){
                        avoidWhileSniping[avoidWhilesnipingCount++] = loc;
                    }
                    break;
                case ARCHON:
                    spottedEnemyArchon(r);


                    if(strategyMode == MACRO_EMERGENCY){
                        M.addVector(loc,3);
                    }
                    break;
                case SCOUT:
                    spottedEnemyScout(loc);

                    if(dogfightEnemy == null){
                        if(strategyMode == DOGFIGHT) {
                            if (rc.getHealth() + 2 >= r.health){
                                dogfightEnemy = loc;
                            }
                        }else{
                            if (rc.getHealth() >= r.health){
                                dogfightEnemy = loc;
                                if (rc.getHealth() >= r.health + 2 && dist < 8) {
                                    strategyMode = DOGFIGHT;
                                    lastSwitchedStrat = turn;
                                }
                            }
                        }
                    }

                    break;
                case TANK:
                    spottedEnemyTank(loc);

                    M.addVectorCircleZone(loc,radius + type.sensorRadius + type.strideRadius + 2, -200,-10  * scaryfactor);
                    M.addVectorCircleZone(loc, type.bodyRadius + sightradius - type.strideRadius, 25, 5);

                    if(dist < 7){
                        avoidWhileSniping[avoidWhilesnipingCount++] = loc;
                    }
                    break;
            }
        }

        float bestscore = Float.NEGATIVE_INFINITY;
        for(int i = 0; i < Math.min(3,possibleSnipeTargetsCount);i++){
            if(treeSnipingSpotStandHere == null || possibleSnipeScores[i] > bestscore ){
                findSnipeSpotGardener(possibleSnipeTargets[i]);
            }
            if(treeSnipingSpotStandHere == null){
                if(stuckRating < 15 && ( strategyMode != SCOUT || myLocation.isWithinDistance(possibleSnipeTargets[i],7))) {
                    M.addVector(possibleSnipeTargets[i], 6 );
                }
                M.noBoostSpecialLocations[M.noBoostLocationsCount++] = myLocation.add(myLocation.directionTo(possibleSnipeTargets[i]), maxMove);
            }
            else {
                bestscore = possibleSnipeScores[i];
                Test.circle(treeSnipingSpotStandHere, 1, R.SCOUTCOLOR);
            }
        }



        friendlyScoutsCount = 0;
        friendlyTanksCount = 0;

        for (int i = 0; i < robotsAlly.length; i++) {
            RobotInfo r = robotsAlly[i];
            MapLocation loc = r.location;
//            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;

            switch (type) {
                case SCOUT:
                    nearFriendlyScouts[friendlyScoutsCount++] = loc;
                    if(loc.isWithinDistance(myLocation,4)) {
                        M.addVectorCircleZone(loc, 3, -20, -2);
                        M.addVector(loc, -1);
                    }
                    break;

                case TANK:
                    nearFriendlyTanks[friendlyTanksCount++] = loc;
                    M.addCircleZone(loc,10,20);
                    M.addVectorCircleZone(loc,5,-10,-2);

                    if(mainTarget != null){
                        M.addVectorCircleZone(loc.add(loc.directionTo(mainTarget),4), 3, -10, -1);
                        M.addVectorCircleZone(loc.add(loc.directionTo(mainTarget),7), 3, -10, -1);

                    }
                    break;

            }
        }


        if(friendlyTanksCount > 0){
            float bestTankScore = -10000;
            MapLocation bestTankToChase = null;

            tankloop:
            for(int i = 0 ; i < friendlyTanksCount; i++){

                float score = -myLocation.distanceTo(nearFriendlyTanks[i]);
                if(lastTankBuddy != null && nearFriendlyTanks[i].isWithinDistance(lastTankBuddy,1)){
                    score += 10;
                }

                if(score > bestTankScore){

                    for(int j = 0 ; j < friendlyScoutsCount; j++) {
                        if(nearFriendlyScouts[j].isWithinDistance(nearFriendlyTanks[i],4f)) continue tankloop;
                    }

                    bestTankScore = score;
                    bestTankToChase = nearFriendlyTanks[i];
                }

            }

            if(bestTankToChase!=null){
                strategyMode = TANK_BUDDY;
                MapLocation chaseSpot;
                MapLocation avoidSpot;
                if(robotsEnemy.length > 0){
                    chaseSpot = bestTankToChase.subtract(bestTankToChase.directionTo(robotsEnemy[0].location).rotateRightDegrees(70), radius + RobotType.TANK.bodyRadius + 0.3f);
                    avoidSpot = bestTankToChase.add(bestTankToChase.directionTo(robotsEnemy[0].location).rotateRightDegrees(70), 5);

                }
                else if(mainTarget != null){
                    chaseSpot = bestTankToChase.subtract(bestTankToChase.directionTo(mainTarget).rotateRightDegrees(70), radius + RobotType.TANK.bodyRadius + 0.3f);
                    avoidSpot = bestTankToChase.add(bestTankToChase.directionTo(mainTarget).rotateRightDegrees(70), 5);

                }else{
                    chaseSpot = bestTankToChase.subtract(bestTankToChase.directionTo(mapCenter).rotateRightDegrees(70), radius + RobotType.TANK.bodyRadius + 0.3f);
                    avoidSpot = bestTankToChase.add(bestTankToChase.directionTo(mapCenter).rotateRightDegrees(70), 5);

                }


                M.addVector(chaseSpot,9);
                Test.lineTo(chaseSpot,255,180,200);
                M.addVectorCircleZone(bestTankToChase,4f,10,2);
                M.addSpecialMapLocation(chaseSpot,5);
                M.addVectorCircleZone(avoidSpot,4f,-10,-3);

                if(chaseSpot.isWithinDistance(myLocation, maxMove)){
                    M.doBugPathingThisTurn = false;
                }

                lastTankBuddy = bestTankToChase;

            }else{
                lastTankBuddy = null;
            }
        }else{
            lastTankBuddy = null;
        }

        if (robotsEnemy.length > 0) {
            canBroadcast = true; //No point in trying to remain silent if they can see us
        }



        MapLocation[] gardeners = gardenerList.getAll(15);
        float count = 0;
        for(int i = gardeners.length-1; i>= 0; i--){
            if(MapLocation.doCirclesCollide(gardeners[i],24,R.myLocation,R.maxMove)) {
                count++;
            }
        }
        if(count > 0) {
            for (int i = gardeners.length - 1; i >= 0; i--) {
                if(MapLocation.doCirclesCollide(gardeners[i],24,R.myLocation,R.maxMove)) {
                    if(strategyMode == HUNT) {
                        M.addVector(gardeners[i], 9 + (10 / count));
                    }else{
                        M.addVector(gardeners[i], 6 + (10 / count));
                    }
                }
                // Test.circle(gardeners[i], 24, R.GARDENERCOLOR);
            }
        }

    }

    public void findSnipeSpotGardener(MapLocation loc) throws Exception{

        if (nearTrees == null) {
            nearTrees = rc.senseNearbyTrees(2);
        }

        int attempts = 0;
        Direction opposite = loc.directionTo(myLocation);
        MapLocation spot;
        float dist = RobotType.GARDENER.bodyRadius + radius + GameConstants.BULLET_SPAWN_OFFSET - floatSafety;

        int maxavoid = Math.min(avoidWhilesnipingCount - 1,3);
        while(true){
            float rotate;
            if(attempts %2==0) {
                rotate = 5f * (attempts / 2);
            }else{
                rotate = -5f * (attempts / 2);
            }

            spot = loc.add(opposite.rotateRightDegrees(rotate),dist);

            if(rc.canSenseLocation(spot) && rc.onTheMap(spot) && (!rc.isCircleOccupiedExceptByThisRobot(spot,radius) || rc.isLocationOccupiedByTree(spot))){

                //avoid scary enemies
                boolean foundone = false;
                for(int i = maxavoid; i >= 0; i--){
                    if(avoidWhileSniping[i].isWithinDistance(spot,5f)){
                        foundone = true;
                        attempts += 2;
                        Test.debug_circle(avoidWhileSniping[i],5,0,0,0, Test.LOG_MODE);
                        break;
                    }
                }
                if(!foundone) {
                    break;
                }
            }

            attempts++;
            if(attempts > 72 || (Clock.getBytecodesLeft() < 7000 && attempts > 4)){
                spot = null;
                break;
            }
        }

        if(spot != null){
            treeSnipingSpotStandHere = spot;
            treeSnipingSpotShootHere = loc;
            snipeForGardener = loc;
        }
    }

    public void dealWithTrees() throws Exception{
        trees = rc.senseNearbyTrees();
        sittingOnTree = 0;
        int extra = 0;

        if(strategyMode == HIDE){
            extra += 10;
        }else if(strategyMode == SCOUT){
            extra += 3;
        }
        int count = Math.min(trees.length,15 + extra);

        treeChasingDesire = -1000;
        totaltreebullets = 0f;

        MapLocation bestTree = null;


        for(int i = 0; i < count; i++){
            TreeInfo tree = trees[i];


            if(tree.containedBullets > 0){
                totaltreebullets += tree.containedBullets;
                if(rc.canShake(tree.getID())){
                    rc.shake(tree.getID());
                }
                else{
                    MapLocation loc = tree.location;


                    float score = (4 + (tree.containedBullets / 2) - myLocation.distanceTo(loc) * 0.5f) * 2f;
                    if(strategyMode == SCOUT) {
                        treeChasingDesire *= 1.5f;
                    }
                    if(score > treeChasingDesire){
                        treeChasingDesire = score;
                        bestTree = loc;
                    }



                }
            }
            if(sittingOnTree <= 1) {
                float dist = myLocation.distanceTo(tree.location);
                if (dist - tree.radius < radius) {
                    if (dist - tree.radius < 0 && sittingOnTree <=1) {
                        sittingOnTree = 1;
                    } else {
                        sittingOnTree = 2;
                    }
                }
            }
        }



        if(bestTree != null){
            M.addSpecialMapLocationTowards(bestTree,5);
            if(R.treeCount < 5){
                treeChasingDesire *= 1.5f;
            } else if(R.treeCount < 10){
                treeChasingDesire *= 1.25f;
            }
            M.addVector(bestTree, Math.min(10,Math.max(5,treeChasingDesire)));
        }
    }

}
