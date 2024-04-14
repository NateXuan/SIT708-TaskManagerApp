package com.example.taskmanagerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
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

public class EditTask extends AppCompatActivity {
    private EditText titleEditText, descriptionEditText, dueDateEditText;
    private Calendar dueDate = Calendar.getInstance();
    private RequestQueue requestQueue;
    private long taskId;  // Task ID to update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        Button saveButton = findViewById(R.id.saveButton);

        // Initialize
        requestQueue = Volley.newRequestQueue(this);

        // Get task details from intent
        taskId = getIntent().getLongExtra("taskId", -1);
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        long dueDateMillis = getIntent().getLongExtra("dueDate", System.currentTimeMillis());

        titleEditText.setText(title);
        descriptionEditText.setText(description);
        dueDate.setTimeInMillis(dueDateMillis);
        updateDueDateEditText();

        dueDateEditText.setOnClickListener(this::showDatePickerDialog);
        saveButton.setOnClickListener(this::updateTask);
    }

    private void updateDueDateEditText() {
        dueDateEditText.setText(String.format("%d/%d/%d",
                dueDate.get(Calendar.DAY_OF_MONTH),
                dueDate.get(Calendar.MONTH) + 1, // Month is 0-based
                dueDate.get(Calendar.YEAR)));
    }

    public void showDatePickerDialog(View view) {
        new DatePickerDialog(this, this::onDateSet, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void onDateSet(DatePicker view, int year, int month, int day) {
        dueDate.set(Calendar.YEAR, year);
        dueDate.set(Calendar.MONTH, month);
        dueDate.set(Calendar.DAY_OF_MONTH, day);
        updateDueDateEditText();
    }

    private void updateTask(View view) {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        long dueDateMillis = dueDate.getTimeInMillis();

        String url = "http://10.0.2.2:3000/tasks/" + taskId;

        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("description", description);
        params.put("due_date", String.valueOf(dueDateMillis));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters,
                response -> {
                    Toast.makeText(EditTask.this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close this activity and return to previous one
                },
                error -> {
                    Toast.makeText(EditTask.this, "Failed to update task.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }
}
