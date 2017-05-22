#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <errno.h>

#include "uart.h"

#include "android/log.h"
static const char *TAG="uart";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

struct UartPort {
	struct termios tiosfd; /* the saved UART port termios structure */
	struct termios tio; /* now opt UART port termios structure */
	int fd; /* the UART port opend fd */
} uartPort = {
	.fd = -1
};

static speed_t getBaudrate(int baudrate)
{
    speed_t speed;

    switch(baudrate) {
    case 50:            speed = B50; break;
    case 75:            speed = B75; break;
    case 110:           speed = B110; break;
    case 134:           speed = B134; break;
    case 150:           speed = B150; break;
    case 200:           speed = B200; break;
    case 300:           speed = B300; break;
    case 600:           speed = B600; break;
    case 1200:          speed = B1200; break;
    case 1800:          speed = B1800; break;
    case 2400:          speed = B2400; break;
    case 4800:          speed = B4800; break;
    case 9600:          speed = B9600; break;
    case 19200:         speed = B19200; break;
    case 38400:         speed = B38400; break;
#ifdef B57600
    case 57600:         speed = B57600; break;
#endif
#ifdef B115200
    case 115200:        speed = B115200; break;
#endif
#ifdef B230400
    case 230400:        speed = B230400; break;
#endif
#ifdef B460800
    case 460800:        speed = B460800; break;
#endif
#ifdef B500000
    case 500000:        speed = B500000; break;
#endif
#ifdef B576000
    case 576000:        speed = B576000; break;
#endif
#ifdef B921600
    case 921600:        speed = B921600; break;
#endif
#ifdef B1000000
    case 1000000:       speed = B1000000; break;
#endif
#ifdef B1152000
    case 1152000:       speed = B1152000; break;
#endif
#ifdef B1500000
    case 1500000:       speed = B1500000; break;
#endif
#ifdef B2000000
    case 2000000:       speed = B2000000; break;
#endif
#ifdef B2500000
    case 2500000:       speed = B2500000; break;
#endif
#ifdef B3000000
    case 3000000:       speed = B3000000; break;
#endif
#ifdef B3500000
    case 3500000:       speed = B3500000; break;
#endif
#ifdef B4000000
    case 4000000:       speed = B4000000; break;
#endif
    default:            speed = B0; break;
    }

    return speed;
}

/* xtcgetattr 获取串口终端属性
 *
 * 参数：fd 打开的串口设备的文件描述符 (value)
 *      t 指向struct termios的指针，需要修改的串口属性 (result)
 *      oldt 指向struct termios的指针，保存的串口属性 (result)
 *
 * 返回：0成功，非0失败 */
int xtcgetattr(int fd, struct termios *t, struct termios *oldt)
{
    int ret;

    if ((ret = tcgetattr(fd, oldt))) {
    	LOGE("tcgetattr() failed: %s", strerror(errno));
    	return ret;
    }
    *t = *oldt;

    return ret;
}

/* xtcsetattr 设置串口终端属性
 *
 * 参数：fd 打开的串口设备的文件描述符 (value)
 *      tio 指向struct termios的指针，串口属性 (value)
 *      device 串口设备名 (value)
 *
 * 返回：0成功，非0失败 */
int xtcsetattr(int fd, struct termios *tio)
{
    int ret = tcsetattr(fd, TCSAFLUSH, tio);

    if (ret) {
        LOGE("can't tcsetattr: %s", strerror(errno));
    }
    return ret;
}

int uartOpen(const char *dev, int baudrate, int databits, char parity, int stop) {
	speed_t speed;

	/* Check arguments */
	speed = getBaudrate(baudrate);
	if (speed == B0) {
		LOGE("Invalid baudrate %d", baudrate);
		return -1;
	}
	if (databits < 5 || databits > 8) {
        LOGE("Invalid data bits %d", databits);
        return -1;
	}
	switch (parity) {
	case 'E': case 'M': case 'N': case 'O': case 'S':
	    break;
	default:
	    LOGE("Invalid parity '%c'", parity);
	    return -1;
	}
	if (stop < 1 || stop > 2) {
        LOGE("Invalid stop bits %d", stop);
        return -1;
	}

	/* open device */
	uartPort.fd = open(dev, O_RDWR | O_NOCTTY);
	if (uartPort.fd < 0) {
		LOGE("Cannot open device: %s: %s", dev, strerror(errno));
		return -1;
	} else {
		LOGD("open(%s, O_RDWR | O_NOCTTY) uartPort.fd = %d", dev, uartPort.fd);
	}
	if (fcntl(uartPort.fd, F_SETFL, O_RDWR)) {
		LOGE("fcntl error: %s", strerror(errno));
		goto ERROR_OUT;
	}

	/* get and save term attr */
	if (xtcgetattr(uartPort.fd, &(uartPort.tio), &(uartPort.tiosfd))) {
		goto ERROR_OUT;
	}

//	cfmakeraw(&(uartPort.tio));

	/* set device speed */
	if (cfsetispeed(&uartPort.tio, speed) || cfsetospeed(&uartPort.tio, speed)) {
		LOGE("cfsetspeed error: %s", strerror(errno));
		goto ERROR_OUT;
	}

	/* set data bits */
	switch (databits) {
	case 5:
	    uartPort.tio.c_cflag = (uartPort.tio.c_cflag & ~CSIZE) | CS5;
	    break;
	case 6:
	    uartPort.tio.c_cflag = (uartPort.tio.c_cflag & ~CSIZE) | CS6;
	    break;
	case 7:
	    uartPort.tio.c_cflag = (uartPort.tio.c_cflag & ~CSIZE) | CS7;
	    break;
	case 8:
	default:
	    uartPort.tio.c_cflag = (uartPort.tio.c_cflag & ~CSIZE) | CS8;
	    break;
	}

	/* Set into raw, no echo mode */
	uartPort.tio.c_iflag =  IGNBRK;
	uartPort.tio.c_lflag = 0;
	uartPort.tio.c_oflag = 0;
	uartPort.tio.c_cflag |= CLOCAL | CREAD;
	uartPort.tio.c_cflag &= ~CRTSCTS; // No hard flow control
	uartPort.tio.c_cc[VMIN] = 1;
	uartPort.tio.c_cc[VTIME] = 5;
	uartPort.tio.c_iflag &= ~(IXON|IXOFF|IXANY); // No soft flow control

	/* set parity */
	uartPort.tio.c_cflag &= ~(PARENB | PARODD);
	if (parity == 'E')
	    uartPort.tio.c_cflag |= PARENB;
	else if (parity == 'O')
	    uartPort.tio.c_cflag |= (PARENB | PARODD);

	/* set stop bits */
	if (stop == 2)
	    uartPort.tio.c_cflag |= CSTOPB;
	else
	    uartPort.tio.c_cflag &= ~CSTOPB;


	if (xtcsetattr(uartPort.fd, &(uartPort.tio)))
		goto ERROR_OUT;

	return uartPort.fd;

ERROR_OUT:
	close(uartPort.fd);
	uartPort.fd = -1;
	return -1;
}

int uartClose() {
    int ret = 0;

	if (uartPort.fd >= 0) {
		tcsetattr(uartPort.fd, TCSAFLUSH, &(uartPort.tiosfd));
		ret = close(uartPort.fd);
		uartPort.fd = -1;
	}

	return ret;
}

/*
 * Class:     net_safetone_rfid_lib_CommUart
 * Method:    uart_open
 * Signature: (Ljava/lang/String;IIII)I
 */
JNIEXPORT jint JNICALL Java_net_safetone_rfid_lib_CommUart_uart_1open
  (JNIEnv *env, jclass this, jstring dev, jint baud, jint databits, jint parity, jint stop)
{
    const char *dev_utf;
	int fd;

	jboolean iscopy;
	if ((dev_utf = (*env)->GetStringUTFChars(env, dev, &iscopy)) == NULL) {
	    LOGE("Cannot convert dev to C string");
	    return -1;
	}
	fd = uartOpen(dev_utf, baud, databits, parity, stop);
	(*env)->ReleaseStringUTFChars(env, dev, dev_utf);

	return fd;
}

/*
 * Class:     net_safetone_rfid_lib_CommUart
 * Method:    uart_close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_safetone_rfid_lib_CommUart_uart_1close
  (JNIEnv *env, jobject this)
{
#if 0
    jclass class = (*env)->GetObjectClass(env, this);
    jfieldID fdID = (*env)->GetFieldID(env, class, "fd", "I");
	if (fdID) {
	    jint fd = (*env)->GetIntField(env, this, fdID);
	    if (fd != uartPort.fd && fd >= 0) {
	        LOGE("(%d) Is not previous opened UART (%d)!", fd, uartPort.fd);
	        close(fd);
	    }
	}
#endif

	uartClose();
}
