package com.dong.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dong.video.ui.BaseVideoViewActivity;
import com.dong.video.ui.window.WindowVideoViewActivity;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.entity.DecoderPlan;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView mInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mInfo = findViewById(R.id.tv_info);

        updateDecoderInfo();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.switchIjkPlayer:
                PlayerConfig.setDefaultPlanId(App.PLAN_ID_IJK);
                updateDecoderInfo();
                break;
            case R.id.switchMediaPlayer:
                PlayerConfig.setDefaultPlanId(PlayerConfig.DEFAULT_PLAN_ID);
                updateDecoderInfo();
                break;
            case R.id.switchExoPlayer:
                PlayerConfig.setDefaultPlanId(App.PLAN_ID_EXO);
                updateDecoderInfo();
                break;
            case R.id.inputUrlPlay:
//                intentTo(InputUrlPlayActivity.class);
                break;
        }
        return true;
    }

    private void updateDecoderInfo() {
        DecoderPlan defaultPlan = PlayerConfig.getDefaultPlan();
        mInfo.setText("当前解码方案为:" + defaultPlan.getDesc());
    }


    public void useBaseVideoView(View view){
        intentTo(BaseVideoViewActivity.class);
    }

    public void useWindowVideoView(View view){
        intentTo(WindowVideoViewActivity.class);
    }

    public void useFloatWindow(View view){
//        intentTo(FloatWindowActivity.class);
    }

    public void viewPagerPlay(View view){
//        intentTo(ViewPagerPlayActivity.class);
    }

    public void singleListPlay(View view){
//        intentTo(ListPlayActivity.class);
    }

    public void multiListPlay(View view){
//        intentTo(MultiListActivity.class);
    }

    public void shareAnimationVideos(View view){
//        intentTo(ShareAnimationActivityA.class);
    }


    private void intentTo(Class<? extends Activity> cls){
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
                default:
        }
    }
}
