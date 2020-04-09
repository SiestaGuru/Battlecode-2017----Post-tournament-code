package sootforcer;

/**
 * Created by Hermen on 25/4/2017.
 */
public class oldMethods {

//    public void dodgeBullets(BulletInfo[] bullets){
//        //Analyze incoming bullets and try to dodge them
//        //A couple of shapes are used to model the areas in which we are hit
//        //Shapes have been chosen in an attempt to minimize both false positives (believing it will be hit when it wont) and false negatives (believing it wont be hit when it will)
//        //But both still appear because we use squares/circles instead of lines
//        //The focus on whether we want to minimize false positives/false negatives is based on the amount of bullets we see
//        //With a small amount of bullets, we can afford more false positives so that we'll have less false negatives
//        //With a large amount of bullets, we'd rather focus a bit more on false positives, so that we won't end up thinking every move results in being hit
//
//        float safetyRange = 0.2f;
//        boolean extraFar = false;
//        float avoidRadius = radius + 0.03f;
//        float minDist = avoidRadius + maxMove;
//
//        if (bullets.length > 10) {
//            safetyRange = 0.2f;
//        } else if (bullets.length > 3) {
//            extraFar = true;
//            safetyRange = 0.23f;
//        } else {
//            extraFar = true;
//            safetyRange = 0.27f;
//        }
//
//
//        for (int i = 0; i < bullets.length && zoneLoc < 20; i++) {
//            BulletInfo bullet = bullets[i];
//            MapLocation loc = bullet.location;
//
//            MapLocation bullet1Loc = loc.add(bullet.originalEnemyDir, bullet.speed * 0.65f);
//            float dist0 = myLocation.distanceTo(loc);
//            float dist1 = myLocation.distanceTo(bullet1Loc);
//
//            if (dist1 > dist0 + 0.1f) {
//                //The bullet is flying away from us
//                if (dist0 <= minDist) {
//                    zones[zoneLoc] = new DesireZone(loc, avoidRadius, -230 + -10 * bullet.damage);
//                    zoneLoc++;
//                }
//            } else {
//
//                //The bullet might be coming towards us/sideways
//
//                //The shapes used here have been selected to try to minimize both fals epositives/negatives
//                //Shapes that would accurately handle most spots (as few false negatives as possible, especially in the first turn, and preferably not too many false positives either)
//
//                if (bullet.speed < 3) {
//                    //Scout or soldier bullets, not bothering to do these differently as theyre so similar
//                    if (dist0 <= minDist) {
//                        zones[zoneLoc] = new DesireZone(loc, avoidRadius, -230 + -10 * bullet.damage);
//                        zoneLoc++;
//
//                        //These two spots are common dodge spots. Moving as far awy from the bullet as possible, and moving just outside of the bullets hit radius
//                        //Add them, so that we'll at least consider these  (not being able to move is already handled further on)
//                        addSpecialMapLocation(myLocation.add(bullet.location.directionTo(myLocation),maxMove),5 );
//                        addSpecialMapLocation(bullet.location.add(bullet.location.directionTo(myLocation), radius + 0.1f), 0);
////                       //rc.setIndicatorLine(loc, loc.add(bullet.originalEnemyDir, 0.1f), 255, 0, 0);
//                    }
//
//                    if (dist1 <= minDist + safetyRange) {
//                        zones[zoneLoc] = new DesireZone(bullet1Loc, avoidRadius + safetyRange, -200 + -10 * bullet.damage, true);
//                        zoneLoc++;
//
//                        if(extraFar){
//                            if (bullet1Loc.distanceTo(myLocation) <= minDist) {
//                                zones[zoneLoc] = new DesireZone(bullet1Loc, avoidRadius, -30, true);
//                                zoneLoc++;
//                            }
//                        }
////                        //rc.setIndicatorLine(bullet1Loc, bullet1Loc.add(bullet.originalEnemyDir, 0.1f), 0, 255, 0);
//                    }
//
//                    MapLocation bullet2Loc = loc.add(bullet.originalEnemyDir, bullet.speed * 1.4f);
//                    float dist2 = bullet2Loc.distanceTo(myLocation);
//                    if ( dist2 <= minDist + safetyRange) {
//                        zones[zoneLoc] = new DesireZone(bullet2Loc, avoidRadius + safetyRange, -180 + -10 * bullet.damage, true);
//                        zoneLoc++;
//
//                        if(extraFar){
//                            if (dist2 <= minDist) {
//                                zones[zoneLoc] = new DesireZone(bullet2Loc, avoidRadius, -30, true);
//                                zoneLoc++;
//                            }
//                        }
////                        //rc.setIndicatorLine(bullet2Loc, bullet2Loc.add(bullet.originalEnemyDir, 0.15f), 0, 0, 255);
//                    }
//
//                    MapLocation bullet3Loc = loc.add(bullet.originalEnemyDir, bullet.speed * 2.2f);
//                    float dist3 = myLocation.distanceTo(bullet3Loc);
//                    if (dist3 <= minDist) {
//                        zones[zoneLoc] = new DesireZone(bullet3Loc, avoidRadius, -100 + -10 * bullet.damage, true);
//                        zoneLoc++;
//
//                        if(extraFar){
//                            if (dist3 <= minDist) {
//                                zones[zoneLoc] = new DesireZone(bullet3Loc, avoidRadius -safetyRange, -20, true);
//                                zoneLoc++;
//                            }
//                        }
//
////                        //rc.setIndicatorLine(bullet3Loc, bullet3Loc.add(bullet.originalEnemyDir, 0.1f), 200, 200, 0);
//                    }
//
//                    if (zoneLoc < 20) {
//                        MapLocation bullet4Loc = loc.add(bullet.originalEnemyDir, bullet.speed * 2.8f);
//                        if (myLocation.distanceTo(bullet4Loc) <= minDist - 0.2f) {
//                            zones[zoneLoc] = new DesireZone(bullet4Loc, avoidRadius - 0.2f, -30 + -5 * bullet.damage);
//                            zoneLoc++;
////                            //rc.setIndicatorLine(bullet4Loc, bullet4Loc.add(bullet.originalEnemyDir, 0.15f), 0, 200, 200);
//                        }
//                    }
//                } else {
//                    //Tank bullets
//                    if (dist0 <= minDist) {
//                        zones[zoneLoc] = new DesireZone(loc, avoidRadius, -200 + -10 * bullet.damage);
//                        zoneLoc++;
////                        //rc.setIndicatorLine(loc, loc.add(bullet.originalEnemyDir, 0.1f), 255, 0, 0);
//                    }
//
//                    if (dist1 <= minDist + 0.2f) {
//                        zones[zoneLoc] = new DesireZone(bullet1Loc, avoidRadius + 0.4f, -200 + -10 * bullet.damage, true);
//                        zoneLoc++;
////                        //rc.setIndicatorLine(bullet1Loc, bullet1Loc.add(bullet.originalEnemyDir, 0.1f), 0, 255, 0);
//                    }
//
//                    MapLocation bullet2Loc = loc.add(bullet.originalEnemyDir, bullet.speed * 1.4f);
//                    if (bullet2Loc.distanceTo(myLocation) <= minDist + 0.4f) {
//                        zones[zoneLoc] = new DesireZone(bullet2Loc, avoidRadius + 0.4f, -180 + -10 * bullet.damage, true);
//                        zoneLoc++;
////                        //rc.setIndicatorLine(bullet2Loc, bullet2Loc.add(bullet.originalEnemyDir, 0.15f), 0, 0, 255);
//                    }
//
//                    MapLocation bullet3Loc = loc.add(bullet.originalEnemyDir, bullet.speed * 2.1f);
//                    if (myLocation.distanceTo(bullet3Loc) <= minDist + 0.2f) {
//                        zones[zoneLoc] = new DesireZone(bullet3Loc, avoidRadius + 0.2f, -100 + -10 * bullet.damage, true);
//                        zoneLoc++;
////                        //rc.setIndicatorLine(bullet3Loc, bullet3Loc.add(bullet.originalEnemyDir, 0.1f), 200, 200, 0);
//                    }
//
//                    if (zoneLoc < 20) {
//                        MapLocation bullet4Loc = loc.add(bullet.originalEnemyDir, bullet.speed * 2.7f);
//                        if (myLocation.distanceTo(bullet4Loc) <= minDist) {
//                            zones[zoneLoc] = new DesireZone(bullet4Loc, avoidRadius, -30 + -5 * bullet.damage);
//                            zoneLoc++;
////                            //rc.setIndicatorLine(bullet4Loc, bullet4Loc.add(bullet.originalEnemyDir, 0.15f), 0, 200, 200);
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//
//    public static void dodgeBulletsNew(){
//        float AVOID_RADIUS = radius + floatSafety;
//        float RELEVANCE__RADIUS = AVOID_RADIUS + maxMove;
//
//        float LINE_RELEVANCE_RADIUS = RELEVANCE__RADIUS + 1;
//
//
//        float DIMINISH_2ND = maxMove * 0.8f;
//        float DIMINISH_3RD = maxMove * 1f;
//
//        if(player1){
//            DIMINISH_2ND = maxMove * 0.2f;
//        }
//
//
//        float RELEVANCE__RADIUS_2ND = RELEVANCE__RADIUS - DIMINISH_2ND;
//        float LINE_RELEVANCE_RADIUS_2ND = LINE_RELEVANCE_RADIUS - DIMINISH_2ND;
//        float AVOID_RADIUS_2ND = AVOID_RADIUS - DIMINISH_2ND;
//
//        float RELEVANCE__RADIUS_3RD = RELEVANCE__RADIUS - DIMINISH_3RD;
//        float LINE_RELEVANCE_RADIUS_3RD = LINE_RELEVANCE_RADIUS - DIMINISH_3RD;
//        float AVOID_RADIUS_3RD = AVOID_RADIUS - DIMINISH_3RD;
//
//
//        if(AVOID_RADIUS_3RD < 0.1f){
//            AVOID_RADIUS_3RD = 0.1f;
//        }
//
//        float MAX_REL_X =  myX + RELEVANCE__RADIUS;
//        float MIN_REL_X =  myX - RELEVANCE__RADIUS;
//        float MAX_REL_Y =  myY + RELEVANCE__RADIUS;
//        float MIN_REL_Y =  myY - RELEVANCE__RADIUS;
//
//        float MAX_REL_X_2ND =  MAX_REL_X - DIMINISH_2ND;
//        float MIN_REL_X_2ND =  MIN_REL_X + DIMINISH_2ND;
//        float MAX_REL_Y_2ND =  MAX_REL_Y - DIMINISH_2ND;
//        float MIN_REL_Y_2ND =  MIN_REL_Y + DIMINISH_2ND;
//
//        float MAX_REL_X_3RD =  MAX_REL_X - DIMINISH_3RD;
//        float MIN_REL_X_3RD =  MIN_REL_X + DIMINISH_3RD;
//        float MAX_REL_Y_3RD =  MAX_REL_Y - DIMINISH_3RD;
//        float MIN_REL_Y_3RD =  MIN_REL_Y + DIMINISH_3RD;
//
//
//        float dangerFirst = -240;
//        float danger2ND = -180;
//
//        //Test.log("radius 3 : " + AVOID_RADIUS_3RD);
//
//
//        bullets = rc.senseNearbyBullets(RELEVANCE__RADIUS + C.MAX_BULLET_VISION);
//
//
//        for (int i = 0; i < bullets.length; i++) {
//            BulletInfo bullet = bullets[i];
//            MapLocation loc = bullet.location;
//            float speed = bullet.speed;
//            float dist0 = myLocation.distanceTo(loc);
//
//            if(dist0 > RELEVANCE__RADIUS + 3*speed){continue;}
//
//
//            Direction originalEnemyDir = bullet.originalEnemyDir;
//            MapLocation loc1 = loc.add(originalEnemyDir, speed);
//
//
//            float dist1 = myLocation.distanceTo(loc1);
//
//            if (dist1 > dist0 + 0.5f) {
//
////                if(player1) {
////                    //rc.setIndicatorLine(loc, loc1, 255, 255, 255);
////                }
//                //The bullet is flying away from us
//                if (dist0 <= RELEVANCE__RADIUS) {
//                    zones[zoneLoc] = new DesireZone(loc.add(originalEnemyDir,speed/2), AVOID_RADIUS + speed/2, dangerFirst + -10 * bullet.damage, true);
//                    zoneLoc++;
//                    // if(player1) {
//                    //  //rc.setIndicatorLine(loc, loc1, 255, 30, 255);
//                    //   }
//                }
//
//            } else {
//
//                float l0x = loc.x;
//                float l1x = loc1.x;
//                float l0y = loc.y;
//                float l1y = loc1.y;
//                MapLocation loc2 = loc1.add(originalEnemyDir, speed);
//                float l2x = loc2.x;
//                float l2y = loc2.y;
//
//
//                if(dist1 < LINE_RELEVANCE_RADIUS){
//                    if(MAX_REL_X > l0x || MIN_REL_X < l1x || MAX_REL_X > l1x ||  MIN_REL_X < l0x ||  MAX_REL_Y > l0y || MAX_REL_Y > l1y || MIN_REL_Y < l0y || MIN_REL_Y < l1y){
//                        zones[zoneLoc] = new DesireZone(loc, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,dangerFirst + -10 * bullet.damage);
//                        zoneLoc++;
//
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS), 10);
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS), 10);
//
//
////                        //rc.setIndicatorLine(loc, loc1, 220, 160,40);
////                        if(myId % 2== 0) {
////                            MapLocation m1 = loc.add(originalEnemyDir, -AVOID_RADIUS).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS);
////                            MapLocation m2 = loc.add(originalEnemyDir, -AVOID_RADIUS).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS);
////                            MapLocation m3 = loc1.add(originalEnemyDir, AVOID_RADIUS).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS);
////                            MapLocation m4 = loc1.add(originalEnemyDir, AVOID_RADIUS).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS);
////                            //rc.setIndicatorLine(m1, m2, 255, 0, 0);
////                            //rc.setIndicatorLine(m1, m3, 255, 0, 0);
////                            //rc.setIndicatorLine(m4, m2, 255, 0, 0);
////                            //rc.setIndicatorLine(m4, m3, 255, 0, 0);
////                        }
//
//                    }
//
//
//
//                    if(dist1 < LINE_RELEVANCE_RADIUS_2ND){
//                        if(MAX_REL_X_2ND > l2x || MIN_REL_X_2ND < l1x || MAX_REL_X_2ND > l1x ||  MIN_REL_X_2ND < l2x  || MAX_REL_Y_2ND > l2y || MAX_REL_Y_2ND > l1y || MIN_REL_Y_2ND < l2y || MIN_REL_Y_2ND < l1y){
//                            zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,danger2ND + -10 * bullet.damage);
//                            zoneLoc++;
//                            addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                            addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//
//
//                            //  //rc.setIndicatorLine(loc1, loc2, 80, 250,60);
//
////                            if(myId % 2== 0) {
////                                MapLocation m1 = loc1.add(originalEnemyDir, -AVOID_RADIUS_2ND).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_2ND);
////                                MapLocation m2 = loc1.add(originalEnemyDir, -AVOID_RADIUS_2ND).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_2ND);
////                                MapLocation m3 = loc2.add(originalEnemyDir, AVOID_RADIUS_2ND).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_2ND);
////                                MapLocation m4 = loc2.add(originalEnemyDir, AVOID_RADIUS_2ND).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_2ND);
////                                //rc.setIndicatorLine(m1, m2, 0, 0, 120);
////                                //rc.setIndicatorLine(m1, m3, 0, 0, 120);
////                                //rc.setIndicatorLine(m4, m2, 0, 0, 120);
////                                //rc.setIndicatorLine(m4, m3, 0, 0, 120);
////                            }
//
//                        }
//                    }else if(myLocation.distanceTo(loc2) < LINE_RELEVANCE_RADIUS_2ND){
//                        if(MAX_REL_X_2ND > l2x || MIN_REL_X_2ND < l1x || MAX_REL_X_2ND > l1x ||  MIN_REL_X_2ND < l2x  || MAX_REL_Y_2ND > l2y || MAX_REL_Y_2ND > l1y || MIN_REL_Y_2ND < l2y || MIN_REL_Y_2ND < l1y){
//                            zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,danger2ND + -10 * bullet.damage);
//                            zoneLoc++;
//
//                            addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                            addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//
//                            // //rc.setIndicatorLine(loc1, loc2, 80, 250,60);
//
////                            if(myId % 2== 0) {
////                                MapLocation m1 = loc1.add(originalEnemyDir, -AVOID_RADIUS_2ND).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_2ND);
////                                MapLocation m2 = loc1.add(originalEnemyDir, -AVOID_RADIUS_2ND).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_2ND);
////                                MapLocation m3 = loc2.add(originalEnemyDir, AVOID_RADIUS_2ND).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_2ND);
////                                MapLocation m4 = loc2.add(originalEnemyDir, AVOID_RADIUS_2ND).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_2ND);
////                                //rc.setIndicatorLine(m1, m2, 0, 0, 120);
////                                //rc.setIndicatorLine(m1, m3, 0, 0, 120);
////                                //rc.setIndicatorLine(m4, m2, 0, 0, 120);
////                                //rc.setIndicatorLine(m4, m3, 0, 0, 120);
////                            }
//                        }
//                    }
//                }
//                else if(dist0 < LINE_RELEVANCE_RADIUS){
//                    if(MAX_REL_X > l0x || MIN_REL_X < l1x || MAX_REL_X > l1x ||  MIN_REL_X < l0x  || MAX_REL_Y > l0y || MAX_REL_Y > l1y || MIN_REL_Y < l0y || MIN_REL_Y < l1y){
//                        zones[zoneLoc] = new DesireZone(loc, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,dangerFirst + -10 * bullet.damage);
//                        zoneLoc++;
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS), 10);
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS), 10);
//
//
////                        //rc.setIndicatorLine(loc, loc1, 220, 160,40);
//
////                        if(myId % 2== 0) {
////                            MapLocation m1 = loc.add(originalEnemyDir, -AVOID_RADIUS).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS);
////                            MapLocation m2 = loc.add(originalEnemyDir, -AVOID_RADIUS).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS);
////                            MapLocation m3 = loc1.add(originalEnemyDir, AVOID_RADIUS).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS);
////                            MapLocation m4 = loc1.add(originalEnemyDir, AVOID_RADIUS).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS);
////                            //rc.setIndicatorLine(m1, m2, 255, 0, 0);
////                            //rc.setIndicatorLine(m1, m3, 255, 0, 0);
////                            //rc.setIndicatorLine(m4, m2, 255, 0, 0);
////                            //rc.setIndicatorLine(m4, m3, 255, 0, 0);
////                        }
//
//                    }
//                } else if(myLocation.distanceTo(loc2) < LINE_RELEVANCE_RADIUS_2ND){
//                    if(MAX_REL_X_2ND > l2x || MIN_REL_X_2ND < l1x || MAX_REL_X_2ND > l1x ||  MIN_REL_X_2ND < l2x  || MAX_REL_Y_2ND > l2y || MAX_REL_Y_2ND > l1y || MIN_REL_Y_2ND < l2y || MIN_REL_Y_2ND < l1y){
//                        zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,danger2ND + -10 * bullet.damage);
//                        zoneLoc++;
//                        addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                        addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//
//
////                        //rc.setIndicatorLine(loc1, loc2, 80, 250,60);
//
//
////                        if(myId % 2== 0) {
////                            MapLocation m1 = loc1.add(originalEnemyDir, -AVOID_RADIUS_2ND).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_2ND);
////                            MapLocation m2 = loc1.add(originalEnemyDir, -AVOID_RADIUS_2ND).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_2ND);
////                            MapLocation m3 = loc2.add(originalEnemyDir, AVOID_RADIUS_2ND).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_2ND);
////                            MapLocation m4 = loc2.add(originalEnemyDir, AVOID_RADIUS_2ND).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_2ND);
////                            //rc.setIndicatorLine(m1, m2, 0, 0, 120);
////                            //rc.setIndicatorLine(m1, m3, 0, 0, 120);
////                            //rc.setIndicatorLine(m4, m2, 0, 0, 120);
////                            //rc.setIndicatorLine(m4, m3, 0, 0, 120);
////                        }
//                    }
//                }
//
//
////                MapLocation loc35 = loc2.add(originalEnemyDir, speed * 1.5f);
////                if(myLocation.distanceTo(loc35) < RELEVANCE__RADIUS_2ND){
////                    zones[zoneLoc] = new DesireZone(loc35,AVOID_RADIUS_2ND,-15);
////                    zoneLoc++;
////                    //rc.setIndicatorDot(loc35,0,0,255);
////                }
//
//
//
//                MapLocation loc3 = loc2.add(originalEnemyDir,speed);
//
//
//
////                if(dist0 < LINE_RELEVANCE_RADIUS || dist1 < LINE_RELEVANCE_RADIUS){
////                    if(MAX_REL_X > l0x || MAX_REL_X > l1x ||  MIN_REL_X < l0x || MIN_REL_X < l1x || MAX_REL_Y > l0y || MAX_REL_Y > l1y || MIN_REL_Y < l0y || MIN_REL_Y < l1y){
////                        zones[zoneLoc] = new DesireZone(loc, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,-230 + -10 * bullet.damage);
////                        zoneLoc++;
////                    }
////                }
////                if(myLocation.distanceTo(loc2) < LINE_RELEVANCE_RADIUS_2ND || dist1 < LINE_RELEVANCE_RADIUS_2ND){
////                    if(MAX_REL_X_2ND > l2x || MAX_REL_X_2ND > l1x ||  MIN_REL_X_2ND < l2x || MIN_REL_X_2ND < l1x || MAX_REL_Y_2ND > l2y || MAX_REL_Y_2ND > l1y || MIN_REL_Y_2ND < l2y || MIN_REL_Y_2ND < l1y){
////                        zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,-80 + -10 * bullet.damage);
////                        zoneLoc++;
////                    }
////                }
//
//
//                if(myLocation.distanceTo(loc3) < LINE_RELEVANCE_RADIUS_3RD || myLocation.distanceTo(loc2) < LINE_RELEVANCE_RADIUS_3RD){
//                    if(MAX_REL_X_3RD > l2x || MAX_REL_X_3RD > l1x ||  MIN_REL_X_3RD < l2x || MIN_REL_X_3RD < l1x || MAX_REL_Y_3RD > l2y || MAX_REL_Y_3RD > l1y || MIN_REL_Y_3RD < l2y || MIN_REL_Y_3RD < l1y){
//                        zones[zoneLoc] = new DesireZone(loc2, originalEnemyDir,speed+ AVOID_RADIUS_3RD,AVOID_RADIUS_3RD,-20 + -10 * bullet.damage);
//                        zoneLoc++;
//
//                        // //rc.setIndicatorLine(loc2, loc3, 100, 40,60);
//
////                          if(myId % 2== 0 && player1) {
////                            MapLocation m1 = loc2.add(originalEnemyDir, -AVOID_RADIUS_3RD).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_3RD);
////                            MapLocation m2 = loc2.add(originalEnemyDir, -AVOID_RADIUS_3RD).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_3RD);
////                            MapLocation m3 = loc3.add(originalEnemyDir, AVOID_RADIUS_3RD).add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_3RD);
////                            MapLocation m4 = loc3.add(originalEnemyDir, AVOID_RADIUS_3RD).add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_3RD);
////                            //rc.setIndicatorLine(m1, m2, 0, 0, 120);
////                            //rc.setIndicatorLine(m1, m3, 0, 0, 120);
////                            //rc.setIndicatorLine(m4, m2, 0, 0, 120);
////                            //rc.setIndicatorLine(m4, m3, 0, 0, 120);
////                        }
//                    }
//                }
//
//            }
//        }
//    }
//
//
//
//    public static void dodgeBulletsNewOptimized(){
//
//
//        MAX_REL_X =  myX + RELEVANCE__RADIUS;
//        MIN_REL_X =  myX - RELEVANCE__RADIUS;
//        MAX_REL_Y =  myY + RELEVANCE__RADIUS;
//        MIN_REL_Y =  myY - RELEVANCE__RADIUS;
//
//        MAX_REL_X_2ND =  MAX_REL_X - DIMINISH_2ND;
//        MIN_REL_X_2ND =  MIN_REL_X + DIMINISH_2ND;
//        MAX_REL_Y_2ND =  MAX_REL_Y - DIMINISH_2ND;
//        MIN_REL_Y_2ND =  MIN_REL_Y + DIMINISH_2ND;
//
//        MAX_REL_X_3RD =  MAX_REL_X - DIMINISH_3RD;
//        MIN_REL_X_3RD =  MIN_REL_X + DIMINISH_3RD;
//        MAX_REL_Y_3RD =  MAX_REL_Y - DIMINISH_3RD;
//        MIN_REL_Y_3RD =  MIN_REL_Y + DIMINISH_3RD;
//
//
//        bullets = rc.senseNearbyBullets(MAX_BULLET_DETECT_CHECK);
//        int maxChecks = bullets.length - 1;
//        if(maxChecks > 20) maxChecks = 20;
//
//        for (int i = maxChecks; i>=0; i--) {
//            BulletInfo bullet = bullets[i];
//            MapLocation loc = bullet.location;
//            float speed = bullet.speed;
//            float dist0 = myLocation.distanceTo(loc);
//            float dmgscore = -10 * bullet.damage;
//
//            Direction originalEnemyDir = bullet.originalEnemyDir;
//            MapLocation loc1 = loc.add(originalEnemyDir, speed);
//
//            float dist1 = myLocation.distanceTo(loc1);
//
//            if (dist1 > dist0 + 0.5f) {
//                //The bullet is flying away from us
//                if (dist0 <= RELEVANCE__RADIUS) {
//                    zones[zoneLoc] = new DesireZone(loc.add(originalEnemyDir,speed/2), AVOID_RADIUS + speed/2, DANGER_FIRST + dmgscore, true);
//                    zoneLoc++;
//                }
//            } else {
//
//                MapLocation loc2 = loc1.add(originalEnemyDir, speed);
//
//                float dist2= myLocation.distanceTo(loc2);
//                MapLocation loc3 = loc2.add(originalEnemyDir, speed);
//
//
//                if(dist1 < LINE_RELEVANCE_RADIUS){
//                    float l0x = loc.x;
//                    float l1x = loc1.x;
//                    float l0y = loc.y;
//                    float l1y = loc1.y;
//
//
//
//
//
//
//
//                    if(MAX_REL_X > l0x || MIN_REL_X < l1x || MAX_REL_X > l1x ||  MIN_REL_X < l0x ||  MAX_REL_Y > l0y || MAX_REL_Y > l1y || MIN_REL_Y < l0y || MIN_REL_Y < l1y){
//                        zones[zoneLoc] = new DesireZone(loc, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,DANGER_FIRST + dmgscore);
//                        zoneLoc++;
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS), 10);
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS), 10);
//
//                        //drawBullet(loc,loc1,originalEnemyDir, AVOID_RADIUS, 230,20,20);
//
//                        loc1 = loc1.add(originalEnemyDir,OVERLAP_1_2);
//                        dist1 = myLocation.distanceTo(loc1);
//                        l1x = loc1.x;
//                        l1y = loc1.y;
//
//
//                        ////rc.setIndicatorDot(loc1,255,0,0);
//                    }
//                    // else{
//                    // //rc.setIndicatorDot(loc1,255,255,0);
//
//                    //}
//
//                    if(dist1 < LINE_RELEVANCE_RADIUS_2ND || dist2 < LINE_RELEVANCE_RADIUS_2ND){
//                        float l2x = loc2.x;
//                        float l2y = loc2.y;
//                        if(MAX_REL_X_2ND > l2x || MIN_REL_X_2ND < l1x || MAX_REL_X_2ND > l1x ||  MIN_REL_X_2ND < l2x  || MAX_REL_Y_2ND > l2y || MAX_REL_Y_2ND > l1y || MIN_REL_Y_2ND < l2y || MIN_REL_Y_2ND < l1y){
//
//                            zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,DANGER_2ND + dmgscore);
//                            zoneLoc++;
//
//                            addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                            addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//
//                            //drawBullet(loc1,loc2 , originalEnemyDir, AVOID_RADIUS_2ND, 150,20,20);
//
//                        }
//                    }
//                }
//                else if(dist2 < LINE_RELEVANCE_RADIUS_2ND){
//                    float l1x = loc1.x;
//                    float l1y = loc1.y;
//                    float l2x = loc2.x;
//                    float l2y = loc2.y;
//                    if(MAX_REL_X_2ND > l2x || MIN_REL_X_2ND < l1x || MAX_REL_X_2ND > l1x ||  MIN_REL_X_2ND < l2x  || MAX_REL_Y_2ND > l2y || MAX_REL_Y_2ND > l1y || MIN_REL_Y_2ND < l2y || MIN_REL_Y_2ND < l1y){
//                        zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,DANGER_2ND + dmgscore);
//                        zoneLoc++;
//                        addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                        addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//
//
//                        //drawBullet(loc1,loc2 , originalEnemyDir, AVOID_RADIUS_2ND, 150,20,20);
//
//                        loc2 = loc2.add(originalEnemyDir,OVERLAP_2_3);
//                        dist2 = myLocation.distanceTo(loc2);
//                        l2x = loc2.x;
//                        l2y = loc2.y;
//                    }
//
//                    if(myLocation.distanceTo(loc3) < LINE_RELEVANCE_RADIUS_3RD || dist2 < LINE_RELEVANCE_RADIUS_3RD){
//                        float l3x = loc3.x;
//                        float l3y = loc3.y;
//                        if(MAX_REL_X_3RD > l2x || MAX_REL_X_3RD > l3x ||  MIN_REL_X_3RD < l2x || MIN_REL_X_3RD < l3x || MAX_REL_Y_3RD > l2y || MAX_REL_Y_3RD > l3y || MIN_REL_Y_3RD < l2y || MIN_REL_Y_3RD < l3y){
//                            zones[zoneLoc] = new DesireZone(loc2, originalEnemyDir,speed+ AVOID_RADIUS_3RD,AVOID_RADIUS_3RD,DANGER_3RD + dmgscore);
//                            zoneLoc++;
//
//                            //drawBullet(loc2,loc3 , originalEnemyDir, AVOID_RADIUS_3RD, 70,20,20);
//                        }
//                    }
//                }
//                else if(dist0 < LINE_RELEVANCE_RADIUS){
//                    float l0x = loc.x;
//                    float l1x = loc1.x;
//                    float l0y = loc.y;
//                    float l1y = loc1.y;
//                    if(MAX_REL_X > l0x || MIN_REL_X < l1x || MAX_REL_X > l1x ||  MIN_REL_X < l0x  || MAX_REL_Y > l0y || MAX_REL_Y > l1y || MIN_REL_Y < l0y || MIN_REL_Y < l1y){
//                        zones[zoneLoc] = new DesireZone(loc, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,DANGER_FIRST + dmgscore);
//                        zoneLoc++;
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS), 10);
//                        addSpecialMapLocation(loc1.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS), 10);
//
//                        // drawBullet(loc,loc1 , originalEnemyDir, AVOID_RADIUS, 230,20,20);
//                    }
//                }  else if(myLocation.distanceTo(loc3) < LINE_RELEVANCE_RADIUS_3RD ){
//                    float l2x = loc2.x;
//                    float l2y = loc2.y;
//                    float l3x = loc3.x;
//                    float l3y = loc3.y;
//                    if(MAX_REL_X_3RD > l2x || MAX_REL_X_3RD > l3x ||  MIN_REL_X_3RD < l2x || MIN_REL_X_3RD < l3x || MAX_REL_Y_3RD > l2y || MAX_REL_Y_3RD > l3y || MIN_REL_Y_3RD < l2y || MIN_REL_Y_3RD < l3y){
//                        zones[zoneLoc] = new DesireZone(loc2, originalEnemyDir,speed+ AVOID_RADIUS_3RD,AVOID_RADIUS_3RD,DANGER_3RD + dmgscore);
//                        zoneLoc++;
//
//                        //drawBullet(loc2,loc3 , originalEnemyDir, AVOID_RADIUS_3RD, 70,20,20);
//                    }
//                }
//
//
//            }
//        }
//
//
//
//    }

//
//    public static void dodgeBulletsSomeIteration(){
//
//
//        MAX_REL_X =  myX + RELEVANCE__RADIUS;
//        MIN_REL_X =  myX - RELEVANCE__RADIUS;
//        MAX_REL_Y =  myY + RELEVANCE__RADIUS;
//        MIN_REL_Y =  myY - RELEVANCE__RADIUS;
//
//        MAX_REL_X_2ND =  MAX_REL_X - DIMINISH_2ND;
//        MIN_REL_X_2ND =  MIN_REL_X + DIMINISH_2ND;
//        MAX_REL_Y_2ND =  MAX_REL_Y - DIMINISH_2ND;
//        MIN_REL_Y_2ND =  MIN_REL_Y + DIMINISH_2ND;
//
//        MAX_REL_X_3RD =  MAX_REL_X - DIMINISH_3RD;
//        MIN_REL_X_3RD =  MIN_REL_X + DIMINISH_3RD;
//        MAX_REL_Y_3RD =  MAX_REL_Y - DIMINISH_3RD;
//        MIN_REL_Y_3RD =  MIN_REL_Y + DIMINISH_3RD;
//
//
//        bullets = rc.senseNearbyBullets(MAX_BULLET_DETECT_CHECK);
//
//
//        float left01;
//        float right01;
//        float top01;
//        float bot01;
//        float left12;
//        float right12;
//        float top12;
//        float bot12;
//        float left23;
//        float right23;
//        float top23;
//        float bot23;
//
//
//
//
//        int maxChecks = bullets.length - 1;
//        if(maxChecks > 20) maxChecks = 20;
//
//        for (int i = maxChecks; i>=0; i--) {
//            BulletInfo bullet = bullets[i];
//            MapLocation loc = bullet.location;
//            float speed = bullet.speed;
//            float dist0 = myLocation.distanceTo(loc);
//            float dmgscore = -10 * bullet.damage;
//
//            Direction originalEnemyDir = bullet.originalEnemyDir;
//            MapLocation loc1 = loc.add(originalEnemyDir, speed);
//
//            float dist1 = myLocation.distanceTo(loc1);
//
//            if (dist1 > dist0 + 0.5f) {
//                //The bullet is flying away from us
//                if (dist0 <= RELEVANCE__RADIUS) {
//                    zones[zoneLoc] = new DesireZone(loc.add(originalEnemyDir,speed/2), AVOID_RADIUS + speed/2, DANGER_FIRST + dmgscore, true);
//                    zoneLoc++;
//                }
//            } else {
//
//                boolean do01 = false;
//                boolean do12 = false;
//                boolean do23 = false;
//
//                boolean goingRight;
//                boolean goingUp;
//                if(loc1.x > loc.x){
//                    goingRight = true;
//                }else{
//                    goingRight = false;
//                }
//                if(loc1.y > loc.y){
//                    goingUp = true;
//                }else{
//                    goingUp = false;
//                }
//
//
//
//                MapLocation loc2 = loc1.add(originalEnemyDir, speed);
//                float dist2= myLocation.distanceTo(loc2);
//                MapLocation loc3 = loc2.add(originalEnemyDir, speed);
//                float dist3 = myLocation.distanceTo(loc3);
//
//
//                if(dist1 < LINE_RELEVANCE_RADIUS){
//
//                    if(dist1 < RELEVANCE__RADIUS){
//                        do01 = true;
//                        loc1 = loc1.add(originalEnemyDir, OVERLAP_1_2);
//                        dist1 = myLocation.distanceTo(loc1);
//                    }
//                    else {
//                        if (goingRight) {
//                            left01 = loc.x;
//                            right01 = loc1.x;
//                        } else {
//                            left01 = loc1.x;
//                            right01 = loc.x;
//                        }
//                        if (goingUp) {
//                            bot01 = loc.y;
//                            top01 = loc1.y;
//                        } else {
//                            bot01 = loc1.y;
//                            top01 = loc.y;
//                        }
//
//                        if (((myX >= left01 && myX <= right01) && ((MIN_REL_Y >= bot01 && MIN_REL_Y <= top01) || (MAX_REL_Y >= bot01 && MAX_REL_Y <= top01) || (myY >= bot01 && myY <= top01))) || ((myY >= bot01 && myY <= top01) && ((MIN_REL_X >= left01 && MIN_REL_X <= right01) || (MAX_REL_X >= left01 && MAX_REL_X <= right01)))) {
//                            do01 = true;
//                            loc1 = loc1.add(originalEnemyDir, OVERLAP_1_2);
//                            dist1 = myLocation.distanceTo(loc1);
//                        }
//                    }
//
//                    if(dist1 < LINE_RELEVANCE_RADIUS_2ND || dist2 < LINE_RELEVANCE_RADIUS_2ND){
//                        if(dist1 < RELEVANCE__RADIUS_2ND || dist2 < RELEVANCE__RADIUS_2ND){
//                            do12 = true;
//                        }else {
//                            if (goingRight) {
//                                left12 = loc1.x;
//                                right12 = loc2.x;
//                            } else {
//                                left12 = loc2.x;
//                                right12 = loc1.x;
//                            }
//                            if (goingUp) {
//                                bot12 = loc1.y;
//                                top12 = loc2.y;
//                            } else {
//                                bot12 = loc2.y;
//                                top12 = loc1.y;
//                            }
//                            if (((myX >= left12 && myX <= right12) && ((MIN_REL_Y >= bot12 && MIN_REL_Y <= top12) || (MAX_REL_Y >= bot12 && MAX_REL_Y <= top12) || (myY >= bot12 && myY <= top12))) || ((myY >= bot12 && myY <= top12) && ((MIN_REL_X >= left12 && MIN_REL_X <= right12) || (MAX_REL_X >= left12 && MAX_REL_X <= right12)))) {
//                                do12 = true;
//                            }
//                        }
//                    }
//                }
//                else if(dist2 < LINE_RELEVANCE_RADIUS_2ND){
//                    if(dist2 < RELEVANCE__RADIUS_2ND){
//                        do23 = true;
//                        loc2 = loc2.add(originalEnemyDir, OVERLAP_2_3);
//                        dist2 = myLocation.distanceTo(loc2);
//                    }
//                    else {
//                        if (goingRight) {
//                            left12 = loc1.x;
//                            right12 = loc2.x;
//                        } else {
//                            left12 = loc2.x;
//                            right12 = loc1.x;
//                        }
//                        if (goingUp) {
//                            bot12 = loc1.y;
//                            top12 = loc2.y;
//                        } else {
//                            bot12 = loc2.y;
//                            top12 = loc1.y;
//                        }
//                        if (((myX >= left12 && myX <= right12) && ((MIN_REL_Y >= bot12 && MIN_REL_Y <= top12) || (MAX_REL_Y >= bot12 && MAX_REL_Y <= top12) || (myY >= bot12 && myY <= top12))) || ((myY >= bot12 && myY <= top12) && ((MIN_REL_X >= left12 && MIN_REL_X <= right12) || (MAX_REL_X >= left12 && MAX_REL_X <= right12)))) {
//                            do23 = true;
//                            loc2 = loc2.add(originalEnemyDir, OVERLAP_2_3);
//                            dist2 = myLocation.distanceTo(loc2);
//                        }
//                    }
//
//                    if(dist3 < LINE_RELEVANCE_RADIUS_3RD || dist2 < LINE_RELEVANCE_RADIUS_3RD){
//                        if(dist3 < RELEVANCE__RADIUS_3RD || dist2 < RELEVANCE__RADIUS_2ND){
//                            do23 = true;
//                        }else {
//                            if (goingRight) {
//                                left23 = loc1.x;
//                                right23 = loc2.x;
//                            } else {
//                                left23 = loc2.x;
//                                right23 = loc1.x;
//                            }
//                            if (goingUp) {
//                                bot23 = loc1.y;
//                                top23 = loc2.y;
//                            } else {
//                                bot23 = loc2.y;
//                                top23 = loc1.y;
//                            }
//                            if (((myX >= left23 && myX <= right23) && ((MIN_REL_Y >= bot23 && MIN_REL_Y <= top23) || (MAX_REL_Y >= bot23 && MAX_REL_Y <= top23) || (myY >= bot23 && myY <= top23))) || ((myY >= bot23 && myY <= top23) && ((MIN_REL_X >= left23 && MIN_REL_X <= right23) || (MAX_REL_X >= left23 && MAX_REL_X <= right23)))) {
//                                do23 = true;
//                            }
//                        }
//                    }
//                }
//                else if(dist0 < LINE_RELEVANCE_RADIUS){
//                    if(dist0 < RELEVANCE__RADIUS){
//                        do01 = true;
//                    }else {
//                        if (goingRight) {
//                            left01 = loc.x;
//                            right01 = loc1.x;
//                        } else {
//                            left01 = loc1.x;
//                            right01 = loc.x;
//                        }
//                        if (goingUp) {
//                            bot01 = loc.y;
//                            top01 = loc1.y;
//                        } else {
//                            bot01 = loc1.y;
//                            top01 = loc.y;
//                        }
//                        if (((myX >= left01 && myX <= right01) && ((MIN_REL_Y >= bot01 && MIN_REL_Y <= top01) || (MAX_REL_Y >= bot01 && MAX_REL_Y <= top01) || (myY >= bot01 && myY <= top01))) || ((myY >= bot01 && myY <= top01) && ((MIN_REL_X >= left01 && MIN_REL_X <= right01) || (MAX_REL_X >= left01 && MAX_REL_X <= right01) ))) {
//                            do01 = true;
//                        }
//                    }
//                }  else if(dist3 < LINE_RELEVANCE_RADIUS_3RD ){
//                    if(dist3 < RELEVANCE__RADIUS_3RD) {
//                        do23 = true;
//                    }else {
//                        if (goingRight) {
//                            left23 = loc1.x;
//                            right23 = loc2.x;
//                        } else {
//                            left23 = loc2.x;
//                            right23 = loc1.x;
//                        }
//                        if (goingUp) {
//                            bot23 = loc1.y;
//                            top23 = loc2.y;
//                        } else {
//                            bot23 = loc2.y;
//                            top23 = loc1.y;
//                        }
//                        if (((myX >= left23 && myX <= right23) && ((MIN_REL_Y >= bot23 && MIN_REL_Y <= top23) || (MAX_REL_Y >= bot23 && MAX_REL_Y <= top23) || (myY >= bot23 && myY <= top23))) || ((myY >= bot23 && myY <= top23) && ((MIN_REL_X >= left23 && MIN_REL_X <= right23) || (MAX_REL_X >= left23 && MAX_REL_X <= right23) ))) {
//                            do23 = true;
//                        }
//                    }
//                }
//
//
//                if(do01){
//                    zones[zoneLoc] = new DesireZone(loc, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,DANGER_FIRST + dmgscore);
//                    zoneLoc++;
//                    addSpecialMapLocation(loc1.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS), 10);
//                    addSpecialMapLocation(loc1.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS), 10);
//                    drawBullet(loc,loc1 , originalEnemyDir, AVOID_RADIUS, 230,20,20);
//                }
//                if(do12){
//                    zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,DANGER_2ND + dmgscore);
//                    zoneLoc++;
//
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//                    drawBullet(loc1,loc2 , originalEnemyDir, AVOID_RADIUS_2ND, 150,20,20);
//
//                }
//                if(do23){
//                    zones[zoneLoc] = new DesireZone(loc2, originalEnemyDir,speed+ AVOID_RADIUS_3RD,AVOID_RADIUS_3RD,DANGER_3RD + dmgscore);
//                    zoneLoc++;
//                    drawBullet(loc2,loc3 , originalEnemyDir, AVOID_RADIUS_3RD, 70,20,20);
//                }
//
//
//            }
//        }
//
//
//
//    }
//
//
//    public static void dodgeBulletsStartCircle() throws Exception{
//
//
//        MAX_REL_X =  myX + RELEVANCE__RADIUS;
//        MIN_REL_X =  myX - RELEVANCE__RADIUS;
//        MAX_REL_Y =  myY + RELEVANCE__RADIUS;
//        MIN_REL_Y =  myY - RELEVANCE__RADIUS;
//
//        MAX_REL_X_2ND =  MAX_REL_X - DIMINISH_2ND;
//        MIN_REL_X_2ND =  MIN_REL_X + DIMINISH_2ND;
//        MAX_REL_Y_2ND =  MAX_REL_Y - DIMINISH_2ND;
//        MIN_REL_Y_2ND =  MIN_REL_Y + DIMINISH_2ND;
//
//        MAX_REL_X_3RD =  MAX_REL_X - DIMINISH_3RD;
//        MIN_REL_X_3RD =  MIN_REL_X + DIMINISH_3RD;
//        MAX_REL_Y_3RD =  MAX_REL_Y - DIMINISH_3RD;
//        MIN_REL_Y_3RD =  MIN_REL_Y + DIMINISH_3RD;
//
//
//        bullets = rc.senseNearbyBullets(MAX_BULLET_DETECT_CHECK);
//
//
//        float left01;
//        float right01;
//        float top01;
//        float bot01;
//        float left12;
//        float right12;
//        float top12;
//        float bot12;
//        float left23;
//        float right23;
//        float top23;
//        float bot23;
//
//
//
//
//        int maxChecks = bullets.length - 1;
//        if(maxChecks > 20) maxChecks = 20;
//
//        for (int i = maxChecks; i>=0; i--) {
//            BulletInfo bullet = bullets[i];
//            MapLocation loc = bullet.location;
//            float speed = bullet.speed;
//            float dist0 = myLocation.distanceTo(loc);
//            float dmgscore = -10 * bullet.damage;
//
//            Direction originalEnemyDir = bullet.originalEnemyDir;
//            MapLocation loc1 = loc.add(originalEnemyDir, speed);
//
//            float dist1 = myLocation.distanceTo(loc1);
//
//            if (dist1 > dist0 + 0.5f) {
//                //The bullet is flying away from us
//                if (dist0 <= RELEVANCE__RADIUS) {
//                    zones[zoneLoc] = new DesireZone(loc.add(originalEnemyDir,speed/2), AVOID_RADIUS + speed/2, DANGER_FIRST + dmgscore, true);
//                    zoneLoc++;
//                }
//            } else {
//
//                boolean do01 = false;
//                boolean do12 = false;
//                boolean do23 = false;
//
//                boolean goingRight;
//                boolean goingUp;
//                if(loc1.x > loc.x){
//                    goingRight = true;
//                }else{
//                    goingRight = false;
//                }
//                if(loc1.y > loc.y){
//                    goingUp = true;
//                }else{
//                    goingUp = false;
//                }
//
//
//                MapLocation loc05 = loc.add(originalEnemyDir, AVOID_RADIUS);
//                float dist05= myLocation.distanceTo(loc05);
//
//
//                MapLocation loc2 = loc1.add(originalEnemyDir, speed);
//                float dist2= myLocation.distanceTo(loc2);
//                MapLocation loc3 = loc2.add(originalEnemyDir, speed);
//                float dist3 = myLocation.distanceTo(loc3);
//
//
//
//
//
//                if(dist0 < RELEVANCE__RADIUS){
//                    addCircleZone(loc,AVOID_RADIUS, DANGER_FIRST  + dmgscore);
//                    //rc.setIndicatorDot(loc, 0,0,0);
//                    at1++;
//                }
//
//                if(rc.canSenseLocation(loc1) && rc.isLocationOccupiedByTree(loc1)){
//                    //Don't have to dodge shots fired at trees
//
//                    addCircleZone(loc.add(originalEnemyDir,speed / 2),AVOID_RADIUS + 0.8f, DANGER_FIRST  + dmgscore);
//                    return;
//                }
//
//
//                if(dist1 < LINE_RELEVANCE_RADIUS){
//
//                    if(dist1 < RELEVANCE__RADIUS){
//                        do01 = true;
//                        loc1 = loc1.add(originalEnemyDir, OVERLAP_1_2);
//                        dist1 = myLocation.distanceTo(loc1);
//                        at2++;
//                    }
//                    else {
//                        q++;
//                        if (goingRight) {
//                            left01 = loc05.x;
//                            right01 = loc1.x;
//                        } else {
//                            left01 = loc1.x;
//                            right01 = loc05.x;
//                        }
//                        if (goingUp) {
//                            bot01 = loc05.y;
//                            top01 = loc1.y;
//                        } else {
//                            bot01 = loc1.y;
//                            top01 = loc05.y;
//                        }
//
//                        Test.log("bot: " + bot01 + " t:" + top01 +  " l:" + left01 + " r: " + right01   +"    x: " + myX +  "  y: " + myY    +  " minx: " + MIN_REL_X  + " maxx: " + MAX_REL_X  + "  miny: " + MIN_REL_Y  + " maxy: " + MAX_REL_Y  );
//
//                        if (((myX >= left01 && myX <= right01) && ((MIN_REL_Y >= bot01 && MIN_REL_Y <= top01) || (MAX_REL_Y >= bot01 && MAX_REL_Y <= top01) || (myY >= bot01 && myY <= top01))) || ((myY >= bot01 && myY <= top01) && ((MIN_REL_X >= left01 && MIN_REL_X <= right01) || (MAX_REL_X >= left01 && MAX_REL_X <= right01)))) {
//                            do01 = true;
//                            loc1 = loc1.add(originalEnemyDir, OVERLAP_1_2);
//                            dist1 = myLocation.distanceTo(loc1);
//                            at3++;
//                        }
//                    }
//
//                    if(dist1 < LINE_RELEVANCE_RADIUS_2ND || dist2 < LINE_RELEVANCE_RADIUS_2ND){
//                        if(dist1 < RELEVANCE__RADIUS_2ND || dist2 < RELEVANCE__RADIUS_2ND){
//                            do12 = true;
//                            at4++;
//                        }else {
//                            if (goingRight) {
//                                left12 = loc1.x;
//                                right12 = loc2.x;
//                            } else {
//                                left12 = loc2.x;
//                                right12 = loc1.x;
//                            }
//                            if (goingUp) {
//                                bot12 = loc1.y;
//                                top12 = loc2.y;
//                            } else {
//                                bot12 = loc2.y;
//                                top12 = loc1.y;
//                            }
//                            if (((myX >= left12 && myX <= right12) && ((MIN_REL_Y >= bot12 && MIN_REL_Y <= top12) || (MAX_REL_Y >= bot12 && MAX_REL_Y <= top12) || (myY >= bot12 && myY <= top12))) || ((myY >= bot12 && myY <= top12) && ((MIN_REL_X >= left12 && MIN_REL_X <= right12) || (MAX_REL_X >= left12 && MAX_REL_X <= right12)))) {
//                                do12 = true;
//                                at5++;
//                            }
//                        }
//                    }
//                }
//                else if(dist2 < LINE_RELEVANCE_RADIUS_2ND){
//                    if(dist2 < RELEVANCE__RADIUS_2ND){
//                        do23 = true;
//                        loc2 = loc2.add(originalEnemyDir, OVERLAP_2_3);
//                        dist2 = myLocation.distanceTo(loc2);
//                        at6++;
//                    }
//                    else {
//                        if (goingRight) {
//                            left12 = loc1.x;
//                            right12 = loc2.x;
//                        } else {
//                            left12 = loc2.x;
//                            right12 = loc1.x;
//                        }
//                        if (goingUp) {
//                            bot12 = loc1.y;
//                            top12 = loc2.y;
//                        } else {
//                            bot12 = loc2.y;
//                            top12 = loc1.y;
//                        }
//                        if (((myX >= left12 && myX <= right12) && ((MIN_REL_Y >= bot12 && MIN_REL_Y <= top12) || (MAX_REL_Y >= bot12 && MAX_REL_Y <= top12) || (myY >= bot12 && myY <= top12))) || ((myY >= bot12 && myY <= top12) && ((MIN_REL_X >= left12 && MIN_REL_X <= right12) || (MAX_REL_X >= left12 && MAX_REL_X <= right12)))) {
//                            do23 = true;
//                            loc2 = loc2.add(originalEnemyDir, OVERLAP_2_3);
//                            dist2 = myLocation.distanceTo(loc2);
//                            at7++;
//
//                        }
//                    }
//
//                    if(dist3 < LINE_RELEVANCE_RADIUS_3RD || dist2 < LINE_RELEVANCE_RADIUS_3RD){
//                        if(dist3 < RELEVANCE__RADIUS_3RD || dist2 < RELEVANCE__RADIUS_2ND){
//                            do23 = true;
//                            at8++;
//                        }else {
//                            if (goingRight) {
//                                left23 = loc1.x;
//                                right23 = loc2.x;
//                            } else {
//                                left23 = loc2.x;
//                                right23 = loc1.x;
//                            }
//                            if (goingUp) {
//                                bot23 = loc1.y;
//                                top23 = loc2.y;
//                            } else {
//                                bot23 = loc2.y;
//                                top23 = loc1.y;
//                            }
//                            if (((myX >= left23 && myX <= right23) && ((MIN_REL_Y >= bot23 && MIN_REL_Y <= top23) || (MAX_REL_Y >= bot23 && MAX_REL_Y <= top23) || (myY >= bot23 && myY <= top23))) || ((myY >= bot23 && myY <= top23) && ((MIN_REL_X >= left23 && MIN_REL_X <= right23) || (MAX_REL_X >= left23 && MAX_REL_X <= right23)))) {
//                                do23 = true;
//                                at9++;
//                            }
//                        }
//                    }
//                }
//                else if(dist05 < LINE_RELEVANCE_RADIUS){
//                    if(dist05 < RELEVANCE__RADIUS){
//                        do01 = true;
//                        at10++;
//                    }else {
//                        if (goingRight) {
//                            left01 = loc05.x;
//                            right01 = loc1.x;
//                        } else {
//                            left01 = loc1.x;
//                            right01 = loc05.x;
//                        }
//                        if (goingUp) {
//                            bot01 = loc05.y;
//                            top01 = loc1.y;
//                        } else {
//                            bot01 = loc1.y;
//                            top01 = loc05.y;
//                        }
//                        if (((myX >= left01 && myX <= right01) && ((MIN_REL_Y >= bot01 && MIN_REL_Y <= top01) || (MAX_REL_Y >= bot01 && MAX_REL_Y <= top01) || (myY >= bot01 && myY <= top01))) || ((myY >= bot01 && myY <= top01) && ((MIN_REL_X >= left01 && MIN_REL_X <= right01) || (MAX_REL_X >= left01 && MAX_REL_X <= right01) ))) {
//                            do01 = true;
//                            at11++;
//                        }
//                    }
//                }  else if(dist3 < LINE_RELEVANCE_RADIUS_3RD ){
//                    if(dist3 < RELEVANCE__RADIUS_3RD) {
//                        do23 = true;
//                        at12++;
//                    }else {
//                        if (goingRight) {
//                            left23 = loc1.x;
//                            right23 = loc2.x;
//                        } else {
//                            left23 = loc2.x;
//                            right23 = loc1.x;
//                        }
//                        if (goingUp) {
//                            bot23 = loc1.y;
//                            top23 = loc2.y;
//                        } else {
//                            bot23 = loc2.y;
//                            top23 = loc1.y;
//                        }
//                        if (((myX >= left23 && myX <= right23) && ((MIN_REL_Y >= bot23 && MIN_REL_Y <= top23) || (MAX_REL_Y >= bot23 && MAX_REL_Y <= top23) || (myY >= bot23 && myY <= top23))) || ((myY >= bot23 && myY <= top23) && ((MIN_REL_X >= left23 && MIN_REL_X <= right23) || (MAX_REL_X >= left23 && MAX_REL_X <= right23) ))) {
//                            do23 = true;
//                            at13++;
//                        }
//                    }
//                }
//
//
//
//                if(do12){
//                    zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,DANGER_2ND + dmgscore);
//                    zoneLoc++;
//
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//                    drawBullet(loc1,loc2 , originalEnemyDir, AVOID_RADIUS_2ND, 150,20,20);
//
//                }
//                if(do01){
//                    zones[zoneLoc] = new DesireZone(loc05, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,DANGER_FIRST + dmgscore);
//                    zoneLoc++;
//                    addSpecialMapLocation(loc1.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS), 10);
//                    addSpecialMapLocation(loc1.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS), 10);
//                    drawBullet(loc05,loc1 , originalEnemyDir, AVOID_RADIUS, 230,20,20);
//                }
//
//                if(do23){
//                    zones[zoneLoc] = new DesireZone(loc2, originalEnemyDir,speed+ AVOID_RADIUS_3RD,AVOID_RADIUS_3RD,DANGER_3RD + dmgscore);
//                    zoneLoc++;
//                    drawBullet(loc2,loc3 , originalEnemyDir, AVOID_RADIUS_3RD, 70,20,20);
//                }
//
//
//                Test.log("[1]:" + at1  + "[2]:" + at2  + "[3]:" + at3  + "[4]:" + at4  + "[5]:" + at5  + "[6]:" + at6  + "[7]:" + at7  + "[8]:" + at8  + "[9]:" + at9  + "[10]:" + at10  + "[11]:" + at11+ "[12]:" + at12+ "[13]:" + at13);
//
//                Test.log("q:" + q);
//            }
//        }
//
//
//
//    }
//
//
//    public static void dodgeBulletsLatest() throws Exception{
//
//
//        bullets = rc.senseNearbyBullets(MAX_BULLET_DETECT_CHECK);
//
//
//        int maxChecks = bullets.length - 1;
//        if(maxChecks > 20) maxChecks = 20;
//
//        for (int i = maxChecks; i>=0; i--) {
//            BulletInfo bullet = bullets[i];
//            MapLocation loc = bullet.location;
//            float speed = bullet.speed;
//            float dist0 = myLocation.distanceTo(loc);
//
//            Direction originalEnemyDir = bullet.originalEnemyDir;
//            MapLocation loc1 = loc.add(originalEnemyDir, speed);
//
//            float dist1 = myLocation.distanceTo(loc1);
//
//            if (dist1 > dist0 + 0.5f) {
//                //The bullet is flying away from us
//                if (dist0 <= RELEVANCE__RADIUS) {
//                    zones[zoneLoc] = new DesireZone(loc.add(originalEnemyDir,speed/2), AVOID_RADIUS + speed/2, DANGER_FIRST + -10 * bullet.damage, true);
//                    zoneLoc++;
//                }
//            } else {
//
//                boolean thirdPossible = true;
//
//                if(dist0 < RELEVANCE__RADIUS){
//                    addCircleZone(loc,AVOID_RADIUS, DANGER_FIRST  + -10 * bullet.damage);
//                    //rc.setIndicatorDot(loc, 0,0,0);
//                    thirdPossible = false;
//                }
//
//                if(rc.canSenseLocation(loc1) && rc.isLocationOccupiedByTree(loc1)){
//                    //Don't have to dodge shots fired at trees
//                    addCircleZone(loc.add(originalEnemyDir,speed / 2),AVOID_RADIUS + 0.8f, DANGER_FIRST  + -10 * bullet.damage);
//                    return;
//                }
//
//
//                MapLocation locStart = loc.add(originalEnemyDir, AVOID_RADIUS);
//                float distStart= myLocation.distanceTo(locStart);
//                MapLocation loc2 = loc1.add(originalEnemyDir, speed);
//                float dist2= myLocation.distanceTo(loc2);
//                float dist07 = myLocation.distanceTo(loc.add(originalEnemyDir, speed * 0.7f));
//                float dist15 = myLocation.distanceTo(loc.add(originalEnemyDir, speed * 1.5f));
//
//
//                if(distStart < SPOT_RELEVANCE_RADIUS || dist1 < SPOT_RELEVANCE_RADIUS || dist07 < LINE_RELEVANCE_RADIUS ){
//                    zones[zoneLoc] = new DesireZone(locStart, originalEnemyDir,speed+ AVOID_RADIUS,AVOID_RADIUS,DANGER_FIRST + -10 * bullet.damage);
//                    zoneLoc++;
//
//                    addSpecialMapLocation(loc1.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS), 10);
//                    addSpecialMapLocation(loc1.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS), 10);
//
//                    drawBullet(locStart,loc1 , originalEnemyDir, AVOID_RADIUS, 230,20,20);
//
//                    loc1 = loc1.add(originalEnemyDir, OVERLAP_1_2);
//                    dist1 = myLocation.distanceTo(loc1);
//                    thirdPossible = false;
//                }
//
//
//
//
//
//
//                if(dist2 < SPOT_RELEVANCE_RADIUS_2ND || dist1 < SPOT_RELEVANCE_RADIUS_2ND || dist15 < LINE_RELEVANCE_RADIUS_2ND ){
//                    zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir,speed+ AVOID_RADIUS_2ND,AVOID_RADIUS_2ND,DANGER_2ND + -10 * bullet.damage);
//                    zoneLoc++;
//
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90),AVOID_RADIUS_2ND), 10);
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90),AVOID_RADIUS_2ND), 10);
//                    drawBullet(loc1,loc2 , originalEnemyDir, AVOID_RADIUS_2ND, 34,180,20);
//
//                    loc2 = loc2.add(originalEnemyDir, OVERLAP_2_3);
//                    dist2 = myLocation.distanceTo(loc2);
//                }
//
//                if(thirdPossible) {
//                    float dist25 = myLocation.distanceTo(loc.add(originalEnemyDir, speed * 2.5f));
//                    MapLocation loc3 = loc2.add(originalEnemyDir, speed);
//                    float dist3 = myLocation.distanceTo(loc3);
//                    if (dist2 < SPOT_RELEVANCE_RADIUS_3RD || dist3 < SPOT_RELEVANCE_RADIUS_3RD || dist25 < LINE_RELEVANCE_RADIUS_3RD) {
//                        zones[zoneLoc] = new DesireZone(loc2, originalEnemyDir, speed + AVOID_RADIUS_3RD, AVOID_RADIUS_3RD, DANGER_3RD + -10 * bullet.damage);
//                        zoneLoc++;
//                        drawBullet(loc2, loc3, originalEnemyDir, AVOID_RADIUS_3RD, 10, 20, 70);
//                    }
//                }
//
//            }
//        }
//
//
//
//    }


//    public static void doMovementOld(int minRemainingBytes) throws Exception{
//
//        Test.log("stating mov with " + (Clock.getBytecodesLeft() - minRemainingBytes)  + " bytes to spare");
//
//        float bestScore = -9999999;
//        int bestMovement = 0;
//
//        float moveVectorStr = -1 * (new MapLocation(0,0).distanceTo(new MapLocation(moveVectorX,moveVectorY))); //Negative because we want to move towards it, not away (and we calculate distance)
//
//        if(moveVectorStr > 1){
//            moveVectorStr /= 4;
//        }
//
//        int found = 0;
//
//
//        if(zoneLoc > 50) zoneLoc = 50; //absolute max we'll consider. the array is bigger to prevent exceptions, but further spots in the array are not used
//
//        int resize = zoneLoc;
//        int origZoneLoc = zoneLoc;
//        //We want to have enough passes to have a decent amount of movement spots looked at
//        //So limit the amount of desire zones we use
//
//        int toSpare = Clock.getBytecodesLeft() - minRemainingBytes;
//        if( toSpare < 1000){
//            resize = 0;
//        } else if( toSpare < 1500){
//            if(zoneLoc > 3) {
//                resize = 3;
//            }
//        }
//        else if( toSpare < 2000){
//            if(zoneLoc > 8) {
//                resize = 8;
//            }
//        }
//        else if(toSpare < 3000){
//            if(zoneLoc > 11) {
//                resize = 11;
//            }
//        }else if(toSpare< 4000){
//            if(zoneLoc > 14) {
//                resize = 14;
//            }
//        }else if(toSpare < 5000){
//            if(zoneLoc > 19) {
//                resize = 19;
//            }
//        }
//        else if(toSpare < 6000){
//            if(zoneLoc > 26) {
//                resize = 26;
//            }
//        }else if(toSpare < 7000){
//            if(zoneLoc > 30) {
//                resize = 30;
//            }
//        }
//        else if(toSpare< 8000){
//            if(zoneLoc > 35) {
//                resize = 35;
//            }
//        }else if(toSpare < 9000){
//            if(zoneLoc > 42) {
//                resize = 42;
//            }
//        }
//
//        DesireZone[] newOrder;
//        if(resize < zoneLoc && resize > 0){
//            //Do a fast sort-like thing that makes sure to put the desirezones that are most important at the front
//            //Precision doesn't matter much here
//            //Could potentially do the reordering instantly, by putting the different zones in different arrays to begin with
//
//
//
//            block:
//            {
//                newOrder = new DesireZone[zoneLoc];
//                int count = 0;
//                for (int i = zoneLoc - 1; i >= 0; i--) {
//                    if (zones[i].desire <= DANGER_2ND) {
//                        newOrder[count] = zones[i];
//                        count++;
//                    }
//                }
//                for (int i = zoneLoc - 1; i >= 0; i--) {
//                    if (zones[i].desire > 40) {
//                        newOrder[count] = zones[i];
//                        count++;
//                    }
//                }
//                for (int i = zoneLoc - 1; i >= 0; i--) {
//                    if (zones[i].desire >= DANGER_2ND && zones[i].desire < -40) {
//                        newOrder[count] = zones[i];
//                        count++;
//                        if(count > resize){
//                            break block;
//                        }
//                    }
//                }
//                for (int i = zoneLoc - 1; i >= 0; i--) {
//                    if (zones[i].desire >= -40 && zones[i].desire <= 40) {
//                        newOrder[count] = zones[i];
//                        count++;
//                        if(count > resize){
//                            break block;
//                        }
//                    }
//                }
//            }
//
//            zoneLoc = resize;
//        }
//        else{
//            //We could also be using the original zones thing which would be chaper here.
//            //But then we'd have to reset the variable after the reorder
//            //Which incurs a penalty on the situations wherein we don't have enough btytecodes. Which is worse
//            newOrder = zones;
//        }
//
//
//        //We have a movement direction, now put it at the constant distance of 10 and apply a strength relative to its size
//        //This to be sure our point isn't very close to us (as we don't want it inside our movement circle)
//        //Nor very far as it can cause flatness issues (if it's very far, the differences start mattering less, even though they should matter more)
//        float finalMoveVectorX;
//        float finalMoveVectorY;
//
//        int multX = 1;
//        int multY = 1;
//
//        if(moveVectorX < 0){
//            moveVectorX *= -1;
//            multX = -1;
//        }
//        if(moveVectorY < 0){
//            moveVectorY *= -1;
//            multY = -1;
//        }
//        if(moveVectorStr > -0.05f){
//            finalMoveVectorX = myLocation.x;
//            finalMoveVectorY = myLocation.y;
//            moveVectorStr = 0;
//        }
//        else{
//            float multFactor = 10 / (moveVectorX + moveVectorY);
//            finalMoveVectorX = (multFactor * moveVectorX * multX) + myLocation.x;
//            finalMoveVectorY = (multFactor * moveVectorY * multY) + myLocation.y;
//        }
//        MapLocation finalMove = new MapLocation(finalMoveVectorX,finalMoveVectorY);
//
//
//        int specialCount = specialMapLocationsCount;
//
//        //If have some, put special calculated positions in first (like lining up a shot through trees)
//        MapLocation[] movementSlots =new MapLocation[81 + specialCount];
//        for(int i = 0; i< specialCount; i++) {
//            movementSlots[i] = specialMapLocations[i];
//        }
//
//
//        //And we usually want to at least consider standing still
//        movementSlots[specialCount] = myLocation;
//
//        //The best move is usually right towards the final move
//        movementSlots[specialCount+1] = myLocation.add(myLocation.directionTo(finalMove),maxMove);
//
//
////      //rc.setIndicatorLine(myLocation,finalMove, rc.getID() % 255, (rc.getID() + 100) % 255, (rc.getID() + 175) % 255);
//
//        int currentMax = 2 + specialCount;
//        int slot = 0;
//        int slotMin = currentMax - 1;
//        int zoneMinus1 = zoneLoc - 1;
//
//
//        //We want to cut off this procedure a little bit before we hit the limit
//        //Doing the minus calculation here to save a bit of bytecode
//        int minRemainingPlus200 = minRemainingBytes + 200;
//        int minRemainingPlus600 = minRemainingBytes + 600;
//
//        float curScore;
//
//        boolean printed = false;
//
//        outerloop:
//        for(int pass = 0; pass < 8; pass++) {
//
//            if(Clock.getBytecodesLeft() < minRemainingPlus600){
//                System.out.println("pass : " + pass + " slot: " + slot   +  "  final choice: " + bestMovement  +  " vector: " +  finalMove  +  "  zones used:  " + zoneLoc +  " zones total:" + origZoneLoc +  "bestscore: " + bestScore);
//                System.out.println("slot: " + slot + " zones: " + zoneLoc + "/" + origZoneLoc);
//                printed = true;
//                break;
//            }
//            currentMax += 8;
//
//
//            int angleIndex;
//            float distance = maxMove;
//
//            //Every pass adds 8 points, nicely separated around the potential movement circle
//            //First pass is just max distance with N, NE, E, SE, S, SW, W, NW angles
//            //The others are variations, either less than max distance, or different angles
//            switch(pass){
//                case 0:
//                    angleIndex = 0;
//                    break;
//                case 1:
//                    angleIndex = 8;
//                    break;
//                case 2:
//                    angleIndex = 24;
//                    distance *= 0.5f;
//                    break;
//                case 3:
//                    angleIndex = 16;
//                    distance *= 0.66f;
//                    break;
//                case 4:
//                    angleIndex = 40;
//                    distance *= 0.33f;
//                    break;
//                case 5:
//                    angleIndex = 24;
//                    break;
//                case 6:
//                    angleIndex = 32;
//                    break;
//                case 7:
//                    angleIndex = 8;
//                    distance *= 0.1f;
//                    break;
//                case 8:
//                    angleIndex = 16;
//                    distance *= 0.8;
//                    break;
//                default:
//                    angleIndex = 0;
//                    break;
//            }
//
//
//            for(int i = currentMax;i>slotMin;i--){
//                if(angleIndex >= 48){
//                    Test.log("index: " + angleIndex  + "  currentmax" + currentMax + "Slot: " + slotMin);
//                    break outerloop;
//                }
//                movementSlots[i] = myLocation.add(someDirections[angleIndex],distance);
//                angleIndex++;
//            }
//
//            //This loop calculates the valuations of all the potential move spots.
//            //It's the core of the movement system, and tends to take up most of the bytecode budget
//            for (;slot < currentMax; slot++ ) {
//                MapLocation loc = movementSlots[slot];
//                if (rc.canMove(loc)) {
//                    //Start with valuing the spots based on the distance to the total move vector point
//                    //Give some extra valuation boost to the 'special' locations that have been calculated elsewhere (example: spot that lines up a shot through trees)
//                    if(slot < specialCount){
//                        curScore = loc.distanceTo(finalMove) * moveVectorStr + extraPoints[slot];
//                    }
//                    else{
//                        curScore = loc.distanceTo(finalMove) * moveVectorStr;
//                    }
//
//                    //There's a bytecode overhead for accessing class variables
//                    float locX = loc.x;
//                    float locY = loc.y;
//
//                    //Change the valuation if this spot is inside of any of the desirezones
//                    //Heaviest part of program due to double loop. Also heavily optimized
//                    //Theoretically, everything could be unpacked from the loops, with reused variables for some bytecode gains
//                    //But that's a little too insane. Like through having 50 zone.x variables
//                    for (int i = zoneMinus1; i >= 0; i--) {
//                        DesireZone zone = newOrder[i];
//
//                        if(zone.bulletLine) {
//                            //Test.beginClockTestAvg(8);
//                            final float test1 = (locX - zone.x1) * zone.p21x + (locY - zone.y1) * zone.p21y;
//                            if ( test1 >= 0f && test1 <= zone.p21MagSquared) {
//                                final float test2 = (locX - zone.x1) * zone.p41x + (locY - zone.y1) * zone.p41y;
//                                if (test2 >= 0f && test2 <= zone.p41MagSquared) {
//                                    curScore += zone.desire;
//                                    found++;
//                                }
//                            }
//                            // Test.endClockTestAvg(8, "zonetest: ");
//                        }
//                        else if (zone.left < locX && locX < zone.right && zone.top < locY && locY < zone.bot) {
//                            if(zone.circular){
//                                if(loc.distanceTo(zone.center) < zone.radius){
//                                    curScore += zone.desire;
//                                }
//                            }else {
//                                curScore += zone.desire;
//                            }
//                        }
//                    }
//
//                    if (curScore > bestScore) {
//                        bestScore = curScore;
//                        bestMovement = slot;
//                        //  Test.log("spot: " + slot +  "  score: " + bestScore);
//                    }
//                }
//
//                //Gotta exit if we're close to hitting the bytecode limit
//                if(Clock.getBytecodesLeft() < minRemainingPlus200){
//                    System.out.println("pass : " + pass + " slot: " + slot   +  "  final choice: " + bestMovement  +  " vector: " +  finalMove  +  "  zones used:  " + zoneLoc +  " zones total:" + origZoneLoc +  "bestscore: " + bestScore);
//                    System.out.println("slot: " + slot + " zones: " + zoneLoc + "/" + origZoneLoc);
//
//                    printed = true;
//                    break outerloop;
//                }
//            }
//            slotMin = slot -1;
//        }
//
//        if(bestScore == -9999999) {
//            if(!printed){
//                System.out.println("All slots! Zones: " + zoneLoc + "/" + origZoneLoc);
//                System.out.println(" did all passes!  final choice: " + bestMovement + " vector: " + finalMove + "  zones used:  " + zoneLoc + " zones total:" + origZoneLoc + "bestscore: " + bestScore);
//            }
//        }
//
//
//
//        //Test.log("f:"  + found);
//        rc.move(movementSlots[bestMovement]);
//        myLocation = rc.getLocation();
//        myX = myLocation.x;
//        myY = myLocation.y;
//
//
////        if(bestScore < -100){
////            //rc.setIndicatorDot(myLocation, 255,0,0);
////        }
////        else{
////            //rc.setIndicatorDot(myLocation, 0,255,0);
////        }
//    }



//    public static void dodgeBulletsold() throws Exception{
//        bullets = rc.senseNearbyBullets(MAX_BULLET_DETECT_CHECK);
//
//        int maxChecks = bullets.length - 1;
//        if(maxChecks > 20) maxChecks = 20;
//
//        for (int i = maxChecks; i>=0; i--) {
//            BulletInfo bullet = bullets[i];
//            float speed = bullet.speed;
//            Direction originalEnemyDir = bullet.originalEnemyDir;
//            MapLocation loc = bullet.location;
//            float dist0 = myLocation.distanceTo(loc);
//
//            boolean allow12 = true;
//            //boolean allow23 = true;
//
//
//
//            MapLocation loc1 = loc.add(originalEnemyDir, speed);
//            float dist1 = myLocation.distanceTo(loc1);
//            MapLocation loc05 = loc.add(originalEnemyDir, speed* 0.5f);
//
//            float dist05 = myLocation.distanceTo(loc05);
//
//
//            //Bullets travel in lines, and don't just start and end locations
//            //And apparently, looking at the calculations and in game behavior a bullet hits both in front and behind it.
//            // The hit distance method would register a hit for all of the locations between the bullets loc
//            // and the next position. But also between the loc and the previous location
//            //However, the check is only called if the robot is within a distance of   GameConstants.MAX_ROBOT_RADIUS + speed/2
//            //from the middle point. So not all backwards points get hit.
//            //And with within, the center of the robot is meant.
//
//            //This means that we get a sort of awkward shape of what can be hit. We'll just approximate it with a rectangle, starting at that
//            //spot from which robots can get checked
//
//            float maxCheckVal = GameConstants.MAX_ROBOT_RADIUS + speed/2;
//            if(dist05 <  maxCheckVal + AVOID_RADIUS) {
//
//                boolean do1 = false;
//
//                if (dist0 < SPOT_RELEVANCE_RADIUS) {
//                    do1 = true;
////                    allow23 = false;
//                } else if (dist1 < SPOT_RELEVANCE_RADIUS) {
//                    do1 = true;
//                } else {
//                    if (dist05 < LINE_RELEVANCE_RADIUS) {
//                        do1 = true;
//                    }else if (myLocation.distanceTo(loc05.add(originalEnemyDir, AVOID_RADIUS-maxCheckVal)) < SPOT_RELEVANCE_RADIUS) {
//                        do1 = true;
//                        allow12 = false;
////                        allow23 = false;
//                    }
//                }
//
//                if (do1) {
//
//                    zones[zoneLoc] = new DesireZone(loc05.add(originalEnemyDir, AVOID_RADIUS-maxCheckVal), originalEnemyDir, loc1, AVOID_RADIUS, DANGER_FIRST + -10 * bullet.damage);
//                    zoneLoc++;
//
//                    addSpecialMapLocation(loc.add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_PLUS), 10);
//                    addSpecialMapLocation(loc.add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_PLUS), 10);
//
//                    drawBullet(loc05.add(originalEnemyDir, AVOID_RADIUS-maxCheckVal), loc1, originalEnemyDir, AVOID_RADIUS, 230, 20, 20);
//
//                    loc1 = loc1.add(originalEnemyDir, OVERLAP_1_2);
//                    dist1 = myLocation.distanceTo(loc1);
//                }
//
//            }
//
//
//            if(dist1 > dist0 + 0.1f  || rc.isLocationOccupiedByTree(loc1)){
//                //The bullet is flying away, we don't have to be concerned about either the 12 range or the 23 range
//                //Or: The bullet will hit a tree, let's not bother dodging it.
//                continue;
//            }
//
//            if(allow12) {
//                MapLocation loc2 = loc1.add(originalEnemyDir, speed);
//                boolean do2 = false;
//
//                if(dist1 < SPOT_RELEVANCE_RADIUS_2ND){
//                    do2 = true;
//                }else{
//                    float dist2 = myLocation.distanceTo(loc2);
//                    if(dist2 < SPOT_RELEVANCE_RADIUS_2ND){
//                        do2 = true;
//                    }
//                    else{
//                        float dist16 = myLocation.distanceTo(loc.add(originalEnemyDir, speed * 1.6f));
//                        if(dist16 <LINE_RELEVANCE_RADIUS_2ND ){
//                            do2 = true;
//                        }
//                    }
//                }
//                if(do2){
//                    zones[zoneLoc] = new DesireZone(loc1, originalEnemyDir, loc2, AVOID_RADIUS_2ND, DANGER_2ND + -10 * bullet.damage);
//                    zoneLoc++;
//
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateLeftDegrees(90), AVOID_RADIUS_2ND_PLUS), 10);
//                    addSpecialMapLocation(loc2.add(originalEnemyDir.rotateRightDegrees(90), AVOID_RADIUS_2ND_PLUS), 10);
//                    drawBullet(loc1, loc2, originalEnemyDir, AVOID_RADIUS_2ND, 34, 180, 20);
//
////                    if (allow23) {
////                        loc2 = loc2.add(originalEnemyDir, OVERLAP_2_3);
////                        dist2 = myLocation.distanceTo(loc2);
////                    }
//                }
//
////                if (allow23) {
////                    float dist25 = myLocation.distanceTo(loc.add(originalEnemyDir, speed * 2.5f));
////                    MapLocation loc3 = loc2.add(originalEnemyDir, speed);
////                    float dist3 = myLocation.distanceTo(loc3);
////
////                    if (dist2 < SPOT_RELEVANCE_RADIUS_3RD || dist3 < SPOT_RELEVANCE_RADIUS_3RD || dist25 < LINE_RELEVANCE_RADIUS_3RD) {
////                        zones[zoneLoc] = new DesireZone(loc2, originalEnemyDir, speed + AVOID_RADIUS_3RD, AVOID_RADIUS_3RD, DANGER_3RD + -10 * bullet.damage);
////                        zoneLoc++;
////                        drawBullet(loc2, loc3, originalEnemyDir, AVOID_RADIUS_3RD, 10, 20, 70);
////                    }
////                }
//            }
//        }
//    }

//    public static Direction getPredictiveDirOld(MapLocation loc, RobotType type, int amountOfShots) throws GameActionException{
//        Direction dir = myLocation.directionTo(loc);
//
//        float dist = myLocation.distanceTo(loc);
//        float size = type.bodyRadius;
//        float speed = type.strideRadius;
//
//        float realDist = dist - (radius + GameConstants.BULLET_SPAWN_OFFSET);
//        if(type != RobotType.SCOUT) {
//            if (speed + realDist - bulletSpeed <= size *0.7f) {
//                //Non-scouts can't dodge a shot at 1 turn distance if fired straight at the middle
//                //This checks whether it's possible to get into two turn territory. If not definite hit
//                //Moving in a straight path away also just puts them into the same position, so we allow this at slightly shorter ranges
//
//                //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),200,140,0);
//                return dir;
//            }
//        }else{
//            if (realDist - (size +GameConstants.BULLET_SPAWN_OFFSET) <= 0f) {
//                //Instant hit
//                return dir;
//            }
//        }
//
//
//
//        //1.1 because they can sometimes sneak an extra turn in by moving away. Generally will actually be less
//        //Because were neglecting their body size
//        int turns = (int) ((realDist / bulletSpeed) + 1.3f);
//
////        if(turns == 1){
////            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),255,255,255);
////        }else if(turns == 2){
////            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),255,0,0);
////        } else if(turns == 3){
////            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),0,255,0);
////        } else if(turns == 4){
////            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),0,0,255);
////        } else if(turns == 5){
////            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),0,0,0);
////        } else{
////            //rc.setIndicatorLine(myLocation, myLocation.add(dir,1f),100,100,50);
////        }
//
//
//        float maxMovement = turns * speed;
//        float maxEscape =  (maxMovement - size) + floatSafety;
//
//        Direction left= dir.rotateLeftDegrees(90);
//        Direction right = left.opposite();
//
//
//
//
//        MapLocation mapLeftEscape = loc.add(left,maxEscape);
//        MapLocation mapRightEscape = loc.add(right,maxEscape);
//
//
//
//        if(type != RobotType.SCOUT && rc.canSenseLocation(loc) ) {
//            while (rc.canSenseLocation(mapLeftEscape) && rc.isLocationOccupiedByTree(mapLeftEscape)){
//                mapLeftEscape = mapLeftEscape.add(right,size*1.8f);
//            }
//            while (rc.canSenseLocation(mapRightEscape) && rc.isLocationOccupiedByTree(mapRightEscape)){
//                mapRightEscape = mapRightEscape.add(left,size*1.8f);
//            }
//        }
//
//
//        float rotation = myLocation.directionTo(lastBestTarget.location).getAngleDegrees() - dir.getAngleDegrees();
//        if (rotation > 200) {
//            rotation -= 360;
//        }
//        if (rotation < -200) {
//            rotation += 360;
//        }
//        if (rotation > 2) {
//            if (directionTracker <= 0) {
//                directionTracker = 1;
//            } else {
//                directionTracker++;
//            }
//        } else if(rotation < -2) {
//            if (directionTracker >= 0) {
//                directionTracker = -1;
//            } else {
//                directionTracker--;
//            }
//        }else{
//            directionTracker = 0;
//        }
//
//        Test.log("rotation: " + rotation);
//
//        //rc.setIndicatorLine(mapLeftEscape,mapRightEscape,0,0,0);
//
//        float multiplier;
//        boolean forceChoice = false;
//
//
//        int absTracker = Math.abs(directionTracker);
//        if(absTracker <= 2 ){
//            remainsStillTracker++;
//        }else{
//            remainsStillTracker = 0;
//        }
//        greed += absTracker;
//
//        Direction fireAt;
//        if(absTracker < 4){
//            //Prediction = they're not dodging, or they're trying to do a backwards moving dodge
//            //rc.setIndicatorDot(myLocation, 0,0,0);
//            if(toggleShot) fireAt = myLocation.directionTo(loc.add(dir,turns*speed).add(right,speed - floatSafety));
//            else fireAt = myLocation.directionTo(loc.add(dir,turns*speed).add(left,speed - floatSafety));
//        } else if(true){
//            //They're doing a side-shuffle dodge style
//            if(directionTracker < 0){
//                //They're going left
//                if(toggleShot) fireAt = myLocation.directionTo(mapLeftEscape); //Fire ahead of them
//                else fireAt = myLocation.directionTo(loc.add(dir,turns*speed).add(left,speed - floatSafety)); //Fire on the left side of their body
//            }else{
//                //They're going right
//                if(toggleShot) fireAt = myLocation.directionTo(mapRightEscape); //Fire ahead of them
//                else fireAt = myLocation.directionTo(loc.add(dir,turns*speed).add(right,speed - floatSafety)); //Fire on the right side of their body
//
//            }
//
//        }
//        else {
//
//            switch (absTracker) {
//                case 2:
//                    multiplier = 0.55f;
//                    break;
//                case 3:
//                    multiplier = 0.9f;
//                    forceChoice = true;
//                    break;
//                case 4:
//                    multiplier = 1.2f;
//                    forceChoice = true;
//                    break;
//                case 5:
//                    multiplier = 1.3f;
//                    forceChoice = true;
//                    break;
//                case 6:
//                    multiplier = 0.4f;
//                    forceChoice = true;
//                    break;
//                case 7:
//                    multiplier = 1.2f;
//                    forceChoice = true;
//                    break;
//                case 8:
//                    multiplier = 0.5f;
//                    forceChoice = true;
//                    break;
//                case 9:
//                    multiplier = 0;
//                    break;
//                case 10:
//                    multiplier = 1f;
//                    forceChoice = true;
//                    break;
//                case 11:
//                    multiplier = -0.5f;
//                    forceChoice = true;
//                    break;
//                default:
//                    multiplier = 0.1f;
//                    break;
//            }
//
//
//            Direction dirToEscape;
//
//
////        if(forceChoice){
////            if(directionTracker > 0){
////                dirToEscape = myLocation.directionTo(mapRightEscape);
////            }
////            else{
////                dirToEscape = myLocation.directionTo(mapLeftEscape);
////            }
////        }
////        else
//
//            if (forceChoice) {
//                if (directionTracker > 0) {
//                    mapLeftEscape = loc;
//                } else {
//                    mapRightEscape = loc;
//                }
//            }
//            if (toggleShot) {
//                dirToEscape = myLocation.directionTo(mapRightEscape);
//            } else {
//                dirToEscape = myLocation.directionTo(mapLeftEscape);
//            }
//            fireAt = dir.rotateRightDegrees(dir.degreesBetween(dirToEscape) * multiplier);
//
//            if (DO_SHOT_ANALYSIS && !didShotAnalysisThisTurn) {
//                int enemyId = lastBestTarget.ID;
//                if (shotAnalyzers[3] != null) {
//                    if (enemyId == shotAnalyzers[3].enemyId) {
//                        analyzeShot(3, loc, type);
//                    }
//                }
//                if (shotAnalyzers[2] != null) {
//                    if (enemyId == shotAnalyzers[2].enemyId) {
//                        analyzeShot(2, loc, type);
//                    }
//                }
//                if (shotAnalyzers[1] != null) {
//                    if (enemyId == shotAnalyzers[1].enemyId) {
//                        analyzeShot(1, loc, type);
//                    }
//                }
//                if (shotAnalyzers[0] != null) {
//                    if (enemyId == shotAnalyzers[0].enemyId) {
//                        analyzeShot(0, loc, type);
//                    }
//                }
//
//                shotAnalyzers[3] = shotAnalyzers[2];
//                shotAnalyzers[2] = shotAnalyzers[1];
//                shotAnalyzers[1] = shotAnalyzers[0];
//
//                ShotAnalyzer s = new ShotAnalyzer();
//                s.bulletSpeed = bulletSpeed;
//                s.firingLocation = myLocation;
//                s.bulletStartLocation = myLocation.add(fireAt, radius + GameConstants.BULLET_SPAWN_OFFSET);
//                s.originalEnemyDir = dir;
//                s.actuallyFiredAt = fireAt;
//                s.predictedEscapeAngle = dir.degreesBetween(dirToEscape);
//                s.firedAtDist = dist;
//                s.turnShot = turn;
//                s.enemyId = enemyId;
//                s.directionTracker = absTracker;
//                shotAnalyzers[0] = s;
//
//                didShotAnalysisThisTurn = true;
//
//                Test.log("estimated hits: " + estimatedHits + " misses: " + estimatedMisses + "  dmg done:" + estimatedHits * 2);
//
//            }
//        }
//
//        return fireAt;
//
//    }

//    public void fireoldstuff() throws GameActionException {
//        if (rc.canFireSingleShot()) {
//
//
//            int rLength = robots.length;
//            if(rLength > 0){
//                RobotInfo bestTarget = null;
//                float bestScore = 0;
//                int friendlies = 0;
//                int enemies = 0;
//
//
//                for (int i = 0; i < rLength; i++) {
//                    RobotInfo r = robots[i];
//
//                    if(r.getTeam().equals(enemy)) {
//                        enemies++;
//                        float curScore = 0;
//
//                        MapLocation loc = r.location;
//                        float dist = myLocation.distanceTo(loc);
//
//                        switch (r.type) {
//                            case SOLDIER:
//                                curScore = (8f - dist) * 2 + 3;
//
//                                if(r.health < 10){
//                                    curScore += 3;
//                                }
//                                break;
//                            case GARDENER:
//                                curScore = 4;
//                                if(dist < 4){
//                                    curScore += 6;
//                                }
//
//                                break;
//                            case LUMBERJACK:
//                                curScore = 8f - dist;
//                                if(dist < C.LUMBERJACK_CUT_PLUS_SAFETY + radius){
//                                    curScore += 4;
//                                }
//                                break;
//                            case ARCHON:
//                                if(turn > 300 || R.treeCount > 4){
//                                    curScore = (9f - dist)/3;
//                                }
//                                break;
//                            case SCOUT:
//
//                                if(R.treeCount > 15){
//                                    curScore += 2;
//                                }
//
//                                if(dist < 4){
//                                    curScore += 3;
//                                }
//                                break;
//                            case TANK:
//                                curScore = 6;
//                                break;
//
//                        }
//
//                        if(curScore > bestScore){
//                            Direction originalEnemyDir = myLocation.directionTo(loc);
//
//                            if(dist > radius + r.getRadius() + 1f) {
//                                float halfway = radius + (((dist - radius) - r.getRadius()) / 2f);
//                                //Check whether there's something in the way
//                                if (rc.isLocationOccupied(myLocation.add(originalEnemyDir, halfway))) {
//                                    continue;
//                                }
//                            }
//                            bestScore = curScore;
//                            bestTarget = r;
//                        }
//                    }
//                    else{
//                        friendlies++;
//                    }
//                }
//
//
//
//
//                if(bestTarget != null){
//                    MapLocation loc = bestTarget.location;
//                    float dist = myLocation.distanceTo(loc);
//                    Direction originalEnemyDir = myLocation.directionTo(loc);
//                    RobotType type = bestTarget.type;
//
//
//                    greed = 0;
//
//                    if(turn < 100){
//                        greed += 10;
//                    } else if(turn < 250){
//                        greed += 5;
//                    }
//                    if(R.treeCount < 1){
//                        greed -= 2;
//                    }
//                    else if(R.treeCount < 5){
//                        greed += 1;
//                    } else if(R.treeCount < 15){
//                        greed += 5;
//                    }else{
//                        greed += 10;
//                    }
//                    greed += enemies * 2;
//                    greed -= friendlies * 2;
//
//                    if(R.bulletCount > 100){
//                        greed += 4;
//                    } else if(R.bulletCount > 50){
//                        greed+=1;
//                    }
//
//
//
////                    if(greed < 5){
////                        //rc.setIndicatorDot(myLocation,0,200,130);
////                    } else if(greed < 8){
////                        //rc.setIndicatorDot(myLocation,200,210,120);
////                    } else{
////                        //rc.setIndicatorDot(myLocation,240,140,20);
////
////                    }
//
//                    if(lastBestTarget != null && lastBestTarget.getID() == bestTarget.getID()) {
//                        originalEnemyDir = getPredictiveDir(loc, type);
//                    }
//
//                    switch (type) {
//                        case SOLDIER:
//
//                            boolean canAndWillingPenta = false;
//                            boolean canAndWillingTogglePenta = false;
//                            boolean canAndWillingPureTri = false;
//                            boolean canAndWillingToggleTri = false;
//
////                            if(greed >= 8){
////                                if(rc.canFirePentadShot()) {
////                                    if (dist < 6f) {
////                                        canAndWillingPenta = true;
////                                    } else {
////                                        canAndWillingToggleTri = true;
////                                    }
////                                }
////                                else if(rc.canFireTriadShot()){
////                                    canAndWillingPureTri = true;
////                                }
////                            } else if(greed >= 5){
////                                if (dist < radius + type.bodyRadius + 3f) {
////                                    if(rc.canFirePentadShot()){
////                                        canAndWillingPenta = true;
////                                    } else if(rc.canFireTriadShot()){
////                                        canAndWillingPureTri = true;
////                                    }
////                                }
////                                else{
////                                    if(rc.canFireTriadShot()){
////                                        canAndWillingToggleTri = true;
////                                    }
////                                }
////                            }else{
////                                if (dist < radius + type.bodyRadius + 3f) {
////                                    if(rc.canFirePentadShot()){
////                                        canAndWillingPenta = true;
////                                    } else if(rc.canFireTriadShot()){
////                                        canAndWillingPureTri = true;
////                                    }
////                                }
////                            }
//
//
////                            if(canAndWillingPenta && toggleShot && lastPentaCenter != null){
////
////                                if(loc.distanceTo(lastPentaLeft) < loc.distanceTo(lastPentaRight)){
////                                    originalEnemyDir = myLocation.directionTo(   lastPentaLeft.add(lastPentaLeft.directionTo(lastPentaCenter), lastPentaLeft.distanceTo(lastPentaCenter)/2f)      );
////                                }else{
////                                    originalEnemyDir = myLocation.directionTo(   lastPentaRight.add(lastPentaRight.directionTo(lastPentaCenter), lastPentaRight.distanceTo(lastPentaCenter)/2f)      );
////                                }
////                            }else {
////                                float rotation = 0;
////
////                                if (lastBestTarget != null && lastBestTarget.getID() == bestTarget.getID()) {
////                                    rotation = myLocation.directionTo(lastBestTarget.location).getAngleDegrees() - originalEnemyDir.getAngleDegrees();
////
////                                    if (rotation > 200) {
////                                        rotation -= 360;
////                                    }
////                                    if (rotation < -200) {
////                                        rotation += 360;
////                                    }
////
////                                    if (rotation > 0) {
////                                        if (directionTracker <= 0) {
////                                            directionTracker = 1;
////                                        } else {
////                                            directionTracker++;
////                                        }
////                                    } else {
////                                        if (directionTracker >= 0) {
////                                            directionTracker = -1;
////                                        } else {
////                                            directionTracker--;
////                                        }
////                                    }
////
////
////                                    if (rotation < 0) {
////                                        rotation -= 2;
////                                    } else {
////                                        rotation += 2;
////                                    }
////
////                                    if(directionTracker == 1 || directionTracker == -1){
////                                        rotation *= 0.8f;
////                                    }
////                                    else if(directionTracker == 2 || directionTracker == -2){
////                                        rotation *= 1.1f;
////                                    } else if(directionTracker == 3 || directionTracker == -3){
////                                        rotation *= 1.3f;
////                                    }else{
////                                        rotation *= 1.6f;
////                                    }
////
////                                }
////
////                                float rotation2;
////                                if (toggleShot) {
////                                    MapLocation sideShot = loc.add(originalEnemyDir.rotateLeftDegrees(90), 0.5f);
////                                    rotation2 = originalEnemyDir.getAngleDegrees() - myLocation.directionTo(sideShot).getAngleDegrees();
////                                } else {
////                                    MapLocation sideShot = loc.add(originalEnemyDir.rotateRightDegrees(90), 0.5f);
////                                    rotation2 = originalEnemyDir.getAngleDegrees() - myLocation.directionTo(sideShot).getAngleDegrees();
////                                }
//
////                                Test.log("rotation1: " + rotation);
////                                Test.log("rotation2: " + rotation);
//
//////                                float mult1 = 6f;
//////                                float mult2 = 1f;
//////                                float mult3 = 0.9f;
//
////                                float mult1 = 1f;
////                                float mult2 = 3f;
////                                float mult3 = 1f;
//
//
////                                originalEnemyDir = originalEnemyDir.rotateRightDegrees((rotation * mult1 + rotation2 * mult2) / ((mult1 + mult2) * mult3));
////                            }
//
//
//
//                            lastPentaCenter = null;
//                            lastPentaLeft = null;
//                            lastPentaRight = null;
//
//
//                            if(rc.canFirePentadShot() && dist < 5.5){
//                                canAndWillingPenta = true;
//                            }
//
//                            if(canAndWillingPenta){
//                                rc.firePentadShot(originalEnemyDir);
//
//                                lastPentaCenter = myLocation.add(originalEnemyDir,dist);
//                                lastPentaLeft = myLocation.add(originalEnemyDir.rotateLeftDegrees(15),dist);
//                                lastPentaRight = myLocation.add(originalEnemyDir.rotateRightDegrees(15),dist);
//                            }
//                            else if(canAndWillingTogglePenta){
//                                if(toggleShot){
//                                    rc.fireSingleShot(originalEnemyDir);
//                                }else{
//                                    rc.firePentadShot(originalEnemyDir);
//                                    lastPentaCenter = myLocation.add(originalEnemyDir,dist);
//                                    lastPentaLeft = myLocation.add(originalEnemyDir.rotateLeftDegrees(15),dist);
//                                    lastPentaRight = myLocation.add(originalEnemyDir.rotateRightDegrees(15),dist);
//                                }
//                            }
//                            else if(canAndWillingPureTri){
//                                rc.fireTriadShot(originalEnemyDir);
//                            } else if(canAndWillingToggleTri){
//                                if(toggleShot){
//                                    rc.fireTriadShot(originalEnemyDir);
//
//                                }else{
//                                    rc.fireSingleShot(originalEnemyDir);
//                                }
//                            } else{
//                                rc.fireSingleShot(originalEnemyDir);
//                            }
//
//
//
//
//
//
//
//
//                            break;
//                        case GARDENER:
//                            if (dist < radius + type.bodyRadius + 2.5f) {
//                                if(rc.canFirePentadShot()){
//                                    rc.firePentadShot(originalEnemyDir);
//                                } else if(rc.canFireTriadShot()){
//                                    rc.fireTriadShot(originalEnemyDir);
//                                }else{
//                                    rc.fireSingleShot(originalEnemyDir);
//                                }
//                            }
//                            else {
//                                rc.fireSingleShot(originalEnemyDir);
//                            }
//                            break;
//                        case LUMBERJACK:
//                            if (dist < radius + type.bodyRadius + 2.5f) {
//                                if(rc.canFirePentadShot()){
//                                    rc.firePentadShot(originalEnemyDir);
//                                } else if(rc.canFireTriadShot()){
//                                    rc.fireTriadShot(originalEnemyDir);
//                                }else{
//                                    rc.fireSingleShot(originalEnemyDir);
//                                }
//                            }
//                            else {
//                                if(toggleShot){
//                                    originalEnemyDir = originalEnemyDir.rotateLeftDegrees(5);
//                                    toggleShot = false;
//                                }else{
//                                    originalEnemyDir = originalEnemyDir.rotateRightDegrees(5);
//                                    toggleShot = true;
//                                }
//
//                                rc.fireSingleShot(originalEnemyDir);
//                            }
//                            break;
//                        case ARCHON:
//                            if(dist < radius + type.bodyRadius + 2.5f && R.treeCount > 15){
//                                if(rc.canFirePentadShot()){
//                                    rc.firePentadShot(originalEnemyDir);
//                                } else if(rc.canFireTriadShot()){
//                                    rc.fireTriadShot(originalEnemyDir);
//                                }else{
//                                    rc.fireSingleShot(originalEnemyDir);
//                                }
//                            }
//                            else{
//                                rc.fireSingleShot(originalEnemyDir);
//                            }
//                            break;
//                    }
//
//
//                    if(toggleShot) toggleShot = false;
//                    else toggleShot = true;
//
//                    lastBestTarget = bestTarget;
//                    lastDir = originalEnemyDir;
//
//                }
//                else{
//                    lastBestTarget = null;
//                    lastDir = null;
//                    directionTracker = 0;
//                    lastPentaCenter = null;
//                    lastPentaLeft = null;
//                    lastPentaRight = null;
//                }
//            }
//            else{
//                lastBestTarget = null;
//                lastDir = null;
//                directionTracker = 0;
//                lastPentaCenter = null;
//                lastPentaLeft = null;
//                lastPentaRight = null;
//            }
//        }
//        else{
//            lastBestTarget = null;
//            lastDir = null;
//            directionTracker = 0;
//            lastPentaCenter = null;
//            lastPentaLeft = null;
//            lastPentaRight = null;
//        }
//    }

//    public static void dodgeBulletsDistanceBased() throws Exception{
//        bullets = rc.senseNearbyBullets(MAX_BULLET_DETECT_CHECK);
//
//        int maxChecks = Math.min(bullets.length - 1, 20);
//
//        for (int i = maxChecks; i>=0; i--) {
//            BulletInfo bullet = bullets[i];
//            float speed = bullet.speed;
//            Direction dir = bullet.dir;
//            MapLocation loc = bullet.location;
//            float dist0 = myLocation.distanceTo(loc);
//
//            boolean allow12 = angledRectangleIndex < 7;
//            boolean allow23 = maxChecks < 5 && allow12;
//
//
//
//            MapLocation loc1 = loc.add(dir, speed);
//            float dist1 = myLocation.distanceTo(loc1);
//            MapLocation loc05 = loc.add(dir, speed* 0.5f);
//
//            float dist05 = myLocation.distanceTo(loc05);
//
//            boolean do1 = false;
//
//            //Bullets travel in lines, and don't just start and end locations
//            //And apparently, looking at the calculations and in game behavior a bullet hits both in front and behind it.
//            // The hit distance method would register a hit for all of the locations between the bullets loc
//            // and the next position. But also between the loc and the previous location
//            //However, the check is only called if the robot is within a distance of   GameConstants.MAX_ROBOT_RADIUS + speed/2
//            //from the middle point. So not all backwards points get hit.
//            //And with within, the center of the robot is meant.
//
//            //This means that we get a sort of awkward shape of what can be hit. We'll just approximate it with a rectangle, starting at that
//            //spot from which robots can get checked
//
//            float maxCheckVal = GameConstants.MAX_ROBOT_RADIUS + speed/2;
//            if(dist05 <  maxCheckVal + AVOID_RADIUS) {
//
//
//                if (dist0 < SPOT_RELEVANCE_RADIUS) {
//                    do1 = true;
//                    allow23 = false;
//                } else if (dist1 < SPOT_RELEVANCE_RADIUS) {
//                    do1 = true;
//                } else {
//                    if (dist05 < LINE_RELEVANCE_RADIUS) {
//                        do1 = true;
//                    }else if (myLocation.distanceTo(loc05.add(dir, AVOID_RADIUS-maxCheckVal)) < SPOT_RELEVANCE_RADIUS) {
//                        do1 = true;
//                        allow12 = false;
//                        allow23 = false;
//                    }
//                }
//
//                if (do1) {
//
//                    angledRectangles[angledRectangleIndex] = new DesireZone(loc05.add(dir, AVOID_RADIUS-maxCheckVal), dir, loc1, AVOID_RADIUS, DANGER_FIRST + -10 * bullet.damage);
//                    angledRectangleIndex++;
//
////                    drawBullet(loc05.add(originalEnemyDir, AVOID_RADIUS-maxCheckVal), loc1, originalEnemyDir, AVOID_RADIUS, 230, 20, 20);
//
//                    loc1 = loc1.add(dir, OVERLAP_1_2);
//                    dist1 = myLocation.distanceTo(loc1);
//                }
//
//            }
//
//
//            if(dist1 > dist0 + 0.1f  || rc.isLocationOccupiedByTree(loc1)){
//                //The bullet is flying away, we don't have to be concerned about either the 12 range or the 23 range
//                //Or: The bullet will hit a tree, let's not bother dodging it.
//
//                if(do1){
//                    MapLocation specialLoc = loc.add(dir, dist0);
//                    addSpecialMapLocation(specialLoc.add(dir.rotateLeftDegrees(90), AVOID_RADIUS_PLUS), 0);
//                    addSpecialMapLocation(specialLoc.add(dir.rotateRightDegrees(90), AVOID_RADIUS_PLUS), 0);
//
//                    addSpecialMapLocation(myLocation.add(loc.directionTo(myLocation),maxMove),0);
//
////                    //rc.setIndicatorLine(myLocation,myLocation.add(loc.directionTo(myLocation),maxMove), 130,40,70 );
//                    // //rc.setIndicatorLine(specialLoc.add(originalEnemyDir.rotateLeftDegrees(90)),specialLoc.add(originalEnemyDir.rotateRightDegrees(90)), 10, 255,200);
//                }
//
//                continue;
//            }
//            boolean do2 = false;
//
//            if(allow12) {
//                MapLocation loc2 = loc1.add(dir, speed);
//                float dist2 = myLocation.distanceTo(loc2);
//
//                if(dist1 < SPOT_RELEVANCE_RADIUS_2ND){
//                    do2 = true;
//                }else{
//
//                    if(dist2 < SPOT_RELEVANCE_RADIUS_2ND){
//                        do2 = true;
//                    }
//                    else{
//                        float dist16 = myLocation.distanceTo(loc.add(dir, speed * 1.6f));
//                        if(dist16 <LINE_RELEVANCE_RADIUS_2ND ){
//                            do2 = true;
//                        }
//                    }
//                }
//                if(do2){
//                    angledRectangles[angledRectangleIndex] = new DesireZone(loc1, dir, loc2, AVOID_RADIUS_2ND, DANGER_2ND + -10 * bullet.damage);
//                    angledRectangleIndex++;
//
////                    drawBullet(loc1, loc2, originalEnemyDir, AVOID_RADIUS_2ND, 34, 180, 20);
//
////                    if (allow23) {
////                        loc2 = loc2.add(originalEnemyDir, OVERLAP_2_3);
////                        dist2 = myLocation.distanceTo(loc2);
////                    }
//                }
//
//                if (allow23) {
//                    float dist25 = myLocation.distanceTo(loc.add(dir, speed * 2.5f));
//                    MapLocation loc3 = loc2.add(dir, speed);
//                    float dist3 = myLocation.distanceTo(loc3);
//
//                    if (dist2 < SPOT_RELEVANCE_RADIUS_3RD || dist3 < SPOT_RELEVANCE_RADIUS_3RD || dist25 < LINE_RELEVANCE_RADIUS_3RD) {
//                        angledRectangles[angledRectangleIndex] = new DesireZone(loc2, dir, speed + AVOID_RADIUS_3RD, AVOID_RADIUS_3RD, DANGER_3RD + -10 * bullet.damage);
//                        angledRectangleIndex++;
//                        //   drawBullet(loc2, loc3, originalEnemyDir, AVOID_RADIUS_3RD, 10, 20, 70);
//                    }
//                }
//            }
//
//            if(do1 || do2){
//                MapLocation specialLoc = loc.add(dir, dist0);
//                addSpecialMapLocation(specialLoc.add(dir.rotateLeftDegrees(85), AVOID_RADIUS_PLUS), 0);
//                addSpecialMapLocation(specialLoc.add(dir.rotateRightDegrees(85), AVOID_RADIUS_PLUS), 0);
//
//
//                addSpecialMapLocation(myLocation.add(loc.directionTo(myLocation),maxMove),0);
//
//
////                //rc.setIndicatorLine(myLocation,myLocation.add(loc.directionTo(myLocation),maxMove), 130,40,70 );
//                // //rc.setIndicatorLine(specialLoc.add(originalEnemyDir.rotateLeftDegrees(90)),specialLoc.add(originalEnemyDir.rotateRightDegrees(90)), 10, 255,200);
//            }
//        }
//    }


//    public static void bugPathing1() throws Exception{
//
//        if(R.turn - lastNeededBug > 2){
//            bugPathingDirection = 0;
//
//        }
//        if(R.turn - lastSwappedBug > 100){
//            if(bugPathingDirection == -1){
//                bugPathingDirection = 1;
//            }else if(bugPathingDirection == 1){
//                bugPathingDirection = -1;
//            }
//            lastSwappedBug = R.turn;
//        }
//        if(R.prevLocation.isWithinDistance(R.myLocation, 0.02f)){
//            lastSwappedBug -= 5;//May be stuck
//            Test.log("barely moved");
//        }
//
//
//        Direction attemptedDir = null;
//
//        float sensitivity = 30;
//
//        if(Clock.getBytecodesLeft() > 10000){
//            sensitivity = 60;
//        }
//
//        float multiplier = 0;
//        if(bugPathingDirection == -1){
//            multiplier = -170f / sensitivity;
//        }
//        else if(bugPathingDirection == 0){
//            multiplier = 85f / sensitivity;
//        } else if(bugPathingDirection == 1){
//            multiplier = 170f / sensitivity;
//        }
//
//        for(float i = 1 ; i < sensitivity; i++){
//            if(bugPathingDirection == 0) multiplier *= -1;
//            if(rc.canMove(dirToMove.rotateRightDegrees(multiplier * i),0.7f)){
//                attemptedDir= dirToMove.rotateRightDegrees(multiplier * i);
//
////                        //rc.setIndicatorLine(R.myLocation, R.myLocation.add(dirToMove.rotateRightDegrees(multiplier * i)),255,255,255);
//
//                break;
//            }
////                    //rc.setIndicatorLine(R.myLocation, R.myLocation.add(dirToMove.rotateRightDegrees(multiplier * i)),255,0,0);
//        }
//
//        if(attemptedDir == null){
//            multiplier *= 0.8f;
//            for(float i = 1 ; i < sensitivity; i++){
//                if(bugPathingDirection == 0) multiplier *= -1;
//                if(rc.canMove(dirToMove.rotateRightDegrees(multiplier * i),0.2f)){
//                    attemptedDir= dirToMove.rotateRightDegrees(multiplier * i);
//
////                            //rc.setIndicatorLine(R.myLocation, R.myLocation.add(dirToMove.rotateRightDegrees(multiplier * i)),255,255,255);
//                    break;
//                }
//
////                        //rc.setIndicatorLine(R.myLocation, R.myLocation.add(dirToMove.rotateRightDegrees(multiplier * i)),0,0,0);
//            }
//        }
//
//
//
//
//
//        if(attemptedDir == null){
//            attemptedDir = dirToMove.opposite();
//        }
//
//        float angleDifference = dirToMove.degreesBetween(attemptedDir);
//
//        float difNorth = dirToMove.degreesBetween(Direction.NORTH);
//        float difSouth = dirToMove.degreesBetween(Direction.SOUTH);
//        float difEast = dirToMove.degreesBetween(Direction.EAST);
//        float difWest = dirToMove.degreesBetween(Direction.WEST);
//
//        if(!rc.canMove(Direction.NORTH,0.2f)) difNorth = 180;
//        if(!rc.canMove(Direction.SOUTH,0.2f)) difSouth = 180;
//        if(!rc.canMove(Direction.EAST,0.2f)) difEast = 180;
//        if(!rc.canMove(Direction.WEST,0.2f)) difWest = 180;
//
//
//        Test.log("bugpathing: " + bugPathingDirection);
//
//        if(bugPathingDirection == 0){
//            float truedif = Math.abs(angleDifference);
//
//            float trueN  = Math.abs(difNorth);
//            float trueE  = Math.abs(difEast);
//            float trueS  = Math.abs(difSouth);
//            float trueW  = Math.abs(difWest);
//
//            if(trueN < truedif){
//                attemptedDir = Direction.NORTH;
//                truedif = trueN;
//            }
//            if(trueS < truedif){
//                attemptedDir = Direction.SOUTH;
//                truedif = trueS;
//            }
//            if(trueE < truedif){
//                attemptedDir = Direction.EAST;
//                truedif = trueE;
//            }
//            if(trueW < truedif){
//                attemptedDir = Direction.WEST;
//                truedif = trueW;
//            }
//
//            if(angleDifference >= 0){
//                bugPathingDirection = 1;
//                lastSwappedBug = R.turn;
//            }else{
//                bugPathingDirection = -1;
//                lastSwappedBug = R.turn;
//            }
//        } else if(bugPathingDirection == -1){
//
//            if(difNorth < 0 && difNorth > angleDifference){
//                attemptedDir = Direction.NORTH;
//                angleDifference = difNorth;
//            }
//            if(difEast < 0 && difEast > angleDifference){
//                attemptedDir = Direction.EAST;
//                angleDifference = difEast;
//            }
//            if(difSouth < 0 && difSouth > angleDifference){
//                attemptedDir = Direction.SOUTH;
//                angleDifference = difSouth;
//            }
//            if(difWest < 0 && difWest > angleDifference){
//                attemptedDir = Direction.WEST;
//                angleDifference = difWest;
//            }
//        } else if(bugPathingDirection == 1){
//
//            if(difNorth >= 0 && difNorth < angleDifference){
//                attemptedDir = Direction.NORTH;
//                angleDifference = difNorth;
//            }
//            if(difEast >= 0 && difEast < angleDifference){
//                attemptedDir = Direction.EAST;
//                angleDifference = difEast;
//            }
//            if(difSouth >= 0 && difSouth < angleDifference){
//                attemptedDir = Direction.SOUTH;
//                angleDifference = difSouth;
//            }
//            if(difWest >= 0 && difWest < angleDifference){
//                attemptedDir = Direction.WEST;
//                angleDifference = difWest;
//            }
//        }
//
//        if(Math.abs(angleDifference) < 170) {
//            dirToMove = attemptedDir;
//        }
//
//
//        lastNeededBug = R.turn;
//
//
//    }

//    public static void calculateBestMove(int minRemainingBytes) throws Exception{
//
//        int startBytecode = Clock.getBytecodesLeft();
//        moveVectorStr = -1* (new MapLocation(0,0).distanceTo(new MapLocation(moveVectorX,moveVectorY)));
//
//        //Err, what was this again??
////        if(moveVectorStr > 1){
////            moveVectorStr /= 4;
////        }
//
//
//        //We have a movement direction, now put it at the constant distance of 10 and apply a strength relative to its size
//        //This to be sure our point isn't very close to us (as we don't want it inside our movement circle)
//        //Nor very far as it can cause flatness issues (if it's very far, the differences start mattering less, even though they should matter more)
//        float finalMoveVectorX;
//        float finalMoveVectorY;
//        if(moveVectorStr > -0.05f){
//            finalMoveVectorX = R.myX;
//            finalMoveVectorY = R.myY;
//            moveVectorStr = 0;
////            Test.log("move str too weak");
//
//        }
//        else{
//            if(moveVectorX < 0){
//                if(moveVectorY < 0){
//                    float multFactor = maxMove / (-moveVectorX - moveVectorY);
//                    finalMoveVectorX = (multFactor * moveVectorX) + R.myX;
//                    finalMoveVectorY = (multFactor * moveVectorY) + R.myY;
//                }else if(moveVectorY > 0){
//                    float multFactor = maxMove / (moveVectorY -moveVectorX );
//                    finalMoveVectorX = (multFactor * moveVectorX) + R.myX;
//                    finalMoveVectorY = (multFactor * moveVectorY) + R.myY;
//                } else{
//                    float multFactor = maxMove / (-moveVectorX);
//                    finalMoveVectorX = (multFactor * moveVectorX) + R.myX;
//                    finalMoveVectorY = R.myY;
//                }
//
//            }else if(moveVectorX > 0){
//                if(moveVectorY < 0){
//                    float multFactor = maxMove / (moveVectorX - moveVectorY);
//                    finalMoveVectorX = (multFactor * moveVectorX) + R.myX;
//                    finalMoveVectorY = (multFactor * moveVectorY) + R.myY;
//                }else if(moveVectorY > 0){
//                    float multFactor = maxMove / (moveVectorX + moveVectorY);
//                    finalMoveVectorX = (multFactor * moveVectorX) + R.myX;
//                    finalMoveVectorY = (multFactor * moveVectorY) + R.myY;
//                } else{
//                    float multFactor = maxMove / (moveVectorX);
//                    finalMoveVectorX = (multFactor * moveVectorX) + R.myX;
//                    finalMoveVectorY = R.myY;
//                }
//            }
//            else{
//                if(moveVectorY < 0){
//                    float multFactor = maxMove / (-moveVectorY);
//                    finalMoveVectorX = R.myX;
//                    finalMoveVectorY = (multFactor * moveVectorY) + R.myY;
//                }else if(moveVectorY > 0){
//                    float multFactor = maxMove / (moveVectorY);
//                    finalMoveVectorX = R.myX;
//                    finalMoveVectorY = (multFactor * moveVectorY) + R.myY;
//                } else{
//                    finalMoveVectorX = 0;
//                    finalMoveVectorY = 0;
//                    moveVectorStr = 0;
////                    Test.log("movevector right underneath");
//                }
//            }
//        }
//
//        finalMove = new MapLocation(finalMoveVectorX,finalMoveVectorY);
//        dirToMove = R.myLocation.directionTo(finalMove);
//
////          //rc.setIndicatorLine(R.myLocation,R.myLocation.add(dirToMove,10), 200, 200, 200);
//        if (moveVectorStr != 0 && !R.amIScout && Clock.getBytecodesLeft() > 6000 ) {
//            if (!rc.canMove(dirToMove)) {
//                bugPathing2();
//            }
//        }
//
//        finalMove = R.myLocation.add(dirToMove, 10);
//        //      Test.log(" x: " + moveVectorX + "  Y: " + moveVectorY +  " str" + moveVectorStr);
//        //      //rc.setIndicatorLine(R.myLocation,finalMove, rc.getID() % 255, (rc.getID() + 100) % 255, (rc.getID() + 175) % 255);
//
//
//
//
//        //We might not be using all zones.
//        //It appears better to just not bother rearranging in this case
//        //Takes too many bytecodes while the point of reducing the amount of zones is to save bytecodes
//        int toSpare = (Clock.getBytecodesLeft() - minRemainingBytes) / 1000;
//        int totalZones;
//        switch (toSpare){
//            case 0:
//                totalZones = 0;
//                break;
//            case 1:
//                totalZones = 0;
//                break;
//            case 2:
//                totalZones = 1;
//                break;
//            case 3:
//                totalZones = 2;
//                break;
//            case 4:
//                totalZones = 3;
//                break;
//            case 5:
//                totalZones = 5;
//                break;
//            case 6:
//                totalZones = 7;
//                break;
//            case 7:
//                totalZones = 9;
//                break;
//            case 8:
//                totalZones = 12;
//                break;
//            case 9:
//                totalZones = 15;
//                break;
//            case 10:
//                totalZones = 18;
//                break;
//            case 11:
//                totalZones = 22;
//                break;
//            case 12:
//                totalZones = 25;
//                break;
//            case 13:
//                totalZones = 28;
//                break;
//            case 14:
//                totalZones = 30;
//                break;
//            default:
//                totalZones = 30;
//                break;
//        }
//        if(angledRectangleIndex > totalZones){
//            angledRectangleIndex = totalZones;
//            vectorCircleIndex = 0;
//            circleIndex = 0;
//            squareIndex = 0;
//        }else{
//            if(angledRectangleIndex + vectorCircleIndex > totalZones) {
//                vectorCircleIndex = totalZones - angledRectangleIndex;
//                circleIndex = 0;
//                squareIndex = 0;
//            }else {
//                if (angledRectangleIndex  + vectorCircleIndex + circleIndex > totalZones) {
//                    circleIndex = totalZones - (angledRectangleIndex + vectorCircleIndex );
//                    squareIndex = 0;
//                } else {
//                    if (angledRectangleIndex + circleIndex + squareIndex + vectorCircleIndex  > totalZones) {
//                        squareIndex = totalZones - (angledRectangleIndex + circleIndex + vectorCircleIndex );
//                    }
//                }
//            }
//        }
//
//
//
//        //Bytecode saving calculations
//        int minRemainingPlus200 = minRemainingBytes + 200;
//        angledRectangleIndexMinusOne = angledRectangleIndex - 1;
//        squareIndexMinusOne = squareIndex -1;
//        circleIndexMinusOne = circleIndex -1;
//        vectorCircleIndexMinusOne = vectorCircleIndex - 1;
//
//
//        bestMoveScore = C.CUTOFFZONEEVALUTION;//neccessary before first valuationslot call
//
//        //We want to at least consider standing still, especially in situations where we may be stuck
//        bestMove = R.myLocation;
//        evaluateLoc = R.myLocation;
//        MonsterMove.getValuationSlot();
//        bestMoveScore = lastZoneValuation;
//
//        int spotsTestedCommentThisOut = 2;
//        int trackFinalLastSpot = 0;
//
//
//        Test.log("move factor str  " + moveVectorStr);
//
//        locationevaluation:
//        {
//            //The best move is commonly right along the move vector
//            if(!R.myLocation.isWithinDistance(finalMove,0.01f)) {
//                evaluateLoc = R.myLocation.add(R.myLocation.directionTo(finalMove), maxMove);
//                if (rc.canMove(evaluateLoc)) {
////                    Test.beginClockTestAvg(7);
//                    MonsterMove.getValuationSlot();
////                    Test.endClockTestAvg(7, "average evaulate");
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = 1;
//                    }else{
//                        Test.log("evaluate: " + lastZoneValuation);
//                    }
//                }
//            }
//
//            //The best move is also commonly exactly the same thing we did last turn
//            if(lastBestMove != null) {
//                evaluateLoc = R.myLocation.translate(lastBestMove.x, lastBestMove.y);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            //Go through all of the special slots
//            for (int i = specialMapLocationsCount - 1; i >= 0; i--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = specialMapLocations[i];
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    float eval = lastZoneValuation + extraPoints[i];
//                    if (eval > bestMoveScore) {
//                        bestMoveScore = eval;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//
//            //Go through all of the special slots
//            for (int i = noBoostLocationsCount - 1; i >= 0; i--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = noBoostSpecialLocations[i];
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//
//            //So, we usually have bytecodes left after this. So go through a lot of simple angle/direction combinations
//            float distance = maxMove;
//            for (int angleIndex = 7; angleIndex >= 0; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            for (int angleIndex = 15; angleIndex >= 8; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            distance = maxMove * 0.5f;
//
//            for (int angleIndex = 31; angleIndex >= 24; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            distance = maxMove * 0.66f;
//
//            for (int angleIndex = 23; angleIndex >= 16; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            distance = maxMove * 0.33f;
//
//            for (int angleIndex = 47; angleIndex >= 40; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            distance = maxMove;
//
//            for (int angleIndex = 31; angleIndex >= 24; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            for (int angleIndex = 39; angleIndex >= 32; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//            distance = maxMove * 0.1f;
//
//            for (int angleIndex = 15; angleIndex >= 8; angleIndex--) {
//                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
//                evaluateLoc = R.myLocation.add(someDirections[angleIndex], distance);
//                if(rc.canMove(evaluateLoc)) {
//                    MonsterMove.getValuationSlot();
//                    if (lastZoneValuation > bestMoveScore) {
//                        bestMoveScore = lastZoneValuation;
//                        bestMove = evaluateLoc;
//                        trackFinalLastSpot = spotsTestedCommentThisOut;
//                    }
//                }
//                spotsTestedCommentThisOut++;
//            }
//
//        }
//
//
//
////
//        Test.log("Movement,  Spots:  "   +  spotsTestedCommentThisOut +   "  Zones: " +( MonsterMove.angledRectangleCount + MonsterMove.circleCount +squareIndex+vectorCircleIndex) +   "  bc: " + startBytecode + "->" + Clock.getBytecodesLeft());
//        Test.log("best spot: " + trackFinalLastSpot + " pos: " + bestMove + " best score: " + bestMoveScore);
//
//
//    }


//
//
//
//    public static void bugPathing2() throws Exception {
//        if (R.turn - lastNeededBug > 2) {
//            bugPathingDirection = 0;
//        }
//        if (R.turn - lastSwappedBug > 250) {
//            if (bugPathingDirection == -1) {
//                bugPathingDirection = 1;
//            } else if (bugPathingDirection == 1) {
//                bugPathingDirection = -1;
//            }
//            lastSwappedBug = R.turn;
//        }
//        if(R.turn - lastSwappedBug > 15) {
//            if (R.beforePrevLocation.isWithinDistance(R.myLocation, 0.1f)) {
//                //Probably stuck
//
//                if (bugPathingDirection == -1) {
//                    bugPathingDirection = 1;
//                } else if (bugPathingDirection == 1) {
//                    bugPathingDirection = -1;
//                }
//                lastSwappedBug = R.turn;
//                Test.log("barely moved");
//            }
//        }
//
//        lastNeededBug = R.turn;
//        RobotInfo[] plausibleBlockingRobots = rc.senseNearbyRobots(R.radius + maxMove - C.SAFETY);
//        TreeInfo[] plausibleBlockingTrees = rc.senseNearbyTrees(R.radius + maxMove - C.SAFETY);
//
//        if (plausibleBlockingRobots.length == 0 && plausibleBlockingTrees.length == 0) return; //Shouldnt really happen,
//
//        Test.log("trees: " + plausibleBlockingTrees.length);
//
//        MapLocation bestBugSpot = null;
//        float bestScore = -1000000;
//        boolean bestLeft = false;
//
//
//        for (int i = plausibleBlockingRobots.length - 1; i >= 0; i--) {
//            MapLocation[] spots = Helper.FindCircleIntersectionPoints(R.myLocation, maxMove, plausibleBlockingRobots[i].location, plausibleBlockingRobots[i].getRadius() + R.radius + C.SAFETY);
//            if (spots != null) {
//                boolean spot1Left =  dirToMove.degreesBetween(R.myLocation.directionTo(spots[0])) > 0;
//                boolean spot2Left =  dirToMove.degreesBetween(R.myLocation.directionTo(spots[1])) > 0;
//
//                if ((spot1Left && bugPathingDirection <= 0)||(!spot1Left && bugPathingDirection>=0)) {
//                    MapLocation m = spots[0];
//                    if (rc.onTheMap(m)) {
//                        if (rc.canMove(m)) {
//                            float score = -m.distanceTo(finalMove);
//                            if (score > bestScore) {
//                                bestScore = score;
//                                bestBugSpot = m;
//                                bestLeft = true;
//                            }
//
//                            extraPoints[specialMapLocationsCount] = 3;
//                            specialMapLocations[specialMapLocationsCount++] = m;
//
//                            //rc.setIndicatorLine(R.myLocation, m, 0, 100, 0);
//                        }
//                    }
//                }
//                if ((spot2Left && bugPathingDirection <= 0)||(!spot2Left && bugPathingDirection>=0)) {
//                    MapLocation m = spots[1];
//                    if (rc.onTheMap(m)) {
//                        if (rc.canMove(m)) {
//                            float score = -m.distanceTo(finalMove);
//                            if (score > bestScore) {
//                                bestScore = score;
//                                bestBugSpot = m;
//                                bestLeft = false;
//                            }
//
//                            extraPoints[specialMapLocationsCount] = 3;
//                            specialMapLocations[specialMapLocationsCount++] = m;
//
//                            //rc.setIndicatorLine(R.myLocation, m, 0, 0, 100);
//                        }
//                    }
//                }
//            }
//        }
//
//        for (int i = plausibleBlockingTrees.length - 1; i >= 0; i--) {
//            MapLocation[] spots = Helper.FindCircleIntersectionPoints(R.myLocation, maxMove, plausibleBlockingTrees[i].location, plausibleBlockingTrees[i].getRadius() + R.radius + (2f * C.SAFETY));
//            if (spots != null) {
//
//                boolean spot1Left = dirToMove.degreesBetween(R.myLocation.directionTo(spots[0])) > 0;
//                boolean spot2Left = dirToMove.degreesBetween(R.myLocation.directionTo(spots[1])) > 0;
//
//                if ((spot1Left && bugPathingDirection <= 0) || (!spot1Left && bugPathingDirection >= 0)) {
//                    MapLocation m = spots[0];
//                    if (rc.onTheMap(m)) {
//                        if (rc.canMove(m)) {
//                            float score = -m.distanceTo(finalMove);
//                            if (score > bestScore) {
//                                bestScore = score;
//                                bestBugSpot = m;
//                                bestLeft = true;
//                            }
//                            extraPoints[specialMapLocationsCount] = 3;
//                            specialMapLocations[specialMapLocationsCount++] = m;
//
//                            //rc.setIndicatorLine(R.myLocation, m, 0, 150, 0);
//                        } else {
//                            //rc.setIndicatorLine(R.myLocation, m, 100, 0, 0);
//                        }
//                    }
//                }
//                if ((spot2Left && bugPathingDirection <= 0) || (!spot2Left && bugPathingDirection >= 0)) {
//                    MapLocation m = spots[1];
//                    if (rc.onTheMap(m)) {
//                        if (rc.canMove(m)) {
//                            float score = -m.distanceTo(finalMove);
//                            if (score > bestScore) {
//                                bestScore = score;
//                                bestBugSpot = m;
//                                bestLeft = false;
//                            }
//                            extraPoints[specialMapLocationsCount] = 3;
//                            specialMapLocations[specialMapLocationsCount++] = m;
//
//                            //rc.setIndicatorLine(R.myLocation, m, 0, 0, 150);
//                        } else {
//                            //rc.setIndicatorLine(R.myLocation, m, 100, 0, 0);
//                        }
//
//                    }
//                }
//
//
//            }
//        }
//
//        if (bestBugSpot != null) {
//            dirToMove = R.myLocation.directionTo(bestBugSpot);
//            if (bugPathingDirection == 0) {
//                if (bestLeft) bugPathingDirection = -1;
//                else bugPathingDirection = 1;
//
//                lastSwappedBug = R.turn;
//            }
//        }
//
//    }

}
