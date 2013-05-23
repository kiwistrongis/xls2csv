//standard library imports
import java.io.File;
import javax.swing.UIManager;
//non-standard library imports
import org.ini4j.Ini;

public class Configuration{
	Ini ini;
	Ini.Section meta_section;
	Ini.Section conv_section;
	Ini.Section gui_section;
	
	public Configuration(){
		ini = null;
		conv_section = null;
		gui_section = null;
		meta_section = null;}
	public Configuration( File file)
			throws java.io.IOException{
		open( file);}

	//open file
	public void open( File file)
			throws java.io.IOException{
		Ini ini = new Ini();
		ini.load(file);
		conv_section = ini.get("Converter");
		meta_section = ini.get("Meta");
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
		data = conv_section.get("output_ext");
		if( data != null)
			conv.output_ext = data;
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
			System.out.println(e);}}
}
