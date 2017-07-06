package com.esint.photopicker.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.esint.photopicker.Adapter.ViewPagerAdapter;
import com.esint.photopicker.Bean.Image;
import com.esint.photopicker.R;
import com.esint.photopicker.Util.ImageDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class ShowImageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout ll_points;
    private List<View> viewList = new ArrayList<>();
    private int count;
    private int currentIndex;
    private ImageDatabaseHelper helper;
    private List<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        helper = new ImageDatabaseHelper(ShowImageActivity.this, "Image.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("Image", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            // 遍历Cursor对象，取出数据并打印
            String uri = cursor.getString(cursor.getColumnIndex("uri"));
            Image image = new Image();
            image.setImgUri(uri);
            images.add(image);
            if (images.size() > 6) {
                for (int i = 0; i < images.size() - 6; i++) {
                    images.remove(i);
                }
            }
        }
        cursor.close();

        count = getIntent().getIntExtra("count", 0);
        currentIndex = getIntent().getIntExtra("position", 0);

        ll_points = (LinearLayout) this.findViewById(R.id.ll_ponits);

        viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        for (int i = 0; i < count; i++) {
            View view = LayoutInflater.from(ShowImageActivity.this).inflate(R.layout.viewpager_item, viewPager, false);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
            String uri = images.get(i).getImgUri();
            Glide.with(ShowImageActivity.this)
                    .load(uri)
                    .into(photoView);
            viewList.add(view);
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(viewList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
        setOvalLayout();
    }

    /**
     * 设置导航圆点
     */
    private void setOvalLayout() {
        for (int i = 0; i < count; i++) {
            ll_points.addView(LayoutInflater.from(ShowImageActivity.this).inflate(R.layout.point, null));
        }
        // 设置当前显示页数
        ll_points.getChildAt(currentIndex).findViewById(R.id.point).setBackgroundResource(R.drawable.point_focused);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 取消圆点选中
                ll_points.getChildAt(currentIndex).findViewById(R.id.point).setBackgroundResource(R.drawable.point_normal);
                // 圆点选中
                ll_points.getChildAt(position).findViewById(R.id.point).setBackgroundResource(R.drawable.point_focused);
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
