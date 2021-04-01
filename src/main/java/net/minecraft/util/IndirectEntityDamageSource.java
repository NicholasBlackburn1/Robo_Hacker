package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IndirectEntityDamageSource extends EntityDamageSource {
   private final Entity indirectEntity;

   public IndirectEntityDamageSource(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn) {
      super(damageTypeIn, source);
      this.indirectEntity = indirectEntityIn;
   }

   @Nullable
   public Entity getImmediateSource() {
      return this.damageSourceEntity;
   }

   @Nullable
   public Entity getTrueSource() {
      return this.indirectEntity;
   }

   public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
      ITextComponent itextcomponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack itemstack = this.indirectEntity instanceof LivingEntity ? ((LivingEntity)this.indirectEntity).getHeldItemMainhand() : ItemStack.EMPTY;
      String s = "death.attack." + this.damageType;
      String s1 = s + ".item";
      return !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TranslationTextComponent(s1, entityLivingBaseIn.getDisplayName(), itextcomponent, itemstack.getTextComponent()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), itextcomponent);
   }
}
