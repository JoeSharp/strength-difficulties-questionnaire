package uk.ratracejoe.sdq.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import uk.ratracejoe.sdq.dto.ErrorResponseDTO;

@ControllerAdvice
public class SdqExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
    ErrorResponseDTO error =
        ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .build();
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpClientErrorException.BadRequest.class)
  public ResponseEntity<ErrorResponseDTO> handleBadRequest(HttpClientErrorException.BadRequest ex) {
    ErrorResponseDTO error =
        ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .build();
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }
}
