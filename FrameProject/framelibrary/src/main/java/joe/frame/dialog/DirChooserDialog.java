package joe.frame.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import joe.framelibrary.R;

/**
 * @author chenqiao
 */
public class DirChooserDialog extends Dialog implements View.OnClickListener {

    private ListView list;
    ArrayAdapter<String> Adapter;
    ArrayList<String> arr = new ArrayList<>();

    private OkClickListener listener;

    Context context;
    private String path;

    private TextView title;
    private EditText et;
    private Button home, back, ok;

    private int type = 1;
    private String[] fileType = null;

    public final static int TypeOpen = 1;
    public final static int TypeSave = 2;

    /**
     * @param context
     * @param type        值为1表示创建打开目录类型的对话框，2为创建保存文件到目录类型的对话框
     * @param fileType    要过滤的文件类型,null表示只选择目录
     * @param initialpath 初始化显示的路径
     */
    public DirChooserDialog(Context context, int type, String[] fileType, String initialpath) {
        super(context);
        this.context = context;
        this.type = type;
        this.fileType = fileType;
        this.path = initialpath;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chooserdialog);
        if (TextUtils.isEmpty(path)) {
            path = getRootDir();
        }
        arr = (ArrayList<String>) getDirs(path);
        Adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arr);

        list = (ListView) findViewById(R.id.list_dir);
        list.setAdapter(Adapter);

        list.setOnItemClickListener(lvLis);

        home = (Button) findViewById(R.id.btn_home);
        home.setOnClickListener(this);

        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(this);

        ok = (Button) findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);

        LinearLayout titleView = (LinearLayout) findViewById(R.id.dir_layout);

        if (type == TypeOpen) {
            title = new TextView(context);
            titleView.addView(title);
            title.setText(path);
        } else if (type == TypeSave) {
            et = new EditText(context);
            et.setWidth(240);
            et.setHeight(70);
            et.setGravity(Gravity.CENTER);
            et.setPadding(0, 2, 0, 0);
            titleView.addView(et);
            et.setText("wfFileName");
        }
    }

    // 动态更新ListView
    Runnable add = new Runnable() {

        @Override
        public void run() {
            arr.clear();
            // 必须得用这种方法为arr赋值才能更新
            List<String> temp = getDirs(path);
            for (int i = 0; i < temp.size(); i++)
                arr.add(temp.get(i));
            Adapter.notifyDataSetChanged();
        }
    };

    private OnItemClickListener lvLis = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String temp = (String) arg0.getItemAtPosition(arg2);
            if (temp.equals(".."))
                path = getSubDir(path);
            else if (path.equals("/"))
                path = path + temp;
            else
                path = path + "/" + temp;

            if (type == TypeOpen)
                title.setText(path);

            Handler handler = new Handler();
            handler.post(add);
        }
    };

    private List<String> getDirs(String ipath) {
        List<String> file = new ArrayList<>();
        File[] myFile = new File(ipath).listFiles();
        if (myFile == null) {
            file.add("..");

        } else
            for (File f : myFile) {
                // 过滤目录
                if (f.isDirectory()) {
                    String tempf = f.toString();
                    int pos = tempf.lastIndexOf("/");
                    String subTemp = tempf.substring(pos + 1, tempf.length());
                    file.add(subTemp);
                }
                // 过滤知道类型的文件
                if (f.isFile() && fileType != null) {
                    for (String aFileType : fileType) {
                        int typeStrLen = aFileType.length();
                        String fileName = f.getPath().substring(f.getPath().length() - typeStrLen);
                        if (fileName.toLowerCase().equals(aFileType)) {
                            file.add(f.toString().substring(path.length() + 1, f.toString().length()));
                        }
                    }
                }
            }

        if (file.size() == 0)
            file.add("..");
        return file;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == home.getId()) {
            path = getRootDir();
            if (type == TypeOpen)
                title.setText(path);
            Handler handler = new Handler();
            handler.post(add);
        } else if (v.getId() == back.getId()) {
            path = getSubDir(path);
            if (type == TypeOpen)
                title.setText(path);
            Handler handler = new Handler();
            handler.post(add);
        } else if (v.getId() == ok.getId()) {
            dismiss();
            if (type == TypeSave)
                path = path + "/" + et.getEditableText().toString() + ".wf";
            if (this.listener != null) {
                listener.pathChoose(path);
            }
        }

    }

    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取根目录
        }
        if (sdDir == null) {
            return null;
        }
        return sdDir.toString();

    }

    private String getRootDir() {
        String root = "/";

        path = getSDPath();
        if (path == null)
            path = "/";

        return root;
    }

    private String getSubDir(String path) {
        String subpath = null;

        int pos = path.lastIndexOf("/");

        if (pos == path.length()) {
            path = path.substring(0, path.length() - 1);
            pos = path.lastIndexOf("/");
        }

        subpath = path.substring(0, pos);

        if (pos == 0)
            subpath = path;

        return subpath;
    }

    public void setOkClickListener(OkClickListener listener) {
        this.listener = listener;
    }

    public interface OkClickListener {
        void pathChoose(String path);
    }
}
