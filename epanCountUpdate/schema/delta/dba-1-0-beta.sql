/*
MessageFrom: Doug Fetcho [doug_fetcho@surveysampling.com]
Sent: Thursday, April 21, 2005 11:24 AM
To: Chris Mosher
Subject: RE: epanCountUpdate user and roles for alpha testing

All set

*/
####### Create for the QC user ##############
CREATE USER EPANCOUNTQC
    IDENTIFIED BY checkittwice DEFAULT TABLESPACE SSI_COUNTS 
    TEMPORARY TABLESPACE TEMP
    QUOTA UNLIMITED 
    ON SSI_COUNTS 
    ACCOUNT UNLOCK;
    
GRANT CONNECT,SSI_EMAIL_COUNT_UPDATE TO EPANCOUNTQC;
ALTER USER EPANCOUNTQC default role all except SSI_EMAIL_COUNT_UPDATE;
/*
  -----Original Message-----
  From: Chris Mosher [mailto:chris_mosher@surveysampling.com]
  Sent: Thursday, April 21, 2005 10:40 AM
  To: 'Doug Fetcho'
  Subject: RE: epanCountUpdate user and roles for alpha testing


  Yes, those same roles will be fine. Thank you.


  Christopher A. Mosher
  Sr. Software Engineer
  Survey Sampling International, LLC
  1 Post Rd., Fl. 3
  Fairfield, CT 06824-6237

  -----Original Message-----
  From: Doug Fetcho [mailto:doug_fetcho@surveysampling.com] 
  Sent: Thursday, April 21, 2005 10:40 AM
  To: Chris Mosher
  Cc: 'Danny Agapito'
  Subject: RE: epanCountUpdate user and roles for alpha testing


  Chris,

  Should the EPANCOUNTQC user have the same roles as the EPANCOUNT user?
    -----Original Message-----
    From: Chris Mosher [mailto:chris_mosher@surveysampling.com]
    Sent: Thursday, April 21, 2005 10:21 AM
    To: 'Doug Fetcho'
    Cc: 'Danny Agapito'
    Subject: RE: epanCountUpdate user and roles for alpha testing


    Hi, Doug,
    We are just now ready to go into BETA with this project, so first we need you to make these same changes (see below) that you made for alpha. The only difference for beta will be to use the username EPANCOUNTQC instead of EPANCOUNT.
    Thanks!
    Christopher A. Mosher
    Sr. Software Engineer
    Survey Sampling International, LLC
    1 Post Rd., Fl. 3
    Fairfield, CT 06824-6237

    -----Original Message-----
    From: Doug Fetcho [mailto:doug_fetcho@surveysampling.com] 
    Sent: Monday, March 28, 2005 2:11 PM
    To: Chris Mosher
    Cc: 'Erik Paulsen'; 'Danny Agapito'
    Subject: RE: epanCountUpdate user and roles for alpha testing


    All set!   Below is all the info

    CREATE TABLESPACE SSI_COUNTS
        LOGGING 
        DATAFILE '/ocfst301/oradata/dev/ssi_counts01.dbf' SIZE 1024M 
        REUSE EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT  AUTO 
        
    CREATE USER EPANCOUNT
        IDENTIFIED BY icountrows DEFAULT TABLESPACE SSI_COUNTS 
        TEMPORARY TABLESPACE TEMP
        QUOTA UNLIMITED 
        ON SSI_COUNTS 
        ACCOUNT UNLOCK;
        
    GRANT CONNECT TO EPANCOUNT;

    CREATE ROLE SSI_EMAIL_COUNT IDENTIFIED BY oneby1;
        
    CREATE ROLE SSI_EMAIL_COUNT_UPDATE IDENTIFIED BY b4ugo;


    GRANT SSI_EMAIL_COUNT_UPDATE to EPANCOUNT;

    ALTER USER EPANCOUNT default role all except SSI_EMAIL_COUNT_UPDATE;
      -----Original Message-----
      From: Chris Mosher [mailto:chris_mosher@surveysampling.com]
      Sent: Monday, March 28, 2005 11:55 AM
      To: 'Doug Fetcho'
      Cc: 'Erik Paulsen'; 'Danny Agapito'
      Subject: epanCountUpdate user and roles for alpha testing


      Doug,

      As we discussed in the meeting on Friday, please add the following user and roles to DEV for my alpha testing of phase one of epanCountUpdate:

      1. new role SSI_Email_Count (with password)

      2. new role SSI_Email_Count_Update (with password)

      3. new user EpanCount (this user will own the new PanelistSummary table)
      This user should have the SSI_Email_Count_Update role (not with admin, and not enabled by default).

      (And please send me the passwords for the new user and roles.)

      Thank you,
      Christopher A. Mosher
      Sr. Software Engineer
      Survey Sampling International, LLC
      1 Post Rd., Fl. 3
      Fairfield, CT 06824-6237

*/
