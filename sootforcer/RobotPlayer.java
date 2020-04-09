package sootforcer;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public strictfp class RobotPlayer {

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        try {
            sootforcer.R.rc = rc;
            Adjustables.init();
            while(rc.getRoundNum() < Adjustables.HANDICAP){
                System.out.println("HANDiCAP");
                Clock.yield();
            }


            R robot = null;
            switch (rc.getType()) {
                case ARCHON:
                    robot = new Archon();
                    break;
                case GARDENER:
                    robot = new Gardener();
                    break;
                case SOLDIER:
                    robot = new Soldier();
                    break;
                case LUMBERJACK:
                    robot = new Lumberjack();
                    break;
                case TANK:
                    robot = new Tank();
                    break;
                case SCOUT:
                    robot = new Scout();
                    break;
            }
            robot.run();
        }catch (Exception e){
            Test.log(e);
            Clock.yield();

        }
	}

}
