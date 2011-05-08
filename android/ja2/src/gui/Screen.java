/*
 * Created on Nov 28, 2007
 */
package gui;

import video.ScreenImage;
import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Screen extends SurfaceView {
	private final ScreenImage image;

	public Screen(final Context context, final ScreenImage screenImage) {
		super(context);
		this.image = screenImage;
	}

	public void plot() {
		final SurfaceHolder holder = getHolder();
		final Canvas canvas = holder.lockCanvas(null);
		try {
			synchronized (holder) {
				Screen.this.onDraw(canvas);
			}
		} finally {
			holder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.image.drawOnto(canvas);
	}
}
