package campusresume;

public class Resume {
    private final Student student;
    private final String targetRole;
    private final String content;

    public Resume(Student student, String targetRole, String content) {
        this.student = student;
        this.targetRole = targetRole;
        this.content = content;
    }

    public Student getStudent() { return student; }
    public String getTargetRole() { return targetRole; }
    public String getContent() { return content; }
}
