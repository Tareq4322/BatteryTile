package com.cominatyou.batterytile.standalone;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

public class TileAccessibilityService extends AccessibilityService {

    // We hold a reference to the running service here.
    private static TileAccessibilityService instance;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        // When the system enables the service, we capture it.
        instance = this;
    }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        // If the system kills it, we release the reference to avoid leaks.
        instance = null;
        return super.onUnbind(intent);
    }

    // This is the new, crash-proof way to lock
    public static boolean requestLock() {
        // If instance is null, the service is disabled or killed.
        if (instance != null) {
            return instance.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
        }
        return false; // Failed to lock
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not used
    }

    @Override
    public void onInterrupt() {
        // Not used
    }

    // Helper to check if the user has actually enabled this service in Settings
    public static boolean isServiceEnabled(Context context) {
        String expectedServiceName = context.getPackageName() + "/" + TileAccessibilityService.class.getCanonicalName();
        String enabledServices = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        
        if (TextUtils.isEmpty(enabledServices)) {
            return false;
        }
        
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        splitter.setString(enabledServices);
        
        while (splitter.hasNext()) {
            String componentName = splitter.next();
            if (componentName.equalsIgnoreCase(expectedServiceName)) {
                return true;
            }
        }
        return false;
    }
}
