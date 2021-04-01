package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FallingBlock extends Block {
   public FallingBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.getFallDelay());
   }

   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.getFallDelay());
      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (canFallThrough(worldIn.getBlockState(pos.down())) && pos.getY() >= 0) {
         FallingBlockEntity fallingblockentity = new FallingBlockEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
         this.onStartFalling(fallingblockentity);
         worldIn.addEntity(fallingblockentity);
      }
   }

   protected void onStartFalling(FallingBlockEntity fallingEntity) {
   }

   protected int getFallDelay() {
      return 2;
   }

   public static boolean canFallThrough(BlockState state) {
      Material material = state.getMaterial();
      return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
   }

   public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
   }

   public void onBroken(World worldIn, BlockPos pos, FallingBlockEntity fallingBlock) {
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (rand.nextInt(16) == 0) {
         BlockPos blockpos = pos.down();
         if (canFallThrough(worldIn.getBlockState(blockpos))) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            worldIn.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, stateIn), d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
      return -16777216;
   }
}
