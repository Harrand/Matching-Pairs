import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class ColorButton extends JButton implements ActionListener
{
	public int iButton;
	public PsyhshMain pm;
	public static final Color BACK = Color.GRAY, DONE = Color.BLACK;
	public Color front, buttonColour;
	public boolean active;
	
	public ColorButton(int iButton, PsyhshMain pm, int width, int height)
	{
		this.active = true;
		this.iButton = iButton;
		this.pm = pm;
		this.setPreferredSize(new Dimension(width, height));
		this.buttonColour = ColorButton.BACK;
		this.setVisible(true);
		this.addActionListener(this);
	}
	
	public void flip(boolean showColour)
	{
		this.buttonColour = showColour ? front : ColorButton.BACK;
		this.repaint();
	}
	
	public void finish()
	{
		this.buttonColour = ColorButton.DONE;
		this.repaint();
		this.active = false;
	}
	
	public void setFrontColour(Color col)
	{
		this.front = col;
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(this.buttonColour);
		g.fillRect(0, 0, (int)this.getSize().getWidth(), (int)this.getSize().getHeight());
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(!this.active)
			return;
		this.pm.performTurn(this.iButton);
	}
}
