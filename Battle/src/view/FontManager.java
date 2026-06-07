package view;

import java.awt.*;
import java.io.File;

public class FontManager {
	private static Font galmuriFont;
	private static Font minecraftiaFont;
	private static Font neoDgmFont;

	static {
		try {
			galmuriFont = Font.createFont(Font.TRUETYPE_FONT, FontManager.class.getResourceAsStream("/fonts/Galmuri11.ttf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(galmuriFont);
		} catch (Exception e) {
			galmuriFont = new Font("Monospaced", Font.BOLD, 14);
		}

		try {
			minecraftiaFont = Font.createFont(Font.TRUETYPE_FONT, FontManager.class.getResourceAsStream("/fonts/Minecraftia-Regular.ttf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(minecraftiaFont);
		} catch (Exception e) {
			minecraftiaFont = new Font("Monospaced", Font.BOLD, 14);
		}

		try {
			neoDgmFont = Font.createFont(Font.TRUETYPE_FONT, FontManager.class.getResourceAsStream("/fonts/neodgm.ttf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(neoDgmFont);
		} catch (Exception e) {
			neoDgmFont = (galmuriFont != null) ? galmuriFont : new Font("Monospaced", Font.PLAIN, 14);
		}
	}

	public static Font get(int size) {
		return galmuriFont.deriveFont(Font.PLAIN, (float) Math.max(size, 8));
	}

	public static Font getMC(int size) {
		return minecraftiaFont.deriveFont(Font.PLAIN, (float) Math.max(size, 8));
	}

	public static Font getNeoDgm(int size) {
		return neoDgmFont.deriveFont(Font.PLAIN, (float) Math.max(size, 8));
	}
}
