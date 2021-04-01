package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TileEntityType<?> type;
   @Nullable
   protected World world;
   protected BlockPos pos = BlockPos.ZERO;
   protected boolean removed;
   @Nullable
   private BlockState cachedBlockState;
   private boolean warnedInvalidBlock;

   public TileEntity(TileEntityType<?> tileEntityTypeIn) {
      this.type = tileEntityTypeIn;
   }

   @Nullable
   public World getWorld() {
      return this.world;
   }

   public void setWorldAndPos(World world, BlockPos pos) {
      this.world = world;
      this.pos = pos.toImmutable();
   }

   public boolean hasWorld() {
      return this.world != null;
   }

   public void read(BlockState state, CompoundNBT nbt) {
      this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
   }

   public CompoundNBT write(CompoundNBT compound) {
      return this.writeInternal(compound);
   }

   private CompoundNBT writeInternal(CompoundNBT compound) {
      ResourceLocation resourcelocation = TileEntityType.getId(this.getType());
      if (resourcelocation == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         compound.putString("id", resourcelocation.toString());
         compound.putInt("x", this.pos.getX());
         compound.putInt("y", this.pos.getY());
         compound.putInt("z", this.pos.getZ());
         return compound;
      }
   }

   @Nullable
   public static TileEntity readTileEntity(BlockState state, CompoundNBT nbt) {
      String s = nbt.getString("id");
      return Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(s)).map((p_213134_1_) -> {
         try {
            return p_213134_1_.create();
         } catch (Throwable throwable) {
            LOGGER.error("Failed to create block entity {}", s, throwable);
            return null;
         }
      }).map((p_235656_3_) -> {
         try {
            p_235656_3_.read(state, nbt);
            return p_235656_3_;
         } catch (Throwable throwable) {
            LOGGER.error("Failed to load data for block entity {}", s, throwable);
            return null;
         }
      }).orElseGet(() -> {
         LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
         return null;
      });
   }

   public void markDirty() {
      if (this.world != null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
         this.world.markChunkDirty(this.pos, this);
         if (!this.cachedBlockState.isAir()) {
            this.world.updateComparatorOutputLevel(this.pos, this.cachedBlockState.getBlock());
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public double getMaxRenderDistanceSquared() {
      return 64.0D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlockState() {
      if (this.cachedBlockState == null) {
         this.cachedBlockState = this.world.getBlockState(this.pos);
      }

      return this.cachedBlockState;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return null;
   }

   public CompoundNBT getUpdateTag() {
      return this.writeInternal(new CompoundNBT());
   }

   public boolean isRemoved() {
      return this.removed;
   }

   public void remove() {
      this.removed = true;
   }

   public void validate() {
      this.removed = false;
   }

   public boolean receiveClientEvent(int id, int type) {
      return false;
   }

   public void updateContainingBlockInfo() {
      this.cachedBlockState = null;
   }

   public void addInfoToCrashReport(CrashReportCategory reportCategory) {
      reportCategory.addDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.world != null) {
         CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockState());
         CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.world.getBlockState(this.pos));
      }
   }

   public void setPos(BlockPos posIn) {
      this.pos = posIn.toImmutable();
   }

   public boolean onlyOpsCanSetNbt() {
      return false;
   }

   public void rotate(Rotation rotationIn) {
   }

   public void mirror(Mirror mirrorIn) {
   }

   public TileEntityType<?> getType() {
      return this.type;
   }

   public void warnInvalidBlock() {
      if (!this.warnedInvalidBlock) {
         this.warnedInvalidBlock = true;
         LOGGER.warn("Block entity invalid: {} @ {}", () -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType());
         }, this::getPos);
      }
   }
}
