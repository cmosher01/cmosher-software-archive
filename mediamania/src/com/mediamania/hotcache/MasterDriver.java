package com.mediamania.hotcache;

public class MasterDriver extends AbstractDriver {
    protected MasterDriver(String updateURL, String requestURL, 
        String timeout) {
            super(updateURL, requestURL, timeout);
            cache = new MasterCache();
    }
    
    public static void main(String[] args) {
        MasterDriver master = new MasterDriver(
            args[0], args[1], args[2]);
        master.serviceReaders();
    }
}