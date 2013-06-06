//standard library imports
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Document;

/** Controller sub-class
 * handles all user input
 **/
public class Controller extends KeyAdapter
		implements ActionListener, Observer, DocumentListener {
	//constant fields
	public final static String
		sp_start_ac = "sp_start_ac",
		sp_close_ac = "sp_close_ac",
		sp_options_ac = "sp_options_ac",
		sp_input_text_ac = "sp_input_text_ac",
		sp_input_button_ac = "sp_input_button_ac",
		sp_output_text_ac = "sp_output_text_ac",
		sp_output_button_ac = "sp_output_button_ac",
		op_toCSV_ac = "op_toCSV_ac",
		op_toXLS_ac = "op_toXLS_ac",
		op_cancel_ac = "op_cancel_ac",
		op_done_ac = "op_done_ac",
		pp_done_ac = "pp_done_ac",
		documentContainer_property = "documentContainer_property";
	// major objects
	Gui gui;
	Converter converter;
	// minor fields

	public Controller(){
		gui = null;
		converter = null;}

	public void listenTo(Converter converter){
		//listen to converter
		this.converter = converter;
		converter.addObserver(this);}

	public void listenTo( Gui gui){
		//listen to gui
		this.gui = gui;

		// start panel
		this.listenTo( gui.startPanel.start, sp_start_ac);
		this.listenTo( gui.startPanel.close, sp_close_ac);
		this.listenTo( gui.startPanel.options, sp_options_ac);
		this.listenTo( gui.startPanel.input.text, sp_input_text_ac);
		this.listenTo( gui.startPanel.input.button, sp_input_button_ac);
		this.listenTo( gui.startPanel.output.text, sp_output_text_ac);
		this.listenTo( gui.startPanel.output.button, sp_output_button_ac);

		// options panel
		gui.optionsPanel.mode_toCSV.setSelected( true);
		this.listenTo( gui.optionsPanel.mode_toCSV, op_toCSV_ac);
		this.listenTo( gui.optionsPanel.mode_toXLS, op_toXLS_ac);
		this.listenTo( gui.optionsPanel.cancel, op_cancel_ac);
		this.listenTo( gui.optionsPanel.done, op_done_ac);

		// progress panel
		this.listenTo( gui.progressPanel.progressBar);
		this.listenTo( gui.progressPanel.button, pp_done_ac);

		//update gui with converter's files
		setInput( converter.input);
		setOutput( converter.output);
		updateFileCount();}

	public void listenTo( JButton button, String ac){
		button.setActionCommand( ac);
		button.addActionListener( this);
		button.addKeyListener( this);}

	public void listenTo( JRadioButton button, String ac){
		button.setActionCommand( ac);
		button.addActionListener( this);
		button.addKeyListener( this);}

	public void listenTo( JTextField field, String ac){
		field.setActionCommand( ac);
		field.addActionListener( this);
		field.addKeyListener( this);
		Document doc = field.getDocument();
		doc.putProperty( documentContainer_property, ac);
		doc.addDocumentListener( this);}

	public void listenTo( JProgressBar bar){
		bar.addKeyListener( this);}
	
	//handles
	public void update(Observable o, Object arg){
		if( converter!= null)
			synchronized( converter.statslock){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						gui.progressPanel.progressBar.setValue(
							converter.completed);}});
				if( converter.done)
					gui.progressPanel.button.setEnabled( true);}}

	public void actionPerformed( ActionEvent event){
		String ac = event.getActionCommand();
		System.out.println(ac);
		switch( ac){
			//start panel
			case sp_start_ac:{
				synchronized( converter.statslock){
					gui.progressPanel.progressBar.setMaximum(
						converter.total);
					gui.progressPanel.progressBar.setValue(
						converter.completed);
					gui.progressPanel.switchTo();}
				converter.start();
				break;}
			case sp_close_ac:{
				gui.close();
				break;}
			case sp_options_ac:{
				gui.optionsPanel.switchTo();
				break;}
			case sp_input_text_ac:{
				break;}
			case sp_input_button_ac:{
				gui.fileChooser.setCurrentDirectory(
					converter.input.isDirectory() ?
						converter.input :
						converter.input.getParentFile());
				File selection = gui.getFile(
					"Select Input Directory");
				if( selection != null)
					setInput( selection);
				break;}
			case sp_output_text_ac:{
				break;}
			case sp_output_button_ac:{
				gui.fileChooser.setCurrentDirectory(
					converter.output.isDirectory() ?
						converter.output :
						converter.output.getParentFile());
				File selection = gui.getFile(
					"Select Output Directory");
				if( selection != null)
					setOutput( selection);
				break;}

			//progress panel
			case pp_done_ac:{
				if( converter.done){
					gui.startPanel.switchTo();
					try{ converter.prep();}
					catch( java.io.IOException exception){
						exception.printStackTrace();}
					gui.startPanel.start.setEnabled( converter.ready);}
				break;}

			//options panel
			case op_toCSV_ac:{
				converter.direction = Converter.Direction.toCSV;
				updateFileCount();
				break;}
			case op_toXLS_ac:{
				converter.direction = Converter.Direction.toXLS;
				updateFileCount();
				break;}
			case op_cancel_ac:{
				break;}
			case op_done_ac:{
				gui.startPanel.switchTo();
				break;}
			//say what?
			default:break;}}

	public void keyPressed(KeyEvent e){
		//System.out.println( e.getKeyCode());
		switch( e.getKeyCode()){
			case KeyEvent.VK_ESCAPE:{
				synchronized( converter.statslock){
					if( converter.done || ! converter.started)
						gui.close();}
				break;}
			case KeyEvent.VK_ENTER:{
				updateFileCount();
				break;}
			default: break;}}

	public void changedUpdate(DocumentEvent event){
		documentUpdate( event);}
	public void insertUpdate(DocumentEvent event){
		documentUpdate( event);}
	public void removeUpdate(DocumentEvent event){
		documentUpdate( event);}
	public void documentUpdate( DocumentEvent event){
		Document doc = event.getDocument();
		String field_ac = (String) doc.getProperty(
			documentContainer_property);
		textFieldUpdate( field_ac);}

	public void textFieldUpdate( String ac){
		String content = null;
		switch( ac){
			case sp_input_text_ac:{
				content = gui.startPanel.input.text.getText();
				setInput( content);
				break;}
			case sp_output_text_ac:{
				content = gui.startPanel.output.text.getText();
				setOutput( content);
				break;}
			default:break;}}
		//System.out.printf("%s: %s\n", ac, content);}

	//private functions
	private void setInput(String text){
		File input = new File( text);
		converter.input = input;
		updateFileCount();}
	private void setInput(File file){
		converter.input = file;
		gui.startPanel.input.text.setText(
			file.getAbsolutePath());
		updateFileCount();}

	private void setOutput(String text){
		File output = new File( text);
		converter.output = output;
		updateFileCount();}
	private void setOutput(File file){
		converter.output = file;
		gui.startPanel.output.text.setText(
			file.getAbsolutePath());
		updateFileCount();}

	private void updateFileCount(){
		converter.ready = false;
		if( converter.input.exists())
			try{
				converter.prep();
				int count = converter.filepairs.size();
				gui.startPanel.fileCount.setCount(count);
				converter.ready = converter.ready && count > 0;
				gui.startPanel.start.setEnabled( converter.ready);}
			catch( FileNotFoundException e){
				gui.startPanel.fileCount.setCount(0);
				gui.startPanel.start.setEnabled( false);}
			catch( IOException e){
				gui.startPanel.fileCount.setCount(
					converter.input.listFiles( converter.filter)
						.length);
				gui.startPanel.start.setEnabled( false);}
		else {
			gui.startPanel.fileCount.setCount( 0);
			gui.startPanel.start.setEnabled( false);}}
}
