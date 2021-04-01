package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatLieOnBedGoal extends MoveToBlockGoal {
   private final CatEntity cat;

   public CatLieOnBedGoal(CatEntity catIn, double speed, int length) {
      super(catIn, speed, length, 6);
      this.cat = catIn;
      this.field_203112_e = -2;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.func_213416_eg() && super.shouldExecute();
   }

   public void startExecuting() {
      super.startExecuting();
      this.cat.setSleeping(false);
   }

   protected int getRunDelay(CreatureEntity creatureIn) {
      return 40;
   }

   public void resetTask() {
      super.resetTask();
      this.cat.func_213419_u(false);
   }

   public void tick() {
      super.tick();
      this.cat.setSleeping(false);
      if (!this.getIsAboveDestination()) {
         this.cat.func_213419_u(false);
      } else if (!this.cat.func_213416_eg()) {
         this.cat.func_213419_u(true);
      }

   }

   protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
      return worldIn.isAirBlock(pos.up()) && worldIn.getBlockState(pos).getBlock().isIn(BlockTags.BEDS);
   }
}
