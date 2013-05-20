//standard library imports
import java.io.File;

public class Driver {
	public static void main( String[] args){
		//local variables
		Converter converter;
		Configuration config;

		//handle args
		if( args.length == 0){
			File dir = new File( System.getProperty("user.dir"));
			converter = new Converter( dir, dir);}
		else if( args.length == 1){
			File file = new File( args[0]);
			if( file.isDirectory())
				converter = new Converter( file, file);
			else
				converter = new Converter( file,
					Converter.changeFileExt( file, "csv"));}
		else
			converter = new Converter(
				new File( args[0]),
				new File( args[1]));

		//load configuration from file
		try{
			config = new Configuration( new File("config.ini"));
			config.load( converter);}
		catch( Exception e){
			e.printStackTrace();}

		//prepare and print stats
		try{
			converter.prep();}
		catch( Exception e){
			e.printStackTrace();}
		converter.start();}
}
