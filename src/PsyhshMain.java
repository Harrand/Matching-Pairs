import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PsyhshMain implements Iterable<ColorButton>
{
	public final static int WIDTH = 4, HEIGHT = 4, COLOURS = 4, MAX_PER_COLOUR = 4, BUT_SIZE = 100;
	
	public JFrame frame;
	public JPanel buttonPanel;
	public JLabel top, bottom;
	public boolean player1Turn;
	public int numberOfClicks;
	public int player1Score, player2Score;
	public int firstIButton, secondIButton;
	public boolean hadMatch;
	
	ColorButton[] buttons;
	
	boolean canUseColour(Color c, int curMax)
	{
		// we can use the colour if the number of elements in the list with that colour already is less than MAX_PER_COLOUR
		int same = 0;
		for(int i = 0; i < curMax; i++)
		{
			if(this.buttons[i].front.equals(c))
				same++;
		}
		return same < MAX_PER_COLOUR;
	}
	
	public void initialise()
	{
		this.player1Turn = true;
		this.numberOfClicks = 0;
		this.player1Score = 0;
		this.player2Score = 0;
		this.firstIButton = 0;
		this.secondIButton = 0;
		this.hadMatch = false;
		this.buttons = new ColorButton[WIDTH * HEIGHT];
		
		this.frame = new JFrame("Matching Pairs");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setVisible(true);
		
		this.buttonPanel = new JPanel(new GridLayout(WIDTH, HEIGHT));
		this.buttonPanel.setSize(WIDTH * BUT_SIZE, HEIGHT * BUT_SIZE);
		
		top = new JLabel("Player 1 [Score: " + player1Score + "]");
		top.setBackground(Color.GREEN);
		top.setOpaque(true);
		bottom = new JLabel("Player 2 [Score: " + player2Score + "]");
		bottom.setBackground(Color.RED);
		bottom.setOpaque(true);
		
		this.frame.add(this.top, BorderLayout.NORTH);
		this.frame.add(this.bottom, BorderLayout.SOUTH);
		this.frame.add(this.buttonPanel);
		
		for(int x = 0; x < WIDTH; x++)
		{
			for(int y = 0; y < HEIGHT; y+=2)
			{
				int buttonID = x * WIDTH + y;
				this.buttons[buttonID] = new ColorButton(buttonID, this, BUT_SIZE, BUT_SIZE);
				this.buttons[buttonID + 1] = new ColorButton(buttonID + 1, this, BUT_SIZE, BUT_SIZE);
				Random rand = new Random();
				Color aColour = new Color(rand.nextInt(COLOURS) * 255/COLOURS, rand.nextInt(COLOURS) * 255/COLOURS, rand.nextInt(COLOURS) * 255/COLOURS);
				this.buttons[buttonID].front = aColour;
				this.buttonPanel.add(this.buttons[buttonID]);
				this.buttons[buttonID + 1].front = aColour;
				this.buttonPanel.add(this.buttons[buttonID + 1]);
			}
		}
		// now shuffle them
		for(int x = 0; x < WIDTH; x++)
		{
			for(int y = 1; y < HEIGHT; y++)
			{
				int buttonID = x * WIDTH + y;
				Color curColour = this.buttons[buttonID].front;
				//swap colours with a random other element
				Random rand = new Random();
				int anotherPos = buttonID - rand.nextInt(buttonID);
				this.buttons[buttonID].front = this.buttons[anotherPos].front;
				this.buttons[anotherPos].front = curColour;
			}
		}
		this.frame.pack();
	}
	
	public PsyhshMain()
	{
		this.initialise();
	}
	
	public void performTurn(int iButtonPressed)
	{
		switch(this.numberOfClicks)
		{
		case 0:
			this.firstIButton = iButtonPressed;
			this.buttons[firstIButton].flip(true);
			this.numberOfClicks++;
		break;
		case 1:
			if(this.firstIButton == iButtonPressed)
				return;
			this.secondIButton = iButtonPressed;
			this.buttons[secondIButton].flip(true);
			checkMatch();
			this.numberOfClicks++;
		break;
		case 2:
			for(ColorButton b : this.buttons)
				if(b.active)
					b.flip(false);
			endTurn();
		break;
		}
	}
	
	public boolean gameEnded()
	{
		for(ColorButton b : buttons)
			if(b.active)
				return false;
		return true;
	}
	
	public void showScores()
	{
		System.out.println("END");
		//JDialog.setDefaultLookAndFeelDecorated(true);
		JDialog jd = new JDialog(this.frame, "Game Over!");
		jd.setVisible(true);
		JTextField msg = new JTextField();
		jd.add(msg);
		if(player1Score > player2Score)
		{
			//player 1 wins
			msg.setText("Player 1 Wins!");
		}
		else if(player2Score > player1Score)
		{
			//player 2 wins
			msg.setText("Player 2 Wins!");
		}
		else
		{
			//draw
			msg.setText("It's a tie!");
		}
		msg.setHorizontalAlignment(JTextField.CENTER);
		jd.pack();
		jd.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jd.addWindowListener(new WindowListener()
		{
			// disable default close operation and add custom windowlistener to re-start the game when the dialog box is closed
			@Override
			public void windowOpened(WindowEvent e){}
			@Override
			public void windowClosing(WindowEvent e)
			{
				frame.dispose();
				initialise();
			}
			@Override
			public void windowClosed(WindowEvent e){}
			@Override
			public void windowIconified(WindowEvent e){}
			@Override
			public void windowDeiconified(WindowEvent e){}
			@Override
			public void windowActivated(WindowEvent e){}
			@Override
			public void windowDeactivated(WindowEvent e){}
		});
	}
	public void checkMatch()
	{
		if(this.buttons[this.firstIButton].buttonColour.equals(this.buttons[this.secondIButton].buttonColour))
		{
			this.hadMatch = true;
			//toggle turn again so the player gets another turn
			//this.player1Turn = !this.player1Turn;
			this.buttons[this.firstIButton].finish();
			this.buttons[this.secondIButton].finish();
			if(player1Turn)
				top.setText("Player 1 [Score: " + (player1Score += 2) + "]");
			else
				bottom.setText("Player 2 [Score: " + (player2Score += 2) + "]");
			if(gameEnded())
				showScores();
		}
	}
	
	public void endTurn()
	{
		this.numberOfClicks = 0;
		if(!this.hadMatch)
			this.player1Turn = !this.player1Turn;
		if(this.player1Turn)
		{
			top.setBackground(Color.GREEN);
			bottom.setBackground(Color.RED);
		}
		else
		{
			top.setBackground(Color.RED);
			bottom.setBackground(Color.GREEN);
		}
		for(ColorButton b : this.buttons)
		{
			if(b.active)
				b.flip(false);
		}
		this.firstIButton = 0;
		this.secondIButton = 0;
		this.hadMatch = false;
	}
	
	boolean buttonClicked(int iButton)
	{
		return false;
	}
	public static void main(String[] args)
	{
		PsyhshMain pm = new PsyhshMain();
		for(ColorButton button : pm)
		{
			System.out.println("Iterating across " + button.iButton);
		}
	}
	
	class PsyhshIterator implements Iterator<ColorButton>
	{
		int index = 0;
		@Override
		public boolean hasNext()
		{
			return index < buttons.length;
		}

		@Override
		public ColorButton next()
		{
			return buttons[index++];
		}
		
	}

	@Override
	public Iterator<ColorButton> iterator()
	{
		return new PsyhshIterator();
	}
}
