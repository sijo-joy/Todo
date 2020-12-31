package com.example.todo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NewTodoList extends AppCompatActivity {
    private DBOpenHelper tdb;
    private SQLiteDatabase sdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_todo_list_activity);

    }

    public void saveTodoList(View view) {
        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();
        EditText todo_name = (EditText) findViewById(R.id.todo_name);
        String str_todo_name = todo_name.getText().toString();
        if (str_todo_name.length() != 0) {
            String table_name = "todo_list";
            String[] columns = {"ID", "TODO_NAME", "NEAREST_DUE_DATE"};
            String where = "TODO_NAME = " + "'" + str_todo_name + "'";
            String where_args[] = null;
            String group_by = null;
            String having = null;
            String order_by = null;
            Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                    having, order_by);
            if (c.getCount() == 0) {
                ContentValues cv = new ContentValues();
                cv.put("TODO_NAME", str_todo_name);
                cv.put("NEAREST_DUE_DATE", "");
                sdb.insert("todo_list", null, cv);
                Toast.makeText(this, "Todo list Added successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NewTodoList.this, MainActivity.class);
                startActivity(intent);


            } else {
                Toast.makeText(this, "Todo list this name already exist!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Todo name can not be empty!", Toast.LENGTH_LONG).show();
        }
    }
    public void discard(View view) {
        Intent intent = new Intent(NewTodoList.this, MainActivity.class);
        startActivity(intent);
    }

}
