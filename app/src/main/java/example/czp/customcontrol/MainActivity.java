package example.czp.customcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import example.czp.customcontrol.canvas.PieView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private PieView pieView;
    private boolean isRunning=false;
    private int[] i=new int[]{0,2,5,7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView= findViewById(R.id.image);
        pieView = findViewById(R.id.zpan);
        TextView title = findViewById(R.id.tv_title);
        title.setText("王者荣耀抽英雄啦");

        Intent intent = getIntent();
        pieView.setValue(intent.getStringExtra("k1"),intent.getStringExtra("k2"));

        pieView.setListener(new PieView.RotateListener() {
            @Override
            public void value(String s) {
                isRunning=false;
                new AlertDialog.Builder(MainActivity.this)
                            .setTitle("鹿死谁手呢？")
                            .setMessage(s)
                            .setIcon(R.drawable.f015)
                            .setNegativeButton("退出",null)
                            .show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRunning){
                    Random random = new Random();
                    //pieView.rotate(i[random.nextInt(4)]);
                    pieView.rotate(1);
                }
                isRunning=true;
            }
        });


    }


}
