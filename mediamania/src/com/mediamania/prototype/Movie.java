package com.mediamania.prototype;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Movie {
    private static SimpleDateFormat yearFmt = new SimpleDateFormat("yyyy");
    
    public static final String[] MPAAratings =
    { "G", "PG", "PG-13", "R", "NC-17", "NR" };
    private String      title;
    private Date        releaseDate;
    private int         runningTime;
    private String      rating;
    private String      webSite;
    private String      genres;
    private Set         cast;    // element type: Role

private Movie()
{ }

public Movie(String title, Date release, int duration, String rating,
             String genres)
{
    this.title = title;
    releaseDate = release;
    runningTime = duration;
    this.rating = rating;
    this.genres = genres;
    cast = new HashSet();
}

public String getTitle()
{
    return title;
}

public Date getReleaseDate()
{
    return releaseDate;
}

public String getRating()
{
    return rating;
}
    
public int getRunningTime()
{
    return runningTime;
}

public String getGenres()
{
    return genres;
}

public void setWebSite(String site) 
{
    webSite = site;
}

public String getWebSite()
{
    return webSite;
}

public void addRole(Role role)
{
    cast.add(role);
}

public Set getCast()
{
    return Collections.unmodifiableSet(cast);
}

public static Date parseReleaseDate(String val)
{
    Date date = null;
    try {
        date = yearFmt.parse(val);
    } catch(java.text.ParseException exc){ }
    return date;
}

public String formatReleaseDate()
{
    return yearFmt.format(releaseDate);
}
}