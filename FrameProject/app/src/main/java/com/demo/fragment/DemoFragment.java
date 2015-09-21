package com.demo.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.demo.frameproject.R;

import java.util.ArrayList;
import java.util.List;

import joe.frame.adapter.CommonAdapter;
import joe.frame.adapter.ViewHolder;
import joe.frame.annotations.ViewInject;
import joe.frame.fragment.FrameBaseFragment;

/**
 * Description
 * Created by chenqiao on 2015/7/20.
 */
public class DemoFragment extends FrameBaseFragment {

    @ViewInject(R.id.jump)
    private Button btn;

    @ViewInject(R.id.listView)
    private ListView listView;

    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.demo_weblayout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SecondFragment(), "Second");
            }
        });
        //要使Fragment重写Toolbar的菜单栏，需设置true
        setHasOptionsMenu(true);
        List<String> data = new ArrayList<>();
        data.add("hello world");
        data.add("it's a test");
        data.add("it's a demo");
        data.add("welcome frame");
        data.add("author Joe");

        CommonAdapter myAdapter = new CommonAdapter<String>(context, data, R.layout.demo_listitem) {
            @Override
            public void convert(ViewHolder holder, String item) {
                holder.setText(R.id.itemtextview, item);
            }
        };
        listView.setAdapter(myAdapter);
    }

    //重写Toolbar的菜单
    @Override
    protected void onCreateMyToolbarMenu(Menu menu, MenuInflater inflater) {
        //如果不进行clear，将会保留Activity的菜单（根据实际情况选择是否clear）
        menu.clear();
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    protected boolean onMyToolbarMenuItemClicked(MenuItem item) {
        return false;
    }
}