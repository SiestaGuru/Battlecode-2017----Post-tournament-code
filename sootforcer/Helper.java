package sootforcer;

import battlecode.common.*;
import sootforcer.C;
import sootforcer.R;
import sootforcer.Test;

/**
 * Created by Hermen on 13/5/2017.
 */
public class Helper extends sootforcer.R {



    public static float[] floatArrayBase = new float[0];

    //Ignoring circles within circles, as that's almost never relevant because of collisions
    //Also ignoring 1 intersction point situations (will return 2 spots at the location)
    public static MapLocation[] FindCircleIntersectionPoints(MapLocation m1, float radius1, MapLocation m2, float radius2){
        float dist = m1.distanceTo(m2);
        if(dist > radius1 + radius2 || dist < sootforcer.R.floatSafety) return null; //Can be skipped if proper dist checks are done beforehand

        float distFrom1ToInterSectionLine = ((radius1*radius1) - (radius2 * radius2) + (dist * dist)) / (2f * dist);
        float distOnIntersectionLine = (float)Math.sqrt((radius1*radius1)-(distFrom1ToInterSectionLine * distFrom1ToInterSectionLine));

        Direction dir = m1.directionTo(m2);
        Direction dirLeft = dir.rotateLeftDegrees(90);
        MapLocation intersectionLineMiddle = m1.add(dir,distFrom1ToInterSectionLine);
//        Test.log("d1: " + distFrom1ToInterSectionLine   +" d2:  " + distOnIntersectionLine );
        return new MapLocation[]{intersectionLineMiddle.add(dirLeft,distOnIntersectionLine),intersectionLineMiddle.subtract(dirLeft,distOnIntersectionLine)};
    }


    //Returns null if circles overlap
    public static MapLocation findSpotBetweenTwoCircles(MapLocation m1, float radius1, MapLocation m2, float radius2){
        if( m1.isWithinDistance(m2,radius1 + radius2)) return null;
        return m1.add(m1.directionTo(m2),radius1 + (m1.distanceTo(m2) - radius1 - radius2)/2f);
    }

    //Returns null if not enough space for us
    public static MapLocation findFitSpotBetweenTwoCircles(MapLocation m1, float radius1, MapLocation m2, float radius2){
        if( m1.isWithinDistance(m2,radius1 + radius2 + R.radius*2f)) return null;
        return m1.add(m1.directionTo(m2),radius1 + (m1.distanceTo(m2) - radius1 - radius2)/2f);
    }


    //Is comparisonDir clockwise compared to baseDir
    public static boolean isClockwise(Direction baseDir, Direction comparisonDir){
        return baseDir.degreesBetween(comparisonDir) < 0;
    }


    //Does a line collide with a circle
    public static boolean lineCollision(MapLocation lineOrigin, Direction dir, MapLocation circleCenter, float radius) {
        float angle = dir.radiansBetween(lineOrigin.directionTo(circleCenter));
        if (Math.abs(angle) > sootforcer.C.DEGREES_90_IN_RADS) {
            return false;
        }
        return ((float) Math.abs(lineOrigin.distanceTo(circleCenter) * Math.sin(angle)) <= radius);
    }

    //This is not 100% accurate, but gets close enough to be worth the bytecode savings. It has a few false negatives:
    //If the closest distance is further than the max distance, but there's also a spot closer than maxdistance that would hit it
    //This returns false
    public static boolean lineCollisionSection(MapLocation origin, Direction dir, MapLocation circleCenter, float radius, float segmentLength) {
        float angle = dir.radiansBetween(origin.directionTo(circleCenter));
        if (Math.abs(angle) > sootforcer.C.DEGREES_90_IN_RADS) {
            return false;
        }
        float dist = origin.distanceTo(circleCenter);

        if(dist > segmentLength || Math.abs(dist * Math.cos(angle)) > segmentLength ) return false;
        return Math.abs(dist * Math.sin(angle)) <= radius;
    }




    public static boolean freeFiringShot(Direction dir, float maxDistance, float extraSafety, boolean canIgnoreTrees) throws GameActionException{

        int count = robotsAlly.length;

        if(!canIgnoreTrees){
            count += trees.length;
        }

        if(count > 7){
            return freeFiringShotCircles(dir,maxDistance); //saves bytecode if there's a ton of stuff since it as a constant cost, less precise though
        }else{
            if(canIgnoreTrees){
                return freeFiringShotLinesIgnoreTrees(dir,maxDistance, extraSafety);
            }else{
                return freeFiringShotLines(dir,maxDistance, extraSafety);
            }
        }

    }

    public static boolean freeFiringShotLines(Direction dir, float maxDistance, float safety){
        for(int i = robotsAlly.length - 1; i >= 0; i--){
            float angle = dir.radiansBetween(myLocation.directionTo(robotsAlly[i].location));
            if (Math.abs(angle) > sootforcer.C.DEGREES_90_IN_RADS) {
                continue;
            }
            float dist = myLocation.distanceTo(robotsAlly[i].location);
            if(dist > maxDistance) continue;
            if ( Math.abs(dist * Math.sin(angle)) <= Float.sum(robotsAlly[i].getRadius(), safety)) return false;
        }

        for(int i = trees.length - 1; i >= 0; i--){
            float angle = dir.radiansBetween(myLocation.directionTo(trees[i].location));
            if (Math.abs(angle) > sootforcer.C.DEGREES_90_IN_RADS) {
                continue;
            }
            float dist = myLocation.distanceTo(trees[i].location);
            if(dist > maxDistance) continue;
            if ( Math.abs(dist * Math.sin(angle)) <= trees[i].getRadius()) return false;
        }
        return true;
    }

    public static boolean freeFiringShotLinesIgnoreTrees(Direction dir, float maxDistance, float safety){
        for(int i = robotsAlly.length - 1; i >= 0; i--){
            float angle = dir.radiansBetween(myLocation.directionTo(robotsAlly[i].location));
            if (Math.abs(angle) > C.DEGREES_90_IN_RADS) {
                continue;
            }
            float dist = myLocation.distanceTo(robotsAlly[i].location);
            if(dist > maxDistance) continue;
            if ( Math.abs(dist * Math.sin(angle)) <= Float.sum(robotsAlly[i].getRadius() , safety)){
                sootforcer.Test.lineTo(dir,2);
                return false;
            }
        }
        return true;
    }

    //Will also return false if an enemy is in the way
    public static boolean freeFiringShotCircles(Direction dir, float distanceToObject) throws GameActionException{


        float start = radius + GameConstants.BULLET_SPAWN_OFFSET;
        MapLocation m = myLocation.add(dir,start);

        if(distanceToObject - start  < 1f) return true; //very unlikely anything is in between in such a short distance

        distanceToObject = Math.min(distanceToObject - floatSafety, sightradius - floatSafety);

        float sixteenthSection = (distanceToObject - start) / 16f;

        if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 4f),sixteenthSection * 4f)){
            if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 2f),sixteenthSection * 2f)){
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection),sixteenthSection)){
                    sootforcer.Test.lineTo(dir,2);

                    return false;
                }
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 3f),sixteenthSection)){
                    sootforcer.Test.lineTo(dir,2);

                    return false;
                }
            }
            if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 6f),sixteenthSection * 2f)){
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 5f),sixteenthSection)){
                    sootforcer.Test.lineTo(dir,2);

                    return false;
                }
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 7f),sixteenthSection)){
                    sootforcer.Test.lineTo(dir,2);

                    return false;
                }
            }
        }
        if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 12f),sixteenthSection * 4f)){
            if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 10f),sixteenthSection * 2f)){
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 9f),sixteenthSection)){
                    sootforcer.Test.lineTo(dir,2);

                    return false;
                }
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 11f),sixteenthSection)){
                    sootforcer.Test.lineTo(dir,2);

                    return false;
                }
            }
            if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 14f),sixteenthSection * 2f)){
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 13f),sixteenthSection)){
                    sootforcer.Test.lineTo(dir,2);

                    return false;
                }
                if(rc.isCircleOccupiedExceptByThisRobot(    m.add(dir, sixteenthSection * 15f),sixteenthSection)){
                    Test.lineTo(dir,2);

                    return false;
                }
            }
        }
        return true;
    }




    //Returns a random int from 0 (inclusive) to max (exclusive)
    //Relies on the cheapness of methods used inside, might have to alter this if bytecode costs change in the future
    public static int randomInt(int max){
        return Math.floorMod(Math.addExact( Clock.getBytecodeNum(),Math.addExact( Clock.getBytecodesLeft(),    Math.multiplyExact(Clock.getBytecodeNum(), Math.floorDiv(Math.addExact(Math.multiplyExact(Clock.getBytecodesLeft(), Clock.getBytecodeNum()), Clock.getBytecodeNum()),Clock.getBytecodesLeft())))) , max);
    }
    public static int randomInt(int max,int seed){
        return Math.floorMod(Math.addExact( Clock.getBytecodeNum(),Math.addExact( Clock.getBytecodesLeft(),    Math.multiplyExact(Clock.getBytecodeNum(), Math.floorDiv(Math.addExact(Math.multiplyExact(Math.addExact(Clock.getBytecodesLeft(),seed), Clock.getBytecodeNum()), Clock.getBytecodeNum()),Clock.getBytecodesLeft())))) , max);
    }


    //In performance heavy sections, just inline these. Usually neater/more readable to use them though
    public static MapLocation towards(MapLocation to, float distance){
        return myLocation.add(myLocation.directionTo(to), distance);
    }
    public static MapLocation towards(MapLocation from, MapLocation to, float distance){
        return from.add(from.directionTo(to), distance);
    }
    public static MapLocation towardsFactor(MapLocation to, float factor){
        return myLocation.add(myLocation.directionTo(to), factor * myLocation.distanceTo(to));
    }
    public static MapLocation towardsFactor(MapLocation from, MapLocation to, float factor){
        return from.add(from.directionTo(to), factor * myLocation.distanceTo(to));
    }
    public static MapLocation towardsCollisionCheck(MapLocation to, float objectRadius, float distance){
        return myLocation.add(myLocation.directionTo(to),Math.min(distance, myLocation.distanceTo(to) - (objectRadius + radius + floatSafety)));
    }
    public static MapLocation towardsCollisionCheck(MapLocation from, float fromRadius, MapLocation to, float toRadius,  float distance ){
        return from.add(from.directionTo(to),Math.min(distance, from.distanceTo(to) - (toRadius + fromRadius + floatSafety)));
    }



}
