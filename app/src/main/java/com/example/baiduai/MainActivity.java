package com.example.baiduai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {


    private ImageView picture;
    private static Bitmap carm;
    private static String path;
    private static  Bitmap answer;
    private Uri imageuri;
    private static int choice;
    private static Bitmap allanswer;

    private static CheckBox xbCBox;
    private static CheckBox ageCBox;
    private static CheckBox syCBox;
    private static CheckBox xyCBox;

    private String xb;
    private String  age;
    private String Uwear;
    private String Lwear;


    ArrayList<String> watermark = new ArrayList<String>();




     //UI改变只在主线程中

    public Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                    ImageView bodyimage = (ImageView) findViewById(R.id.body);
                    bodyimage.setImageBitmap(answer);
                    break;
                case 2:
                    xbCBox.setText(xb );
                    ageCBox.setText(age);
                    syCBox.setText(Uwear);
                    xyCBox.setText(Lwear);
                    break;
                default:
                    break;

            }
        }



    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //相册选择
        final DrawerLayout mainlayout = (DrawerLayout) findViewById(R.id.draw_mainlayout);

        final Button camera = (Button) findViewById(R.id.take_photo);
        Button chooseFromalbum = (Button) findViewById(R.id.choose_from_album);
        Button back = (Button) findViewById(R.id.background);
        Button save = (Button) findViewById(R.id.save);

        Button  shuiy = (Button) findViewById(R.id.sy_button);

        SeekBar bhsee = (SeekBar) findViewById(R.id.bh);
        SeekBar ldsee = (SeekBar) findViewById(R.id.ld);
        SeekBar dbsee = (SeekBar) findViewById(R.id.db);

        xbCBox = (CheckBox) findViewById(R.id.xb_CBox);
        ageCBox = (CheckBox) findViewById(R.id.age_CBox);
        syCBox = (CheckBox) findViewById(R.id.sy_CBox);
        xyCBox = (CheckBox) findViewById(R.id.xy_CBox);


        TextView help = (TextView) findViewById(R.id.help);
        picture = (ImageView) findViewById(R.id.body);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        TextView watertext = (TextView) findViewById(R.id.water_text);

        watertext.bringToFront();

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                mainlayout.openDrawer(GravityCompat.START);
            }
        });

        shuiy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(xbCBox.isChecked()){
                    watermark.add(xbCBox.getText().toString().trim());
                }
                if(ageCBox.isChecked()){
                    watermark.add(ageCBox.getText().toString().trim());
                }
                if(syCBox.isChecked()){
                    watermark.add(syCBox.getText().toString().trim());
                }
                if(xyCBox.isChecked()){
                    watermark.add(xyCBox.getText().toString().trim());
                }

                Bitmap bmp = Bitmap.createBitmap(allanswer.getWidth(),allanswer.getHeight(),allanswer.getConfig());
                Canvas canvas = new Canvas(bmp);

               // 建立画笔
                Paint photoPaint = new Paint();
                // 获取更清晰的图像采样，防抖动
                photoPaint.setDither(true);
                // 过滤一下，抗剧齿
                photoPaint.setFilterBitmap(true);

                canvas.drawBitmap(allanswer,0,0,photoPaint);
                canvas.translate(allanswer.getWidth()/10,allanswer.getHeight()*2/3); //改画布中心点

                TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
                int TEXT_SIZE = 15;
                textPaint.setTextSize(TEXT_SIZE);// 字体大小
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
                textPaint.setColor(Color.argb(255,0,0,0));// 采用的颜色

                String textString="";

                Log.d("MainActivity","lhx 79 22 ");

                while(!watermark.isEmpty()){
                    textString += watermark.get(0)+'\n';
                    watermark.remove(0);

                }

             //   watermark.clear();

                Log.d("MainActivity","lhx 79 22 "+textString);



               StaticLayout staticLayout=new StaticLayout(textString, textPaint, 100,
                        Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);
                staticLayout.draw(canvas);

                ImageView here = (ImageView) findViewById(R.id.choice1);
                here.setImageBitmap(bmp);
                allanswer = bmp ;
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("使用介绍：");
                dialog.setMessage("有两种方式识别人体，在选择背景，融合，可调节图片属性，再保存");
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });


        bhsee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Bitmap bmp = Bitmap.createBitmap(allanswer.getWidth(),allanswer.getHeight(),allanswer.getConfig());
                ColorMatrix cMatrix = new ColorMatrix();
                cMatrix.setSaturation((float)(i/100.0));

                Paint paint =new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

                Canvas canvas = new Canvas(bmp);
                canvas.drawBitmap(allanswer,0,0,paint);

                ImageView here = (ImageView) findViewById(R.id.choice1);
                here.setImageBitmap(bmp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ldsee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Bitmap bmp = Bitmap.createBitmap(allanswer.getWidth(),allanswer.getHeight(),allanswer.getConfig());
                int brightness = i - 127;
                ColorMatrix cMatrix = new ColorMatrix();
                cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,
                        0, 0, brightness,// 改变亮度
                        0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });

                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

                Canvas canvas = new Canvas(bmp);
                // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
                canvas.drawBitmap(allanswer, 0, 0, paint);
                ImageView here = (ImageView) findViewById(R.id.choice1);
                here.setImageBitmap(bmp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        dbsee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Bitmap bmp = Bitmap.createBitmap(allanswer.getWidth(),allanswer.getHeight(),allanswer.getConfig());
                // int brightness = progress - 127;
                float contrast = (float) ((i + 64) / 128.0);
                ColorMatrix cMatrix = new ColorMatrix();
                cMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0,
                        contrast, 0, 0, 0,// 改变对比度
                        0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });

                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

                Canvas canvas = new Canvas(bmp);
                // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
                canvas.drawBitmap(allanswer, 0, 0, paint);

                ImageView here = (ImageView) findViewById(R.id.choice1);
                here.setImageBitmap(bmp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });













        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                File file = new File(getExternalCacheDir(),"output.jpg");
                try{
                    if(file.exists()){
                        file.delete();
                    }
                    file.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }

                if(Build.VERSION.SDK_INT>=24){
                    imageuri = FileProvider.getUriForFile(MainActivity.this,"com.example.baiduai.fileprovider",file);
                }
                else imageuri = Uri.fromFile(file);

                Intent next = new Intent("android.media.action.IMAGE_CAPTURE");
                next.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
                startActivityForResult(next,1);
            }
        });





        chooseFromalbum.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    choice = 1;
                    openAlbum();
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    choice = 2;
                    openAlbum();
                }
            }
        });


        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".jpg");
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    allanswer.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    System.out.println("___________保存的__sd___下_______________________");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this,"保存已经至"+ Environment.getExternalStorageDirectory()+"下", Toast.LENGTH_SHORT).show();
            }



        });






    }













    public void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,2);

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    try{
                        Log.d("MainActivity","woshisb");
                        String ccc = null;
                        Uri uri = imageuri;
                        //改了最小版本  原来15 现19

                     /*

                        Log.d("MainActivity","woshisb3 "+ uri.toString());
                        ccc = getImagePath(uri,null);

                        Log.d("MainActivity","woshisb9" + ccc);

                        path = ccc;

                        Log.d("MainActivity","woshisb5");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("MainActivity","woshisb6");
                                String a = getAuth();

                                Log.d("MainActivity","lhx hhh"+a);

                                String b = bodySeg(a);

                                Log.d("MainActivity","lhx ggg"+b);


                                parseJSONWithJSONObiect(b);

                            }
                        }).start();

                        */
                       Bitmap bitmap =BitmapFactory.decodeStream(getContentResolver().openInputStream(imageuri));
                       picture.setImageBitmap(bitmap);


                       File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".jpg");
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            System.out.println("___________保存的__sd___下_______________________");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this,"保存已经至"+ Environment.getExternalStorageDirectory()+"下", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){

                        System.err.printf("获取path失败！");
                        e.printStackTrace(System.err);
                    }

                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    handleImageOnKitKat(data);
                }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requesyCode,String[] permissions,int[] grantResults){
        switch (requesyCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else {
                    Toast.makeText(this,"no permission",Toast.LENGTH_SHORT).show();
                }
        }
    }



    public void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();

        //改了最小版本  原来15 现19

        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id =docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath =  getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }

        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        path = imagePath;
        Log.d("MainActivity","lhx 711  "+ uri.toString());


        Log.d("MainActivity","lhx 711 2 "+ path);

        //网络访问在子线程中


        if(choice == 1){
            if(imagePath !=  null){
                Bitmap bitmap =BitmapFactory.decodeFile(imagePath);

                ImageView c2 = (ImageView) findViewById(R.id.water_image);
                c2.setImageBitmap(bitmap);

                //       picture.setImageBitmap(bitmap);
            }
            else{
                Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String a = getAuth();

                    Log.d("MainActivity","lhx hhh"+a);

                    String b = bodySeg(a);
                    String c = bodyAttr(a);

                    Log.d("MainActivity","lhx ggg"+b);
                    Log.d("MainActivity","lhx 79"+c);


                    parseJSONWithJSONObiect(b);
                    parseJSONWithJSONObiect1(c);


                }
            }).start();

        }
        else{
            displayImage(imagePath);
        }








    }

    public String getImagePath(Uri uri,String selection){
        String path= null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                Log.d("MainActivity","lhx 711 22");
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Log.d("MainActivity","lhx 711 26");
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath !=  null){
            Bitmap bitmap =BitmapFactory.decodeFile(imagePath);
            Bitmap lhx = mergeBitmap(bitmap,answer);

            ImageView c2 = (ImageView) findViewById(R.id.choice1);
            c2.setImageBitmap(lhx);
            allanswer = lhx;

     //       picture.setImageBitmap(bitmap);
        }
        else{
            Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap mergeBitmap(Bitmap first,Bitmap second){
        Bitmap bitmap = Bitmap.createBitmap(first.getWidth(),first.getHeight(),first.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(first,new Matrix(),null);
        canvas.drawBitmap(second,0,0,null);
        return bitmap;
    }














  //解码JSON

    private void parseJSONWithJSONObiect1(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("person_info");
            String a = jsonArray.getJSONObject(0).toString();

            JSONObject jsonObject2 = new JSONObject(a);
            JSONObject jsonObject3 = jsonObject2.getJSONObject("attributes");
            String b = jsonObject3.getJSONObject("gender").getString("name");

            xb = b;
            age = jsonObject3.getJSONObject("age").getString("name");
            Uwear = jsonObject3.getJSONObject("upper_wear").getString("name")+jsonObject3.getJSONObject("upper_wear_fg").getString("name");
            Lwear = jsonObject3.getJSONObject("lower_wear").getString("name");

            Log.d("MainActivity","lhx 79 3"+xb+age+Uwear+Lwear);
            try{
                Message message = new Message();
                message.what=2;
                handler.sendMessage(message);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObiect(String jsonData){
        try{
          //  JSONArray jsonArray = new JSONArray(jsonData);



            JSONObject jsonObject = new JSONObject(jsonData);
            String body1 = jsonObject.getString("foreground");


            Log.d("MainActivity","lhx 79 2"+body1);


 //           if (body1.contains(",")) {
  //              String[] base64ImgArray = body1.split(",");
   //             if (base64ImgArray.length == 2) {
   //                 body1 = base64ImgArray[1];
   //             }
   //         }
// 解码
            try {
                byte[] decodedString = Base64.decode(body1, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                answer = decodedByte;
        //        ImageView bodyimage = (ImageView) findViewById(R.id.body);
         //       bodyimage.setImageResource(R.drawable.timg);

                Message message = new Message();
                message.what =1;
                handler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
            }












        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
























  //申请Access token

    public static String getAuth() {
        // 官网获取的 API Key
        String clientId = "k6866kGV17kIHgystBs1ofrv";
        // 官网获取的 Secret Key
        String clientSecret = "RKuGRc9zccGpcFqUVBPkAlTPxpo6srqj";
        return getAuth(clientId, clientSecret);
    }

    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + ak
                + "&client_secret=" + sk;

        Log.d("MainActivity","woshisb7");

        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }



    public static String bodyAttr(String token) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_attr";
        try {
            // 本地文件路径
            String filePath = path;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            String accessToken = token;

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //直接访问

    public static String bodySeg(String token) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_seg";
        try {
            // 本地文件路径
            String filePath = path;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;


            String accessToken = token;

            String result = HttpUtil.post(url, accessToken, param);
            out.println(result);
            return result;
        } catch (Exception e) {
            System.err.printf("获取picture失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }





















































}
