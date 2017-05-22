package net.safetone.rfid.lib.exception;

/**
 * 未指定设备
 * 
 * @author SafeTone
 *
 */
public class NoDeviceException extends Exception {
	private static final long serialVersionUID = 1L;
	public NoDeviceException() {}
	public NoDeviceException(String message) {
		super(message);
	}
}
