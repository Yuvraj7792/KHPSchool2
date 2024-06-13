package com.mySchool.KHPShool.Controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mySchool.KHPShool.Model.Student;
import com.mySchool.KHPShool.Model.StudentDto;
import com.mySchool.KHPShool.Service.StudentRepository;

import jakarta.validation.Valid;



@Controller
public class StudentController {
	
	@Autowired
	private StudentRepository repo;
	
	@GetMapping("/allStud")
	public String showStudentList(Model model) {
		List<Student> students =repo.findAll();
		model.addAttribute("students",students);
		return "students/AllStudents";
	}	
	
	@GetMapping("/newStud")
	public String showaddStudent(Model model) {
		StudentDto studentDto = new StudentDto();
		model.addAttribute("studentDto",studentDto);
		return "students/newStudent";
	}	
	
	@PostMapping("/newStud")
	public String addStudent(
			@Valid @ModelAttribute StudentDto studentDto,
			BindingResult result
			) {
		
		if(studentDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("studentDto", "imageFile", "This image file is empty!"));
		}
		
		// save image file

		MultipartFile image =  studentDto.getImageFile();
		Date createdAt = new Date();
		String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
		
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories (uploadPath);
			}
			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			
		}catch (Exception e) {
			System.out.println("Exception : "+e.getMessage());
		}
		
		Student student = new Student();
		student.setAge(studentDto.getAge());
		student.setCategory(studentDto.getCategory());
		student.setFees(studentDto.getFees());
		student.setImage(storageFileName);
		student.setmName(studentDto.getmName());
		student.setName(studentDto.getName());
		student.setReligion(studentDto.getReligion());
		student.setStd(studentDto.getStd());
	
		repo.save(student);
		return "redirect:/allStud";
	}	
	
	
	@GetMapping("/editStud")
	public String showeditStudent(
			Model model,
			@RequestParam int regNo
			) {
		
		
		try {
			
			Student student = repo.findById(regNo).get();
			model.addAttribute("student",student);
			
			StudentDto studentDto = new StudentDto();
			studentDto.setAge(student.getAge());
			studentDto.setCategory(student.getCategory());
			studentDto.setFees(student.getFees());
			studentDto.setmName(student.getmName());
			studentDto.setName(student.getName());
			studentDto.setReligion(student.getReligion());
			studentDto.setStd(student.getStd());
			
			model.addAttribute("studentDto",studentDto);

		} catch (Exception e) {
			System.out.println("Error Massage : "+ e.getMessage());
		}
		return "students/editStudent";
	}
	

	@PostMapping("/editStud")
	public String updateStudent(
			Model model,
			@RequestParam int regNo,
			@Valid @ModelAttribute StudentDto studentDto,
			BindingResult result
			) {
				
		try {
			Student student = repo.findById(regNo).get();
			model.addAttribute("student",student);
			
			if (result.hasErrors()) {
				return "students/editStudent";
				}

				if (!studentDto.getImageFile().isEmpty()) {
					// delete old image
					String uploadDir = "public/images/";
					Path oldImagePath = Paths.get(uploadDir + student.getImage());

					try {
						Files.delete(oldImagePath);
					}
					catch (Exception ex) {
						System.out.println("Exception:" + ex.getMessage());
					}
				
				    // save new image file
					MultipartFile image = studentDto.getImageFile();
					Date createdAt = new Date();
					String storageFileName = createdAt.getTime() + "" + image.getOriginalFilename();
					try (InputStream inputStream = image.getInputStream()) {
						Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
								StandardCopyOption.REPLACE_EXISTING);
					}
					student.setImage(storageFileName);
				}
			
				student.setAge(studentDto.getAge());
				student.setCategory(studentDto.getCategory());
				student.setFees(studentDto.getFees());
				student.setmName(studentDto.getmName());
				student.setName(studentDto.getName());
				student.setReligion(studentDto.getReligion());
				student.setStd(studentDto.getStd());
			
				repo.save(student);
				
		
		} catch (Exception e) {
			System.out.println("Exception : "+e.getMessage());
		}			
		
		return "redirect:/allStud";
	}
	
	@GetMapping("/delete")
	public String deleteStudent(
			@RequestParam int regNo
	) {
		try {
			Student student = repo.findById(regNo).get();
			
			// delete student image
			Path imagePath = Paths.get("public/images/" + student.getImage());
			try {
				Files.delete(imagePath);
			} catch (Exception ex) {
				System.out.println("Exception:" + ex.getMessage());
			}
			
			//delete Student
			repo.delete(student);
			
		}
		catch (Exception ex) {
			System.out.println("Exception:" + ex.getMessage());
		}

		
		return "redirect:/allStud";

	}
	
	
}
