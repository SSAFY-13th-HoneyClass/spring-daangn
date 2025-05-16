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
@Table(name = "employees")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(nullable = false)
    private String name;

    private String position;

    // 부서와의 양방향 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // 프로젝트와의 양방향 연관관계 - EAGER 로딩 설정
    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    @Builder
    public Employee(String name, String position) {
        this.name = name;
        this.position = position;
    }

    // 부서 설정 메서드
    public void setDepartment(Department department) {
        this.department = department;
    }

    // 프로젝트 추가 연관관계 편의 메서드
    public void addProject(Project project) {
        this.projects.add(project);
        project.setEmployee(this);
    }
} 