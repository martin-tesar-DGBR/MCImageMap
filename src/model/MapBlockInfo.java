package model;

class MapBlockInfo {
	byte blockType;
	Elevation elevation;
	
	MapBlockInfo(byte blockType, Elevation elevation){
		this.blockType = blockType;
		this.elevation = elevation;
	}
}
