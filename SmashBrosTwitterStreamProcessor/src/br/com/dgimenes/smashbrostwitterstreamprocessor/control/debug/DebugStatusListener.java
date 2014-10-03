/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Daniel Gimenes
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
 */
package br.com.dgimenes.smashbrostwitterstreamprocessor.control.debug;

import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Logger;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class DebugStatusListener implements StatusListener {

	@Override
	public void onException(Exception exception) {
		Logger.warn("onException: " + exception, DebugStatusListener.class);
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice notice) {
		Logger.warn("onDeletionNotice: " + notice, DebugStatusListener.class);
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		Logger.warn("onScrubGeo: " + arg0 + ", " + arg1, DebugStatusListener.class);
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		Logger.warn("onStallWarning: " + warning, DebugStatusListener.class);
	}

	@Override
	public void onStatus(Status status) {
		Logger.info("onStatus: " + status, DebugStatusListener.class);
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		Logger.warn("onTrackLimitationNotice: " + arg0, DebugStatusListener.class);
	}

}
