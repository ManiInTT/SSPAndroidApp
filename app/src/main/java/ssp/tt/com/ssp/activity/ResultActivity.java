package ssp.tt.com.ssp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.GridSpacingItemDecoration;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.adapter.ResultMenuAdapter;
import ssp.tt.com.ssp.model.ResultMenu;

public class ResultActivity extends BaseActivity {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;

    int[] menuIcon = {
            R.string.icon_wallet,
            R.string.icon_result,
            R.string.icon_ticket};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        registerBaseActivityReceiver();
        ButterKnife.bind(this);
        getWidgetConfig();
        setListView();
    }

    private void setListView() {
        ArrayList<ResultMenu> menuList = new ArrayList<>();
        menuList.add(new ResultMenu("My result", menuIcon[0]));
        menuList.add(new ResultMenu("Block result", menuIcon[1]));
        menuList.add(new ResultMenu("Overall", menuIcon[2]));
        ResultMenuAdapter menuAdapter = new ResultMenuAdapter(menuList);
        int spacingInPixels = 20;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));
        recyclerView.setAdapter(menuAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    if (position == 0) {
                        Intent intent = new Intent(ResultActivity.this, MyResult.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(ResultActivity.this, MyBlockResult.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(ResultActivity.this, OverAllBlock.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }


    private void getWidgetConfig() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_result));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void callbackReturn(String response) {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }


}
