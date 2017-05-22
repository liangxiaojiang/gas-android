package net.safetone.rfid.lib.exception;

/**
 * UID长度错误 (主要指MIFARE Classic卡读写操作)
 * 
 * @author SafeTone
 *
 */
public class UidLengthException extends Exception {
	private static final long serialVersionUID = 1L;
	public UidLengthException() {}
	public UidLengthException(String message) {
		super(message);
	}
}