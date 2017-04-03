/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DumpManager;

public class Downloader extends Observable implements Runnable{
	
	public static final int DOWNLOADING = 0;
	
	public static final int PAUSED = 1;
	
	public static final int COMPLETED = 2;
	
	public static final int CANCELLED = 3;
	
	public static final int ERROR = 4;
	
	private static final int MAX_BUFFER_SIZE = 1024;
	
	private final URL url;
	
	private final String folder;
	
	private final String fileName;
	
	private int size = -1;
	
	private int downloaded = 0;
	
	private int status;

	public Downloader(String downloadFolder, String fileName, URL downloadUrl) {
		this.url = downloadUrl;
		this.folder = downloadFolder;
		this.fileName = fileName;
		
		status = DOWNLOADING;
		
		download();
	}
	
	public String getUrl(){
		return url.toString();
	}
	
	public int getSize(){
		return size;
	}
	
	public float getProgress(){
		return ((float) downloaded / size) * 100;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void pause(){
		status = PAUSED;
		reportState();
	}
	
	public void resume(){
		status = DOWNLOADING;
		reportState();
		download();
	}
	
	public void cancel(){
		status = CANCELLED;
		reportState();
		
		File file = new File(folder + "\\" + fileName + ".osz");
		file.delete();
		
		downloaded = 0;
	}
	
	private void error(){
		status = ERROR;
		reportState();
	}
	
	private void download(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		RandomAccessFile file = null;
		InputStream in = null;
		
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestProperty("Range", "bytes=" + downloaded + "-");
			
			
			conn.connect();
			
			if (conn.getResponseCode() / 100 != 2){
				error();
			}
			
			int len = conn.getContentLength();
			
			if (len < 1){
				error();
			}
			
			if (size == -1){
				size = len;
				reportState();
			}
			
			file = new RandomAccessFile(folder + "\\" + fileName, "rw");
			file.seek(downloaded);
			
			in = conn.getInputStream();
			
			while (status == DOWNLOADING){
				byte[] buffer = size - downloaded > MAX_BUFFER_SIZE ?
						new byte[MAX_BUFFER_SIZE] :
						new byte[size - downloaded];
				int read = in.read(buffer);
				if (read == -1){
					break;
				}
				
				file.write(buffer, 0, read);
				downloaded += read;
				reportState();
			}
			
			if (status == DOWNLOADING){
				status = COMPLETED;
				reportState();
			}
		} catch (IOException e){
			DumpManager.getInstance()
				.addDump(new DebugDump(
						null,
						"(Try&catch try)",
						"Error reporting and debug dump",
						"(Try&catch finally)",
						"Error when downloading",
						false,
						e));
			error();
		} finally {
			if (file != null){
				try {
					file.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			
			if (in != null){
				try {
					in.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void reportState(){
		setChanged();
		notifyObservers();
	}

}
