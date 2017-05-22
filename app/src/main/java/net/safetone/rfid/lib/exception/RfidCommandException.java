package net.safetone.rfid.lib.exception;

/**
 * RFID命令执行失败
 * 
 * @author SafeTone
 *
 */
public class RfidCommandException extends Exception {
	private static final long serialVersionUID = 1L;
	private int mErrno;
	
	public RfidCommandException(int errno) {
		mErrno = errno;
	}
	
	public RfidCommandException(int errno, String msg) {
		super(msg);
		mErrno = errno;
	}
	
	public int getErrno() {
		return mErrno;
	}
}
