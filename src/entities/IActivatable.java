package entities;

public interface IActivatable extends ILinkable {

	public IActivatable getActiveState();

	public IActivatable getInActiveState();

	public void setActiveState(IActivatable a);

	public void setInActiveState();

	public void setActivated(boolean b);

	public void setState(boolean b);

	public boolean isActivated();

	public boolean isState();
}
