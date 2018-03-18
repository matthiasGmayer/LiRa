package light;

import org.newdawn.slick.Color;

public interface IColorGetter extends IIntersectable {
	public void onColorGet(Color color, Object source);
}
