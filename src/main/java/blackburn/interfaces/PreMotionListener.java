package blackburn.interfaces;

import java.util.ArrayList;

public interface PreMotionListener extends Listener
{
	public void onPreMotion();
	
	public static class PreMotionEvent extends Event<PreMotionListener>
	{
		public static final PreMotionEvent INSTANCE = new PreMotionEvent();
		
		public void fire(ArrayList<PreMotionListener> listeners)
		{
			for(PreMotionListener listener : listeners)
				listener.onPreMotion();
		}
		
	
		public Class<PreMotionListener> getListenerType()
		{
			return PreMotionListener.class;
		}

       
	}
}
