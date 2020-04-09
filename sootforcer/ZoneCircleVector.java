package sootforcer;

import battlecode.common.MapLocation;

/**
 * Created by Hermen on 29/4/2017.
 */
public class ZoneCircleVector {

    public float edgeDesire;
    public float distanceDesire;

    public MapLocation center;
    public float radius;

    //Edge desire is the desire difference between being on the circle and off
    //Distance desire is the desire per distance as long as you're on the circle.
    public ZoneCircleVector(MapLocation map, float size, float edgeDesire, float distanceDesire){
        this.edgeDesire = edgeDesire + (distanceDesire*size);
        this.distanceDesire = distanceDesire;
        center = map;
        radius = size;
    }
}
