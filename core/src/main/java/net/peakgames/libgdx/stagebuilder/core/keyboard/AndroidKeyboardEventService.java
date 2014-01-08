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
				Log.d("<<", "keyboard check " + screenHeight + " " + visibleAreaHeight + " " + heightDifference );
				if (heightDifference > MIN_KEYBOARD_HEIGHT) {
					Log.d("<<", "open");
					//softKeyboardEventListener.softKeyboardOpened(heightDifference);
					keyboardVisible = true;
					eventSenderTask.cancel();
					Timer.schedule(eventSenderTask, 0.2f);
				} else {
					Log.d("<<", "close");
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
