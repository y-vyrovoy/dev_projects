package JPoll;
import java.awt.*;


public class CApplication 
{
	
	  public static void main(String[] args) 
	  {
		  EventQueue.invokeLater(new CMainFrameCreator());
	  }
}

class CMainFrameCreator implements Runnable
{
	
	public void run()
	{
		showFrame();
	}
	
	 static void showFrame()
	 {

		 // Create frame with specific title
		Frame frame = new CMainFrame();

		frame.setSize(1300, 800);
		
		frame.setVisible(true);
		
	}
	 
}