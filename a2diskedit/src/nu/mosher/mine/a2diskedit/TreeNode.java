package nu.mosher.mine.a2diskedit;

import java.awt.Component;
import javax.swing.Icon;

public interface TreeNode
{
	public String toString();
	public Icon getIcon();
	public Component getRightPane();
}
