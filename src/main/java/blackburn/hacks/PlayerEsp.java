package blackburn.hacks;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import blackburn.BlackburnConst;
import blackburn.utils.EnumSetting;
import blackburn.utils.RenderUtils;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class PlayerEsp {
	
	private int playerBox;
	private final ArrayList<PlayerEntity> players = new ArrayList<>();
	
	public PlayerEsp()
	{
		
	}
	
	public void onEnable()
	{
		
		
		playerBox = GL11.glGenLists(1);
		GL11.glNewList(playerBox, GL11.GL_COMPILE);
		AxisAlignedBB bb = new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5);
		RenderUtils.drawOutlinedBox(bb);
		GL11.glEndList();
	}
	

	public void onDisable()
	{
		
		GL11.glDeleteLists(playerBox, 1);
		playerBox = 0;
	}
	
	public void onUpdate()
	{
		PlayerEntity player = BlackburnConst.mc.player;
		ClientWorld world = BlackburnConst.mc.world;
		
		players.clear();
		Stream<AbstractClientPlayerEntity> stream = world.getPlayers()
			.parallelStream().filter(e -> !e.removed && e.getHealth() > 0)
			.filter(e -> e != player)
			.filter(e -> Math.abs(e.getPosY() - BlackburnConst.mc.player.getPosY()) <= 1e6);

		players.addAll(stream.collect(Collectors.toList()));
	}
	

	public void onRender(float partialTicks)
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();
		RenderUtils.applyRegionalRenderOffset();
		
		BlockPos camPos = RenderUtils.getCameraBlockPos();
		int regionX = (camPos.getX() >> 9) * 512;
		int regionZ = (camPos.getZ() >> 9) * 512;
		
		// draw boxes
		
		renderBoxes(partialTicks, regionX, regionZ);
	    renderTracers(partialTicks, regionX, regionZ);
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
    // Renders gl boxes
	private void renderBoxes(double partialTicks, int regionX, int regionZ)
	{
		int extraSize = 3;

		for(PlayerEntity e : players)
		{
			GL11.glPushMatrix();
			
			GL11.glTranslated(
				e.prevPosX + (e.getPosX() - e.prevPosX * partialTicks - regionX,
				e.prevPosY + (e.getPosY() - e.prevPosY) * partialTicks,
				e.prevPosZ + (e.getPosZ() - e.prevPosZ) * partialTicks - regionZ);
			
			GL11.glScaled(e.getWidth() + extraSize, e.getHeight() + extraSize,
				e.getWidth() + extraSize);
			
		
		    GL11.glColor4f(0, 0, 1, 0.5F);
			
			
			GL11.glCallList(playerBox);
			
			GL11.glPopMatrix();
		}
	}
	
	private void renderTracers(double partialTicks, int regionX, int regionZ)
	{
		Vec3d start =
			RotationUtils.getClientLookVec().add(RenderUtils.getCameraPos());
		
		GL11.glBegin(GL11.GL_LINES);
		for(PlayerEntity e : players)
		{
			Vec3d end = e.getBoundingBox().getCenter()
				.subtract(new Vec3d(e.getX(), e.getY(), e.getZ())
					.subtract(e.prevX, e.prevY, e.prevZ)
					.multiply(1 - partialTicks));
			
			if(WURST.getFriends().contains(e.getEntityName()))
				GL11.glColor4f(0, 0, 1, 0.5F);
			else
			{
				float f = MC.player.distanceTo(e) / 20F;
				GL11.glColor4f(2 - f, f, 0, 0.5F);
			}
			
			GL11.glVertex3d(start.x - regionX, start.y, start.z - regionZ);
			GL11.glVertex3d(end.x - regionX, end.y, end.z - regionZ);
		}
		GL11.glEnd();
	}
	
	private enum Style
	{
		BOXES("Boxes only", true, false),
		LINES("Lines only", false, true),
		LINES_AND_BOXES("Lines and boxes", true, true);
		
		private final String name;
		private final boolean boxes;
		private final boolean lines;
		
		private Style(String name, boolean boxes, boolean lines)
		{
			this.name = name;
			this.boxes = boxes;
			this.lines = lines;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	private enum BoxSize
	{
		ACCURATE("Accurate", 0),
		FANCY("Fancy", 0.1);
		
		private final String name;
		private final double extraSize;
		
		private BoxSize(String name, double extraSize)
		{
			this.name = name;
			this.extraSize = extraSize;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}

}
