package sootforcer.DataStructures;

import sootforcer.R;

/**
 * Created by Hermen on 22/4/2017.
 */
public class ReusableBroadcastReader {
    public static int i1;
    public static int i2;
    public static int i3;
    public static int i4;




    private static final int twoValuesSplitter = 65536;
    private static final int threeValuesSplitter = 1024;
    private static final int threeValuesSplitter2 = 1048576;

    private static final int fourValuesSplitter = 256;
    private static final int fourValuesSplitter2 = 65536;
    private static final int fourValuesSplitter3 = 16777216;




    //Can only handle variables up to 65536
    public static void readChannel2Values(int channel) throws Exception{
        int val = R.rc.readBroadcastInt(channel);
        i1 = val % twoValuesSplitter;
        i2 = val / twoValuesSplitter;
    }
    public static void broadcastChannel2Values(int channel, int val1, int val2) throws Exception{
        R.rc.broadcastInt(channel,val1 * twoValuesSplitter + val2);
    }


    //Can only handle variables up to 1024
    public static void readChannel3Values(int channel) throws Exception{
        int val = R.rc.readBroadcastInt(channel);
        i1 = val / threeValuesSplitter2;
        int splitted = val % threeValuesSplitter2;
        i2 = splitted / threeValuesSplitter;
        i3 = splitted % threeValuesSplitter;
    }
    public static void broadcastChannel3Values(int channel, int val1, int val2, int val3) throws Exception{
        R.rc.broadcastInt(channel,val1 * threeValuesSplitter2 + val2 * threeValuesSplitter + val3 );
    }


    //Can only handle variables up to 256
    public static void readChannel4Values(int channel) throws Exception{
        int val = R.rc.readBroadcastInt(channel);

        int splitted = val % fourValuesSplitter3;
        int splitted2 = splitted % fourValuesSplitter2;
        i1 = val / fourValuesSplitter3;
        i2 = splitted / fourValuesSplitter2;
        i3 = splitted2 / fourValuesSplitter;
        i4 = splitted2 % fourValuesSplitter;
    }
    public static void broadcastChannel4Values(int channel, int val1, int val2, int val3, int val4) throws Exception{
        R.rc.broadcastInt(channel,val1 * fourValuesSplitter3 + val2 * fourValuesSplitter2 + val3 * fourValuesSplitter + val4 );
    }


}
