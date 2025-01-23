module com.studygroup.studygroup {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.studygroup.studygroup to javafx.fxml;
    exports com.studygroup.studygroup;
}