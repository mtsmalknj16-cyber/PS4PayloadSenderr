package com.motasem.ps4payload;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int PICK_FILE = 41;
    private Uri selectedUri = null;
    private String selectedName = "لم يتم اختيار ملف";
    private TextView fileLabel, status;
    private EditText ipField, portField;
    private Button chooseButton, sendButton, resetButton;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(16,24,39));
        getWindow().setNavigationBarColor(Color.rgb(16,24,39));
        buildUi();
    }

    private TextView text(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); return t;
    }
    private GradientDrawableCompat rounded(int color) { return new GradientDrawableCompat(color, dp(14)); }
    private void buildUi() {
        int bg = Color.rgb(12,18,30), panel = Color.rgb(24,34,50), white = Color.WHITE, muted = Color.rgb(177,190,207), green = Color.rgb(22,163,74);
        android.widget.ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(bg);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20),dp(16),dp(20),dp(24)); scroll.addView(root);
        LinearLayout top = new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL); top.setOrientation(LinearLayout.HORIZONTAL);
        TextView title = text("PS4 Payload Sender", 22, white); title.setTypeface(null, Typeface.BOLD); top.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        resetButton = new Button(this); resetButton.setText("Reset ↻"); resetButton.setAllCaps(false); top.addView(resetButton); root.addView(top);
        TextView subtitle = text("إرسال البايلود إلى جهاز PS4 عبر الشبكة", 14, muted); subtitle.setPadding(0,dp(4),0,dp(22)); root.addView(subtitle);

        root.addView(label("1. ملف البايلود", muted));
        LinearLayout fileCard = new LinearLayout(this); fileCard.setOrientation(LinearLayout.VERTICAL); fileCard.setPadding(dp(14),dp(14),dp(14),dp(14)); fileCard.setBackground(rounded(panel).drawable());
        fileLabel = text(selectedName, 14, white); fileLabel.setMaxLines(2); fileCard.addView(fileLabel);
        chooseButton = new Button(this); chooseButton.setText("اختيار ملف من الهاتف"); chooseButton.setAllCaps(false); fileCard.addView(chooseButton); root.addView(fileCard, params(-1,-2,dp(8),dp(18)));

        root.addView(label("2. عنوان IP للبلايستيشن", muted));
        ipField = field("مثال: 192.168.1.100", "text"); root.addView(ipField, params(-1,dp(54),0,dp(14)));
        root.addView(label("3. Port", muted));
        portField = field("9020", "number"); portField.setText("9020"); root.addView(portField, params(-1,dp(54),0,dp(20)));

        sendButton = new Button(this); sendButton.setText("إرسال Payload  ➤"); sendButton.setTextSize(16); sendButton.setAllCaps(false); sendButton.setTextColor(white); sendButton.setBackground(rounded(green).drawable()); root.addView(sendButton, params(-1,dp(58),0,dp(16)));
        status = text("جاهز للإرسال. تأكد أن الهاتف وPS4 على الشبكة نفسها وأن مستقبِل البايلود يعمل.", 14, muted); status.setPadding(dp(4),dp(8),dp(4),dp(8)); root.addView(status);
        TextView note = text("ملاحظة: نجاح نقل الملف لا يضمن أن البايلود متوافق مع إصدار النظام أو طريقة الاستغلال.", 12, muted); note.setPadding(dp(4),dp(14),dp(4),0); root.addView(note);
        setContentView(scroll);

        chooseButton.setOnClickListener(v -> { Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("*/*"); startActivityForResult(i, PICK_FILE); });
        resetButton.setOnClickListener(v -> { selectedUri = null; selectedName = "لم يتم اختيار ملف"; fileLabel.setText(selectedName); ipField.setText(""); portField.setText(""); status.setText("تم مسح الملف وعنوان IP والبورت."); sendButton.setEnabled(true); });
        sendButton.setOnClickListener(v -> sendPayload());
    }
    private TextView label(String s, int c) { TextView t = text(s,15,c); t.setTypeface(null,Typeface.BOLD); t.setPadding(0,0,0,dp(8)); return t; }
    private EditText field(String hint, String type) { EditText e = new EditText(this); e.setSingleLine(true); e.setTextSize(16); e.setTextColor(Color.WHITE); e.setHintTextColor(Color.rgb(140,153,172)); e.setHint(hint); e.setPadding(dp(14),0,dp(14),0); e.setBackground(new GradientDrawableCompat(Color.rgb(24,34,50),dp(12)).drawable()); if(type.equals("number")) e.setInputType(2); else e.setInputType( android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); return e; }
    private LinearLayout.LayoutParams params(int w,int h,int top,int bottom) { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w,h); p.topMargin=top; p.bottomMargin=bottom; return p; }
    @Override protected void onActivityResult(int req, int result, Intent data) { super.onActivityResult(req,result,data); if(req==PICK_FILE && result==RESULT_OK && data!=null && data.getData()!=null) { selectedUri=data.getData(); String path=selectedUri.getLastPathSegment(); selectedName=(path==null?"تم اختيار الملف":path.substring(path.lastIndexOf('/')+1)); fileLabel.setText("📄  "+selectedName); status.setText("تم اختيار الملف. راجع IP والبورت قبل الإرسال."); } }

    private void sendPayload() {
        if(selectedUri==null) { status.setText("اختر ملف البايلود أولاً."); return; }
        String host=ipField.getText().toString().trim(); String ps=portField.getText().toString().trim();
        if(host.isEmpty()) { status.setText("أدخل عنوان IP الخاص بالبلايستيشن."); return; }
        int port; try { port=Integer.parseInt(ps); if(port<1||port>65535) throw new NumberFormatException(); } catch(Exception e) { status.setText("رقم البورت غير صحيح. استخدم رقماً من 1 إلى 65535."); return; }
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ipField.getWindowToken(),0);
        sendButton.setEnabled(false); status.setText("جارٍ الاتصال وإرسال الملف…"); final Uri uri=selectedUri; final String target=host; final int targetPort=port;
        executor.execute(() -> {
            Socket socket=null; long total=0;
            try {
                socket=new Socket(); socket.connect(new InetSocketAddress(target,targetPort),5000); socket.setSoTimeout(10000);
                try(InputStream in=getContentResolver().openInputStream(uri); OutputStream out=socket.getOutputStream()) {
                    if(in==null) throw new java.io.IOException("تعذر فتح الملف"); byte[] buffer=new byte[8192]; int n;
                    while((n=in.read(buffer))!=-1) { out.write(buffer,0,n); total+=n; }
                    out.flush(); socket.shutdownOutput();
                }
                final long bytes=total; runOnUiThread(() -> status.setText("انتهى إرسال الملف بنجاح. الحجم: "+bytes+" بايت. هذا يؤكد النقل فقط، وليس تنفيذ البايلود.") );
            } catch(Exception ex) { String msg=ex.getMessage(); if(msg==null) msg=ex.getClass().getSimpleName(); final String err=msg; runOnUiThread(() -> status.setText("فشل الإرسال: "+err+". تحقق من IP والبورت والشبكة ومستقبِل البايلود.")); }
            finally { try { if(socket!=null) socket.close(); } catch(Exception ignored) {} runOnUiThread(() -> sendButton.setEnabled(true)); }
        });
    }
    @Override protected void onDestroy() { super.onDestroy(); executor.shutdownNow(); }

    private static class GradientDrawableCompat {
        private final android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        GradientDrawableCompat(int color, int radius) { d.setColor(color); d.setCornerRadius(radius); }
        android.graphics.drawable.Drawable drawable() { return d; }
    }
}
