package sootforcer;

import battlecode.common.*;
import sootforcer.*;
import sootforcer.C;
import kotlin.jvm.internal.Intrinsics;

//All movement related code
//Probably mostly reusable in any continous setting with decent bytecode allowance (8000 - 15000)
public class M extends R {


    public static MapLocation moveVector;
    public static MapLocation zeroVector = new MapLocation(0,0);



    public static MapLocation finalMove;
//    public static DesireZone[] zones = new DesireZone[200];
    public static MapLocation[] specialMapLocations = new MapLocation[70];
    public static MapLocation[] noBoostSpecialLocations = new MapLocation[70];

    public static float[] extraPoints = new float[70];
    public static int specialMapLocationsCount = 0;
    public static int noBoostLocationsCount = 0;

    public static Direction[] someDirections = new Direction[48];
    public static int totalDirections = 48;
//    public static int zoneLoc = 0;
//    public static int zoneLocMinusOne = 0;
    public static float lastZoneValuation = 0;
    public static MapLocation evaluateLoc = null;
    public static float moveVectorStr;


    public static float[] moveSpotsX  = new float[60];
    public static float[] moveSpotsY  = new float[60];



    // Oversized arrays. Only a few slots are used, rest is just so there's no need to check whether we're going out of array bounds
    public static int angledRectangleIndex = 0;
    public static int angledRectangleIndexMinusOne = 0;
    public static ZoneAngledRectangle[] angledRectangles = new ZoneAngledRectangle[40];

    public static int squareIndex = 0;
    public static int squareIndexMinusOne = 0;
    public static ZoneSquare[] squares = new ZoneSquare[40];

    public static int circleIndex = 0;
    public static int circleIndexMinusOne = 0;
    public static ZoneCircle[] circles = new ZoneCircle[40];

    public static int vectorCircleIndex = 0;
    public static int vectorCircleIndexMinusOne = 0;
    public static ZoneCircleVector[] vectorCircles = new ZoneCircleVector[40];

    public static MapLocation lastBestMove = null;
    public static MapLocation bestMove = null;
    public static float bestMoveScore;


    public static float maxMove;
    public static RobotController rc;


    public static int bugPathingDirection; //0 = unknown,  1 = right, -1 = left
    public static int lastSwappedBug;
    public static int lastNeededBug;


    public static Direction dirToMove;

    private static boolean wasBugPathingLastTurn = false;
    private static int walkingWrongWayCounter = 0;

    private static String longtermMoveHistory = "";

    public static boolean doBugPathingThisTurn;


    //Drops off somewhat gently in the last 10% of basedamage
    public static float[] V_EndDropOff = new float[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0.95f,0,8f,0.6f,0.35f,0.1f,0f};
    public static float[] V_LinearDropOff = new float[]{1,0.95f,0.9f,0.85f,0.8f,0.75f,0.7f,0.65f,0.6f,0.55f,0.5f,0.45f,0.4f,0.35f,0.3f,0.25f,0.2f,0.15f,0.1f,0.05f,0f};
    public static float[] V_ExpoCenter = new float[]{1,0.8f,0.65f,0.53f,0.43f,0.35f,0.29f,0.25f,0.21f,0.17f, 0.14f, 0.11f,0.08f,0.05f,0.02f,0f};


    public static void init(){
        maxMove = R.maxMove;
        rc = R.rc;
        initiateDirections();
    }


    public static void prestep(){
        squareIndex = 0;
        circleIndex = 0;
        vectorCircleIndex = 0;
        angledRectangleIndex = 0;
        specialMapLocationsCount = 0;
        noBoostLocationsCount = 0;
        moveVector = new MapLocation(0,0);


        doBugPathingThisTurn = true;
    }


    public static void addVector(MapLocation m, float force){
        //Other versions in bytecodetests. This is by cheapest found, given the cheap maplocation functions
        moveVector = moveVector.add(zeroVector.directionTo(m.translate(negMyX,negMyY)),force);
        // Test.debug_cross(m,1,R.DARKGRAY, Test.Modes.MOVEMENT);
        // Test.lineTo(m,R.LIGHTGRAY, Test.Modes.MOVEMENT);
    }

    //A normal vector above maxDistance, gently goes down to 0 where distance > 0 and distance < baseDistance
    public static void addInwardsSqrtVector(MapLocation m, float force, float maxDistance){
        float dist = m.distanceTo(R.myLocation);
        if (dist < maxDistance){
            force = force * (float)(Math.sqrt(dist) / Math.sqrt(maxDistance));
        }
        moveVector = moveVector.add(zeroVector.directionTo(m.translate(negMyX,negMyY)),force);
    }
    //A normal vector above maxDistance, gently goes down to 0 where distance > minDistance and distance < baseDistance
    public static void addInwardsSqrtVector(MapLocation m, float force, float minDistance,float maxDistance){
        float dist = m.distanceTo(R.myLocation);
        if (dist < minDistance){
            return;
        }
        else if (dist < maxDistance){
            force = force * (float)(Math.sqrt(dist-minDistance) / Math.sqrt(maxDistance-minDistance));
        }
        moveVector = moveVector.add(zeroVector.directionTo(m.translate(negMyX,negMyY)),force);
    }

    //Doesn't do anything above max distance. Below that, a gentle curve between 0 and maxDistance. (0 at maxdistance, 1*force at distance 0)
    public static void addSqrtVector(MapLocation m, float force, float maxDistance){
        float dist = m.distanceTo(R.myLocation);
        if (dist > maxDistance){
            return;
        }
        force = force * (float)(Math.sqrt(maxDistance-dist) / Math.sqrt(maxDistance));
        moveVector = moveVector.add(zeroVector.directionTo(m.translate(negMyX,negMyY)),force);
    }
    //Doesn't do anything above max distance. Below that, a gentle curve between 0 and maxDistance. (0 at maxdistance, 1*force at distance 0)
    public static void addSqrtVector(MapLocation m, float force, float minDistance, float maxDistance){
        float dist = m.distanceTo(R.myLocation);
        if (dist > maxDistance){
            return;
        }else if(dist > minDistance){
            force = force * (float)(Math.sqrt(maxDistance-dist) / Math.sqrt(maxDistance-minDistance));
        }
        moveVector = moveVector.add(zeroVector.directionTo(m.translate(negMyX,negMyY)),force);
    }

    //Adds a vector with a force proportional to what is found in a lookup table of floats
    //It picks the entry that corresponds to the distance/distanceBase * arraylength.
    //So it picks the first entry of the table if distance/distancebase = 0, the last if it's 1 (and picks the closest match in all cases)

    public static void addVectorDistanceArray(MapLocation m, float force, float distanceBase, float[] array){
        int l = array.length -1;
        moveVector = moveVector.add(zeroVector.directionTo(m.translate(negMyX,negMyY)),force *  array[Math.min(l, Math.round((m.distanceTo(R.myLocation) / distanceBase) * (float)l))]);
    }


    //Much neater than doing the code, probably better to use this
    public static void addSpecialMapLocation(MapLocation m, float force){
        if( R.myLocation.isWithinDistance(m,maxMove)) {
            specialMapLocations[specialMapLocationsCount] = m;
            extraPoints[specialMapLocationsCount++] = force;
        }
    }
    //Preferably don't use this, instead just use the actual line
    public static void addSpecialMapLocationUnchecked(MapLocation m, float force){
        specialMapLocations[specialMapLocationsCount] = m;
        extraPoints[specialMapLocationsCount++] = force;
    }

    //Much neater than doing the code
    //Probably the cheapest way of doing this too
    public static void addSpecialMapLocationTowards(MapLocation m, float force){
        specialMapLocations[specialMapLocationsCount] = myLocation.add(myLocation.directionTo(m),maxMove);
        extraPoints[specialMapLocationsCount++] = force;
    }

    //Move towards the object, making sure not to collide
    public static void addSpecialMapLocationTowardsObject(MapLocation m, float objectRadius, float force){
        specialMapLocations[specialMapLocationsCount] = myLocation.add(myLocation.directionTo(m),Math.min(maxMove,    myLocation.distanceTo(m) - (objectRadius + radius + floatSafety)));
        extraPoints[specialMapLocationsCount++] = force;
    }
    public static void addSpecialMapLocationTowardsObject(RobotInfo robot, float force){
        specialMapLocations[specialMapLocationsCount] = myLocation.add(myLocation.directionTo(robot.location),Math.min(maxMove,    myLocation.distanceTo(robot.location) - (robot.getRadius() + radius + floatSafety)));
        extraPoints[specialMapLocationsCount++] = force;
    }
    public static void addSpecialMapLocationTowardsObject(TreeInfo tree, float force){
        specialMapLocations[specialMapLocationsCount] = myLocation.add(myLocation.directionTo(tree.location),Math.min(maxMove,    myLocation.distanceTo(tree.location) - (tree.getRadius() + radius + floatSafety)));
        extraPoints[specialMapLocationsCount++] = force;
    }
    public static void addSpecialMapLocationAway(RobotInfo robot, float force){
        specialMapLocations[specialMapLocationsCount] = myLocation.subtract(myLocation.directionTo(robot.location),maxMove);
        extraPoints[specialMapLocationsCount++] = force;
    }
    public static void addSpecialMapLocationAway(TreeInfo tree, float force){
        specialMapLocations[specialMapLocationsCount] = myLocation.subtract(myLocation.directionTo(tree.location),maxMove);
        extraPoints[specialMapLocationsCount++] = force;
    }
    public static void addSpecialMapLocationAway(MapLocation m, float force){
        specialMapLocations[specialMapLocationsCount] = myLocation.subtract(myLocation.directionTo(m),maxMove);
        extraPoints[specialMapLocationsCount++] = force;
    }


    //Same deal, but without the force
    public static void addSpecialMapLocation(MapLocation m){
        if( R.myLocation.isWithinDistance(m,maxMove)) {
            noBoostSpecialLocations[noBoostLocationsCount++] = m;
        }
    }
    public static void addSpecialMapLocationUnchecked(MapLocation m){
        noBoostSpecialLocations[noBoostLocationsCount++] = m;
    }
    public static void addSpecialMapLocationTowards(MapLocation m){
        noBoostSpecialLocations[noBoostLocationsCount++] = myLocation.add(myLocation.directionTo(m),maxMove);
    }
    public static void addSpecialMapLocationTowardsObject(MapLocation m, float objectRadius){
        noBoostSpecialLocations[noBoostLocationsCount++] = myLocation.add(myLocation.directionTo(m),Math.min(maxMove,    myLocation.distanceTo(m) - (objectRadius + radius + floatSafety)));
    }
    public static void addSpecialMapLocationTowardsObject(RobotInfo robot){
        noBoostSpecialLocations[noBoostLocationsCount++] = myLocation.add(myLocation.directionTo(robot.location),Math.min(maxMove,    myLocation.distanceTo(robot.location) - (robot.getRadius() + radius + floatSafety)));
    }
    public static void addSpecialMapLocationTowardsObject(TreeInfo tree){
        noBoostSpecialLocations[noBoostLocationsCount++] = myLocation.add(myLocation.directionTo(tree.location),Math.min(maxMove,    myLocation.distanceTo(tree.location) - (tree.getRadius() + radius + floatSafety)));
    }
    public static void addSpecialMapLocationAway(RobotInfo robot){
        noBoostSpecialLocations[noBoostLocationsCount++] = myLocation.subtract(myLocation.directionTo(robot.location),maxMove);
    }
    public static void addSpecialMapLocationAway(TreeInfo tree){
        noBoostSpecialLocations[noBoostLocationsCount++] = myLocation.subtract(myLocation.directionTo(tree.location),maxMove);
    }
    public static void addSpecialMapLocationAway(MapLocation m){
        noBoostSpecialLocations[noBoostLocationsCount++] = myLocation.subtract(myLocation.directionTo(m),maxMove);
    }


    //For 'hard' desire, use angled rectangles
    public static void addLineDesireSoft(MapLocation start, Direction dir, float width, float basedesire, float distancedesire){
        if(dir == null) return;
        float angle = dir.radiansBetween(start.directionTo(myLocation));
        if (Math.abs(angle) < sootforcer.C.DEGREES_90_IN_RADS) {
            if(((float) Math.abs(start.distanceTo(myLocation) * Math.sin(angle)) <= width)){
                MapLocation avoidSpot = start.add(dir,(start.distanceTo(myLocation) * (float)Math.cos(angle)));
                Test.lineTo(avoidSpot,255,255,255);
                M.addSpecialMapLocationAway(avoidSpot,2);
                M.addVectorCircleZone(avoidSpot, width,basedesire,distancedesire);
            }
        }
    }


    public static void addCircleZone(MapLocation m, float size, float force){
        if(MapLocation.doCirclesCollide(m,size,R.myLocation,R.maxMove)   &&    !m.isWithinDistance(R.myLocation, size - R.maxMove)) {
//            circles[circleIndex++] = new ZoneCircle(m, size, force);
            MonsterMove.addCircle(m,size,force);
        }
    }

    public static void addVectorCircleZone(MapLocation m, float size, float baseForce, float distanceForce){

        if(MapLocation.doCirclesCollide(m,size,R.myLocation,R.maxMove)) {
            MonsterMove.addVectorCircle(m,size,baseForce,distanceForce);
//            vectorCircles[vectorCircleIndex++] = new ZoneCircleVector(m, size, baseForce,distanceForce);
        }
    }
//    public static void addVectorCircleZoneUnchecked(MapLocation m, float size, float baseForce, float distanceForce){
//        vectorCircles[vectorCircleIndex++] = new ZoneCircleVector(m, size, baseForce,distanceForce);
//    }


    public static void addSquareZone(MapLocation m, float size, float force){
        squares[squareIndex++] = new ZoneSquare(m,size,force);
    }
    public static void addRectangle(float left, float right, float top, float bot, float force){
        squares[squareIndex++]  = new ZoneSquare(left,right,top,bot,force);
    }

    public static boolean amIStuckMovementCheck(){
        for(int i = totalDirections -1; i >= 0; i--){
            if(rc.canMove(R.myLocation.add(someDirections[i],maxMove))){
                return false;
            }
        }
        return true;
    }
    public static boolean amIStuckMovementCheckCheap(){
        for(int i = 8; i >= 0; i--){
            if(rc.canMove(R.myLocation.add(someDirections[i],maxMove))){
                return false;
            }
        }
        return true;
    }

    public static void initiateDirections(){
        float startAngle = 0;
        Direction north = Direction.NORTH;

        for(int i = 0; i < 8; i++){
            someDirections[i] = north.rotateRightDegrees(startAngle + i * 45);
        }
        startAngle = 22.5f;
        for(int i = 8; i < 16; i++){
            someDirections[i] = north.rotateRightDegrees(startAngle + i * 45);
        }
        startAngle = 60f;
        for(int i = 16; i < 24; i++){
            someDirections[i] = north.rotateRightDegrees(startAngle + i * 45);
        }
        startAngle = 30;
        for(int i = 24; i < 32; i++){
            someDirections[i] = north.rotateRightDegrees(startAngle + i * 45);
        }
        startAngle = 0;
        for(int i = 32; i < 40; i++){
            someDirections[i] = north.rotateRightDegrees(startAngle + i * 45);
        }
        startAngle = 0.1f;
        for(int i = 40; i < 48; i++){
            someDirections[i] = north.rotateRightDegrees(startAngle + i * 45);
        }

        MapLocation zeroPoint = new MapLocation(0,0);
        MapLocation reuse;



        int spot = 59;

        float rotateStart = 0;
        float rotate = 90;
        float distance = maxMove;


        //These are the spots we'll iterate through during our move method

        for(int i = 0 ; i < 4;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }

        rotateStart = 30;
        for(int i = 0 ; i < 4;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }
        rotateStart = 60;
        for(int i = 0 ; i < 4;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }


        rotateStart = 15;
        rotate = 60;
        distance = maxMove/2;
        for(int i = 0 ; i < 6;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }



        rotateStart = 45f;
        rotate = 90;
        distance = 3*maxMove/4;
        for(int i = 0 ; i < 4;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }

        rotateStart = 15f;
        for(int i = 0 ; i < 4;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }

        rotateStart = 75f;
        for(int i = 0 ; i < 4;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }


        rotateStart = 0f;
        rotate = 60;
        distance = maxMove/4.5f;
        for(int i = 0 ; i < 6;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }



        rotateStart = 45;
        rotate = 60;
        distance = maxMove/2;
        for(int i = 0 ; i < 6;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }


        distance = maxMove;
        rotate = 210f;
        rotateStart = 195f;
        for(int i = 0 ; i < 12;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }

        rotateStart = 0;
        rotate = 90;
        distance = 4*maxMove/5;
        for(int i = 0 ; i < 4;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }

        rotateStart = 90;
        rotate = 180;
        distance = maxMove/8;
        for(int i = 0 ; i < 2;i++){
            reuse = zeroPoint.add(north.rotateRightDegrees(rotateStart + i * rotate),distance);
            moveSpotsX[spot] = reuse.x;
            moveSpotsY[spot] = reuse.y;
            spot--;
        }


//        Test.log("spots left: " + spot);
//
//        for(int i = 0; i < 60; i++){
//
//            //rc.setIndicatorDot(R.myLocation.translate(moveSpotsX[i]*20,moveSpotsY[i]*20),i * 6, 0, 255 - (i*16));
//
//        }


    }



    //Calculate the best move given our goal and our movement desire shapes
    public static void calculateBestMove(int minRemainingBytes, boolean takeItEasy) throws Exception{

        if(Clock.getBytecodesLeft() - 1000 < minRemainingBytes){
            bestMove = R.myLocation;
            return;
        }
        int startBytecode = Clock.getBytecodesLeft();

        moveVectorStr = (zeroVector.distanceTo(moveVector));
        dirToMove = zeroVector.directionTo(moveVector);

        finalMove = myLocation.add(dirToMove,maxMove);
        Test.lineTo(finalMove, Test.Modes.MOVEMENT);
        Test.log(moveVector +  " str" + moveVectorStr, Test.Modes.MOVEMENT);
//      Test.lineTo(R.myLocation.add(dirToMove, 10),100,100,100, Test.Modes.MOVEMENT);

        if(moveVectorStr > 0.1f && Clock.getBytecodesLeft() > 6000 && doBugPathingThisTurn) {
            bugPathing();
        }
        moveVectorStr = -moveVectorStr;
        finalMove = R.myLocation.add(dirToMove, 10);//put it at a constant distance of 10 to not warp the values too much

        Test.lineTo(finalMove, Test.Modes.MOVEMENT);




        //Bytecode saving calculations
        int minRemainingPlus200 = minRemainingBytes + 200;
        angledRectangleIndexMinusOne = angledRectangleIndex - 1;
        squareIndexMinusOne = squareIndex -1;
        circleIndexMinusOne = circleIndex -1;
        vectorCircleIndexMinusOne = vectorCircleIndex - 1;


        bestMoveScore = -10000;//neccessary before first valuationslot call

        //We want to at least consider standing still, especially in situations where we may be stuck
        bestMove = R.myLocation;
        evaluateLoc = R.myLocation;
        MonsterMove.cutoff = -1000000f;
        MonsterMove.getValuationSlot();
        bestMoveScore = lastZoneValuation;
        MonsterMove.cutoff = Float.sum(bestMoveScore,-200);

        int spotsTestedCommentThisOut = 2;
        int trackFinalLastSpot = 0;


        //Go over all the sensible locations to test one by one in this block
        locationevaluation:
        {
            //The best move is commonly right along the move vector
            if(!R.myLocation.isWithinDistance(finalMove,0.01f)) {
                evaluateLoc = R.myLocation.add(R.myLocation.directionTo(finalMove), maxMove);
                if (rc.canMove(evaluateLoc)) {
                    MonsterMove.getValuationSlot();
                    if (lastZoneValuation > bestMoveScore) {
                        bestMoveScore = lastZoneValuation;
                        bestMove = evaluateLoc;
                        MonsterMove.cutoff = Float.sum(bestMoveScore,-200);
                        trackFinalLastSpot = 1;
                    }
                }
            }

            //The best move is also commonly exactly the same thing we did last turn
            if(lastBestMove != null) {
                evaluateLoc = R.myLocation.translate(lastBestMove.x, lastBestMove.y);
                if(rc.canMove(evaluateLoc)) {
                    MonsterMove.getValuationSlot();
                    if (lastZoneValuation > bestMoveScore) {
                        bestMoveScore = lastZoneValuation;
                        bestMove = evaluateLoc;
                        MonsterMove.cutoff = Float.sum(bestMoveScore,-200);
                        trackFinalLastSpot = spotsTestedCommentThisOut;
                    }
                }
                spotsTestedCommentThisOut++;
            }




            //Go through all of the 'special' slots, the points specifically added by code as move suggestions
            //These are the 'boosted' special slots which may have an additional desire to step here
            for (int i = specialMapLocationsCount - 1; i >= 0; i--) {
                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
                evaluateLoc = specialMapLocations[i];
                if(rc.canMove(evaluateLoc)) {
                    MonsterMove.getValuationSlot();
                    float eval = lastZoneValuation + extraPoints[i];
                    if (eval > bestMoveScore) {
                        bestMoveScore = eval;
                        bestMove = evaluateLoc;
                        MonsterMove.cutoff = Float.sum(bestMoveScore,-200);
                        trackFinalLastSpot = spotsTestedCommentThisOut;
                    }
                }
                spotsTestedCommentThisOut++;
            }


            //Go through all of the 'special' slots, the points specifically added by code as move suggestions
            for (int i = noBoostLocationsCount - 1; i >= 0; i--) {
                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
                evaluateLoc = noBoostSpecialLocations[i];
                if(rc.canMove(evaluateLoc)) {
                    MonsterMove.getValuationSlot();
                    if (lastZoneValuation > bestMoveScore) {
                        bestMoveScore = lastZoneValuation;
                        bestMove = evaluateLoc;
                        MonsterMove.cutoff = Float.sum(bestMoveScore,-200);
                        trackFinalLastSpot = spotsTestedCommentThisOut;
                    }
                }
                spotsTestedCommentThisOut++;
            }

            int take = takeItEasy ? 47 : 0;
            //So, we usually have bytecodes left after this. So we go through a lot of standard move directions
            //Like: a full step north, half a step east etc.
            //These steps have been precalculated and are saved in moveSpotsX,moveSpotsY
            for(int i = 59; i >=take; i--){
                if (Clock.getBytecodesLeft() < minRemainingPlus200) break locationevaluation;
                evaluateLoc = R.myLocation.translate(moveSpotsX[i], moveSpotsY[i]);
                if(rc.canMove(evaluateLoc)) {
                    MonsterMove.getValuationSlot();
                    if (lastZoneValuation > bestMoveScore) {
                        bestMoveScore = lastZoneValuation;
                        bestMove = evaluateLoc;
                        MonsterMove.cutoff = Float.sum(bestMoveScore,-200);
                        trackFinalLastSpot = spotsTestedCommentThisOut;
                    }
                }
                spotsTestedCommentThisOut++;
            }
        }



//        if(bestMoveScore < -200){
//            Test.dot(255,0,0);
//        }else{
//            Test.dot(0,255,0);
//
//        }

        Test.log("Movement,  Spots:  "   +  spotsTestedCommentThisOut +   "  Zones: " + (MonsterMove.circleCount+MonsterMove.angledRectangleCount +squareIndex+MonsterMove.vectorCircleCount)  +   "bc: " + startBytecode + "->" + Clock.getBytecodesLeft(), Test.Modes.MOVEMENT);
        Test.log("best spot: " + trackFinalLastSpot + " pos: " + bestMove + " best score: " + bestMoveScore, Test.Modes.MOVEMENT);



    }

    //A custom algorithm for 'bug pathing', a technique for units to move along a wall if they can't get directly get
    //where they want to go. Unfortunately, the limited bytecode budget as well as the limited map knowledge, require
    //require these kinds of tricks instead of proper algorithms
    public static void bugPathing() throws Exception {

        //Determine whether we should even do bugpathing
        if (R.mainTarget == null || !R.mainTarget.isWithinDistance(R.myLocation, 5)) { //stop pathing away when weve reached the end
            if (rc.canMove(dirToMove)) {
                if (wasBugPathingLastTurn) {
                    MapLocation anticipatedSpot = R.myLocation.add(dirToMove, maxMove);
                    if (!(amIArchon || amIGardener)&& (R.myLocation.distanceTo(finalMove) > R.prevLocation.distanceTo(finalMove) || (anticipatedSpot.isWithinDistance(R.prevLocation, maxMove))  || longtermMoveHistory.contains(anticipatedSpot.toString()))  ) {
                        Test.log("bugpathing despite ability to move", Test.Modes.BUGPATHING);
                    } else {
                        wasBugPathingLastTurn = false;
                        return;
                    }
                } else {
                    return;
                }
            }
        } else {
            wasBugPathingLastTurn = false;
            return;
        }

        RobotInfo[] plausibleBlockingRobots = rc.senseNearbyRobots(R.radius + maxMove - sootforcer.C.SAFETY);


        TreeInfo[] plausibleBlockingTrees;

        if(R.amIScout) {
            plausibleBlockingTrees = new TreeInfo[0];
        }
        else{
            plausibleBlockingTrees =rc.senseNearbyTrees(R.radius + maxMove - sootforcer.C.SAFETY);
        }

        if (plausibleBlockingRobots.length == 0 && plausibleBlockingTrees.length == 0) {
            wasBugPathingLastTurn = false;
            return;
        }

        wasBugPathingLastTurn = true;

        ////rc.setIndicatorLine(R.myLocation,R.myLocation.add(dirToMove,10), 200, 200, 200);



        //Determine what direction we should be walking in / when we should switch directions
        if (R.turn - lastNeededBug > 2) {
            bugPathingDirection = 0;
            Test.log("Swapping direction because we stopped pathing", Test.Modes.BUGPATHING);
        }
        if (R.turn - lastSwappedBug > 150) {
            bugPathingDirection *= -1;
            lastSwappedBug = R.turn;
            Test.log("Swapping direction because we've walked in one direction for too long", Test.Modes.BUGPATHING);
        }
        if (R.turn - lastSwappedBug > 15) {
            if (R.beforePrevLocation.isWithinDistance(R.myLocation, 0.1f)) {
                //Probably stuck
                bugPathingDirection *= -1;
                lastSwappedBug = R.turn;
                Test.log("Swapping direction because were stuck", Test.Modes.BUGPATHING);
            }
        }
        if (bugPathingDirection == -1) {
            Test.log("Bugging left", Test.Modes.BUGPATHING);
        } else if (bugPathingDirection == 1) {
            Test.log("Bugging right", Test.Modes.BUGPATHING);

        }

        lastNeededBug = R.turn;


        MapLocation bestBugSpot = null;
        float bestScore = -1000000;
        boolean bestLeft = false;

        boolean oneSpotInCorrectDirection = false;


        for (int i = plausibleBlockingRobots.length - 1; i >= 0; i--) {
            MapLocation[] spots = Helper.FindCircleIntersectionPoints(R.myLocation, maxMove, plausibleBlockingRobots[i].location, plausibleBlockingRobots[i].getRadius() + R.radius + sootforcer.C.SAFETY);
            if (spots != null) {
                MapLocation m = spots[0];
                if (rc.onTheMap(m)) {
                    if (rc.canMove(m) && ((R.turn - lastSwappedBug == 0) || !m.isWithinDistance(R.prevLocation, maxMove * 1.1f))) {
                        float score = -m.distanceTo(finalMove);

                        boolean spot1Left = dirToMove.degreesBetween(R.myLocation.directionTo(spots[0])) < 0;
                        if ((spot1Left && bugPathingDirection <= 0) || (!spot1Left && bugPathingDirection >= 0)) {
                            score -= 10;
                        } else {
                            oneSpotInCorrectDirection = true;
                        }

                        if (score > bestScore) {
                            bestScore = score;
                            bestBugSpot = m;
                            bestLeft = true;
                        }

                        extraPoints[specialMapLocationsCount] = 3;
                        specialMapLocations[specialMapLocationsCount++] = m;

                        Test.lineTo(m, 0, 0, 100, Test.Modes.BUGPATHING);
                    }
                }

                MapLocation m1 = spots[1];
                if (rc.onTheMap(m1)) {
                    if (rc.canMove(m1) && ((R.turn - lastSwappedBug == 0) || !m1.isWithinDistance(R.prevLocation, maxMove * 1.1f))) {
                        float score = -m1.distanceTo(finalMove);

                        boolean spot2Left = dirToMove.degreesBetween(R.myLocation.directionTo(spots[1])) < 0;
                        if ((spot2Left && bugPathingDirection <= 0) || (!spot2Left && bugPathingDirection >= 0)) {
                            score -= 10;
                        } else {
                            oneSpotInCorrectDirection = true;
                        }

                        if (score > bestScore) {
                            bestScore = score;
                            bestBugSpot = m1;
                            bestLeft = false;
                        }

                        extraPoints[specialMapLocationsCount] = 3;
                        specialMapLocations[specialMapLocationsCount++] = m1;

                        Test.lineTo(m1, 0, 0, 100, Test.Modes.BUGPATHING);
                    }
                }
            }
        }

        for (int i = plausibleBlockingTrees.length - 1; i >= 0; i--) {
            MapLocation[] spots = Helper.FindCircleIntersectionPoints(R.myLocation, maxMove, plausibleBlockingTrees[i].location, plausibleBlockingTrees[i].getRadius() + R.radius + (2f * C.SAFETY));
            if (spots != null) {

                MapLocation m = spots[0];
                if (rc.onTheMap(m)) {
                    if (rc.canMove(m) && ((R.turn - lastSwappedBug == 0) || !m.isWithinDistance(R.prevLocation, maxMove * 1.1f))) {
                        float score = -m.distanceTo(finalMove);

                        boolean spot1Left = dirToMove.degreesBetween(R.myLocation.directionTo(spots[0])) < 0;
                        if ((spot1Left && bugPathingDirection <= 0) || (!spot1Left && bugPathingDirection >= 0)) {
                            score -= 10;
                        } else {
                            oneSpotInCorrectDirection = true;
                        }

                        if (score > bestScore) {
                            bestScore = score;
                            bestBugSpot = m;
                            bestLeft = true;
                        }

                        extraPoints[specialMapLocationsCount] = 3;
                        specialMapLocations[specialMapLocationsCount++] = m;

                        Test.lineTo(m, 0, 100, 0, Test.Modes.BUGPATHING);
                    }
                }

                MapLocation m1 = spots[1];
                if (rc.onTheMap(m1)) {
                    if (rc.canMove(m1) && ((R.turn - lastSwappedBug == 0) || !m1.isWithinDistance(R.prevLocation, maxMove * 1.1f))) {
                        float score = -m1.distanceTo(finalMove);

                        boolean spot2Left = dirToMove.degreesBetween(R.myLocation.directionTo(spots[1])) < 0;
                        if ((spot2Left && bugPathingDirection <= 0) || (!spot2Left && bugPathingDirection >= 0)) {
                            score -= 10;
                        } else {
                            oneSpotInCorrectDirection = true;
                        }

                        if (score > bestScore) {
                            bestScore = score;
                            bestBugSpot = m1;
                            bestLeft = false;
                        }

                        extraPoints[specialMapLocationsCount] = 3;
                        specialMapLocations[specialMapLocationsCount++] = m1;
                        Test.lineTo(m1, 0, 0, 100, Test.Modes.BUGPATHING);
                    }
                }
            }
        }


        if (bestBugSpot != null) {

            if (!oneSpotInCorrectDirection) {
                walkingWrongWayCounter++;
                if (walkingWrongWayCounter > 4) {
                    Test.log("Walking wrong way, flipping direction", Test.Modes.BUGPATHING);
                    bugPathingDirection *= -1;
                    lastSwappedBug = R.turn;
                    walkingWrongWayCounter = 0;
                }
            } else {
                walkingWrongWayCounter = 0;
            }

            dirToMove = R.myLocation.directionTo(bestBugSpot);
            if (bugPathingDirection == 0) {
                if (bestLeft) bugPathingDirection = -1;
                else bugPathingDirection = 1;

                lastSwappedBug = R.turn;
            }
        }
    }




    public static void doBestMove() throws GameActionException{
        R.beforeBeforePrevLocation = R.beforePrevLocation;
        R.beforePrevLocation = R.prevLocation;
        R.prevLocation = R.myLocation;

        longtermMoveHistory = Intrinsics.stringPlus(longtermMoveHistory,R.myLocation.toString());

        rc.move(bestMove);
        lastBestMove = bestMove.translate(-R.myX,-R.myY);
        R.myLocation = rc.getLocation();
        R.myX = R.myLocation.x;
        R.myY = R.myLocation.y;
    }

    public static void getValuationSlot(){
        //There's a bytecode overhead for accessing class variables
        float locX = evaluateLoc.x;
        float locY = evaluateLoc.y;

        lastZoneValuation = evaluateLoc.distanceTo(finalMove) * moveVectorStr;

        //Change the valuation if this spot is inside of any of the desirezones
        //Heaviest part of program due to double loop. Heavily optimized, but still takes a lot
        //Theoretically, everything could be unpacked from the loops, with reused variables for some bytecode gains
        //But that's a little too insane. Like through having 50 zone.x variables
        for (int i = angledRectangleIndexMinusOne; i >= 0; i--) {
            ZoneAngledRectangle zone = angledRectangles[i];
            final float test1 = (locX - zone.x1) * zone.p21x + (locY - zone.y1) * zone.p21y;
            if ( test1 >= 0f && test1 <= zone.p21MagSquared) {
                final float test2 = (locX - zone.x1) * zone.p41x + (locY - zone.y1) * zone.p41y;
                if (test2 >= 0f && test2 <= zone.p41MagSquared) {
                    lastZoneValuation += zone.desire;
                }
            }
        }

        for (int i = vectorCircleIndexMinusOne; i >= 0; i--) {
            ZoneCircleVector zone = vectorCircles[i];
            float distance = evaluateLoc.distanceTo(zone.center);
            if(distance < zone.radius){
                lastZoneValuation += zone.edgeDesire - distance * zone.distanceDesire;
//                //rc.setIndicatorLine(R.myLocation,zone.center,Clock.getBytecodeNum() % 255,50,50);
            }
        }

        for (int i = circleIndexMinusOne; i >= 0; i--) {
            ZoneCircle zone = circles[i];
            if(evaluateLoc.isWithinDistance(zone.center,zone.radius)){
                lastZoneValuation += zone.desire;
            }
        }

        for (int i = squareIndexMinusOne; i >= 0; i--) {
            ZoneSquare zone = squares[i];
            if (zone.left < locX && locX < zone.right && locY < zone.top && locY > zone.bot) {
                lastZoneValuation += zone.desire;
            }
        }
    }

}
