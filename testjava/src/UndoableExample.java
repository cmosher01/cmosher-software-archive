import com.surveysampling.util.UndoableReference;

public class UndoableExample implements Cloneable
{
    private int x;
    private UndoableReference u;



    public UndoableExample(int a)
    {
        x = a;
        try
        {
            u = new UndoableReference(this);
        }
        catch (CloneNotSupportedException cantHappen)
        {
        }
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
        this.x = x;
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
