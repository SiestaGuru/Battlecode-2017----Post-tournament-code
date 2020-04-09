package sootforcer;

import battlecode.common.*;
import sootforcer.*;
import sootforcer.C;
import sootforcer.DataStructures.BroadcastListMapLocationsHighPrecision;
import sootforcer.M;
import sootforcer.Map;
import sootforcer.R;

/**
 * Created by Hermen on 22/4/2017.
 */
public class Tank extends Shooter {

    private static BulletInfo[] counterBullets = new BulletInfo[20];
    private static int coutnerBulletCount = 0;

    private static MapLocation lastturnsomeonfiringatus;
    private  static MapLocation someoneFiringAtUs;
    private static boolean predictionToggle = false;
    private  static MapLocation lastTurnSnipeMovingTarget;


    private static MapLocation[] tanks;
    private static MapLocation[] soldiers;


    private static boolean haveScoutBuddy = false;


    private static MapLocation lastSnipeTarget = null;

    public void step() throws Exception{
        someoneFiringAtUs = null;
        dodgeBulletsTrigTrackIncoming();


        dealWithLongDistUnits();

        dealWithTrees();
        dealWithRobots();
        dealWithMacroCircle();


        if(mainTarget != null) sootforcer.M.addVector(mainTarget, 4);
        if(mapCenter != null){
            sootforcer.M.addVectorCircleZone(mapCenter, Math.min(map_right - map_left, map_top - map_bot) /4f, 0, -2f);
            sootforcer.M.addVector(getRandomGoal(),2);
//            M.addVectorCircleZone(mapCenter, Math.min(map_right - map_left, map_top - map_bot) /4f, -10, -3f);
        }



        sootforcer.M.calculateBestMove(4000, bullets.length == 0 && robotsEnemy.length == 0);
        sootforcer.M.doBestMove();
        fire(false,false);


        lastturnsomeonfiringatus = someoneFiringAtUs;

    }


    public void dealWithLongDistUnits() throws Exception{
        soldiers = soldierList.getAll();
        tanks = tankList.getAll();


        for(int i = Math.min(soldiers.length - 1,20) ; i >= 0; i-- ){
            sootforcer.M.addVectorCircleZone(soldiers[i],10,-60,-4);
        }

        for(int i = Math.min(tanks.length - 1,20) ; i >= 0; i-- ){
            sootforcer.M.addCircleZone(tanks[i],9,-60);
        }

    }


    public void dealWithRobots() throws Exception {

        robotsEnemy = rc.senseNearbyRobots(sightradius, enemy);
        haveScoutBuddy = false;

        boolean forbidreporting;
        if(robotsEnemy.length == 0 && lastBestTarget != null){
            attemptMaintainLastTarget();
            forbidreporting = true;
        }else{
            forbidreporting = false;
        }

        if(robotsEnemy.length *4 + bullets.length > 15){
            massCombat = true;
        }else{
            massCombat = false;
        }

        robotsAlly = rc.senseNearbyRobots(sightradius, ally);


        for (int i = 0; i < robotsEnemy.length; i++) {
            RobotInfo r = robotsEnemy[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;

            Direction dir = myLocation.directionTo(loc);
            switch (type) {
                case SOLDIER:
                    if(!forbidreporting)spottedEnemySoldier(loc);

                    sootforcer.M.addVector(loc,-10); //wed rather snipe enemy soldiers
                    break;
                case GARDENER:
                    if(!forbidreporting)spottedEnemyGardener(loc);

                    //Move towards
                    sootforcer.M.addCircleZone(loc, sootforcer.C.POINT_BLANK_SOLDIER_ON_GARDENER, 50);

                    if(dist < 5 && treeSnipingSpotStandHere == null){
                        if(dist > sootforcer.C.POINT_BLANK_SOLDIER_ON_GARDENER + 1) {
                            getSnipingSpot(loc,dir);
                        }
                    }
                    if(treeSnipingSpotStandHere == null){
                        sootforcer.M.addVector(loc, 8);
                        sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.add(dir, maxMove);
                    }
                    break;
                case LUMBERJACK:

                    MonsterMove.addVectorCircle(loc, sootforcer.C.LUMBERJACK_DANGER,-500,-20);
                    MonsterMove.addVectorCircle(loc, sootforcer.C.LUMBERJACK_DANGER_PLUS_SOLDIER_MOVE,-50,-2);

                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.subtract(dir, maxMove);


                    break;
                case ARCHON:
                    sootforcer.M.addVector(loc, 2);
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = myLocation.add(dir, maxMove);

                    if(!forbidreporting)spottedEnemyArchon(r);
                    break;
                case SCOUT:
                    if(!forbidreporting)spottedEnemyScout(loc);
                    break;
                case TANK:
                    if(!forbidreporting)spottedEnemyTank(loc);

                    sootforcer.M.addVector(loc,-10); //wed rather snipe enemy tanks

                    break;
            }


        }

        for (int i = 0; i < robotsAlly.length; i++) {
            RobotInfo r = robotsAlly[i];
            MapLocation loc = r.location;
            float dist = myLocation.distanceTo(loc);
            RobotType type = r.type;


            sootforcer.M.addVectorCircleZone(loc,type.bodyRadius + radius + 3,-50,-4);
            switch (type) {
                case SCOUT:
                    if(loc.isWithinDistance(myLocation,4.5f)){
                        haveScoutBuddy = true;
                    }
                    break;
                case GARDENER:
                    friendlyGardenersSpotted++;
                    break;
            }
        }

        if (robotsEnemy.length > 0) {
            canBroadcast = true; //No point in trying to remain silent if they can see us
        }
    }

    public void dealWithTrees() throws Exception{
        trees = rc.senseNearbyTrees(6);
        int count = trees.length;


        for(int i = 0; i < count; i++){
            TreeInfo tree = trees[i];
            MapLocation loc = tree.location;
            float dist = myLocation.distanceTo(loc);


            if(tree.containedBullets > 0){
                if(rc.canShake(tree.getID())){
                    rc.shake(tree.getID());
                }
            }

            if(tree.radius < 1 || tree.containedRobot != null) {
                sootforcer.M.addCircleZone(loc, tree.getRadius(), -50);
            }

            if(tree.getTeam().equals(ally) && dist < GameConstants.BULLET_TREE_RADIUS + radius + maxMove){
                sootforcer.M.addCircleZone(loc, tree.getRadius(), -50);
            }
        }
    }


    //This method attempts to find shots outside of its vision range
    //It uses communicated unit information and incoming bullets to determine where enemies might be
    //And checks communicated map information to determine whether it is safe to fire such shots
    //Since we don't want to shoot our friends in the back / waste bullets on trees
    public static MapLocation findLongRangeShot() throws Exception{

        MapLocation bestSnipe = null;
        float bestScore = -30;


        if(!haveScoutBuddy) {
            MapLocation[] possibleSpots = new MapLocation[7];
            int foundSpots = 0;

            outerloop:
            for (int i2 = 0; i2 < coutnerBulletCount; i2++) {
                BulletInfo bullet = counterBullets[i2];

                //rc.setIndicatorLine(counterBullets[i2].location, counterBullets[i2].location.add(bullet.location.directionTo(myLocation), 0.4f), 100, 50, 50);

                if (myLocation.distanceTo(bullet.location) > 8f) { //This is approx the distance we expect soldiers at
                    possibleSpots[foundSpots++] = bullet.location;
                    if (foundSpots == 7) break outerloop;
                } else {
                    MapLocation newSpot = bullet.location.subtract(bullet.dir, bullet.speed);
                    if (myLocation.distanceTo(newSpot) > 8f) { //This is approx the distance we expect soldiers at
                        possibleSpots[foundSpots++] = newSpot;
//                    //rc.setIndicatorLine(bullet.location, newSpot, 0, 255, 255);
                        if (foundSpots == 7) break outerloop;
                    }
                }
            }


            int found = 0;



            Test.log("foundspots: " + foundSpots);

            outerloop:
            for (int i = 0; i < foundSpots; i++) {
                for (int i2 = i + 1; i2 < foundSpots; i2++) {
                    MapLocation spot1 = possibleSpots[i];
                    MapLocation spot2 = possibleSpots[i2];

                    float dist = spot1.distanceTo(spot2);

                    if (dist < 1.5f) {

                        MapLocation middle = spot1.add(spot1.directionTo(spot2), dist / 2f);
                        float score = 10 - (myLocation.distanceTo(middle) + dist);
                        if (score > bestScore) {
                            bestScore = score;
                            bestSnipe = middle;

//                        //rc.setIndicatorDot(bestMatch,255,255,0);

                        }
                        found++;
                        if (found > 5) break outerloop;
                    }
                }
            }


            if (found < 5) {
                if (lastturnsomeonfiringatus != null) {
                    for (int i = 0; i < foundSpots; i++) {
                        if (lastturnsomeonfiringatus.distanceTo(possibleSpots[i]) < 0.5f) {

                            float score = 10 - myLocation.distanceTo(possibleSpots[i]) - lastturnsomeonfiringatus.distanceTo(possibleSpots[i]) - 12;

                            if (score > bestScore) {
                                bestScore = score;
                                bestSnipe = possibleSpots[i];
//                            //rc.setIndicatorDot(bestMatch, 255, 0, 0);
                            }
                            found++;
                            if (found > 5) break;
                        }
                    }
                }
            }


            if (bestSnipe != null) {
                someoneFiringAtUs = bestSnipe;

                if (lastturnsomeonfiringatus != null && lastturnsomeonfiringatus.isWithinDistance(bestSnipe, 2.5f)) {
                    //Trying to do predictive shooting when returning fire
                    if (predictionToggle) {
                        bestSnipe = bestSnipe.add(lastturnsomeonfiringatus.directionTo(bestSnipe), lastturnsomeonfiringatus.distanceTo(bestSnipe) * 1.25f);
                        //rc.setIndicatorDot(bestSnipe, 200, 150, 150);
                        predictionToggle = false;
                    } else {
                        bestSnipe = bestSnipe.add(lastturnsomeonfiringatus.directionTo(bestSnipe), lastturnsomeonfiringatus.distanceTo(bestSnipe) * 0.8f);
                        //rc.setIndicatorDot(bestSnipe, 200, 150, 150);

                        predictionToggle = true;
                    }
                } else {
                    if (lastturnsomeonfiringatus == null) {
                        Test.log("no last turn");
                    } else {
                        Test.log("last turn too far");

                    }
                    predictionToggle = false;
                }
            }
        }




//        if(bestSnipe != null){
//            //rc.setIndicatorDot(bestMatch,255,255,255);
//            someoneFiringAtUs = bestSnipe;
//            lastSnipeTarget = null;
//            return bestMatch;
//        }




        boolean bestCanMove = false;


        Test.log(tanks.length +"   tanks");
        for(int i = tanks.length - 1; i >= 0; i--){
            MapLocation m = tanks[i];

            if(m.isWithinDistance(myLocation, 40)) {
                float score =  40  - m.distanceTo(myLocation);
                Direction dir = myLocation.directionTo(m);
                if(lastTurnSnipeMovingTarget != null && m.isWithinDistance(lastTurnSnipeMovingTarget,2f)) score += 5;
                if (score > bestScore) {
                    if (!rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 3.1f), 1.3f) && !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 4.3f), 1.3f)&& !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 5.5f), 1.3f)) {
                        if(hasGoodLineToTarget(m)) {
                            bestScore = score + 10;
                            bestSnipe = m;
                            bestCanMove = true;
                        }
                        else if(Clock.getBytecodesLeft() < 3000){
                            bestScore = score;
                            bestSnipe = m;
                            bestCanMove = true;
                        }
                        else{
                            Test.log("potential target excluded");
                        }

                    } else {
//                        Test.lineTo(m, 0, 0, 0);
                    }

                }
            }
        }


        MapLocation m1 = getTheirArchon1Loc();
        MapLocation m2 = getTheirArchon2Loc();
        MapLocation m3 = getTheirArchon3Loc();


        if(m1 != null){
            float score =  20  - m1.distanceTo(myLocation);
            Direction dir = myLocation.directionTo(m1);

            if(lastTurnSnipeMovingTarget != null && m1.isWithinDistance(lastTurnSnipeMovingTarget,2f)) score += 5;

            if (score > bestScore) {

                if (!rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 3.1f), 1.3f) && !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 4.3f), 1.3f)&& !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 5.5f), 1.3f)) {
                    if(hasGoodLineToTarget(m1)) {
                        bestScore = score + 10;
                        bestSnipe = m1;
                        bestCanMove = true;
                    }
                    else if(Clock.getBytecodesLeft() < 3000){
                        bestScore = score;
                        bestSnipe = m1;
                        bestCanMove = true;
                    }
                    else{
                        Test.log("potential target excluded");
                    }
                } else {
//                    Test.lineTo(m1, 0, 0, 0);
                }
            }
        }

        if(m2 != null){
            float score =  20  - m2.distanceTo(myLocation);
            Direction dir = myLocation.directionTo(m2);

            if(lastTurnSnipeMovingTarget != null && m2.isWithinDistance(lastTurnSnipeMovingTarget,2f)) score += 5;

            if (score > bestScore) {
                if (!rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 3.1f), 1.3f) && !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 4.3f), 1.3f)&& !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 5.5f), 1.3f)) {
                    if(hasGoodLineToTarget(m2)) {
                        bestScore = score + 10;
                        bestSnipe = m2;
                        bestCanMove = true;
                    }
                    else if(Clock.getBytecodesLeft() < 3000){
                        bestScore = score;
                        bestSnipe = m2;
                        bestCanMove = true;
                    }
                    else{
                        Test.log("potential target excluded");
                    }
                } else {
//                    Test.lineTo(m2, 0, 0, 0);
                }
            }
        }

        if(m3 != null){
            float score =  20  - m3.distanceTo(myLocation);
            Direction dir = myLocation.directionTo(m3);

            if(lastTurnSnipeMovingTarget != null && m3.isWithinDistance(lastTurnSnipeMovingTarget,2f)) score += 5;
            if (score > bestScore) {
                if (!rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 3.1f), 1.3f) && !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 4.3f), 1.3f)&& !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 5.5f), 1.3f)) {
                    if(hasGoodLineToTarget(m3)) {
                        bestScore = score + 10;
                        bestSnipe = m3;
                        bestCanMove = true;
                    }
                    else if(Clock.getBytecodesLeft() < 3000){
                        bestScore = score;
                        bestSnipe = m3;
                        bestCanMove = true;
                    }
                    else{
                        Test.log("potential target excluded");
                    }
                } else {
//                    Test.lineTo(m3, 0, 0, 0);
                }
            }
        }




//        MapLocation[] tanks = BroadcastListMapLocationsHighPrecision.getAll(LIST_ENEMY_TANKS_LOCS_START);




        if(Clock.getBytecodesLeft() > 2000){
            for(int i = soldiers.length - 1; i >= 0; i--){
                MapLocation m = soldiers[i];
                if(m.isWithinDistance(myLocation, 15)) {
                    float score = 30 - m.distanceTo(myLocation);

                    if(haveScoutBuddy) score += 5;

                    if (lastTurnSnipeMovingTarget != null && m.isWithinDistance(lastTurnSnipeMovingTarget, 2f))
                        score += 5;

                    Direction dir = myLocation.directionTo(m);
                    if (score > bestScore) {
                        if (!rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 3.1f), 1.3f) && !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 4.3f), 1.3f)&& !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 5.5f), 1.3f)) {
                            if(hasGoodLineToTarget(m)) {
                                bestScore = score + 10;
                                bestSnipe = m;
                                bestCanMove = true;
                            }
                            else if(Clock.getBytecodesLeft() < 3000){
                                bestScore = score;
                                bestSnipe = m;
                                bestCanMove = true;
                            }
                            else{
                                Test.log("potential target excluded");
                            }
                        } else {
//                            Test.lineTo(m, 0, 0, 0);
                        }

                    }
                }
            }

            if(Clock.getBytecodesLeft() > 2500) {
                MapLocation[] snipeables = BroadcastListMapLocationsHighPrecision.getAll(LIST_SNIPE_THESE_START);


                Test.log(snipeables.length + "   snipeables");
                for (int i = snipeables.length - 1; i >= 0; i--) {
                    MapLocation m = snipeables[i];
                    float score = -m.distanceTo(myLocation)  -20;
                    Direction dir = myLocation.directionTo(m);

                    if (m.equals(lastSnipeTarget)) score += 20;

                    if (score > bestScore) {

                        if (!rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 3.1f), 1.3f) && !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 4.3f), 1.3f)&& !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 5.5f), 1.3f)) {
                            if(hasGoodLineToTarget(m)) {
                                bestScore = score + 10;
                                bestSnipe = m;
                                bestCanMove = false;
                            }
                            else if(Clock.getBytecodesLeft() < 3000){
                                bestScore = score;
                                bestSnipe = m;
                                bestCanMove = false;
                            }
                            else{
                                Test.log("potential target excluded");
                            }

                        } else {
//                            Test.lineTo(m, 0, 0, 0);
                        }

                    }
                }
            }

            MapLocation[] gardeners = gardenerList.getAll(1000 + Clock.getBytecodesLeft() / 30);

            for(int i = gardeners.length - 1; i >= 0; i--){
                MapLocation m = gardeners[i];
                if(m.isWithinDistance(myLocation, 20)) {
                    float score =    - m.distanceTo(myLocation) - 15;
                    Direction dir = myLocation.directionTo(m);

                    if(lastTurnSnipeMovingTarget != null && m.isWithinDistance(lastTurnSnipeMovingTarget,2f)) score += 15;

                    if (score > bestScore) {
                        if (!rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 3.1f), 1.3f) && !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 4.3f), 1.3f)&& !rc.isCircleOccupiedExceptByThisRobot(myLocation.add(dir, 5.5f), 1.3f)) {
                            if(hasGoodLineToTarget(m)) {
                                bestScore = score + 10;
                                bestSnipe = m;
                                bestCanMove = true;
                            }
                            else if(Clock.getBytecodesLeft() < 3000){
                                bestScore = score;
                                bestSnipe = m;
                                bestCanMove = true;
                            }
                            else{
                                Test.log("potential target excluded");
                            }

                        } else {
//                            Test.lineTo(m, 0, 0, 0);
                        }
                    }
                }
            }






        }



        if(bestSnipe != null) {
            if(bestCanMove){
                MapLocation temp = bestSnipe;
                if(lastTurnSnipeMovingTarget != null) {

                    float modifier = 0.5f + (myLocation.distanceTo(bestSnipe) / 30f) ;


                    if (toggleShot) {
                        bestSnipe = bestSnipe.add(lastTurnSnipeMovingTarget.directionTo(bestSnipe), lastTurnSnipeMovingTarget.distanceTo(bestSnipe) * 2f * modifier);
                     }
                     else{
                        bestSnipe = bestSnipe.add(lastTurnSnipeMovingTarget.directionTo(bestSnipe), lastTurnSnipeMovingTarget.distanceTo(bestSnipe) * 1f * modifier);
                    }
                }
                lastTurnSnipeMovingTarget = temp;
            }
            else{
                lastTurnSnipeMovingTarget = null;
            }


            //Final check whether we truly have free line of fire, based on our map analysis
            if(Clock.getBytecodesLeft() >  2000){
                if(!hasGoodLineToTarget(bestSnipe)){
                    bestSnipe = null;
                    lastTurnSnipeMovingTarget = null;
                }
            }

//            Test.lineTo(bestSnipe);
            return  bestSnipe;

        }else{
            lastTurnSnipeMovingTarget = null;
        }



        return null;
    }


    public static boolean hasGoodLineToTarget(MapLocation snipeTarget) throws GameActionException{

        Direction dir = myLocation.directionTo(snipeTarget);
        float score = 0;

        MapLocation spot = myLocation.add(dir,7);
        float actualDist = myLocation.distanceTo(snipeTarget);
        float dist = myLocation.distanceTo(spot);
//        //rc.setIndicatorLine(myLocation,snipeTarget,  255,0,255);

        while(dist < actualDist && Clock.getBytecodesLeft() > 1000 && score >= -3){
            int tile = sootforcer.Map.getMapSpot(spot);
            spot = spot.add(dir,0.7f);
            dist = myLocation.distanceTo(spot);
            switch(tile){
                case sootforcer.Map.TILE_TREE:
                    score -= 1;
//                    //rc.setIndicatorLine(spot,spot.subtract(dir,0.7f),  0,0,255);
                    break;
                case sootforcer.Map.TILE_ALLY:
                    score -= 0.7f;
//                    //rc.setIndicatorLine(spot,spot.subtract(dir,0.7f),  200,0,0);
                    break;
                case sootforcer.Map.TILE_ENEMY:
                    score += 0.2f;
//                    //rc.setIndicatorLine(spot,spot.subtract(dir,0.7f),  0,200,0);
                    break;
                case sootforcer.Map.TILE_UNKNOWN:
                    score -= 0.06f;
//                    //rc.setIndicatorLine(spot,spot.subtract(dir,0.7f),  50,50,50);
                    break;
                case Map.TILE_PARTIAL:
                    score -= 0.15f;
//                    //rc.setIndicatorLine(spot,spot.subtract(dir,0.7f),  70,30,10);
                    break;
                default:
//                    //rc.setIndicatorLine(spot,spot.subtract(dir,0.7f),  180,180,180);
                    break;
            }
        }


        if(score < -2){
            return false;
        }else{
            return true;
        }

    }


    public void initial() throws Exception{

    }


    //See also BulletDodging
    //This is a slightly modified version that also notes the incoming trajectories and uses that to determine where
    //Enemies are likely to be outside of our vision range
    //This circumvents a lot of our very limited vision range, making tanks a lot more viable
    public static void dodgeBulletsTrigTrackIncoming() throws Exception{
        coutnerBulletCount = 0;

        sootforcer.R.bullets = sootforcer.R.rc.senseNearbyBullets(bulletsightradius);

        for (int i = sootforcer.R.bullets.length - 1; i>=0; i--) {
            BulletInfo bullet = sootforcer.R.bullets[i];
            Direction dir = bullet.dir;
            MapLocation loc = bullet.location;
            float dist0 = sootforcer.R.myLocation.distanceTo(loc);

            float theta = dir.radiansBetween(loc.directionTo(sootforcer.R.myLocation));

            if(theta > -sootforcer.C.DEGREES_45_IN_RADS && theta < C.DEGREES_45_IN_RADS  && dist0 + bullet.speed*2 > 8f){
                if(bullet.damage< 4 && coutnerBulletCount < 19) {
                    counterBullets[coutnerBulletCount++] = bullet;
                }
            }


            if(i > 15) continue;

            float perpendicularDist = (float)Math.abs(dist0 * Math.sin(theta));

            boolean check01 = true;
            boolean check12 = true;
            boolean check23 = true;

            if(perpendicularDist > BulletDodging.PERPENDICULAR_RELEVANCE){
                continue;
            } else if(perpendicularDist > BulletDodging.RELEVANCE__RADIUS_2ND){
                check12 = false;
                check23 = false;
            } else if(perpendicularDist > BulletDodging.RELEVANCE__RADIUS_3RD){
                check23 = false;
            }

            float longitudinalDist = (dist0 * (float)Math.cos(theta));
            float speed = bullet.speed;

            //Max voor 23
            if(longitudinalDist >  (speed * 3f) + BulletDodging.RELEVANCE__RADIUS_3RD){
                //R.//rc.setIndicatorLine(loc, loc.add(dir,0.2f),0,255,0);

                continue;
            }
            //Min voor 12
            else if( longitudinalDist <  speed - BulletDodging.RELEVANCE__RADIUS_2ND){
                check23 = false;
                check12 = false;
            }
            //Max voor 12
            else if(longitudinalDist >  (speed * 2f) + BulletDodging.RELEVANCE__RADIUS_2ND){
                check01 = false;
                check12 = false;
            }
            //Min voor 23
            else if(check23 && longitudinalDist <  (speed * 2f) - BulletDodging.RELEVANCE__RADIUS_3RD){
                check23 = false;
            }



            MapLocation loc1 = loc.add(dir, speed);
            MapLocation loc2 = loc1.add(dir, speed);

            //Min and max for 01.  Min based on a check found in the server code
            //Apparently bullets can hit behind them
            if(check01 && longitudinalDist < speed + BulletDodging.PERPENDICULAR_RELEVANCE){
                MapLocation loc05 = loc.add(dir, speed* 0.5f);
                float maxCheckVal = (GameConstants.MAX_ROBOT_RADIUS + speed/2f);
                if( sootforcer.R.myLocation.isWithinDistance(loc05,maxCheckVal + sootforcer.R.maxMove)) {
//                    M.angledRectangles[M.angledRectangleIndex++] = new ZoneAngledRectangle(loc05.add(dir, AVOID_RADIUS-maxCheckVal), dir, loc1, AVOID_RADIUS, DANGER_FIRST + -10 * bullet.damage);
                    MonsterMove.addAngledRectangle(loc05.add(dir, BulletDodging.AVOID_RADIUS-maxCheckVal), dir, loc1, BulletDodging.AVOID_RADIUS, BulletDodging.DANGER_FIRST + -10 * bullet.damage, speed);
//                    drawBullet(loc05.add(dir, AVOID_RADIUS-maxCheckVal), loc1, dir, AVOID_RADIUS, 230, 20, 20);

                    if(check12){
                        if( longitudinalDist + BulletDodging.OVERLAP_1_2 <  speed - BulletDodging.RELEVANCE__RADIUS_2ND){
                            loc1 = loc1.add(dir, BulletDodging.OVERLAP_1_2);
                        }else{
                            check12 = false;
                        }
                    }

                }
                else{
                    check01 = false;
                }
            }else{
                check01 = false;
            }

            //Could prob be refined
            if((check12 || check23) &&  (MonsterMove.angledRectangleCount >= 12|| sootforcer.R.rc.isLocationOccupiedByTree(sootforcer.R.myLocation.add(dir,speed)))) {
                check12 = false;
                check23 = false;
            }

            if(check12){
//                M.angledRectangles[M.angledRectangleIndex++] = new ZoneAngledRectangle(loc1, dir, loc2, AVOID_RADIUS_2ND, DANGER_2ND + -10 * bullet.damage);
                MonsterMove.addAngledRectangle(loc1, dir, loc2, BulletDodging.AVOID_RADIUS_2ND, BulletDodging.DANGER_2ND + -10 * bullet.damage,speed);

//                drawBullet(loc1, loc2, dir, AVOID_RADIUS_2ND, 40, 230, 20);

                if(check23){
                    if( longitudinalDist  + BulletDodging.OVERLAP_2_3  <  (speed * 2f) - BulletDodging.RELEVANCE__RADIUS_3RD){
                        loc2 = loc2.add(dir, BulletDodging.OVERLAP_2_3);
                    }
                    else{
                        check23 = false;
                    }
                }
            }

            if(check23){
//                M.angledRectangles[M.angledRectangleIndex++] = new ZoneAngledRectangle(loc2, dir, speed + AVOID_RADIUS_3RD, AVOID_RADIUS_3RD, DANGER_3RD + -10 * bullet.damage);
                MonsterMove.addAngledRectangle(loc2, dir, loc2.add(dir,speed), BulletDodging.AVOID_RADIUS_3RD, BulletDodging.DANGER_3RD + -10 * bullet.damage,speed);

//                drawBullet(loc2, loc.add(dir,speed*3), dir, AVOID_RADIUS_3RD, 50, 20, 230);
            }

            if(check01 || check12 || check23){



                MapLocation collisionPoint = loc.add(dir,longitudinalDist);

                if(sootforcer.R.myLocation.distanceTo(collisionPoint) > 0.01f) {
                    //Subtly avoid collision points
                    sootforcer.M.addVector(collisionPoint, -2);
                    Direction moveStraightAway = collisionPoint.directionTo(sootforcer.R.myLocation);


                    //Go exactly to the closest spot at which the bullet can be dodged
                    float sideMovement = BulletDodging.AVOID_RADIUS + sootforcer.R.floatSafety - perpendicularDist;
                    MapLocation sideStep = sootforcer.R.myLocation.add(moveStraightAway, sideMovement);
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = sideStep;

                    //walk backwards a bit while sidestepping. Not the furthest point that can be reached backwards, but
                    //want to conserve bytecodes
                    float extra = sootforcer.R.maxMove - sideMovement;
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = sideStep.add(dir, extra);


                    //Run as far away from the dodge point as possible
                    sootforcer.M.extraPoints[sootforcer.M.specialMapLocationsCount] = 10;
                    sootforcer.M.specialMapLocations[sootforcer.M.specialMapLocationsCount++] = sootforcer.R.myLocation.add(moveStraightAway, sootforcer.R.maxMove);


                    // //rc.setIndicatorLine(collisionPoint,collisionPoint.add(moveStraightAway,AVOID_RADIUS + MOVE_SAFETY -perpendicularDist), 100,200,0);
                }
                else{
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = sootforcer.R.myLocation.add(dir.rotateRightDegrees(90), sootforcer.R.maxMove);
                    sootforcer.M.noBoostSpecialLocations[sootforcer.M.noBoostLocationsCount++] = sootforcer.R.myLocation.add(dir.rotateLeftDegrees(90), sootforcer.R.maxMove);
                }

                //Move directly away from the bullet itself
                sootforcer.M.noBoostSpecialLocations[M.noBoostLocationsCount++] = sootforcer.R.myLocation.add(loc.directionTo(sootforcer.R.myLocation), R.maxMove);
            }

        }
    }

}
