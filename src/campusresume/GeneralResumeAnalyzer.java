package campusresume;

import java.util.ArrayList;
import java.util.List;

public class GeneralResumeAnalyzer extends ResumeAnalyzer {

    @Override
    public ResumeAnalysis analyze(Resume resume)
            throws InvalidResumeException {

        if (resume == null || resume.getContent() == null ||
            resume.getContent().trim().isEmpty()) {
            throw new InvalidResumeException("Resume content cannot be empty.");
        }

        String text = normalize(resume.getContent());
        String[] keywords = {"communication", "teamwork", "leadership",
                             "problem solving", "education", "project",
                             "internship", "skills"};

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String keyword : keywords) {
            if (text.contains(normalize(keyword))) matched.add(keyword);
            else missing.add(keyword);
        }

        int score = (int) Math.round((matched.size() * 100.0) / keywords.length);

        List<String> suggestions = new ArrayList<>();
        for (String item : missing)
            suggestions.add("Consider adding " + item + " with a specific example.");

        return new ResumeAnalysis(score, matched, missing, suggestions, statusFor(score));
    }
}
