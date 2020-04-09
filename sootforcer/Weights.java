package sootforcer;


import sootforcer.Test;

//Used for development. For final versions, inline everything, since this is quite a bit more expensive
public class Weights {

    static float[] totalweights = new float[10];
    static StringBuilder[] builder = new StringBuilder[10];
    static String[] channelName = new String[10];

    public static void Init(int channel, String name){
        totalweights[channel] = 0;
        channelName[channel] = name;
        builder[channel] = new StringBuilder();
    }

    public static void Add(int channel, String identifier, float value){
        if(value != 0) {
            totalweights[channel] += value;
            builder[channel].append("   ").append(identifier).append(": ").append(((float) Math.round(value * 100)) / 100f);
        }
    }

    public static float Get(int channel){
        return totalweights[channel];
    }

    public static void Dump(int channel){
        Test.log(channelName[channel] + ":    " + totalweights[channel] + builder[channel].toString());
    }

}
