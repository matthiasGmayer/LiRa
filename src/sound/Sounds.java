package sound;

import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Sounds {
	private static HashMap<String, Sound> soundMap = new HashMap<>();
	
	public static Sound getSound(String name) {
		if(!name.contains("."))
			name += ".ogg";
		Sound sound = soundMap.get(name);
		if(sound == null) {
			try {
				sound = new Sound("sounds/"+name);
				soundMap.put(name, sound);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
		return sound;
	}
	public static void play(String name) {
		Sound sound = getSound(name);
		sound.play();
	}
}
