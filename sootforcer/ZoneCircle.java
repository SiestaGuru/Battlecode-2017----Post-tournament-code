package sootforcer;

import battlecode.common.MapLocation;

/**
 * Created by Hermen on 29/4/2017.
 */
public class ZoneCircle {

    public float desire;
    public MapLocation center;
    public float radius;

    public ZoneCircle(MapLocation map, float size, float desire){
        this.desire = desire;
        center = map;
        radius = size;
    }
}
