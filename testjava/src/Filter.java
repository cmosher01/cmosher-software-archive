import java.util.Collection;
import java.util.Iterator;

public abstract class Filter
{
    private final Collection input;

    public Filter(Collection input)
    {
        this.input = input;
    }

    public void filter(Collection output)
    {
        for (Iterator i = input.iterator(); i.hasNext();)
        {
            Object element = i.next();
            if (element != null)
            {
                output.add(operation(element));
            }
        }
    }

    protected abstract Object operation(Object element);
}
