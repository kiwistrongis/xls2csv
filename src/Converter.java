//standard library imports
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
//poi imports
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Converter {
	//fields
	char delimiter;
	String eol;
	String encoding;
	File input;
	File output;
	InputMode input_mode;
	OutputMode output_mode;

	//constructors
	public Converter( File input, File output){
		delimiter = '\t';
		eol = "\r\n";
		encoding = "UTF-8";
		this.input = input;
		this.output = output;
		input_mode = input.isDirectory() ?
			InputMode.Multi : InputMode.Single;
		output_mode = output.isDirectory() ?
			OutputMode.Directory : OutputMode.File;}

	//public member functions
	public void prep(){
		//create file-pairs
		//create worker threads
		//evaluate stats
		return;}

	public void start(){
		try{
			//convert( input, new PrintWriter( System.out));}
			PrintWriter writer = open( output);
			convert( input, writer);
			writer.close();}
		catch( Exception e){
			e.printStackTrace();}}

	//private member functions
	private PrintWriter open( File file)
			throws FileNotFoundException {
		return new PrintWriter(
			new BufferedWriter(
				new OutputStreamWriter(
					new FileOutputStream( file),
					Charset.forName( encoding).newEncoder())));}

	private void convert( File in, PrintWriter out )
			throws FileNotFoundException, IOException {
		HSSFWorkbook workbook = new HSSFWorkbook(
			new FileInputStream( in));
		//for( HSSFSheet sheet : workbook._sheets)
		HSSFSheet sheet = workbook.getSheetAt(0);
		int columnCount = 0;
		for( Row row : sheet)
			if( row.getLastCellNum() > columnCount)
				columnCount = row.getLastCellNum();
		out.println("\u65e5\u672c\u8a9e\u6587\u5b57\u5217");
		for( Row row : sheet){
			Iterator<Cell> i = row.cellIterator();
			int cellCount = 0;
			while( i.hasNext()){
				Cell cell = i.next();
				if( i.hasNext())
					out.printf("%s%c", cell, delimiter);
				else
					out.printf("%s", cell);
				cellCount++;}
			while( cellCount++ < columnCount)
				out.print( delimiter);
			out.print(eol);
			out.flush();}}

	//enum definitions
	public enum InputMode {
		Multi, Single }
	public enum OutputMode {
		StandardOut, File, Directory }

	//static functions
	public static File changeFileExt(
			File original, String ext){
		Path path = original.toPath();
		String basename = path.getFileName().toString();
		int dot_i = basename.lastIndexOf('.');
		if( dot_i > 0) //ignore unix-style hidden files ( .hidden )
			basename = basename.substring( 0, dot_i + 1) + ext;
		else
			basename = basename + "." + ext;
		return path.getParent().resolve( basename).toFile();}

	//subclasses
	private class Worker extends Thread {}

	private class FilePair{
		public File input, output;
		public FilePair( File input, File output){
			this.input = input;
			this.output = output;}
	}
}