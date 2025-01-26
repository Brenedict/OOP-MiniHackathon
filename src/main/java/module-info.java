module com.studygroup.studygroup {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.studygroup.studygroup to javafx.fxml;
    exports com.studygroup.studygroup;
}