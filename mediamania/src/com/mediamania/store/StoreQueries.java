package com.mediamania.store;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.mediamania.content.MediaContent;
import com.mediamania.content.MediaPerson;
import com.mediamania.content.Movie;

public class StoreQueries {
    
    public static RentalCode getRentalCode(PersistenceManager pm,
                                           String codeName) {
        Extent codeExtent = pm.getExtent(RentalCode.class, false);
        Query query = pm.newQuery(codeExtent, "code == codeName");
        query.declareParameters("String codeName");
        Collection result = (Collection) query.execute(codeName);
        Iterator iter = result.iterator();
        RentalCode rentalCode = 
            (RentalCode) (iter.hasNext() ? iter.next() : null);
        query.close(result);
        return rentalCode;
    }
    
    public static Movie getMovieByTitle(PersistenceManager pm,
                                        String movieTitle) {
        Extent movieExtent = pm.getExtent(Movie.class, true);
        Query query = pm.newQuery(movieExtent, "title == movieTitle");
        query.declareParameters("String movieTitle");
        Collection result = (Collection) query.execute(movieTitle);
        Iterator iter = result.iterator();
        Movie movie = (Movie) (iter.hasNext() ? iter.next() : null);
        query.close(result);
        return movie;
    }
    
    public static Customer getCustomer(PersistenceManager pm,
                                    String fname, String lname) {
        Extent customerExtent = pm.getExtent(Customer.class, true);
        String filter = "fname == firstName && lname == lastName";
        Query query = pm.newQuery(customerExtent, filter);
        query.declareParameters("String fname, String lname");
        Collection result = (Collection) query.execute(fname, lname);
        Iterator iter = result.iterator();
        Customer customer = (Customer) (iter.hasNext() ? iter.next() : null);
        query.close(result);
        return customer;
    }
    
    public static void queryCustomers(PersistenceManager pm,
                                    String city, String state) {
        Extent customerExtent = pm.getExtent(Customer.class, true);
        String filter = "address.city == city && address.state == state";
        Query query = pm.newQuery(customerExtent, filter);
        query.declareParameters("String city, String state");
        query.setOrdering(
        "address.zipcode ascending, lastName ascending, firstName ascending");
        Collection result = (Collection) query.execute(city, state);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Customer customer = (Customer) iter.next();
            Address address = customer.getAddress();
            System.out.print(address.getZipcode());     System.out.print(" ");
            System.out.print(customer.getFirstName());  System.out.print(" ");
            System.out.print(customer.getLastName());   System.out.print(" ");
            System.out.println(address.getStreet());
        }
        query.close(result);   
    }

    public static void queryMovie1(PersistenceManager pm,
                               String rating, int runtime, MediaPerson dir) {
        Extent movieExtent = pm.getExtent(Movie.class, true);
        String filter =
        "rating == movieRating && runningTime <= runTime && director == dir";
        Query query = pm.newQuery(movieExtent, filter);
        query.declareParameters(
                    "String movieRating, int runTime, MediaPerson dir");
        Collection result = (Collection)
                            query.execute(rating, new Integer(runtime), dir);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Movie movie = (Movie) iter.next();
            System.out.println(movie.getTitle());
        }
        query.close(result);
    }

    public static void queryMovie2(PersistenceManager pm,
                               String rating, int runtime, MediaPerson dir,
                               Date date) {
        Extent movieExtent = pm.getExtent(Movie.class, true);
        String filter = "rating == movieRating && runningTime <= runTime && " +
                        "director == dir && releaseDate >= date";
        Query query = pm.newQuery(movieExtent, filter);
        query.declareImports("import java.util.Date");
        query.declareParameters(
                "String movieRating, int runTime, MediaPerson dir, Date date");
        HashMap parameters = new HashMap();
        parameters.put("movieRating", rating);
        parameters.put("runTime", new Integer(runtime));
        parameters.put("dir", dir);
        parameters.put("date", date);
        Collection result = (Collection) query.executeWithMap(parameters);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Movie movie = (Movie) iter.next();
            System.out.println(movie.getTitle());
        }
        query.close(result);
    }

    public static void queryMovie3(PersistenceManager pm,
                                    String rating, int runtime, MediaPerson dir,
                                    Date date) {
        Extent movieExtent = pm.getExtent(Movie.class, true);
        String filter = "rating == movieRating && runningTime <= runTime && " +
                        "director == dir && releaseDate >= date";
        Query query = pm.newQuery(movieExtent, filter);
        query.declareImports("import java.util.Date");
        query.declareParameters(
                "String movieRating, int runTime, MediaPerson dir, Date date");
        Object[] parameters = { rating, new Integer(runtime), dir, date };
        Collection result = (Collection) query.executeWithArray(parameters);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Movie movie = (Movie) iter.next();
            System.out.println(movie.getTitle());
        }
        query.close(result);
    }
    
    public static void queryMovie4(PersistenceManager pm) {
        Extent movieExtent = pm.getExtent(Movie.class, true);
        String filter = "!(rating == \"G\" || rating == \"PG\") && " +
                        "(runningTime >= 60 && runningTime <= 105)";
        Query query = pm.newQuery(movieExtent, filter);
        Collection result = (Collection) query.execute();
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Movie movie = (Movie) iter.next();
            System.out.println(movie.getTitle());
        }
        query.close(result);
    }

    public static void getDirectorAlsoActor(PersistenceManager pm) {
        Extent movieExtent = pm.getExtent(Movie.class, true);
        String filter = "cast.contains(role) && role.actor == director";
        Query query = pm.newQuery(movieExtent, filter);
        query.declareVariables("Role role");
        Collection result = (Collection) query.execute();
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Movie movie = (Movie) iter.next();
            System.out.print(movie.getTitle());
            System.out.print(", ");
            System.out.println(movie.getDirector().getName());
        }
    }

    public static void queryTransactions(PersistenceManager pm, Customer cust) {
        Query query = pm.newQuery(com.mediamania.store.Transaction.class,
                                  cust.getTransactionHistory());
        String filter =
            "((Movie)(((Rental)this).rentalItem.mediaItem.content)).director." +
            "mediaName == \"James Cameron\"";
        query.declareImports("import com.mediamania.content.Movie");
        query.setFilter(filter);
        Collection result = (Collection) query.execute();
        Iterator iter = result.iterator();
        while (iter.hasNext() ){
            Rental rental = (Rental) iter.next();
            MediaContent content =
                    rental.getRentalItem().getMediaItem().getMediaContent();
            System.out.println(content.getTitle());
        }
        query.close(result);
    }

    public static void queryMoviesSeenInCity(PersistenceManager pm,
                                             String city) {
        String filter = "mediaItems.contains(item) &&" +
                    "(item.rentalItems.contains(rentItem) && " +
                    "(rentItem.currentRental.customer.address.city == city))";
        Extent movieExtent = pm.getExtent(Movie.class, true);
        Query query = pm.newQuery(movieExtent, filter);
        query.declareImports("import com.mediamania.store.MediaItem; " +
                         "import com.mediamania.store.RentalItem");
        query.declareVariables("MediaItem item; RentalItem rentItem");
        query.declareParameters("String city");
        Collection result = (Collection) query.execute(city);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Movie movie = (Movie) iter.next();
            System.out.println(movie.getTitle());
        }
        query.close(result);
    }

    public static void queryTransactionsInCity(PersistenceManager pm,
                               String city, String state, Date acquired) {
        Extent transactionExtent =
                    pm.getExtent(com.mediamania.store.Transaction.class, true);
        Query query = pm.newQuery(transactionExtent);
        query.declareParameters("String thecity, String thestate, Date date");
        query.declareImports("import java.util.Date");
        String filter = "customer.address.city == thecity && " +
                "customer.address.state == thestate && acquisitionDate >= date";
        query.setFilter(filter);
        String order =  "customer.address.zipcode descending, " +
                      "customer.lastName ascending, " +
                      "customer.firstName ascending, acquisitionDate ascending";
        query.setOrdering(order);
        Collection result = (Collection) query.execute(city, state, acquired);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            com.mediamania.store.Transaction tx =
                                (com.mediamania.store.Transaction) iter.next();        
            Customer cust = tx.getCustomer();
            Address addr = cust.getAddress();
            System.out.print(addr.getZipcode());
            System.out.print(cust.getLastName());  System.out.print(" ");
            System.out.print(cust.getFirstName()); System.out.print(" ");
            System.out.println(tx.getAcquisitionDate());        
        }
        query.close(result);
    }

    public static void queryProfits(PersistenceManager pm, BigDecimal value,
                                    BigDecimal sellCost, BigDecimal rentCost) {
        Query query = pm.newQuery(MediaItem.class);
        query.declareImports("import java.math.BigDecimal");
        query.declareParameters(
                "BigDecimal value, BigDecimal sellCost, BigDecimal rentCost");
        query.setFilter("soldYTD * (purchasePrice - sellCost) + " +
                        "rentedYTD * (rentalCode.cost - rentCost) > value");
        Collection result = (Collection)query.execute(value, sellCost,rentCost);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            MediaItem item = (MediaItem) iter.next();
            // process MediaItem
        }
        query.close(result);
    }

    public static RentalItem getRentalItem(
                                PersistenceManager pm, String serialNumber) {
        Query query = pm.newQuery(RentalItem.class);
        query.declareParameters("String serialNumber");
        query.setFilter("this.serialNumber == serialNumber");
        Collection result = (Collection)query.execute(serialNumber);
        Iterator iter = result.iterator();
        RentalItem item = (RentalItem) (iter.hasNext() ? iter.next() : null);
        query.close(result);
        return item;
    }
    
    public static MediaItem getMediaItem(
                        PersistenceManager pm, String title, String format) {
        Query query = pm.newQuery(MediaItem.class);
        query.declareParameters("String title, String format");
        query.setFilter("this.format == format && content.title == title");
        Collection result = (Collection)query.execute(title, format);
        Iterator iter = result.iterator();
        MediaItem item = (MediaItem) (iter.hasNext() ? iter.next() : null);
        query.close(result);
        return item;
    }

    public static Query newQuery(PersistenceManager pm, Class cl,InputStream is) 
                                throws IOException {
        Properties props = new Properties();
        props.load(is);
        Query q = pm.newQuery(cl);
        q.setFilter((String)props.get("filter"));
        q.declareParameters((String)props.get("parameters"));
        q.declareVariables((String)props.get("variables"));
        q.setOrdering((String)props.get("ordering"));
        q.declareImports((String)props.get("imports"));
        q.setIgnoreCache(Boolean.getBoolean((String)props.get("ignoreCache")));
        return q;
    }
}