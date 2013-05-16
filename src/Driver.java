//standard library imports
import java.io.File;
import java.io.PrintStream;

public class Driver {
	public static void main( String[] args){
		System.out.println("asdf");
		System.out.println(
			new File("/usr/bin/fdsa").getName());
		System.out.println(
			new File("/usr/bin/fdsa").getParent());
		Converter converter;

		//handle args
		if( args.length == 0){
			File dir = new File( System.getProperty("user.dir"));
			converter = new Converter( dir, dir);}
		else if( args.length == 1){
			//setup input
			File file = new File( args[0]);
			if( file.isDirectory())
				converter = new Converter( file, file);
			else
				converter = new Converter( file,
					new File(
						Converter.changeFileExt( args[0], "csv")));}
			//setup output
		else if( args.length >= 2){
			//setup input
			File input = new File( args[0]);}}
}

