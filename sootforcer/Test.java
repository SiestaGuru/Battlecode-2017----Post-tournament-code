package sootforcer;

import battlecode.common.*;

/**
 * Created by Hermen on 22/4/2017.
 */
public class Test extends R {

    public enum Modes{NONE,ALL,BUGPATHING,BYTECODEEXPERIMENTS,MOVEMENT,GARDENER_VARIED, ECON, ENEMY_TRACKING, SHOOTING, MAPANALYSIS,CIRCLES, TEMP,LEARNING, LUMBERJACK}

    public static Modes LOG_MODE = Modes.BYTECODEEXPERIMENTS;


    public static R r;
    public static RobotController rc;

    public static int[] timeChannel;
    public static int[] timeChannelAvgs;

    public static int[] countChannel;

    //public static String identifier;

    public static void init(){
        Test.r = R.robot;
        rc = R.rc;
        timeChannel = new int[20];
        countChannel = new int[20];
        timeChannelAvgs = new int[20];

        //identifier = R.myType.name() + "-" + rc.getID() + ": ";


    }





    public static void beginClockTest(int channel){
        timeChannel[channel] = Clock.getBytecodesLeft();
    }
    public static void endClockTest(int channel){
        int time = timeChannel[channel] - 16; //16 bytecodes are wasted between the two bytecodeleft commands
        System.out.println("(" + channel + ") " +(time - Clock.getBytecodesLeft()));
    }
    public static void endClockTest(int channel, String indicator){
        int time = timeChannel[channel] - 18; //18 bytecodes are wasted between the two bytecodeleft commands
        System.out.println(indicator + " : " +(time - Clock.getBytecodesLeft()));
    }
    public static void endClockTestCheap(int channel){
        System.out.println(timeChannel[channel] - Clock.getBytecodesLeft());
    }


    public static void beginClockTestAvg(int channel){
        countChannel[channel]++;
        int clock = Clock.getBytecodesLeft();
        timeChannelAvgs[channel] += clock;
        timeChannel[channel] = clock;
    }
    public static void endClockTestAvg(int channel, String indicator){
        int clock = Clock.getBytecodesLeft() + 15;
        timeChannelAvgs[channel] -= clock;
        int dif =  timeChannel[channel] - clock;

        System.out.println(indicator + " - NOW:  " + dif + "    AVG: " + (  ((double)timeChannelAvgs[channel])/((double)countChannel[channel])));
    }



    public static void line(MapLocation m1, MapLocation m2, int red, int green, int blue, Modes mode){
        debug_drawLine(m1,m2,red,green,blue,mode);

    }
    public static void line(MapLocation m1, MapLocation m2, int red, int green, int blue){
        debug_drawLine(m1,m2,red,green,blue);
    }
    public static void line(MapLocation m1, MapLocation m2, int[] colors, Modes mode){
        debug_drawLine(m1,m2,colors,mode);

    }
    public static void line(MapLocation m1, MapLocation m2, int[] colors){
        debug_drawLine(m1,m2,colors);
    }

    public static  void lineTo(MapLocation m){
        rc.setIndicatorLine(R.myLocation,m, rc.getID()+200 % 255, (rc.getID()*2 + 10) % 255, (rc.getID() + 100) % 255);
    }
    public static  void lineTo(MapLocation m, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorLine(R.myLocation, m, rc.getID() + 200 % 255, (rc.getID()*2 + 10) % 255, (rc.getID() + 100) % 255);
        }
    }


    public static  void lineTo(MapLocation m, int r, int g, int b, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorLine(R.myLocation, m, r, g, b);
        }
    }
    public static  void lineTo(MapLocation m, int r, int g, int b){
        rc.setIndicatorLine(R.myLocation, m, r, g, b);
    }

    public static  void lineTo(Direction d, float dist, int r, int g, int b){
        rc.setIndicatorLine(R.myLocation, myLocation.add(d,dist), r, g, b);
    }

    public static  void lineTo(MapLocation m, int[] colors, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorLine(R.myLocation, m, colors[0], colors[1], colors[2]);
        }
    }
    public static  void lineTo(MapLocation m, int[] colors){
        rc.setIndicatorLine(R.myLocation, m, colors[0], colors[1], colors[2]);
    }

    public static  void lineTo(Direction d, float dist, int[] colors){
        rc.setIndicatorLine(R.myLocation, myLocation.add(d,dist), colors[0], colors[1], colors[2]);
    }


    public static  void lineTo(Direction d, float dist){
        rc.setIndicatorLine(R.myLocation, myLocation.add(d,dist), rc.getID()+200 % 255, (rc.getID()*2 + 10) % 255, (rc.getID() + 100) % 255);
    }

    public static void dot(int r, int g, int b){
        rc.setIndicatorDot(R.myLocation,r,g,b);
    }

    public static void dot(MapLocation m ){
        rc.setIndicatorDot(m,rc.getID()+200 % 255, (rc.getID()*2 + 10) % 255, (rc.getID() + 100) % 255);
    }

    public static void dot(MapLocation m,int r, int g, int b){
        rc.setIndicatorDot(m,r,g,b);
    }
    public static void debug_dot(MapLocation m,int r, int g, int b){
        rc.setIndicatorDot(m,r,g,b);
    }


    public static void debug_halo(int r, int g, int b, int ring){

        float dist = 1f +  ((float)ring) * 0.2f;
        MapLocation leftTop = R.myLocation.add(Direction.WEST, dist).add(Direction.NORTH,dist + 0.2f);
        MapLocation rightTop = R.myLocation.add(Direction.EAST, dist).add(Direction.NORTH,dist + 0.2f);
        MapLocation leftBot = R.myLocation.add(Direction.WEST, dist).add(Direction.SOUTH,dist + 0.2f);
        MapLocation rightBot = R.myLocation.add(Direction.EAST, dist).add(Direction.SOUTH,dist + 0.2f);

        R.rc.setIndicatorLine(leftTop,leftBot, r,g,b);
        R.rc.setIndicatorLine(leftTop,rightTop, r,g,b);
        R.rc.setIndicatorLine(rightTop,rightBot, r,g,b);
        R.rc.setIndicatorLine(leftBot,rightBot, r,g,b);
    }


    public static void cross(MapLocation m, float radius, int r, int g, int b, Modes mode){
        debug_cross(m,radius,r,g,b,mode);
    }
    public static void cross(MapLocation m, float radius, int[] colors, Modes mode){
        debug_cross(m,radius,colors,mode);
    }

    public static void debug_cross(MapLocation m, float radius, int[] colors, Modes mode) {
        debug_cross(m,radius,colors[0],colors[1],colors[2],mode);
    }
    public static void debug_cross(MapLocation m, float radius, int r, int g, int b, Modes mode) {
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorLine(m.translate(radius,radius), m.translate(-radius,-radius), r, g, b);
            rc.setIndicatorLine(m.translate(radius,-radius), m.translate(-radius,radius), r, g, b);
        }
    }


    public static void circle(MapLocation m, float radius, int r, int g, int b, Modes mode){
        debug_circle(m,radius,r,g,b,mode);
    }
    public static void circle(MapLocation m, float radius, int r, int g, int b){
        debug_circle(m,radius,r,g,b,LOG_MODE);
    }

    public static void circle(MapLocation m, float radius, int[] colors, Modes mode){
        debug_circle(m,radius,colors,mode);
    }
    public static void circle(MapLocation m, float radius, int[] colors){
        debug_circle(m,radius,colors,LOG_MODE);
    }


    public static void debug_circle(MapLocation m, float radius, int[] colors, Modes mode) {
        debug_circle(m,radius,colors[0],colors[1],colors[2],mode);
    }

    public static void debug_circle(MapLocation m, float radius, int r, int g, int b, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            MapLocation[] points = new MapLocation[12];

            for (int i = 0; i < 12; i++) {
                points[i] = m.add(Direction.NORTH.rotateRightDegrees(30 * i), radius);
            }

            for (int i = 0; i < 11; i++) {
                debug_drawLine(points[i], points[i+1], r, g, b);
            }
            debug_drawLine(points[0], points[11], r, g, b);
        }
    }



    public static void debug_drawLine(MapLocation m1, MapLocation m2, int red, int green, int blue, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorLine(m1, m2, red, green, blue);
        }
    }
    public static void debug_drawLine(MapLocation m1, MapLocation m2, int[] colors, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorLine(m1,m2,colors[0],colors[1],colors[2]);
        }
    }

    public static void debug_drawLine(MapLocation m1, MapLocation m2, int red, int green, int blue){

        rc.setIndicatorLine(m1,m2,red,green,blue);
    }

    public static void debug_drawLine(MapLocation m1, MapLocation m2, int[] colors){

        rc.setIndicatorLine(m1,m2,colors[0],colors[1],colors[2]);
    }


    public static void dot(int r, int g, int b, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorDot(R.myLocation, r, g, b);
        }

    }
    public static void dot(MapLocation m, int r, int g, int b, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            rc.setIndicatorDot(m, r, g, b);
        }

    }
    public static void debug_log(String s ){System.out.println(s);}

    public static void log(int[] arr, Modes mode){debug_log(arr,mode);}
    public static void log(int[] arr){ debug_log(arr);}
    public static void debug_log(int[] arr, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)){
            debug_log(arr);
        }
    }
    public static void debug_log(int[] arr){
        String str = "[";

        for(int i = 0; i < arr.length -1; i++){
            str = str + arr[i] + ",  ";
        }
        if(arr.length != 0) {
            str = str + arr[arr.length - 1];
        }
        System.out.println( str + "]");
    }


    public static void log(float[] arr, Modes mode){debug_log(arr,mode);}
    public static void log(float[] arr){ debug_log(arr);}
    public static void debug_log(float[] arr, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)){
            debug_log(arr);
        }
    }
    public static void debug_log(float[] arr){
        String str = "[";

        for(int i = 0; i < arr.length -1; i++){
            str = str +   ((float)(Math.round(arr[i]*100)))/100f  + ",  ";
        }
        if(arr.length != 0) {

            str = str + ((float) (Math.round(arr[arr.length - 1] * 100))) / 100f;
        }
        System.out.println( str + "]");
    }



    public static void log(String s, Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)){
            System.out.println(s);
        }
    }
    public static void log(String s){
        System.out.println(s);
    }
    public static void log(Exception e){
        System.out.println("Exception: ");
        System.out.println(e.toString());
        e.printStackTrace(System.out);

        if (e.getStackTrace().length > 2) {
            System.out.println(e.getStackTrace()[0]);
            System.out.println(e.getStackTrace()[1]);
            System.out.println(e.getStackTrace()[2]);
        } else if (e.getStackTrace().length == 2) {
            System.out.println(e.getStackTrace()[0]);
            System.out.println(e.getStackTrace()[1]);
        } else if (e.getStackTrace().length == 1) {
            System.out.println(e.getStackTrace()[0]);
        }
    }


    public static int[] debug_randomcolor(){
        return new int[]{ Helper.randomInt(255,113),Helper.randomInt(255,77),Helper.randomInt(255,8)};
    }


    public static void drawFloat(MapLocation start, float val ,Modes mode){
        drawFloat(start,val,debug_randomcolor(),mode);
    }
    public static void drawFloat(MapLocation start, float val){
        drawFloat(start,val,debug_randomcolor());
    }
    public static void drawFloat(float val){
        drawFloat(R.myLocation,val,debug_randomcolor());
    }


    public static void drawFloat(MapLocation start, float val, int r,int g,int b,Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            debug_draw_string(start,"" + ((float)Math.round(val *100)) /100f,r,g,b);
        }
    }
    public static void drawFloat(MapLocation start, float val, int[] colors,Modes mode){
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)) {
            debug_draw_string(start,"" + ((float)Math.round(val *100)) /100f,colors[0],colors[1],colors[2]);
        }
    }
    public static void drawFloat(MapLocation start, float val, int r,int g,int b){
        debug_draw_string(start,"" + ((float)Math.round(val *100)) /100f,r,g,b);

    }
    public static void drawFloat(MapLocation start, float val, int[] colors){
        debug_draw_string(start,"" + ((float)Math.round(val *100)) /100f,colors[0],colors[1],colors[2]);
    }

    public static void drawString(MapLocation start, String str, int[] colors) {
        debug_draw_string(start,str,colors[0],colors[1],colors[2]);

    }
    public static void drawString(MapLocation start, String str, int[] colors, Modes mode) {
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)){
            debug_draw_string(start,str,colors[0],colors[1],colors[2]);
        }
    }

    public static void drawString(MapLocation start, String str, int r, int g, int b, Modes mode) {
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)){
            debug_draw_string(start,str,r,g,b);
        }
    }
    public static void drawString(MapLocation start, String str, int r, int g, int b) {
        debug_draw_string(start,str,r,g,b);
    }
    public static void drawStrings(MapLocation start, String[] strings, int[] colors) {
        debug_draw_strings(start,strings,colors[0],colors[1],colors[2]);

    }
    public static void drawStrings(MapLocation start, String[] strings, int[] colors, Modes mode) {
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)){
            debug_draw_strings(start,strings,colors[0],colors[1],colors[2]);
        }
    }

    public static void drawStrings(MapLocation start, String[] strings, int r, int g, int b, Modes mode) {
        if(mode.equals(LOG_MODE) || mode.equals(Modes.ALL)){
            debug_draw_strings(start,strings,r,g,b);
        }
    }
    public static void drawStrings(MapLocation start, String[] strings, int r, int g, int b) {
        debug_draw_strings(start,strings,r,g,b);
    }

    public static void debug_draw_strings(MapLocation start, String[] strings, int r, int g, int b){
        for(int i = 0; i < strings.length;i++){
            debug_draw_string(start.translate(0,(float)i * 1.5f),strings[i],r,g,b);
        }
    }
    public static void debug_draw_string(MapLocation start, String str, int r, int g, int b){
        start = start.translate(0,-1.5f);
        str = str.toLowerCase();

        for (int i = 0 ; i < str.length(); i++){
            char c = str.charAt(i);
            MapLocation botleft = start.add(Direction.EAST,i);

            switch (c) {
                case '0':
                    rc.setIndicatorLine(botleft.translate(0.5f,0),botleft.translate(0.2f,0.5f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0),botleft.translate(0.8f,0.5f),r,g,b);

                    rc.setIndicatorLine(botleft.translate(0.2f,1f),botleft.translate(0.2f,0.5f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.8f,1f),botleft.translate(0.8f,0.5f),r,g,b);

                    rc.setIndicatorLine(botleft.translate(0.2f,1f),botleft.translate(0.5f,1.5f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.8f,1f),botleft.translate(0.5f,1.5f),r,g,b);
                case '1':
                    rc.setIndicatorLine(botleft.translate(0.7f,0),botleft.translate(0.7f,1.5f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.2f,0.9f),botleft.translate(0.8f,1.5f),r,g,b);
                    break;
                case '2':
                    rc.setIndicatorLine(botleft.translate(0.1f,1.4f),botleft.translate(0.6f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,1.1f),botleft.translate(0.5f,1.45f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,1.2f),botleft.translate(0.9f,0.7f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.15f,0.4f),botleft.translate(0.9f,0.75f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.15f,0.1f),botleft.translate(0.15f,0.45f),r,g,b);

                    rc.setIndicatorLine(botleft.translate(0.15f,0.1f),botleft.translate(1f,0.15f),r,g,b);

                    break;
                case '3':
                    rc.setIndicatorLine(botleft.translate(0.15f,1.3f),botleft.translate(0.6f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,0.75f),botleft.translate(0.6f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,0.75f),botleft.translate(0.3f,0.75f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,0.75f),botleft.translate(0.6f,0.1f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.15f,0.2f),botleft.translate(0.6f,0.1f),r,g,b);
                    break;
                case '4':
                    rc.setIndicatorLine(botleft.translate(0.05f,0.5f),botleft.translate(0.95f,0.5f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.05f,0.5f),botleft.translate(0.6f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.6f,0.1f),botleft.translate(0.6f,1.4f),r,g,b);
                    break;
                case '5':
                    rc.setIndicatorLine(botleft.translate(0.1f,1.4f),botleft.translate(0.9f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.1f,1.4f),botleft.translate(0.1f,0.8f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,0.7f),botleft.translate(0.1f,0.8f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,0.7f),botleft.translate(0.8f,0.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.3f,0.1f),botleft.translate(0.8f,0.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.3f,0.1f),botleft.translate(0.1f,0.4f),r,g,b);
                    break;
                case '6':
                    rc.setIndicatorLine(botleft.translate(0.65f,1.4f),botleft.translate(0.1f,0.65f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0.1f),botleft.translate(0.1f,0.65f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0.1f),botleft.translate(0.9f,0.65f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.1f,0.6f),botleft.translate(0.9f,0.65f),r,g,b);
                    break;
                case '7':
                    rc.setIndicatorLine(botleft.translate(0.1f,1.4f),botleft.translate(0.9f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.1f,0.1f),botleft.translate(0.9f,1.4f),r,g,b);
                    break;
                case '8':

                    rc.setIndicatorLine(botleft.translate(0.5f,1.4f),botleft.translate(0.1f,1.1f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,1.4f),botleft.translate(0.9f,1.1f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0.8f),botleft.translate(0.9f,1.1f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0.8f),botleft.translate(0.1f,1.1f),r,g,b);

                    rc.setIndicatorLine(botleft.translate(0.5f,0.1f),botleft.translate(0.1f,0.45f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0.1f),botleft.translate(0.9f,0.45f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0.8f),botleft.translate(0.9f,0.45f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,0.8f),botleft.translate(0.1f,0.45f),r,g,b);
                    break;
                case '9':
                    rc.setIndicatorLine(botleft.translate(0.35f,0.1f),botleft.translate(0.9f,0.65f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,1.4f),botleft.translate(0.9f,0.65f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.5f,1.4f),botleft.translate(0.1f,0.65f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,0.8f),botleft.translate(0.1f,0.65f),r,g,b);
                    break;
                case '.':
                    rc.setIndicatorLine(botleft.translate(0.5f,0.1f),botleft.translate(0.5f,0.4f),r,g,b);
                    break;
                case ',':
                    rc.setIndicatorLine(botleft.translate(0.55f,0.1f),botleft.translate(0.4f,0.35f),r,g,b);
                    break;
                case '[':
                    rc.setIndicatorLine(botleft.translate(0.1f,0.1f),botleft.translate(0.7f,0.1f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.1f,1.4f),botleft.translate(0.7f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.1f,1.4f),botleft.translate(0.1f,0.1f),r,g,b);
                    break;
                case ']':
                    rc.setIndicatorLine(botleft.translate(0.3f,0.1f),botleft.translate(0.9f,0.1f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.3f,1.4f),botleft.translate(0.9f,1.4f),r,g,b);
                    rc.setIndicatorLine(botleft.translate(0.9f,1.4f),botleft.translate(0.9f,0.1f),r,g,b);
                    break;
                case '-':
                    rc.setIndicatorLine(botleft.translate(0.2f,0.75f),botleft.translate(0.8f,0.75f),r,g,b);
                    break;

                default:
                    break;
            }


        }





    }



    public static void logCurrentStracktrace(){
        try{
            throw new Exception("stacktrace exception");
        }catch (Exception e){
            log(e);
        }
    }
}
