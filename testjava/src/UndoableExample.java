import com.surveysampling.util.UndoableReference;

public class UndoableExample implements Cloneable
{
    private int x;
    private final nu.mine.mosher.util.UndoableReference u = new UndoableReference(this);



    public UndoableExample(int a)
    {
        x = a;
    }

    public Object clone()
    {
        UndoableExample clon = null;
        try
        {
            clon = (UndoableExample)super.clone();
        }
        catch (CloneNotSupportedException cantHappen)
        {
        }
        return clon;
    }

    private UndoableExample ref()
    {
        return (UndoableExample)u.state();
    }

    public String toString()
    {
        return "" + ref().x;
    }

    public void set(int x)
    {
        try
        {
            u.save();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        ref().x = x;
    }

    public void undo()
    {
        u.undo();
    }

    public void redo()
    {
        u.redo();
    }
}
