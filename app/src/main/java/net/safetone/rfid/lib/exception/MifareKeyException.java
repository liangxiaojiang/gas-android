package net.safetone.rfid.lib.exception;

/**
 * 所指定的MIFARE卡的密钥错误（长度！= 6 bytes）
 * 
 * @author SafeTone
 *
 */
public class MifareKeyException extends Exception {
	private static final long serialVersionUID = 1L;
	public MifareKeyException() {}
	public MifareKeyException(String message) {
		super(message);
	}
}