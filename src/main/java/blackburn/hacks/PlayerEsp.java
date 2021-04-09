package blackburn.hacks;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import blackburn.BlackburnConst;
import blackburn.utils.RenderUtils;
import blackburn.utils.RotationUtils;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.Config;

public class PlayerEsp {
	
	private int playerBox;
	private final ArrayList<PlayerEntity> players = new ArrayList<>();
    private float partialTicks;
	private boolean enable;
	
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
		Stream<AbstractClientPlayerEntity> stream = (world.getPlayers())
			.parallelStream().filter(e -> e.getHealth() > 0 && !e.isServerWorld())
			.filter(e -> Math.abs(e.getPosY() - BlackburnConst.mc.player.getPosY()) <= 1e6);

		players.addAll(stream.collect(Collectors.toList()));
	}
	

	public void onRender()
	{
		if(this.enable){
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(3);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();
		RenderUtils.applyRegionalRenderOffset();
		
		BlockPos camPos = RenderUtils.getCameraBlockPos();
		int regionX = (camPos.getX() >> 2) * 512;
		int regionZ = -(camPos.getZ() >> 2) * 512;
		
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

	}

	
    // Renders gl boxes
	private void renderBoxes(double partialTicks, int regionX, int regionZ)
	{
	
		for(PlayerEntity e : players)
		{	
			Config.warnblackburn("Amout of Drawn players are" + players.size());
			GL11.glPushMatrix();
			
			GL11.glTranslated( 
				e.prevChasingPosX + (e.getPosX() - e.prevPosX) * partialTicks - regionX,
				e.prevChasingPosY + (e.getPosY() - e.prevPosY) * partialTicks,
				e.prevChasingPosZ + (e.getPosZ() - e.prevPosZ) * partialTicks - regionZ);
			
			GL11.glScaled(e.getBoundingBox().getXSize(), e.getBoundingBox().getYSize(),
				e.getBoundingBox().getZSize());
			
		
		    GL11.glColor4f(1, 0,0 , 0.5F);
			
			
			GL11.glCallList(playerBox);
			
			GL11.glPopMatrix();
		}
	}
	
	private void renderTracers(double partialTicks, int regionX, int regionZ)
	{
		Vector3d start =
			RotationUtils.getClientLookVec().add(RenderUtils.getCameraPos());
		
		GL11.glBegin(GL11.GL_LINES);
		for(PlayerEntity e : players)
		{
			Vector3d end = e.getBoundingBox().getCenter()
				.subtract(new Vector3d(e.getPosX(), e.getPosY(), e.getPosZ())
					.subtract(e.prevPosX, e.prevPosY, e.prevPosZ *1 - partialTicks));
			
			GL11.glColor4f(0, 1, 0, 0.5F);

			GL11.glVertex3d(start.x - regionX, start.y, start.z - regionZ);
			GL11.glVertex3d(end.x - regionX, end.y, end.z - regionZ);
		}
		GL11.glEnd();
	}
	public float setParticalTicks(float ticks){
        return this.partialTicks = ticks;
    }
	
	public boolean enableESP(boolean enabler){
		return this.enable = enabler;
		
	}
        
	}

