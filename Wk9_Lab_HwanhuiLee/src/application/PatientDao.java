package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {

    //CREATE
    public int savePatient(Patient patient) throws SQLException {

        String query = "INSERT INTO patients " +
                "(name, age, gender, phone, diagnosis, admission_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getDBConnection();
            PreparedStatement statement = connection.prepareStatement(
                    query, Statement.RETURN_GENERATED_KEYS
            )
        ) {

            int counter = 1;

            statement.setString(counter++, patient.getName());
            statement.setInt(counter++, patient.getAge());
            statement.setString(counter++, patient.getGender());
            statement.setString(counter++, patient.getPhone());
            statement.setString(counter++, patient.getDiagnosis());
            statement.setDate(
                    counter++, Date.valueOf(patient.getAdmissionDate())
            );

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }


    //READ
    public List<Patient> getAllPatients() throws SQLException {

        List<Patient> patients = new ArrayList<>();

        String query = "SELECT * FROM patients";

        try (
            Connection connection = DBConnection.getDBConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Patient patient = new Patient();

                patient.setPatientId(resultSet.getInt("patient_id"));
                patient.setName(resultSet.getString("name"));
                patient.setAge(resultSet.getInt("age"));
                patient.setGender(resultSet.getString("gender"));
                patient.setPhone(resultSet.getString("phone"));
                patient.setDiagnosis(resultSet.getString("diagnosis"));

                patient.setAdmissionDate(
                        resultSet.getDate("admission_date").toLocalDate()
                );

                patients.add(patient);
            }
        }

        return patients;
    }


    //UPDATE
    public boolean updatePatient(Patient patient) throws SQLException {

        String query = "UPDATE patients SET " +
                "name = ?, age = ?, gender = ?, phone = ?, " +
                "diagnosis = ?, admission_date = ? " +
                "WHERE patient_id = ?";

        try (
            Connection connection = DBConnection.getDBConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            int counter = 1;

            statement.setString(counter++, patient.getName());
            statement.setInt(counter++, patient.getAge());
            statement.setString(counter++, patient.getGender());
            statement.setString(counter++, patient.getPhone());
            statement.setString(counter++, patient.getDiagnosis());
            statement.setDate(
                    counter++, Date.valueOf(patient.getAdmissionDate())
            );
            statement.setInt(counter++, patient.getPatientId());

            return statement.executeUpdate() > 0;
        }
    }


    //DELETE
    public boolean deletePatient(int patientId) throws SQLException {

        String query = "DELETE FROM patients WHERE patient_id = ?";

        try (
            Connection connection = DBConnection.getDBConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setInt(1, patientId);

            return statement.executeUpdate() > 0;
        }
    }


    //SEARCH
    public List<Patient> searchPatients(String keyword) throws SQLException {

        List<Patient> patients = new ArrayList<>();

        String query = "SELECT * FROM patients " +
                "WHERE name LIKE ? OR phone LIKE ?";

        try (
            Connection connection = DBConnection.getDBConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            String searchKeyword = "%" + keyword + "%";

            statement.setString(1, searchKeyword);
            statement.setString(2, searchKeyword);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Patient patient = new Patient();

                    patient.setPatientId(resultSet.getInt("patient_id"));
                    patient.setName(resultSet.getString("name"));
                    patient.setAge(resultSet.getInt("age"));
                    patient.setGender(resultSet.getString("gender"));
                    patient.setPhone(resultSet.getString("phone"));
                    patient.setDiagnosis(resultSet.getString("diagnosis"));

                    patient.setAdmissionDate(
                            resultSet.getDate("admission_date").toLocalDate()
                    );

                    patients.add(patient);
                }
            }
        }

        return patients;
    }
}