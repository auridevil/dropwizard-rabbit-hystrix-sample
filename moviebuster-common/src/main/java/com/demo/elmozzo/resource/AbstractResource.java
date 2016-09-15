package com.demo.elmozzo.resource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.demo.elmozzo.queue.object.RPCMessage;

/**
 * The Class AbstractResource.
 */
public class AbstractResource {

	/**
	 * Bad request.
	 *
	 * @param o
	 *          the o
	 * @return the response builder
	 */
	protected static ResponseBuilder badRequest(Object o) {
		return getResponseBuilder(Response.Status.BAD_REQUEST, o);
	}

	/**
	 * Bad request.
	 *
	 * @param ex
	 *          the ex
	 * @return the response builder
	 */
	protected static ResponseBuilder badRequest(Throwable ex) {
		return getResponseBuilder(Response.Status.BAD_REQUEST, ex);
	}

	/**
	 * Created.
	 *
	 * @param entity
	 *          the entity
	 * @return the response builder
	 */
	protected static ResponseBuilder created(Object entity) {
		return getResponseBuilder(Response.Status.CREATED, entity);
	}

	/**
	 * Empty.
	 *
	 * @return the response builder
	 */
	protected static ResponseBuilder empty() {
		return getResponseBuilder(Response.Status.NO_CONTENT, null);
	}

	/**
	 * Gets the eway timeout.
	 *
	 * @return the eway timeout
	 */
	protected static ResponseBuilder getewayTimeout() {
		return getResponseBuilder(Response.Status.GATEWAY_TIMEOUT, null);
	}

	/**
	 * Internal server error.
	 *
	 * @param message
	 *          the message
	 * @return the response builder
	 */
	protected static ResponseBuilder internalServerError(String message) {
		return internalServerError(new Error(message));
	}

	/**
	 * Internal server error.
	 *
	 * @param ex
	 *          the ex
	 * @return the response builder
	 */
	protected static ResponseBuilder internalServerError(Throwable ex) {
		return getResponseBuilder(Response.Status.INTERNAL_SERVER_ERROR, ex);
	}

	/**
	 * Not found.
	 *
	 * @return the response builder
	 */
	protected static ResponseBuilder notFound() {
		return getResponseBuilder(Response.Status.NOT_FOUND, new NotFoundException());
	}

	/**
	 * Ok.
	 *
	 * @param entity
	 *          the entity
	 * @return the response builder
	 */
	protected static ResponseBuilder ok(Object entity) {
		return getResponseBuilder(Response.Status.OK, entity);
	}

	/**
	 * Gets the response builder.
	 *
	 * @param status
	 *          the status
	 * @param entity
	 *          the entity
	 * @return the response builder
	 */
	private static ResponseBuilder getResponseBuilder(Response.Status status, Object entity) {
		final ResponseBuilder responseBuilder = Response.status(status);
		responseBuilder.entity(entity);
		return responseBuilder;
	}

	/**
	 * Builds the created response.
	 *
	 * @param result
	 *          the result
	 * @return the response
	 * @throws InterruptedException
	 *           the interrupted exception
	 * @throws ExecutionException
	 *           the execution exception
	 */
	protected Response buildCreatedResponse(final Future<String> result) throws InterruptedException, ExecutionException {
		// manage timeout from request
		final String raw = result.get();
		if (raw == null) {
			return getewayTimeout().build();
		}
		// parse message
		final RPCMessage responseMsg = RPCMessage.fromString(raw);
		// create response
		if (responseMsg.getException() == null) {
			if (responseMsg.getObj() != null) {
				return created(responseMsg.getObj()).build();
			} else {
				return empty().build();
			}
		} else {
			return internalServerError(responseMsg.getException().getMessage()).build();
		}
	}

	/**
	 * Builds the response.
	 *
	 * @param result
	 *          the result
	 * @return the response
	 * @throws InterruptedException
	 *           the interrupted exception
	 * @throws ExecutionException
	 *           the execution exception
	 */
	protected Response buildResponse(final Future<String> result) throws InterruptedException, ExecutionException {
		// manage timeout from request
		final String raw = result.get();
		if (raw == null) {
			return getewayTimeout().build();
		}
		// parse message
		final RPCMessage responseMsg = RPCMessage.fromString(raw);
		if (responseMsg.getException() == null) {
			if (responseMsg.getObj() != null) {
				// all ok
				return ok(responseMsg.getObj()).build();
			} else {
				// nothing found but no error
				return empty().build();
			}
		} else {
			// something occourred
			return internalServerError(responseMsg.getException().getMessage()).build();
		}
	}

	/**
	 * Builds the response from array of String
	 *
	 * @param result
	 *          the result
	 * @return the response
	 * @throws InterruptedException
	 *           the interrupted exception
	 * @throws ExecutionException
	 *           the execution exception
	 */
	@SuppressWarnings("unchecked")
	protected Response buildResponseFromArray(final Future<String> result) throws InterruptedException, ExecutionException {
		// manage timeout from request
		final String raw = result.get();
		if (raw == null) {
			return getewayTimeout().build();
		}
		// parse message
		final RPCMessage responseMsg = RPCMessage.fromString(raw);
		if (responseMsg.getException() == null) {
			if (responseMsg.getObj() != null) {
				// create representation
				Object out;
				final JSONParser parser = new JSONParser();
				try {
					final Object obj = parser.parse(responseMsg.getObj());
					final JSONArray array = (JSONArray) obj;
					for (int i = 0; i < array.size(); i++) {
						// final String line = array.get(i).toString();
						final Object line = parser.parse((String) array.get(i));
						array.set(i, line);
					}
					out = array;
				} catch (final ParseException e) {
					out = responseMsg.getObj();
				}
				// all ok
				return ok(out).build();
			} else {
				// all ok but no content
				return empty().build();
			}
		} else {
			// something occourred
			return internalServerError(responseMsg.getException().getMessage()).build();
		}
	}

}
