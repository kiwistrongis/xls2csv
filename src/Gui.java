//standard library imports
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.	swing.*;
import javax.imageio.ImageIO;

public class Gui extends JFrame{
	// minor fields
	public String title;
	public String icon_filename;
	public int window_x;
	public int window_y;
	public int window_width;
	public int window_height;
	public boolean options_enabled;
	//reference to this top-level window
	private JFrame top;
	//components
	public MyCardLayout cards;
	public Container frame;
	public JPanel panel;
	public OptionsPanel optionsPanel;
	public ProgressPanel progressPanel;
	public StartPanel startPanel;
	public JFileChooser fileChooser;


	public Gui(){
		//default fields
		title = "Xls2Csv Converter";
		icon_filename = "resources/icon.png";
		window_x = 0;
		window_y = 0;
		window_width = 500;
		window_height = 200;
		options_enabled = false;
		//this reference
		top = this;
		//components
		cards = null;
		frame = null;
		panel = null;
		optionsPanel = null;
		progressPanel = null;
		startPanel = null;
		fileChooser = new JFileChooser();}

	public void setup(){
		//Window setup
		setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// set icon
		try{
			this.setIconImage(
				ImageIO.read(
					new File(icon_filename)));}
		catch(Exception e){ System.out.println(e);}

		//add content
		// get frame
		frame = getContentPane();
		// main panel
		cards =  new MyCardLayout();
		panel = new JPanel( cards);
		// start panel
		startPanel = new StartPanel();
		panel.add( startPanel, startPanel.NAME);
		// options panel
		optionsPanel = new OptionsPanel();
		panel.add( optionsPanel, optionsPanel.NAME);
		// progress panel
		progressPanel = new ProgressPanel();
		panel.add( progressPanel, progressPanel.NAME);
		// show start panel
		frame.add( panel);

		//done
		startPanel.switchTo();
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		System.out.println("Done Loading GUI");
		this.setVisible(true);}

	//functions
	public void close(){
		Toolkit.getDefaultToolkit().
			getSystemEventQueue().postEvent(
			new WindowEvent(this, WindowEvent.WINDOW_CLOSING));}

	//sub-classes
	protected class MyCardLayout extends CardLayout {
		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Component current = findCurrentComponent(parent);
			if (current != null) {
				Insets insets = parent.getInsets();
				Dimension pref = current.getPreferredSize();
				pref.width += insets.left + insets.right;
				pref.height += insets.top + insets.bottom;
				return pref;}
			else return super.preferredLayoutSize(parent);}

		public Component findCurrentComponent(Container parent) {
			for (Component comp : parent.getComponents())
				if (comp.isVisible())
					return comp;
			return null;}
	}
	
	protected class CustomJPanel extends JPanel {
		public String NAME;
		public JButton defaultButton;
		protected CustomJPanel( String NAME){
			this.NAME = NAME;
			defaultButton = null;}
		public void switchTo(){
			top.setVisible(false);
			cards.show( panel, NAME);
			top.setMinimumSize(null);
			top.pack();
			top.setMinimumSize( top.getSize());
			top.getRootPane().setDefaultButton( defaultButton);
			top.setLocationRelativeTo(null);
			top.setVisible(true);}
	}
	protected class ProgressPanel extends CustomJPanel {
		JProgressBar progressBar;
		JButton button;

		public ProgressPanel(){
			super("Progress");
			GridBagLayout layout = new GridBagLayout();
			this.setLayout( layout);
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets( 2, 2, 2, 2);

			//progress bar
			progressBar = new JProgressBar(0, 10);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			// add progress bar with constraints
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.weightx = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			this.add( progressBar, constraints);
			//reset constraints
			constraints.weightx = 0;
			constraints.fill = GridBagConstraints.NONE;

			//button
			button = new JButton("Done");
			button.setVerticalTextPosition( AbstractButton.BOTTOM);
			button.setHorizontalTextPosition( AbstractButton.CENTER);
			button.setEnabled(false);
			button.setMnemonic( KeyEvent.VK_D);
			// add button with constraints
			constraints.gridx = 1;
			this.add( button, constraints);

			//set default button
			defaultButton = button;}
	}
	protected class StartPanel extends CustomJPanel {
		JLabel file_count;
		FileSelectionRow input;
		FileSelectionRow output;
		JButton close;
		JButton options;
		JButton start;

		public StartPanel(){
			super("Start");
			GridBagLayout layout = new GridBagLayout();
			this.setLayout( layout);
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets( 1, 1, 1, 1);

			//input row
			input = new FileSelectionRow("Input");
			// label
			constraints.gridx = 0;
			constraints.gridy = 0;
			this.add( input.label, constraints);
			// text field
			constraints.gridx = 1;
			constraints.weightx = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			this.add( input.text, constraints);
			//reset constraints
			constraints.weightx = 0;
			constraints.fill = GridBagConstraints.NONE;
			// button
			constraints.gridx = 2;
			this.add( input.button, constraints);

			//output row
			output = new FileSelectionRow("Output");
			// label
			constraints.gridx = 0;
			constraints.gridy = 1;
			this.add( output.label, constraints);
			// text field
			constraints.gridx = 1;
			constraints.weightx = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			this.add( output.text, constraints);
			//reset constraints
			constraints.weightx = 0;
			constraints.fill = GridBagConstraints.NONE;
			// button
			constraints.gridx = 2;
			this.add( output.button, constraints);

			//button row
			JPanel buttons = new JPanel();
			close = new JButton("Close");
			close.setMnemonic( KeyEvent.VK_C);
			options = new JButton("Options");
			options.setEnabled( options_enabled);
			options.setVisible( options_enabled);
			options.setMnemonic( KeyEvent.VK_O);
			start = new JButton("Start");
			start.setMnemonic( KeyEvent.VK_S);
			buttons.add( close);
			buttons.add( options);
			buttons.add( start);
			// add button row
			constraints.gridx = 0;
			constraints.gridy = 3;
			constraints.gridwidth = 3;
			this.add( buttons, constraints);

			//set default button
			defaultButton = start;}

		protected class FileSelectionRow {
			JLabel label;
			JTextField text;
			JButton button;
			public FileSelectionRow(String labelText){
				//label
				label = new JLabel( labelText);
				//text field
				text = new JTextField(20);
				//button
				button = new JButton("...");
				Dimension button_dim = button.getMinimumSize();
				button_dim.width = button_dim.height;
				button.setMinimumSize(button_dim);
				button.setPreferredSize(button_dim);}
		}
	}
	protected class OptionsPanel extends CustomJPanel {
		public OptionsPanel(){
			super("Options");}
	}
}