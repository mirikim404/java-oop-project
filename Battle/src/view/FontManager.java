package view;

import java.awt.*;
import java.io.File;

public class FontManager {
	private static Font galmuriFont;
	private static Font minecraftiaFont;
	private static Font neoDgmFont; // 1. 네오둥근모 변수 추가

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

		// 2. 네오둥근모(neodgm.ttf) 로드 추가
		try {
			neoDgmFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/neodgm.ttf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(neoDgmFont);
		} catch (Exception e) {
			// 로드 실패 시 갈무리 폰트로 대체하도록 안전장치 마련
			neoDgmFont = (galmuriFont != null) ? galmuriFont : new Font("Monospaced", Font.PLAIN, 14);
		}
	}

	// Galmuri - 기존 배틀 뷰 등에서 유지
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

	// 3. 네오둥근모 반환 메서드 추가
	public static Font getNeoDgm(int size) {
		// ★ 도트 폰트는 PLAIN(기본) 상태일 때 외곽선이 가장 깔끔합니다.
		return neoDgmFont.deriveFont(Font.PLAIN, (float) Math.max(size, 8));
	}
}