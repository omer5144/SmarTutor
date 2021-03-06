package com.example.smartutor.model;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@androidx.room.Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) void insertStudent(Student student);
    @Delete void deleteStudent(Student student);
    @Transaction @Query("SELECT * FROM Student") LiveData<List<Student>> getStudents();
    @Query("SELECT * FROM Student WHERE email = :email") LiveData<Student> getStudent(String email);
}
