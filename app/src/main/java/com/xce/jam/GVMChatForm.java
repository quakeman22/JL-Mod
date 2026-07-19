package com.xce.jam;

import com.xce.util.Debug;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;

/* JADX INFO: loaded from: classes.dex */
public class GVMChatForm extends List implements CommandListener {
    private static final int GVM_MAX_STR = 12;
    static GVMChatForm form;
    Command backComm;
    TextBox box;
    Command modifyComm;
    Command okComm;

    GVMChatForm() {
        super("채팅 예약어", 3);
        this.backComm = new Command("뒤로", 2, 1);
        this.modifyComm = new Command("수정", 4, 2);
        this.okComm = new Command("확인", 4, 2);
        this.box = new TextBox("채팅예약어 수정", "", 12, 0);
        form = this;
        addCommand(this.backComm);
        addCommand(this.modifyComm);
        setCommandListener(this);
        this.box.addCommand(this.backComm);
        this.box.addCommand(this.okComm);
        this.box.setCommandListener(this);
        load();
    }

    void init() {
        XBrowser.setCurrent(this);
    }

    void load() {
        int i = Gvm.getMaxChatStrings();
        for (int j = 0; j <= i; j++) {
            String s = Gvm.getChatString(j);
            if (s != null) {
                form.append(s, null);
            } else {
                Debug.debugOut("Gvm.getChatString() : " + j);
            }
        }
    }

    @Override // javax.microedition.lcdui.CommandListener
    public void commandAction(Command command, Displayable displayable) {
        int i;
        if (displayable == this) {
            if (command != this.backComm) {
                if ((command == this.modifyComm || command == List.SELECT_COMMAND) && (i = getSelectedIndex()) != -1) {
                    this.box.setString(getString(i));
                    XBrowser.setCurrent(this.box);
                    return;
                }
                return;
            }
            return;
        }
        TextBox textBox = this.box;
        if (displayable == textBox) {
            if (command == this.backComm) {
                XBrowser.setCurrent(this);
                return;
            }
            if (command == this.okComm) {
                String s = textBox.getString();
                if (!s.equals("")) {
                    String s2 = s.substring(0, s.length() <= 12 ? s.length() : 12);
                    int j = getSelectedIndex();
                    set(j, s2, null);
                    Gvm.setChatString(j, s2);
                }
                XBrowser.setCurrent(this);
            }
        }
    }

    static GVMChatForm getForm() {
        if (form == null) {
            new GVMChatForm();
        }
        return form;
    }
}
