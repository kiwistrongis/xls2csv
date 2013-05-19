//standard library imports
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Vector;
//poi imports
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Converter {
	//conversion parameters
	public char delimiter;
	public String eol;
	public String encoding;
	public String output_ext;
	//conversion variables
	public File input;
	public File output;
	public InputMode input_mode;
	public OutputMode output_mode;
	public Vector<FilePair> filepairs;
	public Vector<Worker> workers;
	//statistic variables

	//constructors
	public Converter( File input, File output){
		delimiter = '\t';
		eol = "\r\n";
		encoding = "UTF-8";
		output_ext = "csv";
		this.input = input;
		this.output = output;
		filepairs = new Vector<FilePair>();
		workers = new Vector<Worker>();}

	//public member functions
	public void prep()
			throws IOException, FileNotFoundException {
		if( ! input.exists())
			throw new FileNotFoundException(
				"Input file does not exist.");
		FileFilter filter = new FileExtensionFilter("xls");
		//select mode
		if( input.isDirectory()){
			input_mode = InputMode.Directory;
			if( output.exists()){
				if( ! output.isDirectory())
					throw new IOException("I/O type mismatch");}
			else
				if( !output.mkdirs())
					throw new IOException(
						"Failed to create missing output folder.");
			output_mode = OutputMode.Directory;
			//create file-pairs
			Path output_path = output.toPath();
			for( File child : input.listFiles( filter))
				if( ! child.isDirectory()){
					File child_out =
						changeFileExt(
							output_path.resolve(child.getName()).toFile(),
							output_ext);
					filepairs.add(
						new FilePair( child, child_out));}}
		else {
			if( ! filter.accept( input))
				throw new IOException(
					"Input not directory nor xls file");
			input_mode = InputMode.File;
			if( output.exists())
				if( output.isDirectory())
					throw new IOException("I/O type mismatch");
			output_mode = OutputMode.File;
			//create file-pairs
			filepairs.add( new FilePair( input, output));}
		//create worker threads
		for( FilePair pair : filepairs)
			workers.add( new Worker( pair));
		//evaluate stats
		return;}

	public void start(){
		for( Worker worker : workers)
			worker.start();}

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
		//out.println("\u65e5\u672c\u8a9e\u6587\u5b57\u5217");
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
		Directory, File }
	public enum OutputMode {
		StandardOut, File, Directory }

	//static functions
	public static File changeFileExt(
			File original, String ext){
		System.out.println( original);
		Path path = original.toPath();
		String basename = path.getFileName().toString();
		int dot_i = basename.lastIndexOf('.');
		if( dot_i > 0) //ignore unix-style hidden files ( .hidden )
			basename = basename.substring( 0, dot_i + 1) + ext;
		else
			basename = basename + "." + ext;
		return path.getParent().resolve( basename).toFile();}

	//subclasses
	private class Worker extends Thread {
		FilePair files;
		boolean succeeded;
		Exception failure_cause;
		public Worker( FilePair files){
			this.files = files;}
		public void run(){
			try{
				//convert( input, new PrintWriter( System.out));}
				PrintWriter writer = open( files.output);
				convert( files.input, writer);
				writer.close();}
			catch( Exception e){
				e.printStackTrace();}}
	}

	private class FilePair{
		public File input, output;
		public FilePair( File input, File output){
			this.input = input;
			this.output = output;}
	}

	private class FileExtensionFilter implements FileFilter{
		String extension;
		public FileExtensionFilter( String extension){
			this.extension = extension;}
		public boolean accept( File file){
			if( file.isDirectory())
				return false;
			String name = file.getName();
			int dot_i = name.lastIndexOf('.');
			//ignore unix-style hidden files ( .hidden )
			return dot_i > 0 ?
				name.substring( dot_i + 1).equals( extension) :
				false;}
	}
}