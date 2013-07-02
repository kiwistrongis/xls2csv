//standard library imports
import java.awt.Font;
import java.io.File;
import java.util.Enumeration;	
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
//non-standard library imports
import org.ini4j.Ini;

public class Configuration{
	public File file;
	public Ini ini;
	public Ini.Section meta_section;
	public Ini.Section conv_section;
	public Ini.Section gui_section;
	
	public Configuration(){
		ini = null;
		conv_section = null;
		gui_section = null;
		meta_section = null;}
	public Configuration( File file)
			throws java.io.IOException{
		ini = null;
		conv_section = null;
		gui_section = null;
		meta_section = null;
		this.file = file;
		open( file);}

	//open file
	public void open( File file)
			throws java.io.IOException{
		this.file = file;
		Ini ini = new Ini();
		ini.load(file);
		meta_section = ini.get("Meta");
		conv_section = ini.get("Converter");
		gui_section = ini.get("Gui");}

	//load methods
	public void load( Converter conv){
		//setup
		if( conv_section == null) return;
		String data;
		//check if this configuration file is enabled or not
		if( meta_section != null){
			data = meta_section.get("enabled");
			boolean enabled = Boolean.parseBoolean(data);
			if( ! enabled) return;}
		//delimiter
		data = conv_section.get("delimiter");
		if( data != null)
			conv.delimiter = data.replace("\\t","\t").charAt(0);
		//encoding
		data = conv_section.get("encoding");
		if( data != null)
			conv.encoding = data;
		//output_ext
		/*data = conv_section.get("output_ext");
		if( data != null)
			conv.output_ext = data;*/
		//workerLimitEnabled
		data = conv_section.get("workerLimitEnabled");
		if( data != null)
			conv.workerLimitEnabled = Boolean.parseBoolean(data);
		//maxWorkerCount
		data = conv_section.get("maxWorkerCount");
		if( data != null)
			conv.maxWorkerCount = Integer.parseInt(data);}

	public void load( Gui gui){}

	public void loadGuiSettings(){
		javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
		javax.swing.JDialog.setDefaultLookAndFeelDecorated(true);
		try{
			UIManager.setLookAndFeel(
				"org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel");}
		catch( Exception e){
			System.out.println(e);}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				multiplyGuiFontSize( 1.2);}});}

	public void multiplyGuiFontSize( double multiplier){
		UIDefaults defaults = UIManager.getDefaults();
		Enumeration e = defaults.keys();
		while( e.hasMoreElements()){
			Object key = e.nextElement();
			Object value = defaults.get( key);
			if (value instanceof Font) {
				Font font = (Font) value;
				int newSize = (int) Math.round(
					font.getSize() * multiplier);
				value = value instanceof FontUIResource ?
					new FontUIResource(
						font.getName(), font.getStyle(), newSize) :
					new Font(
						font.getName(), font.getStyle(), newSize);
				defaults.put( key, value);}}}
}
