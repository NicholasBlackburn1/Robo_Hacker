package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShieldRecipes extends SpecialRecipe {
   public ShieldRecipes(ResourceLocation idIn) {
      super(idIn);
   }

   public boolean matches(CraftingInventory inv, World worldIn) {
      ItemStack itemstack = ItemStack.EMPTY;
      ItemStack itemstack1 = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack2 = inv.getStackInSlot(i);
         if (!itemstack2.isEmpty()) {
            if (itemstack2.getItem() instanceof BannerItem) {
               if (!itemstack1.isEmpty()) {
                  return false;
               }

               itemstack1 = itemstack2;
            } else {
               if (itemstack2.getItem() != Items.SHIELD) {
                  return false;
               }

               if (!itemstack.isEmpty()) {
                  return false;
               }

               if (itemstack2.getChildTag("BlockEntityTag") != null) {
                  return false;
               }

               itemstack = itemstack2;
            }
         }
      }

      return !itemstack.isEmpty() && !itemstack1.isEmpty();
   }

   public ItemStack getCraftingResult(CraftingInventory inv) {
      ItemStack itemstack = ItemStack.EMPTY;
      ItemStack itemstack1 = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack2 = inv.getStackInSlot(i);
         if (!itemstack2.isEmpty()) {
            if (itemstack2.getItem() instanceof BannerItem) {
               itemstack = itemstack2;
            } else if (itemstack2.getItem() == Items.SHIELD) {
               itemstack1 = itemstack2.copy();
            }
         }
      }

      if (itemstack1.isEmpty()) {
         return itemstack1;
      } else {
         CompoundNBT compoundnbt = itemstack.getChildTag("BlockEntityTag");
         CompoundNBT compoundnbt1 = compoundnbt == null ? new CompoundNBT() : compoundnbt.copy();
         compoundnbt1.putInt("Base", ((BannerItem)itemstack.getItem()).getColor().getId());
         itemstack1.setTagInfo("BlockEntityTag", compoundnbt1);
         return itemstack1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_SHIELD;
   }
}
