package com.xce.jam;

import com.xce.io.FileOutputStream;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

/* JADX INFO: loaded from: classes.dex */
public class URLSelectForm extends TextBox implements CommandListener {
    static URLSelectForm form;
    Command backComm;
    Command downComm;

    public URLSelectForm() {
        throw new Error("Unresolved compilation problems: \n\tThe import com.xce.lcdui.MIDPRes cannot be resolved\n\tThe import javax.microedition.midlet.MIDletMan cannot be resolved\n\tMIDPRes cannot be resolved to a type\n\tMIDPRes cannot be resolved to a type\n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n\tMIDletMan cannot be resolved\n");
    }

    public void init() {
        init(load());
    }

    public void init(String s) {
        setString(s);
        XBrowser.setCurrent(this);
    }

    String load() {
        throw new Error("Unresolved compilation problem: \n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n");
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:6:0x0016 -> B:21:0x0028). Please report as a decompilation issue!!! */
    public void save(String s) {
        FileOutputStream fileoutputstream = null;
        try {
            try {
                fileoutputstream = new FileOutputStream("/rs/url");
                byte[] abyte0 = s.getBytes();
                fileoutputstream.write(abyte0);
                fileoutputstream.close();
            } catch (Exception e) {
                if (fileoutputstream != null) {
                    fileoutputstream.close();
                }
            } catch (Throwable th) {
                if (fileoutputstream != null) {
                    try {
                        fileoutputstream.close();
                    } catch (Exception e2) {
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
        }
    }

    @Override // javax.microedition.lcdui.CommandListener
    public void commandAction(Command command, Displayable displayable) {
        throw new Error("Unresolved compilation problem: \n\tMIDletMan cannot be resolved\n");
    }

    public static URLSelectForm getForm() {
        if (form == null) {
            new URLSelectForm();
        }
        return form;
    }
}
