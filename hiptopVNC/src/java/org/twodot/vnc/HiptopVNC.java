// Copyright (C) 2003 Jake Bordens.  All Rights Reserved.
// Copyright (C) 2002 Ultr@VNC Team Members. All Rights Reserved.
// Copyright (C) 2001,2002 HorizonLive.com, Inc.  All Rights Reserved.
// Copyright (C) 2002 Constantin Kaplinsky.  All Rights Reserved.
// Copyright (C) 1999 AT&T Laboratories Cambridge.  All Rights Reserved.

//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.

//
//  This file is part of
//         Hiptop VNC Client - a VNC client for the danger hiptop.
//

//version 0.10 - origial release
//version 0.11 - added stats code
//			   - modified disconnect code
//			   - fixed BACK(X) code to cancel mouse/alt-tab modes
//             - renamed some classes and changed the package
//-released a prebeta-
//             - added Disconnected/ing text
//             - back btn returns to jmp when not connected
//             - mouse can now translate when edge of screen is hit.
//-released a prebeta-
//             - fixed a fullscreen bug to request full clip rect
//             - cleaned up ALT-TAB mode. (now wheelMode based)
//             - fixed multiple DialogWindow problem created by new disconnect code.
//-released beta 2-
//version 0.12 - fixed a problem with "EOF" (-1 from stream) returning a blank error dialog
//             - added the dynamic splash screen
//             - minor cosmetic improvements
//-released beta 3-
//version 0.13 - added SolMonoZip Encoding, thus fixing Zlib support in UltraVNC
//             - new login dialog, no with encoder selection
//             - added a SettingsDB to remember last connection.

package org.twodot.vnc;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import danger.app.AppResources;
import danger.app.Application;
import danger.app.Event;
import danger.app.SettingsDB;
import danger.app.SettingsDBException;
import danger.ui.AlertWindow;
import danger.ui.Bitmap;
import danger.ui.CheckBox;
import danger.ui.Color;
import danger.ui.DialogWindow;
import danger.ui.Font;
import danger.ui.Menu;
import danger.ui.Pen;
import danger.ui.ProgressBar;
import danger.ui.RadioGroup;
import danger.ui.Rect;
import danger.ui.ScreenWindow;
import danger.ui.StaticTextBox;
import danger.ui.TextField;
import danger.ui.View;
import danger.util.DEBUG;
import danger.util.Pasteboard;

/**
 * @author Jake Bordens
 *
 * The application class is responsible for creating and
 * showing the VNCWindow object.
 */
public class HiptopVNC extends Application implements Resources, Commands {
	static VNCWindow	mainWindow;
    static SettingsDB   HTVNCSettings;
	public HiptopVNC() {
		//create a new VNCWindow object
        HTVNCSettings = new SettingsDB("prefs", true);
		mainWindow = new VNCWindow(this, HTVNCSettings);
	}

	//public void launch()  {}

	public void resume() {
		//show the VNCWindow object.
        mainWindow.show();
	}
	
	public void suspend() {
        updatePreviewScreen();
    }

    public void renderSplashScreen(View inView, Pen inPen) {

        inPen.drawBitmap(0,0, getBitmap(AppResources.ID_SPLASH_SCREEN));

        if (mainWindow.getConnectionState() == VNCWindow.CONNECTED) {

            inPen.drawBitmap(29, 27, mainWindow.getThumbnail());

            inPen.setColor(Color.BLACK);
            inPen.drawRect(28,26,29+91,27+61);

            rfbProto rfb =  mainWindow.getRFB();
            inPen.setFont(Font.findBoldOutlineSystemFont());
            inPen.drawText(20,100,"Connected to: ");

            inPen.setFont(Font.findOutlineSystemFont());
            inPen.drawText(20,110, rfb.desktopName);
            inPen.drawText(20,120, rfb.framebufferWidth + "x" + rfb.framebufferHeight + " display");

        }
    }
}


/**
 * @author Jake Bordens
 *
 * VNCWindow class - the VNC Main Window
 * 
 */
class VNCWindow extends ScreenWindow implements Runnable, Resources, Commands
{
	//****connectionState and Constants
	byte connectionState;
	public static final byte DISCONNECTED = 0;
	public static final byte CONNECTING = 1;
	public static final byte CONNECTED = 2;
	public static final byte DISCONNECTING = 3;

	//****wheelMode and Constants
	byte wheelMode;
	public static final byte NAVIGATE = 0;
	public static final byte SCROLL	= 1;
	public static final byte MOUSE = 2;
    public static final byte ALT_TAB = 3;

    //****miscConstants
    public static final int  DEFAULT_PORT=5900;

	//****VNC Members
	//readThread - owner of the connection to the server
	Thread			readThread;	
	//rfb - remote frame buffer connection object					
	rfbProto	 	rfb;			
	//screenBitmap - holds the local frame buffer
	Bitmap			screenBitmap;		
	Bitmap bitmapTmp;
	int lastWidth;
	int lastHeight;

	//incrementalUpdate - should the next update be full
	//                    or incremental?  true if incremental
	boolean			incrementalUpdate;						
	//clipRegion - holds the current viewing area of the
	//           - server's larger frameBuffer.				
	Rect			clipRegion;			
	//consumeNextJumpUpEvent - for panning the screen
	boolean 		consumeNextJumpUpEvent = false;

	//****Connection members - holds host/port/password
	//                         of the VNC server
	String			host;
	int				port;
	String			password;
    int             preferredEncoding;

    //****GUI objects - such as dialogs and menus
	DialogWindow    connectDialog;
	DialogWindow 	connectProgress;
	Menu 			menu;

	//****Wheel functionality objects - for mouse and
	//								    alt-tab modes
	int				mouseX, mouseY;
	boolean			mouseModeLeftRight;
	int				pointerDimensionOffset;	//half of the height/width of pointer image
	Bitmap			pointerLeftRight;		//leftRight image
	Bitmap			pointerUpDown;			//upDown image

	//****App_Name - default for dialog titles, etc.
	String			App_Name;
	
	//****Encoding memebers - used for decoding VNC rectangles
	RawEncoding		RawRect;
	CopyRectEncoding CopyRect;
	RREEncoding 	RRERect;
	CoRREEncoding	CoRRERect;
	HextileEncoding HextileRect;
	ZlibEncoding	ZlibRect;
	TightEncoding	TightRect;
    SolidColorEncoding SolidRect;
    SolMonoZipEncoding SolMonoZipRect;


    //****Screen translate amount
    int             translateAmount = 50;


    //****Application & Prefs
    Application myApp;
    SettingsDB  myPrefs;



	/**
	 * @param msg the message to display.
	 */
	void showAlert(String msg) {
		AlertWindow a = new AlertWindow(App_Name, msg, false, AlertWindow.SYSTEM_DIALOG_STYLE);
		a.show();
	}

	/**
	 *  Constructor
	 */
    public VNCWindow(Application a, SettingsDB p) {

        // initalize app and prefs
        myApp = a;
        myPrefs = p;

		//Set the default clipping region
		clipRegion = new Rect(0,0,0,0);

		//Initalize the application connection state to false (not connected)
		//connected=false;
		connectionState = DISCONNECTED;
		wheelMode = SCROLL;

		//setup the mouse pointers
		pointerUpDown = myApp.getBitmap(kID_MousePointerUpDown);
		pointerLeftRight = myApp.getBitmap(kID_MousePointerLeftRight);
		pointerDimensionOffset = pointerUpDown.getWidth()/2;
		
		//Setup the main menu
	 	menu = getActionMenu();
		menu.removeAllItems();
		menu.addFromResource(myApp.getResources(), kVNCClientMenuResID);
		menu.setEventListener(this);
		menu.setListenerForAllItems(this);

		//set title
		App_Name = myApp.getString(kID_App_Name);
		setTitle(App_Name);


		// CAM: switch to mouse mode at start
		wheelMode = MOUSE;
		mouseX = clipRegion.getWidth()/2;
		mouseY = clipRegion.getHeight()/2;
		mouseModeLeftRight = true;
		invalidate();
		// end CAM
	}

	/**
	 * Draws the frameBuffer (screenBitmap) and the mouse pointer
	 * @see danger.ui.View#paint(danger.ui.Pen)
	 */
	public void	paint(Pen pen) {
		super.paint(pen);
		if (screenBitmap != null) {
		    if (bitmapTmp == null || clipRegion.getWidth() != lastWidth || clipRegion.getHeight() != lastHeight)
		    {
		        lastWidth = clipRegion.getWidth();
		        lastHeight = clipRegion.getHeight();
			    bitmapTmp = new Bitmap(lastWidth, lastHeight);
			}
			screenBitmap.copyBitsTo(bitmapTmp, getBounds(), clipRegion);

			pen.drawBitmap(0,0,bitmapTmp);

			//if we're in MOUSE mode, draw the proper pointer.
			if (wheelMode == MOUSE)
				if (mouseModeLeftRight)
					pen.drawBitmap(mouseX - pointerDimensionOffset, mouseY - pointerDimensionOffset, pointerLeftRight);
				else
					pen.drawBitmap(mouseX - pointerDimensionOffset, mouseY - pointerDimensionOffset, pointerUpDown);

            //display the alt-tab indicator if necessary.
            if (wheelMode == ALT_TAB) {
                Bitmap altTabIcon = myApp.getBitmap(kID_AltTab);
                pen.drawBitmap(clipRegion.getWidth()-altTabIcon.getWidth() - 3, 3, altTabIcon);
            }
		}

        if (connectionState == DISCONNECTING || connectionState == DISCONNECTED) {
            //if we are disconnected, or in the process of disconnecting, then
            //display some text to the user.  the connecting state needs no
            //text because there is a dialog that is displayed.
            String textToDraw = "";
            switch (connectionState) {

                case DISCONNECTING:
                    textToDraw="Disconnecting...";
                    break;

                case DISCONNECTED:
                    textToDraw="Disconnected.";
            }

            Font myFont = Font.findBoldOutlineSystemFont();
            int xpos = (getWidth() /2) - (myFont.getWidth(textToDraw) / 2);
            int ypos = (getHeight() / 2) - ((myFont.getAscent()+myFont.getDescent()) / 2);
            pen.setFont(myFont);
            pen.setColor(Color.BLACK);
            pen.setTextOutlineColor(Color.WHITE);
            pen.drawText(xpos, ypos, textToDraw);
        }
	}

	
	/**
	 * Handles the widgetUp events
	 * @see danger.ui.View#eventWidgetUp(int, danger.app.Event)
	 */
	public boolean eventWidgetUp(int widget, Event event) {
		boolean	consumed = false;
		try{
			switch (widget){
				//the BACK button is the sends the keycode for
				//ESCAPE to the server, and consumes the event
    			case Event.DEVICE_BUTTON_BACK:
    				if (connectionState == CONNECTED) {
    					//if we are in mouse mode, cancel it
						if (wheelMode == MOUSE) {
							wheelMode = SCROLL;
							invalidate();
							consumed = true;
							break;
						}
    					
    					//send an escape key
    					rfb.sendKeyPressEvent(rfbProto.KEY_ESCAPE); //esc
    					
    					//if we are alt tabbing, cancel it.
    					if (wheelMode == ALT_TAB) {
							rfb.sendModifiers(0);
							wheelMode = NAVIGATE;
                            invalidate(); //to remove alt-tab icon
    					}
    					consumed = true;
    				} else {
                        myApp.returnToLauncher();
                        consumed = true;
                    }
    				break;
    
    			//Wheel PageUp code is handled in the
    			//"handleWheelPageUp()" function.
    			case Event.DEVICE_WHEEL_PAGE_UP:
    				handleWheelPageUp(event);
    				break;

				//Wheel PageUp code is handled in the
				//"handleWheelUp()" function.    
    			case Event.DEVICE_WHEEL:
    				handleWheelUp(event);
    				break;
        				
        		//If we are currently in alt-tab mode, then the
        		//wheel button ends the alt-tab session.
        		//If we are in mouse mode, then the wheel changes
        		//the mouse pointer movement direction.		
    			case Event.DEVICE_WHEEL_BUTTON:
    				if (wheelMode == ALT_TAB && connectionState == CONNECTED) {
    					rfb.sendModifiers(0);
    					wheelMode = NAVIGATE;
    					consumed=true;
                        invalidate(); //to remove the alt-tab icon
    				} else if (wheelMode == MOUSE) {
    					mouseModeLeftRight = !mouseModeLeftRight;
    					invalidate();
    				}
    
    				break;
    
    			//The Up/Down/Left/Right arorws, with JUMP held will
    			//pan the clipRegion.
    			//Without JUMP, the arrows will send the proper
    			//keycode to the VNC server.
    			case Event.DEVICE_ARROW_UP:
    				if ((event.modifiers & Event.EVENT_MODIFIER_JUMP_BUTTON)
    								== Event.EVENT_MODIFIER_JUMP_BUTTON) {
    					translate(0,-translateAmount);
    					consumed = true;
    					consumeNextJumpUpEvent = true;
    				} else {
    					if (connectionState == CONNECTED)
    						rfb.sendKeyPressEvent(rfbProto.KEY_UP, event.modifiers);
    				}
    				break;
    				
    			case Event.DEVICE_ARROW_DOWN:
    				if ((event.modifiers & Event.EVENT_MODIFIER_JUMP_BUTTON)
    								== Event.EVENT_MODIFIER_JUMP_BUTTON) {
    					translate(0,translateAmount);
    					consumed = true;
    					consumeNextJumpUpEvent = true;
    				} else {
    					if (connectionState == CONNECTED)
    						rfb.sendKeyPressEvent(rfbProto.KEY_DOWN, event.modifiers);
    				}
    				break;
    				
    			case Event.DEVICE_ARROW_LEFT:
    				if ((event.modifiers & Event.EVENT_MODIFIER_JUMP_BUTTON)
    								== Event.EVENT_MODIFIER_JUMP_BUTTON) {
    					translate(-translateAmount,0);
    					consumed = true;
    					consumeNextJumpUpEvent = true;
    				} else {
    					if (connectionState == CONNECTED)
    						rfb.sendKeyPressEvent(rfbProto.KEY_LEFT, event.modifiers);
    				}
    				break;
    				
    			case Event.DEVICE_ARROW_RIGHT:
    				if ((event.modifiers & Event.EVENT_MODIFIER_JUMP_BUTTON)
    								== Event.EVENT_MODIFIER_JUMP_BUTTON) {
    					translate(translateAmount,0);
    					consumed = true;
    					consumeNextJumpUpEvent = true;
    				} else {
    					if (connectionState == CONNECTED)
    						rfb.sendKeyPressEvent(rfbProto.KEY_RIGHT, event.modifiers);
    				}
    				break;
    			
    			//After panning the clipRegion, there will be an
    			//"JUMP released" event.  This consumes such an
    			//event.  This prevents us from being transported
    			//back to the JUMP screen after panning. 	
    			case Event.DEVICE_BUTTON_JUMP:
    				if (consumeNextJumpUpEvent) {
    					consumeNextJumpUpEvent = false;
    					consumed=true;
    				}
    				break;
    		}
		} catch (Exception e) {}
		return (consumed || super.eventWidgetUp(widget, event));
	}

	/**
	 * Called by eventWidgetUp function.  Handles the event
	 * thrown when the user moves the wheel up a notch.
	 * 
	 * @param event  the incoming event from the eventWidgetUp handler
	 */
	public void handleWheelUp(Event event) {
		//only bother with this event when connected
		if (connectionState == CONNECTED) {
			try {
				switch (wheelMode) {
					//if we are in navigation mode, wheel up = ALT-TAB
					//to move back one widget.
					case ALT_TAB:
                        rfb.sendKeyPressEvent(rfbProto.KEY_TAB);
                        break;

                    case NAVIGATE:
						rfb.sendKeyPressEvent(rfbProto.KEY_TAB,Event.EVENT_MODIFIER_CAPS); //shift-TAB
						break;

					//if we are in SCROLL mode, wheel up = UP arrow
					case SCROLL:
						rfb.sendKeyPressEvent(rfbProto.KEY_UP, 0); // up
						break;

					//if we are in MOUSE mode, the up arrow subtracts 1
					//from the current movement direction counter.
					case MOUSE:
						if (mouseModeLeftRight) {
							mouseX -= 80;
							if (mouseX < 0) {
                                 if (clipRegion.left > 0) {
                                    mouseX = translateAmount;
                                    translate(-translateAmount, 0);
                                } else mouseX=0;
                            }
						} else {
							mouseY -= 80;
							if (mouseY < 0) {
                                if (clipRegion.top > 0) {
                                    mouseY = translateAmount;
                                    translate(0,-translateAmount);
                                } else mouseY = 0;

                            }
						}
						invalidate();
						break;
				}
			} catch (Exception e) {}
		}
	}

	/**
	 * This function is called by eventWidgetUp to handle
	 * the wheel page up event (MENU+wheel up one notch)
	 * 
	 * @param event  the event object from eventWidgetUp
	 */
	public void handleWheelPageUp(Event event) {
		//only do this if we are connected
		if (connectionState == CONNECTED) {
			try {
				switch (wheelMode) {
					//if we are navigating, page up is ALT-TAB.
					//if we are already ALT-TABBING, don't put down
					//the ALT modifier.  If this is the first ALT-TAB
					//command, send the ALT modifier first.
                    case NAVIGATE:
                        //entering as navigate, this is the
                        //first alt-tab event
                        rfb.sendModifiers(Event.EVENT_MODIFIER_ALT);
						wheelMode = ALT_TAB;
                        invalidate(); //show the alt-tab icon
                        //fall through

                    case ALT_TAB:
						rfb.sendKeyPressEvent(rfbProto.KEY_TAB);
						break;

					//if we are in scroll mode, page up = PAGE UP
					case SCROLL:
						rfb.sendKeyPressEvent(rfbProto.KEY_PAGE_UP, 0); //PageUP
						break;

					//if we are in mouse mode, move the pointer back
					//a larger unit than 1. (Initally implemented as a move
					//of 5 pixels.)
					case MOUSE:
						if (mouseModeLeftRight) {
							mouseX -= 5;
							if (mouseX < 0) {
                                if (clipRegion.left > 0) {
                                    mouseX = translateAmount;
                                    translate(-translateAmount, 0);
                                }  else mouseX=0;
                            }
						} else {
							mouseY -= 5;
							if (mouseY < 0) {
                                if (clipRegion.top > 0) {
                                    mouseY = translateAmount;
                                    translate(0, -translateAmount);
                                } else mouseY = 0;
                            }
						}
						invalidate();
						break;
				}
			} catch (Exception e) {}
		}
	}

	/**
	 * called by eventWidgetDown to handle the case where
	 * the user holds MENU+scrolling down one notch.
	 * @param event  the event from eventWidgetDown
	 */
	public void handleWheelPageDown(Event event) {
		//only if connected
		if (connectionState == CONNECTED) {
			try {
				switch (wheelMode) {
					//in navigation mode, this means ALT-TAB.
					//if this is the first ALT-TAB, send the ALT
					//modifier first.  Otherwise, just send a tab.
					case NAVIGATE:
						rfb.sendModifiers(Event.EVENT_MODIFIER_ALT);
						wheelMode = ALT_TAB;
                        invalidate(); //show the alt-tab icon
                        //fallthrough

                    case ALT_TAB:
						rfb.sendKeyPressEvent(rfbProto.KEY_TAB);
						break;

					//in scroll mode, page down = PAGE DOWN.
					case SCROLL:
						rfb.sendKeyPressEvent(rfbProto.KEY_PAGE_DOWN, 0); //PageDN
						break;

					//advance the mouse pointer 5 pixels.
					case MOUSE:
						if (mouseModeLeftRight) {
							mouseX += 5;
							if (mouseX > clipRegion.getWidth()){
                                //we've hit the edge of the screen.
                                if (clipRegion.right < rfb.framebufferWidth) {
                                    mouseX = clipRegion.getWidth() - translateAmount;
                                    translate(translateAmount,0);
                                } else mouseX = clipRegion.getWidth();
                            }
						} else {
							mouseY += 5;
							if (mouseY > clipRegion.getHeight()) {
                                //hit edge of screen.
                                if (clipRegion.bottom < rfb.framebufferHeight) {
                                    mouseY = clipRegion.getHeight() - translateAmount;
                                    translate(0, translateAmount);
                                } else mouseY = clipRegion.getHeight();
                            }
						}
						invalidate();
						break;
				}
			} catch (Exception e) {}
		}
	}

	/**
	 * called by eventWidgetDown to handle the case where
	 * the user scrolls the wheel down one notch.
	 * 
	 * @param event sent by eventWidgetDown
	 */
	public void handleWheelDown(Event event) {
		//only if connected
		if (connectionState == CONNECTED) {
			try {
				switch (wheelMode) {
					//if navigation mode, this is a TAB event
					//to move the widget focus forward by one.
					case NAVIGATE:
                    case ALT_TAB:
						rfb.sendKeyPressEvent(rfbProto.KEY_TAB);//TAB
						break;

					//if scroll mode, wheel down = DOWN arrow
					case SCROLL:
						rfb.sendKeyPressEvent(rfbProto.KEY_DOWN, 0); //down
						break;

					//if mouse mode, move the pointer by 1 unit.
					case MOUSE:
						if (mouseModeLeftRight) {
							mouseX += 80;
							if (mouseX > clipRegion.getWidth()) {
                                //we've hit the edge of the screen.
                                //check to see if the clipregion is at the edge of the
                                //screen.  If not, then
                                //translate and set mouse pointer
                                if (clipRegion.right < rfb.framebufferWidth) {
                                    mouseX = clipRegion.getWidth() - translateAmount;
                                    translate(translateAmount,0);
                                }  else mouseX = clipRegion.getWidth();
                            }
						} else {
							mouseY += 80;
							if (mouseY > clipRegion.getHeight()) {
                                //we've hit the bottom of the screen
                                //if the clip region is not at the bottom already
                                //translate and set the mouse
                                if (clipRegion.bottom < rfb.framebufferHeight) {
                                    mouseY = clipRegion.getHeight() - translateAmount;
                                    translate(0, translateAmount);
                                } else mouseY = clipRegion.getHeight();
                            }
						}
						invalidate();
						break;
				}
			} catch (Exception e) {}
		}
	}


	/**
	 * handles MENU+character.  In HipTop VNC, MENU is like
	 * the CONTROL modifier.
	 * 
	 * @see danger.ui.View#eventShortcut(char, danger.app.Event)
	 */
	public boolean eventShortcut (char c, Event event) {
		if (connectionState == CONNECTED) {
			try {
				if (c >= 'a' && c <= 'z')
					rfb.sendKeyPressEvent(c, Event.EVENT_MODIFIER_MENU_BUTTON); //control		
			    // special case: change MENU+DEL into Control+Alt+Delete
			    else if (c == '\b')
			        rfb.sendKeyPressEvent(0xFFFF, Event.EVENT_MODIFIER_MENU_BUTTON|Event.EVENT_MODIFIER_ALT);
			} catch (Exception e) {}
		}
		return super.eventShortcut(c, event);
	}


	/**
	 * Handles a keypress event.
	 * 
	 * @see danger.ui.View#eventKeyDown(char, danger.app.Event)
	 */
	public boolean eventKeyDown (char c, Event event) {
		int keycode = c;
		int modifiers = 0xFFFFFFFF;  //set to zero if we don't want to send modifiers

		if (connectionState == CONNECTED) {
			//set up tab, | and ` just like Terminal does.
			if (event.modifierIsActive(Event.EVENT_MODIFIER_ALT) && c == 'a') {
				keycode = (int)'\t';
				modifiers = 0;
			} else if (event.modifierIsActive(Event.EVENT_MODIFIER_CAPS) && c == '\\') {
				keycode = (int)'`';
				modifiers = 0;
			} else if (event.modifierIsActive(Event.EVENT_MODIFIER_CAPS) && c == '/') {
				keycode = (int)'|';
				modifiers = 0;
			} else if (c == 13) {
				keycode = rfbProto.KEY_ENTER;
			} else if (c == 8) {
				keycode = rfbProto.KEY_BACKSPACE;
			}

			try {
				rfb.sendKeyPressEvent(keycode, modifiers & event.modifiers);
			} catch (Exception e) {}
		}
		return super.eventKeyDown(c, event);
	}

	/**
	 * Handle events
	 * 
	 * @see danger.app.Listener#receiveEvent(danger.app.Event)
	 */
	public boolean receiveEvent(Event e) {

        int enc;  //temorary int for encoding radio box processing

		switch (e.type)  {
			//kCancelConnectEvent
			//	raised by:	ConnectionProgress dialog
			//  purpose:	Cancel the connection
			case kCancelConnectEvent:
				vncDisconnect();
				break;
				
			//kMenu_Tabs
			//	raised by:	Wheel Mode Menu
			//  purpose:	set wheel mode to tab navigation
			case kMenu_Tabs:
				wheelMode = NAVIGATE;
				break;

			//kMenu_Scroll
			//	raised by:	Wheel Mode Menu
			//  purpose:	Set wheel mode to scroll
			case kMenu_Scroll:
				wheelMode = SCROLL;
				break;
				
			//kMenu_MouseTarget
			//	raised by:	Wheel Mode Menu
			//  purpose:	Initalize mouse mode
			case kMenu_MouseTarget:
				wheelMode = MOUSE;
				mouseX = clipRegion.getWidth()/2;
				mouseY = clipRegion.getHeight()/2;
				mouseModeLeftRight = true;
				invalidate();
				break;

			//kMenu_Connect
			//	raised by:	main menu
			//  purpose:	display the connection dialog
 			case kMenu_Connect:
                //read in the prefs
                 if (myPrefs != null) {
                    try {
                        host = myPrefs.getStringValue("host");
                        port = myPrefs.getIntValue("port");
                        preferredEncoding = myPrefs.getIntValue("encoding");
                    } catch (SettingsDBException sdbe) {
                        host = "";
                        port = DEFAULT_PORT;
                        preferredEncoding = rfbProto.EncodingTight;
                    }
                 } else {
                    host = "";
                    port = DEFAULT_PORT;
                    preferredEncoding = rfbProto.EncodingTight;
                 }

                //get the connect dialog from the resource.
				connectDialog = myApp.getDialog(kConnectDialog, this);

                // get the current host/port and set the up the dialog
                ((TextField)(connectDialog.getChildWithID(txtHost))).setText(host);
                ((TextField)(connectDialog.getChildWithID(txtPort))).setText(Integer.toString(port));

                // get the current preferred encoding, and set up the dialog
                if (preferredEncoding == rfbProto.EncodingTight) enc = rbTight;
                else if (preferredEncoding == rfbProto.EncodingZlib) enc = rbZlib;
                else enc = rbStandard;
                ((RadioGroup)(connectDialog.getChildWithID(rbgEncoding))).selectItemWithID(enc);

                //set default focus
                if (host != "")
                    connectDialog.setFocusedChild((connectDialog.getChildWithID(txtPassword)), true);


                //show the dialog.
                connectDialog.show();
				break;

			//kMenu_Disconnect
			//  raised by:	main menu
			//  purpose:    disconnect the vnc client
			case kMenu_Disconnect:
				disconnect();
				break;

			//kConnectEvent
			//  raised by:  Connection dialog
			//  purpose:    initiate the connection based on the dialog's settings
			case kConnectEvent:
				String host = (connectDialog.getChildWithID(txtHost)).toString();
				int port = Integer.parseInt((connectDialog.getChildWithID(txtPort)).toString());
				String password = (connectDialog.getChildWithID(txtPassword)).toString();

                enc = ((RadioGroup)(connectDialog.getChildWithID(rbgEncoding))).getIDOfSelectedItem();

                if (enc  == rbTight) preferredEncoding = rfbProto.EncodingTight;
                else if (enc == rbZlib) preferredEncoding = rfbProto.EncodingZlib;
                else preferredEncoding = 0;

                // save these into prefrences if desired.
                if (myPrefs != null && ((CheckBox)connectDialog.getChildWithID(chkSaveSettings)).getValue() != 0) {
                    DEBUG.p("Saving settings!");
                    myPrefs.setStringValue("host", host);
                    myPrefs.setIntValue("port", port);
                    myPrefs.setIntValue("encoding", preferredEncoding);
                }

				connect(host, port, password);
				break;

			//kMenu_Toggle
			//  raised by:	main menu
			//  purpose:	toggle full screen mode.
			case kMenu_Toggle:
				setFullScreen(!isFullScreen());
				//invalidate();
                break;

			//kMenu_LeftClick
			//  raised by:	Mouse Mode Menu
			//  purpose:	Send a left click, if connected
			case kMenu_LeftClick:
				if (connectionState == CONNECTED)
					try {
						rfb.writePointerClick(mouseX + clipRegion.left, mouseY + clipRegion.top);
					} catch (IOException ex) {}
				break;
				
			//kMenu_MiddleClick
			//  raised by:	Mouse Mode Menu
			//  purpose:	send middle click, if connected
			case kMenu_MiddleClick:
			    if (connectionState == CONNECTED)
					try {
						rfb.writePointerClick(mouseX + clipRegion.left, mouseY + clipRegion.top,2);
					} catch (IOException ex) {}
				break;
				
			//kMenu_RightClick
			//  raised by:	Mouse Mode Menu
			//  purpose: 	send right click, if connected
			case kMenu_RightClick:
				if (connectionState == CONNECTED)
					try {
						rfb.writePointerClick(mouseX + clipRegion.left, mouseY + clipRegion.top,4);
					} catch (IOException ex) {}
				break;
			
			//kMenu_CancelMouse
			//  raised by:	Mouse Mode Menu
			//  purpose:	Exit Mouse Mode			
            case kMenu_CancelMouse:
            	wheelMode = SCROLL;
            	invalidate();
            	break;
            
			//kMenu_About
			//  raised by:	main menu
			//  purpose:	display the About dialog
            case kMenu_About:
            	myApp.showDialog(kAboutDialog, this);
            	break;

			//kMenu_Clipboard
			//  raised by:	main menu
			//  purpose:	send the contents of the pasteboard to VNC server
			case kMenu_Clipboard:
				if (connectionState == CONNECTED) {
					try {
						rfb.writeClientCutText(Pasteboard.getString());
					} catch (IOException ex) {}
				}
				break;

			case kMenu_Stats:
				String stats_text = "Statistics\n";
				stats_text += "Encoding: bytes / rects\n";
				stats_text += "Raw: " + RawRect.getStats(Encoding.STAT_BYTES) + " / " + RawRect.getStats(Encoding.STAT_RECTS) + "\n";
				stats_text += "Copy: " + CopyRect.getStats(Encoding.STAT_BYTES) + " / " + CopyRect.getStats(Encoding.STAT_RECTS) + "\n";
				stats_text += "RRE: " + RRERect.getStats(Encoding.STAT_BYTES) + " / " + RRERect.getStats(Encoding.STAT_RECTS) + "\n";
				stats_text += "CoRRE: " + CoRRERect.getStats(Encoding.STAT_BYTES) + " / " + CoRRERect.getStats(Encoding.STAT_RECTS) + "\n";
				stats_text += "Hextile: " + HextileRect.getStats(Encoding.STAT_BYTES) + " / " + HextileRect.getStats(Encoding.STAT_RECTS) + "\n";
				stats_text += "Zlib: " + ZlibRect.getStats(Encoding.STAT_BYTES) + " / " + ZlibRect.getStats(Encoding.STAT_RECTS) + "\n";
				stats_text += "Tight: " + TightRect.getStats(Encoding.STAT_BYTES) + " / " + TightRect.getStats(Encoding.STAT_RECTS) + "\n";
				
				DialogWindow stats = myApp.getDialog(kStatsDialog, this);
				StaticTextBox textBox = (StaticTextBox)stats.getChildWithID(kID_Statistics);
				textBox.setText(stats_text);
				
				stats.show();
				break;

            case kMenu_Help:
                myApp.showDialog(kHelpDialog, this);
                break;

		}
		return (super.receiveEvent(e));
	}

	public boolean eventWidgetHeld(int widget, Event event) {
		boolean	consumed = false;
		switch (widget) {
			case Event.DEVICE_WHEEL_BUTTON:
			
				Menu m = new Menu("Wheel Menu", true);
				m.setWindow(this);
				m.removeAllItems();

				if (wheelMode == MOUSE)
					//Mouse Mode Menu
					m.addFromResource(myApp.getResources(), kMouseMenuResID);
				else {
					//must finish alt tabbing before chaning modes
					if (wheelMode == ALT_TAB) break;
					
					//Setup wheel Mode Menu
					m.addFromResource(myApp.getResources(), kWheelMenuResID);
					m.getItemWithID(kID_MenuScroll).setChecked(wheelMode == SCROLL);
					m.getItemWithID(kID_MenuTabs).setChecked(wheelMode == NAVIGATE);
					m.getItemWithID(kID_MenuMouse).setChecked(wheelMode == MOUSE);
				}
				
				m.setListenerForAllItems(this);
				m.setEventListener(this);
				m.enterMenu(true);  //spring loaded
				break;
		}
        return (consumed || super.eventWidgetHeld(widget, event));
	}

	/**
	 * Handle wheel down and page down events.
	 * Calls the helper functions handleWheelDown and
	 * handleWheelPageDown.
	 * 
	 * @see danger.ui.View#eventWidgetDown(int, danger.app.Event)
	 */
	public boolean eventWidgetDown(int widget, Event event) {
		boolean	consumed = false;
		switch (widget) {
			case Event.DEVICE_WHEEL:
				handleWheelDown(event);
				break;

			case Event.DEVICE_WHEEL_PAGE_DOWN:
				handleWheelPageDown(event);
				break;
		}
        return (consumed || super.eventWidgetDown(widget, event));
	}



	/**
	 * Main thread.  Connectes to the VNC server and then
	 * polls the server repeatedly for updates.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			vncConnect(host, port, password);
			while (connectionState == CONNECTED) {
				vncUpdate();
				Thread.yield();
			}
			vncDisconnect();
        } catch (EOFException eof) {
            showAlert("Server has unexpectedly disconnected.");
            vncDisconnect();
            return;
		} catch (Exception e) {
			//if we are disconnecting, don't show any errors.
			if (connectionState != DISCONNECTING) {
				showAlert(e.getMessage());
				vncDisconnect();
				return;
			}
		}
	}


	/**
	 * Set the host, port, and password members, then start
	 * the main thread.
	 * 
	 * @param _host			VNC server's host name
	 * @param _port			VNC server's port
	 * @param _password		password to VNC server
	 */
	public void connect(String _host, int _port, String _password) {
		host = _host;
		port = _port;
		password = _password;

		connectProgress = myApp.showDialog(kConnectProgress, this);
		readThread = new Thread(this);
		readThread.start();
	}

	/**
	 * Request that the server be disconnected.
	 * This will end the loop in the main thread
	 * and hopefully disconnect gracefully from the server.
	 */
	public void disconnect() {
		connectionState = DISCONNECTING;
		
		//"Kick" the server with a bogus update. (In case the run
		//thread is blocking, this should get it moving, to exit
		//gracefully)
		try {
		  rfb.writeFramebufferUpdateRequest(0, 0, 1, 1, false);
		} catch (Exception e) {}
	}

	/**
	 * The "nuts and bolts" of connecting to the VNC server.
	 * 
	 * @param _host
	 * @param _port
	 * @param _password
	 * @throws IOException
	 */
	private void vncConnect(String _host, int _port, String _password) throws IOException {

		setTitle("Connecting...");
		connectionState = CONNECTING;

		//this progress bar shows the user the progress in
		//the connectionProgress dialog box.
		ProgressBar pbBar= (ProgressBar)(connectProgress.getChildWithID(pbProgressBar));

    	boolean authenticationDone = false;

		//create the connection and handle a few error cases.
		try {
			rfb = new rfbProto(_host, _port);
		} catch (UnknownHostException uhe) {
			showAlert("Cannot find host!");
			vncDisconnect();
			return;
		} catch (ConnectException ce) {
			showAlert("Unable to connect to given host/port");
			vncDisconnect();
			return;
		} catch (Exception e) {
			showAlert("Connection error!");
			vncDisconnect();
			return;
		}

		//if we've succesfully connected, read server version
		pbBar.advanceState();  //1
		rfb.readVersionMsg();

		System.out.println("RFB server supports protocol version " +
					 rfb.serverMajor + "." + rfb.serverMinor);

		pbBar.advanceState(); //2
		rfb.writeVersionMsg();

		System.out.println("Sent version message...");

		//See what kind of authentication the server handles
		switch (rfb.readAuthScheme()) {

		    case rfbProto.NoAuth:
				System.out.println("No authentication needed");
				authenticationDone = true;
				break;

		    case rfbProto.VncAuth:
		    	pbBar.advanceState(); //3
				byte[] challenge = new byte[16];
				rfb.is.readFully(challenge);

				if (_password.length() > 8) _password = _password.substring(0,8); // truncate to 8 chars

				//if (_password.length() == 0) {
				//    //authenticator.retry();
                //    break;
				//}

				//byte[] key = _password.substring(0,7).getBytes();
				byte[] key = new byte[8];
				_password.getBytes(0, _password.length(), key, 0);

				for (int i = _password.length(); i < 8; i++) {
			  		key[i] = (byte)0;
				}

				DesCipher des = new DesCipher(key);

				des.encrypt(challenge,0,challenge,0);
				des.encrypt(challenge,8,challenge,8);

				rfb.os.write(challenge);

				int authResult = rfb.is.readInt();

				switch (authResult) {
					case rfbProto.VncAuthOK:
						pbBar.advanceState(); //4
						System.out.println("VNC authentication succeeded");
			  			authenticationDone = true;
			  			break;

					case rfbProto.VncAuthFailed:
					  	showAlert("VNC authentication failed");
					  	//vncDisconnect();
					  	break;

					case rfbProto.VncAuthTooMany:
						showAlert("VNC authentication failed - " +
									"too many tries");
						//vncDisconnect();

					default:
						showAlert("Unknown VNC authentication result " +
								 authResult);
						//vncDisconnect();
				}
			break;
		}

		if (authenticationDone) {
			pbBar.advanceState(); //5
			setTitle(App_Name);
			setSubTitle("Connected " + host);
		} else {
			connectionState = DISCONNECTED;
			vncDisconnect();
            //connectProgress.hide();  //removed, done by vncDisconnect
			return;
		}

		//Initalize the VNC session
		pbBar.advanceState(); //6
		rfb.writeClientInit();

		pbBar.advanceState(); //7
		rfb.readServerInit();

		System.out.println("Desktop name is " + rfb.desktopName);
		System.out.println("Desktop size is " + rfb.framebufferWidth + " x " +
						       rfb.framebufferHeight);

		//setup the frame buffer bitmap
		screenBitmap = new Bitmap(rfb.framebufferWidth, rfb.framebufferHeight);

		//setup encodings
		RawRect = new RawEncoding(rfb, screenBitmap);
		CopyRect = new CopyRectEncoding(rfb, screenBitmap);
		RRERect = new RREEncoding(rfb, screenBitmap);
		CoRRERect = new CoRREEncoding(rfb, screenBitmap);
		HextileRect = new HextileEncoding(rfb, screenBitmap);
		ZlibRect = new ZlibEncoding(rfb, screenBitmap);
		TightRect = new TightEncoding(rfb, screenBitmap);
        SolidRect = new SolidColorEncoding(rfb, screenBitmap);
        SolMonoZipRect = new SolMonoZipEncoding(rfb, screenBitmap);

		//send supported encodings to the server
		try {
			if ((rfb != null) && rfb.inNormalProtocol) {
				int[] encodings = new int[10];

				int encodingCount = 0;

				encodings[encodingCount++] = rfbProto.EncodingCopyRect;

                if (preferredEncoding == rfbProto.EncodingTight) {
                    // we prefer Tight, then Zlib
                    encodings[encodingCount++] = rfbProto.EncodingTight;
                    encodings[encodingCount++] = rfbProto.EncodingZlib;
                } else if (preferredEncoding == rfbProto.EncodingZlib) {
                    // we prefer Zlib, then tight
                    encodings[encodingCount++] = rfbProto.EncodingZlib;
                    encodings[encodingCount++] = rfbProto.EncodingTight;
                }

                //encodings[encodingCount++] = rfbProto.EncodingXOREnable;


				encodings[encodingCount++] = rfbProto.EncodingHextile;
				encodings[encodingCount++] = rfbProto.EncodingCoRRE;
				encodings[encodingCount++] = rfbProto.EncodingRRE;
				encodings[encodingCount++] = rfbProto.EncodingRaw;

                if (preferredEncoding == rfbProto.EncodingTight ||
                    preferredEncoding == rfbProto.EncodingZlib) {
                    //these modifiers are needed for Zlib or Tight
                    encodings[encodingCount++] = rfbProto.EncodingLastRect;  //Must have for tight/zlib + scaling.
                    encodings[encodingCount++] = rfbProto.EncodingCompressLevel0;
                }

				pbBar.advanceState(); //8
				rfb.writeSetEncodings(encodings, encodingCount);
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}

		//Tell the server we're using 8-bit true color.
		pbBar.advanceState(); //9
		rfb.writeSetPixelFormat(8, 8, false, true, 7, 7, 3, 0, 3, 6);

		//We're connected!
		connectionState = CONNECTED;

		//Set the menu
		menu.getItemWithID(kID_Connect).disable();
		menu.getItemWithID(kID_Disconnect).enable();
		menu.getItemWithID(kID_Clipboard).enable();
		menu.getItemWithID(kID_Stats).enable();

		//setup the viewing area
		clipRegion = getBounds();

		//next update should be a full update.
		incrementalUpdate=false;

		//hide the connection progress dialog, as we are now
		//already connected and ready to begin.
		pbBar.advanceState(); //10
		connectProgress.hide();
		connectProgress = null;
	}
	
	/**
	 * Set the window to full screen or not.
	 * Also update the clipRegion as necessary.
	 * 
	 * @see danger.ui.ScreenWindow#setFullScreen(boolean)
	 */
	public void setFullScreen(boolean fullScreen) {
		super.setFullScreen(fullScreen);
		
		//init of the Screen Window calls this function.  This prevents this
		//section of code from being called before clipRegion and rfb are created.
		if (clipRegion != null && rfb != null) {
			clipRegion.setSize(getBounds().getWidth(), getBounds().getHeight());
		
			//make sure that the current clipRegion is within the frameBuffer bounds.
			if (clipRegion.top < 0) clipRegion.setPosition(clipRegion.left, 0);
			if (clipRegion.left < 0) clipRegion.setPosition(0, clipRegion.top);
			if (clipRegion.top + clipRegion.getHeight() > rfb.framebufferHeight)
				clipRegion.setPosition(clipRegion.left, rfb.framebufferHeight - clipRegion.getHeight());
			if (clipRegion.left + clipRegion.getWidth() > rfb.framebufferWidth)
				clipRegion.setPosition(rfb.framebufferWidth - clipRegion.getWidth(),  clipRegion.top);	
		}

        //if we are connected, send an update request.
        sendRequestForCurrentClipRegion();
		invalidate();
	}

    /**
	 * Helper function: sends a frameBufferUpdate request.
	 *
	 */
    private void sendRequestForCurrentClipRegion() {
        if (connectionState == CONNECTED) {
            try {
			    rfb.writeFramebufferUpdateRequest(clipRegion.left, clipRegion.top,
					clipRegion.getWidth(), clipRegion.getHeight(), false);
		    } catch (Exception e) {}
        }
    }

	/**
	 * Move the clipRegion as necessary
	 * 
	 * @param dx  amount of movement in the X direction
	 * @param dy  amount of movement in the Y direction
	 */
	void translate(int dx, int dy) {
		if (connectionState != CONNECTED) return;

		int realdx = dx;
		int realdy = dy;
		Rect oldRect = new Rect(clipRegion);

		//move the region
		clipRegion.translate(realdx, realdy);
		
		//Make sure the region is still within bounds.
		if (clipRegion.top < 0) clipRegion.setPosition(clipRegion.left, 0);
		if (clipRegion.left < 0) clipRegion.setPosition(0, clipRegion.top);
		if (clipRegion.top + clipRegion.getHeight() > rfb.framebufferHeight)
			clipRegion.setPosition(clipRegion.left, rfb.framebufferHeight - clipRegion.getHeight());
		if (clipRegion.left + clipRegion.getWidth() > rfb.framebufferWidth)
			clipRegion.setPosition(rfb.framebufferWidth - clipRegion.getWidth(),  clipRegion.top);

		//if the clip region has moved, request an update.
		if (!clipRegion.equals(oldRect))
            sendRequestForCurrentClipRegion();
	}

	/**
	 * Disconnect from the VNC server.
	 */
	private void vncDisconnect() {
		connectionState = DISCONNECTING;
		setTitle(App_Name);
		setSubTitle(null);
		if (rfb != null) rfb.close();
		connectionState = DISCONNECTED;

		//if connectProgress is not null, then its still up.
		//hide it and set it to null
		if (connectProgress != null) {
			connectProgress.hide();
			connectProgress = null;
		}

		//Set the menu appropriately
		menu.getItemWithID(kID_Connect).enable();
		menu.getItemWithID(kID_Disconnect).disable();
		menu.getItemWithID(kID_Clipboard).disable();
		menu.getItemWithID(kID_Stats).disable();

        //redraw the screen, so that the text "Disconnected" is displayed
        invalidate();

        //update the splash screen.
        myApp.updatePreviewScreen();
	}

	/**
	 * Handles an update request.  Called repeatedly by the
	 * main thread.
	 * 
	 * @throws IOException
	 */
	private void vncUpdate() throws IOException {

			rfb.writeFramebufferUpdateRequest(clipRegion.left, clipRegion.top,
							  clipRegion.getWidth(), clipRegion.getHeight(), incrementalUpdate);

			incrementalUpdate = true;

			int msgType = rfb.readServerMessageType();

			switch (msgType) {
				case rfbProto.FramebufferUpdate:

					rfb.readFramebufferUpdate();
					
					for (int i = 0; i < rfb.updateNRects; i++) {
						
						rfb.readFramebufferUpdateRectHdr();

						if (rfb.updateRectEncoding == rfbProto.EncodingLastRect)
							break;

						switch (rfb.updateRectEncoding) {


							case rfbProto.EncodingRaw:
								RawRect.doEncoding();
								break;

							case rfbProto.EncodingCopyRect:
								rfb.readCopyRect();
	 							CopyRect.doEncoding();
								break;

					  		case rfbProto.EncodingRRE:
					  			RRERect.doEncoding();
					  			break;

							case rfbProto.EncodingCoRRE:
					  			CoRRERect.doEncoding();
					  			break;

							case rfbProto.EncodingHextile:
								HextileRect.doEncoding();
								break;

							case rfbProto.EncodingZlib:
								ZlibRect.doEncoding();
								break;

							case rfbProto.EncodingTight:
								TightRect.doEncoding();
								break;

							case rfbProto.EncodingZlibHex:
								System.out.println("Unsupported: ZlibHex!");
								break;

							case rfbProto.EncodingZRLE:
								System.out.println("Unsupported: ZRLE!");
								break;

							case rfbProto.EncodingCache:
								System.out.println("Unsupported: Cache!");
								break;

							case rfbProto.EncodingXOR_Zlib:
								System.out.println("Unsupported: XOR_Zlib!");
								break;

							case rfbProto.EncodingXORMonoColor_Zlib:
								System.out.println("Unsupported: XOR Mono Color!");
								break;

							case rfbProto.EncodingXORMultiColor_Zlib:
								System.out.println("Unsupported: XOR Multi Color!");
								break;

							case rfbProto.EncodingSolidColor:
								SolidRect.doEncoding();
								break;

							case rfbProto.EncodingCacheZip:
								System.out.println("Unsupported: Cache Zip!");
								break;

							case rfbProto.EncodingSolMonoZip:
								SolMonoZipRect.doEncoding();
								break;
								
							default:
								String hex = Integer.toString(rfb.updateRectEncoding, 16 /* radix */ );
								System.out.println("Unknown rect encoding:" + hex);
						}

					}

					invalidate();
					break;
					
      			case rfbProto.ServerCutText:
					String s = rfb.readServerCutText();
					Pasteboard.setString(s);
					break;

				case rfbProto.ResizeFrameBuffer:
					System.out.println("Unsupported: ResizeFrameBuffer!");
					break;

				case rfbProto.PalmVNCReSizeFrameBuffer:
					System.out.println("Unsupported: PalmVNCResizeFrameBuffer!");
					break;

			    default:
			   		System.out.println("Unknown message type: " + msgType);
			}

            //update the splash screen.
            myApp.updatePreviewScreen();
	}


    /**
     *
     * @return the current connection state.
     */
    public byte getConnectionState() {
        return connectionState;
    }

    /**
     *
     * @return the rfbProto object
     */
    public rfbProto getRFB() {
        return rfb;
    }

    public Bitmap getThumbnail() {

        Bitmap b = new Bitmap(90,60);

        //screenBitmap.copyBitsTo(b, Rect.newXYWH(0,0,90,60), clipRegion);

        //for some reason, copyBitsTo doesn't seem to work.  Hence this code:
        Bitmap tmp = new Bitmap(clipRegion.getWidth(), clipRegion.getHeight());
        screenBitmap.copyBitsTo(tmp, Rect.newXYWH(0,0,clipRegion.getWidth(), clipRegion.getHeight()), clipRegion);
        b.copyScaled(tmp);

        return b;
    }
} //end of VNCWindow class
