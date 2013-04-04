import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.text.*;

public class LHopText implements ActionListener, ListSelectionListener, ItemListener
{
	JFrame jfrm;
	
	DefaultListModel dlmFiles;
	JList jlstFiles;
	
	JSplitPane jsp;
	
	JPanel jplFind;
	JTextField jtfFind;
	JCheckBoxMenuItem jcbmiFind;
	JToggleButton jtbtnFind;
	
	JFormattedTextField jftfGoto;
		
	JPopupMenu jpu;
	
	JMenuItem jmiUndo;
	JMenuItem jmiRedo;
	JButton jbtnUndo;
	JButton jbtnRedo;
	
	File currentDirectory;

	public LHopText()
	{
		jfrm = new JFrame("LHopText");
		Dimension screenSize = jfrm.getToolkit().getScreenSize();
		int width = screenSize.width * 6 / 10;
		int height = screenSize.height * 6 /10;
		jfrm.setSize(width,height);
		jfrm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
		buildMenu();
		buildToolBar();
		
		currentDirectory = new File(System.getProperty("user.home"));
		
		dlmFiles = new DefaultListModel();		
		jlstFiles = new JList(dlmFiles);
		jlstFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlstFiles.addListSelectionListener(this);
		JLabel jlabFileListHeader = new JLabel("Documents", JLabel.CENTER);
		jlabFileListHeader.setBorder(BorderFactory.createEtchedBorder());
		JScrollPane jscrpFilelist = new JScrollPane(jlstFiles);
		jscrpFilelist.setColumnHeaderView(jlabFileListHeader);
			
		JPanel jplRight = new JPanel();
		JLabel jlabEmpty = new JLabel("No Document");
		jplRight.add(jlabEmpty);
		
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jscrpFilelist, jplRight);
		jsp.setDividerLocation(new Double(.2));
		
		buildFindPanel();
			
		jfrm.add(jsp);
		
		jfrm.setVisible(true);
	}
	
	private void buildMenu()
	{
		JMenuBar mbar = new JMenuBar();
		jfrm.setJMenuBar(mbar);
		
		JMenu jmufile = new JMenu("File");
		JMenuItem jmiNew, jmiOpen, jmiSave, jmiSaveAs, jmiPrint, jmiClose, jmiQuit;
		jmufile.add(jmiNew = new JMenuItem("New"));
		jmufile.add(jmiOpen = new JMenuItem("Open"));
		jmufile.addSeparator();
		jmufile.add(jmiSave = new JMenuItem("Save"));
		jmufile.add(jmiSaveAs = new JMenuItem("Save As"));
		jmufile.addSeparator();
		jmufile.add(jmiPrint = new JMenuItem("Print"));
		jmufile.addSeparator();
		jmufile.add(jmiClose = new JMenuItem("Close"));
		jmufile.add(jmiQuit = new JMenuItem("Quit"));
		mbar.add(jmufile);
		
		jmiNew.addActionListener(this);
		jmiOpen.addActionListener(this);
		jmiSave.addActionListener(this);
		jmiSaveAs.addActionListener(this);
		jmiPrint.addActionListener(this);
		jmiClose.addActionListener(this);
		jmiQuit.addActionListener(this);
		
		jmiNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
			InputEvent.CTRL_DOWN_MASK));
		jmiOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
			InputEvent.CTRL_DOWN_MASK));
		jmiSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
			InputEvent.CTRL_DOWN_MASK));
		jmiSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
			InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		jmiPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 
			InputEvent.CTRL_DOWN_MASK));
		jmiClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, 
			InputEvent.CTRL_DOWN_MASK));
				
		JMenu jmuEdit = new JMenu("Edit");
		JMenuItem jmiCut, jmiCopy, jmiPaste, jmiSelectAll, jmiGoto;
		jmuEdit.add(jmiUndo = new JMenuItem("Undo"));
		jmuEdit.add(jmiRedo = new JMenuItem("Redo"));
		jmuEdit.addSeparator();
		jmuEdit.add(jmiCut = new JMenuItem("Cut"));
		jmuEdit.add(jmiCopy = new JMenuItem("Copy"));
		jmuEdit.add(jmiPaste = new JMenuItem("Paste"));
		jmuEdit.addSeparator();
		jmuEdit.add(jmiSelectAll = new JMenuItem("Select All"));
		jmuEdit.addSeparator();
		jmuEdit.add(jcbmiFind = new JCheckBoxMenuItem("Find"));
		jmuEdit.add(jmiGoto = new JMenuItem("Goto"));
		mbar.add(jmuEdit);
		
		jmiUndo.addActionListener(this);
		jmiRedo.addActionListener(this);
		jmiCut.addActionListener(this);
		jmiCopy.addActionListener(this);
		jmiPaste.addActionListener(this);
		jmiSelectAll.addActionListener(this);
		jcbmiFind.addItemListener(this);
		jmiGoto.addActionListener(this);
		
		jmiUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
			InputEvent.CTRL_DOWN_MASK));
		jmiRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 
			InputEvent.CTRL_DOWN_MASK));
		jmiCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 
			InputEvent.CTRL_DOWN_MASK));
		jmiCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 
			InputEvent.CTRL_DOWN_MASK));
		jmiPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 
			InputEvent.CTRL_DOWN_MASK));
		jmiSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, 
			InputEvent.CTRL_DOWN_MASK));
		jcbmiFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 
			InputEvent.CTRL_DOWN_MASK));
		jmiGoto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, 
			InputEvent.CTRL_DOWN_MASK));
	}
	
	private void buildToolBar()
	{
		JToolBar jtb = new JToolBar();
		JButton jbtnNew = new JButton("New");
		JButton jbtnOpen = new JButton("Open");
		JButton jbtnSave = new JButton("Save");
		JButton jbtnPrint = new JButton("Print");
		
		jbtnUndo = new JButton("Undo");
		jbtnRedo = new JButton("Redo");
		JButton jbtnCut = new JButton("Cut");
		JButton jbtnCopy = new JButton("Copy");
		JButton jbtnPaste = new JButton("Paste");
		
		jtbtnFind = new JToggleButton("Find");
		
		NumberFormat nf = NumberFormat.getIntegerInstance();
		jftfGoto = new JFormattedTextField(nf);
		jftfGoto.setColumns(5);
		jftfGoto.setMaximumSize(jftfGoto.getPreferredSize());
		
		jtb.add(jbtnNew);
		jtb.add(jbtnOpen);
		jtb.add(jbtnSave);
		jtb.addSeparator();
		
		jtb.add(jbtnUndo);
		jtb.add(jbtnRedo);
		jtb.addSeparator();
		
		jtb.add(jbtnCut);
		jtb.add(jbtnCopy);
		jtb.add(jbtnPaste);
		jtb.addSeparator();
		
		jtb.add(jbtnPrint);
		jtb.addSeparator();
		
		jtb.add(jtbtnFind);
		jtb.addSeparator();
		
		jtb.add(new JLabel("Goto: "));
		jtb.add(jftfGoto);
		jtb.addSeparator();
		
		jfrm.add(jtb,BorderLayout.NORTH);
		
		jbtnNew.addActionListener(this);
		jbtnOpen.addActionListener(this);
		jbtnSave.addActionListener(this);
		jbtnPrint.addActionListener(this);
		jbtnUndo.addActionListener(this);
		jbtnRedo.addActionListener(this);
		jbtnCut.addActionListener(this);
		jbtnCopy.addActionListener(this);
		jbtnPaste.addActionListener(this);
		jtbtnFind.addItemListener(this);
		
		jftfGoto.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				int idx = jlstFiles.getSelectedIndex();
			
				if(idx != -1)
				{
					HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
					JTextComponent jtc = hd.getTextComponent();
					
					int intLN = Integer.parseInt(jftfGoto.getText());
					if(intLN > 0)
					{
						try
						{
							jtc.setCaretPosition(jtc.getDocument().getDefaultRootElement().getElement(intLN - 1).getStartOffset());
							jtc.requestFocus();
						}
						catch(NullPointerException ne)
						{
							Toolkit.getDefaultToolkit().beep();
							jftfGoto.setText("");
							jftfGoto.requestFocus();
						}
					}
					else
					{
						Toolkit.getDefaultToolkit().beep();
						jftfGoto.setText("");
					}
				}
			}
		});
	}
	
	private void buildFindPanel()
	{
		jplFind = new JPanel();
		
		JLabel jlab = new JLabel("Find: ");
		jtfFind = new JTextField(15);
		JButton jbtnPrevious = new JButton("Previous");
		JButton jbtnNext = new JButton("Next");
		
		jbtnPrevious.setMnemonic(KeyEvent.VK_P);
		jbtnNext.setMnemonic(KeyEvent.VK_N);
		
		jplFind.add(jlab);
		jplFind.add(jtfFind);
		jplFind.add(jbtnPrevious);
		jplFind.add(jbtnNext);
		
		jbtnPrevious.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				int idx = jlstFiles.getSelectedIndex();
			
				if(idx != -1)
				{
					HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
					JTextComponent jtc = hd.getTextComponent();
					
					try
					{
						String str = jtc.getText().substring(0, (jtc.getCaretPosition() -1));
						String findStr = jtfFind.getText();
						int idxFound;
					
						idxFound = str.lastIndexOf(findStr);
					
						if(idxFound > -1)
						{
							jtc.setCaretPosition(idxFound);
							jtc.moveCaretPosition(idxFound + findStr.length());
							jtc.requestFocus();
						}
						else
						{
							Toolkit.getDefaultToolkit().beep();
						}
					}
					catch(StringIndexOutOfBoundsException se)
					{
						Toolkit.getDefaultToolkit().beep();
					}
				}
			}
		});
		
		jbtnNext.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				int idx = jlstFiles.getSelectedIndex();
			
				if(idx != -1)
				{
					HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
					JTextComponent jtc = hd.getTextComponent();
				
					String str = jtc.getText();
					String findStr = jtfFind.getText();
					int idxFound;
		
					idxFound = str.indexOf(findStr, jtc.getCaretPosition());
							
					if(idxFound > -1)
					{
						jtc.setCaretPosition(idxFound);
						jtc.moveCaretPosition(idxFound + findStr.length());
						jtc.requestFocus();
					}
					else
					{
						Toolkit.getDefaultToolkit().beep();
					}
				}
			}
		});
		
		jtfFind.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				int idx = jlstFiles.getSelectedIndex();
			
				if(idx != -1)
				{
					HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
					JTextComponent jtc = hd.getTextComponent();
				
					String str = jtc.getText();
					String findStr = jtfFind.getText();
					int idxFound;
		
					idxFound = str.indexOf(findStr, jtc.getCaretPosition());
							
					if(idxFound > -1)
					{
						jtc.setCaretPosition(idxFound);
						jtc.moveCaretPosition(idxFound + findStr.length());
						jtc.requestFocus();
					}
					else
					{
						Toolkit.getDefaultToolkit().beep();
					}
				}
			}
		});
	}
	
	private void buildPopupMenu(JTextComponent jtc)
	{
		jpu = new JPopupMenu();
		JMenuItem jmiCut, jmiCopy, jmiPaste;
		jpu.add(jmiCut = new JMenuItem("Cut"));
		jpu.add(jmiCopy = new JMenuItem("Copy"));
		jpu.add(jmiPaste = new JMenuItem("Paste"));
		
		jmiCut.addActionListener(this);
		jmiCopy.addActionListener(this);
		jmiPaste.addActionListener(this);
		
		jtc.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent me)
			{
				if(me.isPopupTrigger())
				{
					jpu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
			
			public void mouseReleased(MouseEvent me)
			{
				if(me.isPopupTrigger())
				{
					jpu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String arg = e.getActionCommand();
	
		if(arg.equals("New"))
		{
			open(null);
		}
		else if(arg.equals("Open"))
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(currentDirectory);	
			
			if(chooser.showOpenDialog(jfrm) == JFileChooser.APPROVE_OPTION)
			{
				open(chooser.getSelectedFile());
				currentDirectory = chooser.getCurrentDirectory();
			}
		}
		else if(arg.equals("Save"))
		{
			int idx = jlstFiles.getSelectedIndex();
			JFileChooser chooser = new JFileChooser();
			
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				if(hd.getFile()==null)
				{
					chooser.setCurrentDirectory(currentDirectory);
					
					if(chooser.showSaveDialog(jfrm) == JFileChooser.APPROVE_OPTION)
					{
						hd.setFile(chooser.getSelectedFile());
						currentDirectory = chooser.getCurrentDirectory();
					}
				}
			
				if(hd.getFile() != null)
				{
					try
					{
						hd.save();
					}
					catch(IOException ie)
					{
						warnUser("Unable to create/save file: " + chooser.getSelectedFile());
						ie.printStackTrace();
					}
				}
			}
		}
		else if(arg.equals("Save As"))
		{
			int idx = jlstFiles.getSelectedIndex();
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(currentDirectory);
				
				if(chooser.showSaveDialog(jfrm) == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						hd.setFile(chooser.getSelectedFile());
						hd.save();
						currentDirectory = chooser.getCurrentDirectory();
					}
					catch(IOException ie)
					{
						warnUser("Unable to create/save file: " + chooser.getSelectedFile());
						ie.printStackTrace();
					}
				}
			}
		}
		else if(arg.equals("Print"))
		{
			int idx = jlstFiles.getSelectedIndex();
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				
				try
				{
					hd.print();
				}
				catch(java.awt.print.PrinterException pe)
				{
					warnUser("Error accessing printer");
					pe.printStackTrace();
				}
			}
		}
		else if(arg.equals("Close"))
		{
			int idx = jlstFiles.getSelectedIndex();
			int divloc = jsp.getDividerLocation();
			int listSize;
			
			if(idx != -1)
			{
				dlmFiles.removeElementAt(idx);
				listSize = dlmFiles.getSize();
				if(listSize == 0)
				{
					JPanel jplEmpty = new JPanel();
					JLabel jlabEmpty = new JLabel("No Document");
					jplEmpty.add(jlabEmpty);
					jsp.setRightComponent(jplEmpty);
				}
				else
				{
					if(idx >= dlmFiles.getSize()) idx--;
					jlstFiles.setSelectedIndex(idx);
				}
			}
			jsp.setDividerLocation(divloc);
		}
		else if(arg.equals("Quit"))
		{
			System.exit(0);
		}
		else if(arg.equals("Undo"))
		{
			int idx = jlstFiles.getSelectedIndex();
			
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				try
				{
					hd.undo();
				}
				catch(CannotUndoException ce)
				{
					Toolkit.getDefaultToolkit().beep();
				}
			}
		}
		else if(arg.equals("Redo"))
		{
			int idx = jlstFiles.getSelectedIndex();
			
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				try
				{
					hd.redo();
				}
				catch(CannotRedoException ce)
				{
					Toolkit.getDefaultToolkit().beep();
				}
			}
		}
		else if(arg.equals("Cut"))
		{
			int idx = jlstFiles.getSelectedIndex();
			
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				JTextComponent jtc = hd.getTextComponent();
				jtc.cut();
			}
		}
		else if(arg.equals("Copy"))
		{
			int idx = jlstFiles.getSelectedIndex();
			
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				JTextComponent jtc = hd.getTextComponent();
				jtc.copy();
			}
			
		}
		else if(arg.equals("Paste"))
		{
			int idx = jlstFiles.getSelectedIndex();
			
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				JTextComponent jtc = hd.getTextComponent();
				jtc.paste();
			}
			
		}
		else if(arg.equals("Select All"))
		{
			int idx = jlstFiles.getSelectedIndex();
			
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				JTextComponent jtc = hd.getTextComponent();
				jtc.requestFocus();
				jtc.selectAll();
			}
		}
		else if(arg.equals("Goto"))
		{
			jftfGoto.requestFocus();
		}
	}
	
	public void valueChanged(ListSelectionEvent le)
	{
		if(!le.getValueIsAdjusting())
		{
			int idx = jlstFiles.getSelectedIndex();
			int divloc = jsp.getDividerLocation();
			if(idx != -1)
			{
				HopDocument hd = (HopDocument) dlmFiles.getElementAt(idx);
				JScrollPane jscrpTextDisplay = new JScrollPane(hd.getTextComponent());
				TextLineNumber tln = new TextLineNumber(hd.getTextComponent());
				jscrpTextDisplay.setRowHeaderView(tln);
				jsp.setRightComponent(jscrpTextDisplay);
				
			}
			else
			{
				JPanel jplEmpty = new JPanel();
				JLabel jlabEmpty = new JLabel("No Document");
				jplEmpty.add(jlabEmpty);
				jsp.setRightComponent(jplEmpty);
			}
			jsp.setDividerLocation(divloc);
		}
	}
	
	public void itemStateChanged(ItemEvent ie)
	{
		if(jtbtnFind == ie.getSource())
		{
			if(jtbtnFind.isSelected())
			{
				jcbmiFind.setSelected(true);
				jfrm.add(jplFind,BorderLayout.SOUTH);
				jtfFind.requestFocus();
			}
			else
			{
				jcbmiFind.setSelected(false);
				jfrm.remove(jplFind);
			}
		}
		else if(jcbmiFind == ie.getSource())
		{
			if(jcbmiFind.isSelected())
			{
				jtbtnFind.setSelected(true);
				jfrm.add(jplFind,BorderLayout.SOUTH);
				jtfFind.requestFocus();
			}
			else
			{
				jtbtnFind.setSelected(false);
				jfrm.remove(jplFind);
			}
		}
		jfrm.validate();
	}
	
	private void open(File file)
	{
		int divloc = jsp.getDividerLocation();
		
		try
		{
			HopDocument hd = new HopDocument(file);
			JTextComponent jtc = hd.getTextComponent();
			buildPopupMenu(jtc);
			
			dlmFiles.addElement(hd);
		
			jlstFiles.setSelectedIndex(dlmFiles.getSize() - 1);	
					
			JScrollPane jscrpTextDisplay = new JScrollPane(jtc);
			TextLineNumber tln = new TextLineNumber(jtc);
			jscrpTextDisplay.setRowHeaderView(tln);
			jsp.setRightComponent(jscrpTextDisplay);
		}
		catch(IOException ie)
		{
			warnUser("Unable to create/open file: " + file.getName());
			ie.printStackTrace();
		}
		
		jsp.setDividerLocation(divloc);
	}
	
	public void warnUser(String message)
	{
		JOptionPane.showMessageDialog(jfrm, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new LHopText();
			}
		});
	}
}