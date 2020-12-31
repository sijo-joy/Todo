package com.example.todo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ArrayAdapterTodoList extends BaseAdapter {
    private Context context;
    private DBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private ArrayList<CustomTodoList> al_items;
    static class ViewHolder {
        public TextView tv_name;
        public TextView tv_date;
        public TextView todo_list_id;

    }
    public ArrayAdapterTodoList(Context c, ArrayList<CustomTodoList> al) {
        context = c;
        al_items = al;
    }
    public View getView(int position, View convert_view, ViewGroup parent) {
        ViewHolder holder;
        if(convert_view == null) {
            holder = new ViewHolder();
            LayoutInflater inflator = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convert_view = inflator.inflate(R.layout.custom_todo_list, parent,
                    false);
            holder.tv_name = (TextView) convert_view.findViewById(R.id.todoName);
            holder.tv_date = (TextView) convert_view.findViewById(R.id.dueDate);
            holder.todo_list_id = (TextView) convert_view.findViewById(R.id.todoListId);
            convert_view.setTag(holder);
        }
        else {
            holder = (ViewHolder) convert_view.getTag();
        }
        tdb = new DBOpenHelper(context, "test.db", null, 1);
        sdb = tdb.getWritableDatabase();
        String table_name = "todo_items";
        String[] columns = {"ID", "TODO_ITEM_NAME", "DUE_DATE","COMPLETED"};
        String where = "TODO_ID ="+al_items.get(position).getTodoId()+" and COMPLETED = 0" ;
        String where_args[] = null;
        String group_by = null;
        String having = null;
        String order_by = null;
        String jhb  = al_items.get(position).getName();
        String jk = al_items.get(position).getTodoId();
        Cursor c = sdb.query(table_name, columns, where, where_args, group_by,
                having, order_by);
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            String date = c.getString(2);

            if (date.length() != 0){
                Date current_date = Calendar.getInstance().getTime();
                Date date1 = new Date(date);

                Calendar cal = Calendar.getInstance();
                cal.setTime(current_date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                current_date = cal.getTime();



                SimpleDateFormat df = new SimpleDateFormat("MMM/dd/yyyy");
                String current_date_formatted = df.format(current_date);
                String item_date_formatted = df.format(date1);
                if(current_date.after(date1)){
                    holder.tv_name.setTextColor(Color.RED);
                    holder.tv_date.setTextColor(Color.RED);
                }
                else if (item_date_formatted.equals(current_date_formatted)){
                    holder.tv_name.setTextColor(Color.YELLOW);
                    holder.tv_date.setTextColor(Color.YELLOW);
                }
                else {

                }

            }

        }


// set all the data on the fields before returning it
        holder.tv_name.setText(al_items.get(position).getName());
        holder.tv_date.setText(al_items.get(position).getDate());
        holder.todo_list_id.setText(al_items.get(position).getTodoId());
// return the constructed view
        return convert_view;

    }
    public int getCount() {
        return al_items.size();
    }

    public long getItemId(int position) {
        return position;
    }
    public Object getItem(int position) {
        return al_items.get(position);
    }


}
