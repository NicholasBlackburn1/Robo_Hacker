package blackburn.event;

import java.util.ArrayList;

public interface RenderListener extends Listener
{
	public void onRender(float partialTicks);
	
	public static class RenderEvent extends Event<RenderListener>
	{
		private final float partialTicks;
		
		public RenderEvent(float partialTicks)
		{
			this.partialTicks = partialTicks;
		}
		
		@Override
		public void fire(ArrayList<RenderListener> listeners)
		{
			for(RenderListener listener : listeners)
				listener.onRender(partialTicks);
		}
		
		@Override
		public Class<RenderListener> getListenerType()
		{
			return RenderListener.class;
		}
	}
}