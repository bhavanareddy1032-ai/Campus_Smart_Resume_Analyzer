const $ = id => document.getElementById(id);

// Skill dictionaries for client-side execution
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

function analyzeClientSide(payload) {
  const normResume = normalize(payload.resume);
  const targetRole = payload.role || 'Java Developer';
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

  const record = {
    student: {
      name: payload.name.trim(),
      branch: payload.branch.trim(),
      cgpa: parseFloat(payload.cgpa) || 8.0
    },
    role: targetRole,
    score,
    status,
    matchedSkills,
    missingSkills,
    suggestions
  };

  // Save to local storage
  try {
    const list = JSON.parse(localStorage.getItem('resume_history') || '[]');
    list.unshift({
      student: payload.name.trim(),
      role: targetRole,
      score,
      status
    });
    localStorage.setItem('resume_history', JSON.stringify(list));
  } catch(e) {}

  return record;
}

$("fileInput").addEventListener("change", e => {
  const file = e.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => $("resume").value = reader.result;
  reader.readAsText(file);
});

$("analyzeBtn").addEventListener("click", async () => {
  $("error").textContent = "";

  const payload = {
    name: $("name").value.trim(),
    branch: $("branch").value.trim(),
    cgpa: $("cgpa").value.trim(),
    role: $("role").value,
    resume: $("resume").value.trim()
  };

  if (!payload.name || !payload.branch || !payload.cgpa || !payload.resume) {
    $("error").textContent = "Please fill all fields and enter your resume text.";
    return;
  }

  $("analyzeBtn").textContent = "Analyzing...";
  $("analyzeBtn").disabled = true;

  try {
    let data;
    const isStaticHost = window.location.hostname.includes('github.io') || window.location.protocol === 'file:';

    if (!isStaticHost) {
      try {
        const response = await fetch("/api/analyze", {
          method: "POST",
          headers: {"Content-Type":"application/json"},
          body: JSON.stringify(payload)
        });
        if (response.ok) {
          data = await response.json();
        }
      } catch (netErr) {
        console.warn("Backend offline, running analysis in browser.");
      }
    }

    if (!data) {
      data = analyzeClientSide(payload);
    }

    $("result").classList.remove("hidden");
    $("score").textContent = data.score + "%";
    $("status").textContent = data.status;
    $("profileName").textContent = data.student.name;
    $("profileMeta").textContent =
      `${data.student.branch} • CGPA ${data.student.cgpa} • ${data.role}`;
    $("scoreBar").style.width = data.score + "%";

    $("matched").innerHTML = data.matchedSkills
      .map(s => `<span>${escapeHtml(s)}</span>`).join("");

    $("missing").innerHTML = data.missingSkills
      .map(s => `<span class="missing">${escapeHtml(s)}</span>`).join("");

    $("suggestions").innerHTML = data.suggestions
      .map(s => `<li>${escapeHtml(s)}</li>`).join("");

    await loadHistory();
    $("result").scrollIntoView({behavior:"smooth"});
  } catch (err) {
    $("error").textContent = err.message;
  } finally {
    $("analyzeBtn").textContent = "Analyze Resume";
    $("analyzeBtn").disabled = false;
  }
});

async function loadHistory() {
  let list = [];
  try {
    const response = await fetch("/api/history");
    if (response.ok) {
      list = await response.json();
    }
  } catch (e) {}

  if (!list || !list.length) {
    try {
      list = JSON.parse(localStorage.getItem('resume_history') || '[]');
    } catch(e) {}
  }

  if (!list.length) {
    list = [
      { student: "Bhavana", role: "Java Developer", score: 85, status: "PLACEMENT READY" }
    ];
  }

  $("historyBody").innerHTML = list.length
    ? list.map(r => `<tr>
        <td>${escapeHtml(r.student)}</td>
        <td>${escapeHtml(r.role)}</td>
        <td><strong>${r.score}%</strong></td>
        <td>${escapeHtml(r.status)}</td>
      </tr>`).join("")
    : `<tr><td colspan="4">No analyses yet.</td></tr>`;
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, c => ({
    "&":"&amp;", "<":"&lt;", ">":"&gt;", '"':"&quot;", "'":"&#039;"
  }[c]));
}

loadHistory();
