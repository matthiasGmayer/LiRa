package levels;

import java.util.ArrayList;

import tools.CSV;

public interface ILoadable {
	public void save(CSV csv, String s);

	public void load(ArrayList<String> list);

}
