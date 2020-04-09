package sootforcer;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import sootforcer.C;
import sootforcer.R;
import sootforcer.Test;

/**
 * Created by Hermen on 6/5/2017.
 */
public class Builder  extends sootforcer.R {

    public static MapLocation bestGardenerHexSpot;
    public static int lastAnalyzedHexes = -1;
    public static float bestGardenerHexSpotScore = 0;

    public static MapLocation bestGardenerSquareSpot;
    public static int lastAnalyzedSquares = -1;
    public static float bestGardenerSquareSpotScore = 0;
    public static StringBuilder spotsClaimedBuilder = new StringBuilder();


    public static void analyzeNearbyHexSpots(int minAcceptable) throws Exception{
        if (turn != lastAnalyzedHexes) {

            sootforcer.Test.beginClockTest(1);
            int[] near = findCloseHex(sootforcer.R.myLocation);

            int limit = (((int) (sootforcer.R.sightradius / sootforcer.C.HEXSPACING)));

            if(sootforcer.R.turn < 3) limit--;


            int nearRow = near[0];
            int nearColumn = near[1];
            float sight = sootforcer.R.sightradius - sootforcer.C.HEX_SPACING_PLUS_TREE_SIZE;


            bestGardenerHexSpotScore = minAcceptable;
            bestGardenerHexSpot = null;

            String spotsClaimed = spotsClaimedBuilder.toString();
            lastAnalyzedHexes = turn;

            for (int x = 1 - limit; x <= limit - 1; x++) {
                for (int y = -limit; y <= limit; y++) {
                    MapLocation spot = getHexSpot(nearRow + x, nearColumn + y);

                    if (spot.isWithinDistance(sootforcer.R.myLocation, sight)) {
                        if (rc.onTheMap(spot, sootforcer.C.HEX_SPACING_PLUS_TREE_SIZE)) {
                            if (rc.isCircleOccupied(spot, GameConstants.BULLET_TREE_RADIUS)) {
                                //rc.setIndicatorDot(spot, 255, 0, 0);
                            } else {

                                if (spotsClaimed.contains(String.format(";%8d%8d", (int) spot.x, (int) spot.y))) {
                                    //rc.setIndicatorDot(spot, 0, 0, 255);
                                } else {

                                    float score = 6f;

                                    MapLocation s1 = spot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                                    MapLocation s2 = spot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_TOP_Y_DIF);
                                    MapLocation s3 = spot.translate(sootforcer.C.HEX_RIGHT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                                    MapLocation s4 = spot.translate(sootforcer.C.HEX_LEFT_OTHER_ROW, sootforcer.C.HEX_BOT_Y_DIF);
                                    MapLocation s5 = spot.translate(sootforcer.C.HEX_LEFT_X_DIF, 0);
                                    MapLocation s6 = spot.translate(sootforcer.C.HEX_RIGHT_X_DIF, 0);


//                                MapLocation s1 = spot.translate(C.HEX_LEFT_X_DIF, C.HEX_RIGHT_OTHER_ROW);
//                                MapLocation s2 = spot.translate(C.HEX_LEFT_X_DIF, C.HEX_LEFT_OTHER_ROW);
//                                MapLocation s3 = spot.translate(C.HEX_RIGHT_X_DIF, C.HEX_RIGHT_OTHER_ROW);
//                                MapLocation s4 = spot.translate(C.HEX_RIGHT_X_DIF, C.HEX_LEFT_OTHER_ROW);
//                                MapLocation s5 = spot.translate(0, C.HEX_TOP_Y_DIF);
//                                MapLocation s6 = spot.translate(0, C.HEX_BOT_Y_DIF);


                                    if (rc.isCircleOccupiedExceptByThisRobot(s1, GameConstants.BULLET_TREE_RADIUS) || spotsClaimed.contains(String.format(";%8d%8d", (int) s1.x, (int) s1.y))) {
                                        score--;
//                                    //rc.setIndicatorLine(spot, s1, 0, 0, 0);
                                    }

                                    if (rc.isCircleOccupiedExceptByThisRobot(s2, GameConstants.BULLET_TREE_RADIUS) || spotsClaimed.contains(String.format(";%8d%8d", (int) s2.x, (int) s2.y))) {
                                        score--;
//                                    //rc.setIndicatorLine(spot, s2, 0, 0, 0);
                                    }

                                    if (rc.isCircleOccupiedExceptByThisRobot(s3, GameConstants.BULLET_TREE_RADIUS) || spotsClaimed.contains(String.format(";%8d%8d", (int) s3.x, (int) s3.y))) {
                                        score--;
//                                    //rc.setIndicatorLine(spot, s3, 0, 0, 0);
                                    }


                                    if (rc.isCircleOccupiedExceptByThisRobot(s4, GameConstants.BULLET_TREE_RADIUS) || spotsClaimed.contains(String.format(";%8d%8d", (int) s4.x, (int) s4.y))) {
                                        score--;
//                                    //rc.setIndicatorLine(spot, s4, 0, 0, 0);

                                    }


                                    if (rc.isCircleOccupiedExceptByThisRobot(s5, GameConstants.BULLET_TREE_RADIUS) || spotsClaimed.contains(String.format(";%8d%8d", (int) s5.x, (int) s5.y))) {
                                        score--;
//                                    //rc.setIndicatorLine(spot, s5, 0, 0, 0);

                                    }

                                    if (rc.isCircleOccupiedExceptByThisRobot(s6, GameConstants.BULLET_TREE_RADIUS) || spotsClaimed.contains(String.format(";%8d%8d", (int) s6.x, (int) s6.y))) {
                                        score--;
//                                    //rc.setIndicatorLine(spot, s6, 0, 0, 0);
                                    }


                                    if (score > minAcceptable) {
                                        if (sootforcer.R.mainTarget != null) {
                                            score += spot.distanceTo(sootforcer.R.mainTarget) / 10f;
                                        }
                                        score += spot.distanceTo(sootforcer.R.mapCenter) / 15f;

                                        if (score > bestGardenerHexSpotScore) {
                                            bestGardenerHexSpotScore = score;
                                            bestGardenerHexSpot = spot;
                                        }
                                    }


                                    //rc.setIndicatorDot(spot, 0, (int) score * 35, 0);
                                }
                            }
                        }

//                        //rc.setIndicatorDot(spot, (x + 5) * 20, (y + 5) * 20, 0);
                    }
                }

            }



//        //rc.setIndicatorDot(R.getHexSpot(near[0],near[1]),0,0,255);
            sootforcer.Test.endClockTest(1, "hex spots checks");
        }
    }


    public static void analyzeSquareSpots(int minAcceptable) throws GameActionException {
        if(turn != lastAnalyzedSquares) {
//            Test.beginClockTest(1);

            int[] near = findCloseSquare(sootforcer.R.myLocation);
            int limit = (((int) (sootforcer.R.sightradius / sootforcer.C.SQUARE_SPACING))) + 1;

            if(sootforcer.R.turn < 3 || Clock.getBytecodesLeft() < 20000) limit--;
            if(Clock.getBytecodesLeft() < 20000) limit--;

            int arSize = limit * 2 + 1;
            int startX = near[0] - limit;
            int startY = near[1] - limit;

            float sight = sootforcer.R.sightradius - GameConstants.BULLET_TREE_RADIUS;
            String spotsClaimed = spotsClaimedBuilder.toString();


            boolean[] spotsFree = new boolean[arSize * arSize];

            int arSizeMinus1 = arSize - 1;
            int arSizeMinus2 = arSize - 2;

            Test.beginClockTest(4);

            for (int x = arSizeMinus1; x >= 0; x--) {
                for (int y = arSizeMinus1; y >= 0; y--) {
                    MapLocation spot = new MapLocation( Float.sum(squareOriginX , ((float)Math.addExact(x , startX) * sootforcer.C.SQUARE_SPACING)), Float.sum( squareOriginY , ((float)Math.addExact(y , startY) * sootforcer.C.SQUARE_SPACING)));
                    if (spot.isWithinDistance(sootforcer.R.myLocation, sight)) {
                        if (rc.onTheMap(spot, sootforcer.C.GARDENER_RADIUS_PLUS_SAFETY)) {
                            if (!rc.isCircleOccupiedExceptByThisRobot(spot, sootforcer.C.GARDENER_RADIUS_PLUS_SAFETY) && !spotsClaimed.contains(String.format(";%8d%8d", (int) spot.x, (int) spot.y))) {
                                spotsFree[  Math.addExact(Math.multiplyExact(x , arSize) , y)] = true;
                            }
                        }
                    }
                }
            }

//            Test.endClockTest(4, "square step 1");


            bestGardenerSquareSpotScore = minAcceptable;
            bestGardenerSquareSpot = null;
            lastAnalyzedSquares = turn;

//            Test.beginClockTest(4);

            for (int x = arSizeMinus2; x >= 1; x--) {
                for (int y = arSizeMinus2; y >= 1; y--) {

                    int index= Math.addExact(Math.multiplyExact(x , arSize) , y);
                    if(spotsFree[index]){

                        float score = 0;
                        if(spotsFree[index + arSize]){
//                            if(spotsFree[(x+1) * arSize + y]){
                            score++;
                        }

                        if(spotsFree[index - arSize]){

//                        if(spotsFree[(x-1) * arSize + y]){
                            score++;
                        }

                        if(spotsFree[index - 1]){

//                        if(spotsFree[(x) * arSize + y - 1]){
                            score++;
                        }
                        if(spotsFree[index + 1]){

//                        if(spotsFree[(x) * arSize + y + 1]){
                            score++;
                        }

                        if (score > 1) {
                            if(spotsFree[index + arSize + 1]){

//                            if(spotsFree[(x+1) * arSize + y + 1]){
//                                score+=0.5f;
                                score = Float.sum(score,0.5f);

                            }

                            if(spotsFree[(index + 1) - arSize]){
//                            if(spotsFree[(x-1) * arSize + y + 1]){
//                                score+=0.5f;
                                score = Float.sum(score,0.5f);
                            }

                            if(spotsFree[(index + arSize) -1]){

//                            if(spotsFree[(x+1) * arSize + y - 1]){
//                                score+=0.5f;
                                score = Float.sum(score,0.5f);

                            }
                            if(spotsFree[index - (arSize + 1)]){
//                            if(spotsFree[(x-1) * arSize + y - 1]){
//                                score+=0.5f;
                                score = Float.sum(score,0.5f);
                            }

                            MapLocation spot = new MapLocation(squareOriginX + ((x + startX) * sootforcer.C.SQUARE_SPACING), squareOriginY + ((y + startY) * sootforcer.C.SQUARE_SPACING));

                            if (sootforcer.R.mainTarget != null) {
                                score += spot.distanceTo(sootforcer.R.mainTarget) / 10f;
                            }
                            score += spot.distanceTo(R.mapCenter) / 15f;


                            if (score > bestGardenerSquareSpotScore) {
                                bestGardenerSquareSpotScore = score;
                                bestGardenerSquareSpot = spot;
                            }
                            //rc.setIndicatorDot(spot, 0, (int) (score * 25), 0);
                        }

                    }
                    else {
                        //rc.setIndicatorDot(getSquareSpot(x + startX, y + startY), 255, 0, 0);

                    }
                }
            }
//            Test.endClockTest(4, "square step 2");


            //rc.setIndicatorDot(getSquareSpot(near[0], near[1]), 0, 0, 255);

//            Test.endClockTest(1, "square spots checks");

        }

    }



    public static MapLocation getHexSpot(int colsFromOrigin, int rowsFromOrigin){
        if(rowsFromOrigin % 2 != 0){
            return new MapLocation(hexOriginX + sootforcer.C.HALF_HEXSPACING + colsFromOrigin * sootforcer.C.HEXSPACING ,hexOriginY + rowsFromOrigin * sootforcer.C.HEX_ROW_DIST);

        }
        else{
            return new MapLocation(hexOriginX + colsFromOrigin * sootforcer.C.HEXSPACING,hexOriginY + rowsFromOrigin * sootforcer.C.HEX_ROW_DIST);
        }
    }
    public static int[] findCloseHex(MapLocation m){

        int row = Math.round((m.y - hexOriginY) / (sootforcer.C.HEX_ROW_DIST));

        int col;
        if(Math.abs(row %2) == 1){
            col = Math.round(((m.x - hexOriginX) / (sootforcer.C.HEXSPACING))-(sootforcer.C.HALF_HEXSPACING/2f));
        }else{
            col = Math.round(((m.x - hexOriginX) / (sootforcer.C.HEXSPACING)));
        }
        return new int[]{col,row};
    }

    public static int[] findCloseSquare(MapLocation m){
        int col = Math.round((m.x - squareOriginX) / (sootforcer.C.SQUARE_SPACING));
        int row = Math.round((m.y - squareOriginY) / (sootforcer.C.SQUARE_SPACING));
        return new int[]{col,row};
    }

    public static MapLocation getSquareSpot(int colsFromOrigin, int rowsFromOrigin){
        return new MapLocation(squareOriginX + (colsFromOrigin * sootforcer.C.SQUARE_SPACING),squareOriginY + (rowsFromOrigin * C.SQUARE_SPACING));
    }

}
