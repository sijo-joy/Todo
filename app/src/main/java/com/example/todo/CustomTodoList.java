package com.example.todo;

public class CustomTodoList {
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getTodoId() {
        return todo_id;
    }
    private String name;
    private String date;
    private String todo_id;

    CustomTodoList(String todo_name, String due_date, String todo_id_id){
        this.name = todo_name;
        date = due_date;
        todo_id = todo_id_id;
    }

}
