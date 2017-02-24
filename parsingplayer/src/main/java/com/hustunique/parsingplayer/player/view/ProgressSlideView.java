/*
 * Copyright (c) 2017 UniqueStudio
 *
 *
 * This file is part of ParsingPlayer.
 *
 * ParsingPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ParsingPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.hustunique.parsingplayer.player.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hustunique.parsingplayer.R;

/**
 * Created by JianGuo on 2/20/17.
 * Wrapper layout class of {@link VerticalProgressBar}
 */

public class ProgressSlideView extends LinearLayout {
    private static final String TAG = "ProgressSlideView";
    private VerticalProgressBar mVerticalProgressBar;
    private ImageView mImageView;
    public ProgressSlideView(Context context) {
        this(context, null);
    }

    public ProgressSlideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressSlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ProgressSlideView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.slide_panel, this);
        mVerticalProgressBar = (VerticalProgressBar) findViewById(R.id.vertical_progressbar);
        mImageView = (ImageView) findViewById(R.id.iv_info);
        // viewgroup will not draw on its own by default
        setWillNotDraw(false);
    }


    public void setProgress(int progress) {
        mVerticalProgressBar.setProgress(progress);
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        mVerticalProgressBar.setMax(maxProgress);

    }

    public void setInfoDrawable(@DrawableRes int drawable) {
        setInfoDrawable(getResources().getDrawable(drawable));
    }

    public void setInfoDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }



    public static ProgressSlideView createView(Context context, ViewGroup.LayoutParams lp, @DrawableRes int drawable) {
        ProgressSlideView progressSlideView = new ProgressSlideView(context);
        progressSlideView.setLayoutParams(lp);
        progressSlideView.setInfoDrawable(context.getResources().getDrawable(drawable));
        return progressSlideView;

    }
}
