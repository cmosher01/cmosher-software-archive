package com.mediamania.content;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MediaPerson {
    private String      mediaName;
    private String      firstName;
    private String      lastName;
    private Date        birthDate;
    private Set         actingRoles; // Role
    private Set         moviesDirected; // Movie
    
    private MediaPerson()
    { }    
    public MediaPerson(String mediaName) {
        this.mediaName = mediaName;
        actingRoles = new HashSet();
        moviesDirected = new HashSet();
    }
    public MediaPerson(String mediaName, String firstName, String lastName,
                       Date birthDate) {
        this.mediaName = mediaName;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.birthDate = birthDate;
        actingRoles = new HashSet();
        moviesDirected = new HashSet();
    }    
    public String getName() {
        return mediaName;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public Date getBirthDate() {
        return birthDate;
    }
    public void addRole(Role role) {
        actingRoles.add(role);
    }
    public Set getRoles() {
        return Collections.unmodifiableSet(actingRoles);
    }
    public void addMoviesDirected(Movie movie) {
        moviesDirected.add(movie);
    }
    public Set getMoviesDirected() {
        return Collections.unmodifiableSet(moviesDirected);
    }
}