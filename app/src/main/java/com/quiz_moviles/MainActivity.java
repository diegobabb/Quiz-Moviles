package com.quiz_moviles;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.database.sqlite.*;


public class MainActivity extends AppCompatActivity {
    public static SQLiteDatabase db;

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creamos la base de datos
        Toast.makeText(getApplicationContext(), "Creamos la base de datos", Toast.LENGTH_LONG).show();

        try {
            db = this.openOrCreateDatabase(
                    "matriculadb",
                    MODE_PRIVATE,
                    null);

            db.beginTransaction();

            db.execSQL("CREATE TABLE IF NOT EXISTS ESTUDIANTE(" +
                    "cedula text PRIMARY KEY, " +
                    "nombre text," +
                    "apellidos text," +
                    "edad integer );");

            db.execSQL("CREATE TABLE IF NOT EXISTS CURSO(" +
                    "codigo text PRIMARY KEY, " +
                    "descripcion text," +
                    "creditos integer );");

            db.execSQL("CREATE TABLE IF NOT EXISTS ESTUDIANTExCURSO(" +
                    "id integer PRIMARY KEY autoincrement," +
                    "cedula text," +
                    "codigo text );");

            db.setTransactionSuccessful(); //commit your changes
            db.endTransaction();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
