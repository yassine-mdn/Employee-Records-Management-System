package com.erms.back.Exception;

public class NonAuthorizedException extends BaseAppException {
    public NonAuthorizedException(String message) {

      super(message);
    }

    public NonAuthorizedException() {
      super("User lacks the required permissions to perform this operation");
    }
}
