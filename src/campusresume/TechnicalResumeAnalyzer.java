package campusresume;

import java.util.ArrayList;
import java.util.List;

public class TechnicalResumeAnalyzer extends ResumeAnalyzer {

    @Override
    public ResumeAnalysis analyze(Resume resume)
            throws InvalidResumeException {

        if (resume == null || resume.getContent() == null ||
            resume.getContent().trim().isEmpty()) {
            throw new InvalidResumeException("Resume content cannot be empty.");
        }

        String text = normalize(resume.getContent());
        String[] required = requiredSkills(resume.getTargetRole());

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : required) {
            if (containsSkill(text, normalize(skill))) {
                matched.add(skill);
            } else {
                missing.add(skill);
            }
        }

        int score = (int) Math.round((matched.size() * 80.0) / required.length);

        // Small academic/project bonus to make the score useful for campus placements.
        String lower = text;
        if (lower.contains("project")) score = Math.min(100, score + 10);
        if (lower.contains("education") || lower.contains("cgpa")) score = Math.min(100, score + 5);
        if (lower.contains("internship") || lower.contains("intern")) score = Math.min(100, score + 5);

        List<String> suggestions = new ArrayList<>();
        for (String skill : missing) {
            suggestions.add("Consider adding evidence of " + skill + ".");
        }
        if (!lower.contains("project"))
            suggestions.add("Add 1–2 academic or personal projects with measurable outcomes.");
        if (!lower.contains("internship") && !lower.contains("intern"))
            suggestions.add("Mention internship, training, or practical experience if available.");
        if (suggestions.isEmpty())
            suggestions.add("Keep your resume concise and tailor keywords to each job description.");

        return new ResumeAnalysis(score, matched, missing, suggestions, statusFor(score));
    }

    private String[] requiredSkills(String role) {
        if (role != null && role.toLowerCase().contains("web")) {
            return WEB_SKILLS;
        }
        return JAVA_SKILLS;
    }

    private boolean containsSkill(String text, String skill) {
        return text.contains(skill);
    }
}
