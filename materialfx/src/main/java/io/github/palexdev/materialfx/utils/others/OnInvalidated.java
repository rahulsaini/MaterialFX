package io.github.palexdev.materialfx.utils.others;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;

import java.util.function.Consumer;

/**
 * Concrete implementation of {@link When} that uses {@link InvalidationListener}s to
 * listen for changes for a given {@link ObservableValue}.
 * <p></p>
 * You can specify the action to perform when this happens using a {@link Consumer},
 * {@link #then(Consumer)}.
 * <p>
 * To activate the construct do not forget to call {@link #listen()} at the end.
 * <p></p>
 * An example:
 * <pre>
 * {@code
 *      BooleanProperty aSwitch = new SimpleBooleanProperty(false);
 *      When.onInvalidated(aSwitch) // You can also use... OnInvalidated.forObservable(...)
 *              .then(value -> System.out.println("Value switched to: " + value))
 *              .oneShot()
 *              .listen();
 * }
 * </pre>
 */
public class OnInvalidated<T> extends When<T> {
	//================================================================================
	// Properties
	//================================================================================
	private InvalidationListener listener;
	private Consumer<T> action;

	//================================================================================
	// Constructors
	//================================================================================
	private OnInvalidated(ObservableValue<T> observableValue) {
		super(observableValue);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Creates and instance of this construct for the given {@link ObservableValue}.
	 */
	public static <T> OnInvalidated<T> forObservable(ObservableValue<T> observableValue) {
		return new OnInvalidated<>(observableValue);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * To set the action to perform when the specified {@link ObservableValue}
	 * becomes invalid. The action is a {@link Consumer} that carries the new value
	 * of the observable.
	 */
	public OnInvalidated<T> then(Consumer<T> action) {
		this.action = action;
		return this;
	}

	/**
	 * Activates the {@code OnInvalidated} construct with the previously specified parameters.
	 * So, builds the {@link InvalidationListener} according to the {@link #isOneShot()} parameter,
	 * then adds the listener to the specified {@link ObservableValue} and finally puts the Observable and
	 * the OnInvalidated construct in the map.
	 */
	public OnInvalidated<T> listen() {
		if (oneShot) {
			listener = invalidated -> {
				action.accept(observableValue.getValue());
				dispose();
			};
		} else {
			listener = invalidated -> action.accept(observableValue.getValue());
		}

		observableValue.addListener(listener);
		whens.put(observableValue, this);
		return this;
	}

	/**
	 * Disposes the {@code OnInvalidated} construct by removing the {@link InvalidationListener}
	 * from the {@link ObservableValue}, then sets the listener to null and finally removes
	 * the observable from the map.
	 */
	@Override
	public void dispose() {
		if (observableValue != null && listener != null) {
			observableValue.removeListener(listener);
			listener = null;
			whens.remove(observableValue);
		}
	}
}
