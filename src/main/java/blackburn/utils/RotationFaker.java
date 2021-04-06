package blackburn.utils;

import blackburn.event.PostMotionListener;
import blackburn.event.PreMotionListener;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.Config;

public final class RotationFaker
	implements PreMotionListener, PostMotionListener
{
	private boolean fakeRotation;
	private float serverYaw;
	private float serverPitch;
	private float realYaw;
	private float realPitch;
	
	@Override
	public void onPreMotion()
	{
		if(!fakeRotation)
			return;
		
		ClientPlayerEntity player = Config.getMinecraft().player;
		realYaw = player.cameraYaw;
		realPitch = player.prevRotationPitch;
		player.cameraYaw = serverYaw;
		player.prevRotationPitch = serverPitch;
	}
	
	@Override
	public void onPostMotion()
	{
		if(!fakeRotation)
			return;
		
		ClientPlayerEntity player = Config.getMinecraft().player;
		player.cameraYaw = realYaw;
		player.prevRotationPitch = realPitch;
		fakeRotation = false;
	}
	
	public void faceVectorPacket(Vector3d vec)
	{
		RotationUtils.Rotation rotations =
			RotationUtils.getNeededRotations(vec);
		
		fakeRotation = true;
		serverYaw = rotations.getYaw();
		serverPitch = rotations.getPitch();
	}
	
	public void faceVectorClient(Vector3d vec)
	{
		RotationUtils.Rotation rotations =
			RotationUtils.getNeededRotations(vec);
		
		Config.getMinecraft().player.cameraYaw = rotations.getYaw();
		Config.getMinecraft().player.prevRotationPitch = rotations.getPitch();
	}
	
	public void faceVectorClientIgnorePitch(Vector3d vec)
	{
		RotationUtils.Rotation rotations =
			RotationUtils.getNeededRotations(vec);
		
		Config.getMinecraft().player.cameraYaw = rotations.getYaw();
		Config.getMinecraft().player.prevRotationPitch = 0;
	}
	
	public float getServerYaw()
	{
		return fakeRotation ? serverYaw : Config.getMinecraft().player.cameraYaw;
	}
	
	public float getServerPitch()
	{
		return fakeRotation ? serverPitch : Config.getMinecraft().player.prevRotationPitch;
	}
}