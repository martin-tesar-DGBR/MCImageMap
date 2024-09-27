package mc_map_image;

import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Utilities;

public class MCImageMap {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}

	private static void createAndShowGUI() throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame frame = new JFrame();
		frame.setTitle("Mapperoo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(true);

		MCMapPanel panel = new MCMapPanel();
		frame.add(panel);
		frame.setJMenuBar(createMenu(panel));
		
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("images/icon.png");
		frame.setIconImage(ImageIO.read(in));
		
		frame.pack();

		frame.setVisible(true);
	}
	
	private static JMenuBar createMenu(MCMapPanel panel) {
		JMenuBar menubar = new JMenuBar();
		
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.schem", ".schem");
		JMenu file = new JMenu("File");
		
		JMenuItem file_open = new JMenuItem("Open");
		file_open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.removeChoosableFileFilter(filter);
				fileChooser.setAcceptAllFileFilterUsed(true);
				int returnVal = fileChooser.showOpenDialog(menubar);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            panel.setImgSrc(fileChooser.getSelectedFile());
		        }
			}
			
		});
		file.add(file_open);
		
		JMenuItem file_export = new JMenuItem("Export");
		file_export.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.addChoosableFileFilter(filter);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnVal = fileChooser.showSaveDialog(menubar);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            panel.setDst(fileChooser.getSelectedFile());
		        }
			}
			
		});
		file.add(file_export);
		
		menubar.add(file);
		
		JMenu options = new JMenu("Options");
		JMenuItem options_setWidth = new JMenuItem("Set width");
		options_setWidth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter a number");
				if (input != null && Utilities.isInteger(input)) {
					panel.setWidth(Integer.parseInt(input));
				}
			}
			
		});
		options.add(options_setWidth);
		
		JMenuItem options_setHeight = new JMenuItem("Set height");
		options_setHeight.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter a number");
				if (input != null && Utilities.isInteger(input)) {
					panel.setHeight(Integer.parseInt(input));
				}
			}
			
		});
		options.add(options_setHeight);
		
		menubar.add(options);
		
		JMenu style = new JMenu("Style");
		
		JCheckBoxMenuItem style_isPainting = new JCheckBoxMenuItem("Painting", false);
		style_isPainting.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panel.setIsPainting(style_isPainting.getState());
			}
			
		});
		style.add(style_isPainting);
		
		menubar.add(style);
		
		return menubar;
	}

}