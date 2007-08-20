package pom1.gui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import pom1.apple1.Keyboard;
import pom1.apple1.Memory;
import pom1.apple1.Pia6820;
import pom1.apple1.Screen;
import pom1.apple1.cpu.M6502;
import pom1.apple1.cpu.M65C02;

public class GUI extends WindowAdapter implements WindowListener, ActionListener
{
	private ClipboardHandler clipboardHandler;

	public GUI() throws IOException
	{
		initVariable();
		initApple1();
		initGui();
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (guiMenuFileLoad.equals(evt.getSource()))
		{
			fileLoad();
			return;
		}
		if (guiMenuFileSave.equals(evt.getSource()))
		{
			fileSave();
			return;
		}
		if (guiMenuFileExit.equals(evt.getSource()))
		{
			close();
		}
		if (guiMenuFilePaste.equals(evt.getSource()))
			clipboardHandler.sendDataToApple1(pia);
		if (guiMenuEmulatorReset.equals(evt.getSource()))
		{
			pia.reset();
			cpu.reset();
			return;
		}
		if (guiMenuEmulatorHardReset.equals(evt.getSource()))
		{
			cpu.stop();
			pia.reset();
			try
			{
				mem.reset();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			screen.reset();
			cpu.reset();
			cpu.start();
			return;
		}
		//        if(guiMenuConfigScreen.equals(evt.getSource()))
		//        {
		//            configScreen();
		//            return;
		//        }
		//        if(guiMenuConfigMemory.equals(evt.getSource()))
		//        {
		//            configMemory();
		//            return;
		//        }
		//        if(guiMenuDebugShow.equals(evt.getSource()))
		//        {
		//            debugShow();
		//            return;
		//        }
		//        if(guiMenuDebugDispose.equals(evt.getSource()))
		//        {
		//            debugDispose();
		//            return;
		//        }
		if (guiMenuHelpAbout.equals(evt.getSource()))
		{
			aboutPom1();
			return;
		}
		if (btSave.equals(evt.getSource()))
		{
			fileSaveExec();
			return;
		}
		if (btLoad.equals(evt.getSource()))
		{
			fileLoadExec();
			return;
		}
		//        if(btScreen.equals(evt.getSource()))
		//        {
		//            configScreenExec();
		//            return;
		//        }
		//        if(btMemory.equals(evt.getSource()))
		//        {
		//            configMemoryExec();
		//            return;
		//        }
	}

	private void close()
	{
		cpu.stop();
		clipboardHandler.close();
		guiDialog.dispose();
		guiFrame.dispose();
	}

	public void windowClosing(WindowEvent e)
	{
		if (guiFrame.equals(e.getSource()))
		{
			close();
		}
	}

	private void initGui()
	{
		clipboardHandler = new ClipboardHandler(keyboard);
		guiFrame = new JFrame("Pom1: The Apple I Emulator");
		guiFrame.setLayout(new BorderLayout());
		this.guiFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.guiFrame.addWindowListener(this);
		guiMenuBar = new JMenuBar();
		this.guiFrame.setJMenuBar(this.guiMenuBar);
		guiMenuFile = new JMenu("File");
		guiMenuFileLoad = new JMenuItem("Load Memory");
		guiMenuFileLoad.addActionListener(this);
		guiMenuFileSave = new JMenuItem("Save Memory");
		guiMenuFileSave.addActionListener(this);
		guiMenuFilePaste = new JMenuItem("Paste");
		guiMenuFilePaste.addActionListener(this);
		guiMenuFileExit = new JMenuItem("Exit");
		guiMenuFileExit.addActionListener(this);
		guiMenuFile.add(guiMenuFileLoad);
		guiMenuFile.add(guiMenuFileSave);
		guiMenuFile.addSeparator();
		guiMenuFile.add(guiMenuFilePaste);
		guiMenuFile.addSeparator();
		guiMenuFile.add(guiMenuFileExit);
		guiMenuBar.add(guiMenuFile);
		guiMenuEmulator = new JMenu("Emulator");
		guiMenuEmulatorReset = new JMenuItem("Reset");
		guiMenuEmulatorReset.addActionListener(this);
		guiMenuEmulatorHardReset = new JMenuItem("Hard Reset");
		guiMenuEmulatorHardReset.addActionListener(this);
		guiMenuEmulator.add(guiMenuEmulatorReset);
		guiMenuEmulator.add(guiMenuEmulatorHardReset);
		guiMenuBar.add(guiMenuEmulator);
		//        guiMenuConfig = new JMenu("Config");
		//        guiMenuConfigScreen = new JMenuItem("Screen");
		//        guiMenuConfigScreen.addActionListener(this);
		//        guiMenuConfig.add(guiMenuConfigScreen);
		//        guiMenuConfigMemory = new JMenuItem("Memory");
		//        guiMenuConfigMemory.addActionListener(this);
		//        guiMenuConfig.add(guiMenuConfigMemory);
		//        guiMenuBar.add(guiMenuConfig);
		//        guiMenuDebug = new JMenu("Debug");
		//        guiMenuDebugShow = new JMenuItem("Show");
		//        guiMenuDebugShow.addActionListener(this);
		//        guiMenuDebugDispose = new JMenuItem("Dispose");
		//        guiMenuDebugDispose.addActionListener(this);
		//        guiMenuDebug.add(guiMenuDebugShow);
		//        guiMenuDebug.add(guiMenuDebugDispose);
		//        guiMenuBar.add(guiMenuDebug);
		guiMenuHelp = new JMenu("Help");
		guiMenuHelpAbout = new JMenuItem("About");
		guiMenuHelpAbout.addActionListener(this);
		guiMenuHelp.add(guiMenuHelpAbout);
		guiMenuBar.add(guiMenuHelp);
		guiDialog = new JDialog(guiFrame,true);
		guiDialog.addWindowListener(this);
		guiDialog.setModal(true);
		startHexTxt = new JTextField("0000",4);
		endHexTxt = new JTextField("FFFF",4);
		rawCbox = new JCheckBox("Raw Data");
		btSave = new JButton("Save");
		btSave.addActionListener(this);
		btLoad = new JButton("Load");
		btLoad.addActionListener(this);
		//        btScreen = new JButton("OK");
		//        btScreen.addActionListener(this);
		//        miscTxt = new JTextField("", 2);
		//        wRomCbox = new JCheckBox("Write in ROM enabled");
		//        btMemory = new JButton("OK");
		//        btMemory.addActionListener(this);
		bt6502 = new JButton("OK");
		bt6502.addActionListener(this);
		guiFrame.add(screen);
		//        Insets i = guiFrame.getInsets();
		//        guiFrame.setSize((280 * pixelSize + (i.left + i.right)) - 2, (192 * pixelSize + (i.top + i.bottom)) - 2);
		guiFrame.setResizable(false);
		guiFrame.pack();
		guiFrame.setVisible(true);
		screen.setFocusTraversalKeysEnabled(false);
		screen.requestFocus();
	}

	private void initApple1() throws IOException
	{
		screen = new Screen();
		pia = new Pia6820(screen);
		mem = new Memory(pia);
		keyboard = new Keyboard(pia);

		screen.addKeyListener(keyboard);

		if (System.getProperty("65C02","N").equalsIgnoreCase("Y"))
		{
			cpu = new M65C02(mem,1000,50);
		}
		else
		{
			cpu = new M6502(mem,1000,50);
		}
		cpu.start();
		synchronise(false);
	}

	public void synchronise(boolean sync)
	{
		if (synchronised != sync)
		{
			synchronised = sync;
			screen.setSynchronise(sync);
			cpu.setSynchronise(sync);
		}
	}

	private void initVariable()
	{
		//        pixelSize = 2;
		//        terminalSpeed = 60;
		//        pixelSize = 1;
		//        terminalSpeed = 60000;
		//        writeInRom = true;
		//        ram8k = false;
	}

	private void fileLoad()
	{
		guiDialog.setTitle("Load memory");
		guiDialog.setLayout(new FlowLayout());
		guiDialog.getContentPane().add(new Label("Starting Address (Hex): "));
		guiDialog.getContentPane().add(startHexTxt);
		guiDialog.getContentPane().add(new Label("(Used only if Raw data is checked)"));
		guiDialog.getContentPane().add(rawCbox);
		guiDialog.getContentPane().add(btLoad);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				guiDialog.dispose();
			}
		});
		guiDialog.getContentPane().add(cancel);
		Point point = new Point();
		point = guiFrame.getLocation();
		int x = (int)point.getX();
		int y = (int)point.getY();
		guiDialog.setSize(220,130);
		guiDialog.setVisible(true);
	}

	private void fileLoadExec()
	{
		int start = hexStringToInt(startHexTxt.getText());
		if (start == -1)
			return;
		String fileName = new String();
		FileDialog fileDialog = new FileDialog(guiFrame,"Load Memory ...",0);
		fileDialog.setVisible(true);
		if (fileDialog.getFile() == null)
			return;
		fileName = fileDialog.getDirectory() + File.separator + fileDialog.getFile();
		FileInputStream fis = null;
		if (rawCbox.isSelected())
			try
			{
				fis = new FileInputStream(fileName);
				int size = fis.available();
				for (int i = start; i < start + size; i++)
					mem.write(i,fis.read());
				fis.close();
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		else
			try
			{
				int lastaddress = 0;
				fis = new FileInputStream(fileName);
				BufferedReader _br = new BufferedReader(new InputStreamReader(fis));
				do
				{
					String _strLine = _br.readLine();
					if (_strLine == null)
						break;
					if (_strLine.length() != 0 && _strLine.charAt(0) != '/')
					{
						int semipos = _strLine.indexOf(':');
						int address;
						if (semipos == 0)
						{
							address = lastaddress + 1;
						}
						else
						{
							String _address = _strLine.substring(0,semipos);
							address = hexStringToInt(_address);
						}
						if (address != -1)
						{
							int offset;
							for (offset = semipos + 1; _strLine.charAt(offset) == ' '; offset++)
								;
							for (int i = offset; i < _strLine.length(); i += 3)
							{
								String _value = _strLine.substring(i,i + 2);
								int value = hexStringToInt(_value);
								if (value == -1)
									break;
								lastaddress = address + (i - offset) / 3;
								mem.write(lastaddress,value);
							}
						}
					}
				}
				while (true);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		guiDialog.dispose();
		guiFrame.toFront();
	}

	private void fileSave()
	{
		guiDialog.setTitle("Save memory");
		guiDialog.setLayout(new FlowLayout());
		guiDialog.getContentPane().add(new Label("From(Hex): "));
		guiDialog.getContentPane().add(startHexTxt);
		guiDialog.getContentPane().add(new Label("To(Hex): "));
		guiDialog.getContentPane().add(endHexTxt);
		guiDialog.getContentPane().add(rawCbox);
		guiDialog.getContentPane().add(btSave);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				guiDialog.dispose();
			}
		});
		guiDialog.getContentPane().add(cancel);
		Point point = new Point();
		point = guiFrame.getLocation();
		int x = (int)point.getX();
		int y = (int)point.getY();
		guiDialog.setLocation(60 + x,70 + y);
		guiDialog.setSize(210,130);
		guiDialog.pack();
		guiDialog.setVisible(true);
	}

	private void fileSaveExec()
	{
		int start = hexStringToInt(startHexTxt.getText());
		int end = hexStringToInt(endHexTxt.getText());
		if ((start == -1) | (end == -1))
			return;
		int fbrut[] = new int[(end - start) + 1];
		fbrut = mem.dumpMemory(start,end);
		String fileName = new String();
		FileDialog fileDialog = new FileDialog(guiFrame,"Save Memory ...",1);
		fileDialog.setVisible(true);
		if (fileDialog.getFile() == null)
			return;
		if (rawCbox.isSelected())
		{
			fileName = fileDialog.getDirectory() + File.separator + fileDialog.getFile();
			FileOutputStream fos = null;
			try
			{
				fos = new FileOutputStream(fileName);
				for (int i = 0; i < (end - start) + 1; i++)
					fos.write(fbrut[i]);
				fos.close();
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
		else
		{
			fileName = fileDialog.getDirectory() + File.separator + fileDialog.getFile();
			FileOutputStream fos = null;
			StringBuffer _buf = new StringBuffer(4 * ((end - start) + 1));
			_buf.append("// Pom1 Save : " + fileDialog.getFile() + "\r\n");
			int j = start;
			for (int i = 0; i < (end - start) + 1;)
			{
				if ((j % 8 == 0) | (j == start))
					_buf.append("\r\n" + toHex4(j) + ": ");
				_buf.append(toHex(fbrut[i]) + " ");
				i++;
				j++;
			}
			try
			{
				fos = new FileOutputStream(fileName);
				fos.write(_buf.toString().getBytes());
				fos.close();
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
		guiDialog.dispose();
	}

	//    private void configScreen()
	//    {
	//    	guiDialog.getContentPane().removeAll();
	//        guiDialog.setTitle("Screen Configuration");
	//        guiDialog.setLayout(new FlowLayout());
	//        guiDialog.setPreferredSize(new Dimension(315, 175));
	//        guiDialog.getContentPane().add(new Label("Choose the Pixel Size :"));
	//        grpScreenX = new ButtonGroup();
	//        AbstractButton b = new JCheckBox("x1", pixelSize == 1);
	//        b.setMnemonic('1');
	//        grpScreenX.add(b);
	//        guiDialog.getContentPane().add(b);
	//        b = new JCheckBox("x2", pixelSize == 2);
	//        b.setMnemonic('2');
	//        grpScreenX.add(b);
	//        guiDialog.getContentPane().add(b);
	//        b = new JCheckBox("single field", scanlines);
	//        b.setMnemonic('F');
	//        grpScreenX.add(b);
	//        guiDialog.getContentPane().add(b);
	//        guiDialog.getContentPane().add(new Label("Terminal speed (characters per second):"));
	//        miscTxt.setText(Integer.toString(terminalSpeed,10));
	//        miscTxt.setMinimumSize(new Dimension(100,30));
	//        guiDialog.getContentPane().add(miscTxt);
	//        guiDialog.getContentPane().add(new Label("  "));
	//        guiDialog.getContentPane().add(btScreen);
	//        Point point = new Point();
	//        point = guiFrame.getLocation();
	//        int x = (int)point.getX();
	//        int y = (int)point.getY();
	//        guiDialog.setLocation(60 + x, 70 + y);
	//        guiDialog.pack();
	//        guiDialog.setVisible(true);
	//    }
	//
	//    private void configScreenExec()
	//    {
	//    	int mnemonic = grpScreenX.getSelection().getMnemonic();
	//        if(mnemonic == '1')
	//        {
	//            pixelSize = 1;
	//            scanlines = false;
	//        }
	//        else if(mnemonic == '2')
	//        {
	//            pixelSize = 2;
	//            scanlines = false;
	//        }
	//        else if(mnemonic == 'F')
	//        {
	//            pixelSize = 2;
	//            scanlines = true;
	//        }
	//        Insets i = guiFrame.getInsets();
	//        guiFrame.setSize(280 * pixelSize + (i.left + i.right), 192 * pixelSize + (i.top + i.bottom));
	//        screen.setPixelSize(pixelSize);
	//        screen.setScanline(scanlines);
	//        terminalSpeed = Integer.decode(miscTxt.getText()).intValue();
	//        screen.setTerminalSpeed(terminalSpeed);
	//        guiDialog.dispose();
	//        screen.repaint();
	//    }
	//    private void configMemory()
	//    {
	//        guiDialog.setTitle("Memory Configuration");
	//        guiDialog.setLayout(new FlowLayout());
	//        guiDialog.getContentPane().add(new Label("Apple I available RAM size :"));
	//        guiDialog.getContentPane().add(new JCheckBox("8kb", ram8k));
	//        guiDialog.getContentPane().add(new JCheckBox("Max", !ram8k));
	//        wRomCbox.setSelected(writeInRom);
	//        guiDialog.getContentPane().add(new Label("    "));
	//        guiDialog.getContentPane().add(wRomCbox);
	//        guiDialog.getContentPane().add(new Label("    "));
	//        guiDialog.getContentPane().add(new Label("IRQ/BRK vector :"));
	//        miscTxt.setText(toHex(mem.read(0xFFFF)) + toHex(mem.read(0xFFFE)));
	//        guiDialog.getContentPane().add(miscTxt);
	//        guiDialog.getContentPane().add(new Label("    "));
	//        guiDialog.getContentPane().add(btMemory);
	//        Point point = new Point();
	//        point = guiFrame.getLocation();
	//        int x = (int)point.getX();
	//        int y = (int)point.getY();
	//        guiDialog.setLocation(60 + x, 70 + y);
	//        guiDialog.setSize(320, 150);
	//        guiDialog.pack();
	//        guiDialog.setVisible(true);
	//    }
	//
	//    private void configMemoryExec()
	//    {
	//        String _str = " max" ; // TODO fix
	//        if(_str == "8kb")
	//            ram8k = true;
	//        if(_str == "Max")
	//            ram8k = false;
	//        mem.setRam8k(ram8k);
	//        writeInRom = wRomCbox.isSelected();
	//        mem.setWriteInRom(writeInRom);
	//        int brkVector = hexStringToInt(miscTxt.getText());
	//        mem.write(0xFFFE, brkVector & 0xFF);
	//        mem.write(0xFFFF, brkVector >>> 8 & 0xFF);
	//        guiDialog.dispose();
	//    }
	private void aboutPom1()
	{
		TextArea ta = new TextArea(
			" *Pom1 0.7b* the Java Apple I Emulator\nWritten by Verhille Arnaud\nE.mail : gist@wanadoo.fr\nhttp://www.chez.com/apple1/\n\nEnhanced by Ken Wessen (21/2/06)\n\nThanks to :\nSteve Wozniak (The Brain)\nFabrice Frances (Java Microtan Emulator)\nAchim Breidenbach from Boinx Software \n(Sim6502, Online 'Apple-1 Operation Manual')\nJuergen Buchmueller (MAME and MESS 6502 core)\nFrancis Limousy (for his help, and his friendship)\nStephano Priore from the MESS DEV\nJoe Torzewski (Apple I owners Club)\nTom Owad (http://applefritter.com/apple1/)",
			23,45,3);
		ta.setEditable(false);
		guiDialog.setTitle("About Pom1");
		guiDialog.setLayout(new FlowLayout());
		guiDialog.getContentPane().add(ta);
		Point point = new Point();
		point = guiFrame.getLocation();
		int x = (int)point.getX();
		int y = (int)point.getY();
		guiDialog.setLocation(60 + x,70 + y);
		guiDialog.setSize(375,250);
		guiDialog.pack();
		guiDialog.setVisible(true);
	}

	private int hexStringToInt(String s)
	{
		return Integer.parseInt(s,16);
	}

	private String toHex(int i)
	{
		String s = Integer.toHexString(i).toUpperCase();
		if (i < 16)
			s = "0" + s;
		return s;
	}

	private String toHex4(int i)
	{
		String s = Integer.toHexString(i).toUpperCase();
		if (i < 4096)
		{
			s = "0" + s;
			if (i < 256)
			{
				s = "0" + s;
				if (i < 16)
					s = "0" + s;
			}
		}
		return s;
	}

	//    Pia6820 getPIA() { return pia; }
	//    Screen getScreen() { return screen; }
	//    M6502 getMicro() { return micro; }
	//    Keyboard getKeyboard() { return keyboard; }
	//    Memory getMemory() { return mem; }
	//
	private JFrame guiFrame;
	private JMenuBar guiMenuBar;
	private JMenu guiMenuFile;
	private JMenu guiMenuEmulator;
	//    private JMenu guiMenuConfig;
	//    private JMenu guiMenuDebug;
	private JMenu guiMenuHelp;
	private JMenuItem guiMenuFileLoad;
	private JMenuItem guiMenuFileSave;
	private JMenuItem guiMenuFilePaste;
	private JMenuItem guiMenuFileExit;
	private JMenuItem guiMenuEmulatorReset;
	private JMenuItem guiMenuEmulatorHardReset;
	//    private JMenuItem guiMenuConfigScreen;
	//    private JMenuItem guiMenuConfigMemory;
	//    private JMenuItem guiMenuDebugShow;
	//    private JMenuItem guiMenuDebugDispose;
	private JMenuItem guiMenuHelpAbout;
	private JDialog guiDialog;
	private JButton btSave;
	private JButton btLoad;
	private JTextField startHexTxt;
	private JTextField endHexTxt;
	//    private JTextField miscTxt;
	private JCheckBox rawCbox;
	//    private JCheckBox wRomCbox;
	//    private JButton btScreen;
	//    private JButton btMemory;
	private JButton bt6502;
	//    private ButtonGroup grpScreenX;
	//    private int pixelSize;
	//    private boolean scanlines;
	//    private int terminalSpeed;
	//    private boolean writeInRom;
	//    private boolean ram8k;
	private Memory mem;
	private M6502 cpu;
	private Pia6820 pia;
	private Screen screen;
	private Keyboard keyboard;
	private boolean synchronised;
}
