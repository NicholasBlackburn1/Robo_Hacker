package blackburn.hacks;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.Config;
import blackburn.utils.*;

public class TrajectoryTracking {
    
	private float partialTicks;

	
	public void onRender()
	{
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(4);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		RenderUtils.applyCameraRotationOnly();
		
		ArrayList<Vector3d> path = getPath(partialTicks);
		Vector3d camPos = RenderUtils.getCameraPos();
		
		drawLine(path, camPos);
		
		if(!path.isEmpty())
		{
			Vector3d end = path.get(path.size() - 1);
			drawEndOfLine(end, camPos);
		}
		
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glPopMatrix();
	}
	
	private void drawLine(ArrayList<Vector3d> path, Vector3d camPos)
	{
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(0, 1, 0, 0.75F);
		
		for(Vector3d point : path)
			GL11.glVertex3d(point.x - camPos.x, point.y - camPos.y,
				point.z - camPos.z);
		
		GL11.glEnd();
	}
	
	private void drawEndOfLine(Vector3d end, Vector3d camPos)
	{
		double renderX = end.x - camPos.x;
		double renderY = end.y - camPos.y;
		double renderZ = end.z - camPos.z;
		
		GL11.glPushMatrix();
		GL11.glTranslated(renderX - 0.5, renderY - 0.5, renderZ - 0.5);
		
		GL11.glColor4f(0, 1, 0, 0.25F);
		RenderUtils.drawSolidBox();
		
		GL11.glColor4f(0, 1, 0, 0.75F);
		RenderUtils.drawOutlinedBox();
		
		GL11.glPopMatrix();
	}
	
	private ArrayList<Vector3d> getPath(float partialTicks)
	{
	
		ClientPlayerEntity player = Config.getMinecraft().player;
		ArrayList<Vector3d> path = new ArrayList<>();
		
		ItemStack stack = player.getHeldItemMainhand();
		Item item = stack.getItem();
		
		// check if item is throwable
		if(stack.isEmpty() || !isThrowable(item))
			return path;
		
		// calculate starting position
		double arrowPosX = player.lastTickPosX
			+ (player.getPosX() - player.lastTickPosX* partialTicks
			- Math.cos(Math.toRadians(player.getYaw(partialTicks) - 0.16 )));
		
		double arrowPosY = player.lastTickPosY
			+ (player.getPosY()) - player.lastTickPosY* partialTicks
			+ player.getEyeHeight() - 0.1;
		
		double arrowPosZ = player.lastTickPosZ
			+ (player.getPosZ()) - player.lastTickPosZ * partialTicks
			- Math.sin(Math.toRadians(player.getYaw(partialTicks)- 0.16));
		
		// Motion factor. Arrows go faster than snowballs and all that...
		double arrowMotionFactor = item instanceof ArrowItem  ? 1.0 : 0.4;
		
		double yaw = Math.toRadians(player.getYaw(partialTicks));
		double pitch = Math.toRadians(player.getPitch(partialTicks));
		
		// calculate starting motion
		double arrowMotionX =
			-Math.sin(yaw) * Math.cos(pitch) * arrowMotionFactor;
		double arrowMotionY = -Math.sin(pitch) * arrowMotionFactor;
		double arrowMotionZ =
			Math.cos(yaw) * Math.cos(pitch) * arrowMotionFactor;
		
		// 3D Pythagorean theorem. Returns the length of the arrowMotion vector.
		double arrowMotion = Math.sqrt(arrowMotionX * arrowMotionX
			+ arrowMotionY * arrowMotionY + arrowMotionZ * arrowMotionZ);
		
		arrowMotionX /= arrowMotion;
		arrowMotionY /= arrowMotion;
		arrowMotionZ /= arrowMotion;
		
		// apply bow charge
		if(item instanceof BowItem)
		{
			float bowPower = (72000 - player.getItemInUseMaxCount()) / 20.0f;
			bowPower = (bowPower * bowPower + bowPower * 2.0f) / 3.0f;
			
			if(bowPower > 1 || bowPower <= 0.1F)
				bowPower = 1;
			
			bowPower *= 3F;
			arrowMotionX *= bowPower;
			arrowMotionY *= bowPower;
			arrowMotionZ *= bowPower;
			
		}else
		{
			arrowMotionX *= 1.5;
			arrowMotionY *= 1.5;
			arrowMotionZ *= 1.5;
		}
		
		double gravity = getProjectileGravity(item);
		Vector3d eyesPos = RotationUtils.getEyesPos();
		
		for(int i = 0; i < 1000; i++)
		{
			// add to path
			Vector3d arrowPos = new Vector3d(arrowPosX, arrowPosY, arrowPosZ);
			path.add(arrowPos);
			
			// apply motion
			arrowPosX += arrowMotionX * 0.1;
			arrowPosY += arrowMotionY * 0.1;
			arrowPosZ += arrowMotionZ * 0.1;
			
			// apply air friction
			arrowMotionX *= 0.999;
			arrowMotionY *= 0.999;
			arrowMotionZ *= 0.999;
			
			// apply gravity
			arrowMotionY -= gravity * 0.1;
			
			// check for collision
			RayTraceContext context = new RayTraceContext(eyesPos, arrowPos,
			BlockMode.COLLIDER,FluidMode.NONE, Config.getMinecraft().player);
			if(Config.getMinecraft().world.rayTraceBlocks(context).getType() != Type.MISS)
				break;
		
		}
		
		return path;
	}
	
	private double getProjectileGravity(Item item)
	{
		if(item instanceof BowItem || item instanceof CrossbowItem)
			return 0.05;
		
		if(item instanceof PotionItem)
			return 0.4;
		
		if(item instanceof FishingRodItem)
			return 0.15;
		
		if(item instanceof TridentItem)
			return 0.015;
		
		return 0.03;
	}
	
	private boolean isThrowable(Item item)
	{
		return item instanceof BowItem || item instanceof CrossbowItem
			|| item instanceof SnowballItem || item instanceof EggItem
			|| item instanceof EnderPearlItem
			|| item instanceof SplashPotionItem
			|| item instanceof LingeringPotionItem
			|| item instanceof FishingRodItem || item instanceof TridentItem;
	}
	public float setParticalfloat(float partical){
		return this.partialTicks = partical;
	}
}
