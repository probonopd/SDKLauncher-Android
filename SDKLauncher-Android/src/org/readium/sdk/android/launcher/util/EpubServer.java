/*
 * EpubServer.java
 * SDKLauncher-Android
 *
 * Created by Yonathan Teitelbaum (Mantano) on 2013-09-03.
 */
//  Copyright (c) 2014 Readium Foundation and/or its licensees. All rights reserved.
//  Redistribution and use in source and binary forms, with or without modification, 
//  are permitted provided that the following conditions are met:
//  1. Redistributions of source code must retain the above copyright notice, this 
//  list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright notice, 
//  this list of conditions and the following disclaimer in the documentation and/or 
//  other materials provided with the distribution.
//  3. Neither the name of the organization nor the names of its contributors may be 
//  used to endorse or promote products derived from this software without specific 
//  prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
//  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
//  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
//  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
//  OF THE POSSIBILITY OF SUCH DAMAGE

package org.readium.sdk.android.launcher.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.readium.sdk.android.ManifestItem;
import org.readium.sdk.android.Package;
import org.readium.sdk.android.PackageResource;
import org.readium.sdk.android.util.ResourceInputStream;

import android.util.Log;
import fi.iki.elonen.NanoHTTPD;

/**
 * This small web server will serve media files such as audio and video.
 */
public class EpubServer extends NanoHTTPD {

	public interface DataPreProcessor {
		byte[] handle(byte[] data, String mime, String uriPath,
				ManifestItem item);
	}

	private static final String TAG = "EpubServer";
	public static final String HTTP_HOST = "127.0.0.1";
	public static final int HTTP_PORT = 8080;
	/**
	 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
	 */
	public static final Map<String, String> MIME_TYPES;

	private final Package mPackage;
	private final boolean quiet;

	private final DataPreProcessor dataPreProcessor;

	static {
		Map<String, String> tmpMap = new HashMap<String, String>();
		tmpMap.put("html", "application/xhtml+xml"); // FORCE
		tmpMap.put("xhtml", "application/xhtml+xml"); // FORCE
		tmpMap.put("xml", "application/xml"); // FORCE
		tmpMap.put("htm", "text/html");
		tmpMap.put("css", "text/css");
		tmpMap.put("java", "text/x-java-source, text/java");
		tmpMap.put("txt", "text/plain");
		tmpMap.put("asc", "text/plain");
		tmpMap.put("gif", "image/gif");
		tmpMap.put("jpg", "image/jpeg");
		tmpMap.put("jpeg", "image/jpeg");
		tmpMap.put("png", "image/png");
		tmpMap.put("mp3", "audio/mpeg");
		tmpMap.put("m3u", "audio/mpeg-url");
		tmpMap.put("mp4", "video/mp4"); // could be audio!
		tmpMap.put("ogv", "video/ogg");
		tmpMap.put("flv", "video/x-flv");
		tmpMap.put("mov", "video/quicktime");
		tmpMap.put("swf", "application/x-shockwave-flash");
		tmpMap.put("js", "application/javascript");
		tmpMap.put("pdf", "application/pdf");
		tmpMap.put("doc", "application/msword");
		tmpMap.put("ogg", "application/x-ogg");
		tmpMap.put("zip", "application/octet-stream");
		tmpMap.put("exe", "application/octet-stream");
		tmpMap.put("class", "application/octet-stream");
		tmpMap.put("webm", "video/webm");
		MIME_TYPES = Collections.unmodifiableMap(tmpMap);
	}

	public EpubServer(String host, int port, Package pckg, boolean quiet,
			DataPreProcessor dataPreProcessor) {
		super(host, port);
		// this.setAsyncRunner(new AsyncRunner() {
		// @Override
		// public void exec(Runnable code) {
		// //SYNC!
		// code.run();
		// }
		// });
		this.mPackage = pckg;
		this.quiet = quiet;
		this.dataPreProcessor = dataPreProcessor;
	}

	Package getPackage() {
		return mPackage;
	}

	public void startServer() {
		try {
			start();
		} catch (IOException e) {
			Log.e(TAG, "" + e.getMessage());
		}
	}

	private final Object criticalSectionSynchronizedLock = new Object();

	public class ByteStreamInput extends Response.NanoInputStream {

		private final Object criticalSectionSynchronizedLock;

		private final int requestedOffset;
		private final int requestedLength;

		public ByteStreamInput(ResourceInputStream is, int offset, int length,
				Object lock) {
			super(is);
			requestedOffset = offset;
			requestedLength = length;
			criticalSectionSynchronizedLock = lock;
		}

		@Override
		public void close() throws IOException {
			synchronized (criticalSectionSynchronizedLock) {
				super.close();
			}
		}

		private int alreadyRead = 0;

		public int available() throws IOException {
			int available;
			synchronized (criticalSectionSynchronizedLock) {
				available = super.available();
			}
			int remaining = requestedLength - alreadyRead;
			return available < remaining ? available : remaining;
		}

		public int read(byte[] b, int len) throws IOException {
			ResourceInputStream ris = (ResourceInputStream) inputStream;
			int read = 0;
			// byte[] bytes = null;
			synchronized (criticalSectionSynchronizedLock) {
				// int read = super.read(b, len);

				if (requestedOffset >= 0) {
					// bytes = ris.getRangeBytes(requestedOffset + alreadyRead,
					// len);
					read = (int) ris.getRangeBytesX(requestedOffset
							+ alreadyRead, (long) len, b);
					// read = bytes.length;
				} else {
					read = (int) ris.readX((long) len, b);
				}
			}

			// if (read > 0) {
			// System.arraycopy(bytes, 0, b, 0, len < read ? len : read);
			// }
			alreadyRead += read;
			return read;
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		Map<String, String> header = session.getHeaders();
		Map<String, String> parms = session.getParms();
		String uri = session.getUri();

		if (!quiet) {
			Log.d(TAG, session.getMethod() + " '" + uri + "' ");

			Iterator<String> e = header.keySet().iterator();
			while (e.hasNext()) {
				String value = e.next();
				Log.d(TAG, "  HDR: '" + value + "' = '" + header.get(value)
						+ "'");
			}
			e = parms.keySet().iterator();
			while (e.hasNext()) {
				String value = e.next();
				Log.d(TAG, "  PRM: '" + value + "' = '" + parms.get(value)
						+ "'");
			}
		}

		String httpPrefix = "http://" + HTTP_HOST + ":" + HTTP_PORT + "/";
		int iHttpPrefix = uri.indexOf(httpPrefix);
		uri = iHttpPrefix == 0 ? uri.substring(httpPrefix.length()) : uri;
		uri = uri.startsWith("/") ? uri.substring(1) : uri;

		int indexOfQ = uri.indexOf('?');
		if (indexOfQ >= 0) {
			uri = uri.substring(0, indexOfQ);
		}

		int indexOfSharp = uri.indexOf('#');
		if (indexOfSharp >= 0) {
			uri = uri.substring(0, indexOfSharp);
		}

		Package pckg = getPackage();

		Response res = null;

		int contentLength = -1;
		synchronized (criticalSectionSynchronizedLock) {
			contentLength = pckg.getArchiveInfoSize(uri);
		}

		if (contentLength == 0) {
			res = new Response(Response.Status.NOT_FOUND,
					NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
		}

		if (res == null) {

			String mime = null;
			int dot = uri.lastIndexOf('.');
			if (dot >= 0) {
				mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
			}
			if (mime == null) {
				mime = "application/octet-stream";
			}

			ManifestItem item = pckg.getManifestItem(uri);
			String contentType = item != null ? item.getMediaType() : null;
			if (mime != "application/xhtml+xml" && mime != "application/xml" // FORCE
					&& contentType != null && contentType.length() > 0) {
				mime = contentType;
			}

			// Calculate etag
			String etag = Integer
					.toHexString((pckg.getUniqueID()
							+ pckg.getModificationDate() + ""
							+ pckg.getBasePath() + "" + uri).hashCode());

			long startFrom = 0;
			long endAt = -1;
			String range = header.get("range");

			if (!quiet)
				Log.d(TAG, ">>>>> HTTP range: " + range);

			if (range != null) {
				if (range.startsWith("bytes=")) {
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					try {
						if (minus > 0) {
							startFrom = Long.parseLong(range
									.substring(0, minus));
						}
					} catch (NumberFormatException ignored) {
						Log.e(TAG, "NumberFormatException (RANGE BEGIN): "
								+ ignored.getMessage());
					}
					try {
						if (minus > 0) {
							String endStr = range.substring(minus + 1);
							if (endStr != null && endStr.length() > 0)
								endAt = Long.parseLong(endStr);
						}
					} catch (NumberFormatException ignored) {
						Log.e(TAG, "NumberFormatException (RANGE END): "
								+ ignored.getMessage());
					}
				}
			}

			// get if-range header. If present, it must match etag or else we
			// should ignore the range request
			String ifRange = header.get("if-range");
			boolean headerIfRangeMissingOrMatching = (ifRange == null || etag
					.equals(ifRange));

			String ifNoneMatch = header.get("if-none-match");
			boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null
					&& (ifNoneMatch.equals("*") || ifNoneMatch.equals(etag));

			// Change return code and add Content-Range header when skipping is
			// requested

			if (headerIfRangeMissingOrMatching && range != null
					&& startFrom >= 0 && startFrom < contentLength) {
				// range request that matches current etag
				// and the startFrom of the range is satisfiable
				if (headerIfNoneMatchPresentAndMatching) {
					// range request that matches current etag
					// and the startFrom of the range is satisfiable
					// would return range from file
					// respond with not-modified
					res = new Response(Response.Status.NOT_MODIFIED, mime, "");

					if (!quiet)
						Log.d(TAG, "NOT_MODIFIED #1");
				} else {
					if (endAt < 0) {
						endAt = contentLength - 1;
					}
					long newLen = endAt - startFrom + 1;
					if (newLen < 0) {
						newLen = 0;
					}

					// byte[] data = null;
					ResourceInputStream is = null;
					synchronized (criticalSectionSynchronizedLock) {
						PackageResource packageResource = pckg
								.getResourceAtRelativePath(uri);

						is = (ResourceInputStream) packageResource
								.getInputStream(true);

						// data = packageResource.readDataOfLength((int) newLen,
						// (int) startFrom);

						int updatedContentLength = packageResource
								.getContentLength();
						if (updatedContentLength != contentLength) {
							Log.e(TAG, "UPDATED CONTENT LENGTH! "
									+ updatedContentLength + "<--"
									+ contentLength);
						}
					}

					// if (newLen != data.length) {
					// Log.e(TAG, "RANGE LENGTH! " + newLen + " != "
					// + data.length);
					// }

					// byte[] data_ = new byte[data.length];
					// System.arraycopy(data,0,data_,0,data.length);

					// InputStream is = new ByteArrayInputStream(data);

					ByteStreamInput bis = new ByteStreamInput(is,
							(int) startFrom, (int) newLen,
							criticalSectionSynchronizedLock);

					res = new Response(Response.Status.PARTIAL_CONTENT, mime,
							bis);

					res.addHeader("Content-Length", "" + newLen);
					res.addHeader("Content-Range", "bytes " + startFrom + "-"
							+ endAt + "/" + contentLength);

					if (!quiet)
						Log.d(TAG, "PARTIAL_CONTENT: " + startFrom + "-"
								+ endAt + " / " + contentLength + " (" + newLen
								+ ")");
				}
			} else {
				if (headerIfRangeMissingOrMatching && range != null
						&& startFrom >= contentLength) {
					// return the size of the file
					// 4xx responses are not trumped by if-none-match
					res = new Response(Response.Status.RANGE_NOT_SATISFIABLE,
							NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes */" + contentLength);

					if (!quiet)
						Log.d(TAG, "RANGE_NOT_SATISFIABLE: " + contentLength);

				} else if (range == null && headerIfNoneMatchPresentAndMatching) {
					// full-file-fetch request
					// would return entire file
					// respond with not-modified
					res = new Response(Response.Status.NOT_MODIFIED, mime, "");

					if (!quiet)
						Log.d(TAG, "NOT_MODIFIED #2");

				} else if (!headerIfRangeMissingOrMatching
						&& headerIfNoneMatchPresentAndMatching) {
					// range request that doesn't match current etag
					// would return entire (different) file
					// respond with not-modified
					res = new Response(Response.Status.NOT_MODIFIED, mime, "");

					if (!quiet)
						Log.d(TAG, "NOT_MODIFIED #3");
				} else {
					// supply the file

					boolean isHTML = mime == "text/html"
							|| mime == "application/xhtml+xml";

					byte[] data = null;
					ResourceInputStream is = null;
					synchronized (criticalSectionSynchronizedLock) {
						PackageResource packageResource = pckg
								.getResourceAtRelativePath(uri);

						if (isHTML) {
							data = packageResource.readDataFull();
						} else {
							is = (ResourceInputStream) packageResource
									.getInputStream(false);
						}

						int updatedContentLength = packageResource
								.getContentLength();
						if (updatedContentLength != contentLength) {
							Log.e(TAG, "UPDATED CONTENT LENGTH! "
									+ updatedContentLength + "<--"
									+ contentLength);
						}
					}

					if (isHTML) {
						if (contentLength != data.length) {
							Log.e(TAG, "CONTENT LENGTH! " + contentLength
									+ " != " + data.length);
							contentLength = data.length;
						}

						// byte[] data_ = new byte[data.length];
						// System.arraycopy(data,0,data_,0,data.length);

						byte[] data_ = dataPreProcessor.handle(data, mime, uri,
								item);
						if (data_ != null) {
							data = data_;
							contentLength = data.length;
						}

						res = new Response(Response.Status.OK, mime,
								new Response.NanoInputStream(
										new ByteArrayInputStream(data)));

					} else {
						ByteStreamInput bis = new ByteStreamInput(is, -1,
								(int) contentLength,
								criticalSectionSynchronizedLock);

						res = new Response(Response.Status.OK, mime, bis);
					}

					res.addHeader("Content-Length", "" + contentLength);

					if (!quiet)
						Log.d(TAG, "OK (FULL): " + contentLength
								+ (isHTML ? " [HTML] " : " [OTHER] ") + mime);
				}
			}

			if (res != null) {
				res.addHeader("ETag", etag);
				// Announce that the file server accepts partial content
				// requests
				res.addHeader("Accept-Ranges", "bytes");
			}
		}
		return res;
	}
}
