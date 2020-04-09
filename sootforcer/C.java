package sootforcer;

import battlecode.common.GameConstants;
import battlecode.common.RobotType;
import sootforcer.R;

/**
 * Created by Hermen on 23/4/2017.
 */
public class C {

    public static float ARCHON_BUILD_DISTANCE;
    public static float GARDENER_RADIUS;
    public static float ARCHON_RADIUS;

    public static float LUMBERJACK_CUT_PLUS_SAFETY;
    public static float LUMBERJACK_CUT_PLUS_MOVE;
    public static float LUMBERJACK_CUT_PLUS_RADIUS_SOLDIER;
    public static float LUMBERJACK_DANGER;
    public static float LUMBERJACK_DANGER_PLUS_SOLDIER_MOVE;

    public static float SOLDIER_DODGE_FRIENDLY_SOLDIERS;
    public static float SOLDIER_DODGE_FRIENDLY_SOLDIERS_PLUS_MOVE;
    public static float SOLDIER_CAN_SEE_SOLDIER;

    public static float MAX_BULLET_VISION;

    public static float SAFETY;

    public static float GARDENERMOVE;
    public static float DOUBLE_GARDENERMOVE;
    public static float TRIPLE_ARCHONMOVE;

    public static float TANKMOVE;
    public static float DOUBLE_TANKMOVE;

    public static float DEGREES_45_IN_RADS;

    public static float DEGREES_90_IN_RADS;
    public static float PI;
    public static float HALFPI;
    public static float SQRT2;
    public static float HALFSQRT2;

    public static float ANGLE_FRIENDLYFIRE_TRIPLE_RAD;
    public static float ANGLE_FRIENDLYFIRE_PENTA_RAD;
    public static float ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER;

    public static float CLUSTER_IDENTIFIER_RADIUS;
    public static float CLUSTER_DODGE_RADIUS;
    public static float CLUSTER_SNIPE_RADIUS;


    public static float HEXSPACING; //The diameter
    public static float HALF_HEXSPACING; //The radius

    public static float HEX_ROW_DIST;


    public static float HEX_LEFT_X_DIF;
    public static float HEX_RIGHT_X_DIF;
    public static float HEX_LEFT_OTHER_ROW;
    public static float HEX_RIGHT_OTHER_ROW;
    public static float HEX_TOP_Y_DIF;
    public static float HEX_BOT_Y_DIF;
    public static float HEX_SPACING_PLUS_TREE_SIZE;

    public static float SQUARE_SPACING;
    public static float SQUARE_DIST;
//    public static float SQUARE_SPACING_PLUS_TREE_SIZE;

    public static float SQUARE_MOVE_FOR_PERFECT_DIAGONALS;

    public static float POINT_BLANK_SOLDIER_ON_GARDENER;


    public static int HASH_SET_SIZE_OWN_REPORTS;
    public static int HASH_SET_SIZE_ENEMIES;

    public static int CUTOFFZONEEVALUTION;
    public static float SCOUT_POINT_BLANK;

    public static float MAP_ANALYSIS_DISTANCE;
    public static float MAP_ANALYSIS_DISTANCE_2x;

    public static float LARGEST_SQUARE_IN_SIGHT_SIDES;
    public static float HALF_LARGEST_SQUARE_IN_SIGHT_SIDES;

    public static float GARDENER_RADIUS_PLUS_SAFETY;
    public static float TREE_SPAWN;


    public static void init(){
        SAFETY = 0.0001f;


        GARDENER_RADIUS = RobotType.GARDENER.bodyRadius;
        ARCHON_RADIUS = RobotType.ARCHON.bodyRadius;
        ARCHON_BUILD_DISTANCE = ARCHON_RADIUS + GameConstants.GENERAL_SPAWN_OFFSET + GARDENER_RADIUS + SAFETY;
        LUMBERJACK_CUT_PLUS_SAFETY = GameConstants.LUMBERJACK_STRIKE_RADIUS + SAFETY;
        LUMBERJACK_CUT_PLUS_RADIUS_SOLDIER = LUMBERJACK_CUT_PLUS_SAFETY + RobotType.SOLDIER.bodyRadius;
        LUMBERJACK_CUT_PLUS_MOVE = LUMBERJACK_CUT_PLUS_SAFETY + RobotType.LUMBERJACK.strideRadius;
        LUMBERJACK_DANGER = LUMBERJACK_CUT_PLUS_RADIUS_SOLDIER + RobotType.LUMBERJACK.strideRadius;
        LUMBERJACK_DANGER_PLUS_SOLDIER_MOVE = LUMBERJACK_DANGER + RobotType.SOLDIER.strideRadius;

        MAX_BULLET_VISION = 3 * RobotType.SOLDIER.bulletSpeed;

        SOLDIER_DODGE_FRIENDLY_SOLDIERS = RobotType.SOLDIER.bodyRadius + RobotType.SOLDIER.bodyRadius + 3f;
        SOLDIER_DODGE_FRIENDLY_SOLDIERS_PLUS_MOVE = SOLDIER_DODGE_FRIENDLY_SOLDIERS + RobotType.SOLDIER.strideRadius;

        SOLDIER_CAN_SEE_SOLDIER = RobotType.SOLDIER.bodyRadius + RobotType.SOLDIER.sensorRadius;
        GARDENERMOVE = RobotType.GARDENER.bodyRadius;
        DOUBLE_GARDENERMOVE = GARDENERMOVE * 2;
        TRIPLE_ARCHONMOVE = RobotType.ARCHON.strideRadius * 3;

        PI = (float)Math.PI;
        HALFPI = PI/2f;
        DEGREES_90_IN_RADS = PI / 2f;
        DEGREES_45_IN_RADS = PI / 4f;

        ANGLE_FRIENDLYFIRE_TRIPLE_RAD = GameConstants.TRIAD_SPREAD_DEGREES * PI / 180f;
        ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER = GameConstants.PENTAD_SPREAD_DEGREES * PI / 180f;
        ANGLE_FRIENDLYFIRE_PENTA_RAD = ANGLE_FRIENDLYFIRE_PENTA_RAD_INNER * 2;

        CLUSTER_IDENTIFIER_RADIUS = 9f;
        CLUSTER_DODGE_RADIUS = 13f;
        CLUSTER_SNIPE_RADIUS = 12f;

        HASH_SET_SIZE_OWN_REPORTS = 2000;
        HASH_SET_SIZE_ENEMIES = 150;
        CUTOFFZONEEVALUTION = -800;

        TREE_SPAWN = GameConstants.BULLET_TREE_RADIUS + GameConstants.GENERAL_SPAWN_OFFSET + RobotType.GARDENER.bodyRadius;
        HEXSPACING = GameConstants.BULLET_TREE_RADIUS * 2f + GameConstants.GENERAL_SPAWN_OFFSET;

        HALF_HEXSPACING = HEXSPACING / 2f;

        HEX_ROW_DIST = ((float)Math.sqrt(3) / 2f) * HEXSPACING;

        HEX_TOP_Y_DIF = HEX_ROW_DIST;
        HEX_BOT_Y_DIF = -HEX_TOP_Y_DIF;

        HEX_RIGHT_X_DIF = HEXSPACING;
        HEX_LEFT_X_DIF = -HEX_RIGHT_X_DIF;

        HEX_RIGHT_OTHER_ROW = HALF_HEXSPACING;
        HEX_LEFT_OTHER_ROW = -HEX_RIGHT_OTHER_ROW;
        HEX_SPACING_PLUS_TREE_SIZE = HEXSPACING + GameConstants.BULLET_TREE_RADIUS;

        SQUARE_DIST = GameConstants.BULLET_TREE_RADIUS + GameConstants.GENERAL_SPAWN_OFFSET;
        SQUARE_SPACING = SQUARE_DIST * 2 + SAFETY;
//        SQUARE_SPACING_PLUS_TREE_SIZE = SQUARE_SPACING + GameConstants.BULLET_TREE_RADIUS;

        POINT_BLANK_SOLDIER_ON_GARDENER = RobotType.GARDENER.bodyRadius + RobotType.SOLDIER.bodyRadius + GameConstants.BULLET_SPAWN_OFFSET - SAFETY;
        SCOUT_POINT_BLANK = RobotType.SCOUT.bodyRadius + RobotType.SCOUT.bodyRadius + GameConstants.BULLET_SPAWN_OFFSET - SAFETY;
        MAP_ANALYSIS_DISTANCE = R.sightradius - 1 - SAFETY;
        MAP_ANALYSIS_DISTANCE_2x = MAP_ANALYSIS_DISTANCE * 2;
//        C.SQUARE_MOVE_FOR_PERFECT_DIAGONALS = (float)( SQUARE_SPACING - ( Math.sqrt(  ((GameConstants.GENERAL_SPAWN_OFFSET+ GameConstants.BULLET_TREE_RADIUS + GARDENER_RADIUS) * (GameConstants.GENERAL_SPAWN_OFFSET+ GameConstants.BULLET_TREE_RADIUS + GARDENER_RADIUS)) - ((GameConstants.BULLET_TREE_RADIUS + GARDENER_RADIUS) * (GameConstants.BULLET_TREE_RADIUS + GARDENER_RADIUS))   )));

        LARGEST_SQUARE_IN_SIGHT_SIDES = (float)Math.sqrt((double)(( (2 * R.sightradius) * (2 * R.sightradius)) / 2));
        HALF_LARGEST_SQUARE_IN_SIGHT_SIDES = LARGEST_SQUARE_IN_SIGHT_SIDES / 2;
        GARDENER_RADIUS_PLUS_SAFETY =  GameConstants.BULLET_TREE_RADIUS + SAFETY;

        TANKMOVE = RobotType.TANK.strideRadius;
        DOUBLE_TANKMOVE = TANKMOVE + TANKMOVE;

        SQRT2 = (float)Math.sqrt(2);
        HALFSQRT2 = SQRT2 / 2f;
    }
}
