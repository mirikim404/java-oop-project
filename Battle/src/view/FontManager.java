package view;

import java.awt.*;
import java.io.File;

import javax.swing.ImageIcon;

public class FontManager {
	private static Font galmuriFont;
	private static Font minecraftiaFont;

	static {
		try {
			galmuriFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/font/Galmuri11.ttf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(galmuriFont);
		} catch (Exception e) {
			galmuriFont = new Font("Monospaced", Font.BOLD, 14);
		}

		try {
			minecraftiaFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Minecraftia-Regular.ttf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(minecraftiaFont);
		} catch (Exception e) {
			minecraftiaFont = new Font("Monospaced", Font.BOLD, 14);
		}
	}

	// Galmuri - 한글 포함 UI에 사용
	public static Font get(int size) {
		return galmuriFont.deriveFont(Font.PLAIN, (float) Math.max(size, 8));
	}

	public static Font getBold(int size) {
		return galmuriFont.deriveFont(Font.BOLD, (float) Math.max(size, 8));
	}

	// Minecraftia - 영문 타이틀 등에 사용
	public static Font getMC(int size) {
		return minecraftiaFont.deriveFont(Font.PLAIN, (float) Math.max(size, 8));
	}

}
