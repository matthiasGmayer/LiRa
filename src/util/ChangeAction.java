package util;

@FunctionalInterface
public interface ChangeAction {
	public void onChange(Object source);
}
