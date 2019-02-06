package gameStates;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import connection.Connections;
import gui.Button;
import gui.Panel;
import gui.Picture;
import gui.Text;
import gui.TextField;
import main.App;
import renderer.IRenderable.Direction;
import settings.Graphic;
import tools.Loader;
import util.ButtonAction;

public class Menu extends BasicState {

	private Text loginName;
	private TextField username;
	private TextField password;
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

		Image lira = Loader.loadImage("!Menu/LiRa");

		int buttonHeight = (int) new Button(null, null, null).getApparentSize(Direction.height);
		int spaceing = buttonHeight + 10;
		float y = ((lira.getHeight() - Graphic.height / 2) + 75f) / spaceing;
		create(new Panel(

				new Picture(new Vector2f(0, -Graphic.height / 2 + lira.getHeight() / 2), lira),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						ButtonAction.super.onRelease(source);
						enterState(sbg, Select.class);
					}
				}, getLanguage().play, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						ButtonAction.super.onRelease(source);
						enterState(sbg, Edit.class);
					}
				}, getLanguage().levelEditor, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						ButtonAction.super.onRelease(source);
						enterState(sbg, Settings.class);
					}
				}, getLanguage().settings, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						ButtonAction.super.onRelease(source);
						App.exit();
					}
				}, getLanguage().quit, Color.black, new Vector2f(0, y++ * spaceing)),
				loginName = new Text("not logged in", Color.black, new Vector2f(-Graphic.width /4, -150)),
				new Text("A Game designed and programmed by Matthias Mayer", Color.black,new Vector2f(Graphic.width/2 - 250,Graphic.height/2 - 25))));
		
		create(new Panel(
				
				new Text("Username", Color.black,new Vector2f(-Graphic.width /4, -50)),
				username = new TextField("", Color.black, new Vector2f(-Graphic.width /4, -25),  1, false),
				new Text("Password", Color.black,new Vector2f(-Graphic.width /4, 25)),
				password = new TextField("", Color.black, new Vector2f(-Graphic.width /4, 50),  1, true),
				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Connections.login(username.getContent(), password.getContent());
								if(Connections.loggedIn())
									loginName.setContent("logged in as " + Connections.getUsername());
								else
									loginName.setContent("login failed");
							}
						}).start();
						ButtonAction.super.onRelease(source);
					}
				},"Log in", Color.black, new Vector2f(-Graphic.width /4, 100), 0.5f),
				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						Connections.logout();
						loginName.setContent("not logged in");
						ButtonAction.super.onRelease(source);
					}
				},"Log out", Color.black, new Vector2f(-Graphic.width /4, 150), 0.5f)
				));
	}
}
