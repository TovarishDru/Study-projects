import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Objects;
 
/**
 * Main class that processes input commands and fills initial data
 */
public class UniversityCoursesManagementSystem {
    private static List<Course> courses = new ArrayList<>();
    private static List<UniversityMember> universityMembers = new ArrayList<>();
    private static Scanner s = new Scanner(System.in);
    private static int operationsDone = 0;
    private static final int BASE_OPERATIONS = 14;
    private static final int INITIAL_CONST_1 = 1;
    private static final int INITIAL_CONST_2 = 2;
    private static final int INITIAL_CONST_3 = 3;
    private static final int INITIAL_CONST_4 = 4;
    private static final int INITIAL_CONST_5 = 5;
    private static final int INITIAL_CONST_6 = 6;
    /**
     * Method activates filling of the initial data and controls input
     * @param args Argument by default
     */
    public static void main(String[] args) {
        fillInitialData();
        boolean result;
        String command;
        do {
            command = s.next();
            if (Objects.equals(command, "course")) {
                result = commandCourse();
            } else if (Objects.equals(command, "student")) {
                result = commandUniversityMember(UniversityMemberType.STUDENT);
            } else if (Objects.equals(command, "professor")) {
                result = commandUniversityMember(UniversityMemberType.PROFESSOR);
            } else if (Objects.equals(command, "enroll")) {
                result = commandIdType(CommandType.ENROLL);
            } else if (Objects.equals(command, "drop")) {
                result = commandIdType(CommandType.DROP);
            } else if (Objects.equals(command, "teach")) {
                result = commandIdType(CommandType.TEACH);
            } else if (Objects.equals(command, "exempt")) {
                result = commandIdType(CommandType.EXEMPT);
            } else {
                System.out.println("Wrong inputs");
                break;
            }
            if (!result) {
                break;
            }
        } while (s.hasNext());
    }
    /**
     * Method that processes "enroll", "drop", "teach", and "exempt" commands of main. Inputs and processes the data
     * that is presented by two integers: memberId and courseId
     * @param commandType represents the type of operation to be done: "enroll", "drop", "teach", or "exempt"
     * @return Returns "false" if an error occurred or "true" if operation was executed successfully
     */
    public static boolean commandIdType(CommandType commandType) {
        int memberId;
        int courseId;
        UniversityMember member;
        Course course;
        if (!s.hasNextInt()) {
            System.out.println("Wrong inputs");
            return false;
        }
        memberId = s.nextInt();
        member = getUniversityMember(memberId);
        if (member.getMemberId() == 0) {
            System.out.println("Wrong inputs");
            return false;
        }
        if ((commandType == CommandType.ENROLL || commandType == CommandType.DROP) & !(member instanceof Student)) {
            System.out.println("Wrong inputs");
            return false;
        }
        if ((commandType == CommandType.TEACH || commandType == CommandType.EXEMPT) & !(member instanceof Professor)) {
            System.out.println("Wrong inputs");
            return false;
        }
        if (!s.hasNextInt()) {
            System.out.println("Wrong inputs");
            return false;
        }
        courseId = s.nextInt();
        course = getCourse(courseId);
        if (course.getCourseId() == 0) {
            System.out.println("Wrong inputs");
            return false;
        }
        boolean result = true;
        switch (commandType) {
            case ENROLL:
                result = enrollToCourse(member, course);
                break;
            case DROP:
                result = dropFromCourse(member, course);
                break;
            case TEACH:
                result = assignToCourse(member, course);
                break;
            case EXEMPT:
                result = exemptFromCourse(member, course);
                break;
            default:
                break;
        }
        return result;
    }
 
    /**
     * Method that processes "student" and "professor "operations in main. Inputs and checks the data about course.
     * Adds new UniversityMember if no mistake were found
     * @param universityMemberType Parameter that shows the class type of UniversityMember: Student or Professor
     * @return Returns "false" if inputs has mistakes or "true" if UniversityMember was added successfully
     */
    public static boolean commandUniversityMember(UniversityMemberType universityMemberType) {
        String memberName;
        if (!s.hasNext()) {
            System.out.println("Wrong inputs");
            return false;
        }
        memberName = s.next().toLowerCase();
        if (!checkUniversityMemberName(memberName) || !commandNameCheck(memberName) || memberName.isEmpty()) {
            System.out.println("Wrong inputs");
            return false;
        }
        return addUniversityMember(memberName, universityMemberType);
    }
 
    /**
     * Method that processes "course" command. Inputs and checks the data of a new course. Adds the course if no
     * mistake in input were found
     * @return Returns "false" if input has mistakes and "true" if course was added successfully
     */
    public static boolean commandCourse() {
        String courseName;
        String courseType;
        CourseLevel courseLevel;
        if (!s.hasNext()) {
            System.out.println("Wrong inputs");
            return false;
        }
        courseName = s.next().toLowerCase();
        if (!checkCourseName(courseName) || !commandNameCheck(courseName) || courseName.isEmpty()) {
            System.out.println("Wrong inputs");
            return false;
        }
        Iterator<Course> courseIterator = courses.iterator();
        while (courseIterator.hasNext()) {
            if (Objects.equals(courseIterator.next().getCourseName(), courseName)) {
                System.out.println("Course exists");
                return false;
            }
        }
        if (!s.hasNext()) {
            System.out.println("Wrong inputs");
            return false;
        }
        courseType = s.next().toLowerCase();
        if (Objects.equals(courseType, "bachelor")) {
            courseLevel = CourseLevel.BACHELOR;
        } else if (Objects.equals(courseType, "master")) {
            courseLevel = CourseLevel.MASTER;
        } else {
            System.out.println("Wrong inputs");
            return false;
        }
        return addCourse(courseName, courseLevel);
    }
 
    /**
     * Method that fills the initial data and creates zeroCourse and zeroMember - instances of Course and
     * UniversityMember by default
     */
    public static void fillInitialData() {
        addCourse("zeroCourse", CourseLevel.BACHELOR);
        addUniversityMember("zeroMember", UniversityMemberType.STUDENT);
        addCourse("java_beginner", CourseLevel.BACHELOR);
        addCourse("java_intermediate", CourseLevel.BACHELOR);
        addCourse("python_basics", CourseLevel.BACHELOR);
        addCourse("algorithms", CourseLevel.MASTER);
        addCourse("advanced_programming", CourseLevel.MASTER);
        addCourse("mathematical_analysis", CourseLevel.MASTER);
        addCourse("computer_vision", CourseLevel.MASTER);
        addUniversityMember("Alice", UniversityMemberType.STUDENT);
        addUniversityMember("Bob", UniversityMemberType.STUDENT);
        addUniversityMember("Alex", UniversityMemberType.STUDENT);
        enrollToCourse(getUniversityMember(INITIAL_CONST_1), getCourse(INITIAL_CONST_1));
        enrollToCourse(getUniversityMember(INITIAL_CONST_1), getCourse(INITIAL_CONST_2));
        enrollToCourse(getUniversityMember(INITIAL_CONST_1), getCourse(INITIAL_CONST_3));
        enrollToCourse(getUniversityMember(INITIAL_CONST_2), getCourse(INITIAL_CONST_1));
        enrollToCourse(getUniversityMember(INITIAL_CONST_2), getCourse(INITIAL_CONST_4));
        enrollToCourse(getUniversityMember(INITIAL_CONST_3), getCourse(INITIAL_CONST_5));
        addUniversityMember("Ali", UniversityMemberType.PROFESSOR);
        addUniversityMember("Ahmed", UniversityMemberType.PROFESSOR);
        addUniversityMember("Andrey", UniversityMemberType.PROFESSOR);
        assignToCourse(getUniversityMember(INITIAL_CONST_4), getCourse(INITIAL_CONST_1));
        assignToCourse(getUniversityMember(INITIAL_CONST_4), getCourse(INITIAL_CONST_2));
        assignToCourse(getUniversityMember(INITIAL_CONST_5), getCourse(INITIAL_CONST_3));
        assignToCourse(getUniversityMember(INITIAL_CONST_5), getCourse(INITIAL_CONST_5));
        assignToCourse(getUniversityMember(INITIAL_CONST_6), getCourse(INITIAL_CONST_6));
    }
 
    /**
     * Method that checks if the name matches with any command
     * @param name Name to be checked
     * @return Returns "false" if the name is not valid, "true" otherwise
     */
    public static boolean commandNameCheck(String name) {
        if (Objects.equals(name, "course")) {
            return false;
        }
        if (Objects.equals(name, "student")) {
            return false;
        }
        if (Objects.equals(name, "professor")) {
            return false;
        }
        if (Objects.equals(name, "enroll")) {
            return false;
        }
        if (Objects.equals(name, "drop")) {
            return false;
        }
        if (Objects.equals(name, "exempt")) {
            return false;
        }
        if (Objects.equals(name, "teach")) {
            return false;
        }
        return true;
    }
 
    /**
     * Method that checks if the name is valid for UniversityMember instance
     * @param memberName The name to be checked
     * @return Returns "false" if the name is not valid, "true" otherwise
     */
    public static boolean checkUniversityMemberName(String memberName) {
        for (int i = 0; i < memberName.length(); i++) {
            char curC = memberName.charAt(i);
            if (curC < 'a' || curC > 'z') {
                return false;
            }
        }
        return true;
    }
    /** Method that checks if the name is valid for Course instance
     * @param courseName The name to be checked
     * @return Returns "false" if the name is not valid, "true" otherwise
     */
    public static boolean checkCourseName(String courseName) {
        courseName = '0' + courseName;
        courseName += '0';
        for (int i = 1; i < courseName.length() - 1; i++) {
            char curC = courseName.charAt(i);
            if (curC == '_') {
                char prevC = courseName.charAt(i - 1);
                char nextC = courseName.charAt(i + 1);
                if ((prevC < 'a' || prevC > 'z') || (nextC < 'a' || nextC > 'z')) {
                    return false;
                }
            } else if (curC < 'a' || curC > 'z') {
                return false;
            }
        }
        return true;
    }
 
    /**
     * Method that searches the UniversityMember by his memberId
     * @param memberId The ID of member to be found
     * @return Returns the object of UniversityMember with corresponding memberId. If the member was not found,
     * returns zeroMember
     */
    public static UniversityMember getUniversityMember(int memberId) {
        Iterator<UniversityMember> iter = universityMembers.iterator();
        UniversityMember universityMember;
        while (iter.hasNext()) {
            universityMember = iter.next();
            if (universityMember.getMemberId() == memberId) {
                return universityMember;
            }
        }
        return universityMembers.get(0);
    }
 
    /**
     * Method that searches the Course by its courseId
     * @param courseId The ID of course to be found
     * @return Returns the object of Course with corresponding courseId. If the course was not found, returns zeroCourse
     */
    public static Course getCourse(int courseId) {
        Iterator<Course> iter = courses.iterator();
        Course course;
        while (iter.hasNext()) {
            course = iter.next();
            if (course.getCourseId() == courseId) {
                return course;
            }
        }
        return courses.get(0);
    }
 
    /**
     * Method that adds a new course with given parameters
     * @param courseName Name of the course to be added
     * @param courseLevel Level of course CourseLevel.(bachelor or master) to added
     * @return Returns "true" by default
     */
    public static boolean addCourse(String courseName, CourseLevel courseLevel) {
        courseName = courseName.toLowerCase();
        if (operationsDone++ > BASE_OPERATIONS) {
            System.out.println("Added successfully");
        }
        courses.add(new Course(courseName, courseLevel));
        return true;
    }
 
    /**
     * Method that adds a new UniversityMember with given parameters
     * @param memberName Name of the UniversityMember to be added
     * @param universityMemberType Type of university member to be added: Professor or Student
     * @return Returns "true" by default
     */
    public static boolean addUniversityMember(String memberName, UniversityMemberType universityMemberType) {
        memberName = memberName.toLowerCase();
        switch (universityMemberType) {
            case STUDENT:
                universityMembers.add(new Student(memberName));
                break;
            case PROFESSOR:
                universityMembers.add(new Professor(memberName));
            default:
                break;
        }
        if (operationsDone++ > BASE_OPERATIONS) {
            System.out.println("Added successfully");
        }
        return true;
    }
 
    /**
     * The method that triggers enrollment of student to a course
     * @param student Student instance to be enrolled
     * @param course Course instance to be enrolled
     * @return Returns "true" if student was enrolled successfully, "false" otherwise
     */
    private static boolean enrollToCourse(UniversityMember student, Course course) {
        return ((Student) student).enroll(course);
    }
 
    /**
     * The method that triggers drop of the student from course
     * @param student Student instance to be dropped from the course
     * @param course Course form which student is being dropped
     * @return Returns "true" if student was dropped successfully, "false" otherwise
     */
    private static boolean dropFromCourse(UniversityMember student, Course course) {
        return ((Student) student).drop(course);
    }
 
    /**
     * The method that triggers the assignment of a professor to a course
     * @param professor Professor instance to be assigned to a course
     * @param course Course instance for which professor is being assigned
     * @return Returns "true" if professor was assigned successfully, "false" otherwise
     */
    private static boolean assignToCourse(UniversityMember professor, Course course) {
        return ((Professor) professor).teach(course);
    }
 
    /**
     * The method that triggers the exemption of a professor from a course
     * @param professor Professor instance to be exempted
     * @param course Course instance from which professor is being exempted
     * @return Returns "true" if professor was exempted successfully, "false" otherwise
     */
    private static boolean exemptFromCourse(UniversityMember professor, Course course) {
        return ((Professor) professor).exempt(course);
    }
}
 
 
/**
 * Course class that implements university' courses, their capacity, and enrolled students
 */
class Course {
    private static final int CAPACITY = 3;
    private static int numberOfCourses = 0;
    private int courseId;
    private String courseName;
    private List<Student> enrolledStudents = new ArrayList<>();
    private CourseLevel courseLevel;
    public Course(String courseName, CourseLevel courseLevel) {
        this.courseName = courseName;
        this.courseLevel = courseLevel;
        this.courseId = numberOfCourses++;
    }
 
    /**
     * Method to get the capacity of a course
     * @return Returns the integer capacity of a course
     */
    public int getCapacity() {
        return CAPACITY;
    }
 
    /**
     * Method to get the list of enrolled students of the course
     * @return Returns ArrayList of enrolled students
     */
    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }
 
    /**
     * Method to get the ID of a course
     * @return Returns integer ID of a course
     */
    public int getCourseId() {
        return courseId;
    }
 
    /**
     * Method to get the name of a course
     * @return Returns String name of a course
     */
    public String getCourseName() {
        return courseName;
    }
 
    /**
     * Method to check if a course is full
     * @return Returns "true" if the course is full, "false" otherwise
     */
    public boolean isFull() {
        return enrolledStudents.size() == CAPACITY;
    }
}
 
 
/**
 * Abstract class UniversityMembers that describes all members of university in general, stores the quantity of them and
 * ID of each member
 */
abstract class UniversityMember {
    private static int numberOfMembers = 0;
    private int memberId;
    private String memberName;
    public UniversityMember(int memberId, String memberName) {
        this.memberId = numberOfMembers++;
        this.memberName = memberName;
    }
 
    /**
     * Method to get the ID of a UniversityMember
     * @return Returns integer ID
     */
    public int getMemberId() {
        return memberId;
    }
}
 
 
/**
 * Class Student that extends UniversityMember and implements interface Enrollable. Describes each student and
 * specifies its two methods: enroll and drop
 */
class Student extends UniversityMember implements Enrollable {
    private static final int MAX_ENROLLMENT = 3;
    private List<Course> enrolledCourses = new ArrayList<>();
    private static final int BASE_ENROLLMENT = 6;
    private static int enrollmentsDone = 0;
    public Student(String memberName) {
        super(0, memberName);
    }
 
    /**
     * Method that drops the student from a course
     * @param course Course instance from which student is being dropped
     * @return Returns "true" if the student was dropped successfully, "false" otherwise
     */
    @Override
    public boolean drop(Course course) {
        if (enrolledCourses.contains(course)) {
            enrolledCourses.remove(course);
            course.getEnrolledStudents().remove(this);
            System.out.println("Dropped successfully");
            return true;
        }
        System.out.println("Student is not enrolled in this course");
        return false;
    }
 
    /**
     * Method that enrolls the student to a course
     * @param course Course instance for which student is being enrolled
     * @return Returns "true" if student was enrolled successfully, "false" otherwise
     */
    @Override
    public boolean enroll(Course course) {
        if (enrolledCourses.contains(course)) {
            System.out.println("Student is already enrolled in this course");
            return false;
        }
        if (enrolledCourses.size() >= MAX_ENROLLMENT) {
            System.out.println("Maximum enrollment is reached for the student");
            return false;
        }
        if (course.isFull()) {
            System.out.println("Course is full");
            return false;
        }
        enrolledCourses.add(course);
        course.getEnrolledStudents().add(this);
        if (BASE_ENROLLMENT <= enrollmentsDone) {
            System.out.println("Enrolled successfully");
        } else {
            enrollmentsDone++;
        }
        return true;
    }
}
 
 
/**
 * Enumeration CourseLevel that describes the level of a course: bachelor or master
 */
enum CourseLevel {
    BACHELOR,
    MASTER
}
 
 
/**
 * Interface Enrollable that declares two methods: drop and enroll
 */
interface Enrollable {
    public boolean drop(Course course);
    public boolean enroll(Course course);
}
 
 
/**
 * Class Professor that extends UniversityMember. Describes each professor and specifies its two methods:
 * teach and exempt
 */
class Professor extends UniversityMember {
    private static final int MAX_LOAD = 2;
    private List<Course> assignedCourses = new ArrayList<>();
    private static final int BASE_ASSIGNMENT = 5;
    private static int assignmentsDone = 0;
    public Professor(String memberName) {
        super(0, memberName);
    }
 
    /**
     * Method that assigns the professor to a course
     * @param course Course instance for which professor is being assigned
     * @return Returns "true" if professor was assigned successfully, "false" otherwise
     */
    public boolean teach(Course course) {
        if (assignedCourses.size() >= MAX_LOAD) {
            System.out.println("Professor's load is complete");
            return false;
        }
        if (assignedCourses.contains(course)) {
            System.out.println("Professor is already teaching this course");
            return false;
        }
        assignedCourses.add(course);
        if (BASE_ASSIGNMENT <= assignmentsDone) {
            System.out.println("Professor is successfully assigned to teach this course");
        } else {
            assignmentsDone++;
        }
        return true;
    }
 
    /**
     * Method that exempts the professor from a course
     * @param course Course instance from which professor is being exempted
     * @return Returns "true" if professor was exempted successfully, "false" otherwise
     */
    public boolean exempt(Course course) {
        if (assignedCourses.contains(course)) {
            assignedCourses.remove(course);
            System.out.println("Professor is exempted");
            return true;
        }
        System.out.println("Professor is not teaching this course");
        return false;
    }
}
 
 
/**
 * Enumeration UniversityMemberType that is utilized to represent if the UniversityMember is a Student or a Professor
 */
enum UniversityMemberType {
    STUDENT,
    PROFESSOR
}
 
 
/**
 * Enumeration that represents the type of operation to be done with UniversityMember instance
 */
enum CommandType {
    ENROLL,
    DROP,
    TEACH,
    EXEMPT
}