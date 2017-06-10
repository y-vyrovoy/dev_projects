import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

import java.util.List;


public class CFileDataProcessor {
	
	private File mfilData;
	
	public CFileDataProcessor() 
	{		
	}
	
	public CFileDataProcessor(File filSource) 
	{
		mfilData = filSource;
	}

	public boolean ParseFileData()
	{
		
		try
		{
			Scanner scnSource = new Scanner(mfilData);
			
			System.out.println("======= Next line!");
			
			while(scnSource.hasNextLine() == true)
			{
				String sNextLine = scnSource.nextLine();
				
				List<String> lstCommands = new ArrayList<String>();
				
				int nStart = 0;
				int nEnd = -1;
				
				while (nStart >=0 )
				{
					String sCurrentLine = sNextLine;
					
					nEnd = sCurrentLine.indexOf(",", nStart)-1;
					if (nEnd < 0)
					{
						nEnd = sCurrentLine.length()-1;
					}
					
					if(nEnd >= nStart)
					{
						String sTmp = new String( sCurrentLine.substring(nStart, nEnd+1));
						lstCommands.add(sTmp);
						
						nStart = sCurrentLine.indexOf(",", nEnd + 1)+1;
					}
					else
					{
						lstCommands.add("");
						nStart = sCurrentLine.indexOf(",", nStart)+1;
					}
					
					if (nEnd >= sCurrentLine.length()-1)
					{
						nStart = -1;						
					}

				}
				
				ListIterator<String> it = lstCommands.listIterator();
				
				while (it.hasNext() == true)
				{
					System.out.println(it.next());
				}
				
				System.out.println("======= Next line!");
			}
			
			scnSource.close();
			
			
		}
		catch(IOException ex)
		{
			System.out.print(ex.getMessage());
		}
		
		
		
		return true;
	}
}
