package sootforcer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * Created by Hermen on 23/4/2017.
 */
public class DesireZone {


    public float left;
    public float right;
    public float top;
    public float bot;
    public float desire;
    public boolean circular;
    public MapLocation center;
    public boolean bulletLine;


    public float x1;
    public float y1;

    public float p21x;
    public float p21y;
    public float p41x;
    public float p41y;
    public float p21MagSquared;
    public float p41MagSquared;


    public float radius;

    public DesireZone(MapLocation map, float size, float desire){
        left = map.x - size;
        right = map.x + size;
        top = map.y - size;
        bot = map.y + size;
        this.desire = desire * 2;
    }

    //Circular zones are more accurate, but have a performance impact, allowing less movement slots to be checked
    public DesireZone(MapLocation map, float size, float desire, boolean circular){
        this.desire = desire * 2;
        if(circular){
            center = map;
            radius = size;
            this.circular = true;
        }else{
            left = map.x - size;
            right = map.x + size;
            top = map.y - size;
            bot = map.y + size;
        }
    }

    public DesireZone(float l, float r, float t, float b, float desire){
        this.left = l;
        this.right = r;
        this.top = t;
        this.bot = b;
        this.desire = desire * 2;
    }



    public DesireZone(MapLocation start, Direction dir, float lengthplusavoid, float avoidwidth, float desire){
        this.bulletLine = true;
        this.desire = desire;
        MapLocation leftBehind = start.add(dir,-avoidwidth).add(dir.rotateLeftDegrees(90),avoidwidth);
        MapLocation leftAhead = start.add(dir,lengthplusavoid).add(dir.rotateLeftDegrees(90),avoidwidth);
        MapLocation rightAhead = start.add(dir,lengthplusavoid).add(dir.rotateRightDegrees(90),avoidwidth);

        x1 = leftAhead.x;
        y1 = leftAhead.y;

        p21x = rightAhead.x - x1;
        p21y = rightAhead.y - y1;
        p41x = leftBehind.x - x1 ;
        p41y = leftBehind.y - y1;
        p21MagSquared = p21x * p21x + p21y * p21y;
        p41MagSquared = p41x * p41x + p41y * p41y;
    }

    public DesireZone(MapLocation start, Direction dir, MapLocation end, float avoidwidth, float desire){
        this.bulletLine = true;
        this.desire = desire;
        MapLocation leftBehind = start.add(dir,-avoidwidth).add(dir.rotateLeftDegrees(90),avoidwidth);
        MapLocation leftAhead = end.add(dir,avoidwidth).add(dir.rotateLeftDegrees(90),avoidwidth);
        MapLocation rightAhead = end.add(dir,avoidwidth).add(dir.rotateRightDegrees(90),avoidwidth);

        x1 = leftAhead.x;
        y1 = leftAhead.y;

        p21x = rightAhead.x - x1;
        p21y = rightAhead.y - y1;
        p41x = leftBehind.x - x1 ;
        p41y = leftBehind.y - y1;
        p21MagSquared = p21x * p21x + p21y * p21y;
        p41MagSquared = p41x * p41x + p41y * p41y;
    }
}
