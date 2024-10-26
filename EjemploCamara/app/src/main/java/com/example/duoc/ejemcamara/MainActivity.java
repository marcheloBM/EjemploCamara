package com.example.duoc.ejemcamara;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import static com.example.duoc.ejemcamara.ToDoDbHelper.*;

public class MainActivity extends Activity {

    private String[] projection = {TAREA_NOMBRE, TAREA_TIPO, TAREA_VALOR, TAREA_STOCK, TAREA_FECHA_CREACION};

    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private ImageView imageView;
    //metodo q se ejecuta al onclip
    //despacha la toma a
    public void tomarFoto(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            //ImageView imageView = ((ImageView)findViewById(R.id.imageView));
            //imageView.setImageBitmap(bitmap);

            //pasamos el bitmap a base64 (a un String)
            String imgTemp =BitmapToString(bitmap);
            //TextView text = (TextView)findViewById(R.id.textView);
            //text.setText(imgTemp);

            Bitmap img =StringToBitMap(imgTemp);
            imageView.setImageBitmap(img);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Recuperamos el ImageView
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public void guardarProducto(View view){
        //recuperacion valores de controles
        String Nombre = ((EditText)findViewById(R.id.Nombre)).getText().toString();
        String Tipo = ((EditText)findViewById(R.id.Tipo)).getText().toString();
        int Valor = Integer.parseInt(((EditText) findViewById(R.id.Valor)).getText().toString());
        int Stock = Integer.parseInt(((EditText) findViewById(R.id.Stock)).getText().toString());
        String imgTemp =BitmapToString(bitmap);




        //codigo SQLite
        ToDoDbHelper toDoDbHelper = new ToDoDbHelper(this);
        SQLiteDatabase db = toDoDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TAREA_NOMBRE, Nombre);
        cv.put(TAREA_TIPO, Tipo);
        cv.put(TAREA_VALOR, Valor);
        cv.put(TAREA_STOCK, Stock);
        cv.put(TAREA_IMAGEN,imgTemp);


        //nombre de la tabla, nullhack, valores
        db.insert(TAREA_TABLE, null, cv);

        //notifica la creacion con un TOAST
        Toast.makeText(this, "Producto Guardado", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, ListarDatos.class);
        startActivity(intent);
    }
}
