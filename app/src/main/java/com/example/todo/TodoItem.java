package com.example.todo;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class TodoItem extends AppCompatActivity implements ArrayAdapterTodoItem.OnItemClickListener {
    private static LayoutInflater inflater = null;
    private DBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private String todo_name;
    private int total_items;
    private int completed_items;
    private TextView tv_display;
    private ListView lv_mainlist;
    private EditText et_new_strings;
    private ArrayList<CustomTodoItem> al_items;
    private ArrayList<CustomTodoItem> al_temp;
    private ArrayAdapterTodoItem caa;
    String todo_id;
    Integer todo_id_int;

    DatePickerDialog picker;
    EditText due_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_item);


        EditText todo_item_name = (EditText) findViewById(R.id.todo_item_name);

        todo_item_name.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(todo_item_name, InputMethodManager.SHOW_IMPLICIT);


        TextView textView = (TextView)findViewById(R.id.todo_name);
        lv_mainlist = (ListView) findViewById(R.id.todo_items);


        Intent intent = getIntent();
        todo_name = intent.getStringExtra("todo_name");
        todo_id = intent.getStringExtra("todo_id");
        todo_id_int = Integer.parseInt(todo_id);

        textView.setText(todo_name);

        due_date=(EditText) findViewById(R.id.due_date);
        due_date.setInputType(InputType.TYPE_NULL);
        due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(TodoItem.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                due_date.setText( (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });







        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();


        al_items = new ArrayList<CustomTodoItem>();
// create an array adapter for al_strings and set it on the listview
        caa = new ArrayAdapterTodoItem(this, al_items,this);
        lv_mainlist.setAdapter(caa);
        String table_name = "todo_items";
        String[] columns = {"ID", "TODO_ITEM_NAME", "DUE_DATE","COMPLETED"};
        String where = "TODO_ID ="+todo_id_int ;
        String where_args[] = null;
        String group_by = null;
        String having = null;
        String order_by = "COMPLETED asc";
        Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                having, order_by);
        c.moveToFirst();
        total_items = 0;
        completed_items = 0;
        for(int i = 0; i < c.getCount(); i++) {
            String date  = c.getString(2);
            Integer todo_item_id = c.getInt(0);
            String todo_item_id_string =  Integer.toString(todo_item_id);

            int complete_int = Integer.parseInt(c.getString(3));
            boolean complete_bo = false;
            total_items++;
            if (complete_int == 1) {
                complete_bo = true;
                completed_items++;
            }
            else{
                complete_bo = false;
                }

            al_items.add(new CustomTodoItem(c.getString(1),c.getString(2),complete_bo,todo_item_id_string));

            c.moveToNext();
        }
        TextView total_items_count =(TextView) findViewById(R.id.totalItemsCount);
        TextView completed_items_count =(TextView) findViewById(R.id.completedItemsCount);
        total_items_count.setText(Integer.toString(total_items));
        completed_items_count.setText(Integer.toString(completed_items));

        caa.notifyDataSetChanged();
        lv_mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View row,    int position, long rowid)
            {
                // TODO Auto-generated method stub
                CustomTodoItem item_obj  =(CustomTodoItem) parentAdapter.getItemAtPosition(position);
                String todo_item_name = item_obj.getName();
                String todo_date = item_obj.getDate();
                String todo_item_id = item_obj.getTodoId();
                Intent intent = new Intent(TodoItem.this, EditTodoItem.class);
                intent.putExtra("todo_name", todo_item_name);
                intent.putExtra("due_date", todo_date);
                intent.putExtra("todo_item_id", todo_item_id);
                intent.putExtra("todo_id", todo_id);

                intent.putExtra("todo_list_name", todo_name);
                startActivity(intent);
            }
        });
        //Defining an item click listener


    }


    public void renameTodoName(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();
        final TextView edit_text_todo_name = (TextView) findViewById(R.id.todo_name);
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New name for todo")
//                .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String new_name = String.valueOf(taskEditText.getText());
                        if (new_name == todo_name){
                            alert.setTitle("Name not changed");
                            alert.setMessage("Give a new name");
                            alert.show();
//                            Toast.makeText(this, "Change name before saving!", Toast.LENGTH_LONG).show();
//                            edit_text_todo_name.setText(new_name);
//                            System.exit(0);
                        }
                        if (new_name.length() == 0) {
                            alert.setTitle("Empty name");
                            alert.setMessage("Name can not be empty!");
                            alert.show();
//                            edit_text_todo_name.setText(todo_name);
//                            System.exit(0);
                        }
                        if (todo_name != new_name & (new_name.length() != 0)){
                            String table_name = "todo_list";
                            String[] columns = {"ID", "TODO_NAME", "NEAREST_DUE_DATE"};
                            String where = "TODO_NAME = " + "'" + new_name + "'";
                            String where_args[] = null;
                            String group_by = null;
                            String having = null;
                            String order_by = null;
                            Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                                    having, order_by);
                            if (c.getCount() == 0) {
                                ContentValues cv = new ContentValues();
                                cv.put("TODO_NAME",new_name);
                                sdb.update(table_name, cv, "id="+todo_id_int, null);
                                edit_text_todo_name.setText(new_name);
                                todo_name = new_name;
                                Intent intent = new Intent(TodoItem.this, TodoItem.class);
                                intent.putExtra("todo_name", todo_name);
//                                intent.putExtra("todo_date", todo_date);
//                                String item_id_string = Integer.toString(todo_id);
                                intent.putExtra("todo_id", todo_id);
                                startActivity(intent);
//                                System.exit(0);

                            }
                            else {
                                alert.setTitle("Duplicated name");
                                alert.setMessage("Todo name already exist!");
                                alert.show();

//                                Toast.makeText(this, "jabbed", Toast.LENGTH_LONG).show();

//                                edited_todo_name.setText(todo_name);
                            }
                            }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();







        }

    public void saveTodoItem(View view) {
        tdb = new DBOpenHelper(this, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();
        EditText todo_name = (EditText) findViewById(R.id.todo_item_name);
        String str_todo_name = todo_name.getText().toString();
        due_date = (EditText) findViewById(R.id.due_date);
        String str_due_date = due_date.getText().toString();
        if (str_todo_name.length() != 0) {
            String table_name = "todo_items";
            String[] columns = {"ID", "TODO_ITEM_NAME", "DUE_DATE"};
            String where = "TODO_ITEM_NAME = " + "'" + str_todo_name + "'" +"and TODO_ID = "+todo_id_int;
            String where_args[] = null;
            String group_by = null;
            String having = null;
            String order_by = null;
            Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                    having, order_by);
            if (c.getCount() == 0) {
                ContentValues cv = new ContentValues();
                cv.put("TODO_ITEM_NAME", str_todo_name);
                cv.put("DUE_DATE", str_due_date);
                cv.put("TODO_ID", todo_id_int);
                cv.put("COMPLETED", 0);
                sdb.insert(table_name, null, cv);
                Toast.makeText(this, "Todo item Added successful", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());


            } else {
                Toast.makeText(this, "Todo item name already exist!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Todo item name can not be empty!", Toast.LENGTH_LONG).show();
        }
    }
    public void discard(View view) {
        Intent intent = new Intent(TodoItem.this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public Void onItemClick(CustomTodoItem object) {

        al_temp = new ArrayList<CustomTodoItem>();

        int size_arr = al_items.size();
        if (size_arr >= 1){
            int itemPos = al_items.indexOf(object);
            al_items.remove(itemPos);
            al_items.add(object);
            caa.notifyDataSetChanged();
        }
//        Intent intent = new Intent(TodoItem.this, MainActivity.class);
//        startActivity(intent);
        return null;
    }

    @Override
    public Void onItemUncheckClick(CustomTodoItem object) {
        al_temp = new ArrayList<CustomTodoItem>();

        int size_arr = al_items.size();
        if (size_arr >= 1){

            int itemPos = al_items.indexOf(object);
            al_items.remove(itemPos);
            al_items.add(0, object );
            caa.notifyDataSetChanged();

        }
        return null;
    }

    @Override
    public Void setLiveCounter() {
        int tot_count = al_items.size();
        int com_count = 0;

        for(CustomTodoItem obj : al_items){
            if (obj.getComplete()== true){
                com_count++;
            }
        }
        TextView total_items_count =(TextView) findViewById(R.id.totalItemsCount);
        TextView completed_items_count =(TextView) findViewById(R.id.completedItemsCount);
        total_items_count.setText(Integer.toString(tot_count));
        completed_items_count.setText(Integer.toString(com_count));
        return null;
    }
}
