package mc_map_image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import model.ImageClass;
import model.Structure;
import model.Utilities;

@SuppressWarnings("serial")
class MCMapPanel extends JPanel {
	
	/**
	 * 
	 */
	BufferedImage imageOriginal;
	BufferedImage image;
	String[][] pixelNames;
	int mapWidth, mapHeight;
	boolean isPainting = false;
	
	int prevMousePosX;
	int prevMousePosY;
	
	double zoom = 1.0;
	int panX = 0;
	int panY = 0;
	
	File fileSrc;
	String fileDst;

	public MCMapPanel() {
		
		mapWidth = 1;
		mapHeight = 1;
		
		fileSrc = new File("");
		fileDst = "";
		
		prevMousePosX = 0;
		prevMousePosY = 0;
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int button = e.getButton();
				switch (button) {
				case MouseEvent.BUTTON1:
					break;
				case MouseEvent.BUTTON2:
					panX = 0;
					panY = 0;
					zoom = 1.0;
                	break;
				case MouseEvent.BUTTON3:
                	break;
				}
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			
			public void mouseMoved(MouseEvent e) {
				Point mousePos = getMousePosition();
				if (mousePos != null) {
					prevMousePosX = getMousePosition().x;
					prevMousePosY = getMousePosition().y;
				}
				repaint();
			}
			
			public void mouseDragged(MouseEvent e) {
				Point mousePos = getMousePosition();
				
				if (mousePos != null) {
					panX += getMousePosition().x - prevMousePosX;
					panY += getMousePosition().y - prevMousePosY;
					prevMousePosX = getMousePosition().x;
					prevMousePosY = getMousePosition().y;
				}
				int button = e.getButton();
				switch (button) {
				case MouseEvent.BUTTON1:
					break;
				case MouseEvent.BUTTON2:
                	break;
				case MouseEvent.BUTTON3:
                	break;
				}
				repaint();
			}
		});
		
		addMouseWheelListener(new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int wheelRotations = e.getWheelRotation();
				zoom *= Math.pow(0.75, wheelRotations);
				if (Math.abs(zoom - 1.0) < 0.00390625) {
					zoom = 1.0;
				}
				repaint();
			}
		});
	}

	public void setImgSrc(File file) {
		fileSrc = file;
		mapWidth = 1;
		mapHeight = 1;
		setImgFromFile();
	}
	
	private void setImg() {
		if (imageOriginal == null) {
			return;
		}
		image = ImageClass.resizeImage(imageOriginal, 128 * mapWidth, 128 * mapHeight);
		if (isPainting) {
			image = ImageClass.minecraftPainting(image, mapWidth, mapHeight);
		}
		image = ImageClass.convertToPalette(image, Structure.mapPalette);
		pixelNames = ImageClass.ImagePixelsToMCTileNames(image);
	}
	
	public void setImgFromFile() {
		if (fileSrc.exists()) {
			try {
				imageOriginal = ImageIO.read(fileSrc);
				setImg();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setDst(File file) {
		fileDst = file.getParent();
		String fileName = file.getName();
		if (fileName.length() > 6 && fileName.substring(fileName.length() - 6).equals(".schem")) {
			fileName = fileName.substring(0, fileName.length() - 6);
		}
		try {
			exportImageToSchematic(fileDst, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setWidth(int width) {
		if (width > 0 && mapWidth != width) {
			mapWidth = width;
			setImg();
		}
	}
	
	public void setHeight(int height) {
		if (height > 0 && mapHeight != height) {
			mapHeight = height;
			setImg();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(960, 720);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		
		if (image != null) {
			
			
			g.drawImage(image, (int) ((-image.getWidth() / 2) * zoom) + panX, (int) ((-image.getHeight() / 2) * zoom) + panY, (int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom), this);
			
			//debug
			//mouse position
			Point mousePos = getMousePosition();
			Point imgCoords = screenCoords2ImageCoords(mousePos);
			String output1 = ((mousePos != null) ? "(" + mousePos.x + ", " + mousePos.y + ")" : "");
			String output2 = ((imgCoords != null) ? "(" + imgCoords.x + ", " + imgCoords.y + ")": "");
			String output3 = imgCoords != null && (imgCoords.x >= 0 && imgCoords.x < image.getWidth() &&
							 imgCoords.y >= 0 && imgCoords.y < image.getHeight()) ?
							 pixelNames[imgCoords.y][imgCoords.x] : "";
			g.setColor(Color.WHITE);
			g.fillRect(-this.getWidth() / 2 + 10, -this.getHeight() / 2 + 10, (int) (7 * Utilities.max(output1.length(), output2.length(), output3.length())) + 4, 72);
			g.setColor(Color.BLACK);
			setFont(new Font("monospaced", Font.PLAIN, 12));
			g.drawString(output1, -this.getWidth() / 2 + 12, -this.getHeight() / 2 + 24);
			g.drawString(output2, -this.getWidth() / 2 + 12, -this.getHeight() / 2 + 48);
			g.drawString(output3, -this.getWidth() / 2 + 12, -this.getHeight() / 2 + 72);
		}
	}
	
	private Point screenCoords2ImageCoords(Point point) {
		if (point == null) {
			return null;
		}
		int x = image.getWidth() / 2 + (int) ((point.x - panX - this.getWidth() / 2) / zoom) - 1;
		int y = image.getHeight() / 2 + (int) ((point.y - panY - this.getHeight() / 2) / zoom) - 1;
		return new Point(x, y);
	}
	
	private void exportImageToSchematic(String fileDst, String fileName) throws IOException {
		if (mapWidth * mapHeight == 1) {
			Structure struct = new Structure(image);
			struct.exportStructure(fileDst + '/' + fileName + ".schem");
		}
		else if (mapHeight % 2 == 0) {
			BufferedImage[] sections = new BufferedImage[mapHeight / 2];
			Structure structs;
			
			for (int i = 0; i < mapHeight / 2; i++) {
				sections[i] = image.getSubimage(0, 128 * 2 * i, 128 * mapWidth, 128 * 2);
				structs = new Structure(sections[i]);
				structs.exportStructure(fileDst + '/' + fileName + '-' + i + ".schem");
			}
		}
		else {
			BufferedImage[] sections = new BufferedImage[mapHeight];
			Structure structs;
			
			for (int i = 0; i < mapHeight; i++) {
				sections[i] = image.getSubimage(0, 128 * i, 128 * mapWidth, 128);
				structs = new Structure(sections[i]);
				structs.exportStructure(fileDst + '/' + fileName + '-' + i + ".schem");
			}
		}
	}

	public void setIsPainting(boolean state) {
		isPainting = state;
		setImg();
	}
}
