package com.villagesim.helpers;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import com.villagesim.interfaces.Printable;

public class LimitedQueue<E> extends LinkedList<E> {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 4846218714104553812L;
	private int limit;
	private HashMap<String, Integer> actionCounter;

    public LimitedQueue(int limit) {
        this.limit = limit;
        this.actionCounter = new HashMap<String, Integer>();
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        
        if(o instanceof Printable)
    	{
    		String key = ((Printable)o).getDebugPrint();
    		if(actionCounter.containsKey(key))
    		{
    			actionCounter.put(key, actionCounter.get(key) + 1);
    		}
    		else
    		{
    			actionCounter.put(key, 1);
    		}
    	}
        
        while (size() > limit) 
        {
        	super.remove(); 
        }
        return true;
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        super.addAll(c);
        
        for(E o : c)
        {
	        if(o instanceof Printable)
	    	{
	    		String key = ((Printable)o).getDebugPrint();
	    		if(actionCounter.containsKey(key))
	    		{
	    			actionCounter.put(key, actionCounter.get(key) + 1);
	    		}
	    		else
	    		{
	    			actionCounter.put(key, 1);
	    		}
	    	}
	    }
        
        while (size() > limit) 
        {
        	super.remove(); 
        }
        return true;
    }
    
    public String printCounter()
    {
    	String info = "";
    	for(String key : actionCounter.keySet())
    	{
    		info += (key + ":"+ actionCounter.get(key)) + " ";
    	}
    	return info;
    }
}