package net.minecraft.entity.item.minecart;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class HopperMinecartEntity extends ContainerMinecartEntity implements IHopper {
   private boolean isBlocked = true;
   private int transferTicker = -1;
   private final BlockPos lastPosition = BlockPos.ZERO;

   public HopperMinecartEntity(EntityType<? extends HopperMinecartEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public HopperMinecartEntity(World worldIn, double x, double y, double z) {
      super(EntityType.HOPPER_MINECART, x, y, z, worldIn);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.HOPPER;
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.HOPPER.getDefaultState();
   }

   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   public int getSizeInventory() {
      return 5;
   }

   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
      boolean flag = !receivingPower;
      if (flag != this.getBlocked()) {
         this.setBlocked(flag);
      }

   }

   public boolean getBlocked() {
      return this.isBlocked;
   }

   public void setBlocked(boolean blocked) {
      this.isBlocked = blocked;
   }

   public World getWorld() {
      return this.world;
   }

   public double getXPos() {
      return this.getPosX();
   }

   public double getYPos() {
      return this.getPosY() + 0.5D;
   }

   public double getZPos() {
      return this.getPosZ();
   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote && this.isAlive() && this.getBlocked()) {
         BlockPos blockpos = this.getPosition();
         if (blockpos.equals(this.lastPosition)) {
            --this.transferTicker;
         } else {
            this.setTransferTicker(0);
         }

         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.captureDroppedItems()) {
               this.setTransferTicker(4);
               this.markDirty();
            }
         }
      }

   }

   public boolean captureDroppedItems() {
      if (HopperTileEntity.pullItems(this)) {
         return true;
      } else {
         List<ItemEntity> list = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), EntityPredicates.IS_ALIVE);
         if (!list.isEmpty()) {
            HopperTileEntity.captureItem(this, list.get(0));
         }

         return false;
      }
   }

   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.entityDropItem(Blocks.HOPPER);
      }

   }

   protected void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("TransferCooldown", this.transferTicker);
      compound.putBoolean("Enabled", this.isBlocked);
   }

   protected void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.transferTicker = compound.getInt("TransferCooldown");
      this.isBlocked = compound.contains("Enabled") ? compound.getBoolean("Enabled") : true;
   }

   public void setTransferTicker(int transferTickerIn) {
      this.transferTicker = transferTickerIn;
   }

   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   public Container createContainer(int id, PlayerInventory playerInventoryIn) {
      return new HopperContainer(id, playerInventoryIn, this);
   }
}
