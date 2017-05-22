package net.safetone.rfid.lib;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.safetone.rfid.lib.exception.*;
import net.safetone.rfid.utils.Utils;
import android.content.Context;
import android.text.TextUtils;

/**
 * RFID 读卡器
 * <p>
 * 适用于是老固件通信格式。
 * <p>
 * 使用说明请移步 {@link RfidReader}。
 * <p>
 * <b><i>注意：在较长时间不使用时，应使用 standby() 让读卡器进入低功耗休眠模式。</i></b>
 * 
 * @author SafeTone
 * 
 * @hide
 */
public class RfidReaderEvm extends RfidReader {
	
	/** 读卡器固件信息。 */
	public static final String READER_INFO = "Bluetooth RFID Reader";
	
	private static final String SOF = "01";
	private static final String EOF = "0000";
	private static final String READER_TYPE = "0304";
	
	private static final String FINISH_TAG = "FINISH\r\n";
	
	protected RfidReaderEvm(Context context) throws NoDeviceException,
			IOException, InvalidDeviceException {
		super(context);
		
		if (mComm == null) {
			mComm = Comm.getInstance(context);
		}
		
		readerAuthConnection();
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

		if (!rsp.equals(READER_INFO)) {
			throw new InvalidDeviceException();
		}
	}

	@Override
	protected void transmit(byte[] data, int offset, int length)
			throws IOException, NotImplementedException {
		transmit(Utils.rawToHexString(data, offset, length));
	}

	private void transmit(String data) throws IOException {
		mComm.skipDirtyData();
		data = READER_TYPE + data;
		send(data);
	}
	
	private void send(String data) throws IOException {
		String pakage;
		int len = data.length() / 2 + 5;
		
		pakage = String.format("%s%02X%02X%s%s", SOF,
				len & 0xFF, (len >>> 8) & 0xFF,
				data, EOF);
		
		mComm.getOutputStream().write(pakage.getBytes());
	}
	
	@Override
	protected byte[] receive(int timeout) throws IOException,
			TimeoutException, RfidCommandException {
		
		String res = recv(true, timeout);
		
		if (TextUtils.isEmpty(res)
				|| TextUtils.isEmpty(res = res.substring(res.indexOf('[') + 1,
						res.indexOf(']'))))
			throw new RfidCommandException(3, "Timeout");
		
		return Utils.HexStringToRaw(res);
	}
	
	private String receive(boolean withFinish, int timeout) throws IOException,
			TimeoutException, RfidCommandException {
		String result;
		result = recv(withFinish, timeout);
		
		if (withFinish && !result.contains(FINISH_TAG))
			throw new RfidCommandException(3, "Timeout");
		
		return result.replace(FINISH_TAG, "").trim();
	}
	
	private String recv(boolean withFinish, int timeout) throws IOException,
			TimeoutException {
		
		long outtime = System.currentTimeMillis() + timeout + mComm.baseTimeout;
		
		byte[] buffer = new byte[512];
		int rn = 0;
		int total = 0;
		String res;
		
		do {
			do {
				if (mComm.getInputStream().available() == 0) {
					if (System.currentTimeMillis() >= outtime)
						throw new TimeoutException();
					try { Thread.sleep(1); } catch (InterruptedException e) {}
					continue;
				}
				rn = mComm.getInputStream().read(buffer, total, 1);
				total += rn;
			} while (total == 0 || buffer[total - 1] != '\n');

			res = new String(buffer, 0, total);

			if (withFinish && res.contains(FINISH_TAG))
				break;
		} while (withFinish);
		
		return res;
	}
	
	@Override
	public String hello() throws IOException, TimeoutException,
			RfidCommandException {
		transmit("FF01");
		return receive(false, 10);
	}

	@Override
	public String getFirmwareVersion() throws IOException, TimeoutException,
			RfidCommandException {
		transmit("FE");
		return receive(false, 10);
	}

	@Override
	public String findTag() throws IOException, TimeoutException,
			RfidCommandException {
		String ret;
		int idx;
		
		transmit("02");
		ret = receive(true, 500);
		
		ret = ret.replace("[", "");
		ret = ret.replaceAll(",\\p{XDigit}{2}]", "").trim();
		
		if (TextUtils.isEmpty(ret))
			throw new RfidCommandException(3, "Timeout");
		
		if ((idx = ret.indexOf("ISO14443 type A: ")) >= 0) {
			idx += "ISO14443 type A: ".length() + 8;
			ret = ret.substring(0, idx);
		}
		
		return ret;
	}

	@Override
	public void turnRfOn() throws IOException, TimeoutException,
			RfidCommandException {
		String ret;
		
		transmit("01FF");
		ret = receive(true, 50);
		
		if (!TextUtils.isEmpty(ret))
			throw new RfidCommandException(-1, "Unknown");
	}

	@Override
	public void turnRfOff() throws IOException, TimeoutException,
			RfidCommandException {
		String ret;
		
		transmit("0100");
		ret = receive(true, 50);
		
		if (!TextUtils.isEmpty(ret))
			throw new RfidCommandException(-1, "Unknown");
	}

	@Override
	public void standby() throws IOException, TimeoutException,
			RfidCommandException {
		String ret;
		
		transmit("00");
		ret = receive(true, 50);
		
		if (!TextUtils.isEmpty(ret))
			throw new RfidCommandException(-1, "Unknown");
	}

	@Override
	public boolean protocolToIso14443a(boolean turnRfOn) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] iso14443aReqa() throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] iso14443aWupa() throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] iso14443aHlta() throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] iso14443aSelect(int cascadeLevel, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] iso14443aAnticollision() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] iso14443aRats(int cid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public boolean protocolToMifareClassic(boolean turnRfOn)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] mifareClassicReadBlock(byte[] uid, int blockNo,
			MifareKeyType keyType, byte[] key) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, UidLengthException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public byte[] mifareClassicWriteBlock(byte[] uid, int blockNo,
			MifareKeyType keyType, byte[] key, byte[] data) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, UidLengthException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public boolean protocolToIso15693(boolean turnRfOn) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		String ret;
		
		transmit("0402");
		ret = receive(true, 50);
		if (!TextUtils.isEmpty(ret))
			return false;
		
		if (turnRfOn)
			turnRfOn();
		
		return true;
	}

	@Override
	public byte[] iso15693Inventory(int flags, int afi) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[10];
		int i = 0;
		String result;
		
		flags &= ~0x01; // 单副载波
		flags |= 0x06; // 高数据速率 + Inventory_flag
	
		cmd[i++] = 0x14;
		cmd[i++] = (byte) flags;
		if ((flags & 0x10) == 0x10)
			cmd[i++] = (byte) afi;
		cmd[i++] = 0x00;
		
		transmit(cmd, 0, i);
		result = receive(true, 200);
		if (TextUtils.isEmpty(result)
				|| TextUtils.isEmpty(result = result.substring(
						result.indexOf('[') + 1, result.indexOf(']'))))
			throw new RfidCommandException(3, "Timeout");
		
		return Utils.HexStringToRaw(result.substring(0, result.indexOf(',')));
	}

	@Override
	public byte[] iso15693StayQuiet(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x02;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693ReadSingleBlock(int flags, int block, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x20;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) block;
		
		return transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693WriteSingleBlock(int flags, int block, byte[] data,
			byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		
		byte[] cmd = new byte[40];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x21;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) block;
		for (int x = 0; x < data.length; ++x)
			cmd[i++] = data[x];
		
		return transceive(cmd, 0, i, 80);
	}

	@Override
	public byte[] iso15693LockBlock(int flags, int block, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x22;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) block;
		
		return transceive(cmd, 0, i, 80);
	}

	@Override
	public byte[] iso15693ReadMultiBlocks(int flags, int first, int num,
			byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x23;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) first;
		cmd[i++] = (byte) num;
		
		return transceive(cmd, 0, i, 50 + 5 * num);
	}

	@Override
	public byte[] iso15693Select(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x37) | 0x22; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x25;
		for (int x = 0; x < 8; ++x)
			cmd[i++] = uid[x];
		
		return transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693ResetToReady(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x26;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693WriteAfi(int flags, int afi, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x27;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) afi;
		
		return transceive(cmd, 0, i, 80);
	}

	@Override
	public byte[] iso15693LockAfi(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x28;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return transceive(cmd, 0, i, 80);
	}

	@Override
	public byte[] iso15693WriteDsfid(int flags, int dsfid, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x29;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) dsfid;
		
		return transceive(cmd, 0, i, 80);
	}

	@Override
	public byte[] iso15693LockDsfid(int flags, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x2A;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return transceive(cmd, 0, i, 80);
	}

	@Override
	public byte[] iso15693GetSystemInformation(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x2B;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		
		return transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] iso15693GetMultiBlockSecurityStatus(int flags, int first,
			int num, byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		
		byte[] cmd = new byte[20];
		int i = 0;
		
		flags = (flags & ~0x07) | 0x02; // 单副载波, 高数据速率, 清除Inventory_flag
		
		cmd[i++] = 0x18;
		cmd[i++] = (byte) flags;
		cmd[i++] = 0x2C;
		if ((flags & 0x20) == 0x20)
			for (int x = 0; x < 8; ++x)
				cmd[i++] = uid[x];
		cmd[i++] = (byte) first;
		cmd[i++] = (byte) num;
		
		return transceive(cmd, 0, i, 50);
	}

	@Override
	public byte[] rawCommand(byte[] cmd) throws IOException, TimeoutException {
		mComm.getOutputStream().write(cmd);
		long outtime = System.currentTimeMillis() + 3000;
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
