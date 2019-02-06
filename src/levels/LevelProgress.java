package levels;

import java.io.File;
import java.util.ArrayList;

import tools.CSV;
import tools.Parser;

public class LevelProgress {
	public static CSV progress = new CSV("save/progress");

	public static void setLevel(Level level, boolean done) {
		if (level == null)
			return;
		ArrayList<String> l = progress.get(level.getName());
		if (l == null) {
			progress.add(level.getName(), Parser.toString(done));
		} else {
			progress.set(level.getName(), 0, level.getName(), Parser.toString(done));
		}
		progress.write();
	}

	public static boolean isLevelDone(Level level) {
		if (level == null)
			return false;
		ArrayList<String> l = progress.get(level.getName());
		if (l == null)
			return false;
		if (l.size() < 2)
			return false;
		return Parser.toBoolean(l.get(1));
	}

	public static void reset() {
		new File("save/progress.csv").delete();
		progress = new CSV("save/progress");
	}

}
