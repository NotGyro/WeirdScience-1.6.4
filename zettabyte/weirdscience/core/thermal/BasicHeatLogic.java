package zettabyte.weirdscience.core.thermal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

//For use with tile entities and the like, which have instance-specific heat information.
public class BasicHeatLogic implements IHeatHandler {
	
	protected int temperature = 18;
	protected int range = 8;
	protected int rangeSquared = range*range;
	
	@Override
	public int getHeat(World world, int x, int y, int z) {
		return temperature;
	}

	@Override
	public int getHeatAt(World world, int x, int y, int z, int targetX,
			int targetY, int targetZ) {
		int diffX = x - targetX;
		int diffY = y - targetY;
		int diffZ = z - targetZ;
		int squaredDist = (diffX*diffX + diffY*diffY + diffZ*diffZ);
		//Squared distance check, to make sure it's within range.
		if(squaredDist <= rangeSquared) {
			return (int)(((float)temperature) * getFalloffFactor((int)Math.sqrt(squaredDist)));
		}
		else {
			return 0;
		}
	}
	//For basic linear falloff.
	protected float getFalloffFactor(int distance){
		return ((float)distance)/((float)range);
	}

	@Override
	public int AmountAlterHeat(int maxAlter, World world, int x, int y, int z) {
		int amt = maxAlter;
		return amt;
	}

	@Override
	public int AlterHeat(int maxAlter, World world, int x, int y, int z) {
		temperature += AmountAlterHeat(maxAlter, world, x, y, z);
		return maxAlter;
	}
	
	public void setHeat(int temp) {
		temperature = temp;
	}
	
	//Wherein heat balancing code happens. Args are world and our position. 
	public void TickHeat(World world, int x, int y, int z) {
		//TODO
	}

}
