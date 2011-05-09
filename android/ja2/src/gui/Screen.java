/*
 * Created on Nov 28, 2007
 */
package gui;

import java.util.concurrent.atomic.AtomicBoolean;

import emu.Emulator;
import video.ScreenImage;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Screen extends SurfaceView {
	private static final String LOG_TAG = Screen.class.getName();
	private final ScreenImage screenImage;
	private final AtomicBoolean ready = new AtomicBoolean();
	private final Activity activity;

	public Screen(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.screenImage = ((Emulator) context).getScreenImage();
		this.activity = (Activity) context;
		init();
	}

	public Screen(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.screenImage = ((Emulator) context).getScreenImage();
		this.activity = (Activity) context;
		init();
	}

	public Screen(final Context context) {
		super(context);
		this.screenImage = ((Emulator) context).getScreenImage();
		this.activity = (Activity) context;
		init();
	}

	private void init() {
		this.getHolder().addCallback(new SurfaceHolder.Callback() {
			@SuppressWarnings("unused")
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				// OK, so what?
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void surfaceCreated(@SuppressWarnings("unused") SurfaceHolder holder) {
				Screen.this.ready.set(true);
				Log.i(LOG_TAG, "surfaceCreated");
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void surfaceDestroyed(@SuppressWarnings("unused") SurfaceHolder holder) {
				Screen.this.ready.set(false);
				Log.i(LOG_TAG, "surfaceDestroyed");
			}
		});
	}

	public void plot() {
		if (!this.ready.get()) {
			Log.i(LOG_TAG, "skipping plot because surface is not ready");
			return;
		}

		this.activity.runOnUiThread(new Runnable() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {

				final SurfaceHolder holder = getHolder();
				final Canvas canvas = holder.lockCanvas();
				try {
					synchronized (holder) {
						Screen.this.screenImage.drawOnto(canvas);
					}
				} finally {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		});

	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(Screen.this.screenImage.getWidth(), Screen.this.screenImage.getHeight());
	}

	@SuppressWarnings("unused")
	@Override
	public void onDraw(final Canvas canvas) {
		// don't do anything!
	}

	@SuppressWarnings("unused")
	@Override
	public void draw(Canvas canvas) {
		// don't do anything!
	}
}
