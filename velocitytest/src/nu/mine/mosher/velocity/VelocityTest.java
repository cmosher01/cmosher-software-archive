import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

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

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));

        velocity.mergeTemplate("test.vm", context, writer);
        writer.flush();
        writer.close();
    }
}
