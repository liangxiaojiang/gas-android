package net.safetone.rfid.lib;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.safetone.rfid.lib.exception.*;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * RFID 读卡器抽象类
 * <p>
 * 使用 {@link RfidReader.getInstance(Context)} 取得读卡器实例。
 * <p>
 * <b>使用步骤:</b>
 * <p>
 * 1. 调用 {@link #getInstance(Context)} 方法取得读卡器实例。
 * <p>
 * 2. 调用 {@link #protocolToIso14443a()}/{@link #protocolToIso15693()}/
 * {@link #protocolToMifareClassic()} 方法将读卡器配置到对应的协议。
 * <p>
 * 3. 调用 {@link #turnRfOn()} 激活RF，以建立必要的射频场。 也可以使用
 * {@link #protocolToIso14443a(boolean)}/{@link #protocolToIso15693(boolean)}/
 * {@link #protocolToMifareClassic(boolean)} 方法在配置协议时就激活RF。
 * <p>
 * 4. 调用协议相关的方法来执行所需功能。 例如
 * {@link #mifareClassicReadBlock(int, MifareKeyType, byte[])} 读取 MIFARE Classic
 * 标签内的块数据
 * <p>
 * 5. 使用完毕后，尽早调用 {@link #turnRfOff()} 方法关闭射频以节省电量。
 * <p>
 * 6. 长时间不适用应调用 {@link #standby()} 让读卡器进入休眠状态，以进一步节省电量。
 * <p>
 * <p>
 * <b><i>注意：在较长时间不使用时，应使用 standby() 让读卡器进入低功耗休眠模式。 </i></b>
 * 
 * @author SafeTone
 * 
 */
public abstract class RfidReader {

	private static RfidReader mInstance = null;
	/** Communication interface @hide */
	protected static Comm mComm = null;

	private Context mContext;
	
	/**
	 * 构造方法
	 * 
	 * @param context Android Application Context
	 * 
	 * @see #reader(Context, BluetoothSocket)
	 * @hide
	 */
	protected RfidReader(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	/**
	 * 获取读卡器实例。
	 * <p>
	 * 连接读卡器，并验证是否有效。
	 * 
	 * @param context Android Application Context
	 * @param dev 读卡器设备
	 * 
	 * @return RfidReader 实例
	 * 
	 * @throws NoDeviceException 无读卡器
	 * @throws IOException
	 * @throws InvalidDeviceException 无效设备
	 * 
	 * @see #getInstance(Context)
	 * @see #destroy()
	 */
	public static final RfidReader getInstance(Context context, String dev)
			throws NoDeviceException, IOException, InvalidDeviceException {

		try {
			mComm = Comm.getInstance(context, dev);
			mInstance = new RfidReaderSrrm(context);
		} catch (InvalidDeviceException e) {
			try {
				mInstance = new RfidReaderEvm(context);
			} catch (InvalidDeviceException ie) {
				mComm.close();
				mComm = null;
				throw ie;
			}
		}

		return mInstance;
	}

	/**
	 * 获取读卡器实例。
	 * <p>
	 * 等效于 getInstance(context, null)
	 * 
	 * @param context Android Application Context
	 * 
	 * @return RfidReader 实例
	 * 
	 * @throws NoDeviceException 无读卡器
	 * @throws IOException
	 * @throws InvalidDeviceException 无效设备
	 * 
	 * @see #getInstance(Context, String)
	 * @see #destroy()
	 */
	public static final RfidReader getInstance(Context context)
			throws NoDeviceException, IOException, InvalidDeviceException {
		return getInstance(context, null);
	}
	
	/**
	 * 让读卡器进入休眠并断开读卡器。
	 * 
	 * @see #getInstance(Context)
	 * @see #getInstance(Context, String)
	 */
	public void destroy() {
		try {
			standby();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (RfidCommandException e) {
			e.printStackTrace();
		} finally {
			try {
				mComm.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				mComm = null;
				mInstance = null;
			}
		}
	}
		
	/** 
	 * 向读卡器传输发送数据
	 * 
	 * @param data
	 * @param offset
	 * @param length
	 * 
	 * @throws IOException
	 * @throws NotImplementedException
	 * 
	 * @see #receive(int)
	 * @see #transceive(byte[], int, int, int)
	 */
	protected abstract void transmit(byte[] data, int offset, int length)
			throws IOException, NotImplementedException;

	/**
	 * 从读卡器接收数据
	 * 
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws RfidCommandException
	 * @throws NotImplementedException 
	 * 
	 * @see #transmit(byte[], int, int)
	 * @see #transceive(byte[], int, int, int)
	 */
	protected abstract byte[] receive(int timeout) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException;

	/**
	 * 向读卡器发送数据，然后从读卡器接收数据
	 * 
	 * @param data
	 * @param offset
	 * @param length
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws RfidCommandException
	 * @throws NotImplementedException 
	 * 
	 * @see #transceive(byte[], int)
	 * @see #transmit(byte[], int, int)
	 * @see #receive(int)
	 */
	protected byte[] transceive(byte[] data, int offset, int length, int timeout)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException {
		transmit(data, offset, length);
		return receive(timeout);
	}
	
	/**
	 * 向读卡器发送数据，然后从读卡器接收数据
	 * <p>
	 * 等价于 transceive(data, 0, data.length, timeout)
	 * 
	 * @param data
	 * @param timeout
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws RfidCommandException
	 * @throws NotImplementedException 
	 * 
	 * @see #transceive(byte[], int, int, int)
	 * @see #transmit(byte[], int, int)
	 * @see #receive(int)
	 */
	protected byte[] transceive(byte[] data, int timeout) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		return transceive(data, 0, data.length, timeout);
	}

	/**
	 * 向RFID读卡器发送hello命令，读卡器应返回相应的信息。
	 * 
	 * @return RFID Reader 信息
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws RfidCommandException
	 * 
	 * @see #getFirmwareVersion()
	 */
	public abstract String hello() throws IOException, TimeoutException,
			RfidCommandException;
	
	/**
	 * 获取RFID Reader的固件版本
	 * 
	 * @return 固件版本。形如 "Firmware Revision 02 from 2014-07-22"。
	 * 
	 * @throws IOException 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * 
	 * @see #hello()
	 */
	public abstract String getFirmwareVersion() throws IOException,
			TimeoutException, RfidCommandException;
	
	/**
	 * 扫描RFID读卡器附近的标签卡
	 * 
	 * @return 扫描结果。
	 *         未发现标签，返回 ""；
	 *         发现标签，返回类型和UID，形如 "ISO14443 type A: C4B373C8\nISO15693: BFC3A34E000104E0"。
	 *             ISO14443A 标签有4/7/10个字节，ISO15693 标签有8个字节，低字节在前。
	 *          
	 * @throws IOException 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 */
	public abstract String findTag() throws IOException, TimeoutException,
			RfidCommandException;
	
	/**
	 * 激活射频，以建立射频场。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * 
	 * @see #turnRfOff()
	 * @see #standby()
	 */
	public abstract void turnRfOn() throws IOException, TimeoutException,
			RfidCommandException;
	
	/**
	 * 关闭射频，以节省电量
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * 
	 * @see #turnRfOn()
	 * @see #standby()
	 */
	public abstract void turnRfOff() throws IOException, TimeoutException,
			RfidCommandException;
	
	/**
	 * 关闭RFID的射频，并让其进入待机模式
	 * 
	 * @throws IOException 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * 
	 * @see #turnRfOn()
	 * @see #turnRfOff()
	 */
	public abstract void standby() throws IOException, TimeoutException,
			RfidCommandException;
	
	/**
	 * 将读卡器设置为ISO14443 type A协议
	 * 
	 * @param turnRfOn 用于指示是否需要开启射频。
	 * 
	 * @return 结果
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToIso14443a()
	 * @see #protocolToMifareClassic()
	 * @see #protocolToIso15693()
	 */
	public abstract boolean protocolToIso14443a(boolean turnRfOn)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 将读卡器设置为ISO14443 type A协议
	 * <p>
	 * 仅仅将读卡器设置为ISO14443 type A协议，读卡器的射频并未激活。
	 * 您需要调用 {@link #turnRfOn()} 来激活射频。
	 * 
	 * @return 结果
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToIso14443a(boolean)
	 * @see #protocolToMifareClassic()
	 * @see #protocolToIso15693()
	 * @see #turnRfOn()
	 */
	public boolean protocolToIso14443a() throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException {
		return protocolToIso14443a(false);
	}
	
	/**
	 * 请求标签，将标签从IDLE状态激活到READY状态。
	 * 
	 * @return ATQA (Answer To Request)
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso14443aHlta()
	 * @see #iso14443aWupa()
	 */
	public abstract byte[] iso14443aReqa() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException;
	
	/**
	 * 唤醒标签，将标签从HALT状态唤醒到READY状态。
	 * 
	 * @return ATQA (Answer To Request)
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso14443aHlta()
	 * @see #iso14443aReqa()
	 */
	public abstract byte[] iso14443aWupa() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException;
	
	/**
	 * 休眠标签，将标签设置到HALT状态。
	 * <p>
	 * 该命正确执行后，不会返回任何数据，而直接抛出 RfidCommandException(3， "Timeout")。
	 * 其他情况表示该命令执行失败。
	 * 
	 * @return 通常应直接抛出 RfidCommandException。
	 * 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws RfidCommandException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso14443aReqa()
	 * @see #iso14443aWupa()
	 */
	public abstract byte[] iso14443aHlta() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException;
	
	/**
	 * 执行选择命令。
	 * 
	 * @param cascadeLevel UID串联级别，可用的值有0,1,2。
	 * @param uid uid值
	 * 
	 * @return 当UID长度为5字节(4字节UID(或1字节串联标记+3字节UID)+1字节BCC)时，返回SAK(包含2字节CRC)；
	 *         否则返回剩余UID数据(包括串联标记以及BCC)。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso14443aAnticollision()
	 */
	public abstract byte[] iso14443aSelect(int cascadeLevel, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 执行一次防冲突，并将标签置为ACTIVE状态。
	 * 
	 * @return UID(4/7/10字节)+SAK(1字节)。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso14443aSelect(int, byte[])
	 */
	public abstract byte[] iso14443aAnticollision() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException;
	
	/**
	 * 选择应答请求。(Request for answer to select)
	 * 
	 * @param cid 指定标签的逻辑号。
	 * 
	 * @return 选择应答(ATS)。详细信息请参考ISO14443A相关文档。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 */
	public abstract byte[] iso14443aRats(int cid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException;
	
	/**
	 * 将读卡器设置为MIFARE Classic协议
	 * 
	 * @param turnRfOn 用于指示是否需要开启射频。
	 * 
	 * @return 结果
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToIso14443a()
	 * @see #protocolToIso15693()
	 */
	public abstract boolean protocolToMifareClassic(boolean turnRfOn)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 将读卡器设置为MIFARE Classic协议
	 * <p>
	 * 仅仅将读卡器设置为MIFARE Classic协议，读卡器的射频并未激活。
	 * 您需要调用 {@link #turnRfOn()} 来激活射频。
	 * 
	 * @return 结果
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToIso14443a()
	 * @see #protocolToIso15693()
	 * @see #turnRfOn()
	 */
	public boolean protocolToMifareClassic() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		return protocolToMifareClassic(false);
	}
	
	/**
	 * 读取 MIFARE Classic 的 EEPROM。
	 * 
	 * @param uid 欲读取的标签UID(4/7/10字节)，
	 *        若填null，则读卡器试图自动选择一个，
	 *        若有多个可能会抛出冲突异常(RfidCommandException的message为Collision)。
	 * @param blockNo MIFARE EEPROM 的块号。
	 * @param keyType 用于认证的密钥的类型。
	 * @param key 用于认证的密钥。
	 * 
	 * @return 数据(16字节)+UID(4/7/10字节)。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws BlockNumberException 无效的块号
	 * @throws MifareKeyException  无效的秘钥(!=6 bytes)
	 * @throws UidLengthException UID 长度不对(应该为4/7/10字节)
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToMifareClassic(boolean)
	 * @see #mifareClassicWriteBlock(int, MifareKeyType, byte[], byte[])
	 */
	public abstract byte[] mifareClassicReadBlock(byte[] uid, int blockNo,
			MifareKeyType keyType, byte[] key) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, UidLengthException, NotImplementedException;
	
	/**
	 * 读取 MIFARE Classic 的 EEPROM。
	 * <p>
	 * 等价于 mifareClassicReadBlock(null, blockNo, keyType, key);
	 * 
	 * @param blockNo MIFARE EEPROM 的块号。
	 * @param keyType 用于认证的密钥的类型。
	 * @param key 用于认证的密钥。
	 * 
	 * @return 数据(16字节)+UID(4/7/10字节)。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws BlockNumberException 无效的块号
	 * @throws MifareKeyException  无效的秘钥(!=6 bytes)
	 * @throws NotImplementedException 
	 * 
	 * @see #mifareClassicReadBlock(byte[], int, MifareKeyType, byte[])
	 * @see #protocolToMifareClassic(boolean)
	 * @see #mifareClassicWriteBlock(int, MifareKeyType, byte[], byte[])
	 */
	public byte[] mifareClassicReadBlock(int blockNo,
			MifareKeyType keyType, byte[] key) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, NotImplementedException {
		try {
			return mifareClassicReadBlock(null, blockNo, keyType, key);
		} catch (UidLengthException e) {
			// 不会进入该代码块
			return null;
		}
	}
	
	/**
	 * 写入数据到 MIFARE Classic 的 EEPROM。
	 * 
	 * @param uid 欲写入的标签UID(4/7/10字节)，
	 *        若填null，则读卡器试图自动选择一个，
	 *        若有多个可能会抛出冲突异常(RfidCommandException的message为Collision)。
	 * @param blockNo MIFARE EEPROM 的块号。
	 * @param keyType 用于认证的密钥的类型。
	 * @param key 用于认证的密钥。
	 * @param data 欲写入的数据，必须为16字节。
	 * 
	 * @return 成功写入数据的标签UID(4/7/10字节)。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws BlockNumberException 无效块号
	 * @throws MifareKeyException 无效秘钥(!=6 bytes)
	 * @throws UidLengthException UID 长度不对(应该为4/7/10字节)
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToMifareClassic(boolean)
	 * @see #mifareClassicReadBlock(int, MifareKeyType, byte[])
	 */
	public abstract byte[] mifareClassicWriteBlock(byte[] uid, int blockNo,
			MifareKeyType keyType, byte[] key, byte[] data) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, UidLengthException, NotImplementedException;
	
	/**
	 * 写入数据到 MIFARE Classic 的 EEPROM。
	 * <p>
	 * 等价于 mifareClassicWriteBlock(null, blockNo, keyType, key, data)
	 * 
	 * @param blockNo MIFARE EEPROM 的块号。
	 * @param keyType 用于认证的密钥的类型。
	 * @param key 用于认证的密钥。
	 * @param data 欲写入的数据，必须为16字节。
	 * 
	 * @return 成功写入数据的标签UID(4/7/10字节)。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws BlockNumberException 无效块号
	 * @throws MifareKeyException 无效秘钥(!=6 bytes)
	 * @throws NotImplementedException 
	 * 
	 * @see #mifareClassicWriteBlock(byte[], int, MifareKeyType, byte[], byte[])
	 * @see #protocolToMifareClassic(boolean)
	 * @see #mifareClassicReadBlock(int, MifareKeyType, byte[])
	 */
	public byte[] mifareClassicWriteBlock(int blockNo,
			MifareKeyType keyType, byte[] key, byte[] data) throws IOException,
			TimeoutException, RfidCommandException, BlockNumberException,
			MifareKeyException, NotImplementedException {
		try {
			return mifareClassicWriteBlock(null, blockNo, keyType, key, data);
		} catch (UidLengthException e) {
			// 不会进入该代码块
			return null;
		}
	}

	/**
	 * 将读卡器设置为ISO15693协议
	 * 
	 * @param turnRfOn 用于指示是否需要开启射频。
	 * 
	 * @return 结果
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws RfidCommandException
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToIso15693(boolean)
	 * @see #protocolToIso14443a()
	 * @see #protocolToMifareClassic()
	 */
	public abstract boolean protocolToIso15693(boolean turnRfOn)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 将读卡器设置为ISO15693协议
	 * <p>
	 * 仅仅将读卡器设置为ISO15693协议，读卡器的射频并未激活。您需要调用 {@link #turnRfOn()}
	 * 来激活射频。
	 * 
	 * @return 结果
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException
	 * @throws NotImplementedException 
	 * 
	 * @see #protocolToIso15693(boolean)
	 * @see #protocolToIso14443a()
	 * @see #protocolToMifareClassic()
	 * @see #turnRfOn()
	 */
	public boolean protocolToIso15693() throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException {
		return protocolToIso15693(false);
	}
	
	/**
	 * 清点ISO15693标签
	 * 
	 * @param flags 请求标志
	 * @param afi 若flags中AFI_flag位被设置，该参数应填为预期的AFI值。
	 * 
	 * @return 去掉CRC后的结果，包括flags、DSFID及UID。
	 * 对于老固件(Firmware Revision 04 from 2014-05-06及其之前的版本)：
	 * 无flags和DSFID，只有UID数据。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693StayQuiet(int, byte[])
	 * @see #iso15693ResetToReady(int, byte[])
	 */
	public abstract byte[] iso15693Inventory(int flags, int afi)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 将ISO15693标签设为静默状态
	 * <p>
	 * 该命正确执行后，不会返回任何数据，而直接抛出 RfidCommandException(3， "Timeout")。
	 * 其他情况表示该命令执行失败。
	 * 
	 * @param flags 请求标志
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 通常应直接抛出 RfidCommandException。
	 * 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws RfidCommandException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693ResetToReady(int, byte[])
	 * @see #iso15693Inventory(int, int)
	 */
	public abstract byte[] iso15693StayQuiet(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 在ISO15693标签里读取单个块
	 * 
	 * @param flags 请求标志
	 * @param block 欲读取的块号
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693ReadMultiBlocks(int, int, int, byte[])
	 * @see #iso15693WriteSingleBlock(int, int, byte[], byte[])
	 */
	public abstract byte[] iso15693ReadSingleBlock(int flags, int block,
			byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException;
	
	/**
	 * 向ISO15693标签写入单个块
	 * 
	 * @param flags 请求标志
	 * @param block 欲写入的块号
	 * @param data 欲写入的数据
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693ReadSingleBlock(int, int, byte[])
	 * @see #iso15693ReadMultiBlocks(int, int, int, byte[])
	 * @see #iso15693LockBlock(int, int, byte[])
	 */
	public abstract byte[] iso15693WriteSingleBlock(int flags, int block,
			byte[] data, byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException;
	
	/**
	 * 锁定ISO15693标签某一数据块
	 * 
	 * @param flags 请求标志
	 * @param block 欲锁定的块号
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693WriteSingleBlock(int, int, byte[], byte[])
	 */
	public abstract byte[] iso15693LockBlock(int flags, int block, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 在ISO15693标签里读取多个块
	 * 
	 * @param flags 请求标志
	 * @param first 起始块号
	 * @param num 块数量（块数量-1，比如，欲读取2块，应填1）
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693ReadSingleBlock(int, int, byte[])
	 * @see #iso15693WriteSingleBlock(int, int, byte[], byte[])
	 */
	public abstract byte[] iso15693ReadMultiBlocks(int flags, int first,
			int num, byte[] uid) throws IOException, TimeoutException,
			RfidCommandException, NotImplementedException;
	
	/**
	 * 将ISO15693标签切换到选择状态。
	 * 
	 * @param flags 请求标志
	 * @param uid 必填。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693ResetToReady(int, byte[])
	 */
	public abstract byte[] iso15693Select(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 将ISO5693标签复位到Ready状态。
	 * 
	 * @param flags 请求标志
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693Select(int, byte[])
	 * @see #iso15693Inventory(int, int)
	 */
	public abstract byte[] iso15693ResetToReady(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 向ISO15693标签写入AFI
	 * 
	 * @param flags 请求标志
	 * @param afi Application family identifier
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693LockAfi(int, byte[])
	 */
	public abstract byte[] iso15693WriteAfi(int flags, int afi, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 锁定ISO15693标签的AFI
	 * 
	 * @param flags 请求标志
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693WriteAfi(int, int, byte[])
	 */
	public abstract byte[] iso15693LockAfi(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 向ISO15693标签写入DSFID
	 * 
	 * @param flags 请求标志
	 * @param dsfid Data storage format identifier
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693LockDsfid(int, byte[])
	 */
	public abstract byte[] iso15693WriteDsfid(int flags, int dsfid, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 锁定ISO15693标签的DSFID
	 * 
	 * @param flags 请求标志
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693WriteDsfid(int, int, byte[])
	 */
	public abstract byte[] iso15693LockDsfid(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 获取ISO15693标签信息
	 * 
	 * @param flags 请求标志
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的标签信息，信息内容的解析请参考ISO15693-3协议标准
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693Inventory(int, int)
	 */
	public abstract byte[] iso15693GetSystemInformation(int flags, byte[] uid)
			throws IOException, TimeoutException, RfidCommandException,
			NotImplementedException;
	
	/**
	 * 获取ISO15693标签存储块的安全状态，即数据块是否被锁定。
	 * 
	 * @param flags 请求标志
	 * @param first 起始块号
	 * @param num 块数量（块数量-1，比如，欲读取2块，应填1）
	 * @param uid 若flags的Address_flag位被设置，该参数应设为预期的UID。
	 * 
	 * @return 去掉CRC后的结果。
	 * 
	 * @throws RfidCommandException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 * @throws NotImplementedException 
	 * 
	 * @see #iso15693WriteSingleBlock(int, int, byte[], byte[])
	 */
	public abstract byte[] iso15693GetMultiBlockSecurityStatus(int flags,
			int first, int num, byte[] uid) throws IOException,
			TimeoutException, RfidCommandException, NotImplementedException;
	
	/**
	 * 执行命令
	 * <p>
	 * 这是比较低级的 command-replay 命令方法。
	 * 使用者应自行构造命令数据。自行解析命令回响。
	 * <p>
	 * ps:该方法主要用于调试
	 * 
	 * @param cmd 命令数据
	 * 
	 * @return 命令执行结果
	 * 
	 * @throws IOException
	 * @throws TimeoutException 
	 * 
	 * @hide
	 */
	public abstract byte[] rawCommand(byte[] cmd) throws IOException,
			TimeoutException;
}
