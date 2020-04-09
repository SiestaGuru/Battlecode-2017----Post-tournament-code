package sootforcer;

import battlecode.common.MapLocation;
import sootforcer.M;
import sootforcer.ZoneAngledRectangle;
import sootforcer.ZoneCircle;
import sootforcer.ZoneCircleVector;
import sootforcer.ZoneSquare;

/**
 * Created by Hermen on 1/5/2017.
 */
public class MonsterMoveReadable {


    public static int angledRectangleCount = 0;
    public static int circleCount = 0;




    public static void getValuationSlot(){
        //There's a bytecode overhead for accessing class variables
        float locX = sootforcer.M.evaluateLoc.x;
        float locY = sootforcer.M.evaluateLoc.y;

        sootforcer.M.lastZoneValuation = sootforcer.M.evaluateLoc.distanceTo(sootforcer.M.finalMove) * sootforcer.M.moveVectorStr;

        //Change the valuation if this spot is inside of any of the desirezones
        //Heaviest part of program due to double loop. Heavily optimized, but still takes a lot
        //Theoretically, everything could be unpacked from the loops, with reused variables for some bytecode gains
        //But that's a little too insane. Like through having 50 zone.x variables
        //Ok, i did it..
        for (int i = sootforcer.M.angledRectangleIndexMinusOne; i >= 0; i--) {
            ZoneAngledRectangle zone = sootforcer.M.angledRectangles[i];
            final float test1 = (locX - zone.x1) * zone.p21x + (locY - zone.y1) * zone.p21y;
            if ( test1 >= 0f && test1 <= zone.p21MagSquared) {
                final float test2 = (locX - zone.x1) * zone.p41x + (locY - zone.y1) * zone.p41y;
                if (test2 >= 0f && test2 <= zone.p41MagSquared) {
                    sootforcer.M.lastZoneValuation += zone.desire;
                }
            }
        }

        for (int i = sootforcer.M.vectorCircleIndexMinusOne; i >= 0; i--) {
            sootforcer.ZoneCircleVector zone = sootforcer.M.vectorCircles[i];
            float distance = sootforcer.M.evaluateLoc.distanceTo(zone.center);
            if(distance < zone.radius){
                sootforcer.M.lastZoneValuation += zone.edgeDesire - distance * zone.distanceDesire;
            }
        }

        for (int i = sootforcer.M.circleIndexMinusOne; i >= 0; i--) {
            sootforcer.ZoneCircle zone = sootforcer.M.circles[i];
            if(sootforcer.M.evaluateLoc.isWithinDistance(zone.center,zone.radius)){
                sootforcer.M.lastZoneValuation += zone.desire;
            }
        }

        for (int i = sootforcer.M.squareIndexMinusOne; i >= 0; i--) {
            sootforcer.ZoneSquare zone = sootforcer.M.squares[i];
            if (zone.left < locX && locX < zone.right && locY < zone.top && locY > zone.bot) {
                sootforcer.M.lastZoneValuation += zone.desire;
            }
        }
    }







// Slightly more readable versions of the shape-adding functions, these weren't optimized fully yet
//Commented out because they're not supposed to be used

//    public static void add(MapLocation start, Direction dir, MapLocation end, float avoidwidth, float desire){
//        switch (zoneCount){
//            case 0:
//                Z1desire = desire;
//                Direction left = dir.rotateLeftDegrees(90);
//                MapLocation leftStart = start.subtract(dir,avoidwidth).add(left,avoidwidth);
//                MapLocation leftEnd = end.add(dir,avoidwidth).add(left,avoidwidth);
//                MapLocation rightEnd = leftEnd.subtract(left,avoidwidth*2);
//                Z1x1 = leftEnd.x;
//                Z1y1 = leftEnd.y;
//                Z1p21x = rightEnd.x - Z1x1;
//                Z1p21y = rightEnd.y - Z1y1;
//                Z1p41x = leftStart.x - Z1x1 ;
//                Z1p41y = leftStart.y - Z1y1;
//                Z1p21MagSquared = Z1p21x * Z1p21x + Z1p21y * Z1p21y;
//                Z1p41MagSquared = Z1p41x * Z1p41x + Z1p41y * Z1p41y;
//                zoneCount++;
//                break;
//        }
//    }
//
//    public static void addDontAvoidEnds(MapLocation start, Direction dir, MapLocation end, float avoidwidth, float desire){
//
//        switch (zoneCount){
//            case 0:
//                Z1desire = desire;
//                Direction left = dir.rotateLeftDegrees(90);
//                MapLocation leftBehind = start.add(left,avoidwidth);
//                MapLocation leftAhead = end.add(left,avoidwidth);
//                MapLocation rightAhead = end.subtract(left,avoidwidth);
//                Z1x1 = leftAhead.x;
//                Z1y1 = leftAhead.y;
//                Z1p21x = rightAhead.x - Z1x1;
//                Z1p21y = rightAhead.y - Z1y1;
//                Z1p41x = leftBehind.x - Z1x1 ;
//                Z1p41y = leftBehind.y - Z1y1;
//                Z1p21MagSquared = Z1p21x * Z1p21x + Z1p21y * Z1p21y;
//                Z1p41MagSquared = Z1p41x * Z1p41x + Z1p41y * Z1p41y;
//                zoneCount++;
//                break;
//        }
//    }
//
//
//
//    public static void add4(MapLocation leftBehind, MapLocation leftAhead, MapLocation rightAhead, float desire){
//
//        switch (zoneCount){
//            case 0:
//                Z1desire = desire;
//                Z1x1 = leftAhead.x;
//                Z1y1 = leftAhead.y;
//                Z1p21x = rightAhead.x - Z1x1;
//                Z1p21y = rightAhead.y - Z1y1;
//                Z1p41x = leftBehind.x - Z1x1 ;
//                Z1p41y = leftBehind.y - Z1y1;
//                Z1p21MagSquared = Z1p21x * Z1p21x + Z1p21y * Z1p21y;
//                Z1p41MagSquared = Z1p41x * Z1p41x + Z1p41y * Z1p41y;
//                zoneCount++;
//                break;
//        }
//    }


    public static void addCircle(MapLocation map, float size, float desire){
        switch (circleCount){
            case 0:
                Z1c_desire = desire;
                Z1c_center = map;
                Z1c_size = size;
                circleCount++;
                break;
        }
    }

    public static float Z1c_desire;
    public static MapLocation Z1c_center;
    public static float Z1c_size;

}
