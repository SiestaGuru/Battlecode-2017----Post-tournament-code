package sootforcer;

import battlecode.common.*;
import sootforcer.*;
import sootforcer.C;
import sootforcer.Helper;
import sootforcer.M;
import sootforcer.R;
import sootforcer.Tank;
import sootforcer.Test;

/**
 * Created by Hermen on 21/5/2017.
 */
public class Shooter extends R {


    protected static MapLocation treeSnipingSpotStandHere;
    protected static MapLocation treeSnipingSpotShootHere;
    protected static MapLocation snipeForGardener;


    private static int directionTracker = 0;

    protected static boolean toggleShot;
    private static boolean shotLeftLastTime;

    private static float greed = 0;

    private static int turnLastSnipeSpotUpdate;


    protected static RobotInfo lastBestTarget = null;

    private static final ShotAnalyzer[] shotAnalyzers = new ShotAnalyzer[4];
    private static final boolean DO_SHOT_ANALYSIS = true;
    private static  boolean didShotAnalysisThisTurn = false;


    private static int estimatedHits = 0;
    private static int estimatedMisses = 0;
    private static float totalMultiplier1 = 0;
    private static float multiplierCount1 = 0;
    private static float totalMultiplier2 = 0;
    private static float multiplierCount2 = 0;
    private static float totalMultiplier3 = 0;
    private static float multiplierCount3 = 0;
    private static float totalMultiplier4 = 0;
    private static float multiplierCount4 = 0;

    private static MapLocation cluster1 = null;
    private static int cluster1Size = -1;
    private static MapLocation cluster2 = null;
    private static int cluster2Size = -1;
    private static MapLocation cluster3 = null;
    private static int cluster3Size = -1;
    private static MapLocation cluster4 = null;
    private static int cluster4Size = -1;
    private static MapLocation cluster5 = null;
    private static int cluster5Size = -1;
    private static MapLocation cluster6 = null;
    private static int cluster6Size = -1;
    private static MapLocation cluster7 = null;
    private static int cluster7Size = -1;

    protected static TreeInfo[] nearTrees;

    protected static int strategyMode = FREE_FOR_ALL;
    private static int friendsInMassCombat = 0;
    protected static boolean massCombat;


    protected  static int friendlyGardenersSpotted;


    private static boolean amGuardingTheirArchon1;
    private static boolean amGuardingTheirArchon2;
    private static boolean amGuardingTheirArchon3;
    protected static boolean amIGuardingSomething;


    private  static int lastShotAtSpot;

    private  static float tighten;

    private static int cycleShot = 0;

    private static MapLocation lastPredictiveShotAt = null;

    public void shooterEveryTurn() throws Exception{
        nearTrees = null;
        friendlyGardenersSpotted = 0;

        if(turn - turnLastSnipeSpotUpdate > 12 || sootforcer.R.amIScout) {
            treeSnipingSpotShootHere = null;
            treeSnipingSpotStandHere = null;
            snipeForGardener = null;
        }

        if(semiRandom % 3 == 0){
            friendsInMassCombat = rc.readBroadcast(MASS_COMBAT_LASTTURN);
        }

    }

    public static void readClusters() throws  Exception{
        if(Commander.isCommanderArchon){
            cluster1Size = rc.readBroadcast(CLUSTER_1_SIZE);
            if(cluster1Size > 0){
                cluster1 = new MapLocation(rc.readBroadcastFloat(CLUSTER_1_X),rc.readBroadcastFloat(CLUSTER_1_Y));
                if(strategyMode == DODGE_SNIPE_CLUSTERS) {
                    sootforcer.M.addVectorCircleZone(cluster1, sootforcer.C.CLUSTER_DODGE_RADIUS,5,-15);
                }
            }

            cluster2Size = rc.readBroadcast(CLUSTER_2_SIZE);
            if(cluster2Size > 0){
                cluster2 = new MapLocation(rc.readBroadcastFloat(CLUSTER_2_X),rc.readBroadcastFloat(CLUSTER_2_Y));
                if(strategyMode == DODGE_SNIPE_CLUSTERS) {
                    sootforcer.M.addVectorCircleZone(cluster2, sootforcer.C.CLUSTER_DODGE_RADIUS,5,-15);
                }
            }

            cluster3Size = rc.readBroadcast(CLUSTER_3_SIZE);
            if(cluster3Size > 0){
                cluster3 = new MapLocation(rc.readBroadcastFloat(CLUSTER_3_X),rc.readBroadcastFloat(CLUSTER_3_Y));
                if(strategyMode == DODGE_SNIPE_CLUSTERS) {
                    sootforcer.M.addVectorCircleZone(cluster3, sootforcer.C.CLUSTER_DODGE_RADIUS,5,-15);
                }
            }

            cluster4Size = rc.readBroadcast(CLUSTER_4_SIZE);
            if(cluster4Size > 0){
                cluster4 = new MapLocation(rc.readBroadcastFloat(CLUSTER_4_X),rc.readBroadcastFloat(CLUSTER_4_Y));
                if(strategyMode == DODGE_SNIPE_CLUSTERS) {
                    sootforcer.M.addVectorCircleZone(cluster4, sootforcer.C.CLUSTER_DODGE_RADIUS,5,-15);
                }
            }

            cluster5Size = rc.readBroadcast(CLUSTER_5_SIZE);
            if(cluster5Size > 0){
                cluster5 = new MapLocation(rc.readBroadcastFloat(CLUSTER_5_X),rc.readBroadcastFloat(CLUSTER_5_Y));
                if(strategyMode == DODGE_SNIPE_CLUSTERS) {
                    sootforcer.M.addVectorCircleZone(cluster5, sootforcer.C.CLUSTER_DODGE_RADIUS,5,-15);
                }
            }

            cluster6Size = rc.readBroadcast(CLUSTER_6_SIZE);
            if(cluster6Size > 0){
                cluster6 = new MapLocation(rc.readBroadcastFloat(CLUSTER_6_X),rc.readBroadcastFloat(CLUSTER_6_Y));
                if(strategyMode == DODGE_SNIPE_CLUSTERS) {
                    sootforcer.M.addVectorCircleZone(cluster6, sootforcer.C.CLUSTER_DODGE_RADIUS,5,-15);
                }
            }

            cluster7Size = rc.readBroadcast(CLUSTER_7_SIZE);
            if(cluster7Size > 0){
                cluster7 = new MapLocation(rc.readBroadcastFloat(CLUSTER_7_X),rc.readBroadcastFloat(CLUSTER_7_Y));
                if(strategyMode == DODGE_SNIPE_CLUSTERS) {
                    sootforcer.M.addVectorCircleZone(cluster7, sootforcer.C.CLUSTER_DODGE_RADIUS,5,-15);
                }
            }

        }
        else{
            cluster1Size = -1;
            cluster2Size = -1;
            cluster3Size = -1;
            cluster4Size = -1;
            cluster5Size = -1;
            cluster6Size = -1;
            cluster7Size = -1;
        }
    }



    public static void attemptMaintainLastTarget() throws Exception{

        MapLocation[] check = null;
        MapLocation found = null;
        MapLocation old = lastBestTarget.location;

        float distAlowed = (lastBestTarget.type.strideRadius * 2) + floatSafety;

        switch(lastBestTarget.type){
            case SOLDIER:
                check = soldierList.getAll(20);
                break;
            case GARDENER:
                check = gardenerList.getAll(20);
                break;
            case TANK:
                check = tankList.getAll(20);
                break;
            case ARCHON:
                MapLocation m1 = getTheirArchon1Loc();
                MapLocation m2 = getTheirArchon1Loc();
                MapLocation m3 = getTheirArchon1Loc();

                if(m1 != null && m1.isWithinDistance(old, distAlowed)){
                    found = m1;
                }
                else if(m2 != null && m2.isWithinDistance(old, distAlowed)){
                    found = m2;
                }
                else if(m3 != null && m3.isWithinDistance(old, distAlowed)) {
                    found = m3;

                }
                break;
        }

        if(check != null){
            for(int i = check.length -1; i>= 0; i--){
                if(check[i].isWithinDistance(old,distAlowed)){
                    found = check[i];
                    break;
                }
            }
        }

        if(found == null && Clock.getBytecodesLeft() > 10000){
            MapLocation[] pings = rc.senseBroadcastingRobotLocations();
            for(int i = pings.length -1; i>= 0; i--){
                if(pings[i].isWithinDistance(old,distAlowed)){
                    found = pings[i];
                    break;
                }
            }
        }


        if(found != null && found.isWithinDistance(myLocation, 12)){



            robotsEnemy = new RobotInfo[]{new RobotInfo(lastBestTarget.ID, enemy, lastBestTarget.type,found,lastBestTarget.health,0,0)};
            sootforcer.Test.lineTo(found, 100,20,120);
        }


    }



    public void considerBeingEnemyArchonGuard() throws Exception{

        if(true)return; //Seems to just not be good enough
        if(robotsEnemy.length < 3) {
            boolean goingToGuard = false;
            int estimatedShooters = rc.readBroadcast(ESTIMATED_SHOOTERS_ALIVE);
            MapLocation m1 = null;
            MapLocation m2 = null;
            MapLocation m3 = null;

            if (startArchonCount > 1 && estimatedShooters > 1) {
                if (!(amIScout && robotsEnemy != null && robotsEnemy.length > 3)) {
                    m1 = getTheirArchon1Loc();
                    m2 = getTheirArchon2Loc();
                    m3 = getTheirArchon3Loc();
                    int archons = 0;
                    if (m1 != null) archons++;
                    if (m2 != null) archons++;
                    if (m3 != null) archons++;

                    if (archons > 0 && estimatedShooters > archons) {

                        boolean guard1Available = turn - rc.readBroadcastInt(GUARD_THEIR_1_LAST_REPORTED) < 5;
                        boolean guard2Available = turn - rc.readBroadcastInt(GUARD_THEIR_2_LAST_REPORTED) < 5;
                        boolean guard3Available = turn - rc.readBroadcastInt(GUARD_THEIR_3_LAST_REPORTED) < 5;
                        int guards = 0;
                        if (guard1Available) guards++;
                        if (guard2Available) guards++;
                        if (guard3Available) guards++;

                        if (guards < 3 || amIGuardingSomething) {
                            if (amGuardingTheirArchon1) {
                                if (m1 != null && m1.isWithinDistance(myLocation, 12)) {
                                    goingToGuard = true;
                                } else {
                                    amGuardingTheirArchon1 = false;
                                }
                            } else if (amGuardingTheirArchon2) {
                                if (m2 != null && m2.isWithinDistance(myLocation, 12)) {
                                    goingToGuard = true;
                                } else {
                                    amGuardingTheirArchon2 = false;
                                }
                            } else if (amGuardingTheirArchon3) {
                                if (m3 != null && m3.isWithinDistance(myLocation, 12)) {
                                    goingToGuard = true;
                                } else {
                                    amGuardingTheirArchon3 = false;
                                }
                            }
                            if (!goingToGuard && !guard1Available && m1 != null && m1.isWithinDistance(myLocation, 12)) {
                                goingToGuard = true;
                                amGuardingTheirArchon1 = true;
                            }
                            if (!goingToGuard && !guard2Available && m2 != null && m2.isWithinDistance(myLocation, 12)) {
                                goingToGuard = true;
                                amGuardingTheirArchon2 = true;
                            }
                            if (!goingToGuard && !guard3Available && m3 != null && m3.isWithinDistance(myLocation, 12)) {
                                goingToGuard = true;
                                amGuardingTheirArchon3 = true;
                            }
                        }
                    }


                }
            }

            if (!goingToGuard) {
                amIGuardingSomething = false;
                amGuardingTheirArchon1 = false;
                amGuardingTheirArchon2 = false;
                amGuardingTheirArchon3 = false;
            } else {
                amIGuardingSomething = true;
                if (amGuardingTheirArchon1) {
                    sootforcer.M.addVector(m1, 4);
                    rc.broadcast(GUARD_THEIR_1_LAST_REPORTED, turn);
                    sootforcer.Test.lineTo(m1, 100, 150, 60);
                } else if (amGuardingTheirArchon2) {
                    sootforcer.M.addVector(m2, 4);
                    rc.broadcast(GUARD_THEIR_2_LAST_REPORTED, turn);
                    sootforcer.Test.lineTo(m2, 100, 150, 60);
                } else if (amGuardingTheirArchon2) {
                    sootforcer.M.addVector(m3, 4);
                    rc.broadcast(GUARD_THEIR_3_LAST_REPORTED, turn);
                    sootforcer.Test.lineTo(m3, 100, 150, 60);
                } else {
                    amIGuardingSomething = false; //??
                }

            }
        }

    }

    protected  void fireBeforeMove() throws Exception{
//        if(lastBestTarget != null && myLocation.distanceTo(lastBestTarget.location) + 0.1f < M.bestMove.distanceTo(lastBestTarget.location)){
//            fire(true);
//        }

        if(lastBestTarget != null){
            //These are plausible locations for our bullets
            Direction dir = myLocation.directionTo(lastBestTarget.location);

            float dist = radius + 0.2f;
            float fire = radius + GameConstants.BULLET_SPAWN_OFFSET;

            if(amIScout && !sootforcer.M.bestMove.isWithinDistance( myLocation.add(dir,fire), dist) ){
                fire(true, false);
            }
            else if(!sootforcer.M.bestMove.isWithinDistance( myLocation.add(dir,fire), dist) && !sootforcer.M.bestMove.isWithinDistance( myLocation.add(dir.rotateRightDegrees(GameConstants.PENTAD_SPREAD_DEGREES * 2),fire),dist) && !sootforcer.M.bestMove.isWithinDistance( myLocation.add(dir.rotateRightDegrees(GameConstants.PENTAD_SPREAD_DEGREES),fire),dist) && !sootforcer.M.bestMove.isWithinDistance( myLocation.add(dir.rotateLeftDegrees(GameConstants.PENTAD_SPREAD_DEGREES * 2),fire),dist) && !sootforcer.M.bestMove.isWithinDistance( myLocation.add(dir.rotateLeftDegrees(GameConstants.PENTAD_SPREAD_DEGREES),fire),dist)        ){
                fire(true, false);
            }



        }
    }

    protected void fireAfterMove() throws Exception{

        if(rc.canFireSingleShot()) {
            if(Clock.getBytecodesLeft() > 1500) robotsEnemy = rc.senseNearbyRobots(sightradius,enemy);
            fire(false, false);

        }
        if(rc.canFireSingleShot()) {
            lastPredictiveShotAt = null;
        }
        if(robotsEnemy.length == 0) lastBestTarget = null;

    }


    public void fire(boolean requireLastTarget, boolean addAvoidCircles) throws Exception {
        if (rc.canFireSingleShot()) {
            boolean banGardener = false;

            if (!requireLastTarget && treeSnipingSpotStandHere != null) {
                if (myLocation.isWithinDistance(treeSnipingSpotStandHere, 0.02f)) {
                    if (myLocation.isWithinDistance(snipeForGardener, 2.3f)) {
                        if (rc.canFirePentadShot()) {
                            rc.fireSingleShot(myLocation.directionTo(treeSnipingSpotShootHere));
                        } else if (rc.canFireTriadShot()) {
                            rc.fireSingleShot(myLocation.directionTo(treeSnipingSpotShootHere));
                        }
                    }

                    rc.fireSingleShot(myLocation.directionTo(treeSnipingSpotShootHere));
                    return;
                } else {
                    if (nearTrees != null && nearTrees.length > 0) {
                        banGardener = true;
                    }
                }
            }

            float bestScore = 0;

            RobotInfo bestTarget = null;
            int friendlies = robotsAlly.length;
            int enemies = robotsEnemy.length;


            MapLocation bestCluster = null;
            if (enemies > 0) {
                boolean onlyLast;

                onlyLast = (requireLastTarget && Clock.getBytecodesLeft() < 500);

                int lastId;
                if (lastBestTarget != null) {
                    lastId = lastBestTarget.ID;
                } else {
                    lastId = -1;
                }

                for (int i = 0; i < enemies; i++) {
                    RobotInfo r = robotsEnemy[i];
                    float curScore;

                    if (onlyLast) {
                        if (r.ID == lastId) {
                            bestTarget = r;
                            break;
                        } else {
                            return;
                        }
                    } else if (lastId == r.ID) {
                        curScore = 3;
                    } else {
                        curScore = 0;
                    }


                    MapLocation loc = r.location;
                    float dist = myLocation.distanceTo(loc);

                    switch (r.type) {
                        case SOLDIER:
                            curScore = (8f - dist) * 2 + 3;

                            if (r.health < 10) {
                                curScore += 4;
                            }
                            if(rc.getHealth() < 10){
                                curScore += 2;
                            }
                            break;
                        case GARDENER:
                            if (banGardener) {
//                                //rc.setIndicatorLine(myLocation,new MapLocation(0,0),255,0,0);
                                //rc.setIndicatorDot(myLocation, 100, 100, 100);

                                continue;
                            }
                            curScore = 4;
                            if (dist < 4) {
                                curScore += 6;
                            }

                            break;
                        case LUMBERJACK:
                            curScore = 8f - dist;
                            if (dist < sootforcer.C.LUMBERJACK_CUT_PLUS_SAFETY + radius) {
                                curScore += 4;
                            }
                            break;
                        case ARCHON:
                            if (turn > 300 || R.treeCount > 4 || amITank) {
                                curScore = 1;
                                if(rc.getHealth() < 20 && dist < 4){
                                    curScore += 4;
                                }
                                if(R.treeCount > 30){
                                    curScore += 5;
                                }
                            }
                            break;
                        case SCOUT:

                            if (R.treeCount > 15) {
                                curScore += 2;
                            }

                            if (dist < 4) {
                                curScore += 3;
                            }
                            break;
                        case TANK:
                            curScore = 6;
                            break;

                    }

                    if (curScore > bestScore) {
                        if (sootforcer.Helper.freeFiringShot(myLocation.directionTo(loc), dist - r.getRadius(), 0.3f, false)) {
                            bestScore = curScore;
                            bestTarget = r;
                        }
                    }
                }
            } else {
                if (requireLastTarget) return;
                if (trees.length < 2) {

                    float bestclusterScore = 0;

                    float distanceAllowed = sootforcer.C.CLUSTER_SNIPE_RADIUS + Math.max(6, ((float) R.treeCount) / 15f + R.bulletCount / 150f + robotsAlly.length / 5f);

                    if (cluster1Size > 0) {
                        if (myLocation.isWithinDistance(cluster1, distanceAllowed)) {
                            float curScore = cluster1Size * 2 + myLocation.distanceTo(cluster1);
                            if (curScore > bestclusterScore) {
                                bestclusterScore = curScore;
                                bestCluster = cluster1;
                            }
                        }
                    }
                    if (cluster2Size > 0) {
                        if (myLocation.isWithinDistance(cluster2, distanceAllowed)) {
                            float curScore = cluster2Size * 2 + myLocation.distanceTo(cluster2);
                            if (curScore > bestclusterScore) {
                                bestclusterScore = curScore;
                                bestCluster = cluster2;
                            }
                        }
                    }
                    if (cluster3Size > 0) {
                        if (myLocation.isWithinDistance(cluster3, distanceAllowed)) {
                            float curScore = cluster3Size * 2 + myLocation.distanceTo(cluster3);
                            if (curScore > bestclusterScore) {
                                bestclusterScore = curScore;
                                bestCluster = cluster3;
                            }
                        }
                    }
                    if (cluster4Size > 0) {
                        if (myLocation.isWithinDistance(cluster4, distanceAllowed)) {
                            float curScore = cluster4Size * 2 + myLocation.distanceTo(cluster4);
                            if (curScore > bestclusterScore) {
                                bestclusterScore = curScore;
                                bestCluster = cluster4;
                            }
                        }
                    }
                    if (cluster5Size > 0) {
                        if (myLocation.isWithinDistance(cluster5, distanceAllowed)) {
                            float curScore = cluster5Size * 2 + myLocation.distanceTo(cluster5);
                            if (curScore > bestclusterScore) {
                                bestclusterScore = curScore;
                                bestCluster = cluster5;
                            }
                        }
                    }
                    if (cluster6Size > 0) {
                        if (myLocation.isWithinDistance(cluster6, distanceAllowed)) {
                            float curScore = cluster6Size * 2 + myLocation.distanceTo(cluster6);
                            if (curScore > bestclusterScore) {
                                bestclusterScore = curScore;
                                bestCluster = cluster6;
                            }
                        }
                    }
                    if (cluster7Size > 0) {
                        if (myLocation.isWithinDistance(cluster7, distanceAllowed)) {
                            float curScore = cluster7Size * 2 + myLocation.distanceTo(cluster7);
                            if (curScore > bestclusterScore) {
                                bestclusterScore = curScore;
                                bestCluster = cluster7;
                            }
                        }
                    }
                }


            }

            int shotType;
            boolean pseudoCluster = false;

            if (bestTarget == null && bestCluster == null) {

                if (amITank) bestCluster = Tank.findLongRangeShot();

                if (bestCluster == null) {
                    lastBestTarget = null;
                    directionTracker = 0;
                    sootforcer.Test.log("No target found", sootforcer.Test.Modes.SHOOTING);
                    return;
                } else {
                    pseudoCluster = true;
                }
            }


            if (requireLastTarget && bestTarget.ID != lastBestTarget.ID) {
                sootforcer.Test.log("Not shooting before move - wrong target", sootforcer.Test.Modes.SHOOTING);
                return;
            }

            MapLocation myArchon1 = getMyArchon1Loc();
            MapLocation myArchon2 = getMyArchon2Loc();
            MapLocation myArchon3 = getMyArchon3Loc();
            int archonsAlive = 0;
            if (myArchon1 != null) archonsAlive++;
            if (myArchon2 != null) archonsAlive++;
            if (myArchon3 != null) archonsAlive++;


            greed = 0;

            if (turn < 100) {
                greed -= 4;
            } else if (turn < 250) {
                greed -= 6;
            }else if (turn < 400) {
                greed -= 2;
            }

            if (R.treeCount < 1) {
                greed -= 2;
            } else if (R.treeCount < 5) {
                greed += 1;
            } else if (R.treeCount < 15) {
                greed += 5;
            } else if (R.treeCount < 25) {
                greed += 10;
            } else {
                greed += 14;
            }
            greed += enemies * 4;


            if (amITank) {
                greed += enemies * 4;
            }

            greed += friendsInMassCombat;

            if (massCombat) greed += 3;

            if(mapType == CERTAIN_RUSH) greed += 3;

            if (rc.getHealth() < myType.maxHealth / 3f) {
                greed += 5;
            }
            if (bestTarget != null) {
                if (bestTarget.getHealth() < bestTarget.type.maxHealth / 3f) {
                    greed += 5;
                }
            }

            if (R.bulletCount > 500) {
                greed += 7;
            } else if (R.bulletCount > 100) {
                greed += 4;
            } else if (R.bulletCount > 50) {
                greed += 1;
            } else if (R.bulletCount < 10) {
                greed -= 2;
            }

            greed += friendlyGardenersSpotted * 4;


            if(rc.readBroadcastBoolean(FOCUS_VP)){
                greed -= 6;
            }

            if (archonsAlive == 1) {
                if (myArchon1 != null && myArchon1.isWithinDistance(myLocation, 10)) {
                    greed += 5;
                }
                if (myArchon2 != null && myArchon2.isWithinDistance(myLocation, 10)) {
                    greed += 5;
                }
                if (myArchon3 != null && myArchon3.isWithinDistance(myLocation, 10)) {
                    greed += 5;
                }
            }

            int friendlyGardenersAlive = rc.readBroadcast(GARDENER_TOTAL) - rc.readBroadcast(GARDENER_DEATH_FLAGS);

            boolean conserveMode = false;

            boolean isOurArchonUnderArrest = rc.readBroadcastBoolean(MAIN_ARCHON_UNDER_ARREST);
            if (isOurArchonUnderArrest) {
                greed += 6;
            } else {

                if (friendlyGardenersSpotted == 0 && bullets.length < 4 && R.bulletCount > 60 && R.bulletCount < 110) {

                    //We have no resourc gathering, while we can still build gardeners
                    if (friendlyGardenersAlive == 0 && R.treeCount <= 1 && archonsAlive > 0) {

                        boolean shouldConserve = true;

                        if (archonsAlive == 1) {
                            if (myArchon1 != null && myArchon1.isWithinDistance(myLocation, 15)) {
                                shouldConserve = false;
                            }
                            if (myArchon2 != null && myArchon2.isWithinDistance(myLocation, 15)) {
                                shouldConserve = false;
                            }
                            if (myArchon3 != null && myArchon3.isWithinDistance(myLocation, 15)) {
                                shouldConserve = false;
                            }
                        }

                        if (shouldConserve) {
                            conserveMode = true;
                            greed -= 2;
                            sootforcer.Test.log("Conserving bullets - no gardeners alive", sootforcer.Test.Modes.SHOOTING);
                        }
                    }
                }
            }

            if (!conserveMode && friendlyGardenersAlive <= 1) {
                greed += 4;
            }


            sootforcer.Test.log("Greed: " + greed, sootforcer.Test.Modes.SHOOTING);

            Direction dir;
            if (bestCluster != null) {


                if (strategyMode == DODGE_SNIPE_CLUSTERS) greed += 3;

                if (!amITank && (greed < 8 || !rc.canFirePentadShot()))
                    return; //We only fire pentads at clusters as soldiers, anything else is just not going to hit


                if (pseudoCluster) {
                    dir = myLocation.directionTo(bestCluster);
                    if (!sootforcer.Helper.freeFiringShot(dir, sightradius, 0.3f, false)) return;
                    shotType = 1;


//                        for (int i = 0; i < friendlies; i++) {
//                            MapLocation friendly = robotsAlly[i].location;
//                            float angle = dir.radiansBetween(myLocation.directionTo(friendly));
//                            if (Math.abs(angle) > C.DEGREES_90_IN_RADS) {
//                                continue;
//                            }
//                            float distance = myLocation.distanceTo(friendly);
//                            float radius = robotsAlly[i].getRadius() + 0.8f; //Add quite a bit of safety here
//
//                            if (((float) Math.abs(distance * Math.sin(angle)) <= radius)) {
//                                Test.log("Cant shoot at long range target - friendly", Test.Modes.SHOOTING);
//                                return;
//                            }
//                        }
//                        for (int i = 0; i < trees.length; i++) {
//                            if(trees[i].getTeam().equals(enemy)) continue;;
//
//                            MapLocation tree = trees[i].location;
//                            float angle = dir.radiansBetween(myLocation.directionTo(tree));
//                            if (Math.abs(angle) > C.DEGREES_90_IN_RADS) {
//                                continue;
//                            }
//                            float distance = myLocation.distanceTo(tree);
//                            float radius = trees[i].getRadius();
//                            if (((float) Math.abs(distance * Math.sin(angle)) <= radius)) {
//                                Test.log("Cant shoot at long range target - trees", Test.Modes.SHOOTING);
//                                return;
//                            }
//                        }


                } else {

                    if (conserveMode) return;

                    if (toggleShot) dir = myLocation.directionTo(bestCluster).rotateRightDegrees(2);
                    else dir = myLocation.directionTo(bestCluster).rotateLeftDegrees(2);


                    sootforcer.Test.lineTo(bestCluster, 120, 100, 255, sootforcer.Test.Modes.SHOOTING);


                    if (!sootforcer.Helper.freeFiringShot(dir, sightradius, 1.5f, false)) return;


                    boolean pentaForbidden = !rc.canFirePentadShot() || !myLocation.isWithinDistance(bestCluster, 13); //too much risk of collateral out of vision range

                    if (!pentaForbidden)
                        pentaForbidden = !sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), sightradius, 1.5f, true);
                    if (!pentaForbidden)
                        pentaForbidden = !sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), sightradius, 1.5f, true);

                    if (pentaForbidden) {
                        boolean triadforbidden = !rc.canFireTriadShot() || !sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), sightradius, 1.5f, true);
                        if (!triadforbidden)
                            triadforbidden = !sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), sightradius, 1.5f, true);
                        if (triadforbidden) {

                            if (myLocation.isWithinDistance(bestCluster, 11)) {
                                shotType = 1;
                            } else {
                                return; //eh, no point in shooting only single bullets at clusters like this
                            }
                        } else {
                            shotType = 3;
                        }
                    } else {
                        shotType = 5;
                    }

                    sootforcer.Test.lineTo(bestCluster, 255, 100, 20, sootforcer.Test.Modes.SHOOTING);


//                        boolean triadForbidden = false;
//
//                        for (int i = 0; i < friendlies; i++) {
//                            MapLocation friendly = robotsAlly[i].location;
//                            float angle = dir.radiansBetween(myLocation.directionTo(friendly));
//                            if (Math.abs(angle) > C.DEGREES_90_IN_RADS) {
//                                continue;
//                            }
//                            float distance = myLocation.distanceTo(friendly);
//                            float radius = robotsAlly[i].getRadius() + 2f; //Add quite a bit of safety here
//
//                            if (((float) Math.abs(distance * Math.sin(angle)) <= radius)) {
//                                Test.log("Cant shoot at cluster 1", Test.Modes.SHOOTING);
//                                return;
//                            }
//                            if(!pentaForbidden) {
//                                if (angle >= 0) {
//                                    if (((float) Math.abs(distance * Math.sin(angle + C.ANGLE_FRIENDLYFIRE_PENTA_RAD)) <= radius)) {
//                                        Test.log("Cant shoot penta  at cluster 2", Test.Modes.SHOOTING);
//                                        pentaForbidden = true;
//                                        continue;
//                                    }
//                                    if (((float) Math.abs(distance * Math.sin(angle + C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER)) <= radius)) {
//                                        Test.log("Cant shoot  penta at cluster 3", Test.Modes.SHOOTING);
//                                        pentaForbidden = true;
//                                        continue;
//                                    }
//                                } else {
//                                    if (((float) Math.abs(distance * Math.sin(angle - C.ANGLE_FRIENDLYFIRE_PENTA_RAD)) <= radius)) {
//                                        Test.log("Cant shoot penta  at cluster 4", Test.Modes.SHOOTING);
//                                        pentaForbidden = true;
//                                        continue;
//                                    }
//                                    if (((float) Math.abs(distance * Math.sin(angle - C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER)) <= radius)) {
//                                        Test.log("Cant shoot penta at cluster 5", Test.Modes.SHOOTING);
//                                        pentaForbidden = true;
//                                        continue;
//                                    }
//                                }
//                            }
//                            if(!triadForbidden){
//                                if (angle >= 0) {
//                                    if (((float) Math.abs(distance * Math.sin(angle + C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD)) <= radius)) {
//                                        Test.log("Cant shoot triad at cluster 2", Test.Modes.SHOOTING);
//                                        triadForbidden = true;
//                                        continue;
//                                    }
//                                } else {
//                                    if (((float) Math.abs(distance * Math.sin(angle - C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD)) <= radius)) {
//                                        Test.log("Cant shoot triad at cluster 4", Test.Modes.SHOOTING);
//                                        triadForbidden = true;
//                                        continue;
//                                    }
//                                }
//                            }
//                        }


//                        if(pentaForbidden){
//                            if(triadForbidden) {
//                                shotType = 1;
//                            }else{
//                                shotType = 3;
//                            }
//                        }else {
//                            shotType = 5;
//                        }
                }


            } else {
                greed -= friendlies * 2;

                MapLocation loc = bestTarget.location;
                dir = myLocation.directionTo(loc);
                RobotType type = bestTarget.type;

                if (bestTarget.getHealth() < type.maxHealth / 4f) {
                    greed += 2;
                }


                if (Clock.getBytecodesLeft() < 500) {
                    shotType = 1;
                    if (lastBestTarget != null && Clock.getBytecodesLeft() > 200) {
                        dir = getPredictiveDir(loc, type, 1);
                    }
                    sootforcer.Test.log("Too few bytecodes, go for single shots");

                } else {
                    float dist = myLocation.distanceTo(loc);


                            sootforcer.Test.log("Greed:" + greed, sootforcer.Test.Modes.SHOOTING);


//                    if(greed < 5){
//                        //rc.setIndicatorDot(myLocation,0,200,130);
//                    } else if(greed < 8){
//                        //rc.setIndicatorDot(myLocation,200,210,120);
//                    } else{
//                        //rc.setIndicatorDot(myLocation,240,140,20);
//
//                    }


                    boolean canAndWillingPenta = false;
                    boolean canAndWillingTogglePentaSingle = false;
                    boolean canAndWillingTogglePentaTriad = false;
                    boolean canAndWillingPureTri = false;
                    boolean canAndWillingToggleTri = false;
                    boolean canAndWillingSingle = amITank;

                    if (greed > 0) canAndWillingSingle = true;

                    switch (type) {
                        case SOLDIER:

                            canAndWillingSingle = true;

                            if (greed >= 16) {
                                if(dist < 8.5f) {
                                    if (rc.canFirePentadShot()) {
                                        canAndWillingPenta = true;
                                        canAndWillingPureTri = true;
                                    } else if (rc.canFireTriadShot()) {
                                        canAndWillingPureTri = true;
                                    }
                                }
                            } else if (greed >= 13) {
                                if (dist < 7f) {
                                    if (rc.canFirePentadShot()) {
                                        canAndWillingPenta = true;
                                        canAndWillingPureTri = true;
                                    } else if (rc.canFireTriadShot()) {
                                        canAndWillingPureTri = true;
                                    }
                                }
                            } else if (greed >= 10) {

                                if (dist < 7f) {
                                    if (dist < 5f) {
                                        if (rc.canFirePentadShot()) {
                                            canAndWillingPenta = true;
                                            canAndWillingPureTri = true;
                                        } else if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    } else {
                                        if (rc.canFirePentadShot()) {
                                            canAndWillingTogglePentaTriad = true;
                                            canAndWillingPureTri = true;
                                        } else if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    }
                                }
                            } else if (greed >= 5) {
                                if (dist < 5f) {
                                    if (rc.canFirePentadShot()) {
                                        canAndWillingPenta = true;
                                        canAndWillingPureTri = true;
                                    } else if (rc.canFireTriadShot()) {
                                        canAndWillingPureTri = true;
                                    }
                                } else {
                                    if (rc.canFireTriadShot()) {
                                        canAndWillingToggleTri = true;
                                    }
                                }
                            } else if (greed > 0) {
                                if (dist < radius + type.bodyRadius + 3f) {
                                    if (dist < radius + type.bodyRadius + 2f) {
                                        if (rc.canFirePentadShot()) {
                                            canAndWillingPenta = true;
                                            canAndWillingPureTri = true;
                                        } else if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    } else {
                                        if (rc.canFirePentadShot()) {
                                            canAndWillingTogglePentaTriad = true;
                                            canAndWillingPenta = true;
                                            canAndWillingPureTri = true;
                                        } else if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    }

                                }

                            } else {

                                if (dist < radius + type.bodyRadius + 3f) {
                                    if (dist < radius + type.bodyRadius + 2f) {
                                        if (rc.canFirePentadShot()) {
                                            canAndWillingPenta = true;
                                            canAndWillingPureTri = true;
                                        } else if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    } else {
                                        if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    }

                                }

                            }

                            if (conserveMode) {
                                canAndWillingPenta = false;
                                canAndWillingTogglePentaTriad = false;
                                canAndWillingTogglePentaSingle = false;
                            }

                            break;
                        case GARDENER:
                            if (dist < radius + type.bodyRadius + 2.5f) {
                                if (rc.canFirePentadShot()) {
                                    canAndWillingPenta = true;
                                    canAndWillingPureTri = true;

                                } else if (rc.canFireTriadShot()) {
                                    canAndWillingPureTri = true;
                                }
                            }
                            canAndWillingSingle = true;
                            break;
                        case LUMBERJACK:
                            if (greed > 0 && !conserveMode) {
                                if (dist < radius + type.bodyRadius + 2.5f) {
                                    if (rc.canFirePentadShot()) {
                                        canAndWillingPenta = true;
                                        canAndWillingPureTri = true;

                                    } else if (rc.canFireTriadShot()) {
                                        canAndWillingPureTri = true;
                                    }
                                }
                            }
                            canAndWillingSingle = true;
                            break;
                        case TANK:
                            if (greed > 0) {
                                if (dist < 7) {
                                    if (rc.canFirePentadShot()) {
                                        if (!conserveMode || dist < 6) {
                                            canAndWillingPenta = true;
                                        }
                                        canAndWillingPureTri = true;
                                    } else if (rc.canFireTriadShot()) {
                                        canAndWillingPureTri = true;
                                    }
                                }
                            }
                            canAndWillingSingle = true;

                            break;
                        case ARCHON:
                            if (conserveMode) return;
                            if (turn < 200) {
                                greed -= startArchonCount * 300f / (float) turn;
                            }

                            if (greed > 0 || isOurArchonUnderArrest || amITank) {
                                if (greed > 18) {
                                    if (dist < 7 && rc.canFirePentadShot()) {
                                        canAndWillingPenta = true;
                                    } else if (dist < 7 && rc.canFireTriadShot()) {
                                        canAndWillingPureTri = true;
                                    }
                                    canAndWillingSingle = true;

                                } else if (greed > 10 || amITank  || isOurArchonUnderArrest) {
                                    if (dist < 6 && R.bulletCount > 50) {
                                        if (rc.canFirePentadShot()) {
                                            canAndWillingPenta = true;
                                        } else if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    }
                                    canAndWillingSingle = true;

                                } else if (R.treeCount > 15) {
                                    if (dist < 6) {
                                        if (rc.canFireTriadShot()) {
                                            canAndWillingPureTri = true;
                                        }
                                    }
                                } else if (R.treeCount > 5) {
                                    canAndWillingSingle = true;
                                } else if (turn > 150) {
                                    canAndWillingSingle = true;
                                } else {
                                    canAndWillingSingle = false;
                                }
                            }
                            break;
                    }


                    didShotAnalysisThisTurn = false;

                    float maxRangeCheck = dist - (bestTarget.getRadius() + floatSafety);

                    boolean wereFollowingTarget = lastBestTarget != null && lastBestTarget.getID() == bestTarget.getID();

                    boolean cando5 = canAndWillingPenta || (canAndWillingTogglePentaTriad && !toggleShot) || (canAndWillingTogglePentaSingle && !toggleShot);

                    if (cando5) {
                        if (Clock.getBytecodesLeft() > 500 && wereFollowingTarget) {
                            dir = getPredictiveDir(loc, type, 5);
                        }
                        if (Clock.getBytecodesLeft() > 600) {
                            if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), maxRangeCheck, 0.1f, false)) {
                                if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), maxRangeCheck, 0.1f, true)) {
                                    //A friend on the right side, no friend left
                                    cando5 = false;
                                } else {
                                    if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER), maxRangeCheck, 0.1f, true)) {
                                        cando5 = false;
                                    } else if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER), maxRangeCheck, 0.1f, true)) {
                                        cando5 = false;
                                    }
                                }
                            } else {
                                if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), maxRangeCheck, 0.1f, true)) {
                                    //A friend on the right
                                    cando5 = false;
                                } else if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), maxRangeCheck, 0.1f, true)) {
                                    //A friend left
                                    cando5 = false;
                                } else {
                                    if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER), maxRangeCheck, 0.1f, true)) {
                                        cando5 = false;
                                    } else if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER), maxRangeCheck, 0.1f, true)) {
                                        cando5 = false;
                                    }
                                }
                            }
                        } else if (Clock.getBytecodesLeft() > 300) {
                            if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER), maxRangeCheck, 0.1f, true)) {
                                cando5 = false;
                            } else if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER), maxRangeCheck, 0.1f, true)) {
                                cando5 = false;
                            } else if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), maxRangeCheck, 0.1f, true)) {
                                cando5 = false;
                            } else if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD), maxRangeCheck, 0.1f, true)) {
                                cando5 = false;
                            }
                        }


                        if (!cando5) {
                            sootforcer.Test.log("Forbidding pentads because object in the way");
                        }
                        //if we dont have enough bytecodes, just shoot. we already vetted single shots anyway, so hopefully fine
                    }


                    if (cando5) {
                        shotType = 5;
                    } else {

                        boolean cando3 = canAndWillingPureTri || (canAndWillingTogglePentaTriad && toggleShot) || (canAndWillingToggleTri && !toggleShot);

                        if (cando3) {
                            if (Clock.getBytecodesLeft() > 500 && wereFollowingTarget) {
                                dir = getPredictiveDir(loc, type, 3);
                            }
                            if (Clock.getBytecodesLeft() > 600) {
                                if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), maxRangeCheck, 0.1f, false)) {
                                    if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), maxRangeCheck, 0.1f, true)) {
                                        //A friend on the right side, no friend left
                                        cando3 = false;
                                    }
                                } else {
                                    if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), maxRangeCheck, 0.1f, false)) {
                                        //Either a friend right, or trees in both sides
                                        cando3 = false;
                                    } else if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), maxRangeCheck, 0.1f, true)) {
                                        //A friend left
                                        cando3 = false;
                                    }
                                }
                            } else if (Clock.getBytecodesLeft() > 300) {
                                if (!sootforcer.Helper.freeFiringShot(dir.rotateLeftRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), maxRangeCheck, 0.1f, true)) {
                                    cando3 = false;
                                } else if (!sootforcer.Helper.freeFiringShot(dir.rotateRightRads(sootforcer.C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD), maxRangeCheck, 0.1f, true)) {
                                    cando3 = false;
                                }
                            }
                        }


                        if (cando3) {
                            shotType = 3;
                        } else {

                            if (!cando5) {
                                sootforcer.Test.log("Forbidding triads because object in the way");
                            }

                            boolean cando1 = canAndWillingSingle || (canAndWillingTogglePentaSingle && toggleShot) || (canAndWillingToggleTri && toggleShot);

                            if (cando1) {
                                if (Clock.getBytecodesLeft() > 500 && wereFollowingTarget) {
                                    dir = getPredictiveDir(loc, type, 1);

                                    if (Clock.getBytecodesLeft() > 500) {
                                        if (!sootforcer.Helper.freeFiringShot(dir, maxRangeCheck, 0.1f, true)) {
                                            dir = myLocation.directionTo(loc);
                                        }
                                    }
                                }
                                shotType = 1;

                            } else {
                                sootforcer.Test.log("cant do 0?");
                                shotType = 0;
                            }
                        }
                    }

//
//
//                        boolean alreadycheckedSingle = false;
//
//                        shotTypeDecider:
//                        while (true) {
//                            if (canAndWillingPenta) {
//                                shotType = 5;
//                            } else if (canAndWillingTogglePentaTriad) {
//                                if (toggleShot) {
//                                    shotType = 3;
//                                } else {
//                                    shotType = 5;
//                                }
//                            } else if (canAndWillingTogglePentaSingle) {
//                                if (toggleShot) {
//                                    shotType = 1;
//                                } else {
//                                    shotType = 5;
//                                }
//                            } else if (canAndWillingPureTri) {
//                                shotType = 3;
//                            } else if (canAndWillingToggleTri) {
//                                if (toggleShot) {
//                                    shotType = 3;
//                                } else {
//                                    shotType = 1;
//                                }
//                            } else if (canAndWillingSingle) {
//                                shotType = 1;
//                            } else {
//                                shotType = 0;
//                            }
//
//
//
//
//                            if (shotType == 0 || Clock.getBytecodesLeft() < 400) {
//                                break;
//                            }
//
//                            if (lastBestTarget != null && lastBestTarget.getID() == bestTarget.getID()) {
////                                if (!canAndWillingTogglePentaTriad) {
//                                    dir = getPredictiveDir(loc, type, shotType);
////                                }
////                                else {
////                                    Test.log("pentad stuff");
////
////                                    dir = myLocation.directionTo(loc);
////                                }
//                            }else{
//                                Test.log("no target: " + shotType);
//
//                            }
//
//                            if (Clock.getBytecodesLeft() < 100+ 100 * friendlies) break;
//
//                            if (shotType == 5) {
//                                for (int i = 0; i < friendlies; i++) {
//                                    MapLocation friendly = robotsAlly[i].location;
//
//                                    if (!friendly.isWithinDistance(myLocation, dist)) continue;
//                                    float angle = dir.radiansBetween(myLocation.directionTo(friendly));
//                                    if (Math.abs(angle) > C.DEGREES_90_IN_RADS) {
//                                        continue;
//                                    }
//
//                                    float distance = myLocation.distanceTo(friendly);
//                                    float radius = robotsAlly[i].getRadius();
//
//                                    if (((float) Math.abs(distance * Math.sin(angle)) <= radius)) {
//                                        shotType = 0;
//                                        Test.log("Cant shoot ( 5)");
//                                        break shotTypeDecider;
//                                    }
//
//                                    if (angle >= 0) {
//                                        if (((float) Math.abs(distance * Math.sin(angle + C.ANGLE_FRIENDLYFIRE_PENTA_RAD)) <= radius)) {
//                                            canAndWillingPenta = false;
//                                            canAndWillingTogglePentaSingle = false;
//                                            canAndWillingTogglePentaTriad = false;
//                                            alreadycheckedSingle = true;
//                                            Test.log("Cant penta");
//                                            continue shotTypeDecider;
//                                        }
//                                    } else {
//                                        if (((float) Math.abs(distance * Math.sin(angle - C.ANGLE_FRIENDLYFIRE_PENTA_RAD)) <= radius)) {
//                                            canAndWillingPenta = false;
//                                            canAndWillingTogglePentaSingle = false;
//                                            canAndWillingTogglePentaTriad = false;
//                                            alreadycheckedSingle = true;
//                                            Test.log("Cant penta");
//                                            continue shotTypeDecider;
//                                        }
//                                    }
//                                }
//                                break;
//                            } else if (shotType == 3) {
//                                for (int i = 0; i < friendlies; i++) {
//                                    MapLocation friendly = robotsAlly[i].location;
//
//                                    if (!friendly.isWithinDistance(myLocation, dist)) continue;
//                                    float angle = dir.radiansBetween(myLocation.directionTo(friendly));
//                                    if (Math.abs(angle) > C.DEGREES_90_IN_RADS) {
//                                        continue;
//                                    }
//
//                                    float distance = myLocation.distanceTo(friendly);
//                                    float radius = robotsAlly[i].getRadius();
//
//                                    if (!alreadycheckedSingle && ((float) Math.abs(distance * Math.sin(angle)) <= radius)) {
//                                        shotType = 0;
//                                        Test.log("Cant shoot (3)");
//
//                                        break shotTypeDecider;
//                                    }
//
//                                    if (angle >= 0) {
//                                        if (((float) Math.abs(distance * Math.sin(angle + C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD)) <= radius)) {
//                                            alreadycheckedSingle = true;
//                                            Test.log("Cant triad");
//                                            canAndWillingPureTri = false;
//                                            canAndWillingToggleTri = false;
//                                            continue shotTypeDecider;
//                                        }
//                                    } else {
//                                        if (((float) Math.abs(distance * Math.sin(angle - C.ANGLE_FRIENDLYFIRE_TRIPLE_RAD)) <= radius)) {
//                                            alreadycheckedSingle = true;
//                                            Test.log("Cant triad");
//                                            canAndWillingPureTri = false;
//                                            canAndWillingToggleTri = false;
//                                            continue shotTypeDecider;
//                                        }
//                                    }
//                                }
//                                break;
//                            } else if (shotType == 1 && !alreadycheckedSingle) {
//                                for (int i = 0; i < friendlies; i++) {
//                                    MapLocation friendly = robotsAlly[i].location;
//
//                                    if (!friendly.isWithinDistance(myLocation, dist)) continue;
//                                    float angle = dir.radiansBetween(myLocation.directionTo(friendly));
//                                    if (Math.abs(angle) > C.DEGREES_90_IN_RADS) {
//                                        continue;
//                                    }
//
//                                    float distance = myLocation.distanceTo(friendly);
//                                    float radius = robotsAlly[i].getRadius();
//                                    if ((float) Math.abs(distance * Math.sin(angle)) <= radius) {
//                                        shotType = 0;
//                                        Test.log("Cant shoot (1)");
//
//                                        break shotTypeDecider;
//                                    }
//                                }
//                                break;
//                            }
//                        }

                    //use this for testing single shot aiming capbilities etc
//                        if((shotType == 3 || shotType == 5)&&dist > 4.5f) shotType = 1;

                }

            }


//            shotType = 500;


            if (shotType == 5) {
                rc.firePentadShot(dir);

                if (addAvoidCircles) {
                    MonsterMove.addCircle(myLocation.add(dir, radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                    MonsterMove.addCircle(myLocation.add(dir.rotateRightDegrees(GameConstants.PENTAD_SPREAD_DEGREES), radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                    MonsterMove.addCircle(myLocation.add(dir.rotateRightDegrees(GameConstants.PENTAD_SPREAD_DEGREES * 2), radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                    MonsterMove.addCircle(myLocation.add(dir.rotateLeftDegrees(GameConstants.PENTAD_SPREAD_DEGREES), radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                    MonsterMove.addCircle(myLocation.add(dir.rotateLeftDegrees(GameConstants.PENTAD_SPREAD_DEGREES * 2), radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);

                }

                sootforcer.Test.lineTo(myLocation.add(dir), 255, 180, 180, sootforcer.Test.Modes.SHOOTING);

            } else if (shotType == 3) {
                rc.fireTriadShot(dir);

                if (addAvoidCircles) {
                    MonsterMove.addCircle(myLocation.add(dir, radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                    MonsterMove.addCircle(myLocation.add(dir.rotateRightDegrees(GameConstants.TRIAD_SPREAD_DEGREES), radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                    MonsterMove.addCircle(myLocation.add(dir.rotateLeftDegrees(GameConstants.TRIAD_SPREAD_DEGREES), radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                }

                sootforcer.Test.lineTo(myLocation.add(dir), 180, 180, 255, sootforcer.Test.Modes.SHOOTING);

            } else if (shotType == 1) {
                rc.fireSingleShot(dir);


                if (addAvoidCircles) {
                    MonsterMove.addCircle(myLocation.add(dir, radius + GameConstants.BULLET_SPAWN_OFFSET), radius + floatSafety, -500);
                }
                sootforcer.Test.lineTo(myLocation.add(dir), 180, 255, 180, sootforcer.Test.Modes.SHOOTING);


            }


            toggleShot = !toggleShot;

            if (lastBestTarget == null || bestTarget == null || lastBestTarget.getID() != bestTarget.getID()) {
                directionTracker = 0;
            }
            lastBestTarget = bestTarget;

        }

    }



    public static Direction getPredictiveDir(MapLocation loc, RobotType type, int amountOfShots) throws GameActionException{
        Direction dir = myLocation.directionTo(loc);

        float dist = myLocation.distanceTo(loc);
        float size = type.bodyRadius;
        float speed = type.strideRadius;

        float realDist = dist - (radius + GameConstants.BULLET_SPAWN_OFFSET);
        if(type != RobotType.SCOUT) {
            if (speed + realDist - bulletSpeed <= size *0.7f) {
                //Non-scouts can't dodge a shot at 1 turn distance if fired straight at the middle
                //This checks whether it's possible to get into two turn territory. If not definite hit
                //Moving in a straight path away also just puts them into the same position, so we allow this at slightly shorter ranges

//                //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),200,140,0);
                return dir;
            }
        }else{
            if (realDist - (size +GameConstants.BULLET_SPAWN_OFFSET) <= 0f) {
                //Instant hit
                return dir;
            }
        }



        //1.05 because they can sometimes sneak an extra turn in by moving away. Generally will actually be less
        //Because were neglecting their body size
        int turns = (int) ((realDist / bulletSpeed) + 1.05f);

        if(turns == 1){
            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),255,255,255);
        }else if(turns == 2){
            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),255,0,0);
        } else if(turns == 3){
            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),0,255,0);
        } else if(turns == 4){
            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),0,0,255);
        } else if(turns == 5){
            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),0,0,0);
        } else{
            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),100,100,50);
        }


        float maxMovement = turns * speed;
        float maxEscape =  (maxMovement - size) + floatSafety;

        Direction left= dir.rotateLeftDegrees(90);


        MapLocation mapLeftEscape = loc.add(left,maxEscape);
        MapLocation mapRightEscape = loc.subtract(left,maxEscape);



        if(type != RobotType.SCOUT && rc.canSenseLocation(loc) ) {
            MapLocation leftTree = mapLeftEscape.add(left,size);
            while (rc.canSenseLocation(leftTree) && rc.isLocationOccupiedByTree(leftTree) && Clock.getBytecodesLeft() > 300){
                leftTree = leftTree.subtract(left,size*1.8f);
                mapLeftEscape = mapLeftEscape.subtract(left,size*1.8f);

            }
            MapLocation rightTree = mapRightEscape.subtract(left,size);

            while (rc.canSenseLocation(rightTree) && rc.isLocationOccupiedByTree(rightTree)  && Clock.getBytecodesLeft() > 300){
                mapRightEscape = mapRightEscape.add(left,size*1.8f);
                rightTree = rightTree.add(left,size*1.8f);

            }
        }


        float maxLeftEscapeDist = loc.distanceTo(mapLeftEscape);
        float maxRightEscapeDist = loc.distanceTo(mapRightEscape);



        float angleFloat = myLocation.directionTo(lastBestTarget.location).getAngleDegrees();

//        Test.lineTo(myLocation.add(prevLocation.directionTo(lastBestTarget.location),4),0,0,255);
//        Test.lineTo(myLocation.add(dir,4),150,0,255);
//        Test.dot(prevLocation,150,0,255);
//        Test.dot(lastBestTarget.location,0,150,255);


        float rotation = angleFloat - dir.getAngleDegrees();
        if (rotation > 200) {
            rotation -= 360;
        }
        if (rotation < -200) {
            rotation += 360;
        }
        if (rotation > 1) {
            if (directionTracker <= 0) {
                directionTracker = 1;
            } else {
                directionTracker++;
            }
        } else if(rotation < -1) {
            if (directionTracker >= 0) {
                directionTracker = -1;
            } else {
                directionTracker--;
            }
        }else{
            directionTracker = 0;
        }

//        Test.log("rotation: " + rotation);

//        //rc.setIndicatorLine(mapLeftEscape,mapRightEscape,0,0,0);


        int absTracker = Math.abs(directionTracker);
        greed += absTracker;

        Direction fireAt;


        float maxRotationItCouldveDone;

        if(rotation < 0){
            maxRotationItCouldveDone = angleFloat - myLocation.directionTo(lastBestTarget.location.add(left,maxMove)).getAngleDegrees();
        }else{
            maxRotationItCouldveDone = angleFloat - myLocation.directionTo(lastBestTarget.location.subtract(left,maxMove)).getAngleDegrees();
        }


        float ratio = rotation / maxRotationItCouldveDone;


        boolean isStrafing;

        if(ratio < 0.25f){
            if(absTracker > 4) {
                isStrafing = true;
            }else{
                isStrafing = false;
            }
        }else if(ratio > 0.8f){
            isStrafing = true;
        } else if(ratio < 0.35f){
            if(absTracker > 3) {
                isStrafing = true;
            }else{
                isStrafing = false;
            }
        }else{
            if(absTracker > 2){
                isStrafing = true;
            }else{
                isStrafing = false;
            }
        }


        //positive = enemy coming towards us, negative: hes backpaddling
        int comingTowardsMeasure;

        float dif =  myLocation.distanceTo(loc) - myLocation.distanceTo(lastBestTarget.location);

        if(dif > 0.7f) {
            comingTowardsMeasure = -2;
        }else if(dif > 0.3f){
            comingTowardsMeasure = -1;
        }else if(dif < -0.7){
            comingTowardsMeasure = 2;
        } else if(dif < -0.3){
            comingTowardsMeasure = 1;
        } else{
            comingTowardsMeasure = 0;
        }




        int option = Adjustables.PREDICTIVE_SHOOTING_MODE;

        //Tried several shooting emchanisms over time in 1v 1 soldier
        //Doing a big test, out of these. options 1, 5,6 and 7 are the clear winners
        //with 7 and 5 hitting the highest marks (but small differencE)
        //note: test used a limit on pentad/triad at distance, so its mostly single shot based
        if(option == 1){
            if(directionTracker > 3 && isStrafing){
                tighten = 0.5f;
                fireAt = myLocation.directionTo(mapRightEscape);
            } else if(directionTracker < -3 && isStrafing){
                fireAt = myLocation.directionTo(mapLeftEscape);
                tighten = 0.5f;
            }else {
                if (lastBestTarget == null) {
                    tighten = 0.5f;
                }
                if(isStrafing){
                    tighten += 0.05f;
                }
                else if(directionTracker == 0 || directionTracker == 1 || directionTracker == -1){
                    tighten -= 0.07f;
                }
                else{
                    tighten += 0.05f;
                }

                if(tighten < 0.15f) tighten = 0.15f;
                if(tighten > 1) tighten = 1;

                float score = tighten *  mapLeftEscape.distanceTo(mapRightEscape) /2;
                if(toggleShot){
                    score *= -1;
                }
                fireAt = myLocation.directionTo(   loc.add(mapLeftEscape.directionTo(mapRightEscape), score));


//                Test.lineTo( loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

            }
        }
        else if(option == 2) {
            int shootAtSpot;

            if (lastShotAtSpot == 0) {
                if (directionTracker > 0) {
                    shootAtSpot = 1;
                } else {
                    shootAtSpot = -1;
                }
            } else if (lastShotAtSpot == 1) {
                if (directionTracker > 0) {
                    shootAtSpot = 2;
                } else {
                    shootAtSpot = 0;
                }
            } else if (lastShotAtSpot == 2) {
                if (directionTracker > 0) {
                    shootAtSpot = 3;
                } else {
                    shootAtSpot = 0;
                }
            } else if (lastShotAtSpot == 3) {
                if (directionTracker > 0) {
                    shootAtSpot = 3;
                } else {
                    shootAtSpot = 1;
                }
            } else if (lastShotAtSpot == -1) {
                if (directionTracker < 0) {
                    shootAtSpot = -2;
                } else {
                    shootAtSpot = 1;
                }
            } else if (lastShotAtSpot == -2) {
                if (directionTracker < 0) {
                    shootAtSpot = -3;
                } else {
                    shootAtSpot = -1;
                }
            } else if (lastShotAtSpot == -3) {
                if (directionTracker < 0) {
                    shootAtSpot = -3;
                } else {
                    shootAtSpot = -1;
                }
            } else {
                shootAtSpot = 1;
            }

            if (shootAtSpot < 0) {
                fireAt = myLocation.directionTo(loc.add(left,  ((float)shootAtSpot)/3f  *  (loc.distanceTo(mapRightEscape) /2)));
            } else {
                fireAt = myLocation.directionTo(loc.add(left,  ((float)shootAtSpot)/3f  *  (loc.distanceTo(mapLeftEscape) /2)));
            }

            lastShotAtSpot = shootAtSpot;


        } else if(option == 3){
            boolean weWillShootLeft;
            boolean takeTheFurthestShot = isStrafing;


            //Well assume enemies generally stick within their turn distance
            //Suppose we just fired to the left
            //If we have the enemy at 2 turns distance, it means that following the regular pattern: left/right, left/right
            //The enemy only sees a bullet to the left, so will dodge right
            //If this is incorrect, and theyre actually dodging left, we should fire left

            if(turns <= 2 || turns == 4){
                if(shotLeftLastTime){
                    if(directionTracker == -1){
                        weWillShootLeft = true;
                        takeTheFurthestShot = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = false;
                    }else{
                        weWillShootLeft = false;
                    }
                }else{
                    if(directionTracker == -1) {
                        weWillShootLeft = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = true;
                    }else{
                        weWillShootLeft = false;
                        takeTheFurthestShot = true;
                    }
                }
            }
            //If turns is 3, then assuming the regular pattern, the enemy must see both a bullet going right and left.
            //If we shot left last time, then the right bullet will be closest, and they should dodge left following the regular pattern
            else if(turns == 3 || turns == 5){
                if(shotLeftLastTime){
                    if(directionTracker == -1){
                        weWillShootLeft = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = true;
                    }else{
                        weWillShootLeft = false;
                        takeTheFurthestShot = true;
                    }
                }else{
                    if(directionTracker == -1) {
                        weWillShootLeft = true;
                        takeTheFurthestShot = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = false;
                    }else{
                        weWillShootLeft = false;
                    }
                }
            }else{
                weWillShootLeft = toggleShot;
            }



            if(takeTheFurthestShot){
                if(weWillShootLeft){
                    fireAt = myLocation.directionTo(mapLeftEscape);
                }
                else{
                    fireAt = myLocation.directionTo(mapRightEscape);
                }
            }else{


                if(weWillShootLeft) fireAt = myLocation.directionTo(loc.add(left,size - floatSafety));
                else fireAt =  myLocation.directionTo(loc.subtract(left,    size - floatSafety));
            }

            shotLeftLastTime = weWillShootLeft;


        } else if(option == 4){

            boolean weWillShootLeft;
            boolean takeTheFurthestShot = isStrafing;


            //Well assume enemies generally stick within their turn distance
            //Suppose we just fired to the left
            //If we have the enemy at 2 turns distance, it means that following the regular pattern: left/right, left/right
            //The enemy only sees a bullet to the left, so will dodge right
            //If this is incorrect, and theyre actually dodging left, we should fire left

            if(turns <= 2 || turns == 4){
                if(shotLeftLastTime){
                    if(directionTracker == -1){
                        weWillShootLeft = true;
                        takeTheFurthestShot = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = false;
                    }else{
                        weWillShootLeft = false;
                    }
                }else{
                    if(directionTracker == -1) {
                        weWillShootLeft = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = true;
                    }else{
                        weWillShootLeft = false;
                        takeTheFurthestShot = true;
                    }
                }
            }
            //If turns is 3, then assuming the regular pattern, the enemy must see both a bullet going right and left.
            //If we shot left last time, then the right bullet will be closest, and they should dodge left following the regular pattern
            else if(turns == 3 || turns == 5){
                if(shotLeftLastTime){
                    if(directionTracker == -1){
                        weWillShootLeft = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = true;
                    }else{
                        weWillShootLeft = false;
                        takeTheFurthestShot = true;
                    }
                }else{
                    if(directionTracker == -1) {
                        weWillShootLeft = true;
                        takeTheFurthestShot = true;
                    }
                    else if(directionTracker == 0){
                        weWillShootLeft = false;
                    }else{
                        weWillShootLeft = false;
                    }
                }
            }else{
                weWillShootLeft = toggleShot;
            }



            if(takeTheFurthestShot){
                if(weWillShootLeft){
                    fireAt = myLocation.directionTo(mapLeftEscape);
                }
                else{
                    fireAt = myLocation.directionTo(mapRightEscape);
                }
            }else{
                float range = mapLeftEscape.distanceTo(mapRightEscape);

                if(turns <= 2) {
                    if (toggleShot) {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.1f));
                    } else {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.9f));
                    }
                }
                else if(turns == 3){
                    if (toggleShot) {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.33f));
                    } else {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.67f));
                    }
                }
                else if(turns == 4){
                    if (toggleShot) {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.38f));
                    } else {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.62f));
                    }
                }
                else{
                    if (toggleShot) {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.4f));
                    } else {
                        fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.6f));
                    }
                }
            }

            shotLeftLastTime = weWillShootLeft;
        } else if(option == 5) {
            if (!isStrafing) {
                //Prediction = they're not dodging, or they're trying to do a backwards moving dodge
                //rc.setIndicatorDot(myLocation, 0, 0, 0);

                if (toggleShot)
                    fireAt = myLocation.directionTo(loc.add(dir, turns * speed).subtract(left, speed - floatSafety));
                else fireAt = myLocation.directionTo(loc.add(dir, turns * speed).add(left, speed - floatSafety));


            } else {
                //rc.setIndicatorDot(myLocation, 255, 255, 255);

                //They're doing a side-shuffle dodge style
                if (directionTracker < 0) {
                    //They're going left
                if(toggleShot) fireAt = myLocation.directionTo(mapLeftEscape); //Fire ahead of them
                else fireAt = myLocation.directionTo(loc.add(dir,turns*speed).add(left,speed - floatSafety)); //Fire on the left side of their body


//                    if (toggleShot) fireAt = myLocation.directionTo(mapLeftEscape); //Fire ahead of them
//                    else fireAt = dir; //Fire on the left side of their body

                } else {
                    //They're going right
                if(toggleShot) fireAt = myLocation.directionTo(mapRightEscape); //Fire ahead of them
                else fireAt = myLocation.directionTo(loc.add(dir,turns*speed).subtract(left,speed - floatSafety)); //Fire on the right side of their body

//                    if (toggleShot) fireAt = myLocation.directionTo(mapRightEscape); //Fire ahead of them
//                    else fireAt = dir; //Fire on the right side of their body

                }

            }
        }
        else if(option == 6) {
            if (!isStrafing) {
//            //Prediction = they're not dodging, or they're trying to do a backwards moving dodge
                //rc.setIndicatorDot(myLocation, 0, 0, 0);

            float range = mapLeftEscape.distanceTo(mapRightEscape);



            if(turns <= 2) {
                if (toggleShot) {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.1f));
                } else {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.9f));
                }
            }
            else if(turns == 3){
                if (toggleShot) {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.2f));
                } else {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.8f));
                }
            }
            else if(turns == 4){
                if (toggleShot) {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.35f));
                } else {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.75f));
                }
            }
            else{
                if (toggleShot) {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.4f));
                } else {
                    fireAt = myLocation.directionTo(mapLeftEscape.add(mapLeftEscape.directionTo(mapRightEscape), range * 0.6f));
                }
            }


            } else {
                //rc.setIndicatorDot(myLocation, 255, 255, 255);

                //They're doing a side-shuffle dodge style
                if (directionTracker < 0) {
                    //They're going left
//                if(toggleShot) fireAt = myLocation.directionTo(mapLeftEscape); //Fire ahead of them
//                else fireAt = myLocation.directionTo(loc.add(dir,turns*speed).add(left,speed - floatSafety)); //Fire on the left side of their body


                    if (toggleShot) fireAt = myLocation.directionTo(mapLeftEscape); //Fire ahead of them
                    else fireAt = dir; //Fire on the left side of their body

                } else {
                    //They're going right
//                if(toggleShot) fireAt = myLocation.directionTo(mapRightEscape); //Fire ahead of them
//                else fireAt = myLocation.directionTo(loc.add(dir,turns*speed).add(right,speed - floatSafety)); //Fire on the right side of their body


                    if (toggleShot) fireAt = myLocation.directionTo(mapRightEscape); //Fire ahead of them
                    else fireAt = dir; //Fire on the right side of their body

                }

            }
        }
        else if(option == 7) {  //the original??
            if (!isStrafing) {
                //Prediction = they're not dodging, or they're trying to do a backwards moving dodge
                //rc.setIndicatorDot(myLocation, 0, 0, 0);


                if (toggleShot)
                    fireAt = myLocation.directionTo(loc.add(dir, turns * speed).subtract(left, speed - floatSafety));
                else fireAt = myLocation.directionTo(loc.add(dir, turns * speed).add(left, speed - floatSafety));


            } else {
                //rc.setIndicatorDot(myLocation, 255, 255, 255);

                //They're doing a side-shuffle dodge style
                if (directionTracker < 0) {
                    //They're going left
                    if (toggleShot) fireAt = myLocation.directionTo(mapLeftEscape); //Fire ahead of them
                    else fireAt = dir; //Fire on the left side of their body

                } else {
                    //They're going right
                    if (toggleShot) fireAt = myLocation.directionTo(mapRightEscape); //Fire ahead of them
                    else fireAt = dir; //Fire on the right side of their body

                }

            }
        }
        else if(option == 8){
            if(isStrafing){
                if(absTracker > 2) {
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(mapLeftEscape);
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(mapRightEscape);
                        toggleShot = false;
                    }
                }
                else{
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(loc.add(left, speed * 2 + 0.05f - size));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(loc.subtract(left, speed * 2 + 0.05f - size));
                        toggleShot = false;
                    }
                }
            }
            else {
                if (lastBestTarget == null) {
                    tighten = 0.5f;
                }
                if(directionTracker == 0 || directionTracker == 1 || directionTracker == -1){
                    tighten -= 0.07f;
                }
                else{
                    tighten += 0.010f;
                }

                if(tighten < 0.15f) tighten = 0.15f;
                if(tighten > 1) tighten = 1;


                float score = tighten *  mapLeftEscape.distanceTo(mapRightEscape) /2;
                if(toggleShot){
                    score *= -1;
                }

                if(directionTracker < -3){
                    if(toggleShot){
                        fireAt = myLocation.directionTo(loc);
                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

                    }
                }else if(directionTracker > 3){
                    if(!toggleShot){
                        fireAt = myLocation.directionTo(loc);
                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

                    }
                }else{
                    fireAt = myLocation.directionTo(   loc.add(mapLeftEscape.directionTo(mapRightEscape), score));
                }

                sootforcer.Test.log("tighten: " + tighten);
//                Test.lineTo( loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

            }
        }
        else if(option == 9){
            if(isStrafing){
                if(absTracker > 4) {
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(mapLeftEscape);
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(mapRightEscape);
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,0,0);
                }
                else if(absTracker == 4) {
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,255,0);

                }
                else if(absTracker == 3) {
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(100,255,0);

                }
                else{
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(0,255,200);

                }
            }
            else {
                if (lastBestTarget == null) {
                    tighten = 0.5f;
                }
                if(directionTracker == 0 || directionTracker == 1 || directionTracker == -1){
                    tighten -= 0.07f;
                }
                else{
                    tighten += 0.010f;
                }

                if(tighten < 0.15f) tighten = 0.15f;
                if(tighten > 1) tighten = 1;


                float score = tighten *  mapLeftEscape.distanceTo(mapRightEscape) /2;
                if(toggleShot){
                    score *= -1;
                }

                if(directionTracker < -3){
                    if(toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);

                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, score));
                        sootforcer.Test.dot(0,0,200);


                    }
                }else if(directionTracker > 3){
                    if(!toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);

                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, score));
                        sootforcer.Test.dot(0,0,200);

                    }
                }else{
                    fireAt = myLocation.directionTo(   loc.subtract(left, score));
                    sootforcer.Test.dot(0,0,200);

                }


//                Test.log("tighten: " + tighten);
//                Test.lineTo( loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

            }
        }
        else if(option == 10){
            if(isStrafing){
                if(absTracker > 4) {
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(mapLeftEscape);
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(mapRightEscape);
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,0,0);
                }
                else if(absTracker == 4) {
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,255,0);

                }
                else if(absTracker == 3) {
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(100,255,0);

                }
                else{
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(0,255,200);

                }
            }
            else {
                if (lastBestTarget == null) {
                    tighten = 0.6f;
                }
                if(directionTracker == 0 || directionTracker == 1 || directionTracker == -1){
                    tighten -= 0.07f;
                }
                else{
                    tighten += 0.010f;
                }

                if(tighten < 0.25f) tighten = 0.25f;
                if(tighten > 1) tighten = 1;


                float score = tighten *  mapLeftEscape.distanceTo(mapRightEscape) /2;
                if(toggleShot){
                    score *= -1;
                }

                if(directionTracker < -2){
                    if(toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);

                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, score));
                        sootforcer.Test.dot(0,0,200);


                    }
                }else if(directionTracker > 2){
                    if(!toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);
                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, score));
                        sootforcer.Test.dot(0,0,200);
                    }
                }else{
                    fireAt = myLocation.directionTo(   loc.subtract(left, score));
                    sootforcer.Test.dot(0,0,200);
                }


//                Test.log("tighten: " + tighten);
//                Test.lineTo( loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

            }
        }
        else if(option == 11){
            if(isStrafing){
                if(absTracker > 4) {
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(mapLeftEscape);
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(mapRightEscape);
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,0,0);
                }
                else if(absTracker == 4) {
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,255,0);

                }
                else if(absTracker == 3) {
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(100,255,0);

                }
                else{
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(0,255,200);

                }
            }
            else {
                if (lastBestTarget == null) {
                    tighten = 0.6f;
                }
                if(directionTracker == 0 || directionTracker == 1 || directionTracker == -1){
                    tighten -= 0.07f;
                }
                else{
                    tighten += 0.010f;
                }

                if(tighten < 0.25f) tighten = 0.25f;
                if(tighten > 1) tighten = 1;


                float score = tighten *  mapLeftEscape.distanceTo(mapRightEscape) /2;
                if(toggleShot){
                    score *= -1;
                }

                if(directionTracker < -2){
                    if(toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);

                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, Math.min(maxLeftEscapeDist,score)));
                        sootforcer.Test.dot(0,0,200);


                    }
                }else if(directionTracker > 2){
                    if(!toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);
                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, Math.min(maxLeftEscapeDist,score)));
                        sootforcer.Test.dot(0,0,200);
                    }
                }else{
                    fireAt = myLocation.directionTo(   loc.subtract(left, score));
                    sootforcer.Test.dot(0,0,200);
                }


                sootforcer.Test.log("tighten: " + tighten);
                sootforcer.Test.lineTo( loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

            }
        }else if(option == 12){

            sootforcer.Test.log("ratio: " + ratio);

            if(isStrafing){
                if(absTracker > 4) {
                    tighten = 0.35f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(mapLeftEscape);
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(mapRightEscape);
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,0,0);
                }
                else if(absTracker == 4) {
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 3 + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(255,255,0);

                }
                else if(absTracker == 3) {
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2.3f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(100,255,0);

                }
                else{
                    tighten = 0.5f;
                    if(directionTracker < 0) {
                        fireAt = myLocation.directionTo(  loc.add(left, Math.min(maxLeftEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = true;
                    }else{
                        fireAt = myLocation.directionTo(  loc.subtract(left, Math.min(maxRightEscapeDist, speed * 2f + 0.05f - size)));
                        toggleShot = false;
                    }

                    sootforcer.Test.dot(0,255,200);

                }
            }
            else {
                if (lastBestTarget == null) {
                    tighten = 0.6f;
                }
                if(directionTracker == 0 || directionTracker == 1 || directionTracker == -1){
                    tighten -= 0.07f;
                }
                else{
                    tighten += 0.010f;
                }

                if(tighten < 0.25f) tighten = 0.25f;
                if(tighten > 1) tighten = 1;


                float score = tighten *  mapLeftEscape.distanceTo(mapRightEscape) /2;
                if(toggleShot){
                    score *= -1;
                }

                if(directionTracker < -2){
                    if(toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);

                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, Math.min(maxLeftEscapeDist,score)));
                        sootforcer.Test.dot(0,0,200);


                    }
                }else if(directionTracker > 2){
                    if(!toggleShot){
                        fireAt = myLocation.directionTo(loc);
                        sootforcer.Test.dot(0,0,0);
                    }
                    else{
                        fireAt = myLocation.directionTo(   loc.subtract(left, Math.min(maxLeftEscapeDist,score)));
                        sootforcer.Test.dot(0,0,200);
                    }
                }else{
                    fireAt = myLocation.directionTo(   loc.subtract(left, score));
                    sootforcer.Test.dot(0,0,200);
                }

                sootforcer.Test.log("tighten: " + tighten);
//                Test.lineTo( loc.add(mapLeftEscape.directionTo(mapRightEscape), score));

            }

            MapLocation attemptShotAt = myLocation.add(fireAt,dist);
            MapLocation old = attemptShotAt;
            if(Clock.getBytecodesLeft() > 300 && type == RobotType.SOLDIER && dist > 4 && lastPredictiveShotAt != null && attemptShotAt.isWithinDistance(lastPredictiveShotAt, 1.3f)){
                sootforcer.Test.lineTo(fireAt, 8,200,50,50);

                if(fireAt.degreesBetween(dir) < 0){
                    //Left of the target
                    while(Clock.getBytecodesLeft() > 300 && attemptShotAt.isWithinDistance(lastPredictiveShotAt, 1.3f)){
                        fireAt.rotateRightDegrees(0.5f);
                        attemptShotAt =  myLocation.add(fireAt,dist);
                    }

                }else{
                    while(Clock.getBytecodesLeft() > 300 && attemptShotAt.isWithinDistance(lastPredictiveShotAt, 1.3f)){
                        fireAt.rotateLeftDegrees(0.5f);
                        attemptShotAt =  myLocation.add(fireAt,dist);
                    }

                }
                //rc.setIndicatorLine(old, attemptShotAt, 255,50,50);
                //rc.setIndicatorLine(attemptShotAt, attemptShotAt.add(dir.rotateLeftDegrees(90),0.9f), 255,255,50);


            }
            lastPredictiveShotAt = attemptShotAt;

        }

        else{
            fireAt = myLocation.directionTo(loc);
        }

        return fireAt;

    }


    public void getSnipingSpot(MapLocation loc, Direction dir) throws Exception{
        //  Test.beginClockTestAvg(4);

        if (nearTrees == null) {
            nearTrees = rc.senseNearbyTrees(myLocation.distanceTo(loc)-0.3f);
        }

        if (nearTrees.length == 0) {
            if (rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, maxMove), radius)) {
                treeSnipingSpotStandHere = myLocation;
                treeSnipingSpotShootHere = loc;
                snipeForGardener = loc;

                rc.setIndicatorLine(myLocation,treeSnipingSpotShootHere, 100,0,0);
            } else {
                treeSnipingSpotStandHere = myLocation.add(dir, maxMove);
                treeSnipingSpotShootHere = loc;
                snipeForGardener = loc;

                rc.setIndicatorLine(myLocation,treeSnipingSpotShootHere, 0,200,0);

            }
        } else {
            boolean foundCollision = false;
            for (int i = 0; i < nearTrees.length; i++) {
                if (Helper.lineCollision(myLocation, dir, nearTrees[i].location, nearTrees[i].radius)) {
                    foundCollision = true;
                    calculateSnipeSpots(nearTrees[i], loc, loc);

                    if (treeSnipingSpotStandHere == null) {
                        MapLocation l1 = loc.add(dir.rotateLeftDegrees(90), C.GARDENER_RADIUS /2f);
                        calculateSnipeSpots(nearTrees[i], loc, l1);
                    }
                    if (treeSnipingSpotStandHere == null) {
                        MapLocation l2 = loc.add(dir.rotateRightDegrees(90), C.GARDENER_RADIUS /2f);
                        calculateSnipeSpots(nearTrees[i], loc, l2);
                    }
                    break;
                }
            }
            if (!foundCollision) {
                if (rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, maxMove), radius)) {
                    treeSnipingSpotStandHere = myLocation;
                    treeSnipingSpotShootHere = loc;
                    snipeForGardener = loc;

                    rc.setIndicatorLine(myLocation,treeSnipingSpotShootHere, 0,0,1000);

                } else {
                    treeSnipingSpotStandHere = myLocation.add(dir, maxMove);
                    treeSnipingSpotShootHere = loc;
                    snipeForGardener = loc;

                    rc.setIndicatorLine(myLocation,treeSnipingSpotShootHere, 100,100,0);

                }
            }

            //Rotate around gardener if we cant find a spot
            if(treeSnipingSpotStandHere == null){
                M.addVector(myLocation.add(dir.rotateRightDegrees(90),3),7);
            }
        }
        //Test.endClockTest(4, " gardener snipe spot");
    }

    private void calculateSnipeSpots(TreeInfo tree, MapLocation loc, MapLocation aimspot) throws Exception{

        //Seems we have to find a snipe spot



        Direction dirToTreeRotated = loc.directionTo(tree.location).rotateRightDegrees(60);
        trySnipeSideOfTree(loc.add(dirToTreeRotated, 2), tree, dirToTreeRotated, loc,aimspot,-90);
        if(treeSnipingSpotShootHere == null){
            dirToTreeRotated = dirToTreeRotated.rotateLeftDegrees(120);
            trySnipeSideOfTree(loc.add(dirToTreeRotated, 2), tree, dirToTreeRotated, loc,aimspot,90);
        }
    }
    private void trySnipeSideOfTree(MapLocation secondTreePossibility, TreeInfo tree, Direction dirToTreeRotated, MapLocation gardenerSpot, MapLocation aimSpot, float rotate) throws Exception{
        MapLocation centerTree = tree.location;


        if(!secondTreePossibility.isWithinDistance(myLocation,3)){
            //  Test.log("too far");
            return;
        }

        if (rc.isLocationOccupiedByTree(secondTreePossibility)) {
            //  Test.log("found 2nd tree");
            secondTreePossibility = secondTreePossibility.add(dirToTreeRotated, 0.1f);

            if (rc.isLocationOccupiedByTree(secondTreePossibility)) {
                //      Test.log("found 'third' tree");

                TreeInfo tree2 = rc.senseTreeAtLocation(secondTreePossibility);

                Direction directionBetween = centerTree.directionTo(tree2.location);

                MapLocation trialSpot = centerTree.add(directionBetween, tree.radius + 0.001f);


                rc.setIndicatorLine(tree.location, tree2.location,50,200,50);

                if (!rc.isLocationOccupiedByTree(trialSpot)) {

                    Direction shootAngle = directionBetween.rotateLeftDegrees(rotate);

                    if (Helper.lineCollision(trialSpot, shootAngle, gardenerSpot, C.GARDENER_RADIUS)) {
                        if (!Helper.lineCollision(trialSpot, shootAngle, centerTree, tree.radius)) {
                            if (!Helper.lineCollision(trialSpot, shootAngle, tree2.location, tree2.radius)) {
                                //Yay!
                                determineStandSpot(trialSpot, shootAngle, gardenerSpot);

                                if (treeSnipingSpotShootHere != null) {
                                    rc.setIndicatorLine(treeSnipingSpotStandHere, treeSnipingSpotShootHere, 255, 0, 0);
                                } else{
                                    rc.setIndicatorDot(trialSpot, 255,255,255);

                                }
                            }
                            else{
                                rc.setIndicatorDot(trialSpot, 255,0,0);
                            }
                        }
                        else{
                            rc.setIndicatorDot(trialSpot, 0,255,0);

                        }
                    }
                    else{
                        rc.setIndicatorDot(trialSpot, 0,0,255);
                    }
                }
                else{
                    rc.setIndicatorDot(trialSpot, 0,0,0);

                }


            } else {
                //  Test.log("no 2nd tree");

                Direction shootAngle = secondTreePossibility.directionTo(aimSpot);

                if (Helper.lineCollision(secondTreePossibility, shootAngle, gardenerSpot, C.GARDENER_RADIUS)) {
                    if (!Helper.lineCollision(secondTreePossibility, shootAngle, centerTree, tree.radius)) {
                        //Yay!
                        determineStandSpot(secondTreePossibility, shootAngle, gardenerSpot);

                        if (treeSnipingSpotShootHere != null) {
                            rc.setIndicatorLine(treeSnipingSpotStandHere, treeSnipingSpotShootHere, 0, 255, 0);
                        }
                    }
                }

            }

        } else {
            Direction shootAngle = secondTreePossibility.directionTo(aimSpot);

            if (Helper.lineCollision(secondTreePossibility, shootAngle, gardenerSpot, C.GARDENER_RADIUS)) {
                if (!Helper.lineCollision(secondTreePossibility, shootAngle, centerTree, tree.radius)) {
                    //Yay!
                    determineStandSpot(secondTreePossibility, shootAngle, gardenerSpot);

                    if (treeSnipingSpotShootHere != null) {
                        rc.setIndicatorLine(treeSnipingSpotStandHere, treeSnipingSpotShootHere, 0, 0, 255);
                    }
                }
//                else{
//                    rc.setIndicatorDot(secondTreePossibility, 255,255,0);
//                }
            }
//            else{
//                rc.setIndicatorLine(secondTreePossibility,secondTreePossibility.add(shootAngle,5),255,0,255);

//                rc.setIndicatorDot(spotAttempt, 255,0,255);
//            }
        }
    }
    private void determineStandSpot(MapLocation shootAtSpot, Direction shootAngle, MapLocation gardenerLoc) throws Exception{
        for(float f = 0.1f; f < 3f; f += 0.2f){

            MapLocation spot = shootAtSpot.subtract(shootAngle, f);

            if(spot.isWithinDistance(myLocation, 3)){
                if(!rc.isCircleOccupiedExceptByThisRobot(spot, radius + floatSafety)){
                    if(rc.onTheMap(spot,radius)){

                        treeSnipingSpotStandHere = spot;
                        treeSnipingSpotShootHere = shootAtSpot;
                        turnLastSnipeSpotUpdate = turn;
                        snipeForGardener = gardenerLoc;
                        stuckRating = 0;

                        //  Test.log("found spot at " + spot.toString());
                        return;

                    }
                    else{
                        return;
                    }
                }
            }
            else{
                return;
            }

        }

    }




    //Basically an imaginary bullet we track. Helps us figure out whether our bullets hit, what our angle should've been etc
    public static void analyzeShot(int shotId, MapLocation currentSpot, RobotType type){
        ShotAnalyzer s= shotAnalyzers[shotId];
        MapLocation bulletLoc = s.bulletStartLocation;
        int turnsAgo = turn - s.turnShot;

        float travelDist = turnsAgo * s.bulletSpeed + radius + GameConstants.BULLET_SPAWN_OFFSET;

        float eDist = s.firingLocation.distanceTo(currentSpot);
        MapLocation bulletSpot = s.firingLocation.add(s.actuallyFiredAt,travelDist);


        if(bulletSpot.distanceTo(currentSpot) < type.bodyRadius){
            //A hit
            estimatedHits++;
            shotAnalyzers[shotId] =null;
        }
        else if(travelDist >= eDist){
            if(Helper.lineCollision(s.firingLocation,s.actuallyFiredAt,currentSpot,type.bodyRadius)){
                //A hit
                estimatedHits++;
                shotAnalyzers[shotId] =null;
            }else{
                //A miss
                estimatedMisses++;
                shotAnalyzers[shotId] =null;
            }
        }else{
            return; //Haven't reached the opponent yet
        }

        float finalDir =  s.originalEnemyDir.degreesBetween(s.firingLocation.directionTo(currentSpot));

        if(s.predictedEscapeAngle != 0) {
            float correctMultiplier = finalDir / s.predictedEscapeAngle;

            if(s.directionTracker <= 1) {
                totalMultiplier1+= correctMultiplier;
                multiplierCount1++;
            }else if(s.directionTracker == 2){
                totalMultiplier2+= correctMultiplier;
                multiplierCount2++;
            } else if(s.directionTracker == 3){
                totalMultiplier3+= correctMultiplier;
                multiplierCount3++;
            } else{
                totalMultiplier4+= correctMultiplier;
                multiplierCount4++;
            }

            sootforcer.Test.log("best multi wouldve been: " + correctMultiplier +  " on:" + s.directionTracker);

            Test.log("1: " + (totalMultiplier1/multiplierCount1)   + "      2: " + (totalMultiplier2/multiplierCount2)   +"     3: " + (totalMultiplier3/multiplierCount3)   +"    4: " + (totalMultiplier4/multiplierCount4) );
        }

    }
}
