package net.safetone.rfid.lib.exception;


/**
 * EEPROM 块号错误
 * <p>
 * 块号为非负数。
 * <p>
 * 对于MIFARE Classic来说，0 <= 块号 <= 64
 * 
 * @author SafeTone
 *
 */
public class BlockNumberException extends Exception {
	private static final long serialVersionUID = 1L;
	public BlockNumberException() {}
	public BlockNumberException(String message) {
		super(message);
	}
}
