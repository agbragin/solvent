package pro.parseq.ghop.rest;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import pro.parseq.ghop.data.UnknownContigException;
import pro.parseq.ghop.data.UnknownReferenceGenomeException;

@ControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler(TrackNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorResponse requestHandlingTrackNotFound(
			HttpServletRequest request, TrackNotFoundException e) {

		return new ErrorResponse(new Date(), 404, "Track not found",
				TrackNotFoundException.class, e.getMessage(), request.getServletPath());
	}

	@ExceptionHandler(UnknownReferenceGenomeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse requestHandlingUnknownReferenceGenome(
			HttpServletRequest request, UnknownReferenceGenomeException e) {

		return new ErrorResponse(new Date(), 400, "Unknown reference genome",
				UnknownReferenceGenomeException.class, e.getMessage(), request.getServletPath());
	}

	@ExceptionHandler(UnknownContigException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse requestHandlingUnknownContig(
			HttpServletRequest request, UnknownContigException e) {

		return new ErrorResponse(new Date(), 400, "Unknown contig",
				UnknownContigException.class, e.getMessage(), request.getServletPath());
	}
}
