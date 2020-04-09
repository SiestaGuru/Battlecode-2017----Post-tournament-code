package sootforcer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * Created by Hermen on 29/4/2017.
 */
public class ZoneAngledRectangle {

    public float p21x;
    public float p21y;
    public float p41x;
    public float p41y;
    public float p21MagSquared;
    public float p41MagSquared;
    public float x1;
    public float y1;
    public float desire;


    public ZoneAngledRectangle(MapLocation start, Direction dir, float lengthplusavoid, float avoidwidth, float desire){
        this.desire = desire;
        Direction left = dir.rotateLeftDegrees(90);
        MapLocation leftBehind = start.subtract(dir,avoidwidth).add(left,avoidwidth);
        MapLocation leftAhead = start.add(dir,lengthplusavoid).add(left,avoidwidth);
        MapLocation rightAhead = leftAhead.subtract(left,avoidwidth*2);

        x1 = leftAhead.x;
        y1 = leftAhead.y;

        p21x = rightAhead.x - x1;
        p21y = rightAhead.y - y1;
        p41x = leftBehind.x - x1 ;
        p41y = leftBehind.y - y1;
        p21MagSquared = p21x * p21x + p21y * p21y;
        p41MagSquared = p41x * p41x + p41y * p41y;
    }

    public ZoneAngledRectangle(MapLocation start, Direction dir, MapLocation end, float avoidwidth, float desire){
        this.desire = desire;
        Direction left = dir.rotateLeftDegrees(90);
        MapLocation leftStart = start.subtract(dir,avoidwidth).add(left,avoidwidth);
        MapLocation leftEnd = end.add(dir,avoidwidth).add(left,avoidwidth);
        MapLocation rightEnd = leftEnd.subtract(left,avoidwidth*2);

        x1 = leftEnd.x;
        y1 = leftEnd.y;

        p21x = rightEnd.x - x1;
        p21y = rightEnd.y - y1;
        p41x = leftStart.x - x1 ;
        p41y = leftStart.y - y1;
        p21MagSquared = p21x * p21x + p21y * p21y;
        p41MagSquared = p41x * p41x + p41y * p41y;
    }

}
