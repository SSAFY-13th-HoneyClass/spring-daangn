package org.example.springboot.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.springboot.domain.Department;
import org.example.springboot.domain.Employee;
import org.example.springboot.domain.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class CircularReferenceTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("EAGER 로딩이 있는 순환 참조 테스트")
    @Transactional
    public void testCircularReferenceWithEagerLoading() {
        System.out.println("\n\n===== 순환 참조 EAGER 로딩 테스트 =====");
        
        // 1. 테스트 데이터 생성
        System.out.println("===== 데이터 준비 시작 =====");
        
        // 부서 생성
        Department devDept = Department.builder()
                .name("개발부")
                .build();
        
        Department marketingDept = Department.builder()
                .name("마케팅부")
                .build();
        
        // 부서 저장
        departmentRepository.save(devDept);
        departmentRepository.save(marketingDept);
        
        // 직원 생성
        Employee employee1 = Employee.builder()
                .name("홍길동")
                .position("Senior Developer")
                .build();
        
        Employee employee2 = Employee.builder()
                .name("김철수")
                .position("Junior Developer")
                .build();
        
        Employee employee3 = Employee.builder()
                .name("이영희")
                .position("Marketing Manager")
                .build();
        
        // 부서에 직원 추가
        devDept.addEmployee(employee1);
        devDept.addEmployee(employee2);
        marketingDept.addEmployee(employee3);
        
        // 직원 저장
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
        
        // 프로젝트 생성 및 직원에 연결
        Project project1 = Project.builder()
                .name("모바일 앱 개발")
                .description("안드로이드/iOS 앱 개발")
                .department(devDept) // 개발부 프로젝트
                .build();
        
        Project project2 = Project.builder()
                .name("웹 서비스 리뉴얼")
                .description("기존 웹 서비스 UI/UX 개선")
                .department(devDept) // 개발부 프로젝트
                .build();
        
        Project project3 = Project.builder()
                .name("신규 마케팅 캠페인")
                .description("여름 시즌 마케팅 캠페인")
                .department(marketingDept) // 마케팅부 프로젝트
                .build();
        
        // 직원에 프로젝트 추가
        employee1.addProject(project1);
        employee1.addProject(project2);
        employee3.addProject(project3);
        
        // 프로젝트 저장
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);
        
        System.out.println("===== 데이터 준비 완료 =====");
        
        // 변경 내용을 DB에 반영하고 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();
        System.out.println("===== 영속성 컨텍스트 초기화 완료 =====\n");
        
        // 2. EAGER 로딩으로 인한 순환 참조 테스트
        System.out.println("\n===== Department 조회 시 EAGER 로딩 동작 확인 =====");
        System.out.println("부서를 조회하면 @OneToMany EAGER 로딩으로 인해 소속 직원도 함께 조회됩니다.");
        System.out.println("SQL 예상: SELECT d.* FROM departments d WHERE d.department_id = ?");
        System.out.println("      + SELECT e.* FROM employees e WHERE e.department_id = ?");
        System.out.println("      + SELECT p.* FROM projects p WHERE p.employee_id IN (?, ?, ...)");
        System.out.println("      + SELECT d.* FROM departments d WHERE d.department_id IN (?, ?, ...)");
        
        // 부서 조회 - EAGER 로딩으로 연관된 직원과 프로젝트 모두 로딩됨
        Department foundDevDept = departmentRepository.findById(devDept.getDepartmentId()).orElseThrow();
        System.out.println("\n개발부 조회 결과: " + foundDevDept.getName());
        System.out.println("개발부 소속 직원 수: " + foundDevDept.getEmployees().size());
        
        // 직원 정보 접근 - 이미 로딩되어 있음
        System.out.println("\n===== Employee 정보 접근 (이미 로딩됨) =====");
        for (Employee emp : foundDevDept.getEmployees()) {
            System.out.println("직원 이름: " + emp.getName() + ", 직책: " + emp.getPosition());
            
            // 직원의 프로젝트 정보 접근 - EAGER 로딩으로 이미 로딩됨
            System.out.println("  담당 프로젝트 수: " + emp.getProjects().size());
            
            for (Project proj : emp.getProjects()) {
                // 프로젝트의 부서 정보 접근 - EAGER 로딩으로 이미 로딩됨
                System.out.println("    프로젝트 이름: " + proj.getName() + 
                                 ", 소속 부서: " + proj.getDepartment().getName());
            }
        }
        
        // 영속성 컨텍스트 초기화
        entityManager.clear();
        
        // 3. 직원부터 조회 시작하는 경우
        System.out.println("\n\n===== Employee 엔티티부터 조회 시 EAGER 로딩 동작 확인 =====");
        System.out.println("직원을 조회하면 @OneToMany EAGER 로딩으로 인해 담당 프로젝트도 함께 조회됩니다.");
        System.out.println("SQL 예상: SELECT e.* FROM employees e WHERE e.employee_id = ?");
        System.out.println("      + SELECT p.* FROM projects p WHERE p.employee_id = ?");
        System.out.println("      + SELECT d.* FROM departments d WHERE d.department_id IN (?, ?, ...)");
        
        // 직원 조회 - EAGER 로딩으로 연관된 프로젝트 로딩됨
        Employee foundEmployee = employeeRepository.findById(employee1.getEmployeeId()).orElseThrow();
        System.out.println("\n직원 조회 결과: " + foundEmployee.getName());
        
        // 프로젝트 정보 접근 - 이미 로딩되어 있음
        System.out.println("담당 프로젝트 수: " + foundEmployee.getProjects().size());
        for (Project proj : foundEmployee.getProjects()) {
            // 프로젝트의 부서 정보 접근 - EAGER 로딩으로 이미 로딩됨
            System.out.println("  프로젝트 이름: " + proj.getName() + 
                             ", 소속 부서: " + proj.getDepartment().getName());
            
            // 부서의 다른 직원 정보 접근 - EAGER 로딩으로 이미 로딩됨
            System.out.println("  해당 부서 직원 수: " + proj.getDepartment().getEmployees().size());
        }
        
        // 영속성 컨텍스트 초기화
        entityManager.clear();
        
        // 4. 프로젝트부터 조회 시작하는 경우
        System.out.println("\n\n===== Project 엔티티부터 조회 시 EAGER 로딩 동작 확인 =====");
        System.out.println("프로젝트를 조회하면 @ManyToOne EAGER 로딩으로 인해 부서도 함께 조회됩니다.");
        System.out.println("SQL 예상: SELECT p.* FROM projects p WHERE p.project_id = ?");
        System.out.println("      + SELECT d.* FROM departments d WHERE d.department_id = ?");
        System.out.println("      + SELECT e.* FROM employees e WHERE e.department_id = ?");
        
        // 프로젝트 조회 - EAGER 로딩으로 연관된 부서 로딩됨
        Project foundProject = projectRepository.findById(project1.getProjectId()).orElseThrow();
        System.out.println("\n프로젝트 조회 결과: " + foundProject.getName());
        
        // 부서 정보 접근 - EAGER 로딩으로 이미 로딩됨
        Department projectDept = foundProject.getDepartment();
        System.out.println("프로젝트 소속 부서: " + projectDept.getName());
        
        // 부서의 직원 정보 접근 - EAGER 로딩으로 이미 로딩됨
        System.out.println("부서 소속 직원 수: " + projectDept.getEmployees().size());
        for (Employee emp : projectDept.getEmployees()) {
            System.out.println("  직원 이름: " + emp.getName());
            System.out.println("  담당 프로젝트 수: " + emp.getProjects().size());
        }
        
        System.out.println("\n===== 순환 참조 EAGER 로딩 테스트 종료 =====");
    }
} 