/*
File Init02.java
Copyright 2003 R.G.Baldwin

Illustrates the use of instance initializers.

Instance initializers behave much like noarg
constructors, and are particularly useful for
anonymous classes, which are not allowed to
define any constructors, even those that take no
arguments.

This program defines a class named B that extends
a class named A.  Parameterized constructors are
used in both A and B to instantiate an object of
the class named B.

The controlling class defines and initializes
a static variable containing the time that the
program starts running in msec.  This value is
used as the base for computing time intervals
later as the execution of the program progresses.
The times that are computed and displayed later
are in msec relative to the time at which the
program starts running.

Static initializers are defined in both A and B
to display the time that the two classes are
loaded and the order in which they are loaded.

An instance variable is defined in the class
named B and is initialized (using a simple
initialization expression) with the time in msec
that the variable is initialized.  The physical
location of the instance variable follows the
first of two instance initializer in the class
definition.

In addition, two separate instance initializers
are defined in the class named B that perform
initialization after the constructor for A
completes execution and before the constructor
for B begins execution.  The first of these
initializers executes before the instance
variable mentioned above is initialized.  The
second of these initializers executes after the
instance variable is initialized, demonstrating
that initialization based on simple
initialization expressions and instance
initializers occurs in the order that the code'
appears in the class definition.

The two constructors and the two initializers
each get and print time information when they are
executed to show the order in which the
constructors and the initializers are executed.

Two separate instances of the class named B are
created, showing not only the order in which the
instance initializers and the constructors are
executed, but also showing that the static
initializers are executed one time only when the
classes are loaded.

Each time an object of the class named B is
instantiated, an instance method of the class
is invoked to display the values of the instance
variables initialized during the process
of instantiating the object of the class named B.

100-msec time delays are purposely inserted at
strategic points within the program in order to
force the time intervals between the occurrence
of the different steps in the program to be
measurable.

The output for one run is shown below.  Your
results may be different depending on the speed
of your computer.

Instantiate first obj: 0
Class A loaded: 10
Class B loaded: 110

Construct 1A:  210
Initializer-1: 310
Initializer-2: 510
Construct 1B:  611

Initialization values:
class A xstr: 210
class B init-1: 310
class B simple init: 410
class B init-2: 510
class B xstr: 611

Instantiate second obj: 711
Construct 2A:  811
Initializer-1: 911
Initializer-2: 1111
Construct 2B:  1211

Initialization values:
class A xstr: 811
class B init-1: 911
class B simple init: 1011
class B init-2: 1111
class B xstr: 1211

Note the 100-msec elapsed time intervals
between the various steps in the execution of the
program.  Also note the order in which the class
loading operations and the initialization steps
occur.

Tested using SDK 1.4.1 under WinXP
************************************************/
import java.util.Date;

public class Init02
{
    //Establish the base time in msec.
    static long baseTime = new Date().getTime();

    //This is a utility method used to insert a
    // 100-millisecod delay.
    static void delay()
    {
        try
        {
            Thread.sleep(100);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    } //end delay
    //-------------------------------------------//

    //This is a utility method used to compute the
    // current time relative to the value stored
    // in the static variable named baseTime.
    static long relTime()
    {
        return ((new Date().getTime()) - baseTime);
    } //end printTime

    //-------------------------------------------//

    public static void main(String[] args)
    {
        //Invoke a parameterized constructor for the
        // class named B, which is a subclass of A.
        // Also invoke the showData method on that
        // object to display the values of the
        // instance variables that were initialized
        // during the construction of the object.
        System.out.println("Instantiate first obj: " + relTime());
        new B("Construct 1").showData();
        //Sleep 100 msec and then instantiate another
        // object.
        delay();
        System.out.println("Instantiate second obj: " + relTime());
        new B("Construct 2").showData();
    } //end main
    //-------------------------------------------//
} //end class Init02
//=============================================//

class A
{
    long aXstrTime;

    static { //This is a static initializer, which is
        // run one time only when the class is loaded.
        //Print a message showing the time that the
        // class finishes loading.
        System.out.println("Class A loaded: " + Init02.relTime());
    } //End static initializer
    //-------------------------------------------//

    A(String str)
    { //constructor
        //Sleep for 100 msec before completing this
        // construction
        Init02.delay();

        //Record the time of construction and print
        // a message showing the construction time.
        aXstrTime = Init02.relTime();
        System.out.println(str + "A:  " + aXstrTime);
    } //end constructor for A

} //end class A
//=============================================//

class B extends A
{
    long bXstrTime;
    long init1Time;
    long init2Time;

    static { //This is a static initializer, which is
        // run one time only when the class is loaded.
        //Sleep for 100 msec to show the order
        // that the classes named A and B are loaded.
        Init02.delay();
        //Print a message showing the time that the
        // class finishes loading.
        System.out.println("Class B loaded: " + Init02.relTime() + "\n");
    } //End static initializer
    //-------------------------------------------//

    { //This is an instance initializer
        //Sleep for 100 msec before doing this
        // initialization.
        Init02.delay();
        //Record the time and print a message showing
        // the time that this instance initializer
        // was executed.
        init1Time = Init02.relTime();
        System.out.println("Initializer-1: " + init1Time);

        //Sleep for 100 msec after doing this
        // initialization to separate this
        // initialization from the initialization of
        // the instance variable that follows.
        Init02.delay();
    } //end instance initializer
    //-------------------------------------------//

    //Note that this initialized instance variable
    // is located after the first instance
    // initializer and before the second instance
    // initializer..
    long simpleInitTime = Init02.relTime();

    //-------------------------------------------//

    //Note that this constructor is physically
    // located between the two instance initializer
    // blocks.  Both initializer blocks are
    // executed before the constructor for this
    // class is executed, but after the constructor
    // for the superclass is executed.
    B(String str)
    {
        //Invoke a parameterized constructor on the
        // superclass.
        super(str);
        //Sleep for 100 msec before constructing
        // this part of the object.
        Init02.delay();
        //Record the time and print a message showing
        // the construction time for this part of
        // the object.
        bXstrTime = Init02.relTime();
        System.out.println(str + "B:  " + bXstrTime);
    } //end constructor for B
    //-------------------------------------------//

    { //This is another instance initializer
        //Sleep for 100 msec before doing this
        // initialization.
        Init02.delay();
        //Record the time and print a message showing
        // the time that this instance initializer
        // was executed.
        init2Time = Init02.relTime();
        System.out.println("Initializer-2: " + init2Time);
    } //end instance initializer
    //-------------------------------------------//

    void showData()
    {
        //This method displays the values that were
        // saved in the instance variables during the
        // five initialization steps, one of which
        // was execution of the superclass
        // constructor.  The values are displayed
        // in the order that the initialization steps
        // occurred.
        System.out.println("\nInitialization values:");
        System.out.println("class A xstr: " + aXstrTime);
        System.out.println("class B init-1: " + init1Time);
        System.out.println("class B simple init: " + simpleInitTime);
        System.out.println("class B init-2: " + init2Time);
        System.out.println("class B xstr: " + bXstrTime);
        System.out.println(); //blank line
    } //end showData
} //end class B
