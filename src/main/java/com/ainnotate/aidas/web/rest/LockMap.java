package com.ainnotate.aidas.web.rest;

import java.util.HashMap;

public class LockMap {

    private static LockMap lockMap = null;
    private HashMap<Long,Integer> objMap = new HashMap<>();

    public HashMap<Long, Integer> getObjMap() {
        return objMap;
    }

    public void setObjMap(HashMap<Long, Integer> objMap) {
        this.objMap = objMap;
    }

    private LockMap(){

    }
    public static LockMap getInstance(){
        if(lockMap==null){
            lockMap = new LockMap();
        }
        return lockMap;
    }
}
