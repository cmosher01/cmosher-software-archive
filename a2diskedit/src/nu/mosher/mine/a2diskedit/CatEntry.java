package nu.mosher.mine.a2diskedit;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class CatEntry implements TreeNode
{
	private final JEditorPane viewRight = new JEditorPane();
	private DefaultMutableTreeNode node = null;
	private String sName;
	private static final Icon mIcon = new ImageIcon("d:\\temp\\a2file.gif");
	private byte[] raw;
	private String hexRaw;

	public CatEntry(byte[] rb)
	{
		raw = new byte[rb.length];
		System.arraycopy(rb,0,raw,0,rb.length);

		byte[] rname = new byte[0x1e];
		System.arraycopy(raw,3,rname,0,0x1e);
		sName = A2DiskContents.dosName(rname);

		StringBuffer s = new StringBuffer(100);
		for (int i = 0; i < raw.length; ++i)
		{
			byte b = raw[i];
			int x = b;
			if (x<0)
				x += 256;
			String h = Integer.toHexString(x);
			if (h.length()==1)
				s.append("0");
			s.append(h);
			s.append(" ");
		}
		hexRaw = s.toString();

        viewRight.setEditable(false);
        viewRight.setContentType("text/html");
	}

	public String toString()
	{
		return sName;
	}
	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#getIcon()
	 */
	public Icon getIcon()
	{
		return mIcon;
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#getRightPane()
	 */
	public Component getRightPane()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<html>Catalog entry: <b>");
		sb.append(sName);
		sb.append("</b><br></br>");
		sb.append("Raw bytes: ");
		sb.append(hexRaw);
		sb.append("</html>");
		viewRight.setText(sb.toString());
		return viewRight;
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#setNode(DefaultMutableTreeNode)
	 */
	public void setNode(DefaultMutableTreeNode n)
	{
		node = n;
	}

	public boolean isDisplayable()
	{
		return sName.length() > 0;
	}
}
