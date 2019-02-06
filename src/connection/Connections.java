package connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import levels.Level;
import tools.CSV;

public class Connections {
	private static String connectionURL;
	private static String sessionId = null;
	private static String username = null;

	public static boolean loggedIn() {
		return sessionId != null;
	}

	public static String getUsername() {
		return username;
	}

	private static final String standardURL = "http://postponeunited.bplaced.net/";
	static {
		if (new File("settings/server.csv").exists()) {
			CSV csv = new CSV("settings/server.csv");
			String s = csv.get("server", 0, 1);
			if (s == null || "".equals(s)) {
				connectionURL = standardURL;
			} else {
				connectionURL = s.replace("!", standardURL);
			}
		} else {
			connectionURL = standardURL;
		}
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return returns the session id or null
	 */
	public static void login(String username, String password) {
//		sessionId="0";
//		if(sessionId!=null)
//		return;
//		if("".equals(username)||"".equals(password)) {
//			return;
//		}
		try {
			sessionId = execute("AccountSystem/loginlater.php", "user=" + username, "password=" + password, "lira=1")
					.readLine();
			System.out.println(sessionId);
			if (sessionId == null || "null".equals(sessionId)) {
				sessionId = "0";
				return;
			}
			Connections.username = username;
		} catch (Exception e) {
			e.printStackTrace();
			sessionId = null;
			Connections.username = null;
		}

	}

	public static void logout() {

		// send logout to server
		sessionId = null;
		username = null;
	}

	public static void uploadLevel(Level l, String levelName) {
		String[] s = l.toString().split("\\r?\\n");
		String id = "-1";
		try {
			id = execute("!allocateLevel", "level=" + encode(s[0]), "sessionId=" + sessionId, "levelName=" + levelName)
					.readLine();
			if ("-1".equals(id) || id == null || "null".equals(id))
				return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 1; i < s.length; i++) {
			execute("!appendLevel", "levelId=" + id, "level=" + encode(s[i]), "sessionId=" + sessionId);
		}

	}

	public static void rateLevel(int levelId, int rating) {
		execute("!rateLevel", "levelId=" + levelId, "rating=" + rating, "sessionId=" + sessionId);
	}

	public static String getLevelInfo(int levelId) {
		try {
			return execute("!getLevelInfo", "levelId=" + levelId, "sessionId=" + sessionId).readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static List<Integer> searchLevels(String userName, String levelName, String order) {
		List<Integer> list = new ArrayList<Integer>();
		try {
			String s = execute("!searchLevels", "userName=" + userName, "levelName=" + levelName, "order=" + order)
					.readLine();
			System.out.println(s);
			if (s != null && !s.equals(""))
				for (String str : s.split(",")) {
					list.add(Integer.parseInt(str));
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void deleteLevel(int levelId) {
		execute("!clearLevel", "sessionId=" + sessionId, "levelId=" + levelId);
	}

	private static String encode(String s) {
		return s.replace(",", ")(").replace(" ", "").replace(".", "()");
	}

	public static Level downloadLevel(int id) {
		Level l = new Level(new CSV(execute("!downloadLevel", "levelId=" + Integer.toString(id))));
		l.setPath("n");
		return l;
	}

	private static BufferedReader execute(String action) {
		try {
			String request = connectionURL + action.substring(0, 1).replace("!", "LiRa/") + action.substring(1);
			System.out.println(request);
			return new BufferedReader(new InputStreamReader(new URL(request).openStream()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static BufferedReader execute(String file, String... action) {
		String s = null;
		int i;
		for (i = 0; i < action.length; i++) {
			if(action[i].equals(""))
				continue;
			s = action[i];
			i++;
			break;
		}
		for (; i < action.length; i++) {
			if(action[i].equals(""))
				continue;
			s += "&" + action[i];
		}
		return execute(file + (file.contains(".") ? "" : ".php") + "?" + s);
	}
}
