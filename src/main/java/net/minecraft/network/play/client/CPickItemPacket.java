package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPickItemPacket implements IPacket<IServerPlayNetHandler> {
   private int pickIndex;

   public CPickItemPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPickItemPacket(int pickIndexIn) {
      this.pickIndex = pickIndexIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.pickIndex = buf.readVarInt();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.pickIndex);
   }

   public void processPacket(IServerPlayNetHandler handler) {
      handler.processPickItem(this);
   }

   public int getPickIndex() {
      return this.pickIndex;
   }
}
