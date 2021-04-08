package blackburn.event;

import java.util.ArrayList;

public interface PostMotionListener extends Listener
{
	public void onPostMotion();
	
	public static class PostMotionEvent extends Event<PostMotionListener>
	{
		public static final PostMotionEvent INSTANCE = new PostMotionEvent();
		
		
		@Override
		public Class<PostMotionListener> getListenerType()
		{
			return PostMotionListener.class;
		}

        @Override
        public void fire(ArrayList<PostMotionListener> listeners) {

			for(PostMotionListener listener : listeners)
            listener.onPostMotion();
            // TODO Auto-generated method stub
            
        }
	}
}