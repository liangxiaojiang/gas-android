package net.safetone.rfid.lib;

public enum MifareKeyType {
	
	KeyA(0x60), KeyB(0x61);
	
	private int type;
	
	private MifareKeyType(int type) {  
        this.type = type;  
    }
	
	public int toInteger() {
		return type;
	}
	
	public static MifareKeyType valueOf(int type) {
		switch (type) {
		case 0x60:
			return KeyA;
		case 0x61:
			return KeyB;
		default:
			return null;
		}
	}
}