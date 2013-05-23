//standard library imports
import java.io.File;
import java.nio.file.Path;

public class Driver {
	public static void main( String[] args){
		//local variables
		boolean nogui = false;
		Path program_path;
		File input;
		File output;
		Converter converter;
		final Configuration config = new Configuration();
		final Controller controller = new Controller();

		//find program path
		//handle args
		if( args.length == 0){
			File dir = new File( System.getProperty("user.dir"));
			input = dir;
			output = dir;}
		else if( args.length == 1){
			File file = new File( args[0]);
			if( file.isDirectory())
				input = output = file;
			else
				input = file;
				output = Converter.changeFileExt( file, "csv");}
		else{
				input = new File( args[0]);
				output = new File( args[1]);}

		//initialize converter
		converter = new Converter( input, output);
		controller.listenTo( converter);

		//locate config file
		File config_file = new File("config.ini");
		//load configuration from file
		try{
			config.open( config_file);}
		catch( Exception e){
			System.out.println("Configuration loading failed");
			e.printStackTrace();}

		//prepare converter
		try{
			if( config != null)
				config.load( converter);
			converter.prep();}
		catch( Exception e){
			e.printStackTrace();}

		//start gui
		config.loadGuiSettings();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui gui = new Gui();
				config.load( gui);
				gui.setup();
				controller.listenTo( gui);}});

		//start
		if( nogui)
			converter.start();}
}
