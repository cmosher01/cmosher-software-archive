import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

public class VelocityTest
{
    public static void main(String[] args) throws Throwable
    {
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        Context context = new VelocityContext();
        FileDescriptor x;
        BufferedWriter writer = new OutputStreamWriter(new FileOutputStream());
        velocity.mergeTemplate("test.vm", context, writer);
    }
}
