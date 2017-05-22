package net.safetone.rfid.lib;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.safetone.rfid.lib.exception.*;
import net.safetone.rfid.utils.CRC;
import net.safetone.rfid.utils.Utils;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.text.TextUtils;

/**
 * RFID 读卡器
 * <p>
 * 使用说明请移步 {@link RfidReader}。
 * <p>
 * <b><i>注意：在较长时间不使用时，应使用 {@link #standby()} 让读卡器进入低功耗休眠模式。
 *    另外，读卡器固件在1小时内无任何操作，会自动关闭RF；再过10分钟后进入休眠模式。</i></b>
 * 
 * @author SafeTone
 *
 * @hide
 */
public class RfidReaderSrrm extends RfidReader {
	
	private static final byte STX = 0x02;
	private static final byte ETX = 0x03;
	
	private static final byte PROTOCOL_ISO14443A = 0x01;
	private static final byte PROTOCOL_ISO14443B = 0x02;
	private static final byte PROTOCOL_ISO15693 = 0x03;
	private static final byte PROTOCOL_FELICA = 0x04;
	
	private static final byte CMD_HELLO = (byte) 0xFF;
	private static final byte CMD_VERSION = (byte) 0xFE;
	private static final byte CMD_FIND_TAG = 0x70;
	private static final byte CMD_RF_ON_OFF = (byte) 0x90;
	private static final byte CMD_STANDBY = (byte) 0x91;
	private static final byte CMD_MIFARE = 0x05;
	private static final byte MIFARE_READ_BLOCK = 0x30;
	private static final byte MIFARE_WRITE_BLOCK = (byte) 0xA0;
	
	private static final byte ERR_UNSUPPORTED = 0x01;
	private static final byte ERR_PARAMETER = 0x02;
	private static final byte ERR_TIMEOUT = 0x03;
	private static final byte ERR_COLLISION = 0x04;
	private static final byte ERR_PARITY = 0x05;
	private static final byte ERR_CRC = 0x06;
	private static final byte ERR_BCC = 0x07;
	private static final byte ERR_NAK = 0x08;
	private static final byte ERR_INVALID_TAG = 0x09;
	private static final byte ERR_SELECT = 0x0A;
	private static final byte ERR_AUTH = 0x0B;
	private static final byte ERR_LENGTH = 0x0C;
	private static final byte ERR_UNKNOWN = (byte) 0xFF;
	
	/** 读卡器固件信息。 */
	public static final String READER_INFO_LEGACY = "SafeTone Bluetooth RFID Reader";
	/** 读卡器固件信息。 */
	public static final String READER_INFO = "SafeTone RFID Reader";

	private int vno = -1;
	
	/**
	 * 构造方法
	 * 
	 * @param context android application Context
	 * @throws IOException 
	 * @throws NoDeviceException 
	 * 
	 * @see #reader(Context, BluetoothSocket)
	 */
	protected RfidReaderSrrm(Context context) throws NoDeviceException, IOException,
			InvalidDeviceException {
		super(context);
		
		if (mComm == null) {
			mComm = Comm.getInstance(context);
		}
		
		readerAuthConnection();
		
		// get version no
		try {
			String vstr = getFirmwareVersion();
			if (!TextUtils.isEmpty(vstr))
				vno = Integer.valueOf(vstr.substring(18, 20));
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (RfidCommandException e) {
			e.printStackTrace();
		}
	}
	
	private void readerAuthConnection() throws IOException,
			InvalidDeviceException {
		String rsp;
		
		try {
			rsp = hello();
		} catch (TimeoutException e) {
			throw new InvalidDeviceException();
		} catch (RfidCommandException e) {
			throw new InvalidDeviceException();
		}
		
		if (!rsp.equals(READER_INFO) && !rsp.equals(READER_INFO_LEGACY)) {
			throw new InvalidDeviceException();
		}
	}
	
	@Override
	protected void transmit(byte[] data, int offset, int length)
			throws IOException {
		mComm.skipDirtyData();
		send(data, offset, length);
	}
	
	@Override
	protected byte[] receive(int timeout) throws IOException, TimeoutException,
			RfidCommandException {

		byte[] result = recv(timeout);
		
		if (result == null || result.length < 1)
			return null;
		
		switch (result[0]) {
		case 0:
			break;
		case ERR_UNSUPPORTED:
			throw new RfidCommandException(result[0], "Unsupported command");
		case ERR_PARAMETER:
			throw new RfidCommandException(result[0], "Parameter");
		case ERR_TIMEOUT:
			throw new RfidCommandException(result[0], "Timeout");
		case ERR_COLLISION:
			throw new RfidCommandException(result[0], "Collision");
		case ERR_PARITY:
			throw new RfidCommandException(result[0], "Parity");
		case ERR_CRC:
			throw new RfidCommandException(result[0], "CRC");
		case ERR_BCC:
			throw new RfidCommandException(result[0], "BCC");
		case ERR_NAK:
			throw new RfidCommandException(result[0], "Negative acknowledgment");
		case ERR_INVALID_TAG:
			throw new RfidCommandException(result[0], "Invalid tag");
		case ERR_SELECT:
			throw new RfidCommandException(result[0], "Select tag failed");
		case ERR_AUTH:
			throw new RfidCommandException(result[0], "Authentication");
		case ERR_LENGTH:
			throw new RfidCommandException(result[0], "Data length error");
		case ERR_UNKNOWN:
		default:
			throw new RfidCommandException(result[0], "Unknown");
		}
		
		byte[] ret = new byte[result.length - 1];
		System.arraycopy(result, 1, ret, 0, ret.length);
		
		return ret;
	}
	
	private void send(byte[] data, int offset, int length) throws IOException {
		byte[] pakage = new byte[length + 4];

		pakage[0] = STX;
		pakage[1] = (byte)((length >>> 8) & 0xFF);
		pakage[2] = (byte)(length & 0xFF);
		System.arraycopy(data, offset, pakage, 3, length);
		pakage[length + 3] = ETX;
		
		mComm.getOutputStream().write(pakage);
	}
	
	private void timedFullRead(byte[] buf, int offset, int len, long absTimeout)
			throws IOException, TimeoutException {
		
		while (len > 0) {
			/* wait available data */
			while (mComm.getInputStream().available() == 0) {
				if (System.currentTimeMillis() >= absTimeout)
					throw new TimeoutException();
				try { Thread.sleep(1); } catch (InterruptedException e) {}
				continue;
			}
			int n = mComm.getInputStream().read(buf, offset, len);
			if (n < 0)
				throw new IOException("read return -1");
			offset += n;
			len -= n;
		}
	}
	
	private byte[] recv(int timeout) throws IOException, TimeoutException {
		byte[] buf = new byte[512];
		byte[] ret = null;
		long outtime = System.currentTimeMillis() + timeout + mComm.baseTimeout;
		int len = 0;

		while (true) {
			/* STX */
			timedFullRead(buf, 0, 1, outtime);
			if (buf[0] != STX)
				continue;
			/* Package length */
			timedFullRead(buf, 0, 2, outtime);
			if (buf[0] > 0x04)
				continue;
			if (buf[0] == 0x00 && buf[1] == 0x00)
				continue;
			len = (buf[0] << 8) | buf[1];
			/* data and ETX*/
			timedFullRead(buf, 0, len + 1, outtime);
			if (buf[len] != ETX)
				continue;
			
			break;
		}
		
		if (len == 0)
			return null;
		
		ret = new byte[len];
		System.arraycopy(buf, 0, ret, 0, ret.length);

		return ret;
	}
	
	@Override
	public String hello() throws IOException, TimeoutException,
			RfidCommandException {
		
		byte[] cmd = new byte[] { CMD_HELLO };
		byte[] ret = null;
		
		try {
			ret = transceive(cmd, 10);
		} catch (NotImplementedException e) {
		}
		
		return new String(ret);
	}
	
	@Override
	public String getFirmwareVersion() throws IOException, TimeoutException,
			RfidCommandException {
		byte[] cmd = new byte[] { CMD_VERSION };
		byte[] ret = null;
		
		try {
			ret = transceive(cmd, 10);
		} catch (NotImplementedException e) {
		}
		
		return new String(ret);
	}
	
	@Override
	public String findTag() throws IOException, TimeoutException,
			RfidCommandException {
		String tag = "";
		byte[] cmd = new byte[] { CMD_FIND_TAG };
		byte[] resp = null;
		
		try {
			resp = transceive(cmd, 100);
		} catch (NotImplementedException e) {
		}
		
		if (vno >= 4) {
			if (resp != null && resp.length > 3) {
				int i = 0;
				int p;
				while (i < resp.length) {
					String uid = Utils.rawToHexString(resp, i + 2, resp[i + 1]);
					p = i;
					i += 2 + resp[i + 1];
					switch (resp[p]) {
					case PROTOCOL_ISO14443A:
						uid = "ISO14443 type A: " + uid;
						break;
					case PROTOCOL_ISO15693:
						uid = "ISO15693: " + uid;
						break;
					case PROTOCOL_ISO14443B:
					case PROTOCOL_FELICA:
					default:
						break;
					}

					tag += uid + '\n';
				}
			}
		} else {
			if (resp != null) {
				int i = 0;
				while (i < resp.length) {
					String uid;
					switch (resp[i]) {
					case PROTOCOL_ISO14443A:
						uid = Utils.rawToHexString(resp, i + 1, 4);
						tag += "ISO14443 type A: " + uid + '\n';
						i += 6;
						break;
					default:
						return "";
					}
				}
			}
		}

		return tag.trim();
	}
	
	@Override
	public void turnRfOn() throws IOException, TimeoutException,
			RfidCommandException {
		byte[] cmd = new byte[] { CMD_RF_ON_OFF, 0x01 };
		try {
			transceive(cmd, 10);
		} catch (NotImplementedException e) {
		}
	}

	@Override
	public void turnRfOff() throws IOException, TimeoutException,
			RfidCommandException {
		byte[] cmd = new byte[] { CMD_RF_ON_OFF, 0x00 };
		try {
			transceive(cmd, 10);
		} catch (NotImplementedException e) {
		}
	}
	
	@Override
	public void standby() throws IOException, TimeoutException,
			RfidCommandException {
		byte[] cmd = new byte[] { CMD_STANDBY };
		try {
			transceive(cmd, 10);
		} catch (NotImplementedException e) {
		}
	}
	
	@Override
	public boolean protocolToIso14443a(boolean turnRfOn) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[] {0x00, PROTOCOL_ISO14443A, 0x08};
		byte[] ret = null;
		
		if (turnRfOn)
			cmd[2] |= 0x20;
		
		ret = transceive(cmd, 50);
		
		return ret != null && ret.length == 0;
	}
	
	@Override
	public byte[] iso14443aReqa() throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[] {PROTOCOL_ISO14443A, 0x26};
		
		return transceive(cmd, 10);
	}

	@Override
	public byte[] iso14443aWupa() throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[] {PROTOCOL_ISO14443A, 0x52};
		
		return transceive(cmd, 10);
	}

	@Override
	public byte[] iso14443aHlta() throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[] {PROTOCOL_ISO14443A, 0x50, 0x00};

		return transceive(cmd, 5);
	}

	@Override
	public byte[] iso14443aSelect(int cascadeLevel, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
				
		cmd[i++] = PROTOCOL_ISO14443A;
		cmd[i++] = (byte) (0x93 + cascadeLevel * 2);
		cmd[i++] = (byte) ((((uid == null ? 0 : uid.length) + 2) << 4) & 0xFF);
		if (uid != null)
			for (int x = 0; x < uid.length; ++x)
				cmd[i++] = uid[x];
		
		return transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso14443aAnticollision() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[] {PROTOCOL_ISO14443A, (byte) 0xFE};
		
		return transceive(cmd, 200);
	}

	@Override
	public byte[] iso14443aRats(int cid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[] {PROTOCOL_ISO14443A, (byte) 0xE0, 0x00};
		
		cmd[2] |= cid & 0x0F;
		
		return transceive(cmd, 50);
	}

	@Override
	public boolean protocolToMifareClassic(boolean turnRfOn)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		return protocolToIso14443a(turnRfOn);
	}
	
	@Override
	public byte[] mifareClassicReadBlock(byte[] uid, int blockNo,
			MifareKeyType keyType, byte[] key) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, UidLengthException, NotImplementedException {
		
		if (uid != null && uid.length != 4 && uid.length != 7 && uid.length != 10)
			throw new UidLengthException("UID length is " + uid.length);
		
		if (blockNo < 0 || blockNo > 0xFF) {
			String errstr;
			if (blockNo < 0)
				errstr = "Block number too small";
			else 
				errstr = "Block number too large";
			throw new BlockNumberException(errstr);
		}
		if (key.length != 6) {
			throw new MifareKeyException("The key must 6 bytes");
		}
		
		byte[] cmd = new byte[40];
		int i = 0;
		
		cmd[i++] = CMD_MIFARE;
		cmd[i++] = MIFARE_READ_BLOCK;
		cmd[i++] = (byte) blockNo;
		cmd[i++] = (byte) keyType.toInteger();
		System.arraycopy(key, 0, cmd, i, 6);
		i += 6;
		if (uid != null) {
			System.arraycopy(uid, 0, cmd, i, uid.length);
			i += uid.length;
		}
		
		return transceive(cmd, 0, i, 200);
	}
	
	@Override
	public byte[] mifareClassicWriteBlock(byte[] uid, int blockNo,
			MifareKeyType keyType, byte[] key, byte[] data) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, UidLengthException, NotImplementedException {
		
		if (uid != null && uid.length != 4 && uid.length != 7 && uid.length != 10)
			throw new UidLengthException("UID length is " + uid.length);
		
		if (blockNo < 0 || blockNo > 0xFF) {
			String errstr;
			if (blockNo < 0)
				errstr = "Block number too small";
			else 
				errstr = "Block number too large";
			throw new BlockNumberException(errstr);
		}
		if (key.length != 6) {
			throw new MifareKeyException("The key must 6 bytes");
		}
		
		byte[] cmd = new byte[60];
		int i = 0;
		
		cmd[i++] = CMD_MIFARE;
		cmd[i++] = MIFARE_WRITE_BLOCK;
		cmd[i++] = (byte) blockNo;
		cmd[i++] = (byte) keyType.toInteger();
		System.arraycopy(key, 0, cmd, i, 6);
		i += 6;
		System.arraycopy(data, 0, cmd, i, 16);
		i += 16;
		if (uid != null) {
			System.arraycopy(uid, 0, cmd, i, uid.length);
			i += uid.length;
		}
		
		return transceive(cmd, 0, i, 200);
	}
	
	@Override
	public boolean protocolToIso15693(boolean turnRfOn) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[] {0x00, PROTOCOL_ISO15693, 0x02};
		byte[] ret = null;
		
		if (turnRfOn)
			cmd[2] |= 0x20;
		
		ret = transceive(cmd, 50);
		
		return ret != null && ret.length == 0;
	}

	private byte[] iso15693Transceive(byte[] cmd, int offset, int length,
			int timeout) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		
		byte[] result = transceive(cmd, offset, length, timeout);
		if (result == null || result.length <= 2) {
			return result;
		}
		
		if (!CRC.iso15693Check(result))
			throw new RfidCommandException(result[0], "CRC");
		
		byte[] ret = new byte[result.length - 2];
		System.arraycopy(result, 0, ret, 0, ret.length);
		
		return ret;
	}
	
	@Override
	public byte[] iso15693Inventory(int flags, int afi) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[10];
		int i = 0;
		
		flags |= 0x20; // 目前的固件还不支持16个时隙。
		flags |= 0x04; // 保证 Inventory_flag 被设置。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		if ((flags & 0x10) == 0x10)
			cmd[i++] = (byte) afi;
		cmd[i++] = 0x01;
		cmd[i++] = 0x00;

		return iso15693Transceive(cmd, 0, i, 100);
	}

	@Override
	public byte[] iso15693StayQuiet(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x02;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];

		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693ReadSingleBlock(int flags, int block, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x20;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) block;
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693WriteSingleBlock(int flags, int block, byte[] data,
			byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[128];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x21;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) block;
		for (int x = 0; x < data.length; ++x)
			cmd[i++] = data[x];
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693LockBlock(int flags, int block, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x22;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) block;
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693ReadMultiBlocks(int flags, int first, int num,
			byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x23;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) first;
		cmd[i++] = (byte) num;
		
		return iso15693Transceive(cmd, 0, i, 50 + 5 * num);
	}

	@Override
	public byte[] iso15693Select(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x25;
		for (int x = 0; x < 8; ++x)
			cmd[i++] = uid[x];
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693ResetToReady(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x26;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693WriteAfi(int flags, int afi, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x27;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) afi;
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693LockAfi(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x28;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693WriteDsfid(int flags, int dsfid, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x29;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) dsfid;
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693LockDsfid(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x2A;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693GetSystemInformation(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x2B;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693GetMultiBlockSecurityStatus(int flags, int first,
			int num, byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags &= ~0x04; // 保证 Inventory_flag 被清除。
		
		cmd[i++] = PROTOCOL_ISO15693;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x2C;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) first;
		cmd[i++] = (byte) num;
		
		return iso15693Transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] rawCommand(byte[] cmd) throws IOException {
		mComm.getOutputStream().write(cmd);
		long outtime = System.currentTimeMillis() + 1000;
		byte[] buf = new byte[2048];
		int n = 0;

		while (System.currentTimeMillis() < outtime) {
			try { Thread.sleep(1); } catch (InterruptedException e) {}
			continue;
		}
		n = mComm.getInputStream().read(buf);
		
		byte[] ret = new byte[n];
		System.arraycopy(buf, 0, ret, 0, ret.length);
		return ret;
	}
}
