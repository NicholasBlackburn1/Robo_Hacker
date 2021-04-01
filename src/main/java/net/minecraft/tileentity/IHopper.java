package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public interface IHopper extends IInventory {
   VoxelShape INSIDE_BOWL_SHAPE = Block.makeCuboidShape(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   VoxelShape BLOCK_ABOVE_SHAPE = Block.makeCuboidShape(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
   VoxelShape COLLECTION_AREA_SHAPE = VoxelShapes.or(INSIDE_BOWL_SHAPE, BLOCK_ABOVE_SHAPE);

   default VoxelShape getCollectionArea() {
      return COLLECTION_AREA_SHAPE;
   }

   @Nullable
   World getWorld();

   double getXPos();

   double getYPos();

   double getZPos();
}
