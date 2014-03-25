package zettabyte.weirdscience.core.thermal;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutablePair;

import zettabyte.weirdscience.cofh.util.BlockCoord;
import zettabyte.weirdscience.cofh.util.ChunkCoord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

//For use with tile entities and the like, which have instance-specific heat information.
public class BasicHeatLogic implements IHeatHandler {
	
	protected HeatManager ourManager = null;
	protected int temperature = 18;
	protected int range = 8;
	protected int rangeSquared = range*range;
	public int transferRate = 32000;
	
	protected ArrayList<IHeatHandler> nearbyHeatHandlers = new ArrayList<IHeatHandler>(24);
	
	public TileEntity master = null;
	
	public void setManager(HeatManager man) {
		ourManager = man;
	}
	@Override
	public int getHeat() {
		return temperature;
	}

	@Override
	public int getHeatAt(int targetX, int targetY, int targetZ) {
		int diffX = getPosition().x - targetX;
		int diffY = getPosition().y - targetY;
		int diffZ = getPosition().z - targetZ;
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
	public int AmountAlterHeat(int maxAlter) {
		int amt = maxAlter;
		return amt;
	}

	@Override
	public int AlterHeat(int maxAlter) {
		temperature += AmountAlterHeat(maxAlter);
		return maxAlter;
	}
	
	public void setHeat(int temp) {
		temperature = temp;
	}
	
	//Wherein heat balancing code happens. Args are world and our position. 
	public void TickHeat() {
	}
	
	public void LoseHeatAmbient() {
		
	}
	
	//Must be called before 
	public void init() { 

		//Sanity Chex: my favorite cereal.
		if(ourManager != null) {
			for(int offsetX = -1; offsetX <= 1; ++offsetX) {
				for(int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
					//Set handlers to those gotten for this chunk from this world's heat manager.
					//Bitshift right 4 to convert to chunk coords.
					ArrayList<IHeatHandler> handlers = 
							ourManager.getHandlersInChunk(new ChunkCoord(
									(getPosition().x >> 4) + offsetX, (getPosition().z >> 4) + offsetZ));
					for(int i = 0; i < handlers.size(); i++) {
						//Don't balance out heat with ourselves. That is a waste of time.
						if(handlers.get(i) != this) {
							//Heat balancing code goes here.
							
						}
					}
				}
			}
		}
	}
	@Override
	public void NotifyNearby(IHeatHandler other) {
		if(other != null) {
			nearbyHeatHandlers.add(other);
		}
	}
	@Override
	public BlockCoord getPosition() {
		if(master != null) {
			return new BlockCoord(master.xCoord, master.yCoord, master.zCoord);
		}
		return new BlockCoord(0,0,0);
	}
	@Override
	public void onKill() {
		for(int i = 0; i < this.nearbyHeatHandlers.size(); ++i) {
			nearbyHeatHandlers.get(i).NearbyRemoved(this);
		}
	}
	@Override
	public void NearbyRemoved(IHeatHandler other) {
		if(other != null) {
			nearbyHeatHandlers.remove(other);
		}
	}

}
