/**
 * 
 */
package emu;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * @author Administrator
 * 
 */
public class AssetManagerSet extends HashSet<AssetManager> {
	public InputStream open(final String filename) throws IOException {
		for (final AssetManager mgr : this) {
			try {
				Log.i(this.getClass().getName(),"Trying to load asset: "+filename);
				return mgr.open(filename, AssetManager.ACCESS_BUFFER);
			} catch (final Throwable e) {
				// didn't work, so go on to the next one
				e.printStackTrace(); // just for debugging
			}
		}
		throw new IOException("Could not find any asset file named: " + filename);
	}
}
