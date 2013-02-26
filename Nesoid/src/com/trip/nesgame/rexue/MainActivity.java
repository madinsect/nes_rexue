package com.trip.nesgame.rexue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import java.io.File;
import java.io.IOException;

import com.trip.nesgame.rexue.R;

public class MainActivity extends Activity {

	private static final Uri HELP_URI = Uri.parse(
			"file:///android_asset/faq.html");

	private static final int DIALOG_SHORTCUT = 1;

	private static Intent emulatorIntent;
	private SharedPreferences settings;
	private boolean creatingShortcut;

	private Rect[] btns = new Rect[6];
	private String[] files= {"wuyu.nes","zuqiu3.nes","bingqiu.nes","gedou.nes","jinxingqu.nes","lanqiu.nes"};
	private float rate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences("MainActivity", MODE_PRIVATE);

		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setTitle(R.string.title_select_rom);

		init_res(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置全屏
		
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start);
		creatingShortcut = getIntent().getAction().equals(
				Intent.ACTION_CREATE_SHORTCUT);
	}
	private void init_res(Context context) {
		Resources r = context.getResources();
		rate = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth() / 480f;
		
		int i = 0, x1 ,y1, x2, y2;
		while(i<6){
			x1 = (int) ((25 + (i%2*(14 + 210))) *rate);
			y1 = (int) ((87 + (Math.floor(i/2)*(20 + 190))) *rate);
			x2 = (int) ((x1 + 210)*rate); 
			y2 = (int) ((y1 + 190)*rate);
			Log.d("rexue",x1+";"+y1+";"+x2+";"+y2);
			btns[i] = new Rect(x1, y1, x2, y2);
			i++;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int i = 0;
		int x = (int) event.getX(); // 获得点击处的X坐标
		int y = (int) event.getY(); // 获得点击处的Y坐标
		String file_name = null;
		if (action == MotionEvent.ACTION_UP) {
			while(i<6){
				if(btns[i].contains(x, y)){
					file_name = files[i];
					break;
				}
				i++;
			}
		}
		Log.d("rexue","file_name:"+ file_name);
		if(file_name!=null){
			Uri rom_uri = Uri.parse(FileHandler.copyFile2Rom(this, file_name));
			onFileSelected(rom_uri);
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		if (!creatingShortcut)
			getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search_roms:
			startActivity(EmulatorSettings.getSearchROMIntent());
			return true;

		case R.id.menu_help:
			startActivity(new Intent(this, HelpActivity.class).
					setData(HELP_URI));
			return true;

		case R.id.menu_settings:
			startActivity(new Intent(this, EmulatorSettings.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SHORTCUT:
			return createShortcutNameDialog();
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_SHORTCUT:
			String name = emulatorIntent.getData().getPath();
			name = new File(name).getName();
			int dot = name.lastIndexOf('.');
			if (dot > 0)
				name = name.substring(0, dot);

			EditText v = ((EditText) dialog.findViewById(R.id.name));
			v.setText(name);
			v.selectAll();
			break;
		}
	}

	protected String getInitialPath() {
		return settings.getString("lastGame", null);
	}

	protected String[] getFileFilter() {
		return getResources().getStringArray(R.array.file_chooser_filters);
	}

	protected void onFileSelected(Uri uri) {
		// remember the last file
		settings.edit().
				putString("lastGame", uri.getPath()).commit();

		Intent intent = new Intent(Intent.ACTION_VIEW, uri,
				this, EmulatorActivity.class);
		if (!creatingShortcut)
			startActivity(intent);
		else {
			emulatorIntent = intent;
			showDialog(DIALOG_SHORTCUT);
		}
	}

	private Dialog createShortcutNameDialog() {
		DialogInterface.OnClickListener l =
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final Dialog d = (Dialog) dialog;
					String name = ((EditText) d.findViewById(R.id.name)).
							getText().toString();

					Intent intent = new Intent();
					intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
							emulatorIntent);
					intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
					Parcelable icon = Intent.ShortcutIconResource.fromContext(
							MainActivity.this, R.drawable.app_icon);
					intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

					setResult(RESULT_OK, intent);
					finish();
				}
			};

		return new AlertDialog.Builder(this).
				setTitle(R.string.shortcut_name).
				setView(getLayoutInflater().inflate(R.layout.shortcut, null)).
				setPositiveButton(android.R.string.ok, l).
				setNegativeButton(android.R.string.cancel, null).
				create();
	}
}
