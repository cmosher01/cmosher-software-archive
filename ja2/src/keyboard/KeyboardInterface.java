/*
 * Created on Dec 1, 2007
 */
package keyboard;

public interface KeyboardInterface
{
	byte get();
	void clear();
	boolean isLossless();
	void setLossless(boolean lossless);
}
