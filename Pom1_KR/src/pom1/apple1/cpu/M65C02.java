// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   M6502.java

package pom1.apple1.cpu;

import pom1.apple1.Memory;

// Referenced classes of package pom1.apple1:
//            Memory

public class M65C02 extends M6502
{
	public M65C02(Memory mem)
    {
    	super(mem);
    }
    
    public boolean executeOpcode()
    {
    	if (super.executeOpcode() == false)
    	{
        	valid = true;
	        int opcode = mem.read(--programCounter);	// step back and refetch
	        programCounter++;
	        switch(opcode)
	        {
	        case 0x04: // '\004'
	            Zero();
	            TSB();
	            break;
	            
	        case 0x0C: // '\f'
	            Abs();
	            TSB();
	            break;
	            
	        case 0x14: // '\004'
	            Zero();
	            TRB();
	            break;
	            
	        case 0x1C: // '\f'
	            Abs();
	            TRB();
	            break;
	
	        case 0x12: // '\r'
	            IndZero();
	            ORA();
	            break;
	
	        case 0x32: // '\r'
	            IndZero();
	            AND();
	            break;
	
	        case 0x52: // '\r'
	            IndZero();
	            EOR();
	            break;
	
	        case 0x72: // '\r'
	            IndZero();
	            ADC();
	            break;
	
	        case 0x92: // '\r'
	            IndZero();
	            STA();
	            break;
	
	        case 0xB2: // '\r'
	            IndZero();
	            LDA();
	            break;
	
	        case 0xD2: // '\r'
	            IndZero();
	            CMP();
	            break;
	
	        case 0xF2: // '\r'
	            IndZero();
	            SBC();
	            break;
	
	        case 0x7C: // '\016'
	            IndAbsX();
	            JMP();
	            break;
		        
	        case 0x80: // '\020'
	            Rel();
	            branch();
	            break;

	        case 0x89: // '\017'
	            Imm();
	            BIT();
	            break;

	        case 0x34: // '\017'
	            ZeroX();
	            BIT();
	            break;

	        case 0x3C: // '\017'
	            AbsX();
	            BIT();
	            break;
	        
	        case 0x64: // '\025'
	            Zero();
	            STZ();
	            break;
	        
	        case 0x9C: // '\025'
	            Abs();
	            STZ();
	            break;
	        
	        case 0x74: // '\025'
	            ZeroX();
	            STZ();
	            break;
	        
	        case 0x9E: // '\025'
	            AbsX();
	            STZ();
	            break;
		
	        case 0x1A: // '\030'
	            Imp();
	            INA();
	            break;
		
	        case 0x3A: // '\030'
	            Imp();
	            DEA();
	            break;
		
	        case 0x5A: // '\030'
	            Imp();
	            PHY();
	            break;
		
	        case 0x7A: // '\030'
	            Imp();
	            PLY();
	            break;
		
	        case 0xDA: // '\030'
	            Imp();
	            PHX();
	            break;
		
	        case 0xFA: // '\030'
	            Imp();
	            PLX();
	            break;
		
	        default: 
	        	handleNOPs(opcode);
	            break;
	        }
    	}
		return true;
    }

	// all other opcodes are NOPs, but they take different numbers of cycles and arguments    
    private void handleNOPs(int opcode) 
    {
    	int test = opcode & 0x03;
    	
    	if (test == 2)
    	{
    		programCounter++;
    	}
    	else if (test == 3)
    	{
    	}
    	else if (opcode == 0x44)
    	{
    		programCounter++;
    	}
    	else if (opcode == 0x5A)
    	{
    		programCounter++;
    		programCounter++;
    	}
    	else
    	{
    		programCounter++;
    		if ((opcode & 0x08) > 0)
        		programCounter++;
    	}
	}

    /* additional addressing modes for the 65C02 */
	protected void IndAbsX()
    {
        ptrL = xRegister + mem.read(programCounter++);
        ptrH = mem.read(programCounter++) << 8;
        op = mem.read(ptrH + ptrL);
        ptrL = ptrL + 1 & 0xff;
        op += mem.read(ptrH + ptrL) << 8;
    }
    
    protected void IndZero()
    {
        ptr = mem.read(programCounter++);
        op = mem.read(ptr);
        op += mem.read(ptr + 1 & 0xff) << 8;
    }
    
    /* additional 65C02 instructions */
    
    private void TSB()
    {
    	// Z = M & A
    	// M = M | A
        btmp = (byte)(mem.read(op) & 0xff);
        btmp &= accumulator;
        setStatusRegisterZ(btmp);
        btmp = (byte)(mem.read(op) & 0xff);
        btmp |= accumulator;
        mem.write(op, btmp & 0xff);
    }

    private void TRB()
    {
    	// Z = M & A
    	// M = M & ~A
        btmp = (byte)(mem.read(op) & 0xff);
        btmp &= accumulator;
        setStatusRegisterZ(btmp);
        btmp = (byte)(mem.read(op) & 0xff);
        btmp &= (~accumulator);
        mem.write(op, btmp & 0xff);
    }

    private void STZ()
    {
        mem.write(op, 0);
    }

    private void INA()
    {
        accumulator = (accumulator + 1) & 0xff;
        setStatusRegisterNZ((byte)accumulator);
    }

    private void DEA()
    {
        accumulator = (accumulator - 1) & 0xff;
        setStatusRegisterNZ((byte)accumulator);
    }

    private void PHX()
    {
        mem.write(256 + stackPointer, xRegister & 0xff);
        stackPointer = stackPointer - 1 & 0xff;
    }

    private void PHY()
    {
        mem.write(256 + stackPointer, yRegister & 0xff);
        stackPointer = stackPointer - 1 & 0xff;
    }

    private void PLX()
    {
        mem.read(stackPointer + 256);
        stackPointer = stackPointer + 1 & 0xff;
        xRegister = mem.read(stackPointer + 256) & 0xff;
        setStatusRegisterNZ((byte)xRegister);
    }

    private void PLY()
    {
        mem.read(stackPointer + 256);
        stackPointer = stackPointer + 1 & 0xff;
        yRegister = mem.read(stackPointer + 256) & 0xff;
        setStatusRegisterNZ((byte)yRegister);
    }
    
    // these all do nothing now
    protected void Unoff()
    {
    	valid = false;
    }

    protected void Unoff1()
    {
    	valid = false;
    }

    protected void Unoff2()
    {
    	valid = false;
    }

    protected void Unoff3()
    {
    	valid = false;
    }

    protected void Hang()
    {
    	valid = false;
    }

    protected void setStatusRegisterZ(byte val)
    {
        Z = val == 0;
    }
}
