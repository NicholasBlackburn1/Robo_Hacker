package blackburn.utils;

import blackburn.BlackburnConst;
import blackburn.interfaces.PostMotionListener;
import blackburn.interfaces.PreMotionListener;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

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
    
    ClientPlayerEntity player = BlackburnConst.mc.player;
    realYaw = player.getPitchYaw().x;
    realPitch = player.getPitchYaw().y;
    serverYaw = player.getPitchYaw().x; 
    serverPitch =player.getPitchYaw().y;
}

@Override
public void onPostMotion()
{
    if(!fakeRotation)
        return;
    
    ClientPlayerEntity player = BlackburnConst.mc.player;
    ;
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
    
    //BlackburnConst.mc.player.getPitchYaw().x = rotations.getYaw();
   // BlackburnConst.mc.player.getPitchYaw().y = rotations.getPitch();
}

public void faceVectorClientIgnorePitch(Vector3d vec)
{
    RotationUtils.Rotation rotations =
        RotationUtils.getNeededRotations(vec);
    
    //BlackburnConst.mc.player.getPitchYaw().x = rotations.getYaw();
    //BlackburnConst.mc.player.getPitchYaw().y = 0;
}

public float getServerYaw()
{
    return fakeRotation ? serverYaw : BlackburnConst.mc.player.getPitchYaw().x;
}

public float getServerPitch()
{
    return fakeRotation ? serverPitch : BlackburnConst.mc.player.getPitchYaw().y;
}
}
