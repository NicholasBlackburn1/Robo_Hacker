package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlayerTryUseItemOnBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockRayTraceResult field_218795_a;
   private Hand hand;

   public CPlayerTryUseItemOnBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerTryUseItemOnBlockPacket(Hand p_i50756_1_, BlockRayTraceResult p_i50756_2_) {
      this.hand = p_i50756_1_;
      this.field_218795_a = p_i50756_2_;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.hand = buf.readEnumValue(Hand.class);
      this.field_218795_a = buf.readBlockRay();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.hand);
      buf.writeBlockRay(this.field_218795_a);
   }

   public void processPacket(IServerPlayNetHandler handler) {
      handler.processTryUseItemOnBlock(this);
   }

   public Hand getHand() {
      return this.hand;
   }

   public BlockRayTraceResult func_218794_c() {
      return this.field_218795_a;
   }
}
