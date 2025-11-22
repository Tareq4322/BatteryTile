package com.cominatyou.batterytile.standalone;

import android.app.PendingIntent; // NEW IMPORT
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build; // NEW IMPORT
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class DataSimTileService extends TileService {

    @Override
    public void onStartListening() {
        updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        tile.setLabel("Data SIM");
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_sim_dashboard));

        // Use the global setting value to READ the current data SIM
        try {
            int currentSub = Settings.Global.getInt(getContentResolver(), "multi_sim_data_call", 1);
            
            if (currentSub == 1) {
                tile.setSubtitle("SIM 1 Active");
                tile.setState(Tile.STATE_ACTIVE); 
            } else if (currentSub == 2) {
                tile.setSubtitle("SIM 2 Active");
                tile.setState(Tile.STATE_ACTIVE);
            } else {
                tile.setSubtitle("No SIM Data");
                tile.setState(Tile.STATE_INACTIVE);
            }
        } catch (Exception e) {
            tile.setSubtitle("Tap to Manage");
            tile.setState(Tile.STATE_INACTIVE);
        }

        tile.updateTile();
    }

    @Override
    public void onClick() {
        // Use the ACTION_WIRELESS_SETTINGS Intent, as it is more robust across devices
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS); 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        try {
            // Check for modern Android APIs (API 34 / Android 14)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 
                    0, 
                    intent, 
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );
                // Launch using the PendingIntent method (required for stability on 14+)
                startActivityAndCollapse(pendingIntent); 
            } else {
                // For older Android versions, use the direct Intent method
                startActivityAndCollapse(intent);
            }
        } catch (Exception e) {
            // If both primary and fallback methods fail (highly unlikely now), show error
            Toast.makeText(this, "Error: Could not launch network settings.", Toast.LENGTH_LONG).show();
        }
    }
}
