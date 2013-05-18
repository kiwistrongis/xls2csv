//standard library imports
import java.io.File;
import java.io.PrintStream;

public class Driver {
	public static void main( String[] args){
		System.out.printf("%s,%s\n", args[0], args[1]);
		//local variables
		Converter converter;

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

		//prepare and print stats
		converter.prep();
		converter.start();}
}
