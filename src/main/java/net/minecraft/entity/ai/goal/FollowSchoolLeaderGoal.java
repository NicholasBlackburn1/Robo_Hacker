package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;

public class FollowSchoolLeaderGoal extends Goal {
   private final AbstractGroupFishEntity taskOwner;
   private int navigateTimer;
   private int cooldown;

   public FollowSchoolLeaderGoal(AbstractGroupFishEntity taskOwnerIn) {
      this.taskOwner = taskOwnerIn;
      this.cooldown = this.getNewCooldown(taskOwnerIn);
   }

   protected int getNewCooldown(AbstractGroupFishEntity taskOwnerIn) {
      return 200 + taskOwnerIn.getRNG().nextInt(200) % 20;
   }

   public boolean shouldExecute() {
      if (this.taskOwner.isGroupLeader()) {
         return false;
      } else if (this.taskOwner.hasGroupLeader()) {
         return true;
      } else if (this.cooldown > 0) {
         --this.cooldown;
         return false;
      } else {
         this.cooldown = this.getNewCooldown(this.taskOwner);
         Predicate<AbstractGroupFishEntity> predicate = (p_212824_0_) -> {
            return p_212824_0_.canGroupGrow() || !p_212824_0_.hasGroupLeader();
         };
         List<AbstractGroupFishEntity> list = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), this.taskOwner.getBoundingBox().grow(8.0D, 8.0D, 8.0D), predicate);
         AbstractGroupFishEntity abstractgroupfishentity = list.stream().filter(AbstractGroupFishEntity::canGroupGrow).findAny().orElse(this.taskOwner);
         abstractgroupfishentity.func_212810_a(list.stream().filter((p_212823_0_) -> {
            return !p_212823_0_.hasGroupLeader();
         }));
         return this.taskOwner.hasGroupLeader();
      }
   }

   public boolean shouldContinueExecuting() {
      return this.taskOwner.hasGroupLeader() && this.taskOwner.inRangeOfGroupLeader();
   }

   public void startExecuting() {
      this.navigateTimer = 0;
   }

   public void resetTask() {
      this.taskOwner.leaveGroup();
   }

   public void tick() {
      if (--this.navigateTimer <= 0) {
         this.navigateTimer = 10;
         this.taskOwner.moveToGroupLeader();
      }
   }
}
