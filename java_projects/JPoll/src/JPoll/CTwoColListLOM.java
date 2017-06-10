package JPoll;
import java.awt.*;


public class CTwoColListLOM implements LayoutManager 
{

	private List m_lstLeft;
	private List m_lstRList;
	
	public CTwoColListLOM()
	{
		
	}
	
	public void addLayoutComponent(String name, Component comp)
	{
		
		m_lstLeft = ((CTwoColList)comp).getLeftList();
		m_lstRList = ((CTwoColList)comp).getRightList();

	}
	
	public void removeLayoutComponent(Component comp)
	{
		if (m_lstLeft == comp) 
		{
			m_lstLeft = null;
		}

		if (m_lstRList == comp) 
		{
			m_lstRList = null;
		}
	}
	
	public Dimension preferredLayoutSize(Container parent)
	{		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double dblWidth = screenSize.getWidth()/4;
		double dblHeight = screenSize.getHeight()/4;
		
		Dimension dimResult = new Dimension((int)dblWidth, (int)dblHeight);
		
		return dimResult;
	}
	
	public Dimension minimumLayoutSize(Container parent)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double dblWidth = screenSize.getWidth()/6;
		double dblHeight = screenSize.getHeight()/6;
		
		Dimension dimResult = new Dimension((int)dblWidth, (int)dblHeight);
		
		return dimResult;

	}
	
	public void layoutContainer(Container parent)
	{

		
		Dimension dimParent = parent.getSize();
		
		if (m_lstLeft != null)
		{
			m_lstLeft.setBounds(0, 
								0, 
								(int)(dimParent.getWidth() * 0.7), 
								(int)(dimParent.getHeight()));
		}
		
		if (m_lstRList != null)
		{
			m_lstRList.setBounds((int)(dimParent.getWidth() * 0.7), 
								0, 
								(int)(dimParent.getWidth() * 0.3), 
								(int)(dimParent.getHeight()));
		}


	}
}
