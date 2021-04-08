/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package blackburn.utils;

import java.util.ArrayList;

import blackburn.BlackburnConst;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftGame;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public enum BlockUtils {
	;

	private static final MinecraftGame MC = BlackburnConst.mc.getMinecraftGame();

	public static BlockState getState(BlockPos pos) {
		return BlackburnConst.mc.world.getBlockState(pos);
	}

	public static Block getBlock(BlockPos pos) {
		return getState(pos).getBlock();
	}

	public static int getId(BlockPos pos) {
		return Block.getStateId(getState(pos));
	}

	public static int getName(BlockPos pos) {
		return getName(getBlock(pos));
	}

	public static int getName(Block block) {
		return Registry.BLOCK.getId(block);
	}

	public static ArrayList<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
		ArrayList<BlockPos> blocks = new ArrayList<>();

		BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()),
				Math.min(from.getZ(), to.getZ()));
		BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()),
				Math.max(from.getZ(), to.getZ()));

		for (int x = min.getX(); x <= max.getX(); x++)
			for (int y = min.getY(); y <= max.getY(); y++)
				for (int z = min.getZ(); z <= max.getZ(); z++)
					blocks.add(new BlockPos(x, y, z));

		return blocks;
	}
}
