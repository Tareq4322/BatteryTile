package com.cominatyou.batterytile.standalone;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class VolumeTileService extends TileService {

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        if (tile == null) return;
        
        tile.setLabel("Volume");
        tile.setState(Tile.STATE_ACTIVE);
        
        // Correct Icon
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_volume));
        
        tile.updateTile();
    }

    @Override
    public void onClick() {
        // 1. Trigger Volume Slider
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
        
        // 2. The "Ghost Activity" Trick (Restored)
        // This is required to collapse the panel on Android 12+
        Intent intent = new Intent(this, DummyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // This method forces the shade to close because it thinks it's launching an app
        startActivityAndCollapse(intent);
    }
}
