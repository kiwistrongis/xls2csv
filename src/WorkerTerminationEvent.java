import java.util.EventObject;
public class WorkerTerminationEvent extends EventObject {
	Converter.Worker worker;
	public WorkerTerminationEvent( Object source,
			Converter.Worker worker) {
		super(source);
		this.worker = worker;}
}