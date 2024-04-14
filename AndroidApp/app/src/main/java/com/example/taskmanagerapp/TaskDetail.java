package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskDetail extends AppCompatActivity {

    private TextView taskTitleTextView, taskDescriptionTextView, taskDueDateTextView;
    private RequestQueue requestQueue;
    private long taskId;
    private long dueDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskTitleTextView = findViewById(R.id.taskTitleTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
        taskDueDateTextView = findViewById(R.id.taskDueDateTextView);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button editButton = findViewById(R.id.editButton);

        // Initialize
        requestQueue = Volley.newRequestQueue(this);

        taskId = getIntent().getLongExtra("taskId", -1);
        if (taskId != -1) {
            loadTaskDetails(taskId);

            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(TaskDetail.this, EditTask.class);
                intent.putExtra("taskId", taskId);
                intent.putExtra("title", taskTitleTextView.getText().toString());
                intent.putExtra("description", taskDescriptionTextView.getText().toString());
                intent.putExtra("dueDate", dueDateMillis);
                startActivity(intent);
            });
            deleteButton.setOnClickListener(v -> deleteTask(taskId));
        } else {
            Toast.makeText(this, "Error: Task ID is missing.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadTaskDetails(long taskId) {
        String url = "http://10.0.2.2:3000/tasks/" + taskId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        taskTitleTextView.setText(jsonObject.getString("title"));
                        taskDescriptionTextView.setText(jsonObject.getString("description"));
                        dueDateMillis = jsonObject.getLong("due_date");
                        taskDueDateTextView.setText(DateFormat.getDateInstance().format(new Date(dueDateMillis)));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error loading task details", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Error loading task details", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (taskId != -1) {
            loadTaskDetails(taskId);  // Reload task details every time this Activity resumes
        }
    }

    private void deleteTask(long taskId) {
        String url = "http://10.0.2.2:3000/tasks/" + taskId;
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(getApplicationContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(getApplicationContext(), "Error deleting task", Toast.LENGTH_SHORT).show());

        requestQueue.add(deleteRequest);
    }
}
