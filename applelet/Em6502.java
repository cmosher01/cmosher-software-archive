
/**
 * 6502 for Apple II Emulator by Steven Hugg (hugg@pobox.com) Adapted from code
 * (C) 1989 Ben Koning [556498717 408/738-1763 ben@apple.com]
 */

public abstract class Em6502
{

/*
 * 6502 Globals:
 */

   public int A;
   public int X;
   public int Y;
   public int P;  // flags
   public int S;  // stack pointer
   public int/* ADDR */ PPC; // program counter
   public int clock;

   public static final int FLAG_C = 1;
   public static final int FLAG_Z = 2;
   public static final int FLAG_I = 4;
   public static final int FLAG_D = 8;
   public static final int FLAG_B = 16;
   public static final int FLAG_V = 64;
   public static final int FLAG_N = 128;

   public byte[] mem;

   public int[] profile = new int[256];


/** Reads a byte of memory at a given address (0x0000-0xffff) */
   public abstract int readMemory(int address);

/**
 * Reads a byte of memory from the top 0x200 bytes of memory Must have same
 * functionality as readMemory()
 */
   public abstract int readMemory512(int address);

/** Writes a byte of memory to a given address (0x0000-0xffff) */
   public abstract void writeMemory(int address, int value);

/**
 * Writes a byte of memory to the top 0x200 bytes of memory Must have same
 * functionality as writeMemory()
 */
   public abstract void writeMemory512(int address, int value);

/** Increments the clock by a given number of cycles -- can be overriden */
   final void tick(int cycles)
   {
      clock += cycles;
   }

/**
 * Resets the machine. Loads the PPC from 0xFFFC and 0xFFFD, zeroes all the
 * registers, sets the stack pointer to 0xFF.
 */
   public void reset()
   {
      /* Setup registers: */
      A = X = Y = 0;
      P = 0x30;
      S = 0xff;

      /* Set PPC to pointer at $FFFC: */
      PPC = readMemory (0xfffc) + (readMemory (0xfffd) << 8);

      /* Should execute instructions forever after this */
   }

   public void clearProfile()
   {
      for (int i=0; i<256; i++)
         profile[i] = 0;
   }

   private final void hangup()
   {
      // CPU is hung up!
      PPC = (PPC - 1) & 0xffff;  // just run this instruction forever
   }

   private final void _incppc_()
   {
      PPC = (PPC + 1) & 0xffff;
   }

/* This internal routine pushes a byte onto the stack */
   private final void _push_ (int/* BYTE */ b)
   {
      mem[0x100 + S] = (byte) b;
      S = (S-1) & 0xff;
   }

/* This internal routine pulls a byte from the stack */
   private final int/* BYTE */ _pull_ ()
   {
      S = (S+1) & 0xff;
      return mem[0x100 + S] & 0xff;
   }

/* This internal routine fetches an immediate operand value */
   private final int/* BYTE */ _eaimm_ ()
   {
      int i = readMemory (PPC);
      _incppc_();
      return i;
   }

/* This internal routine fetches a zero-page operand address */
   private final int/* BYTE */ _eazp_ ()
   {
      int a = readMemory (PPC);
      _incppc_();

      return a;
   }

/* This internal routine fetches a zpage,X operand address */
   private final int/* BYTE */ _eazpx_ ()
   {
      int a = readMemory (PPC);
      _incppc_();

      return (a + X) & 0xff;
   }

/* This internal routine fetches a zpage,Y operand address */
   private final int/* BYTE */ _eazpy_ ()
   {
      int a = readMemory (PPC);
      _incppc_();

      return (a + Y) & 0xff;
   }

/* This internal routine fetches an absolute operand address */
   private final int/* ADDR */ _eaabs_ ()
   {
      int/* BYTE */ lo, hi;

      lo = readMemory (PPC);
      _incppc_();
      hi = readMemory (PPC);
      _incppc_();

      return lo + (hi << 8);
   }

/* This internal routine fetches an indirect absolute operand address */
   private final int/* ADDR */ _eaabsind_ ()
   {
      int a, lo, hi;

      a = _eaabs_();
      lo = readMemory (a);
      // ** simulates bug in indirect mode
      hi = readMemory ( ((a+1) & 0xff) | (a & 0xff00) );

      return lo + (hi << 8);
   }

/* This internal routine fetches an absolute,X operand address */
   private final int/* ADDR */ _eaabsx_ ()
   {
      int a;

      a = _eaabs_() + X;

      return (a & 0xffff); /* Not entirely correct */
   }

/* This internal routine fetches an absolute,Y operand address */
   private final int/* ADDR */ _eaabsy_ ()
   {
      int a;

      a = _eaabs_() + Y;

      return (a & 0xffff); /* Not entirely correct */
   }

/* This internal routine fetches a (zpage,X) operand address */
   private final int/* ADDR */ _eaindx_ ()
   {
      int a, lo, hi;

      a = _eazpx_();
      lo = readMemory (a);
// hi = readMemory ((a+1) & 0xff);
      hi = readMemory (a+1);

      return lo + (hi << 8);
   }

/* This internal routine fetches a (zpage),Y operand address */
   private final int/* ADDR */ _eaindy_ ()
   {
      int a, lo, hi;

      a = _eazp_();
      lo = readMemory (a);
// hi = readMemory ((a+1) & 0xff);
      hi = readMemory (a+1);

      return ( (Y + lo + (hi << 8)) & 0xffff ); /* Not entirely correct */
   }

/* Macros to set the P flags: */
   private final void _setN_(boolean b){ if (b) P |= 0x80;
      else P &= ~0x80;}
   private final void _setV_(boolean b){ if (b) P |= 0x40;
      else P &= ~0x40;}
   private final void _setB_(boolean b){ if (b) P |= 0x10;
      else P &= ~0x10;}
//   private final void _setD_(boolean b){ if (b) P |= 0x08;
//      else P &= ~0x08;}
//   private final void _setI_(boolean b){ if (b) P |= 0x04;
//      else P &= ~0x04;}
   private final void _setZ_(boolean b){ if (b) P |= 0x02;
      else P &= ~0x02;}
   private final void _setC_(boolean b){ if (b) P |= 0x01;
      else P &= ~0x01;}
//   private final void _setC_(int b){ if (b != 0) P |= 0x01;
//      else P &= ~0x01;}
// sets the C flag to the 1st bit of the parameter.
   private final void _setCvalue_(int b){ P = (P & ~FLAG_C) | (b & FLAG_C);}

   private final void _setNZ_(int x) {
      /*
		 * _setN_ (x >= 0x80); _setZ_ (x == 0);
		 */
      // fast way
      P = (P & ~0x82) | (x & FLAG_N);
      if (x == 0) P |= FLAG_Z;
   }

   /* Macros to read the P flags: */
   private final boolean _getN_() { return ((P & 0x80) > 0);}
   private final boolean _getV_() { return ((P & 0x40) > 0);}
//   private final boolean _getB_() { return ((P & 0x10) > 0);}
//   private final boolean _getD_() { return ((P & 0x08) > 0);}
//   private final boolean _getI_() { return ((P & 0x04) > 0);}
   private final boolean _getZ_() { return ((P & 0x02) > 0);}
   private final boolean _getC_() { return ((P & 0x01) > 0);}
   private final int _getCvalue_() { return (P & 0x01);}
//   private final int _getNotCvalue_() { return ((P ^ 0x01) & 0x01);}

   private final void _branch_ (boolean condition, int amt)
   {
      if (condition)
      {
         PPC = (PPC + (amt >= 0x80 ? amt-0x100 : amt)) & 0xffff;
      }
   }

   private final void _adc_ (int d)
   {
      int sum;
// sum = (byte)A + (byte)d + _getCvalue_();
// if ((sum>0x7f) || (sum<-0x80)) P |= 0x40; else P &= 0xbf;
      sum = A + d + _getCvalue_();
      _setV_ ( (((A ^ d) & 0x80) != 0) && (sum > 0xff) );
      if ((P & 0x08) != 0)
      {
         P &= 0xfe;
         if ((sum & 0x0f)>0x09)
            sum += 0x06;
         if ((sum & 0xf0)>0x90)
         {
            sum += 0x60;
            P |= 0x01;
         }
         A = sum & 0xff;
      } else
      {
         _setC_ (sum > 0xff);
         _setNZ_ (A = (sum & 0xff));
      }
   }

   private final void _sbc_ (int d)
   {
      int sum;
      d ^= 0xff;
// sum = (byte)A + (byte)d + _getCvalue_();
// if ((sum>0x7f) || (sum<-0x80)) P |= 0x40; else P &= 0xbf;
      sum = A + d + _getCvalue_();
      _setV_ ( (((A ^ d) & 0x80) != 0) && (sum > 0xff) );
      if ((P & 0x08) != 0)
      {
         sum -= 0x66;
         P &= 0xfe;
         if ((sum & 0x0f)>0x09)
            sum += 0x06;
         if ((sum & 0xf0)>0x90)
         {
            sum += 0x60;
            P |= 0x01;
         }
         A = sum & 0xff;
      } else
      {
         _setC_ (sum > 0xff);
         _setNZ_ (A = (sum & 0xff));
      }
   }

   private final void _bit_ (int d)
   {
      if ((d & A) != 0) P &= 0xfd;
      else P |= 0x02;
      P = (P & 0x3f) | (d & 0xc0);
   }

   private final void _cmp_ (int a, int d)
   {
      _setC_ ( (d = (a - d)) >= 0);
      _setNZ_ (d &= 0xff);
   }

   private final void _aslora_ (int a)
   {
      int value = readMemory(a);
      P = (P & 0xfe) | ((value >> 7) & 0x01);
      value = value << 1;
      writeMemory(a, value);
      value = readMemory(a);
      A |= value;
      _setNZ_(A);
   }

   private final void _roland_ (int a)
   {
      int saveflags = (P & 0x01);
      int value = readMemory(a);
      P = (P & 0xfe) | ((value >> 7) & 0x01);
      value = value << 1;
      value |= saveflags;
      writeMemory(a,value);
      A &= value;
      _setNZ_(A);
   }

   private final void _lsreor_ (int a)
   {
      int value = readMemory(a);
      P = (P & 0xfe) | (value & 0x01);
      value = value >> 1;
      writeMemory(a, value);
      A ^= value;
      _setNZ_(A);
   }

   private final void _roradc_ (int a)
   {
      int saveflags=(P & 0x01);
      int value = readMemory(a);
      P= (P & 0xfe) | (value & 0x01);
      value = value >> 1;
      if (saveflags != 0) value |= 0x80;
      writeMemory(a,value);
      saveflags=(P & 0x01);
      int sum = A + value + saveflags;
      if ((sum&0x80)!=0) P |= 0x40;
      else P &= 0xbf;
      sum = A + value + saveflags;
      if (sum>0xff) P |= 0x01;
      else P &= 0xfe;
      A = sum & 0xff;
      if ((P & 0x08) != 0)
      {
         P &= 0xfe;
         if ((A & 0x0f)>0x09)
            A += 0x06;
         if ((A & 0xf0)>0x90)
         {
            A += 0x60;
            P |= 0x01;
         }
      } else
      {
         _setNZ_(A);
      }
   }

//   private final void assert(boolean b, String s)
//   {
//      if (!b)
//      {
//         System.out.println("**Assertion failed, " + s);
//         for (int i=-8; i<8; i++)
//         {
//            System.out.print(
//                            Integer.toString(readMemory(PPC+i), 16) + " ");
//         }
//         System.out.println();
//      }
//   }


/* This routine executes a single instruction. */
   public final void executeInstruction ()
   {
      int opcode;      /* Scratch: Opcode fetched */
      int d;           /* Scratch: Data byte fetched */
      int lo;          /* Scratch: Lo8 for building ptr */
      int hi;          /* Scratch: Hi8 for building ptr */
      int al;          /* Scratch: Accumulator lo nibble */

      opcode = readMemory (PPC);
      
      // use when profiling
      //
      // profile[opcode]++;

      PPC = (PPC + 1) & 0xffff;

      switch (opcode)
      {
         case 0x00:     /* BRK */
            lo = (PPC+1);
            _push_ ((lo >> 8) & 0xff);
            _push_ (lo & 0xff);
            _push_ (P);
            _setB_ (true);
            lo = readMemory (0xfffe);
            hi = readMemory (0xffff);
            PPC = lo + (hi << 8);
            break;

         case 0x01:    /* ORA (aa,X) */
            _setNZ_( A |= readMemory (_eaindx_()) );
            break;

         case 0x05:    /* ORA aa */
            _setNZ_( A |= (mem[_eazp_()] & 0xff) );
            break;

         case 0x06:    /* ASL aa */
            hi = _eazp_();
            d = mem[hi] & 0xff;
            _setC_ (d >= 0x80);
            _setNZ_( d = (d << 1) & 0xff );
            mem[hi] = (byte) d;
            break;

         case 0x08:    /* PHP */
            _push_ (P);
            break;

         case 0x09:    /* ORA #dd */
            _setNZ_( A |= _eaimm_() );
            break;

         case 0x0a:    /* ASL A */
            _setC_ (A >= 0x80);
            _setNZ_( A = (A << 1) & 0xff );
            break;

         case 0x0d:    /* ORA aaaa */
            _setNZ_( A |= readMemory (_eaabs_()) );
            break;

         case 0x0e:    /* ASL aaaa */
            hi = _eaabs_();
            d = readMemory (hi);
            _setC_ (d >= 0x80);
            _setNZ_( d = (d << 1) & 0xff );
            writeMemory (hi,d);
            break;

         case 0x02:
            hangup();
            break;

         case 0x03:
            _aslora_( _eaindx_() );
            break;

         case 0x04:
            PPC++;
            break;

         case 0x07:
            _aslora_( _eazp_() );
            break;

         case 0x0c:
            PPC+=2;
            break;

         case 0x0f:
            _aslora_( _eaabs_() );
            break;

         case 0x10:    /* BPL rr */
            _branch_( !_getN_(), _eaimm_() );
            break;

         case 0x18:    /* CLC */
            // _setC_ (false);
            P &= ~FLAG_C;
            break;

         case 0x11:    /* ORA (aa),Y */
            _setNZ_( A |= readMemory (_eaindy_()) );
            break;

         case 0x19:    /* ORA aaaa,Y */
            _setNZ_( A |= readMemory (_eaabsy_()) );
            break;

         case 0x15:    /* ORA aa,X */
            _setNZ_( A |= (mem[_eazpx_()] & 0xff) );
            break;

         case 0x16:    /* ASL aa,X */
            hi = _eazpx_();
            d = mem[hi] & 0xff;
            _setC_ (d >= 0x80);
            _setNZ_( d = (d << 1) & 0xff );
            mem[hi] = (byte) d;
            break;

         case 0x1d:    /* ORA aaaa,X */
            _setNZ_( A |= readMemory (_eaabsx_()) );
            break;

         case 0x1e:    /* ASL aaaa,X */
            hi = _eaabsx_();
            d = readMemory (hi);
            _setC_ (d >= 0x80);
            _setNZ_( d = (d << 1) & 0xff );
            writeMemory (hi,d);
            break;

         case 0x12:
            hangup();
            break;

         case 0x13:
            _aslora_( _eaindy_() );
            break;

         case 0x14:
            PPC++;
            break;

         case 0x17:
            _aslora_( _eazpx_() );
            break;

         case 0x1b:
            _aslora_( _eaabsy_() );
            break;

         case 0x1c:
            PPC+=2;
            break;

         case 0x1f:
            _aslora_( _eaabsx_() );
            break;

         case 0x2a:    /* ROL A */
            al = _getCvalue_();
            _setC_ (A >= 0x80);
            _setNZ_( A = ((A << 1) & 0xff) | al );
            break;

         case 0x2c:    /* BIT aaaa */
            _bit_( readMemory (_eaabs_ ()) );
            break;

         case 0x20:    /* JSR aaaa */
            /* Push address of 3rd byte of jsr: */
            lo = (PPC+1);
            _push_ ((lo >> 8) & 0xff);
            _push_ (lo & 0xff);
            PPC = _eaabs_();
            break;

         case 0x21:    /* AND (aa,X) */
            _setNZ_( A &= readMemory (_eaindx_ ()) );
            break;

         case 0x24:    /* BIT aa */
            _bit_( mem[_eazp_()] );
            break;

         case 0x25:    /* AND aa */
            _setNZ_( A &= (mem[_eazp_()] & 0xff) );
            break;

         case 0x26:    /* ROL aa */
            hi = _eazp_();
            d = mem[hi] & 0xff;
            al = _getCvalue_();
            _setC_ (d >= 0x80);
            _setNZ_( d = ((d << 1) & 0xff) | al );
            mem[hi] = (byte) d;
            break;

         case 0x28:    /* PLP */
            P = _pull_() | 0x20;
            break;

         case 0x29:    /* AND #dd */
            _setNZ_( A &= _eaimm_ () );
            break;

         case 0x2d:    /* AND aaaa */
            _setNZ_( A &= readMemory (_eaabs_ ()) );
            break;

         case 0x2e:    /* ROL aaaa */
            hi = _eaabs_();
            d = readMemory (hi);
            al = _getCvalue_();
            _setC_ (d >= 0x80);
            _setNZ_( d = ((d << 1) & 0xff) | al );
            writeMemory (hi,d);
            break;

         case 0x22:
            hangup();
            break;

         case 0x23:
            _roland_( _eaindx_() );
            break;

         case 0x27:
            _roland_( _eazp_() );
            break;

         case 0x2f:
            _roland_( _eaabs_() );
            break;

         case 0x30:    /* BMI rr */
            _branch_( _getN_(), _eaimm_() );
            break;

         case 0x31:    /* AND (aa),Y */
            _setNZ_( A &= readMemory (_eaindy_ ()) );
            break;

         case 0x35:    /* AND aa,X */
            _setNZ_( A &= (mem[_eazpx_()] & 0xff) );
            break;

         case 0x36:    /* ROL aa,X */
            hi = _eazpx_();
            d = mem[hi] & 0xff;
            al = _getCvalue_();
            _setC_ (d >= 0x80);
            _setNZ_( d = ((d << 1) & 0xff) | al );
            mem[hi] = (byte) d;
            break;

         case 0x38:     /* SEC */
            // _setC_ (true);
            P |= FLAG_C;
            break;

         case 0x39:    /* AND aaaa,Y */
            _setNZ_( A &= readMemory (_eaabsy_ ()) );
            break;

         case 0x3d:    /* AND aaaa,X */
            _setNZ_( A &= readMemory (_eaabsx_ ()) );
            break;

         case 0x3e:    /* ROL aaaa,X */
            hi = _eaabsx_();
            d = readMemory (hi);
            al = _getCvalue_();
            _setC_ (d >= 0x80);
            _setNZ_( d = ((d << 1) & 0xff) | al );
            writeMemory (hi,d);
            break;

         case 0x32:
            hangup();
            break;

         case 0x33:
            _roland_( _eaindy_() );
            break;

         case 0x34:
            PPC++;
            break;

         case 0x37:
            _roland_( _eazpx_() );
            break;

         case 0x3b:
            _roland_( _eaabsy_() );
            break;

         case 0x3c:
            PPC+=2;
            break;

         case 0x3f:
            _roland_( _eaabsx_() );
            break;

         case 0x40:    /* RTI */
            P = _pull_() | 0x20;
            lo = _pull_();
            hi = _pull_();
            PPC = lo + (hi << 8);
            break;

         case 0x41:    /* EOR (aa,X) */
            d = readMemory (_eaindx_());
            A ^= d;
            _setNZ_ (A);
            break;

         case 0x45:    /* EOR aa */
            d = mem[_eazp_()] & 0xff;
            A ^= d;
            _setNZ_ (A);
            break;

         case 0x46:    /* LSR aa */
            hi = _eazp_();
            d = mem[hi] & 0xff;
            _setCvalue_ (d);
            d = d >> 1;
            _setN_ (false);
            _setZ_ (d == 0);
            mem[hi] = (byte) d;
            break;

         case 0x48:    /* PHA */
            _push_ (A);
            break;

         case 0x49:    /* EOR #dd */
            d = _eaimm_();
            A ^= d;   A &= 0xff;
            _setNZ_ (A);
            break;

         case 0x4a:    /* LSR A */
            _setCvalue_ (A);
            A = A >> 1;
            _setN_ (false);
            _setZ_ (A == 0);
            break;

         case 0x4c:    /* JMP aaaa */
            PPC = _eaabs_();
            break;

         case 0x4d:    /* EOR aaaa */
            d = readMemory (_eaabs_());
            A ^= d;
            _setNZ_ (A);
            break;

         case 0x4e:    /* LSR aaaa */
            hi = _eaabs_();
            d = readMemory (hi);
            _setCvalue_ (d);
            d = d >> 1;
            _setN_ (false);
            _setZ_ (d == 0);
            writeMemory (hi,d);
            break;

         case 0x51:    /* EOR (aa),Y */
            d = readMemory (_eaindy_());
            A ^= d;
            _setNZ_ (A);
            break;

         case 0x55:    /* EOR aa,X */
            d = mem[_eazpx_()] & 0xff;
            A ^= d;
            _setNZ_ (A);
            break;

         case 0x56:    /* LSR aa,X */
            hi = _eazpx_();
            d = mem[hi] & 0xff;
            _setCvalue_ (d);
            d = d >> 1;
            _setN_ (false);
            _setZ_ (d == 0);
            mem[hi] = (byte) d;
            break;

         case 0x50:    /* BVC rr */
            _branch_( !_getV_(), _eaimm_() );
            break;

         case 0x58:    /* CLI */
            // _setI_ (false);
            P &= ~FLAG_I;
            break;

         case 0x59:    /* EOR aaaa,Y */
            d = readMemory (_eaabsy_());
            A ^= d;
            _setNZ_ (A);
            break;

         case 0x5d:    /* EOR aaaa,X */
            d = readMemory (_eaabsx_());
            A ^= d;
            _setNZ_ (A);
            break;

         case 0x5e:    /* LSR aaaa,X */
            hi = _eaabsx_();
            d = readMemory (hi);
            _setCvalue_ (d);
            d = d >> 1;
            _setN_ (false);
            _setZ_ (d == 0);
            writeMemory (hi,d);
            break;

         case 0x6e:    /* ROR aaaa */
            hi = _eaabs_();
            d = readMemory (hi);
            al = _getCvalue_();
            _setCvalue_ (d);
            _setNZ_( d = (d >> 1) | (al << 7) );
            writeMemory (hi,d);
            break;

         case 0x60:    /* RTS */
            lo = _pull_();
            hi = _pull_();
            PPC = 1 + lo + (hi << 8);
            break;

         case 0x61:    /* ADC (aa,X) */
            _adc_( readMemory (_eaindx_ ()) );
            break;

         case 0x65:    /* ADC aa */
            _adc_( mem[_eazp_()] & 0xff );
            break;

         case 0x66:    /* ROR aa */
            hi = _eazp_();
            d = mem[hi] & 0xff;
            al = _getCvalue_();
            _setCvalue_ (d);
            _setNZ_( d = (d >> 1) | (al << 7) );
            mem[hi] = (byte) d;
            break;

         case 0x68:    /* PLA */
            _setNZ_( A = _pull_() );
            break;

         case 0x69:    /* ADC #dd */
            _adc_( _eaimm_() );
            break;

         case 0x6a:    /* ROR A */
            al = _getCvalue_();
            _setCvalue_ (A);
            _setNZ_( A = (A >> 1) | (al << 7) );
            break;

         case 0x6c:    /* JMP (aaaa) */
            PPC = _eaabsind_();
            break;

         case 0x6d:    /* ADC aaaa */
            _adc_( readMemory (_eaabs_ ()) );
            break;

         case 0x70:    /* BVS rr */
            _branch_( _getV_(), _eaimm_() );
            break;

         case 0x71:    /* ADC (aa),Y */
            _adc_( readMemory (_eaindy_ ()) );
            break;

         case 0x75:    /* ADC aa,X */
            _adc_( mem[_eazpx_()] & 0xff );
            break;

         case 0x76:    /* ROR aa,X */
            hi = _eazpx_();
            d = mem[hi] & 0xff;
            al = _getCvalue_();
            _setCvalue_ (d);
            _setNZ_( d = (d >> 1) | (al << 7) );
            mem[hi] = (byte) d;
            break;

         case 0x78:     /* SEI */
            // _setI_ (true);
            P |= FLAG_I;
            break;

         case 0x79:    /* ADC aaaa,Y */
            _adc_( readMemory (_eaabsy_ ()) );
            break;

         case 0x7d:    /* ADC aaaa,X */
            _adc_( readMemory (_eaabsx_ ()) );
            break;

         case 0x7e:    /* ROR aaaa,X */
            hi = _eaabsx_();
            d = readMemory (hi);
            al = _getCvalue_();
            _setCvalue_ (d);
            _setNZ_( d = (d >> 1) | (al << 7) );
            writeMemory (hi,d);
            break;

         case 0x88:    /* DEY */
            _setNZ_ ( Y = (Y-1) & 0xff );
            break;

         case 0x8d:     /* STA aaaa */
            writeMemory (_eaabs_(),A);
            break;

         case 0x8c:     /* STY aaaa */
            writeMemory (_eaabs_(),Y);
            break;

         case 0x8e:     /* STX aaaa */
            writeMemory (_eaabs_(),X);
            break;

         case 0x81:     /* STA (aa,X) */
            writeMemory (_eaindx_(),A);
            break;

         case 0x84:     /* STY aa */
            mem[_eazp_()] = (byte) Y;
            break;

         case 0x85:     /* STA aa */
            mem[_eazp_()] = (byte) A;
            break;

         case 0x86:     /* STX aa */
            mem[_eazp_()] = (byte) X;
            break;

         case 0x8a:     /* TXA */
            _setNZ_( A = X );
            break;

         case 0x90:    /* BCC rr */
            _branch_( !_getC_(), _eaimm_() );
            break;

         case 0x91:     /* STA (aa),Y */
            writeMemory (_eaindy_(),A);
            break;

         case 0x94:     /* STY aa,X */
            mem[_eazpx_()] = (byte) Y;
            break;

         case 0x95:     /* STA aa,X */
            mem[_eazpx_()] = (byte) A;
            break;

         case 0x96:     /* STX aa,Y */
            mem[_eazpy_()] = (byte) X;
            break;

         case 0x98:     /* TYA */
            _setNZ_( A = Y );
            break;

         case 0x99:     /* STA aaaa,Y */
            writeMemory (_eaabsy_(),A);
            break;

         case 0x9a:     /* TXS */
            S = X;
            break;

         case 0x9d:     /* STA aaaa,X */
            writeMemory (_eaabsx_(),A);
            break;

         case 0xad:    /* LDA aaaa */
            _setNZ_( A = readMemory (_eaabs_()) );
            break;

         case 0xa0:    /* LDY #dd */
            _setNZ_ ( Y = _eaimm_() );
            break;

         case 0xa1:    /* LDA (aa,X) */
            _setNZ_ ( A = readMemory (_eaindx_()) );
            break;

         case 0xa2:    /* LDX #dd */
            _setNZ_ ( X = _eaimm_() );
            break;

         case 0xa4:    /* LDY aa */
            _setNZ_ ( Y = mem[_eazp_()] & 0xff );
            break;

         case 0xa5:    /* LDA aa */
            _setNZ_( A = mem[_eazp_()] & 0xff );
            break;

         case 0xa6:    /* LDX aa */
            _setNZ_ ( X = mem[_eazp_()] & 0xff );
            break;

         case 0xa8:     /* TAY */
            _setNZ_( Y = A );
            break;

         case 0xa9:    /* LDA #dd */
            _setNZ_( A = _eaimm_() );
            break;

         case 0xaa:     /* TAX */
            _setNZ_( X = A );
            break;

         case 0xac:    /* LDY aaaa */
            _setNZ_ ( Y = readMemory (_eaabs_()) );
            break;

         case 0xae:    /* LDX aaaa */
            _setNZ_ ( X = readMemory (_eaabs_()) );
            break;

         case 0xbd:    /* LDA aaaa,X */
            _setNZ_( A = readMemory (_eaabsx_()) );
            break;

         case 0xb0:    /* BCS rr */
            _branch_( _getC_(), _eaimm_() );
            break;

         case 0xb1:    /* LDA (aa),Y */
            _setNZ_ ( A = readMemory (_eaindy_()) );
            break;

         case 0xb4:    /* LDY aa,X */
            _setNZ_ ( Y = mem[_eazpx_()] & 0xff );
            break;

         case 0xb5:    /* LDA aa,X */
            _setNZ_( A = mem[_eazpx_()] & 0xff );
            break;

         case 0xb6:    /* LDX aa,Y */
            _setNZ_ ( X = mem[_eazpy_()] & 0xff );
            break;

         case 0xb8:    /* CLV */
            // _setV_ (false);
            P &= ~FLAG_V;
            break;

         case 0xb9:    /* LDA aaaa,Y */
            _setNZ_( A = readMemory (_eaabsy_()) );
            break;

         case 0xba:     /* TSX */
            _setNZ_( X = S );
            break;

         case 0xbc:    /* LDY aaaa,X */
            _setNZ_ ( Y = readMemory (_eaabsx_()) );
            break;

         case 0xbe:    /* LDX aaaa,Y */
            _setNZ_ ( X = readMemory (_eaabsy_()) );
            break;

         case 0xca:    /* DEX */
            _setNZ_ ( X = (X-1) & 0xff );
            break;

         case 0xc8:    /* INY */
            _setNZ_ ( Y = (Y + 1) & 0xff );
            break;

         case 0xc9:    /* CMP #dd */
            _cmp_( A, _eaimm_() );
            break;

         case 0xc0:    /* CPY #dd */
            _cmp_( Y, _eaimm_() );
            break;

         case 0xc1:    /* CMP (aa,X) */
            _cmp_( A, readMemory (_eaindx_()) );
            break;

         case 0xc4:    /* CPY aa */
            _cmp_( Y, mem[_eazp_()] & 0xff );
            break;

         case 0xc5:    /* CMP aa */
            _cmp_( A, mem[_eazp_()] & 0xff);
            break;

         case 0xc6:    /* DEC aa */
            hi = _eazp_();
            _setNZ_( d = (mem[hi] - 1) & 0xff );
            mem[hi] = (byte) d;
            break;

         case 0xcc:    /* CPY aaaa */
            _cmp_( Y, readMemory (_eaabs_()) );
            break;

         case 0xcd:    /* CMP aaaa */
            _cmp_( A, readMemory (_eaabs_()) );
            break;

         case 0xce:    /* DEC aaaa */
            hi = _eaabs_();
            _setNZ_( d = (readMemory (hi) - 1) & 0xff );
            writeMemory (hi,d);
            break;

         case 0xd0:    /* BNE rr */
            _branch_( !_getZ_(), _eaimm_() );
            break;

         case 0xd1:    /* CMP (aa),y */
            _cmp_( A, readMemory (_eaindy_()) );
            break;

         case 0xd5:    /* CMP aa,X */
            _cmp_( A, mem[_eazpx_()] & 0xff );
            break;

         case 0xd6:    /* DEC aa,X */
            hi = _eazpx_();
            _setNZ_( d = (mem[hi] - 1) & 0xff );
            mem[hi] = (byte) d;
            break;

         case 0xd8:    /* CLD */
            // _setD_ (false);
            P &= ~FLAG_D;
            break;

         case 0xd9:    /* CMP aaaa,Y */
            _cmp_( A, readMemory (_eaabsy_()) );
            break;

         case 0xdd:    /* CMP aaaa,X */
            _cmp_( A, readMemory (_eaabsx_()) );
            break;

         case 0xde:    /* DEC aaaa,X */
            hi = _eaabsx_();
            _setNZ_( d = (readMemory (hi) - 1) & 0xff );
            writeMemory (hi,d);
            break;

         case 0xe0:    /* CPX #dd */
            _cmp_( X, _eaimm_() );
            break;

         case 0xe1:    /* SBC (aa,X) */
            _sbc_( readMemory (_eaindx_ ()) );
            break;

         case 0xe4:    /* CPX aa */
            _cmp_( X, mem[_eazp_()] & 0xff );
            break;

         case 0xe5:    /* SBC aa */
            _sbc_( mem[_eazp_()] & 0xff );
            break;

         case 0xe6:    /* INC aa */
            hi = _eazp_();
            _setNZ_( d = (mem[hi] + 1) & 0xff );
            mem[hi] = (byte) d;
            break;

         case 0xe8:    /* INX */
            _setNZ_ ( X = (X + 1) & 0xff );
            break;

         case 0xe9:    /* SBC #dd */
            _sbc_( _eaimm_ () );
            break;

         case 0xea:    /* NOP */
            break;

         case 0xec:    /* CPX aaaa */
            _cmp_( X, readMemory (_eaabs_()) );
            break;

         case 0xed:    /* SBC aaaa */
            _sbc_( readMemory (_eaabs_ ()) );
            break;

         case 0xee:    /* INC aaaa */
            hi = _eaabs_();
            _setNZ_( d = (readMemory (hi) + 1) & 0xff );
            writeMemory (hi,d);
            break;

         case 0xf0:    /* BEQ rr */
            _branch_( _getZ_(), _eaimm_() );
            break;

         case 0xf1:    /* SBC (aa),Y */
            _sbc_( readMemory (_eaindy_ ()) );
            break;

         case 0xf5:    /* SBC aa,x */
            _sbc_( mem[_eazpx_ ()] & 0xff );
            break;

         case 0xf6:    /* INC aa,X */
            hi = _eazpx_();
            _setNZ_( d = (mem[hi] + 1) & 0xff );
            mem[hi] = (byte) d;
            break;

         case 0xf8:     /* SED */
            // _setD_ (true);
            P |= FLAG_D;
            break;

         case 0xf9:    /* SBC aaaa,Y */
            _sbc_( readMemory (_eaabsy_ ()) );
            break;

         case 0xfd:    /* SBC aaaa,X */
            _sbc_( readMemory (_eaabsx_ ()) );
            break;

         case 0xfe:    /* INC aaaa,X */
            hi = _eaabsx_();
            _setNZ_( d = (readMemory (hi) + 1) & 0xff );
            writeMemory (hi,d);
            break;
      }

      clock += opticks[opcode];
      /*
		 * if (!(A >= 0 && A <= 0xff)) assert(false, "A out of range, opcode " +
		 * opcode); assert(X >= 0 && X <= 0xff, "X out of range"); assert(Y >= 0 &&
		 * Y <= 0xff, "Y out of range"); assert(S >= 0 && S <= 0xff, "S out of
		 * range"); assert(P >= 0 && P <= 0xff, "P out of range"); assert(PPC >=
		 * 0 && PPC <= 0xffff, "PPC out of range");
		 */

   }


   private static int opticks[] = {
// 0 1 2 3 4 5 6 7 8 9 a b c d e f
      7, 6, 2, 8, 2, 3, 5, 5, 3, 3, 2, 3, 2, 4, 6, 6,
      2, 5, 2, 8, 2, 4, 6, 6, 2, 4, 2, 7, 2, 4, 7, 7,
      6, 6, 2, 8, 3, 3, 5, 5, 4, 3, 2, 3, 4, 4, 6, 6,
      2, 5, 2, 8, 2, 4, 6, 6, 2, 4, 2, 7, 2, 4, 7, 7,
      6, 6, 2, 8, 2, 3, 5, 5, 3, 3, 2, 2, 3, 4, 6, 6,
      2, 5, 2, 8, 2, 4, 6, 6, 2, 4, 2, 7, 2, 4, 7, 7,
      6, 6, 2, 8, 2, 3, 5, 5, 4, 3, 2, 2, 5, 4, 6, 6,
      2, 5, 2, 8, 2, 4, 6, 6, 2, 4, 2, 7, 2, 4, 7, 7,
      2, 6, 2, 6, 2, 2, 2, 3, 2, 2, 2, 2, 4, 4, 4, 4,
      2, 6, 2, 6, 4, 4, 4, 4, 2, 5, 2, 5, 5, 5, 5, 5,
      3, 6, 3, 6, 3, 3, 3, 3, 2, 3, 2, 2, 4, 4, 4, 4,
      2, 5, 2, 5, 4, 4, 4, 4, 2, 4, 2, 4, 4, 4, 4, 5,
      3, 6, 2, 8, 3, 3, 5, 5, 2, 3, 2, 2, 4, 4, 6, 6,
      2, 5, 2, 8, 2, 4, 6, 6, 2, 4, 2, 7, 2, 4, 7, 7,
      3, 6, 2, 8, 3, 3, 5, 5, 2, 3, 2, 3, 4, 4, 6, 6,
      2, 5, 2, 8, 2, 4, 6, 6, 2, 4, 2, 7, 2, 4, 7, 7
   };


   public final void executeInstructions (int num)
   {
      while (num >= 16)
      {
         executeInstruction();  executeInstruction();
         executeInstruction();  executeInstruction();
         executeInstruction();  executeInstruction();
         executeInstruction();  executeInstruction();
         executeInstruction();  executeInstruction();
         executeInstruction();  executeInstruction();
         executeInstruction();  executeInstruction();
         executeInstruction();  executeInstruction();
         num -= 16;
      }
      while (num > 0)
      {
         executeInstruction();
         num--;
      }
   }


} /* Em6502 */
