package com.example.todo;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String create_todo_table = "create table todo_list(" +
            "ID INTEGER primary key autoincrement, " +
            "TODO_NAME STRING," +
            "NEAREST_DUE_DATE STRING" +
            ")";
    private static final String create_todo_list_table = "create table todo_items(" +
            "ID integer primary key autoincrement, " +
            "TODO_ID INTEGER," +
            "TODO_ITEM_NAME STRING," +
            "DUE_DATE STRING," +
            "COMPLETED INTEGER," + " FOREIGN KEY (TODO_ID) REFERENCES todo_list(ID));";

    private static final String drop_todo_table = "drop table todo_list";
    private static final String drop_table_todo_list = "drop table todo_items";

    public DBOpenHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    // overridden method that is called when the database is to be created
    public void onCreate(SQLiteDatabase db) {
// create the database
        db.execSQL(create_todo_table);
        db.execSQL(create_todo_list_table);
    }
    // overridden method that is called when the database is to be upgraded
// note in this example we simply reconstruct the database not caring for
// data loss ideally you should have a method for storing the data while you
    // are reconstructing the database
    public void onUpgrade(SQLiteDatabase db, int version_old, int version_new)
    {
// drop the tables and recreate them
        db.execSQL(drop_todo_table);
        db.execSQL(create_todo_table);

        db.execSQL(drop_table_todo_list);
        db.execSQL(create_todo_list_table);
    }
    // a bunch of constant strings that will be needed to create and drop
// databases

}
