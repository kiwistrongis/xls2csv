//standard library imports
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Observable;
import java.util.Scanner;
import java.util.Vector;
//poi imports
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Converter extends Observable {
	//conversion parameters
	public char delimiter;
	public String eol;
	public String encoding;
	public String output_ext;
	public boolean workerLimitEnabled;
	public int maxWorkerCount;
	public Integer nextWorker;
	public Direction direction;
	//conversion variables
	public File input;
	public File output;
	public InputMode input_mode;
	public OutputMode output_mode;
	public Vector<FilePair> filepairs;
	public Vector<Worker> workers;
	//statistic variables
	public Object statslock;
	public boolean ready;
	public boolean started;
	public boolean done;
	public int completed;
	public int succeeded;
	public int failed;
	public int total;
	public Vector<Exception> failureCauses;
	//misc vars
	public PrintStream log;
	public FileFilter filter;

	//constructors
	public Converter( File input, File output){
		//convertion parameters
		delimiter = '\t';
		eol = "\r\n";
		encoding = "UTF-8";
		workerLimitEnabled = false;
		maxWorkerCount = 0;
		//conversion variables
		this.input = input;
		this.output = output;
		//statistic variables
		statslock = new Object();
		//misc vars
		direction = Direction.toCSV;
		log = System.out;}

	//public member functions
	public void prep()
			throws IOException, FileNotFoundException {
		//reinitialization
		ready = false;
		started = false;
		done = false;
		completed = 0;
		succeeded = 0;
		failed = 0;
		nextWorker = 0;
		failureCauses = new Vector<Exception>();
		filepairs = new Vector<FilePair>();
		workers = new Vector<Worker>();
		//assert input file existance
		if( ! input.exists())
			throw new FileNotFoundException(
				"Input file does not exist.");
		//select mode
		if( input.isDirectory()){
			input_mode = InputMode.Directory;
			if( output.exists()){
				if( ! output.isDirectory())
					throw new IOException("I/O type mismatch");}
			/*else
				if( !output.mkdirs())
					throw new IOException(
						"Failed to create missing output folder.");*/
			output_mode = OutputMode.Directory;
			//create file filter
			switch( direction){
				default:
				case toCSV:
					filter = new FileExtensionFilter("xls");
					output_ext = "csv";
					break;
				case toXLS:
					filter = new FileExtensionFilter("csv");
					output_ext = "xls";
					break;}

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
				throw new FileNotFoundException(
					"Input not directory nor correct file type");
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
		//reset stats
		total = workers.size();
		ready = true;
		return;}

	public void start(){
		if( output_mode == OutputMode.Directory)
			output.mkdirs();
		synchronized( statslock){
			started = true;}
		synchronized( nextWorker){
			if( workerLimitEnabled)
				while( nextWorker <
						Math.min( maxWorkerCount, workers.size()))
					workers.get( nextWorker++).start();
			else
				while( nextWorker < workers.size())
					workers.get( nextWorker++).start();}}

	//private member functions
	private PrintWriter open( File file)
			throws FileNotFoundException {
		return new PrintWriter(
			new BufferedWriter(
				new OutputStreamWriter(
					new FileOutputStream( file),
					Charset.forName( encoding).newEncoder())));}

	private void convertToCSV( File in, File out )
			throws FileNotFoundException, IOException {
		HSSFWorkbook workbook = new HSSFWorkbook(
			new FileInputStream( in));
		PrintWriter writer = open( out);
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
					writer.printf("%s%c", cell, delimiter);
				else
					writer.printf("%s", cell);
				cellCount++;}
			while( cellCount++ < columnCount)
				writer.print( delimiter);
			writer.print(eol);
			writer.flush();
			writer.close();}}

	private void convertToXLS( File in, File out )
			throws FileNotFoundException, IOException {
		//setup
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Sheet 1");
		Scanner fileScanner = new Scanner( in);

		//write data to workbook
		int row_i = 0;
		String line;
		//for each line
		while( fileScanner.hasNextLine()){
			line = fileScanner.nextLine();
			Row row = sheet.createRow( row_i++);
			Scanner lineScanner = new Scanner( line);
			lineScanner.useDelimiter( String.valueOf( delimiter));

			int cell_i = 0;
			while( lineScanner.hasNext())
				row.createCell( cell_i++).setCellValue(
					lineScanner.next());}

			//write to file
			FileOutputStream writer = new FileOutputStream(out);
			workbook.write( writer);
			writer.close();}

	private void handleWorkerTermination( Worker worker){
		//start next worker ( if needed )
		synchronized( nextWorker){
			if( nextWorker < workers.size())
				workers.get( nextWorker++).start();}
		//update stats
		synchronized( statslock){
			if( worker.succeeded)
				succeeded++;
			else{
				failed++;
				failureCauses.add( worker.failureCause);}
			completed++;
			if( nextWorker >= workers.size())
				done = true;}
		setChanged();
		notifyObservers();}

	//enum definitions
	public enum InputMode {
		Directory, File }
	public enum OutputMode {
		StandardOut, File, Directory }
	public enum Direction {
		toCSV, toXLS }

	//static functions
	public static File changeFileExt(
			File original, String ext){
		Path path = original.toPath();
		String basename = path.getFileName().toString();
		int dot_i = basename.lastIndexOf('.');
		//ignore unix-style hidden files ( .hidden )
		if( dot_i > 0)
			basename = basename.substring( 0, dot_i + 1) + ext;
		else
			basename = basename + "." + ext;
		return path.getParent().resolve( basename).toFile();}

	//subclasses
	protected class Worker extends Thread {
		public FilePair files;
		public boolean completed;
		public boolean succeeded;
		public Exception failureCause;
		public Worker( FilePair files){
			this.files = files;
			completed = false;
			succeeded = false;}
		public void run(){
			log.printf("Worker starting on %s\n", files.input);
					try{
						switch( direction){
							case toCSV:
								convertToCSV( files.input, files.output);
								break;
							case toXLS:
								convertToXLS( files.input, files.output);
								break;
							default:break;}
						succeeded = true;}
					catch( Exception e){
						e.printStackTrace();
						failureCause = e;}
			completed = true;
			log.printf("Worker finished on %s\n", files.input);
			handleWorkerTermination(this);}
	}

	private class FilePair{
		public File input, output;
		public FilePair( File input, File output){
			this.input = input;
			this.output = output;}
	}

	private class FileExtensionFilter implements FileFilter{
		public String extension;
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