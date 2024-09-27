package model;

import java.awt.image.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Structure {
	private final static byte TAG_End = 0x00;
	private final static byte TAG_Byte = 0x01;
	private final static byte TAG_Short = 0x02;
	private final static byte TAG_Int = 0x03;
	private final static byte TAG_Long = 0x04;
	private final static byte TAG_Float = 0x05;
	private final static byte TAG_Double = 0x06;
	private final static byte TAG_Byte_Array = 0x07;
	private final static byte TAG_String = 0x08;
	private final static byte TAG_List = 0x09;
	private final static byte TAG_Compound = 0x0a;
	private final static byte TAG_Int_Array = 0x0b;
	private final static byte TAG_Long_Array = 0x0c;
	
	public static String[] mapMaterialPalette = { "minecraft:air", "minecraft:grass_block", "minecraft:sandstone", "minecraft:cobweb", "minecraft:redstone_block", "minecraft:packed_ice",
			"minecraft:iron_block", "minecraft:oak_leaves", "minecraft:white_wool", "minecraft:clay", "minecraft:dirt", "minecraft:stone", "minecraft:water", "minecraft:oak_planks", "minecraft:quartz_block",
			"minecraft:orange_wool", "minecraft:magenta_wool", "minecraft:light_blue_wool", "minecraft:yellow_wool", "minecraft:lime_wool", "minecraft:pink_wool", "minecraft:gray_wool",
			"minecraft:light_gray_wool", "minecraft:cyan_wool", "minecraft:purple_wool", "minecraft:blue_wool", "minecraft:brown_wool", "minecraft:green_wool", "minecraft:red_wool",
			"minecraft:black_wool", "minecraft:gold_block", "minecraft:diamond_block", "minecraft:lapis_block", "minecraft:emerald_block", "minecraft:podzol",
			"minecraft:netherrack", "minecraft:white_terracotta", "minecraft:orange_terracotta", "minecraft:magenta_terracotta", "minecraft:light_blue_terracotta",
			"minecraft:yellow_terracotta", "minecraft:lime_terracotta", "minecraft:pink_terracotta", "minecraft:gray_terracotta", "minecraft:light_gray_terracotta",
			"minecraft:cyan_terracotta", "minecraft:purple_terracotta", "minecraft:blue_terracotta", "minecraft:brown_terracotta", "minecraft:green_terracotta",
			"minecraft:red_terracotta", "minecraft:black_terracotta", "minecraft:crimson_nylium", "minecraft:crimson_planks", "minecraft:crimson_hyphae", "minecraft:warped_nylium",
			"minecraft:warped_planks", "minecraft:warped_hyphae", "minecraft:warped_wart_block", "minecraft:cobbled_deepslate", "minecraft:raw_iron_block",
			"minecraft:glow_lichen[down=true,east=false,north=false,south=false,up=false,waterlogged=false,west=false]" };
	
	// default colours for Minecraft maps, minus the shades.
			public static int[] defaultMapPalette = { 0x7FB238, 0xF7E9A3, 0xC7C7C7, 0xFF0000, 0xA0A0FF, 0xA7A7A7, 0x007C00,
					0xFFFFFF, 0xA4A8B8, 0x976D4D, 0x707070, 0x4040FF, 0x8F7748, 0xFFFCF5, 0xD87F33, 0xB24CD8, 0x6699D8,
					0xE5E533, 0x7FCC19, 0xF27FA5, 0x4C4C4C, 0x999999, 0x4C7F99, 0x7F3FB2, 0x334CB2, 0x664C33, 0x667F33,
					0x993333, 0x191919, 0xFAEE4D, 0x5CDBD5, 0x4A80FF, 0x00D93A, 0x815631, 0x700200, 0xD1B1A1, 0x9F5224,
					0x95576C, 0x706C8A, 0xBA8524, 0x677535, 0xA04D4E, 0x392923, 0x876B62, 0x575C5C, 0x7A4958, 0x4C3E5C,
					0x4C3223, 0x4C522A, 0x8E3C2E, 0x251610, 0xBD3031, 0x943F61, 0x5C191D, 0x167E86, 0x3A8E8C, 0x562C3E,
					0x14B421, 0x646464, 0xD8AF93, 0x7FA796 };
	
	public static int[] mapPalette = expandPalette(defaultMapPalette);
	
	static Map<Integer, String> colourToMaterial = constructMap();
	
	static Map<Integer, MapBlockInfo> colourToBlockHeightmap = constructBlockHeightmap();
	
	private static Map<Integer, MapBlockInfo> constructBlockHeightmap(){
		Map<Integer, MapBlockInfo> map = new HashMap<>();
		for (int i = 1; i < Structure.mapMaterialPalette.length; i++) {
			map.put(mapPalette[3 * (i - 1) + 0], new MapBlockInfo((byte) i, Elevation.LOWER));
			map.put(mapPalette[3 * (i - 1) + 1], new MapBlockInfo((byte) i, Elevation.LEVEL));
			map.put(mapPalette[3 * (i - 1) + 2], new MapBlockInfo((byte) i, Elevation.RAISED));
		}
		return map;
	}
	
	private static int[] expandPalette(int[] palette) {
		int[] newPalette = new int[palette.length * 3];
		for (int i = 0; i < palette.length; i++) {
			int red = ImageClass.redRGB(palette[i]);
			int green = ImageClass.greenRGB(palette[i]);
			int blue = ImageClass.blueRGB(palette[i]);
			newPalette[3 * i + 0] = ImageClass.values2RGB((red * 180) / 255, (green * 180) / 255, (blue * 180) / 255);
			newPalette[3 * i + 1] = ImageClass.values2RGB((red * 220) / 255, (green * 220) / 255, (blue * 220) / 255);
			newPalette[3 * i + 2] = ImageClass.values2RGB(red, green, blue);
		}
		return newPalette;
	}

	private static Map<Integer, String> constructMap() {
		Map<Integer, String> map = new HashMap<>();
		for (int i = 1; i < Structure.mapMaterialPalette.length; i++) {
			map.put(mapPalette[3 * (i - 1) + 0], Structure.mapMaterialPalette[i] + ", " + "Lower");
			map.put(mapPalette[3 * (i - 1) + 1], Structure.mapMaterialPalette[i] + ", " + "Level");
			map.put(mapPalette[3 * (i - 1) + 2], Structure.mapMaterialPalette[i] + ", " + "Raised");
		}
		return map;
	}
	
	String[] palette;
	int height, length, width;
	int dataVersion;
	byte[] blockData;
	int xOffsetWE, yOffsetWE, zOffsetWE;
	
	public Structure(int xLen, int yLen, int zLen) {
		palette = mapMaterialPalette;
		width = xLen;
		height = yLen;
		length = zLen;
		dataVersion = 2730; //version for 1.17.1
		blockData = new byte[width * height * length];
		for (int i = 0; i < blockData.length; i++) {
			blockData[i] = 2;
		}
		xOffsetWE = 0;
		yOffsetWE = 0;
		zOffsetWE = 0;
	}
	
	public Structure(BufferedImage img) {
		palette = mapMaterialPalette;
		dataVersion = 2730; //version for 1.17.1
		
		MapBlockInfo[] heightMap = new MapBlockInfo[img.getWidth() * img.getHeight()];
		for (int i = 0; i < heightMap.length; i++) {
			heightMap[i] = colourToBlockHeightmap.get(img.getRGB(i % img.getWidth(), i / img.getWidth()) & 0x00FFFFFF);
		}
		
		int[] maxDepth = new int[img.getWidth()];
		int[] maxHeight = new int[img.getWidth()];
		
		for (int i = 0; i < img.getWidth(); i++) {
			maxDepth[i] = 0;
			maxHeight[i] = 0;
			int accumulator = 0;
			for (int j = 0; j < img.getHeight(); j++) {
				if (heightMap[i + j * img.getWidth()].elevation == Elevation.LOWER) {
					accumulator++;
				}
				else if (heightMap[i + j * img.getWidth()].elevation == Elevation.RAISED) {
					accumulator--;
				}
				if (accumulator > maxDepth[i]) {
					maxDepth[i] = accumulator;
				}
				if (accumulator < maxHeight[i]) {
					maxHeight[i] = accumulator;
				}
			}
		}
		
		width = img.getWidth();
		height = 0;
		length = img.getHeight() + 1;
		
		for (int i = 0; i < img.getWidth(); i++) {
			if (maxDepth[i] - maxHeight[i] + 1 > height) {
				height = maxDepth[i] - maxHeight[i] + 1;
			}
		}
		
		if (height > 256) {
			throw new RuntimeException("Height of structure exceeds maximum height limit.");
		}
		
		blockData = new byte[width * height * length];
		for (int i = 0; i < blockData.length; i++) {
			blockData[i] = 0;
		}
		
		for (int i = 0; i < img.getWidth(); i++) {
			int accumulator = maxDepth[i];
			blockData[i + accumulator * width * length] = 4; //set top line of image to redstone blocks to make shading work properly
			for (int j = 0; j < img.getHeight(); j++) {
				switch(heightMap[i + j * img.getWidth()].elevation) {
				case LOWER:
					accumulator--;
					break;
				case LEVEL:
					break;
				case RAISED:
					accumulator++;
					break;
				}
				int x = i;
				int y = accumulator;
				int z = j + 1;
				blockData[x + width * (z + length * y)] = heightMap[i + j * img.getWidth()].blockType;
				
				//check if block is glow lichen
				if (heightMap[i + j * img.getWidth()].blockType == 61) {
					y = accumulator - 1;
					if (y < 0) {
						continue;
					}
					//set block below to white wool
					blockData[x + width * (z + length * y)] = 8;
				}
			}
		}
		
		xOffsetWE = 0;
		yOffsetWE = 0;
		zOffsetWE = -1;
	}
	
	
	
	public void exportStructure(String fileName) throws IOException {
		File tempFile = File.createTempFile("temp_struct", ".nbt");
		tempFile.deleteOnExit();
		FileOutputStream fout = new FileOutputStream(tempFile);
		DataOutputStream dout = new DataOutputStream(fout);
		
		//write the root tag Schematic to file
		dout.write(TAG_Compound);
		dout.writeUTF("Schematic");
		
		//write the int PaletteMax to file
		dout.writeByte(TAG_Int);
		dout.writeUTF("PaletteMax");
		dout.writeInt(palette.length);
		
		//write the compound tag Palette to file
		dout.write(TAG_Compound);
		dout.writeUTF("Palette");
		for (int i = 0; i < palette.length; i++) {
			dout.write(TAG_Int);
			dout.writeUTF(palette[i]);
			dout.writeInt(i);
		}
		//close the palette compound tag
		dout.write(TAG_End);
		
		//write an empty BlockEntities list
		dout.write(TAG_List);
		dout.writeUTF("BlockEntities");
		dout.write(TAG_Compound);
		dout.writeInt(0);
		
		//write width, height, length
		dout.write(TAG_Short);
		dout.writeUTF("Width");
		dout.writeShort(width);
		
		dout.write(TAG_Short);
		dout.writeUTF("Height");
		dout.writeShort(height);
		
		dout.write(TAG_Short);
		dout.writeUTF("Length");
		dout.writeShort(length);
		
		//write version
		dout.write(TAG_Int);
		dout.writeUTF("DataVersion");
		dout.writeInt(dataVersion);
		
		dout.write(TAG_Int); //no idea what this one is for, but it's apparently important
		dout.writeUTF("Version");
		dout.writeInt(1);
		
		//write block data
		dout.write(TAG_Byte_Array);
		dout.writeUTF("BlockData");
		dout.writeInt(blockData.length);
		dout.write(blockData);
		
		//write metadata
		dout.write(TAG_Compound);
		dout.writeUTF("Metadata");
		
		//write worldedit offset parameters
		dout.write(TAG_Int);
		dout.writeUTF("WEOffsetX");
		dout.writeInt(xOffsetWE);
		
		dout.write(TAG_Int);
		dout.writeUTF("WEOffsetY");
		dout.writeInt(yOffsetWE);
		
		dout.write(TAG_Int);
		dout.writeUTF("WEOffsetZ");
		dout.writeInt(zOffsetWE);
		
		//close metadata
		dout.write(TAG_End);
		
		//close the schematic compound tag
		dout.write(TAG_End);
		
		fout.close();
		dout.close();
		
		File destination = new File(fileName);
		Utilities.compressGzipFile(tempFile, destination);
	}
}
