package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	/*
	 * Declarar uma variavel estatica impede que o junit reinicie a variavel
	 */
	private LocacaoService service;
		
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before //executa antes do teste
	public void setup() {
		service = new LocacaoService();
	}
	
	@BeforeClass
	public static void beforeClass() {
		System.out.println("Before class");
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println("After class");
	}
	
	@Test
	public void testeLocacao() throws Exception {
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
			
		//verificacao
		Assert.assertEquals("Erro na linha 33 ", 5.0, locacao.getValor(), 0.01); //comentario para facilitar a localização do erro
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); //verifique que
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(4.0)));
		assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(3)), is(false)); //aqui foi realizado o importe estatico
		
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeFilmeSemEstoque() throws Exception {
		
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		service.alugarFilme(usuario, filme);
	}
	
	@Test
	public void testeUsuarioVazio() throws FilmeSemEstoqueException {
		
		Filme filme = new Filme("Filme 1", 1, 5.0);
		
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	@Test
	public void testeFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = new Usuario("Usuario 1");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		service.alugarFilme(usuario, null);
		
	}
	
}
