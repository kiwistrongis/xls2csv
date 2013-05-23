//standard library imports
import java.io.File;
import javax.swing.UIManager;
//non-standard library imports
import org.ini4j.Ini;

public class Configuration{
	Ini ini;
	Ini.Section conv_section;
	public Configuration( File file)
			throws java.io.IOException{
		Ini ini = new Ini();
		ini.load(file);
		conv_section = ini.get("Converter");}
	public void load( Converter conv){
		String data = conv_section.get("enabled");
		boolean enabled = Boolean.parseBoolean(data);
		if( ! enabled) return;
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
	public void loadGuiSettings(){
		javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
		try{
			UIManager.setLookAndFeel(
				"org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel");}
		catch( Exception e){
			System.out.println(e);}}
}
