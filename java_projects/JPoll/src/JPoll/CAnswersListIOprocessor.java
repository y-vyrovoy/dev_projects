package JPoll;
import java.io.*;

public class CAnswersListIOprocessor implements Serializable 
{
	private static final long serialVersionUID = -1196511190214726146L;


	public CAnswersListIOprocessor()
	{}
	
	public void WriteAnswerListToFile(CAnswerListIOcontainer lstToSave, String sFilePathName)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(sFilePathName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(lstToSave);
			oos.flush();
			oos.close();
		
		}
		catch(IOException e)
		{
			System.out.println(e.getStackTrace());
			System.out.println(e.getMessage());
		}
			
	}
	
	
	public CAnswerListIOcontainer ReadAnswerListToFile(String sFilePathName)
	{
		CAnswerListIOcontainer lstToSave = null;
		
		try
		{

			FileInputStream fos = new FileInputStream(sFilePathName);
			ObjectInputStream oos = new ObjectInputStream(fos);
			
			try
			{
				lstToSave = (CAnswerListIOcontainer)oos.readObject();
			}
			catch (ClassNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			oos.close();
		
		}
		catch(IOException e)
		{
			System.out.println(e.getStackTrace());
			System.out.println(e.getMessage());
		}
		
		return lstToSave;
			
	}
	

	

}
