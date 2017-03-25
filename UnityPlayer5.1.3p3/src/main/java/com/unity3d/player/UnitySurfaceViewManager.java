package com.unity3d.player;

import android.view.*;
import android.content.*;
import java.util.*;

final class UnitySurfaceViewManager
{
    public static UnitySurfaceViewManager Instance;
    private final ViewGroup mRootView;
    private Set mViews;
    private View d;
    private View e;
    
    UnitySurfaceViewManager(final ViewGroup b) {
        this.mViews = new HashSet();
        this.mRootView = b;
        UnitySurfaceViewManager.Instance = this;
    }
    
    public final Context getContext() {
        return this.mRootView.getContext();
    }
    
    public final void a(final View view) {
        this.mViews.add(view);
        if (this.d != null) {
            this.e(view);
        }
    }
    
    public final void removeCameraSurface(final View view) {
        this.mViews.remove(view);
        if (this.d != null) {
            this.f(view);
        }
    }
    
    public final void c(final View d) {
        if (this.d != d) {
            this.d = d;
            this.mRootView.addView(d);
            final Iterator<View> iterator = this.mViews.iterator();
            while (iterator.hasNext()) {
                this.e(iterator.next());
            }
            if (this.e != null) {
                this.e.setVisibility(4);
            }
        }
    }
    
    public final void d(final View view) {
        if (this.d == view) {
            final Iterator<View> iterator = this.mViews.iterator();
            while (iterator.hasNext()) {
                this.f(iterator.next());
            }
            this.mRootView.removeView(view);
            this.d = null;
            if (this.e != null) {
                this.e.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void e(final View view) {
        this.mRootView.addView(view, this.mRootView.getChildCount());
    }
    
    private void f(final View view) {
        this.mRootView.removeView(view);
        this.mRootView.requestLayout();
    }
}
