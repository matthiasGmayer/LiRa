package util;

@FunctionalInterface
public interface UpdateAction {
	public void onUpdate(Object source, int delta);
}
