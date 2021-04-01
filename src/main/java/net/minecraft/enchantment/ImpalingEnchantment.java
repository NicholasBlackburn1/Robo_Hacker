package net.minecraft.enchantment;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;

public class ImpalingEnchantment extends Enchantment {
   public ImpalingEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.TRIDENT, slots);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + (enchantmentLevel - 1) * 8;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 20;
   }

   public int getMaxLevel() {
      return 5;
   }

   public float calcDamageByCreature(int level, CreatureAttribute creatureType) {
      return creatureType == CreatureAttribute.WATER ? (float)level * 2.5F : 0.0F;
   }
}
