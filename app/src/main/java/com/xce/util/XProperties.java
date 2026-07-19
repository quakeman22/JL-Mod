package com.xce.util;

import com.xce.io.FileInputStream;
import com.xce.io.XFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
public class XProperties {
    private Hashtable hash = new Hashtable(23);
    private byte[] ori;

    private static native int next(byte[] bArr, int i, String[] strArr);

    public int size() {
        return this.hash.size();
    }

    public String getProperty(String s) {
        return (String) this.hash.get(s);
    }

    public void setProperty(String s, String s1) {
        this.hash.put(s, s1);
    }

    public Enumeration propertyNames() {
        return this.hash.keys();
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:6:0x0015 -> B:19:0x0027). Please report as a decompilation issue!!! */
    public void loadFile(String s) {
        FileInputStream fileinputstream = null;
        try {
            try {
                fileinputstream = new FileInputStream(s);
                load(fileinputstream, XFile.filesize(s), false);
                fileinputstream.close();
            } catch (IOException e) {
            }
        } catch (Exception e2) {
            if (fileinputstream != null) {
                fileinputstream.close();
            }
        } catch (Throwable th) {
            if (fileinputstream != null) {
                try {
                    fileinputstream.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    public void load(InputStream inputstream, int i, boolean flag) throws IOException {
        int j;
        if (inputstream == null) {
            return;
        }
        if (i <= 0) {
            j = inputstream.available();
        } else {
            j = i;
        }
        byte[] abyte0 = new byte[j];
        int l = 0;
        while (true) {
            int k = inputstream.read(abyte0, l, abyte0.length - l);
            if (k == -1) {
                break;
            }
            l += k;
            if (abyte0.length == l) {
                byte[] abyte1 = new byte[abyte0.length + 2048];
                System.arraycopy(abyte0, 0, abyte1, 0, l);
                abyte0 = abyte1;
            }
        }
        if (j != l) {
            throw new IOException();
        }
        int i1 = 0;
        String[] as = new String[2];
        do {
            i1 = next(abyte0, i1, as);
            if ((as[0] != null) & (as[1] != null)) {
                this.hash.put(as[0], as[1]);
            }
            as[0] = null;
            as[1] = null;
        } while (i1 != -1);
        if (flag) {
            byte[] bArr = new byte[l];
            this.ori = bArr;
            System.arraycopy(abyte0, 0, bArr, 0, l);
        }
    }

    public void store(OutputStream outputstream) throws IOException {
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
        Enumeration enumeration = this.hash.keys();
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            outputstreamwriter.write(String.valueOf(s) + ": " + this.hash.get(s) + "\n");
        }
        outputstreamwriter.flush();
    }

    public void storeOriginal(OutputStream outputstream) throws IOException {
        byte[] bArr = this.ori;
        if (bArr != null) {
            outputstream.write(bArr);
            outputstream.flush();
            return;
        }
        throw new IOException();
    }
}
