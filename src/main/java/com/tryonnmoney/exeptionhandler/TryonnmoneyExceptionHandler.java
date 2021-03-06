package com.tryonnmoney.api.exeptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/*
 * Controle advice 
 * por que ele fica observando 
 * toda a aplicação
 * 
 * */
@ControllerAdvice
public class TryonnmoneyExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String messagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(messagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, status, request);
	}
	
	
	/*
	 * Validando incosistencia
	 * de dados passados
	 * */
	@ExceptionHandler({DataIntegrityViolationException.class})
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest webRequest){
		
		String messagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString(); //org.apache.commons.lang3.exception.ExceptionUtils  //ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(messagemUsuario, mensagemDesenvolvedor));
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
	}
	
	/*
	 * Tratando execption e customizando
	 * 
	 * utilizado quando vai deleta um registro e o mesmo nao existe mais
	 * */
	@ExceptionHandler({EmptyResultDataAccessException.class})
	//@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest webRequest) {
	
		String messagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(messagemUsuario, mensagemDesenvolvedor));
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
	}
	
	
	private List<Erro> criarListaDeErros(BindingResult bindingResult){
		
		List<Erro> erros = new ArrayList<>();
		
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			
			String mensagemUsuairo = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuairo, mensagemDesenvolvedor));			
		}
		

		return erros;
	}
	
	public static class Erro {
		
		private String mensagemDesenvolvedor;
		private String mensagemUsuairo;
		
		public Erro(String mensagemUsuairo,String mensagemDesenvolvedor) {
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
			this.mensagemUsuairo = mensagemUsuairo;
			
		}

		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}

		public void setMensagemDesenvolvedor(String mensagemDesenvolvedor) {
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemUsuairo() {
			return mensagemUsuairo;
		}

		public void setMensagemUsuairo(String mensagemUsuairo) {
			this.mensagemUsuairo = mensagemUsuairo;
		}

	}

}
