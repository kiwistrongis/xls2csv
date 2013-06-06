//standard library imports
import java.io.File;
import java.nio.file.Path;
import java.util.Vector;

public class ResourceManager {
	//private fields
	private Vector<Path> paths;

	//constructor
	public ResourceManager(){
		paths = new Vector<Path>();
		//find possible paths
		Path classloc = new File( getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).toPath();
		Path pwd = new File(
			System.getProperty("user.dir")).toPath();
		//add paths
		paths.add( pwd);
		paths.add( classloc.getParent());}

	//functions
	public File locate( String filename){
		for( Path path : paths){
			File attempt = path.resolve( filename).toFile();
			if( attempt.exists()) return attempt;}
		return null;}

	public void add( Path path){
		paths.add( path);}
}