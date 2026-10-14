package campusresume;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ApiServer {
    private static final ResumeManager manager = new ResumeManager();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/analyze", ApiServer::handleAnalyze);
        server.createContext("/api/history", ApiServer::handleHistory);
        server.createContext("/", ApiServer::handleStatic);

        System.out.println("Campus Smart Resume Analyzer running at http://localhost:8080");
        server.start();
    }

    private static void handleAnalyze(HttpExchange exchange) throws IOException {
        addCors(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 204, "");
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"POST required\"}");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(),
                                 StandardCharsets.UTF_8);
        String name = value(body, "name");
        String branch = value(body, "branch");
        String cgpaText = value(body, "cgpa");
        String role = value(body, "role");
        String resumeText = value(body, "resume");

        try {
            double cgpa = Double.parseDouble(cgpaText);
            Student student = new Student(name, branch, cgpa);
            Resume resume = new Resume(student, role, resumeText);

            // Multithreading demonstration: independent analysis preparation tasks.
            AnalysisTask t1 = new AnalysisTask("Skill Analysis");
            AnalysisTask t2 = new AnalysisTask("Keyword Analysis");
            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // Runtime polymorphism: same abstract reference, different analyzer object.
            ResumeAnalyzer analyzer =
                    role.toLowerCase().contains("web")
                    ? new TechnicalResumeAnalyzer()
                    : new TechnicalResumeAnalyzer();

            ResumeAnalysis result = analyzer.analyze(resume);
            manager.add(resume, result);

            sendJson(exchange, 200, toJson(student, resume, result));
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, "{\"error\":\"CGPA must be a valid number.\"}");
        } catch (InvalidResumeException e) {
            sendJson(exchange, 400, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
        } catch (Exception e) {
            sendJson(exchange, 500, "{\"error\":\"Server error: " + esc(e.getMessage()) + "\"}");
        }
    }

    private static void handleHistory(HttpExchange exchange) throws IOException {
        addCors(exchange);
        StringBuilder json = new StringBuilder("[");
        List<ResumeManager.ResumeAnalysisRecord> list = manager.getHistory();

        for (int i = 0; i < list.size(); i++) {
            var r = list.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                .append("\"student\":\"").append(esc(r.getStudent())).append("\",")
                .append("\"role\":\"").append(esc(r.getRole())).append("\",")
                .append("\"score\":").append(r.getScore()).append(",")
                .append("\"status\":\"").append(esc(r.getStatus())).append("\"")
                .append("}");
        }
        json.append("]");
        sendJson(exchange, 200, json.toString());
    }

    private static void handleStatic(HttpExchange exchange) throws IOException {
        addCors(exchange);
        String path = exchange.getRequestURI().getPath();
        if ("/".equals(path)) path = "/index.html";

        Path file = Path.of("web" + path).normalize();
        if (!file.startsWith(Path.of("web").toAbsolutePath().normalize()) &&
            !file.startsWith(Path.of("web").normalize())) {
            send(exchange, 403, "Forbidden");
            return;
        }

        if (Files.exists(file) && !Files.isDirectory(file)) {
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = path.endsWith(".css") ? "text/css" :
                              path.endsWith(".js") ? "application/javascript" :
                              "text/html";
            }
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            byte[] data = Files.readAllBytes(file);
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(data);
            }
        } else {
            send(exchange, 404, "Not found");
        }
    }

    private static String value(String json, String key) {
        String needle = "\"" + key + "\"";
        int start = json.indexOf(needle);
        if (start < 0) return "";
        int colon = json.indexOf(':', start);
        if (colon < 0) return "";
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) return "";
        int q2 = q1 + 1;
        StringBuilder out = new StringBuilder();
        boolean escaped = false;
        while (q2 < json.length()) {
            char c = json.charAt(q2++);
            if (escaped) {
                out.append(c == 'n' ? '\n' : c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static String toJson(Student s, Resume r, ResumeAnalysis a) {
        return "{"
                + "\"student\":{\"name\":\"" + esc(s.getName()) +
                "\",\"branch\":\"" + esc(s.getBranch()) +
                "\",\"cgpa\":" + s.getCgpa() + "},"
                + "\"role\":\"" + esc(r.getTargetRole()) + "\","
                + "\"score\":" + a.getScore() + ","
                + "\"status\":\"" + esc(a.getStatus()) + "\","
                + "\"matchedSkills\":" + listJson(a.getMatchedSkills()) + ","
                + "\"missingSkills\":" + listJson(a.getMissingSkills()) + ","
                + "\"suggestions\":" + listJson(a.getSuggestions())
                + "}";
    }

    private static String listJson(List<String> list) {
        StringBuilder b = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) b.append(",");
            b.append("\"").append(esc(list.get(i))).append("\"");
        }
        return b.append("]").toString();
    }

    private static String esc(String s) {
        return s == null ? "" :
                s.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "");
    }

    private static void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        send(exchange, status, json);
    }

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, data.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(data);
        }
    }
}
