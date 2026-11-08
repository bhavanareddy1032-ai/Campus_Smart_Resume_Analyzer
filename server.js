const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 8080;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Skills dictionary (identical to Java ResumeAnalyzer)
const JAVA_SKILLS = [
  "Java", "OOP", "Collections", "Exception Handling", "Multithreading",
  "MySQL", "HTML", "CSS", "JavaScript", "Git", "Spring Boot", "REST API"
];

const WEB_SKILLS = [
  "HTML", "CSS", "JavaScript", "React", "Node.js", "REST API",
  "MySQL", "Git", "Java", "Python"
];

function normalize(text) {
  return (text || '').toLowerCase().replace(/[^a-z0-9+#.\- ]/g, ' ');
}

function getStatus(score) {
  if (score >= 75) return "PLACEMENT READY";
  if (score >= 50) return "GOOD";
  return "NEEDS IMPROVEMENT";
}

// In-Memory & File-based History Storage
const historyFile = path.join(__dirname, 'history.json');
let history = [
  {
    student: "Bhavana",
    role: "Java Developer",
    score: 85,
    status: "PLACEMENT READY"
  }
];

if (fs.existsSync(historyFile)) {
  try {
    history = JSON.parse(fs.readFileSync(historyFile, 'utf8'));
  } catch (e) {
    console.warn('Could not read history.json, using defaults.');
  }
}

function saveHistory() {
  try {
    fs.writeFileSync(historyFile, JSON.stringify(history, null, 2), 'utf8');
  } catch (e) {
    // Read-only filesystem safe
  }
}

// Router
const router = express.Router();

router.post('/analyze', (req, res) => {
  const { name, branch, cgpa, role, resume } = req.body;

  if (!name || !branch || !cgpa || !resume) {
    return res.status(400).json({ error: "Please fill all fields and enter your resume text." });
  }

  const parsedCgpa = parseFloat(cgpa);
  if (isNaN(parsedCgpa)) {
    return res.status(400).json({ error: "CGPA must be a valid number." });
  }

  const normResume = normalize(resume);
  const targetRole = role || 'Java Developer';
  const required = targetRole.toLowerCase().includes('web') ? WEB_SKILLS : JAVA_SKILLS;

  const matchedSkills = [];
  const missingSkills = [];

  for (const skill of required) {
    if (normResume.includes(normalize(skill))) {
      matchedSkills.push(skill);
    } else {
      missingSkills.push(skill);
    }
  }

  let score = Math.round((matchedSkills.length * 80) / required.length);

  // Academic / Project Bonus
  if (normResume.includes('project')) score = Math.min(100, score + 10);
  if (normResume.includes('education') || normResume.includes('cgpa')) score = Math.min(100, score + 5);
  if (normResume.includes('internship') || normResume.includes('intern')) score = Math.min(100, score + 5);

  const suggestions = [];
  for (const skill of missingSkills) {
    suggestions.push(`Consider adding evidence of ${skill}.`);
  }
  if (!normResume.includes('project')) {
    suggestions.push("Add 1–2 academic or personal projects with measurable outcomes.");
  }
  if (!normResume.includes('internship') && !normResume.includes('intern')) {
    suggestions.push("Mention internship, training, or practical experience if available.");
  }
  if (suggestions.length === 0) {
    suggestions.push("Keep your resume concise and tailor keywords to each job description.");
  }

  const status = getStatus(score);

  const result = {
    student: {
      name: name.trim(),
      branch: branch.trim(),
      cgpa: parsedCgpa
    },
    role: targetRole,
    score,
    status,
    matchedSkills,
    missingSkills,
    suggestions
  };

  history.unshift({
    student: name.trim(),
    role: targetRole,
    score,
    status
  });
  saveHistory();

  return res.json(result);
});

router.get('/history', (req, res) => {
  return res.json(history);
});

app.use('/api', router);
app.use(router);

// Serve web frontend
const webDir = path.join(__dirname, 'web');
if (fs.existsSync(webDir)) {
  app.use(express.static(webDir));
}
app.use(express.static(__dirname));

app.get('*', (req, res) => {
  const indexPath = fs.existsSync(path.join(webDir, 'index.html'))
    ? path.join(webDir, 'index.html')
    : path.join(__dirname, 'index.html');
  if (fs.existsSync(indexPath)) {
    return res.sendFile(indexPath);
  }
  return res.status(404).send('Page not found');
});

module.exports = app;

if (require.main === module) {
  app.listen(PORT, () => {
    console.log(`====================================================`);
    console.log(`  📄 CAMPUS SMART RESUME ANALYZER (NODE.JS + EXPRESS)`);
    console.log(`  🌐 Server running at: http://localhost:${PORT}`);
    console.log(`====================================================`);
  });
}
