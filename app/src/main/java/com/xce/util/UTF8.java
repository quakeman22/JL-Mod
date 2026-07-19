package com.xce.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.billthefarmer.mididriver.MidiConstants;

/* JADX INFO: loaded from: classes.dex */
public class UTF8 {
    public static String decode(byte[] abyte0) throws IOException {
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        int j = abyte0.length;
        while (i < j) {
            int i2 = i + 1;
            byte byte0 = abyte0[i];
            if ((byte0 & 240) == 224) {
                if (i2 + 2 > j) {
                    throw new UTFDataFormatException("truncated 3");
                }
                int i3 = i2 + 1;
                byte byte1 = abyte0[i2];
                int i4 = i3 + 1;
                byte byte3 = abyte0[i3];
                if ((byte1 & 192) != 128 || (byte3 & 192) != 128) {
                    throw new UTFDataFormatException("invalid format");
                }
                stringbuffer.append((char) (((byte0 & GeneralMidiConstants.DULCIMER) << 12) + ((byte1 & GeneralMidiConstants.SYNTHBRASS_1) << 6) + (byte3 & GeneralMidiConstants.SYNTHBRASS_1)));
                i = i4;
            } else if ((byte0 & MidiConstants.PITCH_BEND) == 192) {
                if (i2 + 1 > j) {
                    throw new UTFDataFormatException("truncated 2");
                }
                int i5 = i2 + 1;
                byte byte2 = abyte0[i2];
                if ((byte2 & 192) != 128) {
                    throw new UTFDataFormatException("invalid format");
                }
                stringbuffer.append(((char) ((byte0 & GeneralMidiConstants.GUITAR_HARMONICS) << 6)) + (byte2 & GeneralMidiConstants.SYNTHBRASS_1));
                i = i5;
            } else if ((byte0 & 128) == 0) {
                stringbuffer.append((char) byte0);
                i = i2;
            } else {
                throw new UTFDataFormatException("format error");
            }
        }
        return stringbuffer.toString();
    }

    public static String decode(DataInput datainput, int i) throws IOException {
        StringBuffer stringbuffer = new StringBuffer();
        int j = 0;
        while (j < i) {
            byte byte0 = datainput.readByte();
            j++;
            if ((byte0 & 240) == 224) {
                if (j + 2 > i) {
                    throw new UTFDataFormatException("truncated 3");
                }
                byte byte1 = datainput.readByte();
                byte byte3 = datainput.readByte();
                j += 2;
                if ((byte1 & 192) != 128 || (byte3 & 192) != 128) {
                    throw new UTFDataFormatException("invalid format");
                }
                stringbuffer.append((char) (((byte0 & GeneralMidiConstants.DULCIMER) << 12) + ((byte1 & GeneralMidiConstants.SYNTHBRASS_1) << 6) + (byte3 & GeneralMidiConstants.SYNTHBRASS_1)));
            } else if ((byte0 & MidiConstants.PITCH_BEND) == 192) {
                if (j + 1 > i) {
                    throw new UTFDataFormatException("truncated 2");
                }
                byte byte2 = datainput.readByte();
                j++;
                if ((byte2 & 192) != 128) {
                    throw new UTFDataFormatException("invalid format");
                }
                stringbuffer.append(((char) ((byte0 & GeneralMidiConstants.GUITAR_HARMONICS) << 6)) + (byte2 & GeneralMidiConstants.SYNTHBRASS_1));
            } else if ((byte0 & 128) == 0) {
                stringbuffer.append((char) byte0);
            } else {
                throw new UTFDataFormatException("error decoding UTF");
            }
        }
        return stringbuffer.toString();
    }

    public static byte[] encode(String s) {
        char[] ac = s.toCharArray();
        int i = 0;
        int j = 0;
        for (int k = 0; k < ac.length; k++) {
            if (ac[k] >= 1 && ac[k] <= 127) {
                i++;
            } else if (ac[k] <= 2047) {
                i += 2;
            } else {
                i += 3;
            }
        }
        byte[] abyte0 = new byte[i];
        for (int l = 0; l < ac.length; l++) {
            if (ac[l] >= 1 && ac[l] <= 127) {
                abyte0[j] = (byte) ac[l];
                j++;
            } else if (ac[l] <= 2047) {
                int j2 = j + 1;
                abyte0[j] = (byte) (((ac[l] >> 6) & 63) | 192);
                j = j2 + 1;
                abyte0[j2] = (byte) ((ac[l] & '?') | 128);
            } else {
                int j3 = j + 1;
                abyte0[j] = (byte) (((ac[l] >> '\f') & 15) | 224);
                int j4 = j3 + 1;
                abyte0[j3] = (byte) (((ac[l] >> 6) & 63) | 128);
                abyte0[j4] = (byte) ((ac[l] & '?') | 128);
                j = j4 + 1;
            }
        }
        return abyte0;
    }
}
