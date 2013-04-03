import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class HopDocument
{
	JTextArea jtc;
	UndoManager udmgr;
	File file;
	
	public HopDocument(File f) throws IOException
	{
		if(f != null)
		{
			file = f;
			FileReader fw = new FileReader(file);
			jtc = new JTextArea();
			jtc.read(fw, null);
			fw.close();
		}
		else
		{
			jtc = new JTextArea();
		}
		jtc.setMargin(new Insets(10,10,10,10));
		jtc.setTabSize(3);
		
		udmgr = new UndoManager();
		jtc.getDocument().addUndoableEditListener(new UndoableEditListener()
		{
			public void undoableEditHappened(UndoableEditEvent ue)
			{
				udmgr.addEdit(ue.getEdit());
			}
		});
		
	}
	
	public void undo() throws CannotUndoException
	{
		if(udmgr.canUndo())
		{
			udmgr.undo();
		}
	}
	
	public boolean canUndo()
	{
		return udmgr.canUndo();
	}
	
	public void redo() throws CannotRedoException
	{
		if(udmgr.canRedo())
		{
			udmgr.redo();
		}
	}
	
	public boolean canRedo()
	{
		return udmgr.canRedo();
	}
	
	public File getFile()
	{
		return file;
	}
	
	public JTextComponent getTextComponent()
	{
		return jtc;
	}
	
	public void setFile(File f)
	{
		file = f;
	}
	
	public void save() throws IOException
	{
		if(file != null)
		{
			FileWriter fw = new FileWriter(file);
			jtc.write(fw);
			fw.close();
		}
	}
	
	public void print() throws java.awt.print.PrinterException
	{
		jtc.print();
	}
	
	public String toString()
	{
		if(file != null)
		{
			return file.getName();
		}
		else
		{
			return new String("<Unsaved Document>");
		}
	}
}