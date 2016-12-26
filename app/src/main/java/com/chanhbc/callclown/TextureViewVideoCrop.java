package com.chanhbc.callclown;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.TextureView;

public class TextureViewVideoCrop extends TextureView{
    private MediaPlayer mediaPlayer;

    public TextureViewVideoCrop(Context context) {
        super(context);
    }

    public TextureViewVideoCrop(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextureViewVideoCrop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
