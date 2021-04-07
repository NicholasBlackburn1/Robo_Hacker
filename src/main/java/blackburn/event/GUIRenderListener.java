package blackburn.event;

import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface GUIRenderListener extends Listener
{
	public void onRenderGUI(MatrixStack matrixStack, float partialTicks);
	
	public static class GUIRenderEvent extends Event<GUIRenderListener>
	{
		private final float partialTicks;
		private final MatrixStack matrixStack;
		
		public GUIRenderEvent(MatrixStack matrixStack, float partialTicks)
		{
			this.matrixStack = matrixStack;
			this.partialTicks = partialTicks;
		}
		
	
		
		@Override
		public Class<GUIRenderListener> getListenerType()
		{
			return GUIRenderListener.class;
		}

		@Override
		public void fire(ArrayList<GUIRenderListener> listeners) {
			for(GUIRenderListener listener : listeners)
				listener.onRenderGUI(matrixStack, partialTicks);	
			
		}
	}
}