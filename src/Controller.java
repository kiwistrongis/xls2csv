//standard library imports
import java.awt.*;
import java.awt.event.*;
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
		sp_start_ac = "start",
		sp_close_ac = "close",
		sp_options_ac = "options",
		sp_input_text_ac = "input_text",
		sp_input_button_ac = "input_button",
		sp_output_text_ac = "output_text",
		sp_output_button_ac = "output_button",
		pp_done_ac = "done",
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
		// progress panel
		this.listenTo( gui.progressPanel.progressBar);
		this.listenTo( gui.progressPanel.button, pp_done_ac);}
		// options panel

	public void listenTo( JButton button, String ac){
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
				break;}
			case sp_input_text_ac:{
				break;}
			case sp_input_button_ac:{
				int result = gui.fileChooser.showOpenDialog( gui);
				switch( result){
					case JFileChooser.CANCEL_OPTION:
						System.out.println("File selection cancelled");
						break;
					case JFileChooser.APPROVE_OPTION:
						System.out.println("File chosen");
						break;
					case JFileChooser.ERROR_OPTION:
						System.out.println("File selection error");
						break;
					default: break;}
				break;}
			case sp_output_text_ac:{
				break;}
			case sp_output_button_ac:{
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
			default:break;}}

	public void keyPressed(KeyEvent e){
		//System.out.println( e.getKeyCode());
		switch( e.getKeyCode()){
			case KeyEvent.VK_ESCAPE:{
				synchronized( converter.statslock){
					if( converter.done || ! converter.started)
						gui.close();}
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
			case sp_input_text_ac:
				content = gui.startPanel.input.text.getText();
				break;
			case sp_output_text_ac:
				content = gui.startPanel.output.text.getText();
				break;
			default:break;}
		System.out.printf("%s: %s\n", ac, content);}
}
