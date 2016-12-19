package pro.parseq.ghop.rest;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import pro.parseq.ghop.entities.Error;
import pro.parseq.ghop.exceptions.ContigNotFoundException;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;
import pro.parseq.ghop.exceptions.TrackNotFoundException;

@ControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler(ReferenceGenomeNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Error handleReferenceGenomeNotFoundException(
			HttpServletRequest request, ReferenceGenomeNotFoundException e) {

		return new Error(new Date(), 400, "Reference genome not found",
				ReferenceGenomeNotFoundException.class, e.getMessage(),
				request.getServletPath());
	}

	@ExceptionHandler(ContigNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Error handleContigNotFoundException(
			HttpServletRequest request, ContigNotFoundException e) {

		return new Error(new Date(), 400, "Contig not found",
				ContigNotFoundException.class, e.getMessage(),
				request.getServletPath());
	}

	@ExceptionHandler(TrackNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public Error handleTrackNotFoundException(
			HttpServletRequest request, TrackNotFoundException e) {

		return new Error(new Date(), 404, "Track not found",
				TrackNotFoundException.class, e.getMessage(),
				request.getServletPath());
	}
}
