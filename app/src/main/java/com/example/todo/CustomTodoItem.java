package com.example.todo;


public class CustomTodoItem {
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getTodoId() {
        return todo_id;
    }
    public boolean getComplete() {
        return complete;
    }
    public void setComplete(boolean val) {
        this.complete = val;
    }
    private String name;
    private String date;
    private String todo_id;
    private boolean complete;
    CustomTodoItem(String todo_name, String due_date, boolean complete_check,String todo_id_in) {
        this.name = todo_name;
        complete = complete_check;
        date = due_date;
        todo_id = todo_id_in;
    }

}
