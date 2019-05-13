package Client.Controllers;

import javafx.scene.control.TextField;

/**
 * This class handles exceptions that prevent the user from not entering information
 * into blank fields.
 * @author Jim Spagnola
 */
class EmptyFieldException extends Exception{
    private TextField emptyField;
    EmptyFieldException(TextField t){
        super();
        emptyField = t;
    }

    TextField getEmptyField() {
        return emptyField;
    }
}
