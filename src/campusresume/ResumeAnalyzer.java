package campusresume;

public abstract class ResumeAnalyzer {
    protected static final String[] JAVA_SKILLS =
            {"Java", "OOP", "Collections", "Exception Handling", "Multithreading",
             "MySQL", "HTML", "CSS", "JavaScript", "Git", "Spring Boot", "REST API"};

    protected static final String[] WEB_SKILLS =
            {"HTML", "CSS", "JavaScript", "React", "Node.js", "REST API",
             "MySQL", "Git", "Java", "Python"};

    public abstract ResumeAnalysis analyze(Resume resume)
            throws InvalidResumeException;

    protected String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-z0-9+#.\\- ]", " ");
    }

    protected String statusFor(int score) {
        if (score >= 75) return "PLACEMENT READY";
        if (score >= 50) return "GOOD";
        return "NEEDS IMPROVEMENT";
    }
}
