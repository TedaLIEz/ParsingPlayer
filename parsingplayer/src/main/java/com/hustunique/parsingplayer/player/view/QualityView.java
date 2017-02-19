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

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hustunique.parsingplayer.util.LogUtil;
import com.hustunique.parsingplayer.R;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;

/**
 * Created by JianGuo on 2/14/17.
 * View for video quality chosen
 */

public class QualityView extends LinearLayout {
    private static final String TAG = "QualityChooseView";
    public QualityView(Context context) {
        this(context, null);
    }

    public QualityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QualityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public QualityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        setBackgroundColor(getResources().getColor(R.color.panel_background_dark));
    }


    public void attachViewWithInfo(final ParsingVideoView view, VideoInfo videoInfo) {
        LogUtil.d(TAG, "hds size " + videoInfo.toString());
        for (final int q : videoInfo.getStreamMap().keySet()) {
            TextView tv = new TextView(getContext());
            LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = 16;
            lp.topMargin = 16;
            lp.leftMargin = 16;
            lp.rightMargin = 16;
            tv.setLayoutParams(lp);
            tv.setPadding(16, 16, 16, 16);
            tv.setBackground(getResources().getDrawable(R.drawable.quality_text_bgd));
            tv.setTextSize(14);
            tv.setText(getString(q));
            tv.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            tv.setTextColor(getResources().getColor(R.color.dim_foreground_dark));
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.setQuality(q);
                }
            });
            addView(tv);
        }
    }

    private String getString(int q) {
        if (q == VideoInfo.HD_HIGH) {
            return getResources().getString(R.string.hd_high);
        } else if (q == VideoInfo.HD_LOW) {
            return getResources().getString(R.string.hd_low);
        } else if (q == VideoInfo.HD_MEDIUM) {
            return getResources().getString(R.string.hd_medium);
        } else if (q == VideoInfo.HD_STANDARD) {
            return getResources().getString(R.string.hd_standard);
        }
        return null;
    }

    public void show() {
        LogUtil.d(TAG, "width: " + getMeasuredWidth());
        animate().translationXBy(-getMeasuredWidth()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                bringToFront();
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

    }

    public void hide() {
        animate().translationXBy(getMeasuredWidth()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

    }

}
