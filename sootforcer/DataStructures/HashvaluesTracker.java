package sootforcer.DataStructures;


import battlecode.common.MapLocation;

/**
 * Created by Hermen on 20/5/2017.
 */




//Simple structure to allow tracking whether we've seen a hasvalue before
public class HashvaluesTracker {

    public  StringBuilder hashmap = new StringBuilder();

    public void add(char hashvalue){
        hashmap.append(hashvalue); //4 bytes
    }

    public boolean contains(char hashvalue){
        return hashmap.indexOf(Character.toString(hashvalue)) >= 0; //4 bytes
    }

    public void add(int hashvalue){
        hashmap.append((char)hashvalue); //4 bytes
    }
    public boolean contains(int hashvalue){
        return hashmap.indexOf(Character.toString((char)hashvalue)) >= 0; //4 bytes
    }

    public void add(MapLocation m){
        hashmap.append((char)m.hashCode());
    }
    public boolean contains(MapLocation m){
        return hashmap.indexOf(Character.toString((char)m.hashCode())) >= 0;
    }


}
