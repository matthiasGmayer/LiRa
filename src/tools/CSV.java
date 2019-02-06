package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;


public class CSV implements Iterable<ArrayList<String>> {

	private static final String defaultPath = "settings/";
	private static final String defaultFormat = ".csv";

	private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	private String path;

	public CSV(File file) {
		path = file.getPath();
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			BufferedReader b = new BufferedReader(new FileReader(file));
			read(b);
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public CSV(BufferedReader b) {
		try {
			read(b);
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ! is replaced with the default path

	public CSV(String path) {
		this(new File(path.replace("!", defaultPath) + (path.contains(".") ? "" : defaultFormat)));
	}

	public CSV() {
	}

	private void read(BufferedReader reader) {
		String s;
		try {
			while ((s = reader.readLine()) != null) {

				ArrayList<String> l;
				this.data.add(l = new ArrayList<String>());

				String[] data = s.split(",");
				for (int i = 0; i < data.length; i++) {
					String d = data[i];
					while (d.endsWith(" ")) {
						d = d.substring(0, d.length() - 1);
					}
					while (d.startsWith(" ")) {
						d = d.substring(1, d.length());
					}
					l.add(d);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		try {
			read(new BufferedReader(new FileReader(new File(path))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void write(File file) {
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			BufferedWriter b = new BufferedWriter(new FileWriter(file));
			write(b);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void write(BufferedWriter b) {

		try {
			for (ArrayList<String> l : data) {

				boolean first = true;
				for (String s : l) {
					if (first) {
						b.write(s);
						first = false;
					} else {
						b.write(", " + s);
					}
				}
				b.newLine();
			}
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String path) {
		write(new File(path = path.replace("!", defaultPath) + (path.contains(".") ? "" : defaultFormat)));
		setPath(path);
	}

	public void write() {
		if (path == null || path.equals(""))
			return;
		write(path);
	}

	// public void cleanUp() {
	// for (int i = 0; i < data.size(); i++) {
	// ArrayList<String> list = data.get(i);
	// if (list == null || list.size() == 0)
	// data.remove(i);
	// else {
	// boolean b = true;
	// ;
	// for (String s : list) {
	// if (!(s == null || s.equals(""))) {
	// b = false;
	// }
	// }
	// if (b)
	// data.remove(i);
	// }
	// }
	// }

	public ArrayList<String> get(String search, int inColumn) {
		for (ArrayList<String> l : data) {
			String s = l.get(inColumn);
			if (s == null)
				continue;
			if (s.equals(search))
				return l;
		}
		return null;
	}

	public ArrayList<String> get(String search) {
		return get(search, 0);
	}

	public String get(String search, int inColumn, int position) {
		ArrayList<String> l = get(search, inColumn);
		if (l == null || position > l.size() - 1)
			return null;
		return l.get(position);
	}

	public ArrayList<String> get(int row) {
		if (row > data.size() - 1)
			return null;
		return data.get(row);
	}

	public String get(int row, int column) {

		ArrayList<String> l = get(row);
		if (l == null || column > l.size() - 1)
			return null;
		return l.get(column);
	}

	public void set(String search, int inColumn, ArrayList<String> strings) {
		for (int i = 0; i < data.size(); i++) {

			String s = data.get(i).get(inColumn);
			if (s == null)
				continue;
			if (s.equals(search)) {
				data.set(i, strings);
				return;
			}
		}
		data.add(strings);
	}

	public void set(String search, int inColumn, String... s) {
		set(search, inColumn, arrayToList(s));
	}

	public void set(String search, int inColumn, int position, String s) {
		ArrayList<String> l = get(search, inColumn);
		if (l == null) {
			data.add(l = new ArrayList<String>());
			l.add(search);
		}
		while (position > l.size() - 1) {
			l.add("");
		}

		l.set(position, s);
	}

	public void set(int row, ArrayList<String> strings) {
		while (row > data.size()) {
			data.add(new ArrayList<String>());
		}
		data.add(strings);
	}

	public void set(int row, String... s) {
		set(row, arrayToList(s));
	}

	public void set(int row, int column, String s) {

		ArrayList<String> l = get(row);
		if (l == null)
			set(row, l = new ArrayList<String>());
		l.set(column, s);
	}

	public int add(ArrayList<String> l) {
		int h;
		set(h = data.size(), l);
		return h;
	}

	public int add(String... strings) {
		return add(arrayToList(strings));
	}

	public void add(int row, String s) {
		ArrayList<String> l = get(row);
		if (l == null)
			data.add(l = new ArrayList<String>());
		l.add(s);
	}

	public void add(int row, ArrayList<String> s) {
		for (String string : s) {
			add(row, string);
		}
	}

	public void add(int row, String... s) {
		add(row, arrayToList(s));
	}

	public void clear() {
		data.clear();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private ArrayList<String> arrayToList(String... s) {
		ArrayList<String> l = new ArrayList<String>();
		for (int i = 0; i < s.length; i++) {
			l.add(s[i]);
		}
		return l;
	}

	@Override
	public Iterator<ArrayList<String>> iterator() {
		return data.iterator();
	}
	@Override
	public String toString() {
		StringWriter s = new StringWriter();
		write(new BufferedWriter(s));
		return s.toString();
	}
}
