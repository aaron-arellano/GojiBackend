package goji.webapp.exception;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalServerExceptionWrapper implements ExceptionMapper<InternalServerErrorException>{

	@Override
    public Response toResponse(InternalServerErrorException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .build();
    }
}
