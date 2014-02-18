package org.parse4j.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.parse4j.callback.ProgressCallback;

public class CountingHttpEntity extends HttpEntityWrapper {

	private final ProgressCallback progressCallback;

	public CountingHttpEntity(final HttpEntity entity, ProgressCallback progressCallback) {
		super(entity);
		this.progressCallback = progressCallback;
	}
	
	@Override
    public void writeTo(final OutputStream out) throws IOException {
        this.wrappedEntity.writeTo(new CountingOutputStream(out, this.progressCallback, getContentLength()));
    }
	
	static class CountingOutputStream extends FilterOutputStream {

        private final ProgressCallback progressCallback;
        private long transferred;
        private long totalSize;

        CountingOutputStream(final OutputStream out, final ProgressCallback progressCallback, long totalSize) {
            super(out);
            this.progressCallback = progressCallback;
            this.transferred = 0;
            this.totalSize = totalSize;
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            //// NO, double-counting, as super.write(byte[], int, int) delegates to write(int).
            //super.write(b, off, len);
            out.write(b, off, len);
            this.transferred += len;
            notifyCallback();
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            this.transferred++;
            notifyCallback();
        }
        
        private void notifyCallback() {
        	int progressToReport = Math.round((float)this.transferred / (float)this.totalSize * 100.0F);
        	this.progressCallback.done(progressToReport);
        }

    }

}
