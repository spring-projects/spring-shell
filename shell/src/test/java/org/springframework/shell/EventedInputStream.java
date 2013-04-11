/*
 * Copyright 2011-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.lang.ShellException;

/**
 * Input stream class for simulating shell events
 * 
 * @author tushark
 */
public class EventedInputStream extends InputStream implements ShellEvent {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final int DEFAULT_SIZE = 1024;

    private ByteArrayInputStream bis = null;
    private int size = DEFAULT_SIZE;
    private StringBuilder buffer = new StringBuilder();
    private AtomicBoolean streamClosed = new AtomicBoolean(false);
    private AtomicInteger events = new AtomicInteger();
    // private CountDownLatch latch = null;
    private final Lock lock = new ReentrantLock();
    private final Condition eventsCompleted = lock.newCondition();
    private final Condition eventsArrived = lock.newCondition();
    private StringBuilder bufferFormdAfterReading = new StringBuilder();

    public EventedInputStream(int size) {
        this.size = size;
        // latch = new CountDownLatch(1);
    }

    public EventedInputStream() {
        this(DEFAULT_SIZE);
    }

    /**
     * Reads the next byte of data from this input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned.
     * <p>
     * This <code>read</code> method cannot block.
     * 
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream has been reached.
     */
    public int read() {
        lock.lock();
        try {
            logger.debug("Waiting for new events to arrive and complete events " + events.get());
            /*
             * boolean waitComplete = false; int k=0; while(!waitComplete){ try
             * { waitComplete = eventsCompleted.await(100,
             * TimeUnit.MILLISECONDS); Util.log("Wait cycle " + k++ + " ? " +
             * waitComplete); if(k%10==0) eventsCompleted.signal(); } catch
             * (InterruptedException e2) { e2.printStackTrace(); } }
             */
            while (events.get() == 0)
                try {
                    eventsCompleted.await();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }

            while (bis == null)
                try {
                    eventsCompleted.await();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }

            logger.debug("All events have been arrived =" + events.get());
            int charRead = bis.read();
            if (charRead == EOF) {
                if (!streamClosed.get()) {
                    logger.debug("All input characters have been consumed so wait again events = " + events.get());
                    /*
                     * try { latch.await(); } catch (InterruptedException e) {
                     * logger.error(e); e.printStackTrace(); throw new
                     * RuntimeException(e); }
                     */
                    try {
                        // eventsArrived.await();
                        eventsCompleted.await();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    logger.debug("Wait complete with new events = " + events.get());
                    if (events.get() > 0) {
                        charRead = bis.read();
                        logger.debug("Returning char " + charRead);
                        bufferFormdAfterReading.append((char) charRead);
                        return charRead;
                    } else if (events.get() == -1) {
                        return -1;
                    } else {
                        ShellException e = new ShellException("you should not reach here without any events !!!! Events " + events.get() + 
                                                                  " streamClsoed " + streamClosed.get());
                        logger.error(e.getMessage(), e);
                        throw e;
                    }
                } else
                    return EOF;
            } else {
                bufferFormdAfterReading.append((char) charRead);
                return charRead;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reads up to <code>len</code> bytes of data into an array of bytes from
     * this input stream. If <code>pos</code> equals <code>count</code>, then
     * <code>-1</code> is returned to indicate end of file. Otherwise, the
     * number <code>k</code> of bytes read is equal to the smaller of
     * <code>len</code> and <code>count-pos</code>. If <code>k</code> is
     * positive, then bytes <code>buf[pos]</code> through
     * <code>buf[pos+k-1]</code> are copied into <code>b[off]</code> through
     * <code>b[off+k-1]</code> in the manner performed by
     * <code>System.arraycopy</code>. The value <code>k</code> is added into
     * <code>pos</code> and <code>k</code> is returned.
     * <p>
     * This <code>read</code> method cannot block.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset in the destination array <code>b</code>
     * @param len
     *            the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of the
     *         stream has been reached.
     * @exception NullPointerException
     *                If <code>b</code> is <code>null</code>.
     * @exception IndexOutOfBoundsException
     *                If <code>off</code> is negative, <code>len</code> is
     *                negative, or <code>len</code> is greater than
     *                <code>b.length - off</code>
     */
    public int read(byte b[], int off, int len) {
        lock.lock();
        try {
            check();
            return bis.read(b, off, len);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Skips <code>n</code> bytes of input from this input stream. Fewer bytes
     * might be skipped if the end of the input stream is reached. The actual
     * number <code>k</code> of bytes to be skipped is equal to the smaller of
     * <code>n</code> and <code>count-pos</code>. The value <code>k</code> is
     * added into <code>pos</code> and <code>k</code> is returned.
     * 
     * @param n
     *            the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     */
    public long skip(long n) {
        lock.lock();
        try {
            check();
            return bis.skip(n);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the number of remaining bytes that can be read (or skipped over)
     * from this input stream.
     * <p>
     * The value returned is <code>count&nbsp;- pos</code>, which is the number
     * of bytes remaining to be read from the input buffer.
     * 
     * @return the number of remaining bytes that can be read (or skipped over)
     *         from this input stream without blocking.
     */
    public int available() {
        lock.lock();
        try {
            check();
            return bis.available();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Tests if this <code>InputStream</code> supports mark/reset. The
     * <code>markSupported</code> method of <code>ByteArrayInputStream</code>
     * always returns <code>true</code>.
     * 
     * @since JDK1.1
     */
    public boolean markSupported() {
        lock.lock();
        try {
            check();
            return bis.markSupported();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set the current marked position in the stream. ByteArrayInputStream
     * objects are marked at position zero by default when constructed. They may
     * be marked at another position within the buffer by this method.
     * <p>
     * If no mark has been set, then the value of the mark is the offset passed
     * to the constructor (or 0 if the offset was not supplied).
     * 
     * <p>
     * Note: The <code>readAheadLimit</code> for this class has no meaning.
     * 
     * @since JDK1.1
     */
    public void mark(int readAheadLimit) {
        lock.lock();
        try {
            check();
            bis.mark(readAheadLimit);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resets the buffer to the marked position. The marked position is 0 unless
     * another position was marked or an offset was specified in the
     * constructor.
     */
    public void reset() {
        lock.lock();
        try {
            // pos = mark;
            check();
            bis.reset();
        } finally {
            lock.unlock();
        }
    }

    private void check() {
        if (bis == null) {
            if (streamClosed.get())
                return;
            try {
                throw new ShellException();
            } catch (Exception e) {
                logger.debug("End not called ");
            }
            throw new ShellException("Event End not marked use end() method to end events before calling InputStream methods.");
        }
    }

    /**
     * Closing a <tt>ByteArrayInputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     * <p>
     */
    public void close() throws IOException {
        lock.lock();
        try {
            if (bis != null)
                bis.close();
            streamClosed.set(false);
        } finally {
            lock.unlock();
        }
        // notifyAll();
    }

    public ShellEvent tab() throws IOException {
        lock.lock();
        try {
            buffer.append((char) TAB);
            events.incrementAndGet();
            eventsArrived.signalAll();
            return this;
        } finally {
            // notifyAll();
            lock.unlock();
        }
    }

    public ShellEvent addChars(String seq) throws IOException {
        lock.lock();
        try {
            buffer.append(seq);
            events.incrementAndGet();
            eventsArrived.signalAll();
            return this;
        } finally {
            // notifyAll();
            lock.unlock();
        }
    }

    public ShellEvent addCtrlZ() throws IOException {
        try {
            // TODO Auto-generated method stub
            events.incrementAndGet();
            return this;
        } finally {
            // notifyAll();
        }
    }

    public ShellEvent addCtrlD() throws IOException {
        try {
            // TODO Auto-generated method stub
            events.incrementAndGet();
            return this;
        } finally {
            // notifyAll();
        }
    }

    public ShellEvent newline() throws IOException {
        lock.lock();
        try {
            buffer.append(NEW_LINE);
            byte array[] = buffer.toString().getBytes();
            bis = new ByteArrayInputStream(array);
            logger.debug("End of Events due to ENTER ... with buffer " + buffer);
            // latch.countDown();
            eventsCompleted.signalAll();
            logger.debug("Signaled shell to read next character");
            return this;
        } finally {
            // notifyAll();
            lock.unlock();
        }
    }

    public ShellEvent end() {
        lock.lock();
        try {
            // buffer.append(COMMAND_SEP);
            byte array[] = buffer.toString().getBytes();
            bis = new ByteArrayInputStream(array);
            logger.debug("End of Events due to ENTER ... with buffer " + buffer);
            // latch.countDown();
            eventsCompleted.signalAll();
            logger.debug("Signaled shell to read next character");
            return this;
        } finally {
            // notifyAll();
            lock.unlock();
        }
    }

    public void clearEvents() {
        lock.lock();
        try {
            // clear buffer
            buffer = null;
            buffer = new StringBuilder();
            // latch = new CountDownLatch(1);
            bis = null;
            bufferFormdAfterReading = new StringBuilder();
            events.set(0);
        } finally {
            // notifyAll();
            lock.unlock();
        }
    }

    public StringBuilder getBufferFormdAfterReading() {
        return bufferFormdAfterReading;
    }

    public void waitForOutput() {

    }

    public void terminate() {
        events.set(0);
        streamClosed.set(true);
    }

    public void eof() {
        lock.lock();
        try {
            buffer.append(EOF);
            events.set(-1);
            byte array[] = buffer.toString().getBytes();
            bis = new ByteArrayInputStream(array);
            streamClosed.set(true);
            // latch.countDown();
            eventsCompleted.signalAll();
        } finally {
            logger.debug("Buffer EOF notifying threads ");
            // notifyAll();
            lock.unlock();
        }
    }

}
