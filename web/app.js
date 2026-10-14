const $ = id => document.getElementById(id);

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
    const response = await fetch("/api/analyze", {
      method: "POST",
      headers: {"Content-Type":"application/json"},
      body: JSON.stringify(payload)
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "Analysis failed.");

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
  const response = await fetch("/api/history");
  const data = await response.json();
  $("historyBody").innerHTML = data.length
    ? data.map(r => `<tr>
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
