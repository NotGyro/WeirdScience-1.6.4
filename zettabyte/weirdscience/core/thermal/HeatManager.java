package zettabyte.weirdscience.core.thermal;

import java.util.ArrayList;
import java.util.HashMap;

import zettabyte.weirdscience.cofh.util.BlockCoord;
import zettabyte.weirdscience.cofh.util.ChunkCoord;
import net.minecraft.world.World;

public class HeatManager {

	private final World world;
	public HeatManager(World w) {
		world = w;
	}
	
	//Apologies for the nested types. Was necessary in this case.
	//Key: Coordinate of the chunk to investigate.
	//ArrayList: Heat handlers in the chunk.
	private final HashMap<ChunkCoord, ArrayList<IHeatHandler>> handlerChunks
					= new HashMap<ChunkCoord, ArrayList<IHeatHandler>>(128);
	
	
	
	public final int getHeatAt(int x, int y, int z) {
		BlockCoord blockCoord = new BlockCoord(x, y, z);
		ChunkCoord chunkCoord = new ChunkCoord(blockCoord);
		//No entries for this chunk, therefore, no heat handlers within.
		if(!handlerChunks.containsKey(chunkCoord)) {
			return getAmbientHeatAt(x, y, z);
		}
		else {
			int total = 0;
			//Get heat information for this chunk and adjacent.
			ChunkCoord current = new ChunkCoord(chunkCoord.chunkX, chunkCoord.chunkZ);
			for(int offsetX = -1; offsetX <= 1; ++offsetX) {
				for(int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
					current.chunkX = chunkCoord.chunkX + offsetX; 
					current.chunkZ = chunkCoord.chunkZ + offsetZ;
					total += getHeatFromChunk(current, x, y, z);
				}
			}
			return total + getAmbientHeatAt(x, y, z);
		}
	}
	//Avoiding some copy+paste
	private final int getHeatFromChunk(ChunkCoord chunk, int x, int y, int z) {
		//Check to be sure that the target block is loaded.
		ArrayList<IHeatHandler> handlers = getHandlersInChunk(chunk);
		if(handlers == null) {
			return this.getAmbientHeatAt(x, y, z);
		}
		int total = 0;
		//Iterate through aforementioned list.
		//Assume the IHeatHandler can deal with its own range mechanics.
		for(int i = 0; i < handlers.size(); ++i) {
			IHeatHandler current = handlers.get(i);

			//Make sure our heat producer is loaded.
			if(!world.blockExists(current.getPosition().x, current.getPosition().y, current.getPosition().z)) {
				throw new IllegalArgumentException(
						"Attempted to get heat information produced by block at "
							+ x + "," + y + "," + z + ", which does not exist.");
			}
			total += current.getHeatAt(x, y, z);
		}
		return total;
	}
	
	//Gets the ambient heat from the biome at the specified block location.
	public final int getAmbientHeatAt(int x, int y, int z) {
		//TODO
		return 18;
	}
	
	public final void RegisterHeatBlock(IHeatHandler toReg) {
		BlockCoord blockCoord = new BlockCoord(toReg.getPosition().x, toReg.getPosition().y, toReg.getPosition().z);
		ChunkCoord chunkCoord = new ChunkCoord(blockCoord);

		ArrayList<IHeatHandler> handlers = null;
		if(!handlerChunks.containsKey(chunkCoord)) {
			handlers = new ArrayList<IHeatHandler>(32);
			handlerChunks.put(chunkCoord, handlers);
		}
		else {
			handlers = handlerChunks.get(chunkCoord);
		}
		
		handlers.add(toReg);
	}	
	public final void UnregisterHeatBlock(IHeatHandler toReg) {
		BlockCoord blockCoord = new BlockCoord(toReg.getPosition().x, toReg.getPosition().y, toReg.getPosition().z);
		ChunkCoord chunkCoord = new ChunkCoord(blockCoord);

		ArrayList<IHeatHandler> handlers = null;
		if(!handlerChunks.containsKey(chunkCoord)) {
			//There's no way this exists to unreg to begin with.
			return;
		}
		else {
			handlers = handlerChunks.get(chunkCoord);
		}
		for(int i = 0; i < handlers.size(); ++i) {
			//Compare position
			if(((handlers.get(i).getPosition().x == toReg.getPosition().x) && 
					(handlers.get(i).getPosition().y == toReg.getPosition().y)) && 
					(handlers.get(i).getPosition().x == toReg.getPosition().z)) {
				//Remove this element from the list.
				handlers.remove(i);
			}
		}	
	}
	
	public ArrayList<IHeatHandler> getHandlersInChunk(ChunkCoord chunk) {
		//Check to be sure that the target block is loaded.
		if(!world.blockExists(chunk.chunkX << 4, 64, chunk.chunkZ << 4)) {
			throw new IllegalArgumentException(
					"Attempted to get heat information for chunk at " + chunk.chunkX + "," + chunk.chunkZ + ", which is not loaded.");
		}
		return handlerChunks.get(chunk);
	}
}
