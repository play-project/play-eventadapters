/**
 * 
 */
package ningyuan.pan.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



/**
 * @author Ningyuan Pan
 *
 */
public class Buffer <E>{
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition notEmpty = lock.newCondition();
	private final Condition notFull = lock.newCondition();
	
	private volatile int bufferSize;
	private final List<E> buffer = new ArrayList<E>();
	
	public Buffer(int bs){
		bufferSize = bs;
	}
	
	//for test
	public int getSize(){
		return buffer.size();
	}
	
	public boolean isEmpty(){
		return buffer.isEmpty();
	}
	
	public void  clear(){
		try{
			lock.lock();
			buffer.clear();
		}
		finally{
			lock.unlock();
		}	
	}
	
	public void add(E s) throws InterruptedException{
		try{
			lock.lock();
			if(buffer.size() >= bufferSize)
				notFull.await();
			buffer.add(s);
			notEmpty.signal();
		}
		finally{
			lock.unlock();
		}
	}
	
	public E get() throws InterruptedException{
		E ret = null;
		try{
			lock.lock();
			if(buffer.isEmpty())
				notEmpty.await();
			ret = buffer.remove(0);
			notFull.signal();
			return ret;
		}
		finally{
			lock.unlock();
		}
	}
}
