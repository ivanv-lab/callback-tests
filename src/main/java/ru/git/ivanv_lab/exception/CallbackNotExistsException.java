package ru.git.ivanv_lab.exception;

import ru.git.ivanv_lab.callback.CallbackKey;

public class CallbackNotExistsException extends RuntimeException {
  public CallbackNotExistsException(CallbackKey key) {
    super(message(key));
  }

  private static String message(CallbackKey key){
    return String.format("Не найден коллбэк с ключом %s", key);
  }
}
