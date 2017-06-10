package JPoll;
import java.io.File;


import javax.swing.JFileChooser;


public class CUtils 
{
	
	public static File ChooseFileDlg(String sPath)
	{
	    JFileChooser chooser = new JFileChooser();
	    
	    //FileNameExtensionFilter filter = new FileNameExtensionFilter("","*.*");
	    //chooser.setFileFilter(filter);
	   
	    File file = new File(sPath);
	    
	    chooser.setCurrentDirectory(file);
	    int returnVal = chooser.showOpenDialog(null);
	    
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	return chooser.getSelectedFile();
	    }
	    else
	    {
	    	File filEmpty = new File("");
	    	return filEmpty;
	    }
	}
	
	
}
