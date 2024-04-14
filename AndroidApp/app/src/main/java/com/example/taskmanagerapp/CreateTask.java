package com.example.taskmanagerapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTask extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, dueDateEditText;
    private Calendar dueDate = Calendar.getInstance();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        Button saveButton = findViewById(R.id.saveButton);

        // Initialize
        requestQueue = Volley.newRequestQueue(this);

        dueDateEditText.setOnClickListener(this::showDatePickerDialog);
        saveButton.setOnClickListener(this::saveTask);
    }

    public void showDatePickerDialog(View view) {
        new DatePickerDialog(this, this::onDateSet, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void onDateSet(DatePicker view, int year, int month, int day) {
        dueDate.set(Calendar.YEAR, year);
        dueDate.set(Calendar.MONTH, month);
        dueDate.set(Calendar.DAY_OF_MONTH, day);
        dueDateEditText.setText(day + "/" + (month + 1) + "/" + year);
    }

    private void saveTask(View view) {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        long dueDateMillis = dueDate.getTimeInMillis();

        String url = "http://10.0.2.2:3000/tasks";

        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("description", description);
        params.put("due_date", String.valueOf(dueDateMillis));

        JSONObject parameters = new JSONObject(params);

        //Post
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,
                response -> {
                    Toast.makeText(CreateTask.this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(CreateTask.this, "Failed to save task.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }
}
