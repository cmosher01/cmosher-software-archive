package com.mediamania.content;

public class Role {
    private String      name;
    private MediaPerson actor;
    private Movie       movie;

    private Role()
    { }
    public Role(String name, MediaPerson actor, Movie movie) {
        this.name = name;
        this.actor = actor;
        this.movie = movie;
        actor.addRole(this);
        movie.addRole(this);
    }
    public String getName() {
        return name;
    }
    public MediaPerson getActor() {
        return actor;
    }
    public Movie getMovie() {
        return movie;
    }
    public void setMovie(Movie theMovie) {
        movie = theMovie;
    }
}