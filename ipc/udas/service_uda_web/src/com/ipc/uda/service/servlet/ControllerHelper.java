package com.ipc.uda.service.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.Returnable;
import com.ipc.uda.types.MessageSerializer;
import com.ipc.uda.types.MessageSerializer.InvalidXML;

class ControllerHelper
{
    private final MessageSerializer serializer = new MessageSerializer();

    protected ControllerHelper()
    {
        // protected access
    }

    /**
     * Deserializes the query or command (in XML) from the given
     * {@link BufferedReader} <code>xml</code>, and
     * creates the corresponding {@link Executable} instance. The
     * {@link Executable} instance's variables will be populated with
     * the argument values read from the XML.
     * @param xml contains the XML describing the query or command
     * @return the {@link Executable} that can be used to execute the command or query
     * @throws BadRequest if anything goes wrong (primarily invalid XML).
     */
    protected ExecutableWithContext deserialize(final BufferedReader xml) throws InvalidXML
    {
        return this.serializer.deserialize(xml);
    }

    /**
     * Serializes the given {@link Returnable} <code>result</code> into XML and writes it
     * to the given {@link BufferedWriter} <code>out</code>.
     * @param result the results of a query to be serialized
     * @param out the writer to write the serialized results to
     */
    protected void serialize(final Returnable result, final BufferedWriter out)
    {
        this.serializer.serialize(result,out);
    }
}
