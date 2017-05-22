package com.joe.oil.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;
import com.joe.oil.R;
import com.joe.oil.activity.MainActivity;
import com.joe.oil.activity.SoundHandle;
import com.joe.oil.entity.User;
import com.joe.oil.mifare.Converter;
import com.joe.oil.mifare.MifareBlock;
import com.joe.oil.mifare.MifareClassCard;
import com.joe.oil.mifare.MifareSector;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import net.safetone.rfid.lib.MifareKeyType;
import net.safetone.rfid.lib.RfidReader;
import net.safetone.rfid.lib.exception.BlockNumberException;
import net.safetone.rfid.lib.exception.MifareKeyException;
import net.safetone.rfid.lib.exception.NotImplementedException;
import net.safetone.rfid.lib.exception.RfidCommandException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * 公共工具类
 * Created by scar1et on 15-7-14.
 */
public class CustomUtil {

    private static DisplayImageOptions defaultOptions;

    public static DisplayImageOptions getDefaultOptions() {

        if (defaultOptions == null) {
            defaultOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.default_img)
                    .showImageOnLoading(new ColorDrawable(Color.parseColor("#A7A7A7")))
                    .showImageOnFail(R.drawable.default_img)
                    .resetViewBeforeLoading(true)
//				.delayBeforeLoading (1000)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(false)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
//				.displayer(new FadeInBitmapDisplayer (300))
                    .build();
        }
        return defaultOptions;
    }

    public static void createNewTaskNo(User user) {
        String officeId = "";
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmm");
        String date = format.format(new Date());
        if (user.getOfficeCode() != null) {
            int id = Integer.parseInt(user.getOfficeCode().substring(5, 6));
            switch (id) {
                case 1:
                    officeId = "01";
                    break;

                case 2:
                    officeId = "02";
                    break;

                case 3:
                    officeId = "03";
                    break;

                case 4:
                    officeId = "04";
                    break;

                default:
                    break;
            }
        }
        Constants.GIS_START_NUM = "XJ" + officeId + user.getUserId() + date;
    }

    public static String getNFCCode(Context context, Intent intent) {
        String[] rfidCode = new String[3];
        byte[] passwordArray = {(byte) 0xa1, (byte) 0xa1, (byte) 0xa1, (byte) 0xa1, (byte) 0xa1, (byte) 0xa1};
        String action = intent.getAction();
        try {
            // 2) Check if it was triggered by a tag discovered interruption.
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
                SoundHandle soundHandle = new SoundHandle();
                soundHandle.setContext(context);
                soundHandle.execute();
                // 3) Get an instance of the TAG from the NfcAdapter
                Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                // 4) Get an instance of the Mifare classic card from this TAG
                // intent
                MifareClassic mfc = MifareClassic.get(tagFromIntent);
                MifareClassCard mifareClassCard = null;

                try { // 5.1) Connect to card
                    mfc.connect();
                    boolean auth = false;
                    // 5.2) and get the number of sectors this card has..and
                    // loop
                    // thru these sectors
                    int secCount = mfc.getSectorCount();
                    mifareClassCard = new MifareClassCard(secCount);
                    int bCount = 0;
                    int bIndex = 0;
                    for (int j = 0; j < secCount; j++) {
                        MifareSector mifareSector = new MifareSector();
                        mifareSector.sectorIndex = j;
                        // 6.1) authenticate the sector
                        if (j == 1) {
                            auth = mfc.authenticateSectorWithKeyA(j, passwordArray);
                        } else {
                            auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
                        }
                        mifareSector.authorized = auth;
                        if (auth) {
                            // 6.2) In each sector - get the block count
                            bCount = mfc.getBlockCountInSector(j);
                            bCount = Math.min(bCount, MifareSector.BLOCKCOUNT);
                            bIndex = mfc.sectorToBlock(j);
                            for (int i = 0; i < bCount; i++) {

                                // 6.3) Read the block
                                byte[] data = mfc.readBlock(bIndex);
                                MifareBlock mifareBlock = new MifareBlock(data);
                                mifareBlock.blockIndex = bIndex;
                                // 7) Convert the data into a string from Hex
                                // format.

                                bIndex++;
                                mifareSector.blocks[i] = mifareBlock;

                            }
                            mifareClassCard.setSector(mifareSector.sectorIndex, mifareSector);
                        } else { // Authentication failed - Handle it
                            Log.i("auth " + j, "auth error!");
                        }
                    }
                    ArrayList<String> blockData = new ArrayList<String>();
                    int blockIndex = 0;
                    for (int i = 0; i < secCount; i++) {

                        MifareSector mifareSector = mifareClassCard.getSector(i);
                        for (int j = 0; j < MifareSector.BLOCKCOUNT; j++) {
                            MifareBlock mifareBlock = mifareSector.blocks[j];
                            byte[] data = mifareBlock.getData();
                            blockData.add(Converter.getAsciiString(data, data.length));
                        }
                    }
                    String[] contents = new String[blockData.size()];
                    blockData.toArray(contents);

                    rfidCode[0] = blockData.get(4);
                    rfidCode[1] = blockData.get(5);
                    rfidCode[2] = blockData.get(6);
                    Log.e("log ", rfidCode[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (mifareClassCard != null) {
                        mifareClassCard.debugPrint();
                    }
                }
                return rfidCode[0].substring(2, 14);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCodeOfBeiJing() {
        String code;
        RfidReader mRfidReader = MainActivity.getRfidReader();
        byte[] passwordArray = {(byte) 0xa1, (byte) 0xa1, (byte) 0xa1, (byte) 0xa1, (byte) 0xa1, (byte) 0xa1};
        byte[] rsp = null;
        try {
            rsp = mRfidReader.mifareClassicReadBlock(4, MifareKeyType.KeyA, passwordArray);
        } catch (IOException | BlockNumberException | NotImplementedException | MifareKeyException | RfidCommandException | TimeoutException e) {
            e.printStackTrace();
        }
        if (rsp != null && rsp.length > 0) {
            code = Converter.getAsciiString(rsp, 14);
        } else {
            code = null;
        }
        return code;
    }
}
