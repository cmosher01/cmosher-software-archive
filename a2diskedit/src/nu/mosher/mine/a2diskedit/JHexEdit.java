package nu.mosher.mine.a2diskedit;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class JHexEdit extends JPanel
{
	private HexData hexData = null;

   public JHexEdit(File file)
      throws IOException
   {
      this(new HexFile(file));
   }

   public JHexEdit(HexData data)
   {
   	  hexData = data;

      HexTableModel model = new HexTableModel(hexData);
      JTable table = new HexTable(model);
      setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      setLayout(new GridLayout());
      add(new JScrollPane(table));
//      setPreferredSize(new Dimension(table.getPreferredSize().width + 8, 400));
   }

   public byte[] image()
   {
   	  return hexData.image();
   }
}
