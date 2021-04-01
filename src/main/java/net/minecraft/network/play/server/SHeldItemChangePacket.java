package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SHeldItemChangePacket implements IPacket<IClientPlayNetHandler> {
   private int heldItemHotbarIndex;

   public SHeldItemChangePacket() {
   }

   public SHeldItemChangePacket(int hotbarIndexIn) {
      this.heldItemHotbarIndex = hotbarIndexIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.heldItemHotbarIndex = buf.readByte();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.heldItemHotbarIndex);
   }

   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleHeldItemChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getHeldItemHotbarIndex() {
      return this.heldItemHotbarIndex;
   }
}
