const express = require('express');
const mysql = require('mysql');
const bodyParser = require('body-parser');
const cors = require('cors');

require('dotenv').config();

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Connect to MySQL
const connection = mysql.createConnection({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASS,
    database: process.env.DB_NAME
});

connection.connect(err => {
    if (err) throw err;
    console.log('Connected to the MySQL server.');
});

//CRUD
app.get('/tasks', (req, res) => {
    connection.query('SELECT * FROM tasks', (err, results) => {
        if (err) throw err;
        res.status(200).send(results);
    });
});

app.post('/tasks', (req, res) => {
    const { title, description, due_date } = req.body;
    const query = 'INSERT INTO tasks (title, description, due_date) VALUES (?, ?, ?)';
    connection.query(query, [title, description, due_date], (err, results) => {
        if (err) throw err;
        res.status(201).send({ id: results.insertId });
    });
});

app.get('/tasks/:id', (req, res) => {
    const query = 'SELECT * FROM tasks WHERE id = ?';
    connection.query(query, [req.params.id], (err, results) => {
        if (err) throw err;
        if (results.length > 0) {
            res.status(200).send(results[0]);
        } else {
            res.status(404).send({ message: 'Task not found.' });
        }
    });
});

app.put('/tasks/:id', (req, res) => {
    const { title, description, due_date } = req.body;
    const query = 'UPDATE tasks SET title = ?, description = ?, due_date = ? WHERE id = ?';
    console.log(`Updating task with ID ${req.params.id} with`, { title, description, due_date });
    connection.query(query, [title, description, due_date, req.params.id], (err, results) => {
        if (err) {
            console.error("Database error:", err);
            throw err;
        }
        console.log("Update results:", results);
        if (results.affectedRows > 0) {
            res.status(200).send({ message: 'Task updated successfully.' });
        } else {
            res.status(404).send({ message: 'Task not found.' });
        }
    });
});

app.delete('/tasks/:id', (req, res) => {
    const query = 'DELETE FROM tasks WHERE id = ?';
    connection.query(query, [req.params.id], (err, results) => {
        if (err) throw err;
        if (results.affectedRows > 0) {
            res.status(200).send({ message: 'Task deleted successfully.' });
        } else {
            res.status(404).send({ message: 'Task not found.' });
        }
    });
});

const port = 3000;
app.listen(port, () => {
    console.log(`Server is running on port ${port}.`);
});
