/*
 *
 * Copyright (c) 2017 UniqueStudio
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

import android.view.View;

import java.lang.ref.WeakReference;


public final class MeasureHelper {
    private static final String TAG = "MeasureHelper";
    private WeakReference<View> mWeakView;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mVideoRotationDegree;

    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private float mCurrentAspectRatio;
    private int mCurrentAspectRatioMode = IRenderView.AR_ASPECT_FIT_PARENT;

    public MeasureHelper(View view) {
        mWeakView = new WeakReference<View>(view);
    }

    public View getView() {
        if (mWeakView == null)
            return null;
        return mWeakView.get();
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }


    private float getDisplayRatio() {
        float displayAspectRatio;
        switch (mCurrentAspectRatioMode) {
            case IRenderView.AR_16_9_FIT_PARENT:
                displayAspectRatio = 16.0f / 9.0f;
                if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                    displayAspectRatio = 1.0f / displayAspectRatio;
                break;
            case IRenderView.AR_4_3_FIT_PARENT:
                displayAspectRatio = 4.0f / 3.0f;
                if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                    displayAspectRatio = 1.0f / displayAspectRatio;
                break;
            case IRenderView.AR_ASPECT_EXACTLY:
                displayAspectRatio = mCurrentAspectRatio;
                if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                    displayAspectRatio = 1.0f / displayAspectRatio;
                break;
            case IRenderView.AR_ASPECT_FIT_PARENT:
            case IRenderView.AR_ASPECT_FILL_PARENT:
            case IRenderView.AR_ASPECT_WRAP_CONTENT:
            default:
                displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
                if (mVideoSarNum > 0 && mVideoSarDen > 0)
                    displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
                break;
        }
        return displayAspectRatio;
    }

    /**
     * Must be called by View.onMeasure(int, int)
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            int tempSpec = widthMeasureSpec;
            widthMeasureSpec  = heightMeasureSpec;
            heightMeasureSpec = tempSpec;
        }
        float displayAspectRatio = getDisplayRatio();
        int width = mVideoWidth == 0 ? 0 : View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = mVideoHeight == 0 ? 0 : View.getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mCurrentAspectRatioMode == IRenderView.AR_MATCH_PARENT) {
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;

                switch (mCurrentAspectRatioMode) {
                    case IRenderView.AR_ASPECT_EXACTLY:
                        width = (int) (mMeasuredWidth * displayAspectRatio);
                        height = (int) (mMeasuredHeight * displayAspectRatio);
                        break;
                    case IRenderView.AR_ASPECT_FIT_PARENT:
                    case IRenderView.AR_16_9_FIT_PARENT:
                    case IRenderView.AR_4_3_FIT_PARENT:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                    case IRenderView.AR_ASPECT_FILL_PARENT:
                        if (shouldBeWider) {
                            // not high enough, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        } else {
                            // not wide enough, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        }
                        break;
                    case IRenderView.AR_ASPECT_WRAP_CONTENT:
                    default:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                if (mCurrentAspectRatioMode == IRenderView.AR_ASPECT_EXACTLY) {
                    width = (int) (mMeasuredWidth * displayAspectRatio);
                    height = (int) (mMeasuredHeight * displayAspectRatio);
                }
                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        }

        mMeasuredWidth = width;
        mMeasuredHeight = height;
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    public void setAspectRatioMode(int aspectRatioMode) {
        mCurrentAspectRatioMode = aspectRatioMode;
    }

    /**
     * Change ratio of video size, this will only apply after the first measurement
     * @param aspectRatio the ratio
     */
    public void setAspectRatio(float aspectRatio) {
        if (mMeasuredWidth == 0 || mMeasuredHeight == 0)
            throw new IllegalStateException("You must set ratio after first measurement");
        mCurrentAspectRatio = aspectRatio;
        setAspectRatioMode(IRenderView.AR_ASPECT_EXACTLY);
    }

}
