package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView todo_lv;
    private ArrayList<String> al_strings;
    private ArrayAdapter<String> aa_strings;
    private DBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private ArrayList<CustomTodoList> al_items;
    private ArrayAdapterTodoList caa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todo_lv = (ListView) findViewById(R.id.todo_IV);

        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();

        al_items = new ArrayList<CustomTodoList>();
        // create an array adapter for al_strings and set it on the listview
        caa = new ArrayAdapterTodoList(this, al_items);
        todo_lv.setAdapter(caa);
        String table_name = "todo_list";
        String[] columns = {"ID", "TODO_NAME", "NEAREST_DUE_DATE"};
        String where = null ;
        String where_args[] = null;
        String group_by = null;
        String having = null;
        String order_by = null;
        Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                having, order_by);
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            int todo_list_id  = c.getInt(0);
            String todo_list_id_string = Integer.toString(todo_list_id);
            al_items.add(new CustomTodoList(c.getString(1),c.getString(2),todo_list_id_string));

            c.moveToNext();
        }
        caa.notifyDataSetChanged();
        todo_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // overridden method that we must implement to get access to short clicks
            public void onItemClick(AdapterView<?> adapterview, View view, int pos,
                                    long id) {
                CustomTodoList item_obj  =(CustomTodoList) adapterview.getItemAtPosition(pos);
                String todo_name = item_obj.getName();
                String todo_date = item_obj.getDate();
                String table_name = "todo_list";
                String[] columns = {"ID", "TODO_NAME"};
                String where = "TODO_NAME = "+"'"+ todo_name+"'" ;
                String where_args[] = null;
                String group_by = null;
                String having = null;
                String order_by = null;
                Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                        having, order_by);
                c.moveToFirst();
                Integer item_id = 0;
                for(int i = 0; i < c.getCount(); i++) {
                    item_id = c.getInt(0);
                }
            // update the text view to state that a short click was made here
                Intent intent = new Intent(MainActivity.this, TodoItem.class);
                intent.putExtra("todo_name", todo_name);
//                intent.putExtra("todo_date", todo_date);
                String item_id_string = Integer.toString(item_id);
                intent.putExtra("todo_id", item_id_string);
                startActivity(intent);
            }
        });


    }
    public void toNewTodoActivity(View view) {

        Intent intent = new Intent(MainActivity.this, NewTodoList.class);
        startActivity(intent);



        }
}
