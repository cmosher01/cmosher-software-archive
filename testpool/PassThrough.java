import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PassThrough implements InvocationHandler
{
    private final Object obj;

    public PassThrough(Object obj)
    {
        this.obj = obj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        return method.invoke(obj,args);
    }
}
