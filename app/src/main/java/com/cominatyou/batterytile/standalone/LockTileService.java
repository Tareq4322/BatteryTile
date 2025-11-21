package com.cominatyou.batterytile.standalone;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

public class LockTileService extends TileService {

    private long lastClickTime = 0;
    private static final long CLICK_COOLDOWN = 500; 

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        if (tile == null) return;

        tile.setLabel("Lock Screen");
        
        // VISUALS: Always show as INACTIVE (Grey)
        // This makes it look like a clickable button rather than a toggle switch
        tile.setState(Tile.STATE_INACTIVE);
        
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_lock));
        tile.updateTile();
    }

    @Override
    public void onClick() {
        // Spam filter: prevent accidental double-taps
        if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_COOLDOWN) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        // Check permission and lock
        if (TileAccessibilityService.isServiceEnabled(this)) {
            Intent intent = new Intent(this, TileAccessibilityService.class);
            intent.setAction(TileAccessibilityService.ACTION_LOCK_SCREEN);
            startService(intent);
        } else {
            // Permission missing? Send to settings
            Toast.makeText(this, "Please enable 'Tile Toolkit' in Accessibility Settings", Toast.LENGTH_LONG).show();
            
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            try {
                if (Build.VERSION.SDK_INT >= 34) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 
                        0, 
                        intent, 
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    startActivityAndCollapse(pendingIntent);
                } else {
                    startActivityAndCollapse(intent);
                }
            } catch (Exception e) {
                Log.e("LockTileService", "Failed to launch settings", e);
                Toast.makeText(this, "Could not open settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
