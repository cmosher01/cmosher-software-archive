package com.mediamania.prototype;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import com.mediamania.MediaManiaApp;

public class LoadRoles extends MediaManiaApp {
    private BufferedReader  reader;

    public static void main(String[] args)
    {
        LoadRoles loadRoles = new LoadRoles(args[0]);
        loadRoles.executeTransaction();
    }

    public LoadRoles(String filename)
    {
        try {
            FileReader fr = new FileReader(filename);
            reader = new BufferedReader(fr);
        } catch(java.io.IOException e){
            System.err.print("Unable to open input file ");
            System.err.println(filename);
            System.exit(-1);
        }
    }

    public void execute()
    {
        String lastTitle = "";
        Movie movie = null;
        try {
            while( reader.ready() ){
                String line = reader.readLine();
                StringTokenizer tokenizer = new StringTokenizer(line, ";");
                String title     = tokenizer.nextToken();
                String actorName = tokenizer.nextToken();
                String roleName  = tokenizer.nextToken();
                if( !title.equals(lastTitle) ){
                    movie = PrototypeQueries.getMovie(pm, title);
                    if( movie == null ){
                        System.out.print("Movie title not found: ");
                        System.out.println(title);
                        continue;
                    }
                    lastTitle = title;
                }
                Actor actor = PrototypeQueries.getActor(pm, actorName);
                if( actor == null ){
                    actor = new Actor(actorName);
                    pm.makePersistent(actor);
                }
                Role role = new Role(roleName, actor, movie);
            }
        } catch(java.io.IOException e){
            System.err.println("Exception reading input file");
            System.err.println(e);
            return;
        }
    }
}