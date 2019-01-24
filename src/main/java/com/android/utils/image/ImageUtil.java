package com.android.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;

import com.android.ui.widget.NetworkImageView.LoadMethod;
import com.android.utils.Constants;
import com.android.utils.FileUtil;
import com.android.utils.StringUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @description 图片处理工具类
 * 
 * @author wangyang
 * @create 2014-2-21 上午10:26:26
 * @version 1.0.0
 */
public class ImageUtil {
	/**
	 * 压缩图片宽
	 */
	private static final int DEFALUT_IMAGE_SIZE = 200;

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param zf
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float zf) {
		Matrix matrix = new Matrix();
		matrix.postScale(zf, zf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param hf
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float wf, float hf) {
		Matrix matrix = new Matrix();
		matrix.postScale(wf, hf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 获取图片角度信息
	 * 
	 * @param path
	 * @return
	 */
	public static int getPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 图片旋转
	 * 
	 * @param degree
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotaingImage(int degree, Bitmap bitmap) {
		if (bitmap != null) {
			// 旋转图片 动作
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			// 创建新的图片
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}

	
	public static boolean  saveBitmap(Bitmap bitmap,File file){
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 读取本地图片，进行压缩处理
	 * 
	 * @param path
	 *            图片路径
	 * @return
	 */
	public static Bitmap decodeFile(File path) {
		return decodeFile(path, DEFALUT_IMAGE_SIZE);
	}

	/**
	 * 读取本地图片
	 * 
	 * @param path
	 *            图片路径
	 * @param size
	 *            图片的压缩宽
	 * @return
	 */
	public static Bitmap decodeFile(File path, int size) {
		FileInputStream fisOne = null;
		FileInputStream fisTwo = null;
		BitmapFactory.Options optionOne = new BitmapFactory.Options();
		BitmapFactory.Options optionTwo = new BitmapFactory.Options();
		try {
			// 只解码图片的Bounds
			optionOne.inJustDecodeBounds = true;
			fisOne = new FileInputStream(path);
			BitmapFactory.decodeStream(fisOne, null, optionOne);

			// 计算压缩率
			final int REQUIRED_SIZE = size > 0 ? size : DEFALUT_IMAGE_SIZE;
			int width_tmp = optionOne.outWidth, height_tmp = optionOne.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// if (scale >= 2) {
			// scale /= 2;
			// }

			// 压缩图片
			optionTwo.inSampleSize = scale;
			fisTwo = new FileInputStream(path);
			return BitmapFactory.decodeStream(fisTwo, null, optionTwo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fisOne != null)
					fisOne.close();
				if (fisTwo != null)
					fisTwo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 绘制圆型图片
	 * 
	 * @param image
	 * @param circularScale
	 *            圆角比例
	 * @return
	 */
	public static Bitmap createFramedPhoto(Bitmap image, float circularScale) {

		int width = 100;
		int height = 100;
		// 根据源文件新建一个darwable对象
		Bitmap bitmap = null;
		// 新建一个新的输出图片
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		int offset = image.getHeight() - image.getWidth();
		if (offset > 0) {// 高大于宽
			bitmap = Bitmap.createBitmap(image, 0, offset / 2, image.getWidth(), image.getWidth());
		} else if (offset < 0) {// 宽大于高
			bitmap = Bitmap.createBitmap(image, Math.abs(offset) / 2, 0, image.getHeight(), image.getHeight());
		} else {
			bitmap = image;
		}
		Canvas canvas = new Canvas(output);
		// 新建一个矩形
		RectF outerRect = new RectF(0, 0, width, height);
		// 产生一个红色的圆角矩形
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		canvas.drawRoundRect(outerRect, width * circularScale / 2, height * circularScale / 2, paint);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// 将源图片绘制到这个圆角矩形上

		if (image != null) {
			float scaleX = Float.parseFloat(width + "") / bitmap.getHeight();
			float scaleY = Float.parseFloat(height + "") / bitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(scaleX, scaleY);
			Bitmap sizeBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			canvas.drawBitmap(sizeBm, 0, 0, paint);
		}
		return output;
	}

	/**
	 * 
	 * 创建圆角bitmap(解决以前是网络图片时不清晰的问题)
	 * 
	 * @param bitmap
	 * @param circularScale
	 *            圆角比例
	 * @return
	 */
	public static Bitmap CreateRoundedCornerBitmap(Bitmap bitmap, float circularScale) {
		try {
			// Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
			// bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.WHITE);
			canvas.drawRoundRect(rectF, bitmap.getWidth() * circularScale / 2, bitmap.getHeight() * circularScale / 2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return bitmap;
		} catch (Exception e) {
			return bitmap;
		}
	}

	/**
	 * 从资源文件中读取图片,防止内存溢出
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap decodeResource(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inPreferredConfig = Bitmap.Config.RGB_565;

		opt.inPurgeable = true;

		opt.inInputShareable = true;

		InputStream is = context.getResources().openRawResource(resId);

		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 
	 * 从本地获取大图片,防止内存溢出
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap decodeFile(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		InputStream input = null;
		try {
			input = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bmp = BitmapFactory.decodeStream(input, null, options);
		return bmp;
	}

	/**
	 * 获取视频略缩图
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static Bitmap getVideoImage(Context context, String path) {
		Bitmap bitmap = null;
		try {
			if (path != null) {
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				retriever.setDataSource(path);
				bitmap = retriever.getFrameAtTime();
			}
		} catch (Exception e) {
			return null;
		}
		return bitmap;
	}

	/**
	 * drawable 转 Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	// =============================================新加入的方法

	private static com.nostra13.universalimageloader.core.ImageLoader imageLoader;

	/**
	 * 获取Universal图片加载器
	 * 
	 * @author 王坤辉
	 * @create 2014-7-2 下午5:16:19
	 * @return
	 */
	public static com.nostra13.universalimageloader.core.ImageLoader getUniversalImageLoader() {
		return com.nostra13.universalimageloader.core.ImageLoader.getInstance();
	}

	/**
	 * 获取图片加载器
	 * 
	 * @author 王坤辉
	 * @create 2014-7-18 下午2:35:04
	 * @param context
	 * @return
	 */
	public static com.nostra13.universalimageloader.core.ImageLoader getUniversalImageLoader(Context context) {
		if (imageLoader == null) {
			imageLoader = getUniversalImageLoader();
			ImageLoaderConfiguration configuration = ImageUtil.getConfiguration(context, FileUtil.getFileCacheDir(context, "universal"));
			imageLoader.init(configuration);
		}
		return imageLoader;
	}

	/**
	 * 获取Universal图片加载器初始化参数
	 * 
	 * @author 王坤辉
	 * @create 2014-7-2 下午5:21:31
	 * @param context
	 * @param cacheFile
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ImageLoaderConfiguration getConfiguration(Context context, File cacheFile) {
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		builder.diskCacheFileNameGenerator(new HashCodeFileNameGenerator());
		//builder.memoryCache(new WeakMemoryCache());
		if (cacheFile != null)
			builder.discCache(new UnlimitedDiskCache(cacheFile));
		return builder.build();
	}

	/**
	 * 获取Universal图片加载器缓存参数
	 * 
	 * @author 王坤辉
	 * @create 2014-7-2 下午5:21:31
	 * @param context
	 * @param dirFile
	 * @return
	 */
	public static DisplayImageOptions getOptions(int failureImageRes, int loadingImageRes,Config config,ImageScaleType imageScaleType) {
		return new DisplayImageOptions.Builder().bitmapConfig(config).imageScaleType(imageScaleType).cacheInMemory(true).cacheOnDisk(true).showImageForEmptyUri(failureImageRes).showImageOnFail(failureImageRes).showImageOnLoading(loadingImageRes).build();
	}
	
	public static DisplayImageOptions getOptions(Config config,ImageScaleType imageScaleType) {
		return new DisplayImageOptions.Builder().bitmapConfig(config).imageScaleType(imageScaleType).cacheInMemory(true).cacheOnDisk(true).build();
	}

	/**
	 * 
	 * 显示网络图片（头像）
	 * 
	 * @author 张建光
	 * @param context
	 * @param imageview
	 * @param loadNetImageMethod
	 *            加载网络图片的方式（现在有volley，universal两种方式）
	 * @param url
	 * @param failureImageRes
	 *            加载失败时显示的图片资源
	 * @param loadingImageRes
	 *            加载中显示的图片资源
	 */
	public static void DisplayImage(Context context, ImageView imageview, LoadMethod method, String url,ImageListener listener, int failureImageRes, int loadingImageRes,Config config,ImageScaleType imageScaleType) {
		switch (method) {
		case Universal:
			ImageUtil.getUniversalImageLoader(context).displayImage(url, imageview, getOptions(failureImageRes, loadingImageRes,config,imageScaleType),listener,listener);
			break;
		default:
			break;
		}
	}
	
	public static void loadImage(Context context, LoadMethod method, String url,ImageListener listener,ImageSize imageSize,Config config,ImageScaleType imageScaleType) {
		switch (method) {
		case Universal:
			ImageUtil.getUniversalImageLoader(context).loadImage(url, imageSize, getOptions(config, imageScaleType), listener, listener);
			break;
		default:
			break;
		}
	}
	
	public static void loadImage(Context context, LoadMethod method, String url,ImageListener listener,ImageSize imageSize) {
		loadImage(context, method, url, listener,imageSize, Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2);
	}
	
	public static void loadImage(Context context, LoadMethod method, String url,ImageListener listener){
		loadImage(context, method, url, listener, null);
	}
	
	
	/**
	 * 压缩图片大小
	 * 
	 * @param bmImg
	 * @param maxSize
	 *            (压缩过后图片大小的最大值，单位kb)
	 * @return
	 */
	public static Bitmap compressImage(Bitmap bmImg, int maxSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bmImg.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 照相机图片处理
	 * 
	 * @param context
	 * @param imagePath
	 * @return
	 */
	public static String handleCameraImage(Context context, String srcPath, String destDir) {
		String name = System.currentTimeMillis() + ".jpg";
		if (StringUtil.isEmpty(destDir))
			destDir = Constants.FILE_ROOT_DIRECTORY + "/" + Constants.FILE_IMAGE_DIRECTORY;
		File saveFile = new File(FileUtil.getFileDir(context, destDir), name);
		return handleImage(context, srcPath, saveFile);
	}

	/**
	 * 照相机图片处理
	 * 
	 * @param context
	 * @param imagePath
	 * @return
	 */
	public static String handleCameraImage(Context context, String srcPath) {
		return handleCameraImage(context, srcPath, null);
	}

	/**
	 * 相册图片处理,旋转图片并将其复制进项目图片缓存处
	 * 
	 * @param imagePath
	 * @param destFile
	 *            保存路径
	 * @return 图片复制后的路径
	 */
	public static String handleImage(Context context, String srcPath, File destFile) {
		if (StringUtil.isEmpty(srcPath))
			return null;

		FileOutputStream fos = null;
		try {
			Bitmap bitmap = ImageUtil.decodeFile(new File(srcPath));
			int degree = ImageUtil.getPictureDegree(srcPath);
			Bitmap image = ImageUtil.rotaingImage(degree, bitmap);

			fos = new FileOutputStream(destFile);
			image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			return destFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}