package tools;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Lists {

	public static <T> void forEach(Iterable<T> list, Predicate<T> ifThis, Consumer<T> doThat, Consumer<T> orElse) {
		for (T t : list) {
			if (ifThis == null)
				return;
			if (ifThis.test(t) && doThat != null)
				doThat.accept(t);
			else if (orElse != null)
				orElse.accept(t);
		}
	}

	public static <T> void forEach(Iterable<T> list, Predicate<T> ifThis, Consumer<T> doThat) {
		forEach(list, ifThis, doThat, null);
	}

	public static <T> int getLastIndexOf(Iterable<T> list, Predicate<T> ifThis) {
		int index = -1;
		int i = 0;
		for (T t : list) {

			if (ifThis.test(t)) {
				index = i;
			}
			i++;
		}
		return index;
	}
}
