import java.util.Collection;
import java.util.Iterator;

public class Filter
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
            output.add(operation(element));
        }
    }

    protected abstract Object operation(Object element);
}
