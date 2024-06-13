package com.mySchool.KHPShool.Service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mySchool.KHPShool.Model.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}
 