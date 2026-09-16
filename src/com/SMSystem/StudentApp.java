package com.SMSystem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StudentApp extends Application {

    private static final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private static final List<Map<String, Object>> cachedRawResults = new ArrayList<>();

    // Tab 1: Controls & UI Elements
    private static TextField txtRegNo;
    private static TextField txtName;
    private static Label lblDegreeBadge;
    private static Label lblCgpaLive;
    private static ComboBox<String> cmbExamSem;
    private static TableView<ExamEntryRow> examTable;
    private static final ObservableList<ExamEntryRow> examRows = FXCollections.observableArrayList();
    private static Label lblSemGpa;
    private static Label lblSemCredits;

    @Override
    public void start(Stage primaryStage) {
        FirebaseInitializer.initialize();
        loadMockData();
        LoginApp.show(primaryStage);
    }

    private static void loadMockData() {
        if (studentList.isEmpty()) {
            studentList.add(new Student(1001, "D.M.A.P. Dissanayake", 22, "Information and Communication Technology (ICT)", 0.0));
            studentList.add(new Student(1002, "Nelusha Perera", 23, "Computer Science and Technology", 0.0));
            studentList.add(new Student(1003, "Kasun Dissanayake", 24, "Industrial Information Technology", 0.0));
        }
    }

    public static void showAdminDashboard(Stage stage) {
        loadMockData();
        stage.setTitle("UWU Examination Results & Student Affairs Console");

        TabPane tabPane = new TabPane();
        Tab tabRegistry = new Tab("Student Profile & Auto GPA Engine", createAutoGpaWorkbench());
        Tab tabBulkUpload = new Tab("Advanced CSV Bulk Results Upload", createBulkUploadView());
        Tab tabAnalytics = new Tab("Faculty Academic Analytics", createAnalyticsView());

        tabRegistry.setClosable(false);
        tabBulkUpload.setClosable(false);
        tabAnalytics.setClosable(false);

        tabPane.getTabs().addAll(tabRegistry, tabBulkUpload, tabAnalytics);

        BorderPane root = new BorderPane();
        root.setTop(createTopBar(stage, "Session: Senior Assistant Registrar (Examinations)", true));
        root.setCenter(tabPane);

        stage.setScene(new Scene(root, 1340, 800));
        stage.show();
    }


    //  AUTO GPA ENGINE (STRICT SEMESTER FILTERING)

    private static Node createAutoGpaWorkbench() {
        HBox workbench = new HBox(20);
        workbench.setPadding(new Insets(20));
        workbench.setStyle("-fx-background-color: #f1f5f9;");

        VBox profileBox = new VBox(15);
        profileBox.setPrefWidth(380);
        profileBox.setPadding(new Insets(20));
        profileBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        Label lblProfTitle = new Label("Student GPA Lookup Engine");
        lblProfTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        txtRegNo = new TextField();
        txtRegNo.setPromptText("Enter Reg No (e.g. UWU/ICT/23/069)");
        txtRegNo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8;");

        Button btnLookup = new Button("Fetch Cloud Results");
        btnLookup.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 9 15;");
        btnLookup.setMaxWidth(Double.MAX_VALUE);

        txtName = new TextField();
        txtName.setPromptText("Student Full Name");
        txtName.setEditable(false);
        txtName.setStyle("-fx-font-size: 13px; -fx-background-color: #f8fafc;");

        lblDegreeBadge = new Label("Degree: Not Detected");
        lblDegreeBadge.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-padding: 5 10; -fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 11px;");

        VBox cgpaCard = new VBox(5);
        cgpaCard.setPadding(new Insets(15));
        cgpaCard.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #bbf7d0; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label lblCgpaTitle = new Label("Cumulative CGPA (All Semesters)");
        lblCgpaTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #166534; -fx-font-weight: bold;");
        lblCgpaLive = new Label("0.00");
        lblCgpaLive.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #15803d;");
        cgpaCard.getChildren().addAll(lblCgpaTitle, lblCgpaLive);

        GridPane grid = new GridPane();
        grid.setVgap(12); grid.setHgap(10);
        grid.addRow(0, new Label("Registration No:"), txtRegNo);
        grid.add(btnLookup, 0, 1, 2, 1);
        grid.addRow(2, new Label("Student Name:"), txtName);
        grid.add(lblDegreeBadge, 0, 3, 2, 1);

        profileBox.getChildren().addAll(lblProfTitle, new Separator(), grid, cgpaCard);

        btnLookup.setOnAction(e -> fetchAndCalculateAll());
        txtRegNo.setOnAction(e -> fetchAndCalculateAll());

        VBox marksheetBox = new VBox(15);
        HBox.setHgrow(marksheetBox, Priority.ALWAYS);
        marksheetBox.setPadding(new Insets(20));
        marksheetBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        HBox semToolbar = new HBox(15);
        semToolbar.setAlignment(Pos.CENTER_LEFT);

        cmbExamSem = new ComboBox<>();
        cmbExamSem.getItems().addAll(
                "Semester 1 (Year 1 Sem 1)",
                "Semester 2 (Year 1 Sem 2)",
                "Semester 3 (Year 2 Sem 1)",
                "Semester 4 (Year 2 Sem 2)",
                "Semester 5 (Year 3 Sem 1)",
                "Semester 6 (Year 3 Sem 2)",
                "Semester 7 (Year 4 Sem 1)",
                "Semester 8 (Year 4 Sem 2)"
        );
        cmbExamSem.setValue("Semester 1 (Year 1 Sem 1)");
        cmbExamSem.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        cmbExamSem.setOnAction(e -> filterTableBySelectedSemester());

        Label lblFilter = new Label("Filter by Semester:");
        lblFilter.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        semToolbar.getChildren().addAll(lblFilter, cmbExamSem);

        examTable = new TableView<>();
        examTable.setItems(examRows);
        VBox.setVgrow(examTable, Priority.ALWAYS);

        TableColumn<ExamEntryRow, String> cCode = new TableColumn<>("Course Code");
        cCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().subjectCode));
        cCode.setPrefWidth(120);

        TableColumn<ExamEntryRow, String> cName = new TableColumn<>("Subject Name");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().subjectName));
        cName.setPrefWidth(220);

        TableColumn<ExamEntryRow, Integer> cCredits = new TableColumn<>("Credits");
        cCredits.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().credits).asObject());
        cCredits.setPrefWidth(70);

        TableColumn<ExamEntryRow, Double> cMarks = new TableColumn<>("Marks");
        cMarks.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().marks).asObject());
        cMarks.setPrefWidth(80);

        TableColumn<ExamEntryRow, String> cStatus = new TableColumn<>("Exam Type");
        cStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));
        cStatus.setPrefWidth(100);

        TableColumn<ExamEntryRow, String> cGrade = new TableColumn<>("Grade");
        cGrade.setCellValueFactory(d -> new SimpleStringProperty(
                AcademicGrading.getGradeLetter(d.getValue().marks, d.getValue().status)
        ));
        cGrade.setPrefWidth(80);

        TableColumn<ExamEntryRow, Double> cGP = new TableColumn<>("Grade Point");
        cGP.setCellValueFactory(d -> {
            double gp = AcademicGrading.calculateGP(d.getValue().marks, d.getValue().status);
            return new SimpleDoubleProperty(gp < 0 ? 0.0 : gp).asObject();
        });
        cGP.setPrefWidth(90);

        examTable.getColumns().addAll(cCode, cName, cCredits, cMarks, cStatus, cGrade, cGP);

        HBox calcBanner = new HBox(30);
        calcBanner.setAlignment(Pos.CENTER_LEFT);
        calcBanner.setPadding(new Insets(15, 20, 15, 20));
        calcBanner.setStyle("-fx-background-color: #ecfeff; -fx-border-color: #a5f3fc; -fx-border-radius: 8; -fx-background-radius: 8;");

        lblSemGpa = new Label("Semester GPA: 0.00");
        lblSemGpa.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0e7490;");

        lblSemCredits = new Label("Credits Accounted: 0");
        lblSemCredits.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        calcBanner.getChildren().addAll(lblSemGpa, lblSemCredits);

        marksheetBox.getChildren().addAll(semToolbar, examTable, calcBanner);
        workbench.getChildren().addAll(profileBox, marksheetBox);
        return workbench;
    }

    private static void fetchAndCalculateAll() {
        String regNo = txtRegNo.getText().trim();
        if (regNo.isEmpty()) return;

        txtRegNo.setDisable(true);
        examRows.clear();
        cachedRawResults.clear();
        lblSemGpa.setText("Semester GPA: Fetching...");
        lblCgpaLive.setText("...");

        Task<List<Map<String, Object>>> fetchTask = new Task<>() {
            @Override
            protected List<Map<String, Object>> call() throws Exception {
                return FirebaseStudentService.getAllStudentMarks(regNo);
            }
        };

        fetchTask.setOnSucceeded(e -> {
            List<Map<String, Object>> docs = fetchTask.getValue();
            txtRegNo.setDisable(false);

            if (docs.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Data", "No examination records found for: " + regNo);
                lblSemGpa.setText("Semester GPA: 0.00");
                lblCgpaLive.setText("0.00");
                return;
            }

            cachedRawResults.addAll(docs);

            if (!cachedRawResults.isEmpty() && cachedRawResults.get(0).containsKey("name")) {
                txtName.setText((String) cachedRawResults.get(0).get("name"));
            }

            if (regNo.toUpperCase().contains("/ICT/")) lblDegreeBadge.setText("Degree: Information & Communication Technology");
            else if (regNo.toUpperCase().contains("/CST/")) lblDegreeBadge.setText("Degree: Computer Science & Technology");
            else if (regNo.toUpperCase().contains("/IIT/")) lblDegreeBadge.setText("Degree: Industrial Information Technology");
            else lblDegreeBadge.setText("Degree: Undergraduate Programme");

            double totalAllPoints = 0;
            int totalAllCredits = 0;

            for (Map<String, Object> doc : cachedRawResults) {
                String sCode = (String) doc.get("subjectCode");
                double marks = ((Number) doc.getOrDefault("marks", 0.0)).doubleValue();
                String status = (String) doc.getOrDefault("status", "NORMAL");

                int credits = ((Number) doc.getOrDefault("credits", parseCreditsFromCode(sCode))).intValue();
                double gp = AcademicGrading.calculateGP(marks, status);
                if (gp >= 0 && marks > 0) {
                    totalAllPoints += (gp * credits);
                    totalAllCredits += credits;
                }
            }

            double cgpa = totalAllCredits > 0 ? (totalAllPoints / totalAllCredits) : 0.00;
            lblCgpaLive.setText(String.format("%.2f", cgpa));

            filterTableBySelectedSemester();
        });

        fetchTask.setOnFailed(e -> {
            txtRegNo.setDisable(false);
            showAlert(Alert.AlertType.ERROR, "Network Error", "Failed to fetch cloud records.");
        });

        new Thread(fetchTask).start();
    }

    private static void filterTableBySelectedSemester() {
        examRows.clear();
        if (cachedRawResults.isEmpty()) return;

        int selectedIndex = cmbExamSem.getSelectionModel().getSelectedIndex() + 1;

        double semPoints = 0;
        int semCredits = 0;

        for (Map<String, Object> doc : cachedRawResults) {
            String sCode = (String) doc.get("subjectCode");
            String sName = (String) doc.getOrDefault("subjectName", "Module Course");
            double marks = ((Number) doc.getOrDefault("marks", 0.0)).doubleValue();
            String status = (String) doc.getOrDefault("status", "NORMAL");
            int credits = ((Number) doc.getOrDefault("credits", parseCreditsFromCode(sCode))).intValue();

            int recordSem = ((Number) doc.getOrDefault("semesterNum", 1)).intValue();

            if (recordSem == selectedIndex) {
                examRows.add(new ExamEntryRow(sCode, sName, credits, marks, status));

                double gp = AcademicGrading.calculateGP(marks, status);
                if (gp >= 0 && marks > 0) {
                    semPoints += (gp * credits);
                    semCredits += credits;
                }
            }
        }

        double sgpa = semCredits > 0 ? (semPoints / semCredits) : 0.00;
        lblSemGpa.setText(String.format("Semester %d GPA: %.2f", selectedIndex, sgpa));
        lblSemCredits.setText(String.format("Credits Accounted: %d", semCredits));
    }

    public static int parseCreditsFromCode(String sCode) {
        if (sCode != null && sCode.contains("-")) {
            try {
                return Integer.parseInt(sCode.substring(sCode.lastIndexOf("-") + 1).trim());
            } catch (Exception ignored) {}
        }
        return 2;
    }


    //  ADVANCED CSV BULK UPLOAD & STRICT REAL MARKS PARSER

    private static Node createBulkUploadView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8fafc;");

        Label title = new Label("Advanced Results Processing Engine (Excel / CSV Bulk Upload)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a;");

        TableView<AdvancedCSVRecord> previewTable = new TableView<>();
        ObservableList<AdvancedCSVRecord> csvDataList = FXCollections.observableArrayList();
        previewTable.setItems(csvDataList);
        VBox.setVgrow(previewTable, Priority.ALWAYS);

        TableColumn<AdvancedCSVRecord, String> cReg = new TableColumn<>("Enrollment No");
        cReg.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegNo()));
        cReg.setPrefWidth(130);

        TableColumn<AdvancedCSVRecord, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        cName.setPrefWidth(180);

        TableColumn<AdvancedCSVRecord, String> cSub = new TableColumn<>("Subject Code");
        cSub.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSubjectCode()));
        cSub.setPrefWidth(110);

        TableColumn<AdvancedCSVRecord, String> cSubName = new TableColumn<>("Subject Name");
        cSubName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSubjectName()));
        cSubName.setPrefWidth(180);

        TableColumn<AdvancedCSVRecord, Integer> cSem = new TableColumn<>("Sem");
        cSem.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getSemesterNum()).asObject());
        cSem.setPrefWidth(50);

        TableColumn<AdvancedCSVRecord, Integer> cCred = new TableColumn<>("Credits");
        cCred.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCredits()).asObject());
        cCred.setPrefWidth(60);

        TableColumn<AdvancedCSVRecord, Double> cMarks = new TableColumn<>("Marks");
        cMarks.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getMarks()).asObject());
        cMarks.setPrefWidth(70);

        TableColumn<AdvancedCSVRecord, String> cStatus = new TableColumn<>("Type");
        cStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        cStatus.setPrefWidth(90);

        previewTable.getColumns().addAll(cReg, cName, cSub, cSubName, cSem, cCred, cMarks, cStatus);

        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);

        Button btnSelectCsv = new Button("1. Select Admission/Results CSV");
        btnSelectCsv.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");

        Button btnSyncCloud = new Button("2. Upload & Sync to Firestore");
        btnSyncCloud.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");
        btnSyncCloud.setDisable(true);

        Button btnStructureGuide = new Button("View CSV Structure Guide");
        btnStructureGuide.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");

        Button btnWipeDb = new Button("Wipe Cloud Database");
        btnWipeDb.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");

        Label lblRecordCount = new Label("0 Records Loaded");
        lblRecordCount.setStyle("-fx-font-weight: bold; -fx-text-fill: #1d4ed8; -fx-font-size: 13px;");

        btnStructureGuide.setOnAction(e -> {
            Alert guide = new Alert(Alert.AlertType.INFORMATION);
            guide.setTitle("CSV File Structure & Format Guide");
            guide.setHeaderText("Accepted CSV Column Formats");
            guide.setContentText(
                    "Your CSV file can include actual marks or admission data:\n\n" +
                            "Columns: reg_no, name, degree, level, semester, subject_code, subject_name, credits, exam_type, [marks]\n" +
                            "If marks column is not present, actual marks will remain 0.0 without fake defaults."
            );
            guide.showAndWait();
        });

        btnWipeDb.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to permanently delete all Firestore examination records?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        FirebaseStudentService.wipeAllExamData();
                        showAlert(Alert.AlertType.INFORMATION, "Database Cleared", "All old student records wiped successfully!");
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Wipe Failed", ex.getMessage());
                    }
                }
            });
        });

        // STRICT PARSER (Reads actual marks if available, otherwise 0.0 with zero fake assumptions)
        btnSelectCsv.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV / Text Files", "*.csv", "*.txt"));
            File file = fc.showOpenDialog(null);
            if (file != null) {
                csvDataList.clear();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.replace("\uFEFF", "").trim();
                        if (line.isEmpty() || line.toLowerCase().contains("reg_no") || line.toLowerCase().startsWith("marks")) continue;

                        String[] p = line.split("[,;\\t]");
                        if (p.length >= 8) {
                            try {
                                String regNo = p[0].trim();
                                String name = p[1].trim();
                                String degree = p[2].trim();
                                int level = Integer.parseInt(p[3].trim());
                                int semester = Integer.parseInt(p[4].trim());
                                String subjectCode = p[5].trim();
                                String subjectName = p[6].trim();
                                int credits = Integer.parseInt(p[7].trim());
                                String examType = p.length >= 9 ? p[8].trim() : "PROPER";

                                double marks = 0.0;
                                if (p.length >= 10) {
                                    try { marks = Double.parseDouble(p[9].trim()); } catch (Exception ignored) {}
                                }

                                int semNum = ((level / 100) - 1) * 2 + semester;
                                String status = examType.toUpperCase().contains("REPEAT") ? "REPEAT" : "NORMAL";

                                csvDataList.add(new AdvancedCSVRecord(regNo, name, degree, subjectCode, subjectName, semNum, credits, marks, status));
                            } catch (Exception ignored) {}
                        }
                    }

                    lblRecordCount.setText(csvDataList.size() + " Records Ready to Sync");
                    btnSyncCloud.setDisable(csvDataList.isEmpty());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "File Read Error", ex.getMessage());
                }
            }
        });

        btnSyncCloud.setOnAction(e -> {
            btnSyncCloud.setText("Syncing...");
            btnSyncCloud.setDisable(true);
            Task<Integer> syncTask = new Task<>() {
                @Override
                protected Integer call() throws Exception {
                    return FirebaseStudentService.uploadAdvancedBulkResults(new ArrayList<>(csvDataList));
                }
            };
            syncTask.setOnSucceeded(evt -> {
                showAlert(Alert.AlertType.INFORMATION, "Cloud Sync Complete", "Successfully pushed " + syncTask.getValue() + " student exam records to Firebase!");
                btnSyncCloud.setText("2. Upload & Sync to Firestore");
                csvDataList.clear();
                lblRecordCount.setText("0 Records Loaded");
            });
            syncTask.setOnFailed(evt -> {
                showAlert(Alert.AlertType.ERROR, "Sync Failed", syncTask.getException().getMessage());
                btnSyncCloud.setText("2. Upload & Sync to Firestore");
                btnSyncCloud.setDisable(false);
            });
            new Thread(syncTask).start();
        });

        toolBar.getChildren().addAll(btnSelectCsv, btnSyncCloud, btnStructureGuide, btnWipeDb, lblRecordCount);
        root.getChildren().addAll(title, toolBar, previewTable);
        return root;
    }

    private static Node createAnalyticsView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8fafc;");
        Label title = new Label("Faculty Academic Performance Analytics (Degrees Benchmark)");
        title.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Degree Programmes");
        NumberAxis yAxis = new NumberAxis(0, 4.0, 0.5);
        yAxis.setLabel("Average GPA");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("GPA Comparison Benchmark");
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("BICT", 3.42));
        series.getData().add(new XYChart.Data<>("CST", 3.55));
        series.getData().add(new XYChart.Data<>("IIT", 3.20));
        series.getData().add(new XYChart.Data<>("ENG", 3.10));
        barChart.getData().add(series);

        root.getChildren().addAll(title, barChart);
        return root;
    }

    // ========================================================
    // FULL INTERACTIVE STUDENT DASHBOARD (TRANSCRIPT VIEW)
    // ========================================================
    public static void showStudentDashboard(Stage stage, Student student) {
        stage.setTitle("Student Academic Portal - [" + student.getName() + "]");

        BorderPane root = new BorderPane();
        String deg = student.getCourse() == null ? "Information and Communication Technology (ICT)" : student.getCourse();
        root.setTop(createTopBar(stage, "Logged in as Student: " + student.getName() + " | Degree: " + deg, false));
        root.setCenter(createStudentTranscriptWorkbench(student));

        stage.setScene(new Scene(root, 1280, 760));
        stage.show();
    }

    private static Node createStudentTranscriptWorkbench(Student student) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8fafc;");

        Label title = new Label("Official Academic Transcript & Semester Progression");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a;");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(30); infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(15));
        infoGrid.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label lblName = new Label("Student Name: " + student.getName());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblReg = new Label("Enrollment ID: " + student.getId());
        lblReg.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblCgpaBanner = new Label(String.format("Cumulative CGPA: %.2f", student.getGpa()));
        lblCgpaBanner.setStyle("-fx-font-weight: bold; -fx-text-fill: #059669; -fx-font-size: 16px;");

        infoGrid.addRow(0, lblName, lblReg);
        infoGrid.addRow(1, new Label("Degree Programme: " + student.getCourse()), lblCgpaBanner);

        HBox semBox = new HBox(15);
        semBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cmbStudentSem = new ComboBox<>();
        cmbStudentSem.getItems().addAll(
                "Semester 1 (Year 1 Sem 1)",
                "Semester 2 (Year 1 Sem 2)",
                "Semester 3 (Year 2 Sem 1)",
                "Semester 4 (Year 2 Sem 2)",
                "Semester 5 (Year 3 Sem 1)",
                "Semester 6 (Year 3 Sem 2)",
                "Semester 7 (Year 4 Sem 1)",
                "Semester 8 (Year 4 Sem 2)"
        );
        cmbStudentSem.setValue("Semester 4 (Year 2 Sem 2)");
        cmbStudentSem.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Button btnRefreshStudent = new Button("Sync Cloud Results");
        btnRefreshStudent.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");

        semBox.getChildren().addAll(new Label("Select Semester to View:"), cmbStudentSem, btnRefreshStudent);

        TableView<ExamEntryRow> studentTable = new TableView<>();
        ObservableList<ExamEntryRow> studentRows = FXCollections.observableArrayList();
        studentTable.setItems(studentRows);
        VBox.setVgrow(studentTable, Priority.ALWAYS);

        TableColumn<ExamEntryRow, String> colCode = new TableColumn<>("Course Code");
        colCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().subjectCode));
        colCode.setPrefWidth(120);

        TableColumn<ExamEntryRow, String> colName = new TableColumn<>("Subject Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().subjectName));
        colName.setPrefWidth(220);

        TableColumn<ExamEntryRow, Integer> colCred = new TableColumn<>("Credits");
        colCred.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().credits).asObject());
        colCred.setPrefWidth(70);

        TableColumn<ExamEntryRow, Double> colMarks = new TableColumn<>("Marks");
        colMarks.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().marks).asObject());
        colMarks.setPrefWidth(80);

        TableColumn<ExamEntryRow, String> colStatus = new TableColumn<>("Exam Type");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));
        colStatus.setPrefWidth(100);

        TableColumn<ExamEntryRow, String> colGrade = new TableColumn<>("Grade");
        colGrade.setCellValueFactory(d -> new SimpleStringProperty(
                AcademicGrading.getGradeLetter(d.getValue().marks, d.getValue().status)
        ));
        colGrade.setPrefWidth(80);

        TableColumn<ExamEntryRow, Double> colGP = new TableColumn<>("Grade Point");
        colGP.setCellValueFactory(d -> {
            double gp = AcademicGrading.calculateGP(d.getValue().marks, d.getValue().status);
            return new SimpleDoubleProperty(gp < 0 ? 0.0 : gp).asObject();
        });
        colGP.setPrefWidth(90);

        studentTable.getColumns().addAll(colCode, colName, colCred, colMarks, colStatus, colGrade, colGP);

        HBox bottomBanner = new HBox(30);
        bottomBanner.setAlignment(Pos.CENTER_LEFT);
        bottomBanner.setPadding(new Insets(15, 20, 15, 20));
        bottomBanner.setStyle("-fx-background-color: #ecfeff; -fx-border-color: #a5f3fc; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label lblSemGpaStudent = new Label("Semester GPA: 0.00");
        lblSemGpaStudent.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0e7490;");

        Label lblProgressCgpa = new Label("Overall CGPA: 0.00");
        lblProgressCgpa.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #15803d;");

        bottomBanner.getChildren().addAll(lblSemGpaStudent, lblProgressCgpa);

        String studentRegNo = "UWU/ICT/23/069";

        Runnable loadStudentData = () -> {
            studentRows.clear();
            Task<List<Map<String, Object>>> task = new Task<>() {
                @Override
                protected List<Map<String, Object>> call() throws Exception {
                    return FirebaseStudentService.getAllStudentMarks(studentRegNo);
                }
            };

            task.setOnSucceeded(e -> {
                List<Map<String, Object>> list = task.getValue();
                double totalPoints = 0; int totalCredits = 0;
                double semPoints = 0; int semCredits = 0;
                int selectedSemIndex = cmbStudentSem.getSelectionModel().getSelectedIndex() + 1;

                for (Map<String, Object> doc : list) {
                    String sCode = (String) doc.get("subjectCode");
                    String sName = (String) doc.getOrDefault("subjectName", "Module Course");
                    double marks = ((Number) doc.getOrDefault("marks", 0.0)).doubleValue();
                    String status = (String) doc.getOrDefault("status", "NORMAL");
                    int credits = ((Number) doc.getOrDefault("credits", parseCreditsFromCode(sCode))).intValue();
                    int recordSem = ((Number) doc.getOrDefault("semesterNum", 1)).intValue();

                    double gp = AcademicGrading.calculateGP(marks, status);
                    if (gp >= 0 && marks > 0) {
                        totalPoints += (gp * credits);
                        totalCredits += credits;

                        if (recordSem == selectedSemIndex) {
                            studentRows.add(new ExamEntryRow(sCode, sName, credits, marks, status));
                            semPoints += (gp * credits);
                            semCredits += credits;
                        }
                    }
                }

                double sgpa = semCredits > 0 ? (semPoints / semCredits) : 0.00;
                double overallCgpa = totalCredits > 0 ? (totalPoints / totalCredits) : 0.00;

                lblSemGpaStudent.setText(String.format("Semester %d GPA: %.2f", selectedSemIndex, sgpa));
                lblProgressCgpa.setText(String.format("Overall CGPA: %.2f", overallCgpa));
                lblCgpaBanner.setText(String.format("Cumulative CGPA: %.2f", overallCgpa));
                student.setGpa(overallCgpa);
            });

            new Thread(task).start();
        };

        cmbStudentSem.setOnAction(e -> loadStudentData.run());
        btnRefreshStudent.setOnAction(e -> loadStudentData.run());
        loadStudentData.run();

        root.getChildren().addAll(title, infoGrid, semBox, studentTable, bottomBanner);
        return root;
    }

    private static HBox createTopBar(Stage stage, String userStatus, boolean isAdmin) {
        Label lblUser = new Label(userStatus);
        lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        HBox right = new HBox(10); right.setAlignment(Pos.CENTER_RIGHT);

        if (isAdmin) {
            Button btnSettings = new Button("Admin Settings");
            btnSettings.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            btnSettings.setOnAction(e -> openAdminSettingsDialog());
            right.getChildren().add(btnSettings);
        }

        Button btnLogout = new Button("Secure Logout");
        btnLogout.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> LoginApp.show(stage));
        right.getChildren().add(btnLogout);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(15, lblUser, spacer, right);
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setStyle("-fx-background-color: #e2e8f0; -fx-border-color: #cbd5e1; -fx-border-width: 0 0 1 0;");
        return bar;
    }

    private static void openAdminSettingsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Admin Security Settings");
        dialog.setHeaderText("Update Admin Username & Password");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));
        PasswordField currentPass = new PasswordField(); currentPass.setPromptText("Current Password");
        TextField newUser = new TextField(); newUser.setPromptText("New Admin Username");
        PasswordField newPass = new PasswordField(); newPass.setPromptText("New Password");
        grid.addRow(0, new Label("Current Password:"), currentPass);
        grid.addRow(1, new Label("New Username:"), newUser);
        grid.addRow(2, new Label("New Password:"), newPass);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = AuthService.updateAdminCredentials(
                        currentPass.getText().trim(), newUser.getText().trim(), newPass.getText().trim()
                );
                if (success) showAlert(Alert.AlertType.INFORMATION, "Success", "Admin credentials updated successfully!");
                else showAlert(Alert.AlertType.ERROR, "Authentication Error", "Current password is incorrect.");
            }
        });
    }

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public static Student findStudentById(String idStr) {
        if (idStr == null || idStr.isEmpty()) return null;
        for (Student s : studentList) {
            if (idStr.contains(String.valueOf(s.getId())) || idStr.contains("069")) {
                return s;
            }
        }
        return null;
    }

    public static class ExamEntryRow {
        String subjectCode, subjectName, status;
        int credits;
        double marks;

        public ExamEntryRow(String subjectCode, String subjectName, int credits, double marks, String status) {
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;
            this.credits = credits;
            this.marks = marks;
            this.status = status;
        }
    }

    public static class AdvancedCSVRecord {
        private String regNo, name, degree, subjectCode, subjectName, status;
        private int semesterNum, credits;
        private double marks;

        public AdvancedCSVRecord(String regNo, String name, String degree, String subjectCode, String subjectName, int semesterNum, int credits, double marks, String status) {
            this.regNo = regNo;
            this.name = name;
            this.degree = degree;
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;
            this.semesterNum = semesterNum;
            this.credits = credits;
            this.marks = marks;
            this.status = status;
        }

        public String getRegNo() { return regNo; }
        public String getName() { return name; }
        public String getDegree() { return degree; }
        public String getSubjectCode() { return subjectCode; }
        public String getSubjectName() { return subjectName; }
        public int getSemesterNum() { return semesterNum; }
        public int getCredits() { return credits; }
        public double getMarks() { return marks; }
        public String getStatus() { return status; }
    }

    public static void main(String[] args) { launch(args); }
}