module com.javaproject.frontjavaproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.compiler;
    requires gson;
    requires org.json;
    requires java.desktop;

    opens com.javaproject.frontjavaproject to javafx.fxml;
    exports com.javaproject.frontjavaproject;
    exports com.javaproject.frontjavaproject.viewcontrollers;
    opens com.javaproject.frontjavaproject.viewcontrollers to javafx.fxml;
    exports com.javaproject.frontjavaproject.authorization;
    opens com.javaproject.frontjavaproject.authorization to javafx.fxml;
}