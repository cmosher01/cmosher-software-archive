//----------------------------------------------
//
// FSM:       PropertiesFileParser
// Context:   PropertiesFileActions
// Exception: ParseException
// Version:   
// Generated: Thursday 07/24/2008 at 17:28:57 EDT
//
//----------------------------------------------


package smcsolution;

//----------------------------------------------
//
// class PropertiesFileParser
//    This is the Finite State Machine class
//
public class PropertiesFileParser extends PropertiesFileActions
{
  private State itsState;
  private static String itsVersion = "";

  // instance variables for each state
  private static Want_equals itsWant_equalsState;
  private static Name itsNameState;
  private static Value itsValueState;
  private static Want_value itsWant_valueState;
  private static Want_name itsWant_nameState;

  // constructor
  public PropertiesFileParser()
  {
    itsWant_equalsState = new Want_equals();
    itsNameState = new Name();
    itsValueState = new Value();
    itsWant_valueState = new Want_value();
    itsWant_nameState = new Want_name();

    itsState = itsWant_nameState;

    // Entry functions for: want_name
  }

  // accessor functions

  public String getVersion()
  {
    return itsVersion;
  }

  public String getCurrentStateName()
  {
    return itsState.stateName();
  }

  // event functions - forward to the current State

  public void Oth() throws ParseException
  {
    itsState.oth();
  }

  public void Ws() throws ParseException
  {
    itsState.ws();
  }

  public void Eq() throws ParseException
  {
    itsState.eq();
  }

  public void X() throws ParseException
  {
    itsState.x();
  }

//--------------------------------------------
//
// private class State
//    This is the base State class
//
  private abstract class State
  {
    public abstract String stateName();

    // default event functions

    public void oth() throws ParseException
    {
      throw new ParseException( "oth", itsState.stateName());
    }

    public void ws() throws ParseException
    {
      throw new ParseException( "ws", itsState.stateName());
    }

    public void eq() throws ParseException
    {
      throw new ParseException( "eq", itsState.stateName());
    }

    public void x() throws ParseException
    {
      throw new ParseException( "x", itsState.stateName());
    }

  }
  //--------------------------------------------
  //
  // class Want_equals
  //    handles the want_equals State and its events
  //
  private class Want_equals extends State
  {
    public String stateName()
      { return "want_equals"; }

    //
    // responds to eq event
    //
    public void eq()
    {

      // change the state
      itsState = itsWant_valueState;
    }

    //
    // responds to ws event
    //
    public void ws()
    {
    }
  }

  //--------------------------------------------
  //
  // class Name
  //    handles the name State and its events
  //
  private class Name extends State
  {
    public String stateName()
      { return "name"; }

    //
    // responds to ws event
    //
    public void ws()
    {

      // change the state
      itsState = itsWant_equalsState;
    }

    //
    // responds to x event
    //
    public void x()
    {

      append_name();
    }

    //
    // responds to eq event
    //
    public void eq()
    {

      // change the state
      itsState = itsWant_valueState;
    }
  }

  //--------------------------------------------
  //
  // class Value
  //    handles the value State and its events
  //
  private class Value extends State
  {
    public String stateName()
      { return "value"; }

    //
    // responds to x event
    //
    public void x()
    {

      append_value();
    }

    //
    // responds to ws event
    //
    public void ws()
    {

      append_value();
    }

    //
    // responds to oth event
    //
    public void oth()
    {

      append_value();
    }

    //
    // responds to eq event
    //
    public void eq()
    {

      append_value();
    }
  }

  //--------------------------------------------
  //
  // class Want_value
  //    handles the want_value State and its events
  //
  private class Want_value extends State
  {
    public String stateName()
      { return "want_value"; }

    //
    // responds to x event
    //
    public void x()
    {

      append_value();

      // change the state
      itsState = itsValueState;
    }

    //
    // responds to oth event
    //
    public void oth()
    {

      append_value();

      // change the state
      itsState = itsValueState;
    }

    //
    // responds to ws event
    //
    public void ws()
    {
    }

    //
    // responds to eq event
    //
    public void eq()
    {

      append_value();

      // change the state
      itsState = itsValueState;
    }
  }

  //--------------------------------------------
  //
  // class Want_name
  //    handles the want_name State and its events
  //
  private class Want_name extends State
  {
    public String stateName()
      { return "want_name"; }

    //
    // responds to x event
    //
    public void x()
    {

      append_name();

      // change the state
      itsState = itsNameState;
    }

    //
    // responds to ws event
    //
    public void ws()
    {
    }
  }

}
