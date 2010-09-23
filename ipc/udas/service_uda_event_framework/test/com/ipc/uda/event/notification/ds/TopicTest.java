package com.ipc.uda.event.notification.ds;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.ipc.uda.event.notification.ds.NotificationListener.Action;

public class TopicTest
{
    @Test
    public void nominal()
    {
        final Topic topic = new Topic("a","b",1,Action.updated);
        assertEquals("a",topic.getSubsystem());
        assertEquals("b",topic.getEntityName());
        assertEquals(1,topic.getEntityID());
        assertEquals(Action.updated,topic.getAction());
    }

    @Test(expected=RuntimeException.class)
    public void nullAction()
    {
        new Topic("A","B",1,null);
    }

    @Test
    public void testToString()
    {
        final Topic topic = new Topic("a","b",1,Action.updated);
        assertEquals("a.b.1.updated",topic.toString());
    }

    @Test
    public void testValueOf()
    {
        final Topic topic1 = new Topic("a","b",1,Action.updated);
        final Topic topic2 = Topic.valueOf("a.b.1.updated");
        assertTrue(topic1.equals(topic2));
        assertTrue(topic2.equals(topic1));
    }

    @Test(expected=RuntimeException.class)
    public void badValue()
    {
        Topic.valueOf("a.b");
    }

    @Test
    public void useInSet()
    {
        final Topic topic1 = new Topic("a","b",1,Action.updated);
        final Topic topic2 = Topic.valueOf("a.b.1.updated");
        assertNotSame(topic1,topic2);
        final Set<Topic> set = new HashSet<Topic>();
        set.add(topic1);
        assertEquals(1,set.size());
        set.add(topic2);
        assertEquals(1,set.size());
    }
}
