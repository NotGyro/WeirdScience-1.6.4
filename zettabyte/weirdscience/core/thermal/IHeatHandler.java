package zettabyte.weirdscience.core.thermal;

import net.minecraft.world.World;

/**
 * Note: "heat" is used where "temperature" would be more appropriate
 * to save on code length and thereby improve readability.
 */
public interface IHeatHandler {
	/**
	 * @return Absolute, not relative to ambient temperature in biome.
	 */
	int getHeat(World world, int x, int y, int z);
	/**
	 * First set of coordinates is the position of our block.
	 * The second set of coordinates is the position of the block
	 * we are checking - the block affected by this one's heat.
	 */
	int getHeatAt(World world, int x, int y, int z, int targetX, int targetY, int targetZ);
	
	/**
	 * Takes in the largest (absolute, distance-from-zero) value to
	 * look at, to see if we can alter this block's temperature by
	 * that value.
	 * @return The greatest (distance-from-zero) value up to maxAlter
	 * we can shift this IHeatHandler's temperature by.
	 */
	int AmountAlterHeat(int maxAlter, World world, int x, int y, int z); 
	
	/**
	 * Attempts to shift our IHeatHandler's temperature
	 * by maxAlter.
	 * Is implied to call AmountAlterHeat under the hood.
	 * @return The value by which our temperature was
	 * ultimately altered.
	 */
	int AlterHeat(int maxAlter, World world, int x, int y, int z);
}
