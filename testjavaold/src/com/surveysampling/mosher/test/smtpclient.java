package com.surveysampling.mosher.test;

import java.net.*;
import java.io.*;

public class smtpclient
{
    public static void main(String[] rArg) throws Throwable
    {
        Socket s1=new Socket("mail.surveysampling.com",25);
        PrintStream d1=new DataInputStream(new BufferedInputStream(s1.getInputStream(),2500));
        DataInputStream p1=new PrintStream(new BufferedOutputStream(s1.getOutputStream(),2500),true);
        String recvreply=d1.readLine();
        System.out.println("Server Response : " + recvreply);

        p1.println("HELO mail.surveysampling.com");
        recvreply=d1.readLine(); 
        System.out.println("Server Response : " + recvreply);

        p1.println("MAIL FROM: <chris_mosher@surveysampling.com>");
        recvreply=d1.readLine(); 
        System.out.println("Server Response : " + recvreply);

        p1.println("RCPT TO: <cmosher01@yahoo.com>");
        recvreply=d1.readLine(); 
        System.out.println("Server Response : " + recvreply);

        p1.println("DATA");
        recvreply=d1.readLine(); 
        System.out.println("Server Response : " + recvreply);


        p1.println("From : The God of Love<das@rocketmail.com>");
        p1.println("Reply-To : asds@rocketmail.com");
        p1.println("To : sdv@hotmail.com>");
        p1.println("Subject : Gameplan for Disaster"); 
        p1.println("Date : 5th Dec"); 
        p1.println("Content-Type: text/html; charset=us-ascii"); 
        p1.println("Content-Transfer-Encoding: 7-bit"); 


        p1.println("<b>Hello sd,</b>");
        p1.println("Howdy!!!.......So how are things with u");
        p1.println("Just wanted to give u this link....check it out");
        p1.println("\r\n");
        p1.println("\r\n");
        p1.println("<a href=http://www.yahoo.com>Search Engine</a>");
        p1.println("\r\n");
        p1.println("Bye for Now");
        p1.println("<b>The God of Love</b>");


        p1.println(".");
        recvreply=d1.readLine(); 
        System.out.println("Server Response : " + recvreply);

        p1.println("QUIT");
        recvreply=d1.readLine(); 
        System.out.println("Server Response : " + recvreply);

        s1.close();
        System.out.println("Closed Connection with Server");
    }
} 
