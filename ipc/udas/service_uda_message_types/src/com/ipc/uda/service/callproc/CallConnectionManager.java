package com.ipc.uda.service.callproc;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.va.cti.ConnectionID;

public class CallConnectionManager
{
    private final ConcurrentMap<CtiConnection,ConferenceCall> mapConnectionIdToCall = new ConcurrentHashMap<CtiConnection,ConferenceCall>();

    public Optional<ConferenceCall> get(final ConnectionID connectionID)
    {
        final ConferenceCall callOrNull = this.mapConnectionIdToCall.get(new CtiConnection(connectionID));
        return new Optional<ConferenceCall>(callOrNull);
    }

    public void put(final CtiConnection connection, final ConferenceCall call)
    {
        this.mapConnectionIdToCall.put(connection,call);
    }

    public void remove(final CtiConnection connection)
    {
        this.mapConnectionIdToCall.remove(connection);
    }

    public void gc()
    {
        final Set<CtiConnection> connections = this.mapConnectionIdToCall.keySet();
        final Iterator<CtiConnection> iterConnections = connections.iterator();
        while (iterConnections.hasNext())
        {
            if (!isConnectionValid(iterConnections.next().getID()))
            {
                iterConnections.remove();
            }
        }
    }

    private boolean isConnectionValid(final ConnectionID id)
    {
        // TODO call CTI to determine if the connectionID is still valid
        return true;
    }
}
