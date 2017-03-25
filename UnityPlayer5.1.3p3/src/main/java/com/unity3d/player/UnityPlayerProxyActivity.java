package com.unity3d.player;

import android.app.*;
import android.os.*;
import android.content.*;

public class UnityPlayerProxyActivity extends Activity
{
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Intent intent;
        (intent = new Intent((Context)this, (Class)UnityPlayerActivity.class)).addFlags(65536);
        final Bundle extras;
        if ((extras = this.getIntent().getExtras()) != null) {
            intent.putExtras(extras);
        }
        this.startActivity(intent);
    }
}
