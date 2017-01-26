package com.hustunique.jianguo.parsingplayer.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hustunique.jianguo.parsingplayer.LogUtil;
import com.hustunique.jianguo.parsingplayer.R;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by JianGuo on 1/20/17.
 * Custom media controller view for video view.
 */
// TODO: 1/20/17 Custom media controller panel
// TODO: 1/26/17 Currently we use popupwindow to show controll panel. Consider using WindowManager in later development.
public class ParsingMediaController implements IMediaController {
    private MediaController.MediaPlayerControl mPlayer;
    private static final int sDefaultTimeOut = 5000;
    private static final String TAG = "ParsingMediaController";
    private View mRoot;
    private Context mContext;
    private View mAnchor;
    private ImageButton mPauseButton;
    private SeekBar mProgress;
    private TextView mCurrentTime, mEndTime;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private boolean mShowing;
    private PopupWindow mPopupWindow;
    private int mX, mY;

    public ParsingMediaController(Context context, AttributeSet attrs) {
        mContext = context;
        initPopupWindow();
    }

    public ParsingMediaController(Context context) {
        this(context, null);
    }


    private void initPopupWindow() {
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(mContext.getDrawable(android.R.color.transparent));
        mPopupWindow.setFocusable(true);
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }
        mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            mProgress.setOnSeekBarChangeListener(mSeekListener);
            mProgress.setMax(1000);
        }
        mEndTime = (TextView) v.findViewById(R.id.time);
        mCurrentTime = (TextView) v.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        show(0);
                        break;
                    case MotionEvent.ACTION_UP:
                        show(sDefaultTimeOut);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        hide();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    private View makeControllerView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.media_controller, null);
        initControllerView(mRoot);
        return mRoot;
    }


    private final View.OnClickListener mPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            show(0);
        }
    };

    private boolean mDragging;

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (!mDragging && mShowing && mPlayer.isPlaying()) {
                mRoot.postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime((int) newposition));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            show(3600000);

            mDragging = true;
            mRoot.removeCallbacks(mShowProgress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeOut);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mRoot.post(mShowProgress);
        }
    };

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }


    @Override
    public void hide() {
        if (mAnchor == null)
            return;

        if (mShowing) {
            try {
                mRoot.removeCallbacks(mShowProgress);
                mPopupWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                LogUtil.w(TAG, "already removed");
            }
            mShowing = false;
        }
        for (View view : mShowOnceArray)
            view.setVisibility(View.GONE);
        mShowOnceArray.clear();
    }


    @Override
    public void setAnchorView(View view) {
        if (mAnchor != null)
            mAnchor.removeOnLayoutChangeListener(mOnLayoutChangeListener);
        mAnchor = view;
        LogUtil.i(TAG, "current anchorView: " + mAnchor.toString());
        if (mAnchor != null)
            mAnchor.addOnLayoutChangeListener(mOnLayoutChangeListener);
        View v = makeControllerView();
        mPopupWindow.setContentView(v);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mRoot.setEnabled(enabled);
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }


    private final View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
            updateAnchorViewLayout();
            showPopupWindowLayout();
        }
    };

    private void showPopupWindowLayout() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPopupWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, mX, mY);
    }

    private void updateAnchorViewLayout() {
        assert mAnchor != null;
        int[] anchorPos = new int[2];
        mAnchor.getLocationOnScreen(anchorPos);
        mRoot.measure(View.MeasureSpec.makeMeasureSpec(mAnchor.getWidth(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(mAnchor.getHeight(), View.MeasureSpec.AT_MOST));
        int width = mAnchor.getWidth();
        mX = anchorPos[0] + (mAnchor.getWidth() - width) / 2;
        mY = anchorPos[1] + mAnchor.getHeight() - mRoot.getMeasuredHeight();
        LogUtil.i(TAG, "update mX: " + mX + ", mY: " + mY);
    }

    // FIXME: 1/26/17 Buggy when toggle controller multiple times in a short period
    @Override
    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            LogUtil.i(TAG, "show popupWindow at top-left pos:" + "(" + mX + ", " + mY + ")");
            showPopupWindowLayout();
            mShowing = true;
        }
        updatePausePlay();


        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mRoot.post(mShowProgress);

        if (timeout != 0) {
            mRoot.removeCallbacks(mFadeOut);
            mRoot.postDelayed(mFadeOut, timeout);
        }
    }


    @Override
    public void show() {
        show(sDefaultTimeOut);
    }

    private ArrayList<View> mShowOnceArray = new ArrayList<View>();

    @Override
    public void showOnce(View view) {
        mShowOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }




}
