package com.SMSystem;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AICareerAgent {

    // Ollama local endpoint (Default for offline AI)
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "phi3"; // lightweight and fast model

    private final HttpClient httpClient;

    public AICareerAgent() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    /**
     * සිසුවාගේ දත්ත මත පදනම්ව AI උපදෙස් ලබා ගැනීම
     */
    public String getCareerAdvice(Student student) {
        String prompt = String.format(
                "You are an expert University Career Guidance Counselor and Talent Analytics Agent.\\n" +
                        "Analyze this student profile and provide highly strategic, professional advice.\\n\\n" +
                        "--- Student Profile ---\\n" +
                        "Name: %s\\n" +
                        "Course: %s\\n" +
                        "GPA: %.2f\\n" +
                        "Target Career Path: %s\\n" +
                        "Current Skills: %s\\n" +
                        "Certifications: %s\\n\\n" +
                        "Please provide:\\n" +
                        "1. An overall readiness rating (e.g., 8/10) with a 1-sentence justification.\\n" +
                        "2. The top 2 skill gaps they MUST bridge to achieve their target career path.\\n" +
                        "3. A recommended micro-credential or online course.\\n\\n" +
                        "Keep the response professional, encouraging, and under 150 words.",
                student.getName(), student.getCourse(), student.getGpa(),
                student.getTargetRole(), student.getSkills(), student.getCertifications()
        );

        try {
            String jsonRequest = String.format(
                    "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                    MODEL_NAME, prompt.replace("\"", "\\\"").replace("\n", "\\n")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractResponseFromJSON(response.body());
            } else {
                return getFallbackAdvice(student) + "\n\n(Note: Ollama API returned status code " + response.statusCode() + ")";
            }

        } catch (Exception e) {
            return getFallbackAdvice(student) + "\n\n[System Alert: Currently operating in Offline Fallback Mode. Start Ollama to activate full AI Agent.]";
        }
    }

    private String extractResponseFromJSON(String json) {
        try {
            int responseIndex = json.indexOf("\"response\":\"");
            if (responseIndex != -1) {
                int startIndex = responseIndex + 12;
                int endIndex = json.indexOf("\",\"", startIndex);
                if (endIndex != -1) {
                    String cleanText = json.substring(startIndex, endIndex);
                    return cleanText.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
                }
            }
            return json;
        } catch (Exception e) {
            return "Error parsing AI response: " + e.getMessage();
        }
    }

    private String getFallbackAdvice(Student student) {
        StringBuilder fallback = new StringBuilder();
        fallback.append("--- LOCAL RULE-BASED CAREER REPORT (Fallback Mode) ---\n\n");
        fallback.append("Overall Readiness: Good progress in ").append(student.getCourse()).append(".\n");

        if (student.getGpa() < 3.0) {
            fallback.append("- Recommendation: Focus on improving GPA above 3.0 to meet corporate interview benchmarks.\n");
        }

        String target = student.getTargetRole().toLowerCase();
        String currentSkills = student.getSkills().toLowerCase();

        if (target.contains("software") && !currentSkills.contains("git")) {
            fallback.append("- Identified Gap: Version Control (Git) is missing. Recommended Action: Learn Git & GitHub basics.\n");
        }

        fallback.append("- Career Path Recommendation: Since your target role is '")
                .append(student.getTargetRole())
                .append("', build projects using industry tools and add them to your resume.");

        return fallback.toString();
    }
}