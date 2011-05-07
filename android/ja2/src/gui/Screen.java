/*
 * Created on Nov 28, 2007
 */
package gui;

//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
//import javax.swing.JPanel;
//import javax.swing.SwingUtilities;

import android.content.Context;
import android.opengl.GLSurfaceView;
import video.ScreenImage;



public class Screen extends GLSurfaceView
{
//	private static final int FACTOR = 1;

	private final ScreenImage image;
//	private final AffineTransform affine = new AffineTransform();

//	private Graphics2D graphics;

	public Screen(final Context context, final ScreenImage screenImage)
	{
		super(context);
		this.image = screenImage;
		setRenderer(new GLSurfaceView.Renderer() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void onDrawFrame(final GL10 gl) {
				Screen.this.image.drawOnto(gl);
			}

			@Override
			public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
				gl.glViewport(0, 0, width, height);
			}

			@SuppressWarnings("unused")
			@Override
			public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
				// nothing to do, OK
			}});
	}

	public void plot()
	{
//		try
//		{
//			if (SwingUtilities.isEventDispatchThread())
//			{
//				plotScreen();
//			}
//			else
//			{
//				SwingUtilities.invokeAndWait(new Runnable()
//				{
//					public void run()
//					{
//						plotScreen();
//					}
//				});
//			}
//		}
//		catch (InterruptedException e)
//		{
//			Thread.currentThread().interrupt();
//		}
//		catch (InvocationTargetException e)
//		{
//			e.printStackTrace();
//		}
	}

	
//	protected void plotScreen()
//	{
//		if (this.graphics == null)
//		{
//			this.graphics = (Graphics2D)getGraphics();
//			if (this.graphics == null)
//			{
//				return;
//			}
//		}
//
//		this.image.drawOnto(this.graphics,this.affine);
//	}
//
//	/**
//	 * @param g
//	 */
//	@Override
//	public void paint(@SuppressWarnings("unused") Graphics g)
//	{
//		// we don't need to paint anything; we just let the
//		// emulated screen refresh do it's thing
//	}
}
