package blackburn.hacks;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import blackburn.BlackburnConst;
import blackburn.utils.RenderUtils;
import blackburn.utils.RotationUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.Config;

public final class ItemEsp
{
	
	private int itemBox;
	private final ArrayList<ItemEntity> items = new ArrayList<>();
    private static String item_Name;
    private float ParticalTick;
	private boolean enable;

	
	public void onEnable()
	{
		
		
		itemBox = GL11.glGenLists(1);
		GL11.glNewList(itemBox, GL11.GL_COMPILE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 0, 0.5F);
		RenderUtils.drawOutlinedBox(new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5));
		GL11.glEndList();
	}
	

	public void onDisable()
	{
		
		GL11.glDeleteLists(itemBox, 1);
	}
	
	
	public void onUpdate()
	{
		items.clear();
		for(Entity entity : BlackburnConst.mc.world.getAllEntities()){
			if(entity instanceof ItemEntity)
				items.add((ItemEntity)entity);
        }
	}
	


	public void onRender()
	{
		
		// GL settings
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();
		RenderUtils.applyRegionalRenderOffset();
		
		BlockPos camPos = RenderUtils.getCameraBlockPos();
		int regionX = (camPos.getX() >> 9) * 512;
		int regionZ = (camPos.getZ() >> 9) * 512;
		
		if (this.enable){
		renderBoxes(ParticalTick, regionX, regionZ);
		renderTracers(ParticalTick, regionX, regionZ);
		}
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		
	}
	
	private void renderBoxes(float partialTicks, int regionX, int regionZ)
	{
		
		
		for(ItemEntity e : items)
		{
			GL11.glPushMatrix();
			
			GL11.glTranslated(
				e.prevPosX-(e.getPosX() - e.prevPosY) * partialTicks - regionX,
				e.prevPosY + (e.getPosY() - e.prevPosY) * partialTicks,
				e.prevPosZ + (e.getPosZ() - e.prevPosZ) * partialTicks - regionZ);
		
				GL11.glPushMatrix();
				GL11.glScaled(e.getWidth(),e.getHeight(), e.getWidth());
				GL11.glCallList(itemBox);
				GL11.glPopMatrix();
				
			
			GL11.glPopMatrix();
		}
	}
	
	private void renderTracers(double partialTicks, int regionX, int regionZ)
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1, 1, 0, 0.5F);
		
		Vector3d start =
			RotationUtils.getClientLookVec().add(RenderUtils.getCameraPos());
		
		GL11.glBegin(GL11.GL_LINES);
		for(ItemEntity e : items)
		{
			Vector3d end = e.getBoundingBox().getCenter()
				.subtract(new Vector3d(e.getPosX(), e.getPosY(), e.getPosZ())
					.subtract(e.prevPosX, e.prevPosY, e.prevPosZ *1 - partialTicks));
			
			GL11.glVertex3d(start.x - regionX, start.y, start.z - regionZ);
			GL11.glVertex3d(end.x - regionX, end.y, end.z - regionZ);
		}
		GL11.glEnd();
	}

    public Float setParticleTics(float part){
		return this.ParticalTick = part;
	}
	public boolean setEnabled(boolean enabler){
		return this.enable = enabler;
	}
	
}
