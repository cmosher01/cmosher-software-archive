package com.mediamania.content;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Studio {
    private String  name;
    private Set     content; // MediaContent
    
    private Studio()
    { }    
    public Studio(String studioName) {
        name = studioName;
        content = new HashSet();
    }    
    public String getName() {
        return name;
    }    
    public Set getContent() {
        return Collections.unmodifiableSet(content);
    }
    public void addContent(MediaContent mc) {
        content.add(mc);
    }
    public void removeContent(MediaContent mc) {
        content.remove(mc);
    }
}