package nu.mosher.mine.a2diskedit;

/*
=====================================================================

  HexFile.java
  
  Created by Claude Duguay
  Copyright (c) 2001
  
=====================================================================
*/

import java.io.*;

public class HexFile /*extends RandomAccessFile*/ implements HexData
{
  public static final int ROW_SIZE = 16;
	private byte[] mDiskBytes = null;
  
  protected int size;

  public HexFile(String filename) throws IOException
  {
    this(new File(filename));
  }
  
  public HexFile(File file) throws IOException
  {
    //super(file, "rw");
		if (file != null)
		{
			FileInputStream fin = null;
			try
			{
				fin = new FileInputStream(file);
			    size = fin.available();
				mDiskBytes = new byte[size];
				fin.read(mDiskBytes);
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (fin != null)
				{
					try
					{
						fin.close();
					}
					catch (Exception e)
					{
					}
				}
			}
		}
	}

  public int getRowCount()
  {
    int rows = size / ROW_SIZE;
    if (rows * ROW_SIZE < size)
    	rows++;
    return rows;
  }
  
  public int getColumnCount()
  {
    return ROW_SIZE;
  }
  
  public int getLastRowSize()
  {
    int max = (getRowCount() - 1) * ROW_SIZE;
    if ((size - max) == 0)
    	return ROW_SIZE;
    return size - max;
  }
  
  public byte getByte(int row, int col)
  {
//    try
//    {
//      seek(row * ROW_SIZE + col);
//      return (byte)read();
		return mDiskBytes[row * ROW_SIZE + col];
//    }
/*    catch (IOException e)
    {
      e.printStackTrace();
      return 0;
    }*/
  }

  public void setByte(int row, int col, byte value)
  {
//    try
//    {
		mDiskBytes[row * ROW_SIZE + col] = value;
//      seek(row * ROW_SIZE + col);
//      write(value);
//    }
/*    catch (IOException e)
    {
      e.printStackTrace();
    }*/
  }

  public byte[] getRow(int row)
  {
    int rowSize = ROW_SIZE;
    if (row == getRowCount() - 1)
      rowSize = getLastRowSize();
    byte[] data = new byte[rowSize];
//    try
//    {
    	System.arraycopy(mDiskBytes,row*ROW_SIZE,data,0,rowSize);
    	for (int i = 0; i < data.length; i++)
		{
			data[i] = (byte)(data[i] & 0x7f);
			if ((data[i] & 0xe0) == 0)
				data[i] = (byte)(data[i] | 0x40);
		}
//      seek(row * ROW_SIZE);
//      read(data);
      return data;
 //   }
/*    catch (IOException e)
    {
      e.printStackTrace();
      return data;
    }*/
  }

	public byte[] image()
	{
		return mDiskBytes;
	}
}
