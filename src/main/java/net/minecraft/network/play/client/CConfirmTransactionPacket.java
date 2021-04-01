package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CConfirmTransactionPacket implements IPacket<IServerPlayNetHandler> {
   private int windowId;
   private short uid;
   private boolean accepted;

   public CConfirmTransactionPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CConfirmTransactionPacket(int windowIdIn, short uidIn, boolean acceptedIn) {
      this.windowId = windowIdIn;
      this.uid = uidIn;
      this.accepted = acceptedIn;
   }

   public void processPacket(IServerPlayNetHandler handler) {
      handler.processConfirmTransaction(this);
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.uid = buf.readShort();
      this.accepted = buf.readByte() != 0;
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.uid);
      buf.writeByte(this.accepted ? 1 : 0);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public short getUid() {
      return this.uid;
   }
}
