package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityMetadataPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

   public SEntityMetadataPacket() {
   }

   public SEntityMetadataPacket(int entityIdIn, EntityDataManager dataManagerIn, boolean sendAll) {
      this.entityId = entityIdIn;
      if (sendAll) {
         this.dataManagerEntries = dataManagerIn.getAll();
         dataManagerIn.setClean();
      } else {
         this.dataManagerEntries = dataManagerIn.getDirty();
      }

   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.dataManagerEntries = EntityDataManager.readEntries(buf);
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      EntityDataManager.writeEntries(this.dataManagerEntries, buf);
   }

   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleEntityMetadata(this);
   }

   @OnlyIn(Dist.CLIENT)
   public List<EntityDataManager.DataEntry<?>> getDataManagerEntries() {
      return this.dataManagerEntries;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }
}
