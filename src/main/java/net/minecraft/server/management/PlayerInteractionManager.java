package net.minecraft.server.management;

import java.util.Objects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerInteractionManager {
   private static final Logger field_225418_c = LogManager.getLogger();
   public ServerWorld world;
   public ServerPlayerEntity player;
   private GameType gameType = GameType.NOT_SET;
   private GameType field_241813_e_ = GameType.NOT_SET;
   private boolean isDestroyingBlock;
   private int initialDamage;
   private BlockPos destroyPos = BlockPos.ZERO;
   private int ticks;
   private boolean receivedFinishDiggingPacket;
   private BlockPos delayedDestroyPos = BlockPos.ZERO;
   private int initialBlockDamage;
   private int durabilityRemainingOnBlock = -1;

   public PlayerInteractionManager(ServerWorld p_i50702_1_) {
      this.world = p_i50702_1_;
   }

   public void setGameType(GameType type) {
      this.func_241820_a(type, type != this.gameType ? this.gameType : this.field_241813_e_);
   }

   public void func_241820_a(GameType p_241820_1_, GameType p_241820_2_) {
      this.field_241813_e_ = p_241820_2_;
      this.gameType = p_241820_1_;
      p_241820_1_.configurePlayerCapabilities(this.player.abilities);
      this.player.sendPlayerAbilities();
      this.player.server.getPlayerList().sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_GAME_MODE, this.player));
      this.world.updateAllPlayersSleepingFlag();
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public GameType func_241815_c_() {
      return this.field_241813_e_;
   }

   public boolean survivalOrAdventure() {
      return this.gameType.isSurvivalOrAdventure();
   }

   public boolean isCreative() {
      return this.gameType.isCreative();
   }

   public void initializeGameType(GameType type) {
      if (this.gameType == GameType.NOT_SET) {
         this.gameType = type;
      }

      this.setGameType(this.gameType);
   }

   public void tick() {
      ++this.ticks;
      if (this.receivedFinishDiggingPacket) {
         BlockState blockstate = this.world.getBlockState(this.delayedDestroyPos);
         if (blockstate.isAir()) {
            this.receivedFinishDiggingPacket = false;
         } else {
            float f = this.func_229859_a_(blockstate, this.delayedDestroyPos, this.initialBlockDamage);
            if (f >= 1.0F) {
               this.receivedFinishDiggingPacket = false;
               this.tryHarvestBlock(this.delayedDestroyPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         BlockState blockstate1 = this.world.getBlockState(this.destroyPos);
         if (blockstate1.isAir()) {
            this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
            this.durabilityRemainingOnBlock = -1;
            this.isDestroyingBlock = false;
         } else {
            this.func_229859_a_(blockstate1, this.destroyPos, this.initialDamage);
         }
      }

   }

   private float func_229859_a_(BlockState p_229859_1_, BlockPos p_229859_2_, int p_229859_3_) {
      int i = this.ticks - p_229859_3_;
      float f = p_229859_1_.getPlayerRelativeBlockHardness(this.player, this.player.world, p_229859_2_) * (float)(i + 1);
      int j = (int)(f * 10.0F);
      if (j != this.durabilityRemainingOnBlock) {
         this.world.sendBlockBreakProgress(this.player.getEntityId(), p_229859_2_, j);
         this.durabilityRemainingOnBlock = j;
      }

      return f;
   }

   public void func_225416_a(BlockPos p_225416_1_, CPlayerDiggingPacket.Action p_225416_2_, Direction p_225416_3_, int p_225416_4_) {
      double d0 = this.player.getPosX() - ((double)p_225416_1_.getX() + 0.5D);
      double d1 = this.player.getPosY() - ((double)p_225416_1_.getY() + 0.5D) + 1.5D;
      double d2 = this.player.getPosZ() - ((double)p_225416_1_.getZ() + 0.5D);
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      if (d3 > 36.0D) {
         this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false, "too far"));
      } else if (p_225416_1_.getY() >= p_225416_4_) {
         this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false, "too high"));
      } else {
         if (p_225416_2_ == CPlayerDiggingPacket.Action.START_DESTROY_BLOCK) {
            if (!this.world.isBlockModifiable(this.player, p_225416_1_)) {
               this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false, "may not interact"));
               return;
            }

            if (this.isCreative()) {
               this.func_229860_a_(p_225416_1_, p_225416_2_, "creative destroy");
               return;
            }

            if (this.player.blockActionRestricted(this.world, p_225416_1_, this.gameType)) {
               this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false, "block action restricted"));
               return;
            }

            this.initialDamage = this.ticks;
            float f = 1.0F;
            BlockState blockstate = this.world.getBlockState(p_225416_1_);
            if (!blockstate.isAir()) {
               blockstate.onBlockClicked(this.world, p_225416_1_, this.player);
               f = blockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, p_225416_1_);
            }

            if (!blockstate.isAir() && f >= 1.0F) {
               this.func_229860_a_(p_225416_1_, p_225416_2_, "insta mine");
            } else {
               if (this.isDestroyingBlock) {
                  this.player.connection.sendPacket(new SPlayerDiggingPacket(this.destroyPos, this.world.getBlockState(this.destroyPos), CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
               }

               this.isDestroyingBlock = true;
               this.destroyPos = p_225416_1_.toImmutable();
               int i = (int)(f * 10.0F);
               this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225416_1_, i);
               this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, true, "actual start of destroying"));
               this.durabilityRemainingOnBlock = i;
            }
         } else if (p_225416_2_ == CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK) {
            if (p_225416_1_.equals(this.destroyPos)) {
               int j = this.ticks - this.initialDamage;
               BlockState blockstate1 = this.world.getBlockState(p_225416_1_);
               if (!blockstate1.isAir()) {
                  float f1 = blockstate1.getPlayerRelativeBlockHardness(this.player, this.player.world, p_225416_1_) * (float)(j + 1);
                  if (f1 >= 0.7F) {
                     this.isDestroyingBlock = false;
                     this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225416_1_, -1);
                     this.func_229860_a_(p_225416_1_, p_225416_2_, "destroyed");
                     return;
                  }

                  if (!this.receivedFinishDiggingPacket) {
                     this.isDestroyingBlock = false;
                     this.receivedFinishDiggingPacket = true;
                     this.delayedDestroyPos = p_225416_1_;
                     this.initialBlockDamage = this.initialDamage;
                  }
               }
            }

            this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, true, "stopped destroying"));
         } else if (p_225416_2_ == CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            if (!Objects.equals(this.destroyPos, p_225416_1_)) {
               field_225418_c.warn("Mismatch in destroy block pos: " + this.destroyPos + " " + p_225416_1_);
               this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
               this.player.connection.sendPacket(new SPlayerDiggingPacket(this.destroyPos, this.world.getBlockState(this.destroyPos), p_225416_2_, true, "aborted mismatched destroying"));
            }

            this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225416_1_, -1);
            this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, true, "aborted destroying"));
         }

      }
   }

   public void func_229860_a_(BlockPos p_229860_1_, CPlayerDiggingPacket.Action p_229860_2_, String p_229860_3_) {
      if (this.tryHarvestBlock(p_229860_1_)) {
         this.player.connection.sendPacket(new SPlayerDiggingPacket(p_229860_1_, this.world.getBlockState(p_229860_1_), p_229860_2_, true, p_229860_3_));
      } else {
         this.player.connection.sendPacket(new SPlayerDiggingPacket(p_229860_1_, this.world.getBlockState(p_229860_1_), p_229860_2_, false, p_229860_3_));
      }

   }

   public boolean tryHarvestBlock(BlockPos pos) {
      BlockState blockstate = this.world.getBlockState(pos);
      if (!this.player.getHeldItemMainhand().getItem().canPlayerBreakBlockWhileHolding(blockstate, this.world, pos, this.player)) {
         return false;
      } else {
         TileEntity tileentity = this.world.getTileEntity(pos);
         Block block = blockstate.getBlock();
         if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.canUseCommandBlock()) {
            this.world.notifyBlockUpdate(pos, blockstate, blockstate, 3);
            return false;
         } else if (this.player.blockActionRestricted(this.world, pos, this.gameType)) {
            return false;
         } else {
            block.onBlockHarvested(this.world, pos, blockstate, this.player);
            boolean flag = this.world.removeBlock(pos, false);
            if (flag) {
               block.onPlayerDestroy(this.world, pos, blockstate);
            }

            if (this.isCreative()) {
               return true;
            } else {
               ItemStack itemstack = this.player.getHeldItemMainhand();
               ItemStack itemstack1 = itemstack.copy();
               boolean flag1 = this.player.func_234569_d_(blockstate);
               itemstack.onBlockDestroyed(this.world, blockstate, pos, this.player);
               if (flag && flag1) {
                  block.harvestBlock(this.world, this.player, pos, blockstate, tileentity, itemstack1);
               }

               return true;
            }
         }
      }
   }

   public ActionResultType processRightClick(ServerPlayerEntity player, World worldIn, ItemStack stack, Hand hand) {
      if (this.gameType == GameType.SPECTATOR) {
         return ActionResultType.PASS;
      } else if (player.getCooldownTracker().hasCooldown(stack.getItem())) {
         return ActionResultType.PASS;
      } else {
         int i = stack.getCount();
         int j = stack.getDamage();
         ActionResult<ItemStack> actionresult = stack.useItemRightClick(worldIn, player, hand);
         ItemStack itemstack = actionresult.getResult();
         if (itemstack == stack && itemstack.getCount() == i && itemstack.getUseDuration() <= 0 && itemstack.getDamage() == j) {
            return actionresult.getType();
         } else if (actionresult.getType() == ActionResultType.FAIL && itemstack.getUseDuration() > 0 && !player.isHandActive()) {
            return actionresult.getType();
         } else {
            player.setHeldItem(hand, itemstack);
            if (this.isCreative()) {
               itemstack.setCount(i);
               if (itemstack.isDamageable() && itemstack.getDamage() != j) {
                  itemstack.setDamage(j);
               }
            }

            if (itemstack.isEmpty()) {
               player.setHeldItem(hand, ItemStack.EMPTY);
            }

            if (!player.isHandActive()) {
               player.sendContainerToPlayer(player.container);
            }

            return actionresult.getType();
         }
      }
   }

   public ActionResultType func_219441_a(ServerPlayerEntity playerIn, World worldIn, ItemStack stackIn, Hand handIn, BlockRayTraceResult blockRaytraceResultIn) {
      BlockPos blockpos = blockRaytraceResultIn.getPos();
      BlockState blockstate = worldIn.getBlockState(blockpos);
      if (this.gameType == GameType.SPECTATOR) {
         INamedContainerProvider inamedcontainerprovider = blockstate.getContainer(worldIn, blockpos);
         if (inamedcontainerprovider != null) {
            playerIn.openContainer(inamedcontainerprovider);
            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      } else {
         boolean flag = !playerIn.getHeldItemMainhand().isEmpty() || !playerIn.getHeldItemOffhand().isEmpty();
         boolean flag1 = playerIn.isSecondaryUseActive() && flag;
         ItemStack itemstack = stackIn.copy();
         if (!flag1) {
            ActionResultType actionresulttype = blockstate.onBlockActivated(worldIn, playerIn, handIn, blockRaytraceResultIn);
            if (actionresulttype.isSuccessOrConsume()) {
               CriteriaTriggers.RIGHT_CLICK_BLOCK_WITH_ITEM.test(playerIn, blockpos, itemstack);
               return actionresulttype;
            }
         }

         if (!stackIn.isEmpty() && !playerIn.getCooldownTracker().hasCooldown(stackIn.getItem())) {
            ItemUseContext itemusecontext = new ItemUseContext(playerIn, handIn, blockRaytraceResultIn);
            ActionResultType actionresulttype1;
            if (this.isCreative()) {
               int i = stackIn.getCount();
               actionresulttype1 = stackIn.onItemUse(itemusecontext);
               stackIn.setCount(i);
            } else {
               actionresulttype1 = stackIn.onItemUse(itemusecontext);
            }

            if (actionresulttype1.isSuccessOrConsume()) {
               CriteriaTriggers.RIGHT_CLICK_BLOCK_WITH_ITEM.test(playerIn, blockpos, itemstack);
            }

            return actionresulttype1;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   public void setWorld(ServerWorld serverWorld) {
      this.world = serverWorld;
   }
}
