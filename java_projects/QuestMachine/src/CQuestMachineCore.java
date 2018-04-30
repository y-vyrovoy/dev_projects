import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;


public class CQuestMachineCore{

	public static void main(String[] args )
	{

		File fileInput;
		fileInput = CUtils.ChooseFileDlg("C:\\Users\\Yura Vyrovoy\\Desktop");
		//fileInput = CUtils.ChooseFileDlg("C:\\Users\\VYROVOY\\Desktop");
		
		
		if (fileInput.exists() == false) 
		{
			System.out.println("No file has been choosen.");
			return;
		}

		System.out.println(fileInput.getAbsoluteFile()); // debug option
	
		CQuestionnaire qnrNew = InitQuestionnaireFromCSV(fileInput);
		if (qnrNew == null)
		{	
			System.out.println("Can't read questionnaire from selected file.");
			return;
		}

		ShowQuestionnaire(qnrNew);
		
		Path p = fileInput.toPath();
		p = p.getParent();
		
		SaveQuestionnaireToCSV(qnrNew, p);
	}

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

			CQuestAnswer qaNew  = new CQuestAnswer();
			CQuestion qstNew = new CQuestion();
			CQuestStage stgNew = new CQuestStage();	
			
			Iterator<String> itCurrentLine = itLine.next().iterator();
			
			// filling questionnaire stage info 
			stgNew.setID(itCurrentLine.next());
			
			
			stgNew.setStageType(itCurrentLine.next().equals("S")?
									CQuestStage.enmStageType.ST_SINGLE_ANSWER:
									CQuestStage.enmStageType.ST_MULTY_ANSWER);
			
			//system tag. unused
			itCurrentLine.next(); 

			
			while (itCurrentLine.hasNext() == true)
			{
				
				// filling questionnaire stage info
				qaNew.setCaption(itCurrentLine.next());
				
				String sNextStageID = "";
				try
				{
					int nNextStage = Integer.valueOf(itCurrentLine.next());
					
					Iterator<List<String>> itTmp = lstCommands.iterator();
					
					
					for(int iLines = 1; iLines <= nNextStage; iLines++ )
					{
						if (itTmp.hasNext() == true)
						{
							sNextStageID = itTmp.next().iterator().next();
						}
						else
						{
							sNextStageID = "";
						}
					}
				}
				catch(NumberFormatException ex)
				{
					System.out.println("Wrong next_stage_number:");
					System.out.println(ex.getLocalizedMessage());
				}
				
				qaNew.setNextStageID(sNextStageID);
			
				qstNew.AddItem(qaNew);
			}
			
			
			qstNew.setID(stgNew.getID());
			stgNew.AddQuestion(qstNew);
			
			
			qnrNew.AddStage(stgNew);
		}

		return qnrNew;
	}	
	
	public static void SaveQuestionnaireToCSV(CQuestionnaire qnrSource, Path pDestinationFolder)
	{
		
		// If this path doesn't exist or is not writable
		if (Files.exists(pDestinationFolder) &&
				Files.isDirectory(pDestinationFolder) &&
				Files.isWritable(pDestinationFolder) == false) 
		{
			return;
		}
		
		
		List<String> lstCommands = new ArrayList<String>();
		
		ListIterator<CQuestStage> itStages = qnrSource.getStagesIterator();
		
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
				sCommand = "M";
			}
			sCommand = sCommand + ",";
			
			// Unused tag
			sCommand = sCommand + "0";
			
			CQuestion qstQuestion = qstStage.getQuestionsIterator().next();
			
			Iterator<CQuestAnswer> itAnswer = qstQuestion.GetAnswersIterator();
			
			while(itAnswer.hasNext() == true)
			{
				CQuestAnswer qaAnswer = itAnswer.next();
				
				sCommand = sCommand + "," + qaAnswer.getCaption();
				sCommand = sCommand + "," + qnrSource.getStageNumberByID(qaAnswer.getNextStageID());
			}
			
			
			lstCommands.add(sCommand);
		}
		
		Path pDestFile = Paths.get(pDestinationFolder.toAbsolutePath().toString(), "Test Quest Result.csv");
		
		try
		{

			byte [] abBomUTF8 = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};
			
			OutputStream osDestination = Files.newOutputStream(pDestFile, StandardOpenOption.CREATE, 
																				StandardOpenOption.TRUNCATE_EXISTING, 
																				StandardOpenOption.WRITE);
			
			osDestination.write(abBomUTF8);
			osDestination.flush();
			
			OutputStreamWriter oswDestination = new OutputStreamWriter(osDestination, StandardCharsets.UTF_8);
			
			Iterator<String> itCommands = lstCommands.iterator();
			
			while(itCommands.hasNext())
			{
				oswDestination.write(itCommands.next());
				oswDestination.write(System.lineSeparator());
				oswDestination.flush();
			}
			

			oswDestination.close();
			
			
			


/*			
			Files.write(pDestFile, abBomUTF8, StandardOpenOption.CREATE, 
												StandardOpenOption.TRUNCATE_EXISTING, 
												StandardOpenOption.WRITE);
								
			Files.write(pDestFile, lstCommands, 
						StandardCharsets.UTF_8, 
						StandardOpenOption.APPEND);
			
			
*/
	
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
			
			ListIterator<CQuestion> itQuestion = stStage.getQuestionsIterator();
			
			while(itQuestion.hasNext()){
				
				CQuestion qstQuest = (CQuestion)itQuestion.next();
				
				System.out.print("\t");
				System.out.print(qstQuest.getID());
				System.out.print(". ");
				System.out.println(qstQuest.getCaption());
				
				ListIterator<CQuestAnswer> itAnswer = qstQuest.GetAnswersIterator();
				
				System.out.print("\t(");
				
				while(itAnswer.hasNext()){
					
					CQuestAnswer qaAnswer = (CQuestAnswer)itAnswer.next();
					
					System.out.print(qaAnswer.getCaption() + " | " + Integer.toString( qaAnswer.getValue())+"\t");
				
				}
				
				System.out.print(")");
				
			}
			
			System.out.print("\n");

		}
		
	
	}
	
}
