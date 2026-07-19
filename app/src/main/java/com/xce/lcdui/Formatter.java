package com.xce.lcdui;

import javax.microedition.lcdui.TextField;
import org.objectweb.asm.signature.SignatureVisitor;

/* JADX INFO: loaded from: classes.dex */
public class Formatter {
    public static int format(int i, char[] ac, int j, char[] ac1) {
        int k;
        if (j == 0) {
            return 0;
        }
        if ((TextField.CONSTRAINT_MASK & i) == 3) {
            k = toPhonenumber(ac, j, ac1);
        } else {
            System.arraycopy(ac, 0, ac1, 0, j);
            k = j;
        }
        if ((TextField.PASSWORD & i) > 0) {
            if (k == 0) {
                k = j;
            }
            toPassword(i, ac1, k);
        }
        return k;
    }

    public static void toPassword(int i, char[] ac, int j) {
        if ((TextField.CONSTRAINT_MASK & i) == 3) {
            for (int k = 0; k < j; k++) {
                if (ac[k] != '-') {
                    ac[k] = '*';
                }
            }
            return;
        }
        for (int l = 0; l < j; l++) {
            ac[l] = '*';
        }
    }

    public static int toPhonenumber(char[] ac, int i, char[] ac1) {
        int l;
        int j = 0;
        int k = 0;
        if (ac[0] == '0' && i > 2) {
            char c = ac[1];
            if (c == '0') {
                char c2 = ac[2];
                if (c2 == '1' || c2 == '2' || c2 == '8') {
                    l = 3;
                } else if (i >= 5) {
                    l = 5;
                } else {
                    l = i;
                }
            } else if (c == '2') {
                l = 2;
            } else {
                l = 3;
            }
            if (0 + l >= i) {
                System.arraycopy(ac, 0, ac1, 0, i - 0);
                k = i;
                j = i;
            } else {
                System.arraycopy(ac, 0, ac1, 0, l);
                k = 0 + l;
                int j2 = 0 + l;
                ac1[j2] = SignatureVisitor.SUPER;
                j = j2 + 1;
            }
        }
        int l2 = 0;
        int i1 = i - k;
        if (i1 > 3 && (ac[0] != '0' || ac[1] != '0')) {
            if (3 < i1 && i1 <= 7) {
                l2 = 3;
            } else if (7 < i1) {
                l2 = 4;
            }
        }
        if (l2 > 0) {
            System.arraycopy(ac, k, ac1, j, l2);
            k += l2;
            int j3 = j + l2;
            ac1[j3] = SignatureVisitor.SUPER;
            j = j3 + 1;
        }
        int l3 = i - k;
        if (l3 > 0) {
            System.arraycopy(ac, k, ac1, j, l3);
            return j + l3;
        }
        return j;
    }

    public static int checkFormat(int i, char[] ac, int j, char[] ac1) {
        int k = 0;
        int i2 = i & TextField.CONSTRAINT_MASK;
        if (i2 == 0) {
            System.arraycopy(ac, 0, ac1, 0, j);
            return j;
        }
        if (i2 == 1) {
            for (int j1 = 0; j1 < j; j1++) {
                if ((ac[j1] >= '0' && ac[j1] <= '9') || ((ac[j1] >= 'a' && ac[j1] <= 'z') || ((ac[j1] >= 'A' && ac[j1] <= 'Z') || ac[j1] == '_' || ac[j1] == '.' || ac[j1] == '@'))) {
                    System.arraycopy(ac, j1, ac1, k, 1);
                    k++;
                }
            }
            return k;
        }
        if (i2 == 2) {
            for (int i1 = 0; i1 < j; i1++) {
                if (ac[i1] >= '0' && ac[i1] <= '9') {
                    System.arraycopy(ac, i1, ac1, k, 1);
                    k++;
                } else if (i1 == 0 && ac[i1] == '-') {
                    System.arraycopy(ac, i1, ac1, k, 1);
                    k++;
                }
            }
            return k;
        }
        if (i2 == 3) {
            for (int l = 0; l < j; l++) {
                if (ac[l] >= '0' && ac[l] <= '9') {
                    System.arraycopy(ac, l, ac1, k, 1);
                    k++;
                }
            }
            return k;
        }
        if (i2 != 4) {
            return 0;
        }
        for (int k1 = 0; k1 < j; k1++) {
            if ((ac[k1] >= '0' && ac[k1] <= '9') || ((ac[k1] >= 'a' && ac[k1] <= 'z') || ((ac[k1] >= 'A' && ac[k1] <= 'Z') || ac[k1] == '_' || ac[k1] == '.'))) {
                System.arraycopy(ac, k1, ac1, k, 1);
                k++;
            }
        }
        return k;
    }
}
