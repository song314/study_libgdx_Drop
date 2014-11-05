package com.badlogic.drop.model;

import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by tangsong on 11/5/14.
 */
public class TapBallManagerModel {

    private ContinuousClickListener mListener;
    private int mCount = 0;
    private int mContinunousCount;
    private int mMultCount;

    private long mLastClickMill;
    private long mStartMills;

    public void start() {
        mCount = 0;
        mContinunousCount = 0;
        mMultCount = 0;

        mLastClickMill = 0;
        mStartMills = TimeUtils.millis();
    }

    public void success() {
        mCount++;
        mContinunousCount++;
        mMultCount++;

        final long time = TimeUtils.millis();

        if (time - mStartMills < 1000 && mCount == 1) {
            mListener.onFirstBlood();
        }

        if (mCount > 1) {
            if (time - mLastClickMill < 1000) {
                mListener.onMultClick(mMultCount);
            } else {
                mMultCount = 0;
            }

            mListener.onContinuousClick(mContinunousCount);
        }

        mLastClickMill = TimeUtils.millis();
    }

    public void failed() {
        if (mContinunousCount > 2) {
            mListener.onShutDown();
        }

        mContinunousCount = 0;
        mMultCount = 0;
    }

    public void setContinuousClickListener(ContinuousClickListener listener) {
        mListener = listener;
    }

    public static interface ContinuousClickListener {
        void onContinuousClick(int count);

        void onMultClick(int count);

        void onFirstBlood();

        void onShutDown();
    }
}
