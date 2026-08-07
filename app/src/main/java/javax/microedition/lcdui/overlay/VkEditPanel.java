package javax.microedition.lcdui.overlay;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import javax.microedition.lcdui.keyboard.VirtualKeyboard;
import javax.microedition.util.ContextHolder;
import ru.playsoftware.j2meloader.R;

/* JADX INFO: loaded from: classes3.dex */
public class VkEditPanel extends FrameLayout {
    private LinearLayout contentPanel;
    private LinearLayout delayRow;
    private Spinner delaySpinner;
    private boolean expanded;
    private Spinner gridSizeSpinner;
    private SwitchCompat gridSwitch;
    private Button hideButtonsBtn;
    private SwitchCompat joystickSwitch;
    private final Listener listener;
    private final FrameLayout midletFrame;
    private Spinner presetSpinner;
    private SwitchCompat snapSwitch;
    private VirtualKeyboard vk;

    public interface Listener {
        void finishEditing();

        void refitKeys();

        void resetLayout();

        void showHideButtons();
    }

    public VkEditPanel(FrameLayout midletFrame, Listener listener) {
        super(midletFrame.getContext());
        this.expanded = false;
        this.midletFrame = midletFrame;
        this.listener = listener;
        this.vk = ContextHolder.getVk();
        buildCollapsedBar();
    }

    private void buildCollapsedBar() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-2, -2, 49));
        int topMargin = (int) TypedValue.applyDimension(1, 8.0f, getResources().getDisplayMetrics());
        linearLayout.setPadding(topMargin, topMargin, topMargin, topMargin);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(-585491942);
        bg.setCornerRadius(TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics()));
        if (Build.VERSION.SDK_INT >= 16) {
            linearLayout.setBackground(bg);
        } else {
            linearLayout.setBackgroundDrawable(bg);
        }
        LinearLayout bar = new LinearLayout(getContext());
        bar.setOrientation(0);
        bar.setGravity(16);
        int btnPad = (int) TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics());
        float corner6 = TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        Button expandBtn = new Button(getContext());
        expandBtn.setText("Expand");
        expandBtn.setTextColor(-1);
        expandBtn.setTextSize(1, 13.0f);
        expandBtn.setTag("expand_btn");
        if (Build.VERSION.SDK_INT >= 16) {
            expandBtn.setBackground(makeButtonBg(-11184811, corner6));
        } else {
            expandBtn.setBackgroundDrawable(makeButtonBg(-11184811, corner6));
        }
        expandBtn.setPadding(btnPad, btnPad / 3, btnPad, btnPad / 3);
        expandBtn.setOnClickListener(new View.OnClickListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$buildCollapsedBar$0(view);
            }
        });
        bar.addView(expandBtn);
        TextView title = new TextView(getContext());
        title.setText("VK Edit");
        title.setTextColor(-1);
        title.setTextSize(1, 14.0f);
        title.setGravity(17);
        int titlePad = (int) TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics());
        title.setPadding(titlePad, 0, titlePad, 0);
        bar.addView(title);
        Button finishBtn = new Button(getContext());
        finishBtn.setText("Finish");
        finishBtn.setTextColor(-1);
        finishBtn.setTextSize(1, 13.0f);
        if (Build.VERSION.SDK_INT >= 16) {
            finishBtn.setBackground(makeButtonBg(-11184811, corner6));
        } else {
            finishBtn.setBackgroundDrawable(makeButtonBg(-11184811, corner6));
        }
        finishBtn.setPadding(btnPad, btnPad / 3, btnPad, btnPad / 3);
        finishBtn.setOnClickListener(new View.OnClickListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$buildCollapsedBar$1(view);
            }
        });
        bar.addView(finishBtn);
        linearLayout.addView(bar);
        this.contentPanel = new LinearLayout(getContext());
        this.contentPanel.setOrientation(1);
        this.contentPanel.setVisibility(8);
        linearLayout.addView(this.contentPanel);
        addView(linearLayout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildCollapsedBar$0(View v) {
        toggleExpanded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildCollapsedBar$1(View v) {
        if (this.listener != null) {
            this.listener.finishEditing();
        }
    }

    private void buildContent() {
        this.contentPanel.removeAllViews();
        int dp4 = (int) TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics());
        this.contentPanel.setPadding(0, dp4, 0, 0);
        SwitchCompat switchCompat = new SwitchCompat(getContext());
        this.joystickSwitch = switchCompat;
        addToggleRow("Joystick", switchCompat);
        SwitchCompat switchCompat2 = new SwitchCompat(getContext());
        this.gridSwitch = switchCompat2;
        addToggleRow("Show Grid", switchCompat2);
        SwitchCompat switchCompat3 = new SwitchCompat(getContext());
        this.snapSwitch = switchCompat3;
        addToggleRow("Snap to Grid", switchCompat3);
        addPresetRow();
        addDelayRow();
        addGridSizeRow();
        addActionButton("Refit Keys", new Runnable() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$buildContent$2();
            }
        });
        addActionButton("Hide Buttons", new Runnable() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$buildContent$3();
            }
        });
        addActionButton("Reset Layout", new Runnable() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$buildContent$4();
            }
        });
        updateSwitchStates();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildContent$2() {
        if (this.listener != null) {
            this.listener.refitKeys();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildContent$3() {
        if (this.listener != null) {
            this.listener.showHideButtons();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildContent$4() {
        if (this.listener != null) {
            this.listener.resetLayout();
        }
    }

    private void addToggleRow(String label, SwitchCompat sw) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(0);
        int dp6 = (int) TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        row.setPadding(dp6, dp6, dp6, dp6);
        TextView tv = new TextView(getContext());
        tv.setText(label);
        tv.setTextColor(-1);
        tv.setTextSize(1, 14.0f);
        row.addView(tv, new LinearLayout.LayoutParams(0, -2, 1.0f));
        row.addView(sw);
        this.contentPanel.addView(row);
    }

    private void addGridSizeRow() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(0);
        int dp6 = (int) TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        row.setPadding(dp6, dp6, dp6, dp6);
        TextView tv = new TextView(getContext());
        tv.setText("Grid Size");
        tv.setTextColor(-1);
        tv.setTextSize(1, 14.0f);
        row.addView(tv, new LinearLayout.LayoutParams(0, -2, 1.0f));
        this.gridSizeSpinner = new Spinner(getContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.grid_size_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.gridSizeSpinner.setAdapter((SpinnerAdapter) adapter);
        final int[] gridValues = {64, 32, 24, 16, 8};
        if (this.vk != null) {
            float current = this.vk.getGridSize();
            int sel = 0;
            int i = 0;
            while (true) {
                if (i >= gridValues.length) {
                    break;
                }
                if (Math.abs(gridValues[i] - current) >= 0.1f) {
                    i++;
                } else {
                    sel = i;
                    break;
                }
            }
            this.gridSizeSpinner.setSelection(sel);
        }
        this.gridSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(this) { // from class: javax.microedition.lcdui.overlay.VkEditPanel.1
            final /* synthetic */ VkEditPanel this$0;

            {
                this.this$0 = this;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (this.this$0.vk != null) {
                    this.this$0.vk.setGridSize(gridValues[position]);
                    this.this$0.vk.postInvalidate();
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        row.addView(this.gridSizeSpinner);
        this.contentPanel.addView(row);
    }

    private void addPresetRow() {
        final String[] presetNames = {"4-Way", "8-Way", "8-Way 2", "8-Way 3", "8-Way 4"};
        final String[] presetDescs = {"(up, down, left, right)", "(up-left, up, up-right, left, right, down-left, down, down-right)", "(1, up, 3, left, right, 7, down, 9)", "(1, 2, 3, 4, 6, 7, 8, 9)", "(Smart diagonal directions)"};
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(0);
        int dp6 = (int) TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        row.setPadding(dp6, dp6, dp6, dp6);
        TextView tv = new TextView(getContext());
        tv.setText("Joystick Mode");
        tv.setTextColor(-1);
        tv.setTextSize(1, 14.0f);
        row.addView(tv, new LinearLayout.LayoutParams(0, -2, 1.0f));
        this.presetSpinner = new Spinner(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, getContext(), android.R.layout.simple_spinner_item, presetNames) { // from class: javax.microedition.lcdui.overlay.VkEditPanel.2
            final /* synthetic */ VkEditPanel this$0;

            {
                this.this$0 = this;
            }

            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                }
                TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
                TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
                text1.setText(presetNames[position]);
                text2.setText(presetDescs[position]);
                text2.setTextColor(-7829368);
                return convertView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        this.presetSpinner.setAdapter((SpinnerAdapter) adapter);
        if (this.vk != null) {
            this.presetSpinner.setSelection(this.vk.getSettings().joyPreset);
        }
        this.presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (VkEditPanel.this.vk != null) {
                    VkEditPanel.this.vk.applyJoyPreset(position);
                    VkEditPanel.this.vk.postInvalidate();
                }
                if (VkEditPanel.this.delayRow != null) {
                    VkEditPanel.this.delayRow.setVisibility(position == 4 ? 0 : 8);
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        row.addView(this.presetSpinner);
        this.contentPanel.addView(row);
    }

    private void addDelayRow() {
        String[] delayValues = {"0", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000"};
        LinearLayout row = new LinearLayout(getContext());
        this.delayRow = row;
        row.setOrientation(0);
        int dp6 = (int) TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        row.setPadding(dp6, dp6, dp6, dp6);
        TextView tv = new TextView(getContext());
        tv.setText("Joystick delay time (8-Way 4 Only)");
        tv.setTextColor(-1);
        tv.setTextSize(1, 14.0f);
        row.addView(tv, new LinearLayout.LayoutParams(0, -2, 1.0f));
        this.delaySpinner = new Spinner(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, delayValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.delaySpinner.setAdapter((SpinnerAdapter) adapter);
        if (this.vk != null) {
            int delay = this.vk.getSettings().joyRepeatDelay;
            int idx = delay / 100;
            if (idx < 0) {
                idx = 0;
            }
            if (idx >= delayValues.length) {
                idx = delayValues.length - 1;
            }
            this.delaySpinner.setSelection(idx);
            row.setVisibility(this.vk.getSettings().joyPreset != 4 ? 8 : 0);
        } else {
            row.setVisibility(8);
        }
        this.delaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel.4
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (VkEditPanel.this.vk != null) {
                    VkEditPanel.this.vk.getSettings().joyRepeatDelay = position * 100;
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        row.addView(this.delaySpinner);
        this.contentPanel.addView(row);
    }

    private void addActionButton(String label, final Runnable onClick) {
        int margin = (int) TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        int pad = (int) TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics());
        float corner6 = TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        Button btn = new Button(getContext());
        btn.setText(label);
        btn.setTextColor(-1);
        btn.setTextSize(1, 13.0f);
        if (Build.VERSION.SDK_INT >= 16) {
            btn.setBackground(makeButtonBg(-11184811, corner6));
        } else {
            btn.setBackgroundDrawable(makeButtonBg(-11184811, corner6));
        }
        btn.setPadding(pad, pad, pad, pad);
        btn.setAllCaps(false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(margin, margin / 2, margin, margin / 2);
        this.contentPanel.addView(btn, lp);
        btn.setOnClickListener(new View.OnClickListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                onClick.run();
            }
        });
        if (label.equals("Hide Buttons")) {
            this.hideButtonsBtn = btn;
        }
    }

    public void setHideModeActive(boolean active) {
        if (this.hideButtonsBtn == null) {
            return;
        }
        int color = active ? -13421773 : -11184811;
        float corner6 = TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics());
        if (Build.VERSION.SDK_INT >= 16) {
            this.hideButtonsBtn.setBackground(makeButtonBg(color, corner6));
        } else {
            this.hideButtonsBtn.setBackgroundDrawable(makeButtonBg(color, corner6));
        }
    }

    private void toggleExpanded() {
        this.expanded = !this.expanded;
        if (this.expanded) {
            buildContent();
        }
        this.contentPanel.setVisibility(this.expanded ? 0 : 8);
        updateExpandButtonText(this.expanded ? "Collapse" : "Expand");
    }

    private void updateExpandButtonText(String text) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout root = (LinearLayout) child;
                for (int j = 0; j < root.getChildCount(); j++) {
                    View barChild = root.getChildAt(j);
                    if (barChild instanceof LinearLayout) {
                        LinearLayout bar = (LinearLayout) barChild;
                        for (int k = 0; k < bar.getChildCount(); k++) {
                            View b = bar.getChildAt(k);
                            if ((b instanceof Button) && "expand_btn".equals(b.getTag())) {
                                ((Button) b).setText(text);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateSwitchStates() {
        if (this.vk == null) {
            return;
        }
        if (this.joystickSwitch != null) {
            this.joystickSwitch.setChecked(this.vk.getSettings().joyEnabled);
            this.joystickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda6
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    this.f$0.lambda$updateSwitchStates$6(compoundButton, z);
                }
            });
        }
        if (this.gridSwitch != null) {
            this.gridSwitch.setChecked(this.vk.isShowGrid());
            this.gridSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda7
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    this.f$0.lambda$updateSwitchStates$7(compoundButton, z);
                }
            });
        }
        if (this.snapSwitch != null) {
            this.snapSwitch.setChecked(this.vk.isSnapToGrid());
            this.snapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: javax.microedition.lcdui.overlay.VkEditPanel$$ExternalSyntheticLambda8
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    this.f$0.lambda$updateSwitchStates$8(compoundButton, z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSwitchStates$6(CompoundButton buttonView, boolean isChecked) {
        this.vk.getSettings().joyEnabled = isChecked;
        this.vk.postInvalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSwitchStates$7(CompoundButton buttonView, boolean isChecked) {
        this.vk.setShowGrid(isChecked);
        this.vk.postInvalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSwitchStates$8(CompoundButton buttonView, boolean isChecked) {
        this.vk.setSnapToGrid(isChecked);
        this.vk.postInvalidate();
    }

    public void show() {
        if (this.expanded) {
            this.expanded = false;
            this.contentPanel.setVisibility(8);
            updateExpandButtonText("Expand");
        }
        if (getParent() == null) {
            this.midletFrame.addView(this, new FrameLayout.LayoutParams(-1, -1));
        }
        setVisibility(0);
        bringToFront();
    }

    public void dismiss() {
        setVisibility(8);
    }

    private static StateListDrawable makeButtonBg(int normalColor, float cornerRadiusPx) {
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(normalColor);
        normal.setCornerRadius(cornerRadiusPx);
        GradientDrawable pressed = new GradientDrawable();
        pressed.setColor(darkenColor(normalColor));
        pressed.setCornerRadius(cornerRadiusPx);
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_pressed}, pressed);
        sld.addState(new int[0], normal);
        return sld;
    }

    private static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.7f;
        return Color.HSVToColor(Color.alpha(color), hsv);
    }
}
