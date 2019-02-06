package gameStates;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import connection.Connections;
import gui.Button;
import gui.CheckBox;
import gui.Panel;
import gui.Text;
import gui.TextField;
import settings.Actions;
import settings.Graphic;
import util.ButtonAction;

public class OnlineLevels extends BasicState {

	Panel p, p2;
	TextField author, name;
	CheckBox rating, date;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		
		create(	p = new Panel(),
				p2 = new Panel(new Text("Author, Name, Rating", Color.black, new Vector2f(0, -25))),
				new Panel(
				
					new Text("Filters", Color.black ,new Vector2f()),
					new Text("Author:", Color.black, new Vector2f(0, 50)),
					author = new TextField(o->refresh(sbg), "", Color.black, new Vector2f(100, 50), 1, false),
					new Text("Name:", Color.black, new Vector2f(0, 100)),
					name = new TextField(o->refresh(sbg), "", Color.black, new Vector2f(100, 100), 1, false),
					new Text("Sort by", Color.black, new Vector2f(0, 150)),
					new Text("Rating:", Color.black, new Vector2f(0,200)),
					new Text("Date:", Color.black, new Vector2f(0,250)),
					rating = new CheckBox(o->{date.setChecked(!o); refresh(sbg);}, new Vector2f(50,200)),
					date = new CheckBox(o->{rating.setChecked(!o); refresh(sbg);}, new Vector2f(50,250)),
					new Button(new ButtonAction() {
						@Override
						public void onRelease(Object source) {
							StateHandler.enterState(sbg, Menu.class);
						};
					},getLanguage().menu, Color.black, new Vector2f(0,350))
					).initPosition(new Vector2f(Graphic.width/2-200, -Graphic.height/2 +100)));
		rating.setChecked(true);
		super.init(gc, sbg);
	}

	class Refresh implements Runnable {
		Panel p;
		String author, name, order;
		StateBasedGame sbg;
		boolean stop;

		public Refresh(Panel p, String author, String name, String order, StateBasedGame sbg) {
			super();
			this.p = p;
			this.author = author;
			this.name = name;
			this.order = order;
			this.sbg = sbg;
		}

		@Override
		public void run() {
			p.clear();
			List<Integer> list = Connections.searchLevels(author, name, order);
			int k = 1;
			for (Integer i : list) {
				String s = Connections.getLevelInfo(i);
				if (stop)
					return;
				p.add(new Text(new ButtonAction() {
					@Override
					public void onDeselect(Object source) {
						((Text) source).setFontColor(Color.black);
						ButtonAction.super.onDeselect(source);
					}
					@Override
					public void onRelease(Object source) {
						((Text) source).setFontColor(Color.blue);
						ButtonAction.super.onRelease(source);
					}
					@Override
					public void onDoublePress(Object source) {
						StateHandler.getState(Game.class).setLevel(Connections.downloadLevel(i));
						StateHandler.enterState(sbg, Game.class);
						ButtonAction.super.onDoublePress(source);
					}
				}, s, Color.black, new Vector2f(0, spacing * k++)));
			}
		}

		public void stop() {
			stop = true;
		}
	}

	Refresh oldRefresh;

	private void refresh(StateBasedGame sbg) {
		if (oldRefresh != null)
			oldRefresh.stop();
		new Thread(oldRefresh = new Refresh(p, author.getContent(), name.getContent(),
				rating.isChecked() ? "rating" : "date", sbg)).start();
	}
	float offset;
	float spacing = 50;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		int count = p.objects.size();
		float wheel = Actions.getWheelAmount();
		float panelY = (float) (Math.round(wheel / spacing) * spacing);
		float minY =  Graphic.height / 5 / spacing;
		if (offset + panelY < (-count + minY) * spacing) {
			offset = (-count + minY) * spacing - panelY;
		}
		if (offset + panelY > -minY * spacing) {
			offset = -minY * spacing -panelY;
		}

		p.setTargetPosition(new Vector2f(0, offset + panelY));
		p2.setPosition(p.getPosition());
		super.update(gc, sbg, delta);
	}
	@Override
	public void enter(GameContainer container, StateBasedGame sbg) throws SlickException {
		refresh(sbg);
		super.enter(container, sbg);
	}
}
