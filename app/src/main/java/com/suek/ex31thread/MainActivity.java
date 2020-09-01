package com.suek.ex31thread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView tv;
    int num;     //텍스트뷰에 보여질 숫자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv= findViewById(R.id.tv);

    }



    public void clickBtn(View view) {
        //오래걸리는 작업(ex;네트워크 작업)
        //별도의 'Thread 를 만들지 않았기에 MainThread 가 이 작업을 수행'
        //버튼 눌렀을대 반복문..
        for(int i=0; i<20; i++){
            num++;
            tv.setText(num + "");    //num 은 int 형이기 때문에 문자열로 바꿔주기-> 빈문자열추가
            //MainThread 가 이 반복문안에서만 작업중이어서 TextView 에
            //num 값을 보여주는 갱신작업을 수행할 수 없음!!
            //그래서 num 값이 증가되는 모습이 보여지지 않고 이 반복문이
            //끝난 후 마지막 num 값인 20만 보여짐.
            //그래서 오래걸리는 작업은 MainThread 가 하지 않도록 해야함
            //즉, 별도의 Thread 가 작업하도록..

            //0.1초(500millis)동안 잠시대기
            try {
                Thread.sleep(500);   //0.001초 (millisecond) =1000millis==1초
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }//for

    }//clickBtn







    public void clickBtn2(View view) {
        // 1)  오래걸리는 작업 수행..(ex; network, db 작업)
        //하는 직원객체(MyThread) 생성 및 실행
        MyThread t= new MyThread();
        t.start();   //직원에게 작업수행을 지시!! [이 클래스의 run()이 실행]

    }


    // 2)  오래걸리는 작업을 수행하는 Thread 의 동작을 설계
    class MyThread extends Thread{
        //이 클래스 객체를 start()하면 자동으로 실행되는 메소드-> run() 오버라이드
        @Override
        public void run() {
            //이 스레드가 해야할 작업을 코딩
            for(int i=0; i<20; i++){
                num++;

                //화면에 보여지는 작업 수행..
               // tv.setText(num+"");   //하위버전들은 여기서 에러남!
                //UI(화면작업)은 반드시 MainThread 만 할 수 있도록 강제화 되어있음..
                //즉, 별도의 스레드(MyThread 같은..)는 화면변경작업을 수행할 수 없기에..
                //MainThread 에게 화면변경 작업수행을 요청하도록 코딩..

                // 방법1.  Handler 객체를 이용      --> 아랫줄에서 Handler 객체 참조..
                //handler.sendEmptyMessage(0);   //Handler 를 통해 빈메세지를 Message Queue 에 보내기.  //식별번호 아무숫자나

                // 방법2.  runOnUiThread()메소드(Activity 클래스의 멤버)를 이용
                //  UI 변경작업을 수행 할 수 있도록 Main 으로부터
                //  위임장을 받을 Runnable 인터페이스를 구현한 별로 Thread 객체생성
                Runnable runnable= new Runnable() {
                    @Override
                    public void run() {

                    }
                };

                // 위임장을 주는 기능 메소드 실행
                runOnUiThread(runnable);    //파라미터로 전달한 runnable 객체에게 UI 변경작업이 가능하도록 위임장 부여


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            super.run();  //아무 의미없음-지워도됨
        }//run()
    }//MyThread





    // 방법1.
    // 별도의 Thread 가 Main Thread 에게 UI 변경작업을 요청할 때
    // 활용될 객체
    Handler handler= new Handler(){
        //handler.sendEmptyMessage()를 실행하여 MainThread 가
        //이 메세지를 처리하면 자동으로 실행되는 메소드-> handleMessage
        @Override                                     //handler.sendEmptyMessage()를 실행하면 여기 handleMessage 메소드가 실행됨됨
        public void handleMessage(@NonNull Message sg) {
            //이 곳에서 UI 변경작업 가능함
            tv.setText(num+"");

        }
    };



}//MainActivity class
