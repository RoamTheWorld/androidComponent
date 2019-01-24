package com.android.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadByMutilThread {

    /**
     * @author wangyang 2014-11-14 上午11:40:29
     */
    public interface DownLoadListener {
        void beforeDownLoad(int max);

        void onDownLoadProcess(int progress, int max);

        void finished(String filePath);

        void onError(Throwable error);
    }

    // 超时时间
    private static final int TIME_OUT = Constants.TIME_OUT;
    // 默认子线程数量
    private int threadCount = Constants.THREAD_COUNT;
    private int onceThreadReadByteCount = Constants.THREAD_BUFFER_SIZE;
    private int refreshProgressDelay = Constants.REFRESH_PROGRESS_DELAY;
    private int fileLength;
    private DownLoadListener downLoadListener;
    private String saveFilePath;
    private String urlPath;
    private int totalDownLength;
    private String[] subFiles;
    private static boolean stopFlag = true;
    private boolean threadStopFlag = true;
    private boolean isSupportBreakPoint;

    public DownLoadByMutilThread(int threadCount, DownLoadListener downLoadListener, String saveFilePath, String urlPath) {
        super();
        this.threadCount = threadCount;
        this.downLoadListener = downLoadListener;
        this.saveFilePath = saveFilePath;
        this.urlPath = urlPath;
        this.subFiles = new String[threadCount];
    }

    public DownLoadByMutilThread(DownLoadListener downLoadListener, String saveFilePath, String urlPath) {
        super();
        this.downLoadListener = downLoadListener;
        this.saveFilePath = saveFilePath;
        this.urlPath = urlPath;
        this.subFiles = new String[threadCount];
    }

    private String getSubThreadFile(String fileName, int i) {
        return fileName.substring(0, fileName.lastIndexOf(".")) + i + ".txt";
    }

    public void stop() {
        stopFlag = true;
        threadStopFlag = true;
    }

    public void startDownLoad() {
        startDownLoad(false);
    }

    public void startDownLoad(boolean isSupportBreakPoint) {
        this.isSupportBreakPoint = isSupportBreakPoint;
        if (!stopFlag)
            return;
        new Thread() {
            @Override
            public void run() {
                stopFlag = false;
                while (!stopFlag) {
                    try {
                        if (fileLength > 0)
                            downLoadListener.onDownLoadProcess(totalDownLength, fileLength);
                        Thread.sleep(refreshProgressDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stopFlag = true;
                        downLoadListener.onError(e);
                    }
                }
            }
        }.start();
        threadStopFlag = false;
        new Thread() {
            @Override
            public void run() {
                try {
                    startThreadDownLoad();
                } catch (IOException e) {
                    e.printStackTrace();
                    stopFlag = true;
                    downLoadListener.onError(e);
                }
            }
        }.start();
    }

    @SuppressWarnings("resource")
    private void startThreadDownLoad() throws IOException {
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIME_OUT);
        int code = conn.getResponseCode();
        if (code == 200) {
            // 获取文件长度并设置进度条最大长度
            fileLength = conn.getContentLength();
            Log.e("length", fileLength + "");
            downLoadListener.beforeDownLoad(fileLength);
            RandomAccessFile raf = new RandomAccessFile(saveFilePath, "rwd");
            raf.setLength(fileLength);
            // 循环遍历线程数,并开启子线程分段下载
            threadCount = subFiles.length;
            for (int i = 1; i <= subFiles.length; i++) {
                // 计算每个线程下载的文件大小
                int blockSize = fileLength / threadCount;
                int startIndex = (i - 1) * blockSize;
                int endIndex = i * blockSize - 1;
                if (i == threadCount)
                    endIndex = fileLength;
                // 生成子线程保存下载字节的文件名数组
                subFiles[i - 1] = getSubThreadFile(saveFilePath, i);

                // 开启子线程进行下载
                Thread subThread = new DownLoadSubThread(startIndex, endIndex, subFiles[i - 1]);
                subThread.start();
            }
        }
    }

    class DownLoadSubThread extends Thread {
        private static final String TAG = "DownLoadSubThread";
        private int startIndex;
        private int endIndex;
        private String subFileName;

        public DownLoadSubThread(int startIndex, int endIndex, String subFileName) {
            super();
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.subFileName = subFileName;
        }

        @SuppressWarnings("resource")
        @Override
        public void run() {
            try {
                // 获取存储在sd卡上子线程开始字节
                if (isSupportBreakPoint) {
                    File file = new File(subFileName);
                    if (file.exists() && file.length() > 0) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        int saveStartIndex = Integer.parseInt(reader.readLine());
                        if (saveStartIndex > 0) {
                            synchronized (DownLoadByMutilThread.class) {
                                totalDownLength += (saveStartIndex - startIndex);
                            }

                            if (saveStartIndex >= endIndex)
                                return;
                            startIndex = saveStartIndex;
                        }
                    }
                }

                // 开启链接,下载分段文件并保存
                URL url = new URL(urlPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(TIME_OUT);

                conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
                Log.e(TAG, "bytes=" + startIndex + "-" + endIndex);
                InputStream input = conn.getInputStream();
                String contentRange = conn.getHeaderField("Content-Range");
                Log.i(TAG, "contentRange  :" + contentRange);
                RandomAccessFile raf = new RandomAccessFile(saveFilePath, "rwd");
                raf.seek(startIndex);
                byte[] args = new byte[onceThreadReadByteCount];
                int len = 0;
                int total = 0;
                while ((len = input.read(args)) != -1) {
                    // 读取文件并保存
                    raf.write(args, 0, len);

                    // 计算每次下载的长度,并根据上次开始节点位置算出下载开始位置
                    if (isSupportBreakPoint && !StringUtil.isEmpty(contentRange)) {
                        total += len;
                        if ((total + startIndex) < endIndex) {
                            FileOutputStream out = new FileOutputStream(subFileName);
                            out.write(String.valueOf(total + startIndex).getBytes());
                            out.close();
                        }
                    }

                    // 更新完成长度
                    synchronized (DownLoadByMutilThread.class) {
                        totalDownLength += len;
                    }

                    if (threadStopFlag) {
                        input.close();
                        raf.close();
                    }
                }
                raf.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
                stopFlag = true;
            } finally {
                // 如果所有线程都完成了下载,则删掉存储的字节文件
                synchronized (DownLoadByMutilThread.class) {
                    threadCount--;
                    if (threadCount == 0) {
                        if (isSupportBreakPoint && totalDownLength == fileLength) {
                            for (int i = 1; i <= subFiles.length; i++) {
                                File subFile = new File(subFiles[i - 1]);
                                boolean b = subFile.delete();
                                Log.e(TAG, "文件" + subFile.getPath() + "删除" + (b ? "成功" : "失败"));
                            }
                        }

                        try {
                            Thread.sleep(Constants.REFRESH_PROGRESS_DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (totalDownLength == fileLength) {
                            stopFlag = true;
                            downLoadListener.finished(saveFilePath);
                        }
                    }
                }
            }
        }

    }
}
