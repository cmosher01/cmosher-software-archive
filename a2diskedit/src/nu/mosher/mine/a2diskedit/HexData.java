package nu.mosher.mine.a2diskedit;

/*
=====================================================================

  HexData.java
  
  Created by Claude Duguay
  Copyright (c) 2001
  
=====================================================================
*/

import java.io.*;

public interface HexData
{
  public int getRowCount();
  public int getColumnCount();
  public int getLastRowSize();
  
  public byte getByte(int row, int col);
  public void setByte(int row, int col, byte value);
  public byte[] getRow(int row);
  public byte[] image();
}
