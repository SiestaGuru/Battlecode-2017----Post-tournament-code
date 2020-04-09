package sootforcer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * Created by Hermen on 28/4/2017.
 */
public class ShotAnalyzer {


    public int turnShot;
    public float bulletSpeed;
    public Direction originalEnemyDir;
    public int enemyId;
    public MapLocation firingLocation;
    public MapLocation bulletStartLocation;
    public float firedAtDist;
    public float predictedEscapeAngle;
    public Direction actuallyFiredAt;
    public int directionTracker;

}
