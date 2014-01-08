package net.peakgames.libgdx.stagebuilder.core.keyboard;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.utils.Timer;

public class AndroidKeyboardEventService implements SoftKeyboardEventInterface{

	private static final int MIN_KEYBOARD_HEIGHT = 120;
	private AndroidGraphics graphics;
	private SoftKeyboardEventListener softKeyboardEventListener;
	private boolean keyboardVisible = false;
	private int heightDifference;
	public AndroidKeyboardEventService(AndroidGraphics graphics) {
		this.graphics = graphics;
	}
	
	@Override
	public void initialize() {
		final View root = graphics.getView();
		root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				Rect visibleAreaRectangle = new Rect();
				root.getWindowVisibleDisplayFrame(visibleAreaRectangle);
				int screenHeight = root.getRootView().getHeight();
				int visibleAreaHeight = visibleAreaRectangle.bottom - visibleAreaRectangle.top;
				heightDifference = screenHeight - visibleAreaHeight;
				/**
				 * Added a delay before sending the events. On some devices, this is called three times while
				 * openning the keyboard, it fires three events: open close open. This causes some glitches.
				 * It waits 200ms before sending the event (each events resets cooldown) and sends the final state
				 * of the keyboard state
				 */
				if (heightDifference > MIN_KEYBOARD_HEIGHT) {
					//softKeyboardEventListener.softKeyboardOpened(heightDifference);
					keyboardVisible = true;
					eventSenderTask.cancel();
					Timer.schedule(eventSenderTask, 0.2f);
				} else {
					//softKeyboardEventListener.softKeyboardClosed(heightDifference);
					keyboardVisible = false;
					eventSenderTask.cancel();
					Timer.schedule(eventSenderTask, 0.2f);
				}
			}
		});
	}

	@Override
	public void setSoftKeyboardEventListener(
			SoftKeyboardEventListener eventListener) {
		this.softKeyboardEventListener = eventListener;
	}

	Timer.Task eventSenderTask = new Timer.Task() {
		@Override
		public void run() {
			if (keyboardVisible){
				softKeyboardEventListener.softKeyboardOpened(heightDifference);
			}else{
				softKeyboardEventListener.softKeyboardClosed(heightDifference);
			}
		}
	};
}
