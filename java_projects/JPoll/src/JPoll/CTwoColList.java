package JPoll;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CTwoColList extends Panel implements ItemListener
{
	private static final long serialVersionUID = 20150928;
	
	private List m_lstLeft;
	private List m_lstRight;

	public CTwoColList()
	{
		CTwoColListLOM lmTwoCol = new CTwoColListLOM();
		setLayout(lmTwoCol);
		
		m_lstLeft = new List();
		m_lstRight = new List();
		
		
		add(m_lstLeft);
		add(m_lstRight);
		
		setBackground(Color.gray);
		lmTwoCol.addLayoutComponent("", this);
		
		m_lstLeft.addItemListener(this);
		m_lstRight.addItemListener(this);
		
		
	}
	
	public List getLeftList()
	{
		return m_lstLeft;
	}
	
	public List getRightList()
	{
		return m_lstRight;
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() == m_lstLeft)
		{
			m_lstRight.select(m_lstLeft.getSelectedIndex());
		}

		if (e.getSource() == m_lstRight)
		{
			m_lstLeft.select(m_lstRight.getSelectedIndex());
		}

	}
	
	public void RemoveAll()
	{
		m_lstLeft.removeAll();
		m_lstRight.removeAll();
		
	}
	
	public void AddItem(String sAlternative, String sNextQuestion)
	{
		m_lstLeft.add(sAlternative);
		m_lstRight.add(sNextQuestion);
		
	}
	
	public void AddItem(String sAlternative, String sNextQuestion, int nPosition)
	{
		m_lstLeft.add(sAlternative, nPosition);
		m_lstRight.add(sNextQuestion, nPosition);
		
	}
	
	public void RemoveItem(int nIndex)
	{
		m_lstLeft.remove(nIndex);
		m_lstRight.remove(nIndex);
	}
	
	public String getSelectedItem()
	{
		return m_lstLeft.getSelectedItem();
	}
	
	public String getSelectedItemNextStage()
	{
		return m_lstRight.getSelectedItem();
	}

	public int getSelectedIndex()
	{
		return m_lstLeft.getSelectedIndex();
	}
	
	public String getItemCaption(int nItemIndex)
	{
		return m_lstLeft.getItem(nItemIndex);
	}
	
	public String getItemNextStage(int nItemIndex)
	{
		return m_lstRight.getItem(nItemIndex);
	}

}
