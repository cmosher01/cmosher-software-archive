/*
 * This file contains an email conversation between Software
 * Dept. and DBA concerning the steps to be run BY THE DBA
 * in the release process of the epanCountUpdate program.
 */

/*
MessageFrom: Doug Fetcho [doug_fetcho@surveysampling.com]
Sent: Monday, March 28, 2005 2:11 PM
To: Chris Mosher
Cc: 'Erik Paulsen'; 'Danny Agapito'
Subject: RE: epanCountUpdate user and roles for alpha testing

All set!   Below is all the info
*/

CREATE TABLESPACE SSI_COUNTS
    LOGGING 
    DATAFILE '/ocfst301/oradata/dev/ssi_counts01.dbf' SIZE 1024M 
    REUSE EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT  AUTO;
    
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
/*
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
