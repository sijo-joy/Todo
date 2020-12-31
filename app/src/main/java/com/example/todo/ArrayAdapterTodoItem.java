package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import java.util.ArrayList;


public class ArrayAdapterTodoItem extends BaseAdapter {
    private Context context;
    private OnItemClickListener click_listener;
    private DBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private ArrayList<CustomTodoItem> al_items;
    private TextView tv_name_new;
    private TextView todo_id_new;
    static class ViewHolder {
        public TextView tv_name;
        public TextView todo_id;
        public TextView tv_date;
        public CheckBox complete;
    }
    public interface OnItemClickListener
    {
        Void onItemClick(CustomTodoItem object);
        Void onItemUncheckClick(CustomTodoItem object);
        Void setLiveCounter();
    }


    public ArrayAdapterTodoItem(Context c, ArrayList<CustomTodoItem> al,OnItemClickListener click) {
        context = c;
        al_items = al;
        click_listener = click;
    }



    public View getView(final int position, View convert_view, ViewGroup parent) {
        ViewHolder holder;
        if(convert_view == null) {
            holder = new ViewHolder();
            LayoutInflater inflator = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convert_view = inflator.inflate(R.layout.custom_todo_item, parent,
                    false);
            holder.tv_name = (TextView) convert_view.findViewById(R.id.todoItem);
            holder.tv_date = (TextView) convert_view.findViewById(R.id.dueDate);
            holder.complete = (CheckBox) convert_view.findViewById(R.id.complete);
            holder.todo_id = (TextView) convert_view.findViewById(R.id.todoId);

            CheckBox repeatChkBx = ( CheckBox ) convert_view.findViewById( R.id.complete );

            repeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    tdb = new DBOpenHelper(context, "test.db", null, 1);
                    sdb = tdb.getWritableDatabase();



                    if ( isChecked )
                    {
                        CustomTodoItem obj = al_items.get(position);
                        String todo_item_id = al_items.get(position).getTodoId();
                        al_items.get(position).setComplete(true);


                        ContentValues cv = new ContentValues();
                        cv.put("COMPLETED", 1);
                        sdb.update("todo_items", cv, "id="+todo_item_id, null);
                        click_listener.onItemClick(obj);
                        click_listener.setLiveCounter();
                        setNearestDueDate(todo_item_id);



                    }
                    else {
                        CustomTodoItem obj = al_items.get(position);
                        String todo_item_id = al_items.get(position).getTodoId();
                        al_items.get(position).setComplete(false);
                        ContentValues cv = new ContentValues();
                        cv.put("COMPLETED", 0);
                        sdb.update("todo_items", cv, "id="+todo_item_id, null);
                        setNearestDueDate(todo_item_id);
                        click_listener.setLiveCounter();
                        click_listener.onItemUncheckClick(obj);

                    }



                }
            });

            convert_view.setTag(holder);
        }
        else {
            holder = (ViewHolder) convert_view.getTag();
        }


// set all the data on the fields before returning it
        holder.tv_name.setText(al_items.get(position).getName());
        holder.tv_date.setText(al_items.get(position).getDate());
        holder.todo_id.setText(al_items.get(position).getTodoId());
        holder.complete.setChecked(al_items.get(position).getComplete());
// return the constructed view
        return convert_view;


    }
    public int getCount() {
        return al_items.size();
    }

    public void setNearestDueDate(String todo_item_id){
        String table_name = "todo_items";
        String[] columns = {"ID", "TODO_ID"};
        String where = "ID ="+todo_item_id ;
        Cursor c = sdb.query(table_name, columns, where, null, null,
                null, null);
        c.moveToFirst();
        if (c.getCount() >0 ){
            int todo_list_id_int = c.getInt(1);
            String todo_list_id = Integer.toString(todo_list_id_int);
            table_name = "todo_items";
            String[] columnss = {"ID", "TODO_ID", "DUE_DATE"};
            where = "TODO_ID ="+todo_list_id+" and COMPLETED = 0" ;
            String order_by = "DUE_DATE asc";
            c = sdb.query(table_name, columnss, where, null, null,
                    null, null);
            if (c.getCount() >0 ) {
                c.moveToFirst();
                String nearest_date = c.getString(2);
                ContentValues cv = new ContentValues();
                cv.put("NEAREST_DUE_DATE", nearest_date);
                sdb.update("todo_list", cv, "id=" + todo_list_id, null);
            }
            else {
                ContentValues cv = new ContentValues();
                cv.put("NEAREST_DUE_DATE", "");
                sdb.update("todo_list", cv, "id=" + todo_list_id, null);
            }

        }

    }

    public long getItemId(int position) {
        return position;
    }
    public Object getItem(int position) {
        return al_items.get(position);
    }


}