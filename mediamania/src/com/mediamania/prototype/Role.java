package com.mediamania.prototype;

public class Role {
    private String  name;
    private Actor   actor;
    private Movie   movie;

private Role()
{ }

public Role(String name, Actor actor, Movie movie)
{
    this.name = name;
    this.actor = actor;
    this.movie = movie;
    actor.addRole(this);
    movie.addRole(this);
}

public String getName()
{
    return name;
}

public Actor getActor()
{
    return actor;
}

public Movie getMovie()
{
    return movie;
}
}