package campusresume;

import java.util.List;

public class ResumeAnalysis {
    private final int score;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final List<String> suggestions;
    private final String status;

    public ResumeAnalysis(int score, List<String> matchedSkills,
                          List<String> missingSkills,
                          List<String> suggestions, String status) {
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.status = status;
    }

    public int getScore() { return score; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public List<String> getSuggestions() { return suggestions; }
    public String getStatus() { return status; }
}
