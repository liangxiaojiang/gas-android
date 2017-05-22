LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := uart
LOCAL_SRC_FILES := uart.c
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
