import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/*
 * Created on Oct 16, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class MainFrame extends JFrame
{
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
}
