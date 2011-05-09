/*
 * Created on Jan 21, 2008
 */

// TODO ANDROID implement screen drawing

package video;

import java.util.Observable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rasterizer;
import display.AppleNTSC;

public class ScreenImage extends Observable {
	private static final int WIDTH = AppleNTSC.H - AppleNTSC.PIC_START - 2;
	private static final int HEIGHT = VideoAddressing.VISIBLE_ROWS_PER_FIELD * 2;

	private int[] imageBuf;

	public ScreenImage() {
		this.imageBuf = new int[ScreenImage.WIDTH * ScreenImage.HEIGHT];
	}

	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	public void setElem(final int i, final int val) {
		synchronized (this.imageBuf) {
			this.imageBuf[i] = val;
		}
	}

	public void setAllElem(final int v) {
		final int n = ScreenImage.WIDTH * ScreenImage.HEIGHT;
		for (int i = 0; i < n; ++i) {
			setElem(i, v);
		}
	}

	public int getWidth() {
		return ScreenImage.WIDTH;
	}

	public int getHeight() {
		return ScreenImage.HEIGHT;
	}

//@formatter:off
//	public BufferedImage getImage()
//	{
//		return this.image;
//	}

//	private static final ImageObserver nullImageObserver = new ImageObserver()
//	{
//		@SuppressWarnings("unused")
//		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
//		{
//			return false;
//		}
//	};
//@formatter:on

	public void drawOnto(final Canvas canvas) {
		synchronized (this.imageBuf) {
//			Bitmap bitmap = Bitmap.createBitmap(this.imageBuf, WIDTH, HEIGHT, Bitmap.Config.RGB_565);
//			bitmap = Bitmap.createScaledBitmap(bitmap, 457, HEIGHT, false);
//			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.drawBitmap(this.imageBuf, 0, ScreenImage.WIDTH, 0, 0, ScreenImage.WIDTH, ScreenImage.HEIGHT, false, null);
		}
	}

//@formatter:off
//	public void dump(final String type, final File file) throws IOException
//	{
//		ImageIO.write(this.image,type,file);
//	}
//@formatter:on
}
