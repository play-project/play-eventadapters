package ningyuan.pan.eventstream;

/**
 * @author Ningyuan Pan
 *
 */

public interface  HTTPStreamObserver <T>{
	
	public void update(T o);
	
	public String getID();
}
