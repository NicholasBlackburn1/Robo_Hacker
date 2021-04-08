package blackburn.utils;

import blackburn.BlackburnConst;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.Config;

public enum RotationUtils
    {
        ;
        
        public static Vector3d getEyesPos()
        {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            
            return new Vector3d(player.getPosX(),
                player.getPosY()+ player.getEyeHeight(player.getPose()),
                player.getPosZ());
        }
        
        public static Vector3d getClientLookVec()
        {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            float f = 0.017453292F;
            float pi = (float)Math.PI;
            
            float f1 = MathHelper.cos(-player.cameraYaw * f - pi);
            float f2 = MathHelper.sin(-player.cameraYaw * f - pi);
            float f3 = -MathHelper.cos(-player.prevRotationPitch * f);
            float f4 = MathHelper.sin(-player.prevRotationPitch * f);
            
            return new Vector3d(f2 * f3, f4, f1 * f3);
        }
        
        public static Vector3d getServerLookVec()
        {
            RotationFaker rotationFaker = BlackburnConst.faker;
            float serverYaw = rotationFaker.getServerYaw();
            float serverPitch = rotationFaker.getServerPitch();
            
            float f = MathHelper.cos(-serverYaw * 0.017453292F - (float)Math.PI);
            float f1 = MathHelper.sin(-serverYaw * 0.017453292F - (float)Math.PI);
            float f2 = -MathHelper.cos(-serverPitch * 0.017453292F);
            float f3 = MathHelper.sin(-serverPitch * 0.017453292F);
            return new Vector3d(f1 * f2, f3, f * f2);
        }
        
        public static Rotation getNeededRotations(Vector3d vec)
        {
            Vector3d eyesPos = getEyesPos();
            
            double diffX = vec.x - eyesPos.x;
            double diffY = vec.y - eyesPos.y;
            double diffZ = vec.z - eyesPos.z;
            
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
            
            float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
            float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
            
            return new Rotation(yaw, pitch);
        }
        
        public static double getAngleToLookVec(Vector3d vec)
        {
            Rotation needed = getNeededRotations(vec);
            
            ClientPlayerEntity player = Config.getMinecraft().player;
            float currentYaw = MathHelper.wrapDegrees(player.cameraYaw);
            float currentPitch = MathHelper.wrapDegrees(player.prevRotationPitch);
            
            float diffYaw = currentYaw - needed.yaw;
            float diffPitch = currentPitch - needed.pitch;
            
            return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
        }
        
        public static double getAngleToLastReportedLookVec(Vector3d vec)
        {
            Rotation needed = getNeededRotations(vec);
            
            ClientPlayerEntity player = Config.getMinecraft().player;
            float lastReportedYaw = MathHelper.wrapDegrees(player.prevRotationYaw);
            float lastReportedPitch = MathHelper.wrapDegrees(player.prevRotationPitch);
            
            float diffYaw = lastReportedYaw - needed.yaw;
            float diffPitch = lastReportedPitch - needed.pitch;
            
            return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
        }
        
        public static float getHorizontalAngleToLookVec(Vector3d vec)
        {
            Rotation needed = getNeededRotations(vec);
            return MathHelper.wrapDegrees(Config.getMinecraft().player.cameraYaw) - needed.yaw;
        }
        
        public static final class Rotation
        {
            private final float yaw;
            private final float pitch;
            
            public Rotation(float yaw, float pitch)
            {
                this.yaw = MathHelper.wrapDegrees(yaw);
                this.pitch = MathHelper.wrapDegrees(pitch);
            }
            
            public float getYaw()
            {
                return yaw;
            }
            
            public float getPitch()
            {
                return pitch;
            }
        }
    }