package org.example.springboot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false)
    private String name;

    private String description;

    // 사원과의 양방향 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 부서와의 연관관계 - EAGER 로딩 설정
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Builder
    public Project(String name, String description, Department department) {
        this.name = name;
        this.description = description;
        this.department = department;
    }

    // 사원 설정 메서드
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
} 