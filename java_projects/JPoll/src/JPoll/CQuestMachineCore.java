package JPoll;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;


public class CQuestMachineCore{


	protected static CQuestionnaire InitQuestionnaireFromCSV(File filCSV)
	{
		
		List<List<String>> lstCommands = CreateListFromCSV(filCSV);
		
		CQuestionnaire qnrNew = CreateQuestionnaireFromList(lstCommands);
		
		return qnrNew;
	}
	
	protected static List<List<String>> CreateListFromCSV(File filCSV)
	{
		List<List<String>> lstCommands = new ArrayList<List<String>>();
		
		try
		{
			
			Scanner scnrSource = new Scanner(filCSV);
			
			// Reading string by string from input file
			// Commas separate tags. Tags are added to list
			while(scnrSource.hasNextLine() == true)
			{
				String sNextLine = scnrSource.nextLine();

				List<String> lstLine = new ArrayList<String>();
				
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
						lstLine.add(sTmp);
						
						nStart = sCurrentLine.indexOf(",", nEnd + 1)+1;
					}
					else
					{
						lstLine.add("");
						nStart = sCurrentLine.indexOf(",", nStart)+1;
					}
					
					if (nEnd >= sCurrentLine.length()-1)
					{
						nStart = -1;						
					}

				}
				
				lstCommands.add(lstLine);
			}
			
			scnrSource.close();
			
		}
		catch(IOException ex)
		{
			System.out.print(ex.getMessage()); // debug option
			return new ArrayList<List<String>>();
		}
		
		return lstCommands;
	}
	
	protected static CQuestionnaire CreateQuestionnaireFromList(List<List<String>> lstCommands)
	{
		CQuestionnaire qnrNew = new CQuestionnaire(); 
		
		//------------------------------------------------------------		
		// Now let's fill CQuestionnaire with data from CSV!
		
		Iterator<List<String>> itLine = lstCommands.iterator();
		
		while (itLine.hasNext() == true)
		{
			CQuestStage stgNew = new CQuestStage();	
			
			Iterator<String> itCurrentLine = itLine.next().iterator();
			
			// filling questionnaire stage info 
			stgNew.setID(itCurrentLine.next());
			
			
			stgNew.setStageType(itCurrentLine.next().equals("S")?
									CQuestStage.enmStageType.ST_SINGLE_ANSWER:
									CQuestStage.enmStageType.ST_MULTY_ANSWER);
			
			//system tag. unused
			itCurrentLine.next(); 

			qnrNew.AddStage(stgNew, null);
		}
			
		itLine = lstCommands.iterator();
		Iterator<CQuestStage> itStages = qnrNew.getStagesIterator();
		
		while ( (itLine.hasNext() == true) && (itStages.hasNext() == true) ) 
		{			
			CQuestStage stgCurrent  = itStages.next();	
			
			Iterator<String> itCurrentLine = itLine.next().iterator();
			
			// filling questionnaire stage info 
			itCurrentLine.next(); // Caption
			itCurrentLine.next(); // Quest type
			itCurrentLine.next(); // System tag. unused 

			while (itCurrentLine.hasNext() == true)
			{
				CQuestAnswer qaNew = new CQuestAnswer();
				
				// filling questionnaire stage info
				qaNew.setCaption(itCurrentLine.next());
				
				int nNextStage = 0;
				
				try
				{
					nNextStage = Integer.valueOf(itCurrentLine.next());
				}
				catch(NumberFormatException ex)
				{
					System.out.println("Wrong next_stage_number:");
					System.out.println(ex.getLocalizedMessage());
				}

				ListIterator<CQuestStage> itTmp = qnrNew.getStagesIterator();
				int iStage = 1;
					
				while(itTmp.hasNext() == true)
				{
					itTmp.next();
					
					if(iStage == nNextStage)
						break;
					
					iStage++;
				
				}
				
				qaNew.setNextStage(itTmp.previous());
			
				stgCurrent.AddItem(qaNew);
			}
			
		}

		return qnrNew;
	}	
	
	public static void SaveQuestionnaireToCSV(CQuestionnaire qnrSource, Path pDestinationFile)
	{
		
		// If this path doesn't exist or is not writable
		if (Files.exists(pDestinationFile) == true &&
				Files.isWritable(pDestinationFile) == false) 
		{
			return;
		}
		
		
		List<String> lstCommands = new ArrayList<String>();
		
		ListIterator<CQuestStage> itStages = qnrSource.getStagesIterator();
		int iStageIndex = 1;
		
		while(itStages.hasNext() == true)
		{
			String sCommand = "";
			
			CQuestStage qstStage = itStages.next();
			sCommand = sCommand + qstStage.getID() + ",";
			
			if (qstStage.getStageType() == CQuestStage.enmStageType.ST_SINGLE_ANSWER)
			{
				sCommand = sCommand  + "S";
			}
			else
			{
				sCommand =  sCommand  + "M";
			}
			sCommand = sCommand + ",";
			
			// Unused tag
			sCommand = sCommand + "0";
			
			Iterator<CQuestAnswer> itAnswer = qstStage.GetAnswersIterator();
			
			while(itAnswer.hasNext() == true)
			{
				CQuestAnswer qaAnswer = itAnswer.next();
				
				if(qaAnswer.getCaption().equals(CQuestAnswer.OPENED_ANSWER) == false)
				{
					sCommand = sCommand + "," + qaAnswer.getCaption();
				}
				else
				{
					sCommand = sCommand + ",";
				}
				
				if(qaAnswer.getNextStage() != null)
				{
					sCommand = sCommand + "," + qnrSource.getStageNumber(qaAnswer.getNextStage());
				}
				else
				{
					sCommand = sCommand + "," + Integer.toString(iStageIndex+1);
				}
				
			}
			
			
			lstCommands.add(sCommand);
			iStageIndex++;
		}
		
		//Path pDestFile = Paths.get(pDestinationFolder.toAbsolutePath().toString(), "Test Quest Result.csv");
		
		try
		{

			///byte [] abBomUTF8 = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};
			
			OutputStream osDestination = Files.newOutputStream(pDestinationFile, StandardOpenOption.CREATE, 
																				StandardOpenOption.TRUNCATE_EXISTING, 
																				StandardOpenOption.WRITE);
			
			//osDestination.write(abBomUTF8);
			osDestination.flush();
			
			OutputStreamWriter oswDestination = new OutputStreamWriter(osDestination /*, StandardCharsets.UTF_8*/);
			
			Iterator<String> itCommands = lstCommands.iterator();
			
			while(itCommands.hasNext())
			{
				oswDestination.write(itCommands.next());
				oswDestination.write(System.lineSeparator());
				oswDestination.flush();
			}
			

			oswDestination.close();

	
		}
		catch(IOException ex)
		{
			System.out.printf("%s%n", ex);			
		}
		
	}
	
	
	public static void ShowQuestionnaire(CQuestionnaire qnrNew)
	{
		
		ListIterator<CQuestStage> itStage = qnrNew.getStagesIterator();
		
		while(itStage.hasNext()){
			
			CQuestStage stStage = (CQuestStage)itStage.next();
			
			System.out.print(stStage.getID());
			System.out.print(". ");
			System.out.println(stStage.getCaption());
			
			ListIterator<CQuestAnswer> itAnswer = stStage.GetAnswersIterator();
			
			System.out.print("\t(");
			
			while(itAnswer.hasNext()){
				
				CQuestAnswer qaAnswer = (CQuestAnswer)itAnswer.next();
				
				System.out.print(qaAnswer.getCaption() + " | " + Integer.toString( qaAnswer.getValue())+"\t");
			
			}
				
			System.out.print(")");
			
			System.out.print("\n");

		}
		
	
	}
	
}
