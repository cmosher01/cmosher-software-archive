package com.mediamania.hotcache;

public class SlaveDriver extends AbstractDriver {
    protected SlaveDriver(String updateURL, String requestURL, 
        String timeout) {
            super(updateURL, requestURL, timeout);
            cache = new SlaveCache();
    }
    
    public static void main(String[] args) {
        SlaveDriver slave = new SlaveDriver(
            args[0], args[1], args[2]);
        slave.serviceReaders();
    }
}