package entites;

import java.util.List;

import renderer.Camera;

public interface IUpdatable {
	public void update(List<Object> gameObjects, Camera camera, int delta);
}
