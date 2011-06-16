package nu.mosher.mine.a2diskedit;

/*
=====================================================================

  HexTableCellEditor.java
  
  Created by Claude Duguay
  Copyright (c) 2001
  
=====================================================================
*/

import java.awt.*;
import javax.swing.*;

public class HexTableCellEditor
  extends DefaultCellEditor
{
  public HexTableCellEditor()
  {
    super(new JTextField());
    JTextField field = (JTextField)getComponent();
    field.setBorder(null);
    field.setHorizontalAlignment(JTextField.CENTER);
    field.setForeground(Color.white);
    field.setBackground(Color.red);
  }
}

