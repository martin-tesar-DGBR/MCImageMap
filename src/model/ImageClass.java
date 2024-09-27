package model;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

public class ImageClass {

	private final static int MAP_RESOLUTION = 128;
	private final static int MINECRAFT_RESOLUTION = 16;

	private final static BufferedImage PAINTING_BORDER = initializePaintingBorder();

	private static char[] luminanceMap = { '@', '$', '#', '*', '!', '=', ';', ':', '~', '-', ',', '.', ' ' };

	public static int redRGB(int colour) {
		return (colour >> 16) & (0b11111111);
	}

	private static BufferedImage initializePaintingBorder() {
		BufferedImage ret;
		try {
			ret = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("images/birch_planks.png"));
		}
		catch (IOException e) {
			ret = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		}

		return ret;
	}

	public static int greenRGB(int colour) {
		return (colour >> 8) & (0b11111111);
	}

	public static int blueRGB(int colour) {
		return colour & (0b11111111);
	}

	public static int values2RGB(int r, int g, int b) {
		return (r << 16) | (g << 8) | (b);
	}

	/**
	 * gives the hue of an RGB colour, in degrees.
	 */
	public static double[] RGB2HSL(int colour) {
		double hue = 0;
		double saturation = 0;
		double luma = RGB2Luma(colour);

		double red = redRGB(colour) / 255.0;
		double green = greenRGB(colour) / 255.0;
		double blue = blueRGB(colour) / 255.0;
		double max = (red > green && red > blue) ? red : (green > red && green > blue) ? green : blue; // lmao
		double min = (red < green && red < blue) ? red : (green < red && green < blue) ? green : blue; // this is
																										// horrific
		double c = max - min;
		if (c == 0) {
			hue = 0;
		}
		else if (max == red) {
			hue = (green - blue) / c + (green < blue ? 6 : 0);
		}
		else if (max == green) {
			hue = (blue - red) / c + 2;
		}
		else if (max == blue) {
			hue = (red - green) / c + 4;
		}
		else {
			throw new RuntimeException("you gotta use epsilons bruv");
		}
		hue *= 60;

		if (c == 0) {
			saturation = 0;
		}
		else {
			saturation = c / (1 - Math.abs(2 * luma - 1));
		}

		return new double[] { hue, saturation, luma };
	}

	/**
	 * converts an RGB value to a luminance value between 0 and 1
	 */
	public static double RGB2Luma(int colour) {
		return 0.2126 * redRGB(colour) / 255.0 + 0.7152 * greenRGB(colour) / 255.0 + 0.0722 * blueRGB(colour) / 255.0;
	}

	public static char index2Braille(int index) {
		/*
		 * Unicode Given 01 08 01 10 02 10 02 20 04 20 04 40 40 80 08 80
		 */
		int newIndex = 0;
		for (int i = 0; i < 8; i++) {
			int bit = (index & (1 << i)) == 0 ? 0 : 1;
			switch (i) {
			case 3:
				newIndex |= bit << 6;
				break;
			case 4:
				newIndex |= bit << 3;
				break;
			case 5:
				newIndex |= bit << 4;
				break;
			case 6:
				newIndex |= bit << 5;
				break;
			default:
				newIndex |= bit << i;
				break;
			}
		}
		return (char) ('\u2800' + newIndex);
	}

	/**
	 * Takes an image, and outputs a file converting the image to ascii art with the
	 * specified size. If the size does not divide the width/height of the image
	 * evenly, it omits the very right and bottom edge.
	 * 
	 * @param imageName       File location of image to be read.
	 * @param outputName      File location of output as a .txt file.
	 * @param size            Width of the output, the function maintains the aspect
	 *                        ratio of the image in the .txt.
	 * @param normalizeColour Whether to normalize the brightness between the
	 *                        maximum and minimum occuring in the image, or to keep
	 *                        the original brightness.
	 */
	public static void convertToText(String imageName, String outputName, int size, boolean normalizeColour)
			throws IOException {
		BufferedImage img = ImageIO.read(new File(imageName));
		int imgW = img.getWidth();
		int imgH = img.getHeight();
		int chunkSize = imgW / size;
		int numChunksW = size;
		int numChunksH = imgH / chunkSize;

		double minLuma;
		double maxLuma;

		if (normalizeColour) {
			minLuma = 1.0;
			maxLuma = 0.0;
			for (int i = 0; i < imgH; i++) {
				for (int j = 0; j < imgW; j++) {
					double lumaValue = RGB2Luma(img.getRGB(j, i));
					if (lumaValue < minLuma) {
						minLuma = lumaValue;
					}
					if (lumaValue > maxLuma) {
						maxLuma = lumaValue;
					}
				}
			}
		}
		else {
			minLuma = 0.0;
			maxLuma = 1.0;
		}

		String textImg = "";
		for (int i = 0; i < numChunksH; i++) {
			for (int j = 0; j < numChunksW; j++) {
				// average colour in
				double averageR = 0.0;
				double averageG = 0.0;
				double averageB = 0.0;
				for (int n = 0; n < chunkSize * chunkSize; n++) {
					int colour = img.getRGB(j * chunkSize + (n % chunkSize), i * chunkSize + (n / chunkSize));
					averageR += redRGB(colour) * redRGB(colour) / (255.0f * 255.0f);
					averageG += greenRGB(colour) * greenRGB(colour) / (255.0f * 255.0f);
					averageB += blueRGB(colour) * blueRGB(colour) / (255.0f * 255.0f);
				}
				averageR = Math.sqrt(averageR / (chunkSize * chunkSize));
				averageG = Math.sqrt(averageG / (chunkSize * chunkSize));
				averageB = Math.sqrt(averageB / (chunkSize * chunkSize));
				// yay magic numbers
				double luma = 0.2126 * averageR + 0.7152 * averageG + 0.0722 * averageB;
				textImg += luminanceMap[(int) Math.round(1 / (maxLuma - minLuma) * (luma - minLuma) * 12)];
				textImg += luminanceMap[(int) Math.round(1 / (maxLuma - minLuma) * (luma - minLuma) * 12)];
			}
			textImg += '\n';
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputName));
		writer.write(textImg);
		writer.close();
	}

	/**
	 * Takes an image, and outputs a file converting the image to braille art with
	 * the specified size. If the size does not divide the width/height of the image
	 * evenly, it omits the very right and bottom edge.
	 * 
	 * @param imageName       File location of image to be read.
	 * @param outputName      File location of output as a .txt file.
	 * @param size            Width of the output in characters, the function
	 *                        maintains the aspect ratio of the image in the .txt.
	 * @param normalizeColour Whether to normalize the brightness between the
	 *                        maximum and minimum occuring in the image, or to keep
	 *                        the original brightness.
	 */
	public static void convertToBrailleText(String imageName, String outputName, int size, double lumaThreshold)
			throws IOException {
		BufferedImage img = ImageIO.read(new File(imageName));
		int imgW = img.getWidth();
		int imgH = img.getHeight();
		// each braille character is 4x2, so we have to double the sample size for the
		// width and quadruple it for the height
		int chunkSize = imgW / (2 * size);
		int numChunksW = 2 * size;
		int numChunksH = imgH / chunkSize;

		boolean[][] lumaMap = new boolean[numChunksH][numChunksW];
		String textImg = "";

		for (int i = 0; i < numChunksH; i++) {
			for (int j = 0; j < numChunksW; j++) {
				// average colour in
				double averageR = 0.0;
				double averageG = 0.0;
				double averageB = 0.0;
				for (int n = 0; n < chunkSize * chunkSize; n++) {
					int colour = img.getRGB(j * chunkSize + (n % chunkSize), i * chunkSize + (n / chunkSize));
					averageR += redRGB(colour) * redRGB(colour) / (255.0f * 255.0f);
					averageG += greenRGB(colour) * greenRGB(colour) / (255.0f * 255.0f);
					averageB += blueRGB(colour) * blueRGB(colour) / (255.0f * 255.0f);
				}
				averageR = Math.sqrt(averageR / (chunkSize * chunkSize));
				averageG = Math.sqrt(averageG / (chunkSize * chunkSize));
				averageB = Math.sqrt(averageB / (chunkSize * chunkSize));
				// yay magic numbers
				double luma = 0.2126 * averageR + 0.7152 * averageG + 0.0722 * averageB;
				lumaMap[i][j] = luma < lumaThreshold;
			}
		}

		for (int i = 0; i < numChunksH / 4; i++) {
			for (int j = 0; j < numChunksW / 2; j++) {
				int dots = 0;
				for (int index = 0; index < 8; index++) {
					if (lumaMap[i * 4 + (index % 4)][j * 2 + (index / 4)]) {
						dots |= 1 << index;
					}
				}
				textImg += dots == 0 ? ' ' : index2Braille(dots);
			}
			textImg += '\n';
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputName));
		writer.write(textImg);
		writer.close();
	}

	public static void convertToGrayscale(String imageName, String outputName) throws IOException {
		BufferedImage img = ImageIO.read(new File(imageName));
		for (int i = 0; i < img.getWidth() * img.getHeight(); i++) {
			int brightness = (int) Math.round(RGB2Luma(img.getRGB(i % img.getWidth(), i / img.getWidth())) * 255);
			int colour = (brightness << 16) | (brightness << 8) | (brightness);
			img.setRGB(i % img.getWidth(), i / img.getWidth(), colour);
		}
		ImageIO.write(img, "png", new File(outputName));
	}

	public static BufferedImage resizeImage(BufferedImage src, int w, int h) {
		BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics2D = resizedImage.createGraphics();
	    graphics2D.drawImage(src, 0, 0, w, h, null);
	    graphics2D.dispose();
	    return resizedImage;
		/*
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		int x, y;
		int ww = src.getWidth();
		int hh = src.getHeight();
		for (x = 0; x < w; x++) {
			for (y = 0; y < h; y++) {
				int col = src.getRGB(x * ww / w, y * hh / h);
				img.setRGB(x, y, col);
			}
		}
		return img;
		/**/
	}

	private static double colourWeight(int colour1, int colour2) {
		double a = 0.2126;
		double b = 0.7152;
		double c = 0.0722;

		int deltaRed = redRGB(colour1) - redRGB(colour2);
		int deltaGreen = greenRGB(colour1) - greenRGB(colour2);
		int deltaBlue = blueRGB(colour1) - blueRGB(colour2);

		return a * deltaRed * deltaRed + b * deltaGreen * deltaGreen + c * deltaBlue * deltaBlue;
	}

	public static void convertToPalette(String imageName, String outputName, int[] palette) throws IOException {
		BufferedImage img = ImageIO.read(new File(imageName));
		img = resizeImage(img, 128, 128);

		for (int i = 0; i < img.getWidth() * img.getHeight(); i++) {
			int imgCol = img.getRGB(i % img.getWidth(), i / img.getWidth());
			int minColour = 0;
			double minWeight = Double.MAX_VALUE;
			for (int j = 0; j < palette.length; j++) {
				double weight = colourWeight(imgCol, palette[j]);
				if (weight < minWeight) {
					minWeight = weight;
					minColour = palette[j];
				}
			}
			img.setRGB(i % img.getWidth(), i / img.getWidth(), minColour);
		}
		ImageIO.write(img, "png", new File(outputName));
	}

	public static BufferedImage convertToPalette(BufferedImage image, int[] palette) {
		BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < image.getWidth() * image.getHeight(); i++) {
			int imgCol = image.getRGB(i % image.getWidth(), i / image.getWidth());
			int minColour = palette[0];
			double minWeight = Double.MAX_VALUE;
			for (int j = 0; j < palette.length; j++) {
				double weight = colourWeight(imgCol, palette[j]);
				if (weight < minWeight) {
					minWeight = weight;
					minColour = palette[j];
				}
			}
			img.setRGB(i % image.getWidth(), i / image.getWidth(), minColour);
		}
		return img;
	}

	public static String[][] ImagePixelsToMCTileNames(BufferedImage img) {
		String[][] pixelNames = new String[img.getHeight()][img.getWidth()];
		for (int i = 0; i < img.getWidth() * img.getHeight(); i++) {
			pixelNames[i / img.getWidth()][i % img.getWidth()] = Structure.colourToMaterial
					.get(img.getRGB(i % img.getWidth(), i / img.getWidth()) & 0x00FFFFFF);
		}
		return pixelNames;
	}

	public static BufferedImage minecraftPainting(BufferedImage image, int width, int height) {
		BufferedImage img = resizeImage(image, width * (MINECRAFT_RESOLUTION), height * (MINECRAFT_RESOLUTION));
		for (int i = 0; i < img.getWidth(); i++) {
			int colourTop = PAINTING_BORDER.getRGB(i % PAINTING_BORDER.getWidth(), 0);
			int colourBottom = PAINTING_BORDER.getRGB(i % PAINTING_BORDER.getWidth(), PAINTING_BORDER.getHeight() - 1);
			img.setRGB(i, 0, colourTop);
			img.setRGB(i, img.getHeight() - 1, colourBottom);
		}
		for (int i = 0; i < img.getHeight(); i++) {
			int colourLeft = PAINTING_BORDER.getRGB(0, i % PAINTING_BORDER.getHeight());
			int colourRight = PAINTING_BORDER.getRGB(PAINTING_BORDER.getWidth() - 1, i % PAINTING_BORDER.getHeight());
			img.setRGB(0, i, colourLeft);
			img.setRGB(img.getWidth() - 1, i, colourRight);
		}
		img = resizeImage(img, image.getWidth(), image.getHeight());

		return img;
	}
}
