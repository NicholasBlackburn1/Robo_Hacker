package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CHeldItemChangePacket implements IPacket<IServerPlayNetHandler> {
   private int slotId;

   public CHeldItemChangePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CHeldItemChangePacket(int slotIdIn) {
      this.slotId = slotIdIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.slotId = buf.readShort();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeShort(this.slotId);
   }

   public void processPacket(IServerPlayNetHandler handler) {
      handler.processHeldItemChange(this);
   }

   public int getSlotId() {
      return this.slotId;
   }
}
