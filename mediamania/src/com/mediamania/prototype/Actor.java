package com.mediamania.prototype;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Actor {
private String  name;
private Set     roles; // element type: Role

private Actor()
{ }

public Actor(String name)
{
    this.name = name;
    roles = new HashSet();
}

public String getName()
{
    return name;
}

public void addRole(Role role)
{
    roles.add(role);
}

public void removeRole(Role role)
{
    roles.remove(role);
}

public Set getRoles()
{
    return Collections.unmodifiableSet(roles);
}
}