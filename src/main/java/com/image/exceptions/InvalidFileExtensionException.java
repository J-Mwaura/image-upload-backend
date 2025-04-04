package com.image.exceptions;


import com.image.response.MessageResponse;

/**
 * @author atjkm
 *
 */

public class InvalidFileExtensionException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MessageResponse messageResponse;

    public InvalidFileExtensionException(MessageResponse messageResponse) {
        this.messageResponse = messageResponse;
    }

    public MessageResponse getMessageResponse() {
        return messageResponse;
    }

}

