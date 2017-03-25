package com.unity3d.player;

import android.content.*;
import android.location.*;
import java.util.*;
import android.os.*;
import android.hardware.*;

final class UnityLocationManager implements LocationListener
{
    private final Context mContext;
    private final UnityPlayer mUnityPlayer;
    private Location mLocation;
    private float d;
    private boolean e;
    private int f;
    private boolean g;
    private int mLocationStatus;
    
    protected UnityLocationManager(final Context context, final UnityPlayer unityPlayer) {
        this.d = 0.0f;
        this.e = false;
        this.f = 0;
        this.g = false;
        this.mLocationStatus = 0;
        this.mContext = context;
        this.mUnityPlayer = unityPlayer;
    }
    
    public final boolean a() {
        return !((LocationManager)this.mContext.getSystemService(Context.LOCATION_SERVICE)).getProviders(new Criteria(), true).isEmpty();
    }
    
    public final void a(final float d) {
        this.d = d;
    }
    
    public final void b(final float n) {
        if (n < 100.0f) {
            this.f = 1;
            return;
        }
        if (n < 500.0f) {
            this.f = 1;
            return;
        }
        this.f = 2;
    }
    
    public final void b() {
        this.g = false;
        if (this.e) {
            UnityLog.Log(5, "Location_StartUpdatingLocation already started!");
            return;
        }
        if (!this.a()) {
            this.setLocationStatus(3);
            return;
        }
        final LocationManager locationManager = (LocationManager)this.mContext.getSystemService("location");
        this.setLocationStatus(1);
        final List<String> providers;
        if ((providers = locationManager.getProviders(true)).isEmpty()) {
            this.setLocationStatus(3);
            return;
        }
        LocationProvider locationProvider = null;
        if (this.f == 2) {
            final Iterator<String> iterator = providers.iterator();
            while (iterator.hasNext()) {
                final LocationProvider provider;
                if ((provider = locationManager.getProvider((String)iterator.next())).getAccuracy() == 2) {
                    locationProvider = provider;
                    break;
                }
            }
        }
        for (final String s : providers) {
            if (locationProvider == null || locationManager.getProvider(s).getAccuracy() != 1) {
                this.changeLocation(locationManager.getLastKnownLocation(s));
                locationManager.requestLocationUpdates(s, 0L, this.d, (LocationListener)this, this.mContext.getMainLooper());
                this.e = true;
            }
        }
    }
    
    public final void stopUpdating() {
        ((LocationManager)this.mContext.getSystemService(Context.LOCATION_SERVICE)).removeUpdates((LocationListener)this);
        this.e = false;
        this.mLocation = null;
        this.setLocationStatus(0);
    }
    
    public final void d() {
        if (this.mLocationStatus == 1 || this.mLocationStatus == 2) {
            this.g = true;
            this.stopUpdating();
        }
    }
    
    public final void e() {
        if (this.g) {
            this.b();
        }
    }
    
    public final void onLocationChanged(final Location location) {
        this.setLocationStatus(2);
        this.changeLocation(location);
    }
    
    public final void onStatusChanged(final String s, final int n, final Bundle bundle) {
    }
    
    public final void onProviderEnabled(final String s) {
    }
    
    public final void onProviderDisabled(final String s) {
        this.mLocation = null;
    }
    
    private void changeLocation(final Location c) {
        if (c == null) {
            return;
        }
        if (a(c, this.mLocation)) {
            this.mLocation = c;
            this.mUnityPlayer.nativeSetLocation((float)c.getLatitude(), (float)c.getLongitude(), (float)c.getAltitude(), c.getAccuracy(), c.getTime() / 1000.0, new GeomagneticField((float)this.mLocation.getLatitude(), (float)this.mLocation.getLongitude(), (float)this.mLocation.getAltitude(), this.mLocation.getTime()).getDeclination());
        }
    }
    
    private static boolean a(final Location location, final Location location2) {
        if (location2 == null) {
            return true;
        }
        final long n;
        final boolean b = (n = location.getTime() - location2.getTime()) > 120000L;
        final boolean b2 = n < -120000L;
        final boolean b3 = n > 0L;
        if (b) {
            return true;
        }
        if (b2) {
            return false;
        }
        final int n2;
        final boolean b4 = (n2 = (int)(location.getAccuracy() - location2.getAccuracy())) > 0;
        final boolean b5 = n2 < 0;
        final boolean b6 = n2 > 200 | location.getAccuracy() == 0.0f;
        final boolean a = a(location.getProvider(), location2.getProvider());
        return b5 || (b3 && !b4) || (b3 && !b6 && a);
    }
    
    private static boolean a(final String s, final String s2) {
        if (s == null) {
            return s2 == null;
        }
        return s.equals(s2);
    }
    
    private void setLocationStatus(final int locationStatus) {
        this.mLocationStatus = locationStatus;
        this.mUnityPlayer.nativeSetLocationStatus(locationStatus);
    }
}
