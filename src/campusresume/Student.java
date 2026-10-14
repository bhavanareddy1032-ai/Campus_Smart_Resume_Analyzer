package campusresume;

public class Student {
    private final String name;
    private final String branch;
    private final double cgpa;

    public Student(String name, String branch, double cgpa) {
        this.name = name;
        this.branch = branch;
        this.cgpa = cgpa;
    }

    public String getName() { return name; }
    public String getBranch() { return branch; }
    public double getCgpa() { return cgpa; }
}
