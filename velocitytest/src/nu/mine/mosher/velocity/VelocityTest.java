import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogSystem;

public class VelocityTest
{
    public static void main(String[] args) throws Throwable
    {
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,new NullLogSystem());

        velocity.init();


        Context context = new VelocityContext();
        context.put("x", "test")

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));

        velocity.mergeTemplate("test.vm", context, writer);

        writer.flush();
        writer.close();
    }
}
