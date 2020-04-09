package sootforcer;

import battlecode.common.*;
import sootforcer.BulletDodging;
import sootforcer.C;
import sootforcer.Commander;
import sootforcer.DataStructures.BroadcastList;
import sootforcer.M;
import sootforcer.R;
import sootforcer.Test;

import static sootforcer.M.bestMove;
import static sootforcer.M.doBugPathingThisTurn;

/**
 * Created by Hermen on 22/4/2017.
 */
public class Lumberjack extends R {


    private static float currentStrikeScore;
    private static float afterMoveStrikeScore;
    private static float afterMoveStrikeScoreTrees;

    private static RobotInfo[] enemyLumberjacks = new RobotInfo[20];
    private static int enemyLumberjackCount;
    private static float bestTreeScore = 0;
    private static TreeInfo bestTree = null;
    private static MapLocation closestArchon;
    private static int cuttableTrees = 0;


    private static MapLocation target = null;
    private static int lastpickedTarget;


    public void step() throws Exception {



        sootforcer.M.addVector(getRandomGoal(),1.5f);

        closestArchon = null;
        if(mapType == EXTREMELY_TIGHT){
            if(turn - rc.readBroadcast(MY_ARCHON_1_LAST_SPOTTED) < 5){
                closestArchon = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_1_X),rc.readBroadcastFloat(MY_ARCHON_1_Y));
            }
            if(turn - rc.readBroadcast(MY_ARCHON_2_LAST_SPOTTED) < 5){
                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_2_X),rc.readBroadcastFloat(MY_ARCHON_2_Y));
                if(closestArchon == null || closestArchon.distanceTo(myLocation) < m1.distanceTo(myLocation)) closestArchon = m1;
            }
            if(turn - rc.readBroadcast(MY_ARCHON_3_LAST_SPOTTED) < 5){
                MapLocation m1 = new MapLocation(rc.readBroadcastFloat(MY_ARCHON_3_X),rc.readBroadcastFloat(MY_ARCHON_3_Y));
                if(closestArchon == null || closestArchon.distanceTo(myLocation) < m1.distanceTo(myLocation)) closestArchon = m1;
            }
        }



        BulletDodging.dodgeBulletsTrig();

        findLumberTargets();
        dealWithRobotsInitial();
        dealWithTreesPre();
        stuckCalculations();
        dealWithMacroCircle();



        if(target != null){

            if(robotsEnemy.length == 0) {
                sootforcer.M.addVectorCircleZone(target, 30, 0, 5);
                sootforcer.M.addVector(target, 2);
                sootforcer.M.addVector(mapCenter, 2);
            }else{
                sootforcer.M.addVectorCircleZone(target, 30, 0, 2);
            }
            sootforcer.Test.lineTo(target, 170, 170, 130);


        }
        else if(mapType == EXTREMELY_TIGHT && cuttableTrees > 0 ){
            if(closestArchon != null) {
                sootforcer.M.addVector(closestArchon, 2);
                sootforcer.M.addVectorCircleZone(closestArchon, 10,0,-1); //Dont want to get too close either
                sootforcer.M.addVectorCircleZone(closestArchon, 12,0,-1); //Dont want to get too close either
                sootforcer.M.addVectorCircleZone(closestArchon, 14,0,-1); //Dont want to get too close either
                sootforcer.M.addVectorCircleZone(closestArchon, 16,0,-1); //Dont want to get too close either
                sootforcer.M.addVectorCircleZone(closestArchon, 20,0,3); //Dont want to get too close either
            }
            if(mapCenter != null) {
                sootforcer.M.addVector(mapCenter, 3);
            }

        }
        else if (mainTarget != null) {
            sootforcer.M.addVector(mainTarget, 4);
        }

        if(canBroadcast && sootforcer.Commander.isCommanderArchon) {
            BroadcastList.push(LIST_MY_TROOP_REPORT_HASHES_START, LIST_MY_TROOP_REPORT_HASHES_END, myLocation.hashCode());
        }

        sootforcer.M.calculateBestMove(1500, false);
        determineStrikeBeforeAfter();
        sootforcer.M.doBestMove();



        if(rc.canStrike()) {
            dealWithTreesPost();

            if(bestTree != null){
                if(afterMoveStrikeScore >= 0 &&  afterMoveStrikeScore + afterMoveStrikeScoreTrees > bestTreeScore){
                    rc.strike();
                    stuckRating = 0;
                    //rc.setIndicatorDot(myLocation,150,0,0);
                }else{
                    rc.chop(bestTree.ID);
                    //rc.setIndicatorDot(myLocation,0,0,150);
                    stuckRating = 0;
                }
            }else{
                if(afterMoveStrikeScore >= 0.8f){
                    rc.strike();
                    stuckRating = 0;
                    //rc.setIndicatorDot(myLocation,255,0,0);
                }
            }


        }



        if(Clock.getBytecodesLeft() > 100 ){
            if(canBroadcast && sootforcer.Commander.isCommanderArchon) {
                BroadcastList.push(LIST_MY_TROOP_REPORT_HASHES_START, LIST_MY_TROOP_REPORT_HASHES_END, myLocation.hashCode());
            }
        }




    }



    public static void findLumberTargets() throws Exception{
        if(target == null ||  (turn - lastpickedTarget > 50 && !target.isWithinDistance(myLocation,3))){
            MapLocation[] possibleTargets = lumberTargetsList.getAll();

            float bestScore = -10000;

            for(int i = possibleTargets.length - 1; i >=0 ;i--){
                float dist = myLocation.distanceTo(possibleTargets[i]);
                if(dist < 25) {
                    float curscore = -dist;

                    if(closestArchon != null) curscore -= closestArchon.distanceTo(myLocation) / 5f;

                    if(curscore > bestScore){
                        target = possibleTargets[i];
                        bestScore = curscore;
                    }
                }


            }
        }else{
           if(target != null){
               if(target.isWithinDistance(myLocation,sightradius - floatSafety)){
                   if(!rc.isLocationOccupiedByTree(target)){
                       target = null;
                       lastpickedTarget = -100;
                   }else{
                       TreeInfo tree = rc.senseTreeAtLocation(target);
                       if(tree.getTeam() == ally){
                           target = null;
                           lastpickedTarget = -100;
                       }
                   }
               }
           }

        }

    }

    public static void determineStrikeBeforeAfter() throws Exception{
        afterMoveStrikeScore = 0;
        for (int i = 0; i < robotsEnemy.length; i++) {
            if(bestMove.isWithinDistance(robotsEnemy[i].location, robotsEnemy[i].getRadius() + GameConstants.LUMBERJACK_STRIKE_RADIUS)) {
                afterMoveStrikeScore++;
            }
        }
        for (int i = 0; i < robotsAlly.length; i++) {
            if(bestMove.isWithinDistance(robotsAlly[i].location, robotsAlly[i].getRadius() + GameConstants.LUMBERJACK_STRIKE_RADIUS)) {
                afterMoveStrikeScore--;
            }
        }


        if(currentStrikeScore >= 1 && currentStrikeScore > afterMoveStrikeScore){
            rc.strike();
            //rc.setIndicatorDot(myLocation,0,255,0);

        }
    }


    public void dealWithRobotsInitial() throws Exception {
        currentStrikeScore = 0;
        enemyLumberjackCount = 0;

        robotsEnemy = rc.senseNearbyRobots(sightradius, enemy);
        robotsAlly = rc.senseNearbyRobots(sightradius, ally);

        for (int i = 0; i < robotsEnemy.length; i++) {
            RobotInfo r = robotsEnemy[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;

            Direction dir = myLocation.directionTo(loc);


            if(dist < GameConstants.LUMBERJACK_STRIKE_RADIUS + r.getRadius()){
                currentStrikeScore++;
            }


            sootforcer.M.addCircleZone(loc, type.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS, 40);

            switch (type) {
                case SOLDIER:
                    spottedEnemySoldier(loc);


                    if(dist < 5f || dist < 7f && r.getHealth() < 10){
                        sootforcer.M.addVectorCircleZone(loc, sootforcer.C.LUMBERJACK_CUT_PLUS_RADIUS_SOLDIER,200,5);
                        sootforcer.M.addCircleZone(loc, radius + type.bodyRadius + GameConstants.BULLET_SPAWN_OFFSET, -1000);
                        sootforcer.M.addVector(loc,8);
                        sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.add(dir, maxMove);
                    }
                    else {
                        //Letting them waste bullets is productive
                        sootforcer.M.addVectorCircleZone(loc, 8f, 20, -10);

                        //Try moving directly away
                        sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.subtract(dir, maxMove);
                    }

                    break;
                case GARDENER:
                    spottedEnemyGardener(loc);

                    //Move towards
                    sootforcer.M.addVector(loc, 8);
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.add(dir, maxMove);
                    break;
                case LUMBERJACK:
                    //Whether we move in or out will depend on whether were going to strike before moving
                    enemyLumberjacks[enemyLumberjackCount++] = r;
                    break;
                case ARCHON:
                    sootforcer.M.addVector(loc, 5); //These are Excellent archon killers
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.add(dir, maxMove);
                    spottedEnemyArchon(r);
                    break;
                case SCOUT:
                    sootforcer.M.addVectorCircleZone(loc, sootforcer.C.LUMBERJACK_CUT_PLUS_RADIUS_SOLDIER,200,5);
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.add(dir, maxMove);
                    sootforcer.M.addSpecialMapLocation(loc.subtract(dir,type.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS - floatSafety),40);

                    if(dist < 6){
                        sootforcer.M.addVector(loc,4);
                    }
                    break;
                case TANK:
                    sootforcer.M.addVectorCircleZone(loc,radius + type.sensorRadius + type.strideRadius, -200,-10);
                    spottedEnemyTank(loc);

                    break;
            }
        }

        for (int i = 0; i < robotsAlly.length; i++) {
            RobotInfo r = robotsAlly[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;


            if(dist < GameConstants.LUMBERJACK_STRIKE_RADIUS + r.getRadius()){
                currentStrikeScore--;
            }


            if(robotsEnemy.length > 0 || bullets.length > 3) {
                sootforcer.M.addCircleZone(loc, sootforcer.C.LUMBERJACK_CUT_PLUS_SAFETY + r.getRadius(), -50);
                sootforcer.M.addVectorCircleZone(loc, sootforcer.C.LUMBERJACK_CUT_PLUS_SAFETY + r.getRadius() + 1.5f, -20,-1 );
            }else{
                sootforcer.M.addCircleZone(loc, C.LUMBERJACK_CUT_PLUS_SAFETY + r.getRadius(), -20);
            }

        }

        if (robotsEnemy.length > 0) {
            canBroadcast = true; //No point in trying to remain silent if they can see us
        }


    }

    public void dealWithTreesPre() throws GameActionException {
        trees = rc.senseNearbyTrees(6);
        int count = trees.length;
        cuttableTrees = 0;

        boolean alreadyGoingForShakeTree = false;

        for(int i = 0; i < count; i++){
            TreeInfo tree = trees[i];
            MapLocation loc = tree.location;
            float dist = myLocation.distanceTo(loc);

            if(tree.containedBullets > 0){
                if(rc.canShake(tree.getID())){
                    rc.shake(tree.getID());
                }
                else if(!alreadyGoingForShakeTree){
                    if(dist < tree.radius + radius + maxMove * 2){
                        if(!rc.isLocationOccupied(myLocation.add(myLocation.directionTo(loc),maxMove))) {
                            sootforcer.M.addVector(loc, 7);
                            alreadyGoingForShakeTree = true;
                        }
                    }
                }
            }


            if(tree.containedRobot != null){
                sootforcer.M.addCircleZone(loc,tree.radius + GameConstants.LUMBERJACK_STRIKE_RADIUS - floatSafety, 40);

                if(dist < GameConstants.LUMBERJACK_STRIKE_RADIUS) doBugPathingThisTurn = false;

                if(tree.containedRobot.equals( RobotType.TANK)){
                    sootforcer.M.addVector(loc,6);

                }else{
                    sootforcer.M.addVector(loc,4);

                }

            }

            if(!tree.getTeam().equals(ally))
            {
                cuttableTrees++;
                sootforcer.M.addCircleZone(loc,tree.radius + GameConstants.LUMBERJACK_STRIKE_RADIUS - floatSafety, 4);

                if(mapType == EXTREMELY_TIGHT){
                    if(closestArchon != null) {
                        float distToArchon = loc.distanceTo(closestArchon);
                        if(distToArchon < 23){
                            if(distToArchon < 5) {
                                sootforcer.M.addVector(loc, 4);
                                sootforcer.M.addCircleZone(loc, tree.radius + GameConstants.LUMBERJACK_STRIKE_RADIUS - floatSafety, 40);
                                sootforcer.Test.log("trees very close to archon");

                                if(dist < GameConstants.LUMBERJACK_STRIKE_RADIUS) doBugPathingThisTurn = false;
                            }
                            else if(distToArchon < 10) {
                                sootforcer.M.addVector(loc, 3);
                                sootforcer.M.addCircleZone(loc, tree.radius + GameConstants.LUMBERJACK_STRIKE_RADIUS - floatSafety, 30);
                                sootforcer.Test.log("trees rather close to archon");
                                if(dist < GameConstants.LUMBERJACK_STRIKE_RADIUS) doBugPathingThisTurn = false;

                            }
                            else if(distToArchon < 16) {
                                sootforcer.M.addVector(loc, 2);
                                sootforcer.M.addCircleZone(loc, tree.radius + GameConstants.LUMBERJACK_STRIKE_RADIUS - floatSafety, 20);
                                sootforcer.Test.log("trees close to archon");
                                if(dist < GameConstants.LUMBERJACK_STRIKE_RADIUS) doBugPathingThisTurn = false;

                            }
                            else{
                                sootforcer.M.addVector(loc, 1);
                                M.addCircleZone(loc, tree.radius + GameConstants.LUMBERJACK_STRIKE_RADIUS - floatSafety, 10);
                                Test.log("trees kindda close to archon");
                            }
                        }

                    }

                }

            }
        }
    }

    public void dealWithTreesPost() throws Exception{
        trees = rc.senseNearbyTrees(GameConstants.LUMBERJACK_STRIKE_RADIUS);
        int count = trees.length;

        bestTree = null;
        bestTreeScore = 0;
        afterMoveStrikeScoreTrees = 0;

        for(int i = 0; i < count; i++){
            TreeInfo tree = trees[i];

            if(tree.getTeam().equals(ally)){
                afterMoveStrikeScoreTrees -= 0.25f;
            }
            else{
                float curBestTreeScore = 0.6f;

                if(target != null && tree.location.isWithinDistance(target,2f)){
                    curBestTreeScore += 0.2f;
                }



                if(tree.containedRobot != null){
                    curBestTreeScore += 0.4;
                    if(tree.getHealth() <= 6) curBestTreeScore+=20;

                    switch(tree.containedRobot){
                        case TANK:
                            curBestTreeScore += 0.8;
                            break;
                        case ARCHON:
                            if(!Commander.isCommanderArchon) curBestTreeScore += 0.8;
                            break;
                        case LUMBERJACK:
                            curBestTreeScore += 0.4;
                            break;
                        case SOLDIER:
                            curBestTreeScore += 0.3;
                            break;
                        case GARDENER:
                            if(rc.readBroadcast(GARDENER_TOTAL) - rc.readBroadcast(GARDENER_DEATH_FLAGS) < 2){
                                curBestTreeScore += 0.8f;
                            }
                            break;
                    }
                }

                afterMoveStrikeScoreTrees += 0.35f;

                if(curBestTreeScore > bestTreeScore){
                    bestTreeScore = curBestTreeScore;
                    bestTree = tree;
                }
            }



        }
    }


    public void initial() throws Exception{

    }
}
