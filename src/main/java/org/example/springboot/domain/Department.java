package org.example.springboot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "departments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long departmentId;

    @Column(nullable = false)
    private String name;

    // 양방향 연관관계 - EAGER 로딩 설정
    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

    @Builder
    public Department(String name) {
        this.name = name;
    }

    // 연관관계 편의 메서드
    public void addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setDepartment(this);
    }
} 