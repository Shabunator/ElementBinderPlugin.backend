package element.binder.plugin.backend.exception;

public class ExcelReportException extends RuntimeException {

    public ExcelReportException(String message) {
        super(message);
    }

    public ExcelReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
