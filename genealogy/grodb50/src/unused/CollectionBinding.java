/*
 * Created on Nov 13, 2005
 */
package unused;

import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class CollectionBinding<C> extends TupleBinding
{
	private final Class<C> c;
	private final TupleBinding bindingElement;

	public CollectionBinding(final Class<C> c, final TupleBinding bindingElement)
	{
		this.c = c;
		this.bindingElement = bindingElement;
	}

	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Collection<C> r = (Collection<C>)(object);

		output.writeInt(r.size());
		for (final C e : r)
		{
			this.bindingElement.objectToEntry(e,output);
		}
	}

	@Override
	public Collection<C> entryToObject(final TupleInput input)
	{
		final Collection<C> r = new Collection<C>();
		try
		{
			r = this.c.newInstance();
		}
		catch (final Throwable e)
		{
			throw new RuntimeException(e);
		}

		final int siz = input.readInt();
		for (int i = 0; i < siz; ++i)
		{
			final TypeVariable<Class<Collection<C>>>[] typeParameters = this.c.getTypeParameters();

			final C e = this.c.cast(this.bindingElement.entryToObject(input));
			r.add(e);
		}

		return r;
	}
}
