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

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = "SimpleGestureListener";
    private Listener mListener;

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mListener == null)
            return true;

        if (distanceX == 0){
            mListener.onScrollVertical(e1.getX(),distanceY);
        }

        if (distanceY == 0){
            mListener.onScrollHorizontal(distanceX);
        }
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mListener != null)
            mListener.onClick();
        Log.d(TAG,"up");
        return false;
    }


    public void setListener(Listener mListener) {
        this.mListener = mListener;
    }

    interface Listener{
        int left = 0;
        int right = 1;
        /**
         * left scroll dx &gt; 0
         * right scroll dx &lt; 0
         * @param dx
         */
        void onScrollHorizontal(float dx);

        /**
         * upward scroll dy &gt; 0
         * downward scroll dy &lt; 0
         * @param x
         * @param dy
         */
        void onScrollVertical(float x,float dy);

        void onClick();
    }
}
