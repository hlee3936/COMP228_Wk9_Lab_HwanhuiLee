package application;

import java.sql.SQLException;
import java.time.LocalDate;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		
		BorderPane pane = new BorderPane();
		
		//header
		Label title = new Label("Title");
		Label subtitle = new Label("subtitle");
		
		VBox header = new VBox(title, subtitle);
		
		header.setAlignment(Pos.CENTER_LEFT);
		header.setPadding(new Insets(15));
		header.setStyle("-fx-background-color: #6CC5EB;");
		
		//center
		GridPane form = new GridPane();

		form.setPadding(new Insets(20));
		form.setHgap(10);
		form.setVgap(10);

		TextField patientIdField = new TextField();
        patientIdField.setEditable(false);
		TextField nameField = new TextField();
		TextField ageField = new TextField();
		ComboBox<String> genderBox = new ComboBox<>();
		genderBox.getItems().addAll("Female", "Male", "Non-binary", "Prefer not to say");
		TextField phoneField = new TextField();
		TextField diagnosisField = new TextField();
		DatePicker admissionDate = new DatePicker();
		
		patientIdField.setPromptText("Auto Generated ID");

		form.add(new Label("Patient ID:"), 0, 0);
        form.add(patientIdField, 1, 0);
        form.add(new Label("Full Name:"), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label("Age:"), 0, 2);
        form.add(ageField, 1, 2);
        form.add(new Label("Gender:"), 0, 3);
        form.add(genderBox, 1, 3);
        form.add(new Label("Phone:"), 0, 4);
        form.add(phoneField, 1, 4);
        form.add(new Label("Diagnosis:"), 0, 5);
        form.add(diagnosisField, 1, 5);
        form.add(new Label("Admission Date:"), 0, 6);
        form.add(admissionDate, 1, 6);
        //Form action area
        Button addButton = new Button("Add Patient");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");
        
        HBox actionform = new HBox(10, addButton, updateButton, deleteButton, clearButton);
        
        form.add(actionform, 0, 7, 2, 1);
        GridPane.setMargin(actionform, new Insets(20));
        
        //Patient Records
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or phone");

        Button searchButton = new Button("Search");
        Button showAllButton = new Button("Show All");

        HBox searchArea = new HBox(10, searchField, searchButton, showAllButton);

        TableView<Patient> recordsTable = new TableView<>();
        
        TableColumn<Patient, Integer> idColumn = new TableColumn<>("Patient ID");
    	idColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
    	TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    	TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
    	ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
    	TableColumn<Patient, String> genderColumn = new TableColumn<>("Gender");
    	genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
    	TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone");
    	phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    	TableColumn<Patient, String> diagnosisColumn = new TableColumn<>("Diagnosis");
    	diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
    	TableColumn<Patient, LocalDate> dateColumn = new TableColumn<>("Admission");
    	dateColumn.setCellValueFactory(new PropertyValueFactory<>("admissionDate"));

    	recordsTable.getColumns().add(idColumn);
    	recordsTable.getColumns().add(nameColumn);
    	recordsTable.getColumns().add(ageColumn);
    	recordsTable.getColumns().add(genderColumn);
    	recordsTable.getColumns().add(phoneColumn);
    	recordsTable.getColumns().add(diagnosisColumn);
    	recordsTable.getColumns().add(dateColumn);
        	
        VBox recordsArea = new VBox(10, searchArea, recordsTable);
        
		HBox center = new HBox(form, recordsArea);
		
		center.setSpacing(20);
		center.setPadding(new Insets(15));
		
		Label status = new Label("status");
		
		status.setPadding(new Insets(15));
		
		pane.setTop(header);
		pane.setCenter(center);
		pane.setBottom(status);
		
		Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        
        PatientDao patientDao = new PatientDao();
        
        //SHOW ALL
        showAllButton.setOnAction(e -> {
            try {
                recordsTable.getItems().setAll(
                        patientDao.getAllPatients()
                );

                status.setText(
                        "Loaded " + recordsTable.getItems().size()
                        + " patient(s)."
                );

            } catch (SQLException ex) {
                status.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        //ADD PATIENT
        addButton.setOnAction(e -> {

            Patient patient = getPatientFromForm(
                    nameField,
                    ageField,
                    genderBox,
                    phoneField,
                    diagnosisField,
                    admissionDate,
                    status
            );

            if (patient == null) {
                return;
            }

            try {
                int generatedId = patientDao.savePatient(patient);

                if (generatedId > 0) {
                    patient.setPatientId(generatedId);
                    recordsTable.getItems().add(patient);

                    status.setText(
                            "Patient added successfully. ID: "
                            + generatedId
                    );

                    clearForm(
                            patientIdField,
                            nameField,
                            ageField,
                            genderBox,
                            phoneField,
                            diagnosisField,
                            admissionDate
                    );
                }

            } catch (SQLException ex) {
                status.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        //SELECT PATIENT
        recordsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldPatient, selectedPatient) -> {

                    if (selectedPatient != null) {

                        patientIdField.setText(
                                String.valueOf(selectedPatient.getPatientId())
                        );

                        nameField.setText(selectedPatient.getName());

                        ageField.setText(
                                String.valueOf(selectedPatient.getAge())
                        );

                        genderBox.setValue(selectedPatient.getGender());

                        phoneField.setText(selectedPatient.getPhone());

                        diagnosisField.setText(
                                selectedPatient.getDiagnosis()
                        );

                        admissionDate.setValue(
                                selectedPatient.getAdmissionDate()
                        );

                        status.setText(
                                "Selected patient: "
                                + selectedPatient.getName()
                        );
                    }
                });

        //UPDATE PATIENT
        updateButton.setOnAction(e -> {

            Patient selectedPatient =
                    recordsTable.getSelectionModel().getSelectedItem();

            if (selectedPatient == null) {
                status.setText("Please select a patient first.");
                return;
            }

            Patient patient = getPatientFromForm(
                    nameField,
                    ageField,
                    genderBox,
                    phoneField,
                    diagnosisField,
                    admissionDate,
                    status
            );

            if (patient == null) {
                return;
            }

            patient.setPatientId(selectedPatient.getPatientId());

            try {
                boolean updated = patientDao.updatePatient(patient);

                if (updated) {
                    recordsTable.getItems().setAll(
                            patientDao.getAllPatients()
                    );

                    status.setText("Patient updated successfully.");

                    recordsTable.getSelectionModel().clearSelection();

                    clearForm(
                            patientIdField,
                            nameField,
                            ageField,
                            genderBox,
                            phoneField,
                            diagnosisField,
                            admissionDate
                    );

                } else {
                    status.setText("No patient was updated.");
                }

            } catch (SQLException ex) {
                status.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

     // DELETE PATIENT
        deleteButton.setOnAction(e -> {

            Patient selectedPatient =
                    recordsTable.getSelectionModel().getSelectedItem();

            if (selectedPatient == null) {
                status.setText("Please select a patient first.");
                return;
            }

            try {
                boolean deleted = patientDao.deletePatient(
                        selectedPatient.getPatientId()
                );

                if (deleted) {
                    recordsTable.getItems().remove(selectedPatient);

                    clearForm(
                            patientIdField,
                            nameField,
                            ageField,
                            genderBox,
                            phoneField,
                            diagnosisField,
                            admissionDate
                    );

                    recordsTable.getSelectionModel().clearSelection();

                    status.setText("Patient deleted successfully.");

                } else {
                    status.setText("No patient was deleted.");
                }

            } catch (SQLException ex) {
                status.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        //SEARCH PATIENT
        searchButton.setOnAction(e -> {

            String keyword = searchField.getText().trim();

            try {
                recordsTable.getItems().setAll(
                        patientDao.searchPatients(keyword)
                );

                status.setText(
                        recordsTable.getItems().size()
                        + " patient(s) found."
                );

            } catch (SQLException ex) {
                status.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // CLEAR FORM
        clearButton.setOnAction(e -> {

            clearForm(
                    patientIdField,
                    nameField,
                    ageField,
                    genderBox,
                    phoneField,
                    diagnosisField,
                    admissionDate
            );

            recordsTable.getSelectionModel().clearSelection();

            status.setText("Form cleared.");
        });

        primaryStage.show();
    }

    //FORM VALIDATION
    private Patient getPatientFromForm(
            TextField nameField,
            TextField ageField,
            ComboBox<String> genderBox,
            TextField phoneField,
            TextField diagnosisField,
            DatePicker admissionDate,
            Label status) {

        if (nameField.getText().trim().isEmpty()) {
            status.setText("Name is required.");
            return null;
        }

        int age;

        try {
            age = Integer.parseInt(ageField.getText().trim());

        } catch (NumberFormatException e) {
            status.setText("Age must be a valid number.");
            return null;
        }

        if (age < 0 || age > 125) {
            status.setText("Age must be between 0 and 125.");
            return null;
        }

        if (genderBox.getValue() == null) {
            status.setText("Please select a gender.");
            return null;
        }

        String phone = phoneField.getText().trim();

        if (phone.isEmpty()) {
            status.setText("Phone number is required.");
            return null;
        }

        if (!phone.matches("[0-9+()\\s-]{7,20}")) {
            status.setText("Please enter a valid phone number.");
            return null;
        }

        if (diagnosisField.getText().trim().isEmpty()) {
            status.setText("Diagnosis is required.");
            return null;
        }

        if (admissionDate.getValue() == null) {
            status.setText("Admission date is required.");
            return null;
        }

        if (admissionDate.getValue().isAfter(LocalDate.now())) {
            status.setText("Admission date cannot be in the future.");
            return null;
        }

        Patient patient = new Patient();

        patient.setName(nameField.getText().trim());
        patient.setAge(age);
        patient.setGender(genderBox.getValue());
        patient.setPhone(phone);
        patient.setDiagnosis(diagnosisField.getText().trim());
        patient.setAdmissionDate(admissionDate.getValue());

        return patient;
    }

    //CLEAR FORM FIELDS
    private void clearForm(
            TextField patientIdField,
            TextField nameField,
            TextField ageField,
            ComboBox<String> genderBox,
            TextField phoneField,
            TextField diagnosisField,
            DatePicker admissionDate) {

        patientIdField.clear();
        nameField.clear();
        ageField.clear();
        genderBox.setValue(null);
        phoneField.clear();
        diagnosisField.clear();
        admissionDate.setValue(null);
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
