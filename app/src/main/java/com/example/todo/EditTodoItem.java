package com.example.todo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditTodoItem extends AppCompatActivity {
    DatePickerDialog picker;
    private EditText due_date;
    private DBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private EditText todo_item_name;
    private String todo_name;
    String todo_id_int;
    private String due_date_string;
    private String todo_list_name;
    private int todo__item_id_int;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_todo_item);

        todo_item_name = (EditText)findViewById(R.id.todoItemName);
        due_date = (EditText)findViewById(R.id.todoDueDate);
        Intent intent = getIntent();

        todo_list_name = intent.getStringExtra("todo_list_name");
        todo_name = intent.getStringExtra("todo_name");
        todo_id_int = intent.getStringExtra("todo_id");
        String todo_item_id = intent.getStringExtra("todo_item_id");
        due_date_string = intent.getStringExtra("due_date");
        todo__item_id_int = Integer.parseInt(todo_item_id);

        todo_item_name.setText(todo_name);
        due_date.setText(due_date_string);

        due_date.setInputType(InputType.TYPE_NULL);
        due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(EditTodoItem.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                due_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });




    }
    public void saveChanges(View view) {
        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();

        todo_item_name = (EditText)findViewById(R.id.todoItemName);
        due_date = (EditText)findViewById(R.id.todoDueDate);
        String todo_item_name_string = todo_item_name.getText().toString();
        String due_date_string_new = due_date.getText().toString();

        if (due_date_string_new != due_date_string || todo_item_name_string != todo_name){
            String table_name = "todo_items";
            String[] columns = {"ID", "TODO_ITEM_NAME", "DUE_DATE"};
            String where = "TODO_ITEM_NAME = " + "'" + todo_item_name_string + "'" +"and TODO_ID = "+todo_id_int+" and ID != "+todo__item_id_int;
            String where_args[] = null;
            String group_by = null;
            String having = null;
            String order_by = null;
            Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                    having, order_by);
            if (c.getCount() == 0) {
                ContentValues cv = new ContentValues();
                cv.put("TODO_ITEM_NAME", todo_item_name_string); //These Fields should be your String values of actual column names
                cv.put("DUE_DATE", due_date_string_new);
                sdb.update("todo_items", cv, "id=" + todo__item_id_int, null);
                due_date_string = due_date_string_new;
                todo_name = todo_item_name_string;
                Intent intent = new Intent(EditTodoItem.this, TodoItem.class);
                intent.putExtra("todo_name", todo_list_name);
//                String item_id_string = Integer.toString(todo__item_id_int);
                intent.putExtra("todo_id", todo_id_int);
                startActivity(intent);

            }
            else {
                Toast.makeText(this, "Todo item name already found!", Toast.LENGTH_LONG).show();
            }



        }
        else {
            Toast.makeText(this, "No changes found!", Toast.LENGTH_LONG).show();
        }




    }
    public void deleteItem(View view) {
        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();

        sdb.execSQL("delete from todo_items where id="+todo__item_id_int);
        Intent intent = new Intent(EditTodoItem.this, TodoItem.class);
        intent.putExtra("todo_name", todo_list_name);
//                String item_id_string = Integer.toString(todo__item_id_int);
        intent.putExtra("todo_id", todo_id_int);
        startActivity(intent);



    }
    public void discard(View view) {

        Intent intent = new Intent(EditTodoItem.this, TodoItem.class);
        intent.putExtra("todo_name", todo_list_name);
//                String item_id_string = Integer.toString(todo__item_id_int);
        intent.putExtra("todo_id", todo_id_int);
        startActivity(intent);



    }

    public void listTodo(View view) {
        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Todo List");


        String table_name = "todo_list";
        String[] columns = {"ID", "TODO_NAME"};
        String where = "ID !="+todo_id_int;
        String where_args[] = null;
        String group_by = null;
        String having = null;
        String order_by = null;
        Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                having, order_by);
        c.moveToFirst();
        final String[] todo_list = new String[c.getCount()];
        final int[] todo_ids = new int[c.getCount()];
        Integer item_id = 0;
        for(int i = 0; i < c.getCount(); i++) {
            todo_list[i]=c.getString(1);
            todo_ids[i]=c.getInt(0);
            c.moveToNext();

        }

        builder.setItems(todo_list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String table_name = "todo_items";
                String[] columns = {"ID", "TODO_ITEM_NAME", "DUE_DATE","COMPLETED"};
                String where = "TODO_ID ="+todo_ids[which]+" and TODO_ITEM_NAME = '"+todo_name+"'" ;
                String where_args[] = null;
                String group_by = null;
                String having = null;
                String order_by = null;
                Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                        having, order_by);

                if (c.getCount() == 0){
                    ContentValues cv = new ContentValues();
                    cv.put("TODO_ID", todo_ids[which]); //These Fields should be your String values of actual column names
                    sdb.update("todo_items", cv, "id=" + todo__item_id_int, null);
                    Intent intent = new Intent(EditTodoItem.this, TodoItem.class);
                    intent.putExtra("todo_name", todo_list_name);
                    intent.putExtra("todo_id", todo_id_int);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(EditTodoItem.this,"Same todo item name exist in this todo" , Toast.LENGTH_SHORT).show();
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();




    }


}
