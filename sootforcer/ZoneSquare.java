package sootforcer;

import battlecode.common.MapLocation;

/**
 * Created by Hermen on 29/4/2017.
 */
public class ZoneSquare {

    public float left;
    public float right;
    public float top;
    public float bot;
    public float desire;

    public ZoneSquare(MapLocation map, float size, float desire){
        left = map.x - size;
        right = map.x + size;
        top = map.y + size;
        bot = map.y - size;
        this.desire = desire;
    }

    public ZoneSquare(float l, float r, float t, float b, float desire){
        this.left = l;
        this.right = r;
        this.top = t;
        this.bot = b;
        this.desire = desire;
    }

}
