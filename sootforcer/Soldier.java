package sootforcer;

import battlecode.common.*;
import sootforcer.BulletDodging;
import sootforcer.C;
import sootforcer.Commander;
import sootforcer.DataStructures.BroadcastList;
import sootforcer.Helper;
import sootforcer.M;
import sootforcer.MonsterMove;
import sootforcer.Shooter;
import sootforcer.Test;

/**
 * Created by Hermen on 22/4/2017.
 */
public class Soldier extends Shooter {





    private static MapLocation[] friendlyShooters = new MapLocation[20];
    private static MapLocation[] enemyShooters = new MapLocation[20];
    private static int friendlyShootersCount;
    private static int enemyShootersCount;


    private static boolean rightLeaning;


    private static boolean isOnMacroCircle;
    private static MapLocation macroCircleCenter;
    private static boolean fireIndiscriminately;


    private static int lastInCombat;
    private static MapLocation rescueFriendsAt = null;

    public void step() throws Exception{

        rightLeaning = (semiRandom/20) % 2 == 0;


        Commander.doCommanderIfNecessary();

        shooterEveryTurn();
            BulletDodging.dodgeBulletsTrig();

        doStrategyStuff();

        dealWithRobots();
        dealWithTrees();
        dealWithInvisibleRobots();
        dealWithMacroCircleSoldier();

        stuckCalculations();
        considerBeingEnemyArchonGuard();


        if(treeSnipingSpotStandHere != null){
            sootforcer.M.addVector(treeSnipingSpotStandHere,10);
            sootforcer.M.addSpecialMapLocation(treeSnipingSpotStandHere, 60);

            //rc.setIndicatorLine(myLocation,treeSnipingSpotStandHere, 0,0,0);
        }

        if(canBroadcast) doSomeBroadcasting();

//        Test.beginClockTestAvg(3);
            sootforcer.M.calculateBestMove(3000,false);
//        Test.endClockTestAvg(3, " move cost");

        fireBeforeMove();
        sootforcer.M.doBestMove();
        fireAfterMove();
        fireAtMacro();
        fireAtTrees();

    }


    public void initial() throws Exception{
    }


    public static void doStrategyStuff() throws Exception{

        rescueFriendsAt = null;
        if(bullets.length < 10) {
            MapLocation[] emergencies = emergencyDefenceList.getAll();

            float bestScore = -1000;
            for (int i = emergencies.length - 1; i >=0 ; i--){
                if(emergencies[i].isWithinDistance(myLocation,18 + Math.max(10,turn / 100))){
                    float score = emergencies[i].distanceTo(myLocation);
                    if(score > bestScore){
                        bestScore = score;
                        rescueFriendsAt = emergencies[i];
                    }
                }
            }
        }

        if(rescueFriendsAt == null) {
            if (!amIGuardingSomething) {
                if (mainTarget != null) {
                    sootforcer.M.addVector(mainTarget, 6);
                    sootforcer.M.addVector(getRandomGoal(), 1.5f);

    //            //rc.setIndicatorLine(myLocation,mainTarget,0,0,0);
                } else {
                    sootforcer.M.addVector(mapCenter, -1.5f);
                    sootforcer.M.addVectorCircleZone(mapCenter, (map_right - map_left) / 5f, 0, -1);
                    sootforcer.M.addVector(getRandomGoal(), 2f);
                    addDesireToEdges(14, 8, 0);
                    addDesireToEdges(7, 2, 0);

    //            //rc.setIndicatorLine(myLocation,getRandomGoal(),0,0,0);
                }
            }
        }else{
            sootforcer.M.addVector(rescueFriendsAt, 6);

            sootforcer.M.addVectorCircleZone(rescueFriendsAt, 20,0,4);
            sootforcer.M.addSpecialMapLocationTowards(rescueFriendsAt);
            sootforcer.Test.lineTo(rescueFriendsAt,0,0,255);
            sootforcer.Test.debug_halo(0,0,255,1);
        }


        if(mapSize >= LARGE) {
            strategyMode = DODGE_SNIPE_CLUSTERS;
        }else{
            strategyMode = FREE_FOR_ALL;
        }
        readClusters();

    }


    public static void dealWithMacroCircleSoldier() throws Exception{

        float desire = rc.readBroadcastFloat(MACRO_SPHERE_DESIRE);


        if(desire > 0.5f || desire < -0.5f){

            macroCircleCenter = new MapLocation(rc.readBroadcastFloat(MACRO_SPHERE_X),rc.readBroadcastFloat(MACRO_SPHERE_Y));

            float inner = rc.readBroadcastFloat(MACRO_SPHERE_INNER_SIZE);
            float outer = rc.readBroadcastFloat(MACRO_SPHERE_SIZE);

            float farOut = outer + 5;

            sootforcer.M.addVectorCircleZone(macroCircleCenter, inner, -desire * 2, -80);
            sootforcer.M.addVectorCircleZone(macroCircleCenter, outer, desire, 15);
            sootforcer.M.addVectorCircleZone(macroCircleCenter, farOut, 0, 5);




            if(myLocation.isWithinDistance(macroCircleCenter,outer)){
                isOnMacroCircle = true;
                fireIndiscriminately = rc.readBroadcastBoolean(MACRO_SPHERE_FIRE_INDISCRIMINATELY);

//                if(myLocation.isWithinDistance(macroCircleCenter,inner)) {
//
//                    Test.lineTo(macroCircleCenter, 255, 0, 0);
//                }
//                else{
//                    Test.lineTo(macroCircleCenter, 0, 255, 0);
//
//                }

            }else{
                isOnMacroCircle = false;
                fireIndiscriminately = false;
//                Test.lineTo(macroCircleCenter,0,0,255);

            }




        }
        else{
            macroCircleCenter = null;
            isOnMacroCircle = false;
            fireIndiscriminately = false;
        }



    }



    public static void dealWithInvisibleRobots() throws GameActionException{

        MapLocation[] gardeners = gardenerList.getAll(15);

        for(int i = gardeners.length-1; i>= 0; i--){
            sootforcer.M.addVectorCircleZone(gardeners[i],15,5,5);
        }

        if(robotsEnemy.length == 0 && bullets.length < 5){
            MapLocation[] soldiers = soldierList.getAll(15);

            for(int i = soldiers.length-1; i>= 0; i--){
                sootforcer.M.addVectorCircleZone(soldiers[i],13,5,1.5f);
            }

            MapLocation[] tanks = tankList.getAll(15);

            for(int i = tanks.length-1; i>= 0; i--){
                sootforcer.M.addVectorCircleZone(tanks[i],16,5,1.5f);
            }
        }


    }
    
    public static void doSomeBroadcasting() throws Exception{

        if(Commander.isCommanderArchon) {
            BroadcastList.push(LIST_MY_TROOP_REPORT_HASHES_START, LIST_MY_TROOP_REPORT_HASHES_END, myLocation.hashCode());
        }

        if(massCombat){
            if(rc.readBroadcast(TURN_MASS_COMBAT_REPORTS) == turn){
                rc.broadcast(MASS_COMBAT_REPORTS, rc.readBroadcast(MASS_COMBAT_REPORTS) + 1);
            }
            else{
                rc.broadcast(TURN_MASS_COMBAT_REPORTS,turn);
                rc.broadcast(MASS_COMBAT_LASTTURN, rc.readBroadcast(MASS_COMBAT_REPORTS));
                rc.broadcast(MASS_COMBAT_REPORTS, 1);
            }
        }
    }


    public void dealWithRobots() throws Exception {

        friendlyShootersCount = 0;
        enemyShootersCount = 0;
        int activeFriendlyShooters = 0;


        robotsEnemy = rc.senseNearbyRobots(sightradius, enemy);


        boolean forbidReporting;
        if(robotsEnemy.length == 0 && lastBestTarget != null){
            attemptMaintainLastTarget();
            forbidReporting = true;
        }else{
            forbidReporting = false;
        }

        if(robotsEnemy.length *4 + bullets.length > 15){
            massCombat = true;
//            //rc.setIndicatorDot(myLocation,255,0,0);
        }else{
            massCombat = false;
//            //rc.setIndicatorDot(myLocation,0,0,255);

        }


        robotsAlly = rc.senseNearbyRobots(sightradius, ally);



        for (int i = 0; i < robotsEnemy.length; i++) {
            RobotInfo r = robotsEnemy[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;

            sootforcer.Test.log("movecount: " + r.moveCount  + "  attack: " + r.attackCount);

            Direction dir = myLocation.directionTo(loc);
            switch (type) {
                case SOLDIER:

                    if(!forbidReporting)spottedEnemySoldier(loc);
                    //This tries to keep us at a steady distance of 5.9, which is the distance at which
                    //We can sit exactly between tri-shots
                    sootforcer.M.addVectorCircleZone(loc, 5.9f, 20, -4);

                    sootforcer.M.addVectorCircleZone(loc, 7.2f, 10, -0.5f);


                    //Try strafing
                    if (rightLeaning) {
                        MapLocation right = myLocation.add(dir.rotateRightDegrees(90));
                        sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = right;
                        sootforcer.M.addVector(right, 3);
                    } else {
                        MapLocation left = myLocation.add(dir.rotateLeftDegrees(90));
                        sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = left;
                        sootforcer.M.addVector(left, 3);
                    }

                    if(R.bulletCount < 5){
                        sootforcer.M.addVectorCircleZone(loc, 5f, -20, -25f);
                    }

                    //Try moving directly away
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.subtract(dir, maxMove);


                    enemyShooters[enemyShootersCount] = loc;
                    enemyShootersCount++;


                    lastInCombat = turn;
                    break;
                case GARDENER:
                    if(!forbidReporting)spottedEnemyGardener(loc);

                    //Move towards

                    sootforcer.M.addCircleZone(loc, sootforcer.C.POINT_BLANK_SOLDIER_ON_GARDENER, 50);



                     if(dist < 5 && treeSnipingSpotStandHere == null){
                         if(dist > sootforcer.C.POINT_BLANK_SOLDIER_ON_GARDENER + 1) {
                             if(!rc.isCircleOccupiedExceptByThisRobot(sootforcer.Helper.towards(loc,maxMove),radius) ||  rc.isLocationOccupiedByTree(sootforcer.Helper.towards(loc,myLocation,2f)) ) {
                                 getSnipingSpot(loc, dir);
                             }
                         }
                    }

                    if(treeSnipingSpotStandHere == null){
                        sootforcer.M.addVector(loc, 8);
                        sootforcer.M.addSpecialMapLocationTowardsObject(r);
                    }

                    break;
                case LUMBERJACK:

                    MonsterMove.addVectorCircle(loc, sootforcer.C.LUMBERJACK_DANGER,-500,-20);
                    MonsterMove.addVectorCircle(loc, sootforcer.C.LUMBERJACK_DANGER_PLUS_SOLDIER_MOVE,-50,-2);
                    sootforcer.M.addSpecialMapLocationAway(loc);
                    lastInCombat = turn;

                    break;
                case ARCHON:
                    sootforcer.M.addSpecialMapLocationTowardsObject(r);


                    if(R.treeCount > 5) {
                        //just stand point blank to shoot it asap
                        sootforcer.M.addCircleZone(loc, type.bodyRadius + radius + 0.3f, 20);
                        sootforcer.M.addVector(loc, 4);
                    }
                    else{
                        //stand slightly away from the archon so we can dodge other enemies
                        sootforcer.M.addVectorCircleZone(loc, type.bodyRadius + radius + 0.5f, -20,-4);
                        sootforcer.M.addVector(loc, 2);
                    }


                    if(!forbidReporting)spottedEnemyArchon(r);
                    lastInCombat = turn;
                    break;
                case SCOUT:
                    if(!forbidReporting)spottedEnemyScout(loc);
                    break;
                case TANK:
                    sootforcer.M.addVectorCircleZone(loc,radius + type.sensorRadius + type.strideRadius + 0.2f, -200,-40);
                    sootforcer.M.addVectorCircleZone(loc, type.bodyRadius + sightradius - (type.strideRadius + floatSafety), 200, 15);
                    sootforcer.M.addVector(loc, 10);
                    if(!forbidReporting)spottedEnemyTank(loc);
                    lastInCombat = turn;

                    break;
            }


        }

        for (int i = 0; i < robotsAlly.length; i++) {
            RobotInfo r = robotsAlly[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;

            switch (type) {
                case SOLDIER:
                    sootforcer.M.addVectorCircleZone(loc, sootforcer.C.SOLDIER_DODGE_FRIENDLY_SOLDIERS, -20, -4);

                    friendlyShooters[friendlyShootersCount] = loc;
                    friendlyShootersCount++;

                    for(int j = enemyShootersCount - 1; j >=  0; j--) {
                        if(loc.isWithinDistance(enemyShooters[j], sootforcer.C.SOLDIER_CAN_SEE_SOLDIER)) {
                            activeFriendlyShooters++;
                            break;
                        }
                    }

                    //Try to figure out if the line between enemy and friend goes near us, if so, try dodging the
                    //closest point of this line. This helps us better position in team fights (two soldiers next to each
                    //other instead of behind each other)
                    for(int j = enemyShootersCount - 1; j >=  0; j--) {
                        sootforcer.M.addLineDesireSoft(enemyShooters[j],enemyShooters[j].directionTo(loc),5,-10,-5);

//                        MapLocation e = enemyShooters[j];
//                        float angle = e.directionTo(loc).radiansBetween(e.directionTo(myLocation));
//                        if (Math.abs(angle) < C.DEGREES_90_IN_RADS) {
//                            if(((float) Math.abs(e.distanceTo(myLocation) * Math.sin(angle)) <= 5)){
//                                MapLocation avoidSpot = e.add(e.directionTo(loc),(e.distanceTo(myLocation) * (float)Math.cos(angle)));
//                                Test.lineTo(avoidSpot,255,255,255);
//                                M.addSpecialMapLocationAway(avoidSpot,2);
//                                M.addVectorCircleZone(avoidSpot, 5,-10,-5);
//                            }
//                        }
                    }

                    break;
                case GARDENER:
                    sootforcer.M.addVectorCircleZone(loc, 4, -20, -2);

                    friendlyGardenersSpotted++;
                    break;
                case LUMBERJACK:
                    sootforcer.M.addVectorCircleZone(loc, 4, -20, -2);
                    sootforcer.M.addSpecialMapLocationAway(loc);

                    break;
                case TANK:
                    sootforcer.M.addVectorCircleZone(loc, 6, -20, -3);

                    if(mainTarget != null){
                        sootforcer.M.addVectorCircleZone(loc.add(loc.directionTo(mainTarget),5), 5, -5, -1);
                    }

                    friendlyShooters[friendlyShootersCount] = loc;
                    friendlyShootersCount++;

                    for(int j = enemyShootersCount - 1; j >=  0; j--) {
                        if(loc.isWithinDistance(enemyShooters[j],8)) {
                            activeFriendlyShooters++;
                            break;
                        }
                    }
                    break;


            }
        }

        if (robotsEnemy.length > 0) {
            canBroadcast = true; //No point in trying to remain silent if they can see us
        }


        //This bit is try to get ourselves in position where were double-teaming enemies, while preventing
        //Situations they're double teaming us
        if (enemyShootersCount > 0) {
            if (enemyShootersCount == 1) {
                if (friendlyShootersCount == 0) {
                    //We don't mind duels, and seems to be better to play them aggressively (pushes them into trees etc)
                    sootforcer.M.addVector(enemyShooters[0], 5);
                    ////rc.setIndicatorLine(myLocation, enemyShooters[0], 0,255,0);
                } else {
                    //Might be doing a double team (not confirmed), stick to our buddy and the enemy
                    sootforcer.M.addVector(enemyShooters[0], 4);
                    // //rc.setIndicatorLine(myLocation, enemyShooters[0], 0,255,0);

                    if (myLocation.distanceTo(friendlyShooters[0]) > 5.8f) {
                        //Too far, we may lose contact
                        sootforcer.M.addVector(friendlyShooters[0], 2);
                        //  //rc.setIndicatorLine(myLocation, friendlyShooters[0], 0,0,255);

                    } else {
                        //Too close, we may be hit simulatenously
                        sootforcer.M.addVector(friendlyShooters[0], -2);
                        //  //rc.setIndicatorLine(myLocation, friendlyShooters[0], 200,0,255);

                    }
                }
            } else {
                if (activeFriendlyShooters == 0) {
                    sootforcer.Test.log("double teamed");

                    //We're being double-teamed! Run away
                    for (int i = 0; i < enemyShootersCount; i++) {
                        sootforcer.M.addVector(enemyShooters[i], -5);
                        //  //rc.setIndicatorLine(myLocation, enemyShooters[i], 255,0,0);
                    }
                } else {

                    Test.log("tactically remaneuver");

                    //Seems we're in a team battle of sorts
                    //Try to maneuver so that we end up in a double-teaming situation by running away from all soldiers we don't
                    //have a confirmed double-team on.

                    for (int i = 0; i < enemyShootersCount; i++) {
                        MapLocation eLoc = enemyShooters[i];

                        int friendlyConfirmed = 0;

                        for (int j = 0; j < friendlyShootersCount; j++) {
                            if (friendlyShooters[j].distanceTo(eLoc) < sootforcer.C.SOLDIER_CAN_SEE_SOLDIER) {
                                friendlyConfirmed++;
                            }
                        }
                        if (friendlyConfirmed >= enemyShootersCount) {
                            sootforcer.M.addVector(enemyShooters[i], 5);
                            //  //rc.setIndicatorLine(myLocation, enemyShooters[i], 0,255,0);
                        } else {
                            sootforcer.M.addVector(enemyShooters[i], -5);
                            // //rc.setIndicatorLine(myLocation, enemyShooters[i], 255,0,0);
                        }
                    }

                    if (myLocation.distanceTo(friendlyShooters[0]) > 5.8f) {
                        //Too far, we may lose contact
                        sootforcer.M.addVector(friendlyShooters[0], 2);
                        ////rc.setIndicatorLine(myLocation, friendlyShooters[0], 0,0,255);

                    } else {
                        //Too close, we may be hit simulatenously
                        sootforcer.M.addVector(friendlyShooters[0], -2);
                        // //rc.setIndicatorLine(myLocation, friendlyShooters[0], 200,0,255);

                    }

                }
            }
        }
    }




    public void dealWithTrees() throws Exception{
        trees = rc.senseNearbyTrees(6);
        int count = trees.length;

        boolean alreadyGoingForShakeTree = false;


        boolean avoidTrees;
        if(turn - lastInCombat < 15){
            avoidTrees = true;

            sootforcer.M.addDesireToEdges(3,-40,-8);
        }else{
            avoidTrees = false;
        }

        for(int i = 0; i < count; i++){
            TreeInfo tree = trees[i];
            MapLocation loc = tree.location;
//            float dist = myLocation.distanceTo(loc);

            if(avoidTrees || bullets.length > 3){

                sootforcer.M.addVectorCircleZone(loc, tree.radius + radius + 2f, 0,-6);

//                M.addVector(loc, -1);
//                if(dist < tree.radius + radius + maxMove + 2f) {
//                    M.addCircleZone(loc, tree.radius + radius + 1.2f, -40);
//                }
            }

            if(tree.containedBullets > 0){
                if(rc.canShake(tree.getID())){
                    rc.shake(tree.getID());
                }
                else if(!alreadyGoingForShakeTree){
                    alreadyGoingForShakeTree = true;
                    sootforcer.M.addSpecialMapLocationUnchecked(myLocation.add(myLocation.directionTo(loc),maxMove),2);
                    float treeChasingDesire = (tree.containedBullets / 2) - myLocation.distanceTo(loc);
                    if(R.treeCount < 5){
                        treeChasingDesire *= 1.5f;
                    } else if(R.treeCount < 10){
                        treeChasingDesire *= 1.25f;
                    }
                    M.addVector(loc, Math.min(7,Math.max(2,treeChasingDesire)));

                }
            }

//            if( dist< tree.radius + radius + maxMove ){
//                MapLocation[] locs = Helper.FindCircleIntersectionPoints(myLocation,maxMove,loc,tree.radius + radius);
//                if(locs.length > 0) {
//                    //rc.setIndicatorLine(locs[0], locs[1], 200, 200, 100);
//                }
//            }


        }
    }


    public boolean shouldBecomeCommander() throws Exception {
        if(turn - rc.readBroadcast(COMMANDER_TURN_REPORTED) > 7){
            return true;
        }else{
            return false;
        }
    }

    public static void fireAtMacro() throws  Exception{

        if(rc.canFireSingleShot()){
            if(isOnMacroCircle && fireIndiscriminately){
                Direction dir =myLocation.directionTo(macroCircleCenter);
                int rand = Math.floorMod(Math.addExact( Clock.getBytecodeNum(),Math.addExact( Clock.getBytecodesLeft(),    Math.multiplyExact(Clock.getBytecodeNum(), Math.floorDiv(Math.addExact(Math.multiplyExact(Clock.getBytecodesLeft(), Clock.getBytecodeNum()), Clock.getBytecodeNum()),Clock.getBytecodesLeft())))) , 10);

                dir = dir.rotateRightDegrees(rand - 5);


                if(Clock.getBytecodesLeft() > 400){
                    if(!sootforcer.Helper.freeFiringShot(dir,6,1,true)){
                        return;
                    }


                    if(Clock.getBytecodesLeft() > 400){
                        if(!sootforcer.Helper.freeFiringShot(dir.rotateLeftDegrees(sootforcer.C.ANGLE_FRIENDLYFIRE_PENTA_RAD),4,0,true)){
                            return;
                        }
                    }
                    if(Clock.getBytecodesLeft() > 400){
                        if(!sootforcer.Helper.freeFiringShot(dir.rotateRightDegrees(C.ANGLE_FRIENDLYFIRE_PENTA_RAD),4,0,true)){
                            return;
                        }



                    }
                }

                if(rc.canFirePentadShot()){
                    rc.firePentadShot(dir);
                }else if(rc.canFireTriadShot()){
                    rc.fireTriadShot(dir);
                } else{
                    rc.fireSingleShot(dir);
                }
            }
        }



    }

    public static void fireAtTrees() throws Exception{
        if(Clock.getBytecodesLeft() > 500 && rc.canFireSingleShot() && stuckRating > 40 && (R.treeCount >= 10 ||  (stuckRating > 100 && R.treeCount >= 5)) && R.bulletCount > 400) {
            TreeInfo[] nearTrees = rc.senseNearbyTrees(radius + 1);

            MapLocation bestTree = null;
            float bestRadius = 0;
            float bestScore = -10000;

            for(int i = nearTrees.length - 1; i >= 0; i--){
                TreeInfo t = nearTrees[i];
                if(t.getTeam() != ally){
                    if(rc.getHealth() < 250){
                        float curScore =  -rc.getHealth();

                        if(mainTarget != null) {
                            curScore -= t.location.distanceTo(mainTarget) * 5;
                        }else {
                            curScore -= t.location.distanceTo(mapCenter) * 5;
                        }

                        if(curScore > bestScore){
                            bestTree = t.location;
                            bestScore = curScore;
                            bestRadius = t.radius;
                        }

                    }
                }
            }

            if(bestTree != null){
                if(sootforcer.Helper.freeFiringShot(myLocation.directionTo(bestTree),  myLocation.distanceTo(bestTree) - (bestRadius + floatSafety),0,true )) {
                    rc.fireSingleShot(myLocation.directionTo(bestTree));
                }
            }

        }
        else if(Clock.getBytecodesLeft() > 500 & rc.canFirePentadShot() && R.treeCount > 25){
            TreeInfo[] nearTrees = rc.senseNearbyTrees(4,enemy);

            MapLocation bestTree = null;
            float bestRadius = 0;
            float bestScore = -10000;
            for(int i = nearTrees.length - 1; i >= 0; i--){
                TreeInfo t = nearTrees[i];
                if(t.getTeam() == enemy){
                    float curScore =  -rc.getHealth();
                    if(curScore > bestScore){
                        bestTree = t.location;
                        bestScore = curScore;
                        bestRadius = t.radius;
                    }
                }
            }
            if(bestTree != null){
                if(Helper.freeFiringShot(myLocation.directionTo(bestTree), myLocation.distanceTo(bestTree) - (bestRadius + floatSafety)  ,0,true )) {
                    rc.firePentadShot(myLocation.directionTo(bestTree));
                }
            }
        }

    }

}
