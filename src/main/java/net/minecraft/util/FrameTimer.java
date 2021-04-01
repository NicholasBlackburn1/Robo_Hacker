package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FrameTimer {
   private final long[] frames = new long[240];
   private int lastIndex;
   private int counter;
   private int index;

   public void addFrame(long runningTime) {
      this.frames[this.index] = runningTime;
      ++this.index;
      if (this.index == 240) {
         this.index = 0;
      }

      if (this.counter < 240) {
         this.lastIndex = 0;
         ++this.counter;
      } else {
         this.lastIndex = this.parseIndex(this.index + 1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getLineHeight(long valueIn, int scale, int divisor) {
      double d0 = (double)valueIn / (double)(1000000000L / (long)divisor);
      return (int)(d0 * (double)scale);
   }

   @OnlyIn(Dist.CLIENT)
   public int getLastIndex() {
      return this.lastIndex;
   }

   @OnlyIn(Dist.CLIENT)
   public int getIndex() {
      return this.index;
   }

   public int parseIndex(int rawIndex) {
      return rawIndex % 240;
   }

   @OnlyIn(Dist.CLIENT)
   public long[] getFrames() {
      return this.frames;
   }
}
