package com.mediamania.content;

import com.mediamania.MediaManiaApp;

public class LoadStudios extends MediaManiaApp {
    public static void main(String[] args) {
        LoadStudios studios = new LoadStudios();
        studios.executeTransaction();        
    }
    public void execute() {
        Studio studio = new Studio("Buena Vista");
        pm.makePersistent(studio);
        studio = new Studio("20th Century Fox");
        pm.makePersistent(studio);
        studio = new Studio("DreamWorks SKG");
        pm.makePersistent(studio);
    }
}