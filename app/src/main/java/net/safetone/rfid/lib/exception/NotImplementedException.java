package net.safetone.rfid.lib.exception;

/**
 * 方法未实现/或不支持
 * 
 * @author SafeTone
 *
 */
public class NotImplementedException extends Exception {
	private static final long serialVersionUID = 1L;
	public NotImplementedException() {}
	public NotImplementedException(String message) {
		super(message);
	}
}
