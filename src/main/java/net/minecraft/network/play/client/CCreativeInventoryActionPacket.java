package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCreativeInventoryActionPacket implements IPacket<IServerPlayNetHandler> {
   private int slotId;
   private ItemStack stack = ItemStack.EMPTY;

   public CCreativeInventoryActionPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCreativeInventoryActionPacket(int slotIdIn, ItemStack stackIn) {
      this.slotId = slotIdIn;
      this.stack = stackIn.copy();
   }

   public void processPacket(IServerPlayNetHandler handler) {
      handler.processCreativeInventoryAction(this);
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.slotId = buf.readShort();
      this.stack = buf.readItemStack();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeShort(this.slotId);
      buf.writeItemStack(this.stack);
   }

   public int getSlotId() {
      return this.slotId;
   }

   public ItemStack getStack() {
      return this.stack;
   }
}
