package com.ipc.uda.types;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ipc.uda.service.util.Optional;
import com.ipc.va.dialog.Dialog;
import com.ipc.va.dialog.Participant;
import com.ipc.va.dialog.State;

public class SystemLineStatusTest
{
    @Test
    public void testNullDialog()
    {
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(null);
        assertTrue(!status.exists());
    }

    @Test
    public void testNullState()
    {
        final State state = null;
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(!status.exists());
    }

    @Test
    public void testStateWithNullValue()
    {
        final State state = new State();
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(!status.exists());
    }

    @Test
    public void testEmptyState()
    {
        final State state = new State();
        state.setValue("");
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(!status.exists());
    }

    @Test
    public void testTerminated()
    {
        testState(SystemLineStatus.IDLE,"terminated");
    }

    @Test
    public void testTrying()
    {
        testState(SystemLineStatus.BUSY,"trying");
    }

    @Test
    public void testEarly()
    {
        testState(SystemLineStatus.INCOMING,"early");
    }

    @Test
    public void testEarlyNonRecipient()
    {
        final State state = new State();
        state.setValue("early");
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        dialog.setDirection("sender");
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(!status.exists());
    }

    @Test
    public void testConfirmed()
    {
        testState(SystemLineStatus.BUSY,"confirmed");
    }

    @Test
    public void testConfirmedNotRendering()
    {
        testRendering(SystemLineStatus.HOLD,"sip.rendering","no");
    }

    @Test
    public void testConfirmedRendering()
    {
        testRendering(SystemLineStatus.BUSY,"sip.rendering","yes");
    }

    @Test
    public void testConfirmedPlusNotRendering()
    {
        testRendering(SystemLineStatus.HOLD,"+sip.rendering","no");
    }

    @Test
    public void testConfirmedPlusNRendering()
    {
        testRendering(SystemLineStatus.BUSY,"Nsip.rendering","no");
    }

    @Test
    public void testConfirmedPlusRenderingN()
    {
        testRendering(SystemLineStatus.BUSY,"sip.renderingN","no");
    }

    private void testState(final SystemLineStatus expectedStatus, final String stateName)
    {
        final State state = new State();
        state.setValue(stateName);
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        dialog.setDirection("recipient");
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(status.exists());
        assertEquals(expectedStatus,status.get());
    }

    private void testRendering(final SystemLineStatus expected, final String pname, final String pval)
    {
        final State state = new State();
        state.setValue("confirmed");
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        
        final Participant.Target.Param noRender = new Participant.Target.Param();
        noRender.setPname(pname);
        noRender.setPval(pval);
    
        final Participant.Target target = new Participant.Target();
        target.getParam().add(noRender);
    
        final Participant participant = new Participant();
        participant.setTarget(target);
    
        dialog.setLocal(participant);
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(status.exists());
        assertEquals(expected,status.get());
    }

    @Test
    public void testConfirmedPlusNullTarget()
    {
        final State state = new State();
        state.setValue("confirmed");
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        
        final Participant.Target target = null;
    
        final Participant participant = new Participant();
        participant.setTarget(target);
    
        dialog.setLocal(participant);
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(status.exists());
        assertEquals(SystemLineStatus.BUSY,status.get());
    }

    @Test
    public void testConfirmedPlusNullParams()
    {
        final State state = new State();
        state.setValue("confirmed");
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        
        final Participant.Target.Param noRender = null;
    
        final Participant.Target target = new Participant.Target();
        target.getParam().add(noRender);
    
        final Participant participant = new Participant();
        participant.setTarget(target);
    
        dialog.setLocal(participant);
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(status.exists());
        assertEquals(SystemLineStatus.BUSY,status.get());
    }


    @Test
    public void testConfirmedPlusNullPName()
    {
        final State state = new State();
        state.setValue("confirmed");
        final Dialog dialog = new Dialog();
        dialog.setState(state);
        
        final Participant.Target.Param noRender = new Participant.Target.Param();
        noRender.setPname(null);
        noRender.setPval(null);
    
        final Participant.Target target = new Participant.Target();
        target.getParam().add(noRender);
    
        final Participant participant = new Participant();
        participant.setTarget(target);
    
        dialog.setLocal(participant);
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);
        assertTrue(status.exists());
        assertEquals(SystemLineStatus.BUSY,status.get());
    }
}
