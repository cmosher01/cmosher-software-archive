/*
 * Created on Jan 18, 2008
 */
public class analogtv_
{
	int n_colors;
	int interlace;
	int interlace_counter;
	double agclevel;
	/* If you change these, call analogtv_set_demod */
	double tint_control, color_control, brightness_control, contrast_control;
	double height_control, width_control, squish_control;
	double horiz_desync;
	double squeezebottom;
	double powerup;
	/* internal cache */
	int blur_mult;
	/* For fast display, set fakeit_top, fakeit_bot to
	 the scanlines (0..ANALOGTV_V) that can be preserved on screen.
	 fakeit_scroll is the number of scan lines to scroll it up,
	 or 0 to not scroll at all. It will DTRT if asked to scroll from
	 an offscreen region.
	 */
	int fakeit_top;
	int fakeit_bot;
	int fakeit_scroll;
	int redraw_all;
	int use_shm, use_cmap, use_color;
	int bilevel_signal;
	int visdepth, visclass, visbits;
	int red_invprec, red_shift, red_mask;
	int green_invprec, green_shift, green_mask;
	int blue_invprec, blue_shift, blue_mask;
	//	  Colormap colormap;
	int usewidth, useheight, xrepl, subwidth;
	//	  XImage *image; /* usewidth * useheight */
	//	  GC gc;
	int screen_xo, screen_yo; /* centers image in window */
	//	  void (*event_handler)(Display *dpy, XEvent *event);
	//	  int (*key_handler)(Display *dpy, XEvent *event,void *key_data);
	//	  void *key_data;
	int flutter_horiz_desync;
	int flutter_tint;
	//	  struct timeval last_display_time;
	int need_clear;
	/* Add hash (in the radio sense, not the programming sense.) These
	 are the small white streaks that appear in quasi-regular patterns
	 all over the screen when someone is running the vacuum cleaner or
	 the blender. We also set shrinkpulse for one period which
	 squishes the image horizontally to simulate the temporary line
	 voltate drop when someone turns on a big motor */
	double hashnoise_rpm;
	int hashnoise_counter;
	int[] hashnoise_times = new int[ANALOGTV.V];
	int[] hashnoise_signal = new int[ANALOGTV.V];
	int hashnoise_on;
	int hashnoise_enable;
	int shrinkpulse;
	double[] crtload = new double[ANALOGTV.V];
	int[] red_values = new int[ANALOGTV.CV_MAX];
	int[] green_values = new int[ANALOGTV.CV_MAX];
	int[] blue_values = new int[ANALOGTV.CV_MAX];
	analogtv_yiq[] yiq = new analogtv_yiq[ANALOGTV.PIC_LEN + 10];
	int[] colors = new int[256];
	int cmap_y_levels;
	int cmap_i_levels;
	int cmap_q_levels;
	int cur_hsync;
	int[] line_hsync = new int[ANALOGTV.V];
	int cur_vsync;
	double[] cb_phase = new double[4];
	double[][] line_cb_phase = new double[ANALOGTV.V][4];
	int channel_change_cycles;
	double rx_signal_level;
	double[] rx_signal = new double[ANALOGTV.SIGNAL_LEN + 2 * ANALOGTV.H];

	public analogtv_()
	{
		this.tint_control = 5.0;
		this.color_control = 70.0 / 100.0;
		this.brightness_control = 2.0 / 100.0;
		this.contrast_control = 50.0 / 100.0;
		this.height_control = 1.0;
		this.width_control = 1.0;
		this.powerup = 1000.0;
		this.agclevel = 1.0;
		for (int i = 0; i < this.yiq.length; ++i)
		{
			this.yiq[i] = new analogtv_yiq();
		}

		this.red_mask = 0xf800;//this.xgwa.visual->red_mask;
		this.green_mask = 0x07e0;//this.xgwa.visual->green_mask;
		this.blue_mask = 0x001f;//this.xgwa.visual->blue_mask;
		this.red_shift = this.red_invprec = -1;
		this.green_shift = this.green_invprec = -1;
		this.blue_shift = this.blue_invprec = -1;
		/* Is there a standard way to do this? Does this handle all cases? */
		int shift, prec;
		for (shift = 0; shift < 32; shift++)
		{
			for (prec = 1; prec < 16 && prec < 40 - shift; prec++)
			{
				long mask = (0xffffL >> (16 - prec)) << shift;
				if (this.red_shift < 0 && mask == this.red_mask)
				{
					this.red_shift = shift;
					this.red_invprec = 16 - prec;
				}
				if (this.green_shift < 0 && mask == this.green_mask)
				{
					this.green_shift = shift;
					this.green_invprec = 16 - prec;
				}
				if (this.blue_shift < 0 && mask == this.blue_mask)
				{
					this.blue_shift = shift;
					this.blue_invprec = 16 - prec;
				}
			}
		}
		if (this.red_shift < 0 || this.green_shift < 0 || this.blue_shift < 0)
		{
			throw new IllegalStateException("Can't figure out color space");
		}
		for (int i = 0; i < ANALOGTV.CV_MAX; i++)
		{
			int intensity = (int)Math.rint(Math.floor(Math.pow(i / 256.0,0.8) * 65535.0)); /* gamma correction */
			if (intensity > 65535)
				intensity = 65535;
			this.red_values[i] = ((intensity >> this.red_invprec) << this.red_shift);
			this.green_values[i] = ((intensity >> this.green_invprec) << this.green_shift);
			this.blue_values[i] = ((intensity >> this.blue_invprec) << this.blue_shift);
		}
	}
}
