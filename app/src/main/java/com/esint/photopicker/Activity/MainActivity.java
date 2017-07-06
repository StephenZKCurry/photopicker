package com.esint.photopicker.Activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.esint.photopicker.Adapter.ImageAdapter;
import com.esint.photopicker.Bean.Image;
import com.esint.photopicker.R;
import com.esint.photopicker.Util.Common;
import com.esint.photopicker.Util.ImageDatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class MainActivity extends AppCompatActivity {

    private Button bt_add;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<Image> images = new ArrayList<>();
    private Uri imageUri;
    private ImageDatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        initData();

        bt_add = (Button) this.findViewById(R.id.bt_add);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindow();
            }
        });

        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageAdapter(MainActivity.this, images);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        helper = new ImageDatabaseHelper(MainActivity.this, "Image.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase(); // 创建数据库
        // 从数据库查询数据，初始化RecyclerView
        Cursor cursor = db.query("Image", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String uri = cursor.getString(cursor.getColumnIndex("uri"));
            Image image = new Image();
            image.setImgUri(uri);
            images.add(image);
            // 只显示最后添加的6张图片
            if (images.size() > 6) {
                for (int i = 0; i < images.size() - 6; i++) {
                    images.remove(i);
                }
            }
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.TAKE_PHOTO: {
                    Image image = new Image();
                    image.setImgUri(imageUri.toString());
                    images.add(image);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("uri", imageUri.toString());
                    db.insert("Image", null, values);
                    // 只显示最后添加的6张图片
                    if (images.size() > 6) {
                        for (int i = 0; i < images.size() - 6; i++) {
                            images.remove(i);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
                case Common.CHOOSE_PHOTO: {
//                    imageUri = data.getData();
//                    Image image = new Image();
//                    image.setImgUri(imageUri.toString());
//                    images.add(image);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (int i = 0; i < path.size(); i++) {
                        imageUri = Uri.fromFile(new File(path.get(i)));
                        Image image = new Image();
                        image.setImgUri(imageUri.toString());
                        images.add(image);
                        ContentValues values = new ContentValues();
                        values.put("uri", imageUri.toString());
                        db.insert("Image", null, values);
                    }
                    // 只显示最后添加的6张图片
                    int size = images.size();
                    if (images.size() > 6) {
                        for (int i = 0; i < size - 6; i++) {
                            images.remove(0); // 这里remove(i)的话会出现问题，因为每次remove掉一个元素，下一个元素的索引会减一
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    /**
     * 显示底部PopWindow
     */
    public void showPopwindow() {

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View view = inflater.inflate(R.layout.popwindow, null);
        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.pop_anim);
        // 在底部显示
        window.showAtLocation(bt_add, Gravity.BOTTOM, 0, 0);

        // popWindow里的button点击事件
        Button bt_camera = (Button) view.findViewById(R.id.bt_camera);
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File image = new File(getExternalCacheDir(), new Date().getTime() + ".jpg");
                try {
                    if (image.exists()) {
                        image.delete();
                    }
                    image.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.cameraalbumtest.fileprovider", image);
                } else {
                    imageUri = Uri.fromFile(image);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, Common.TAKE_PHOTO);
                window.dismiss();
            }
        });

        Button bt_picture = (Button) view.findViewById(R.id.bt_picture);
        bt_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("android.intent.action.GET_CONTENT");
//                intent.setType("image/*");
//                startActivityForResult(intent, Common.CHOOSE_PHOTO);
//                window.dismiss();
                Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 6);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                startActivityForResult(intent, Common.CHOOSE_PHOTO);
                window.dismiss();
            }
        });

        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

        // 点击空白处隐藏popwindow
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

    }
}
