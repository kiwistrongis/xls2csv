//standard library imports
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
//poi imports
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Converter {
	//fields
	File input;
	File output;
	InputMode input_mode;
	OutputMode output_mode;

	//constructors
	public Converter( File input){
		this.input = input;
		this.output = output;
		input_mode = input.isDirectory() ?
			InputMode.Multi : InputMode.Single;
		output_mode = OutputMode.StandardOut;}
	public Converter( File input, File output){
		this.input = input;
		this.output = output;
		input_mode = input.isDirectory() ?
			InputMode.Multi : InputMode.Single;
		output_mode = output.isDirectory() ?
			OutputMode.MultipleFiles : OutputMode.SingleFile;}

	//enum definitions
	public enum InputMode {
		Multi, Single }
	public enum OutputMode {
		StandardOut, SingleFile, MultipleFiles }

	//functions
	public static String changeFileExt(
			String filename, String ext){
		File path = new File( filename);
		String basename = path.getName();
		int dot_i = basename.lastIndexOf('.');
		if( dot_i > 0) //ignore unix-style hidden files ( .hidden )
			basename = basename.substring( 0, dot_i + 1) + ext;
		else
			basename = basename + "." + ext;
		return path.getParent() + basename;}

}