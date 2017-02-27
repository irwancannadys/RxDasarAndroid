package com.domikado.rxdasarandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private TextView tMain;
    private Button btn_do_subscribe, btn_do_subscribe2;
    private RadioButton radio_basic;
    private RadioButton radio_map;
    private RadioButton radio_more_map;
    private RadioGroup radio_active;
    private RadioButton custom_data;
    SetGet setGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tMain = (TextView) findViewById(R.id.tMain);
        btn_do_subscribe = (Button) findViewById(R.id.btn_do_subscribe);
        btn_do_subscribe2 = (Button) findViewById(R.id.button);
        radio_basic = (RadioButton) findViewById(R.id.radio_basic);
        radio_map = (RadioButton) findViewById(R.id.radio_map);
        radio_more_map = (RadioButton) findViewById(R.id.radio_more_map);
        custom_data = (RadioButton) findViewById(R.id.custom_data);
        radio_active = (RadioGroup) findViewById(R.id.radio_active);

        final Observable<String> myobs = Observable.just("Hello say");
        final Observable<Integer> myNumb = Observable.just(1,2,3,4,5);

        //menggunakan cara pertama
        myobs.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                tMain.setText(s);
            }
        });
        //menggunakan cara kedua dengan action button
        btn_do_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myobs.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        tMain.append(" " + s);
                    }
                });
            }
        });


        //menggunakan .map untuk filtering suatu data
        myobs.map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s){
                return s.hashCode();
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                tMain.setText(String.valueOf(integer));
            }
        });


        //menggunakan model
        setGet = new SetGet("irwancannady", "irwancannady@gmail.com");
        final Observable<SetGet> setGetObservable = Observable.just(setGet);

        final Observable<List<SetGet>> listObservable = Observable.create(new Observable.OnSubscribe<List<SetGet>>() {
            @Override
            public void call(Subscriber<? super List<SetGet>> subscriber) {
                for (int i = 0; i < 5; i++) {
                    List<SetGet> data = new ArrayList<SetGet>();
                    data.add(new SetGet("SetGet" + Integer.toString(i), "email@mail.com"));
                    subscriber.onNext(data);
                }
            }
        });

        btn_do_subscribe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = radio_active.getCheckedRadioButtonId();
                switch (id){
                    case R.id.radio_basic:
                        myobs.subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                tMain.setText(s);
                            }
                        });
                        break;

                    case R.id.radio_map:
                        setGetObservable.map(new Func1<SetGet, String>() {
                            @Override
                            public String call(SetGet SetGet) {
                                return "Nama : " + SetGet.nama + "\n" + "Email : " + SetGet.alamat;
                            }
                        }).subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Toast.makeText(MainActivity.this, s , Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case R.id.radio_more_map:
                        final StringBuilder stringBuilder = new StringBuilder();
                        myNumb.map(new Func1<Integer, Integer>() {
                            @Override
                            public Integer call(Integer integer){
                                return integer + 1;
                            }
                        }).map(new Func1<Integer, String>() {
                            @Override
                            public String call(Integer integer){
                                int origin = integer - 1;
                                stringBuilder.append("Angka " + origin + " ditambah 1 = " );
                                stringBuilder.append(integer + "\n");
                                return stringBuilder.toString();
                            }
                        }).subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                tMain.setText(s);
                            }
                        });
                        break;

                    case R.id.custom_data:
                        final StringBuilder sb = new StringBuilder();
                        listObservable.map(new Func1<List<SetGet>, String>() {
                            @Override
                            public String call(List<SetGet> SetGets) {
                                for (int i = 0; i < SetGets.size(); i++) {
                                    sb.append("Nama : " + SetGets.get(i).nama + "\n" + "Email : " + SetGets.get(i).alamat + "\n");
                                }
                                return sb.toString();
                            }
                        }).subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                tMain.setText(s);
                            }
                        });
                        break;
                }
            }
        });
    }
}
