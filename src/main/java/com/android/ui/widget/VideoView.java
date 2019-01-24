package com.android.ui.widget;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import com.android.utils.Log;

/**
 * 自定义播放VideoView
 * 
 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
 * 
 * @author wangyang
 * @2014-2-25 上午11:28:53
 */
@SuppressLint("NewApi")
public class VideoView extends SurfaceView implements MediaPlayerControl {

	private static final String TAG = "VideoView";

	private static final int STATE_ERROR = -1;// all possible internal states

	private static final int STATE_IDLE = 0;// MediaPlayer的空闲状态

	private static final int STATE_PREPARING = 1;// MediaPlayer正在准备的状态

	private static final int STATE_PREPARED = 2;// MediaPlayer准备好了的状态

	private static final int STATE_PLAYING = 3;// MediaPlayer播放中的状态

	private static final int STATE_PAUSED = 4;// MediaPlayer暂停的状态

	private static final int STATE_PLAYBACK_COMPLETED = 5;// MediaPlayer播放完成的状态

	public static boolean RESETSETTING = false;

	private Uri mUri;// 视频URI路径

	private String mpath;// 设置要播放音频、视频的路径。

	private Context mContext;// 上下文，或者说坏境变量

	private SurfaceHolder mSurfaceHolder = null;// 播放和显示视频需要,是显示视频的帮助类。

	private MediaPlayer mMediaPlayer = null;// 播放视频、音乐其实就是靠这个类。

	private MediaController mMediaController;// 媒体控制面板，上面有暂停、快进、快退、拖动条等按钮和控件

	private OnCompletionListener mOnCompletionListener;// 监听器，当视频、音频、视频播放完后MediaPlayer会告诉监听器-“嘿我播放完了”

	private OnInfoListener mOnInfoListener;// 监听器，是否文件里面有空白数据

	private OnBufferingUpdateListener mOnBufferingUpdateListener;// 监听器，当视频、音频、视频播放前得缓冲

	private MediaPlayer.OnPreparedListener mOnPreparedListener;// 监听器，当视频、音频、视频播放前准备好了MediaPlayer会告诉该监听器-“嘿准备好可以播放了调用我开始播放了”

	private OnErrorListener mOnErrorListener;// 监听器，MediaPlayer加载视频时，或者播放视频时，如果出错。MediaPlayer会告诉该监听器。

	private MySizeChangeLinstener mMyChangeLinstener;// 自定义监听器，用于监听屏蔽大小是否改变

	private int mVideoWidth;// 控件显示宽度

	private int mVideoHeight;// 控件显示高度

	private int mSurfaceWidth;// 视频实际播放时显示宽度

	private int mSurfaceHeight;// 视频实际播放时显示宽度

	private int mCurrentState = STATE_IDLE;// 默认是空闲状态

	private int mTargetState = STATE_IDLE;// 默认是空闲状态

	private int mCurrentBufferPercentage;// 加载视频缓冲区的百分比，用于提示用户，视频加载多少了，好不让用户等着急。

	private int mSeekWhenPrepared; // 记录当前视频播放器播放位置

	private int mDuration;// 音频、视频文件播放时间总长度

	private boolean mIsRelease;// 判断是否需要释放MediaPlayer

	private boolean mCanPause;// 是否能暂停

	private boolean mCanSeekBack;// 是否能快退

	private boolean mCanSeekForward;// 是否能快进

	/**
	 * 自定义监听器，用于监听屏蔽大小是否改变
	 */
	public interface MySizeChangeLinstener {
		void doMyThings();
	}

	/**
	 * 自定义监听器，用于监听屏蔽大小是否改变,用于提供给其他类调用。
	 */
	public void setMySizeChangeLinstener(MySizeChangeLinstener l) {
		mMyChangeLinstener = l;
	}

	/**
	 * 视图运行的应用程序上下文，通过它可以访问当前主题、资源等等。
	 */
	public VideoView(Context context) {
		super(context);
		initVideoView();
		this.mContext = context;
	}

	/**
	 * context 视图运行的应用程序上下文，通过它可以访问当前主题、资源等等。 attrs 用于视 图的 XML标签属性集合。
	 */
	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initVideoView();
		this.mContext = context;
	}

	/**
	 * context 视图运行的应用程序上下文，通过它可以访问当前主题、资源等等。 attrs 用于视图的 XML 标签属性集合。 defStyle
	 * 应用到视图的默认风格。如果为 0 则不应用（包括当前主题中的）风格。 该值可 以是当前主题中的属性资源，或者是明确的风格资源ID。
	 */
	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView();
		this.mContext = context;
	}

	/**
	 * 是使用 View 前需要调用的方法. 通知View进行自身尺寸测量.
	 * 如果自己重写的话测量完自身大小注意需要调用setMeasuredDimension(int, int);这个方法
	 * 
	 * 设置控 件大小.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = desiredSize;
			break;

		case MeasureSpec.AT_MOST:
			/*
			 * Parent says we can be as big as we want, up to specSize. Don't be
			 * larger than specSize, and don't be larger than the max size
			 * imposed on ourselves.
			 */
			result = Math.min(desiredSize, specSize);
			break;

		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}

	/**
	 * 设置视频规模,是否全屏
	 * 
	 * @param width
	 * @param height
	 */
	public void setVideoScale(int width, int height, int left, int top, int right, int bottom) {
		MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
		lp.setMargins(left, top, right, bottom);
		lp.height = height;
		lp.width = width;
		// getHolder().set
		setLayoutParams(lp);
	}

	/**
	 * 设置布局参数
	 */
	public void setVideoLayoutParm(LayoutParams lp) {
		setLayoutParams(lp);
	}

	/**
	 * 初始化VideoView，设置相关参数。
	 */
	private void initVideoView() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		// 给SurfaceView当前的持有者一个回调对象。如果没有这个，表现是黑屏
		getHolder().addCallback(mSHCallback);

		// 通过setFocusable().来设置SurfaceView接受焦点的资格,
		setFocusable(true);
		// 对应在触摸模式下，你可以调用isFocusableInTouchMode().来获知是否有焦点来响应点触，
		// 也可以通过setFocusableInTouchMode().来设置是否有焦点来响应点触的资格.
		setFocusableInTouchMode(true);
		// 当用户请求在某个界面聚集焦点时，会调用requestFocus().这个方法。
		requestFocus();
		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
	}

	public synchronized void setVideoPath(String path) {
		mpath = path;
		Log.d(" video path = " + path);
		setVideoURI(Uri.parse(path));
	}

	/**
	 * 设置要播放音频、视频的路径。
	 * 
	 * @param uri
	 *            视频、音频路径
	 */
	public void setVideoURI(Uri uri) {
		mUri = uri;
		mSeekWhenPrepared = 0;
		openVideo();
		// 当某些变更导致视图的布局失效时调用该方法。该方法按照视图树的顺序调用。
		requestLayout();
		// 更新视图
		invalidate();
	}

	/*
	 * 停止播放，并释放资源，让MediaPlayer处于空闲状态。
	 * 
	 * public void stopPlayback() { if (mMediaPlayer != null) {
	 * mMediaPlayer.stop(); mMediaPlayer.release(); mMediaPlayer = null;
	 * mCurrentState = STATE_IDLE; mTargetState = STATE_IDLE; } }
	 */

	/**
	 * 
	 * 停止播放,设置uri为null,但是保留传入的url
	 * 
	 */
	public void stopPlay() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mUri = null;
			// mpath = null;
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
			mIsRelease = true;
		}
	}

	/**
	 * 
	 * @author wangyang
	 * @2014-2-25 上午11:32:28
	 * @param width
	 * @param height
	 */
	public void fullScreen(int width, int height) {
		setX(0);
		setY(0);
		getHolder().setFixedSize(width, height);
		setVideoScale(width, height, 0, 0, 0, 0);
	}

	/**
	 * 打开视频路径，并设置相关参数-该方法重要啊
	 */
	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null) {
			// not ready for playback just yet, will try again later
			return;
		}
		// Tell the music playback service to pause
		// framework.
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		// we shouldn't clear the target state, because somebody might have
		// called start() previously
		release(false);
		try {
			if (mMediaPlayer != null && !mIsRelease) {
				mMediaPlayer = null;
			}
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mDuration = -1;
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mCurrentBufferPercentage = 0;
			mMediaPlayer.setDataSource(mContext, mUri);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			// we don't set the target state here either, but preserve the
			// target state that was there before.
			mCurrentState = STATE_PREPARING;
			attachMediaController();
		} catch (IOException ex) {
			Log.w(TAG, "Unable to open content: " + mUri + "," + ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		} catch (IllegalArgumentException ex) {
			Log.w(TAG, "Unable to open content: " + mUri + "," + ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

	/**
	 * 设置系统默认控制面板
	 * 
	 * @param controller
	 */
	public void setMediaController(MediaController controller) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		mMediaController = controller;
		attachMediaController();
	}

	/**
	 * 显示系统默认控制面板
	 */
	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(isInPlaybackState());
		}
	}
	
	/**
	 * MediaPlayer大小变化后的监听
	 */
	MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		@Override
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mMyChangeLinstener != null) {
				mMyChangeLinstener.doMyThings();
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			}
		}
	};
	
	/**
	 * MediaPlayer预加载监听
	 */
	MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			mCurrentState = STATE_PREPARED;
			mCanPause = mCanSeekBack = mCanSeekForward = true;
			if (mOnPreparedListener != null) {
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}
			if (mMediaController != null) {
				mMediaController.setEnabled(true);
			}
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be

			if (seekToPosition != 0) {
				seekTo(seekToPosition);
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {

				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					// We didn't actually change the size (it was already at the
					// size
					// we need), so we won't get a "surface changed" callback,
					// so
					// start the video here instead of in the callback.
					if (mTargetState == STATE_PLAYING) {
						start();
						if (mMediaController != null) {
							mMediaController.show();
						}
					} else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null) {
							mMediaController.show(0);
						}
					}
				}
			} else {
				// We don't know the video size yet, but should start anyway.
				// The video size might be reported to us later.
				if (mTargetState == STATE_PLAYING) {
					start();
				}
			}
		}
	};
	
	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			if (mMediaController != null) {
				mMediaController.hide();
			}

			/* If an error handler has been supplied, use it and finish. */
			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
					return true;
				}
			}
			return true;
		}
	};

	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;
			// LogUtil.i(TAG, "videoView:缓冲数据为："+String.valueOf(percent));
			if (mOnBufferingUpdateListener != null) {
				mOnBufferingUpdateListener.onBufferingUpdate(mMediaPlayer, percent);
			}

		}
	};
	/**
	 * 播放空的内容做相关处理
	 */
	private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			if (mOnInfoListener != null) {
				mOnInfoListener.onInfo(mp, what, extra);
			}
			return false;
		}

	};

	public void setOnInfoListener(OnInfoListener listener) {
		mOnInfoListener = listener;
	}

	/**
	 * Register a callback to be invoked when the media file is loaded and ready
	 * to go.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	/**
	 * Register a callback to be invoked when the end of a media file has been
	 * reached during playback.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		mOnBufferingUpdateListener = l;
	}

	public void setOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	/**
	 * Register a callback to be invoked when an error occurs during playback or
	 * setup. If no listener is specified, or if the listener returned false,
	 * VideoView will inform the user of any errors.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize) {
				if (mSeekWhenPrepared != 0) {
					seekTo(mSeekWhenPrepared);
				}
				start();
				if (mMediaController != null) {
					mMediaController.show();
				}
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			openVideo();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// after we return from this we can't use the surface any more
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			release(true);
		}
	};

	/**
	 * release the media player in any state
	 * 不管MediaPlayer是处于正在播放，还是暂停状态，只要你MediaPlayer存在，我就要MediaPlayer播放结束并处于空闲状态。
	 */
	private void release(boolean cleartargetstate) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			if (cleartargetstate) {
				mTargetState = STATE_IDLE;
			}
		}
	}

	/**
	 * 用于处理触屏时间
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	/**
	 * 处理一个跟踪球事件。 默认情况下，不做任何动作， ..
	 */
	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				} else {
					start();
					mMediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) {
				pause();
				mMediaController.show();
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 显示、隐藏控制面板
	 */
	private void toggleMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
	}

	/**
	 * 开始播放
	 */
	@Override
	public void start() {
		if (isInPlaybackState()) {
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
			mIsRelease = false;
		}
		mTargetState = STATE_PLAYING;
	}

	/**
	 * 暂停播放
	 */
	@Override
	public void pause() {
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
	}

	/**
	 * 得到音乐、视频文件播放时时间的总长度
	 */
	@Override
	public int getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0) {
				return mDuration;
			}
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	/**
	 * 得到音乐、视频文件播放到的当前时间位置
	 */
	@Override
	public int getCurrentPosition() {
		if (isInPlaybackState()) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	/**
	 * 播放设置指定的位置
	 */
	@Override
	public void seekTo(int msec) {
		if (isInPlaybackState()) {
			mMediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	/**
	 * MediaPlayer是否是处于播放状态
	 */
	@Override
	public boolean isPlaying() {
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	/**
	 * 得到当前加载视频的百分比
	 */
	@Override
	public int getBufferPercentage() {
		if (mMediaPlayer != null) {
			return mCurrentBufferPercentage;
		}
		return 0;
	}

	/**
	 * 是否可以播放情况一切可好
	 * 
	 * @return
	 */
	private boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	/**
	 * 是否MediaPlayer能否暂停
	 */
	@Override
	public boolean canPause() {
		return mCanPause;
	}

	/**
	 * 是否MediaPlayer能否快退
	 */
	@Override
	public boolean canSeekBackward() {
		return mCanSeekBack;
	}

	/**
	 * 是否MediaPlayer能否快进
	 */
	@Override
	public boolean canSeekForward() {
		return mCanSeekForward;
	}
	
	/**
	 * 是否暂停状态
	 */
	public boolean isPause() {
		return mCurrentState == STATE_PAUSED;
	}
	
	/**
	 * 是否错误状态
	 */
	public boolean isError() {
		return mCurrentState == STATE_ERROR;
	}
	
	/**
	 * 是否停止播放状态
	 */
	public boolean isNoPlaying() {
		return !isPlaying() && !"".equals(mpath);
	}

	public MediaPlayer getmMediaPlayer() {
		return mMediaPlayer;
	}

	public static int getStatePlaying() {
		return STATE_PLAYING;
	}

	public void setmCurrentState(int mCurrentState) {
		this.mCurrentState = mCurrentState;
	}

	public void setmTargetState(int mTargetState) {
		this.mTargetState = mTargetState;
	}

	public int getmCurrentState() {
		return mCurrentState;
	}

	public int getmTargetState() {
		return mTargetState;
	}

	public String getMpath() {
		return mpath;
	}

	public void setMpath(String path) {
		this.mpath = path;
	}

	/**
	 * 得到视频的宽
	 */
	public int getVideoWidth() {
		return mVideoWidth;
	}

	/**
	 * @return：视频高度
	 */
	public int getVideoHeight() {
		return mVideoHeight;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}
}
