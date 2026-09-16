package com.SMSystem;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.*;

public class FirebaseStudentService {

    public static String toDocId(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) return "UNKNOWN";
        return regNo.trim().replace("/", "_");
    }

    public static String fromDocId(String docId) {
        if (docId == null || docId.trim().isEmpty()) return "UNKNOWN";
        return docId.trim().replace("_", "/");
    }

    /**
     * Completely wipes out all examination records and student profiles from Firestore.
     * Useful for clearing old corrupt or inconsistent test data.
     */
    public static void wipeAllExamData() throws Exception {
        Firestore db = FirebaseInitializer.getDB();

        // 1. Delete all documents in 'exam_admissions'
        ApiFuture<QuerySnapshot> examDocs = db.collection("exam_admissions").get();
        List<QueryDocumentSnapshot> examList = examDocs.get().getDocuments();
        if (!examList.isEmpty()) {
            WriteBatch batch1 = db.batch();
            for (DocumentSnapshot doc : examList) {
                batch1.delete(doc.getReference());
            }
            batch1.commit().get();
        }

        // 2. Delete all documents in 'students'
        ApiFuture<QuerySnapshot> studentDocs = db.collection("students").get();
        List<QueryDocumentSnapshot> studentList = studentDocs.get().getDocuments();
        if (!studentList.isEmpty()) {
            WriteBatch batch2 = db.batch();
            for (DocumentSnapshot doc : studentList) {
                batch2.delete(doc.getReference());
            }
            batch2.commit().get();
        }
    }

    /**
     * Bulk upload exam results into Firestore with WriteBatch handling.
     * Synchronizes student master profile and exam admission marksheet records including subject names and credits.
     */
    public static int uploadAdvancedBulkResults(List<StudentApp.AdvancedCSVRecord> records) throws Exception {
        if (records == null || records.isEmpty()) return 0;

        Firestore db = FirebaseInitializer.getDB();
        WriteBatch batch = db.batch();
        int count = 0;

        for (StudentApp.AdvancedCSVRecord rec : records) {
            String studentDocId = toDocId(rec.getRegNo());

            // 1. Student Profile Data (Merge into 'students' collection)
            DocumentReference studentRef = db.collection("students").document(studentDocId);
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("regNo", rec.getRegNo().trim());
            studentData.put("name", rec.getName().trim());
            studentData.put("degree", rec.getDegree().trim());
            batch.set(studentRef, studentData, SetOptions.merge());

            // 2. Exam Admission Record Data (Merge into 'exam_admissions' collection)
            String cleanSubCode = rec.getSubjectCode().trim().replace(" ", "-");
            String examDocId = studentDocId + "_" + cleanSubCode;
            DocumentReference examRef = db.collection("exam_admissions").document(examDocId);

            Map<String, Object> examData = new HashMap<>();
            examData.put("regNo", rec.getRegNo().trim());
            examData.put("name", rec.getName().trim());
            examData.put("subjectCode", rec.getSubjectCode().trim());
            examData.put("subjectName", rec.getSubjectName().trim());
            examData.put("semesterNum", rec.getSemesterNum()); // Academic Semester Index (1 to 8)
            examData.put("credits", rec.getCredits());
            examData.put("marks", rec.getMarks());
            examData.put("status", rec.getStatus() != null ? rec.getStatus().trim() : "NORMAL");

            batch.set(examRef, examData, SetOptions.merge());
            count++;
        }

        // Atomic commit to Cloud Firestore
        batch.commit().get();
        return count;
    }

    /**
     * Fetches all examination results for a specific student across all academic semesters
     */
    public static List<Map<String, Object>> getAllStudentMarks(String regNo) throws Exception {
        if (regNo == null || regNo.trim().isEmpty()) return Collections.emptyList();

        Firestore db = FirebaseInitializer.getDB();
        List<Map<String, Object>> results = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = db.collection("exam_admissions")
                .whereEqualTo("regNo", regNo.trim())
                .get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            results.add(doc.getData());
        }
        return results;
    }

    /**
     * Fetches examination records filtered by specific academic semester number (1 - 8)
     */
    public static List<Map<String, Object>> getStudentExamRecords(String regNo, int semesterNum) throws Exception {
        if (regNo == null || regNo.trim().isEmpty()) return Collections.emptyList();

        Firestore db = FirebaseInitializer.getDB();
        List<Map<String, Object>> results = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = db.collection("exam_admissions")
                .whereEqualTo("regNo", regNo.trim())
                .whereEqualTo("semesterNum", semesterNum)
                .get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            results.add(doc.getData());
        }
        return results;
    }

    /**
     * Updates or inserts a single subject mark record directly
     */
    public static void saveSingleMark(String regNo, String subCode, String subName, double marks, String status, int semesterNum, int credits) throws Exception {
        Firestore db = FirebaseInitializer.getDB();
        String cleanSubCode = subCode.trim().replace(" ", "-");
        String examDocId = toDocId(regNo) + "_" + cleanSubCode;
        DocumentReference examRef = db.collection("exam_admissions").document(examDocId);

        Map<String, Object> update = new HashMap<>();
        update.put("marks", marks);
        update.put("status", status != null ? status.trim() : "NORMAL");
        update.put("regNo", regNo.trim());
        update.put("subjectCode", subCode.trim());
        update.put("subjectName", subName.trim());
        update.put("semesterNum", semesterNum);
        update.put("credits", credits);

        examRef.set(update, SetOptions.merge()).get();
    }
}