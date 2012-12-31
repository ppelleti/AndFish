/*******************************************************************************
 * Copyright (c) 2009 Ferenc Hechler - ferenc_hechler@users.sourceforge.net
 * 
 * This file is part of AndroFish
 *
 * AndroFish is free software;
 * you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 * 
 * AndroFish is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the AndroFish;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *  
 *******************************************************************************/
package de.hechler.andfish;

import com.andoop.highscore.api.HighScoreManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class AndroFishMainActivity extends Activity {

	public static final String INTENT_EXTRA_LEVEL_NAME = "de.hechler.androfish.extra.LEVEL";
	public static final String INTENT_EXTRA_PLAY_MUSIC = "de.hechler.androfish.extra.PLAY_MUSIC";
	public static final String INTENT_EXTRA_PLAY_SOUND = "de.hechler.androfish.extra.PLAY_SOUND";
	public static final String INTENT_EXTRA_ONLINE_HIGHSCORE_INSTALLED = "de.hechler.androfish.extra.ONLINE_HIGHSCORE_INSTALLED";

	public final static boolean DEFAULT_PLAY_MUSIC_VALUE = true;
	public final static boolean DEFAULT_PLAY_SOUND_VALUE = true;
	public final static boolean DEFAULT_ONLINE_HIGHSCORE_INSTALLED_VALUE = false;

	protected static final int START_LEVELSET_SELECTION = 1;
	protected static final int HISC_LEVELSET_SELECTION = 2;
	
	
	CheckBox cbPlayMusic;
	CheckBox cbPlaySound;
	Button   btStart;
	Button   btHighscore;
	Button   btOnlineHighscore;
	Button   btHelp;
	Button   btExit;
	
	private boolean  mOnlineHighscoreInstalled;
	
	SimplePersistence persist; 
	public final static String PREFS_NAME = "AndroFishPrefs";
	private final static String PREFS_PLAY_MUSIC = "play_music";
	private final static String PREFS_PLAY_SOUND = "play_sound";
	
    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// create view from xml
        setContentView(R.layout.main);

        // checkboxes
        cbPlayMusic = (CheckBox) findViewById(R.id.cbPlayMusic);
        cbPlaySound = (CheckBox) findViewById(R.id.cbPlaySound);
        
        // find buttons in view
        btStart = ((Button) findViewById(R.id.btStart));
        btHighscore = ((Button) findViewById(R.id.btHighscore));
        btOnlineHighscore = ((Button) findViewById(R.id.btOnlineHighscore));
        btHelp = ((Button) findViewById(R.id.btHelp));
        btExit = ((Button) findViewById(R.id.btExit));

        checkOnlineHighscoreInstalled();
        if (mOnlineHighscoreInstalled) {
        	btHighscore.setVisibility(View.INVISIBLE);
        	btOnlineHighscore.setText("Online Highscore");
        	LinearLayout layout = (LinearLayout)btHighscore.getParent();
        	layout.removeView(btHighscore);
        }
        else {
        	btHighscore.setVisibility(View.VISIBLE);
        	btOnlineHighscore.setText("Install Online Highscore");
        }
        
        // load persisted values
        persist = new SimplePersistence(this, PREFS_NAME);
    	boolean playMusic = persist.getBoolean(PREFS_PLAY_MUSIC, DEFAULT_PLAY_MUSIC_VALUE);
    	boolean playSound = persist.getBoolean(PREFS_PLAY_SOUND, DEFAULT_PLAY_SOUND_VALUE);
    	cbPlayMusic.setChecked(playMusic);
    	cbPlaySound.setChecked(playSound);
        
        // set actions for buttons
        btStart.setOnClickListener(StartListener);
        btHighscore.setOnClickListener(HighscoreListener);
        btOnlineHighscore.setOnClickListener(OnlineHighscoreListener);
        btHelp.setOnClickListener(HelpListener);
        btExit.setOnClickListener(ExitListener);
    }


    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    	checkOnlineHighscoreInstalled();
    }
    
    private void checkOnlineHighscoreInstalled() {
    	PackageManager pm = getPackageManager();
    	try {
			pm.getApplicationInfo("com.andoop.highscore", 0);
			mOnlineHighscoreInstalled = true;
		} catch (NameNotFoundException e) {
			mOnlineHighscoreInstalled = false;
		}
	}


	@Override
    protected void onStop() {
    	super.onStop();
    	persistValues();
    }

	private void persistValues() {
    	boolean playMusic = cbPlayMusic.isChecked();
    	boolean playSound = cbPlaySound.isChecked();
    	persist.putBoolean(PREFS_PLAY_MUSIC, playMusic);
    	persist.putBoolean(PREFS_PLAY_SOUND, playSound);
    	persist.commit();
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// do updates
    }
    

    
//    OnClickListener StartListener = new OnClickListener() {
//        public void onClick(View v) {
//	    	Intent intent = new Intent(AndroMazeActivity.this, AndroMazeLevelActivity.class);
//        	startActivity(intent);
//        }
//    };
    OnClickListener StartListener = new OnClickListener() {
        public void onClick(View v) {
            showDialog(START_LEVELSET_SELECTION);
        }
    };
    OnClickListener OnlineHighscoreListener = new OnClickListener() {
        public void onClick(View v) {
        	HighScoreManager _scoreManager = new HighScoreManager(AndroFishMainActivity.this);
        	_scoreManager.showBoard();
        }
    };
	
    OnClickListener HighscoreListener = new OnClickListener() {
        public void onClick(View v) {
            showDialog(HISC_LEVELSET_SELECTION);
	    	//Intent intent = new Intent(AndroFishMainActivity.this, AndroFishHighscoreActivity.class);
        	//startActivity(intent);
        }
    };
	
    OnClickListener HelpListener = new OnClickListener() {
        public void onClick(View v) {
	    	Intent intent = new Intent(AndroFishMainActivity.this, AndroFishHelpActivity.class);
        	startActivity(intent);
        }
    };
    /**
     * A call-back for when the user presses the back button.
     */
    OnClickListener ExitListener = new OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };

	
	final static String[] levels = {"easy","medium","hard"};
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case START_LEVELSET_SELECTION:
            return new AlertDialog.Builder(this)
                .setTitle("Level selection")
                .setItems(levels, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /* User clicked so do some stuff */
                        String levelName = levels[which].toLowerCase();
            	    	Intent intent = new Intent(AndroFishMainActivity.this, AndroidFishEatingFish.class);
            	    	intent.putExtra(INTENT_EXTRA_LEVEL_NAME, levelName);
            	    	intent.putExtra(INTENT_EXTRA_PLAY_MUSIC, cbPlayMusic.isChecked());
            	    	intent.putExtra(INTENT_EXTRA_PLAY_SOUND, cbPlaySound.isChecked());
            	    	intent.putExtra(INTENT_EXTRA_ONLINE_HIGHSCORE_INSTALLED, mOnlineHighscoreInstalled);
            	    	startActivity(intent);
                    }
                })
                .create();
	    case HISC_LEVELSET_SELECTION:
	        return new AlertDialog.Builder(this)
	            .setTitle("Level selection")
	            .setItems(levels, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    /* User clicked so do some stuff */
	                    String levelName = levels[which].toLowerCase();
	        	    	Intent intent = new Intent(AndroFishMainActivity.this, AndroFishHighscoreActivity.class);
	        	    	intent.putExtra(INTENT_EXTRA_LEVEL_NAME, levelName);
	                	startActivity(intent);
	                }
	            })
	            .create();
        }
	    return null;
    }
    
    
}
