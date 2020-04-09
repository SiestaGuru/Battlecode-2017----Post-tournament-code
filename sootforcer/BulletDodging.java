package sootforcer;

import battlecode.common.*;

/**
 * Created by Hermen on 29/4/2017.
 */
public class BulletDodging {


    static float AVOID_RADIUS;
    static float AVOID_RADIUS_PLUS;
    static float AVOID_RADIUS_PLUS_PLUS;

    static float RELEVANCE_RADIUS;
    static float PERPENDICULAR_RELEVANCE;
    static float PERPENDICULAR_RELEVANCE_2;
    static float PERPENDICULAR_RELEVANCE_3;

    static float LINEAR_RELEVANCE;
    static float LINEAR_RELEVANCE_DOUBLE;
    static float LINEAR_RELEVANCE_NEG;

    static float LINE_RELEVANCE_RADIUS;
//    static float SPOT_RELEVANCE_RADIUS;
//    static float SPOT_RELEVANCE_RADIUS_2ND;
//    static float SPOT_RELEVANCE_RADIUS_3RD;

    static float DIMINISH_2ND;
    static float DIMINISH_3RD;
    static float RELEVANCE__RADIUS_2ND;
    static float RELEVANCE__RADIUS_2ND_NEG;
    static float RELEVANCE__RADIUS_3RD_NEG;
    static float LINEAR_RELEVANCE__RADIUS_2ND;
    static float LINEAR_RELEVANCE__RADIUS_2ND_NEG;
    static float LINEAR_RELEVANCE__RADIUS_3RD;
    static float LINEAR_RELEVANCE__RADIUS_3RD_NEG;

    static float LINE_RELEVANCE_RADIUS_2ND;
    static float AVOID_RADIUS_2ND;
    static float AVOID_RADIUS_2ND_PLUS;

    static float RELEVANCE__RADIUS_3RD;
    static float LINE_RELEVANCE_RADIUS_3RD;
    static float AVOID_RADIUS_3RD;
    //    static float MAX_REL_X;
//    static float MIN_REL_X;
//    static float MAX_REL_Y;
//    static float MIN_REL_Y;
//    static float MAX_REL_X_2ND;
//    static float MIN_REL_X_2ND;
//    static float MAX_REL_Y_2ND;
//    static float MIN_REL_Y_2ND;
//    static float MAX_REL_X_3RD;
//    static float MIN_REL_X_3RD;
//    static float MAX_REL_Y_3RD;
//    static float MIN_REL_Y_3RD;
    static float DANGER_FIRST;
    static float DANGER_2ND;
    static float DANGER_3RD;
    static float MAX_BULLET_DETECT_CHECK;
    static float OVERLAP_1_2;
    static float OVERLAP_2_3;
    public static float MOVE_SAFETY;

    static float MOVE_SQUARED;

    static float IGNORE_DISTANCE;

    public static ZoneAngledRectangle[] secondaryRects = new ZoneAngledRectangle[50];
    public static int secondaryRectsIndex;
    public static MapLocation[] collisionPoints = new MapLocation[20];
    public static MapLocation[] attemptedSpecials = new MapLocation[80];
    public static int attemptedSpecialsIndex;

    public static void init(){
        //Stored to save bytecodes
        MOVE_SAFETY = 0.005f;
        DIMINISH_2ND = Adjustables.DIMINISH_2ND;
        DIMINISH_3RD = Adjustables.DIMINISH_3RD;

        AVOID_RADIUS = R.radius + MOVE_SAFETY;
        AVOID_RADIUS_PLUS = AVOID_RADIUS + MOVE_SAFETY;
        AVOID_RADIUS_PLUS_PLUS = AVOID_RADIUS + MOVE_SAFETY + R.floatSafety;
        RELEVANCE_RADIUS = AVOID_RADIUS + R.maxMove;

        PERPENDICULAR_RELEVANCE = RELEVANCE_RADIUS;
        PERPENDICULAR_RELEVANCE_2 = PERPENDICULAR_RELEVANCE - DIMINISH_2ND;
        PERPENDICULAR_RELEVANCE_3 = PERPENDICULAR_RELEVANCE - DIMINISH_3RD;

        LINEAR_RELEVANCE = RELEVANCE_RADIUS + 0.3f;
        LINEAR_RELEVANCE_DOUBLE = RELEVANCE_RADIUS *2f;
        LINEAR_RELEVANCE_NEG = -LINEAR_RELEVANCE;


        AVOID_RADIUS_2ND = AVOID_RADIUS - DIMINISH_2ND;
        AVOID_RADIUS_3RD = AVOID_RADIUS - DIMINISH_3RD;
        if(AVOID_RADIUS_3RD < 0.1f){
            AVOID_RADIUS_3RD = 0.1f;
        }

        AVOID_RADIUS_2ND_PLUS = AVOID_RADIUS_2ND + MOVE_SAFETY;

        DANGER_FIRST = Adjustables.DANGER_FIRST;
        DANGER_2ND = Adjustables.DANGER_2ND;
        DANGER_3RD = Adjustables.DANGER_3RD;

        OVERLAP_1_2 = AVOID_RADIUS + AVOID_RADIUS_2ND;
        OVERLAP_2_3 = AVOID_RADIUS_2ND + AVOID_RADIUS_3RD;


//        SPOT_RELEVANCE_RADIUS = RELEVANCE_RADIUS + 0.3f;
        LINE_RELEVANCE_RADIUS = RELEVANCE_RADIUS + 1;
        RELEVANCE__RADIUS_2ND = RELEVANCE_RADIUS - DIMINISH_2ND;
        LINE_RELEVANCE_RADIUS_2ND = LINE_RELEVANCE_RADIUS - DIMINISH_2ND;

//        SPOT_RELEVANCE_RADIUS_2ND = SPOT_RELEVANCE_RADIUS - DIMINISH_2ND;
//        SPOT_RELEVANCE_RADIUS_3RD = SPOT_RELEVANCE_RADIUS - DIMINISH_3RD;
        RELEVANCE__RADIUS_3RD = RELEVANCE_RADIUS - DIMINISH_3RD;
        LINE_RELEVANCE_RADIUS_3RD = LINE_RELEVANCE_RADIUS - DIMINISH_3RD;


        MAX_BULLET_DETECT_CHECK = RELEVANCE_RADIUS + C.MAX_BULLET_VISION;

        LINEAR_RELEVANCE__RADIUS_2ND = RELEVANCE_RADIUS;
        LINEAR_RELEVANCE__RADIUS_3RD = RELEVANCE_RADIUS;

        RELEVANCE__RADIUS_2ND_NEG = -RELEVANCE__RADIUS_2ND;
        RELEVANCE__RADIUS_3RD_NEG = -RELEVANCE__RADIUS_3RD;
        LINEAR_RELEVANCE__RADIUS_2ND_NEG = -LINEAR_RELEVANCE__RADIUS_2ND;
        LINEAR_RELEVANCE__RADIUS_3RD_NEG = -LINEAR_RELEVANCE__RADIUS_3RD;
        MOVE_SQUARED = R.maxMove * R.maxMove;

        IGNORE_DISTANCE = R.maxMove + R.radius + 0.2f;



    }

    //This detects incoming bullets, and adds avoidant movement shapes for the bullets we want to dodge
    //This is a fairly expensive process, so this method is optimized heavily
    public static void dodgeBulletsTrig() throws Exception{
        // Test.beginClockTestAvg(19);
        R.bullets = R.rc.senseNearbyBullets(MAX_BULLET_DETECT_CHECK);

        int maxChecks = Math.min(R.bullets.length - 1, 15);

        for (int i = 0; i < maxChecks; i++) {
            BulletInfo bullet = R.bullets[i];
            Direction dir = bullet.dir;
            MapLocation loc = bullet.location;
            float dist0 = R.myLocation.distanceTo(loc);
            float theta = dir.radiansBetween(loc.directionTo(R.myLocation));
            if(dist0 > IGNORE_DISTANCE && Math.abs(theta) > C.HALFPI){
                //Test.line(loc,loc.add(dir,0.1f),0,0,0);
                continue; //bullet moving away
            }

            //Bullets move in between turns bullet 01 is the predicted movement of the bullet between now and turn 1
            //Check 12 determines whether we want to check whether the bullet is going to be dangerous more than 1 from now
            boolean check01;
            boolean check12;
            boolean check23;
            //First check whether our perpendicular distance to the bullet is close enough (within movement and decent safety range)
            //Also consider performance, if we have a million bullets, only consider the most dangerous (cloest) ones
            float perpendicularDist = (float)Math.abs(dist0 * Math.sin(theta));
            if(perpendicularDist > PERPENDICULAR_RELEVANCE){
                continue;
            } else if(perpendicularDist > PERPENDICULAR_RELEVANCE_3){
                check01 = true;
                check12 = MonsterMove.angledRectangleCount < 12 && perpendicularDist < PERPENDICULAR_RELEVANCE_2;
                check23 = false;
            }else{
                check01 = true;
                check12 = MonsterMove.angledRectangleCount < 12;
                check23 = MonsterMove.angledRectangleCount < 8;
            }

            float speed = bullet.speed;
            float doubleSpeed = Float.sum(speed,speed);
            float signedLongiDist = dist0 * (float)Math.cos(theta);
            float longitudinalDist =  Math.abs(signedLongiDist);

            if(speed > LINEAR_RELEVANCE_DOUBLE) {
                //Use of this codepath is limited. Mostly tank fire


                //So there's basically these points in this order
                //Point 0:  Start of 01  at 0
                //Point 1:  Start of 12 at speed - rel
                //Point 2:  End of 01 at speed + rel
                //Point 3: Start of 23 at 2*speed - rel
                //Point 4: End of l2 at 2*speed + rel
                //Point 5: End of 23 at 3*speed + rel
                //Where "rel" is the linear relevance, value that determines how close we ought to be for us to
                //Consider the bullets movement on the specified turn

                //Points > 3
                if (longitudinalDist > Float.sum(doubleSpeed, LINEAR_RELEVANCE_NEG)) {
                    //Points > 5
                    if (longitudinalDist > Float.sum((speed * 3f), LINEAR_RELEVANCE)) {
                        //R.rc.setIndicatorLine(loc, loc.add(dir,0.2f),0,255,0);
                        continue;
                    }
                    //Points  4-5
                    else if (longitudinalDist > Float.sum(doubleSpeed, LINEAR_RELEVANCE)) {
                        if(!check23) continue;
                        check01 = false;
                        check12 = false;
                        // Test.debug_draw_string(loc, "4-5", 0, 0, 0);
                    }
                    //Point 3-4
                    else {
                        if(!(check12 || check23)) continue;
                        check01 = false;
                        //Test.debug_draw_string(loc, "3-4", 0, 0, 0);
                    }
                }
                //Points > 1
                else if (longitudinalDist > Float.sum(speed, LINEAR_RELEVANCE_NEG)) {
                    //Points 2-3
                    if (longitudinalDist > Float.sum(speed, LINEAR_RELEVANCE)) {
                        if(!(check12)) continue;
                        check01 = false;
                        check23 = false;
                        // Test.debug_draw_string(loc, "2-3", 0, 0, 0);
                    }
                    //Points 1-2
                    else {
                        check23 = false;
                        //Test.debug_draw_string(loc, "1-2", 0, 0, 0);
                    }
                }
                //Points 0-1
                else {
                    check12 = false;
                    check23 = false;
                    //Test.debug_draw_string(loc, "0-1", 0, 0, 0);
                }
            }else{
                //Bullets move in between turns bullet 01 is the predicted movement of the bullet between now and turn 1
                //So there's basically these points in this order
                //Point 0:  Start of 01  at 0
                //Point 1:  Start of 12 at speed - rel
                //Point 2:  End of 01 at speed + rel
                //Point 3: Start of 23 at 2*speed - rel
                //Point 4: End of l2 at 2*speed + rel
                //Point 5: End of 23 at 3*speed + rel
                //Where "rel" is the linear relevance, value that determines how close we ought to be for us to
                //Consider the bullets movement on the specified turn

                //Points > 3
                if (longitudinalDist > Float.sum(speed, LINEAR_RELEVANCE)) {
                    //Points > 5
                    if (longitudinalDist > Float.sum((speed * 3f), LINEAR_RELEVANCE)) {
                        //R.rc.setIndicatorLine(loc, loc.add(dir,0.2f),0,255,0);
                        continue;
                    }
                    //Points  4-5
                    else if (longitudinalDist > Float.sum(doubleSpeed, LINEAR_RELEVANCE)) {
                        if(!(check23)) continue;
                        check01 = false;
                        check12 = false;
                        //Test.debug_draw_string(loc, "4-5", 0, 0, 0);
                    }
                    //Point 3-4
                    else {
                        if(!(check12 || check23)) continue;
                        check01 = false;
                        //Test.debug_draw_string(loc, "3-4", 0, 0, 0);
                    }
                }
                //Points > 1
                else if (longitudinalDist > Float.sum(speed, LINEAR_RELEVANCE_NEG)) {
                    //Points 1-2
                    if (longitudinalDist <= Float.sum(doubleSpeed, LINEAR_RELEVANCE_NEG)) {
                        check23 = false;

                        //  Test.debug_draw_string(loc, "1-2", 0, 0, 0);
                    }
                    //Points 2-3   (everything is acceptable from 2-3 here)
                    else {
                        //  Test.debug_draw_string(loc, "1-2", 0, 0, 0);
                    }
                }
                //Points 0-1
                else {
                    check12 = false;
                    check23 = false;
                    // Test.debug_draw_string(loc, "0-1", 0, 0, 0);
                }
            }




            MapLocation loc1;
            //Min and max for 01.  Min based on a check found in the server code
            //Apparently bullets can hit behind them
            if(check01){
                loc1 = loc.add(dir, speed);
                float danger = Float.sum(DANGER_FIRST, -10 * bullet.damage);
                MonsterMove.addAngledRectangle(loc.add(dir,AVOID_RADIUS), dir, loc1, AVOID_RADIUS, danger,speed); //adding avoid radius because we dont want a rectangular end here
                MonsterMove.addCircle(loc,AVOID_RADIUS,danger);
                if (MonsterMove.angledRectangleCount >= 18) {
                    break;
                }
                //   debug_drawBullet(loc, loc1, dir, AVOID_RADIUS, 230, 20, 20);

                if (check12) {
                    if (Float.sum(longitudinalDist, OVERLAP_1_2) < Float.sum(doubleSpeed, LINEAR_RELEVANCE)) { //TODO: not sure if correct, from overlap -> avoid radius
                        loc1 = loc1.add(dir, OVERLAP_1_2);
                    } else {
                        check12 = false;
                    }
                }
            }else{
                if(check12){
                    loc1 = loc.add(dir, speed);
                }else{
                    loc1 = null; //ugh, why can't I just suppress this
                }
            }

            MapLocation loc2;
            if(check12){
                loc2 = loc.add(dir, doubleSpeed);
//                M.angledRectangles[M.angledRectangleIndex++] = new ZoneAngledRectangle(loc1, dir, loc2, AVOID_RADIUS_2ND, DANGER_2ND + -10 * bullet.damage);
                MonsterMove.addAngledRectangle(loc1, dir, loc2, AVOID_RADIUS_2ND, Float.sum(DANGER_2ND , -10 * bullet.damage),speed);
                //  debug_drawBullet(loc1, loc2, dir, AVOID_RADIUS_2ND, 40, 230, 20);

                if(check23){
                    if( Float.sum(longitudinalDist, OVERLAP_2_3)  <  Float.sum(speed * 3f , LINEAR_RELEVANCE)){
                        loc2 = loc2.add(dir, OVERLAP_2_3);
                    }
                    else{
                        check23 = false;
                    }
                }
            }else if(check23){
                loc2 =  loc.add(dir, doubleSpeed);
            } else{
                loc2 = null;
            }

            if(check23 && !(check01 && check12)){
//              M.angledRectangles[M.angledRectangleIndex++] = new ZoneAngledRectangle(loc2, dir, speed + AVOID_RADIUS_3RD, AVOID_RADIUS_3RD, DANGER_3RD + -10 * bullet.damage);
                MonsterMove.addAngledRectangle(loc2, dir, loc2.add(dir,speed), AVOID_RADIUS_3RD, Float.sum(DANGER_3RD , -10 * bullet.damage),speed);
                //   debug_drawBullet(loc2, loc.add(dir,speed*3), dir, AVOID_RADIUS_3RD, 50, 20, 230);
            }

            MapLocation collisionPoint = loc.add(dir,Math.max(0,signedLongiDist));

            //Subtly avoid collision points
            //M.addVector(collisionPoint, -3);
            M.addVector(collisionPoint, -3);


            //This section will set suggested move points to dodge the bullets, basically spots at which we just barely dodge the bullets
            //This is really important, since without these suggested moves, our bot may not be able to find a free spot among the standard
            //Move directions. This allows staying much closer to a firing enemy without getting hit
            if(M.noBoostLocationsCount < 18) {
                Direction moveStraightAway;
                if(R.myLocation.equals(collisionPoint)){
                    moveStraightAway = dir.rotateLeftDegrees(90);
                }else{
                    //Is basically 90 degrees from dir, but already incorporates the correct escape direction
                    moveStraightAway = collisionPoint.directionTo(R.myLocation);
                }

                float sideMovement = AVOID_RADIUS_PLUS_PLUS -  R.myLocation.distanceTo(collisionPoint);
                if(sideMovement < R.maxMove && sideMovement > R.maxMoveNeg) {
                    //Go exactly to the closest spot at which the bullet can be dodged, regardless of whether it is closer to us than the bullet
                    MapLocation sideStep = R.myLocation.add(moveStraightAway, sideMovement);
                    M.noBoostSpecialLocations[M.noBoostLocationsCount++] = sideStep;

                    //Test.line(sideStep,loc.add(dir,speed),Test.debug_randomcolor());
                    //Strafe from the best dodge position. Generally along with the bullets direction
                    if(M.noBoostLocationsCount < 13) {
                        float strafe = (float) Math.sqrt(MOVE_SQUARED - sideMovement * sideMovement);
                        M.noBoostSpecialLocations[M.noBoostLocationsCount++] = sideStep.add(moveStraightAway.rotateRightDegrees(90), strafe);
                        M.noBoostSpecialLocations[M.noBoostLocationsCount++] = sideStep.add(moveStraightAway.rotateLeftDegrees(90), strafe);
                    }
                }
                //move as far from collision point as possible
                if(M.noBoostLocationsCount < 13) {
                    M.noBoostSpecialLocations[M.noBoostLocationsCount++] = R.myLocation.add(moveStraightAway, R.maxMove);
                }

                //Along with the bullet. Especially useful if we weren't going to be hit in the first place
                //May not really be necessary since it's already partially caught by the strafing
                // M.noBoostSpecialLocations[M.noBoostLocationsCount++] = R.myLocation.add(dir, R.maxMove);

                //Move directly away from the bullet itself
                // M.noBoostSpecialLocations[M.noBoostLocationsCount++] = R.myLocation.add(loc.directionTo(R.myLocation), R.maxMove);

                //Step behind the bullet, not sure if any point in this
                // MapLocation behind = loc.subtract(dir,AVOID_RADIUS_PLUS_PLUS);
                //if(R.myLocation.isWithinDistance(behind,R.maxMove)){
                //     M.noBoostSpecialLocations[M.noBoostLocationsCount++] = behind;
                //}

                Test.debug_log(bullet.damage + "");

                if (perpendicularDist < R.radius && longitudinalDist > Float.sum(speed, R.radius)) {
                    //A hit somewhat far in the future, try moving away from the actual bullet loc, we may be able to still dodge it if we move further
                    //side dodging is already handled
                    M.addVector(loc, -8);
                }

            }


        }
        //  Test.endClockTestAvg(19,"bulletsfaster ");
    }



    private static void debug_drawBullet(MapLocation loc1, MapLocation loc2, Direction dir, float avoidRadius, int r, int g, int b){
        if( R.player1) {
//            rc.setIndicatorLine(loc1.add(originalEnemyDir,-avoidRadius), loc2.add(originalEnemyDir,avoidRadius), r, g, b);


            MapLocation m1 = loc1.add(dir, -avoidRadius).add(dir.rotateLeftDegrees(90), avoidRadius);
            MapLocation m2 = loc1.add(dir, -avoidRadius).add(dir.rotateRightDegrees(90), avoidRadius);
            MapLocation m3 = loc2.add(dir, avoidRadius).add(dir.rotateLeftDegrees(90), avoidRadius);
            MapLocation m4 = loc2.add(dir, avoidRadius).add(dir.rotateRightDegrees(90), avoidRadius);
            R.rc.setIndicatorLine(m1, m2, r, g, b);
            R.rc.setIndicatorLine(m1, m3, r, g, b);
            R.rc.setIndicatorLine(m4, m2, r, g, b);
            R.rc.setIndicatorLine(m4, m3, r, g, b);
        }
    }
}
