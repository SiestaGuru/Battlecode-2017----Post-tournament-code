package sootforcer.DataStructures;

import battlecode.common.MapLocation;
import kotlin.jvm.internal.Intrinsics;

/**
 * Created by Hermen on 30/4/2017.
 */
public class StringBasedMapLocationAnalysis {


    public static String mapLocationStrings = "                ]";

    private static StringBuffer buffer = new StringBuffer();
    public static StringBuilder builder = new StringBuilder();


    //all bytecounts here include the method call. and all methods that return something include storing the variable into something


    //11 bytes
    public static void addMapLocation1(MapLocation m){
        mapLocationStrings = Intrinsics.stringPlus( mapLocationStrings , m.toString());
    }

    //7 bytes
    public static void addMapLocation2(MapLocation m){
        buffer.append(m.toString());
    }
    //7 bytes
    public static void addMapLocation3(MapLocation m){
        builder.append(m.toString());
    }
    public static void addMapLocation4(MapLocation m){
        builder.append(m.toString());
        mapLocationStrings = builder.toString();
    }



    //14 bytes
    public static void addMapLocationInt(MapLocation m, int extraData){
        mapLocationStrings = mapLocationStrings + extraData + m.toString();
    }

    //20 bytes.   note, requires the string to start with something like "      ]" so that it won't crash if we have a hit on the first element
    public static int getIntFromMapLocation(MapLocation m){
        int endIndex = mapLocationStrings.indexOf(m.toString());
        if(endIndex < 0) return endIndex;
        return  Integer.parseInt(mapLocationStrings.substring(mapLocationStrings.indexOf("]",endIndex-10)+1,endIndex));
    }

    //45 bytes
    public static MapLocation getMapLocationFromInt(int i){
        int start = mapLocationStrings.indexOf('[', mapLocationStrings.indexOf(Integer.toString(i)));
        if(start < 0) return null;
        return  MapLocation.valueOf(mapLocationStrings.substring(start, mapLocationStrings.indexOf("]",start)+1));
    }





    //30 bytes
    public static void addCheckNearbyMapLocationsThing(MapLocation m){
        builder.append("+");
        int val = ((int)m.x*1000)+(int)m.y;
        builder.append(val);
        builder.append("+");
        builder.append(val+1);
    }

    //19 bytes
    public static boolean containsNearbyMapLocation(MapLocation m){
        int val = ((int)m.x*1000)+(int)m.y;
        if(builder.toString().contains(Integer.toString(val))) return true;
        return builder.toString().contains(Integer.toString(val + 1));
    }



    //42 bytes
    public static void addCheckNearbyMapLocationsThing2(MapLocation m){
        long x = ((long)m.x) * -100000L;
        long y = (long)m.y;
        builder.append(x - y);
        builder.append(x- y - 1L);
        builder.append(x- y - 100000L);
        builder.append(x- y - 100001L);
    }

    //15 bytes
    public static boolean containsNearbyMapLocation2(MapLocation m){
        return builder.toString().contains(Long.toString((((long)m.x) * -100000L)-((long)m.y)));
    }



    //Following 2 are connected:
    //Work with any variable length
    //23 bytes
    public static void addMapLocationExtra2(MapLocation m, int extraData){
        mapLocationStrings = Intrinsics.stringPlus(mapLocationStrings, String.format("%-31s,%09d;",m.toString(),extraData));
    }
    //17 bytes
    public static int getExtraDataOf2(MapLocation m){
        int index = mapLocationStrings.indexOf(m.toString());
        if(index >= 0)  {
            return Integer.parseInt(mapLocationStrings.substring(index+32,index+41));
        }
        else return -1;
    }




    //13 bytes
    public static void addMapLocTrackIndex(MapLocation m){
        builder.append(String.format("%-29s;",m.toString()));
    }

    //floordiv is required instead of just index/30, because if you divide -1 in java you get 0 (so takes ceiling)
    //8 bytes
    public static int getTrackedIndexOf(MapLocation m){
        return Math.floorDiv(builder.indexOf(m.toString()),30);
    }



    //Throws exception if index doesn't exist
    //37 bytes
    public static MapLocation getMapLocationAtIndex(int i){
        return MapLocation.valueOf(builder.substring(i*30,i*30+29));
    }




    //7 bytes
    public static boolean contains1(MapLocation m){
        return mapLocationStrings.contains(m.toString());
    }
    //10 bytes
    public static boolean contains2(MapLocation m){
        return buffer.indexOf(m.toString()) >= 0;
    }
    //10 bytes
    public static boolean contains3(MapLocation m){
        return builder.indexOf(m.toString()) >= 0;
    }

    //8 bytes
    public static boolean contains4(MapLocation m){
        return buffer.toString().contains(m.toString());
    }
    //8 bytes
    public static boolean contains5(MapLocation m){
        return builder.toString().contains(m.toString());
    }



    //These two are conencted:
    //They only work with known variable length. The extra data here must be 3 chars
    public static void addMapLocationExtra(MapLocation m, String extraData){
        mapLocationStrings = mapLocationStrings + extraData + m.toString();
    }
    public static String getExtraDataOf(MapLocation m){
        int index = mapLocationStrings.indexOf(m.toString());
        if(index > 0)  return mapLocationStrings.substring(index-3,index);
        return null;
    }


}
