import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

public class VelocityTest
{
    public static void main(String[] args) throws Throwable
    {
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM,new LogSystem()
        {
            public void init(RuntimeServices rs) throws Exception
            {
            }

            public void logVelocityMessage(int level, String message)
            {
                if (level >= LogSystem.WARN_ID)
                {
                    System.err.println(message);
                }
            }
        });

        velocity.init();


        Context context = new VelocityContext();
        List deps = getDependencies();
        context.put("deps",deps);
        context.put("main","com.surveysampling.example.Example");

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));

        velocity.mergeTemplate("build_template.vm", context, writer);

        writer.flush();
        writer.close();
    }
    public static List getDependencies()
    {
        List deps = new ArrayList();
        deps.add(new Dependency("jaxp","1-2-0",false));
        deps.add(new Dependency("SurveySampling","2-3",true));
        return deps;
    }
}
