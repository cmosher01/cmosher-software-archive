/*
 * ex: set ro:
 * DO NOT EDIT.
 * generated by smc (http://smc.sourceforge.net/)
 * from file : SubFSMImpl.sm
 */


public class SubFSMImpl
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public SubFSMImpl(Sub owner)
    {
        super (SuperFSM.Idle);

        _owner = owner;
    }

    public SubFSMImpl(Sub owner, SubState initState)
    {
        super (initState);

        _owner = owner;
    }

    public synchronized void enterStartState()
    {
        getState().Entry(this);
        return;
    }

    public synchronized void Cancel()
    {
        _transition = "Cancel";
        getState().Cancel(this);
        _transition = "";
        return;
    }

    public synchronized void Done()
    {
        _transition = "Done";
        getState().Done(this);
        _transition = "";
        return;
    }

    public synchronized void Hit()
    {
        _transition = "Hit";
        getState().Hit(this);
        _transition = "";
        return;
    }

    public synchronized void Release()
    {
        _transition = "Release";
        getState().Release(this);
        _transition = "";
        return;
    }

    public synchronized void toB()
    {
        _transition = "toB";
        getState().toB(this);
        _transition = "";
        return;
    }

    public synchronized void toC()
    {
        _transition = "toC";
        getState().toC(this);
        _transition = "";
        return;
    }

    public synchronized void toX()
    {
        _transition = "toX";
        getState().toX(this);
        _transition = "";
        return;
    }

    public SubState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((SubState) _state);
    }

    protected Sub getOwner()
    {
        return (_owner);
    }

    public void setOwner(Sub owner)
    {
        if (owner == null)
        {
            throw (
                new NullPointerException(
                    "null owner"));
        }
        else
        {
            _owner = owner;
        }

        return;
    }

//---------------------------------------------------------------
// Member data.
//

    transient private Sub _owner;

    public static abstract class SubState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SubState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(SubFSMImpl context) {}
        protected void Exit(SubFSMImpl context) {}

        protected void Cancel(SubFSMImpl context)
        {
            Default(context);
        }

        protected void Done(SubFSMImpl context)
        {
            Default(context);
        }

        protected void Hit(SubFSMImpl context)
        {
            Default(context);
        }

        protected void Release(SubFSMImpl context)
        {
            Default(context);
        }

        protected void toB(SubFSMImpl context)
        {
            Default(context);
        }

        protected void toC(SubFSMImpl context)
        {
            Default(context);
        }

        protected void toX(SubFSMImpl context)
        {
            Default(context);
        }

        protected void Default(SubFSMImpl context)
        {
            throw (
                new statemap.TransitionUndefinedException(
                    "State: " +
                    context.getState().getName() +
                    ", Transition: " +
                    context.getTransition()));
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class SuperFSM
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final SuperFSM_Idle Idle =
            new SuperFSM_Idle("SuperFSM.Idle", 0);
        public static final SuperFSM_Sub Sub =
            new SuperFSM_Sub("SuperFSM.Sub", 1);
        public static final SuperFSM_Next Next =
            new SuperFSM_Next("SuperFSM.Next", 2);
        private static final SuperFSM_Default Default =
            new SuperFSM_Default("SuperFSM.Default", -1);

    }

    protected static class SuperFSM_Default
        extends SubState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SuperFSM_Default(String name, int id)
        {
            super (name, id);
        }

        protected void Default(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();

            SubState endState = context.getState();

            context.clearState();
            try
            {
                ctxt.undefinedChange(endState);
            }
            finally
            {
                context.setState(endState);
            }
            return;
        }
    //-----------------------------------------------------------
    // Member data.
    //
    }

    private static final class SuperFSM_Idle
        extends SuperFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SuperFSM_Idle(String name, int id)
        {
            super (name, id);
        }

        protected void Hit(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.setState(SuperFSM.Sub);
                (context.getState()).Entry(context);
                context.pushState(SubFSM.A);
                (context.getState()).Entry(context);
            }
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    private static final class SuperFSM_Sub
        extends SuperFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SuperFSM_Sub(String name, int id)
        {
            super (name, id);
        }

        protected void Cancel(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.setState(SuperFSM.Idle);
                (context.getState()).Entry(context);
            }
            return;
        }

        protected void Done(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.setState(SuperFSM.Next);
                (context.getState()).Entry(context);
            }
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    private static final class SuperFSM_Next
        extends SuperFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SuperFSM_Next(String name, int id)
        {
            super (name, id);
        }

        protected void Release(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.setState(SuperFSM.Idle);
                (context.getState()).Entry(context);
            }
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class SubFSM
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final SubFSM_A A =
            new SubFSM_A("SubFSM.A", 3);
        public static final SubFSM_B B =
            new SubFSM_B("SubFSM.B", 4);
        public static final SubFSM_C C =
            new SubFSM_C("SubFSM.C", 5);
        public static final SubFSM_SubSub SubSub =
            new SubFSM_SubSub("SubFSM.SubSub", 6);
        private static final SubFSM_Default Default =
            new SubFSM_Default("SubFSM.Default", -1);

    }

    protected static class SubFSM_Default
        extends SubState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SubFSM_Default(String name, int id)
        {
            super (name, id);
        }

        protected void Cancel(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.popState();
            }

            context.Cancel();
            return;
        }

        protected void Default(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();

            SubState endState = context.getState();

            context.clearState();
            try
            {
                ctxt.undefinedChange(endState);
            }
            finally
            {
                context.setState(endState);
            }
            return;
        }
    //-----------------------------------------------------------
    // Member data.
    //
    }

    private static final class SubFSM_A
        extends SubFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SubFSM_A(String name, int id)
        {
            super (name, id);
        }

        protected void toB(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.setState(SubFSM.B);
                (context.getState()).Entry(context);
            }
            return;
        }

        protected void toX(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.setState(SubFSM.SubSub);
                (context.getState()).Entry(context);
                context.pushState(SubSubFSM.X);
                (context.getState()).Entry(context);
            }
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    private static final class SubFSM_B
        extends SubFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SubFSM_B(String name, int id)
        {
            super (name, id);
        }

        protected void toC(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.setState(SubFSM.C);
                (context.getState()).Entry(context);
            }
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    private static final class SubFSM_C
        extends SubFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SubFSM_C(String name, int id)
        {
            super (name, id);
        }

        protected void Done(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.popState();
            }

            context.Done();
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    private static final class SubFSM_SubSub
        extends SubFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SubFSM_SubSub(String name, int id)
        {
            super (name, id);
        }

        protected void Done(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.popState();
            }

            context.Done();
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class SubSubFSM
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Constants.
        //
        public static final SubSubFSM_X X =
            new SubSubFSM_X("SubSubFSM.X", 7);
        private static final SubSubFSM_Default Default =
            new SubSubFSM_Default("SubSubFSM.Default", -1);

    }

    protected static class SubSubFSM_Default
        extends SubState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected SubSubFSM_Default(String name, int id)
        {
            super (name, id);
        }

        protected void Default(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();

            SubState endState = context.getState();

            context.clearState();
            try
            {
                ctxt.undefinedChange(endState);
            }
            finally
            {
                context.setState(endState);
            }
            return;
        }
    //-----------------------------------------------------------
    // Member data.
    //
    }

    private static final class SubSubFSM_X
        extends SubSubFSM_Default
    {
    //-------------------------------------------------------
    // Member methods.
    //

        private SubSubFSM_X(String name, int id)
        {
            super (name, id);
        }

        protected void Done(SubFSMImpl context)
        {
            Sub ctxt = context.getOwner();


            (context.getState()).Exit(context);
            context.clearState();
            try
            {
                ctxt.nothing();
            }
            finally
            {
                context.popState();
            }

            context.Done();
            return;
        }

    //-------------------------------------------------------
    // Member data.
    //
    }
}

/*
 * Local variables:
 *  buffer-read-only: t
 * End:
 */
