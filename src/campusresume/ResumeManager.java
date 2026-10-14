package campusresume;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResumeManager {
    private final List<ResumeAnalysisRecord> history = new ArrayList<>();

    public void add(Resume resume, ResumeAnalysis analysis) {
        history.add(new ResumeAnalysisRecord(
                resume.getStudent().getName(),
                resume.getTargetRole(),
                analysis.getScore(),
                analysis.getStatus()
        ));
    }

    public List<ResumeAnalysisRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public static class ResumeAnalysisRecord {
        private final String student;
        private final String role;
        private final int score;
        private final String status;

        public ResumeAnalysisRecord(String student, String role,
                                    int score, String status) {
            this.student = student;
            this.role = role;
            this.score = score;
            this.status = status;
        }

        public String getStudent() { return student; }
        public String getRole() { return role; }
        public int getScore() { return score; }
        public String getStatus() { return status; }
    }
}
