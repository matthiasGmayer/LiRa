package gameStates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import connection.Connections;
import entities.Activator;
import entities.ColoredGlass;
import entities.Goal;
import entities.IActivatable;
import entities.IColorable;
import entities.IControllable;
import entities.ILinkable;
import entities.IPositionable;
import entities.IResizable;
import entities.IRotatable;
import entities.IScalable;
import entities.LightEmitter;
import entities.Mirror;
import entities.Wall;
import gui.Button;
import gui.CheckBox;
import gui.ColorField;
import gui.Colors;
import gui.Panel;
import gui.Plane;
import gui.Text;
import gui.TextField;
import levels.ILoadable;
import levels.Level;
import renderer.IRenderable;
import renderer.IRenderable.Direction;
import settings.Actions;
import settings.Controls;
import settings.Graphic;
import tools.BidirectionalMap;
import tools.CSV;
import tools.Tools;
import util.ButtonAction;
import util.ChangeAction;
import util.SelectAction;
import util.UpdateAction;

public class Edit extends Game {

	Button b, b1, b2, UploadButton;
	Text t;
	Panel directoryPanel, inspectorPanel, creatorPanel;
	Level currentLevel;
	Text selectedText;
	TextField loadTextField;
	ButtonAction deleteAction, deleteLevelAction, yesAction, noAction;
	ArrayList<Text> levelTexts = new ArrayList<Text>();
	ArrayList<Object> toRemove = new ArrayList<Object>();

	IRenderable creatingObject;
	IActivatable clone;

	boolean dirIsOpen, insIsOpen, loadLevel, saveLevel, deleteLevel, inInspector, inCreator, isCreating, isRestricting1,
			isRestricting2, isLinking, isUnlinking;

	float startingAngle;

	HashMap<Class<? extends IRenderable>, Button> createMap = new HashMap<Class<? extends IRenderable>, Button>();

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		States.edit = this;

		// Creates the DirectoryPanel with all its components
		directoryPanel = create(new Panel(b = new Button(new ButtonAction() {

			@Override
			public void onRelease(Object source) {
				if (dirIsOpen) {
					directoryPanel.setTargetPosition(new Vector2f(Graphic.width / 2, -Graphic.height / 2));
					removeOverride();
				} else {
					directoryPanel.setTargetPosition(new Vector2f(-Graphic.width / 2, -Graphic.height / 2));
				}
				dirIsOpen = !dirIsOpen;

			}
		}, getLanguage().open + "    " + getLanguage().close, Color.black, new Vector2f(0, 0))));
		b.setPosition(new Vector2f(0, b.getApparentSize(IRenderable.Direction.height) / 2));
		directoryPanel.setPosition(new Vector2f(Graphic.width / 2, -Graphic.height / 2));

		directoryPanel.add(
				new Plane(new Vector2f(0, 0), new Vector2f(Graphic.width, Graphic.height), new Color(1, 1, 1, 0.5f)),

				loadTextField = new TextField("", Color.black, new Vector2f(Graphic.width / 2, Graphic.height - 100f),
						1, false),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						if (new Level("!" + loadTextField.getContent()).size() == 0) {
							saveLevel = true;
						} else if (t == null && b1 == null && b2 == null) {
							directoryPanel.add(t = new Text(getLanguage().override + "?", Color.black,
									new Vector2f(Graphic.width / 2, Graphic.height / 2)));
							directoryPanel.add(b1 = new Button(

									yesAction = new ButtonAction() {
										public void onRelease(Object source) {
											saveLevel = true;
											removeOverride();
										};
									}, getLanguage().yes, Color.black,
									new Vector2f(Graphic.width / 2 + 100, Graphic.height / 2 + 100), 1f));
							directoryPanel.add(b2 = new Button(

									noAction = new ButtonAction() {
										public void onRelease(Object source) {
											removeOverride();
										};
									}, getLanguage().no, Color.black,
									new Vector2f(Graphic.width / 2 - 100, Graphic.height / 2 + 100)));
						}
					}
				}, getLanguage().save, Color.black, new Vector2f(Graphic.width - 100, 50)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						loadLevel = true;
					}
				}, getLanguage().load, Color.black, new Vector2f(Graphic.width - 100f, 150)),

				new Button(deleteLevelAction = new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						if (new Level("!" + loadTextField.getContent()).size() == 0)
							deleteLevel = true;
						else if (t == null && b1 == null && b2 == null) {
							directoryPanel.add(t = new Text(getLanguage().delete + "?", Color.black,
									new Vector2f(Graphic.width / 2, Graphic.height / 2)));
							directoryPanel.add(b1 = new Button(

									yesAction = new ButtonAction() {
										public void onRelease(Object source) {
											deleteLevel = true;
											removeOverride();
										};
									}, getLanguage().yes, Color.black,
									new Vector2f(Graphic.width / 2 + 100, Graphic.height / 2 + 100), 1f));
							directoryPanel.add(b2 = new Button(

									noAction = new ButtonAction() {
										public void onRelease(Object source) {
											removeOverride();
										};
									}, getLanguage().no, Color.black,
									new Vector2f(Graphic.width / 2 - 100, Graphic.height / 2 + 100)));
						}
					}
				}, getLanguage().delete, Color.black, new Vector2f(Graphic.width - 100f, 250)),
				
				UploadButton = new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						if(currentLevel != null && currentLevel.size() > 0)
							Connections.uploadLevel(currentLevel, loadTextField.getContent());
						ButtonAction.super.onRelease(source);
					}
				}, "Upload", Color.black, new Vector2f(Graphic.width - 100f, 350)),
						
				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().menu, Color.black, new Vector2f(Graphic.width - 100f, 450))
				
				);

		loadLevels();

		// creates the inspectorPanel
		inspectorPanel = create(new Panel());
		inspectorPanel.setPosition(new Vector2f(-Graphic.width / 2 - 5, -Graphic.height / 2));
		inspectorPanel.add(new Plane(new ButtonAction() {
			@Override
			public void onEnter(Object source) {
				inInspector = true;
			}

			@Override
			public void onLeave(Object source) {
				inInspector = false;
			}
		}, new Vector2f(), new Vector2f(-200, Graphic.width), new Color(1, 1, 1, 0.5f)));

		// creates the CreatorPanel
		creatorPanel = create(new Panel());
		creatorPanel.setPosition(new Vector2f(-Graphic.width / 2, Graphic.height / 2 - 20));
		creatorPanel.add(new Plane(new ButtonAction() {
			@Override
			public void onEnter(Object source) {
				inCreator = true;
			}

			@Override
			public void onLeave(Object source) {
				inCreator = false;
			}
		}, new Vector2f(0, 0), new Vector2f(Graphic.width, 200), new Color(1, 1, 1, 0.5f)));

		float xPos = 1;
		float yPos = 100;
		float spaceing = 150;

		creatorPanel.add(putCreator(Mirror.class, new Vector2f(xPos++ * spaceing, yPos), Mirror.standardImage),
				putCreator(LightEmitter.class, new Vector2f(xPos++ * spaceing, yPos), LightEmitter.standardImage),
				putCreator(ColoredGlass.class, new Vector2f(xPos++ * spaceing, yPos), ColoredGlass.standardImage),
				putCreator(Wall.class, new Vector2f(xPos++ * spaceing, yPos), Wall.standardImage),
				putCreator(Activator.class, new Vector2f(xPos++ * spaceing, yPos), Activator.standardImage),
				putCreator(Goal.class, new Vector2f(xPos++ * spaceing, yPos), Goal.standardImage));

		super.init(gc, sbg);
		Button b5;
		p.add(b5 = new Button(new ButtonAction() {
			@Override
			public void onRelease(Object source) {
				clear();
				ButtonAction.super.onRelease(source);
			}
		}, getLanguage().delete, Color.black, new Vector2f(0, 0), 0.5f));
		b5.setPosition(new Vector2f(b5.getApparentSize(Direction.width),
				b5.getApparentSize(Direction.height) / 2 - Graphic.height / 2));
	}

	ArrayList<ButtonAction> createActions = new ArrayList<ButtonAction>();

	private Button putCreator(Class<? extends IRenderable> c, Vector2f pos, Image image) {
		ButtonAction a;
		Button b = new Button(a = new ButtonAction() {
			Class<? extends IRenderable> myClass = c;

			@Override
			public void onRelease(Object source) {
				isCreating = true;
				try {
					creatingObject = create(myClass.newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, "", Color.white, pos, 1f, image, image);
		createActions.add(a);
		return b;
	}

	@Override
	public synchronized <T> T create(T object) {
		if (object instanceof IActivatable) {
			create(((IActivatable) object).getActiveState());
		}
		return super.create(object);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		dirIsOpen = false;
		directoryPanel.setPosition(new Vector2f(Graphic.width / 2, -Graphic.height / 2));
		directoryPanel.setTargetPosition(new Vector2f(Graphic.width / 2, -Graphic.height / 2));
		UploadButton.setPosition(new Vector2f(Graphic.width + (Connections.loggedIn() ? -100 : 100), 350));
		super.enter(container, game);
	}

	@Override
	protected void select(IControllable c) {
		if (c != null) {

			if (isLinking) {
				linkable.link(c);
			} else {
				if (Actions.is(Controls.copy)) {
					isCreating = true;
					creatingObject = create(c.clone());
				}
				super.select(c);
			}
		}
		isLinking = false;
	}

	@Override
	public void keyPressed(int key, char c) {

		if (!dirIsOpen && Actions.is(Controls.link))
			isLinking = true;

		if (!inInspector && !dirIsOpen && !textFieldSelected && key <= createActions.size() + 1 && key <= 10
				&& key > 1) {
			int i = key - 2;
			if (isCreating) {
				remove(creatingObject);
			}
			createActions.get(i).onRelease(this);
		} else if (Actions.is(Controls.delete)) {
			if (dirIsOpen) {
				if (deleteLevelAction != null) {
					deleteLevelAction.onRelease(this);
				}
			}

			else if (deleteAction != null)
				deleteAction.onRelease(this);
		} else if (key == Input.KEY_ENTER) {
			if (yesAction != null)
				yesAction.onRelease(this);
		} else if (key == Input.KEY_ESCAPE) {
			if (noAction != null)
				noAction.onRelease(this);
		}

		super.keyPressed(key, c);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if (button == 0) {
			isCreating = false;
		}

		if (Actions.is(Controls.limitMovement)) {
			if (button == 0) {
				isRestricting1 = true;
			} else if (button == 1) {
				// not working
				// isRestricting2 = true;

				startingAngle = (float) Math.toDegrees(
						Tools.getAngle(getSelected().getPosition(), camera.screenToWorldPoint(Actions.mousePosition))
								% (Math.PI * 2));
			}
		} else
			super.mousePressed(button, x, y);
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		IControllable selected = getSelected();
		if (selected != null) {
			if (isRestricting1) {
				isRestricting1 = false;
				float f = selected.getPosition().distance(camera.screenToWorldPoint(Actions.mousePosition));
				if (f > Math.min(selected.getApparentSize(Direction.width),
						selected.getApparentSize(Direction.height))) {
					selected.setRDistance(f);
					selected.setRPosition(selected.getPosition());
				} else {
					selected.setRDistance(null);
				}
			}
			if (isRestricting2) {
				isRestricting2 = false;
				selected.setRAngle((float) Math.toRadians(startingAngle));
				selected.setRSpectrum((float) Tools.getAngle(getSelected().getPosition(),
						camera.screenToWorldPoint(Actions.mousePosition)));
			}
		}
		super.mouseReleased(button, x, y);
	}

	// removes the Override or Delete? question
	private void removeOverride() {
		directoryPanel.remove(t, b1, b2);
		t = null;
		b1 = null;
		b2 = null;
	}

	private void loadLevels() {

		directoryPanel.remove(levelTexts);

		File file = new File("levels");
		file.mkdirs();
		File[] files = file.listFiles();
		int i = 1;
		BidirectionalMap<Level, Text> levelMap = new BidirectionalMap<Level, Text>();
		for (File file2 : files) {

			Text text = new Text(new ButtonAction() {

				@Override
				public void onRelease(Object source) {
					currentLevel = levelMap.getKey((Text) source);

					if (selectedText != null)
						selectedText.setFontColor(Color.black);
					selectedText = (Text) source;
					selectedText.setFontColor(Color.blue);
					loadTextField.setContent(((Text) source).getContent());

				}

				@Override
				public void onDoublePress(Object source) {
					loadLevel = true;
				}

				@Override
				public void onDeselect(Object source) {
					if (selectedText != null)
						selectedText.setFontColor(Color.black);
					selectedText = null;
				}

			}, file2.getName().split("\\.")[0], Color.black, new Vector2f(300, i * 20));
			levelTexts.add(text);
			levelMap.put(new Level(new CSV(file2)), text);
			i++;
		}
		directoryPanel.add(levelMap.getValueSet());
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setBackground(Color.black);
		g.setColor(Color.black);
		camera.render(camera, g);

		IControllable selected = getSelected();
		if (selected != null) {
			Vector2f position = selected.getPosition();
			Vector2f mouse = camera.screenToWorldPoint(Actions.mousePosition);
			float dis = 2 * selected.getPosition().distance(mouse);
			if (isRestricting1) {
				g.setColor(new Color(0.5f, 0.2f, 0.2f, 0.5f));
				g.fillOval(position.x - dis / 2, position.y - dis / 2, dis, dis);
			} else if (isRestricting2) {
				g.setColor(new Color(0.5f, 0.2f, 0.2f, 0.3f));
				float f = (float) (Math.toDegrees(Tools.getAngle(position, mouse) % (Math.PI * 2)));
				float f1 = Math.min(f, startingAngle);
				float f2 = Math.max(f, startingAngle);
				g.fillArc(position.x - dis / 2, position.y - dis / 2, dis, dis, f1, f2);
			} else {
				Vector2f pos = selected.getRPosition();
				Float dist = selected.getRDistance();
				if (pos != null && dist != null) {
					dist *= 2;
					g.setColor(new Color(0.5f, 0.2f, 0.2f, 0.3f));
					g.fillOval(pos.x - dist / 2, pos.y - dist / 2, dist, dist);
				}
				Float a = selected.getRAngle();
				Float s = selected.getRSpectrum();

				if (a != null && s != null) {
					g.setColor(new Color(0.5f, 0.2f, 0.2f, 0.5f));
					g.fillArc(position.x - 100f / 2, position.y - 100f / 2, 100f, 100f, (float) Math.toDegrees(a),
							(float) Math.toDegrees(s));

				}
			}
			if (selected instanceof Activator) {
				g.setColor(Color.blue);
				Activator act = (Activator) selected;
				Vector2f aPos = act.getPosition();
				if (isLinking) {
					g.drawLine(aPos.x, aPos.y, mouse.x, mouse.y);
				}
			} else if (selected instanceof ILinkable) {
				g.setColor(Color.red);
				Vector2f aPos = selected.getPosition();
				if (isLinking) {
					g.drawLine(aPos.x, aPos.y, mouse.x, mouse.y);
				}
			}

		}
		for (Object object : gameObjects) {
			if (object instanceof Activator) {
				Activator a = (Activator) object;
				Vector2f pos = a.getPosition();
				for (IActivatable ac : a.getLinkedObjects()) {
					if (ac instanceof IPositionable) {
						Vector2f pos2 = ((IPositionable) ac).getPosition();
						g.setColor(Color.blue);
						g.drawLine(pos.x, pos.y, pos2.x, pos2.y);
					}
				}
			} else if (object instanceof IActivatable) {
				IActivatable a = (IActivatable) object;
				if (a.getActiveState() != null) {
					if (a instanceof IPositionable) {
						g.setColor(Color.red);
						Vector2f pos = ((IPositionable) a).getPosition();
						Vector2f pos2 = ((IPositionable) a.getActiveState()).getPosition();
						g.drawLine(pos.x, pos.y, pos2.x, pos2.y);
					}
				}
			}
		}

		super.render(gc, sbg, g);
	}

	IControllable insNew;
	boolean textFieldSelected;

	int time, autoSaveTime = 60000;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		time += delta;
		if (time > autoSaveTime) {
			time = 0;
			if (!idList.isEmpty()) {
				new Level(idList, "!AUTOSAVE");
				loadLevels();
			}
		}

		if (isUnlinking) {
			isUnlinking = false;
			linkable.unlink();
		}
		if (saveLevel && !loadTextField.getContent().equals("")) {
			new Level(idList, "!" + loadTextField.getContent());
			loadLevels();
			saveLevel = false;

		}
		if (deleteLevel) {
			if (currentLevel != null) {
				new File(currentLevel.getPath()).delete();
			}
			loadLevels();
			deleteLevel = false;
		}
		IControllable c = getSelected();
		if (c == null) {
			isLinking = false;
			isUnlinking = false;
		}
		if (!textFieldSelected && !inInspector && (c == null || dirIsOpen) || inCreator) {
			inspectorPanel.setTargetPosition(new Vector2f(-Graphic.width / 2 - 5, -Graphic.height / 2));
			insIsOpen = false;

		} else {
			inspectorPanel.setTargetPosition(new Vector2f(-Graphic.width / 2 + 200, -Graphic.height / 2));
			insIsOpen = true;
			if (c != null && insNew != c) {
				insNew = c;

				inspectorPanel.remove(toRemove);
				toRemove.clear();

				new Thread(() -> getInspector(insNew)).start();
			}
		}
		if (inCreator && !isCreating && !dirIsOpen) {
			creatorPanel.setTargetPosition(new Vector2f(-Graphic.width / 2, Graphic.height / 2 - 200));
		} else {
			creatorPanel.setTargetPosition(new Vector2f(-Graphic.width / 2, Graphic.height / 2 - 20));
		}
		if (creatingObject != null & isCreating) {
			((IControllable) creatingObject).setPosition(camera.screenToWorldPoint(Actions.mousePosition));
			((IControllable) creatingObject).changed(gameObjects);
		}
		super.isSelecting = !dirIsOpen && !inInspector && !inCreator;
		textFieldSelected = false;
		super.update(gc, sbg, delta);
		if (loadLevel) {
			loadLevels();
			if (currentLevel != null) {
				// List<ILoadable> remove = new ArrayList<>();
				for (Object object : gameObjects) {
					if (object instanceof ILoadable) {
						// remove.add((ILoadable)object);
						remove(object);
					}
				}
				clear();
				currentLevel.load();
				create(currentLevel);

				// gameObjects.removeIf((o)->remove.contains(o));
				// idList.removeIf((o)->remove.contains(o));
			}
			loadLevel = false;
		}
	}

	IPositionable positionable;
	IRotatable rotatable;
	IColorable colorable;
	IScalable scalable;
	IResizable resizable;
	ILinkable linkable;
	ColoredGlass coloredGlass;

	private final int spacing = 17;

	public void getInspector(IControllable c) {

		int y = 1;

		Integer id = getId(c);
		if (id != null)
			addToInspector(new Text(Integer.toString(id), new Vector2f(-100, 10)));

		if (c instanceof IPositionable) {

			positionable = (IPositionable) c;
			addToInspector((new Text(getLanguage().position, Color.black, new Vector2f(-100, y++ * 2 * spacing))),
					new TextField(new ChangeAction() {
						@Override
						public void onChange(Object source) {
							try {
								positionable.getPosition().x = Float.parseFloat(((TextField) source).getContent());
								setInactive(c);
							} catch (Exception e) {
							}
						}
					}, new UpdateAction() {
						@Override
						public void onUpdate(Object source, int delta) {
							if (((TextField) source).isSelected())
								textFieldSelected = true;
							if (!textFieldSelected) {
								((TextField) source).setContent(dataToString(positionable.getPosition().x));
							}
						}
					}, "", Color.black, new Vector2f(-150, y * 2 * spacing - spacing / 2f), 1),

					new TextField(new ChangeAction() {
						@Override
						public void onChange(Object source) {
							try {
								positionable.getPosition().y = Float.parseFloat(((TextField) source).getContent());
								setInactive(c);
							} catch (Exception e) {
							}
						}
					}, new UpdateAction() {
						@Override
						public void onUpdate(Object source, int delta) {
							if (((TextField) source).isSelected())
								textFieldSelected = true;
							if (!textFieldSelected) {
								((TextField) source).setContent(dataToString(positionable.getPosition().y));
							}
						}
					}, "", Color.black, new Vector2f(-75, y++ * 2 * spacing - spacing / 2f), 1));
		}
		if (c instanceof IRotatable) {
			rotatable = (IRotatable) c;
			addToInspector(new Text(getLanguage().rotation, Color.black, new Vector2f(-100, y++ * 2 * spacing)),
					new TextField(new ChangeAction() {

						@Override
						public void onChange(Object source) {
							try {
								rotatable.setRotation(
										(float) Math.toRadians(Float.parseFloat(((TextField) source).getContent())));
								setInactive(c);
							} catch (Exception e) {
							}
						}
					}, new UpdateAction() {
						@Override
						public void onUpdate(Object source, int delta) {
							if (((TextField) source).isSelected())
								textFieldSelected = true;
							if (!textFieldSelected)
								((TextField) source).setContent(dataToString(Math.toDegrees(rotatable.getRotation())));

						}
					}, "", Color.black, new Vector2f(-100, y++ * 2 * spacing - spacing / 2f), 1));

		}

		addToInspector(new Text(getLanguage().size, Color.black, new Vector2f(-100, y++ * 2 * spacing)));
		if (c instanceof IScalable) {
			scalable = (IScalable) c;
			addToInspector(new TextField(new ChangeAction() {
				@Override
				public void onChange(Object source) {
					try {
						scalable.setScale(Float.parseFloat(((TextField) source).getContent()));
						setInactive(c);
					} catch (Exception e) {
					}
				}
			}, new UpdateAction() {
				@Override
				public void onUpdate(Object source, int delta) {
					if (((TextField) source).isSelected())
						textFieldSelected = true;
					if (!textFieldSelected) {
						((TextField) source).setContent(dataToString(scalable.getScale(), 2));
					}
				}
			}, "", Color.black, new Vector2f(-100, y++ * 2 * spacing - spacing / 2f), 1));
		}
		if (c instanceof IResizable) {
			resizable = (IResizable) c;
			addToInspector(new TextField(new ChangeAction() {

				@Override
				public void onChange(Object source) {
					try {
						resizable.getDimensions().x = (Float.parseFloat(((TextField) source).getContent()));
						setInactive(c);
					} catch (Exception e) {
					}
				}
			}, new UpdateAction() {
				@Override
				public void onUpdate(Object source, int delta) {
					if (((TextField) source).isSelected())
						textFieldSelected = true;
					if (!textFieldSelected) {
						((TextField) source).setContent(dataToString(resizable.getDimensions().x, 2));
					}
				}
			}, "", Color.black, new Vector2f(-150, y * 2 * spacing - spacing / 2f), 1),
					new TextField(new ChangeAction() {
						@Override
						public void onChange(Object source) {
							try {
								resizable.getDimensions().y = (Float.parseFloat(((TextField) source).getContent()));
								setInactive(c);
							} catch (Exception e) {
							}
						}
					}, new UpdateAction() {
						@Override
						public void onUpdate(Object source, int delta) {
							if (((TextField) source).isSelected())
								textFieldSelected = true;
							if (!textFieldSelected) {
								((TextField) source).setContent(dataToString(resizable.getDimensions().y, 2));
							}
						}
					}, "", Color.black, new Vector2f(-75, y++ * 2 * spacing - spacing / 2f), 1));
		}
		if (c instanceof ColoredGlass)

		{
			coloredGlass = (ColoredGlass) c;
			addToInspector(new Text(getLanguage().strength, Color.black, new Vector2f(-100, y++ * 2 * spacing)),
					new TextField(new ChangeAction() {
						@Override
						public void onChange(Object source) {
							try {
								coloredGlass.setStrength((Float.parseFloat(((TextField) source).getContent())));
								setInactive(c);
							} catch (Exception e) {
							}
						}
					}, new UpdateAction() {
						@Override
						public void onUpdate(Object source, int delta) {
							if (((TextField) source).isSelected())
								textFieldSelected = true;
							if (!textFieldSelected) {
								((TextField) source).setContent(dataToString(coloredGlass.getStrength(), 2));
							}
						}
					}, "", Color.black, new Vector2f(-100, y++ * 2 * spacing - spacing / 2f), 1));
		}
		if (c instanceof IColorable) {
			colorable = (IColorable) c;
			addToInspector(new Text(getLanguage().colors, Color.black, new Vector2f(-100, y++ * 2 * spacing)));

			float yPos = y++ * 2 * spacing - spacing / 2f + 10;
			float iteration = 0;
			for (Color color : Colors.colors) {
				iteration++;

				float xPos = iteration * 30 - 195;
				createColorField(new Vector2f(xPos, yPos), color);
				if (xPos >= -45) {
					iteration = 0;
					yPos = y++ * 2 * spacing - spacing / 2f + 10;

				}
			}
		}

		if (c instanceof ILinkable) {
			linkable = (ILinkable) c;
			addToInspector(new Button(new ButtonAction() {
				@Override
				public void onRelease(Object source) {
					isLinking = true;
				}
			}, getLanguage().link, Color.black, new Vector2f(-150, y * 2 * spacing), 0.5f));
			addToInspector(new Button(new ButtonAction() {
				@Override
				public void onRelease(Object source) {
					isUnlinking = true;
				}
			}, getLanguage().unlink, Color.black, new Vector2f(-50, y++ * 2 * spacing), 0.5f));
		}

		addToInspector(

				new Text(getLanguage().movable, Color.black, new Vector2f(-50, y * 2 * spacing)),
				new Text(getLanguage().rotatable, Color.black, new Vector2f(-150, y++ * 2 * spacing)),

				new CheckBox(b -> {
					c.setMovable(b);
					setInactive(c);
				}, (source, delta) -> {
					if (!inInspector) {
						((CheckBox) source).setChecked(c.isMovable());
					}
				}, new Vector2f(-50, y * 2 * spacing - spacing / 2f), 25),

				new CheckBox(b -> {
					c.setRotatable(b);
					setInactive(c);
				}, (source, delta) -> {
					if (!inInspector) {
						((CheckBox) source).setChecked(c.isRotatable());
					}
				}, new Vector2f(-150, y++ * 2 * spacing - spacing / 2f), 25),

				new Button(deleteAction = new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						if (c != null) {
							remove(c);
							remove(lastGlow);
						}
					}
				}, getLanguage().delete, Color.black, new Vector2f(-100, ++y * 2 * spacing - spacing / 2f)));
	}

	private void setInactive(IControllable c) {
		System.out.println(c);
		if (c instanceof IActivatable && !((IActivatable) c).isActivated()) {
			((IActivatable) c).setInActiveState();
		}
	}

	private void addToInspector(IRenderable o) {
		inspectorPanel.add(o);
		toRemove.add(o);
	}

	private void addToInspector(IRenderable... iRenderables) {
		for (IRenderable iRenderable : iRenderables) {
			addToInspector(iRenderable);
		}
	}

	private void createColorField(Vector2f position, Color color) {
		addToInspector(new ColorField(new SelectAction() {

			@Override
			public void onSelect(Object source) {
				colorable.setColor(((ColorField) source).getColor());
				setInactive((IControllable) colorable);
			}
		}, position, 25, color));
	}

	private String dataToString(Number n, int places) {
		float ten = 1;
		while (places-- > 0)
			ten *= 10;
		float val = Math.round(n.floatValue() * ten) / ten;
		return val == (int) val ? Integer.toString((int) val) : Float.toString(val);
	}

	private String dataToString(Number n) {
		return dataToString(n, 0);
	}

	@Override
	public void win() {

	}
}
